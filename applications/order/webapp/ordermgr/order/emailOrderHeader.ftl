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

<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEmailOrderSecureUrl?exists><#assign urlPrefix = baseEmailOrderSecureUrl/></#if>
<#if (orderHeader.externalId)?exists && (orderHeader.externalId)?has_content >
  <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
</#if>

<#assign serverUrl = Static["org.ofbiz.entity.util.EntityUtilProperties"].getPropertyValue("general.properties", "server.url", delegator)/>

<div id="orderHeader">
  <#-- left side -->
  <div class="columnLeft">
    <div class="screenlet">
      <h3>
        <!-- <#if maySelectItems?default("N") == "Y" && returnLink?default("N") == "Y" && (orderHeader.statusId)?if_exists == "ORDER_COMPLETED" && roleTypeId?if_exists == "PLACING_CUSTOMER">
          <a href="<@ofbizUrl fullPath="true">makeReturn?orderId=${orderHeader.orderId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.OrderRequestReturn}</a>
        </#if> -->
        <!-- ${uiLabelMap.OrderOrder} -->
        <#if orderHeader?has_content && "SALES_ORDER" != orderHeader.orderTypeId>
          <block> Order Number: </block> <a href="${serverUrl}ordermgr/control/orderview?orderId=${orderHeader.orderId}">${orderHeader.orderId}</a>
        <#else>
          <block> Order Number: ${orderHeader.orderId}</block>
        </#if>
        <!-- ${uiLabelMap.CommonInformation}
        <#if (orderHeader.orderId)?exists>
          ${externalOrder?if_exists} [ <a href="<@ofbizUrl fullPath="true">order.pdf?orderId=${(orderHeader.orderId)?if_exists}</@ofbizUrl>" class="lightbuttontext">PDF</a> ]
        </#if> -->
      </h3>
      <#-- placing customer information -->
      <ul>
        <#if localOrderReadHelper?exists && orderHeader?has_content>
          <#if localOrderReadHelper?has_content && "PURCHASE_ORDER" == localOrderReadHelper.orderTypeId && orderHeader?has_content>
            <#assign displayParty = localOrderReadHelper.getSupplierAgent()?if_exists/>
          <#elseif localOrderReadHelper?has_content && "SALES_ORDER" == localOrderReadHelper.orderTypeId && orderHeader?has_content && orderRxHeader?has_content>
            <#assign displayPatient = orderRxHeader.firstName?if_exists + " " + orderRxHeader.secondName?if_exists + " " + orderRxHeader.thirdName?if_exists>
          <#else>
            <#assign displayParty = localOrderReadHelper.getPlacingParty()?if_exists/>
          </#if>
          <#if displayParty?has_content>
            <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
          </#if>
          <li>
            <block style="font-weight:bold;"> ${uiLabelMap.PartyName} </block>
            <#if displayPartyNameResult?exists>
              ${(displayPartyNameResult.groupName?if_exists)?default("[Name Not Found]")}
            <#elseif displayPatient?exists>
              ${(displayPatient?if_exists)?default("[Name Not Found]")}
            </#if>
          </li>
        </#if>
        <#-- order status information -->
        <li>
          <block style="font-weight:bold;"> ${uiLabelMap.CommonStatus} </block>
          <#if orderHeader?has_content>
            <#-- ${localOrderReadHelper.getStatusString(locale)} -->
            <#assign currentStatus = orderHeader.getRelatedOne("StatusItem")?if_exists>
            ${currentStatus.get("description",locale)}
          <#else>
            ${uiLabelMap.OrderNotYetOrdered}
          </#if>
        </li>
        <#-- ordered date -->
        <#if orderHeader?has_content>
          <li>
           <block style="font-weight:bold;"> ${uiLabelMap.CommonDate} </block>
            ${orderHeader.orderDate?string("dd/MM/yyyy HH:mm:ss")}
          </li>
        </#if>
        <#if distributorId?exists>
          <li>
            <block style="font-weight:bold;"> ${uiLabelMap.OrderDistributor} </block>
            ${distributorId}
          </li>
        </#if>
      </ul>
    </div>
  </div>
  <#if orderItemShipGroups?has_content && localOrderReadHelper?has_content && "SALES_ORDER" != localOrderReadHelper.orderTypeId>
    <#-- right side -->
    <div class="screenlet columnRight">
      <h3>${uiLabelMap.OrderShippingInformation}</h3>
      <#-- shipping address -->
      <#assign groupIdx = 0>
      <#list orderItemShipGroups as shipGroup>
        <#if orderHeader?has_content>
          <#assign shippingAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
          <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
        <#else>
          <#assign shippingAddress = cart.getShippingAddress(groupIdx)?if_exists>
          <#assign groupNumber = groupIdx + 1>
        </#if>
        <ul>
          <#if shippingAddress?has_content>
            <li>
              <ul>
                <li>
                  ${uiLabelMap.OrderDestination} [${groupNumber}]
                  <#if shippingAddress.toName?has_content>${uiLabelMap.CommonTo}: ${shippingAddress.toName}</#if>
                </li>
                <li>
                  <#if shippingAddress.attnName?has_content>${uiLabelMap.PartyAddrAttnName}: ${shippingAddress.attnName}</#if>
                </li>
                <li>
                  ${shippingAddress.address1}
                </li>
                <li>
                  <#if shippingAddress.address2?has_content>${shippingAddress.address2}</#if>
                </li>
                <li>
                  <#assign shippingStateGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
                  ${shippingAddress.city}<#if shippingStateGeo?has_content>, ${shippingStateGeo.geoName?if_exists}</#if> ${shippingAddress.postalCode?if_exists}
                </li>
                <li>
                  <#assign shippingCountryGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.countryGeoId?if_exists}, false))?if_exists />
                  <#if shippingCountryGeo?has_content>${shippingCountryGeo.geoName?if_exists}</#if>
                </li>
              </ul>
            </li>
          </#if>
          <li>
            <ul>
              <li>
               <block style="font-weight:bold;"> ${uiLabelMap.OrderMethod}: </block>
                <#if orderHeader?has_content>
                  <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
                  <#assign carrierPartyId = shipGroup.carrierPartyId?if_exists>
                <#else>
                  <#assign shipmentMethodType = cart.getShipmentMethodType(groupIdx)?if_exists>
                  <#assign carrierPartyId = cart.getCarrierPartyId(groupIdx)?if_exists>
                </#if>
                <#if carrierPartyId?exists && carrierPartyId != "_NA_">${carrierPartyId?if_exists}</#if>
                ${(shipmentMethodType.description)?default("N/A")}
              </li>
              <li>
                <#if shippingAccount?exists>${uiLabelMap.AccountingUseAccount}: ${shippingAccount}</#if>
              </li>
            </ul>
          </li>
          <#-- tracking number -->
          <#if trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
            <li>
              ${uiLabelMap.OrderTrackingNumber}
              <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
              <#if shipGroup.trackingNumber?has_content>
                ${shipGroup.trackingNumber}
              </#if>
              <#if orderShipmentInfoSummaryList?has_content>
                <#list orderShipmentInfoSummaryList as orderShipmentInfoSummary>
                  <#if (orderShipmentInfoSummaryList?size > 1)>${orderShipmentInfoSummary.shipmentPackageSeqId}: </#if>
                  Code: ${orderShipmentInfoSummary.trackingCode?default("[Not Yet Known]")}
                  <#if orderShipmentInfoSummary.boxNumber?has_content>${uiLabelMap.OrderBoxNumber}${orderShipmentInfoSummary.boxNumber}</#if>
                  <#if orderShipmentInfoSummary.carrierPartyId?has_content>(${uiLabelMap.ProductCarrier}: ${orderShipmentInfoSummary.carrierPartyId})</#if>
                </#list>
              </#if>
            </li>
          </#if>
          <#-- splitting preference -->
          <#-- <#if orderHeader?has_content>
            <#assign maySplit = shipGroup.maySplit?default("N")>
          <#else>
            <#assign maySplit = cart.getMaySplit(groupIdx)?default("N")>
          </#if>
          <li>
            ${uiLabelMap.OrderSplittingPreference}:
            <#if maySplit?default("N") == "N">${uiLabelMap.OrderPleaseWaitUntilBeforeShipping}.</#if>
            <#if maySplit?default("N") == "Y">${uiLabelMap.OrderPleaseShipItemsBecomeAvailable}.</#if>
          </li> -->
          <#-- shipping instructions -->
          <#if orderHeader?has_content>
            <#assign shippingInstructions = shipGroup.shippingInstructions?if_exists>
          <#else>
            <#assign shippingInstructions =  cart.getShippingInstructions(groupIdx)?if_exists>
          </#if>
          <#if shippingInstructions?has_content>
            <li>
             <block style="font-weight:bold;"> ${uiLabelMap.OrderInstructions} </block>
              ${shippingInstructions}
            </li>
          </#if>
          <#-- gift settings -->
          <#-- <#if orderHeader?has_content>
            <#assign isGift = shipGroup.isGift?default("N")>
            <#assign giftMessage = shipGroup.giftMessage?if_exists>
          <#else>
            <#assign isGift = cart.getIsGift(groupIdx)?default("N")>
            <#assign giftMessage = cart.getGiftMessage(groupIdx)?if_exists>
          </#if>
          <#if productStore?has_content>
            <#if productStore.showCheckoutGiftOptions?if_exists != "N">
              <li>
                ${uiLabelMap.OrderGift}?
                <#if isGift?default("N") == "N">${uiLabelMap.OrderThisIsNotGift}.</#if>
                <#if isGift?default("N") == "Y">${uiLabelMap.OrderThisIsGift}.</#if>
              </li>
              <#if giftMessage?has_content>
                <li>
                  <block style="font-weight:bold;"> ${uiLabelMap.OrderGiftMessage} </block>
                  ${giftMessage}
                </li>
              </#if>
            </#if>
          </#if> -->
          <#if shipGroup_has_next>
            
          </#if>
        </ul>
        <#assign groupIdx = groupIdx + 1>
      </#list><#-- end list of orderItemShipGroups -->
    </div>
  <#elseif orderItemShipGroups?has_content && localOrderReadHelper?has_content && "SALES_ORDER" == localOrderReadHelper.orderTypeId && orderHeader?has_content && orderRxHeader?has_content && "Y" == homeService>
    <#-- right side -->
    <div class="screenlet columnRight">
      <h3>${uiLabelMap.OrderShippingInformation}</h3>
      <ul>
        <li>
          <ul>
            <li>
              <block style="font-weight:bold;"> Recepient: </block>
              <#assign patientName = orderRxHeader.firstName?if_exists + " " + orderRxHeader.secondName?if_exists + " " + orderRxHeader.thirdName?if_exists>
              <#if patientName?exists> ${patientName?if_exists} </#if>
            </li>
            <#if orderRxHeader.afyaId?exists && orderRxHeader.firstName?exists && orderRxHeader.thirdName?exists>
              
              <#assign patientList = delegator.findByAnd("Patient", {"afyaId":orderRxHeader.afyaId})/>
              <#if patientList?has_content>
                <#assign patient = patientList.get(0)?if_exists>
                <#if patient.address1?exists>
                  <li>
                    ${patient.address1?if_exists}
                  </li>
                </#if>
                <#if patient.address2?exists || patient.city?exists>
                  <li>
                    <#if patient.address2?exists>${patient.address2?if_exists}, </#if>
                    <#if patient.city?exists>${patient.city?if_exists},</#if>
                  </li>
                </#if>
                <#if patient.postalCode?exists || patient.governorate?exists || patient.country?exists>
                  <li>
                    <#if patient.postalCode?exists> ${patient.postalCode?if_exists} - </#if>
                    <#if patient.governorate?exists> ${patient.governorate?if_exists}, </#if>
                    <#if patient.country?exists> ${patient.country?if_exists}</#if>
                  </li>
                </#if>
              </#if>
            <#elseif !(orderRxHeader.afyaId?exists) && orderRxHeader.firstName?exists && orderRxHeader.thirdName?exists>
              <#assign patientList = delegator.findByAnd("Patient", {"firstName":orderRxHeader.firstName, "thirdName":orderRxHeader.thirdName})/>
              <#if patientList?has_content>
                <#assign patient = patientList.get(0)?if_exists>
                <#if patient.address1?exists>
                  <li>
                    ${patient.address1?if_exists}
                  </li>
                </#if>
                <#if patient.address2?exists || patient.city?exists>
                  <li>
                    <#if patient.address2?exists>${patient.address2?if_exists}, </#if>
                    <#if patient.city?exists>${patient.city?if_exists},</#if>
                  </li>
                </#if>
                <#if patient.postalCode?exists || patient.governorate?exists || patient.country?exists>
                  <li>
                    <#if patient.postalCode?exists> ${patient.postalCode?if_exists} - </#if>
                    <#if patient.governorate?exists> ${patient.governorate?if_exists}, </#if>
                    <#if patient.country?exists> ${patient.country?if_exists}</#if>
                  </li>
                </#if>
                
              </#if>
            </#if>
          </ul>
        </li>
        <li>
          <ul>
            <li>
              <span><block style="font-weight:bold;"> ${uiLabelMap.OrderMethod}: </block>Home Delivery</span>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </#if>

  <div class="clearBoth"></div>
  <div class="clearBoth"></div>

</div>
