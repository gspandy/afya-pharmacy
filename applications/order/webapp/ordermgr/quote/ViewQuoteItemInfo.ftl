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
            <#if maySelectItems?default("N") == "Y">
                <a href="javascript:document.addCommonToCartForm.add_all.value='true';document.addCommonToCartForm.submit()" class="btn btn-success">${uiLabelMap.OrderAddAllToCart}</a>
            </#if>
        </div>
        <div class="h3">${uiLabelMap.OrderOrderQuoteItems}</div>
    </div>
    <div class="screenlet-body">
        <table cellspacing="0" class="basic-table">
            <tr valign="bottom" class="header-row">
                <td width="200px" style="padding-left:120px;">${uiLabelMap.ProductItem}</td>
                <td width="200px" colspan="2" style="padding-left:180px;">${uiLabelMap.ProductProduct}</td>
                <td width="200px" align="right" style="padding-left:150px;">${uiLabelMap.ProductQuantity}</td>
                <#-- <td width="10%" align="right">${uiLabelMap.OrderSelAmount}</td> -->
                <td width="200px" align="right" style="padding-left:150px;">Unit Price</td>
                <td width="200px" align="right" style="padding-left:150px;">${uiLabelMap.OrderAdjustments}</td>
                <td width="200px" align="right" style="padding-left:200px;">${uiLabelMap.CommonSubtotal}</td>
            </tr>
            <#assign totalQuoteAmount = 0.0>
            <#assign alt_row = false>
            <#list quoteItems as quoteItem>
                <#if quoteItem.productId?exists>
                    <#assign product = quoteItem.getRelatedOne("Product")>
                <#else>
                    <#assign product ={}> <#-- don't drag it along to the next iteration -->
                </#if>
                <#assign selectedAmount = quoteItem.selectedAmount?default(1)>
                <#if selectedAmount == 0>
                    <#assign selectedAmount = 1/>
                </#if>
                <#assign quoteItemAmount = quoteItem.quoteUnitPrice?default(0) * quoteItem.quantity?default(0) * selectedAmount>
                <#assign quoteItemAdjustments = quoteItem.getRelated("QuoteAdjustment")>
                <#assign totalQuoteItemAdjustmentAmount = 0.0>
                <#list quoteItemAdjustments as quoteItemAdjustment>
                    <#assign totalQuoteItemAdjustmentAmount = quoteItemAdjustment.amount?default(0) + totalQuoteItemAdjustmentAmount>
                </#list>
                <#assign totalQuoteItemAmount = quoteItemAmount + totalQuoteItemAdjustmentAmount>
                <#assign totalQuoteAmount = totalQuoteAmount + totalQuoteItemAmount>
                <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                    <td  align="right" valign="top"  width="10%">
                        <div style="text-align:right;">
                        <#if showQuoteManagementLinks?exists && quoteItem.isPromo?default("N") == "N">
                            <a href="<@ofbizUrl>EditQuoteItem?quoteId=${quoteItem.quoteId}&amp;quoteItemSeqId=${quoteItem.quoteItemSeqId}</@ofbizUrl>" class="btn btn-link">${quoteItem.quoteItemSeqId}</a>
                        <#else>
                            ${quoteItem.quoteItemSeqId}
                        </#if>
                        </div>
                    </td>
                    <td  align="right" valign="top"  width="35%" colspan="2">
                        <div>
                            ${(product.internalName)?if_exists}&nbsp;
                            <#if showQuoteManagementLinks?exists>
                                <a href="/catalog/control/EditProduct?productId=${quoteItem.productId?if_exists}&amp;externalLoginKey=${externalLoginKey}" class="btn btn-link">
                                  <#if quoteItem.productId?exists>
                                    ${quoteItem.productId}
                                  <#else>
                                    ${uiLabelMap.ProductCreateProduct}
                                  </#if>
                                </a>
                            <#else>
                                <a href="<@ofbizUrl>product?product_id=${quoteItem.productId?if_exists}</@ofbizUrl>" class="btn btn-link">${quoteItem.productId?if_exists}</a>
                            </#if>
                        </div>
                    </td>
                    <td align="right" valign="top"  width="10%">${quoteItem.quantity?if_exists}</td>
                    <#-- <td align="right" valign="top"  width="10%">${quoteItem.selectedAmount?if_exists}</td> -->
                    <td align="right" valign="top"  width="10%"><@ofbizCurrency amount=quoteItem.quoteUnitPrice isoCode=quote.currencyUomId/></td>
                    <td align="right" valign="top"  width="10%"><@ofbizCurrency amount=totalQuoteItemAdjustmentAmount isoCode=quote.currencyUomId/></td>
                    <td align="right" valign="top"  width="10%"><@ofbizCurrency amount=totalQuoteItemAmount isoCode=quote.currencyUomId/></td>
                    <td  width="5%"></td>
                </tr>
                <#-- now show adjustment details per line item -->
                <#list quoteItemAdjustments as quoteItemAdjustment>
                    <#assign adjustmentType = quoteItemAdjustment.getRelatedOne("OrderAdjustmentType")>
                    <tr>
                        <td align="right" colspan="6"><span class="label">${adjustmentType.get("description",locale)?if_exists}</span></td>
                        <td align="right"><@ofbizCurrency amount=quoteItemAdjustment.amount isoCode=quote.currencyUomId/></td>
                    </tr>
                </#list>
                <#-- toggle the row color -->
                <#assign alt_row = !alt_row>
            </#list>
            <tr><td colspan="7"><hr /></td></tr>
            <tr>
                <td align="right" class="label" colspan="6">${uiLabelMap.CommonSubtotal}</td>
                <td align="right"><@ofbizCurrency amount=totalQuoteAmount isoCode=quote.currencyUomId/></td>
            </tr>
            <tr><td colspan="7"><hr /></td></tr>
            <#assign totalQuoteHeaderAdjustmentAmount = 0.0>
            <#assign findAdjustment = false>
            <#list quoteAdjustments as quoteAdjustment>
                <#assign adjustmentType = quoteAdjustment.getRelatedOne("OrderAdjustmentType")>
                <#if !quoteAdjustment.quoteItemSeqId?exists>
                    <#assign totalQuoteHeaderAdjustmentAmount = quoteAdjustment.amount?default(0) + totalQuoteHeaderAdjustmentAmount>
                    <tr>
                      <td align="right" colspan="6"><span class="label">${adjustmentType.get("description",locale)?if_exists}</span></td>
                      <td align="right"><@ofbizCurrency amount=quoteAdjustment.amount isoCode=quote.currencyUomId/></td>
                    </tr>
                </#if>
                <#assign findAdjustment = true>
            </#list>
            <#assign grandTotalQuoteAmount = totalQuoteAmount + totalQuoteHeaderAdjustmentAmount>
            <#if findAdjustment>
            <tr><td colspan="7"><hr /></td></tr>
            </#if>
            <tr>
                <td align="right" colspan="6" class="label">${uiLabelMap.OrderGrandTotal}</td>
                <td align="right">
                    <@ofbizCurrency amount=grandTotalQuoteAmount isoCode=quote.currencyUomId/>
                </td>
            </tr>
        </table>
    </div>
</div>
