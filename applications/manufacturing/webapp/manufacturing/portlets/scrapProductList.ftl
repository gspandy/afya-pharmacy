<#if scrapProductList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
           <td>Scrap Product</td>
          <td>${uiLabelMap.CommonQuantity}</td>
          <td>${uiLabelMap.ManufacturingRoutingTask}</td>
        </tr>
        <#assign alt_row = false>
        <#list scrapProductList as scrapProduct>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td>${scrapProduct.pdInternalName?if_exists}</td>
         	<td>${scrapProduct.quantityOnHandTotal?if_exists}  ${scrapProduct.abbreviation?if_exists}</td>
            <td><a href="javascript:popUp('<@ofbizUrl>showProductionRuns?workEffortName=${scrapProduct.workEffortName}</@ofbizUrl>', '', '450', '550')" class="btn-link">${scrapProduct.workEffortName?if_exists}</a></td>
          </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>

