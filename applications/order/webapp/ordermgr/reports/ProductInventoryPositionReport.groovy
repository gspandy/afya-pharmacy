

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.sme.order.util.OrderMgrUtil;
import org.ofbiz.entity.GenericDelegator;
import javolution.util.FastList;

String productId = request.getParameter("productId");
DecimalFormat myFormatter= new DecimalFormat("###.##");
rows=[];
jrParameters =[:];
Map<String, Object> productInventoryReport = new HashMap<String, Object>();
List conditions = FastList.newInstance();

if( UtilValidate.isEmpty(request.getParameter("fromOrderDate"))&& UtilValidate.isEmpty(productId)){
	request.setAttribute("_ERROR_MESSAGE_", "Product Id or From date cannot be empty ");
	context.showScreen = "message";
	return;
}
if (UtilValidate.isNotEmpty(productId)) {
	conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
}
if (UtilValidate.isNotEmpty(request.getParameter("fromOrderDate"))) {
	conditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO,new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse( request.getParameter("fromOrderDate")).getTime())));
	jrParameters.put("fromDate",new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse( request.getParameter("fromOrderDate")).getTime()));
}
if (UtilValidate.isNotEmpty(request.getParameter("thruOrderDate"))) {
	conditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.LESS_THAN_EQUAL_TO,new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(request.getParameter("thruOrderDate")).getTime())));
	jrParameters.put("thruDate",new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse( request.getParameter("thruOrderDate")).getTime()));
}
conditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.NOT_EQUAL,null));
conditions.add(EntityCondition.makeCondition("orderItemStatusId",EntityOperator.NOT_EQUAL ,"ITEM_CANCELLED"));
conditions.add(EntityCondition.makeCondition("orderItemStatusId",EntityOperator.NOT_EQUAL ,"ITEM_REJECTED"));
conditions.add(EntityCondition.makeCondition("orderItemStatusId",EntityOperator.NOT_EQUAL ,"ITEM_COMPLETED"));
conditions.add(EntityCondition.makeCondition("orderItemStatusId",EntityOperator.NOT_EQUAL ,"ITEM_CREATED"));
conditions.add(EntityCondition.makeCondition("orderTypeId",EntityOperator.EQUALS ,"SALES_ORDER"));

List productList = delegator.findList("OrderReportGroupByProduct",EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);

if(UtilValidate.isNotEmpty(productId)){
	resultOutput =  dispatcher.runSync("getInventoryAvailableByFacility", [facilityId : request.getParameter("facilityId"), productId : productId]);
	for(GenericValue product:productList){
		String uomDescription = " ";
		productGv =delegator.findByPrimaryKey("Product", ["productId" :productId ]);
		if(UtilValidate.isNotEmpty(productGv)){
			uomGv=delegator.findByPrimaryKey("Uom", ["uomId" :productGv.get("quantityUomId")]);
			if(UtilValidate.isNotEmpty(uomGv))
				uomDescription = uomGv.getString("description");
		}
		BigDecimal qtyOrder =product.getString("quantity").toBigDecimal();
		if(("FINISHED_GOOD").equals(productGv.getString("productTypeId"))){
			resultMap = [:];
			resultMap.finishedGood  =OrderMgrUtil.getProductDescription(productId);
			resultMap.ordered= myFormatter.format( qtyOrder)+" "+uomDescription;
			resultMap.available=myFormatter.format(((resultOutput.availableToPromiseTotal)<0?0:(resultOutput.availableToPromiseTotal)))+" "+uomDescription;
			resultMap.rawMaterial=" ";
			resultMap.availableRaw=" ";
			resultMap.requried=" ";
			resultMap.product=OrderMgrUtil.getProductDescription(productId);
			rows+=resultMap;
			if(qtyOrder>resultOutput.availableToPromiseTotal){
				List conditionsRaw = FastList.newInstance();
				conditionsRaw.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
				conditionsRaw.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"MANUF_COMPONENT"));
				List productRawList = delegator.findList("ProductAssoc",EntityCondition.makeCondition(conditionsRaw, EntityOperator.AND), null, null, null, false);
				for(GenericValue productRaw:productRawList){
					BigDecimal requried=productRaw.getString("quantity").toBigDecimal();
					requriedQtyFinished=((qtyOrder-((resultOutput.availableToPromiseTotal)<0?0:(resultOutput.availableToPromiseTotal)))*requried);
					resultMap1 = [:];
					String uomDescription1 = " ";
					productAssocId = productRaw.getString("productIdTo");
					if(UtilValidate.isNotEmpty(productAssocId)){
						productGv1 =delegator.findByPrimaryKey("Product", ["productId" :productAssocId ]);
						uomGv1=delegator.findByPrimaryKey("Uom", ["uomId" :productGv1.get("quantityUomId")]);
						if(UtilValidate.isNotEmpty(uomGv1)){
							uomDescription1 = uomGv1.getString("description");
							}
						resultOutput1 =  dispatcher.runSync("getInventoryAvailableByFacility", [facilityId : request.getParameter("facilityId"), productId :productAssocId]);
						BigDecimal availableQty =((resultOutput1.availableToPromiseTotal)<0?0:(resultOutput1.availableToPromiseTotal));
						BigDecimal requriedRawMaterials=(availableQty >requriedQtyFinished?0:(requriedQtyFinished-availableQty));
						resultOutput1 =  dispatcher.runSync("getInventoryAvailableByFacility", [facilityId : request.getParameter("facilityId"), productId :productAssocId]);
						resultMap1.finishedGood  ="";
						resultMap1.ordered=" ";
						resultMap1.available=" ";
						resultMap1.rawMaterial=OrderMgrUtil.getProductDescription(productAssocId);
						resultMap1.availableRaw=myFormatter.format(((resultOutput1.availableToPromiseTotal)<0?0:(resultOutput1.availableToPromiseTotal)))+" "+uomDescription1;
						resultMap1.requried=myFormatter.format((requriedRawMaterials))+" "+uomDescription1;
						resultMap1.product=OrderMgrUtil.getProductDescription(productId);
						rows+=resultMap1;
					}
				}
			}
		}
	}
}
if(UtilValidate.isEmpty(productId)){
	for(GenericValue product:productList){
		BigDecimal qtyOrder =product.getString("quantity").toBigDecimal();
		String productFinGoodId=null;
		String uomDescription  = " " ;
		productFinGoodId =product.getString("productId");
		if(UtilValidate.isNotEmpty(productFinGoodId)){
			productGv =delegator.findByPrimaryKey("Product", ["productId" :productFinGoodId ]);
			uomGv=delegator.findByPrimaryKey("Uom", ["uomId" :productGv.get("quantityUomId")]);
			if(UtilValidate.isNotEmpty(uomGv)){
				uomDescription = uomGv.getString("description");
				
				}
			if(("FINISHED_GOOD").equals(productGv.getString("productTypeId"))){
				resultOutput =  dispatcher.runSync("getInventoryAvailableByFacility", [facilityId : request.getParameter("facilityId"), productId : product.getString("productId")]);
				resultMap = [:];
				resultMap.finishedGood  =OrderMgrUtil.getProductDescription(product.getString("productId"));
				resultMap.ordered=myFormatter.format(qtyOrder)+" "+uomDescription;
				resultMap.available=myFormatter.format(((resultOutput.availableToPromiseTotal)<0?0:(resultOutput.availableToPromiseTotal)))+" "+uomDescription;
				resultMap.rawMaterial="";
				resultMap.availableRaw="";
				resultMap.requried=" ";
				resultMap.product=OrderMgrUtil.getProductDescription(productFinGoodId);
				rows+=resultMap;
				BigDecimal requriedQty=null;
				if((qtyOrder>resultOutput.availableToPromiseTotal )){
					List conditionsRaw = FastList.newInstance();
					conditionsRaw.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productFinGoodId));
					conditionsRaw.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"MANUF_COMPONENT"));
					List productRawList = delegator.findList("ProductAssoc",EntityCondition.makeCondition(conditionsRaw, EntityOperator.AND), null, null, null, false);
					for(GenericValue productRaw:productRawList){
						resultMap1 = [:];
						String uomDescription1 = " ";
						productAssocId = productRaw.getString("productIdTo");
						BigDecimal requried=productRaw.getString("quantity").toBigDecimal();
						requriedQtyFinished=((qtyOrder-((resultOutput.availableToPromiseTotal)<0?0:(resultOutput.availableToPromiseTotal)))*requried);
						if(UtilValidate.isNotEmpty(productAssocId)){
							productGv1 =delegator.findByPrimaryKey("Product", ["productId" :productAssocId ]);
							uomGv1=delegator.findByPrimaryKey("Uom", ["uomId" :productGv1.get("quantityUomId")]);
							if(UtilValidate.isNotEmpty(uomGv1)){
								
								uomDescription1 = uomGv1.getString("description");
								
								}
							resultOutput1 =  dispatcher.runSync("getInventoryAvailableByFacility", [facilityId : request.getParameter("facilityId"), productId :productAssocId]);
							BigDecimal availableQty =((resultOutput1.availableToPromiseTotal)<0?0:(resultOutput1.availableToPromiseTotal));
							BigDecimal requriedRawMaterials=(availableQty >requriedQtyFinished?0:(requriedQtyFinished-availableQty));
							resultMap1.finishedGood="";
							resultMap1.ordered="";
							resultMap1.available="";
							resultMap1.rawMaterial=OrderMgrUtil.getProductDescription(productAssocId);
							resultMap1.availableRaw=myFormatter.format(((resultOutput1.availableToPromiseTotal)<0?0:(resultOutput1.availableToPromiseTotal)))+" "+uomDescription1;
							resultMap1.requried=(myFormatter.format(requriedRawMaterials)+" "+uomDescription1);
							resultMap1.product=OrderMgrUtil.getProductDescription(productFinGoodId);
							rows+=resultMap1;
						}
					}
				}
			}
		}
	}
}

org.ofbiz.entity.transaction.TransactionUtil.begin();
JRMapCollectionDataSource jrDataSource= new JRMapCollectionDataSource(rows);
if (jrDataSource != null) {
	request.setAttribute("jrDataSource", jrDataSource);
	request.setAttribute("jrParameters", jrParameters);
}

org.ofbiz.entity.transaction.TransactionUtil.commit();
context.resultList = rows;

return "Success";