<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Inventory Receipt</fo:block>
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
<fo:block>
    <fo:inline font-weight="bold">Issue From Date : </fo:inline>
    <fo:inline >
        <#if parameters.fromDate?has_content>
          ${parameters.fromDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
    <fo:inline padding-left="190pt" font-weight="bold">Issue Thru Date : </fo:inline>
    <fo:inline >
        <#if parameters.thruDate?has_content>
          ${parameters.thruDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
    <#if shipmentMaterialReceivedList?has_content>

    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(3)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Received On</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Invoice No.</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Order No.</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Supplier</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Material Description</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Unit</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Received Qty</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Price/Qty (K)</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Total Price (K)</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#assign rowColor = "white">
            <#list shipmentMaterialReceivedList as smr>
                <fo:table-row>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.receivedOn?string("dd/MM/yyyy")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.invoice?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.orderNumber?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.supplier?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.productName?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.unit?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.receivedQty?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.unitPrice}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${smr.totalPrice}</fo:block></fo:table-cell>
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