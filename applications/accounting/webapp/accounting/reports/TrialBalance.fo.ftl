<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Trial Balance</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <#assign CustomTimePeriodGv =  delegator.findOne("CustomTimePeriod", {"customTimePeriodId":parameters.customTimePeriodId?if_exists},false)>
    <fo:block font-size="10pt" font-weight="bold">Custom Time Period: ${CustomTimePeriodGv.periodName?if_exists}: ${CustomTimePeriodGv.fromDate?string("yyyy-MM-dd")} - ${CustomTimePeriodGv.thruDate?string("yyyy-MM-dd")}</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>

    <#if glAccountAndHistoriesList?has_content>
        <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Account Id</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Account Name</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="35px">Dr</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="35px">Cr</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="15px">Ending Balance</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list glAccountAndHistoriesList as glAccountAndHistories>
                        <fo:table-row height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="center" margin-top="3px">${glAccountAndHistories.glAccountId?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="center" margin-top="3px">${glAccountAndHistories.accountName?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right" margin-right="20px" margin-top="3px"><@ofbizCurrency amount=glAccountAndHistories.totalPostedDebits?default(0)?if_exists isoCode=currencyUomId?if_exists /></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right" margin-right="20px" margin-top="3px"><@ofbizCurrency amount=glAccountAndHistories.totalPostedCredits?default(0)?if_exists isoCode=currencyUomId?if_exists /></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right" margin-right="20px" margin-top="3px"><@ofbizCurrency amount=glAccountAndHistories.totalEndingBalance?default(0)?if_exists isoCode=currencyUomId?if_exists /></fo:block>
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
            <fo:table table-layout="fixed" border-top-style="none" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-body>
                    <fo:table-row text-align="center" height="15pt">
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block margin-top="3px"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block margin-top="3px"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block font-weight="bold" text-align="right" margin-right="20px" margin-top="3px">${uiLabelMap.AccountingDebitFlag}: <@ofbizCurrency amount=grandTotalPostedRecord.totalPostedDebits?default(0)?if_exists isoCode=currencyUomId?if_exists /></fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block font-weight="bold" text-align="right" margin-right="20px" margin-top="3px">${uiLabelMap.AccountingCreditFlag}: <@ofbizCurrency amount=grandTotalPostedRecord.totalPostedCredits?default(0)?if_exists isoCode=currencyUomId?if_exists /></fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block margin-top="3px"></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:block>
    <#else>
        <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>
