import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.*;

Delegator delegator = (Delegator) request.getAttribute("delegator");

String baseSku = request.getParameter("makeSku");
String buildingFactor = request.getParameter("buildingFactor");
String productId = request.getParameter("productId");
BigDecimal bundFact;

 productIdL = [];
  if(productId.length()>0 ){
  if(productId){
  productIdL = delegator.findList("Product", EntityCondition.makeCondition([productId : productId]), null, null, null, false);
  if(!productIdL){
   request.setAttribute("_ERROR_MESSAGE_", "Bundling Product is invalid");
	return "error";
	}
  }
  }	
 if(baseSku==productId){
              request.setAttribute("_ERROR_MESSAGE_",
							"Make Sku and Bundling Product Cannot be same");
	
			return "error";
 }
 
  try{
     bundFact = new BigDecimal(buildingFactor);
 } catch(Exception e){
  request.setAttribute("_ERROR_MESSAGE_",
							"Enter Valid Bundling Factor");
	
			return "error";
 }
 if(bundFact<=0){
                  request.setAttribute("_ERROR_MESSAGE_",
							"Bundling Factor Cannot be Zero or lesser ");
	
			return "error";
}
 if(buildingFactor.contains('.')){
                  request.setAttribute("_ERROR_MESSAGE_",
							"Bundling Factor Cannot be a Decimal Value ");
	
			return "error";
}

allBundlingList = []
condition = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, baseSku.toString()),
EntityOperator.AND,EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId.toString()));

BulidIds = delegator.findList("ProductAssoc", condition,null, null, null, false);
allBundlingList +=BulidIds;  

 if(!allBundlingList){
GenericValue createProductBundle = null;
createProductBundle = delegator.makeValue("ProductAssoc");
createProductBundle.set("productId",baseSku);
createProductBundle.set("scrapFactor", new BigDecimal(buildingFactor));
createProductBundle.set("productIdTo", productId);
createProductBundle.set("productAssocTypeId", "BUNDLED_ASSOC");
createProductBundle.set("fromDate",  UtilDateTime.nowTimestamp());
createProductBundle.create();
}

 return "success"