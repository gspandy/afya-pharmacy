<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Shipment Receipt</fo:block>
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
    <fo:table-column column-width="proportional-column-width(80)"/>
    <fo:table-column column-width="proportional-column-width(20)"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell>
                <#if fromDate?has_content>
                    <fo:block number-columns-spanned="2">
                        From Date : ${fromDate?if_exists?string("dd/MM/yyyy")}
                    </fo:block>
                <#else>
                    <fo:block number-columns-spanned="2">
                        From Date :
                    </fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell>
                <#if thruDate?has_content>
                    <fo:block number-columns-spanned="2">
                        Thru Date : ${thruDate?if_exists?string("dd/MM/yyyy")}
                    </fo:block>
                <#else>
                    <fo:block number-columns-spanned="2">
                        Thru Date :
                    </fo:block>
                </#if>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
    <#if mainList?has_content>

    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="8pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-width="proportional-column-width(0.8)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1.2)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <#-- <fo:table-column column-width="proportional-column-width(1.2)"/> -->
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" text-align="center">
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Supplier</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Product</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Invoice No</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Shipment ID</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Loading Date</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Received On</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Transporter</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Truck</fo:block></fo:table-cell>
                <#-- <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Transaction</fo:block></fo:table-cell> -->
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Shipped Quantity</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Received Quantity</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Variance</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#assign rowColor = "white">
            <#list mainList as smr>
                <fo:table-row text-align="center" height="15pt">
                	<#assign supplier = delegator.findOne("PartyGroup",{"partyId",smr.supplierId},false) />
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${supplier.groupName?if_exists}</fo:block></fo:table-cell>
                    <#assign product = delegator.findOne("Product",{"productId",smr.productId},false) />
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${product.productName?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.invoiceId?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.shipmentId?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.loadingDate?if_exists?date("yyyy-MM-dd")?string("dd/MM/yyyy")}</fo:block></fo:table-cell>
                    <#if smr.receivedOn?has_content>
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.receivedOn?date("yyyy-MM-dd")?string("dd/MM/yyyy")}</fo:block></fo:table-cell>
                    <#else>
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px"> </fo:block></fo:table-cell>
                    </#if>
                    <#if smr.transporterId?has_content >
                        <#assign transporter = delegator.findOne("PartyGroup",{"partyId",smr.transporterId},false) />
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${transporter.groupName?if_exists}</fo:block></fo:table-cell>
                    <#else>
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px"> </fo:block></fo:table-cell>
                    </#if>	
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.truck?if_exists}</fo:block></fo:table-cell>
                    <#-- <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.transaction?if_exists}</fo:block></fo:table-cell> -->
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.shippedQuantity?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.receivedQuantity?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${smr.variance?if_exists}</fo:block></fo:table-cell>
                </fo:table-row>
                <#if rowColor == "white">
                    <#assign rowColor = "#D4D0C8">
                <#else>
                    <#assign rowColor = "white">
                </#if>
            </#list>
        </fo:table-body>
    </fo:table>
    <#else>
        <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>