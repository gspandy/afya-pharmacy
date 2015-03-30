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
<#if requestAttributes.uiLabelMap?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>
<#assign selected = tabButtonItem?default("void")>
<#if shipmentId?has_content>
    <ul class="nav nav-pills">
        <li<#if selected="ViewShipment"> class="selected"</#if>><a href="<@ofbizUrl>ViewShipment?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.CommonView}</a></li>
        <li<#if selected="EditShipment"> class="selected"</#if>><a href="<@ofbizUrl>EditShipment?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.CommonEdit}</a></li>
        <#if (shipment.shipmentTypeId)?exists && shipment.shipmentTypeId = "PURCHASE_RETURN">
            <li<#if selected="AddItemsFromInventory"> class="selected"</#if>><a href="<@ofbizUrl>AddItemsFromInventory?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductOrderItems}</a></li>
            <li>
              <a href="<@ofbizUrl>EditThirdPartyInvoice?shipmentId=${shipmentId}</@ofbizUrl>">Third Party Invoice</a>
            </li>
        </#if>
        <#if (shipment.shipmentTypeId)?exists && shipment.shipmentTypeId = "SALES_SHIPMENT">
            <#if planType = "null">
                <li<#if selected="EditShipmentPlan"> class="selected"</#if>><a href="<@ofbizUrl>EditShipmentPlan?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductShipmentPlan}</a></li>
            </#if>
            <#if planType = "STANDARD">
                <li<#if selected="EditShipmentPlan"> class="selected"</#if>><a href="<@ofbizUrl>EditShipmentPlan?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductShipmentPlan}</a></li>
            </#if>
            <#if planType = "ROUTE">
                <li<#if selected="EditShipmentPlan"> class="selected"</#if>><a href="<@ofbizUrl>FindOrdersFromRoute?shipmentId=${shipmentId}<#if routeId?exists>&routeId=${routeId}</#if></@ofbizUrl>">${uiLabelMap.ProductShipmentPlan}</a></li>
            </#if>
            <#if "SHIPMENT_PICKED" = shipment.statusId>
                <li<#if selected="PackShipmentPlan"> class="selected"</#if>><a href="<@ofbizUrl>PackOrderShipment?facilityId=${shipment.originFacilityId}&amp;mode=Manual&amp;shipmentId=${shipmentId}</@ofbizUrl>">Pack</a></li>
            </#if>

            <li<#if selected="AddItemsFromOrder"> class="selected"</#if>><a href="<@ofbizUrl>AddItemsFromOrder?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductOrderItems}</a></li>
            <li<#if selected="EditShipmentItems"> class="selected"</#if>><a href="<@ofbizUrl>EditShipmentItems?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductItems}</a></li>
            <#--<li<#if selected="EditShipmentPackages"> class="selected"</#if>><a href="<@ofbizUrl>EditShipmentPackages?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductPackages}</a></li>
            <li<#if selected="EditShipmentRouteSegments"> class="selected"</#if>><a href="<@ofbizUrl>EditShipmentRouteSegments?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductRouteSegments}</a></li>-->
        </#if>
        <#if shipment?exists && shipment.shipmentTypeId?exists && shipment.shipmentTypeId='TRANSFER'>
            <li<#if selected="EditShipmentItems"> class="selected"</#if>><a href="<@ofbizUrl>EditTransferShipmentItems?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductItems}</a></li>
            <li>
              <a href="<@ofbizUrl>EditShipmentPackages?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductPackages}</a>
            </li>
            <li>
              <a href="<@ofbizUrl>EditShipmentRouteSegments?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductRouteSegments}</a>
            </li>
            <li>
              <a href="<@ofbizUrl>EditThirdPartyInvoice?shipmentId=${shipmentId}</@ofbizUrl>">Third Party Invoice</a>
            </li>
        </#if> <#-- probably a sales shipment -->
        <#if (shipment.shipmentTypeId)?exists && shipment.shipmentTypeId='PURCHASE_SHIPMENT'>
            <li<#if selected="EditShipmentPlan"> class="selected"</#if>><a href="<@ofbizUrl>EditShipmentPlan?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductOrderItems}</a></li>
            <#if shipment.statusId=='PURCH_SHIP_RECEIVED'>
                <li<#if selected="ViewShipmentReceipts"> class="selected"</#if>><a href="<@ofbizUrl>ViewShipmentReceipts?shipmentId=${shipmentId}</@ofbizUrl>">${uiLabelMap.ProductShipmentReceipts}</a></li>
            </#if>
            <#if shipment.statusId=='PURCH_SHIP_SHIPPED'>
                <li><a href="<@ofbizUrl>ReceiveInventory?shipmentId=${shipmentId}&facilityId=${shipment.destinationFacilityId}&purchaseOrderId=${shipment.primaryOrderId}&initialSelected=Y</@ofbizUrl>">${uiLabelMap.ProductReceiveInventoryAgainstPO}</a></li>
            </#if>
            <li>
              <a href="<@ofbizUrl>EditThirdPartyInvoice?shipmentId=${shipmentId}</@ofbizUrl>">Third Party Invoice</a>
            </li>
            <#if shipment.statusId=='PURCH_SHIP_RECEIVED' && isGeneralManager?has_content>
                <li>
                  <a href="<@ofbizUrl>GMApproval?shipmentId=${shipmentId}&facilityId=${shipment.destinationFacilityId}&purchaseOrderId=${shipment.primaryOrderId}</@ofbizUrl>">Approval</a>
                </li>
            </#if>
            <#if shipment.statusId=='PURCH_SHIP_RECEIVED'>
                <li>
                  <a target="_blank" href="<@ofbizUrl>ReceivedMaterialInspectionReport.pdf?shipmentId=${shipmentId}</@ofbizUrl>">Print Inward Material Inspection Note</a>
                </li>
            </#if>
        </#if>
    </ul>
    <br />
</#if>
