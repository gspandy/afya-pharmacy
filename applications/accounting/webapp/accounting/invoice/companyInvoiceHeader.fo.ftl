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

    <fo:block font-size="8pt" font-family="Calibri" border-width="1pt" border-top-style="none" border-bottom-style="solid" border-left-style="none" border-right-style="none" border-color="black">

        <fo:table table-layout="fixed" width="100%">

            <fo:table-column column-number="1" column-width="proportional-column-width(20)"/>
            <fo:table-column column-number="2" column-width="proportional-column-width(80)"/>

            <fo:table-body>

                <fo:table-row>

                    <fo:table-cell>
                        <fo:block number-columns-spanned="2">
                            <#if company.logoImageUrl?has_content><fo:external-graphic src="./framework/images/webapp/images/${company.logoImageUrl}" overflow="hidden" height="65px" content-height="scale-to-fit"/></#if>
                        </fo:block>
                    </fo:table-cell>
                    
                    <fo:table-cell>
                        <fo:block><fo:leader></fo:leader></fo:block>
                        <fo:block font-size="8pt">
                            <#assign billingPartyAddress=Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestPostalAddress(company.partyId,delegator)>
                            <#if billingPartyAddress?has_content>
                                <#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", company.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
                                <#assign stateDescription = delegator.findOne("Geo", {"geoId" : billingPartyAddress.stateProvinceGeoId}, true)>
                                <#assign countryDescription = delegator.findOne("Geo", {"geoId" : billingPartyAddress.countryGeoId}, true)>
                                <fo:block font-weight="bold">${billingPartyNameResult.fullName?default(billingPartyAddress.toName)?default("Billing Name Not Found")}</fo:block>
                                <#if billingPartyAddress?has_content>
                                    <fo:block>
                                        <#if billingPartyAddress.city?exists>${billingPartyAddress.city?if_exists}</#if>
                                    </fo:block>
                                    <fo:block>
                                        <#if billingPartyAddress.address1?exists>${billingPartyAddress.address1?if_exists}, </#if>
                                        <#if billingPartyAddress.address2?exists>${billingPartyAddress.address2?if_exists},</#if>
                                    </fo:block>
                                </#if>
                                <fo:block>
                                    <#if billingPartyAddress.postalCode?exists>${billingPartyAddress.postalCode?if_exists} - </#if>
                                    <#if stateDescription.geoName?exists>${stateDescription.geoName?if_exists}, </#if>
                                    <#if countryDescription.geoName?exists>${countryDescription.geoName?if_exists}.</#if>
                                </fo:block>
                                <#if companyPhone?has_content>
                                    <fo:block>Tel: <#if companyPhone.countryCode?exists>${companyPhone.countryCode} </#if><#if companyPhone.areaCode?exists>${companyPhone.areaCode} </#if>${companyPhone.contactNumber?if_exists}</fo:block>
                                </#if>
                                <#assign billingPartyEmail = (Static["org.ofbiz.party.party.PartyWorker"].findPartyLatestContactMech(company.partyId,"EMAIL_ADDRESS",delegator))>
                                <#if billingPartyEmail?exists><fo:block>Email: ${billingPartyEmail.infoString?if_exists}</fo:block></#if>
                            <#else>
                                <fo:block>${uiLabelMap.AccountingNoGenBilAddressFound}${company.partyId}</fo:block>
                            </#if>
                        </fo:block>
                    </fo:table-cell>

                </fo:table-row>

            </fo:table-body>

        </fo:table>

    </fo:block>

</#escape>