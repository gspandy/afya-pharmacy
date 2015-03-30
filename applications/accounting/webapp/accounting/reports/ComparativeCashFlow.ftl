<#setting number_format="0.00">
<#if parameters.print?has_content>
<script>
window.print();
</script>
</#if>

<#assign customTimePeriodId1 = parameters.customTimePeriodId1>
<#assign customTimePeriodId2 = parameters.customTimePeriodId2>
<#assign periodMap1 = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriodId1)>
<#assign periodMap2 = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriodId2)>
<#assign partyIds = Static["org.ofbiz.party.party.PartyWorker"].getAssociatedPartyIdsByRelationshipType(delegator, parameters.organizationPartyId, "GROUP_ROLLUP")>

<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#assign organizationId = parameters.organizationPartyId>
<script type="text/javascript">
function newPopup(url) {
	popupWindow = window.open(
			url,'popUpWindow','height=700,width=800,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=yes,location=no,directories=no,status=yes')
}
</script>

<style>
	.cashflow td{
		font-size:14px;
		border-bottom:0px solid grey;
 		height: 25px;
    	vertical-align: bottom;
    	text-align:right;
    	}
</style>
		<table style="width:65%" cellspacing="0px" cellpadding="0px" class="cashflow">
			<tr>
				<td style="padding-left:20px;text-align:left !important;font-weight:bold">CashFlow <br/> 
						[<#list partyIds as party><#assign partyGroup = delegator.findOne("PartyGroup",true,"partyId",party)>${partyGroup.groupName}, </#list>] 
				</td>
			</tr>
			<tr>
				<td/>
				<td style="text-align:right;font-weight:bold;text-decoration:underline">${periodMap1.thruDate?string("dd-MM-yyyy")}</td>
				<td width="20%"></td>
				<td style="text-align:right;font-weight:bold;text-decoration:underline">${periodMap2.thruDate?string("dd-MM-yyyy")}</td>
			</tr>
			<tr>
				<td colspan="4" style="padding-left:20px;text-align:left !important;font-weight:bold">
					INFLOW:
				</td>
			</tr>
			<#assign cashGenerataedTotal = Static["org.ofbiz.accounting.util.UtilAccounting"].getProfitAndLossAmount(delegator,customTimePeriodId11,organizationId)>
			<tr>
				<td  width="30%">Cash generated from Operations</td>
				<td style="text-align:right;padding-right:10px" width="25%">
				<@reportCurrency amount=cashGenerataedTotal?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign cashGenerataedTotal2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getProfitAndLossAmount(delegator,customTimePeriodId12,organizationId)>
				<td style="text-align:right;padding-right:10px" width="25%">
				<@reportCurrency amount=cashGenerataedTotal2?if_exists/></td>
			</tr>
			<tr>
			<#assign capitalTotal=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3121000",customTimePeriodId1,organizationId,false)/>
				<td   width="30%">Increase/(Decrease) in Capital</td>
				<td style="text-align:right;padding-right:10px" width="25%"><@reportCurrency amount=capitalTotal?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign capitalTotal2=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3121000",customTimePeriodId2,organizationId,false)/>
				<td style="text-align:right;padding-right:10px" width="25%">
				<@reportCurrency amount=capitalTotal2?if_exists/></td>
			</tr>
			<tr>
			<tr>
			<#assign securedLoans=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3300000",customTimePeriodId1,organizationId,false)/>
				<td  width="30%">Increase/(Decrease) in Loans</td>
				<td style="text-align:right;padding-right:10px" width="25%"><@reportCurrency amount=securedLoans?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign securedLoans2=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3300000",customTimePeriodId2,organizationId,false)/>
				<td style="text-align:right;padding-right:10px" width="25%">
				<@reportCurrency amount=securedLoans2?if_exists/></td>
			</tr>
			<tr>
			<tr>
			<#assign liabilityTotal= Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3000000",customTimePeriodId1,
				organizationId, false)>
				<td >Increase/(Decrease) in Current Liabilities</td>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=liabilityTotal?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign liabilityTotal2= Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3000000",customTimePeriodId2,
				organizationId, false)>
				<td style="text-align:right;padding-right:10px" width="20%">
				<@reportCurrency amount=liabilityTotal2?if_exists/></td>
			</tr>
			
			<tr>
			<#assign suspenceAmount= Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3600000",customTimePeriodId1,
				organizationId, false)>
				<td >Increase/(Decrease) in Suspence A/c</td>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=suspenceAmount?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign suspenceAmount2= Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"3600000",customTimePeriodId2,
				organizationId, false)>
				<td style="text-align:right;padding-right:10px" width="20%">
				<@reportCurrency amount=suspenceAmount2?if_exists/></td>
			</tr>
			<tr>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">_____________</td>
				<td width="20%">&nbsp;</td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">_____________</td>
			</tr>
			<tr>
				<#assign totalInflow=liabilityTotal+securedLoans+capitalTotal+cashGenerataedTotal+suspenceAmount/>
				<td style="padding-left:20px;font-weight:bold;" width="40%">
					Total
				</td>
				<td style="text-align:right;font-weight:bold;padding-right:10px"  width="20%"><@reportCurrency amount=totalInflow?if_exists/></td>
				<td width="20%">&nbsp;</td>
				<td style="text-align:right;font-weight:bold;padding-right:10px" width="20%">
				<#assign totalInflow2=liabilityTotal2+securedLoans2+capitalTotal2+cashGenerataedTotal2+suspenceAmount2/>
				<@reportCurrency amount=totalInflow2?if_exists/></td>
			</tr>
				<tr>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">
					<image src="/accounting/images/doubleline.png" />
				</td>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">
					<image src="/accounting/images/doubleline.png" />
				</td>
			</tr>
			
			
			<tr>
				<td colspan="2" style="padding-left:20px;font-weight:bold;text-align:left !important;">
						OUTFLOW:
				</td>
			</tr>
			<tr>
			<#assign fixAssetAmount=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4200000",customTimePeriodId1,organizationId,true)/>
				<td   width="20%">Increase/(Decrease) in Fixed Assets</td>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=fixAssetAmount?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign fixAssetAmount2=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4200000",customTimePeriodId2,organizationId,true)/>				
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=fixAssetAmount2?if_exists/></td>
			</tr>
			<tr>
			<#assign investAmount=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4300000",customTimePeriodId1,organizationId,true)/>
				<td   width="20%">Increase/(Decrease) in Investments</td>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=investAmount?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign investAmount2=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4300000",customTimePeriodId2,organizationId,true)/>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=investAmount2?if_exists/></td>
			</tr>
			<tr>
				<#assign inventoryAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4150000",customTimePeriodId1, organizationId,true)>
				<td   width="20%">Increase/(Decrease) in Inventory</td>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=inventoryAmount?if_exists/></td>
				<td width="20%">&nbsp;</td>
				<#assign inventoryAmount2 = Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4150000",customTimePeriodId2, organizationId,true)>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=inventoryAmount2?if_exists/></td>
			</tr>
			<tr>
			<#assign debtorsAmount=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4160000",customTimePeriodId1,organizationId,true)/>
				<td   width="20%">Increase/(Decrease) in Debtors</td>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=debtorsAmount?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign debtorsAmount2=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4160000",customTimePeriodId2,organizationId,true)/>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=debtorsAmount2?if_exists/></td>
			</tr>
			<tr>
			<#assign advanceAndDeposits=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4140000",customTimePeriodId1,organizationId,true)/>
			<#assign advanceAndDeposits = advanceAndDeposits>
				<td   width="20%">Increase/(Decrease) in Loans,Advances and Deposits</td>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=advanceAndDeposits?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign advanceAndDeposits2=Static["org.ofbiz.accounting.util.UtilAccounting"].getCashFlowAmount(delegator,"4140000",customTimePeriodId2,organizationId,true)/>
				<td style="text-align:right;padding-right:10px" width="20%"><@reportCurrency amount=advanceAndDeposits2?if_exists/></td>
			</tr>
			<tr>
			<tr>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">_____________</td>
				<td width="20%">&nbsp;</td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">_____________</td>
			</tr>
			<tr>
				<#assign totalOutflow = fixAssetAmount+investAmount+inventoryAmount+debtorsAmount+advanceAndDeposits/>
				<td style="padding-left:20px;font-weight:bold">
					Total
				</td>
				<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=totalOutflow?if_exists/></td>
				<td width="20%">&nbsp;</td>
				<#assign totalOutflow2 = fixAssetAmount2+investAmount2+inventoryAmount2+debtorsAmount2+advanceAndDeposits2/>
				<td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=totalOutflow2?if_exists/></td>
			</tr>
				<tr>
				<td></td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">
					<image src="/accounting/images/doubleline.png" />
				</td>
				<td width="20%">&nbsp;</td>
				<td style="text-align:right;font-weight:bold;padding-right:10px">
					<image src="/accounting/images/doubleline.png" />
				</td>
			</tr>
			<tr>
			<#assign cashAmount=Static["org.ofbiz.accounting.util.UtilAccounting"].getOpeningBalance(delegator,"4120000",customTimePeriodId1,true,organizationId)/>
			<#assign bankAmount=Static["org.ofbiz.accounting.util.UtilAccounting"].getOpeningBalance(delegator,"4110000",customTimePeriodId1,true,organizationId)/>
			<#assign cashBankAmount = bankAmount+cashAmount>
				<td style="padding-left:20px;font-weight:bold">Opening Balance of Cash &amp; Bank</td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=cashBankAmount?if_exists/></td>
			<#assign cashAmount2=Static["org.ofbiz.accounting.util.UtilAccounting"].getOpeningBalance(delegator,"4120000",customTimePeriodId2,true,organizationId)/>
			<#assign bankAmount2=Static["org.ofbiz.accounting.util.UtilAccounting"].getOpeningBalance(delegator,"4110000",customTimePeriodId2,true,organizationId)/>
			<#assign cashBankAmount2 = bankAmount2+cashAmount2>
				<td width="20%">&nbsp;</td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=cashBankAmount2?if_exists/></td>
			</tr>
			<tr>
			<#assign surplusAmount=totalInflow-totalOutflow/>
				<td style="padding-left:20px;font-weight:bold">Add/(Deduct) Surplus</td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=surplusAmount?if_exists/></td>
				<td width="20%">&nbsp;</td>
			<#assign surplusAmount2=totalInflow2-totalOutflow2/>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=surplusAmount2?if_exists/></td>
			</tr>
			<tr>
				<td style="padding-left:20px;font-weight:bold">Closing Balance of Cash &amp; Bank</td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=cashBankAmount+surplusAmount?if_exists/></td>
				<td width="20%">&nbsp;</td>
				<td style="text-align:right;padding-right:10px"><@reportCurrency amount=cashBankAmount+surplusAmount2?if_exists/></td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<br />
					<br />
				</td>
			</tr>
		</table>
