package com.smebiz.sfa.forecasts;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.common.UtilMessage;
import com.smebiz.sfa.opportunities.UtilOpportunity;
import com.smebiz.sfa.party.SfaPartyHelper;


public class ForecastsServices {

    public static final String module = ForecastsServices.class.getName();
    
    protected static final String FORECAST_CHANGE_NOTE_PREFIX_UILABEL = "ForecastChangeNotePrefix";

    public static Map updateForecast(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String salesForecastId = (String) context.get("salesForecastId");
        Double quotaAmount = (Double) context.get("quotaAmount");
        try {
            GenericValue forecast = delegator.findByPrimaryKey("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId));
            if (forecast == null) {
                return UtilMessage.createAndLogServiceError("Forecast with ID [" + salesForecastId + "] not found.", "ErrorComputeForecastFail", locale, module);
            }

            // compute the fields for the forecast (use the internalPartyId of the existing forecast)
            Map computed = UtilForecast.computeForecastByOpportunities(quotaAmount, forecast.getString("organizationPartyId"), 
                    forecast.getString("internalPartyId"),  forecast.getString("currencyUomId"), forecast.getString("customTimePeriodId"), delegator);

            // make the service input map from the context
            ModelService service = dctx.getModelService("updateSalesForecast");
            Map input = service.makeValid(context, "IN"); 

            // add rest of fields (in this case we preserve the previous internalPartyId)
            input.put("salesForecastId", salesForecastId); 
            input.putAll(computed); 
            input.put("userLogin", userLogin);

            // run our update/create service
            Map serviceResults = dispatcher.runSync("updateSalesForecast", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "ErrorComputeForecastFail", locale, module);
            }

            // now recompute the parent forecast by calling our service for this with the parent as input
            // TODO: normally we could use: GenericValue parent = forecast.getRelatedOne("ParentSalesForecast");
            // TODO: but we can't yet untill we re-arrange the way forecasts are created (create all of them once quarter is selected, then compute values)
            // TODO: so this is a complete hack:
            GenericValue period = forecast.getRelatedOne("CustomTimePeriod");
            List parents = delegator.findByAnd("SalesForecastAndCustomTimePeriod", UtilMisc.toMap("customTimePeriodId", period.getString("parentPeriodId"),
                        "internalPartyId", forecast.getString("internalPartyId"), "organizationPartyId", forecast.getString("organizationPartyId"),
                        "periodTypeId", "FISCAL_QUARTER")); // XXX HACK
            GenericValue parent = (GenericValue) parents.get(0); // XXX HACK

            service = dctx.getModelService("sfa.computeForecastParentPeriod");
            input = service.makeValid(parent.getAllFields(), "IN"); 
            input.put("userLogin", userLogin);
            input.put("parentPeriodId", period.getString("parentPeriodId"));
            input.put("changeNote", context.get("changeNote")); // also update the change note in parent
            serviceResults = dispatcher.runSync("sfa.computeForecastParentPeriod", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "ErrorComputeForecastFail", locale, module);
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        }
        return ServiceUtil.returnSuccess();

    }

    // TODO: The next two methods have a lot of overlapping code.  We should try to re-factor.
    public static Map computeForecastPeriod(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String customTimePeriodId = (String) context.get("customTimePeriodId");
        String organizationPartyId = (String) context.get("organizationPartyId");
        String currencyUomId = (String) context.get("currencyUomId");

        // set the quota to 0.00 if the user didn't supply it, that way we get all forecasts for a set of periods rather than a few that had quotas defined
        Double quotaAmount = (Double) context.get("quotaAmount");
        if (quotaAmount == null) quotaAmount = new Double(0.00);

        try {
            String salesForecastId = (String) context.get("salesForecastId");
            String serviceName = null;
            String internalPartyId = null;

            // see if we were passed a salesForecastId and determine service to run and party for whom forecast is computed
            if (salesForecastId == null) {
                serviceName = "createSalesForecast";
                internalPartyId = userLogin.getString("partyId");
            } else {
                serviceName = "updateSalesForecast";
                GenericValue forecast = delegator.findByPrimaryKey("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId));
                if ((forecast != null) && (forecast.getString("internalPartyId") != null)) {
                    internalPartyId = forecast.getString("internalPartyId");
                } else {
                    return UtilMessage.createAndLogServiceError("ErrorInvalidForecast", UtilMisc.toMap("salesForecastId", salesForecastId), locale, module);
                }
            }

            // compute the fields for the forecast
            Map computed = UtilForecast.computeForecastByOpportunities(quotaAmount, organizationPartyId, internalPartyId, 
                    currencyUomId, customTimePeriodId, delegator);

            // make the service input map from the context
            ModelService service = dctx.getModelService(serviceName);
            Map input = service.makeValid(context, "IN"); 

            // add our computed fields and the userlogin
            input.putAll(computed); 
            input.put("userLogin", userLogin);
            input.put("internalPartyId", internalPartyId);

            // run our update/create service
            Map serviceResults = dispatcher.runSync(serviceName, input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "ErrorComputeForecastFail", locale, module);
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map computeForecastParentPeriod(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String parentPeriodId = (String) context.get("parentPeriodId");
        String organizationPartyId = (String) context.get("organizationPartyId");
        String currencyUomId = (String) context.get("currencyUomId");

        try {
            // see if we were passed a salesForecastId
            String salesForecastId = (String) context.get("salesForecastId");
            String serviceName = null;
            String internalPartyId = null;

            // see if we were passed a salesForecastId and determine service to run and party for whom forecast is computed
            if (salesForecastId == null) {
                serviceName = "createSalesForecast";
                internalPartyId = userLogin.getString("partyId");
            } else {
                serviceName = "updateSalesForecast";
                GenericValue forecast = delegator.findByPrimaryKey("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId));
                if ((forecast != null) && (forecast.getString("internalPartyId") != null)) {
                    internalPartyId = forecast.getString("internalPartyId");
                } else {
                    return UtilMessage.createAndLogServiceError("ErrorInvalidForecast", UtilMisc.toMap("salesForecastId", salesForecastId), locale, module);
                }
            }

            // compute the fields for the forecast
            Map computed = UtilForecast.computeForecastByChildren(parentPeriodId, organizationPartyId, internalPartyId, currencyUomId, delegator);

            // make the service input map from the context
            ModelService service = dctx.getModelService(serviceName);
            Map input = service.makeValid(context, "IN"); 
            input.put("customTimePeriodId", parentPeriodId); // the parent period is the custom time period

            // add our computed fields and the userlogin
            input.putAll(computed); 
            input.put("userLogin", userLogin);
            input.put("internalPartyId", internalPartyId);

            // run our update/create service
            Map serviceResults = dispatcher.runSync(serviceName, input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "ErrorComputeForecastFail", locale, module);
            }
            // if we had no sales forecast, then return the one we just created
            if (salesForecastId == null) {
                salesForecastId = (String) serviceResults.get("salesForecastId");
            }
            
            Map results = ServiceUtil.returnSuccess();
            results.put("salesForecastId", salesForecastId);
            return results;
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        }
    }

    public static Map updateForecastsRelatedToOpportunity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("SFAEXTUiLabels", locale);

        String salesOpportunityId = (String) context.get("salesOpportunityId");
        String changeNote = (String) context.get("changeNote");
        Timestamp previousEstimatedCloseDate = (Timestamp) context.get("previousEstimatedCloseDate");
        
        // get the organization from the properties
        String organizationPartyId = (String) UtilProperties.getPropertyValue("sfa.properties", "organizationPartyId");

        try {
            GenericValue opportunity = delegator.findByPrimaryKey("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId));
            
            // construct the changeNote with a link back to the opportunity (for errors/messages)
            StringBuffer changeNoteBuff = new StringBuffer((String) uiLabelMap.get(FORECAST_CHANGE_NOTE_PREFIX_UILABEL));
            changeNoteBuff.append("<a class='buttontext' href='viewOpportunity?salesOpportunityId=").append(salesOpportunityId).append("'>")
                .append(opportunity.getString("opportunityName")).append(" (").append(salesOpportunityId).append(")</a>");
            if (changeNote != null) {
                changeNoteBuff.append(": ").append(changeNote);
            }

            /*
             * Strategy: Build a list of partyIds whose forecasts would be affected by opportunity.
             *
             * For account opportunities, get the team members for the one related account.
             * For lead opportunities, get the LEAD_OWNER partyId.
             *
             * Then, find all forecasts for these partyId's in the "affected" periods.  An 
             * "affected" time period falls in the opportunity's estimatedCloseDate and the last 
             * estimatedCloseDate (determined from SalesOpportunityHistory).  For each FISCAL_MONTH 
             * forecast found, the compute forecast service is called with all the values of that 
             * forecast, as if the forecast were being updated. After doing months, compute the 
             * quarters (FISCAL_QUARTER) in the same manner.
             */

            String accountPartyId = (String) UtilOpportunity.getOpportunityAccountPartyId(opportunity);
            String leadPartyId = (String) UtilOpportunity.getOpportunityLeadPartyId(opportunity);
            boolean isAccountOpportunity = (accountPartyId != null ? true : false);
            Set partyIds = new HashSet();

            if (isAccountOpportunity) {
                // get all the team members and collect their IDs into a Set
                List teamMembers = UtilOpportunity.getOpportunityTeamMembers(salesOpportunityId, delegator);
                for (Iterator iter = teamMembers.iterator(); iter.hasNext(); ) {
                    GenericValue teamMember = (GenericValue) iter.next();
                    partyIds.add(teamMember.getString("partyId"));
                }
            } else {
                // get the LEAD_OWNER partyId
                GenericValue leadOwner = SfaPartyHelper.getCurrentLeadOwner(leadPartyId, delegator);
                if (leadOwner == null) {
                    return UtilMessage.createAndLogServiceError("No LEAD_OWNER for lead ["+leadPartyId+"] found!", locale, module); 
                }
                partyIds.add(leadOwner.getString("partyId"));
            }

            // if no parties found, then we're done
            if (partyIds.size() == 0) {
                return ServiceUtil.returnSuccess();
            }

            // We want all time periods that contain the new estimatedCloseDate, and the old one if it's different
            List periodConditions = new ArrayList();
            periodConditions.add(EntityUtil.getFilterByDateExpr( new java.sql.Date(opportunity.getTimestamp("estimatedCloseDate").getTime()) ));
            if (previousEstimatedCloseDate != null) {
                // because this condition will be joined by OR, we don't need to worry about them being different
                periodConditions.add(EntityUtil.getFilterByDateExpr( new java.sql.Date(previousEstimatedCloseDate.getTime()) ));
            }

            // get the forecasts (ideally we want a distinct, but the way the query is constructed should guarantee a distinct set anyway)
            EntityConditionList conditions = EntityCondition.makeCondition( UtilMisc.toList(
            		EntityCondition.makeCondition(periodConditions, EntityOperator.OR), // join the periods by OR
            		EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId),
            		EntityCondition.makeCondition("internalPartyId", EntityOperator.IN, partyIds)
                        ), EntityOperator.AND);

            List forecasts = delegator.findList("SalesForecastAndCustomTimePeriod", conditions, null, null,null,false);

            // update forecasts of type FISCAL_MONTH first
            for (Iterator iter = forecasts.iterator(); iter.hasNext(); ) {
                GenericValue forecast = (GenericValue) iter.next();
                if (!forecast.getString("periodTypeId").equals("FISCAL_MONTH")) continue;

                Map input = UtilMisc.toMap("userLogin", userLogin, "organizationPartyId", organizationPartyId, "currencyUomId", forecast.get("currencyUomId"));
                input.put("customTimePeriodId", forecast.getString("customTimePeriodId"));
                input.put("salesForecastId", forecast.getString("salesForecastId"));
                input.put("quotaAmount", forecast.getDouble("quotaAmount"));
                input.put("changeNote", changeNoteBuff.toString());
                Map serviceResults = dispatcher.runSync("sfa.computeForecastPeriod", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "ErrorComputeForecastFail", locale, module);
                }
                iter.remove(); // this helps speed up the next iteration
            }

            // update forecasts of type FISCAL_QUARTER
            for (Iterator iter = forecasts.iterator(); iter.hasNext(); ) {
                GenericValue forecast = (GenericValue) iter.next();
                if (!forecast.getString("periodTypeId").equals("FISCAL_QUARTER")) continue;

                Map input = UtilMisc.toMap("userLogin", userLogin, "organizationPartyId", organizationPartyId, "currencyUomId", forecast.get("currencyUomId"));
                input.put("parentPeriodId", forecast.getString("customTimePeriodId"));
                input.put("salesForecastId", forecast.getString("salesForecastId"));
                input.put("changeNote", changeNoteBuff.toString());
                Map serviceResults = dispatcher.runSync("sfa.computeForecastParentPeriod", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "ErrorComputeForecastFail", locale, module);
                }
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "ErrorComputeForecastFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }
}
