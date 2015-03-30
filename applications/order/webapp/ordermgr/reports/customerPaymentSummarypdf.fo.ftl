<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Customer Payment Summary</fo:block>
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
    <#if finalList?has_content>
    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Opening Balance</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Currency</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Payments</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Invoices</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">Closing Balance</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
        <#assign partyId = "">
            <#list finalList as value>
            <#if partyId == value.partyId>
            	<#else>
            	<#assign partyId = value.partyId>
            	<fo:table-row>
            		<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : partyId}, true)>
                    <fo:table-cell number-columns-spanned="5"><fo:block text-align="left">${partyId} - ${partyGroup.groupName?if_exists}</fo:block></fo:table-cell>
                </fo:table-row>
            </#if>
                <fo:table-row>
                    <fo:table-cell border="solid black">
                    <#if value.amount lt 0>
                    	<#assign amount = (value.amount).negate()>
                    	<fo:block text-align="right">${amount?string("#,##0.00")} CR</fo:block>
                    	<#elseif value.amount gt 0>
                    	<fo:block text-align="right">${value.amount?string("#,##0.00")} DR</fo:block>
                    	<#else>
                    	<fo:block text-align="right">${value.amount?string("#,##0.00")}</fo:block>
                    </#if>
                    </fo:table-cell>
                    <fo:table-cell border="solid black"><fo:block text-align="center">${value.currency?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell border="solid black">
                    <#if value.credit lt 0>
                    	<#assign creditAmount = (value.credit).negate()>
                    	<fo:block text-align="right">${creditAmount?string("#,##0.00")} CR</fo:block>
                    	<#elseif value.credit gt 0>
                    	<fo:block text-align="right">${value.credit?string("#,##0.00")} DR</fo:block>
                    	<#else>
                    	<fo:block text-align="right">${value.credit?string("#,##0.00")}</fo:block>
                    </#if>
                    </fo:table-cell>
                    <fo:table-cell border="solid black">
                    <#if value.debit lt 0>
                    	<#assign debit = (value.debit).negate()>
                    	<fo:block text-align="right">${debit?string("#,##0.00")} CR</fo:block>
                    	<#elseif value.debit gt 0>
                    	<fo:block text-align="right">${value.debit?string("#,##0.00")} DR</fo:block>
                    	<#else>
                    	<fo:block text-align="right">${value.debit?string("#,##0.00")}</fo:block>
                    </#if>
                    </fo:table-cell>
                    <#assign amount = value.amount?if_exists>
                    <#assign credit = value.credit?if_exists>
                    <#assign debit = value.debit?if_exists>
                    <#assign closingBalance = amount + credit + debit>
                    <fo:table-cell border="solid black">
                     <#if closingBalance lt 0>
                    	<#assign closingBalancePositive = -closingBalance>
                    	<fo:block text-align="right">${closingBalancePositive?string("#,##0.00")} CR</fo:block>
                    	<#elseif closingBalance gt 0>
                    	<fo:block text-align="right">${closingBalance?string("#,##0.00")} DR</fo:block>
                    	<#else>
                    	<fo:block text-align="right">${closingBalance?string("#,##0.00")}</fo:block>
                    </#if>
                    </fo:table-cell>
                </fo:table-row>
            </#list>
        </fo:table-body>
    </fo:table>
    <#else>
    <fo:block text-align="center">No Record Found.</fo:block>
    </#if>
</#escape>