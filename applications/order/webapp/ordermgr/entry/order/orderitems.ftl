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

<div class="screenlet">
    <div class="screenlet-title-bar">
        <div class="boxlink">
            <#if maySelectItems?default(false)>
                <a href="javascript:document.addOrderToCartForm.add_all.value="true";document.addOrderToCartForm.submit()" class="lightbuttontext">${uiLabelMap.OrderAddAllToCart}</a>
                <a href="javascript:document.addOrderToCartForm.add_all.value="false";document.addOrderToCartForm.submit()" class="lightbuttontext">${uiLabelMap.OrderAddCheckedToCart}</a>
            </#if>
        </div>
        <div class="h3">${uiLabelMap.OrderOrderItems}</div>
    </div>
    <div class="screenlet-body">
        <table width="100%" border="0" cellpadding="0" class="basic-table">
          <tr valign="bottom">
            <td width="65%"><span><b>${uiLabelMap.ProductProduct}</b></span></td>
            <td width="5%" align="right"><span><b>${uiLabelMap.OrderQuantity}</b></span></td>
            <td width="10%" align="right"><span><b>UOM</b></span></td>
            <td width="10%" align="right"><span><b>${uiLabelMap.CommonUnitPrice}</b></span></td>
            <td width="10%" align="right"><span><b>${uiLabelMap.OrderAdjustments}</b></span></td>
            <td width="10%" align="right"><span><b>${uiLabelMap.OrderSubTotal}</b></span></td>
          </tr>
          <#list orderItems?if_exists as orderItem>
            <#assign itemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
            <tr><td colspan="6"><hr /></td></tr>
            <tr>
              <#if orderItem.productId?exists && orderItem.productId == "_?_">
                <td colspan="1" valign="top">
                  <b><div> &gt;&gt; ${orderItem.itemDescription}</div></b>
                </td>
              <#else>
                <td valign="top">
                  <div>
                    <#if orderItem.productId?exists>
                     <!-- <a href="<@ofbizUrl>product?product_id=${orderItem.productId}</@ofbizUrl>" class="btn-link">${orderItem.productId} - ${orderItem.itemDescription}</a> -->
                        <a target="EditProduct" href="/catalog/control/EditProduct?productId=${orderItem.productId}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${orderItem.productId} -${orderItem.itemDescription}</a>
                    <#else>
                      <b>${itemType?if_exists.description?if_exists}</b> : ${orderItem.itemDescription?if_exists}
                    </#if>
                  </div>

                </td>
                <td align="right" valign="top">
                  <div nowrap="nowrap">${orderItem.quantity?string.number}</div>
                </td>
                <td align="right" valign="top">
                <#assign productUomView = (delegator.findOne("ProductUomView", {"productId" : orderItem.productId}, true))!>
                  <#if productUomView?has_content>
                  	<div nowrap="nowrap">${productUomView.description?if_exists}</div>
                  	<#else>
                  	<div nowrap="nowrap"></div>
                  </#if>
                </td>
                <td align="right" valign="top">
                  <div nowrap="nowrap"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></div>
                </td>
                <td align="right" valign="top">
                  <div nowrap="nowrap"><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem) isoCode=currencyUomId/></div>
                </td>
                <td align="right" valign="top" nowrap="nowrap">
                  <div><@ofbizCurrency amount=localOrderReadHelper.getOrderItemSubTotal(orderItem) isoCode=currencyUomId/></div>
                </td>
                <#if maySelectItems?default(false)>
                  <td>
                    <input name="item_id" value="${orderItem.orderItemSeqId}" type="checkbox" />
                  </td>
                </#if>
              </#if>
            </tr>
            <#-- show info from workeffort if it was a rental item -->
            <#if orderItem.orderItemTypeId?exists && orderItem.orderItemTypeId == "RENTAL_ORDER_ITEM">
            <#--
                <#assign WorkOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment")?if_exists>
                <#if WorkOrderItemFulfillments?has_content>
                    <#list WorkOrderItemFulfillments as WorkOrderItemFulfillment>
                        <#assign workEffort = WorkOrderItemFulfillment.getRelatedOneCache("WorkEffort")?if_exists>
                          <tr><td>&nbsp;</td><td>&nbsp;</td><td colspan="8">
                              <div>${uiLabelMap.CommonFrom}: ${workEffort.estimatedStartDate?string("yyyy-MM-dd")} ${uiLabelMap.CommonTo}: ${workEffort.estimatedCompletionDate?string("yyyy-MM-dd")} ${uiLabelMap.OrderNbrPersons}: ${workEffort.reservPersons}</div></td></tr>
                        <#break>&lt;#&ndash; need only the first one &ndash;&gt;
                    </#list>
                </#if>-->
            <#assign cartItem = cart.findCartItem(orderItem_index)>
                <#if cartItem.itemType == "RENTAL_ORDER_ITEM">
                <tr><td>&nbsp;</td><td>&nbsp;</td><td colspan="8">
                        <span style="width:250px;"><b>${uiLabelMap.CommonFrom} </b>: ${cartItem.reservStart?string("yyyy-MM-dd")} </span>
                            <span style="width:250px"><b>${uiLabelMap.CommonTo}</b> : ${cartItem.reservEnd?string("yyyy-MM-dd")}</span>
                        <span style="width:250px"><b>Min hours to Bill </b>: ${cartItem.reservLength}</span>
                   </td></tr>
                </#if>
            </#if>
 
            <#-- now show adjustment details per line item -->
        <#--   <#assign itemAdjustments = localOrderReadHelper.getOrderItemAdjustments(orderItem)> -->
          <#assign itemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
            <#list itemAdjustments as orderItemAdjustment>
              <tr>
                <td align="right">
                  <div>
                    <b><i>${uiLabelMap.OrderAdjustment}</i>:</b> <b>${orderItemAdjustment.comments?if_exists}</b>&nbsp;
                    <#if orderItemAdjustment.description?has_content>: ${orderItemAdjustment.get("description",locale)}</#if>
                    <#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
                      <#if orderItemAdjustment.primaryGeoId?has_content>
                        <#assign primaryGeo = orderItemAdjustment.getRelatedOneCache("PrimaryGeo")/>
                        <#if primaryGeo.geoName?has_content>
                            <b>${uiLabelMap.OrderJurisdiction}:</b> ${primaryGeo.geoName} [${primaryGeo.abbreviation?if_exists}]
                        </#if>
                        <#if orderItemAdjustment.secondaryGeoId?has_content>
                          <#assign secondaryGeo = orderItemAdjustment.getRelatedOneCache("SecondaryGeo")/>
                          (<b>in:</b> ${secondaryGeo.geoName} [${secondaryGeo.abbreviation?if_exists}])
                        </#if>
                      </#if>
                      <#if orderItemAdjustment.sourcePercentage?exists><b>${uiLabelMap.OrderRate}:</b> ${orderItemAdjustment.sourcePercentage}%</#if>
                      <#if orderItemAdjustment.customerReferenceId?has_content><b>${uiLabelMap.OrderCustomerTaxId}:</b> ${orderItemAdjustment.customerReferenceId}</#if>
                      <#if orderItemAdjustment.exemptAmount?exists><b>${uiLabelMap.OrderExemptAmount}:</b> ${orderItemAdjustment.exemptAmount}</#if>
                    </#if>
                  </div>
                </td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
                <td>&nbsp;</td>
                <td align="right">
                  <div><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment) isoCode=currencyUomId/></div>
                </td>
                <td>&nbsp;</td>
                <#else>
                <td align="right">
                  <div><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment) isoCode=currencyUomId/></div>
                </td>
                </#if>
                <#if maySelectItems?default(false)><td>&nbsp;</td></#if>
              </tr>
            </#list>
           </#list>
           <#if !orderItems?has_content>
             <tr><td><font color="red">${uiLabelMap.checkhelpertotalsdonotmatchordertotal}</font></td></tr>
           </#if>

          <tr><td colspan="8"><hr /></td></tr>
          <tr>
            <td align="right" colspan="4"><div><b>${uiLabelMap.OrderSubTotal}</b></div></td>
          <#if orderSubTotal?exists>
            <td align="right" nowrap="nowrap"><div>&nbsp;<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></div></td>
          </#if>
          </tr>
          <#list orderHeaderAdjustments as orderHeaderAdjustment>
                    <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType",true)>
                    <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                    <#if adjustmentAmount != 0 && adjustmentType.assessableValueCalculation?exists && adjustmentType.assessableValueCalculation!="Y">
                        <tr>
                            <td align="right" colspan="5">
                                <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments}
                                <#else>
                                ${orderHeaderAdjustment.description?if_exists}
                                </#if>
                            </td>
                            <td align="right" nowrap="nowrap">
                                <@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
                            </td>
                            <td>&nbsp;</td>
                            
                        </tr>
                       <#else>
                        <tr>
                     <td align="right" colspan="4">
                        <div>
                          <b>
                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments}
                                <#elseif orderHeaderAdjustment.description?exists>
                                	${orderHeaderAdjustment.description}
                                <#else>
                                	${adjustmentType.description}
                                </#if>
                            </td>
                            <td align="right" nowrap="nowrap">
                                <@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
                            </td>
                            <td>&nbsp;</td>
                          </tr>
                         </#if>
                        </b>
                       </div>
                     </td>
                               
         </#list>
        <#list orderPreviewNewListMap as orderPreviewTaxAdjLis> 
       <tr>
         <td align="right" colspan="4"><div><b>${orderPreviewTaxAdjLis.comments?if_exists}</b></div></td>
            <#--  <td align="right" nowrap="nowrap"><div><#if orderTaxTotal?exists><@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/></#if> </div></td> -->
      <td align="right" nowrap="nowrap"><div><#if orderPreviewTaxAdjLis?exists><@ofbizCurrency amount=orderPreviewTaxAdjLis.amount isoCode=currencyUomId/></#if> </div></td>
          </tr> 
          </#list> 
             
           <tr>
             <td colspan=2></td><td colspan="8"><hr /></td>
           </tr>
          <tr>
            <td align="right" colspan="4"><div><b>${uiLabelMap.OrderGrandTotal}</b></div></td>
            <td align="right" nowrap="nowrap">
             <div><#if orderGrandTotal?exists><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/></#if></div>
            </td>
         </tr>
         
        </table>
    </div>
</div>