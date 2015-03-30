
<#if salesReturn?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderReturnId}</td>
          <td width="15%">${uiLabelMap.OrderEntryDate}</td>
          <td width="10%">${uiLabelMap.CommonStatus}</td>
        </tr>
        <#assign alt_row = false>
        <#list salesReturn as salesReturns>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/returnMain?returnId=${salesReturns.returnId}" class="btn btn-link">${salesReturns.returnId}</a></td>
              <td>${salesReturns.entryDate?string("dd-MM-yyyy")}</td>
            <td>${salesReturns.getRelatedOneCache("StatusItem").get("description",locale)}</td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
      </div>  
</div>
</#if>