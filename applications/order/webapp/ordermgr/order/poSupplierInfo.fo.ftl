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

    <fo:block font-size="11pt" font-weight="bold" margin-top="5px" margin-bottom="5px" text-indent="0.1in">Supplier:</fo:block>
    <#if orderHeader.getString("orderTypeId") == "PURCHASE_ORDER">
        <#if supplierGeneralContactMechValueMap?exists>
            <#assign contactMech = supplierGeneralContactMechValueMap.contactMech>
            <#assign postalAddress = supplierGeneralContactMechValueMap.postalAddress>
            <#assign partyGroupGv = delegator.findOne("PartyGroup",{"partyId":supplierId},false)/>
            <fo:block font-size="8pt" margin-bottom="5px"  text-indent="0.1in"> 
                <#if partyGroupGv?has_content>${partyGroupGv.groupName?if_exists}<#else>${supplierPartyId}</#if>
                <#if postalAddress?has_content>
                    <fo:block>${postalAddress.city?if_exists}</fo:block>
                    <#-- <#if postalAddress.toName?has_content><fo:block>${postalAddress.toName}</fo:block></#if> -->
                    <fo:block>
                        <#-- <#if postalAddress.attnName?has_content>${postalAddress.attnName?if_exists}, </#if> -->
                        <#if postalAddress.address1?has_content>${postalAddress.address1?if_exists}, </#if>
                        <#if postalAddress.address2?has_content>${postalAddress.address2?if_exists}, </#if>
                    </fo:block>
                    <fo:block>
                        <#if postalAddress.postalCode?has_content>${postalAddress.postalCode?if_exists} - </#if>
                        <#assign stateGeo = (delegator.findOne("Geo", {"geoId", postalAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
                        <#if stateGeo?has_content>${stateGeo.geoName?if_exists}, </#if>
                        <#assign countryGeo = (delegator.findOne("Geo", {"geoId", postalAddress.countryGeoId?if_exists}, false))?if_exists />
                        <#if countryGeo?has_content>${countryGeo.geoName?if_exists}.</#if>
                    </fo:block>
                </#if>
                <#if phone?exists || email?exists>
                    <fo:list-block provisional-distance-between-starts=".5in">
                        <#if phone?exists && phone.contactNumber?has_content>
                            <fo:list-item>
                                <fo:list-item-label>
                                    <fo:block>${uiLabelMap.CommonTelephoneAbbr}:</fo:block>
                                </fo:list-item-label>
                                <fo:list-item-body start-indent="body-start()">
                                    <fo:block><#if phone.countryCode?exists>${phone.countryCode} </#if><#if phone.areaCode?exists>${phone.areaCode} </#if>${phone.contactNumber?if_exists}</fo:block>
                                </fo:list-item-body>
                            </fo:list-item>
                        </#if>
                        <#if email?exists && email.infoString?has_content>
                            <fo:list-item>
                                <fo:list-item-label>
                                    <fo:block>${uiLabelMap.CommonEmail}:</fo:block>
                                </fo:list-item-label>
                                <fo:list-item-body start-indent="body-start()">
                                    <fo:block>${email.infoString?if_exists}</fo:block>
                                </fo:list-item-body>
                            </fo:list-item>
                        </#if>
                    </fo:list-block>
                </#if>
            </fo:block>
        <#else>
            <#-- here we just display the name of the vendor, since there is no address -->
            <#assign vendorParty = orderReadHelper.getBillFromParty()>
            <fo:block>
                <fo:inline font-weight="bold">${uiLabelMap.OrderPurchasedFrom}:</fo:inline> ${Static['org.ofbiz.party.party.PartyHelper'].getPartyName(vendorParty)}
            </fo:block>
        </#if>
    </#if>

</#escape>
