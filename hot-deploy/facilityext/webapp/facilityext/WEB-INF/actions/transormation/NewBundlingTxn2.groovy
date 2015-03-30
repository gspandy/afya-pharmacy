import org.ofbiz.base.util.http.*;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;


qtyToBundle = request.getParameter("quantityToBundle");
toLocationId = request.getParameter("toLocationId");
toLocationId = toLocationId.trim();
request.setAttribute("facilityId", parameters.facilityId);
if(toLocationId.length()== 0 ){
request.setAttribute("_ERROR_MESSAGE_", "Putaway Location Cannot be null");
	return "error";
}

  locationSeqId = [];
  if(toLocationId.length()>0 ){
  if(toLocationId){
  locationSeqId = delegator.findList("FacilityLocation", EntityCondition.makeCondition([locationSeqId : toLocationId]), null, null, null, false);
  if(!locationSeqId){
   request.setAttribute("_ERROR_MESSAGE_", "Putaway Location is not valid");
	return "error";
	}
  }
  }	

BigDecimal  quantity; 
try {
			quantity = new BigDecimal(qtyToBundle);
		} catch (Exception e) {

			request.setAttribute("_ERROR_MESSAGE_",
					"Quantity should be numeric, entered value is not valid.");
			return "error";
		}
if(quantity<=0){
request.setAttribute("_ERROR_MESSAGE_", "Quantity Now cannot be Zero or lesser value");
	return "error";
} 

if(qtyToBundle.contains('.')){
request.setAttribute("_ERROR_MESSAGE_", "Quantity Now cannot be a decimal value");
	return "error";
}

try{
GenericValue productBundleTxn = delegator.makeValue("ProductBundleTxn");
productBundleTxn.setNextSeqId();
productBundleTxn.productBundleId=parameters.productBundleId;
productBundleTxn.toLocationId=parameters.toLocationId;
productBundleTxn.quantityToBundle=new BigDecimal(parameters.quantityToBundle);
productBundleTxn.palletize=parameters.palletize;
delegator.create(productBundleTxn);

request.setAttribute("productBundleTxnId",productBundleTxn.productBundleTxnId);
}catch (Exception ex){
	request.setAttribute("_ERROR_MESSAGE_", "Insufficient Quantity to Bundle.")
	return "error";
}
return "success";
