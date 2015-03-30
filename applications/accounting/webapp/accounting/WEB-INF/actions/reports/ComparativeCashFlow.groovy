import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilValidate;


GenericValue company = delegator.findOne("PartyGroup",true,"partyId",parameters.organizationPartyId);
String customTimePeriodId1 = parameters.customTimePeriodId1;	
customTimePeriod1 = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriodId1);
context.customTimePeriod1 = customTimePeriod1;
context.customTimePeriodId1 = customTimePeriodId1;

String customTimePeriodId2 = parameters.customTimePeriodId2;	
customTimePeriod2 = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriodId2);
context.customTimePeriod2 = customTimePeriod2;
context.customTimePeriodId2 = customTimePeriodId2;


Map periodMap1 = UtilAccounting.getReportingPeriod(delegator,customTimePeriodId1);
context.periodMap1 = periodMap1;

Map periodMap2 = UtilAccounting.getReportingPeriod(delegator,customTimePeriodId2);
context.periodMap2 = periodMap2;

context.currentLiabilityBalance=balanceTotal-openingTotal+taxAmount;
