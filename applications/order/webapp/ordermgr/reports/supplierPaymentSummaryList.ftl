<div class="screenlet">
    <#if finalList?has_content>
    <table cellspacing="0" cellpadding="2" class="basic-table">
            <tr class="header-row">
                <td><fo:block text-align="center">Opening Balance</block></td>
                <td><fo:block text-align="center">Currency</block></td>
                <td><fo:block text-align="center">Payments</block></td>
                <td><fo:block text-align="center">Invoices</block></td>
                <td><fo:block text-align="center">Closing Balance</block></td>
            </tr>
        <#assign partyId = "">
            <#list finalList as value>
            <#if partyId == value.partyId>
            	<#else>
            	<#assign partyId = value.partyId>
            	<tr class="alternate-row">
            		<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : partyId}, true)>
                    <td colspan="5"><block style="text-align:left;font-weight:bold;">${partyId} - ${partyGroup.groupName?if_exists}</block></td>
                </tr>
            </#if>
                <tr>
                    <td>
					<#if value.amount lt 0>
                    	<#assign amount = (value.amount).negate()>
                    	<block style="text-align:right">${amount?string("#,##0.00")} CR</block>
                    	<#elseif value.amount gt 0>
                    	<block style="text-align:right">${value.amount?string("#,##0.00")} DR</block>
                    	<#else>
                    	<block style="text-align:right">${value.amount?string("#,##0.00")}</block>
                    </#if>                    
                    </td>
                    <td><block text-align="center">${value.currency?if_exists}</block></td>
                    <td>
                    <#if value.credit lt 0>
                    	<#assign creditAmount = (value.credit).negate()>
                    	<block style="text-align:right">${creditAmount?string("#,##0.00")} CR</block>
                    	<#elseif value.credit gt 0>
                    	<block style="text-align:right">${value.credit?string("#,##0.00")} DR</block>
                    	<#else>
                    	<block style="text-align:right">${value.credit?string("#,##0.00")}</block>
                    </#if>
                    </td>
                    <td>
                    <#if value.debit lt 0>
                    	<#assign debit = (value.debit).negate()>
                    	<block style="text-align:right">${debit?string("#,##0.00")} CR</block>
                    	<#elseif value.debit gt 0>
                    	<block style="text-align:right">${value.debit?string("#,##0.00")} DR</block>
                    	<#else>
                    	<block style="text-align:right">${value.debit?string("#,##0.00")}</block>
                    </#if>
                    </td>
                    <#assign amount = value.amount?if_exists>
                    <#assign credit = value.credit?if_exists>
                    <#assign debit = value.debit?if_exists>
                    <#assign closingBalance = amount + debit + credit>
                    <td>
                    <#if closingBalance lt 0>
                    	<#assign closingBalancePositive = -closingBalance>
                    	<block style="text-align:right">${closingBalancePositive?string("#,##0.00")} CR</block>
                    	<#elseif closingBalance gt 0>
                    	<block style="text-align:right">${closingBalance?string("#,##0.00")} DR</block>
                    	<#else>
                    	<block style="text-align:right">${closingBalance?string("#,##0.00")}</block>
                    </#if>
                    </td>
                </tr>
            </#list>
    </table>
    <#else>
    <block text-align="center">No Record Found.</block>
    </#if>
