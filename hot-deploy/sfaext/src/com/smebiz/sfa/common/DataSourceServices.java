package com.smebiz.sfa.common;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.security.CrmsfaSecurity;

public class DataSourceServices {

    public static final String module = DataSourceServices.class.getName();

    public static Map addAccountDataSource(DispatchContext dctx, Map context) {
        return addDataSourceWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE");
    }

    public static Map addLeadDataSource(DispatchContext dctx, Map context) {
        return addDataSourceWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE");
    }

    /**
     * Parametrized service to add a data source to a party. Pass in the security to check.
     */
    private static Map addDataSourceWithPermission(DispatchContext dctx, Map context, String module, String operation) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = (String) context.get("partyId");
        String dataSourceId = (String) context.get("dataSourceId");

        // check parametrized security
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, module, operation, userLogin, partyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // create the PartyDataSource to relate the optional data source to this party
            Map serviceResults = dispatcher.runSync("createPartyDataSource", UtilMisc.toMap("partyId", partyId , "dataSourceId", dataSourceId, "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorAddDataSource", locale, module);
            }
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAddDataSource", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map removeAccountDataSource(DispatchContext dctx, Map context) {
        return removeDataSourceWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE");
    }

    public static Map removeLeadDataSource(DispatchContext dctx, Map context) {
        return removeDataSourceWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE");
    }

    /**
     * Parametrized method to remove a data source from a party. Pass in the security to check.
     * TODO: this isn't implemented until necessary
     */
    private static Map removeDataSourceWithPermission(DispatchContext dctx, Map context, String module, String operation) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = (String) context.get("partyId");
        String dataSourceId = (String) context.get("dataSourceId");
        String fromDateString = (String) context.get("fromDate");
        Timestamp fromDate = UtilDateTime.getDate(fromDateString);
        
        Map input = UtilMisc.toMap("partyId", partyId, "dataSourceId", dataSourceId, "fromDate", fromDate);

        try {
            // check parametrized security
            if (!CrmsfaSecurity.hasPartyRelationSecurity(security, module, operation, userLogin, partyId)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
            }
            
            // There's no service in ofbiz to remove LeadDataSource entries, so we do it by hand
            delegator.removeByAnd("PartyDataSource", input);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveDataSourceFail", locale, module);
        }

        // don't do anything yet
        return ServiceUtil.returnSuccess();
    }
}
