

<#if shipmentReceipt?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Receipt Id</td>
          <td>${uiLabelMap.ProductShipmentId?if_exists}</td>
          <td>Inventory Item Id</td>
          <td>Product Id</td>
          <td>Order Id</td>
          <td>Quantity Rejected</td>
        </tr>
        <#assign alt_row = false>
        <#list shipmentReceipt as shipmentReceiptList>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td>${shipmentReceiptList.receiptId?if_exists}</td>
            <td>
            <#if shipmentReceiptList.shipmentId?has_content>
            <a href="<@ofbizUrl>ViewShipment?shipmentId=${shipmentReceiptList.shipmentId?if_exists}</@ofbizUrl>" class="btn-link">${shipmentReceiptList.shipmentId?if_exists}${shipmentReceiptList.shipmentItemSeqId?if_exists}</a>
            </#if>
            </td>
         <!-- <td><a href="<@ofbizUrl>EditInventoryItem?orderId=${shipmentReceiptList.inventoryItemId?if_exists}</@ofbizUrl>" class="buttontext">${shipmentReceiptList.inventoryItemId?if_exists}</a></td> -->
         	<td><a href="/facility/control/EditInventoryItem?inventoryItemId=${shipmentReceiptList.inventoryItemId?if_exists}&amp;facilityId=${parameters.facilityId}" class="btn-link">${shipmentReceiptList.inventoryItemId?if_exists}</a></td>
            <td>${shipmentReceiptList.productId?if_exists}</td>
            <td>
            <#if shipmentReceiptList.orderId?has_content>
            <a href="<@ofbizUrl>orderview?orderId=${shipmentReceiptList.orderId?if_exists}</@ofbizUrl>" class="btn-link">${shipmentReceiptList.orderId?if_exists}</a>
            </#if>
            </td>
            <td>${shipmentReceiptList.quantityRejected?if_exists}</td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>