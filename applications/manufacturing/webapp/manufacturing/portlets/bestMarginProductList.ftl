<#if prodsSoldWithbestMargin?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
           <td> ${uiLabelMap.ManufacturingFinishedGood}</td>
          <td>
          	    ${uiLabelMap.ManufacturingAvgSalePrice}
          </td>
          <td>
          	    ${uiLabelMap.ManufacturingAvgMnfCost}
          </td>
          <td>
            	${uiLabelMap.ManufacturingMarginInPercentage}
          </td>
          <td>
				${uiLabelMap.CommonAction}
          </td>
        </tr>
        <#assign alt_row = false>
        <#list prodsSoldWithbestMargin as bestProduct>
        
        	<#assign entityData = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",bestProduct.productId?if_exists))>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td>${entityData.internalName?if_exists}</td>
             <td align="right">${bestProduct.avgSalePrice?if_exists}</td>
         	<td align="right">${bestProduct.avgMfgCost?if_exists}  </td>
         	<td align="right">${bestProduct.maginValue?if_exists} </td>
            <td><a href="<@ofbizUrl>FindProductionRun?productId=${bestProduct.productId?if_exists}&workEffortTypeId=PROD_ORDER_HEADER&estimatedStartDate_fld0_value=${timeStampFrom?if_exists}&estimatedStartDate_fld0_op=greaterThan</@ofbizUrl>" class="buttontext">View Runs</a></td>
          </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>
