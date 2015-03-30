<style>
	.rolledUp{
	  color:#222;
	}
	td{
		font-size:14px;
		height:25px;
		vertical-align:bottom;
	}
</style>
<#if parameters.print?has_content>
<script>
window.print();
</script>
</#if>
<#setting number_format="0.00">
<#assign customTimePeriod = parameters.customTimePeriodId1>
<#assign previousTimePeriod = parameters.customTimePeriodId2>

<#assign periodMap = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriod)>

<#assign periodMap2 = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",parameters.customTimePeriodId2)>

<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#assign orgParty = delegator.findOne("PartyGroup",true,"partyId",parameters.organizationPartyId)>
<#assign partyIds = Static["org.ofbiz.party.party.PartyWorker"].getAssociatedPartyIdsByRelationshipType(delegator, parameters.organizationPartyId, "GROUP_ROLLUP")>
<#assign organizationId = parameters.organizationPartyId>
	<#assign assetAccounts = ["4300000","4100000"]>
	<#assign liabilityAccounts =["3100000","3200000","3500000","3600000","3000000_1","3000000_2","3000000_3","3000000_4"]>
	
		[<#list partyIds as party><#assign partyGroup = delegator.findOne("PartyGroup",true,"partyId",party)>${partyGroup.groupName}, </#list>] 
					<table style="width:75% !important" id="detailBalanceSheet">
					<tr>
						<td> Balance Sheet As At </td>
						<td style="text-align:right;text-decoration:underline;font-weight:bold"> ${periodMap.thruDate?string("dd-MM-yyyy")}</td>
						<td colspan="4" style="text-align:right;text-decoration:underline;font-weight:bold">${periodMap2.thruDate?string("dd-MM-yyyy")}</td>
						</td>
					</tr>
					<tr>
						<td colspan="5" style="font-size:16px"><h2>Source of Funds</h2></td>
					</tr>
			<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":"3400000"})>
			<#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
			<#assign hasChild=true>
			<tr>
				<td colspan="2" style="padding-left:0px;font-weight:bold;">
					<h2>Shareholders' Fund</h2>
				</td>
			</tr>
			<tr>
				<td style="padding-right:10px;font-weight:bold">${glAccount.accountName}</td>
				<#if !childGlAccounts?has_content>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
				customTimePeriod, delegator, organizationId)>
				<td></td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
				previousTimePeriod, delegator, organizationId)>
				<td></td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
				<#assign hasChild=false>
				</#if>
			</tr>
				<#if childGlAccounts?has_content>
				<#list childGlAccounts as childGlAccount>
				<@glAccountBalances childGlAccount 2/>
			</#list>
			</#if>
			<#if hasChild>
			<tr>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
				<td></td>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
			</tr>
			<tr>
				<td></td>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,customTimePeriod,
				organizationId, false)>
				<td style="text-align:right;font-weight:bold;padding-right:10px" class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
				<td></td>
				<td></td>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,previousTimePeriod,
				organizationId, false)>
				<td style="text-align:right;font-weight:bold;padding-right:10px" class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
			</tr>
			</#if>
			<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":"3300000"})>
			<#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
			<#assign hasChild=true>
			<tr>
				<td colspan="5" style="padding-left:0px;font-weight:bold;">
					<h2>Loan Funds</h2>
				</td>
			</tr>
			<tr>
				<td style="padding-right:10px;font-weight:bold">${glAccount.accountName}</td>
				<#if !childGlAccounts?has_content>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
				customTimePeriod, delegator, organizationId)>
				<td></td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
				previousTimePeriod, delegator, organizationId)>
				<td></td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
				<#assign hasChild=false>
				</#if>
			</tr>
				<#if childGlAccounts?has_content>
				<#list childGlAccounts as childGlAccount>
				<@glAccountBalances childGlAccount 2/>
			</#list>
			</#if>
			<#if hasChild>
			<tr>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
				<td></td>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
			</tr>
			<tr>
				<td></td>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,customTimePeriod,
				organizationId, false)>
				<td style="text-align:right;font-weight:bold;padding-right:10px" class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
				<td></td>
				<td></td>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,previousTimePeriod,
				organizationId, false)>
				<td style="text-align:right;font-weight:bold;padding-right:10px" class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
			</tr>
			</#if>
						
						<tr>
				<td colspan="2" style="padding-left:0px;font-weight:bold;">
					<h2>Current Liabilities</h2>
				</td>
			</tr>
						<#list liabilityAccounts as glAccountId>
						<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
						<#assign childGlAccounts =
						delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
						<#assign hasChild=true>
						<tr>
							<td width="50%" style="padding-right:10px;font-weight:bold">${glAccount.accountName}</td>
							<#if !childGlAccounts?has_content>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
							customTimePeriod, delegator, organizationId)>
							<td style="text-align:right;padding-right:10px" class="rolledUp" width="40%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
							previousTimePeriod, delegator, organizationId)>
							<td/><td/>
							<td style="text-align:right;padding-right:10px" class="rolledUp" width="40%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
							<#assign hasChild=false>
							</#if>
						</tr>
						<#if childGlAccounts?has_content>
						<#list childGlAccounts as childGlAccount>
						<@glAccountBalances childGlAccount 2/>
						</#list>
						</#if>
						<#if hasChild>
						<tr>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
							<td/><td/>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
						</tr>
						<tr>
							<td></td>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
							customTimePeriod,organizationId, false)>
							<td style="text-align:right;font-weight:bold;padding-right:10px" class="rolledUp" width="45%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
							<td/><td/>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
							previousTimePeriod,organizationId, false)>
							<td style="text-align:right;font-weight:bold;padding-right:10px" class="rolledUp" width="45%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
						</tr>
						</#if>
						</#list>
						<tr>
							<#assign profitAndLossAmt = Static["org.ofbiz.accounting.util.UtilAccounting"].getProfitAndLossAmount(delegator,customTimePeriod,organizationId)>
							<td>Profit & Loss A/c</td>
							<td style="text-align:right;padding-right:10px" class="rolledUp" width="45%"><@reportCurrency amount=profitAndLossAmt?if_exists/></td>
							<td/><td/>
							<#assign profitAndLossAmt = Static["org.ofbiz.accounting.util.UtilAccounting"].getProfitAndLossAmount(delegator,previousTimePeriod,organizationId)>
							<td style="text-align:right;padding-right:10px" class="rolledUp" width="45%"><@reportCurrency amount=profitAndLossAmt?if_exists/></td>
						</tr>
						<tr>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px">____________</td>
							<td/><td/>
							<td style="text-align:right;font-weight:bold;padding-right:10px">____________</td>
						</tr>
					<tr>
					<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"30000000",
					customTimePeriod, organizationId, false)>
						<td style="font-weight:bold;padding-left:55px"> Total Liabilities </td>
						<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=(glAccountAmount+profitAndLossAmt)?if_exists/></td>
					<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"30000000",
					previousTimePeriod, organizationId, false)>
						<td/><td/>
						<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=(glAccountAmount+profitAndLossAmt)?if_exists/></td>
						</tr>
						
						<tr>
							<td colspan="5">Application of Funds</td>
						</tr>
						<tr>
							<td style="font-weight:bold" colspan="5">Gross block</td>
						</tr>
						<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":"4200000"})>
						<#assign childGlAccounts = delegator.findByAnd("GlAccount",
						{"parentGlAccountId" : glAccount.glAccountId?if_exists})>
						<#if childGlAccounts?has_content>
						<#list childGlAccounts as childGlAccount>
						<@glAccountBalances	childGlAccount 2/>
						</#list>
						</#if>
						<tr>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
							<td></td>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
						</tr>
						<tr>
							<td style="font-weight:bold"></td>
							<#assign totalGrossAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"4200000",
							customTimePeriod, organizationId,
							true)>
							<td style="text-align:right;font-weight:bold;padding-right:10px"  class="rolledUp" width="40%"><@reportCurrency amount=totalGrossAmount?if_exists/></td>
							<td></td>
							<td></td>
							<#assign totalGrossAmount2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"4200000",
							previousTimePeriod, organizationId,
							true)>
							<td style="text-align:right;font-weight:bold;padding-right:10px"  class="rolledUp" width="40%"><@reportCurrency amount=totalGrossAmount2?if_exists/></td>
						</tr>

						<tr>
							<td style="font-weight:bold">Less:Accumulated depreciation</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":"4400000"})>
						<#assign childGlAccounts = delegator.findByAnd("GlAccount",
						{"parentGlAccountId" : glAccount.glAccountId?if_exists})>
						<#if childGlAccounts?has_content>
						<#list childGlAccounts as childGlAccount>
						<@glAccountBalances
						childGlAccount 2/>
						</#list>
						</#if>
						<tr>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
							<td></td>
							<td  width="10%"></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
						</tr>
						<tr>
							<td style="font-weight:bold"></td>
							<#assign totalDepreciation = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"4400000",
							customTimePeriod, organizationId,
							false)>
							<td style="text-align:right;font-weight:bold;padding-right:10px"  class="rolledUp" width="40%"><@reportCurrency amount=totalDepreciation?if_exists/></td>
							<td></td><td width="10%"></td>
							<#assign totalDepreciation2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"4400000",
							previousTimePeriod, organizationId,
							false)>
							<td style="text-align:right;font-weight:bold;padding-right:10px"  class="rolledUp" width="40%"><@reportCurrency amount=totalDepreciation2?if_exists/></td>
						</tr>
						<tr>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;" colspan="2">____________</td>
						</tr>
						<tr>
							<td style="font-weight:bold">Net Block</td>
							<#assign netblock = (totalGrossAmount-totalDepreciation)>
							<td style="text-align:right;font-weight:bold;padding-right:10px"  class="rolledUp" width="40%"><@reportCurrency amount=netblock?if_exists/></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="4"  class="rolledUp" width="40%"><@reportCurrency amount=netblock?if_exists/></td>
						</tr>

						<#list assetAccounts as glAccountId>
						<#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
						<#assign childGlAccounts =
						delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
						<#assign hasChild=true>
						<tr>
							<td width="40%" style="padding-right:10px;font-weight:bold">${glAccount.accountName}</td>
							<#if !childGlAccounts?has_content>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
							customTimePeriod, delegator, organizationId)>
							<td style="text-align:right;padding-right:10px"  class="rolledUp" width="45%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
							<#assign glAccountAmount2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
							previousTimePeriod, delegator, organizationId)>
							<td style="text-align:right;padding-right:10px" colspan="4"  class="rolledUp" width="45%"><@reportCurrency amount=glAccountAmount2?if_exists/></td>
							<#assign hasChild=false>
							</#if>
						</tr>
						<#if childGlAccounts?has_content>
						<#list childGlAccounts as childGlAccount>
						<@glAccountBalances childGlAccount 2/>
						</#list>
						</#if>
						<#if hasChild>
						<tr>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;">____________</td>
							<td></td>
							<td style="text-align:right;font-weight:bold;padding-right:10px;" colspan="2">____________</td>
						</tr>
						<tr>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
							customTimePeriod,
							organizationId, true)>
							<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2" class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
							<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
							previousTimePeriod,
							organizationId, true)>
							<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="4" class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
						</tr>
						</#if>
						</#list>
			<tr>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">____________</td>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2">____________</td>
			</tr>
			<tr>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"4000000",
				customTimePeriod, organizationId, true)>
				<td style="font-weight:bold;padding-left:25px"> Total Assets </td>
				<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=glAccountAmount/></td>
				<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"4000000",
				previousTimePeriod, organizationId, true)>
				<td/>
				<td style="text-align:right;font-weight:bold;padding-right:10px" colspan="2"><@reportCurrency amount=glAccountAmount/></td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<br />
					<br />
				</td>
			</tr>
		</table>

<#macro glAccountBalances glAccount level>
<#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
<#assign childPresent = childGlAccounts.size()>
<tr>
	<td style="text-align:left;padding-left:${15*level}px;font-style:italic" width="35%">${glAccount.accountName}</td>
	<#assign glAccountAmount =
	Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,customTimePeriod, delegator,organizationId)>
	<td style="text-align:right;padding-right:10px"  class="single" width="20%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
	<td width="20%"></td>
	<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,
			previousTimePeriod, delegator, organizationId)>
	<td width="20%"></td>
	<td style="text-align:right;padding-right:10px"><@reportCurrency amount=glAccountAmount?if_exists/></td>
</tr>
<#list childGlAccounts as childGl>
	<@glAccountBalances childGl level+1/>
</#list>
</#macro>


