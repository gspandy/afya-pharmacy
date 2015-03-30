
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import java.math.BigDecimal;


String invReqItemId = request.getParameter("invReqItemId");
String acceptedQtyStr = request.getParameter("acceptedQty");

BigDecimal acceptedQty = new BigDecimal(acceptedQtyStr);

GenericValue gv = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",invReqItemId,"acceptedQuantity",acceptedQty));
delegator.store(gv);

return "success";