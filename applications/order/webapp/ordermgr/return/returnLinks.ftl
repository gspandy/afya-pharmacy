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
<#assign selected = tabButtonItem?default("void")>
<#if returnHeader?exists>
    <ul class="nav nav-pills">
      <li<#if selected="OrderReturnHeader"> class="selected"</#if>><a href="<@ofbizUrl>returnMain?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnHeader}</a></li>
      <li<#if selected="OrderReturnItems"> class="selected"</#if>><a href="<@ofbizUrl>returnItems?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnItems}</a></li>
      <li<#if selected="OrderReturnHistory"> class="selected"</#if>><a href="<@ofbizUrl>ReturnHistory?returnId=${returnId?if_exists}</@ofbizUrl>">${uiLabelMap.OrderReturnHistory}</a></li>
      <li><a href="<@ofbizUrl>return.pdf?returnId=${returnId?if_exists}</@ofbizUrl>" class="buttontext">PDF</a></li>

        <#if returnHeader.statusId == "RETURN_COMPLETED" || returnHeader.statusId == "SUP_RETURN_COMPLETED">
      <li<#if selected="OrderReturnExport"> class="selected"</#if>><a href="#" onClick="document.exportForm.submit();">Generate Tally XML</a></li>
      </#if>

    <form name="exportForm" method="POST" action="<@ofbizUrl>massVoucherExport</@ofbizUrl>">
    	<input name="returnIdList" type="hidden" value="${returnId?if_exists}"/>
    	<input name="voucherTypes" type="hidden" value="DebitCredit"/>
    </form>
    <#if selected != "OrderReturnHistory">
        <#if returnId?exists>
        <#assign returnItems = delegator.findByAnd("ReturnItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("returnId", returnId, "returnTypeId", "RTN_REFUND"))/>
        <#if returnItems?has_content>
          <#assign orderId = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(returnItems)).getString("orderId")/>
          <#assign partyId = "${(returnHeader.fromPartyId)?if_exists}"/>
            <li><a href="<@ofbizUrl>setOrderCurrencyAgreementShipDates?type=SALES_ORDER&amp;partyId=${partyId?if_exists}&amp;originOrderId=${orderId?if_exists}</@ofbizUrl>" class="btn">${uiLabelMap.OrderCreateExchangeOrder} ${uiLabelMap.CommonFor} ${orderId?if_exists}</a>
        </#if>
        <#if "RETURN_ACCEPTED" == returnHeader.statusId>
          <#assign returnItems = delegator.findByAnd("ReturnItem", {"returnId" : returnId})/>
          <#if returnItems?has_content>
            <#assign orderId = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(returnItems)).getString("orderId")/>
            <#assign shipGroupAssoc = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("OrderItemShipGroupAssoc", {"orderId" : orderId}))/>
            <#assign shipGroup = delegator.findOne("OrderItemShipGroup", {"orderId" : orderId, "shipGroupSeqId" : shipGroupAssoc.shipGroupSeqId}, false)>
            <#if shipGroup?exists && shipGroup.shipmentMethodTypeId?if_exists != "NO_SHIPPING">
              <#assign shipGroupShipment = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("Shipment", {"primaryOrderId" : shipGroup.orderId, "primaryShipGroupSeqId" : shipGroup.shipGroupSeqId}))/>
              <#if shipGroupShipment?exists>
                <#assign shipmentRouteSegment = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("ShipmentRouteSegment", {"shipmentId" : shipGroupShipment.shipmentId}))>
                <#if shipmentRouteSegment?exists>
                  <#if "UPS" == shipmentRouteSegment.carrierPartyId?if_exists>
                    <a href="javascript:document.upsEmailReturnLabel.submit();" class="btn">${uiLabelMap.ProductEmailReturnShippingLabelUPS}</a>
                    <form name="upsEmailReturnLabel" method="post" action="<@ofbizUrl>upsEmailReturnLabelReturn</@ofbizUrl>">
                      <input type="hidden" name="returnId" value="${returnId}"/>
                      <input type="hidden" name="shipmentId" value="${shipGroupShipment.shipmentId}"/>
                      <input type="hidden" name="shipmentRouteSegmentId" value="${shipmentRouteSegment.shipmentRouteSegmentId}" />
                    </form>
                  </#if>
                </#if>
              </#if>
            </#if>
          </#if>
        </#if>
      </#if>
    </#if>
    </ul>
   </div>
  <div>
<#else>
<#--
  <h1>${uiLabelMap.OrderCreateNewReturn}</h1>
-->
  <#if requestParameters.returnId?has_content>
    <h2>${uiLabelMap.OrderNoReturnFoundWithId} : ${requestParameters.returnId}</h2>
  </#if>
  <br />
</#if>