<#setting number_format="0.00">
<#if parameters.print?has_content>
<script>
    window.print();
</script>
</#if>

<#assign customTimePeriodId = parameters.customTimePeriodId>
<#assign customTimePeriod = customTimePeriod>

<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#assign organizationId = parameters.organizationPartyId>
<script type="text/javascript">
    function newPopup(url) {
        popupWindow = window.open(
                url, 'popUpWindow', 'height=700,width=800,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=yes,location=no,directories=no,status=yes')
    }
</script>
<div>
    <table>
        <tr>
            <td colspan="2">
            <#if parameters.print?has_content>
                <h3>Cash Movement As At ${periodMap.thruDate?string("dd-MM-yyyy")}</h3>
            <#else>
                <h3>Cash Movement As At ${periodMap.thruDate?string("dd-MM-yyyy")}</h3>
            </#if>
            </td>
        </tr>
        <tr>
        <#assign openingBalanceOfBank = Static["org.ofbiz.accounting.util.CashFlowUtil"].getOpeningBalanceOfBankAccountForGivenPeriod(delegator,customTimePeriodId,organizationId)>
            <td style="padding-left:160px;font-weight:bold">Opening Balance of Bank</td>
            <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=openingBalanceOfBank?if_exists/></td>
        </tr>
        <tr>
            <td colspan="2" style="padding-left:10px;font-weight:bold">
                INFLOW:
            </td>
        </tr>
    <#assign cashReceivedFromCustomer = Static["org.ofbiz.accounting.util.CashFlowUtil"].getCustomerReceiptAmountForGivenPeriod(delegator,customTimePeriodId,organizationId)>
        <tr>
            <td style="padding-left:30px;font-weight:bold">Receipts From Customers</td>
            <td style="text-align:right;padding-right:10px;font-weight:bold;"
                width="40%;"> <@reportCurrency amount=cashReceivedFromCustomer?if_exists/></td>
        </tr>
    <#assign assetInflowList = Static["org.ofbiz.accounting.util.CashFlowUtil"].getInflowFromAsset(delegator,customTimePeriodId,organizationId)/>
    <#assign indirectProductionInflowList = Static["org.ofbiz.accounting.util.CashFlowUtil"].getInflowFromIndirectProduction(delegator,customTimePeriodId,organizationId)/>

    
        <tr>
            <td style="padding-left:30px;font-weight:bold">
                Other Inflows
            </td>
        </tr>
    <tr>
    <#if assetInflowList?has_content>
        <#list assetInflowList as assetInflow>
            <tr>
                <td style="padding-right:10px;font-style:italic;padding-left:75px">${assetInflow.accountName?if_exists}</td>
                <td style="text-align:right;padding-right:10px"
                    width="40%"><@reportCurrency amount=assetInflow.amount?if_exists/></td>
            </tr>
        </#list>
    </#if>
    <#if indirectProductionInflowList?has_content>
        <#list indirectProductionInflowList as indirectProductionInflow>
            <tr>
                <td style="padding-right:10px;font-style:italic;padding-left:75px">${indirectProductionInflow.accountName?if_exists}</td>
                <td style="text-align:right;padding-right:10px"
                    width="40%"><@reportCurrency amount=indirectProductionInflow.amount?if_exists/></td>
            </tr>
        </#list>
    </#if>
    
        <tr>
        <#assign totalInflow = Static["org.ofbiz.accounting.util.CashFlowUtil"].getInflowInAGivenPeriod(delegator,customTimePeriodId,organizationId)>
            <td style="padding-left:210px;font-weight:bold">
                TOTAL RECEIPTS
            </td>
            <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=totalInflow?if_exists/></td>
        </tr>
        <tr>
            <td></td>
            <td style="text-align:right;font-weight:bold;padding-right:10px">
                <image src="/accounting/images/doubleline.png"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" style="padding-left:10px;font-weight:bold">
                OUTFLOW:
            </td>
        </tr>
        <tr>
        <#assign cashPaidOutAmount=Static["org.ofbiz.accounting.util.CashFlowUtil"].getSupplierPaidAmountForGivenPeriod(delegator,customTimePeriodId,organizationId)/>
            <td style="padding-left:30px;font-weight:bold">Payments to Suppliers</td>
            <td style="text-align:right;padding-right:10px;font-weight:bold;"
                width="40%;"><@reportCurrency amount=cashPaidOutAmount?if_exists/></td>
        </tr>
    <#assign indirectProductionOutflowList = Static["org.ofbiz.accounting.util.CashFlowUtil"].getOutflowFromIndirectProduction(delegator,customTimePeriodId,organizationId)/>
    <tr>
        <td style="padding-left:30px;font-weight:bold">
            Other Outflow
        </td>
    <#if indirectProductionOutflowList?has_content>
        <#list indirectProductionOutflowList as indirectProductionOutflow>
            <tr>
                <td style="padding-right:10px;font-style:italic;padding-left:75px">${indirectProductionOutflow.accountName?if_exists}</td>
                <td style="text-align:right;padding-right:10px" width="40%">
                    <@reportCurrency amount=indirectProductionOutflow.amount?if_exists/></td>
            </tr>
        </#list>
    </#if>
    <#assign totalOutflow = Static["org.ofbiz.accounting.util.CashFlowUtil"].getOutflowInAGivenPeriod(delegator,customTimePeriodId,organizationId)/>
        <td style="padding-left:200px;font-weight:bold">
            TOTAL PAYMENTS
        </td>
        <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=totalOutflow?if_exists/></td>
        </tr>
        <tr>
            <td></td>
            <td style="text-align:right;font-weight:bold;padding-right:10px">
                <image src="/accounting/images/doubleline.png"/>
            </td>
        </tr>
       <#assign surplusAmount=totalInflow-totalOutflow/>
        <tr>
            <td style="padding-left:170px;font-weight:bold">Closing Balance of Bank</td>
            <td style="text-align:right;font-weight:bold;padding-right:10px"><@reportCurrency amount=openingBalanceOfBank+surplusAmount?if_exists/></td>
        </tr>
        <tr>
            <td></td>
            <td style="text-align:right;font-weight:bold;padding-right:10px">
                <image src="/accounting/images/doubleline.png"/>
            </td>
        </tr>
    </table>
</div>
