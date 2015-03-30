<#if shipmentInvoiceData?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <#assign alt_row = false>
        <#list shipmentInvoiceData as shipmentInvoiceDatas>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                 <td>&nbsp;</td>
                 <td><span class="label">Order Id</span> <a href="/ordermgr/control/orderview?orderId=${shipmentInvoiceDatas.orderId?if_exists}&amp;externalLoginKey=${requestAttributes.externalLoginKey}" class="btn-link">${shipmentInvoiceDatas.orderId?if_exists}</a></td>
                 <td><span class="label">Invoice Id</span> <a href="/accounting/control/invoiceOverview?invoiceId=${shipmentInvoiceDatas.invoiceId?if_exists}&amp;externalLoginKey=${requestAttributes.externalLoginKey}" class="btn-link">${shipmentInvoiceDatas.invoiceId?if_exists}</a></td>           
                </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
        
        </div>
        </div>

 <#else>
 <#if purchaseInvoiceNewData?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <#assign alt_row = false>
        <#list purchaseInvoiceNewData as purchaseInvoiceNewDatas>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                 <td>&nbsp;</td>
                 <td><span class="label">Order Id</span> <a href="/ordermgr/control/orderview?orderId=${purchaseInvoiceNewDatas.orderId?if_exists}&amp;externalLoginKey=${requestAttributes.externalLoginKey}" class="btn-link">${purchaseInvoiceNewDatas.orderId?if_exists}</a></td>
                 <td><span class="label">Invoice Id</span> <a href="/accounting/control/invoiceOverview?invoiceId=${purchaseInvoiceNewDatas.invoiceId?if_exists}&amp;externalLoginKey=${requestAttributes.externalLoginKey}" class="btn-link">${purchaseInvoiceNewDatas.invoiceId?if_exists}</a></td>           
                </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
        </div>
        </div>
  </#if>
</#if>