import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.*;
import javolution.util.FastList;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

if(UtilValidate.isNotEmpty(parameters.invoiceId)){
	invoice = delegator.findByPrimaryKey("Invoice",["invoiceId":parameters.invoiceId]);
	if("SALES_INVOICE".equals(invoice.invoiceTypeId)){
		List salesList = FastList.newInstance();
		List salesList1 = FastList.newInstance();
		if(UtilValidate.isNotEmpty(invoice.partyId)){
			salesList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,invoice.partyId));
			salesList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"SHIPPING_LOCATION"));
			List salesLists= delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(salesList, EntityOperator.AND), null, null, null, false);
			rows = [];
			for(GenericValue sales : salesLists){
				resultMap = [:];
				GenericValue postal =delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId")));
				if(UtilValidate.isNotEmpty(postal)){
					resultMap.contactMechId =(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("contactMechId"));
					resultMap.toName =(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("toName"));
					resultMap.address1=(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("address1"));
					rows += resultMap;
				}
			}
			//System.out.println("\n\n\n\n sales1"+rows+"\n\n\n");
			context.party1 = rows;
		}
		if(UtilValidate.isNotEmpty(invoice.partyIdFrom)) {
			salesList1.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,invoice.partyIdFrom));
			List salesLists1 = delegator.findList("FacilityAndContactMech", EntityCondition.makeCondition(salesList1, EntityOperator.AND), null, null, null, false);
			rows =[];
			for(GenericValue sales: salesLists1){
				resultMap = [:];
				GenericValue postal =delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId")));
				if(UtilValidate.isNotEmpty(postal)){
					resultMap.contactMechId = (delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("contactMechId"));
					resultMap.toName =(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("toName"));
					resultMap.address1=(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("address1"));
					rows += resultMap;
				}
			}
			//System.out.println("\n\n\n\n sales2"+rows+"\n\n\n");
			context.party2= rows;

		}
	}
	if("PURCHASE_INVOICE".equals(invoice.invoiceTypeId)){
		List purchaseList = FastList.newInstance();
		List purchaseList1 = FastList.newInstance();
		if(UtilValidate.isNotEmpty(invoice.partyId)){
			purchaseList1.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,invoice.partyId));
			List purchaseLists1 = delegator.findList("FacilityAndContactMech", EntityCondition.makeCondition(purchaseList1, EntityOperator.AND), null, null, null, false);
			rows =[];
			for(GenericValue sales: purchaseLists1){
				resultMap = [:];
				GenericValue postal = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId")));
				if(UtilValidate.isNotEmpty(postal)){
					resultMap.contactMechId = (delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("contactMechId"));
					resultMap.toName =(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("toName"));
					resultMap.address1=(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("address1"));
					rows += resultMap;
				}
			}
			//System.out.println("\n\n\n\n sales1"+rows+"\n\n\n");
			context.party2= rows;
		}
		if(UtilValidate.isNotEmpty(invoice.partyIdFrom)) {
			purchaseList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,invoice.partyIdFrom));
			purchaseList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"SHIP_ORIG_LOCATION"));
			List purchaseLists= delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(purchaseList, EntityOperator.AND), null, null, null, false);
			rows = [];
			for(GenericValue sales : purchaseLists){
				resultMap = [:];
				GenericValue postal =delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId")));
				if(UtilValidate.isNotEmpty(postal)){
					resultMap.contactMechId = (delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("contactMechId"));
					resultMap.toName =(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("toName"));
					resultMap.address1=(delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", sales.getString("contactMechId"))).getString("address1"));
					rows += resultMap;
				}
			}
			//System.out.println("\n\n\n\n sales2"+rows+"\n\n\n");
			context.party1 =rows;
		}
	}
}