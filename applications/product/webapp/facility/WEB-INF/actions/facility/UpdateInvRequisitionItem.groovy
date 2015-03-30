import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import java.math.BigDecimal;


String invReqItemId = request.getParameter("invReqItemId");
String modifyQtyStr = request.getParameter("modifyQty");
String note = request.getParameter("note");
String glAccountCategory = request.getParameter("glAccountCategory");
String returnable = request.getParameter("returnable");

	List<Map<String,String>> requestItemList = session.getAttribute("requestItemList");
	Map<String,String> headerMap = session.getAttribute("headerMap");
	request.setAttribute("requestType",headerMap.get("requestType"));
	String productId = request.getParameter("productId");
	int count = 0;
	int updateIndex = 0;
	for(Map<String,String> map : requestItemList){
		if(productId.equals(map.get("productId"))){
			updateIndex = count;
		}
		count++;
	}
	
	if(UtilValidate.isNotEmpty(requestItemList)){
		Map<String,String> requestItem = requestItemList.remove(updateIndex);
		if(UtilValidate.isNotEmpty(modifyQtyStr))
			requestItem.put("quantity",new BigDecimal(modifyQtyStr));
		if(UtilValidate.isNotEmpty(note))
			requestItem.put("note",note)
		if(UtilValidate.isNotEmpty(glAccountCategory))
			requestItem.put("glAccountCategoryId",glAccountCategory)
		if(UtilValidate.isNotEmpty(returnable))
		    requestItem.put("returnable", returnable);
		
		requestItemList.add(updateIndex, requestItem);
	}

return "success";