<#if physicalVarianceToday?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
         <#--  <td>Physical Inventory Id</td>
           <td>Inventory Item Id</td> -->
             <td>Variance Reason</td>
                 <td>ATP Variance</td>
                   <td>QOH Variance</td>
                     <td>Total QOH Variance</td>
                     <td>Total ATP Variance</td>
                        <td>Product</td>
          
        </tr>
        <#assign alt_row = false>
        <#list physicalVarianceToday as physicalVariance>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
         <#--   <td>${physicalVariance.physicalInventoryId?if_exists}</td>
            <td>${physicalVariance.inventoryItemId?if_exists}</td>  -->
            <td>${physicalVariance.description?if_exists}</td>
            <#--  <td><a href="/catalog/control/EditProductInventoryItems?productId=${physicalVariance.productId?if_exists}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${physicalVariance.productId?if_exists}</a></td>-->
              <td>${physicalVariance.availableToPromiseVar?if_exists}</td>
               <td>${physicalVariance.quantityOnHandVar?if_exists}</td>
               <td>${physicalVariance.quantityOnHandTotal?if_exists}</td>
               <td>${physicalVariance.availableToPromiseTotal?if_exists}</td>
                <td>
           <#assign productLists = delegator.findByAnd("Product", {"productId" : physicalVariance.productId})>
                 <#list productLists as productList>
            <a href="/catalog/control/EditProductInventoryItems?productId=${physicalVariance.productId?if_exists}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${productList.internalName?if_exists}</a>
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

