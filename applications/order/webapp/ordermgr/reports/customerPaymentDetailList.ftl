    <#if groupedOpeningBalanceMap?has_content>
    <div class="screenlet">
    <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
                <td><block text-align="center">Date</block></td>
                <td><block text-align="center">Payment/Invoice</block></td>
                <td><block text-align="center">Transaction Type</block></td>
                <td><block text-align="center">Reference</block></td>
                <td><block text-align="center">Amount</block></td>
                <td><block text-align="center">Closing Balance</block></td>
        </tr>
          <#list groupedOpeningBalanceMap.entrySet() as entry>
          	<#assign key = entry.getKey()>
            <#assign list = entry.getValue()>
            <#assign closingBalance = key.amount>
            <#list list as map>
            	<#assign closingBalance = closingBalance + map.transactionAmount>
            </#list>
            	<tr style="border-top:1pt solid black;">
            		<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : key.partyId}, true)>
                    <td colspan="5">
                    <div style="margin-left: -18px;">
                    	<table style="margin-top:20px;margin-bottom:10px;">
                    	<tr><td><block style="font-weight:bold;">${key.partyId} - ${partyGroup.groupName} </block></td></tr>
                    	<tr><td><block text-align="left">Currency : <block style="font-weight:bold;"> ${key.currency}</block></block></tr></td>
                    	<tr><td>
	                    <#assign openingBalance = key.amount>
	                    <#if key.amount lt 0>
	                    	<#assign openingBalance = (openingBalance).negate()>
	                    	<block text-align="left">Opening Balance : ${openingBalance?string("#,##0.00")} CR</block>
	                    	<#elseif key.amount gt 0>
	                    	<block text-align="left">Opening Balance : ${key.amount?string("#,##0.00")} DR</block>
	                    	<#else>
	                    	<block text-align="left">Opening Balance : ${key.amount?string("#,##0.00")}</block>
                    	</#if>
                    	</tr></td>
	                    <tr><td>
	                    <#if closingBalance lt 0>
	                    	<#assign closingBalancePositive = -closingBalance>
	                    	<block text-align="left">Closing Balance : ${closingBalancePositive?string("#,##0.00")} CR</block>
	                    	<#elseif closingBalance gt 0>
	                    	<block text-align="left">Closing Balance : ${closingBalance?string("#,##0.00")} DR</block>
	                    	<#else>
	                    	<block text-align="left">Closing Balance : ${closingBalance?string("#,##0.00")}</block>
                    	</#if>
                    	</td></tr>
                    	</table>
                    </div>
                    </td>
                </tr>
               <#assign closingBalance = key.amount>
               <#assign alt_row = true>
               <#list list as map>
                <tr<#if alt_row> class="alternate-row"</#if>>
                <#assign alt_row = !alt_row>
                    <td><block text-align="center">${map.transactionDate}</block></td>
                    <td>
                      <block text-align="center">
                      <#assign paymentGv = delegator.findOne("PaymentType", {"paymentTypeId" : map.transactionType}, true)?default({})>
                     <#assign invoiceTypeGv = delegator.findOne("InvoiceType", {"invoiceTypeId" : map.transactionType}, true)?default({})>
                     
                      <#if paymentGv?has_content || "CUSTOMER_REFUND" == map.transactionType>
                         <a target="_blank" href="/accounting/control/paymentOverview?paymentId=${map.transactionId}" >${map.transactionId}</a>
                         <#else>
                         <a target="_blank" href="/accounting/control/invoiceOverview?invoiceId=${map.transactionId}" >${map.transactionId}</a>
                       </#if>
                      </block>
                    </td>
                    <td><block text-align="center">
                     <#if paymentGv?has_content>
                     	${paymentGv.description}
                     	<#elseif invoiceTypeGv?has_content>
                     	${invoiceTypeGv.description}
                     </#if>
                    </block></td>
                    <td>
                    <#if "CUSTOMER_PAYMENT" == map.transactionType || "CUSTOMER_DEPOSIT" == map.transactionType || "CUSTOMER_REFUND" == map.transactionType>
                     <#assign paymentGv = delegator.findOne("Payment", {"paymentId" : map.transactionId}, false)>
                     <block text-align="center"> ${paymentGv.paymentRefNum?if_exists} </block>
                    </#if>
                    </td>
                    <td style="text-align:right;">
                    <#if map.transactionAmount lt 0>
                    	<#assign transAmount = (map.transactionAmount).negate()>
                    	<block style="text-align:right;">${transAmount?string("#,##0.00")} CR</block>
                    	<#elseif map.transactionAmount gt 0>
                    	<block style="text-align:right;">${map.transactionAmount?string("#,##0.00")} DR</block>
                    	<#else>
                    	<block style="text-align:right;">${map.transactionAmount?string("#,##0.00")}</block>
                    </#if>
                    </td>
                    <td style="text-align:right;">
                    <#assign closingBalance = closingBalance + map.transactionAmount>
                    <#if closingBalance lt 0>
                    	<#assign cloBal = -closingBalance>
                    	<block style="text-align=right;">${cloBal?string("#,##0.00")} CR</block>
                    	<#elseif closingBalance gt 0>
                    	<block style="text-align=right;">${closingBalance?string("#,##0.00")} DR</block>
                    	<#else>
                    	<block style="text-align:right;">${closingBalance?string("#,##0.00")}</block>
                    </#if>
                    </td>
                </tr>
                </#list>
            </#list>
    </table>
    </div>
    <#else>
    	No Record Found.
    </#if>
