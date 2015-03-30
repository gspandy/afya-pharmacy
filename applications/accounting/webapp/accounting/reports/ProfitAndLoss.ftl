<#if parameters.print?has_content>
<script>
    window.print();
</script>
</#if>

<#setting number_format="0.00">
<#assign customTimePeriod = parameters.customTimePeriodId>
<#assign periodMap = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriod)>
<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#assign organizationId = parameters.organizationPartyId>
<#assign periodMap = Static["org.ofbiz.accounting.util.UtilAccounting"].getReportingPeriod(delegator,parameters.customTimePeriodId)>
<div style="clear:both;">
<#assign turnOverGls =["10000000","1020000_1"]>
<#assign expenseGls  =["2000000","2010000","2020000","2150000","3010000","3015000","3020000","3100000","3200000","4200000",
"4300000","4310000","4350000","4600000","4500000_1"]>
<table>
<#assign rateOrAmount = "">
<#if parameters.rateOrAmount?has_content>
    <#assign rateOrAmount = parameters.rateOrAmount>
<#else>
    <#assign rateOrAmount = "rate">
</#if>

    <caption>
    <#if parameters.print?has_content>
        <h3>Profit And Loss for the period ending ${periodMap.thruDate?string("dd-MM-yyyy")}</h3>
    <#else>
        <h3>Profit And Loss for the period ending ${periodMap.thruDate?string("dd-MM-yyyy")}</h3>
    </#if>
    </caption>
<#if !(parameters.print?has_content)>
    <div align="right">
        <tr>
            <td width="15%" style="text-align:right;font-weight:bold;padding-right:10px" colspan="2">
            </td>
            <td width="45%">&nbsp;
            </td>
        </tr>

        <#list turnOverGls as glAccountId>
            <#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
            <#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>

            <#assign hasChild=true>
            <tr>
                <td style="padding-right:10px;padding-left:50px;font-weight:bold">${glAccount.accountName}</td>
                <#if !childGlAccounts?has_content>
                    <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
                    customTimePeriod, delegator, organizationId)>
                    <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
                    <#assign hasChild=false>
                </#if>
            </tr>
            <#if childGlAccounts?has_content>
                <#list childGlAccounts as childGlAccount>
                    <@glAccountBalances childGlAccount 2 false/>
                </#list>
            </#if>
            <#if hasChild>
                <tr>
                    <td/>
                    <td style="text-align:right">________________</td>
                </tr>
                <tr>
                    <td/>
                    <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,customTimePeriod,organizationId, false)>
                    <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
                </tr>
            </#if>
        </#list>

        <tr>
            <td/>
            <td style="text-align:right">________________</td>
        </tr>
        <tr>
            <td style="padding-right:10px;font-style:italic;padding-left:250px;font-weight:bold;">Total</td>
            <#assign turnOverAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"10000000",customTimePeriod,
            organizationId,false)>
            <#assign otherIncomeTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1020000_1",customTimePeriod,
            organizationId,false)>
            <#assign turnOverAmount = turnOverAmount+otherIncomeTotal>
            <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=turnOverAmount?if_exists/></td>
        </tr>
        <tr>
            <td colspan="3" style="font-weight:bold">
                Expense
            </td>
        </tr>
        <#list expenseGls as glAccountId>
            <#assign glAccount = delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})?default({})>
            <#assign childGlAccounts =
            delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
            <#assign hasChild=true>
            <tr>
                <td style="padding-right:10px;font-weight:bold;padding-left:50px">${glAccount.accountName?if_exists}</td>
                <#if !childGlAccounts?has_content>
                    <#assign glAccountAmount = 0.00/>
                    <#if glAccount.glAccountId?has_content>
                        <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
                        customTimePeriod, delegator, organizationId)>
                    </#if>
                    <td style="text-align:right;font-weight:bold;padding-right:10px" ><@reportCurrency amount=glAccountAmount?if_exists/></td>
                    <#assign hasChild=false>
                </#if>
            </tr>
            <#if childGlAccounts?has_content>
                <#list childGlAccounts as childGlAccount>
                    <@glAccountBalances childGlAccount 2 true/>
                </#list>
            </#if>
            <#if hasChild>
                <tr>
                    <td/>
                    <td style="text-align:right">________________</td>
                </tr>
                <tr>
                    <td/>
                    <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
                    customTimePeriod,		organizationId, true)>
                    <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
                </tr>
            </#if>
        </#list>
        <tr>
            <td/>
            <td style="text-align:right;font-weight:bold">________________</td>
        </tr>
        <tr>
            <td style="padding-right:10px;font-style:italic;padding-left:250px;font-weight:bold;">Total</td>
            <#assign incomeTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"10000000",customTimePeriod,
            organizationId,false)>
            <#assign incomeTotal =  incomeTotal+ otherIncomeTotal/>
            <#assign expenseTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"20000000",customTimePeriod,
            organizationId,true)>
            <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=expenseTotal?default(0.00)/></td>
        </tr>
        <tr>
            <td/>
            <td style="text-align:right;font-weight:bold">________________</td>
        </tr>
        <tr>
            <td style="padding-right:10px;font-style:italic;padding-left:99px;font-weight:bold;">
                Excess of Income over Expenditure
            </td>
            <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=(incomeTotal-expenseTotal)/></td>
        </tr>
        <tr>
            <td/>
            <td style="text-align:right;font-weight:bold;">
                <image src="/accounting/images/doubleline.png" />
            </td>
        </tr>
</table>
</div>
</#if>

<#macro glAccountBalances glAccount level debitCreditFlag>
    <#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" :
    glAccount.glAccountId?if_exists},["-sortOrder"])>
    <#assign childPresent = childGlAccounts.size()&gt;0>
<tr>
    <td style="text-align:left;padding-left:${40*level}px;font-style:italic">${glAccount.accountName}</td>
    <#assign debitCreditFlag = Static["org.ofbiz.accounting.util.UtilAccounting"].isExpenseAccount(glAccount)>
    <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
    customTimePeriod, delegator, organizationId)>
    <td style="text-align:right;padding-right:10px"><#if !childPresent><@reportCurrency amount=glAccountAmount?if_exists/></#if></td>
    <td></td>
</tr>
    <#list childGlAccounts as childGl>
        <@glAccountBalances childGl level+1 debitCreditFlag/>
    </#list>
</#macro>

