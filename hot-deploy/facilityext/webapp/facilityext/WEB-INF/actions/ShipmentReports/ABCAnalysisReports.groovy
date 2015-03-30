import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;
import org.sme.order.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Timestamp;
import java.math.RoundingMode;
import javolution.util.FastList;
import java.text.*;
import org.ofbiz.order.report.ComparativeOrderItemReport;
import java.util.Comparator;

facilityId = parameters.facilityId;

SimpleDateFormat dateFormat = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
NumberFormat currency = NumberFormat.getCurrencyInstance();
NumberFormat percent = NumberFormat.getPercentInstance();
DecimalFormat myFormatter = new DecimalFormat("###.##");

if(UtilValidate.isEmpty(parameters.fromDate) && UtilValidate.isEmpty(parameters.thruDate)){
	return "error";
}

java.util.Date parsedDate1 = new java.util.Date();
if(UtilValidate.isNotEmpty(parameters.fromDate))
 parsedDate1 = dateFormat.parse(parameters.fromDate);
java.sql.Timestamp fromDate = UtilDateTime.getDayStart( new java.sql.Timestamp(parsedDate1.getTime()) );

java.util.Date parsedDate2 = null;
if(UtilValidate.isNotEmpty(parameters.thruDate))
 parsedDate2 = dateFormat.parse(parameters.thruDate);
 
java.sql.Timestamp thruDate = null;
if(UtilValidate.isNotEmpty(parsedDate2))
 thruDate = UtilDateTime.getDayEnd( new java.sql.Timestamp(parsedDate2.getTime()) );

List manufacturingIssuanceCondition = FastList.newInstance();
List manufacturingIssuanceList1 = null;

List<Map> manuFecList;
conditions = [];
conditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
if(UtilValidate.isNotEmpty(thruDate))
	conditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	
conditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
conditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
List OrderAbcAnalysisList = delegator.findList("ProductInvItemsAbcAnalysis", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);

List<Map<String,Object>> productInvItemList = new ArrayList<Map<String,Object>>();

for(GenericValue abcAnalysisGv : OrderAbcAnalysisList){
	Map<String,Object> map = new HashMap<String,Object>();
	Boolean isNotExists = Boolean.TRUE;
	for(Map<String,Object> oldMap : productInvItemList ){
		if(oldMap.get("productId") == abcAnalysisGv.getString("productId")){
		double quantity = ((double)oldMap.get("quantity")) + abcAnalysisGv.getDouble("quantityOnHandTotal");
		oldMap.put("quantity", quantity);
		double totalUnitCost = abcAnalysisGv.getDouble("unitCost") * abcAnalysisGv.getDouble("quantityOnHandTotal");
		double totalCost = ((double)oldMap.get("value")) + totalUnitCost;
		oldMap.put("value", totalCost );
		isNotExists = Boolean.FALSE;
		}else{
		isNotExists = Boolean.TRUE;
		}
	}
	
	if(isNotExists){
		map.put("productId", abcAnalysisGv.getString("productId"));
		map.put("quantity", abcAnalysisGv.getDouble("quantityOnHandTotal").intValue() );
		double totalUnitCost = abcAnalysisGv.getDouble("unitCost") * abcAnalysisGv.getDouble("quantityOnHandTotal");
		map.put("value", totalUnitCost );
		productInvItemList.add(map);
	}
	
}


double totalValue = 0d;
List<Map<String,Object>> abcAnalysis = new ArrayList<Map<String,Object>>();
	for(Map<String,Object> abcAnalysisMap : productInvItemList){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("productId", abcAnalysisMap.get("productId"));
		map.put("productDsc", ComparativeOrderItemReport.getProductDescription(abcAnalysisMap.get("productId"),delegator) );
		map.put("quantity", ((double)abcAnalysisMap.get("quantity")).intValue() );
		double totalUnitCost = ((double)abcAnalysisMap.get("value"));
		map.put("value", totalUnitCost );
		abcAnalysis.add(map);
		totalValue = totalValue + totalUnitCost;
	}
	for(int i = 0 ; i < abcAnalysis.size ; i++){
		double usage = 0d;
		double value = (double) abcAnalysis.get(i).get("value");
		usage = (value / totalValue) * 100; 
		abcAnalysis.get(i).put( "usage" , myFormatter.format(usage) );
		abcAnalysis.get(i).put( "usageSorting" , usage );
		abcAnalysis.get(i).put("value",value );
	}
	
/*Collections.sort(abcAnalysis, new Comparator<Map>() {
	@Override
	public int compare(Map o1, Map o2) {
	Map val1 = (Map)o1;
	Map val2 = (Map)o2;
	Long sortOrder1 = new Long(val1.get("usageSorting").intValue() == null ? 0 : val1.get("usageSorting").intValue() );
	Long sortOrder2 = new Long(val2.get("usageSorting").intValue() == null ? 0 : val2.get("usageSorting").intValue() );
	return sortOrder2.compareTo(sortOrder1);
	}
});*/

double commulativeValue = 0d;
for(int i = 0 ; i < abcAnalysis.size ; i++){
	commulativeValue = commulativeValue + abcAnalysis.get(i).get("usageSorting");
	abcAnalysis.get(i).put( "commulativeValue" , myFormatter.format(commulativeValue) );
}


context.totalValue = totalValue;
context.abcAnalysis = abcAnalysis;
return "success"