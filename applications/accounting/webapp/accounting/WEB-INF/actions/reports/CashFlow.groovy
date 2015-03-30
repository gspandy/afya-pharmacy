import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilValidate;


GenericValue company = delegator.findOne("PartyGroup",true,"partyId",parameters.organizationPartyId);
println " ************ Company ********* "+ company;
String customTimePeriodId = parameters.customTimePeriodId;	
customTimePeriod = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriodId);
context.customTimePeriod = customTimePeriod;
context.customTimePeriodId = customTimePeriodId;
Map periodMap = UtilAccounting.getReportingPeriod(delegator,customTimePeriodId);
context.periodMap = periodMap;

context.periodMap = periodMap;
GenericValue currentLiabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "CURRENT_LIABILITY"), true);
List currentLiabilityAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(currentLiabilityGlAccountClass);

println " currentLiabilityAccountClassIds" +currentLiabilityAccountClassIds;
List mainAndExprs = FastList.newInstance();

mainAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,parameters.organizationPartyId));
mainAndExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
mainAndExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL"));
mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodMap.get("fromDate")));
mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, periodMap.get("thruDate")));

List assetAndExprs = FastList.newInstance(mainAndExprs);
assetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentLiabilityAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(assetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null,true);
transactionTotalsMap = [:];
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // equities are accounts of class CREDIT: the balance is given by credits minus debits
    BigDecimal balance = creditAmount.subtract(debitAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}

balanceTotal=0;
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}


Map currentLiabilityOpeningBalances = [:];
	Map result= dispatcher.runSync("getPreviousTimePeriod",UtilMisc.toMap("userLogin", userLogin,"customTimePeriodId",customTimePeriodId));
	previousTimePeriod = result.previousTimePeriod;
	if(previousTimePeriod==null)
		previousTimePeriod=["customTimePeriodId":""];
	timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, parameters.organizationPartyId));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentLiabilityAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,previousTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
       	Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        currentLiabilityOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    
openingTotal=0;
accountBalanceList = UtilMisc.sortMaps(currentLiabilityOpeningBalances.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    openingTotal = openingTotal + ((accountBalance.balance==null)?BigDecimal.ZERO:accountBalance.balance);
}

taxAmount = 0; 
//org.ofbiz.accounting.util.UtilAccounting.getTaxAmount(delegator,customTimePeriodId,parameters.organizationIdPartyId);

context.currentLiabilityBalance=balanceTotal-openingTotal+taxAmount;
