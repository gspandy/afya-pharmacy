<#escape x as x?xml>
		<#if result.listIt.hasNext()>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table font-size="8pt" width="100%">
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="15%"/>
                <fo:table-column column-width="10%"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" >
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Invoice Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Invoice Type</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Invoice Date</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Description</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>From Party</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>To Party</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Total</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list result.listIt as ivoiceReport>
                        <fo:table-row>
                            <fo:table-cell padding="1pt" background-color="${rowColor}">
                                <fo:block>${ivoiceReport.invoiceId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${ivoiceReport.invoiceTypeId?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${ivoiceReport.invoiceDate?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${ivoiceReport.description?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${ivoiceReport.partyIdFrom?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${ivoiceReport.partyIdTo?default("")}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>
                                      ${Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTotal(delegator,ivoiceReport.invoiceId)}
								</fo:block>
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