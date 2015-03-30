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
 <fo:block font-size="15pt" text-align="center" font-weight="bold">ABC Analysis</fo:block>
 <fo:block style="color:#FFF"> </fo:block>
<#if abcAnalysis?has_content>
            <fo:table>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="150pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="120pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold">
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Product Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Description</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Total Quantity</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">Total Value</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">Usage</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">Cumulative</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list abcAnalysis as abc>
                        <fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${abc.productId?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${abc.productDsc?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="center">${abc.quantity?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right" font-family="Rupee,Arial">
                                <@ofbizCurrency amount=abc.value?if_exists/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right">${abc.usage?if_exists}%</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right">${abc.commulativeValue?if_exists}%</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    </#list>
                    	<fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="center">Total</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right" font-family="Rupee,Arial">
                                <@ofbizCurrency amount=totalValue?if_exists/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                </fo:table-body>
            </fo:table>
<#else>
<fo:block>No Record Found.</fo:block>
</#if>
 </fo:flow>
 </fo:page-sequence>
</fo:root>
</#escape>
