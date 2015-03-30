    
     
    <#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
    
  <#assign showInput = "Y">
 
   
         <#assign shipment = parameters.shipmentId>
    <form name="clearPickForm" method="post" action="<@ofbizUrl>cancelAllRowsShipment?mode=${requestParameters.mode}</@ofbizUrl>">
        <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
        <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
        <input type="hidden" name="facilityId" value="${facility.facilityId?if_exists}"/>
         <input type="hidden" name="shipmentId" value="${shipment?if_exists}"/>
      </form>
 
    
    
  <#if showInput != "N" && orderHeader?exists && orderHeader?has_content>
    <div class="screenlet">
      <div class="screenlet-title-bar">
        <ul>
          <li class="h3">${uiLabelMap.ProductOrderId} #<a href="/ordermgr/control/orderview?orderId=${orderId}">${orderId}</a> / ${uiLabelMap.ProductOrderShipGroupId} #${shipGroupSeqId}</li>
        </ul>
        <br class="clear"/>
      </div>
      <div class="screenlet-body">
        <#if orderItemShipGroup?has_content>
          <#assign postalAddress = orderItemShipGroup.getRelatedOne("PostalAddress")>
          <#assign carrier = orderItemShipGroup.carrierPartyId?default("N/A")>
          <table cellpadding="4" cellspacing="4" class="basic-table">
            <tr>
              <td valign="top">
                <span class="label">${uiLabelMap.ProductShipToAddress}</span>
                <br />
                ${uiLabelMap.CommonTo}: ${postalAddress.toName?default("")}
                <br />
                <#if postalAddress.attnName?has_content>
                  ${uiLabelMap.CommonAttn}: ${postalAddress.attnName}
                  <br />
                </#if>
                ${postalAddress.address1}
                <br />
                <#if postalAddress.address2?has_content>
                  ${postalAddress.address2}
                  <br />
                </#if>
                ${postalAddress.city?if_exists}, ${postalAddress.stateProvinceGeoId?if_exists} ${postalAddress.postalCode?if_exists}
                <br />
                ${postalAddress.countryGeoId}
                <br />
              </td>
              <td>&nbsp;</td>
              <td valign="top">
                <span class="label">${uiLabelMap.ProductCarrierShipmentMethod}</span>
                <br />
                <#if carrier == "USPS">
                  <#assign color = "red">
                <#elseif carrier == "UPS">
                  <#assign color = "green">
                <#else>
                  <#assign color = "black">
                </#if>
                <#if carrier != "_NA_">
                  <font color="${color}">${carrier}</font>
                  &nbsp;
                </#if>
                ${orderItemShipGroup.shipmentMethodTypeId?default("??")}
              </td>
              <td>&nbsp;</td>
              <td valign="top">
                <span class="label">${uiLabelMap.OrderInstructions}</span>
                <br />
                ${orderItemShipGroup.shippingInstructions?default("(${uiLabelMap.CommonNone})")}
              </td>
            </tr>
          </table>
        </#if>
        <hr />
        <form name="singlePickForm" method="post" action="<@ofbizUrl>processVerifyPickShipment?mode=${requestParameters.mode}</@ofbizUrl>">
          <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
          <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
          <input type="hidden" name="facilityId" value="${facility.facilityId?if_exists}"/>
          <table cellpadding="2" cellspacing="0" class="basic-table">
            <tr>
              <td>
                <div>
                  <span class="label">${uiLabelMap.ProductProductNumber}</span>
                  <input type="text" name="productId" size="20" maxlength="20" value=""/>
                  @
                  <input type="text" name="quantity" size="6" maxlength="6" value="1"/>
                  <input type="submit" value="${uiLabelMap.ProductVerify}&nbsp;${uiLabelMap.OrderItem}" class="buttontext"/>
                </div>
              </td>
            </tr>
          </table>
        </form>
        <br />
        <#assign orderItems = orderItems?if_exists>
        <form name="multiPickForm" method="post" action="<@ofbizUrl>processBulkVerifyPickShipment?mode=${requestParameters.mode}</@ofbizUrl>">
          <input type="hidden" name="facilityId" value="${facility.facilityId?if_exists}"/>
          <input type="hidden" name="userLoginId" value="${userLoginId?if_exists}"/>
          <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
          <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
           <input type="hidden" name="shipmentId" value="${shipment?if_exists}"/>
          <table class="basic-table" cellspacing='0'>
            <tr class="header-row">
              <td>&nbsp;</td>
              <td>${uiLabelMap.ProductItem} #</td>
              <td>${uiLabelMap.ProductProductId}</td>
              <td>${uiLabelMap.ProductInternalName}</td>
              <td>${uiLabelMap.ProductCountryOfOrigin}</td>
              <td align="">${uiLabelMap.ProductOrderedQuantity}</td>
              <td align="">${uiLabelMap.ProductVerified}&nbsp;${uiLabelMap.CommonQuantity}</td>
              <td align="">${uiLabelMap.CommonQty}&nbsp;${uiLabelMap.CommonTo}&nbsp;${uiLabelMap.ProductVerify}</td>
            </tr>
            <#if orderItems?has_content>
              <#assign rowKey = 1>
              <#assign counter = 1>
              <#assign isShowVerifyItemButton = "false">
              <#list orderItems as orderItem>
                <#assign orderItemSeqId = orderItem.orderItemSeqId?if_exists>
                <#assign readyToVerify = verifyPickSession.getReadyToVerifyQuantity(orderId,orderItemSeqId)>
                <#assign orderItemQuantity = orderItem.getBigDecimal("quantity")>
                <#assign verifiedQuantity = 0.000000>
                <#assign shipments = delegator.findByAnd("Shipment", Static["org.ofbiz.base.util.UtilMisc"].toMap("primaryOrderId", orderItem.getString("orderId"), "statusId", "SHIPMENT_PICKED"))/>
                <#if (shipments?has_content)>
                  <#list shipments as shipment>
                    <#assign itemIssuances = delegator.findByAnd("ItemIssuance", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentId", shipment.getString("shipmentId"), "orderItemSeqId", orderItemSeqId))/>
                    <#if itemIssuances?has_content>
                      <#list itemIssuances as itemIssuance>
                        <#assign verifiedQuantity = verifiedQuantity + itemIssuance.getBigDecimal("quantity")>
                      </#list>
                    </#if>
                  </#list>
                </#if>
                <#if verifiedQuantity == orderItemQuantity>
                  <#assign counter = counter +1>
                </#if>
                <#assign orderItemQuantity = orderItemQuantity.subtract(verifiedQuantity)>
                <#assign product = orderItem.getRelatedOne("Product")?if_exists/>
                <tr>
                  <#if (orderItemQuantity.compareTo(readyToVerify) > 0) >
                    <td><input type="checkbox" name="sel_${rowKey}" value="Y" checked=""/></td>
                    <#assign isShowVerifyItemButton = "true">
                  <#else>
                    <td>&nbsp;</td>
                  </#if>
                  <td>${orderItemSeqId?if_exists}</td>
                  <td>${product.productId?default("N/A")}</td>
                  <td>
                    <a href="/catalog/control/EditProduct?productId=${product.productId?if_exists}${externalKeyParam}" class="btn-link" target="_blank">${(product.internalName)?if_exists}</a>
                  </td>
                  <td>
                    <select name="geo_${rowKey}">
                      <#if product.originGeoId?has_content>
                        <#assign originGeoId = product.originGeoId>
                        <#assign geo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", originGeoId), true)>
                        <option value="${originGeoId}">${geo.geoName?if_exists}</option>
                        <option value="${originGeoId}">---</option>
                      </#if>
                      <option value=""></option>
                      ${screens.render("component://common/widget/CommonScreens.xml#countries")}
                    </select>
                  </td>
                  <td align="">${orderItemQuantity?if_exists}</td>
                  <td align="">${readyToVerify?if_exists}</td>
                  <td align="">
                    <#if (orderItemQuantity.compareTo(readyToVerify) > 0)>
                      <#assign qtyToVerify = orderItemQuantity.subtract(readyToVerify) >
                      <input type="text" size="7" name="qty_${rowKey}" value="${qtyToVerify?if_exists}"/>
                    <#else>
                      0
                    </#if>
                  </td>
                  <input type="hidden" name="prd_${rowKey}" value="${(orderItem.productId)?if_exists}"/>
                  <input type="hidden" name="ite_${rowKey}" value="${(orderItem.orderItemSeqId)?if_exists}"/>
                </tr>
                <#assign workOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment")/>
                <#if workOrderItemFulfillments?has_content>
                  <#assign workOrderItemFulfillment = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workOrderItemFulfillments)/>
                  <#if workOrderItemFulfillment?has_content>
                    <#assign workEffort = workOrderItemFulfillment.getRelatedOne("WorkEffort")/>
                    <#if workEffort?has_content>
                      <#assign workEffortTask = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("WorkEffort", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortParentId", workEffort.workEffortId)))/>
                      <#if workEffortTask?has_content>
                        <#assign workEffortInventoryAssigns = workEffortTask.getRelated("WorkEffortInventoryAssign")/>
                        <#if workEffortInventoryAssigns?has_content>
                          <tr>
                            <th colspan="8">
                              ${uiLabelMap.OrderMarketingPackageComposedBy}
                            </th>
                          </tr>
                          <tr><td colspan="8"><hr /></td></tr>
                          <#list workEffortInventoryAssigns as workEffortInventoryAssign>
                            <#assign inventoryItem = workEffortInventoryAssign.getRelatedOne("InventoryItem")/>
                            <#assign product = inventoryItem.getRelatedOne("Product")/>
                            <tr>
                              <td colspan="2"></td>
                              <td>${product.productId?default("N/A")}</td>
                              <td>${product.internalName?if_exists}</td>
                              <td></td>
                              <td align="right">${workEffortInventoryAssign.quantity?if_exists}</td>
                            </tr>
                          </#list>
                          <tr><td colspan="8"><hr /></td></tr>
                        </#if>
                      </#if>
                    </#if>
                  </#if>
                </#if>
                <#assign rowKey = rowKey + 1>
              </#list>
            </#if>
            <tr>
              <td colspan="10">&nbsp;</td>
            </tr>
            <tr>
              <td colspan="12" align="right">
                  <input type="submit" value="${uiLabelMap.ProductVerify}&nbsp;${uiLabelMap.OrderItems}" class="buttontext"/>
                &nbsp;
                  <input type="button" value="${uiLabelMap.CommonCancel}" class="btn btn=danger" onclick="javascript:document.clearPickForm.submit();"/>
                <a href="/facility/control/VerifyPickInShipment?facilityId=${parameters.facilityId?if_exists}&amp;mode=${requestParameters.mode}&amp;shipmentId=${parameters.shipmentId}" class="buttontext" >${uiLabelMap.CommonGoBack}</a>
                
              </td>
            </tr>
          </table>
        </form>
        <br />
      </div>
    </div>
    <#assign orderId = orderId?if_exists >
    <#assign pickRows = verifyPickSession.getPickRows(orderId)?if_exists>
    <form name="completePickForm" method="post" action="<@ofbizUrl>completeVerifiedPickShipment?mode=${requestParameters.mode}</@ofbizUrl>">
      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
      <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
      <input type="hidden" name="facilityId" value="${facility.facilityId?if_exists}"/>
      <input type="hidden" name="userLoginId" value="${userLoginId?if_exists}"/>
       <input type="hidden" name="shipmentId" value="${shipment?if_exists}"/>
      <#if pickRows?has_content>
        <div class="screenlet">
          <div class="screenlet-title-bar">
            <ul>
              <li class="h3">${uiLabelMap.ProductVerified}&nbsp;${uiLabelMap.OrderItems} : ${pickRows.size()?if_exists}</li>
            </ul>
            <br class="clear"/>
          </div>
          <div class="screenlet-body">
            <table class="basic-table" cellspacing='0'>
              <tr class="header-row">
                <td>${uiLabelMap.ProductItem} #</td>
                <td>${uiLabelMap.ProductProductId}</td>
                <td>${uiLabelMap.ProductInventoryItem} #</td>
                <td align="">${uiLabelMap.ProductVerified}&nbsp;${uiLabelMap.CommonQuantity}</td>
                <td>&nbsp;</td>
              </tr>
              <#list pickRows as pickRow>
                <#if (pickRow.getOrderId()?if_exists).equals(orderId)>
                  <tr>
                    <td>${pickRow.getOrderItemSeqId()?if_exists}</td>
                    <td>${pickRow.getProductId()?if_exists}</td>
                    <td>${pickRow.getInventoryItemId()?if_exists}</td>
                    <td align="">${pickRow.getReadyToVerifyQty()?if_exists}</td>
                  </tr>
                </#if>
              </#list>
            </table>
            <div align="right">
              <a href="javascript:document.completePickForm.submit()" class="buttontext">${uiLabelMap.ProductComplete}</a>
            </div>
          </div>
        </div>
      </#if>
    </form>
 
  </#if>
  
  <#if orderId?has_content>
    <script language="javascript" type="text/javascript">
      document.singlePickForm.productId.focus();
    </script>
  <#else>
    <script language="javascript" type="text/javascript">
      document.selectOrderForm.orderId.focus();
    </script>
  </#if>
  <#if shipmentId?has_content>
    <script language="javascript" type="text/javascript">
      document.selectOrderForm.orderId.focus();
    </script>
  </#if>
<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>