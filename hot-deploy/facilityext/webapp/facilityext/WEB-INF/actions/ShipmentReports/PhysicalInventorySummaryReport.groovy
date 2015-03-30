import java.util.*;
import java.text.*;

import org.ofbiz.entity.condition.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.entity.GenericValue;

import java.sql.Timestamp;
import java.text.*;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import javax.mail.Session;

import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;

import com.smebiz.common.PartyUtil;


facilityId = parameters.facilityId;
String productTypeId = ( parameters.productTypeId) ;
productId = (parameters.productId);
print "\n\n\n productId"+productId+"\n\n\n"
print "\n\n\n productTypeId"+productTypeId+"\n\n\n"
summaryList = [];
InventorySummaryList = [];
EntityCondition condition = null;
EntityCondition condition1 = null;
List<EntityCondition> allCondition = [] as ArrayList;

if(productTypeId) {
	condition1= EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, productTypeId);
	
	gv= delegator.findOne("ProductType", false, "productTypeId", productTypeId) ;
	
	condition = EntityCondition.makeCondition("productType", EntityOperator.EQUALS, gv.description);
	allCondition.add(condition);
}

if(productId) {
	condition= EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId); 
	allCondition.add(condition);
}

summaryList = delegator.findList("ProductDimension", EntityCondition.makeCondition( allCondition ),null, null, null, false);
summaryList.each { list -> 
	condition = EntityCondition.makeCondition("productId", EntityOperator.EQUALS,list.productId);
	InventorySummaryList += delegator.findList("InventoryItemFact", condition,null, null, null, false);
}

rows = [];

for(int i = 0; i < InventorySummaryList.size(); i++) {
	resultMap = [:];
	resultMap.inventoryItemId = InventorySummaryList.get(i).inventoryItemId
	resultMap.datetimeReceived = InventorySummaryList.get(i).datetimeReceived
	//BigDecimal truncated= InventorySummaryList.get(i).quantityOnHandTotal.setScale(0,BigDecimal.ROUND_DOWN);
	//resultMap.quantityOnHandTotal = truncated
	//resultMap.productId = InventorySummaryList.get(i).productId
	//truncated= InventorySummaryList.get(i).availableToPromiseTotal.setScale(0,BigDecimal.ROUND_DOWN);
	//resultMap.availableToPromiseTotal = truncated
	//resultMap.locationSeqId = InventorySummaryList.get(i).locationSeqId
	BigDecimal truncated = 0.00;
	if(InventorySummaryList.get(i).quantityOnHandTotal != null) {
		truncated= InventorySummaryList.get(i).quantityOnHandTotal.setScale(0,BigDecimal.ROUND_DOWN);
		resultMap.quantityOnHandTotal = truncated
	}
	resultMap.productId = InventorySummaryList.get(i).productId

	if(InventorySummaryList.get(i).availableToPromiseTotal != null) {
		truncated= InventorySummaryList.get(i).availableToPromiseTotal.setScale(0,BigDecimal.ROUND_DOWN);
		resultMap.availableToPromiseTotal  = truncated
	}
	resultMap.locationSeqId = InventorySummaryList.get(i).locationSeqId

	rows += resultMap;
}

context.resultList = rows;

Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
String headOfficeImg = "./framework/images/webapp/images/head_office.png";
String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";

Map<Object, Object> jrParameters = new HashMap<Object, Object>();
jrParameters.put("fromDate", "title");
jrParameters.put("headOfficeImg", headOfficeImg);
jrParameters.put("verticalLineImg", verticalLineImg);
jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
jrParameters.put("city", companyHeaderInfoMap.get("city"));
jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
jrParameters.put("email", companyHeaderInfoMap.get("email"));
jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));


org.ofbiz.entity.transaction.TransactionUtil.begin();
JRMapCollectionDataSource jrDataSource = new JRMapCollectionDataSource(rows);
if (jrDataSource != null) {
	request.setAttribute("jrDataSource", jrDataSource);
	request.setAttribute("jrParameters", jrParameters);
}
org.ofbiz.entity.transaction.TransactionUtil.commit();
return "success"
