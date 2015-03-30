<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Product</td>
          <td>Available Quantity [a]</td>
          <td>Incoming Quantity [b]</td>
          <td>Request Quantity [c]</td>
          <td>Net Difference [(a+b)-c] </td>
        </tr>
        <#assign alt_row = false>
        <#assign rowCount = 0>
        <#list openRequestList as openRequest>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <#assign product = delegator.findOne("Product",{"productId" : openRequest.productId}, true) />
            <td>${product.productId} - ${product.productName}</td>
            <td>${openRequest.availableQty}</td>
            <td>${openRequest.quantityArival}</td>
            <td>${openRequest.reqQuantity}</td>
            <td>${openRequest.netDifference}</td>
          </tr>
            <#assign alt_row = !alt_row>
            <#assign rowCount = rowCount + 1>
        </#list>
      </table>
  </div>
</div>