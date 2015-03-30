package com.nthdimenzion.humanres.taxdecl;

/** Pankaj: changed partyId to be a request parameter so that Admin can approve declarations of others 
 * 	Jun 12, 2009
 * **/

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;

import com.smebiz.common.PartyUtil;

public class TaxDeclarationEvents {

    static EntityFindOptions FIND_OPTIONS = new EntityFindOptions();

    static {
        FIND_OPTIONS.setDistinct(true);
        FIND_OPTIONS.setResultSetConcurrency(ResultSet.CONCUR_READ_ONLY);
    }

    public static final String module = "TaxDeclarationEvents";

    public static String initEditForm(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
        String validTaxDeclId = (String) parameterMap.get("validTaxDeclId");
        String lpartyId = (String) parameterMap.get("partyId");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");
        // String vpermission = (String) parameterMap.get("hasPermission");
        // Boolean lpermission = Boolean.FALSE;
        // if (vpermission.equalsIgnoreCase("TRUE")) {
        // lpermission = Boolean.TRUE;
        // }
        // if (!lpermission && !(partyId.equalsIgnoreCase(lpartyId))) {
        // Debug.logError("You cant view records of others", module);
        // return "error";
        // }
        // First Check if its already exists for the Employee.
        EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", lpartyId, "validTaxDeclId",
                validTaxDeclId));

        Boolean belongsToFscal = Boolean.TRUE;

        GenericValue endDateOfFisc = delegator.findByPrimaryKey("ValidTaxDecl", UtilMisc.toMap("validTaxDeclId", validTaxDeclId));
        Date endFiscalYearDate = endDateOfFisc.getDate("endDate");
        java.util.Date endFiscalYeDt = new java.util.Date(endFiscalYearDate.getTime());
        java.util.Date currDate = new java.util.Date();
        if (endFiscalYeDt.before(currDate)) {
            belongsToFscal = Boolean.FALSE;
        }

        List<GenericValue> values = delegator.findList("EmplTaxDecl", entityCondition, null, null, null, false);
        Boolean isNew = Boolean.TRUE;
        List<TaxCategory> categories = null;
        if (values.size() > 0) {
            categories = convertToCategoryList(delegator, values);
            isNew = Boolean.FALSE;
        } else {
            String geoId = PartyUtil.getGeoIdForUserLogin(userLogin); // Correct
            // it to
            // have
            // party
            // userLogin
            // geoId
            EntityCondition whereCondition = EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId));
            EntityCondition condition = EntityCondition.makeConditionDate("fromDate", "thruDate");
            List<EntityCondition> conds = new ArrayList<EntityCondition>(2);
            conds.add(whereCondition);
            conds.add(condition);

            entityCondition = EntityCondition.makeCondition(conds);
            Set<String> fieldsToSelect = null;
            values = delegator.findList("TaxCategory", entityCondition, fieldsToSelect, null, FIND_OPTIONS, false);
            categories = convertToCategoryList(delegator, values);
        }
        request.setAttribute("categories", categories);
        request.setAttribute("isNew", isNew);
        request.setAttribute("belongsToFscal", belongsToFscal);
        request.setAttribute("isEditable", isFormEditable(validTaxDeclId, delegator));
        return "success";
    }

    public static List<TaxCategory> convertToCategoryList(GenericDelegator delegator, List<GenericValue> values)
            throws GenericEntityException {

        Set<TaxCategory> taxCategories = new HashSet<TaxCategory>();
        Map<String, TaxCategory> categoryMap = FastMap.newInstance();
        GenericValue categoryVal = null;

        if (UtilValidate.isEmpty(values)) {
            return new ArrayList<TaxCategory>();
        }
        GenericValue anyOne = values.get(0);
        if ("EmplTaxDecl".equals(anyOne.getEntityName())) {
            for (GenericValue val : values) {
                categoryVal = val.getRelatedOne("TaxCategory");

                TaxCategory category = categoryMap.get(categoryVal.getString("categoryId"));
                if (category == null) {
                    category = new TaxCategory(categoryVal);
                    taxCategories.add(category);
                    categoryMap.put(categoryVal.getString("categoryId"), category);
                }

                Collection<TaxItem> collOfTaxItem = category.getTaxItems();
                TaxItem taxItem = new TaxItem(val);
                collOfTaxItem.add(taxItem);
            }
        } else if ("TaxCategory".equals(anyOne.getEntityName())) {
            for (GenericValue val : values) {
                TaxCategory category = new TaxCategory(val);
                if (!taxCategories.contains(category)) {
                    taxCategories.add(category);
                }

                EntityCondition whereCondition = EntityCondition.makeCondition(UtilMisc.toMap("categoryId", category.getCategoryId()));
                EntityCondition condition = EntityCondition.makeConditionDate("fromDate", "thruDate");
                List<EntityCondition> conds = new ArrayList<EntityCondition>(2);
                conds.add(whereCondition);
                conds.add(condition);

                EntityCondition entityCondition = EntityCondition.makeCondition(conds);

                Set<String> fieldsToSelect = null;
                List<String> orderBy = null;
                List<GenericValue> itemValues = delegator
                        .findList("TaxItem", entityCondition, fieldsToSelect, orderBy, FIND_OPTIONS, false);
                Collection<TaxItem> collOfTaxItem = category.getTaxItems();
                for (GenericValue itemVal : itemValues) {
                    TaxItem taxItem = new TaxItem(itemVal);
                    collOfTaxItem.add(taxItem);
                }
                taxCategories.add(category);
            }
        }

        return new ArrayList<TaxCategory>(taxCategories);
    }

    public static String storeTaxDecl(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);

        String validTaxDeclId = (String) parameterMap.get("validTaxDeclId");
        String lpartyId = (String) parameterMap.get("partyId");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        // GenericValue userLogin = (GenericValue)
        // request.getSession().getAttribute("userLogin");
        // String submitPartyId = userLogin.getString("partyId"); //Get the id
        // of submitting party

        List<GenericValue> taxDeclElems = new LinkedList<GenericValue>();
        Iterator<String> iter = parameterMap.keySet().iterator();

        Map<String, TaxCategory> categoryMap = FastMap.newInstance();

        for (; iter.hasNext(); ) {
            String key = iter.next();
            boolean isTaxDeclField = checkIfTaxDeclField(key);
            if (isTaxDeclField) {
                String paramValue = (String) parameterMap.get(key);
                String parts[] = key.split("_");
                boolean isNumeric = checkIfNumeric(parts[3]);
                String itemId = parts[2];

                TaxCategory category = categoryMap.get(parts[1]);
                if (category == null) {
                    category = new TaxCategory();
                    category.setCategoryId(parts[1]);
                    categoryMap.put(parts[1], category);
                }
                // First Validate Individual Tax Items
                if (isNumeric) {
                    double userInputItemValue = Double.parseDouble(paramValue);
                    TaxItem item = new TaxItem();
                    item.setTaxItemId(itemId);
                    item.setNumValue(userInputItemValue);
                    category.getTaxItems().add(item);
                } else {
                    Map<String, String> keyValuePairs = UtilMisc.toMap("partyId", lpartyId, "validTaxDeclId", validTaxDeclId, "categoryId",
                            parts[1], "itemId", parts[2], "stringValue", paramValue);
                    GenericValue value = delegator.makeValue("EmplTaxDecl");
                    value.setFields(keyValuePairs);
                    taxDeclElems.add(value);
                }

            }
        }

        Iterator<TaxCategory> catIter = categoryMap.values().iterator();
        for (; catIter.hasNext(); ) {
            TaxCategory category = catIter.next();
            for (TaxItem taxItem : category.getTaxItems()) {
                Map<String, Object> keyValuePairs = new HashMap<String, Object>();
                keyValuePairs.put("partyId", lpartyId);
                keyValuePairs.put("validTaxDeclId", validTaxDeclId);
                keyValuePairs.put("categoryId", category.getCategoryId());
                keyValuePairs.put("itemId", taxItem.getItemId());
                keyValuePairs.put("numValue", taxItem.getNumValue());
                GenericValue value = delegator.makeValue("EmplTaxDecl");
                value.setFields(keyValuePairs);
                taxDeclElems.add(value);
            }
        }

        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
            delegator.storeAll(taxDeclElems);
        } catch (GenericEntityException e) {
            try {
                // only roll back the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, "Error Saving Section Attribute Values", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
            }
        } finally {
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not commit transaction for entity"
                        + " engine error occurred while saving abandoned cart information", module);
            }
        }
        return "success";
    }

    /**
     * Approve the tax decls by admin Update empl_tax_decl set accepted_value =
     * num_value where partyId = lpartyId
     *
     * @param str
     * @return
     */
    public static String approveTaxDecl(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);

        String validTaxDeclId = (String) parameterMap.get("validTaxDeclId");
        String lpartyId = (String) parameterMap.get("partyId");
        Map<String, Object> paraMap = FastMap.newInstance();
        paraMap.putAll(UtilMisc.toMap("partyId", lpartyId, "validTaxDeclId", validTaxDeclId));
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        EntityCondition cond = EntityCondition.makeCondition(paraMap);
        List<GenericValue> declList = delegator.findList("EmplTaxDecl", cond, null, null, null, false);
        BigDecimal lnumValue = BigDecimal.ZERO;
        try {
            for (GenericValue taxGV : declList) {
                if (taxGV.get("numValue") != null) {
                    lnumValue = (BigDecimal) taxGV.get("numValue");
                } else
                    lnumValue = BigDecimal.ZERO;
                taxGV.put("acceptedValue", lnumValue);
                taxGV.store();
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Could not commit transaction for party : " + lpartyId + " engine error occurred while saving it", module);
            return "error";
        }
        return "success";
    }

    private static boolean checkIfNumeric(String str) {
        return "N".equals(str);
    }

    private static boolean checkIfTaxDeclField(String key) {
        return key.startsWith("_");
    }

    public static boolean isFormEditable(String validTaxDeclId, GenericDelegator delegator) throws GenericEntityException {
        GenericValue GV = delegator.findOne("ValidTaxDecl", UtilMisc.toMap("validTaxDeclId", validTaxDeclId), false);
        return validate(GV);
    }

    public static boolean validate(GenericValue validTaxDecl) throws GenericEntityException {

        String modifyType = validTaxDecl.getString("modificationType");

        if (validTaxDecl.getDate("endDate").before(UtilDateTime.nowDate()))
            return false;

        if ("Monthly".equals(modifyType)) {
            int latestBy = Integer.parseInt(validTaxDecl.getString("modifyLatestBy"));
            int todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            return latestBy >= todayDay;
        } else {
            Date latestBy = validTaxDecl.getDate("modifyLatestByDate");
            Timestamp latestByTime = new Timestamp(latestBy.getTime());
            Timestamp todate = UtilDateTime.nowTimestamp();
            return todate.before(latestByTime);
        }
    }
}
