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
      <fo:table-column/>
      <#-- <fo:table-column/> -->
      <fo:table-body>
        <fo:table-row >
          <fo:table-cell>
            <fo:block margin-left="3px">
              <#if company.logoImageUrl?has_content><fo:external-graphic src="./framework/images/webapp/images/${company.logoImageUrl}" overflow="hidden" height="65px" content-height="scale-to-fit"/></#if>
            </fo:block>
            <fo:block font-size="8pt" margin-left="3px">
              <#assign billingPartyAddress=Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestPostalAddress(company.partyId,delegator)>
              <#if billingPartyAddress?has_content>
                <#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", company.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
                <#assign stateDescription = delegator.findOne("Geo", {"geoId" : billingPartyAddress.stateProvinceGeoId}, true)>
                <#assign countryDescription = delegator.findOne("Geo", {"geoId" : billingPartyAddress.countryGeoId}, true)>
                <fo:block font-weight="bold">${billingPartyNameResult.fullName?default(billingPartyAddress.toName)?default("Billing Name Not Found")}</fo:block>
                <#if billingPartyAddress.attnName?exists>
                  <fo:block>${billingPartyAddress.attnName}</fo:block>
                </#if>
                <fo:block>${billingPartyAddress.address1?if_exists}</fo:block>
                <#if billingPartyAddress.address2?exists>
                  <fo:block>${billingPartyAddress.address2}</fo:block>
                </#if>
                <fo:block>${billingPartyAddress.city?if_exists} ${billingPartyAddress.stateProvinceGeoId?if_exists} ${billingPartyAddress.postalCode?if_exists}</fo:block>
                <#--${stateDescription.geoName?if_exists}  ${countryDescription.geoName?if_exists} -->
                <fo:block><fo:leader></fo:leader></fo:block>
                <#assign phone = (Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestTelecomNumber(company.partyId,delegator))>
                <#if phone?has_content && phone!=null>
                  <fo:block>Tel: <#if phone.countryCode?exists>+${phone.countryCode} </#if><#if phone.areaCode?exists>${phone.areaCode} </#if>${phone.contactNumber?if_exists}</fo:block>
                </#if>
                <#assign billingPartyEmail = (Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestContactMech(company.partyId,"EMAIL_ADDRESS",delegator))>
                <#if billingPartyEmail?exists><fo:block>Email: ${billingPartyEmail.infoString?if_exists}</fo:block></#if>
              <#else>
                <fo:block>${uiLabelMap.AccountingNoGenBilAddressFound}${company.partyId}</fo:block>
              </#if>
            </fo:block>
          </fo:table-cell>
          <#-- <fo:table-cell>
            <fo:block><fo:leader></fo:leader></fo:block>
            <fo:block font-size="8pt" margin-right="5px" text-align="right">Tel: <#if phone.countryCode?exists>+${phone.countryCode} </#if><#if phone.areaCode?exists>${phone.areaCode} </#if>${phone.contactNumber?if_exists}</fo:block>
            <fo:block><fo:leader></fo:leader></fo:block>
            <fo:block font-size="8pt" margin-right="5px" text-align="right">Company Reg #</fo:block>
            <fo:block font-size="8pt" margin-right="5px" text-align="right">TPIN#: </fo:block>
          </fo:table-cell> -->
        </fo:table-row>
      </fo:table-body>
    <#else>
      <fo:table-column column-width="3.5in"/>
      <fo:table-body>
        <fo:table-row >
          <fo:table-cell>
            <fo:block font-size="10pt" margin-left="3px">
              <#assign billingPartyAddress=Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestPostalAddress(company.partyId,delegator)>
              <#if billingPartyAddress?has_content>
                <#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", company.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
                <#assign stateDescription = delegator.findOne("Geo", {"geoId" : billingPartyAddress.stateProvinceGeoId}, true)>
                <#assign countryDescription = delegator.findOne("Geo", {"geoId" : billingPartyAddress.countryGeoId}, true)>
                <fo:block font-weight="bold">${billingPartyNameResult.fullName?default(billingPartyAddress.toName)?default("Billing Name Not Found")}</fo:block>
                <#if billingPartyAddress.attnName?exists>
                  <fo:block>${billingPartyAddress.attnName}</fo:block>
                </#if>
                <fo:block>${billingPartyAddress.address1?if_exists}</fo:block>
                <#if billingPartyAddress.address2?exists>
                  <fo:block>${billingPartyAddress.address2}</fo:block>
                </#if>
                <fo:block>${billingPartyAddress.city?if_exists} ${billingPartyAddress.stateProvinceGeoId?if_exists} ${billingPartyAddress.postalCode?if_exists}</fo:block>
                <#--${stateDescription.geoName?if_exists}  ${countryDescription.geoName?if_exists} -->
              <#else>
                <fo:block>${uiLabelMap.AccountingNoGenBilAddressFound}${company.partyId}</fo:block>
              </#if>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </#if>
  </fo:table>
</#escape>