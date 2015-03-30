import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*

issuanceId = request.getParameter("issuanceId");

facilityId = request.getParameter("facilityId");
context.facilityId = facilityId;

inventoryItemId = request.getParameter("inventoryItemId");
context.inventoryItemId = inventoryItemId;

productIdFinishGood = request.getParameter("productIdFinishGood");
context.productIdFinishGood = productIdFinishGood;
if(productIdFinishGood){
 productGv = delegator.findOne("Product",true,["productId":productIdFinishGood]);
 if(productGv != null)
 	context.fmProductDes = productGv.internalName;
}

if (issuanceId){
    manufacturingIssuance = delegator.findOne("ManufacturingIssuance", [issuanceId : issuanceId], false);
    context.manufacturingIssuance = manufacturingIssuance;
 }

if (inventoryItemId) {
    inventoryItem = delegator.findOne("InventoryItem", [inventoryItemId : inventoryItemId], false);
    if (facilityId && inventoryItem && inventoryItem.facilityId && !inventoryItem.facilityId.equals(facilityId)) {
        illegalInventoryItem = "Inventory item not found for this facility.";
        inventoryItem = null;
    }
    if (inventoryItem) {
        context.inventoryItem = inventoryItem;
        inventoryItemType = inventoryItem.getRelatedOne("InventoryItemType");

        if (inventoryItemType) {
            context.inventoryItemType = inventoryItemType;
        }
        if (inventoryItem.statusId) {
            inventoryStatus = inventoryItem.getRelatedOne("StatusItem");
            if (inventoryStatus) {
                context.inventoryStatus = inventoryStatus;
            }
        }
    }
}
