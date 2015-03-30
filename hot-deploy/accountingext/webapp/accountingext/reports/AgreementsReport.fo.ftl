<#escape x as x?xml>
      	<#if result.listIt.hasNext()>
        <fo:block space-after="10pt" font-size="10pt">
            <fo:table font-size="6pt" width="100%">
                <fo:table-column column-width="12%"/>
                <fo:table-column column-width="12%"/>
                <fo:table-column column-width="12%"/>
                <fo:table-column column-width="12%"/>
                <fo:table-column column-width="12%"/>
                <fo:table-column column-width="10%"/>
                <fo:table-column column-width="30%"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" >
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Agreement-Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Product-Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>From Party</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>To Party</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>RoleType From</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>RoleType To</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Description</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list result.listIt as agreementReport>
                        <fo:table-row>
                            <fo:table-cell padding="1pt" background-color="${rowColor}">
                                <fo:block>${agreementReport.agreementId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${agreementReport.productId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${agreementReport.partyIdFrom?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${agreementReport.partyIdTo?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${agreementReport.roleTypeIdFrom?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${agreementReport.roleTypeIdTo?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${agreementReport.description?default("")}</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#eeeeee">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>        
                    </#list>          
                </fo:table-body>
            </fo:table>
        </fo:block>
        <#else>
                <fo:block font-size="14pt">${uiLabelMap.NoRecordsFound}</fo:block>
        </#if>
</#escape>