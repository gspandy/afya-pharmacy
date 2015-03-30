<#escape x as x?xml>
		<#if result.listIt.hasNext()>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table font-size="8pt" width="100%">
                <fo:table-header>
                    <fo:table-row font-weight="bold" >
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Account-Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Type</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Status</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Name</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Code</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Pin</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Organization</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Owner</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Post To GL</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Is Refundable</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Actual Balance</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Available Balance</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list result.listIt as accountReport>
                        <fo:table-row>
                            <fo:table-cell padding="1pt" background-color="${rowColor}">
                                <fo:block>${accountReport.finAccountId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.finAccountTypeId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.finAccountName?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.finAccountCode?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.finAccountPin?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.organizationPartyId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.ownerPartyId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.postToGlAccountId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.isRefundable?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.actualBalance?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${accountReport.availableBalance?default("")}</fo:block>
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