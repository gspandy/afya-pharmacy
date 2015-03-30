<style>
    .rolledUp {
        color: #222;
    }

    .single {
        color: #999;
    }
</style>
<#if parameters.print?has_content>
<script>
    window.print();
</script>
</#if>
<#setting number_format="0.00">
<#assign customTimePeriod = parameters.customTimePeriodId>
<#assign periodMap = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriod)>
<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#assign orgParty = delegator.findOne("PartyGroup",true,"partyId",parameters.organizationPartyId)>
<#assign partyIds = Static["org.ofbiz.party.party.PartyWorker"].getAssociatedPartyIdsByRelationshipType(delegator, parameters.organizationPartyId, "GROUP_ROLLUP")>

<#assign organizationId = parameters.organizationPartyId>
<div style="margin:5px;background:#FFF;width:95%">
<#assign assetAccounts = ["9990300","40000000_9","40000000_1"]>
<#assign liabilityAccounts =["9600100","300000000_3","300000000_9","9420000","300000000_8","9100000"]>
<div>
<div align="left">
<#if parameters.print?has_content>
    <h4>Balance Sheet As At ${periodMap.thruDate?string("dd-MM-yyyy")}</h4>
<#else>
    <h4>Balance Sheet As At ${periodMap.thruDate?string("dd-MM-yyyy")}</h4>
</#if>

</div>
<table style="padding:5px;" cellspacing="0" cellpadding="0" width="100%">
<tr>

    <td style="vertical-align:top" colspan="2">
        <table style="float:left;width:100% !important" id="detailBalanceSheet">
            <caption>
            <#if parameters.print?has_content>
                <h3>ASSETS</h3>
            <#else>
                <h3>ASSETS</h3>
            </#if>
            </caption>
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
                    <td style="text-align:right;padding-right:10px" class="rolledUp"
                        width="45%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
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
                    <td style="text-align:right;padding-right: 10px;">____________</td>
                </tr>
                <tr>
                    <td></td>
                    <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
                    customTimePeriod,
                    organizationId, true)>
                    <td style="text-align:right;padding-right:10px"
                        class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
                </tr>
            </#if>
        </#list>
        </table>
    </td>

    <td valign="top;padding-left:75px;" colspan="2">
        <table style="float:left;width:100% !important" id="detailBalanceSheet">

            <tr>
                <td colspan="2" style="padding-left:0px;font-weight:bold;">
                    <h3>Equity and Liabilities</h3>
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
                    <td style="text-align:right;padding-right:10px" class="rolledUp"
                        width="40%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
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
                    <td style="text-align:right;padding-right: 10px;">____________</td>
                </tr>
                <tr>
                    <td></td>
                    <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId,
                    customTimePeriod,organizationId, false)>
                    <td style="text-align:right;padding-right:10px" class="rolledUp"
                        width="45%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
                </tr>
            </#if>
        </#list>
            <tr>
            <#assign retainedEarningGv =delegator.findByPrimaryKey("GlAccount",{"glAccountId":"9620000"})>
            <#assign profitAndLossAmt = Static["org.ofbiz.accounting.util.UtilAccounting"].getProfitAndLossAmount(delegator,customTimePeriod,organizationId)>
                <td  style="font-weight:bold;">
                    <#if retainedEarningGv?has_content>
                        ${retainedEarningGv.accountName?if_exists}
                    </#if>
                </td>
                <td style="text-align:right;padding-right:10px" class="rolledUp"
                    width="45%"><@reportCurrency amount=profitAndLossAmt?if_exists/></td>
            </tr>
        </table>
    </td>

</tr>
<tr>
    <td></td>
    <td style="text-align:right;font-weight:bold;padding-right:110px">____________</td>
    <td></td>
    <td style="text-align:right;font-weight:bold;padding-right:45px">____________</td>
</tr>
<tr>
<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"40000000",
customTimePeriod, organizationId, true)>
    <td style="font-weight:bold;padding-left:20px"> Total Assets</td>
    <td style="text-align:right;padding-right:110px"
        class="rolledUp"><@reportCurrency amount=glAccountAmount?if_exists/></td>
<#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"300000000",
customTimePeriod, organizationId, false)>
    <td style="font-weight:bold;padding-left:145px"> Total Liabilities</td>
    <td style="text-align:right;padding-right:45px"
        class="rolledUp"><@reportCurrency amount=(glAccountAmount+profitAndLossAmt)?if_exists/></td>

</tr>
<tr>
    <td></td>
    <td style="text-align:right;font-weight:bold;padding-right:110px">
        <image src="/accounting/images/doubleline.png"/>
    </td>
    <td></td>
    <td style="text-align:right;font-weight:bold;padding-right:45px">
        <image src="/accounting/images/doubleline.png"/>
    </td>
</tr>
<tr>
    <td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <br/>
        <br/>
    </td>
</tr>
</table>
</div>
</div>
                                                                             `
<#macro glAccountBalances glAccount level>
    <#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists},["accountName"])>
    <#assign childPresent = childGlAccounts.size()>
<tr>
    <td style="text-align:left;padding-left:${15*level}px;font-style:italic" width="35%">${glAccount.accountName}</td>
    <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,customTimePeriod, delegator,organizationId)>
    <td style="text-align:right;padding-right:10px" class="single"
        width="20%"><@reportCurrency amount=glAccountAmount?if_exists/></td>
    <td width="25%"></td>
</tr>
    <#list childGlAccounts as childGl>
        <@glAccountBalances childGl level+1/>
    </#list>
</#macro>



<script>

    function displayChildren(event, glAccountId, level) {
        var innerHtml = '';
        new Ajax.Request('sub?glAccountId=' + glAccountId + '&level=' + level, {
            asynchronous: false,
            onSuccess: function (transport) {
                innerHtml = transport.responseText;
            }
        });
        $(glAccountId).insert({after: innerHtml});
        Event.stop(event);
    }

</script>