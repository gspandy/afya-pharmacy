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
<script language="JavaScript" type="text/javascript">
    function setNow(field) { eval('document.selectAllForm.' + field + '.value="${nowTimestamp}"'); }

    function validate(){
        var zpcInspection = document.getElementById('zpcInspection');
        var zraInspection = document.getElementById('zraInspection');
        var currencyMatch = selectAllForm.elements["currencyMatch"].value;
        var receiveProductButton = document.getElementById('receiveProductButton');

        if (currencyMatch == 'true') {
            if (zpcInspection.checked) {
                jQuery('#receiveProductButton').attr('disabled', false);
            }
            else {
                jQuery('#receiveProductButton').attr('disabled', true);
            }
        }

        if(currencyMatch == 'false') {
            if (zpcInspection.checked && zraInspection.checked) {
                jQuery('#receiveProductButton').attr('disabled', false);
            }
            else {
                jQuery('#receiveProductButton').attr('disabled', true);
            }
        }
    }
</script>
        <#if invalidProductId?exists>
            <div class="errorMessage">${invalidProductId}</div>
        </#if>
        <#if invalidOrderId?exists>
            <div class="errorMessage">${invalidOrderId}</div>
        </#if>
        <#-- Receiving Results -->
        <#if receivedItems?has_content>
          <h3>${uiLabelMap.ProductReceiptPurchaseOrder} ${purchaseOrder.orderId}</h3>
          <table class="basic-table" cellspacing="0">
            <tr class="header-row-2">
              <td>${uiLabelMap.ProductShipmentId}</td>
              <td>${uiLabelMap.ProductReceipt}</td>
              <td>${uiLabelMap.CommonDate}</td>
              <td>${uiLabelMap.ProductPo}</td>
              <td>${uiLabelMap.ProductLine}</td>
              <td>${uiLabelMap.ProductProductId}</td>
              <td>${uiLabelMap.ProductPerUnitPrice}</td>
              <td>${uiLabelMap.CommonRejected}</td>
              <td>${uiLabelMap.CommonAccepted}</td>
              <td>${uiLabelMap.Variance}</td>
              <td></td>
              <td></td>
              <td></td>
            </tr>
            <#list receivedItems as item>
              <form name="cancelReceivedItemsForm_${item_index}" method="post" action="<@ofbizUrl>cancelReceivedItems</@ofbizUrl>">
                <input type="hidden" name="receiptId" value ="${(item.receiptId)?if_exists}"/>
                <input type="hidden" name="purchaseOrderId" value ="${(item.orderId)?if_exists}"/>
                <input type="hidden" name="facilityId" value ="${facilityId?if_exists}"/>
                <tr>
                  <td><a href="<@ofbizUrl>ViewShipment?shipmentId=${item.shipmentId?if_exists}</@ofbizUrl>" class="btn-link">${item.shipmentId?if_exists} ${item.shipmentItemSeqId?if_exists}</a></td>
                  <td>${item.receiptId}</td>
                  <td>${item.getString("datetimeReceived").toString()}</td>
                  <td><a href="/ordermgr/control/orderview?orderId=${item.orderId}" class="btn-link">${item.orderId}</a></td>
                  <td>${item.orderItemSeqId}</td>
                  <td>${item.productId?default("Not Found")}</td>
                  <td>${item.unitCost?default(0)?string("##0.00")}</td>
                  <td>${item.quantityRejected?default(0)?string.number}</td>
                  <td>${item.quantityAccepted?string.number}</td>
                  <td>${item.quantityVariance?string.number}</td>
                  <td>
                    <#if (item.quantityAccepted?int > 0 || item.quantityRejected?int > 0)>
                      <a href="javascript:document.cancelReceivedItemsForm_${item_index}.submit();" class="btn btn-mini btn-danger">${uiLabelMap.CommonCancel}</a>
                    </#if>
                  </td>
                  <td>
                    <a target="_blank" href="<@ofbizUrl>GoodsReceivedNote.pdf?purchaseOrderId=${item.orderId?if_exists}&amp;shipmentId=${item.shipmentId?if_exists}&amp;facilityId=${facilityId?if_exists}</@ofbizUrl>" class="btn btn-link">Print GRN</a>
                  </td>
                  <td>
                    <a href="<@ofbizUrl>editGrnsEmail?orderId=${item.orderId?if_exists}&amp;shipmentId=${item.shipmentId?if_exists}&amp;facilityId=${facilityId?if_exists}</@ofbizUrl>" class="btn btn-link">Email</a>
                  </td>
                </tr>
              </form>
            </#list>
            <tr><td colspan="13"><hr /></td></tr>
          </table>
          <br />
        </#if>

        <#-- Single Product Receiving -->
        <#if requestParameters.initialSelected?exists && product?has_content>
          <form method="post" action="<@ofbizUrl>receiveSingleInventoryProduct</@ofbizUrl>" name="selectAllForm">
            <table class="basic-table" cellspacing="0">
              <#-- general request fields -->
              <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
              <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
              <#-- special service fields -->
              <input type="hidden" name="productId" value="${requestParameters.productId?if_exists}"/>
              <#if purchaseOrder?has_content>
              <#assign unitCost = firstOrderItem.unitPrice?default(standardCosts.get(firstOrderItem.productId)?default(0))/>
              <input type="hidden" name="orderId" value="${purchaseOrder.orderId}"/>
              <input type="hidden" name="orderItemSeqId" value="${firstOrderItem.orderItemSeqId}"/>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductPurchaseOrder}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <b>${purchaseOrder.orderId}</b>&nbsp;/&nbsp;<b>${firstOrderItem.orderItemSeqId}</b>
                  <#if 1 < purchaseOrderItemsSize>
                    (${uiLabelMap.ProductMultipleOrderItemsProduct} - ${purchaseOrderItemsSize}:1 ${uiLabelMap.ProductItemProduct})
                  <#else>
                    (${uiLabelMap.ProductSingleOrderItemProduct} - 1:1 ${uiLabelMap.ProductItemProduct})
                  </#if>
                </td>
              </tr>
              </#if>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductProductId}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <b>${requestParameters.productId?if_exists}</b>
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductProductName}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <a href="/catalog/control/EditProduct?productId=${product.productId}${externalKeyParam?if_exists}" target="catalog" class="btn-link">${product.internalName?if_exists}</a>
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductProductDescription}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  ${product.description?if_exists}
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductItemDescription}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <input type="text" name="itemDescription" size="30" maxlength="60"/>
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductInventoryItemType}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <select name="inventoryItemTypeId" size="1">
                    <#list inventoryItemTypes as nextInventoryItemType>
                      <option value="${nextInventoryItemType.inventoryItemTypeId}"
                        <#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
                          selected="selected"
                        </#if>
                      >${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td colspan="4">&nbsp;</td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductFacilityOwner}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <@htmlTemplate.lookupField formName="selectAllForm" name="ownerPartyId" id="ownerPartyId" fieldFormName="LookupPartyName"/>
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductDateReceived}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <#-- <input type="text" name="datetimeReceived" size="24" value="${nowTimestamp}" /> -->
                  <@htmlTemplate.renderDateTimeField name="datetimeReceived" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${nowTimestamp}" size="25" maxlength="30" id="datetimeReceived" dateType="date-time" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                  <#-- <a href="#" onclick="setNow("datetimeReceived")" class="buttontext">[Now]</a> -->
                </td>
              </tr>

              <#-- facility location(s) -->
              <#assign facilityLocations = (product.getRelatedByAnd("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)))?if_exists/>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductFacilityLocation}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <#if facilityLocations?has_content>
                    <select name="locationSeqId">
                      <#list facilityLocations as productFacilityLocation>
                        <#assign facility = productFacilityLocation.getRelatedOneCache("Facility")/>
                        <#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation")?if_exists/>
                        <#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOneCache("TypeEnumeration"))?if_exists/>
                        <option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.get("description",locale)})</#if>[${productFacilityLocation.locationSeqId}]</option>
                      </#list>
                      <option value="">${uiLabelMap.ProductNoLocation}</option>
                    </select>
                  <#else>
                      <@htmlTemplate.lookupField formName="selectAllForm" name="locationSeqId" id="locationSeqId" fieldFormName="LookupFacilityLocation"/> <!-- <#if parameters.facilityId?exists>?facilityId=${facilityId}</#if> -->
                  </#if>
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductRejectedReason}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <select name="rejectionId" size="1">
                    <option></option>
                    <#list rejectReasons as nextRejection>
                      <option value="${nextRejection.rejectionId}">${nextRejection.get("description",locale)?default(nextRejection.rejectionId)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductQuantityRejected}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <input type="text" name="quantityRejected" size="5" value="0" />
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductQuantityAccepted}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <input type="text" name="quantityAccepted" size="5" value="${defaultQuantity?default(1)?string.number}"/>
                </td>
              </tr>
              <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductPerUnitPrice}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                  <#-- get the default unit cost -->
                  <#if (!unitCost?exists || unitCost == 0.0)><#assign unitCost = standardCosts.get(product.productId)?default(0)/></#if>
                  <input type="text" name="unitCost" size="10" value="${unitCost}"/>
                </td>
              </tr>
              <tr>
                <td colspan="2">&nbsp;</td>
                <td>&nbsp;</td>
                <td colspan="2"><input type="submit" value="${uiLabelMap.CommonReceive}" class="btn"/></td>
              </tr>
            </table>
            <script language="JavaScript" type="text/javascript">
              document.selectAllForm.quantityAccepted.focus();
            </script>
          </form>

        <#-- Select Shipment Screen -->
        <#elseif requestParameters.initialSelected?exists && !requestParameters.shipmentId?exists>

        <#if invalidProductId?exists || invalidOrderId?exists>
        <h3>${uiLabelMap.ProductReceiveItem}</h3>
          <form name="selectAllForm" method="post" action="<@ofbizUrl>ReceiveInventory</@ofbizUrl>">
            <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
            <input type="hidden" name="initialSelected" value="Y"/>
            <table class="basic-table" cellspacing="0">
              <#-- <tr>
                <td class="label">${uiLabelMap.ProductPurchaseOrderNumber}</td>
                <td>
                    <@htmlTemplate.lookupField value="${requestParameters.purchaseOrderId?if_exists}" formName="selectAllForm" name="purchaseOrderId" id="purchaseOrderId" fieldFormName="LookupPurchaseOrderHeaderAndShipInfo"/>
                    <span class="tooltip">${uiLabelMap.ProductLeaveSingleProductReceiving}</span>
                </td>
              </tr> -->
              <tr>
                <td class="label">${uiLabelMap.ProductProductId}</td>
                <td>
                  <@htmlTemplate.lookupField value="${requestParameters.productId?if_exists}" formName="selectAllForm" name="productId" id="productId" fieldFormName="LookupProduct"/>
                  <span class="tooltip">${uiLabelMap.ProductLeaveEntirePoReceiving}</span>
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td>
                  <a href="javascript:document.selectAllForm.submit();" class="btn ">${uiLabelMap.ProductReceiveProduct}</a>
                </td>
              </tr>
            </table>
          </form>
        <#else>
          <h3>${uiLabelMap.ProductSelectShipmentReceive}</h3>
          <form method="post" action="<@ofbizUrl>ReceiveInventory</@ofbizUrl>" name="selectAllForm">
            <#-- general request fields -->
            <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
            <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
            <input type="hidden" name="initialSelected" value="Y"/>
            <input type="hidden" name="partialReceive" value="${partialReceive?if_exists}"/>
            <table class="basic-table" cellspacing="0">
              <#list shipments?if_exists as shipment>
                <#assign originFacility = shipment.getRelatedOneCache("OriginFacility")?if_exists/>
                <#assign destinationFacility = shipment.getRelatedOneCache("DestinationFacility")?if_exists/>
                <#assign statusItem = shipment.getRelatedOneCache("StatusItem")/>
                <#assign shipmentType = shipment.getRelatedOneCache("ShipmentType")/>
                <#assign shipmentDate = shipment.estimatedArrivalDate?if_exists/>
                <tr>
                  <td><hr /></td>
                </tr>
                <tr>
                  <td>
                    <table class="basic-table <#if shipment_index%2!=0>alternate-row<#else></#if>" cellspacing="0">
                      <tr>
                        <td width="5%" nowrap="nowrap"><input type="radio" name="shipmentId" value="${shipment.shipmentId}" /></td>
                        <td width="5%" nowrap="nowrap">${shipment.shipmentId}</td>
                        <td>${shipmentType.get("description",locale)?default(shipmentType.shipmentTypeId?default(""))}</td>
                        <td>${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}</td>
                        <td>${(originFacility.facilityName)?if_exists} [${shipment.originFacilityId?if_exists}]</td>
                        <td>${(destinationFacility.facilityName)?if_exists} [${shipment.destinationFacilityId?if_exists}]</td>
                        <td style="white-space: nowrap;">${(shipment.estimatedArrivalDate.toString())?if_exists}</td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </#list>
              <tr>
                <td><hr /></td>
              </tr>
              <tr>
                <td>
                  <table class="basic-table" cellspacing="0">
                    <tr>
                      <td width="5%" nowrap="nowrap"><input type="radio" name="shipmentId" value="_NA_" /></td>
                      <td width="5%" nowrap="nowrap">${uiLabelMap.ProductNoSpecificShipment}</td>
                      <td colspan="5"></td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr>
                <td>&nbsp;<a href="javascript:document.selectAllForm.submit();" class="btn ">${uiLabelMap.ProductReceiveSelectedShipment}</a></td>
              </tr>
            </table>
          </form>
          </#if>
        <#-- Begin Receive Transfer Shipment Multiple Item-->
        <#elseif shipmentTypeId?exists && shipmentTypeId=="TRANSFER">
        <form method="post" action="<@ofbizUrl>receiveInventoryProduct</@ofbizUrl>" name="selectAllForm">
        <#-- general request fields -->
            <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
            <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
            <input type="hidden" name="initialSelected" value="Y"/>
            <#if shipment?has_content>
                <input type="hidden" name="shipmentIdReceived" value="${shipment.shipmentId}"/>
            </#if>
            <input type="hidden" name="_useRowSubmit" value="Y"/>
            <#assign rowCount = 0/>
            <table class="basic-table" cellspacing="0">
                <#if !shipmentItems?exists || shipmentItemsSize == 0>
                    <tr>
                        <td colspan="2">${uiLabelMap.ProductNoItemsPoReceive}.</td>
                    </tr>
                <#else>
                    <thead>
                    <tr>
                        <td>
                            <#if shipment?has_content>
                                <h4>${uiLabelMap.ProductShipmentId} #${shipment.shipmentId}</h4>
                                <!--<span>Set Shipment As Received</span>&nbsp;
                                <input type="checkbox" name="forceShipmentReceived" value="Y"/>-->
                            </#if>
                        </td>
                        <td align="right">
                            <span class="label">${uiLabelMap.CommonSelectAll}</span>
                            <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this, 'selectAllForm');"/>
                        </td>
                    </tr>
                    </thead>
                    <#list shipmentItems as orderItem>
                        <#assign defaultQuantity = orderItem.quantity - receivedQuantities[orderItem.shipmentItemSeqId]?double/>
                        <#assign itemCost = orderItem.unitPrice?default(0)/>
                        <#if shipment?has_content>
                            <#if shippedQuantities[orderItem.shipmentItemSeqId]?exists>
                                <#assign defaultQuantity = shippedQuantities[orderItem.shipmentItemSeqId]?double - receivedQuantities[orderItem.shipmentItemSeqId]?double/>
                            <#else>
                                <#assign defaultQuantity = 0/>
                            </#if>
                        </#if>
                        <#if 0 < defaultQuantity>
                            <input type="hidden" name="shipmentItemSeqId_o_${rowCount}" value="${orderItem.shipmentItemSeqId?if_exists}"/>
                            <input type="hidden" name="facilityId_o_${rowCount}" value="${requestParameters.facilityId?if_exists}"/>
                            <input type="hidden" name="datetimeReceived_o_${rowCount}" value="${nowTimestamp}"/>
                            <#if shipment?exists && shipment.shipmentId?has_content>
                                <input type="hidden" name="shipmentId_o_${rowCount}" value="${shipment.shipmentId}"/>
                            </#if>
                            <tr  class="<#if orderItem_index%2!=0>alternate-row</#if>">
                                <td>
                                    <table class="basic-table <#if orderItem_index%2!=0>alternate-row</#if>" cellspacing="0">
                                        <tr>
                                            <#if orderItem.productId?exists>
                                                <#assign product = orderItem.getRelatedOneCache("Product")/>
                                                <input type="hidden" name="productId_o_${rowCount}" value="${product.productId}"/>
                                                <td>
                                                    <span class="badge badge-info"> ${product.description?if_exists}</span>
                                                </td>
                                            <#else>
                                                <td width="25%">
                                                    <input type="text" size="12" name="productId_o_${rowCount}"/>
                                                    <a href="/catalog/control/EditProduct?externalLoginKey=${externalLoginKey}" target="catalog" class="btn ">${uiLabelMap.ProductCreateProduct}</a>
                                                </td>
                                            </#if>

                                            <td align="right"><span  class="label">${uiLabelMap.ProductLocation}:</span></td>
                                        <#-- location(s) -->
                                            <td align="left">
                                                <#assign facilityLocations = (product.getRelatedByAnd("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)))?if_exists/>
                                                <#if facilityLocations?has_content>
                                                    <select name="locationSeqId_o_${rowCount}">
                                                        <#list facilityLocations as productFacilityLocation>
                                                            <#assign facility = productFacilityLocation.getRelatedOneCache("Facility")/>
                                                            <#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation")?if_exists/>
                                                            <#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOneCache("TypeEnumeration"))?if_exists/>
                                                            <option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.get("description",locale)})</#if>[${productFacilityLocation.locationSeqId}]</option>
                                                        </#list>
                                                        <option value="">${uiLabelMap.ProductNoLocation}</option>
                                                    </select>
                                                <#else>
                                                    <@htmlTemplate.lookupField formName="selectAllForm" name="locationSeqId_o_${rowCount}" id="locationSeqId_o_${rowCount}" fieldFormName="LookupFacilityLocation"/> <!-- <#if parameters.facilityId?exists>?facilityId=${facilityId}</#if> -->
                                                </#if>
                                            </td>
                                            <td align="right"><span  class="label">Qty Accepted :</span></td>
                                            <td align="left">
                                            <input type="text" name="quantityAccepted_o_${rowCount}" size="6" value=<#if partialReceive?exists>"0"<#else>"${defaultQuantity?string.number}"</#if>/>
                                                <#if orderItem.productId?exists>
                                                    <#assign product = orderItem.getRelatedOneCache("Product")/>
                                                ${product.getRelatedOneCache("QuantityUom").description?if_exists}
                                                </#if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="25%">
                                                <span  class="label"> Received Item Type :</span>
                                                <select name="receivedItemType_o_${rowCount}" size="1">
                                                    <option value="INVENTORY">Inventory</option>
                                                    <#list fixedAssetTypes as fixedAssetType>
                                                        <option value="${fixedAssetType.fixedAssetTypeId}">${fixedAssetType.get("description",locale)}</option>
                                                    </#list>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                        <td width="25%">
                                                <span  class="label"> ${uiLabelMap.ProductInventoryItemType} :</span>
                                                <select name="inventoryItemTypeId_o_${rowCount}" size="1">
                                                    <#list inventoryItemTypes as nextInventoryItemType>
                                                        <option value="${nextInventoryItemType.inventoryItemTypeId}"
                                                            <#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
                                                                selected="selected"
                                                            </#if>
                                                                >${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
                                                    </#list>
                                                </select>
                                            </td>
                                            <td align="right"><span  class="label">${uiLabelMap.ProductRejectionReason} :</span></td>
                                            <td align="left">
                                                <select name="rejectionId_o_${rowCount}" size="1" style="width:182px">
                                                    <option></option>
                                                    <#list rejectReasons as nextRejection>
                                                        <option value="${nextRejection.rejectionId}">${nextRejection.get("description",locale)?default(nextRejection.rejectionId)}</option>
                                                    </#list>
                                                </select>
                                            </td>
                                            <td align="right"><span  class="label">${uiLabelMap.ProductQtyRejected} :</span></td>
                                            <td align="left">
                                                <input type="text" name="quantityRejected_o_${rowCount}" value="0" size="6"/>
                                                <#if orderItem.productId?exists>
                                                    <#assign product = orderItem.getRelatedOneCache("Product")/>
                                                ${product.getRelatedOneCache("QuantityUom").description?if_exists}
                                                </#if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td width="25%">&nbsp;</td>
                                            <td align="right"><span  class="label">${uiLabelMap.ProductFacilityOwner}:</span></td>
                                            <td align="left">${facility.ownerPartyId}</td>
                                            <td align="right"><span  class="label">${uiLabelMap.OrderQtyOrdered} :</span></td>
                                            <td align="left">
                                                <input type="text"  class="inputBox" name="quantityOrdered" value="${orderItem.quantity}" size="6" maxlength="20" disabled="disabled" />
                                                <#if orderItem.productId?exists>
                                                    <#assign product = orderItem.getRelatedOneCache("Product")/>
                                                ${product.getRelatedOneCache("QuantityUom").description?if_exists}
                                                </#if>
                                            </td>
                                            <td colspan="2">&nbsp;</td>
                                        </tr>
                                        <tr>
                                            <td width="25%">&nbsp;</td>
                                            <#if currencyUomId != shipmentCurrencyUomId>
                                                <td align="right"><span  class="label">${uiLabelMap.ProductPerUnitPriceOrder}:</span></td>
                                                <td align="left">
                                                    <input type="hidden" name="orderCurrencyUomId_o_${rowCount}" value="${shipmentCurrencyUomId?if_exists}" />
                                                    <input type="text" id="orderCurrencyUnitPrice_${rowCount}" name="orderCurrencyUnitPrice_o_${rowCount}" value="${shipmentCurrencyUnitPriceMap[orderItem.shipmentItemSeqId]}"
                                                           onchange="javascript:getConvertedPrice(orderCurrencyUnitPrice_${rowCount}, '${shipmentCurrencyUomId}', '${currencyUomId}', '${rowCount}', '${shipmentCurrencyUnitPriceMap[orderItem.shipmentItemSeqId]}', '${itemCost}');"  maxlength="20" />
                                                    <#if shipmentCurrencyUomId?exists && shipmentCurrencyUomId.equals("KWD")>KD<#else>${shipmentCurrencyUomId?if_exists}</#if>
                                                </td>
                                                <td align="right"><span  class="label">${uiLabelMap.ProductPerUnitPriceFacility}:</span></td>
                                                <td align="left">
                                                    <input type="hidden" name="currencyUomId_o_${rowCount}" value="${currencyUomId?if_exists}" />
                                                    <input type="text" id="unitCost_${rowCount}" name="unitCost_o_${rowCount}" value="${shipmentCurrencyUnitPriceMap[orderItem.shipmentItemSeqId]}" readonly="readonly" size="15" maxlength="20" />
                                                    <#if currencyUomId?exists && currencyUomId.equals("KWD")>KD<#else>${currencyUomId?if_exists}</#if>
                                            <#else>
                                                <td align="right"><span  class="label">${uiLabelMap.ProductPerUnitPrice}:</span></td>
                                                <td align="left">
                                                    <input type="hidden" name="currencyUomId_o_${rowCount}" value="${currencyUomId?if_exists}" />
                                                    <input type="text" name="unitCost_o_${rowCount}" value="${shipmentCurrencyUnitPriceMap[orderItem.shipmentItemSeqId]}" size="15" maxlength="20" />
                                                    <#if currencyUomId?exists && currencyUomId.equals("KWD")>KD<#else>${currencyUomId?if_exists}</#if>
                                                </td>
                                            </#if>
                                        </tr>
                                        <tr><td colspan="4">&nbsp;</td></tr>
                                    </table>
                                </td>
                                <td align="right">
                                    <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');"/>
                                </td>
                            </tr>
                            <#assign rowCount = rowCount + 1>
                        </#if>
                    </#list>
                    <#if rowCount == 0>
                        <tr>
                            <td colspan="2">${uiLabelMap.ProductNoItemsPo} #${purchaseOrder.orderId} ${uiLabelMap.ProductToReceive}.</td>
                        </tr>
                        <tr>
                            <td colspan="2" align="right">
                                <a href="<@ofbizUrl>ReceiveInventory?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="btn ">${uiLabelMap.ProductReturnToReceiving}</a>
                            </td>
                        </tr>
                    <#else>
                        <tr>
                            <td colspan="2" align="center" style="padding-top:10px">
                                <a href="javascript:document.selectAllForm.submit();" class="btn ">${uiLabelMap.ProductReceiveSelectedProduct}</a>
                            </td>
                        </tr>
                    </#if>
                </#if>
            </table>
            <input type="hidden" name="_rowCount" value="${rowCount}"/>
        </form>
        <script language="JavaScript" type="text/javascript">selectAll('selectAllForm');</script>
        <#-- End Receive Transfer Shipment Multiple Item-->
        <#-- Multi-Item PO Receiving -->
        <#elseif requestParameters.initialSelected?exists && purchaseOrder?has_content>
          <input type="hidden" id="getConvertedPrice" value="<@ofbizUrl secure="${request.isSecure()?string}">getConvertedPrice"</@ofbizUrl> />
          <input type="hidden" id="alertMessage" value="${uiLabelMap.ProductChangePerUnitPrice}" />
          <form method="post" action="<@ofbizUrl>receiveInventoryProduct</@ofbizUrl>" name="selectAllForm" id="selectAllForm">
            <#-- general request fields -->
            <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
            <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
            <input type="hidden" name="initialSelected" value="Y"/>
            <#if shipment?has_content>
            <input type="hidden" name="shipmentIdReceived" value="${shipment.shipmentId}"/>
            </#if>
            <input type="hidden" name="_useRowSubmit" value="Y"/>
            <#assign rowCount = 0/>
            <table class="basic-table" cellspacing="0">
              <#if !purchaseOrderItems?exists || purchaseOrderItemsSize == 0>
                <tr>
                  <td colspan="2">${uiLabelMap.ProductNoItemsPoReceive}.</td>
                </tr>
              <#else>
              <thead>
                <tr>
                  <td>
                    <h3>${uiLabelMap.ProductReceivePurchaseOrder} #${purchaseOrder.orderId}</h3>
                    <#if shipment?has_content>
                    <h4>${uiLabelMap.ProductShipmentId} #${shipment.shipmentId}</h4>
                    <!--<span>Set Shipment As Received</span>&nbsp;
                    <input type="checkbox" name="forceShipmentReceived" value="Y"/>-->
                    </#if>
                  </td>
                  <td align="right">
                    <span class="label">${uiLabelMap.CommonSelectAll}</span>
                    <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this, 'selectAllForm');"/>
                  </td>
                </tr>
              </thead>
                <#list purchaseOrderItems as orderItem>
                  <#assign defaultQuantity = orderItem.quantity - receivedQuantities[orderItem.orderItemSeqId]?double/>
                  <#assign itemCost = orderItem.unitPrice?default(0)/>
                  <#assign salesOrderItem = salesOrderItems[orderItem.orderItemSeqId]?if_exists/>
                  <#if shipment?has_content>
                    <#if shippedQuantities[orderItem.orderItemSeqId]?exists>
                      <#assign defaultQuantity = shippedQuantities[orderItem.orderItemSeqId]?double - receivedQuantities[orderItem.orderItemSeqId]?double/>
                    <#else>
                      <#assign defaultQuantity = 0/>
                    </#if>
                  </#if>
                  <#if 0 < defaultQuantity>
                  <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")/>
                  <input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}"/>
                  <input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}"/>
                  <input type="hidden" name="facilityId_o_${rowCount}" value="${requestParameters.facilityId?if_exists}"/>
                  <!-- <input type="hidden" name="datetimeReceived_o_${rowCount}" value="${nowTimestamp}"/> -->
                  <#if shipment?exists && shipment.shipmentId?has_content>
                    <input type="hidden" name="shipmentId_o_${rowCount}" value="${shipment.shipmentId}"/>
                  </#if>
                  <#if salesOrderItem?has_content>
                    <input type="hidden" name="priorityOrderId_o_${rowCount}" value="${salesOrderItem.orderId}"/>
                    <input type="hidden" name="priorityOrderItemSeqId_o_${rowCount}" value="${salesOrderItem.orderItemSeqId}"/>
                  </#if>

                  <tr  class="<#if orderItem_index%2!=0>alternate-row</#if>">
                    <td>
                      <table class="basic-table <#if orderItem_index%2!=0>alternate-row</#if>" cellspacing="0">
                        <tr>
                          <#if orderItem.productId?exists>
                            <#assign product = orderItem.getRelatedOneCache("Product")/>
                            <input type="hidden" name="productId_o_${rowCount}" value="${product.productId}"/>
                            <td width="25%">
                                <span class="badge badge-info">${orderItem.itemDescription?if_exists} : ${product.description?if_exists}</span>
                            </td>
                          <#else>
                            <td width="25%">
                                <b>${orderItemType.get("description",locale)}</b> : ${orderItem.itemDescription?if_exists}&nbsp;&nbsp;
                                <input type="text" size="12" name="productId_o_${rowCount}"/>
                                <a href="/catalog/control/EditProduct?externalLoginKey=${externalLoginKey}" target="catalog" class="btn ">${uiLabelMap.ProductCreateProduct}</a>
                            </td>
                          </#if>
                          <td align="right"><span  class="label">${uiLabelMap.ProductLocation} :</span></td>
                          <#-- location(s) -->
                          <td align="left">
                            <#assign facilityLocations = (orderItem.getRelatedByAnd("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId)))?if_exists/>
                            <#if facilityLocations?has_content>
                              <select name="locationSeqId_o_${rowCount}">
                                <#list facilityLocations as productFacilityLocation>
                                  <#assign facility = productFacilityLocation.getRelatedOneCache("Facility")/>
                                  <#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation")?if_exists/>
                                  <#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOneCache("TypeEnumeration"))?if_exists/>
                                  <option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.get("description",locale)})</#if>[${productFacilityLocation.locationSeqId}]</option>
                                </#list>
                                <option value="">${uiLabelMap.ProductNoLocation}</option>
                              </select>
                            <#else>
                              <@htmlTemplate.lookupField formName="selectAllForm" name="locationSeqId_o_${rowCount}" id="locationSeqId_o_${rowCount}" fieldFormName="LookupFacilityLocation"/> <!-- <#if parameters.facilityId?exists>?facilityId=${facilityId}</#if> -->
                            </#if>
                          </td>
                          <td align="right"><span  class="label">Qty Accepted :</span></td>
                          <td align="left">
                            <input type="text"  class="inputBox checkQuantity" name="quantityAccepted_o_${rowCount}" id="quantityAccepted_o_${rowCount}" size="6" value=<#if partialReceive?exists>"0"<#else>"${defaultQuantity?string.number}"</#if>/>
                            <#if orderItem.productId?exists>
                                <#assign product = orderItem.getRelatedOneCache("Product")/>
                                ${product.getRelatedOneCache("QuantityUom").description?if_exists}
                            </#if>
                          </td>
                        </tr>
                        <tr>
                          <td width="25%">
                           <span  class="label"> Received Item Type :</span>
                            <select name="receivedItemType_o_${rowCount}" size="1">
                              <option value="INVENTORY">Inventory</option>
                              <#list fixedAssetTypes as fixedAssetType>
                              <option value="${fixedAssetType.fixedAssetTypeId}">${fixedAssetType.get("description",locale)}</option>
                              </#list>
                            </select>
                          </td>
                          <td align="right"><span  class="label">${uiLabelMap.ProductRejectionReason} :</span></td>
                          <td align="left">
                            <select name="rejectionId_o_${rowCount}" size="1" style="width:182px">
                              <option></option>
                              <#list rejectReasons as nextRejection>
                              <option value="${nextRejection.rejectionId}">${nextRejection.get("description",locale)?default(nextRejection.rejectionId)}</option>
                              </#list>
                            </select>
                          </td>
                          <td align="right"><span  class="label">${uiLabelMap.ProductQtyRejected} :</span></td>
                          <td align="left">
                            <input type="text"  class="inputBox checkQuantity"  name="quantityRejected_o_${rowCount}" id="quantityRejected_o_${rowCount}" value="0" size="6"/>
                              <#if orderItem.productId?exists>
                                  <#assign product = orderItem.getRelatedOneCache("Product")/>
                              ${product.getRelatedOneCache("QuantityUom").description?if_exists}
                              </#if>
                          </td>
                        </tr>
                        <tr>
                            <td width="25%">
                                <span  class="label"> ${uiLabelMap.ProductInventoryItemType} :</span>
                                <select name="inventoryItemTypeId_o_${rowCount}" size="1">
                                    <#list inventoryItemTypes as nextInventoryItemType>
                                        <option value="${nextInventoryItemType.inventoryItemTypeId}"
                                            <#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
                                                selected="selected"
                                            </#if>
                                                >${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
                                    </#list>
                                </select>
                            </td>
                            <td align="right"><span  class="label">${uiLabelMap.ProductFacilityOwner} :</span></td>
                            <td align="left">${facility.ownerPartyId}</td>
                            <td align="right"><span  class="label">Variance :</span></td>
                            <td align="left">
                              <input type="text"  class="inputBox checkQuantity"  name="quantityVariance_o_${rowCount}" id="quantityVariance_o_${rowCount}"
                                     value="0" size="6" maxlength="20"/>
                                <#if product?exists>
                                ${product.getRelatedOneCache("QuantityUom").description?if_exists}
                                </#if>
                            </td>
                            <td colspan="2">&nbsp;</td>
                        </tr>
                        <tr>
                          <td width="25%">&nbsp;</td>
                          <#if currencyUomId != orderCurrencyUomId>
                            <td align="right"><span  class="label">${uiLabelMap.ProductPerUnitPriceOrder}+Landed Cost :</span></td>
                            <td align="left">
                              <input type="hidden" name="orderCurrencyUomId_o_${rowCount}" value="${orderCurrencyUomId?if_exists}" />
                              <input type="text" id="orderCurrencyUnitPrice_${rowCount}" name="orderCurrencyUnitPrice_o_${rowCount}" value="${orderCurrencyUnitPriceMap[orderItem.orderItemSeqId]}"
                              	 onchange="javascript:getConvertedPrice(orderCurrencyUnitPrice_${rowCount}, '${orderCurrencyUomId}', '${currencyUomId}', '${rowCount}', '${orderCurrencyUnitPriceMap[orderItem.orderItemSeqId]}', '${itemCost}');"  maxlength="20" />
                              <#if orderCurrencyUomId?exists && orderCurrencyUomId.equals("KWD")>KD<#else>${orderCurrencyUomId?if_exists}</#if>
                            </td>
                            <td align="right"><span  class="label">${uiLabelMap.ProductPerUnitPriceFacility} :</span></td>
                            <td align="left">
                              <input type="hidden" name="currencyUomId_o_${rowCount}" value="${currencyUomId?if_exists}" />
                              <input type="text" id="unitCost_${rowCount}" name="unitCost_o_${rowCount}" value="${itemCost}" readonly="readonly" size="15" maxlength="20" />
                              <#if currencyUomId?exists && currencyUomId.equals("KWD")>KD<#else>${currencyUomId?if_exists}</#if>
                            </td>
                          <#else>
                            <td align="right"><span  class="label">${uiLabelMap.ProductPerUnitPrice} :</span></td>
                            <td align="left">
                              <input type="hidden" name="currencyUomId_o_${rowCount}" value="${currencyUomId?if_exists}" />
                              <input type="text" name="unitCost_o_${rowCount}" value="${itemCost}" size="15" maxlength="20" />
                              <#if currencyUomId?exists && currencyUomId.equals("KWD")>KD<#else>${currencyUomId?if_exists}</#if>
                            </td>
                          </#if>
                            <td align="right"><span  class="label">${uiLabelMap.ShippedQty} :</span></td>
                            <td align="left">
                                <input type="text" class="inputBox" id="quantityOrdered_o_${rowCount}" name="quantityOrdered_o_${rowCount}" value="${defaultQuantity}" size="6" maxlength="20" disabled="disabled" />
                                <#if product?exists>
                                    ${product.getRelatedOneCache("QuantityUom").description?if_exists}
                                </#if>
                            </td>
                        </tr>
                        <tr>
                            <#if orderItem.productId?exists>
                                <#assign product = orderItem.getRelatedOneCache("Product")/>
                                <#if product?exists && product.requireExpiryDate?exists && product.requireExpiryDate == "Y">
                                    <td width="25%">
                                        <span  class="label"> ${uiLabelMap.ProductBatchNumber} :</span><font color="red"> *</font>
                                        <input type="text" name="batchNumber_o_${rowCount}" class="required" size="20" />
                                    </td>
                                <#else>
                                    <td width="25%">
                                        <span  class="label"> ${uiLabelMap.ProductBatchNumber} :</span>
                                        <input type="text" name="batchNumber_o_${rowCount}" size="20" />
                                    </td>
                                </#if>
                            <#else>
                                <td width="25%">
                                    <span  class="label"> ${uiLabelMap.ProductBatchNumber} :</span>
                                    <input type="text" name="batchNumber_o_${rowCount}" value="" size="20" />
                                </td>
                            </#if>
                            <#if orderItem.productId?exists>
                                <#assign product = orderItem.getRelatedOneCache("Product")/>
                                <#if product?exists && product.requireExpiryDate?exists && product.requireExpiryDate == "Y">
                                    <td align="right"><span  class="label">${uiLabelMap.ProductExpireDate} :</span><font color="red"> *</font></td>
                                    <td align="left">
                                        <@htmlTemplate.renderDateTimeField name="expireDate_o_${rowCount}" event="" action="" className="required" alert=""
                                                title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="expireDate_o_${rowCount}" dateType="date-time" shortDateInput=false
                                                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                                                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			                        </td>
			                    <#else>
			                        <td align="right"><span  class="label">${uiLabelMap.ProductExpireDate} :</span></td>
                                    <td align="left">
                                        <@htmlTemplate.renderDateTimeField name="expireDate_o_${rowCount}" event="" action="" className="" alert=""
                                                title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="expireDate_o_${rowCount}" dateType="date-time" shortDateInput=false
                                                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                                                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			                        </td>
                                </#if>
                            <#else>
                                <td align="right"><span  class="label">${uiLabelMap.ProductExpireDate} :</span></td>
                                <td align="left">
                                    <@htmlTemplate.renderDateTimeField name="expireDate_o_${rowCount}" event="" action="" className="" alert=""
                                                title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="expireDate_o_${rowCount}" dateType="date-time" shortDateInput=false
                                                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString=""
                                                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                                </td>
                            </#if>
                            <td colspan="2">&nbsp;</td>
                        </tr>
                        <tr><td colspan="4">&nbsp;</td></tr>
                      </table>
                    </td>
                    <td align="right">
                      <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');"/>
                    </td>
                  </tr>
                  <#assign rowCount = rowCount + 1>
                  </#if>
                </#list>
                <#if rowCount == 0>
                  <tr>
                    <td colspan="2">${uiLabelMap.ProductNoItemsPo} #${purchaseOrder.orderId} ${uiLabelMap.ProductToReceive}.</td>
                  </tr>
                  <tr>
                    <td colspan="2" align="right">
                      <a href="<@ofbizUrl>ReceiveInventory?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="btn ">${uiLabelMap.ProductReturnToReceiving}</a>
                    </td>
                  </tr>
                <#else>
                  <tr>
                    <td>
                      <table class="basic-table cellspacing="0">
                        <tr><td colspan="4">&nbsp;</td></tr>
                        <tr>
                          <td width="32%">
                            <span class="label">Inspection Result :</span>
                            <select name="inspectionResult">
                              <option></option>
                              <option>Positive</option>
                              <option>Negative</option>
                            </select>
                          </td>
                          <td align="right"><span class="label">Inspected By :</span></td>
                          <td align="left">
                            <@htmlTemplate.lookupField formName="selectAllForm" name="inspectedBy" id="inspectedBy" fieldFormName="LookupPerson"/>
                          </td>
                          <td align="right"><span  class="label">Description :<span></td>
                          <td align="left">
                            <textarea name="inspectionDescription" id="inspectionDescription" cols="30" rows="2" wrap="hard"></textarea>
                          </td>
                        </tr>
                        <tr><td colspan="4">&nbsp;</td></tr>
                      </table>
                    </td>
                  </tr>
                  <#assign currencyMatch = Static["java.lang.Boolean"].toString(isCurrencyUom)>
                  <input type="hidden" id="currencyMatch" name="currencyMatch" value="${currencyMatch}" onclick="validate();"/>
                  <tr style="border">
                  <td>
                      <table class="basic-table cellspacing="0">
                      <tr>
                        <td align="right"><span  class="label">Receive Date :<span></td>
                        <td ><@htmlTemplate.renderDateTimeField name="datetimeReceived" event="" action="" className="" alert="" 
                        title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${nowTimestamp}" size="25" maxlength="30" id="datetimeReceived" 
                        dateType="date-time" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
                        timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" 
                        ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        </td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td style="padding-top:10px;">
                          <div> <input type="checkbox" id="zpcInspection" name="zpcInspection" value="Y" style="margin-bottom: 5px;" onclick="validate();" <#if requestParameters.zpcInspection?exists>checked="checked"</#if> />&nbsp;&nbsp;<span>Internal Inspection<span> </div>
                          <div> <input type="checkbox" id="zraInspection" name="zraInspection" value="Y" style="margin-bottom: 5px;" onclick="validate();" <#if requestParameters.initialSelected?exists && purchaseOrder?has_content && isCurrencyUom>disabled="disabled"</#if><#if requestParameters.initialSelected?exists && purchaseOrder?has_content && !isCurrencyUom && requestParameters.zpcInspection?exists>checked="checked"</#if> />&nbsp;&nbsp;<span>External Inspection<span> </div>
                        </td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td >&nbsp;</td>
                        <td align="right"><span  class="label">Inspection Note :<span></td>
                        <td align="left">
                           <textarea name="inspectionNote" id="inspectionNote" cols="30" rows="2" wrap="hard" quantity></textarea>
                         </td>
                      </tr>
                      </table>
                  </td>
                  </tr>

                  <tr>
                    <td align="center" style="padding-top:10px">
                      <#-- <a href="javascript:document.selectAllForm.submit();" class="btn" > onclick="javascript:receiveSelectedProduct();"${uiLabelMap.ProductReceiveSelectedProduct}</a> -->
                      <input id="receiveProductButton" type="submit" class="btn submit" value="${uiLabelMap.ProductReceiveSelectedProduct}" <#if requestParameters.initialSelected?exists && purchaseOrder?has_content>disabled="disabled"</#if> />
                    </td>
                  </tr>
                </#if>
              </#if>
              
            </table>
            <input type="hidden" name="_rowCount" value="${rowCount}"/>
          </form>
          <script language="JavaScript" type="text/javascript">
            var form = document.selectAllForm;
            jQuery(form).validate();
            selectAll('selectAllForm');
          </script>

        <#-- Initial Screen -->
        <#else>
          <h3>${uiLabelMap.ProductReceiveItem}</h3>
          <form name="selectAllForm" method="post" action="<@ofbizUrl>ReceiveInventory</@ofbizUrl>">
            <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
            <input type="hidden" name="initialSelected" value="Y"/>
            <table class="basic-table" cellspacing="0">
              <#-- <tr>
                <td class="label">${uiLabelMap.ProductPurchaseOrderNumber}</td>
                <td>
                    <@htmlTemplate.lookupField value="${requestParameters.purchaseOrderId?if_exists}" formName="selectAllForm" name="purchaseOrderId" id="purchaseOrderId" fieldFormName="LookupPurchaseOrderHeaderAndShipInfo"/>
                    <span class="tooltip">${uiLabelMap.ProductLeaveSingleProductReceiving}</span>
                </td>
              </tr> -->
              <tr>
                <td class="label">${uiLabelMap.ProductProductId}</td>
                <td>
                  <@htmlTemplate.lookupField value="${requestParameters.productId?if_exists}" formName="selectAllForm" name="productId" id="productId" fieldFormName="LookupProduct"/>
                  <span class="tooltip">${uiLabelMap.ProductLeaveEntirePoReceiving}</span>
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td>
                  <a href="javascript:document.selectAllForm.submit();" class="btn">${uiLabelMap.ProductReceiveProduct}</a>
                </td>
              </tr>
            </table>
          </form>
        </#if>
        <script type="application/javascript">
            function receiveSelectedProduct(){
                //document.selectAllForm.submit();
                var cform = document['selectAllForm'];
                var len = cform.elements.length;
                for (var i = 0; i < len; i++) {
                    var element = cform.elements[i];
                    if (element.name.substring(0, 10) == "_rowSubmit" && element.checked) {
                            var rowNum = element.name.substring(13);
                            var aQty = parseFloat($('#quantityAccepted_o_'+rowNum).val().replace(/,/g,''));
                            var vQty = parseFloat($('#quantityVariance_o_'+rowNum).val().replace(/,/g,''));
                            var rQty = parseFloat($('#quantityRejected_o_'+rowNum).val().replace(/,/g,''));
                            var sQty = parseFloat($('#quantityOrdered_o_'+rowNum).val().replace(/,/g,''));
                            if(aQty+vQty-rQty!=sQty){
                                alert('Accepted Quantity - Rejected Quantity + Variance Quantity is not equal to Shipped Quantity.' );
                            }
                    }
                }
            }


            var validator = $("#selectAllForm").validate({
                debug: true,
                submitHandler: function(form) {
                    if(validator.valid()){
                        form.submit();
                    }
                }
            });


            $.validator.addMethod("checkQuantity", function(value,element) {
                var rowNum =element.name.substring(19);
                this.resetForm();
                var aQty = parseFloat($('#quantityAccepted_o_'+rowNum).val().replace(/,/g,''));
                var vQty = parseFloat($('#quantityVariance_o_'+rowNum).val().replace(/,/g,''));
                var rQty = parseFloat($('#quantityRejected_o_'+rowNum).val().replace(/,/g',''));
                var sQty = parseFloat($('#quantityOrdered_o_'+rowNum).val().replace(/,/g,''));
                return this.optional(element) || (aQty+vQty+rQty==sQty);
            },'Quantity mismatch');
        </script>