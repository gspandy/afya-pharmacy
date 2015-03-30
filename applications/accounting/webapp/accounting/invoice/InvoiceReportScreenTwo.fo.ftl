<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#escape x as x?xml>
    <fo:block font-size="15pt" text-align="center" font-weight="bold">Invoice Report</fo:block>
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
    <#if invoiceItemList?has_content>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-header> 
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Party</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Invoice Type</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Invoice Date</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="5px">Invoiced</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Due Date</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="5px">Applied</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="5px">Paid Amount</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="5px">Outstanding</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list invoiceItemList as listIt>
                        <#assign displayDueAmountResults = dispatcher.runSync("getInvoicePaymentInfoList", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", listIt.invoiceId,"userLogin", userLogin))/>
                        <#assign entries = displayDueAmountResults.entrySet()>
                        <#assign outstandingAmount = Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceNotApplied(delegator, listIt.invoiceId)*Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceCurrencyConversionRate(delegator, listIt.invoiceId)> 
                        <#assign amount = Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTotal(delegator, listIt.invoiceId)*Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceCurrencyConversionRate(delegator, listIt.invoiceId)>
                        <fo:table-row text-align="center" height="15pt" font-size="9pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if "PURCHASE_INVOICE" == listIt.invoiceTypeId || "CUST_RTN_INVOICE" ==listIt.invoiceTypeId || "PAYROL_INVOICE" ==listIt.invoiceTypeId || "COMMISSION_INVOICE" ==listIt.invoiceTypeId>
                                    <#assign party = delegator.findOne("PartyNameView", {"partyId" : listIt.partyIdFrom}, true)>
                                    <fo:block>${party.groupName?if_exists} ${party.firstName?if_exists} ${party.lastName?if_exists}</fo:block>
                                </#if>
                                <#if "SALES_INVOICE" == listIt.invoiceTypeId || "INTEREST_INVOICE" == listIt.invoiceTypeId || "PURC_RTN_INVOICE" ==listIt.invoiceTypeId>
                                    <#assign party = delegator.findOne("PartyNameView", {"partyId" : listIt.partyId}, true)>
                                    <fo:block>${party.groupName?if_exists} ${party.firstName?if_exists} ${party.lastName?if_exists}</fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="3pt" background-color="${rowColor}">
                                <fo:block>${listIt.invoiceTypeDesc?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="3pt" background-color="${rowColor}">
                                <fo:block>${listIt.invoiceDate?string('dd-MM-yyyy')}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="3pt" text-align="right" background-color="${rowColor}">
                               <fo:block><@ofbizCurrency amount = amount isoCode=listIt.currencyUomId?if_exists/></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="3pt" text-align="right" background-color="${rowColor}">
                                <#list entries as entry>
                                    <#assign displayDueDates = entry.getValue()>
                                    <#list displayDueDates as displayDueDate>
                                        <#if displayDueDate.invoiceTermId?has_content>
                                            <fo:block>${displayDueDate.dueDate?string('dd-MM-yyyy')}</fo:block>
                                        </#if>
                                    </#list>
                                </#list>
                            </fo:table-cell>
                            <fo:table-cell padding="3pt" text-align="right" background-color="${rowColor}">
                                <#list entries as entry>
                                    <#assign displayDueAmounts = entry.getValue()>
                                    <#list displayDueAmounts as displayDueAmount>
                                        <#assign amounts = displayDueAmount.amount>
                                        <fo:block><@ofbizCurrency amount = amounts isoCode=listIt.currencyUomId?if_exists/></fo:block>
                                    </#list>
                                </#list>
                            </fo:table-cell>
                            <fo:table-cell padding="3pt" text-align="right" background-color="${rowColor}">
                                <#list entries as entry>
                                    <#assign displayPaidAmounts = entry.getValue()>
                                    <#list displayPaidAmounts as displayPaidAmount>
                                        <#assign paidAmount = displayPaidAmount.paidAmount>
                                        <fo:block><@ofbizCurrency amount = paidAmount isoCode=listIt.currencyUomId?if_exists/></fo:block>
                                    </#list>
                                </#list>
                            </fo:table-cell>
                            <fo:table-cell padding="3pt" text-align="right" background-color="${rowColor}">
                                <#list entries as entry>
                                    <#assign displayOutStnAmountsInCaseInvoiceTerms = entry.getValue()>
                                    <#list displayOutStnAmountsInCaseInvoiceTerms as displayOutStnAmountsInCaseInvoiceTerm>
                                        <#assign outstnAmount = displayOutStnAmountsInCaseInvoiceTerm.outstandingAmount>
                                        <fo:block><@ofbizCurrency amount = outstnAmount isoCode=listIt.currencyUomId?if_exists/></fo:block>
                                    </#list>
                                </#list>
                            </fo:table-cell>
                        </fo:table-row>
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
        <fo:block>No records found.</fo:block>
    </#if>
</#escape>
