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

    <#if orderHeaderList?has_content>
      <table class="basic-table hover-bar" cellspacing='0'>
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderOrder} #</td>
          <#-- <td width="15%">${uiLabelMap.OrderOrderBillToParty}</td>
          <td width="25%">${uiLabelMap.OrderProductStore}</td> -->
          <td width="10%">Clinic Id</td>
          <td width="10%">Visit Id</td>
          <td width="15%">Visit Date</td>
          <td width="20%">Patient Name</td>
          <td width="20%" class="align-text">${uiLabelMap.CommonAmount} &nbsp;&nbsp;</td>
          <#-- <td width="20%">${uiLabelMap.OrderTrackingCode}</td> -->
          <td width="15%" style="text-align:center;">${uiLabelMap.CommonStatus}</td>
        </tr>
        <#assign alt_row = false>
        <#list orderHeaderList as orderHeader>
          <#assign status = orderHeader.getRelatedOneCache("StatusItem")>
          <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
          <#assign billToParty = orh.getBillToParty()?if_exists>
          <#if billToParty?has_content>
            <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
            <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
          </#if>
          <#assign productStore = orderHeader.getRelatedOneCache("ProductStore")?if_exists />
          <#assign orderRxHeader = delegator.findOne("OrderRxHeader",true,{"orderId":orderHeader.orderId})?default({})>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/orderview?orderId=${orderHeader.orderId}" class="btn btn-link">${orderHeader.orderId}</a></td>
            <#-- <td>${billTo?if_exists}</td>
            <td><#if productStore?has_content>${productStore.storeName?default(productStore.productStoreId)}</#if></td> -->
            <td><#if orderRxHeader?has_content>${orderRxHeader.clinicId?if_exists}</#if></td>
            <td><#if orderRxHeader?has_content>${orderRxHeader.visitId?if_exists}</#if></td>
            <td><#if orderRxHeader?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderRxHeader.visitDate, "dd/MM/yyyy")}</#if></td>
            <td><#if orderRxHeader?has_content>${orderRxHeader.patientFirstName?if_exists} ${orderRxHeader.patientLastName?if_exists}</#if></td>
            <td class="align-text"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></td>
            <#-- <td>
              <#assign trackingCodes = orderHeader.getRelated("TrackingCodeOrder")>
              <#list trackingCodes as trackingCode>
                <#if trackingCode?has_content>
                  <a href="/marketing/control/FindTrackingCodeOrders?trackingCodeId=${trackingCode.trackingCodeId}&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${trackingCode.trackingCodeId}</a><br />
                </#if>
              </#list>
            </td> -->
            <td style="text-align:center;">${orderHeader.getRelatedOneCache("StatusItem").get("description",locale)}</td>
          </tr>
        </#list>
      </table>
    </#if>
