import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

List<Map<String,String>> requestItemList = session.getAttribute("requestItemList");
Map<String,String> headerMap = session.getAttribute("headerMap");
request.setAttribute("requestType",headerMap.get("requestType"));
String productId = request.getParameter("productId");
int count = 0;
int deleteindex = 0;
for(Map<String,String> map : requestItemList){
	if(productId.equals(map.get("productId"))){
		deleteindex = count;
		if( UtilValidate.isNotEmpty(map.get("invReqItemId")) ){
			delegator.removeByAnd("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",map.get("invReqItemId") ));
		}
	}
	count++;
}

if(UtilValidate.isNotEmpty(requestItemList))
	requestItemList.remove(deleteindex);

return "success";