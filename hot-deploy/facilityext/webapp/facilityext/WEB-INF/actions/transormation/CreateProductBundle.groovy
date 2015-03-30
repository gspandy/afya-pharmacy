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
import org.ofbiz.base.util.ObjectType;
import java.io.*;


String tagCode = request.getParameter("tagCode");
String makeSku = request.getParameter("makeSku");
quantityRequired = request.getParameter("quantityRequired");
String errMsg

BigDecimal  bundFact; 

 checkMakeSku = [] as ArrayList;
 checkMakeSku = delegator.findByPrimaryKey("Product" ,[productId:makeSku]);
 
 if(!checkMakeSku){
   request.setAttribute("_ERROR_MESSAGE_",
							"Invalid Make Sku");
	              return "error";
 }
try {
			bundFact = new BigDecimal(quantityRequired);
		} catch (Exception e) {

			request.setAttribute("_ERROR_MESSAGE_",
					"Quantity should be numeric, entered value is not valid.");
			return "error";
		}
  if(bundFact<=0){

                  request.setAttribute("_ERROR_MESSAGE_",
							"Quantity required cannot be Zero or lesser ");
	              return "error";
}
 if(quantityRequired.contains('.')){
                  request.setAttribute("_ERROR_MESSAGE_",
							"Quantity required cannot be a decimal value ");
	   			  return "error";
} 

if(quantityRequired.contains('.')){
                  request.setAttribute("_ERROR_MESSAGE_",
							"Quantity required cannot be a decimal value ");
	   			  return "error";
} 
String myString = Long.toString(999999999999);
if(quantityRequired > myString){
                  request.setAttribute("_ERROR_MESSAGE_",
							"Quantity cannot be greater then 999999999999");
	   			  return "error";
} 
GenericValue createProductBundle = null;
createProductBundle = delegator.makeValue("ProductBundle");
createProductBundle.setNextSeqId();
createProductBundle.set("statusId", "STATUS_REQUESTED");



try{
	if(quantityRequired){
		if(Math.round(Double.valueOf (quantityRequired))!= Double.valueOf (quantityRequired)){
			errMsg = "Cannot include decimals in Quantity Required";
			throw new Exception();
		}
		createProductBundle.set("quantityRequired", (quantityRequired));
		createProductBundle.set("pendingQuantity", new BigDecimal(quantityRequired));
	}
	else{
		errMsg = "Quantity Required cannot be blank ";
	}
	createProductBundle.set("makeSku",makeSku);
	createProductBundle.set("tagCode", tagCode);
	createProductBundle.create();
}
catch(Exception e){
	if(!errMsg){
		errMsg = e.getMessage()
	}
	request.setAttribute("_ERROR_MESSAGE_", errMsg);
	return "error"
}

	return "success"
