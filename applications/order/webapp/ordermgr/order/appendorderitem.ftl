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
  function quicklookup(element) {
    window.location='<@ofbizUrl>LookupBulkAddSupplierProductsInApprovedOrder</@ofbizUrl>?orderId='+element.value;
  }
</script>

<#if orderHeader?has_content>
<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">&nbsp;${uiLabelMap.OrderAddToOrder}</li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
        <form method="post" action="<@ofbizUrl>appendItemToOrder</@ofbizUrl>" name="appendItemForm" id="appendItemForm">
            <input type="hidden" size="25" name="orderId" value="${orderId?if_exists}"/>
            <#if !catalogCol?has_content>
                <input type="hidden" name="prodCatalogId" value=""/>
            </#if>
            <#if catalogCol?has_content && catalogCol?size == 1>
                <input type="hidden" name="prodCatalogId" value="${catalogCol.first}"/>
            </#if>
            <#if shipGroups?size == 1>
                <input type="hidden" name="shipGroupSeqId" value="${shipGroups.first.shipGroupSeqId}"/>
            </#if>
            <table class="basic-table" cellspacing="0">
              <#if catalogCol?has_content && (catalogCol?size > 1)>
                <tr>
                  <td class="label">${uiLabelMap.ProductChooseCatalog}</td>
                  <td><select name='prodCatalogId'>
                    <#list catalogCol as catalogId>
                      <#assign thisCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, catalogId)>
                      <option value='${catalogId}'>${thisCatalogName}</option>
                    </#list>
                  </select>
                  </td>
                </tr>
              </#if>
                <tr>
                  <td class="label">${uiLabelMap.ProductProductId}</td>
                  <td>
                      <#-- FIXME Problem here: the input field is shared -->
                      <@htmlTemplate.lookupField formName="appendItemForm" name="productId" id="productId" fieldFormName="LookupProduct" className="required"/><span><font color="red">*</font></span>
                      <#if "PURCHASE_ORDER" == orderHeader.orderTypeId>
                          <a href="javascript:quicklookup(document.appendItemForm.orderId)" class="btn-link">${uiLabelMap.OrderQuickLookup}</a>
                      </#if>
                  </td>
                </tr>
                <tr>
                  <td class="label">${uiLabelMap.OrderPrice}</td>
                  <td>
                    <input type="text" class="currency" size="6" name="basePrice" value="${requestParameters.price?if_exists}"/>
                    <#-- <input type="checkbox" name="overridePrice" value="Y"/>&nbsp;${uiLabelMap.OrderOverridePrice} -->
                  </td>
                </tr>
                <tr>
                  <td class="label">${uiLabelMap.OrderQuantity}</td>
                  <td><input type="text" size="6" name="quantity" class="quantity required" value="${requestParameters.quantity?if_exists}"/><span><font color="red">*</font></span></td>
                </tr>
              <#if (shipGroups?size > 1)>
                <tr>
                  <td class="label">${uiLabelMap.OrderShipGroup}</td>
                  <td><select name="shipGroupSeqId">
                      <#list shipGroups as shipGroup>
                         <option value="${shipGroup.shipGroupSeqId}">${shipGroup.shipGroupSeqId}</option>
                      </#list>
                      </select>
                  </td>
                </tr>
              </#if>
              <#if "PURCHASE_ORDER" == orderHeader.orderTypeId>
                <tr>
                  <td class="label">${uiLabelMap.OrderDesiredDeliveryDate}</td>
                  <td>
					  <@htmlTemplate.renderDateTimeField name="itemDesiredDeliveryDate" value="${value!''}" className="date" alert="" 
					                title="Format: MM/dd/yyyy" size="15" maxlength="10" id="itemDesiredDeliveryDate" dateType="date-time" shortDateInput=true 
					                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
					                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="appendItemForm"/>
                  </td>
                </tr>
              </#if>
                <tr>
                  <td class="label">${uiLabelMap.OrderReturnReason}</td>
                  <td>
                    <select name="reasonEnumId">
                        <option value="">&nbsp;</option>
                        <#list orderItemChangeReasons as reason>
                        <option value="${reason.enumId}">${reason.get("description",locale)?default(reason.enumId)}</option>
                        </#list>
                    </select>
                  </td>
                </tr>
              <#if "PURCHASE_ORDER" == orderHeader.orderTypeId>
                <tr>
                  <td class="label">${uiLabelMap.CommonComment}</td>
                  <td>
                      <input type="text" size="25" name="changeComments"/>
                  </td>
                </tr>
              </#if>
                <tr>
                  <td class="label">&nbsp;</td>
                  <td><input type="submit" value="${uiLabelMap.OrderAddToOrder}" class="btn btn-success"/></td>
                </tr>
            </table>
            <script>
      		var form = document.appendItemForm;
     		jQuery(form).validate();
      </script>
        </form>
    </div>
</div>
</#if>