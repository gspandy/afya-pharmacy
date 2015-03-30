<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
            margin-top="0.1in" margin-bottom="1in" margin-left="0.1in" margin-right="0.1in">
        <fo:region-body margin-top="0.5in"/>
        <fo:region-before extent="0.5in"/>
        <fo:region-after extent="0.5in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
 <fo:page-sequence master-reference="main">
 <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
 <fo:block font-size="15pt" text-align="center" font-weight="bold">Sample To Customer</fo:block>
 <fo:block><fo:leader></fo:leader></fo:block>
<#if phyInvVarianceList?has_content>
            <fo:table>
               <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="105pt"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold">
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Inventory Item Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Product Name</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Date</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Customer</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Issue</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Quantity</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Comments</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list phyInvVarianceList as phyInvVariance>
                        <fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${phyInvVariance.inventoryItemId?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${phyInvVariance.productName?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${phyInvVariance.date?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${phyInvVariance.partyIdTo?if_exists}(${phyInvVariance.partyId?if_exists})</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${phyInvVariance.varianceReason?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${phyInvVariance.quantity?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${phyInvVariance.comments?if_exists}</fo:block>
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
<#else>
<fo:block>No Record Found.</fo:block>
</#if>
 </fo:flow>
 </fo:page-sequence>
</fo:root>
</#escape>
