<#escape x as x?xml>

    <#if todaySalesReport?exists>
        <#if todayMaterialCostReport?exists>
            <#assign todaycostContribution = todaySalesReport.cost - todayMaterialCostReport.cost/>
            <#assign todayQtyContribution = todaySalesReport.quantity - todayMaterialCostReport.quantity/>
        </#if>
    </#if>
    <#if monthToDateSalesReport?exists>
        <#if monthToDateMaterialCostReport?exists>
            <#assign monthToDatecostContribution = monthToDateSalesReport.avgCost - monthToDateMaterialCostReport.avgCost/>
            <#assign monthToDateQtyContribution = monthToDateSalesReport.quantity - monthToDateMaterialCostReport.quantity/>
        </#if>
    </#if>
    <#if yearToDateSalesReport?exists>
        <#if yearToDateMaterialCostReport?exists>
            <#assign yearToDatecostContribution = yearToDateSalesReport.avgCost - yearToDateMaterialCostReport.avgCost/>
            <#assign yearToDateQtyContribution = yearToDateSalesReport.quantity - yearToDateMaterialCostReport.quantity/>
        </#if>
    </#if>

<fo:block font-size="15pt" text-align="center" font-weight="bold">Manufacturing Report</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-weight="bold">PRODUCT - ${productName?if_exists}</fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column  column-width="proportional-column-width(2)"/>
    <fo:table-column  column-width="proportional-column-width(1)"/>
    <fo:table-column  column-width="proportional-column-width(1)"/>
    <fo:table-column  column-width="proportional-column-width(1)"/>
    <fo:table-column  column-width="proportional-column-width(1)"/>
    <fo:table-column  column-width="proportional-column-width(1)"/>
    <fo:table-column  column-width="proportional-column-width(1)"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid" border-color="black"><fo:block>&#32;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black" number-columns-spanned="2"><fo:block font-weight="bold" text-align="center">TODAY</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black" number-columns-spanned="2"><fo:block font-weight="bold" text-align="center">MTD</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black" number-columns-spanned="2"><fo:block font-weight="bold" text-align="center">YTD</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid" border-color="black"><fo:block>&#32;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center">Qty Sold/Produced</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center">Value</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center">Qty Sold/Produced</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center">Average Value</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center">Qty Sold/Produced</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center">Average Value</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center" font-weight="bold">Material Cost</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if todayMaterialCostReport?exists>
                    <fo:block text-align="center">${todayMaterialCostReport.quantity}</fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if todayMaterialCostReport?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=todayMaterialCostReport.cost/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if monthToDateMaterialCostReport?exists>
                    <fo:block text-align="center">${monthToDateMaterialCostReport.quantity}</fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if monthToDateMaterialCostReport?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=monthToDateMaterialCostReport.avgCost/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if yearToDateMaterialCostReport?exists>
                    <fo:block text-align="center">${yearToDateMaterialCostReport.quantity}</fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if yearToDateMaterialCostReport?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=yearToDateMaterialCostReport.avgCost/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center" font-weight="bold">Sales</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if todaySalesReport?exists>
                    <fo:block text-align="center">${todaySalesReport.quantity}</fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if todaySalesReport?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=todaySalesReport.cost/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if monthToDateSalesReport?exists>
                    <fo:block text-align="center">${monthToDateSalesReport.quantity}</fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if monthToDateSalesReport?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=monthToDateSalesReport.avgCost/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if yearToDateSalesReport?exists>
                    <fo:block text-align="center">${yearToDateSalesReport.quantity}</fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if yearToDateSalesReport?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=yearToDateSalesReport.avgCost/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid" border-color="black"><fo:block text-align="center" font-weight="bold">Contribution</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                    <fo:block>&#32;</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if todaycostContribution?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=todaycostContribution/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                    <fo:block>&#32;</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if monthToDatecostContribution?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=monthToDatecostContribution/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                    <fo:block>&#32;</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid" border-color="black">
                <#if yearToDatecostContribution?exists>
                    <fo:block text-align="center"><@ofbizCurrency amount=yearToDatecostContribution/></fo:block>
                <#else>
                    <fo:block>&#32;</fo:block>
                </#if>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
</#escape>