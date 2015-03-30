<div class="screenlet">
	<table cellspacing="0" cellpadding="2" class="sample">
		<#assign assetAccounts = ["4110000"]>
		<tr><td colspan="2" style="font-weight:bold">Bank Account</td></tr>
		<#list assetAccounts as assetGlId>
			<@printCashRow assetGlId/>
		</#list>
		<tr><td></td></tr>
		<tr><td colspan="2" style="font-weight:bold"><hr>Cash</hr></td></tr>
		<tr><td></td></tr>
		<#assign assetAccounts = ["4120000"]>
		<#list assetAccounts as assetGlId>
			<@printCashRow assetGlId/>
		</#list>
	</table>
</div>

<#macro printCashRow glAccountId>
		<#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccountId?if_exists})>
		<#if childGlAccounts?has_content>
				<#list childGlAccounts as glAccount>
				<tr>
				<#assign cashAmount=Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTransactionAmount(delegator,glAccount.glAccountId,parameters.summaryDate,"10000",parameters.organizationPartyId,true)/>
					<td>${glAccount.accountName?if_exists}</td>
					<td class="align-text"><label><@ofbizCurrency amount=cashAmount/></label></td>
				</tr>
				<@printCashRow glAccount.glAccountId/>
				</#list>
		</#if>
</#macro>