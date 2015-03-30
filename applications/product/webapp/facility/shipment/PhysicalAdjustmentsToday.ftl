

<#if physicalVarianceToday?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
           <td>Physical Inventory Id</td>
           <td>Inventory Item Id</td>
           <td>Variance Reason</td>
           <td>Quantity On Hand Var</td>
           <td>Available To Promise Var</td>
           <td>Product Id</td>
           <td>Total QOH</td>
           <td>Total ATP</td>
        </tr>
        <#assign alt_row = false>
        <#list physicalVarianceToday as physicalVariance>
        <#assign product = physicalVariance.getRelatedOneCache("InventoryItem")/>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td>${physicalVariance.physicalInventoryId?if_exists}</td>
            <td>${physicalVariance.inventoryItemId?if_exists}</td>
            <td>${physicalVariance.getRelatedOneCache("VarianceReason").get("description",locale)}</td>
            <td>${physicalVariance.quantityOnHandVar?if_exists}</td>
            <td>${physicalVariance.availableToPromiseVar?if_exists}</td>
            <td><a href="/catalog/control/EditProductInventoryItems?productId=${product.productId?if_exists}" class="btn-link">${product.productId?if_exists}</a></td>
         	<td>${product.quantityOnHandTotal?if_exists}</td>
            <td>${product.availableToPromiseTotal?if_exists}</td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>