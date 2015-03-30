import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;


String invReqItemId = request.getParameter("invReqItemId");
String rejectionReasonId = request.getParameter("rejectionReasonId");

GenericValue gv = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",invReqItemId,"rejectionId",rejectionReasonId));
delegator.store(gv);

return "success";