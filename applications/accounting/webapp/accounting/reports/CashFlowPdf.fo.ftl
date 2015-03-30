<#escape x as x?xml>
    <#setting number_format="0.00">
    <#assign customTimePeriodId = parameters.customTimePeriodId>
    <#assign customTimePeriod = customTimePeriod>
    <#include "component://common/webcommon/includes/commonMacros.ftl"/>
    <#assign organizationId = parameters.organizationPartyId>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Cash Movement Statement</fo:block>
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

    <#if parameters.print?has_content>
        <fo:block font-size="12pt" font-weight="bold" margin-left="5px">Cash Movement As At ${periodMap.thruDate?string("dd-MM-yyyy")}</fo:block>
    <#else>
        <fo:block font-size="12pt" font-weight="bold" margin-left="5px">Cash Movement As At ${periodMap.thruDate?string("dd-MM-yyyy")}</fo:block>
    </#if>
    <fo:block><fo:leader></fo:leader></fo:block>

    <fo:block space-after.optimum="10pt" font-size="9pt">

        <fo:table>
            <fo:table-body>

                <#assign openingBalanceOfBank = Static["org.ofbiz.accounting.util.CashFlowUtil"].getOpeningBalanceOfBankAccountForGivenPeriod(delegator,customTimePeriodId,organizationId)>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="170px" margin-right="10px">
                            Opening Balance of Bank
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=openingBalanceOfBank?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                     <fo:table-cell>
                        <fo:block font-weight="bold" margin-left="10px">
                            INFLOW:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign cashReceivedFromCustomer = Static["org.ofbiz.accounting.util.CashFlowUtil"].getCustomerReceiptAmountForGivenPeriod(delegator,customTimePeriodId,organizationId)>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="30px" margin-right="10px">
                            Receipts From Customers
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=cashReceivedFromCustomer?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign assetInflowTotal = 0.00/>
                <#assign assetInflowList = Static["org.ofbiz.accounting.util.CashFlowUtil"].getInflowFromAsset(delegator,customTimePeriodId,organizationId)/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="30px" margin-right="10px">
                            Asset Inflows
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=assetInflowTotal?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#if assetInflowList?has_content>
                    <#list assetInflowList as assetInflow>
                        <fo:table-row>
                            <#assign assetInflowTotal = assetInflowTotal+assetInflow.amount/>
                            <fo:table-cell>
                                <fo:block style="font-style:italic;"  margin-left="75px" margin-right="10px">
                                    ${assetInflow.accountName?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px">
                                    <@reportCurrency amount=assetInflow.amount?if_exists/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#list>
                </#if>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;"  margin-left="75px" margin-right="10px">
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            _____________
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign indirectProductionInflowTotal = 0.00/>
                <#assign indirectProductionInflowList = Static["org.ofbiz.accounting.util.CashFlowUtil"].getInflowFromIndirectProduction(delegator,customTimePeriodId,organizationId)/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="30px" margin-right="10px">
                            Expense Inflows
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#if indirectProductionInflowList?has_content>
                    <#list indirectProductionInflowList as indirectProductionInflow>
                        <fo:table-row>
                            <#assign indirectProductionInflowTotal = indirectProductionInflowTotal+indirectProductionInflow.amount/>
                            <fo:table-cell>
                                <fo:block style="font-style:italic;" margin-left="40px" margin-right="10px">
                                    ${indirectProductionInflow.accountName?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px">
                                    <@reportCurrency amount=indirectProductionInflow.amount?if_exists/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#list>
                </#if>
                
                <fo:table-row>
                     <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=indirectProductionInflowTotal?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;"  margin-left="75px" margin-right="10px">
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            _____________
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign otherInflowAmount = Static["org.ofbiz.accounting.util.CashFlowUtil"].getOtherInflowAmountForAGivenPeriod(delegator,customTimePeriodId,organizationId)/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="30px" margin-right="10px">
                            Other Amount Received
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                       <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=otherInflowAmount?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;"  margin-left="75px" margin-right="10px">
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            _____________
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign totalInflow=otherInflowAmount+indirectProductionInflowTotal+assetInflowTotal+cashReceivedFromCustomer/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="200px" margin-right="10px">
                            TOTAL RECEIPTS
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=totalInflow?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;"  margin-left="75px" margin-right="10px">
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            <fo:external-graphic src="./applications/accounting/webapp/accounting/images/doubleline.png"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block font-weight="bold" margin-left="10px">
                            OUTFLOW:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign cashPaidOutAmount=Static["org.ofbiz.accounting.util.CashFlowUtil"].getSupplierPaidAmountForGivenPeriod(delegator,customTimePeriodId,organizationId)/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="30px" margin-right="10px">
                            Payments to Suppliers
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=cashPaidOutAmount?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign indirectProductionOutflowTotal = 0.00/>
                <#assign indirectProductionOutflowList = Static["org.ofbiz.accounting.util.CashFlowUtil"].getOutflowFromIndirectProduction(delegator,customTimePeriodId,organizationId)/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="30px" margin-right="10px">
                            Expense Outflow
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#if indirectProductionOutflowList?has_content>
                    <#list indirectProductionOutflowList as indirectProductionOutflow>
                        <fo:table-row>
                            <#assign indirectProductionOutflowTotal = indirectProductionOutflowTotal+indirectProductionOutflow.amount/>
                            <fo:table-cell>
                                <fo:block style="font-style:italic;" margin-left="40px" margin-right="10px">
                                    ${indirectProductionOutflow.accountName?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px">
                                    <@reportCurrency amount=indirectProductionOutflow.amount?if_exists/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#list>
                </#if>
                
                <fo:table-row>
                     <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=indirectProductionOutflowTotal?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;"  margin-left="75px" margin-right="10px">
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            _____________
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign totalOutflow = cashPaidOutAmount+indirectProductionOutflowTotal/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="195px" margin-right="10px">
                            TOTAL PAYMENTS
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=totalOutflow?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" margin-left="75px" margin-right="10px">
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            <fo:external-graphic src="./applications/accounting/webapp/accounting/images/doubleline.png"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <#assign surplusAmount=totalInflow-totalOutflow/>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="170px" margin-right="10px">
                            Closing Balance of Bank
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold">
                            <@reportCurrency amount=openingBalanceOfBank+surplusAmount?if_exists/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" margin-left="75px" margin-right="10px">
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            <fo:external-graphic src="./applications/accounting/webapp/accounting/images/doubleline.png"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

            </fo:table-body>
        </fo:table>

    </fo:block>

</#escape>