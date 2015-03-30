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

<#-- Continuation of showcart.ftl:  List of order items and forms to modify them. -->
<#macro showAssoc productAssoc>
  <#assign productAssocType = (delegator.findOne("ProductAssocType", {"productAssocTypeId" : productAssoc.productAssocTypeId}, false))/>
  <#assign assocProduct = (delegator.findOne("Product", {"productId" : productAssoc.productIdTo}, false))/>
  <#if assocProduct?has_content>
    <td><a href="<@ofbizUrl>/product?product_id=${productAssoc.productIdTo}</@ofbizUrl>"class="btn-link">${productAssoc.productIdTo}</a></td>
    <td>- ${(assocProduct.productName)?if_exists}<i>(${(productAssocType.description)?default("Unknown")})</i></td>
  </#if>
</#macro>
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery.calculator.css</@ofbizContentUrl>" type="text/css"/>
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery.calculator.js</@ofbizContentUrl>"></script>
<div class="screenlet">
    <div class="screenlet-title-bar">    
        <div class='h3'>${uiLabelMap.OrderOrderItems}</div>
    </div>
    <div class="screenlet-body">
  <#if (shoppingCartSize > 0)>
    <form method="post" action="<@ofbizUrl>modifycart</@ofbizUrl>" name="cartform" style="margin: 0;" id="cartform">
      <input type="hidden" name="removeSelected" value="false"/>
      <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
        <input type="hidden" name="finalizeReqShipInfo" value="false"/>
        <input type="hidden" name="finalizeReqOptions" value="false"/>
        <input type="hidden" name="finalizeReqPayInfo" value="false"/>
        <input type="hidden" name="finalizeReqAdditionalParty" value="false"/>
      </#if>
      <table cellspacing="0" cellpadding="1" border="1" width="100%" class="basic-table">
        <tr class="header-row-2">
          <td>&nbsp;</td>
          <td colspan="2">
            <div>
              <b>${uiLabelMap.ProductProduct}</b>
            </div>
          </td>
          <td align=""><div><b>${uiLabelMap.OrderQuantity}</b></div></td>
            <td align=""><div><b>${uiLabelMap.CommonUom}</b></div></td>
          <td align=""><div><b>${uiLabelMap.CommonUnitPrice}</b></div></td>
          <td align="right"><div align="right"><b>${uiLabelMap.OrderItemTotal}</b></div></td>
          <td align="center"><div align="center"><input type="checkbox" name="selectAll" value="0" onclick="javascript:toggleAll(this);" /></div></td>
        </tr>

        <#assign itemsFromList = false>
        <#list shoppingCart.items() as cartLine>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
          <tr valign="top" <#if cartLine_index%2!=0>class="alternate-row"</#if>>
            <td>&nbsp;${cartLine_index+1}</td>
            <td>
          <table border="3" width="100%">
          <tr <#if cartLine_index%2!=0>class="alternate-row"</#if>>
          <td colspan="2">
                <div>
                  <#if cartLine.getProductId()?exists>
                    <#-- product item -->
              		<a target="EditProduct" href="/catalog/control/EditProduct?productId=${cartLine.getProductId()}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${cartLine.getProductId()}</a>
                    <i>${cartLine.getDescription()?if_exists}</i>
                    <#if shoppingCart.getOrderType() != "PURCHASE_ORDER">
                      <#-- only applies to sales orders, not purchase orders -->
                      <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                      <#assign itemProduct = cartLine.getProduct()>
                      <#assign isStoreInventoryNotRequiredAndNotAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequiredAndAvailable(request, itemProduct, cartLine.getQuantity(), false, false)>
                      <#if isStoreInventoryNotRequiredAndNotAvailable && itemProduct.inventoryMessage?has_content>
                          <b>(${itemProduct.inventoryMessage})</b>
                      </#if>
                    </#if>
                  <#else>
                    <#-- this is a non-product item -->
                    <b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
                  </#if>
                    <#-- display the item's features -->
                   <#assign features = "">
                   <#if cartLine.getFeaturesForSupplier(dispatcher,shoppingCart.getPartyId())?has_content>
                       <#assign features = cartLine.getFeaturesForSupplier(dispatcher, shoppingCart.getPartyId())>
                   <#elseif cartLine.getStandardFeatureList()?has_content>
                       <#assign features = cartLine.getStandardFeatureList()>
                   </#if>
                   <#if features?has_content>
                     <br /><i>${uiLabelMap.ProductFeatures}: <#list features as feature>${feature.description?default("")} </#list></i>
                   </#if>
                    <#-- show links to survey response for this item -->
                    <#if cartLine.getAttribute("surveyResponses")?has_content>
                        <br />Surveys:
                       <#list cartLine.getAttribute("surveyResponses") as surveyResponseId>
                        <a href="/content/control/ViewSurveyResponses?surveyResponseId=${surveyResponseId}&amp;externalLoginKey=${externalLoginKey}" class="btn-link" style="font-size: xx-small;">${surveyResponseId}</a>
                       </#list>
                    </#if>
                </div>
            </td></tr>
            <#if cartLine.getItemComment()?has_content>
              <tr>
              <td><div>${uiLabelMap.CommonComment} : </div></td>
                  <td><div>${cartLine.getItemComment()?if_exists}</div>
              </td>
              </tr>
            </#if>
            <#if cartLine.getDesiredDeliveryDate()?has_content>
              <tr><td><div>${uiLabelMap.OrderDesiredDeliveryDate}: </div></td>
                   <td><div>${cartLine.getDesiredDeliveryDate()?if_exists?string('dd-MM-yyyy')}</div> 
              </td></tr>
            </#if>
            <#-- inventory summary -->
            <#if cartLine.getProductId()?exists>
              <#assign productId = cartLine.getProductId()>
              <#assign product = cartLine.getProduct()>
              <tr>
                <td colspan="2">
                  <div>
                    <a href="/catalog/control/EditProductInventoryItems?productId=${productId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext"><b>${uiLabelMap.ProductInventory}</b></a>:
                    ${uiLabelMap.ProductAtp} = ${availableToPromiseMap.get(productId)}, ${uiLabelMap.ProductQoh} = ${quantityOnHandMap.get(productId)}
                    <#if Static["org.ofbiz.common.CommonWorkers"].hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG")>
                    ${uiLabelMap.ProductMarketingPackageATP} = ${mktgPkgATPMap.get(productId)}, ${uiLabelMap.ProductMarketingPackageQOH} = ${mktgPkgQOHMap.get(productId)}
                    <#if ( mktgPkgATPMap.get(cartLine.getProductId()) < cartLine.getQuantity()) && (shoppingCart.getOrderType() == 'SALES_ORDER')>
                      <#assign backOrdered = cartLine.getQuantity() - mktgPkgATPMap.get(cartLine.getProductId())/>
                      <span style="color: red; font-size: 15px;">[${backOrdered?if_exists}&nbsp;${uiLabelMap.OrderBackOrdered}]</span>
                    </#if>
                    </#if>
                    <#if (availableToPromiseMap.get(cartLine.getProductId()) <= 0) && (shoppingCart.getOrderType() == 'SALES_ORDER') && product.productTypeId != "DIGITAL_GOOD" && product.productTypeId != "MARKETING_PKG_AUTO" && product.productTypeId != "MARKETING_PKG_PICK">
                      <span style="color: red;">[${cartLine.getQuantity()}&nbsp;${uiLabelMap.OrderBackOrdered}]</span>
                    <#else>
                      <#if (availableToPromiseMap.get(cartLine.getProductId()) < cartLine.getQuantity()) && (shoppingCart.getOrderType() == 'SALES_ORDER') && product.productTypeId != "DIGITAL_GOOD" && product.productTypeId != "MARKETING_PKG_AUTO" && product.productTypeId != "MARKETING_PKG_PICK">
                        <#assign backOrdered = cartLine.getQuantity() - availableToPromiseMap.get(cartLine.getProductId())/>
                        <span style="color: red;">[${backOrdered?if_exists}&nbsp;${uiLabelMap.OrderBackOrdered}]</span>
                      </#if>
                    </#if>
                  </div>
                </td>
              </tr>
            </#if>
              <#if product.productTypeId=="ASSET_USAGE_OUT_IN">
                    <tr>
                        <td colspan="2">
                            <label class="label">From:</label><span style="text-align:right">${cartLine.getReservStart()}</span><br/>
                            <label class="label">To:</label><span style="text-align:right">${cartLine.getReservEnd()?if_exists}</span><br/>
                            <span><label class="label">Min hours to Bill:</label>${cartLine.getReservLength()}</span>
                        </td>
                    </tr>
              </#if>
       <#--   <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
              <#assign currentOrderItemType = cartLine.getItemTypeGenericValue()?if_exists/>
                <tr>
                  <td>
                    <div>
                      ${uiLabelMap.OrderOrderItemType}:
                      <select name="itemType_${cartLineIndex}">
                        <#if currentOrderItemType?has_content>
                        <option value="${currentOrderItemType.orderItemTypeId}">${currentOrderItemType.get("description",locale)}</option>
                        <option value="${currentOrderItemType.orderItemTypeId}">---</option>
                        </#if>
                        <option value="">&nbsp;</option>
                        <#list purchaseOrderItemTypeList as orderItemType>
                        <option value="${orderItemType.orderItemTypeId}">${orderItemType.get("description",locale)}</option>
                        </#list>
                      </select>
                    </div>
                  </td>
                </tr>
            </#if>  -->

            <#-- ship before/after date -->

            <#-- Show Associated Products (not for Variants) -->
      <#--   <#assign itemProductAssocList = cartLine.getProduct().getRelated("MainProductAssoc",
                Static["org.ofbiz.base.util.UtilMisc"].toList("productAssocTypeId", "sequenceNum"))?if_exists/>
            <#if itemProductAssocList?exists && itemProductAssocList?has_content>
              <tr><td colspan="8"><hr /></td></tr>
              <tr>
                <td>${uiLabelMap.AssociatedProducts}</td>
                <td><a href="<@ofbizUrl>LookupAssociatedProducts?productId=${cartLine.getProductId()?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.OrderQuickLookup}</a></td>
              </tr>
              <#assign relatedProdCount = 0/>
              <#list itemProductAssocList?if_exists as itemProductAssoc>
                <tr>
                  <#if "PRODUCT_VARIANT" != itemProductAssoc.productAssocTypeId>
                    <#assign relatedProdCount = relatedProdCount + 1/>
                    <#if (relatedProdCount > 3)><#break></#if>
                    <@showAssoc productAssoc=itemProductAssoc />
                  </#if>
                </tr>
              </#list>
            </#if> -->
          </table>

                <#if (cartLine.getIsPromo() && cartLine.getAlternativeOptionProductIds()?has_content)>
                  <#-- Show alternate gifts if there are any... -->
                  <div>${uiLabelMap.OrderChooseFollowingForGift}:</div>
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionProduct = delegator.findByPrimaryKeyCache("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", alternativeOptionProductId))>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(alternativeOptionProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
                    <div><a href="<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&amp;alternateGwpLine=${cartLineIndex}</@ofbizUrl>" class="buttontext">Select: ${alternativeOptionName?default(alternativeOptionProductId)}</a></div>
                  </#list>
                </#if>
            </td>

            <#-- gift wrap option -->
            <#assign showNoGiftWrapOptions = false>
            <td nowrap="nowrap" align="right">
              <#assign giftWrapOption = lineOptionalFeatures.GIFT_WRAP?if_exists>
              <#assign selectedOption = cartLine.getAdditionalProductFeatureAndAppl("GIFT_WRAP")?if_exists>
              <#if giftWrapOption?has_content>
                <select name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
                  <option value="NO^">${uiLabelMap.OrderNoGiftWrap}</option>
                  <#list giftWrapOption as option>
                    <option value="${option.productFeatureId}" <#if ((selectedOption.productFeatureId)?exists && selectedOption.productFeatureId == option.productFeatureId)>selected="selected"</#if>>${option.description} : <@ofbizCurrency amount=option.amount?default(0) isoCode=currencyUomId/></option>
                  </#list>
                </select>
              <#elseif showNoGiftWrapOptions>
                <select name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
                  <option value="">${uiLabelMap.OrderNoGiftWrap}</option>
                </select>
              <#else>
                &nbsp;
              </#if>
            </td>
            <#-- end gift wrap option -->
            <td nowrap="nowrap" align="">
              <div>
                <#if cartLine.getIsPromo() || cartLine.getShoppingListId()?exists>
                    ${cartLine.getQuantity()?string.number}
                <#else>
                    <input size="6" type="text" class="quantity" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}"/>
                </#if>
                <#if (cartLine.getSelectedAmount() > 0) >
                  <br /><b>${uiLabelMap.OrderAmount}:</b><br />
                  <input size="6" type="text" name="amount_${cartLineIndex}" value="${cartLine.getSelectedAmount()?string.number}"/>
                </#if>
              </div>
            </td>
            <#if product?has_content>
            <#assign  uomGv = delegator.findOne("Uom",{"uomId":product.quantityUomId},false)/>
            <td>${uomGv.description}</td>
            </#if>
            <td nowrap="nowrap" align="" style="margin-top:-10px">
             <#if cartLine.getDefaultProductPrice()?exists>
			  <b>Base Price:</b> <@ofbizCurrency amount=cartLine.getDefaultProductPrice()  isoCode=currencyUomId /><br/>
			  </#if>
              <div>
                <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
                  <@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=currencyUomId/>
                <#else>
                    <#if (cartLine.getSelectedAmount() > 0) >
                        <#assign price = cartLine.getBasePrice() / cartLine.getSelectedAmount()>
                    <#else>
                        <#assign price = cartLine.getBasePrice()>
                    </#if>
                    <input size="8" type="text" name="price_${cartLineIndex}" value="${price}" class="calPrice"/>
                </#if>
              </div>
          </td>
           <td nowrap="nowrap" align="right"><div><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=currencyUomId/></div></td>
          <#--  <td nowrap="nowrap" align="right"><div><@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId/></div></td>-->
            <td nowrap="nowrap" align="center"><div><#if !cartLine.getIsPromo()><input type="checkbox" name="selectedItem" value="${cartLineIndex}" onclick="javascript:checkToggle(this);"/><#else>&nbsp;</#if></div></td>
          </tr>
        </#list>

        <tr>
          <td colspan="5" align="right" valign="bottom">
            <div><b>${uiLabelMap.OrderCartTotal}:</b></div>
          </td>
          <td align="right" valign="bottom">
            <@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId/>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td colspan="8">&nbsp;</td>
        </tr>
      </table>
      <script>
      		var form = document.cartform;
     		jQuery(form).validate();
      </script>
    </form>
  <#else>
    <div>${uiLabelMap.OrderNoOrderItemsToDisplay}</div>
  </#if>
    </div>
</div>
<script>
       jQuery('.calPrice').calculator({calculatorClass: 'withBG', 
   			 showOn: 'both', buttonImageOnly: true, buttonImage: '/images/calculator.png'});
</script>
