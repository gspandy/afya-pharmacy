<#setting number_format="0.00">
<#assign isDebitAccount = false>
<#assign sundryCreditorsTotalAmount = 0>
<#assign sundryDebtorsTotalAmount = 0>
<#assign customTimePeriod = parameters.customTimePeriodId>
<#assign customeTimePeriodGV =delegator.findOne("CustomTimePeriod",true,{"customTimePeriodId":customTimePeriod})>
<#assign previousCustomTimePeriod=Static["org.ofbiz.accounting.util.UtilAccounting"].getPreviousFiscalCustomTimePeriod(customeTimePeriodGV)>
<#assign previousTimePeriodId = previousCustomTimePeriod.customTimePeriodId>

<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#assign organizationId = parameters.organizationPartyId>
<#assign glAccountsIds = ["300000000","40000000"]>
<script type="text/javascript">

    function getAmount(value) {
        var val = parseFloat(value);
        if (isNaN(val))val = 0.0;
        return val;
    }

    function checkCreditDebits() {
        debitElements = $("input.debit");
        creditElements = $("input.credit");
        var totalDebits = 0;
        for (var j = 0; j < debitElements.length; j++) {
            totalDebits = totalDebits + getAmount(debitElements[j].value);
        }

        var totalCredits = 0;
        for (var j = 0; j < creditElements.length; j++) {
            totalCredits = totalCredits + getAmount(creditElements[j].value);
        }
        var diff = totalDebits.toFixed(2) - totalCredits.toFixed(2);
        if (diff != 0) {
            alert("Total Debit = " + totalDebits.toFixed(2) + "\n" + "Total Credit =  " + totalCredits.toFixed(2) + "\n" + "Difference = " +diff.toFixed(2)+ "\n" + "Total Debit amount does not match the Total Credit amount. Please fix this");
            return false;
        }
        return true;
    }
</script>

<form method="POST" action="updateOpeningBalance" onSubmit="javascript:return checkCreditDebits();"
      id="openingBalanceForm" name="openingBalanceForm">
    <table style="width:100% !important;border:1px solid grey;">
        <tr>
            <#list glAccountsIds as glAccountId>
                <#if glAccountId == "4000000">
                    <#assign isDebitAccount = true>
                </#if>
                <td style="border-right:1px solid grey">
                    <table style="width:100% !important;font-family:Calibri" cellspacing="0" cellpadding="0">
                        <caption>
                            <h2></h2>
                        </caption>
                        <tr>
                            <th></th>
                        </tr>
                        <#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
                        <#assign childGlAccounts=delegator.findByAnd("GlAccount",{"parentGlAccountId" : glAccount.glAccountId?if_exists},["sortOrder"])>
                        <#assign hasChild=true>
                        <tr>
                            <td width="50%" style="padding-left:10px;font-weight:bold;color:#fff;">${glAccount.accountName}</td>
                            <td>Dr</td>
                            <td>Cr</td>
                            <#if childGlAccounts?has_content>
                                <#list childGlAccounts as childGlAccount>
                                     <@glAccountBalances childGlAccount 2 isDebitAccount/>
                                </#list>
                            </#if>
                        </tr>
                        <tr>
                            <td></td>
                            <td></td>
                        </tr>
                    </table>
                </td>
            </#list>
        </tr>
        <tr>
            <td></td>
            <td style="border:1px solid grey">
                <table style="width:100% !important;font-family:Calibri" cellspacing="0" cellpadding="0">
                    <#assign plAccount = delegator.findOne("GlAccount",false,{"glAccountId":"9620000"})/>
                    <tr id="tr_${plAccount.glAccountId}">
                        <td style="text-align:left;padding-left:30px;font-style:italic;font-size:13px;border-right:1px solid black"
                            width="50%" id="td_${plAccount.glAccountId}">${plAccount.accountName}
                        </td>
                        <td style="border-right:1px solid black">
                        <input type="hidden" name="glAccountId" value="${plAccount.glAccountId}"/>
                        <#assign glHistoryGV = delegator.findOne("GlAccountHistory",true,{"organizationPartyId":parameters.organizationPartyId,"customTimePeriodId":previousTimePeriodId,"glAccountId":plAccount.glAccountId})?default({})>
                        <#assign style="cursor:pointer;width:100px !important;text-align:center">
                            <input type="text" name="${plAccount.glAccountId}_db" size="15" class="debit validate-currency-dollar"
                                   value="${glHistoryGV.postedDebits?if_exists}"/>
                        </td>
                        <td style="border-right:1px solid black">
                            <input type="text" name="${plAccount.glAccountId}_cr" size="15" class="credit validate-currency-dollar"
                                   value="${glHistoryGV.postedCredits?if_exists}"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td height="30"></td>
            <td height="30">
                <input type="hidden" name="customTimePeriodId" value="${parameters.customTimePeriodId}"/>
                <input type="hidden" name="organizationPartyId" value="${parameters.organizationPartyId}"/>
                <button type="submit">Save</button>
            </td>
            <td height="30"></td>
            <td height="30"></td>
        </tr>
    </table>
</form>

<#macro glAccountBalances glAccount level isDebitAccount>
    <#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
    <#assign childPresent = childGlAccounts.size()>
    <tr id="tr_${glAccount.glAccountId}">
        <td style="text-align:left;padding-left:${15*level}px;font-style:italic;font-size:13px;border:1px solid black"
            width="404" id="td_${glAccount.glAccountId}">${glAccount.accountName}
        </td>
        <td style="border:1px solid black">
            <input type="hidden" name="glAccountId" value="${glAccount.glAccountId}"/>
            <#assign glHistoryGV = delegator.findOne("GlAccountHistory",true,{"organizationPartyId":parameters.organizationPartyId,"customTimePeriodId":previousTimePeriodId,"glAccountId":glAccount.glAccountId})?default({})>
            <#assign style="cursor:pointer;width:100px !important;text-align:center">
            <input type="text" name="${glAccount.glAccountId}_db" size="15" class="debit validate-currency-dollar"
               value="${glHistoryGV.postedDebits?if_exists}"/>
        </td>
        <td style="border:1px solid black">
            <input type="text" name="${glAccount.glAccountId}_cr" size="15" class="credit validate-currency-dollar"
               value="${glHistoryGV.postedCredits?if_exists}"/>
        </td>
    </tr>
    <#list childGlAccounts as childGl>
        <@glAccountBalances childGl level+1 isDebitAccount/>
    </#list>
</#macro>
