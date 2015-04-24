package com.nthdimenzion.humanres.taxdecl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;

public class TaxService {
	
	public static final String module = TaxService.class.getName();	
	public static final String HRA_CATEGORY = "HRA";
	public static final Double METRO_ORNOT = 0.4D; //For Non Metro HRA is 40% of Basic
	
	/** Create A New Form Field */
	public static Map<String, Object> createFormField(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if (userLogin == null) { 	
			return ServiceUtil.returnError("userLogin is null, cannot create.");
		}
		
		Map<String, Object> cl = UtilMisc.toMap("formId", context.get("formId"),
				"fieldName", context.get("fieldName"), "fieldType", context.get("fieldType"));
		cl.put("sequenceId", context.get("sequenceId"));
		cl.put("description", context.get("description"));
		cl.put("hr_comment", context.get("hr_comment"));
		cl.put("fromDate", context.get("fromDate"));
		cl.put("thruDate", context.get("thruDate"));
		
		
		GenericValue clGV = delegator.makeValue("FormField", cl);
		String l_fieldId = delegator.getNextSeqId("FormField");
		clGV.set("fieldId", l_fieldId);
		try {
			clGV.create();
		}catch(GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred creating the Form Field record" + e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess("Employee Form Fieldt created");
		result.put("fieldId", l_fieldId);
		return result;
	}
	
	public static Map updateFormField(DispatchContext dctx, Map context) {
		Map result = null;
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if (userLogin == null) { 	
			return ServiceUtil.returnError("userLogin is null, cannot create.");
		}
		
		String l_fieldId = (String)context.get("fieldId");
		Map clId = UtilMisc.toMap("fieldId", l_fieldId);
		
		GenericValue fieldGV = null;
		GenericValue loanStatusGV = null;
		try {
			fieldGV = delegator.findByPrimaryKey("FormField", clId);
		} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the Form Field record [" + l_fieldId + "]");
        }
		
		Map cl = UtilMisc.toMap("formId", context.get("formId"),
				"fieldName", context.get("fieldName"), "fieldType", context.get("fieldType"));
		cl.put("sequenceId", context.get("sequenceId"));
		cl.put("description", context.get("description"));
		cl.put("fromDate", context.get("fromDate"));
		cl.put("thruDate", context.get("thruDate"));
		cl.put("hr_comment", context.get("hr_comment"));
		
		fieldGV.putAll(cl);
		try {
			fieldGV.store();
		}catch(GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems updating the Form Field record [" + l_fieldId + "]"+ e.getMessage());
		}
		 result = ServiceUtil.returnSuccess("Employee Form Field updated");
		result.put("fieldId", l_fieldId);
		return result;
	}
	
	 /** For the given period and partyId fetch the TDS details **/
	 public static Map<String, Object> getTDSParty(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();	
		String lpartyId = (String)context.get("partyId");
		
		Date lperiodFrom = (Date)context.get("periodFrom");
		Date lperiodTo = (Date)context.get("periodTo");		
		System.out.println("**************************** lperiodFrom " + lperiodFrom);
		System.out.println("**************************** lperiodTo " + lperiodTo);
	
		EntityCondition partyCond = null;
		EntityCondition fromCond = null;
		EntityCondition toCond = null;
		
		if (lpartyId != null) {
			partyCond = EntityCondition.makeCondition(("partyId"), EntityOperator.EQUALS, lpartyId);
		}
		else {
			return ServiceUtil.returnError("PartyId cannot be null");
		}
	
		fromCond = EntityCondition.makeCondition(("periodFrom"), EntityOperator.GREATER_THAN_EQUAL_TO, lperiodFrom);
		toCond = EntityCondition.makeCondition(("periodTo"), EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo);
			
		List<String> orderBy = new LinkedList<String>();
		orderBy.add("periodFrom");
		List<GenericValue> listIt = null;
		EntityCondition cond = EntityCondition.makeCondition(partyCond, fromCond, toCond);
		try {
			listIt= delegator.findList("TdsParty", cond, null,orderBy, null, false);
		} catch(GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred searching the TdsParty for party :" + lpartyId + e.getMessage());
		}	
		Map<String, Object> result = ServiceUtil.returnSuccess("Employee TDS searched");
		result.put("partyId", lpartyId);
		result.put("listIt", listIt);
		System.out.println("result :" + result);
		return result;
	 }
	 
	 public static Map<String, Object> createTdsParty(DispatchContext dctx, Map<String, Object> context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			if (userLogin == null) { 	
				return ServiceUtil.returnError("userLogin is null, cannot create.");
			}
	        
			String lpartyId = (String)context.get("partyId");
			java.sql.Date lperiodFrom = (java.sql.Date)context.get("periodFrom");
			java.sql.Date lperiodTo = (java.sql.Date)context.get("periodTo");
			Map<String, Object> cl = UtilMisc.toMap("partyId", lpartyId,
					"periodFrom", lperiodFrom, "periodTo", lperiodTo, "tds", context.get("tds"));
			cl.put("surcharge", context.get("surcharge"));
			cl.put("educationCess", context.get("educationCess"));
			cl.put("higherEduCess", context.get("higherEduCess"));
			cl.put("totTds", context.get("totTds"));
			cl.put("chequeNo", context.get("chequeNo"));
			cl.put("paymentDate", context.get("paymentDate"));
			cl.put("branchCode", context.get("branchCode"));
			cl.put("challanNo", context.get("challanNo"));
			
			GenericValue clGV = delegator.makeValue("TdsParty", cl);
			try {
				clGV.create();
			}catch(GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("An Error occurred creating the TdsParty record for party : " + lpartyId + "  "+ e.getMessage());
			}
			Map<String, Object> result = ServiceUtil.returnSuccess("Employee TDS created");
			result.put("partyId", lpartyId);
			result.put("periodFrom", lperiodFrom);
			result.put("periodTo", lperiodTo);
			return result;
		}
		
		public static Map<String, Object> updateTdsParty(DispatchContext dctx, Map<String, Object> context) {
			Map<String, Object> result = null;
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			if (userLogin == null) { 	
				return ServiceUtil.returnError("userLogin is null, cannot create.");
			}
			
			String lpartyId = (String)context.get("partyId");
			java.sql.Date lperiodFrom = (java.sql.Date)context.get("periodFrom");
			java.sql.Date lperiodTo = (java.sql.Date)context.get("periodTo");
			Map<String, Object> clId = UtilMisc.toMap("partyId", lpartyId, "periodFrom", lperiodFrom);
			
			GenericValue tdsGV = null;
			try {
				tdsGV = delegator.findByPrimaryKey("TdsParty", clId);
			} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError("Problems finding the TDS record [" + lpartyId + "]");
	        }
						
			Map<String, Object> cl = UtilMisc.toMap("partyId", lpartyId,
					"periodFrom", lperiodFrom, "periodTo", lperiodTo, "tds", context.get("tds"));
			cl.put("surcharge", context.get("surcharge"));
			cl.put("educationCess", context.get("educationCess"));
			cl.put("higherEduCess", context.get("higherEduCess"));
			cl.put("totTds", context.get("totTds"));
			cl.put("chequeNo", context.get("chequeNo"));
			cl.put("paymentDate", context.get("paymentDate"));
			cl.put("branchCode", context.get("branchCode"));
			cl.put("challanNo", context.get("challanNo"));
			
			tdsGV.putAll(cl);
			try {
				tdsGV.store();
			}catch(GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Problems updating the TDS record [" + lpartyId + "]"+ e.getMessage());
			}
			result = ServiceUtil.returnSuccess("Employee Form Field updated");
			result.put("partyId", lpartyId);
			result.put("periodFrom", lperiodFrom);
			result.put("periodTo", lperiodTo);
			return result;
		}
		
	
}
 