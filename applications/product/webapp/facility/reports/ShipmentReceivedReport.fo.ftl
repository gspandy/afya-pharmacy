<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Shipment Received</fo:block>
<fo:block>
    <fo:leader/>
</fo:block>
<fo:block>
    <fo:inline font-weight="bold">Received From Date : </fo:inline>
    <fo:inline >
        <#if parameters.fromDate?has_content>
          ${parameters.fromDate?date("yyyy-MM-dd")?string("dd-MM-yyyy")}
        </#if>
    </fo:inline>
    <fo:inline padding-left="190pt" font-weight="bold">Received Thru Date : </fo:inline>
    <fo:inline >
        <#if parameters.thruDate?has_content>
          ${parameters.thruDate?date("yyyy-MM-dd")?string("dd-MM-yyyy")}
        </#if>
    </fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
    <#--<#if individualPpeRecordList?has_content>-->
    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="8pt" border-style="solid" border-color="black">
        <fo:table-column column-number="1" column-width="proportional-column-width(2)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(2)"/>
        <fo:table-column column-number="3" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="4" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="5" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="6" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="7" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="8" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="9" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="10" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="11" column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-number="12" column-width="proportional-column-width(1.5)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" text-align="center">
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Supplier</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Product</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Invoice No</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Shipment Id</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Loading Date</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Received On</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Transporter</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Truck</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Transaction</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Shipped Quantity</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Received Quantity</fo:block></fo:table-cell>
                <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>Variance</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#assign rowColor = "white">
            <#--<#list individualPpeRecordList as ipr>-->
                <fo:table-row text-align="center" height="15pt">
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">1</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">2</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">3</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">4</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">5</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">6</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">7</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">8</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">9</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">10</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">11</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block margin-top="3px">12</fo:block></fo:table-cell>
                </fo:table-row>
                <#if rowColor == "white">
                    <#assign rowColor = "#D4D0C8">
                <#else>
                    <#assign rowColor = "white">
                </#if>
            <#--</#list>-->
        </fo:table-body>
    </fo:table>
    <#--<#else>
    <fo:block>No Record Found.</fo:block>
    </#if>-->
</#escape>