import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.GenericEntityException;

 
 InventoryItems = [] as ArrayList;
 checkInventoryItemFact = [] as ArrayList;
 
/*
 
GenericValue InventoryItem = parameters.InventoryItemFact

*/
  GenericValue LoadInventoryItemFact = delegator.makeValue("InventoryItemFact");
		
		LoadInventoryItemFact.set("inventoryItemId", parameters.inventoryItemId);
		LoadInventoryItemFact.create();
                      /*  InventoryItemFact.put("productId",  InventoryItems.productId);
                        InventoryItemFact.put("partyId",  InventoryItems.partyId);
                        InventoryItemFact.put("ownerPartyId",  InventoryItems.ownerPartyId);
                        InventoryItemFact.put("statusId",  InventoryItems.statusId);
                        InventoryItemFact.put("datetimeReceived",  InventoryItems.datetimeReceived);
                        InventoryItemFact.put("datetimeManufactured",  InventoryItems.datetimeManufactured);
                        InventoryItemFact.put("expireDate",  InventoryItems.expireDate);
                        InventoryItemFact.put("containerId",  InventoryItems.containerId);
                        InventoryItemFact.put("lotId",  InventoryItems.lotId);
                        InventoryItemFact.put("uomId",  InventoryItems.uomId);
                        InventoryItemFact.put("binNumber",  InventoryItems.binNumber);
                        InventoryItemFact.put("locationSeqId",  InventoryItems.locationSeqId);
                        InventoryItemFact.put("comments",  InventoryItems.comments);
                        InventoryItemFact.put("accountingQuantityTotal",  InventoryItems.accountingQuantityTotal);
                        InventoryItemFact.put("oldQuantityOnHand",  InventoryItems.oldQuantityOnHand);
                        InventoryItemFact.put("oldAvailableToPromise",  InventoryItems.oldAvailableToPromise);
                        InventoryItemFact.put("serialNumber",  InventoryItems.serialNumber);
                        InventoryItemFact.put("softIdentifier",  InventoryItems.softIdentifier);
                        InventoryItemFact.put("activationValidThru",  InventoryItems.activationValidThru);
                        InventoryItemFact.put("activationNumber",  InventoryItems.activationNumber);
                        InventoryItemFact.put("currencyUomId",  InventoryItems.currencyUomId);
                        */
                     /*   checkInventoryItemFact =  delegator.findOne("InventoryItemFact", [inventoryItemId : parameters.inventoryItemId], false);
                        if(!checkInventoryItemFact)
                        InventoryItemFact.create();
                        else{
                       try { 
                             InventoryItemFact.store();
                              } 
                             catch (GenericEntityException e) {
                           } 
                        }*/
