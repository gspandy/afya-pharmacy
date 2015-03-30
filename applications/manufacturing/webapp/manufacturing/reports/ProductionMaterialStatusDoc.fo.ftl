<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Daily Material Status</fo:block>
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
        <fo:table-column column-width="proportional-column-width(10)"/>
        <fo:table-column column-width="proportional-column-width(2.5)"/>
        <fo:table-body>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">DATE : ${nowTimestamp?string("dd/MM/yyyy")}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block><fo:leader></fo:leader></fo:block>

    <#if productionReportList?has_content>
        <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table table-layout="fixed" border-color="black">
                <fo:table-column column-width="proportional-column-width(2.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(4.5)"/>
                <fo:table-column column-width="proportional-column-width(4.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-body>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                        <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                        <fo:table-cell background-color="#FFF" height="20pt" display-align="center" border-top-style="solid" border-left-style="solid" border-right-style="solid"><fo:block>RECEIPTS / PRODUCTION</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#FFF" height="20pt" display-align="center" border-top-style="solid" border-left-style="solid" border-right-style="solid"><fo:block>CONSUMPTIONS</fo:block></fo:table-cell>
                        <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(2.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>DESCRIPTION</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>OPENING BALANCE</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>TODAY</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>MTD</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>YTD</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>TODAY</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>MTD</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>YTD</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>CLOSING BALANCE</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list productionReportList as prodMaterialStatus> 
                        <fo:table-row text-align="center" height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#assign product = delegator.findOne("Product",{"productId":prodMaterialStatus.productId},false)>
                                <fo:block margin-top="3px">${product.productName?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.openingBalance?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.todayReceive?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.monthReceive?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.yearReceive?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.todayConsumption?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.monthConsumption?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.yearConsumption?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${prodMaterialStatus.closingBalance?if_exists}</fo:block>
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
