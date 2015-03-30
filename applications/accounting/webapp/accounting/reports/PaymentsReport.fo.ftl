<#escape x as x?xml>
    <fo:block font-size="15pt" text-align="center" font-weight="bold">Payment Report</fo:block>
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
    <#if resultList?has_content>
        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-column column-width="proportional-column-width(2.5)"/>
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Party </fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Payment Type</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Effective Date</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="30px">Amount </fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Currency</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Status</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list resultList as paymentGv>
                        <fo:table-row text-align="center" height="15pt" font-size="9pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if "CUSTOMER_DEPOSIT" == paymentGv.paymentTypeId || "INTEREST_RECEIPT" ==paymentGv.paymentTypeId || "CUSTOMER_PAYMENT" ==paymentGv.paymentTypeId || "POS_PAID_IN" ==paymentGv.paymentTypeId >
                                    <#assign party = delegator.findOne("PartyNameView", {"partyId" : paymentGv.partyIdFrom}, true)>
                                    <fo:block margin-top="3px">${party.groupName?if_exists} ${party.firstName?if_exists} ${party.lastName?if_exists}</fo:block>
                                <#else>
                                    <#assign party = delegator.findOne("PartyNameView", {"partyId" : paymentGv.partyIdTo}, true)>
                                    <fo:block margin-top="3px">${party.groupName?if_exists} ${party.firstName?if_exists} ${party.lastName?if_exists}</fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#assign paymentDescription = delegator.findOne("PaymentType", {"paymentTypeId" : paymentGv.paymentTypeId}, true)>
                                <fo:block margin-top="3px">${paymentDescription.description?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${paymentGv.effectiveDate?string('dd-MM-yyyy')}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}" text-align="right">
                                <fo:block text-align="right" margin-right="30px" margin-top="3px">${paymentGv.amount?if_exists?default(0)?string("#,##0.00")}</fo:block> 
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}"  text-align="center">
                                <#assign currencyDescription = delegator.findOne("Uom", {"uomId" : paymentGv.currencyUomId}, true)>
                                <fo:block margin-top="3px">${currencyDescription.abbreviation?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#assign statusDescription = delegator.findOne("StatusItem", {"statusId" : paymentGv.statusId}, true)>
                                <fo:block margin-top="3px">${statusDescription.description?if_exists}</fo:block>
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
