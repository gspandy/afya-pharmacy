import org.ofbiz.entity.condition.EntityCondition;

import org.ofbiz.entity.condition.EntityCondition;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.*;


invoiceId = parameters.get("invoiceId");
String orderId = null;
invoice = delegator.findByPrimaryKey("Invoice", [invoiceId : invoiceId]);
context.invoice = invoice;
invConditions = [];

currency = parameters.currency;        // allow the display of the invoice in the original currency, the default is to display the invoice in the default currency
BigDecimal conversionRate = new BigDecimal("1");
ZERO = BigDecimal.ZERO;
decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

if (invoice) {
    // each invoice of course has two billing addresses, but the one that is relevant for purchase invoices is the PAYMENT_LOCATION of the invoice
    // (ie Accounts Payable address for the supplier), while the right one for sales invoices is the BILLING_LOCATION (ie Accounts Receivable or
    // home of the customer.)
    if ("PURCHASE_INVOICE".equals(invoice.invoiceTypeId)) {
        billingAddress = InvoiceWorker.getSendFromAddress(invoice);
    } else {
        billingAddress = InvoiceWorker.getBillToAddress(invoice);
    }
    if (billingAddress) {
        context.billingAddress = billingAddress;
    }
    billingParty = InvoiceWorker.getBillToParty(invoice);
    context.billingParty = billingParty;
    sendingParty = InvoiceWorker.getSendFromParty(invoice);
    context.sendingParty = sendingParty;
    
    logoImageUrl = null;
    GenericValue senderDetails = delegator.findOne("PartyGroup",["partyId":sendingParty.partyId],true);
    context.senderDetails = senderDetails;
    // the logo
    if (senderDetails?.logoImageUrl) {
        logoImageUrl = senderDetails.logoImageUrl;
    }
    context.logoImageUrl = logoImageUrl;
    
    //telephone
    phones = delegator.findByAnd("PartyContactMechPurpose", [partyId : sendingParty.partyId, contactMechPurposeTypeId : "PRIMARY_PHONE"]);
    selPhones = EntityUtil.filterByDate(phones, nowTimestamp, "fromDate", "thruDate", true);
    if (selPhones) {
        context.phone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : selPhones[0].contactMechId]);
    } else {
        context.phone = [:];
    }

	//Company telephone
	companyPhones = delegator.findByAnd("PartyContactMechPurpose", [partyId : "Company", contactMechPurposeTypeId : "PRIMARY_PHONE"]);
	companyNumbers = EntityUtil.filterByDate(companyPhones, nowTimestamp, "fromDate", "thruDate", true);
	if (companyNumbers) {
		context.companyPhone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : companyNumbers[0].contactMechId]);
	} else {
		context.companyPhone = [:];
	}

    if (currency && !invoice.getString("currencyUomId").equals(currency)) {
        conversionRate = InvoiceWorker.getInvoiceCurrencyConversionRate(invoice);
        invoice.currencyUomId = currency;
        invoice.invoiceMessage = " converted from original with a rate of: " + conversionRate.setScale(8, rounding);
    }

    invoiceItemAdjustments = delegator.findList("InvoiceItem", EntityCondition.makeCondition([invoiceId : invoice.invoiceId, invoiceItemTypeId : "INVOICE_ITM_ADJ"]), null, null, null, false);
    context.invoiceItemAdjustments = invoiceItemAdjustments;

    EntityCondition invCon1 = EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM");
    EntityCondition invCon2 = EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_RENTAL_ITEM");
    EntityCondition invCon3 = EntityCondition.makeCondition([invCon1,invCon2],EntityOperator.OR)
    EntityCondition invCon4 = EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId);
    EntityCondition invCon5 = EntityCondition.makeCondition([invCon3,invCon4],EntityOperator.AND);
    invoiceItems = delegator.findList("InvoiceItemGroupByProduct",invCon5,null,null,null,false);
    //invoice.getRelatedOrderBy("InvoiceItem", ["invoiceItemSeqId"]);
    invoiceItemsConv = FastList.newInstance();
    vatTaxesByType = FastMap.newInstance();
    invoiceItems.each { invoiceItem ->
        invoiceItem.amount = invoiceItem.getBigDecimal("amount").multiply(conversionRate).setScale(decimals, rounding);
        invoiceItemsConv.add(invoiceItem);
        // get party tax id for VAT taxes: they are required in invoices by EU
        // also create a map with tax grand total amount by VAT tax: it is also required in invoices by UE
        taxRate = invoiceItem.getRelatedOne("TaxAuthorityRateProduct");
        if (taxRate && "VAT_TAX".equals(taxRate.taxAuthorityRateTypeId)) {
            taxInfos = EntityUtil.filterByDate(delegator.findByAnd("PartyTaxAuthInfo", [partyId : billingParty.partyId, taxAuthGeoId : taxRate.taxAuthGeoId, taxAuthPartyId : taxRate.taxAuthPartyId]), invoice.invoiceDate);
            taxInfo = EntityUtil.getFirst(taxInfos);
            if (taxInfo) {
                context.billingPartyTaxId = taxInfo.partyTaxId;
            }
            vatTaxesByTypeAmount = vatTaxesByType[taxRate.taxAuthorityRateSeqId];
            if (!vatTaxesByTypeAmount) {
                vatTaxesByTypeAmount = 0.0;
            }
            vatTaxesByType.put(taxRate.taxAuthorityRateSeqId, vatTaxesByTypeAmount + invoiceItem.amount);
        }
    }
    context.vatTaxesByType = vatTaxesByType;
    context.vatTaxIds = vatTaxesByType.keySet().asList();

    context.invoiceItems = invoiceItemsConv;

    invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
    invoiceNoTaxTotal = InvoiceWorker.getInvoiceNoTaxTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
    context.invoiceTaxTotal = InvoiceWorker.getInvoiceTaxTotal(invoice);
    context.invoiceTotal = invoiceTotal;
    context.invoiceNoTaxTotal = invoiceNoTaxTotal;

                //*________________this snippet was added for adding Tax ID in invoice header if needed _________________

               sendingTaxInfos = sendingParty.getRelated("PartyTaxAuthInfo");
               billingTaxInfos = billingParty.getRelated("PartyTaxAuthInfo");
               sendingPartyTaxId = null;
               billingPartyTaxId = null;

               if (billingAddress) {
                   sendingTaxInfos.eachWithIndex { sendingTaxInfo, i ->
                       if (sendingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
                            sendingPartyTaxId = sendingTaxInfos[i-1].partyTaxId;
                       }
                   }
                   billingTaxInfos.eachWithIndex { billingTaxInfo, i ->
                       if (billingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
                            billingPartyTaxId = billingTaxInfos[i-1].partyTaxId;
                       }
                   }
               }
               if (sendingPartyTaxId) {
                   context.sendingPartyTaxId = sendingPartyTaxId;
               }
               if (billingPartyTaxId && !context.billingPartyTaxId) {
                   context.billingPartyTaxId = billingPartyTaxId;
               }
               //________________this snippet was added for adding Tax ID in invoice header if needed _________________*/


    terms = invoice.getRelated("InvoiceTerm");
    context.terms = terms;

    paymentAppls = delegator.findByAnd("PaymentApplication", [invoiceId : invoiceId]);
    context.payments = paymentAppls;

    orderItemBillings = delegator.findByAnd("OrderItemBilling", [invoiceId : invoiceId], ['orderId']);
    orders = new LinkedHashSet();

    orderItemBillings.each { orderIb ->
        orders.add(orderIb.orderId);
        orderId=orderIb.orderId;
    }
    context.orders = orders;

    invoiceStatus = invoice.getRelatedOne("StatusItem");
    context.invoiceStatus = invoiceStatus;

    edit = parameters.editInvoice;
    if ("true".equalsIgnoreCase(edit)) {
        invoiceItemTypes = delegator.findList("InvoiceItemType", null, null, null, null, false);
        context.invoiceItemTypes = invoiceItemTypes;
        context.editInvoice = true;
    }

    // format the date
    if (invoice.invoiceDate) {
        context.invoiceDate = invoice.invoiceDate;
    } else {
        context.invoiceDate = "N/A";
    }
}
context.billingParty=delegator.findOne("PartyGroup",["partyId":billingParty.partyId],false);
formToIssue = "No";

if(UtilValidate.isNotEmpty(orderId) || orderId != null) {
    orderHeaderGV = delegator.findByPrimaryKey("OrderHeader",["orderId":orderId]);
    context.orderHeaderGV = orderHeaderGV;
    if(orderHeaderGV) {
        String formToIssueStr = orderHeaderGV.getString("formToIssue");
        if("C Form".equals(formToIssueStr))
            formToIssue = "Yes";
    }
    context.formToIssue = formToIssue;
}

taxAdjustments = delegator.findByAnd("OrderAdjustmentGrouped",["orderId":orderId,"overrideGlAccountId":null]);
orderAdjustmentGroupeds = delegator.findByAnd("OrderAdjustmentGrouped",["orderId":orderId]);
List glAccounts = [];
orderAdjustmentGroupeds.each { orderAdjustmentGrouped ->
    if(orderAdjustmentGrouped.overrideGlAccountId){
        glAccounts.add(delegator.findOne("GlAccount",["glAccountId":orderAdjustmentGrouped.overrideGlAccountId],false));
    }
}

/*Collections.sort(glAccounts, new Comparator<GenericValue>() {
    @Override
    public int compare(GenericValue o1, GenericValue o2) {
    GenericValue val1 = (GenericValue)o1;
    GenericValue val2 = (GenericValue)o2;
    Long sortOrder1 = new Long(val1.getLong("sortOrder") == null ? 0 : val1.getLong("sortOrder"));
    Long sortOrder2 = new Long(val2.getLong("sortOrder") == null ? 0 : val2.getLong("sortOrder"));
    return sortOrder1.compareTo(sortOrder2);
    }
});*/

List taxAdjustmentsAssending = [];
glAccounts.each { glAccount ->
    taxAdjustmentsAssending.add(delegator.findByAnd("OrderAdjustmentGrouped",["orderId":orderId,"overrideGlAccountId":glAccount.glAccountId]).get(0));
}
taxAdjustmentsAssending.addAll(taxAdjustments);

context.taxAdjustments = taxAdjustmentsAssending;

List<String> orderBy = UtilMisc.toList("sourcePercentage");
    oInvCon = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
    orderAdjInvoiceGrouped = delegator.findList("OrderAdjustmentsGrouped", oInvCon, null, orderBy ,null, false);

context.orderAdjInvoiceGrouped = orderAdjInvoiceGrouped;

EntityCondition invItemCon1 = EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.NOT_EQUAL,"INV_FPROD_ITEM");
EntityCondition invItemCon2 = EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.NOT_EQUAL,"PINV_FPROD_ITEM");
EntityCondition invItemCon3 = EntityCondition.makeCondition([invItemCon1,invItemCon2],EntityOperator.AND)
EntityCondition invItemCon4 = EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId);
EntityCondition invItemCon5 = EntityCondition.makeCondition([invItemCon3,invItemCon4],EntityOperator.AND);
invoiceItemsTax = delegator.findList("InvoiceItem",invItemCon5,null,null,null,false);


context.invoiceItemsTax = invoiceItemsTax;

EntityCondition invItemVatCon1 = EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId);
EntityCondition invItemVatCon2 = EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"ITM_SALES_TAX");
EntityCondition invItemVatCon3 = EntityCondition.makeCondition([invItemVatCon1,invItemVatCon2],EntityOperator.AND);
invoiceItemsVat = delegator.findList("InvoiceItem",invItemVatCon3,null,null,null,false);


context.invoiceItemsVat = invoiceItemsVat;


oagList = delegator.findByAnd("OrderAdjustmentGrouped",["orderId":orderId,"taxAuthPartyId":"BED"]);
if(oagList != null && oagList.size() > 0)
    context.bed = oagList.get(0).getBigDecimal("amount");

oagList = delegator.findByAnd("OrderAdjustmentGrouped",["orderId":orderId,"taxAuthPartyId":"EDUCESS"]);
if(oagList != null && oagList.size() > 0)
    context.eduCess = oagList.get(0).getBigDecimal("amount");

oagList = delegator.findByAnd("OrderAdjustmentGrouped",["orderId":orderId,"taxAuthPartyId":"SHCESS"]);
if(oagList != null && oagList.size() > 0)
    context.shEduCess = oagList.get(0).getBigDecimal("amount");

List<GenericValue> shipmentItemBillingList = delegator.findByAnd("ShipmentItemBilling",["invoiceId":invoiceId]);
if(shipmentItemBillingList != null && shipmentItemBillingList.size() > 0) {
    GenericValue shipmentItemBilling = EntityUtil.getFirst(shipmentItemBillingList);
    String shipmentId = shipmentItemBilling.getString("shipmentId");
    context.shipmentId = shipmentId;
}

context.orderId = orderId;
