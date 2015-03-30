
import org.sme.order.util.*;

 orderId = parameters.orderId;
 userLogin = session.getAttribute("userLogin");
 String partyId = userLogin.getString("partyId");
 if(orderId){
   isOrderCreater = OrderMgrUtil.checkOrderCreater(partyId,orderId);
   if(isOrderCreater)
   	 return "success";
   	else{
   	 return "error";
   	 request.setAttribute("_ERROR_MESSAGE_", "No results found.");
   	 }
 }

