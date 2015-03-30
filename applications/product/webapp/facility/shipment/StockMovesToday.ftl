

<#if stockMoveToday?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
           <td>Product Id</td>
           <td>Inventory Item Id</td>
           <td>To Location</td>
           <td>Quantity</td>
           <#--  <td>Facility</td>  -->
        </tr>
        <#assign alt_row = false>
        <#list stockMoveToday as stockMove>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
             <td><a href="/catalog/control/EditProductInventoryItems?productId=${stockMove.productId?if_exists}" class="btn-link">${stockMove.productId?if_exists}</a></td>
            <td>${stockMove.inventoryItemId?if_exists}</td>
            <td>${stockMove.locationSeqId?if_exists}</td>
            <td>${stockMove.quantity?if_exists}</td>
            <#--  <td>${stockMove.facilityId?if_exists}</td>  -->
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>