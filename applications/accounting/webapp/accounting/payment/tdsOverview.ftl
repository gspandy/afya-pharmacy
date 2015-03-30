<#assign payment = delegator.findOne("Payment",{"paymentId",parameters.paymentId},true)>
<#assign paymentAmt = 0>
<#assign currencyUomId ="">
<table class="basic-table" style="width:30%">
    <tr><td> Invoice Amount </td>
        <td class="align-text">
            <#if payment.actualCurrencyAmount?exists>
                <@ofbizCurrency amount=payment.actualCurrencyAmount isoCode="${payment.actualCurrencyUomId}"/>
                <#assign paymentAmt=payment.actualCurrencyAmount>
                <#assign currencyUomId=payment.actualCurrencyUomId>
            </#if>
            <#if payment.amount?exists>
                <@ofbizCurrency amount=payment.amount isoCode="${payment.currencyUomId}"/>
                <#assign paymentAmt=payment.amount>
                <#assign currencyUomId=payment.currencyUomId>
            </#if> 
        </td>
    </tr>
    <#if mainList?has_content>
        <tr><td > TAX APPLIED : <br/> <font style="italic" weight="normal"></font></td></tr>
        <tr>Tds Types</tr>
        <#list mainList as paymentTdslst>
            <tr>
                <td>${paymentTdslst.tdsTypeId}</td>
                <td class="align-text"> <@ofbizCurrency amount=paymentTdslst.amount isoCode="${currencyUomId}"/></td>
            </tr>
        </#list>
        <tr>
            <td></td>
            <td style="text-align:right;font-weight:bold;padding-right:10px">_____________</td>
        </tr>
    </#if>
    <tr><td > Total Tax Amount<br/> <font style="italic" weight="normal">(Deducted by Customer)</font></td>
    <td class="align-text"><@ofbizCurrency amount=payment.tdsAmount isoCode="${currencyUomId}"/></td></tr>
    <tr>
        <td></td>
        <td style="text-align:right;font-weight:bold;padding-right:10px">_____________</td>
    </tr>
    <tr><td > Total Invoice Amount</td><td class="align-text"><@ofbizCurrency amount=paymentAmt+payment.tdsAmount isoCode="${currencyUomId}"/></td></tr>
    <tr>
        <td></td>
        <td style="text-align:right;font-weight:bold;padding-right:10px"><image src="/accounting/images/doubleline.png" /></td>
    </tr>
</table>

