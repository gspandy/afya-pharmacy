package org.ofbiz.order.report;
import javolution.util.FastList;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.sme.order.util.OrderMgrUtil;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
List conditions = FastList.newInstance();

List<String> requirementCreators = new ArrayList<String>();
requirementCreators.add(userLogin.getString("partyId"));
Boolean requirementManager = OrderMgrUtil.isRequirementManger(userLogin.getString("partyId"));
Boolean requirementsRepresentative = OrderMgrUtil.isRequirementsRepresentative(userLogin.getString("partyId"));
Boolean salesReprensentative =OrderMgrUtil.isSalesReprensentative(userLogin.getString("partyId"));
Boolean salesManager = OrderMgrUtil.isSalesManger(userLogin.getString("partyId"));
Boolean purchaseReprensentative = OrderMgrUtil.isPurchaseReprensentative(userLogin.getString("partyId"));
Boolean purchaseManager = OrderMgrUtil.isPurchaseManger(userLogin.getString("partyId"));
SimpleDateFormat dateFormat = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);

java.util.Date parsedDate2 = new java.util.Date();
if(UtilValidate.isNotEmpty(request.getParameter("requirementStartDate"))){
	parsedDate2 = dateFormat.parse(request.getParameter("requirementStartDate"));
	java.sql.Timestamp requirementStartDate =  UtilDateTime.getDayStart( new java.sql.Timestamp(parsedDate2.getTime()) );
	conditions.add(EntityCondition.makeCondition("requirementStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, requirementStartDate));
}
java.util.Date parsedDate1 = new java.util.Date();
if(UtilValidate.isNotEmpty(request.getParameter("requiredByDate"))){
	parsedDate1 = dateFormat.parse(request.getParameter("requiredByDate"));
	java.sql.Timestamp requirementByDate = UtilDateTime.getDayStart( new java.sql.Timestamp(parsedDate1.getTime()) );
	conditions.add(EntityCondition.makeCondition("requiredByDate", EntityOperator.LESS_THAN_EQUAL_TO, requirementByDate));
}
if (UtilValidate.isNotEmpty(request.getParameter("requirementId"))) {
	conditions.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, request.getParameter("requirementId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("requirementTypeId"))) {
	conditions.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, request.getParameter("requirementTypeId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("productId"))) {
	conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, request.getParameter("productId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("facilityId"))) {
	conditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, request.getParameter("facilityId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("statusId"))) {
	conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, request.getParameter("statusId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("description"))) {
	conditions.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, request.getParameter("description")));
}
if (salesManager || salesReprensentative ||requirementManager || purchaseManager ||purchaseReprensentative || requirementsRepresentative) {
	requirementCreators.addAll(OrderMgrUtil.getManagerRelationship(userLogin.getString("partyId")));

	requirementCreators = OrderMgrUtil.getUserLoginIds(requirementCreators);

	if((salesManager || salesReprensentative) && ( purchaseManager || purchaseReprensentative)){
		List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
		try {
			requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(requirementHeaderList)){
				context.listIt =requirementHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	if(( purchaseManager || purchaseReprensentative)){
		conditions.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.NOT_EQUAL, "CUSTOMER_REQUIREMENT"));
		List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
		try {
			requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(requirementHeaderList)){
				context.listIt =requirementHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	conditions.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.IN, requirementCreators));
	List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
	
	
	try {
		requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(requirementHeaderList)){
			context.listIt =requirementHeaderList;
		}
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return "success";
}