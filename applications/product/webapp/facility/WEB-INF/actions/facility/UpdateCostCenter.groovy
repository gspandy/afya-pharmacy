import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;



String invReqItemId = request.getParameter("invReqItemId");
String glAccountCategory = request.getParameter("glAccountCategory");
String productId = request.getParameter("productId");
String returnable = request.getParameter("returnable");

if(glAccountCategory){
	GenericValue invReqItem = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",invReqItemId,"glAccountCategoryId",glAccountCategory));
	delegator.store(invReqItem);
}
if(returnable){
	GenericValue invReqItem = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",invReqItemId,"returnable",returnable));
	delegator.store(invReqItem);
}
return "success";