import org.ofbiz.accounting.util.UtilAccounting;

////SALES OUTSTANDINGS
BigDecimal s30DayAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",-1,30,parameters.organizationPartyId,Boolean.valueOf("false"));
context.s30DayAmount = s30DayAmount;
BigDecimal s60DayAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",30,60,parameters.organizationPartyId,Boolean.valueOf("false"));
context.s60DayAmount = s60DayAmount;
BigDecimal s90DayAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",60,90,parameters.organizationPartyId,Boolean.valueOf("false"));
context.s90DayAmount = s90DayAmount;
BigDecimal s90DayAboveAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",90,180,parameters.organizationPartyId,Boolean.valueOf("false"));
context.s90DayAboveAmount = s90DayAboveAmount;
//SALES OVERDUES
BigDecimal overDue30DayAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",-1,-30,parameters.organizationPartyId,Boolean.valueOf("true"));
context.overDue30DayAmount = overDue30DayAmount;
BigDecimal overDue60DayAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",-30,-60,parameters.organizationPartyId,Boolean.valueOf("true"));
context.overDue60DayAmount = overDue60DayAmount;
BigDecimal overDue90DayAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",-60,-90,parameters.organizationPartyId,Boolean.valueOf("true"));
context.overDue90DayAmount= overDue90DayAmount;
BigDecimal above90DayAmount=UtilAccounting.getInvoiceAmount(delegator,"SALES_INVOICE",-90,-180,parameters.organizationPartyId,Boolean.valueOf("true"));
context.above90DayAmount= above90DayAmount;

////Purchase OUTSTANDINGS
BigDecimal p30DayAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",-1,30,parameters.organizationPartyId,Boolean.valueOf("false"));
context.p30DayAmount = p30DayAmount;
BigDecimal p60DayAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",30,60,parameters.organizationPartyId,Boolean.valueOf("false"));
context.p60DayAmount = p60DayAmount;
BigDecimal p90DayAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",60,90,parameters.organizationPartyId,Boolean.valueOf("false"));
context.p90DayAmount = p90DayAmount;
BigDecimal p90DayAboveAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",90,180,parameters.organizationPartyId,Boolean.valueOf("false"));
context.p90DayAboveAmount = p90DayAboveAmount;

//Purchase OVERDUES
BigDecimal pOverDue30DayAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",-1,-30,parameters.organizationPartyId,Boolean.valueOf("true"));
context.pOverDue30DayAmount = pOverDue30DayAmount;
BigDecimal pOverDue60DayAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",-30,-60,parameters.organizationPartyId,Boolean.valueOf("true"));
context.pOverDue60DayAmount = pOverDue60DayAmount;
BigDecimal pOverDue90DayAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",-60,-90,parameters.organizationPartyId,Boolean.valueOf("true"));
context.pOverDue90DayAmount= pOverDue90DayAmount;
BigDecimal pAbove90DayAmount=UtilAccounting.getInvoiceAmount(delegator,"PURCHASE_INVOICE",-90,-180,parameters.organizationPartyId,Boolean.valueOf("true"));
context.pAbove90DayAmount= pAbove90DayAmount;

