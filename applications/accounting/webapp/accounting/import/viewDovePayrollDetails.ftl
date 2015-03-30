<div class="screenlet-body">
    <table cellspacing="0" cellpadding="0" class="basic-table hover-bar">
        <tr class="header-row">
            <td width="5%">Salary Item Id</td>
            <td width="5%">Salary Id</td>
            <td width="10%">Payroll Code</td>
            <td width="10px">Payroll Code Description</td>
            <td width="10%">Gl Account Id</td>
            <td width="10%">Amount</td>
            <td width="10%">Debit Credit Flag</td>
        </tr>
    <#if salaryItemList?has_content>
        <#assign alt_row = false>
        <#list salaryItemList as salaryItem>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                <td>${salaryItem.salaryItemId?if_exists}</td>
                <td>${salaryItem.salaryId?if_exists}</td>
                <td>${salaryItem.payrollCode?if_exists}</td>
                <td>${salaryItem.payrollName?if_exists}</td>
                <td>${salaryItem.glAccountId?if_exists}</td>
                <td>${salaryItem.amount?if_exists}</td>
                <td>${salaryItem.transactionType?if_exists}</td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </table>
</div>
<div class="screenlet-body">
    <table cellspacing="0" cellpadding="2" class="basic-table hover-bar">
        <h1>Committed Transactions</h1>
        <tr class="header-row">
            <td  width="5%">Acctg Trans Id</td>
            <td width="5%">Acctg Trans Type</td>
            <td width="10%">Transaction Date</td>
            <td width="10%">Is Posted</td>
            <td width="10%">Is Reverted</td>
        </tr>
    <#if AcctgTransAndEntries?has_content>
        <#assign alt_row = false>
        <#list AcctgTransAndEntries as acctgTrans>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                <td><a href="<@ofbizUrl>EditAcctgTrans?acctgTransId=${acctgTrans.acctgTransId?if_exists}&amp;organizationPartyId=Company"</@ofbizUrl>"> ${acctgTrans.acctgTransId?if_exists}</a></td>
                <td><#if acctgTrans.acctgTransTypeId?if_exists="SALARY">Salary
                </#if></td>
                <td>${acctgTrans.transactionDate?string("dd-MM-yyyy")?if_exists}
                </td>
                <td>
                    <#if acctgTrans.isPosted?has_content>
                        <#if acctgTrans.isPosted?if_exists ="Y">
                            Yes
                        <#else>
                            No
                        </#if>
                    </#if>
                </td>
                <td>
                    <#if acctgTrans.isReverted?has_content>
                        Yes
                    <#else>
                        No
                    </#if>

                </td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </table>
</div>