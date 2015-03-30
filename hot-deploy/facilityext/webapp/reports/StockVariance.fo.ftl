<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Stock Variance</fo:block>
    <#-- blank line -->
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column/>
        <fo:table-column/>
        <fo:table-body>
            <fo:table-row height="12.5px" space-start=".15in">
                <fo:table-cell>
                    <fo:block />
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block />
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(70)"/>
        <fo:table-column column-width="proportional-column-width(40)"/>
        <fo:table-body>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                <#assign productCategory = delegator.findOne("ProductCategory",{"productCategoryId",productCategoryId?if_exists},false)>
                    <fo:block number-columns-spanned="2">
                        Product Category : <#if productCategory?has_content> ${productCategory.description?if_exists} <#else> All </#if> </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                        From Date : ${fromDate?if_exists?string("dd-MM-yyyy")}</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                        Thru Date : ${thruDate?if_exists?string("dd-MM-yyyy")}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block><fo:leader></fo:leader></fo:block>

    <#if stockVarianceList?has_content>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(3.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Product Id</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Description</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Unit</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Quantity</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Stock Count</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Variance</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list stockVarianceList as stockVariance>
                        <fo:table-row text-align="center" height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${stockVariance.productId?if_exists}</fo:block>
                            </fo:table-cell>
                            <#assign productGv =  delegator.findOne("Product", {"productId":stockVariance.productId},false)>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${productGv.internalName?if_exists}</fo:block>
                            </fo:table-cell>
                            <#assign uomGv = delegator.findOne("Uom", {"uomId":productGv.quantityUomId}, false)>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                <#if uomGv?has_content>
                                	${uomGv.description?if_exists}
                                </#if>
                                </fo:block>
                            </fo:table-cell>
                            <#assign stockCount = stockVariance.stockCount?default(0.00)/>
                            <#assign variance = stockVariance.variance?default(0.00)*-1/>
                            <#assign quantity = stockCount + variance/>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${quantity?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${stockCount?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${variance?if_exists}</fo:block>
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
