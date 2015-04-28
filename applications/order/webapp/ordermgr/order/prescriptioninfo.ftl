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

<#if orderRxHeader?has_content>
<script>


</script>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">&nbsp;Patient Information</li>
            <li class="collapsed"><a onclick="javascript:toggleScreenlet(this, 'PatientInfoScreenletBody_${orderId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Expand">&nbsp;</a></li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body" id="PatientInfoScreenletBody_${orderId}" style="display: none;">
        <table class="basic-table" cellspacing='0'>
            <tr>
                <td align="right" valign="top" width="10%"><span class="label">First Name</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="40%">${orderRxHeader.firstName?if_exists}</td>
                <td align="right" valign="top" width="10%"><span class="label">Last Name</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="40%">${orderRxHeader.thirdName?if_exists}</td>
            </tr>
            <tr>
                <td align="right" valign="top" width="19%"><span class="label">Mobile</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="80%">${orderRxHeader.mobileNumber?if_exists}</td>
            </tr>
            <tr>
                <td align="right" valign="top" width="19%"><span class="label">Visit ID</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="40%">${orderRxHeader.visitId?if_exists}</td>
                <td align="right" valign="top" width="19%"><span class="label">Visit Date</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="40%">${orderRxHeader.visitDate?if_exists}</td>
            </tr>
            <tr>
                <td align="right" valign="top" width="19%"><span class="label">Afya ID</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="80%">${orderRxHeader.afyaId?if_exists}</td>
            </tr>
            <tr>
                <td align="right" valign="top" width="19%"><span class="label">Clinic Name</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="40%">${orderRxHeader.clinicName?if_exists}</td>
                <td align="right" valign="top" width="19%"><span class="label">Doctor Name</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="40%">${orderRxHeader.doctorName?if_exists}</td>
            </tr>
            <tr>
                <td align="right" valign="top" width="19%"><span class="label">Patient Type</span></td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="80%">${orderRxHeader.patientType?if_exists}</td>
            </tr>
            <#if benefitPlanName?has_content>
                <tr>
                    <td align="right" valign="top" width="19%"><span class="label">Benefit Plan</span></td>
                    <td width="1%"><input type="hidden" value="${benefitPlanId}" id="benefitPlanId"/></td>
                    <td valign="top" width="40%">${benefitPlanName?if_exists}</td>
                </tr>
                <tr>
                    <td align="right" valign="top" width="19%"><span class="label">Module Name</span></td>
                    <td width="1%"><input type="hidden" value="${benefitPlanId}" id="benefitPlanId"/></td>
                    <td valign="top" width="40%">${orderRxHeader.moduleName?if_exists}</td>
                </tr>
            </#if>
        </table>
    </div>
</div>
</#if>

<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">&nbsp;${uiLabelMap.OrderActions}</li>
            <li class="expanded"><a onclick="javascript:toggleScreenlet(this, 'ActionsScreenletBody_${orderId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Collapse">&nbsp;</a></li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body" id="ActionsScreenletBody_${orderId}">
        <ul class="tab-bar">
        <#if security.hasEntityPermission("FACILITY", "_CREATE", session) && ((orderHeader.statusId == "ORDER_APPROVED") || (orderHeader.statusId == "ORDER_SENT"))>
        <#-- Special shipment options -->
            <#if orderHeader.orderTypeId == "SALES_ORDER">
            <li>
                <form name="quickShipOrder" method="post" action="<@ofbizUrl>quickShipOrder</@ofbizUrl>">
                    <input type="hidden" name="orderId" value="${orderId}"/>
                </form>
                <#assign productStore =  delegator.findByPrimaryKey("ProductStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", orderHeader.productStoreId))?if_exists />
                <#if productStore?has_content && productStore.reserveInventory == "Y">
                    <a href="javascript:document.quickShipOrder.submit()" class="btn  btn-link btn-mini">${uiLabelMap.OrderQuickShipEntireOrder}</a></li>
                </#if>
            <#else> <#-- PURCHASE_ORDER -->
                <span class="label">&nbsp;<#if orderHeader.orderTypeId == "PURCHASE_ORDER">${uiLabelMap.ProductDestinationFacility}</#if></span>
                <#if ownedFacilities?has_content>
                    <#if "N" == orderItemShipGroup.maySplit>
                        <#if !allShipments?has_content>
                            <form action="/facility/control/quickShipPurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
                                <input type="hidden" name="initialSelected" value="Y"/>
                                <input type="hidden" name="orderId" value="${orderId}"/>
                            <#-- destination form (/facility/control/ReceiveInventory) wants purchaseOrderId instead of orderId, so we set it here as a workaround -->
                                <input type="hidden" name="purchaseOrderId" value="${orderId}"/>
                                <select name="facilityId">
                                    <#list ownedFacilities as facility>
                                        <option value="${facility.facilityId}">${facility.facilityName}</option>
                                    </#list>
                                </select>
                                <input type="submit" class="btn" value="${uiLabelMap.OrderQuickReceivePurchaseOrder}"/>
                            </form>
                            <li>
                                <form name="receivePurchaseOrderForm" action="/facility/control/quickShipPurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
                                    <input type="hidden" name="initialSelected" value="Y"/>
                                    <input type="hidden" name="orderId" value="${orderId}"/>
                                    <input type="hidden" name="purchaseOrderId" value="${orderId}"/>
                                    <input type="hidden" name="partialReceive" value="Y"/>
                                    <select name="facilityId">
                                        <#list ownedFacilities as facility>
                                            <option value="${facility.facilityId}">${facility.facilityName}</option>
                                        </#list>
                                    </select>
                                </form>
                                <a href="javascript:document.receivePurchaseOrderForm.submit()" class="btn  btn-link btn-mini">${uiLabelMap.CommonReceive}</a>
                            </li>
                        <#else>
                            <li>
                                <form name="receiveInventoryForm" action="/facility/control/ReceiveInventory" method="post">
                                    <input type="hidden" name="initialSelected" value="Y"/>
                                    <input type="hidden" name="purchaseOrderId" value="${orderId?if_exists}"/>
                                    <select name="facilityId">
                                        <#list ownedFacilities as facility>
                                            <option value="${facility.facilityId}">${facility.facilityName}</option>
                                        </#list>
                                    </select>
                                </form>
                                <a href="javascript:document.receiveInventoryForm.submit()" class="btn btn-success">${uiLabelMap.OrderQuickReceivePurchaseOrder}</a>
                            </li>
                            <li>
                                <form name="partialReceiveInventoryForm" action="/facility/control/ReceiveInventory" method="post">
                                    <input type="hidden" name="initialSelected" value="Y"/>
                                    <input type="hidden" name="purchaseOrderId" value="${orderId?if_exists}"/>
                                    <input type="hidden" name="partialReceive" value="Y"/>
                                    <select name="facilityId">
                                        <#list ownedFacilities as facility>
                                            <option value="${facility.facilityId}">${facility.facilityName}</option>
                                        </#list>
                                    </select>
                                </form>
                                <a href="javascript:document.partialReceiveInventoryForm.submit()" class="btn  btn-link btn-mini">${uiLabelMap.CommonReceive}</a>
                            </li>
                        </#if>
                    </#if>
                    <#if orderHeader.statusId != "ORDER_COMPLETED">
                        <form action="<@ofbizUrl>completePurchaseOrder?externalLoginKey=${externalLoginKey}</@ofbizUrl>" method="post">
                            <input type="hidden" name="orderId" value="${orderId}"/>
                            <select name="facilityId">
                                <#list ownedFacilities as facility>
                                    <option value="${facility.facilityId}">${facility.facilityName}</option>
                                </#list>
                            </select>
                            <input type="submit" class="btn btn-success" value="${uiLabelMap.OrderForceCompletePurchaseOrder}"/>
                        </form>
                    </#if>
                </#if>
            </#if>
        </#if>
        <#-- Refunds/Returns for Sales Orders and Delivery Schedules -->
        <#if orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED">
            <!-- <li><a href="<@ofbizUrl>OrderDeliveryScheduleInfo?orderId=${orderId}</@ofbizUrl>" class="btn">${uiLabelMap.OrderViewEditDeliveryScheduleInfo}</a></li> -->
        </#if>
        <#if security.hasEntityPermission("ORDERMGR", "_RETURN", session) && orderHeader.statusId == "ORDER_COMPLETED">
            <#if returnableItems?has_content>
                <li>
                    <form name="quickRefundOrder" method="post" action="<@ofbizUrl>quickRefundOrder</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId}"/>
                        <input type="hidden" name="receiveReturn" value="true"/>
                        <input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
                    </form>
                    <a href="javascript:document.quickRefundOrder.submit()" class="btn btn-mini btn-link">${uiLabelMap.OrderQuickRefundEntireOrder}</a>
                </li>
                <li>
                    <form name="quickreturn" method="post" action="<@ofbizUrl>quickreturn</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId}"/>
                        <input type="hidden" name="party_id" value="${partyId?if_exists}"/>
                        <input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
                        <input type="hidden" name="needsInventoryReceive" value="${needsInventoryReceive?default("N")}"/>
                    </form>
                    <a href="javascript:document.quickreturn.submit()" class="btn btn-mini btn-link">${uiLabelMap.OrderCreateReturn}</a>
                </li>
            </#if>
        </#if>
        <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED">
            <#if orderHeader.statusId != "ORDER_COMPLETED">
                <li><a href="<@ofbizUrl>cancelOrderItem?${paramString}</@ofbizUrl>" class="btn btn-danger btn-mini btn-link">${uiLabelMap.OrderCancelAllItems}</a></li>
                <li><a href="<@ofbizUrl>editOrderItems?${paramString}</@ofbizUrl>" class="btn btn-mini btn-link">${uiLabelMap.OrderEditItems}</a></li>
            </#if>
            <#--<li><a href="<@ofbizUrl>loadCartFromOrder?${paramString}&amp;finalizeMode=init</@ofbizUrl>" class="btn btn-mini btn-link">${uiLabelMap.OrderCreateAsNewOrder}</a></li>-->
        <#-- <#if orderHeader.statusId == "ORDER_COMPLETED">
           <li><a href="<@ofbizUrl>loadCartForReplacementOrder?${paramString}</@ofbizUrl>" class="btn btn-mini btn-link">${uiLabelMap.OrderCreateReplacementOrder}</a></li>
         </#if>-->
        </#if>
            <li><a href="<@ofbizUrl>OrderHistory?orderId=${orderId}</@ofbizUrl>" class="btn btn-mini btn-link">${uiLabelMap.OrderViewOrderHistory}</a></li>
        </ul>
    </div>
</div>
