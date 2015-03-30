<#if productionRunList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
           <td>${uiLabelMap.CommonEmptyHeader}</td>
          <td>${uiLabelMap.ManufacturingProductionRunName}</td>
          <td>${uiLabelMap.ManufacturingQuantity}</td>
          <td>${uiLabelMap.ManufacturingStartDate}</td>
           <td>${uiLabelMap.ProductFacilityId}</td>
        </tr>
        <#assign alt_row = false>
        <#list productionRunList as productionRun>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/manufacturing/control/ShowProductionRun?productionRunId=${productionRun.workEffortId?if_exists}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${productionRun.workEffortId?if_exists}</a></td>
            <td>${productionRun.workEffortName?if_exists}</td>
         	<td>${productionRun.estimatedQuantity?if_exists}</td>
         	<td>${productionRun.estimatedStartDate?if_exists}</td>
         	<td>${productionRun.facilityId?if_exists}</td>
          </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>

