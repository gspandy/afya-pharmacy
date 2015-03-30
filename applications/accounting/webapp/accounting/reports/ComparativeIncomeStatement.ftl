<#if parameters.print?has_content>
<script>
window.print();
</script>
</#if>
<#-- <style>
	tr{
		border:1px solid red;
	}
</style> -->
<#setting number_format="0.00">
<#if parameters.customTimePeriodId1?has_content && parameters.customTimePeriodId2?has_content>
<#assign customTimePeriod = parameters.customTimePeriodId1>
<#assign customTimePeriod2 = parameters.customTimePeriodId2>
<#assign periodMap = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriod)>

<#assign periodMap2 = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriod2)>

<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#assign organizationId = parameters.organizationPartyId>
<#-- <#assign periodMap = Static["org.ofbiz.accounting.util.UtilAccounting"].getReportingPeriod(delegator,parameters.customTimePeriodId1)> -->
<div style="clear:both;" ">
	<#assign turnOverGls =["1100000"]>
	<#assign otherIncome =["1200000"]>
	<#assign expenseGls  =["2100000","2200000"]>
	<table>
<#assign rateOrAmount = "">		
<#if parameters.rateOrAmount?has_content>
<#assign rateOrAmount = parameters.rateOrAmount>
<#else>	
<#assign rateOrAmount = "rate">
</#if>	
	<tr style="font-size:14px;font-size:bold">
		<td style="font-size:14px;font-weight:bold">Profit And Loss for the period ending</td>
		<td/>
		<td colspan="2" style="text-align:right;text-decoration:underline;font-weight:bold">${periodMap.thruDate?string("dd-MM-yyyy")}</td>
		<td colspan="2" style="text-align:right;text-decoration:underline;font-weight:bold">${periodMap2.thruDate?string("dd-MM-yyyy")}</td>
	</tr>	
	
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	
	<#if !(parameters.print?has_content)>
	<div align="right">
		<tr>
			<td width="30%" style="font-weight:bold">Income</td>
			<td width="25%" style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"></td>
			<td width="10%">&nbsp;</td>
			<td width="25%" style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"></td>
			<td width="10%">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="5" style="padding-right:10px;font-style:italic;padding-left:50px">Total Turnover</td>
		</tr>
		<#list turnOverGls as glAccountId>
		<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
		<#assign childGlAccounts =
		delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
		<#assign hasChild=true>
		<tr>
			<td style="padding-right:10px;font-style:italic;padding-left:75px">${glAccount.accountName}</td>
			<#if !childGlAccounts?has_content>
			<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
			customTimePeriod, delegator, organizationId)>
			<td width="10%">&nbsp;</td>
			<td style="text-align:right;padding-right:10px" colspan="2"><@reportCurrency amount=glAccountAmount?if_exists/></td>
			<#assign glAccountAmount2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
			customTimePeriod2, delegator, organizationId)>
			<td style="text-align:right;padding-right:10px" colspan="2"><@reportCurrency amount=glAccountAmount2?if_exists/></td>
			<#assign hasChild=false>
			</#if>
		</tr>
		<#if childGlAccounts?has_content>
		<#list childGlAccounts as childGlAccount>
			<@glAccountBalances childGlAccount 2 false/>
		</#list>
		</#if>
		<#if hasChild>
		</#if>
		</#list>
		<tr>
			<td></td>
			<td/>
			<td style="text-align:right" colspan="2">________________</td>
			<td>&nbsp;</td>
			<td style="text-align:right">________________</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td></td>
			<td/>
			<#assign turnOverAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1100000",customTimePeriod,
			organizationId,false)>
			
			<#assign turnOverAmount2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1100000",customTimePeriod2,
			organizationId,false)>
			
			<#assign otherIncome = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1200000",customTimePeriod,
			organizationId,false)>
			<#assign otherIncome2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1200000",customTimePeriod2,
			organizationId,false)>
			
			<#assign turnOverAmount = turnOverAmount-otherIncome>
			<#assign turnOverAmount2 = turnOverAmount2-otherIncome2>
			<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"><@reportCurrency amount=turnOverAmount?if_exists/></td>
			<td/>
			<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=turnOverAmount2?if_exists/></td>
		</tr>
		<tr>
			<td colspan="3" style="font-weight:bold">
				Expense
			</td>
		</tr>
		<#list expenseGls as glAccountId>
						<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
						<#assign childGlAccounts =
						delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
						<#assign hasChild=true>
						<tr>
							<td style="padding-right:10px;font-style:italic;padding-left:50px">${glAccount.accountName}</td>
							<#if !childGlAccounts?has_content>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
							customTimePeriod, delegator, organizationId)>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"><@reportCurrency amount=glAccountAmount?if_exists/></td>
							<#assign glAccountAmount2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
							customTimePeriod2, delegator, organizationId)>
							<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"><@reportCurrency amount=glAccountAmount2?if_exists/></td>
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
							<td></td>
							<td></td>
							<td style="text-align:right" colspan="2">________________</td>
							<td/>
							<td style="text-align:right">________________</td>
						</tr>
						<tr>
							<td></td>
							<td></td>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
							customTimePeriod,		organizationId, true)>
							<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"><@reportCurrency amount=glAccountAmount?if_exists/></td>
							<td></td>
							<#assign glAccountAmount2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
							customTimePeriod2,		organizationId, true)>
							<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=glAccountAmount2?if_exists/></td>
						</tr>
						</#if>
		</#list>
		<tr>
			<td></td>
			<td></td>
			<td style="text-align:right;font-weight:bold" colspan="2">________________</td>
			<td></td>
			<td style="text-align:right;font-weight:bold">________________</td>
		</tr>
		<tr>
		<td style="font-weight:bold;">Total</td>
			<#assign incomeTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1000000",customTimePeriod,
			organizationId,false)>
			<#assign expenseTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"2000000",customTimePeriod,
			organizationId,true)>
			<td></td>
			<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"><@reportCurrency amount=expenseTotal?default(0.00)/></td>
			<td></td>
			<#assign incomeTotal2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"1000000",customTimePeriod2,
			organizationId,false)>
			<#assign expenseTotal2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"2000000",customTimePeriod2,
			organizationId,true)>
			<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=expenseTotal2?default(0.00)/></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td style="text-align:right;font-weight:bold" colspan="2">________________</td>
			<td></td>
			<td style="text-align:right;font-weight:bold">________________</td>
		</tr>
		<tr>
			<td style="font-weight:bold;">
				Excess of Income over Expenditure
			</td>
			<td></td>
			<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"><@reportCurrency amount=incomeTotal-expenseTotal/></td>
			<td></td>
			<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=incomeTotal2-expenseTotal2/></td>
		</tr>
		<tr>
			<td></td><td></td>
			<td style="text-align:right;font-weight:bold;" colspan="2">
				<image src="/accounting/images/doubleline.png" />
			</td>
			<td style="text-align:right;font-weight:bold;" colspan="2">
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
		<td></td>
		<#assign glAccountAmount =
		Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,glAccount.glAccountId,customTimePeriod,
		organizationId,debitCreditFlag)>
		<td style="text-align:right;padding-right:10px" colspan="2"><#if !childPresent><@reportCurrency amount=glAccountAmount?if_exists/></#if></td>
		<td></td>
		<#assign glAccountAmount2 =
		Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,glAccount.glAccountId,customTimePeriod2,
		organizationId,debitCreditFlag)>
		<td style="text-align:right;padding-right:10px"><#if !childPresent><@reportCurrency amount=glAccountAmount2?if_exists/></#if></td>
		<td></td>
	</tr>
	<#list childGlAccounts as childGl>
	<@glAccountBalances childGl level+1 debitCreditFlag/>
	</#list>
	</#macro>	
</#if>
	