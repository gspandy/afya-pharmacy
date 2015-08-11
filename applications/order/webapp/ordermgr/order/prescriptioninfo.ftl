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
                <td width="19%"><span class="label">First Name</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.firstName?if_exists}</td>
                <td width="19%"><span class="label">Last Name</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.thirdName?if_exists}</td>
            </tr>
            <#if orderRxHeader?has_content && (!orderRxHeader.mobileNumberVisibleForDelivery?exists || orderRxHeader.mobileNumberVisibleForDelivery == "true")>
                <tr>
                    <td width="19%"><span class="label">Mobile</span></td>
                    <td width="1%">&nbsp;</td>
                    <td width="30%">${orderRxHeader.mobileNumber?if_exists}</td>
                </tr>
            </#if>
            <tr>
                <td width="19%"><span class="label">Visit ID</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.visitId?if_exists}</td>
                <td width="19%"><span class="label">Visit Date</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%"><#if orderRxHeader.visitDate?has_content>${orderRxHeader.visitDate?if_exists?string("dd/MM/yyyy")}</#if></td>
            </tr>
            <tr>
                <td width="19%"><span class="label">Afya ID</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.afyaId?if_exists}</td>
            </tr>
            <tr>
                <td width="19%"><span class="label">Clinic Name</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.clinicName?if_exists}</td>
                <td width="19%"><span class="label">Doctor Name</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.doctorName?if_exists}</td>
            </tr>
            <tr>
                <td width="19%"><span class="label">Patient Type</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.patientType?if_exists}</td>
                <#if orderRxHeader.patientType?exists && "CORPORATE" == orderRxHeader.patientType && orderRxHeader.primaryPayer?exists>
                    <td width="19%"><span class="label">Primary Payer</span></td>
                    <td width="1%">&nbsp;</td>
                    <td width="30%">${orderRxHeader.primaryPayer?if_exists}</td>
                </#if>
            </tr>
            <#if orderRxHeader.patientType?exists && "CORPORATE" == orderRxHeader.patientType && orderRxHeader.primaryPayer?exists && "Corporate" == orderRxHeader.primaryPayer && orderRxHeader.copay?exists>
                <td width="19%"><span class="label">Copay</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.copay?if_exists}</td>
                <td width="19%"><span class="label">Copay Type</span></td>
                <td width="1%">&nbsp;</td>
                <td width="30%">${orderRxHeader.copayType?if_exists}</td>
            </#if>
            <#if benefitPlanName?has_content && orderRxHeader.patientType?exists && "INSURANCE" == orderRxHeader.patientType>
                <tr>
                    <td width="19%"><span class="label">Patient Insurance</span></td>
                    <td width="1%">&nbsp;</td>
                    <td width="30%">${healthPolicyName?if_exists} - ${policyNo?if_exists}</td>
                    <td width="19%"><a href='javascript:void(0);' id="benefitPlanLink">View Plan Details</a></td>
                    <td width="1%"><input type="hidden" value="${benefitPlanId}" id="benefitPlanId"/></td>
                    <td width="30%">&nbsp;</td>
                </tr>
                <tr>
                    <td width="19%"><span class="label">Benefit</span></td>
                    <td width="1%"><input type="hidden" value="${benefitPlanId}" id="benefitPlanId"/></td>
                    <td width="30%">${orderRxHeader.moduleName?if_exists}</td>
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
                <#assign totalDeductible = Static["java.math.BigDecimal"].ZERO>
                <#assign totalCopayPatient = Static["java.math.BigDecimal"].ZERO>
                <#assign totalCopayInsurance = Static["java.math.BigDecimal"].ZERO>
                <#assign totalCopayCorporate = Static["java.math.BigDecimal"].ZERO>
                <#list orderItemList as orderItem>
                    <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>
                    <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">
                        <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                        <#assign productId = orderItem.productId?if_exists>
                        <!-- adjustment details per line item -->
                        <div>
                          <#assign lineItemAdjustmentTotal = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                          <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                            <#list orderItemAdjustments as orderItemAdjustment>
                              <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                              <#assign lineItemAdjustmentTotal = lineItemAdjustmentTotal + lineItemAdjustment>
                            </#list>
                          </#if>
                          <#-- Adj: <@ofbizCurrency amount=lineItemAdjustmentTotal isoCode=currencyUomId/> -->
                        </div>
                        <#if orderRxHeader?has_content && "INSURANCE"==orderRxHeader.patientType>
                          <div>
                              <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>

                              <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                              <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                                <#list orderLineItemAdjustments as orderLineItemAdjustment>
                                  <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                                </#list>
                              </#if>

                              <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                              <#if orderItemAdjustmentsApportion?has_content>
                                  <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                      <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                                  </#list>

                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              <#else>
                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              </#if>

                              <#if orderItem.authorized == "Y">
                                  <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                  <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>

                                  <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                      <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                      <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                          <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#assign deductible = deductibleAmount + copayPatient>
                                          <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) + deductible>
                                              <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                          <#elseif deductible lt deductibleAmount>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                          <#else>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) - deductible>
                                          </#if>
                                      <#else>
                                          <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                      </#if>
                                  <#else>
                                      <#assign copayInsurance = netAmount - (copayPatient + itemDeductible)>
                                  </#if>
                                  <#if orderItem.authorizationAmount?exists>
                                      <#if copayPatient == Static["java.math.BigDecimal"].ZERO>
                                          <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount - deductibleAmount>
                                      <#else>
                                          <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount>
                                      </#if>
                                  </#if>
                                  <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                      <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                      <#-- CP1: <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                      <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                                  <#else>
                                      <#-- CP2: <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                      <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                                  </#if>
                              <#else>
                                  <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                  <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>

                                  <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                      <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                      <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                          <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#assign deductible = deductibleAmount + copayPatient>
                                          <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                          <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) + deductible>
                                          <#else>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                          </#if>
                                      <#else>
                                          <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                      </#if>
                                  <#else>
                                      <#assign copayInsurance = netAmount - (copayPatient + itemDeductible)>
                                  </#if>
                                  <#assign copayPatient = copayPatient + copayInsurance>
                                  <#-- CP3: <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                  <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                              </#if>
                          </div>
                          <div>
                              <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>

                              <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                              <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                                <#list orderLineItemAdjustments as orderLineItemAdjustment>
                                  <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                                </#list>
                              </#if>

                              <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                              <#if orderItemAdjustmentsApportion?has_content>
                                  <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                      <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                                  </#list>

                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              <#else>
                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              </#if>

                              <#if orderItem.authorized == "Y">
                                  <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                  <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                      <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign copayInsurance = grossAmount - (copayPatient + itemDeductible)>
                                      <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                  <#else>
                                      <#assign copayInsurance = netAmount - (copayPatient + itemDeductible)>
                                  </#if>
                                  <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#if orderItem.authorizationAmount?exists>
                                      <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount>
                                      <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                          <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#assign deductible = deductibleAmount + copayPatient>
                                          <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                              <#assign copayInsurance = orderItem.authorizationAmount?default(Static["java.math.BigDecimal"].ZERO) + deductible>
                                              <#-- CI1: <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/> -->
                                              <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                          <#else>
                                              <#assign copayInsurance = orderItem.authorizationAmount?default(Static["java.math.BigDecimal"].ZERO)>
                                              <#-- CI2: <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/> -->
                                              <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                          </#if>
                                      <#else>
                                          <#assign copayInsurance = orderItem.authorizationAmount?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#-- CI3: <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/> -->
                                          <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                      </#if>
                                  <#else>
                                      <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                          <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                              <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                              <#assign deductible = deductibleAmount + copayPatient>
                                              <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                                  <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) + deductible>
                                                  <#-- CI4: <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/> -->
                                                  <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                              <#else>
                                                  <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                                  <#-- CI5: <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/> -->
                                                  <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                              </#if>
                                          <#else>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                              <#-- CI6: <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/> -->
                                              <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                          </#if>
                                      <#else>
                                          <#assign copayInsurance = netAmount - (patientCopay + itemDeductible)>
                                          <#-- CI7: <@ofbizCurrency amount=copayInsurance?default(Static["java.math.BigDecimal"].ZERO) isoCode=currencyUomId/> -->
                                          <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                      </#if>
                                  </#if>
                              <#else>
                                  <#assign copayInsurance = Static["java.math.BigDecimal"].ZERO>
                                  <#-- CI8: <@ofbizCurrency amount=copayInsurance?default(Static["java.math.BigDecimal"].ZERO) isoCode=currencyUomId/> -->
                                  <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                              </#if>
                          </div>
                          <div>
                              <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>

                              <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                              <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                                <#list orderLineItemAdjustments as orderLineItemAdjustment>
                                  <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                                </#list>
                              </#if>

                              <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                              <#if orderItemAdjustmentsApportion?has_content>
                                  <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                      <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                                  </#list>

                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              <#else>
                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              </#if>

                              <#if orderItem.authorized == "Y">
                                  <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                  <#assign copay = Static["java.math.BigDecimal"].ZERO>
                                  <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                      <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>

                                      <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                          <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#assign deductible = deductibleAmount + copayPatient>
                                          <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) + deductible>
                                              <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                          <#elseif deductible lt deductibleAmount>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                          <#else>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) - deductible>
                                          </#if>
                                      <#else>
                                          <#assign copayInsurance = grossAmount - (copayPatient + itemDeductible)>
                                      </#if>
                                  <#else>
                                      <#assign copayInsurance = netAmount - (copayPatient + itemDeductible)>
                                  </#if>
                                  <#if orderItem.authorizationAmount?exists>
                                      <#if copayPatient == Static["java.math.BigDecimal"].ZERO>
                                          <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount - deductibleAmount>
                                      <#else>
                                          <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount>
                                      </#if>
                                  </#if>
                                  <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign deductible = deductibleAmount + copayPatient>
                                      <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                          <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                          <#-- DD1: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                          <#assign totalDeductible = totalDeductible + deductible>
                                      <#else>
                                          <#-- DD2: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                          <#assign totalDeductible = totalDeductible + deductible>
                                      </#if>
                                  <#else>
                                      <#assign deductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#-- DD3: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                      <#assign totalDeductible = totalDeductible + deductible>
                                  </#if>
                              <#else>
                                  <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                  <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>

                                  <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                      <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                      <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                          <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#assign deductible = deductibleAmount + copayPatient>
                                          <#assign copay = Static["java.math.BigDecimal"].ZERO>
                                          <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                              <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                              <#-- DD4: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                              <#assign totalDeductible = totalDeductible + deductible>
                                          <#else>
                                              <#-- DD5: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                              <#assign totalDeductible = totalDeductible + deductible>
                                          </#if>
                                      <#else>
                                          <#-- DD6: <@ofbizCurrency amount=itemDeductible isoCode=currencyUomId/> -->
                                          <#assign totalDeductible = totalDeductible + itemDeductible>
                                      </#if>
                                  <#else>
                                      <#assign copayPatient = netAmount - itemDeductible>
                                      <#if copayPatient lt Static["java.math.BigDecimal"].ZERO>
                                          <#-- <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(orderItem)?default(Static["java.math.BigDecimal"].ZERO)> -->
                                          <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#assign deductible = deductibleAmount + copayPatient>
                                          <#if deductible lt Static["java.math.BigDecimal"].ZERO>
                                              <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                              <#-- DD7: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                              <#assign totalDeductible = totalDeductible + deductible>
                                          <#else>
                                              <#-- DD8: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                              <#assign totalDeductible = totalDeductible + deductible>
                                          </#if>
                                      <#else>
                                          <#-- <#assign deductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(orderItem)?default(Static["java.math.BigDecimal"].ZERO)> -->
                                          <#assign deductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                          <#-- DD9: <@ofbizCurrency amount=deductible isoCode=currencyUomId/> -->
                                          <#assign totalDeductible = totalDeductible + deductible>
                                      </#if>
                                  </#if>
                              </#if>
                          </div>
                        <#elseif orderRxHeader?has_content && "CORPORATE"==orderRxHeader.patientType>
                          <#assign primaryPayer = orderRxHeader.primaryPayer?if_exists>
                          <div>
                              <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                              <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>

                              <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                              <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                                <#list orderLineItemAdjustments as orderLineItemAdjustment>
                                  <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                                </#list>
                              </#if>

                              <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                              <#if orderItemAdjustmentsApportion?has_content>
                                  <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                      <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                                  </#list>

                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              <#else>
                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                      <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                      <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                                  <#else>
                                      <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                  </#if>
                              </#if>

                              <#-- <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(orderItem)?default(Static["java.math.BigDecimal"].ZERO)> -->
                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCorporateCopay(primaryPayer, netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                              <#-- PP1: <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                          </div>
                          <div>
                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCorporateCopay(primaryPayer, netAmount, orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                              <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(Static["java.math.BigDecimal"].ZERO)>
                                  <#assign copayCorporate = grossAmount - copayPatient>
                              <#else>
                                  <#assign copayCorporate = netAmount - copayPatient>
                              </#if>
                              <#-- PP2: <@ofbizCurrency amount=copayCorporate?default(Static["java.math.BigDecimal"].ZERO) isoCode=currencyUomId/> -->
                              <#assign totalCopayCorporate = totalCopayCorporate + copayCorporate>
                          </div>
                        <#else>
                          <div>
                              <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                              <#assign patientPayable = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                              
                              <#assign orderItemApportionAdjustments = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                              <#if orderItemApportionAdjustments?has_content>
                                  <#list orderItemApportionAdjustments as orderItemApportionAdjustment>
                                      <#assign orderItemAdjAmt = orderItemAdjAmt + orderItemApportionAdjustment.amount/>
                                  </#list>
                                  
                                  <#assign copayPatient = patientPayable + lineItemAdjustmentTotal + orderItemAdjAmt>
                                  <#-- PP3: <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                  <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                              <#else>
                                  <#assign copayPatient = patientPayable + lineItemAdjustmentTotal + orderItemAdjAmt>
                                  <#-- PP4: <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                  <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                              </#if>
                          </div>
                        </#if>
                        <div>
                            <#assign subTotal = Static["java.math.BigDecimal"].ZERO>
                            <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                            
                            <#assign orderItemApportionAdjustments = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                            <#if orderItemApportionAdjustments?has_content>
                                <#list orderItemApportionAdjustments as orderItemApportionAdjustment>
                                    <#assign orderItemAdjAmt = orderItemAdjAmt + orderItemApportionAdjustment.amount/>
                                </#list>
                                
                                <#if orderItem.statusId != "ITEM_CANCELLED">
                                    <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                    <#assign subTotal = lineItemSubTotal + lineItemAdjustmentTotal + orderItemAdjAmt>
                                    <#-- ST1: <@ofbizCurrency amount=subTotal isoCode=currencyUomId/> -->
                                <#else>
                                    <#assign subTotal = Static["java.math.BigDecimal"].ZERO>
                                    <#-- ST2: <@ofbizCurrency amount=Static["java.math.BigDecimal"].ZERO isoCode=currencyUomId/> -->
                                </#if>
                            <#else>
                                <#if orderItem.statusId != "ITEM_CANCELLED">
                                    <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(Static["java.math.BigDecimal"].ZERO)>
                                    <#assign subTotal = lineItemSubTotal + lineItemAdjustmentTotal>
                                    <#-- ST3: <@ofbizCurrency amount=subTotal isoCode=currencyUomId/> -->
                                <#else>
                                    <#assign subTotal = Static["java.math.BigDecimal"].ZERO>
                                    <#-- ST4: <@ofbizCurrency amount=Static["java.math.BigDecimal"].ZERO isoCode=currencyUomId/> -->
                                </#if>
                            </#if>
                        </div>
                    </#if>
                </#list>

                <div>
                    <#if orderRxHeader?exists && orderRxHeader.patientType?exists && 'INSURANCE'==orderRxHeader.patientType>
                        <#-- totAmtPayableByPatient (Total Patient Payable) -->
                        <#assign totAmtPayableByPatient = totalCopayPatient + totalDeductible />
                        <#-- TPP1: <@ofbizCurrency amount=totAmtPayableByPatient isoCode=currencyUomId/> -->
                    <#else>
                        <#-- totAmtPayableByPatient (Total Patient Payable) -->
                        <#assign totAmtPayableByPatient = totalCopayPatient />
                        <#-- TPP2: <@ofbizCurrency amount=totAmtPayableByPatient isoCode=currencyUomId/> -->
                    </#if>

                    <#if orderRxHeader?exists && orderRxHeader.patientType?exists && 'CORPORATE'==orderRxHeader.patientType>
                        <#-- totAmtPayableByCorporate (Total Corporate Payable) -->
                        <#assign totAmtPayableByCorporate = totalCopayCorporate />
                        <#-- TCP1: <@ofbizCurrency amount=totAmtPayableByCorporate isoCode=currencyUomId/> -->
                    </#if>

                    <#if orderRxHeader?exists && orderRxHeader.patientType?exists && 'INSURANCE'==orderRxHeader.patientType>
                        <#-- totAmtPayableByInsurance (Total Insurance Payable) -->
                        <#assign totAmtPayableByInsurance = totalCopayInsurance />
                        <#-- TIP1: <@ofbizCurrency amount=totAmtPayableByInsurance isoCode=currencyUomId/> -->
                    </#if>
                </div>

                <li>
                    <form name="quickShipOrder" method="post" action="<@ofbizUrl>quickShipOrder</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId}"/>
                        <#if orderRxHeader?exists && orderRxHeader.patientType?exists && 'INSURANCE'==orderRxHeader.patientType>
                            <input type="hidden" name="patientPayable" value="${totAmtPayableByPatient}"/>
                            <input type="hidden" name="insurancePayable" value="${totAmtPayableByInsurance}"/>
                        <#elseif orderRxHeader?exists && orderRxHeader.patientType?exists && 'CORPORATE'==orderRxHeader.patientType>
                            <input type="hidden" name="patientPayable" value="${totAmtPayableByPatient}"/>
                            <input type="hidden" name="corporatePayable" value="${totAmtPayableByCorporate}"/>
                        <#elseif orderRxHeader?exists>
                            <input type="hidden" name="patientPayable" value="${totAmtPayableByPatient}"/>
                        </#if>
                    </form>
                    <#assign productStore =  delegator.findByPrimaryKey("ProductStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", orderHeader.productStoreId))?if_exists />
                    <#if productStore?has_content && productStore.reserveInventory == "Y">
                        <a href="javascript:document.quickShipOrder.submit()" class="btn btn-link">${uiLabelMap.OrderQuickShipEntireOrder}</a>
                    </#if>
                </li>
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
                                <a href="javascript:document.receivePurchaseOrderForm.submit()" class="btn btn-link">${uiLabelMap.CommonReceive}</a>
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
                                <a href="javascript:document.partialReceiveInventoryForm.submit()" class="btn btn-link">${uiLabelMap.CommonReceive}</a>
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
                    <a href="javascript:document.quickRefundOrder.submit()" class="btn btn-link">${uiLabelMap.OrderQuickRefundEntireOrder}</a>
                </li>
                <li>
                    <form name="quickreturn" method="post" action="<@ofbizUrl>quickreturn</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId}"/>
                        <input type="hidden" name="party_id" value="${partyId?if_exists}"/>
                        <input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
                        <input type="hidden" name="needsInventoryReceive" value="${needsInventoryReceive?default("N")}"/>
                    </form>
                    <a href="javascript:document.quickreturn.submit()" class="btn btn-link">${uiLabelMap.OrderCreateReturn}</a>
                </li>
            </#if>
        </#if>
        <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED">
            <#if orderHeader.statusId != "ORDER_COMPLETED">
                <li><a href="<@ofbizUrl>cancelOrderItem?${paramString}</@ofbizUrl>" class="btn btn-danger btn-link">${uiLabelMap.OrderCancelAllItems}</a></li>
                <li><a href="<@ofbizUrl>editOrderItems?${paramString}</@ofbizUrl>" class="btn btn-link">${uiLabelMap.OrderEditItems}</a></li>
            </#if>
            <#--<li><a href="<@ofbizUrl>loadCartFromOrder?${paramString}&amp;finalizeMode=init</@ofbizUrl>" class="btn btn-link">${uiLabelMap.OrderCreateAsNewOrder}</a></li>-->
        <#-- <#if orderHeader.statusId == "ORDER_COMPLETED">
           <li><a href="<@ofbizUrl>loadCartForReplacementOrder?${paramString}</@ofbizUrl>" class="btn btn-link">${uiLabelMap.OrderCreateReplacementOrder}</a></li>
         </#if>-->
        </#if>
            <li><a href="<@ofbizUrl>OrderHistory?orderId=${orderId}</@ofbizUrl>" class="btn btn-link">${uiLabelMap.OrderViewOrderHistory}</a></li>
        </ul>
    </div>
</div>
