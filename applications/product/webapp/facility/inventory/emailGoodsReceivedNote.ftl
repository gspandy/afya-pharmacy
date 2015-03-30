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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>${title!}</title>
    <#-- this needs to be fully qualified to appear in email; the server must also be available -->
    <style type="text/css">
        html, body, div, h1, h3, a, ul,
        li, table, tbody, tfoot, thead,
        tr, th, td {
            border:0;
            margin:0;
            outline:0;
            padding:0;
            font-size: 100%;
            background:transparent;
            vertical-align: baseline;
        }

        a, body, th {
            font-style: normal;
            font-weight: normal;
            text-decoration: none;
        }

        body, th {
            text-align: left;
        }

        ul {
            list-style: none;
        }

        div.screenlet {
            background-color: #FFFFFF;
            border: 0.1em solid #999999;
            height: auto !important;
            height: 1%;
            margin-bottom: 1em;
        }

        body {
            background: #D4D0C8;
            font-size: 62.5%;
            position: relative;
            line-height: 1;
            color: black;
            font-family: Verdana, Arial, Helvetica, sans-serif;
        }

        h1 {
            font-size: 1.6em;
            font-weight: bold;
        }

        h3 {
            font-size: 1.1em;
            font-weight: bold;
        }

        /* IE7 fix */
        table {
            font-size: 1em;
        }

        div.screenlet ul {
            margin: 10px;
        }

        div.screenlet li {
            line-height: 15px;
        }

        div.screenlet h3 {
            background:#1C334D none repeat scroll 0 0;
            color:#FFFFFF;
            height:auto !important;
            padding:3px 4px 4px;
        }

        .columnLeft {
            width: 45%;
            float: left;
            margin-right: 10px; 
        }

        .columnRight {
            width: 45%;
            float: left;
            margin-left: 10px;
            clear: none;
        }

        div.screenlet table {
            width: 100%;
            margin: 10px;
        }

        div.screenlet table tfoot th {
            text-align: right;
            font-weight: bold;
        }

        .clearBoth {
            clear: both;
        }
    </style>
  </head>
  <body>
    <#assign shipmentReceiptAndItems = delegator.findByAnd("ShipmentReceiptAndItem", {"orderId":parameters.orderId, "facilityId":parameters.facilityId, "shipmentId":parameters.shipmentId})>
    <#assign orderId = shipmentReceiptAndItems.get(0).get("orderId")>
    <#assign shipmentId = shipmentReceiptAndItems.get(0).get("shipmentId")>
    <#assign shipmentGv = delegator.findOne("Shipment", {"shipmentId",shipmentId},false)>
    <#assign partyGroupGv = delegator.findOne("PartyGroup", {"partyId":shipmentGv.partyIdFrom}, false)>
    <#assign serverUrl = Static["org.ofbiz.entity.util.EntityUtilProperties"].getPropertyValue("general.properties", "server.url", delegator)/>

    <div class="screenlet">
      <h3><block><center>GOODS RECEIVED NOTE</center></block></h3>
      <#if shipmentReceiptAndItems?has_content>
        <#if orderId?has_content>
          <#assign orderContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getOrderContactMechValueMaps(delegator, orderId)>
          <#if orderContactMechValueMaps?has_content>
            <#list orderContactMechValueMaps as orderContactMechValueMap>
              <#assign contactMech = orderContactMechValueMap.contactMech>
              <#assign orderContactMech = orderContactMechValueMap.orderContactMech>
              <#if orderContactMech?has_content && orderContactMech.contactMechPurposeTypeId == "SHIP_ORIG_LOCATION">
                <#assign contactMechId = orderContactMech.contactMechId>
                <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                  <#assign originPostalAddress = orderContactMechValueMap.postalAddress>
                  <table>
                    <tr>
                      <td width="5%" style="font-weight:bold;">Supplier:</td>
                      <td>
                        <table>
                          <#if originPostalAddress?has_content && originPostalAddress.contactMechId == contactMechId>
                            <tr>
                              <td>${originPostalAddress.toName?if_exists}</td>
                            </tr>
                            <#if originPostalAddress.attnName?has_content>
                              <tr>
                                <td><b>${uiLabelMap.CommonAttn}: </b>${originPostalAddress.attnName?if_exists}</td>
                              </tr>
                            </#if>
                            <tr>
                              <td>${originPostalAddress.address1?if_exists}</td>
                            </tr>
                            <#if originPostalAddress.address2?has_content>
                              <tr>
                                <td>${originPostalAddress.address2?if_exists}</td>
                              </tr>
                            </#if>
                            <tr>
                              <td>
                                ${originPostalAddress.city?if_exists} 
                                <#if originPostalAddress.stateProvinceGeoId?has_content>, ${originPostalAddress.stateProvinceGeoId}</#if>
                                ${originPostalAddress.postalCode?if_exists} ${originPostalAddress.countryGeoId?if_exists}
                              </td>
                            </tr>
                          <#else>
                            <tr>
                              <td>${partyGroupGv.groupName?if_exists}</td>
                            </tr>
                          </#if>
                        </table>
                      </td>
                      <#if shipmentReceiptAndItems?has_content>
                        <td>
                          <table>
                            <tr>
                              <td width="20%" style="font-weight:bold; text-align: right;">Shipment No.:</td>
                              <td><a href="${serverUrl}facility/control/ViewShipment?shipmentId=${shipmentId}">${shipmentId?if_exists}</td>
                            </tr>
                            <tr>
                              <td width="20%" style="font-weight:bold; text-align: right;">Date Time:</td>
                              <td>${shipmentReceiptAndItems.get(0).get("datetimeReceived")?string("dd/MM/yyyy HH:mm:ss")}</td>
                            </tr>
                          </table>
                        </td>
                      </#if>
                    </tr>
                  </table>
                </#if>
              </#if>
            </#list>
          </#if>
        </#if>
      </div>

      <div class="screenlet">
        <h3><block>Items</block></h3>
        <table style="margin: 12px !important; width: 97% !important; border: thin solid ! important; border-color: #D4D0C8 !important;">
          <tr style="font-weight: bold; text-align: center;">
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">Invoice No.</td>
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">${uiLabelMap.ProductProduct}</td>
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">Unit</td>
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">${uiLabelMap.ProductQuantity}</td>
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">${uiLabelMap.OrderUnitPrice}</td>
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">${uiLabelMap.OrderAmount}</td>
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">Accepted Qty</td>
            <td style="background-color: #D4D0C8; height: 20pt; display-align: center; vertical-align: middle;">Rejected Qty</td>
          </tr>
          <#assign rowColor = "white">
          <#list shipmentReceiptAndItems as item>
            <tr style="height: 15pt; text-align: center;">
              <td background-color="${rowColor}">
                <#assign orderItemGvs = delegator.findByAnd("OrderHeaderAndItems",{"orderId":item.orderId,"orderItemSeqId":item.orderItemSeqId})/>
                <#assign orderItemgv = orderItemGvs.get(0)/>
                <#assign receivedRowItems = delegator.findByAnd("ShipmentReceiptAndItem", {"shipmentId":item.shipmentId, "receiptId":item.receiptId, "productId":item.productId})/>
                <#assign purchaseOrderId = receivedRowItems.get(0).get("orderId")>
                <#assign shipmentReceiptId = receivedRowItems.get(0).get("receiptId")>
                <#assign orderItemBillingList = delegator.findByAnd("OrderItemBilling", {"orderId":purchaseOrderId, "shipmentReceiptId":shipmentReceiptId})>
                <#assign orderItemBillingGv = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderItemBillingList)>
                <#if orderItemBillingGv?has_content>
                  ${orderItemBillingGv.invoiceId?if_exists}
                </#if>
              </td>
              <#assign productGv =  delegator.findOne("Product", {"productId":item.productId},false)>
              <#assign uomGv = delegator.findOne("Uom", {"uomId":productGv.quantityUomId}, false)>
              <td background-color="${rowColor}">${productGv.internalName?if_exists}</td>
              <td background-color="${rowColor}">${uomGv.description?if_exists}</td>
              <td background-color="${rowColor}">${item.quantityAccepted?string.number?if_exists}</td>
              <td background-color="${rowColor}"><@ofbizCurrency amount=orderItemgv.unitPrice?default(0)?if_exists isoCode=orderItemgv.currencyUom?if_exists /></td>
              <td background-color="${rowColor}">
                <#assign sumAmount = item.getBigDecimal("quantityAccepted").multiply(orderItemgv.getBigDecimal("unitPrice"))>
                <@ofbizCurrency amount=sumAmount?if_exists isoCode=orderItemgv.currencyUom?if_exists />
              </td>
              <td background-color="${rowColor}">${item.quantityAccepted?if_exists?string.number}</td>
              <td background-color="${rowColor}">${item.quantityRejected?if_exists?default(0)?string.number}</td>
            </tr>
            <#-- toggle the row color -->
            <#if rowColor == "white">
              <#assign rowColor = "#CCCCCC">
            <#else>
              <#assign rowColor = "white">
            </#if>
          </#list>
        </table>
      </div>

      <br/>

      <table style="width: 100% !important;">
        <tr>
          <#-- <#assign userLogin = session.getAttribute("userLogin")>
          <#assign partyId = userLogin.getString("partyId")>
          <#assign party = delegator.findOne("Person", {"partyId",partyId},false)> -->
          <#if shipmentGv.receivedBy?has_content>
            <#assign receivedParty = delegator.findOne("Person", {"partyId",shipmentGv.receivedBy},false)>
            <#if receivedParty?has_content>
              <td>
                <block style="font-weight:bold;">Received by : </block><block border-bottom="dotted">${receivedParty.firstName?if_exists} ${receivedParty.middleName?if_exists} ${receivedParty.lastName?if_exists}</block>
              </td>
            <#else>
              <td><block style="font-weight:bold;">Received by : </block>............................</td>
            </#if>
          <#else>
            <td><block style="font-weight:bold;">Received by : </block>............................</td>
          </#if>
          <#if shipmentGv.inspectedBy?has_content>
            <#assign inspectedParty = delegator.findOne("Person", {"partyId",shipmentGv.inspectedBy},false)>
            <#if inspectedParty?has_content>
              <td>
                <block style="font-weight:bold;">Checked by : </block><block border-bottom="dotted">${inspectedParty.firstName?if_exists} ${inspectedParty.middleName?if_exists} ${inspectedParty.lastName?if_exists}</block>
              </td>
            <#else>
              <td><block style="font-weight:bold;">Checked by : </block>............................</td>
            </#if>
          <#else>
            <td><block style="font-weight:bold;">Checked by : </block>............................</td>
          </#if>
          <#if shipmentGv.approvedBy?has_content>
            <#assign approvedParty = delegator.findOne("Person", {"partyId",shipmentGv.approvedBy},false)>
            <#assign gmApprovalStatus = shipmentGv.get("approvalStatus")>
            <#if approvedParty?has_content && gmApprovalStatus=="GM_APPROVED">
              <td>
                <block style="font-weight:bold;">Approved by : </block><block border-bottom="dotted">${approvedParty.firstName?if_exists} ${approvedParty.middleName?if_exists} ${approvedParty.lastName?if_exists}</block>
              </td>
            <#elseif approvedParty?has_content && gmApprovalStatus=="GM_REJECTED">
              <td>
                <block style="font-weight:bold;">Rejected by : </block><block border-bottom="dotted">${approvedParty.firstName?if_exists} ${approvedParty.middleName?if_exists} ${approvedParty.lastName?if_exists}</block>
              </td>
            <#else>
              <td><block style="font-weight:bold;">Approved by : </block>............................</td>
            </#if>
          <#else>
            <td><block style="font-weight:bold;">Approved by : </block>............................</td>
          </#if>
        </tr>
      </table>
    <#else>
      <font size="14pt">ERROR: No goods found received for this shipment</font>
    </#if>

  </body>
</html>
