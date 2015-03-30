<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<script language="JavaScript" type="text/javascript">
function shipBillAddr() {
    if (document.checkoutsetupform.useShipAddr.checked) {
        window.location = "<@ofbizUrl>setBilling?createNew=Y&amp;finalizeMode=payment&amp;paymentMethodType=${paymentMethodType?if_exists}&amp;useShipAddr=Y</@ofbizUrl>";
    } else {
        window.location = "<@ofbizUrl>setBilling?createNew=Y&amp;finalizeMode=payment&amp;paymentMethodType=${paymentMethodType?if_exists}</@ofbizUrl>";
    }
}

function makeExpDate() {
    document.checkoutsetupform.expireDate.value = document.checkoutsetupform.expMonth.options[document.checkoutsetupform.expMonth.selectedIndex].value + "/" + document.checkoutsetupform.expYear.options[document.checkoutsetupform.expYear.selectedIndex].value;
}
</script>
<div class="screenlet-title-bar">
                <div class="h3">${uiLabelMap.OrderHowShallYouPay}?</div>
            </div>
<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session) || security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session)>
<div class="screenlet">
    <div class="screenlet-body">
         
         <form method="post" action="<@ofbizUrl>finalizeOrder</@ofbizUrl>" name="checkoutsetupform" id="checkoutsetupform">
            <input type="hidden" name="finalizeMode" value="payment"/>
            <input type="hidden" name="createNew" value="Y"/>
            <table width="100%" border="0" cellpadding="1" cellspacing="0">

              <#if productStorePaymentMethodTypeIdMap.EXT_COD?exists>
                  <tr>
                    <td width="1%" valign="nowrap">
                      <input type="radio" name="checkOutPaymentId" style="margin-bottom: 7px;" value="EXT_COD" <#if "EXT_COD" == checkOutPaymentId>checked="checked"</#if>/>
                    </td>
                     <td width='50%' nowrap="nowrap"><div> COD/Cash </div></td>
                  </tr>
                <tr><td colspan="2"><hr /></td></tr>
              </#if>
             
             
              <#if productStorePaymentMethodTypeIdMap.CASH?exists>
                  <tr>
                    <td width="1%" valign="nowrap">
                      <input type="radio" name="checkOutPaymentId" style="margin-bottom: 7px;" value="CASH" <#if "CASH" == checkOutPaymentId>checked="checked"</#if>/>
                    </td>
                     <td width='50%' nowrap="nowrap"><div> Cash </div></td>
                  </tr>
              <tr><td colspan="2"><hr /></td></tr>
              </#if>
              
             
              <#if productStorePaymentMethodTypeIdMap.COMPANY_CHECK?exists>
                  <tr>
                    <td width="1%" valign="nowrap">
                      <input type="radio" name="checkOutPaymentId" style="margin-bottom: 7px;" value="COMPANY_CHECK" <#if "COMPANY_CHECK" == checkOutPaymentId>checked="checked"</#if>/>
                    </td>
                     <td width='50%' nowrap="nowrap"><div> Company Check </div></td>
                  </tr>
              <tr><td colspan="2"><hr /></td></tr>
              </#if>
              
             
              <#if productStorePaymentMethodTypeIdMap.PERSONAL_CHECK?exists>
                  <tr>
                    <td width="1%" valign="nowrap">
                      <input type="radio" name="checkOutPaymentId" style="margin-bottom: 7px;" value="PERSONAL_CHECK" <#if "PERSONAL_CHECK" == checkOutPaymentId>checked="checked"</#if>/>
                    </td>
                     <td width='50%' nowrap="nowrap"><div> Personal Check </div></td>
                  </tr>
              <tr><td colspan="2"><hr /></td></tr>
              </#if>
              
             
              <#if productStorePaymentMethodTypeIdMap.CERTIFIED_CHECK?exists>
                  <tr>
                    <td width="1%" valign="nowrap">
                      <input type="radio" name="checkOutPaymentId" style="margin-bottom: 7px;" value="CERTIFIED_CHECK" <#if "CERTIFIED_CHECK" == checkOutPaymentId>checked="checked"</#if>/>
                    </td>
                     <td width='50%' nowrap="nowrap"><div> Certified Check </div></td>
                  </tr>
              <tr><td colspan="2"><hr /></td></tr>
              </#if>
              
             
              <#if productStorePaymentMethodTypeIdMap.PRE_PAID?exists>
                  <tr>
                    <td width="1%" valign="nowrap">
                      <input type="radio" name="checkOutPaymentId" style="margin-bottom: 7px;" value="PRE_PAID" <#if "PRE_PAID" == checkOutPaymentId>checked="checked"</#if>/>
                    </td>
                     <td width='50%' nowrap="nowrap"><div> Pre-Paid </div></td>
                  </tr>
              </#if>
              
              
              <#if !paymentMethodList?has_content>
                    <#if (!finAccounts?has_content)>
                      <tr>
                        <td colspan="2">
                        </td>
                      </tr>
                    </#if>
                  <#else>
                  <#list paymentMethodList as paymentMethod>
                    <#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                     <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
                      <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                      <tr>
                        <td width="1%">
                          <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if shoppingCart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if>/>
                        </td>
                        <td width="50%">
                          <span>CC:&nbsp;${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</span>
                          <a href="javascript:submitForm(document.checkoutInfoForm, 'EC', '${paymentMethod.paymentMethodId}');" class="btn btn-success">${uiLabelMap.CommonUpdate}</a>
                          <#if paymentMethod.description?has_content><br /><span>(${paymentMethod.description})</span></#if>
                          &nbsp;${uiLabelMap.OrderCardSecurityCode}&nbsp;<input type="text" size="5" maxlength="10" name="securityCode_${paymentMethod.paymentMethodId}" value=""/>
                        </td>
                      </tr>
                     </#if>
                    <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                     <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
                      <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                      <tr>
                        <td width="1%">
                          <input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if shoppingCart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if>/>
                        </td>
                        <td width="50%">
                          <span>${uiLabelMap.AccountingEFTAccount}:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</span>
                          <a href="javascript:submitForm(document.checkoutInfoForm, 'EE', '${paymentMethod.paymentMethodId}');" class="btn btn-success">${uiLabelMap.CommonUpdate}</a>
                          <#if paymentMethod.description?has_content><br /><span>(${paymentMethod.description})</span></#if>
                        </td>
                      </tr>
                     </#if>
                    </#if>
                  </#list>
                  </#if>

                <#-- special billing account functionality to allow use w/ a payment method -->
                <#if productStorePaymentMethodTypeIdMap.EXT_BILLACT?exists>
                  <#if billingAccountList?has_content>
                    <tr><td colspan="2"><hr /></td></tr>
                    <tr>
                      <td >
                        <span>${uiLabelMap.FormFieldTitle_billingAccountId}</span>
                        <select name="billingAccountId" style="max-width:300px;margin-left:40px;">
                          <option value=""></option>
                            <#list billingAccountList as billingAccount>
                              <#assign availableAmount = billingAccount.accountBalance?double>
                              <#assign accountLimit = billingAccount.accountLimit?double>
                              <option value="${billingAccount.billingAccountId}" <#if billingAccount.billingAccountId == selectedBillingAccountId?default("")>selected="selected"</#if>>${billingAccount.description?default("")} [${billingAccount.billingAccountId}] Available: <@ofbizCurrency amount=availableAmount isoCode=billingAccount.accountCurrencyUomId/> Limit: <@ofbizCurrency amount=accountLimit isoCode=billingAccount.accountCurrencyUomId/></option>
                            </#list>
                        </select>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <span>${uiLabelMap.OrderBillUpTo}</span>
                        <input type="text" style="margin-left:85px;" size="5" name="billingAccountAmount" value=""/>
                      </td>
                    </tr>
                  </#if>
                </#if>
                
              
              
             <#--  <#if !requestParameters.createNew?exists>    
              <tr>
                <td width='1%' nowrap="nowrap">
                <input type="radio" name="paymentMethodTypeAndId" value="EXT_OFFLINE" <#if checkOutPaymentId?exists && checkOutPaymentId == "EXT_OFFLINE">checked="checked"</#if> onchange="setCheckoutPaymentId(this.value)" onclick="setCheckoutPaymentId(this.value)"/></td>
                <td width='50%' nowrap="nowrap"><div>${uiLabelMap.OrderPaymentOfflineCheckMoney}</div></td>
              </tr>
              <tr><td colspan="2"><hr /></td></tr>
              <tr>
                <td width="1%" nowrap="nowrap"><input type="radio" name="paymentMethodTypeAndId" value="EXT_COD" <#if checkOutPaymentId?exists && checkOutPaymentId == "EXT_COD">checked="checked"</#if> onchange="setCheckoutPaymentId(this.value)" onclick="setCheckoutPaymentId(this.value)"/></td>
                <td width="50%" nowrap="nowrap"><div>${uiLabelMap.OrderCOD}</div></td>
              </tr>
              <tr><td colspan="2"><hr /></td></tr>
              </#if>
              <tr>
                <td width='1%' nowrap="nowrap"><input type="radio" name="paymentMethodTypeAndId" value="CC" onchange="setCheckoutPaymentId(this.value)" onclick="setCheckoutPaymentId(this.value)"/>
                <td width='50%' nowrap="nowrap"><div>${uiLabelMap.AccountingVisaMastercardAmexDiscover}</div></td>
              </tr>
              <tr><td colspan="2"><hr /></td></tr>
              <tr>
                <td width='1%' nowrap="nowrap"><input type="radio" name="paymentMethodTypeAndId" value="EFT" onchange="setCheckoutPaymentId(this.value)" onclick="setCheckoutPaymentId(this.value)"/>
                <td width='50%' nowrap="nowrap"><div>${uiLabelMap.AccountingAHCElectronicCheck}</div></td>
              </tr> -->
              
              
            </table>
          </form>
    </div>
</div>
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>
