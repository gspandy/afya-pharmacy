<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Customer Payment Detail</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(70)"/>
        <fo:table-column column-width="proportional-column-width(40)"/>
        <fo:table-body>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                         From Date :
                         <#if parameters.fromDate?has_content>
					          ${parameters.fromDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
					      </#if>
					      </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                         Thru Date : 
                         <#if thruDate?has_content>
				          ${thruDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
				        </#if>
                         
                         </fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
<fo:block><fo:leader/></fo:block>
    <#if groupedOpeningBalanceMap?has_content>
    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Date</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Payment/Invoice</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Transaction Type</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Reference</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Amount</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Closing Balance</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
          <#list groupedOpeningBalanceMap.entrySet() as entry>
          	<#assign key = entry.getKey()>
            <#assign list = entry.getValue()>
            <#assign closingBalance = key.amount>
            <#list list as map>
            	<#assign closingBalance = closingBalance + map.transactionAmount>
            </#list>
            	<fo:table-row>
            		<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : key.partyId}, true)>
                    <fo:table-cell number-columns-spanned="5">
	                    <fo:block text-align="left">${key.partyId} - ${partyGroup.groupName} </fo:block>
	                    <#assign openingBalance = key.amount>
	                    <#if key.amount lt 0>
	                    	<#assign openingBalance = (openingBalance).negate()>
	                    	<fo:block text-align="left">Opening Balance : ${openingBalance?string("#,##0.00")} CR</fo:block>
	                    	<#elseif key.amount gt 0>
	                    	<fo:block text-align="left">Opening Balance : ${key.amount?string("#,##0.00")} DR</fo:block>
	                    	<#else>
	                    	<fo:block text-align="left">Opening Balance : ${key.amount?string("#,##0.00")}</fo:block>
                    	</#if>
	                    <fo:block text-align="left">Currency : ${key.currency}</fo:block>
	                    <#if closingBalance lt 0>
	                    	<#assign closingBalancePositive = -closingBalance>
	                    	<fo:block text-align="left">Closing Balance : ${closingBalancePositive?string("#,##0.00")} CR</fo:block>
	                    	<#elseif closingBalance gt 0>
	                    	<fo:block text-align="left">Closing Balance : ${closingBalance?string("#,##0.00")} DR</fo:block>
	                    	<#else>
	                    	<fo:block text-align="left">Closing Balance : ${closingBalance?string("#,##0.00")}</fo:block>
                    	</#if>
                    </fo:table-cell>
                </fo:table-row>
               <#assign closingBalance = key.amount>
               <#list list as map>
                <fo:table-row>
                    <fo:table-cell border="solid black"><fo:block text-align="center">${map.transactionDate}</fo:block></fo:table-cell>
                    <fo:table-cell border="solid black">
                      <fo:block text-align="center">
                      ${map.transactionId}
                      <#-- <#if "CUSTOMER_PAYMENT" == map.transactionType || "CUSTOMER_DEPOSIT" == map.transactionType>
                         <fo:basic-link background-color="lightblue" text-decoration="underline" 
                            external-destination="${serverUrl}accounting/control/paymentOverview?paymentId=${map.transactionId}">
                             ${map.transactionId}
                         </fo:basic-link>
                         <#else>
                         <fo:basic-link background-color="lightblue" text-decoration="underline" 
                            external-destination="${serverUrl}accounting/control/invoiceOverview?invoiceId=${map.transactionId}">
                             ${map.transactionId}
                         </fo:basic-link>
                       </#if> -->
                      </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid black"><fo:block text-align="center">
                     <#assign paymentGv = delegator.findOne("PaymentType", {"paymentTypeId" : map.transactionType}, true)?default({})>
                     <#assign invoiceTypeGv = delegator.findOne("InvoiceType", {"invoiceTypeId" : map.transactionType}, true)?default({})>
                     <#if paymentGv?has_content>
                     	${paymentGv.description}
                     	<#elseif invoiceTypeGv?has_content>
                     	${invoiceTypeGv.description}
                     </#if>
                    </fo:block></fo:table-cell>
                    <fo:table-cell border="solid black">
                    <#if "CUSTOMER_PAYMENT" == map.transactionType || "CUSTOMER_DEPOSIT" == map.transactionType || "CUSTOMER_REFUND" == map.transactionType>
                     <#assign paymentGv = delegator.findOne("Payment", {"paymentId" : map.transactionId}, false)>
                     <fo:block text-align="center"> ${paymentGv.paymentRefNum?if_exists} </fo:block>
                    </#if>
                    </fo:table-cell>
                    <fo:table-cell border="solid black">
                    <#if map.transactionAmount lt 0>
                    	<#assign transAmount = (map.transactionAmount).negate()>
                    	<fo:block text-align="right">${transAmount?string("#,##0.00")} CR</fo:block>
                    	<#elseif map.transactionAmount gt 0>
                    	<fo:block text-align="right">${map.transactionAmount?string("#,##0.00")} DR</fo:block>
                    	<#else>
                    	<fo:block text-align="right">${map.transactionAmount?string("#,##0.00")}</fo:block>
                    </#if>
                    </fo:table-cell>
                    <fo:table-cell border="solid black">
                    <#assign closingBalance = closingBalance + map.transactionAmount>
                    <#if closingBalance lt 0>
                    	<#assign cloBal = -closingBalance>
                    	<fo:block text-align="right">${cloBal?string("#,##0.00")} CR</fo:block>
                    	<#elseif closingBalance gt 0>
                    	<fo:block text-align="right">${closingBalance?string("#,##0.00")} DR</fo:block>
                    	<#else>
                    	<fo:block text-align="right">${closingBalance?string("#,##0.00")}</fo:block>
                    </#if>
                    </fo:table-cell>
                </fo:table-row>
                </#list>
            </#list>
        </fo:table-body>
    </fo:table>
    <#else>
    <fo:block text-align="center">No Record Found.</fo:block>
    </#if>
</#escape>