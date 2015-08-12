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
<div class="screenlet">
  <h3>
      <#assign numColumns = 12>
      ${uiLabelMap.OrderOrderItems}
  </h3>
  <table>
    <thead>
      <tr>
        <th style="font-weight:bold;" width="25%">${uiLabelMap.OrderProduct}</th>
        <th style="font-weight:bold;">Home Service</td>
        <th style="font-weight:bold;">${uiLabelMap.OrderQuantity}</th>
        <th style="font-weight:bold;">${uiLabelMap.CommonUom}</th>
        <th style="font-weight:bold;text-align:right;padding-right:20px;">${uiLabelMap.OrderUnitPrice}</th>
        <th style="font-weight:bold;text-align:right;padding-right:10px;">${uiLabelMap.OrderAdjustments}</th>
        <th style="font-weight:bold;text-align:right;padding-right:10px;">Patient Payable</td>
        <#if orderRxHeader?exists && orderRxHeader.patientType?exists && 'INSURANCE'==orderRxHeader.patientType>
          <th style="font-weight:bold;text-align:right;padding-right:10px;">Insurance Payable</td>
          <th style="font-weight:bold;text-align:right;padding-right:10px;">Deductible</td>
        </#if>
        <#if orderRxHeader?exists && orderRxHeader.patientType?exists && 'CORPORATE'==orderRxHeader.patientType>
          <th style="font-weight:bold;text-align:right;padding-right:10px;">Corporate Payable</td>
        </#if>
        <th style="font-weight:bold;text-align:right;padding-right:20px;">${uiLabelMap.OrderSubTotal}</th>
        <th style="font-weight:bold;" colspan="2">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
        <#assign totalDeductible = Static["java.math.BigDecimal"].ZERO>
        <#assign totalCopayPatient = Static["java.math.BigDecimal"].ZERO>
        <#assign totalCopayInsurance = Static["java.math.BigDecimal"].ZERO>
        <#assign totalCopayCorporate = Static["java.math.BigDecimal"].ZERO>
        <#list orderItems as orderItem>
            <tr><td colspan="12"> &nbsp; &nbsp; &nbsp; </td></tr>
            <tr><td colspan="${numColumns}"></td></tr>
            <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>
            <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">
                <tr>
                    <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                    <#assign productId = orderItem.productId?if_exists>
                    <td valign="top" nowrap="nowrap">
                        <div class="order-item-description">
                            <#if productId?exists>
                                ${orderItem.itemDescription?if_exists}
                                <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
                                    <br />
                                    <span style="color: red;">${uiLabelMap.OrderItemDiscontinued}: ${product.salesDiscontinuationDate}</span>
                                </#if>
                            <#elseif orderItemType?exists>
                                ${orderItemType.description} - ${orderItem.itemDescription?if_exists}
                            <#else>
                                ${orderItem.itemDescription?if_exists}
                            </#if>
                            <#if  orderRxHeader?exists && orderRxHeader.patientType?exists && 'INSURANCE'==orderRxHeader.patientType>
                                <#if orderItem.authorized?exists && orderItem.authorized=="Y">
                                    <br/><span>(<label style="color: green;">Authorized</label>)</span>
                                <#else>
                                    <br/><span>(<label style="color: red;">Not Authorized</label>)</span>
                                </#if>
                            </#if>
                        </div>
                    </td>
                    <td valign="top" nowrap="nowrap">
                        <#if orderItem.homeService=='Y'> Yes <#else> No </#if>
                    </td>
                    <td valign="top" nowrap="nowrap">
                        <#assign shippedQuantity = orderReadHelper.getItemShippedQuantity(orderItem)>
                        <#assign outstanding = orderItem.quantity?default(0.000) - orderItem.cancelQuantity?default(0.000)>
                        <#if outstanding<shippedQuantity>
                            <#assign shippedQuantity = outstanding>
                        </#if>
                        <#assign remainingQuantity = ((orderItem.quantity?default(0.000) - orderItem.cancelQuantity?default(0.000)) - shippedQuantity?double)>
                        <#-- ${remainingQuantity} -->
                        ${orderItem.quantity?default(0.000)}
                    </td>
                    <td valign="top" nowrap="nowrap">
                        <#assign product = orderItem.getRelatedOneCache("Product")>
                        <#assign uomGv = delegator.findOne("Uom", {"uomId":product.quantityUomId}, false)>
                        <#if uomGv?has_content>
                            ${uomGv.description?if_exists}
                        </#if>
                    </td>
                    <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                        <@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
                    </td>
                    <#-- adjustment details per line item -->
                    <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                      <#assign lineItemAdjustmentTotal = Static["java.math.BigDecimal"].ZERO>
                      <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(0.000)>
                      <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                        <#list orderItemAdjustments as orderItemAdjustment>
                          <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem)?default(0.000)>
                          <#assign lineItemAdjustmentTotal = lineItemAdjustmentTotal + lineItemAdjustment>
                        </#list>
                      </#if>
                      <@ofbizCurrency amount=lineItemAdjustmentTotal isoCode=currencyUomId/>
                    </td>
                    <#if orderRxHeader?has_content && "INSURANCE"==orderRxHeader.patientType>
                      <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                          <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(0.000)>
                          <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                            <#list orderLineItemAdjustments as orderLineItemAdjustment>
                              <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(0.000)>
                              <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                            </#list>
                          </#if>
                          <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                          <#if orderItemAdjustmentsApportion?has_content>
                              <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                  <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                              </#list>
                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          <#else>
                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          </#if>
                          <#if orderItem.authorized == "Y">
                              <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                              <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                  <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                  <#if copayPatient lt 0>
                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                      <#assign deductible = deductibleAmount + copayPatient>
                                      <#if deductible lt 0>
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
                                  <#if copayPatient == 0>
                                      <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount - deductibleAmount>
                                  <#else>
                                      <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount>
                                  </#if>
                              </#if>
                              <#if copayPatient lt 0>
                                  <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                  <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/>
                                  <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                              <#else>
                                  <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/>
                                  <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                              </#if>
                          <#else>
                              <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                              <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                  <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                  <#if copayPatient lt 0>
                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                      <#assign deductible = deductibleAmount + copayPatient>
                                      <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                      <#if deductible lt 0>
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
                              <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/>
                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                          </#if>
                      </td>
                      <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                          <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>

                          <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(0.000)>
                          <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                            <#list orderLineItemAdjustments as orderLineItemAdjustment>
                              <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(0.000)>
                              <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                            </#list>
                          </#if>
                          <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                          <#if orderItemAdjustmentsApportion?has_content>
                              <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                  <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                              </#list>

                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          <#else>
                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          </#if>
                          <#if orderItem.authorized == "Y">
                              <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                              <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                  <#assign copayInsurance = grossAmount - (copayPatient + itemDeductible)>
                                  <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                              <#else>
                                  <#assign copayInsurance = netAmount - (copayPatient + itemDeductible)>
                              </#if>
                              <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#if orderItem.authorizationAmount?exists>
                                  <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount>
                                  <#if copayPatient lt 0>
                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                      <#assign deductible = deductibleAmount + copayPatient>
                                      <#if deductible lt 0>
                                          <#assign copayInsurance = orderItem.authorizationAmount?default(0.000) + deductible>
                                          <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/>
                                          <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                      <#else>
                                          <#assign copayInsurance = orderItem.authorizationAmount?default(0.000)>
                                          <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/>
                                          <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                      </#if>
                                  <#else>
                                      <#assign copayInsurance = orderItem.authorizationAmount?default(0.000)>
                                      <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/>
                                      <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                  </#if>
                              <#else>
                                  <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                      <#if copayPatient lt 0>
                                          <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                          <#assign deductible = deductibleAmount + copayPatient>
                                          <#if deductible lt 0>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) + deductible>
                                              <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/>
                                              <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                          <#else>
                                              <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                              <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/>
                                              <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                          </#if>
                                      <#else>
                                          <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                          <@ofbizCurrency amount=copayInsurance isoCode=currencyUomId/>
                                          <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                      </#if>
                                  <#else>
                                      <#assign copayInsurance = netAmount - (patientCopay + itemDeductible)>
                                      <@ofbizCurrency amount=copayInsurance?default(0.000) isoCode=currencyUomId/>
                                      <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                  </#if>
                              </#if>
                          <#else>
                              <#assign copayInsurance = 0.000>
                              <@ofbizCurrency amount=copayInsurance?default(0.000) isoCode=currencyUomId/>
                              <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                          </#if>
                      </td>
                      <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                          <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(0.000)>
                          <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                            <#list orderLineItemAdjustments as orderLineItemAdjustment>
                              <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(0.000)>
                              <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                            </#list>
                          </#if>
                          <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                          <#if orderItemAdjustmentsApportion?has_content>
                              <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                  <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                              </#list>
                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          <#else>
                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          </#if>
                          <#if orderItem.authorized == "Y">
                              <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                              <#assign copay = Static["java.math.BigDecimal"].ZERO>
                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                              <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                  <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                  <#if copayPatient lt 0>
                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                      <#assign deductible = deductibleAmount + copayPatient>
                                      <#if deductible lt 0>
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
                                  <#if copayPatient == 0>
                                      <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount - deductibleAmount>
                                  <#else>
                                      <#assign copayPatient = copayPatient + copayInsurance - orderItem.authorizationAmount>
                                  </#if>
                              </#if>
                              <#if copayPatient lt 0>
                                  <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                  <#assign deductible = deductibleAmount + copayPatient>
                                  <#if deductible lt 0>
                                      <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                      <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                      <#assign totalDeductible = totalDeductible + deductible>
                                  <#else>
                                      <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                      <#assign totalDeductible = totalDeductible + deductible>
                                  </#if>
                              <#else>
                                  <#assign deductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                  <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                  <#assign totalDeductible = totalDeductible + deductible>
                              </#if>
                          <#else>
                              <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                              <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                              <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                              <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                  <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                  <#if copayPatient lt 0>
                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                      <#assign deductible = deductibleAmount + copayPatient>
                                      <#assign copay = Static["java.math.BigDecimal"].ZERO>
                                      <#if deductible lt 0>
                                          <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                          <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                          <#assign totalDeductible = totalDeductible + deductible>
                                      <#else>
                                          <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                          <#assign totalDeductible = totalDeductible + deductible>
                                      </#if>
                                  <#else>
                                      <@ofbizCurrency amount=itemDeductible isoCode=currencyUomId/>
                                      <#assign totalDeductible = totalDeductible + itemDeductible>
                                  </#if>
                              <#else>
                                  <#assign copayPatient = netAmount - itemDeductible>
                                  <#if copayPatient lt 0>
                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                      <#assign deductible = deductibleAmount + copayPatient>
                                      <#if deductible lt 0>
                                          <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                          <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                          <#assign totalDeductible = totalDeductible + deductible>
                                      <#else>
                                          <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                          <#assign totalDeductible = totalDeductible + deductible>
                                      </#if>
                                  <#else>
                                      <#assign deductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                      <@ofbizCurrency amount=deductible isoCode=currencyUomId/>
                                      <#assign totalDeductible = totalDeductible + deductible>
                                  </#if>
                              </#if>
                          </#if>
                      </td>
                    <#elseif orderRxHeader?has_content && "CORPORATE"==orderRxHeader.patientType>
                      <#assign primaryPayer = orderRxHeader.primaryPayer?if_exists>
                      <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                          <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                          <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>
                          <#assign orderLineItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(0.000)>
                          <#if orderLineItemAdjustments?exists && orderLineItemAdjustments?has_content>
                            <#list orderLineItemAdjustments as orderLineItemAdjustment>
                              <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderLineItemAdjustment, orderItem)?default(0.000)>
                              <#assign lineItemAdjTot = lineItemAdjTot + lineItemAdjustment>
                            </#list>
                          </#if>
                          <#assign orderItemAdjustmentsApportion = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                          <#if orderItemAdjustmentsApportion?has_content>
                              <#list orderItemAdjustmentsApportion as orderItemAdjustmentApportion>
                                  <#assign orderItemAdjAmount = orderItemAdjAmount + orderItemAdjustmentApportion.amount/>
                              </#list>
                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot + orderItemAdjAmount>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          <#else>
                              <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                  <#assign netAmount = lineItemSubTotal + lineItemAdjTot>
                              <#else>
                                  <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                              </#if>
                          </#if>
                          <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCorporateCopay(primaryPayer, netAmount, orderItem)?default(0.000)>
                          <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/>
                          <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                      </td>
                      <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                          <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCorporateCopay(primaryPayer, netAmount, orderItem)?default(0.000)>
                          <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                              <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                              <#assign copayCorporate = grossAmount - copayPatient>
                          <#else>
                              <#assign copayCorporate = netAmount - copayPatient>
                          </#if>
                          <@ofbizCurrency amount=copayCorporate?default(0.000) isoCode=currencyUomId/>
                          <#assign totalCopayCorporate = totalCopayCorporate + copayCorporate>
                      </td>
                    <#else>
                      <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                          <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                          <#assign patientPayable = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                          <#assign orderItemApportionAdjustments = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                          <#if orderItemApportionAdjustments?has_content>
                              <#list orderItemApportionAdjustments as orderItemApportionAdjustment>
                                  <#assign orderItemAdjAmt = orderItemAdjAmt + orderItemApportionAdjustment.amount/>
                              </#list>
                              <#assign copayPatient = patientPayable + lineItemAdjustmentTotal + orderItemAdjAmt>
                              <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/>
                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                          <#else>
                              <#assign copayPatient = patientPayable + lineItemAdjustmentTotal + orderItemAdjAmt>
                              <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/>
                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                          </#if>
                      </td>
                    </#if>
                    <td style="text-align:right;padding-right:10px;" valign="top" nowrap="nowrap">
                        <#assign subTotal = Static["java.math.BigDecimal"].ZERO>
                        <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                        <#assign orderItemApportionAdjustments = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                        <#if orderItemApportionAdjustments?has_content>
                            <#list orderItemApportionAdjustments as orderItemApportionAdjustment>
                                <#assign orderItemAdjAmt = orderItemAdjAmt + orderItemApportionAdjustment.amount/>
                            </#list>
                            <#if orderItem.statusId != "ITEM_CANCELLED">
                                <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                <#assign subTotal = lineItemSubTotal + lineItemAdjustmentTotal + orderItemAdjAmt>
                                <@ofbizCurrency amount=subTotal isoCode=currencyUomId/>
                            <#else>
                                <@ofbizCurrency amount=0.000 isoCode=currencyUomId/>
                            </#if>
                        <#else>
                            <#if orderItem.statusId != "ITEM_CANCELLED">
                                <#assign lineItemSubTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                <#assign subTotal = lineItemSubTotal + lineItemAdjustmentTotal>
                                <@ofbizCurrency amount=subTotal isoCode=currencyUomId/>
                            <#else>
                                <@ofbizCurrency amount=0.000 isoCode=currencyUomId/>
                            </#if>
                        </#if>
                    </td>
                    <td colspan="3">&nbsp;</td>
                </tr>
            </#if>
        </#list>
        <#if orderItems?size == 0 || !orderItems?has_content>
            <tr><td colspan="${numColumns}">${uiLabelMap.OrderSalesOrderLookupFailed}</td></tr>
        </#if>
        <tr><td colspan="${numColumns}"></td></tr>
    </tbody>

    <tfoot>
      <tr><td colspan="${numColumns}"> &nbsp; &nbsp; &nbsp; </td></tr>
      <tr><td colspan="${numColumns}"> &nbsp; &nbsp; &nbsp; </td></tr>
      <#if orderRxHeader?exists && orderRxHeader.patientType?exists && 'INSURANCE'==orderRxHeader.patientType>
        <tr>
          <th colspan="8" style="font-weight:bold;text-align:right;padding-right:40px;">Items Sub Total</th>
          <#assign orderSubTotalExcludeTax = orderSubTotal - salesTax.amount?default(0)>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- <@ofbizCurrency amount=orderSubTotalExcludeTax isoCode=currencyUomId/> -->
            <@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
          </td>
        </tr>
        <tr><td colspan="${numColumns}"> &nbsp; &nbsp; &nbsp; </td></tr>
        <tr>
          <th colspan="8" style="font-weight:bold;text-align:right;padding-right:40px;">Patient Payable</th>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- totAmtPayableByPatient (Total Patient Payable) -->
            <#assign totAmtPayableByPatient = totalCopayPatient + totalDeductible />
            <@ofbizCurrency amount=totAmtPayableByPatient isoCode=currencyUomId/>
          </td>
        </tr>
        <tr><td colspan="${numColumns}"> &nbsp; &nbsp; &nbsp; </td></tr>
        <tr>
          <th colspan="8" style="font-weight:bold;text-align:right;padding-right:40px;">Insurance Payable</th>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- totAmtPayableByInsurance (Total Insurance Payable) -->
            <#assign totAmtPayableByInsurance = totalCopayInsurance />
            <@ofbizCurrency amount=totAmtPayableByInsurance isoCode=currencyUomId/>
          </td>
        </tr>
      <#elseif orderRxHeader?exists && orderRxHeader.patientType?exists && 'CORPORATE'==orderRxHeader.patientType>
        <tr>
          <th colspan="7" style="font-weight:bold;text-align:right;padding-right:40px;">Items Sub Total</th>
          <#assign orderSubTotalExcludeTax = orderSubTotal - salesTax.amount?default(0)>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- <@ofbizCurrency amount=orderSubTotalExcludeTax isoCode=currencyUomId/> -->
            <@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
          </td>
        </tr>
        <tr><td colspan="${numColumns}"> &nbsp; &nbsp; &nbsp; </td></tr>
        <tr>
          <th colspan="7" style="font-weight:bold;text-align:right;padding-right:40px;">Patient Payable</th>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- totAmtPayableByPatient (Total Patient Payable) -->
            <#assign totAmtPayableByPatient = totalCopayPatient />
            <@ofbizCurrency amount=totAmtPayableByPatient isoCode=currencyUomId/>
          </td>
        </tr>
        <tr><td colspan="${numColumns}"> &nbsp; &nbsp; &nbsp; </td></tr>
        <tr>
          <th colspan="7" style="font-weight:bold;text-align:right;padding-right:40px;">Corporate Payable</th>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- totAmtPayableByCorporate (Total Corporate Payable) -->
            <#assign totAmtPayableByCorporate = totalCopayCorporate />
            <@ofbizCurrency amount=totAmtPayableByCorporate isoCode=currencyUomId/>
          </td>
        </tr>
      <#elseif orderRxHeader?exists && orderRxHeader.patientType?exists && ('CASH'==orderRxHeader.patientType || 'CASH PAYING'==orderRxHeader.patientType)>
        <tr>
          <th colspan="7" style="font-weight:bold;text-align:right;padding-right:40px;">Items Sub Total</th>
          <#assign orderSubTotalExcludeTax = orderSubTotal - salesTax.amount?default(0)>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- <@ofbizCurrency amount=orderSubTotalExcludeTax isoCode=currencyUomId/> -->
            <@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
          </td>
        </tr>
        <tr><td colspan="${numColumns}"> &nbsp; &nbsp; &nbsp; </td></tr>
        <tr>
          <th colspan="7" style="font-weight:bold;text-align:right;padding-right:40px;">Patient Payable</th>
          <td colspan="2" style="text-align:right;padding-right:20px;">
            <#-- totAmtPayableByPatient (Total Patient Payable) -->
            <#assign totAmtPayableByPatient = totalCopayPatient />
            <@ofbizCurrency amount=totAmtPayableByPatient isoCode=currencyUomId/>
          </td>
        </tr>
      </#if>
    </tfoot>

  </table>
</div>
