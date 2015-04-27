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

context.isGeneralManager = OrderMgrUtil.isGeneralManager(userLogin.getString("partyId"));

context.isRequirementProposer = OrderMgrUtil.isRequirementProposer(userLogin.getString("partyId"));

if (salesManager || salesReprensentative ||requirementManager || purchaseManager ||purchaseReprensentative || requirementsRepresentative) {
	requirementCreators.addAll(OrderMgrUtil.getManagerRelationship(userLogin.getString("partyId")));

	requirementCreators = OrderMgrUtil.getUserLoginIds(requirementCreators);

	if((salesManager || salesReprensentative) && ( purchaseManager || purchaseReprensentative)){
		conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_CREATED"));
		conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_PROPOSED"));
			List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
		try {
			requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.OR), null, ["createdStamp DESC"], null, false);
			
			if(UtilValidate.isNotEmpty(requirementHeaderList)){
				context.requirementsList =requirementHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	/*if((salesManager || salesReprensentative)){
		conditions.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "CUSTOMER_REQUIREMENT"));
		List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
		try {
			requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, ["createdStamp DESC"], null, false);
			if(UtilValidate.isNotEmpty(requirementHeaderList)){
				context.requirementsList =requirementHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}*/
	if(( purchaseManager || purchaseReprensentative)){
		conditions.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.NOT_EQUAL, "CUSTOMER_REQUIREMENT"));
		List conditions1 = FastList.newInstance();
		conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_CREATED"));
		conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_PROPOSED"));
		conditions.add(EntityCondition.makeCondition(conditions1, EntityOperator.OR));
		List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
		try {
			requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, ["createdStamp DESC"], null, false);
			if(UtilValidate.isNotEmpty(requirementHeaderList)){
				context.requirementsList =requirementHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	conditions.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.IN, requirementCreators));
	List conditions1 = FastList.newInstance();
	conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_CREATED"));
	conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_PROPOSED"));
	conditions.add(EntityCondition.makeCondition(conditions1, EntityOperator.OR));
	List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
	try {
		requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, ["createdStamp DESC"], null, false);
		if(UtilValidate.isNotEmpty(requirementHeaderList)){
			context.requirementsList =requirementHeaderList;
		}
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return "success";
}



