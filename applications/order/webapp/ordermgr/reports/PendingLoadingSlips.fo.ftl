<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Pending Shipments</fo:block>
<fo:block><fo:leader></fo:leader></fo:block>
<fo:block>
    <fo:inline font-weight="bold">Customer : </fo:inline>
    <fo:inline >
       ${groupName?if_exists}
    </fo:inline>
</fo:block>
<fo:block>
    <fo:inline font-weight="bold">From Date : </fo:inline>
    <fo:inline >
        <#if parameters.fromDate?has_content>
          ${parameters.fromDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
    <fo:inline padding-left="190pt" font-weight="bold">Thru Date : </fo:inline>
    <fo:inline >
        <#if parameters.thruDate?has_content>
          ${parameters.thruDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
    <#if pendingLoadingSlipList?has_content>
    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="9pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(0.8)"/>
        <fo:table-column column-width="proportional-column-width(0.55)"/>
        <fo:table-column column-width="proportional-column-width(2.25)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(0.75)"/>
        <fo:table-column column-width="proportional-column-width(0.75)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-header>

            <fo:table-row font-weight="bold" text-align="center">
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Shipment Id</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Order Id</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Customer Code - Customer Name</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Product Code - Description</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Unit</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Shipment Quantity</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Shipment Date</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#assign rowColor = "white">
            <#list pendingLoadingSlipList as pls>

                <fo:table-row text-align="center" height="15pt">
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${pls.shipmentId?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${pls.orderId?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px" text-align="left">${pls.customerPartyId?if_exists} - ${pls.customerName?if_exists}</fo:block></fo:table-cell>
                    <#if pls.productId?has_content>
                        <#assign product = delegator.findOne("Product", {"productId",pls.productId?if_exists},false)>
                        <#assign quantityUom = product.getString("quantityUomId")>
                        <#assign uomGv = (delegator.findOne("Uom", {"uomId", quantityUom?if_exists}, false))?if_exists />
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px" text-align="left">${product.productId?if_exists} - ${product.internalName?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px"><#if uomGv?has_content>${uomGv.description}</#if></fo:block></fo:table-cell>
                    <#else>
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px"></fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px"></fo:block></fo:table-cell>
                    </#if>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">${pls.shipmentQty?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}">
                        <fo:block margin-top="3px">
                            <#if pls.shipmentCreatedDate?exists>
                            ${pls.shipmentCreatedDate?string("dd/MM/yyyy")}
                            </#if>
                        </fo:block>
                    </fo:table-cell>
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