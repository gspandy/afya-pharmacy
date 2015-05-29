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
<#escape x as x?xml>
<#assign shipmentReceiptAndItems = delegator.findByAnd("ShipmentReceiptAndItem", {"orderId":parameters.purchaseOrderId, "shipmentId":parameters.shipmentId})>
<#assign orderId = shipmentReceiptAndItems.get(0).get("orderId")>
<#assign shipmentId = shipmentReceiptAndItems.get(0).get("shipmentId")>
<#assign shipmentGv = delegator.findOne("Shipment", {"shipmentId",shipmentId},false)>
<#assign partyGroupGv = delegator.findOne("PartyGroup", {"partyId":shipmentGv.partyIdFrom}, false)>

    <fo:block><fo:leader></fo:leader></fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <fo:block font-size="14pt" text-align="center" font-weight="bold"><#if shipmentId?has_content>${shipmentId?if_exists}</#if></fo:block>
    <#assign orderDate = orderHeader.get("orderDate")>
    <fo:block font-size="14pt" text-align="center"><#if shipmentReceiptAndItems?has_content>${shipmentReceiptAndItems.get(0).get("datetimeReceived")?string("dd/MM/yyyy")}</#if></fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>

</#escape>