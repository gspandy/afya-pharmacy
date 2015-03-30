<#escape x as x?xml>
    <fo:block font-size="15pt" text-align="center" font-weight="bold">Manufacturing Issuance</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <#if manufacturingIssuanceList?has_content>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Finished Good</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Raw Material</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Fixed Asset</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Issue/Manftd Qty</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Serial No.</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Description</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Variance Reason</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Date/Manftd Date</fo:block></fo:table-cell>
                       
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list manufacturingIssuanceList as manufacturingIssuance>
                        <fo:table-row text-align="center" height="15pt" font-size="9pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.productIdDes?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.productIdToDes?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.fixedAssetId?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.issuanceQty?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.serialNumber?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.description?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.serialzedComments?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${manufacturingIssuance.date?if_exists}</fo:block>
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
