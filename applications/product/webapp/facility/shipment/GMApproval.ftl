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
    function validate(selection) {
        var approveStatus = selection.value;
        if(approveStatus == "GM_REJECTED") {
            reason.style.display = '';
        }
        if(approveStatus == "GM_APPROVED") {
            reason.style.display = 'none';
        }
    }

    // Confirm Popup for hypertext links
    function confirmGRN(shipmentId, facilityId, purchaseOrderId) {
        if (confirm("GRN Approved/Rejected cannot be modified. Do you want to continue?")) {
            document.gmApprovalForm.shipmentId.value = shipmentId;
            document.gmApprovalForm.facilityId.value = facilityId;
            document.gmApprovalForm.purchaseOrderId.value = purchaseOrderId;
            document.gmApprovalForm.submit();
        }
    }
</script>
<div class="screenlet">
    <form action="<@ofbizUrl>setGMApproval</@ofbizUrl>" method="post" name="gmApprovalForm" id="gmApprovalForm">
        <input type="hidden" name="shipmentId" value="${requestParameters.shipmentId?if_exists}"/>
        <input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
        <input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">List of Shipment Receipts</li>
            </ul>
            <br class="clear"/>
        </div>
        <div class="screenlet-body">
            <table cellspacing="0" class="basic-table">
                <tr class="header-row">
                    <td>${uiLabelMap.ProductShipmentId}</td>
                    <td>${uiLabelMap.ProductReceipt}</td>
                    <td>Inventory Item Id</td>
                    <td>Purchase Order</td>
                    <td>Order Item Seq Id</td>
                    <td>${uiLabelMap.ProductProduct}</td>
                    <td>${uiLabelMap.ProductPerUnitPrice}</td>
                    <td>${uiLabelMap.CommonAccepted}</td>
                    <td>${uiLabelMap.CommonRejected}</td>
                </tr>
                <#if shipmentReceipts?has_content>
                    <#list shipmentReceipts as shipmentReceipt>
                        <#assign orderItemGvs = delegator.findByAnd("OrderHeaderAndItems",{"orderId":shipmentReceipt.orderId,"orderItemSeqId":shipmentReceipt.orderItemSeqId})/>
                        <#assign orderItemgv = orderItemGvs.get(0)/>
                        <tr>
                            <td><a href="<@ofbizUrl>ViewShipment?shipmentId=${shipmentReceipt.shipmentId?if_exists}</@ofbizUrl>" class="btn-link">${shipmentReceipt.shipmentId?if_exists} ${shipmentReceipt.shipmentItemSeqId?if_exists}</a></td>
                            <td>${shipmentReceipt.receiptId?if_exists}</td>
                            <td>${shipmentReceipt.inventoryItemId?if_exists}</td>
                            <td><a href="/ordermgr/control/orderview?orderId=${shipmentReceipt.orderId}" class="btn-link">${shipmentReceipt.orderId}</a></td>
                            <td>${shipmentReceipt.orderItemSeqId?if_exists}</td>
                            <#assign product = delegator.findOne("Product",{"productId":shipmentReceipt.productId},false)>
                            <td>${product.internalName} [${product.productId?if_exists}]</td>
                            <td><@ofbizCurrency amount=orderItemgv.unitPrice?default(0)?string("##0.00")?if_exists isoCode=orderItemgv.currencyUom?if_exists /></td>
                            <td>${shipmentReceipt.quantityAccepted?if_exists?string.number}</td>
                            <td>${shipmentReceipt.quantityRejected?if_exists?default(0)?string.number}</td>
                        </tr>
                    </#list>
                </#if>
                <tr><td colspan="9"><hr /></td></tr>
            </table>
            <#if !grnStatus?has_content>
                <table cellspacing="0" class="basic-table">
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <tr>
                        <td align="right" width="45%"><span  class="label">Approve Status :<span></td>
                        <td align="left">
                            <select name="approveStatus" id="approveStatus" onchange="javascript:validate(this);">
                                <#list approvalStatus as gmApprovalStatus>
                                    <option value="${gmApprovalStatus.statusId}">${gmApprovalStatus.get("description",locale)?if_exists}</option>
                                </#list>
                            </select>
                        </td>
                    </tr>
                    <tr id="reason" style="display:none">
                        <td align="right" width="45%"><span  class="label">Rejected Reason <span><font color="red">*</font></span><span></td>
                        <td align="left">
                            <textarea name="rejectedReason" id="rejectedReason" wrap="hard" class="required"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td width="45%">&nbsp;</td>
                        <td>
                            <a class="btn btn-success" href="javascript:confirmGRN('${requestParameters.shipmentId?if_exists}','${requestParameters.facilityId?if_exists}','${requestParameters.purchaseOrderId?if_exists}')">${uiLabelMap.CommonSave}</a>
                            <#-- <input type="submit" class="btn btn-success" value="${uiLabelMap.CommonSave}" onClick="javascript:confirmGRN('${requestParameters.shipmentId?if_exists}','${requestParameters.facilityId?if_exists}','${requestParameters.purchaseOrderId?if_exists}')"/> -->
                        </td>
                    </tr>
                </table>
            </#if>
            <script>
                var form = document.gmApprovalForm;
                jQuery(form).validate();
           </script>
        </div>
    </form>
</div>