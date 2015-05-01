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

<#if shipment?exists>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.PageTitleEditShipmentItems}</li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">
        <table cellspacing="0" class="basic-table">
            <tr class="header-row">
                <td>${uiLabelMap.ProductItem}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>${uiLabelMap.ProductQuantity}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <#assign alt_row = false>
            <#list shipmentItemDatas as shipmentItemData>
                <#assign shipmentItem = shipmentItemData.shipmentItem>
                <#assign itemIssuances = shipmentItemData.itemIssuances>
                <#assign shipmentPackageContents = shipmentItemData.shipmentPackageContents>
                <#assign product = shipmentItemData.product?if_exists>
                <#assign totalQuantityPackaged = shipmentItemData.totalQuantityPackaged>
                <#assign totalQuantityToPackage = shipmentItemData.totalQuantityToPackage>
                <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                    <td>${shipmentItem.shipmentItemSeqId}</td>
                    <td colspan="2">${(product.internalName)?if_exists} <a href="/catalog/control/EditProduct?productId=${shipmentItem.productId?if_exists}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${shipmentItem.productId?if_exists}</a></td>
                    <td>${shipmentItem.quantity?default("&nbsp;")}</td>
                    <td>${shipmentItem.shipmentContentDescription?default("&nbsp;")}</td>
                    <td><a href="javascript:document.deleteShipmentItem${shipmentItemData_index}.submit();" class="btn btn-mini btn-danger">${uiLabelMap.CommonDelete}</a>
                    <a href="javascript:document.findInventories${shipmentItemData_index}.submit();" class="btn btn-mini btn-danger">Inventories</a></td>
                </tr>
                <form name="deleteShipmentItem${shipmentItemData_index}" method="post" action="<@ofbizUrl>deleteTransferShipmentItem</@ofbizUrl>">
                    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                    <input type="hidden" name="shipmentItemSeqId" value="${shipmentItem.shipmentItemSeqId}"/>
                </form>
                <form name="findInventories${shipmentItemData_index}" method="get" action="<@ofbizUrl>findInventoriesForShipment</@ofbizUrl>">
                    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                    <input type="hidden" name="shipmentItemSeqId" value="${shipmentItem.shipmentItemSeqId}"/>
                    <input type="hidden" name="productId" value="${shipmentItem.productId}"/>
                    <input type="hidden" name="quantity" value="${shipmentItem.quantity}"/>
                </form>
                <#list itemIssuances as itemIssuance>
                    <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                        <td>&nbsp;</td>
                        <td><span class="label">${uiLabelMap.ProductInventory}</span> <a href="<@ofbizUrl>EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId?if_exists}</@ofbizUrl>" class="btn-link">${itemIssuance.inventoryItemId?if_exists}</a></td>
                        <td>${itemIssuance.quantity?if_exists}</td>
                        <td></td>
                        <td class="label"></td>
                        <td>&nbsp;<#-- don't allow a delete, need to implement a cancel issuance <a href="<@ofbizUrl>deleteShipmentItemIssuance?shipmentId=${shipmentId}&amp;itemIssuanceId=${itemIssuance.itemIssuanceId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonDelete}</a> --></td>
                    </tr>
                </#list>
                <#list shipmentPackageContents as shipmentPackageContent>
                    <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                        <td>&nbsp;</td>
                        <td><span class="label">${uiLabelMap.ProductPackage}</span> ${shipmentPackageContent.shipmentPackageSeqId}</td>
                        <td>${shipmentPackageContent.quantity?if_exists}&nbsp;</td>
                        <#if shipmentPackageContent.subProductId?has_content>
                            <td><span class="label">${uiLabelMap.ProductSubProduct}</span> ${shipmentPackageContent.subProductId}</td>
                            <td>${shipmentPackageContent.subProductQuantity?if_exists}</td>
                        <#else>
                            <td colspan="2"><a href="javascript:document.deleteShipmentItemPackageContent${shipmentItemData_index}${shipmentPackageContent_index}.submit();" class="btn btn-mini btn-danger">${uiLabelMap.CommonDelete}</a></td>
                        </#if>
                        <td></td>
                    </tr>
                    <form name="deleteShipmentItemPackageContent${shipmentItemData_index}${shipmentPackageContent_index}" method="post" action="<@ofbizUrl>deleteShipmentItemPackageContent</@ofbizUrl>">
                        <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                        <input type="hidden" name="shipmentItemSeqId" value="${shipmentPackageContent.shipmentItemSeqId}"/>
                        <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackageContent.shipmentPackageSeqId}"/>
                    </form>
                </#list>
                <#if (totalQuantityToPackage > 0)>
                    <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                        <form action="<@ofbizUrl>createTransferShipmentItemPackageContent</@ofbizUrl>" method="post" name="createShipmentPackageContentForm${shipmentItemData_index}">
                            <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                            <input type="hidden" name="shipmentItemSeqId" value="${shipmentItem.shipmentItemSeqId}"/>
                            <td>&nbsp;</td>
                            <td>
                                <div><span class="label">${uiLabelMap.ProductAddToPackage}</span>
                                    <select name="shipmentPackageSeqId">
                                        <#list shipmentPackages as shipmentPackage>
                                            <option>${shipmentPackage.shipmentPackageSeqId}</option>
                                        </#list>
                                        <option value="New">${uiLabelMap.CommonNew}</option><!-- Warning: the "New" value cannot be translated because it is used in secas -->
                                    </select>
                                </div>
                            </td>
                            <td>
                                <div>
                                    <input type="text" name="quantity" size="5" value="${totalQuantityToPackage}"/>
                                    <a href="javascript:document.createShipmentPackageContentForm${shipmentItemData_index}.submit()" class="btn btn-success">${uiLabelMap.CommonAdd}</a>
                                </div>
                            </td>
                            <td colspan="2">&nbsp;</td>
                            <td>&nbsp;</td>
                        </form>
                    </tr>
                </#if>
            <#-- toggle the row color -->
                <#assign alt_row = !alt_row>
            </#list>
        </table>
        <form action="<@ofbizUrl>createTransferShipmentItem</@ofbizUrl>" method="post" name="createShipmentItemForm" id="createShipmentItemForm">
            <table cellspacing="0" class="basic-table">
                <tr>
                    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                    <td><span class="label">${uiLabelMap.ProductNewItem}</span></td>
                    <td><span class="label">${uiLabelMap.ProductProductId} <span><font color="red">*</font></span></span>
                      <@htmlTemplate.lookupField formName="createShipmentItemForm" name="productId" id="productId" fieldFormName="LookupProduct" className="required"/>
                      <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('productId,<@ofbizUrl>LookupProduct</@ofbizUrl>,ajaxLookup=Y&amp;searchValueField=productId', true);</script>
                    </td>
                    <td><span class="label">${uiLabelMap.ProductQuantity} <span><font color="red">*</font></span></span>
                        <input type="text" name="quantity" id="shipmentQty" class="quantity required" size="5" value=""/>
                    </td>
                    <td colspan="2">&nbsp;</td>
                    <#-- <td colspan="2"><span class="label">${uiLabelMap.ProductProductDescription}</span> <input name="shipmentContentDescription" size="30" maxlength="255"/></td>
                    <td><a href="javascript:document.createShipmentItemForm.submit()" class="btn btn-success">${uiLabelMap.CommonCreate}</a></td> -->
                    <td><input type="submit" class="btn btn-success" value="${uiLabelMap.CommonCreate}"/></td>
                </tr>
            </table>
        </form>
        <script>
            var form = document.createShipmentItemForm;
            jQuery(form).validate();
        </script>
    </div>
</div>
<#else>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.ProductShipmentNotFoundId} : [${shipmentId?if_exists}]</li>
        </ul>
        <br class="clear"/>
    </div>
</div>
</#if>