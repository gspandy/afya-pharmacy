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

<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEcommerceSecureUrl?exists><#assign urlPrefix = baseEcommerceSecureUrl/></#if>
<#if (orderHeader.externalId)?exists && (orderHeader.externalId)?has_content >
  <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
</#if>

<div class="screenlet">
  <#if paymentMethods?has_content || paymentMethodType?has_content || billingAccount?has_content>
    <#-- order payment info -->
    <h3>${uiLabelMap.AccountingPaymentInformation}</h3>
    <#-- offline payment address infomation :: change this to use Company's address -->
    <ul>
      <#if !paymentMethod?has_content && paymentMethodType?has_content>
        <li>
          <#if paymentMethodType.paymentMethodTypeId == "EXT_OFFLINE">
            ${uiLabelMap.AccountingOfflinePayment}
            <#if orderHeader?has_content && paymentAddress?has_content>
              ${uiLabelMap.OrderSendPaymentTo}:
              <#if paymentAddress.toName?has_content>${paymentAddress.toName}</#if>
              <#if paymentAddress.attnName?has_content>${uiLabelMap.PartyAddrAttnName}: ${paymentAddress.attnName}</#if>
              ${paymentAddress.address1}
              <#if paymentAddress.address2?has_content>${paymentAddress.address2}</#if>
              <#assign paymentStateGeo = (delegator.findOne("Geo", {"geoId", paymentAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
              ${paymentAddress.city}<#if paymentStateGeo?has_content>, ${paymentStateGeo.geoName?if_exists}</#if> ${paymentAddress.postalCode?if_exists}
              <#assign paymentCountryGeo = (delegator.findOne("Geo", {"geoId", paymentAddress.countryGeoId?if_exists}, false))?if_exists />
              <#if paymentCountryGeo?has_content>${paymentCountryGeo.geoName?if_exists}</#if>
              ${uiLabelMap.EcommerceBeSureToIncludeYourOrderNb}
            </#if>
          <#else>
            <#assign outputted = true>
            <table cellspacing="0" cellpadding="0" class="sample">
            <#list orderPaymentPreferences as orderPaymentPreference>
            <tr>
            	<#assign payment={}>
            	<#assign payments=delegator.findByAnd("Payment",{"paymentPreferenceId":orderPaymentPreference.orderPaymentPreferenceId?if_exists},null,false)>
            	<#if payments?has_content>
            		<#assign payment=payments.get(0)>
            	</#if>
            	<#if payment?has_content>
            		<td>
		                 <block style="font-weight:bold;"> Payment Type : </block>
		                  <#assign paymentType=delegator.findOne("PaymentType",{"paymentTypeId":payment.paymentTypeId},false)>
		                  ${paymentType.description?if_exists}
		            </td>
		            <td>
		                  <block style="font-weight:bold;"> Payment Method : </block>
		                  ${payment.paymentMethodTypeId}
		            </td>
		            <td>
		                  <block style="font-weight:bold;"> Amount : </block>
		                  <@ofbizCurrency amount=payment.amount isoCode=payment.currencyUomId/>
		            </td>
            	<#else>
            		<td colspan="2">
            		${uiLabelMap.AccountingPaymentVia} ${paymentMethodType.get("description",locale)}
            		</td>
            	</#if>
            <tr>
            </#list>
            </table>
            <#-- ${uiLabelMap.AccountingPaymentVia} ${paymentMethodType.get("description",locale)} -->
          </#if>
        </li>
      </#if>
      <#if paymentMethods?has_content>
        <#list paymentMethods as paymentMethod>
          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
            <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
            <#assign formattedCardNumber = Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)>
          <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId>
            <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")>
          <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId>
            <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
          </#if>
          <#-- credit card info -->
          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId && creditCard?has_content>
            <#if outputted?default(false)>
            </#if>
            <#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress")>
            <li>
              <ul>
                <li> ${uiLabelMap.AccountingCreditCard}
                  <#if creditCard.companyNameOnCard?has_content>${creditCard.companyNameOnCard}</#if>
                  <#if creditCard.titleOnCard?has_content>${creditCard.titleOnCard}</#if>
                  ${creditCard.firstNameOnCard}
                  <#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}</#if>
                  ${creditCard.lastNameOnCard}
                  <#if creditCard.suffixOnCard?has_content>${creditCard.suffixOnCard}</#if>
                </li>
                <li>${formattedCardNumber}</li>
              </ul>
            </li>
            <#-- Gift Card info -->
          <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId && giftCard?has_content>
            <#if outputted?default(false)>
            </#if>
            <#if giftCard?has_content && giftCard.cardNumber?has_content>
              <#assign pmBillingAddress = giftCard.getRelatedOne("PostalAddress")?if_exists>
              <#assign giftCardNumber = "">
              <#assign pcardNumber = giftCard.cardNumber>
              <#if pcardNumber?has_content>
                <#assign psize = pcardNumber?length - 4>
                <#if 0 < psize>
                  <#list 0 .. psize-1 as foo>
                    <#assign giftCardNumber = giftCardNumber + "*">
                  </#list>
                  <#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3]>
                <#else>
                  <#assign giftCardNumber = pcardNumber>
                </#if>
              </#if>
            </#if>
            <li>
              ${uiLabelMap.AccountingGiftCard}
              ${giftCardNumber}
            </li>
            <#-- EFT account info -->
          <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId && eftAccount?has_content>
            <#if outputted?default(false)>
            </#if>
            <#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress")>
            <li>
              <ul>
                <li>
                  ${uiLabelMap.AccountingEFTAccount}
                  ${eftAccount.nameOnAccount?if_exists}
                </li>
                <li>
                  <#if eftAccount.companyNameOnAccount?has_content>${eftAccount.companyNameOnAccount}</#if>
                </li>
                <li>
                  ${uiLabelMap.AccountingBank}: ${eftAccount.bankName}, ${eftAccount.routingNumber}
                </li>
                <li>
                  ${uiLabelMap.AccountingAccount} #: ${eftAccount.accountNumber}
                </li>
              </ul>
            </li>
          </#if>
          <#if pmBillingAddress?has_content>
            <li>
              <ul>
                <li>
                  <#if pmBillingAddress.toName?has_content>${uiLabelMap.CommonTo}: ${pmBillingAddress.toName}</#if>
                </li>
                <li>
                  <#if pmBillingAddress.attnName?has_content>${uiLabelMap.CommonAttn}: ${pmBillingAddress.attnName}</#if>
                </li>
                <li>
                  ${pmBillingAddress.address1}
                </li>
                <li>
                  <#if pmBillingAddress.address2?has_content>${pmBillingAddress.address2}</#if>
                </li>
                <li>
                <#assign pmBillingStateGeo = (delegator.findOne("Geo", {"geoId", pmBillingAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
                ${pmBillingAddress.city}<#if pmBillingStateGeo?has_content>, ${ pmBillingStateGeo.geoName?if_exists}</#if> ${pmBillingAddress.postalCode?if_exists}
                <#assign pmBillingCountryGeo = (delegator.findOne("Geo", {"geoId", pmBillingAddress.countryGeoId?if_exists}, false))?if_exists />
                <#if pmBillingCountryGeo?has_content>${pmBillingCountryGeo.geoName?if_exists}</#if>
                </li>
              </ul>
            </li>
          </#if>
          <#assign outputted = true>
        </#list>
      </#if>
      <#-- billing account info -->
      <#if billingAccount?has_content>
        <#if outputted?default(false)>
        </#if>
        <#assign outputted = true>
        <li>
          ${uiLabelMap.AccountingBillingAccount}
          #${billingAccount.billingAccountId?if_exists} - ${billingAccount.description?if_exists}
        </li>
      </#if>
      <#if (customerPoNumberSet?has_content)>
        <li>
          ${uiLabelMap.OrderPurchaseOrderNumber}
          <#list customerPoNumberSet as customerPoNumber>
            ${customerPoNumber?if_exists}
          </#list>
        </li>
      </#if>
    </ul>
  </#if>
</div>
</div>

<div class="clearBoth"></div>
