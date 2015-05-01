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

<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#assign currencyUomId = shoppingCart.getCurrency()>
<#assign partyId = shoppingCart.getPartyId()>
<#assign partyMap = Static["org.ofbiz.party.party.PartyWorker"].getPartyOtherValues(request, partyId, "party", "person", "partyGroup")>
<#assign agreementId = shoppingCart.getAgreementId()?if_exists>
<#assign quoteId = shoppingCart.getQuoteId()?if_exists>

<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>

<div class="screenlet">
    <div class="screenlet-title-bar" >
        <h3>${uiLabelMap.OrderOrderHeaderInfo}</h3>
    </div>
    <div class="screenlet-body" style="width: 97%;">
               <form  method="post" action="setOrderName" name="setCartOrderNameForm" id="setCartOrderNameForm">
                <fieldset>
                  <label for="orderName"><strong>${uiLabelMap.OrderOrderName}</strong> <span><font color="red">*</font></span></label>
                  <input type="text" id="orderName" style="width: 90%;"  name="orderName"  maxlength="200" value="${shoppingCart.getOrderName()?default("")}" onChange="javascript:setOrderHeaderInfo();" className="required" />
                  <label for="orderDate"><strong>Order Date</strong>: &nbsp; &nbsp;</label>
                  <@htmlTemplate.renderDateTimeFieldJsMethod name="orderDate" value='${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(shoppingCart.getOrderDate())}' className="date" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" id="orderDate" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="setCartOrderNameForm"
                        javaScriptMethod="javascript:setOrderHeaderInfo();"/>
                  <br />
                  <label for="deliveryDate"><strong>Delivery Date</strong>:</label>
                  <@htmlTemplate.renderDateTimeFieldJsMethod name="deliveryDate" value='${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(shoppingCart.getDeliveryDate())}' className="date" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" id="deliveryDate" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="setCartOrderNameForm"
                        javaScriptMethod="javascript:setOrderHeaderInfo();"/>
                  <br />
                </fieldset>
                <script>
                    var form = document.setCartOrderNameForm;
                    jQuery(form).validate();
                </script>
              </form>
              <p>
              <strong>${uiLabelMap.Party}</strong>:
               <a href="${customerDetailLink}${partyId}&amp;externalLoginKey=${externalLoginKey}" target="partymgr" class="btn-link">${partyId}</a> 
                  <#if partyMap.person?exists>
                    ${partyMap.person.firstName?if_exists}&nbsp;${partyMap.person.lastName?if_exists}
                  </#if>
                  <#if partyMap.partyGroup?exists>
                    ${partyMap.partyGroup.groupName?if_exists}
                  </#if>
              </p>
            <#if shoppingCart.getOrderType() != "PURCHASE_ORDER">
                <form method="post" action="setPoNumber" name="setCartPoNumberForm" id="setCartPoNumberForm">
                  <fieldset>
                    <label for="correspondingPoId"><strong>Customer ${uiLabelMap.OrderPONumber}</strong>:</label>
                    <input type="text" id="correspondingPoId" name="correspondingPoId" size="12" value="${shoppingCart.getPoNumber()?default("")}" onChange="javascript:setPONumber();" />
                  </fieldset>
                </form>
            </#if>
            <p>
              <strong>${uiLabelMap.CommonCurrency}</strong>:
              ${currencyUomId}
            </p>
            <#if agreementId?has_content>
            <p>
              <strong>${uiLabelMap.AccountingAgreement}</strong>:
              ${agreementId}
            </p>
            </#if>
            <#if quoteId?has_content>
            <p>
              <strong>${uiLabelMap.OrderOrderQuote}</strong>:
              ${quoteId}
            </p>
            </#if>
            <p><strong>${uiLabelMap.CommonTotal}</strong>: <@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId/></p>
    </div>
</div>
<br />
