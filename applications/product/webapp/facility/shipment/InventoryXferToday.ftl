<#if inventoryTransferToday?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
       <#--   <td>Inventory Item Id To</td> -->
           <td> Origin</td>
            <td> Destination</td>
             <td>Quantity</td>
             <#--   <#if inventoryTransferToday.inventoryTransferId?if_exists> -->
             <td>Status </td>
            <#--    </#if>   -->
           <td>Product</td>
        </tr>
        <#assign alt_row = false>
        <#list inventoryTransferToday as inventoryTransfer>
         <#assign product = inventoryTransfer.getRelatedOneCache("InventoryItem")/>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
           <#-- <td><a href="/facility/control/EditInventoryItem?inventoryItemId=${inventoryTransfer.inventoryItemId?if_exists}&amp;facilityId=${inventoryTransfer.facilityIdTo?if_exists}" class="buttontext">${inventoryTransfer.inventoryItemId?if_exists}</a></td>-->
            <td>
             <#assign facilityFromLists = delegator.findByAnd("Facility", {"facilityId" : inventoryTransfer.facilityId})>
                 <#list facilityFromLists as facilityFromList>
            ${facilityFromList.facilityName?if_exists}
              </#list>
            </td>
           <#--  <td>${inventoryTransfer.facilityIdTo?if_exists}</td> -->
             <td>
             <#if inventoryTransfer.facilityIdTo?exists> 
               <#assign facilityLists = delegator.findByAnd("Facility", {"facilityId" : inventoryTransfer.facilityIdTo})>
                 <#list facilityLists as facilityList>
                    ${facilityList.facilityName?if_exists} 
                   </#list>
               </td>
               </#if>
               <td>
               <#assign inventoryItems = delegator.findByAnd("InventoryItem", {"inventoryItemId" : inventoryTransfer.inventoryItemId})>
                 <#list inventoryItems as inventoryItem>
                    ${inventoryItem.quantityOnHandTotal?if_exists} 
                   </#list>
               </td>
                <td>
               ${inventoryTransfer.getRelatedOneCache("StatusItem").get("description",locale)}
               </td>
           <#--  <td><a href="/catalog/control/EditProductInventoryItems?productId=${product.productId?if_exists}" class="buttontext">${product.productId?if_exists}</a></td>-->
           <td>
           <#assign productLists = delegator.findByAnd("Product", {"productId" : product.productId})>
                 <#list productLists as productList>
            <a href="/facility/control/TransferInventoryItem?inventoryItemId=${inventoryTransfer.inventoryItemId?if_exists}&amp;facilityId=${inventoryTransfer.facilityIdTo?if_exists}&amp;statusId=${inventoryTransfer.statusId?if_exists}" class="btn-link">${productList.internalName?if_exists}</a>
              </#list>
           </td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>