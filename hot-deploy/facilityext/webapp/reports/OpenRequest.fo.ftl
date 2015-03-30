<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Open Request</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>

    <#if openRequestList?has_content>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Product</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Available Quantity [a]</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Incoming Quantity [b]</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Request Quantity [c]</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Net Difference [(a+b)-c]</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list openRequestList as openRequest>
                        <fo:table-row text-align="center" height="15pt">
                            <#assign product = delegator.findOne("Product",{"productId" : openRequest.productId}, true) />
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${product.productId?if_exists} - ${product.productName?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${openRequest.availableQty?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${openRequest.quantityArival?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${openRequest.reqQuantity?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${openRequest.netDifference?if_exists}</fo:block>
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
