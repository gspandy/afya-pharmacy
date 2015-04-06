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
    function showQohAtp() {
        document.qohAtpForm.productId.value = document.quickaddform.add_product_id.value;
        document.qohAtpForm.submit();
    }
    function quicklookupGiftCertificate() {
        window.location='AddGiftCertificate';
    }
    $(document).ready(function(){
        $("#add_product_id").change(function(){
            setQtyUom();
        })
    })
    function setQtyUom() {
        $.ajax({
            url: 'getProductQtyUom',
            async: false,
            type: 'POST',
            data: jQuery('#quickaddform').serialize(),
            success: function(data) {
                $("#qtyUom").html(data.qtyUom);
            }
        });
    }
</script>
<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
  <#assign target="productAvailabalityByFacility">
<#else>
  <#assign target="getProductInventoryAvailable">
</#if>
<div class="screenlet">
    <div class="screenlet-body">
      <#if shoppingCart.getOrderType() == "SALES_ORDER">
        <div>
          <#if quantityOnHandTotal?exists && availableToPromiseTotal?exists && (productId)?exists>
            <ul>
              <li>
                <label>${uiLabelMap.ProductQuantityOnHand}</label>: ${quantityOnHandTotal}
              </li>
              <li>
                <label>${uiLabelMap.ProductAvailableToPromise}</label>: ${availableToPromiseTotal}
              </li>
            </ul>
          </#if>
        </div>
      <#else>
        <#if parameters.availabalityList?has_content>
          <table>
            <tr>
              <td>${uiLabelMap.Facility}</td>
              <td>${uiLabelMap.ProductQuantityOnHand}</td>
              <td>${uiLabelMap.ProductAvailableToPromise}</td>
            </tr>
            <#list parameters.availabalityList as availabality>
               <tr>
                 <td>${availabality.facilityId}</td>
                 <td>${availabality.quantityOnHandTotal}</td>
                 <td>${availabality.availableToPromiseTotal}</td>
               </tr>
            </#list>
          </table>
        </#if>
      </#if>
      <table border="0" cellspacing="0" cellpadding="0"  width="100%">
        <tr>
          <td>
            <form name="qohAtpForm" method="post" action="<@ofbizUrl>${target}</@ofbizUrl>" id="qohAtpForm">
              <fieldset>
                <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
                <input type="hidden" name="productId"/>
                <input type="hidden" id="ownerPartyId" name="ownerPartyId" value="${shoppingCart.getBillToCustomerPartyId()?if_exists}" />
              </fieldset>
            </form>
            <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="quickaddform" style="margin: 0;" id="quickaddform">
              <table border="0" style="width:100%">
                <tr>
                  <td align="right"><div>${uiLabelMap.ProductProductId} :</div></td>
                  <td>
                    <@htmlTemplate.lookupField formName="quickaddform" name="add_product_id" id="add_product_id" fieldFormName="LookupProduct" className="required"/><span><font color="red">*</font></span>
                    <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('add_product_id,<@ofbizUrl>LookupProduct</@ofbizUrl>,ajaxLookup=Y&amp;searchValueField=add_product_id', true);</script>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div>${uiLabelMap.OrderQuantity} :</div></td>
                  <td>
                    <input type="text" size="6" class="quantity required" id="quantity" name="quantity" value="" onfocus="javascript:setQtyUom();"/><span><font color="red">*</font></span>
                    <span id="qtyUom" style="font-weight: bold"/>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div>${uiLabelMap.OrderDesiredDeliveryDate} :</div></td>
                 <td>
                    <div width="100%">
                      <#if useAsDefaultDesiredDeliveryDate?exists> 
                        <#assign value = defaultDesiredDeliveryDate>
                      </#if>
                      <@htmlTemplate.renderDateTimeField name="itemDesiredDeliveryDate" value="${value!''}" className="date" alert="" title="Format: MM/dd/yyyy" size="15" maxlength="10" id="item1" dateType="date-time" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                      <input type="checkbox" name="useAsDefaultDesiredDeliveryDate" value="true"<#if useAsDefaultDesiredDeliveryDate?exists> checked="checked"</#if>/>
                      ${uiLabelMap.OrderUseDefaultDesiredDeliveryDate}
                    </div>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div>${uiLabelMap.CommonComment} :</div></td>
                  <td>
                    <div>
                      <textarea cols="30" rows="2" wrap="hard" name="itemComment" onfocus="javascript:setQtyUom();">${defaultComment?if_exists}</textarea>
                      <input type="checkbox" name="useAsDefaultComment" value="true" <#if useAsDefaultComment?exists>checked="checked"</#if> />
                      ${uiLabelMap.OrderUseDefaultComment}
                    </div>
                  </td>
                </tr>
                <tr>
                  <td></td>
                  <td><input type="submit" class="btn btn-success" value="${uiLabelMap.OrderAddToOrder}"/></td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
      <script>
      		var form = document.quickaddform;
     		jQuery(form).validate();
      </script>
    </div>
</div>

<script language="JavaScript" type="text/javascript">
  document.quickaddform.add_product_id.focus();
  $("#quickaddform").validate();
</script>

<!-- Internal cart info: productStoreId=${shoppingCart.getProductStoreId()?if_exists} locale=${shoppingCart.getLocale()?if_exists} currencyUom=${shoppingCart.getCurrency()?if_exists} userLoginId=${(shoppingCart.getUserLogin().getString("userLoginId"))?if_exists} autoUserLogin=${(shoppingCart.getAutoUserLogin().getString("userLoginId"))?if_exists} -->