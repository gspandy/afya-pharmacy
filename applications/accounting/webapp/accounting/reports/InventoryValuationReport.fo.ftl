<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Inventory Valuation</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>

    <#if inventoryValuationList?has_content>
        <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Product Id</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Product Name</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Unit Cost</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Quantity</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Value</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list inventoryValuationList as inventoryValuation>
                        <fo:table-row text-align="center" height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${inventoryValuation.productId?if_exists}</fo:block>
                            </fo:table-cell>
                            <#assign productGv =  delegator.findOne("Product", {"productId":inventoryValuation.productId},false)>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${productGv.internalName?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"><@ofbizCurrency amount=inventoryValuation.unitCost?default(0)?if_exists isoCode=inventoryValuation.currencyUomId?if_exists /></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${inventoryValuation.accountingQuantitySum?default(0)?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#assign value = inventoryValuation.getBigDecimal("accountingQuantitySum").multiply(inventoryValuation.getBigDecimal("unitCost"))>
                                <fo:block margin-top="3px"><@ofbizCurrency amount=value?default(0)?if_exists isoCode=inventoryValuation.currencyUomId?if_exists /></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    </#list>
                </fo:table-body>
             </fo:table>
        </fo:block>
    <#else>
        <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>
