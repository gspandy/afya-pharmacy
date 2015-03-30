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

    <#if purchaseOrderHeaderList?has_content>
      <table class="basic-table hover-bar" cellspacing='0'>
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderOrder} #</td>
          <td width="15%">${uiLabelMap.OrderOrderBillToParty}</td>
          <td width="10%" style="text-align:right;">${uiLabelMap.CommonAmount}</td>
          <td width="20%" style="text-align:center;">${uiLabelMap.CommonStatus}</td>
        </tr>
        <#assign alt_row = false>
        <#list purchaseOrderHeaderList as purchaseOrderHeader>
          <#assign status = purchaseOrderHeader.getRelatedOneCache("StatusItem")>
          <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(purchaseOrderHeader)>
           <#assign billFromParty = orh.getBillFromParty()?if_exists>
          <#assign billToParty = orh.getBillToParty()?if_exists>
          <#if billToParty?has_content>
            <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", purchaseOrderHeader.orderDate, "userLogin", userLogin))/>
            <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
          </#if>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/orderview?orderId=${purchaseOrderHeader.orderId}" class="btn btn-link">${purchaseOrderHeader.orderId}</a></td>
            <td>${billFromParty.groupName?default(billFromParty.partyId)?if_exists}</td>
            <td style="text-align:right;"><@ofbizCurrency amount=purchaseOrderHeader.grandTotal isoCode=purchaseOrderHeader.currencyUom/></td>
            <td style="text-align:center;">${purchaseOrderHeader.getRelatedOneCache("StatusItem").get("description",locale)}</td>
          </tr>
        </#list>
      </table>
    </#if>
