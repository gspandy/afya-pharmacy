<#escape x as x?xml>

    <#setting number_format="0.00">
    <#assign customTimePeriod = parameters.customTimePeriodId>
    <#assign periodMap = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriod)>
    <#include "component://common/webcommon/includes/commonMacros.ftl"/>
    <#assign organizationId = parameters.organizationPartyId>
    <#assign periodMap = Static["org.ofbiz.accounting.util.UtilAccounting"].getReportingPeriod(delegator,parameters.customTimePeriodId)>
    <#assign turnOverGls =["10000000","1020000_1"]>
    <#assign expenseGls  =["2000000","2010000","2020000","2150000","3010000","3015000","3020000","3100000","3200000","4200000","4300000","4310000","4350000","4600000","4500000_1"]>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Profit And Loss</fo:block>

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

    <#assign rateOrAmount = "">
    <#if parameters.rateOrAmount?has_content>
        <#assign rateOrAmount = parameters.rateOrAmount>
    <#else>
        <#assign rateOrAmount = "rate">
    </#if>

    <#if parameters.print?has_content>
        <fo:block font-size="12pt" font-weight="bold" margin-left="5px">Profit And Loss for the period ending ${periodMap.thruDate?string("dd-MM-yyyy")}</fo:block>
    <#else>
        <fo:block font-size="12pt" font-weight="bold" margin-left="5px">Profit And Loss for the period ending ${periodMap.thruDate?string("dd-MM-yyyy")}</fo:block>
    </#if>

    <fo:block><fo:leader></fo:leader></fo:block>

    <fo:block space-after.optimum="10pt" font-size="9pt">
        <fo:table table-layout="fixed" space-after.optimum="2pt">
            <fo:table-body>
                <#list turnOverGls as glAccountId>
                    <#assign glAccount = delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
                    <#assign childGlAccounts = delegator.findByAnd("GlAccount",{"parentGlAccountId":glAccount.glAccountId?if_exists})>

                    <#assign hasChild=true>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-weight="bold" margin-left="30px">${glAccount.accountName}</fo:block>
                        </fo:table-cell>
                        <#if !childGlAccounts?has_content>
                            <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,customTimePeriod,delegator,organizationId)>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
                            </fo:table-cell>
                            <#assign hasChild=false>
                        </#if>
                    </fo:table-row>
                    <#if childGlAccounts?has_content>
                        <#list childGlAccounts as childGlAccount>
                            <@glAccountBalances childGlAccount 2 false/>
                        </#list>
                    </#if>
                    <#if hasChild>
                        <fo:table-row>
                            <fo:table-cell/>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px">________________</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell/>
                            <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,glAccount.glAccountId,customTimePeriod,organizationId,false)>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#if>
                </#list>

                <fo:table-row>
                    <fo:table-cell/>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">________________</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="300px" margin-right="10px">
                            Total
                        </fo:block>
                    </fo:table-cell>
                    <#assign turnOverAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"10000000",customTimePeriod,organizationId,false)>
                    <#assign otherIncomeTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1020000_1",customTimePeriod,organizationId,false)>
                    <#assign turnOverAmount = turnOverAmount+otherIncomeTotal>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=turnOverAmount?if_exists/></fo:block></fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block font-weight="bold" margin-left="10px">
                            Expense
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <#list expenseGls as glAccountId>
                    <#assign glAccount = delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})?default({})>
                    <#assign childGlAccounts = delegator.findByAnd("GlAccount",{"parentGlAccountId":glAccount.glAccountId?if_exists})>
                    <#assign hasChild=true>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-weight="bold" margin-left="30px">${glAccount.accountName?if_exists}</fo:block>
                        </fo:table-cell>
                        <#if !childGlAccounts?has_content>
                            <#assign glAccountAmount = 0.00/>
                            <#if glAccount.glAccountId?has_content>
                                <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,customTimePeriod,delegator,organizationId)>
                            </#if>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
                            </fo:table-cell>
                            <#assign hasChild=false>
                        </#if>
                    </fo:table-row>
                    <#if childGlAccounts?has_content>
                        <#list childGlAccounts as childGlAccount>
                            <@glAccountBalances childGlAccount 2 true/>
                        </#list>
                    </#if>
                    <#if hasChild>
                        <fo:table-row>
                            <fo:table-cell/>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px">________________</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell/>
                            <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,glAccount.glAccountId,customTimePeriod,organizationId,true)>
                            <fo:table-cell>
                                <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#if>
                </#list>
                <fo:table-row>
                    <fo:table-cell/>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">________________</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="300px" margin-right="10px">
                            Total
                        </fo:block>
                    </fo:table-cell>
                    <#assign incomeTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"10000000",customTimePeriod,organizationId,false)>
                    <#assign incomeTotal =  incomeTotal+ otherIncomeTotal/>
                    <#assign expenseTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"20000000",customTimePeriod,organizationId,true)>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=expenseTotal?default(0.00)/></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell/>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">________________</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block style="font-style:italic;" font-weight="bold" margin-left="170px" margin-right="10px">
                            Excess of Income over Expenditure
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=(incomeTotal-expenseTotal)/></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell/>
                    <fo:table-cell>
                        <fo:block width="40%" text-align="right" margin-right="270px">
                            <fo:external-graphic src="./applications/accounting/webapp/accounting/images/doubleline.png"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </fo:block>

    <#macro glAccountBalances glAccount level debitCreditFlag>
        <#assign childGlAccounts = delegator.findByAnd("GlAccount",{"parentGlAccountId":glAccount.glAccountId?if_exists},["-sortOrder"])>
        <#assign childPresent = childGlAccounts.size()&gt;0>
        <fo:table-row>
            <fo:table-cell>
                <fo:block style="font-style:italic;" margin-left="${30*level}px" margin-right="10px">${glAccount.accountName}</fo:block>
            </fo:table-cell>
            <#assign debitCreditFlag = Static["org.ofbiz.accounting.util.UtilAccounting"].isExpenseAccount(glAccount)>
            <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,customTimePeriod,delegator,organizationId)>
            <fo:table-cell>
                <fo:block width="40%" text-align="right" margin-right="270px"><#if !childPresent><@reportCurrency amount=glAccountAmount?if_exists/></#if></fo:block>
            </fo:table-cell>
        </fo:table-row>
        <#list childGlAccounts as childGl>
            <@glAccountBalances childGl level+1 debitCreditFlag/>
        </#list>
    </#macro>

</#escape>