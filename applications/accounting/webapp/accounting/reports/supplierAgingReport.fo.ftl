<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Creditors Aging</fo:block>
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
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(70)"/>
        <fo:table-column column-width="proportional-column-width(40)"/>
        <fo:table-body>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                         From Date : ${parameters.fromDate?if_exists?string("dd/MM/yyyy")}</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                         Thru Date : ${parameters.thruDate?if_exists?string("dd/MM/yyyy")}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block><fo:leader></fo:leader></fo:block>
    <#assign invoicePaymentInfoList = parameters.invoicePaymentInfoList?default({})>
    <#if invoicePaymentInfoList?has_content>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Invoice ID</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Term Type ID</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Supplier Code – Supplier Name</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Due Date</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Invoice Amount</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Paid Amount</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Outstanding Amount</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list invoicePaymentInfoList as invoicePaymentInfo>
                        <fo:table-row text-align="center" height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                ${invoicePaymentInfo.invoiceId?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                  <#if invoicePaymentInfo.termTypeId?has_content>
                                  <#assign termType = delegator.findOne("TermType",{"termTypeId",invoicePaymentInfo.termTypeId},false)>
                                  	  ${termType.description?if_exists}
                                  </#if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                 <#if invoicePaymentInfo.partyIdFrom?has_content>
                                 <#assign partyGroup = delegator.findOne("PartyGroup",{"partyId",invoicePaymentInfo.partyIdFrom},false)>
                                 	${partyGroup.partyId} - ${partyGroup.groupName}
                                 </#if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                  <#if invoicePaymentInfo.dueDate?has_content>
                                	${invoicePaymentInfo.dueDate?string("dd/MM/yyyy")}
                                  </#if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                 <@ofbizCurrency amount=invoicePaymentInfo.amount?if_exists />
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                 <@ofbizCurrency amount=invoicePaymentInfo.paidAmount?if_exists />
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                 <@ofbizCurrency amount=invoicePaymentInfo.outstandingAmount?if_exists />
                                </fo:block>
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
