package org.ofbiz.order.report;
import javolution.util.FastList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.sme.order.util.OrderMgrUtil;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
List<String> quoteCreators = new ArrayList<String>();
quoteCreators.add(userLogin.getString("partyId"));
Boolean quoteManager = OrderMgrUtil.isQuoteManger(userLogin.getString("partyId"));
Boolean quoteReprensentative = OrderMgrUtil.isQuoteReprensentative(userLogin.getString("partyId"));
Boolean salesReprensentative =OrderMgrUtil.isSalesReprensentative(userLogin.getString("partyId"));
Boolean salesManager = OrderMgrUtil.isSalesManger(userLogin.getString("partyId"));
Boolean purchaseReprensentative = OrderMgrUtil.isPurchaseReprensentative(userLogin.getString("partyId"));
Boolean purchaseManager = OrderMgrUtil.isPurchaseManger(userLogin.getString("partyId"));
List conditions1 = FastList.newInstance();
/*conditions1.add(EntityCondition.makeCondition("validFromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(filterDate)));*/
if (salesManager || salesReprensentative ||quoteManager || purchaseManager ||purchaseReprensentative ||quoteReprensentative ) {
	quoteCreators.addAll(OrderMgrUtil.getManagerRelationship(userLogin.getString("partyId")));
	quoteCreators = OrderMgrUtil.getUserLoginIds(quoteCreators);
	if((salesManager || salesReprensentative) && ( purchaseManager || purchaseReprensentative)){
		conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "QUO_CREATED"));
		List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
		try {
			quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, ["createdStamp DESC"], null, false);
			if(UtilValidate.isNotEmpty(quoteHeaderList)){
				context.quotesList =quoteHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	if((salesManager || salesReprensentative)){
		conditions1.add(EntityCondition.makeCondition("quoteTypeId", EntityOperator.NOT_EQUAL, "PURCHASE_QUOTE"));
		conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "QUO_CREATED"));
		List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
		try {
			quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, ["createdStamp DESC"], null, false);
			if(UtilValidate.isNotEmpty(quoteHeaderList)){
				context.quotesList =quoteHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	if(( purchaseManager || purchaseReprensentative)){
		conditions1.add(EntityCondition.makeCondition("quoteTypeId", EntityOperator.EQUALS, "PURCHASE_QUOTE"));
		conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "QUO_CREATED"));	
		List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
		try {
			quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, ["createdStamp DESC"], null, false);
			if(UtilValidate.isNotEmpty(quoteHeaderList)){
				context.quotesList =quoteHeaderList;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	conditions1.add(EntityCondition.makeCondition("createdBy", EntityOperator.IN, quoteCreators));
	conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "QUO_CREATED"));
	List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
	try {
		quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, ["createdStamp DESC"], null, false);
		if(UtilValidate.isNotEmpty(quoteHeaderList)){
			context.quotesList =quoteHeaderList;
		}
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return "success";
}
