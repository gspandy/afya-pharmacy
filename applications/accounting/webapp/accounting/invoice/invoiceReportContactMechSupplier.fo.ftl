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
<#escape x as x?xml>
  <fo:table table-layout="fixed" width="100%" space-after="0.3in">
    <#if "SALES_INVOICE"==invoice.invoiceTypeId>
      <fo:table-column column-width="2in"/>
      <fo:table-column/>
      <fo:table-body>
        <fo:table-row >
          <fo:table-cell>
            <fo:block margin-left="3px">
              <#if logoImageUrl?has_content><fo:external-graphic src="./framework/images/webapp/images/${logoImageUrl}" overflow="hidden" height="65px" content-height="scale-to-fit"/></#if>
            </fo:block>
            <fo:block font-size="8pt" margin-left="3px">
              <#assign supplierAddress=Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestPostalAddress(sendingParty.partyId,delegator)>
              <#if supplierAddress?has_content>
                <#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", sendingParty.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
                <#assign stateDescription = delegator.findOne("Geo", {"geoId" : supplierAddress.stateProvinceGeoId}, true)>
                <#assign countryDescription = delegator.findOne("Geo", {"geoId" : supplierAddress.countryGeoId}, true)>
                <fo:block font-weight="bold">${billingPartyNameResult.fullName?default(supplierAddress.toName)?default("Billing Name Not Found")}</fo:block>
                <#if supplierAddress.attnName?exists>
                  <fo:block>${supplierAddress.attnName}</fo:block>
                </#if>
                <fo:block>${supplierAddress.address1?if_exists}</fo:block>
                <#if supplierAddress.address2?exists>
                  <fo:block>${supplierAddress.address2}</fo:block>
                </#if>
                <fo:block>${supplierAddress.city?if_exists} ${supplierAddress.stateProvinceGeoId?if_exists} ${supplierAddress.postalCode?if_exists}</fo:block>
                <#--${stateDescription.geoName?if_exists}  ${countryDescription.geoName?if_exists} -->
                <#assign billingPartyEmail = (Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestContactMech(sendingParty.partyId,"EMAIL_ADDRESS",delegator))>
                <#if billingPartyEmail?exists> <fo:block>${billingPartyEmail.infoString?if_exists}</fo:block></#if>
              <#else>
                <fo:block>${uiLabelMap.AccountingNoGenBilAddressFound}${sendingParty.partyId}</fo:block>
              </#if>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block><fo:leader></fo:leader></fo:block>
            <fo:block font-size="8pt" margin-right="5px" text-align="right">Tel: <#if phone.countryCode?exists>+${phone.countryCode} </#if><#if phone.areaCode?exists>${phone.areaCode} </#if>${phone.contactNumber?if_exists}</fo:block>
            <fo:block><fo:leader></fo:leader></fo:block>
            <fo:block font-size="8pt" margin-right="5px" text-align="right">Company Reg #57313</fo:block>
            <fo:block font-size="8pt" margin-right="5px" text-align="right">TPIN#: 1001806300</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    <#else>
      <fo:table-column column-width="3.5in"/>
      <fo:table-body>
        <fo:table-row >
          <fo:table-cell>
            <fo:block font-size="10pt" margin-left="3px">
              <#assign supplierAddress=Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestPostalAddress(sendingParty.partyId,delegator)>
              <#if supplierAddress?has_content>
                <#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", sendingParty.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
                <#assign stateDescription = delegator.findOne("Geo", {"geoId" : supplierAddress.stateProvinceGeoId}, true)>
                <#assign countryDescription = delegator.findOne("Geo", {"geoId" : supplierAddress.countryGeoId}, true)>
                <fo:block font-weight="bold">${billingPartyNameResult.fullName?default(supplierAddress.toName)?default("Billing Name Not Found")}</fo:block>
                <#if supplierAddress.attnName?exists>
                  <fo:block>${supplierAddress.attnName}</fo:block>
                </#if>
                <fo:block>${supplierAddress.address1?if_exists}</fo:block>
                <#if supplierAddress.address2?exists>
                  <fo:block>${supplierAddress.address2}</fo:block>
                </#if>
                <fo:block>${supplierAddress.city?if_exists} ${supplierAddress.stateProvinceGeoId?if_exists} ${supplierAddress.postalCode?if_exists}</fo:block>
                <#--${stateDescription.geoName?if_exists}  ${countryDescription.geoName?if_exists} -->
              <#else>
                <fo:block>${uiLabelMap.AccountingNoGenBilAddressFound}${sendingParty.partyId}</fo:block>
              </#if>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </#if>
  </fo:table>
</#escape>