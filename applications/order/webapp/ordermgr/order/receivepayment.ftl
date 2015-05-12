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
    function validate(selection) {
        var paymentMode = selection.value;
        if(paymentMode == "CREDIT_CARD") {
            cardTitle.style.display = '';
            refTitle.style.display = 'none';
        }
        if(paymentMode != "CREDIT_CARD") {
            refTitle.style.display = '';
            cardTitle.style.display = 'none';
        }
    }
</script>
<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
  <div class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">${uiLabelMap.OrderReceiveOfflinePayments}</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
      <a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBack}</a>
      <a href="javascript:document.paysetupform.submit()" class="btn btn-success">${uiLabelMap.CommonSave}</a>

      <form method="post" action="<@ofbizUrl>editOrderPaymentPreference/${donePage}</@ofbizUrl>" name="paysetupform">
        <#if requestParameters.workEffortId?exists>
            <input type="hidden" name="workEffortId" value="${requestParameters.workEffortId}" />
        </#if>
        <#if requestParameters.orderPaymentPreferenceId?exists>
            <input type="hidden" name="orderPaymentPreferenceId" value="${requestParameters.orderPaymentPreferenceId}" />
        </#if>
        <table class="basic-table" cellspacing='0'>
          <tr class="header-row">
            <td width="30%" class="align-text">${uiLabelMap.OrderPaymentType}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1">${uiLabelMap.OrderAmount}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%" id="refTitle">${uiLabelMap.OrderReference}</td>
            <td width="70%" id="cardTitle" style="display:none">Credit Card Reference Number</td>
          </tr>
          <tr>
            <td width="30%" class="align-text">
              <select name="checkOutPaymentId" id="paymentMethodTypeId" onchange="javascript:validate(this);">
                <#list paymentMethodTypes as payType>
                  <#if payType?exists && "PATIENT" != payType.paymentMethodTypeId>
                    <option value="${payType.paymentMethodTypeId}">${payType.get("description",locale)?default(payType.paymentMethodTypeId)}</option>
                  <#elseif payType?exists && "PATIENT" == payType.paymentMethodTypeId>
                    <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : "CASH"}, true)>
                    <option value="${paymentMethodType.paymentMethodTypeId}">${paymentMethodType.get("description",locale)?default(paymentMethodType.paymentMethodTypeId)}</option>
                  </#if>
                  <#if payType?exists && ("CASH" == payType.paymentMethodTypeId || "CASH PAYING" == payType.paymentMethodTypeId || "PATIENT" == payType.paymentMethodTypeId)>
                    <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : "CREDIT_CARD"}, true)>
                    <option value="${paymentMethodType.paymentMethodTypeId}">${paymentMethodType.get("description",locale)?default(paymentMethodType.paymentMethodTypeId)}</option>
                  </#if>
                </#list>
              </select>
            </td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1">
              <#assign orderPaymentPreference = delegator.findOne("OrderPaymentPreference", {"orderPaymentPreferenceId" : requestParameters.orderPaymentPreferenceId}, true)>
              <input type="text" size="15" name="receivedAmount" value="${orderPaymentPreference.maxAmount?if_exists}" readonly="true" /></td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%" id="reference"><input type="text" size="20" name="receivedAmtRefNum" /></td>
            <#-- <td width="70%" id="creditCardNumber" style="display:none">
              <#assign payType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : "CREDIT_CARD"}, true)>
              <input type="text" size="15" name="creditCardNumber" /> -->
            </td>
          </tr>
        </table>
      </form>

      <a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBack}</a>
      <a href="javascript:document.paysetupform.submit()" class="btn btn-success">${uiLabelMap.CommonSave}</a>
    </div>
  </div>
  <br />
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>