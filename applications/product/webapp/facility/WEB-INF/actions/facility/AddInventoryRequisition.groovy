import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.ndz.util.ProductHelper;

	Map<String, String> headerMap = session.getAttribute("headerMap");
	if(UtilValidate.isEmpty(headerMap)){
		headerMap = new HashMap<String, String>();
	}
	Integer newQuantity = null;
	try{
		 newQuantity = new Integer(request.getParameter("quantity"));
	}catch(Exception e){
		request.setAttribute("_ERROR_MESSAGE_", "Please provide valid Quantity.");
		return "error";
	}
	String facilityId = request.getParameter("facilityId");
	
	if(UtilValidate.isEmpty(facilityId) || facilityId == "null"){
		return "error-main";
	}
	headerMap.put("requestType", request.getParameter("requestType"));
	headerMap.put("facilityIdFrom", request.getParameter("facilityIdFrom"));
	headerMap.put("facilityIdTo", facilityId);
	
	Timestamp requestDate = UtilValidate.isEmpty(request.getParameter("requestDate"))?null:UtilDateTime.dateStringToTimestampParser(request.getParameter("requestDate"));
	if(requestDate.after(UtilDateTime.nowTimestamp())){
		request.setAttribute("_ERROR_MESSAGE_", "Request Date can't be Future Date");
		return "error";
	}
	Map findCustomTimePeriodMap =  dispatcher.runSync("findCustomTimePeriods",UtilMisc.toMap("findDate",UtilDateTime.nowTimestamp(),"organizationPartyId","Company","userLogin",session.getAttribute("userLogin")));
	List  customTimePeriodList = findCustomTimePeriodMap.get("customTimePeriodList");
	if(UtilValidate.isNotEmpty(customTimePeriodList)){
		Timestamp fromTimestamp = new Timestamp(customTimePeriodList.get(0).getDate("fromDate").getTime()) ;
		Date fromDate = new Date(fromTimestamp.getTime());
		Date requestDateDate = new Date(requestDate.getTime());
		if(fromDate.compareTo(requestDateDate) > 0){
			request.setAttribute("_ERROR_MESSAGE_", "Request Date must be within current Fiscal Year");
			return "error";
		}
	}
	
	headerMap.put("requestDate", request.getParameter("requestDate"));
	if("Return".equals(request.getParameter("requestType"))){

		resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", [productId : request.getParameter("productId"), facilityId : facilityId]);
		Double atp = new Double(resultOutput.availableToPromiseTotal);
		
		if(newQuantity.compareTo(atp.intValue()) == 1){
			request.setAttribute("_ERROR_MESSAGE_", "Quantity can not be greater than ATP.");
			return "error";
		}
		
	}

	List<Map<String, String>> requestItemList = session.getAttribute("requestItemList");
	if(UtilValidate.isEmpty(requestItemList)){
		requestItemList = new ArrayList<Map<String, String>>();
	}
	Map<String, String> lineItem = new HashMap<String, String>();
	boolean isDataAvailable = false;
	int count = 0;
	int deleteindex = 0;
	for(Map<String, String> map : requestItemList){
			
		if(request.getParameter("productId").equals(map.get("productId"))){
			lineItem = map;
			isDataAvailable = true;
			lineItem.put("productId", request.getParameter("productId"));
			Integer oldQuantity = 0;
			try{
			 oldQuantity = new Integer(map.get("quantity"));
			}catch(Exception e){
			 oldQuantity = map.get("quantity").intValue();
			}
			lineItem.put("quantity",  oldQuantity + newQuantity);
			lineItem.put("note", request.getParameter("note"));
			lineItem.put("returnable", isReturnable(request.getParameter("productId")));
			lineItem.put("glAccountCategoryId", request.getParameter("glAccountCategoryId"));
			deleteindex = count;
		}
		count++;
	}
	if(!isDataAvailable){
		lineItem.put("productId", request.getParameter("productId"));
		lineItem.put("quantity", newQuantity);
		lineItem.put("note", request.getParameter("note"));
		lineItem.put("returnable", isReturnable(request.getParameter("productId")));
		lineItem.put("glAccountCategoryId", request.getParameter("glAccountCategoryId"));
	}else{
		requestItemList.remove(deleteindex);
	}
	requestItemList.add(lineItem);

	request.setAttribute("invRequisitionId",headerMap.get("invRequisitionId"));
	session.removeAttribute("headerMap");
	session.removeAttribute("requestItemList");
	
	session.setAttribute("headerMap",headerMap);
	session.setAttribute("requestItemList",requestItemList);
	
	context.headerMap = headerMap;
	context.requestItemList = requestItemList;
	
   String isReturnable(String productId){
		GenericValue productGv = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
		String returnable = "No";
		if("Y".equals(productGv.getString("returnable")) )
			returnable = "Yes"
		return returnable;
	}

return "success";
