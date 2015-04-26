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


<#if orderHeader?has_content>

<#-- price change rules -->
<#assign allowPriceChange = false/>
<#if (orderHeader.orderTypeId == 'PURCHASE_ORDER' || security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
    <#assign allowPriceChange = true/>
</#if>

<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
          <li class="h3">&nbsp;${uiLabelMap.OrderOrderItems}</li>
          <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) || security.hasRolePermission("ORDERMGR", "_UPDATE", "", "", session)>
              <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED">
                  <li><a href="javascript:document.updateItemInfo.action='<@ofbizUrl>cancelSelectedOrderItems</@ofbizUrl>';document.updateItemInfo.submit()">${uiLabelMap.OrderCancelSelectedItems}</a></li>
                  <li><a href="javascript:document.updateItemInfo.action='<@ofbizUrl>cancelOrderItem</@ofbizUrl>';document.updateItemInfo.submit()">${uiLabelMap.OrderCancelAllItems}</a></li>
                  <li><a href="<@ofbizUrl>orderview?${paramString}</@ofbizUrl>">${uiLabelMap.OrderViewOrder}</a></li>
              </#if>
          </#if>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">
        <#if !orderItemList?has_content>
            <span class="alert">${uiLabelMap.checkhelper_sales_order_lines_lookup_failed}</span>
        <#else>
            <form name="updateItemInfo" method="post" action="<@ofbizUrl>updateOrderItems</@ofbizUrl>" id="updateItemInfo">
            <input type="hidden" name="orderId" value="${orderId}"/>
            <input type="hidden" name="orderItemSeqId" value=""/>
            <input type="hidden" name="shipGroupSeqId" value=""/>
            <#if (orderHeader.orderTypeId == 'PURCHASE_ORDER')>
              <input type="hidden" name="supplierPartyId" value="${partyId}"/>
              <input type="hidden" name="orderTypeId" value="PURCHASE_ORDER"/>
            </#if>
            <table class="basic-table order-items" cellspacing="0">
                <tr class="header-row">
                    <td width="30%" style="border-bottom:none;">${uiLabelMap.ProductProduct}</td>
                    <td width="30%" style="border-bottom:none;">${uiLabelMap.CommonStatus}</td>
                    <td width="5%" style="border-bottom:none;" class="align-text">${uiLabelMap.OrderQuantity}</td>
                    <td width="10%" style="border-bottom:none;" class="align-text">${uiLabelMap.OrderUnitPrice}</td>
                    <td width="10%" style="border-bottom:none;" class="align-text">${uiLabelMap.OrderAdjustments}</td>
                    <td width="10%" style="border-bottom:none;" class="align-text">${uiLabelMap.OrderSubTotal}</td>
                    <td width="2%" style="border-bottom:none;">&nbsp;</td>
                    <td width="3%" style="border-bottom:none;">&nbsp;</td>
                </tr>
                <#list orderItemList as orderItem>
                    <#if orderItem.productId?exists> <#-- a null product may come from a quote -->
                      <#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
                      <tr><td colspan="8"><hr /></td></tr>
                      <tr>
                          <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                          <#assign productId = orderItem.productId?if_exists>
                          <#if productId?exists && productId == "shoppingcart.CommentLine">
                              <td colspan="8" valign="top">
                                  <span class="label">&gt;&gt; ${orderItem.itemDescription}</span>
                              </td>
                          <#else>
                              <td valign="top">
                                  <div>
                                      <#if orderHeader.statusId = "ORDER_CANCELLED" || orderHeader.statusId = "ORDER_COMPLETED">
                                      <#if productId?exists>
                                      ${orderItem.productId?default("N/A")} - ${orderItem.itemDescription?if_exists}
                                      <#elseif orderItemType?exists>
                                      ${orderItemType.description} - ${orderItem.itemDescription?if_exists}
                                      <#else>
                                      ${orderItem.itemDescription?if_exists}
                                      </#if>
                                      <#else>
                                      <#if productId?exists>
                                      <#assign orderItemName = orderItem.productId?default("N/A")/>
                                      <#elseif orderItemType?exists>
                                      <#assign orderItemName = orderItemType.description/>
                                      </#if>
                                      <p>${uiLabelMap.ProductProduct}&nbsp;${orderItemName}</p>
                                      <#if productId?exists>
                                          <#assign product = orderItem.getRelatedOneCache("Product")>
                                          <#if product.salesDiscontinuationDate?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
                                              <span class="alert">${uiLabelMap.OrderItemDiscontinued}: ${product.salesDiscontinuationDate}</span>
                                          </#if>
                                      </#if>
                                      ${uiLabelMap.CommonDescription}<br />
                                      ${orderItem.itemDescription?if_exists}
                                      </#if>
                                  </div>
                              </td>

                              <#-- now show status details per line item -->
                              <#assign currentItemStatus = orderItem.getRelatedOne("StatusItem")>
                              <td>
                                  ${uiLabelMap.CommonCurrent}&nbsp;${currentItemStatus.get("description",locale)?default(currentItemStatus.statusId)}<br />
                                  <#assign orderItemStatuses = orderReadHelper.getOrderItemStatuses(orderItem)>
                                  <#list orderItemStatuses as orderItemStatus>
                                  <#assign loopStatusItem = orderItemStatus.getRelatedOne("StatusItem")>
                                  <#if orderItemStatus.statusDatetime?has_content>${orderItemStatus.statusDatetime.toString()}</#if>
                                  &nbsp;${loopStatusItem.get("description",locale)?default(orderItemStatus.statusId)}<br />
                                  </#list>
                                  <#assign returns = orderItem.getRelated("ReturnItem")?if_exists>
                                  <#if returns?has_content>
                                  <#list returns as returnItem>
                                  <#assign returnHeader = returnItem.getRelatedOne("ReturnHeader")>
                                  <#if returnHeader.statusId != "RETURN_CANCELLED">
                                  <div class="alert">
                                      <span class="label">${uiLabelMap.OrderReturned}</span> #<a href="<@ofbizUrl>returnMain?returnId=${returnItem.returnId}</@ofbizUrl>" class="btn-link">${returnItem.returnId}</a>
                                  </div>
                                  </#if>
                                  </#list>
                                  </#if>
                              </td>
                              <td class="align-text" valign="top" nowrap="nowrap">
                                <#assign shippedQuantity = orderReadHelper.getItemShippedQuantity(orderItem)>
                                <#assign shipmentReceipts = delegator.findByAnd("ShipmentReceipt", {"orderId" : orderHeader.getString("orderId"), "orderItemSeqId" : orderItem.orderItemSeqId})/>
                                <#assign totalReceived = 0.0>
                                <#if shipmentReceipts?exists && shipmentReceipts?has_content>
                                  <#list shipmentReceipts as shipmentReceipt>
                                    <#if shipmentReceipt.quantityAccepted?exists && shipmentReceipt.quantityAccepted?has_content>
                                      <#assign  quantityAccepted = shipmentReceipt.quantityAccepted>
                                      <#assign totalReceived = quantityAccepted + totalReceived>
                                    </#if>
                                    <#if shipmentReceipt.quantityRejected?exists && shipmentReceipt.quantityRejected?has_content>
                                      <#assign  quantityRejected = shipmentReceipt.quantityRejected>
                                      <#assign totalReceived = quantityRejected + totalReceived>
                                    </#if>
                                  </#list>
                                </#if>
                                <#if orderHeader.orderTypeId == "PURCHASE_ORDER">
                                  <#assign remainingQuantity = ((orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - totalReceived?double)>
                                <#else>
                                  <#assign remainingQuantity = ((orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0)) - shippedQuantity?double)>
                                </#if>
                                  ${uiLabelMap.OrderOrdered}&nbsp;${orderItem.quantity?default(0)?string.number}&nbsp;&nbsp;<br />
                                  ${uiLabelMap.OrderCancelled}:&nbsp;${orderItem.cancelQuantity?default(0)?string.number}&nbsp;&nbsp;<br />
                                  ${uiLabelMap.OrderRemaining}:&nbsp;${remainingQuantity}&nbsp;&nbsp;<br />
                              </td>
                              <td class="align-text" valign="top" nowrap="nowrap">
                                  <#-- check for permission to modify price -->
                                  <#if (allowPriceChange)>
                                      <input type="text" size="8" class="currency" name="ipm_${orderItem.orderItemSeqId}" value="<@ofbizAmount amount=orderItem.unitPrice/>"/>
                                      &nbsp;<input type="checkbox" name="opm_${orderItem.orderItemSeqId}" value="Y"/>
                                  <#else>
                                      <div><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/> / <@ofbizCurrency amount=orderItem.unitListPrice isoCode=currencyUomId/></div>
                                  </#if>
                              </td>
                              <td class="align-text" valign="top" nowrap="nowrap">
                                  <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false) isoCode=currencyUomId/>
                              </td>
                              <td class="align-text" valign="top" nowrap="nowrap">
                                  <#if orderItem.statusId != "ITEM_CANCELLED">
                                  <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
                                  <#else>
                                  <@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
                                  </#if>
                              </td>
                              <td>&nbsp;</td>
                          </#if>
                      </tr>

                      <#-- now update/cancel reason and comment field -->
                      <#if orderItem.statusId != "ITEM_CANCELLED" && orderItem.statusId != "ITEM_COMPLETED" && ("Y" != orderItem.isPromo?if_exists)>
                        <tr>
                            <td colspan="4"><span class="label">${uiLabelMap.OrderReturnReason}</span>
                            <select name="irm_${orderItem.orderItemSeqId}">
                              <option value="">&nbsp;</option>
                              <#list orderItemChangeReasons as reason>
                                <option value="${reason.enumId}">${reason.get("description",locale)?default(reason.enumId)}</option>
                              </#list>
                            </select>
                            <span class="label">${uiLabelMap.CommonComments}</span>
                            <input type="text" name="icm_${orderItem.orderItemSeqId}" value="" size="30" maxlength="60"/>
                            </td>
                            <td colspan="3"><span class="label">Home Service</span>
                                <input type="checkbox" name="ihm_${orderItem.orderItemSeqId}" value="Y" <#if orderItem.homeService?exists && orderItem.homeService=='Y'>checked='CHECKED'</#if>"/>
                            </td>
                            <#if orderItem.authorized?exists && orderItem.authorized!='Y'>
                            <td colspan="3"><span class="label">Authorization</span>
                                <input type="checkbox" name="iau_${orderItem.orderItemSeqId}" value="Y"/>
                                <span class="label">Authorization Number</span>
                                <input type="text" name="ian_${orderItem.orderItemSeqId}" value="" size="30" maxlength="60"/>
                            </td>
                            </#if>
                        </tr>
                      </#if>
                      <#assign orderItemAdjustments = delegator.findByAnd("OrderItemAdjustment","orderId",orderItem.orderId,"orderItemSeqId",orderItem.orderItemSeqId)>
                        <#list orderItemAdjustments as orderItemAdjustment>
                        <tr>
                            <td class="align-text" colspan="2"><span class="label">${orderItemAdjustment.comments?if_exists}</span></td>
                        	<td><@ofbizCurrency amount=orderItemAdjustment.amount  isoCode=currencyUomId/></td>
                        </tr>
                        </#list>
                      <#-- now show adjustment details per line item -->
                      <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
                      <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
                          <#list orderItemAdjustments as orderItemAdjustment>
                              <#assign adjustmentType = orderItemAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                              <tr>
                                  <td class="align-text" colspan="2">
                                      <span class="label">${uiLabelMap.OrderAdjustment}</span>&nbsp;${adjustmentType.get("description",locale)}&nbsp;
                                      ${orderItemAdjustment.get("description",locale)?if_exists} (${orderItemAdjustment.comments?default("")})
                                      <#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
                                        <#if orderItemAdjustment.primaryGeoId?has_content>
                                            <#assign primaryGeo = orderItemAdjustment.getRelatedOneCache("PrimaryGeo")/>
                                            <span class="label">${uiLabelMap.OrderJurisdiction}</span>&nbsp;${primaryGeo.geoName} [${primaryGeo.abbreviation?if_exists}]
                                            <#if orderItemAdjustment.secondaryGeoId?has_content>
                                                <#assign secondaryGeo = orderItemAdjustment.getRelatedOneCache("SecondaryGeo")/>
                                                (<span class="label">${uiLabelMap.CommonIn}</span>&nbsp;${secondaryGeo.geoName} [${secondaryGeo.abbreviation?if_exists}])
                                            </#if>
                                        </#if>
                                        <#if orderItemAdjustment.sourcePercentage?exists><span class="label">Rate</span>&nbsp;${orderItemAdjustment.sourcePercentage}</#if>
                                        <#if orderItemAdjustment.customerReferenceId?has_content><span class="label">Customer Tax ID</span>&nbsp;${orderItemAdjustment.customerReferenceId}</#if>
                                        <#if orderItemAdjustment.exemptAmount?exists><span class="label">Exempt Amount</span>&nbsp;${orderItemAdjustment.exemptAmount}</#if>
                                      </#if>
                                  </td>
                                  <td>&nbsp;</td>
                                  <td>&nbsp;</td>
                                  <td class="align-text">
                                      <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem) isoCode=currencyUomId/>
                                  </td>
                                  <td colspan="3">&nbsp;</td>
                              </tr>
                          </#list>
                      </#if>

                      <#-- now show ship group info per line item -->
                      <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc")?if_exists>
                      <#if orderItemShipGroupAssocs?has_content>
                          <tr><td colspan="8">&nbsp;</td></tr>
                          <#list orderItemShipGroupAssocs as shipGroupAssoc>
                              <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup")>
                              <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
                              <tr>
                                  <td class="align-text" colspan="2">
                                      <span class="label">${uiLabelMap.OrderShipGroup}</span>&nbsp;[${shipGroup.shipGroupSeqId}] ${shipGroupAddress.address1?default("${uiLabelMap.OrderNotShipped}")}
                                  </td>
                                  <td align="center">
                                      <input type="text" class="quantity" name="iqm_${shipGroupAssoc.orderItemSeqId}:${shipGroupAssoc.shipGroupSeqId}" size="6" value="${shipGroupAssoc.quantity?string.number}"/>
                                  </td>
                                  <td colspan="4">&nbsp;</td>
                                  <td>
                                      <#assign itemStatusOkay = (orderItem.statusId != "ITEM_CANCELLED" && orderItem.statusId != "ITEM_COMPLETED" && (shipGroupAssoc.cancelQuantity?default(0) < shipGroupAssoc.quantity?default(0)) && ("Y" != orderItem.isPromo?if_exists))>
                                      <#if (security.hasEntityPermission("ORDERMGR", "_ADMIN", session) && itemStatusOkay) || (security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && itemStatusOkay && orderHeader.statusId != "ORDER_SENT")>
                                          <input type="checkbox" name="selectedItem" value="${orderItem.orderItemSeqId}" />
                                          <a href="javascript:document.updateItemInfo.action='<@ofbizUrl>cancelOrderItem</@ofbizUrl>';document.updateItemInfo.orderItemSeqId.value='${orderItem.orderItemSeqId}';document.updateItemInfo.shipGroupSeqId.value='${shipGroup.shipGroupSeqId}';document.updateItemInfo.submit()" class="btn btn-danger">${uiLabelMap.CommonCancel}</a>
                                      <#else>
                                          &nbsp;
                                      </#if>
                                  </td>
                              </tr>
                          </#list>
                      </#if>
                    </#if>
                </#list>
                <tr>
                    <td colspan="7">&nbsp;</td>
                    <td>
                        <input type="submit" value="${uiLabelMap.OrderUpdateItems}" class="btn btn-success"/>
                    </td>
                </tr>
            </table>
            <script>
      		var form = document.updateItemInfo;
     		jQuery(form).validate();
      </script>
            </form>
        </#if>
		<hr/>
        <#list orderHeaderAdjustments as orderHeaderAdjustment>
            <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
            <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
            <#assign orderAdjustmentId = orderHeaderAdjustment.get("orderAdjustmentId")>
            <#assign productPromoCodeId = ''>
            <#if adjustmentType.get("orderAdjustmentTypeId") == "PROMOTION_ADJUSTMENT" && orderHeaderAdjustment.get("productPromoId")?has_content>
                <#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo")>
                <#assign productPromoCodes = delegator.findByAnd("ProductPromoCode", {"productPromoId":productPromo.productPromoId})>
                <#assign orderProductPromoCode = ''>
                <#list productPromoCodes as productPromoCode>
                    <#if !(orderProductPromoCode?has_content)>
                        <#assign orderProductPromoCode = delegator.findOne("OrderProductPromoCode", {"productPromoCodeId":productPromoCode.productPromoCodeId, "orderId":orderHeaderAdjustment.orderId}, false)?if_exists>
                    </#if>
                </#list>
                <#if orderProductPromoCode?has_content>
                    <#assign productPromoCodeId = orderProductPromoCode.get("productPromoCodeId")>
                </#if>
            </#if>
            <#if adjustmentAmount != 0>
                <form name="updateOrderAdjustmentForm${orderAdjustmentId}" method="post" action="<@ofbizUrl>updateOrderAdjustment</@ofbizUrl>" id="updateOrderAdjustmentForm${orderAdjustmentId}">
                    <input type="hidden" name="orderAdjustmentId" value="${orderAdjustmentId?if_exists}"/>
                    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                    <table class="basic-table" cellspacing="0">
                        <tr>
                         <#--   <td class="align-text" width="55%">
                                &nbsp;${orderHeaderAdjustment.comments?if_exists}
                            </td>  -->
                            <td class="align-text" width="55%">
                                &nbsp;${adjustmentType.description?if_exists}
                            </td> 
                         <#--   <td nowrap="nowrap" width="30%">
                                <#if (allowPriceChange)>
                                    <input type="text" name="description" value="${orderHeaderAdjustment.get("description")?if_exists}" size="30" maxlength="60"/>
                                <#else>
                                    ${orderHeaderAdjustment.get("description")?if_exists}
                                </#if>
                            </td>  -->
                            <td nowrap="nowrap" width="15%">
                                <#if (allowPriceChange)>
                                    <input type="text" class="adjustment" name="amount" size="6" value="<@ofbizAmount amount=adjustmentAmount/>"/>
                                    <input class="btn btn-primary" type="submit" value="${uiLabelMap.CommonUpdate}"/>
                                    <a href="javascript:document.deleteOrderAdjustment${orderAdjustmentId}.submit();" class="btn btn-danger">${uiLabelMap.CommonDelete}</a>
                                <#else>
                                    <@ofbizAmount amount=adjustmentAmount/>
                                </#if>
                            </td>
                        </tr>
                    </table>
                    <script>
      		var form = document.updateOrderAdjustmentForm${orderAdjustmentId};
     		jQuery(form).validate();
      </script>
                </form>
                <form name="deleteOrderAdjustment${orderAdjustmentId}" method="post" action="<@ofbizUrl>deleteOrderAdjustment</@ofbizUrl>">
                    <input type="hidden" name="orderAdjustmentId" value="${orderAdjustmentId?if_exists}"/>
                    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                    <#if adjustmentType.get("orderAdjustmentTypeId") == "PROMOTION_ADJUSTMENT">
                        <input type="hidden" name="productPromoCodeId" value="${productPromoCodeId?if_exists}"/>
                    </#if>
                </form>
            </#if>
        </#list>

        <#-- add new adjustment -->
        <#if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session) || security.hasRolePermission("ORDERMGR", "_UPDATE", "", "", session)) && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_REJECTED">
            <form name="addAdjustmentForm" method="post" action="<@ofbizUrl>createOrderAdjustment</@ofbizUrl>" id="addAdjustmentForm">
              <#--  <input type="hidden" name="comments" value="Added manually by [${userLogin.userLoginId}]"/> -->
                 <input type="hidden" name="comments"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <table class="basic-table" cellspacing="0">
                    <tr><td colspan="3"><hr /></td></tr>
                    <tr>
                        <td class="align-text" width="55%">
                            <span class="label">Adjustment Type</span>&nbsp;
                            <select name="orderAdjustmentTypeId">
                                <#list orderAdjustmentTypes as type>
                                <option value="${type.orderAdjustmentTypeId}">${type.get("description",locale)?default(type.orderAdjustmentTypeId)}</option>
                                </#list>
                            </select>
                            <input type="hidden" name="shipGroupSeqId" value="_NA_"/>
                        </td>
                        <td width="30%"><input type="hidden" name="description" value="" size="30" maxlength="60"/></td>
                        <td width="15%">
                            <input type="text" class="adjustment" name="amount" size="6" value="<@ofbizAmount amount=0.00/>"/>
                            <input class="btn btn-success" type="submit" value="${uiLabelMap.CommonAdd}"/>
                        </td>
                    </tr>
                </table>
                <script>
      		var form = document.addAdjustmentForm;
     		jQuery(form).validate();
      </script>
            </form>
         
        </#if>
        

       <#-- subtotal -->
        <table class="basic-table" cellspacing="0">
            <tr><td colspan="4"><hr /></td></tr>
            <tr class="align-text">
              <td width="80%"><span class="label">${uiLabelMap.OrderItemsSubTotal}</span></td>
              <td width="10%" nowrap="nowrap"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></td>
              <td width="10%" colspan="2">&nbsp;</td>
            </tr>
 		<#--	 <#list taxAdjustments as taxAdj>
                	<#if "SALES_TAX"!=taxAdj.orderAdjustmentTypeId>
                    <#assign adjType =delegator.findOne("OrderAdjustmentType",true,"orderAdjustmentTypeId",taxAdj.orderAdjustmentTypeId)>
                    <#if adjType.assessableValueCalculation?exists && "Y"!=adjType.assessableValueCalculation?if_exists>
	                    <#if adjType.description?exists>
			                <tr>
	                    	<td width="80%" align="right">
	                    	<span class="label">Total ${adjType.description?if_exists}</span>
	                    	<#else>
	                    	<span class="label">Total ${adjType.comments?if_exists}</span>
	                    	</td>
	                    	<td align="right" nowrap="nowrap">
	                        	<@ofbizCurrency amount=taxAdj.amount isoCode=currencyUomId/>
	                    	</td>
		                    <td>&nbsp;</td>
	       				    </tr>
	                    </#if>
                    </#if>
                    <#else>
                     <tr>
                     <td  width="80%" align="right">
                     <#if taxAdj.description?exists>
                    	<span class="label">Total ${taxAdj.description?if_exists}</span>
                    	<#else>
                    	<span class="label">Total ${taxAdj.comments?if_exists}</span>
                    </#if> 
                    </td>
                    	<td align="right" nowrap="nowrap">
                        	<@ofbizCurrency amount=taxAdj.amount isoCode=currencyUomId/>
                    	</td>
                    <td>&nbsp;</td>
		            </tr>
                    </#if>
                </#list>   -->
              <#--  <#list orderAdjustmentGrouped as orderAdjustmentGrouped>
                	<#if "SALES_TAX"!=orderAdjustmentGrouped.orderAdjustmentTypeId>
                    <#assign adjType =delegator.findOne("OrderAdjustmentType",true,"orderAdjustmentTypeId",orderAdjustmentGrouped.orderAdjustmentTypeId)>
                    <#if adjType.assessableValueCalculation?exists && "Y"!=adjType.assessableValueCalculation?if_exists>
	                    <#if adjType.description?exists>
			                <tr>
	                    	<td width="80%" align="right">
	                    	<span class="label">Total ${orderAdjustmentGrouped.description?if_exists}</span>
	                    	<#else>
	                    	<span class="label">Total ${orderAdjustmentGrouped.comments?if_exists}</span>
	                    	</td>
	                    	<td align="right" nowrap="nowrap">
	                        	<@ofbizCurrency amount=orderAdjustmentGrouped.amount isoCode=currencyUomId/>
	                    	</td>
		                    <td>&nbsp;</td>
	       				    </tr>
	                    </#if>
                    </#if>
                    <#else>
                     <tr>
                     <td  width="80%" align="right">
                     <#if orderAdjustmentGrouped.description?exists>
                    	<span class="label">Total ${orderAdjustmentGrouped.description?if_exists}</span>
                    	<#else>
                    	<span class="label">Total ${orderAdjustmentGrouped.comments?if_exists}</span>
                    </#if> 
                    </td>
                    	<td align="right" nowrap="nowrap">
                        	<@ofbizCurrency amount=orderAdjustmentGrouped.amount isoCode=currencyUomId/>
                    	</td>
                    <td>&nbsp;</td>
		            </tr>
                    </#if>
                </#list> -->
           <#--     <#list orderHeaderAdjustments as orderHeaderAdjustment>
                    <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                    <#assign adjDesc = adjustmentType.description>
                    <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                    <#if adjustmentAmount != 0 && (!adjustmentType.assessableValueCalculation?exists || adjustmentType.assessableValueCalculation!="Y")>
                        <tr>
                             <td align="right">
                                <span class="label"><#if adjustmentType.description?has_content>${adjustmentType.description}
                                <#else>
                                ${adjustmentType.description?if_exists}
                                </#if>
                                </span>
                            </td>
                            <td align="right" nowrap="nowrap">
                                <@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                    </#if>
                </#list> -->
                <#list orderAdjustmentGrouped as orderAdjustmentGrouped>
                  <tr>
                     <td align="right">
                    	<span class="label">Total ${orderAdjustmentGrouped.comments?if_exists}</span>
                    </td>
                    <td align="right" nowrap="nowrap">
                        	<@ofbizCurrency amount=orderAdjustmentGrouped.amount isoCode=currencyUomId/>
                    </td>
                    <td>&nbsp;</td>
		         </tr>
               </#list>
            <#-- grand total -->
            <tr class="align-text">
              <td><span class="label">${uiLabelMap.OrderTotalDue}</span></td>
              <td nowrap="nowrap"><@ofbizCurrency amount=grandTotal isoCode=currencyUomId/></td>
              <td colspan="2">&nbsp;</td>
            </tr>
        </table>
    </div>
</div>
</#if>
