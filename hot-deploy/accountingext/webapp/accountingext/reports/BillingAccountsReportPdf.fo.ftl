<#escape x as x?xml>
    	<#if result.listIt?exists && result.listIt.hasNext()>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table font-size="8pt" width="100%">
                <fo:table-header>
                    <fo:table-row font-weight="bold" >
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Billing Accoun Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Currency</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Account Limit</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>External Account</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Description</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list result.listIt as accountReport>
                        <fo:table-row>
                            <fo:table-cell padding="1pt" background-color="${rowColor}">
                                <fo:block>${accountReport.billingAccountId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.accountCurrencyUomId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.accountLimit?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.externalAccountId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.description?default("")}</fo:block>
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