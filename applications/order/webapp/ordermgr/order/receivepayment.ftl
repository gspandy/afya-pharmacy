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
      <a href="javascript:validateReceivableAmount()" class="btn btn-success">${uiLabelMap.CommonSave}</a>

      <form method="post" action="<@ofbizUrl>editOrderPaymentPreference/${donePage}</@ofbizUrl>" name="paysetupform" id="paysetupform">
        <#if requestParameters.workEffortId?exists>
            <input type="hidden" name="workEffortId" value="${requestParameters.workEffortId}" />
        </#if>
        <#if requestParameters.orderPaymentPreferenceId?exists>
            <input type="hidden" name="orderPaymentPreferenceId" value="${requestParameters.orderPaymentPreferenceId}" />
        </#if>
        <#if requestParameters.maxAmount?exists>
            <input type="hidden" name="maxAmount" id="maxAmount" value="${requestParameters.maxAmount?default(0.000)}" />
        </#if>
        <table class="basic-table" cellspacing='0'>
          <tr class="header-row">
            <td width="30%" class="align-text">${uiLabelMap.OrderPaymentType}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="11%">${uiLabelMap.OrderAmount}</td>
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
            <td width="11%">
              <#assign orderPaymentPreference = delegator.findOne("OrderPaymentPreference", {"orderPaymentPreferenceId" : requestParameters.orderPaymentPreferenceId}, true)>
              <input type="hidden" name="pendingAmount" id="pendingAmount" value="${requestParameters.pendingAmount?default(0.000)}" />
              <input type="text" size="15" maxlength="60" class="currency required" style="text-align: right;padding-right: 3px;" name="receivedAmount" id="receivedAmount" value="<@ofbizAmount amount=requestParameters.pendingAmount?default(0.000)/>"/><font color="red"> *</font>
              <#-- <input type="text" size="15" name="receivedAmount" value="${requestParameters.pendingAmount?if_exists}" readonly="true" style="text-align:right;"/></td> -->
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%" id="reference"><input type="text" size="20" name="receivedAmtRefNum" /></td>
            <#-- <td width="70%" id="creditCardNumber" style="display:none">
              <#assign payType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : "CREDIT_CARD"}, true)>
              <input type="text" size="15" name="creditCardNumber" /> -->
            </td>
          </tr>
        </table>
      </form>
      <script>
          var form = document.paysetupform;
          jQuery(form).validate();

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

          function validateReceivableAmount() {
            var pendingAmount = $('#pendingAmount').val();
            var amountToReceive = $('#receivedAmount').val();
            var receivableAmount = parseFloat(amountToReceive).toFixed(3);
            if(receivableAmount == "NaN") {
              alert('Please enter the amount.');
            } else if (parseFloat(receivableAmount) == 0.000) {
              alert('Amount to receive can not be Zero.');
            } else if (0.000 > parseFloat(receivableAmount)) {
              alert('Amount to receive can not be in negative.');
            } else if(parseFloat(receivableAmount) > parseFloat(pendingAmount)) {
              alert('Receivable Amount can not be greater than the Payable Amount.');
            } else {
              document.paysetupform.submit();
            }
          }
        </script>

      <a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBack}</a>
      <a href="javascript:validateReceivableAmount()" class="btn btn-success">${uiLabelMap.CommonSave}</a>
    </div>
  </div>
  <br />
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>