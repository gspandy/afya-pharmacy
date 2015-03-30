
<#if purchaseReturn?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderReturnId}</td>
          <td width="15%">${uiLabelMap.OrderEntryDate}</td>
          <td width="10%">${uiLabelMap.CommonStatus}</td>
        </tr>
        <#assign alt_row = false>
        <#list purchaseReturn as purchaseReturns>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/returnMain?returnId=${purchaseReturns.returnId}" class="btn btn-link">${purchaseReturns.returnId}</a></td>
            <td>${purchaseReturns.entryDate?string("dd-MM-yyyy")}</td>
            <td>${purchaseReturns.getRelatedOneCache("StatusItem").get("description",locale)}</td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>