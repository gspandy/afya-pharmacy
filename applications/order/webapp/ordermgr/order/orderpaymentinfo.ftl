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
<#macro maskSensitiveNumber cardNumber>
    <#assign cardNumberDisplay = "">
    <#if cardNumber?has_content>
        <#assign size = cardNumber?length - 4>
        <#if (size > 0)>
            <#list 0 .. size-1 as foo>
                <#assign cardNumberDisplay = cardNumberDisplay + "*">
            </#list>
            <#assign cardNumberDisplay = cardNumberDisplay + cardNumber[size .. size + 3]>
        <#else>
        <#-- but if the card number has less than four digits (ie, it was entered incorrectly), display it in full -->
            <#assign cardNumberDisplay = cardNumber>
        </#if>
    </#if>
    ${cardNumberDisplay?if_exists}
</#macro>

<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">&nbsp;${uiLabelMap.AccountingPaymentInformation}</li>
            <li class="expanded"><a onclick="javascript:toggleScreenlet(this, 'OrderPaymentInfoScreenletBody_${orderId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Collapse">&nbsp;</a></li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body" id="OrderPaymentInfoScreenletBody_${orderId}">
        <table class="basic-table" cellspacing='0'>
            <#assign orderTypeId = orderReadHelper.getOrderTypeId()>
            <#if orderTypeId == "PURCHASE_ORDER">
            <tr>
                <th>${uiLabelMap.AccountingPaymentID}</th>
                <th>${uiLabelMap.CommonTo}</th>
                <th>${uiLabelMap.CommonAmount}</th>
                <th>${uiLabelMap.CommonStatus}</th>
            </tr>
                <#list orderPaymentPreferences as orderPaymentPreference>
                    <#assign payments = orderPaymentPreference.getRelated("Payment")>
                    <#list payments as payment>
                        <#assign statusItem = payment.getRelatedOne("StatusItem")>
                        <#assign partyName = delegator.findOne("PartyNameView", {"partyId" : payment.partyIdTo}, true)>
                        <tr>
                            <#if security.hasPermission("PAY_INFO_VIEW", session) || security.hasPermission("PAY_INFO_ADMIN", session)>
                                <td><a href="/accounting/control/paymentOverview?paymentId=${payment.paymentId}">${payment.paymentId}</a></td>
                            <#else>
                                <td>${payment.paymentId}</td>
                            </#if>
                            <td>
                                ${partyName.groupName?if_exists}${partyName.lastName?if_exists} ${partyName.firstName?if_exists} ${partyName.middleName?if_exists}
                                <#if security.hasPermission("PARTYMGR_VIEW", session) || security.hasPermission("PARTYMGR_ADMIN", session)>
                                    [<a href="/partymgr/control/viewprofile?partyId=${partyId}">${partyId}</a>]
                                <#else>
                                    [${partyId}]
                                </#if>
                            </td>
                            <td><@ofbizCurrency amount=payment.amount?if_exists/></td>
                            <td>${statusItem.description}</td>
                        </tr>
                    </#list>
                </#list>

                <#-- invoices -->
                <#if invoices?has_content>
                    <tr><td colspan="4"><hr /></td></tr>
                    <tr>
                        <td align="right" valign="top" width="29%">&nbsp;<span class="label">${uiLabelMap.OrderInvoices}</span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="60%">
                            <#list invoices as invoice>
                                <#assign invoiceGv = delegator.findOne("Invoice", {"invoiceId" : invoice}, false)>
                                <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : invoiceGv.statusId}, false)>
                                <div>${uiLabelMap.CommonNbr}<a target="_blank" href="/accounting/control/invoiceOverview?invoiceId=${invoice}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${invoice}</a>
                                <block style="margin-right:12px;font-weight:bold;"> ${statusItem.description?if_exists} </block>
                                <#if orderTypeId == "SALES_ORDER">(<a target="_blank" href="/accounting/control/invoice.pdf?invoiceId=${invoice}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">PDF</a>)</#if></div>
                            </#list>
                        </td>
                        <td width="10%">&nbsp;</td>
                    </tr>
                </#if>
            <#else>

                <#-- order payment status -->
                <#-- <tr>
                    <td align="center" valign="top" width="29%" class="label">&nbsp;${uiLabelMap.OrderStatusHistory}</td>
                    <td width="1%">&nbsp;</td>
                    <td width="60%">
                        <#assign orderPaymentStatuses = orderReadHelper.getOrderPaymentStatuses()>
                        <#if orderPaymentStatuses?has_content>
                            <#list orderPaymentStatuses as orderPaymentStatus>
                                <#assign statusItem = orderPaymentStatus.getRelatedOne("StatusItem")?if_exists>
                                <#if statusItem?has_content>
                                    <div>
                                    ${statusItem.get("description",locale)} <#if orderPaymentStatus.statusDatetime?has_content>- ${orderPaymentStatus.statusDatetime?if_exists?string("dd/MM/yyyy HH:mm:ss")}</#if><!-- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderPaymentStatus.statusDatetime, "", locale, timeZone)!} &ndash;&gt;
                                        &nbsp;
                                    ${uiLabelMap.CommonBy} - [${orderPaymentStatus.statusUserLogin?if_exists}]
                                    </div>
                                </#if>
                            </#list>
                        </#if>
                    </td>
                    <td width="10%">&nbsp;</td>
                </tr>
                <tr><td colspan="4"><hr /></td></tr> -->
                <#if orderPaymentPreferences?has_content || billingAccount?has_content || invoices?has_content>
                    <#list orderPaymentPreferences as orderPaymentPreference>
                        <#assign paymentList = orderPaymentPreference.getRelated("Payment")>
                        <#assign pmBillingAddress = {}>
                        <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                        <#if outputted?default("false") == "true">
                            <tr><td colspan="4"><hr /></td></tr>
                        </#if>
                        <#assign outputted = "true">
                        <#-- try the paymentMethod first; if paymentMethodId is specified it overrides paymentMethodTypeId -->
                        <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
                        ${paymentMethod}
                        <#if !paymentMethod?has_content>
                            <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")>
                            <#if paymentMethodType.paymentMethodTypeId == "EXT_BILLACT">
                                <#assign outputted = "false">
                                <#-- billing account -->
                                <#if billingAccount?exists>
                                    <#if outputted?default("false") == "true">
                                        <tr><td colspan="4"><hr /></td></tr>
                                    </#if>
                                    <tr>
                                        <td align="right" valign="top" width="29%">
                                        <#-- billing accounts require a special OrderPaymentPreference because it is skipped from above section of OPPs -->
                                            <div>&nbsp;<span class="label">${uiLabelMap.AccountingBillingAccount}</span>&nbsp;
                                                <#if billingAccountMaxAmount?has_content>
                                                    <br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=billingAccountMaxAmount?default(0.000) isoCode=currencyUomId/>
                                                </#if>
                                            </div>
                                        </td>
                                        <td width="1%">&nbsp;</td>
                                        <td valign="top" width="60%">
                                            <table class="basic-table" cellspacing='0'>
                                                <tr>
                                                    <td valign="top">
                                                    ${uiLabelMap.CommonNbr}<a target="_blank" href="/accounting/control/EditBillingAccount?billingAccountId=${billingAccount.billingAccountId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${billingAccount.billingAccountId}</a>  - ${billingAccount.description?if_exists}
                                                    </td>
                                                    <td valign="top" align="right">
                                                        <#-- <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED" && orderPaymentPreference.statusId != "PAYMENT_RECEIVED">
                                                            <a href="<@ofbizUrl>receivepayment?${paramString}</@ofbizUrl>" class="buttontext">${uiLabelMap.AccountingReceivePayment}</a>
                                                        </#if> -->
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                        <td width="10%">
                                            <#if !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                                <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                    <div>
                                                        <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                                                        <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                                                            <input type="hidden" name="orderId" value="${orderId}" />
                                                            <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                            <input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
                                                            <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
                                                        </form>
                                                    </div>
                                                </#if>
                                            </#if>
                                        </td>
                                    </tr>
                                </#if>
                            <#elseif paymentMethodType.paymentMethodTypeId == "FIN_ACCOUNT">
                                <#assign finAccount = orderPaymentPreference.getRelatedOne("FinAccount")?if_exists/>
                                <#if (finAccount?has_content)>
                                    <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                                    <#assign finAccountType = finAccount.getRelatedOne("FinAccountType")?if_exists/>
                                    <tr>
                                        <td align="right" valign="top" width="29%">
                                            <div>
                                                <span class="label">&nbsp;${uiLabelMap.AccountingFinAccount}</span>
                                                <#if orderPaymentPreference.maxAmount?has_content>
                                                    <br />${uiLabelMap.OrderPaymentMaximumAmount}:DDD <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.000) isoCode=currencyUomId/>
                                                </#if>
                                            </div>
                                        </td>
                                        <td width="1%">&nbsp;</td>
                                        <td valign="top" width="60%">
                                            <div>
                                                <#if (finAccountType?has_content)>
                                                    ${finAccountType.description?default(finAccountType.finAccountTypeId)}&nbsp;
                                                </#if>
                                                #${finAccount.finAccountCode?default(finAccount.finAccountId)} (<a href="/accounting/control/EditFinAccount?finAccountId=${finAccount.finAccountId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${finAccount.finAccountId}</a>)
                                                <br />
                                                ${finAccount.finAccountName?if_exists}
                                                <br />

                                                <#-- Authorize and Capture transactions -->
                                                <div>
                                                    <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                        <a href="/accounting/control/AuthorizeTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingAuthorize}</a>
                                                    </#if>
                                                    <#if orderPaymentPreference.statusId == "PAYMENT_AUTHORIZED">
                                                        <a href="/accounting/control/CaptureTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingCapture}</a>
                                                    </#if>
                                                </div>
                                            </div>
                                            <#if gatewayResponses?has_content>
                                                <div>
                                                    <hr />
                                                    <#list gatewayResponses as gatewayResponse>
                                                        <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                                                        ${(transactionCode.get("description",locale))?default("Unknown")}:
                                                        <#if gatewayResponse.transactionDate?has_content> ${gatewayResponse.transactionDate?if_exists?string("dd/MM/yyyy")}</#if><#-- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(gatewayResponse.transactionDate, "", locale, timeZone)!} -->
                                                        <@ofbizCurrency amount=gatewayResponse.amount isoCode=currencyUomId/><br />
                                                        (<span class="label">${uiLabelMap.OrderReference}</span>&nbsp;${gatewayResponse.referenceNum?if_exists}
                                                        <span class="label">${uiLabelMap.OrderAvs}</span>&nbsp;${gatewayResponse.gatewayAvsResult?default("N/A")}
                                                        <span class="label">${uiLabelMap.OrderScore}</span>&nbsp;${gatewayResponse.gatewayScoreResult?default("N/A")})
                                                        <a href="/accounting/control/ViewGatewayResponse?paymentGatewayResponseId=${gatewayResponse.paymentGatewayResponseId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.CommonDetails}</a>
                                                        <#if gatewayResponse_has_next><hr /></#if>
                                                    </#list>
                                                </div>
                                            </#if>
                                        </td>
                                        <td width="10%">
                                            <#if !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                                <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                    <div>
                                                        <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                                                        <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                                                            <input type="hidden" name="orderId" value="${orderId}" />
                                                            <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                            <input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
                                                            <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
                                                        </form>
                                                    </div>
                                                </#if>
                                            </#if>
                                        </td>
                                    </tr>
                                    <#if paymentList?has_content>
                                        <tr>
                                            <td align="right" valign="top" width="29%">
                                                <div>&nbsp;<span class="label">${uiLabelMap.AccountingInvoicePayments}</span></div>
                                            </td>
                                            <td width="1%">&nbsp;</td>
                                            <td width="60%">
                                                <div>
                                                    <#list paymentList as paymentMap>
                                                        <a href="/accounting/control/paymentOverview?paymentId=${paymentMap.paymentId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
                                                    </#list>
                                                </div>
                                            </td>
                                        </tr>
                                    </#if>
                                </#if>
                            <#else>

                                <#if !(orderHeader.statusId.equals("ORDER_CANCELLED")) && !(orderHeader.statusId.equals("ORDER_REJECTED"))>
                                    <tr>
                                        <td align="right" valign="top" width="29%">
                                            <div>&nbsp;<span class="label">${paymentMethodType.get("description",locale)?if_exists}</span>&nbsp;
                                                <#if orderPaymentPreference.maxAmount?has_content && (!(orderHeader.statusId.equals("ORDER_CANCELLED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")))>
                                                    <br /><#-- ${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.000) isoCode=currencyUomId/> -->
                                                    ${uiLabelMap.OrderPaymentMaximumAmount}:
                                                    <#assign totalCopayPatient = Static["java.math.BigDecimal"].ZERO>
                                                    <#assign totalPatientPayable = Static["java.math.BigDecimal"].ZERO>
                                                    <#assign totalCopayInsurance = Static["java.math.BigDecimal"].ZERO>
                                                    <#assign totalAmountPaid = Static["java.math.BigDecimal"].ZERO>
                                                    <#assign maximumAmount = Static["java.math.BigDecimal"].ZERO>
                                                    <#assign maxAmt = 0.000>
                                                    <#assign pendingAmount = 0.000>
                                                    <#if orderRxHeader?has_content && "INSURANCE"==orderRxHeader.patientType>
                                                      <#if ("CASH" == paymentMethodType.paymentMethodTypeId || "CASH PAYING" == paymentMethodType.paymentMethodTypeId || "CREDIT_CARD" == paymentMethodType.paymentMethodTypeId || "PATIENT" == paymentMethodType.paymentMethodTypeId)>

                                                        <#if orderItemList?has_content>

                                                          <#assign totalDeductible = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayPatient = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayInsurance = Static["java.math.BigDecimal"].ZERO>

                                                          <#list orderItemList as orderItem>

                                                            <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>

                                                            <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">

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
                                                                <#assign copayPat = Static["java.math.BigDecimal"].ZERO>
                                                                <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                                                                <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                                                                <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                                                  <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                                                  <#-- <#assign copayInsurance = grossAmount - (copayPatient + itemDeductible)> -->
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
                                                                  <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                  <#assign deductible = deductibleAmount + copayPatient>
                                                                  <#if deductible lt 0>
                                                                      <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                                                      <#assign totalDeductible = totalDeductible + deductible>
                                                                  <#else>
                                                                      <#assign totalDeductible = totalDeductible + deductible>
                                                                  </#if>
                                                                  <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                                                  <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                                                                <#else>
                                                                  <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                                                                  <#assign deductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                  <#assign totalDeductible = totalDeductible + deductible>
                                                                </#if>
                                                              <#else>
                                                                <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                                                <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                                                                <#assign patientCopay = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                                                                <#assign itemDeductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                                                  <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                                                  <#assign copayPatient = copayPatient + lineItemAdjTot + orderItemAdjAmount>
                                                                  <#-- <#assign copayInsurance = grossAmount - (copayPatient + itemDeductible)> -->
                                                                  <#if copayPatient lt 0>
                                                                    <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                    <#assign deductible = deductibleAmount + copayPatient>
                                                                    <#assign copayPatient = Static["java.math.BigDecimal"].ZERO>
                                                                    <#assign copay = Static["java.math.BigDecimal"].ZERO>
                                                                    <#if deductible lt 0>
                                                                      <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) + deductible>
                                                                      <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                                                      <#assign totalDeductible = totalDeductible + deductible>
                                                                    <#else>
                                                                      <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                                                      <#assign totalDeductible = totalDeductible + deductible>
                                                                    </#if>
                                                                  <#else>
                                                                    <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                                                    <#assign totalDeductible = totalDeductible + itemDeductible>
                                                                  </#if>
                                                                <#else>
                                                                  <#assign copayInsurance = netAmount - (copayPatient + itemDeductible)>
                                                                  <#assign copayPat = netAmount - itemDeductible>
                                                                  <#if copayPat lt 0>
                                                                    <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                    <#assign deductible = deductibleAmount + copayPat>
                                                                    <#if deductible lt 0>
                                                                      <#assign deductible = Static["java.math.BigDecimal"].ZERO>
                                                                      <#assign totalDeductible = totalDeductible + deductible>
                                                                    <#else>
                                                                      <#assign totalDeductible = totalDeductible + deductible>
                                                                    </#if>
                                                                  <#else>
                                                                    <#assign deductible = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                    <#assign totalDeductible = totalDeductible + deductible>
                                                                  </#if>
                                                                </#if>
                                                                <#assign copayPatient = copayPatient + copayInsurance>
                                                                <#assign totalCopayPatient = totalCopayPatient + copayPatient>
                                                              </#if>
                                                            </#if>

                                                          </#list>
                                                        </#if>
                                                        <#assign totalPatientPayable = totalCopayPatient + totalDeductible>
                                                        <@ofbizCurrency amount=totalPatientPayable isoCode=currencyUomId/>
                                                        <#assign maxAmt = totalPatientPayable?default(0.000)?string("0.000")/>
                                                        <#assign maximumAmount = totalPatientPayable/>
                                                        <#assign orderPaymentPreferenceHistory = delegator.findByAnd("OrderPaymentPreferenceHistory",{"orderId",orderItem.orderId,"paymentPreferenceId",orderPaymentPreference.orderPaymentPreferenceId,"statusId","PMNT_RECEIVED"})>
                                                        <#if orderPaymentPreferenceHistory?has_content>
                                                          <#list orderPaymentPreferenceHistory as orderPaymentPrefHist>
                                                            <#assign totalAmountPaid = totalAmountPaid + orderPaymentPrefHist.amount>
                                                          </#list>
                                                        </#if>
                                                        <#assign remainingAmount = maximumAmount - totalAmountPaid>
                                                        <#assign pendingAmount = remainingAmount?default(0.000)?string("0.000")>

                                                      <#elseif "INSURANCE" == paymentMethodType.paymentMethodTypeId>

                                                        <#if orderItemList?has_content>

                                                          <#assign totalDeductible = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayPatient = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayInsurance = Static["java.math.BigDecimal"].ZERO>

                                                          <#list orderItemList as orderItem>

                                                            <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>

                                                            <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">

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
                                                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCopay(netAmount, orderItem)?default(0.000)>
                                                              <#-- <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>

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
                                                                      <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                                    <#else>
                                                                      <#assign copayInsurance = orderItem.authorizationAmount?default(0.000)>
                                                                      <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                                    </#if>
                                                                  <#else>
                                                                    <#assign copayInsurance = orderItem.authorizationAmount?default(0.000)>
                                                                    <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                                  </#if>
                                                                <#else>
                                                                  <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                                                    <#if copayPatient lt 0>
                                                                      <#assign deductibleAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemDeductible(netAmount, orderItem)?default(0.000)>
                                                                      <#assign deductible = deductibleAmount + copayPatient>
                                                                      <#if deductible lt 0>
                                                                        <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible) + deductible>
                                                                        <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                                      <#else>
                                                                        <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                                                        <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                                      </#if>
                                                                    <#else>
                                                                      <#assign copayInsurance = grossAmount - (patientCopay + itemDeductible)>
                                                                      <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                                    </#if>
                                                                  <#else>
                                                                    <#assign copayInsurance = netAmount - (patientCopay + itemDeductible)>
                                                                    <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                                  </#if>
                                                                </#if>
                                                              <#else>
                                                                <#assign copayInsurance = 0.000>
                                                                <#assign totalCopayInsurance = totalCopayInsurance + copayInsurance>
                                                              </#if>
                                                            </#if>

                                                          </#list>
                                                        </#if>
                                                        <@ofbizCurrency amount=totalCopayInsurance isoCode=currencyUomId/>
                                                        <#assign maxAmt = totalCopayInsurance?default(0.000)?string("0.000")/>
                                                        <#assign maximumAmount = totalCopayInsurance/>
                                                        <#assign orderPaymentPreferenceHistory = delegator.findByAnd("OrderPaymentPreferenceHistory",{"orderId",orderItem.orderId,"paymentPreferenceId",orderPaymentPreference.orderPaymentPreferenceId,"statusId","PMNT_RECEIVED"})>
                                                        <#if orderPaymentPreferenceHistory?has_content>
                                                          <#list orderPaymentPreferenceHistory as orderPaymentPrefHist>
                                                            <#assign totalAmountPaid = totalAmountPaid + orderPaymentPrefHist.amount>
                                                          </#list>
                                                        </#if>
                                                        <#assign remainingAmount = maximumAmount - totalAmountPaid>
                                                        <#assign pendingAmount = remainingAmount?default(0.000)?string("0.000")>
                                                      </#if>
                                                    <#elseif orderRxHeader?has_content && "CORPORATE"==orderRxHeader.patientType>
                                                      <#assign primaryPayer = orderRxHeader.primaryPayer?if_exists>
                                                      <#if ("CASH" == paymentMethodType.paymentMethodTypeId || "CASH PAYING" == paymentMethodType.paymentMethodTypeId || "CREDIT_CARD" == paymentMethodType.paymentMethodTypeId || "PATIENT" == paymentMethodType.paymentMethodTypeId)>

                                                        <#if orderItemList?has_content>

                                                          <#assign totalDeductible = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayPatient = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayCorporate = Static["java.math.BigDecimal"].ZERO>

                                                          <#list orderItemList as orderItem>

                                                            <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>

                                                            <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">

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
                                                              <#-- <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>

                                                            </#if>

                                                          </#list>
                                                        </#if>
                                                        <#assign totalPatientPayable = totalCopayPatient>
                                                        <@ofbizCurrency amount=totalPatientPayable isoCode=currencyUomId/>
                                                        <#assign maxAmt = totalPatientPayable?default(0.000)?string("0.000")/>
                                                        <#assign maximumAmount = totalPatientPayable/>
                                                        <#assign orderPaymentPreferenceHistory = delegator.findByAnd("OrderPaymentPreferenceHistory",{"orderId",orderItem.orderId,"paymentPreferenceId",orderPaymentPreference.orderPaymentPreferenceId,"statusId","PMNT_RECEIVED"})>
                                                        <#if orderPaymentPreferenceHistory?has_content>
                                                          <#list orderPaymentPreferenceHistory as orderPaymentPrefHist>
                                                            <#assign totalAmountPaid = totalAmountPaid + orderPaymentPrefHist.amount>
                                                          </#list>
                                                        </#if>
                                                        <#assign remainingAmount = maximumAmount - totalAmountPaid>
                                                        <#assign pendingAmount = remainingAmount?default(0.000)?string("0.000")>

                                                      <#elseif "CORPORATE" == paymentMethodType.paymentMethodTypeId>

                                                        <#if orderItemList?has_content>

                                                          <#assign totalDeductible = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayPatient = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign totalCopayCorporate = Static["java.math.BigDecimal"].ZERO>

                                                          <#list orderItemList as orderItem>

                                                            <#assign netAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign orderItemAdjAmount = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign lineItemAdjTot = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>

                                                            <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">

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
                                                              <#-- <@ofbizCurrency amount=copayPatient isoCode=currencyUomId/> -->
                                                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>

                                                              <#assign copayPatient = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemCorporateCopay(primaryPayer, netAmount, orderItem)?default(0.000)>
                                                              <#if orderItem.computeBy?has_content && orderItem.computeBy == "GROSS">
                                                                <#assign grossAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemGrossAmount(orderItem)?default(0.000)>
                                                                <#assign copayCorporate = grossAmount - copayPatient>
                                                              <#else>
                                                                <#assign copayCorporate = netAmount - copayPatient>
                                                              </#if>
                                                              <#-- <@ofbizCurrency amount=copayCorporate?default(0.000) isoCode=currencyUomId/> -->
                                                              <#assign totalCopayCorporate = totalCopayCorporate + copayCorporate>

                                                            </#if>

                                                          </#list>
                                                        </#if>
                                                        <@ofbizCurrency amount=totalCopayCorporate isoCode=currencyUomId/>
                                                        <#assign maxAmt = totalCopayCorporate?default(0.000)?string("0.000")/>
                                                        <#assign maximumAmount = totalCopayCorporate/>
                                                        <#assign orderPaymentPreferenceHistory = delegator.findByAnd("OrderPaymentPreferenceHistory",{"orderId",orderItem.orderId,"paymentPreferenceId",orderPaymentPreference.orderPaymentPreferenceId,"statusId","PMNT_RECEIVED"})>
                                                        <#if orderPaymentPreferenceHistory?has_content>
                                                          <#list orderPaymentPreferenceHistory as orderPaymentPrefHist>
                                                            <#assign totalAmountPaid = totalAmountPaid + orderPaymentPrefHist.amount>
                                                          </#list>
                                                        </#if>
                                                        <#assign remainingAmount = maximumAmount - totalAmountPaid>
                                                        <#assign pendingAmount = remainingAmount?default(0.000)?string("0.000")>
                                                      </#if>
                                                    <#elseif (orderRxHeader?has_content && ("CASH"==orderRxHeader.patientType || "CASH PAYING"==orderRxHeader.patientType))>
                                                      <#if ("CASH" == paymentMethodType.paymentMethodTypeId || "CASH PAYING" == paymentMethodType.paymentMethodTypeId || "CREDIT_CARD" == paymentMethodType.paymentMethodTypeId || "PATIENT" == paymentMethodType.paymentMethodTypeId)>
                                                        <#if orderItemList?has_content>
                                                          <#list orderItemList as orderItem>
                                                            <#assign lineItemAdjustmentTotal = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                                            <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>

                                                            <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">

                                                              <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(0.000)>
                                                              <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                                                                <#list orderItemAdjustments as orderItemAdjustment>
                                                                  <#assign adjustmentType = orderItemAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                                                                  <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem)?default(0.000)>
                                                                  <#assign lineItemAdjustmentTotal = lineItemAdjustmentTotal + lineItemAdjustment>
                                                                </#list>
                                                              </#if>

                                                              <#assign orderItemApportionAdjustments = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                                                              <#if orderItemApportionAdjustments?has_content>
                                                                <#list orderItemApportionAdjustments as orderItemApportionAdjustment>
                                                                  <#assign orderItemAdjAmt = orderItemAdjAmt + orderItemApportionAdjustment.amount/>
                                                                </#list>
                                                              </#if>

                                                              <#assign patientPayable = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                                              <#assign copayPatient = patientPayable + lineItemAdjustmentTotal + orderItemAdjAmt>
                                                              <#assign totalCopayPatient = totalCopayPatient + copayPatient>

                                                            </#if>
                                                          </#list>
                                                        </#if>
                                                        <@ofbizCurrency amount=totalCopayPatient isoCode=currencyUomId/>
                                                        <#assign maxAmt = totalCopayPatient?default(0.000)?string("0.000")/>
                                                        <#assign maximumAmount = totalCopayPatient/>
                                                        <#assign orderPaymentPreferenceHistory = delegator.findByAnd("OrderPaymentPreferenceHistory",{"orderId",orderItem.orderId,"paymentPreferenceId",orderPaymentPreference.orderPaymentPreferenceId,"statusId","PMNT_RECEIVED"})>
                                                        <#if orderPaymentPreferenceHistory?has_content>
                                                          <#list orderPaymentPreferenceHistory as orderPaymentPrefHist>
                                                            <#assign totalAmountPaid = totalAmountPaid + orderPaymentPrefHist.amount>
                                                          </#list>
                                                        </#if>
                                                        <#assign remainingAmount = maximumAmount - totalAmountPaid>
                                                        <#assign pendingAmount = remainingAmount?default(0.000)?string("0.000")>
                                                      </#if>
                                                    <#elseif ("CASH" == paymentMethodType.paymentMethodTypeId || "CASH PAYING" == paymentMethodType.paymentMethodTypeId || "CREDIT_CARD" == paymentMethodType.paymentMethodTypeId || "PATIENT" == paymentMethodType.paymentMethodTypeId)>
                                                      <#if orderItemList?has_content>
                                                        <#list orderItemList as orderItem>
                                                          <#assign lineItemAdjustmentTotal = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign orderItemAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                                          <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>

                                                          <#if orderHeader.orderTypeId == "SALES_ORDER" && currentItemStatus.statusId?has_content && currentItemStatus.statusId != "ITEM_CANCELLED">

                                                            <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)?default(0.000)>
                                                            <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                                                              <#list orderItemAdjustments as orderItemAdjustment>
                                                                <#assign adjustmentType = orderItemAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                                                                <#assign lineItemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem)?default(0.000)>
                                                                <#assign lineItemAdjustmentTotal = lineItemAdjustmentTotal + lineItemAdjustment>
                                                              </#list>
                                                            </#if>

                                                            <#assign orderItemApportionAdjustments = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                                                            <#if orderItemApportionAdjustments?has_content>
                                                              <#list orderItemApportionAdjustments as orderItemApportionAdjustment>
                                                                <#assign orderItemAdjAmt = orderItemAdjAmt + orderItemApportionAdjustment.amount/>
                                                              </#list>
                                                            </#if>

                                                            <#assign patientPayable = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments)?default(0.000)>
                                                            <#assign copayPatient = patientPayable + lineItemAdjustmentTotal + orderItemAdjAmt>
                                                            <#assign totalCopayPatient = totalCopayPatient + copayPatient>

                                                          </#if>
                                                        </#list>
                                                        <@ofbizCurrency amount=totalCopayPatient isoCode=currencyUomId/>
                                                        <#assign maxAmt = totalCopayPatient?default(0.000)?string("0.000")/>
                                                        <#assign maximumAmount = totalCopayPatient/>
                                                        <#assign orderPaymentPreferenceHistory = delegator.findByAnd("OrderPaymentPreferenceHistory",{"orderId",orderItem.orderId,"paymentPreferenceId",orderPaymentPreference.orderPaymentPreferenceId,"statusId","PMNT_RECEIVED"})>
                                                        <#if orderPaymentPreferenceHistory?has_content>
                                                          <#list orderPaymentPreferenceHistory as orderPaymentPrefHist>
                                                            <#assign totalAmountPaid = totalAmountPaid + orderPaymentPrefHist.amount>
                                                          </#list>
                                                        </#if>
                                                        <#assign remainingAmount = maximumAmount - totalAmountPaid>
                                                        <#assign pendingAmount = remainingAmount?default(0.000)?string("0.000")>
                                                      </#if>
                                                    </#if>
                                                </#if>
                                            </div>
                                        </td>

                                        <td width="1%">&nbsp;</td>
                                        <#if (paymentMethodType.paymentMethodTypeId=='INSURANCE' || paymentMethodType.paymentMethodTypeId=='PATIENT' || paymentMethodType.paymentMethodTypeId=='CASH' || paymentMethodType.paymentMethodTypeId=='CASH PAYING' || paymentMethodType.paymentMethodTypeId=='CREDIT_CARD') &&  orderPaymentPreference.statusId!='PAYMENT_NOT_RECEIVED' && pendingAmount=="0.000" && (!(orderHeader.statusId.equals("ORDER_CANCELLED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")))>
                                            <td width="60%">
                                                <div>
                                                    <#if orderPaymentPreference.maxAmount?has_content>
                                                        <#-- <br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.000) isoCode=currencyUomId/> -->
                                                        <br />Amount: <@ofbizCurrency amount=maxAmt?default(0.000) isoCode=currencyUomId/>
                                                    </#if>
                                                    <br />&nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                                                </div>
                                                <#--
                                                <div><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.000) isoCode=currencyUomId/>&nbsp;-&nbsp;${(orderPaymentPreference.authDate.toString())?if_exists}</div>
                                                <div>&nbsp;<#if orderPaymentPreference.authRefNum?exists>(${uiLabelMap.OrderReference}: ${orderPaymentPreference.authRefNum})</#if></div>
                                                -->
                                            </td>
                                        <#else>
                                            <td align="right" width="50%">
                                                <#if orderPaymentPreference.orderPaymentPreferenceId?has_content && (!(orderHeader.statusId.equals("ORDER_CANCELLED")) && !(orderHeader.statusId.equals("ORDER_REJECTED"))) && pendingAmount!="0.000">
                                                    <a href="javascript:document.Receivepayment_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.AccountingReceivePayment}</a>
                                                    <form name="Receivepayment_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>receivepayment?${paramString}</@ofbizUrl>">
                                                        <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                        <input type="hidden" name="maxAmount" value="${maxAmt}" />
                                                        <input type="hidden" name="pendingAmount" value="${pendingAmount}" />
                                                    </form>
                                                <#elseif (!(orderHeader.statusId.equals("ORDER_CANCELLED")) && !(orderHeader.statusId.equals("ORDER_REJECTED"))) && pendingAmount!="0.000">
                                                    <a href="<@ofbizUrl>receivepayment?${paramString}</@ofbizUrl>" class="buttontext">${uiLabelMap.AccountingReceivePayment}</a>
                                                </#if>
                                            </td>
                                        </#if>

                                        <#-- <td width="10%">
                                            <#if !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                                <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                    <div>
                                                        <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                                                        <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>editOrderPaymentPreference</@ofbizUrl>">
                                                            <input type="hidden" name="orderId" value="${orderId}" />
                                                            <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                            <input type="hidden" name="statusId" value="PAYMENT_NOT_RECEIVED" />
                                                            <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
                                                        </form>
                                                    </div>
                                                </#if>
                                            </#if>
                                        </td> -->
                                        <#-- <td width="10%">
                                            <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                                <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                    <div>
                                                        <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                                                        <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                                                            <input type="hidden" name="orderId" value="${orderId}" />
                                                            <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                            <input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
                                                            <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
                                                        </form>
                                                    </div>
                                                </#if>
                                            </#if>
                                        </td> -->
                                    </tr>
                                    <#assign orderPaymentPrefHistory = delegator.findByAnd("OrderPaymentPreferenceHistory",{"orderId",orderItem.orderId,"paymentPreferenceId",orderPaymentPreference.orderPaymentPreferenceId,"statusId","PMNT_RECEIVED"})>
                                    <#if orderPaymentPrefHistory?has_content>
                                      <#list orderPaymentPrefHistory as ordrPaymntPrefHist>
                                        <tr><td>&nbsp;</td><td colspan="3"><hr /></td></tr>
                                        <tr>
                                          <td>&nbsp;</td>
                                          <td align="right" valign="top">
                                            <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : ordrPaymntPrefHist.paymentMethodTypeId}, false)>
                                            <#if paymentMethodType?exists>
                                              <div>&nbsp;<span class="label">${paymentMethodType.get("description",locale)?if_exists}</span></div>
                                            <#else>
                                              <div>&nbsp;<span class="label">${ordrPaymntPrefHist.paymentMethodTypeId?if_exists}</span></div>
                                            </#if>
                                          </td>
                                          <td colspan="2">
                                            Amount Received: <@ofbizCurrency amount=ordrPaymntPrefHist.amount?default(0.000) isoCode=ordrPaymntPrefHist.currencyUomId/>
                                          </td>
                                          <td>&nbsp;</td>
                                        </tr>
                                        <tr>
                                          <td colspan="2">&nbsp;</td>
                                          <td>
                                            <#if ordrPaymntPrefHist.receivedBy?exists && ordrPaymntPrefHist.receivedBy != "system">
                                              <#assign receivedByUser = delegator.findByPrimaryKey("PartyNameView",{"partyId":ordrPaymntPrefHist.receivedBy})>
                                              <#if receivedByUser?has_content>
                                                <#assign receivedBy = receivedByUser.firstName?if_exists + " " + receivedByUser.lastName?if_exists + " " + receivedByUser.groupName?if_exists>
                                                Received By: ${receivedBy?if_exists}
                                              <#else>
                                                Received By: ${ordrPaymntPrefHist.receivedBy?if_exists}
                                              </#if>
                                            <#else>
                                              Received By: ${ordrPaymntPrefHist.receivedBy?if_exists}
                                            </#if>
                                          </td>
                                          <td>
                                            <#-- <#if !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                                <#if ordrPaymntPrefHist.statusId != "PMNT_CANCELLED" && (orderPaymentPreference.statusId != "PAYMENT_RECEIVED" ||  pendingAmount!="0.000")>
                                                    <div>
                                                        <a href="javascript:document.CancelOrderPaymentPreferenceHistory_${ordrPaymntPrefHist.paymentPreferenceHistoryId}.submit()" class="btn btn-mini btn-danger">${uiLabelMap.CommonCancel}</a>
                                                        <form name="CancelOrderPaymentPreferenceHistory_${ordrPaymntPrefHist.paymentPreferenceHistoryId}" method="post" action="<@ofbizUrl>cancelOrderPaymentPreferenceHistory</@ofbizUrl>">
                                                            <input type="hidden" name="paymentPrefHistoryId" value="${ordrPaymntPrefHist.paymentPreferenceHistoryId}" />
                                                            <input type="hidden" name="paymentPreferenceId" value="${ordrPaymntPrefHist.paymentPreferenceId}" />
                                                            <input type="hidden" name="orderId" value="${ordrPaymntPrefHist.orderId}" />
                                                            <input type="hidden" name="paymentMethodTypeId" value="${ordrPaymntPrefHist.paymentMethodTypeId?if_exists}" />
                                                            <input type="hidden" name="statusId" value="PMNT_CANCELLED" />
                                                        </form>
                                                    </div>
                                                </#if>
                                            </#if> -->
                                          </td>
                                        </tr>
                                        <tr>
                                          <td colspan="2">&nbsp;</td>
                                          <td>
                                            Received Date: ${ordrPaymntPrefHist.receivedDate?string("dd/MM/yyyy HH:mm:ss")}
                                          </td>
                                          <td>&nbsp;</td>
                                        </tr>
                                      </#list>
                                    </#if>
                                </#if>
                                <#if paymentList?has_content>
                                    <tr>
                                        <td align="right" valign="top" width="29%">
                                            <div>&nbsp;<span class="label">${uiLabelMap.AccountingInvoicePayments}</span></div>
                                        </td>
                                        <td width="1%">&nbsp;</td>
                                        <td width="60%">
                                            <div>
                                                <#list paymentList as paymentMap>
                                                    <a target="_blank" href="/accounting/control/paymentOverview?paymentId=${paymentMap.paymentId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
                                                    <a target="_blank" href="orderPaymentPrint?paymentId=${paymentMap.paymentId}&amp;orderId=${orderId}" class="buttontext">Print</a><#if paymentMap_has_next><br /></#if>
                                                </#list>
                                            </div>
                                        </td>
                                    </tr>
                                </#if>
                            </#if>
                        <#else>
                            <#if paymentMethod.paymentMethodTypeId?if_exists == "CREDIT_CARD">
                                <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                                <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")?if_exists>
                                <#if creditCard?has_content>
                                    <#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress")?if_exists>
                                </#if>
                                <tr>
                                    <td align="right" valign="top" width="29%">
                                        <div>&nbsp;<span class="label">${uiLabelMap.AccountingCreditCard}</span>
                                            <#if orderPaymentPreference.maxAmount?has_content>
                                                <br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.000) isoCode=currencyUomId/>
                                            </#if>
                                        </div>
                                    </td>
                                    <td width="1%">&nbsp;</td>
                                    <td valign="top" width="60%">
                                        <div>
                                            <#if creditCard?has_content>
                                                <#if creditCard.companyNameOnCard?exists>${creditCard.companyNameOnCard}<br /></#if>
                                                <#if creditCard.titleOnCard?has_content>${creditCard.titleOnCard}&nbsp;</#if>
                                            ${creditCard.firstNameOnCard?default("N/A")}&nbsp;
                                                <#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}&nbsp;</#if>
                                            ${creditCard.lastNameOnCard?default("N/A")}
                                                <#if creditCard.suffixOnCard?has_content>&nbsp;${creditCard.suffixOnCard}</#if>
                                                <br />

                                                <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                                ${creditCard.cardType}
                                                    <@maskSensitiveNumber cardNumber=creditCard.cardNumber?if_exists/>
                                                ${creditCard.expireDate?string("dd/MM/yyyy")}
                                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                                                <#else>
                                                ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                                                </#if>
                                                <br />

                                            <#-- Authorize and Capture transactions -->
                                                <div>
                                                    <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                        <a href="/accounting/control/AuthorizeTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingAuthorize}</a>
                                                    </#if>
                                                    <#if orderPaymentPreference.statusId == "PAYMENT_AUTHORIZED">
                                                        <a href="/accounting/control/CaptureTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingCapture}</a>
                                                    </#if>
                                                </div>
                                            <#else>
                                            ${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
                                            </#if>
                                        </div>
                                        <#if gatewayResponses?has_content>
                                            <div>
                                                <hr />
                                                <#list gatewayResponses as gatewayResponse>
                                                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                                                ${(transactionCode.get("description",locale))?default("Unknown")}:
                                                    <#if gatewayResponse.transactionDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(gatewayResponse.transactionDate, "", locale, timeZone)!} </#if>
                                                    <@ofbizCurrency amount=gatewayResponse.amount isoCode=currencyUomId/><br />
                                                    (<span class="label">${uiLabelMap.OrderReference}</span>&nbsp;${gatewayResponse.referenceNum?if_exists}
                                                    <span class="label">${uiLabelMap.OrderAvs}</span>&nbsp;${gatewayResponse.gatewayAvsResult?default("N/A")}
                                                    <span class="label">${uiLabelMap.OrderScore}</span>&nbsp;${gatewayResponse.gatewayScoreResult?default("N/A")})
                                                    <a href="/accounting/control/ViewGatewayResponse?paymentGatewayResponseId=${gatewayResponse.paymentGatewayResponseId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.CommonDetails}</a>
                                                    <#if gatewayResponse_has_next><hr /></#if>
                                                </#list>
                                            </div>
                                        </#if>
                                    </td>
                                    <td width="10%">
                                        <#if !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                            <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                                                <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                                                    <input type="hidden" name="orderId" value="${orderId}" />
                                                    <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                    <input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
                                                    <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
                                                </form>
                                            </#if>
                                        </#if>
                                    </td>
                                </tr>
                                <#elseif paymentMethod.paymentMethodTypeId?if_exists == "EFT_ACCOUNT">
                                    <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                                    <#if eftAccount?has_content>
                                        <#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress")?if_exists>
                                    </#if>
                                <tr>
                                    <td align="right" valign="top" width="29%">
                                        <div>&nbsp;<span class="label">${uiLabelMap.AccountingEFTAccount}</span>
                                            <#if orderPaymentPreference.maxAmount?has_content>
                                                <br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.000) isoCode=currencyUomId/>
                                            </#if>
                                        </div>
                                    </td>
                                    <td width="1%">&nbsp;</td>
                                    <td valign="top" width="60%">
                                        <div>
                                            <#if eftAccount?has_content>
                                            ${eftAccount.nameOnAccount?if_exists}<br />
                                                <#if eftAccount.companyNameOnAccount?exists>${eftAccount.companyNameOnAccount}<br /></#if>
                                            ${uiLabelMap.AccountingBankName}: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br />
                                            ${uiLabelMap.AccountingAccount}#: ${eftAccount.accountNumber}
                                            <#else>
                                            ${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
                                            </#if>
                                        </div>
                                    </td>
                                    <td width="10%">
                                        <#if !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                            <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                                                <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                                                    <input type="hidden" name="orderId" value="${orderId}" />
                                                    <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                    <input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
                                                    <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
                                                </form>
                                            </#if>
                                        </#if>
                                    </td>
                                </tr>
                                    <#if paymentList?has_content>
                                    <tr>
                                        <td align="right" valign="top" width="29%">
                                            <div>&nbsp;<span class="label">${uiLabelMap.AccountingInvoicePayments}</span></div>
                                        </td>
                                        <td width="1%">&nbsp;</td>
                                        <td width="60%">
                                            <div>
                                                <#list paymentList as paymentMap>
                                                    <a href="/accounting/control/paymentOverview?paymentId=${paymentMap.paymentId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
                                                </#list>
                                            </div>
                                        </td>
                                    </tr>
                                    </#if>
                                <#elseif paymentMethod.paymentMethodTypeId?if_exists == "GIFT_CARD">
                                    <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")>
                                    <#if giftCard?exists>
                                        <#assign pmBillingAddress = giftCard.getRelatedOne("PostalAddress")?if_exists>
                                    </#if>
                                <tr>
                                    <td align="right" valign="top" width="29%">
                                        <div>&nbsp;<span class="label">${uiLabelMap.OrderGiftCard}</span>
                                            <#if orderPaymentPreference.maxAmount?has_content>
                                                <br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.000) isoCode=currencyUomId/>
                                            </#if>
                                        </div>
                                    </td>
                                    <td width="1%">&nbsp;</td>
                                    <td valign="top" width="60%">
                                        <div>
                                            <#if giftCard?has_content>
                                                <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                                ${giftCard.cardNumber?default("N/A")} [${giftCard.pinNumber?default("N/A")}]
                                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                                                <#else>
                                                    <@maskSensitiveNumber cardNumber=giftCard.cardNumber?if_exists/>
                                                    <#if !cardNumberDisplay?has_content>N/A</#if>
                                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                                                </#if>
                                            <#else>
                                            ${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
                                            </#if>
                                        </div>
                                    </td>
                                    <td width="10%">
                                        <#if !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                                            <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                                                <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                                                <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                                                    <input type="hidden" name="orderId" value="${orderId}" />
                                                    <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
                                                    <input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
                                                    <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
                                                </form>
                                            </#if>
                                        </#if>
                                    </td>
                                </tr>
                                <#if paymentList?has_content>
                                    <tr>
                                        <td align="right" valign="top" width="29%">
                                            <div>&nbsp;<span class="label">${uiLabelMap.AccountingInvoicePayments}</span></div>
                                        </td>
                                        <td width="1%">&nbsp;</td>
                                        <td width="60%">
                                            <div>
                                                <#list paymentList as paymentMap>
                                                    <a href="/accounting/control/paymentOverview?paymentId=${paymentMap.paymentId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
                                                </#list>
                                            </div>
                                        </td>
                                    </tr>
                                </#if>
                            </#if>
                        </#if>
                        <#if pmBillingAddress?has_content>
                            <tr><td>&nbsp;</td><td>&nbsp;</td><td colspan="3"><hr /></td></tr>
                            <tr>
                                <td align="right" valign="top" width="29%">&nbsp;</td>
                                <td width="1%">&nbsp;</td>
                                <td valign="top" width="60%">
                                    <div>
                                        <#if pmBillingAddress.toName?has_content><span class="label">${uiLabelMap.CommonTo}</span>&nbsp;${pmBillingAddress.toName}<br /></#if>
                                        <#if pmBillingAddress.attnName?has_content><span class="label">${uiLabelMap.CommonAttn}</span>&nbsp;${pmBillingAddress.attnName}<br /></#if>
                                    ${pmBillingAddress.address1}<br />
                                        <#if pmBillingAddress.address2?has_content>${pmBillingAddress.address2}<br /></#if>
                                    ${pmBillingAddress.city}<#if pmBillingAddress.stateProvinceGeoId?has_content>, ${pmBillingAddress.stateProvinceGeoId} </#if>
                                    ${pmBillingAddress.postalCode?if_exists}<br />
                                    ${pmBillingAddress.countryGeoId?if_exists}
                                    </div>
                                </td>
                                <td width="10%">&nbsp;</td>
                            </tr>
                            <#if paymentList?has_content>
                                <tr>
                                    <td align="right" valign="top" width="29%">
                                        <div>&nbsp;<span class="label">${uiLabelMap.AccountingInvoicePayments}</span></div>
                                    </td>
                                    <td width="1%">&nbsp;</td>
                                    <td width="60%">
                                        <div>
                                            <#list paymentList as paymentMap>
                                                <a href="/accounting/control/paymentOverview?paymentId=${paymentMap.paymentId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
                                            </#list>
                                        </div>
                                    </td>
                                </tr>
                            </#if>
                        </#if>
                    </#list>



                <#-- invoices -->
                <#if invoices?has_content>
                    <tr><td colspan="4"><hr /></td></tr>
                    <tr>
                        <td align="right" valign="top" width="29%">&nbsp;<span class="label">${uiLabelMap.OrderInvoices}</span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="60%">
                            <#list invoices as invoice>
                                <#assign invoiceGv = delegator.findOne("Invoice", {"invoiceId" : invoice}, false)>
                                <#--<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : invoiceGv.statusId}, false)>-->
                                <div>
                                    <#--${uiLabelMap.CommonNbr}<a target="_blank" href="/accounting/control/invoiceOverview?invoiceId=${invoice}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${invoice}</a>-->
                                    <#--<block style="margin-right:12px;font-weight:bold;"> ${statusItem.description?if_exists} </block>-->
                                        ${invoice} <a target="_blank" href="/accounting/control/invoice.pdf?invoiceId=${invoice}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">PDF</a>
                                </div>
                            </#list>
                        </td>
                        <td width="10%">&nbsp;</td>
                    </tr>
                </#if>
            <#else>
                <tr>
                    <td colspan="4" align="center">${uiLabelMap.OrderNoOrderPaymentPreferences}</td>
                </tr>
            </#if>
            <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED")) && (paymentMethodValueMaps?has_content)>
                <tr><td colspan="4"><hr /></td></tr>
                <tr><td colspan="4">
                    <form name="addPaymentMethodToOrder" method="post" action="<@ofbizUrl>addPaymentMethodToOrder</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                        <table class="basic-table" cellspacing='0'>
                            <tr>
                                <td width="29%" align="right" nowrap="nowrap"><span class="label">${uiLabelMap.AccountingPaymentMethod}</span></td>
                                <td width="1%">&nbsp;</td>
                                <td width="60%" nowrap="nowrap">
                                    <select name="paymentMethodId">
                                        <#list paymentMethodValueMaps as paymentMethodValueMap>
                                            <#assign paymentMethod = paymentMethodValueMap.paymentMethod/>
                                            <option value="${paymentMethod.get("paymentMethodId")?if_exists}">
                                                <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
                                                    <#assign creditCard = paymentMethodValueMap.creditCard/>
                                                    <#if (creditCard?has_content)>
                                                        <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                                        ${creditCard.cardType?if_exists} <@maskSensitiveNumber cardNumber=creditCard.cardNumber?if_exists/> ${creditCard.expireDate?if_exists?string("dd/MM/yyyy")}
                                                        <#else>
                                                        ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                                        </#if>
                                                    </#if>
                                                <#else>
                                                ${paymentMethod.paymentMethodTypeId?if_exists}
                                                    <#if paymentMethod.description?exists>${paymentMethod.description}</#if>
                                                    (${paymentMethod.paymentMethodId})
                                                </#if>
                                            </option>
                                        </#list>
                                    </select>
                                </td>
                                <td width="10%">&nbsp;</td>
                            </tr>
                            <#assign openAmount = orderReadHelper.getOrderOpenAmount()>
                            <tr>
                                <td width="29%" align="right"><span class="label">${uiLabelMap.AccountingAmount}</span></td>
                                <td width="1%">&nbsp;</td>
                                <td width="60%" nowrap="nowrap">
                                    <input type="text" name="maxAmount" value="${openAmount}"/>
                                </td>
                                <td width="10%">&nbsp;</td>
                            </tr>
                            <tr>
                                <td align="right" valign="top" width="29%">&nbsp;</td>
                                <td width="1%">&nbsp;</td>
                                <td valign="top" width="60%">
                                    <input type="submit" value="${uiLabelMap.CommonAdd}" class="smallSubmit"/>
                                </td>
                                <td width="10%">&nbsp;</td>
                            </tr>
                        </table>
                    </form>
                </td></tr>
                </#if>
            </#if>
        </table>
    </div>
</div>
