import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.jdbc.ConnectionFactory
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.util.EntityFindOptions;

import sun.net.www.content.text.Generic

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp;
import java.util.Locale;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.product.facility.util.FacilityReportUtil;


String productIdParam = parameters.productId;


conditions = [];
GenericDelegator delegator = delegator;
Timestamp fromDate = UtilDateTime.nowTimestamp();
String facilityId = UtilProperties.getPropertyValue("general.properties", "MANUFACTURING_FACILITY_ID");
Set<String> productIdSet = new HashSet<String>();
List<GenericValue> productAssocList = new ArrayList<String>();
List condition = [];
condition.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"MANUF_COMPONENT"));
if(UtilValidate.isNotEmpty(productIdParam))
    condition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productIdParam));

EntityFindOptions findOptions = new EntityFindOptions();
findOptions.setDistinct(true);
productAssocList = delegator.findList("ProductAssoc",EntityCondition.makeCondition(condition),["productId","productIdTo"] as Set,["productId ASC"],findOptions,false);
List<Map<String,Object>> productionReportList = new ArrayList<Map<String,Object>>();
Set<String> products = new HashSet<String>();
for (GenericValue gv : productAssocList) {
	products.add(gv.getString("productId"));
    products.add(gv.getString("productIdTo"));
}
for (String productId : products) {
	Map<String,Object> productionReport = new HashMap<String,Object>();
	productionReport.put("productId", productId);
	productionReport.put("quantityUomId", "");
	productionReport.put("openingBalance", FacilityReportUtil.getOpeningOrClosingBalance(dispatcher, productId, UtilDateTime.addDaysToTimestamp(fromDate,-1), facilityId));

	Timestamp monthStartDate = UtilDateTime.getMonthStart(fromDate);
	Timestamp yearStartDate = UtilDateTime.getYearStart(fromDate);
    Timestamp endDate = UtilDateTime.getDayEnd(fromDate);

	productionReport.put("todayReceive", FacilityReportUtil.getReceivedAndProducedQty(dispatcher, productId, fromDate,endDate,null));
	productionReport.put("monthReceive", FacilityReportUtil.getReceivedAndProducedQty(dispatcher, productId, monthStartDate,endDate,null));
	productionReport.put("yearReceive", FacilityReportUtil.getReceivedAndProducedQty(dispatcher, productId, yearStartDate,endDate,null));
	productionReport.put("todayConsumption", getConsumption(productId,UtilDateTime.getDayStart(fromDate),endDate));
	productionReport.put("monthConsumption", getConsumption(productId,monthStartDate,endDate));
	productionReport.put("yearConsumption", getConsumption(productId,yearStartDate,endDate));
	productionReport.put("closingBalance", FacilityReportUtil.getOpeningOrClosingBalance(dispatcher, productId, endDate, facilityId));
	
	productionReportList.add(productionReport);
}	

public BigDecimal getConsumption(String productId,Timestamp start, Timestamp end){
	Timestamp date = UtilDateTime.nowTimestamp();
    String helperName = delegator.getGroupHelperName("org.ofbiz");    // gets the helper (localderby, localmysql, localpostgres, etc.) for your entity group org.ofbiz
    Connection conn = ConnectionFactory.getConnection(helperName);
    BigDecimal totalQuantity=BigDecimal.ZERO;
    PreparedStatement statement = conn.prepareStatement( "SELECT I.`PRODUCT_ID`,SUM(A.`QUANTITY`) FROM work_effort W JOIN `work_effort_inventory_assign`\tA ON W.`WORK_EFFORT_ID`=A.`WORK_EFFORT_ID`\n" +
            "\tJOIN inventory_item I ON A.`INVENTORY_ITEM_ID`= I.inventory_item_id \n" +
            "\tAND W.`ACTUAL_START_DATE` >= ? AND W.`ACTUAL_START_DATE` <= ? \n" +
            "\tAND I.`PRODUCT_ID` = ? GROUP BY I.`PRODUCT_ID`;");

    statement.setTimestamp(1, start);
    statement.setTimestamp(2, end);
    statement.setString(3, productId);
    ResultSet results = statement.executeQuery();
    if(results.next()){
        totalQuantity = results.getBigDecimal(2);
    }
    results.close();
    statement.close();
    BigDecimal salesFigure = getSalesFigure(productId,start,end);
    return salesFigure==null?totalQuantity:totalQuantity.add(salesFigure);
}

public BigDecimal getSalesFigure(String productId,Timestamp start, Timestamp end) {
    Timestamp date = UtilDateTime.nowTimestamp();
    String helperName = delegator.getGroupHelperName("org.ofbiz");    // gets the helper (localderby, localmysql, localpostgres, etc.) for your entity group org.ofbiz
    Connection conn = ConnectionFactory.getConnection(helperName);
    BigDecimal totalQuantity=BigDecimal.ZERO;
    PreparedStatement statement = conn.prepareStatement("SELECT SUM(COALESCE(II.QUANTITY,'0')) - SUM(COALESCE(II.CANCEL_QUANTITY,'0')) AS QUANTITY FROM item_issuance II JOIN \n" +
            "\tSHIPMENT S ON II.`SHIPMENT_ID`=S.`SHIPMENT_ID` JOIN INVENTORY_ITEM I ON II.INVENTORY_ITEM_ID=I.INVENTORY_ITEM_ID\n" +
            "\t AND S.`SHIPMENT_TYPE_ID`='SALES_SHIPMENT' AND II.ISSUED_DATE_TIME >= ? AND II.ISSUED_DATE_TIME <= ?\n" +
            "\t AND I.PRODUCT_ID=?;");

    statement.setTimestamp(1, start);
    statement.setTimestamp(2, end);
    statement.setString(3, productId);
    ResultSet results = statement.executeQuery();
    BigDecimal result = BigDecimal.ZERO;
    if(results.next()){
        result = results.getBigDecimal(1);
    }
    results.close();
    statement.close();
    return result;
}
context.fromDate = fromDate;
context.productionReportList = productionReportList;


