<#escape x as x?xml>
		<#if result.listIt.hasNext()>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table font-size="8pt" width="100%">
                <fo:table-column column-width="10%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="18%"/>
                <fo:table-column column-width="12%"/>
                <fo:table-column column-width="10%"/>
                <fo:table-column column-width="9%"/>
                <fo:table-column column-width="20%"/>
                <fo:table-column column-width="6%"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" >
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Payment Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>PaymentMethod</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>PaymentType</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Staus</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>From Party</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>To Party</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Effective date</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Amount</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list result.listIt as paymentReport>
                        <fo:table-row>
                            <fo:table-cell padding="1pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.paymentId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.paymentMethodTypeId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.paymentTypeId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.statusId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.partyIdFrom?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.partyIdTo?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.effectiveDate?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${paymentReport.amount?default("")}</fo:block>
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