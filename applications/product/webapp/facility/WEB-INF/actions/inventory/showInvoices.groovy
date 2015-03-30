import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;import org.ofbiz.entity.condition.EntityOperator;



String partyId = parameters.parm0;

List shipmentInvoices = delegator.findList("ShipmentInvoice",null,null,null,null,false);
Set<String> appliedInvoiceIdSet = new HashSet<String>();
for(GenericValue gv : shipmentInvoices){
	String invoiceStr = gv.getString("invoiceIds");
	if(UtilValidate.isNotEmpty(invoiceStr)){
		String[] invoiceIds = invoiceStr.split(",");
		for(String invoiceId : invoiceIds)
			appliedInvoiceIdSet.add(invoiceId.trim());
	}
}
List conditons = [];
conditons.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"PURCHASE_INVOICE"));
conditons.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_PAID"));
conditons.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,partyId));
if(UtilValidate.isNotEmpty(appliedInvoiceIdSet))
	conditons.add(EntityCondition.makeCondition("invoiceId",EntityOperator.NOT_IN,appliedInvoiceIdSet));
List invoices = delegator.findList("Invoice",EntityCondition.makeCondition(conditons),null,null,null,false);

context.invoices = invoices;