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

    <fo:block font-size="11pt" font-weight="bold" margin-top="5px" text-indent="0.1in">Supplier:</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <#if orderHeader.getString("orderTypeId") == "PURCHASE_ORDER">
        <#if supplierGeneralContactMechValueMap?exists>
            <#assign contactMech = supplierGeneralContactMechValueMap.contactMech>
            <#assign postalAddress = supplierGeneralContactMechValueMap.postalAddress>
            <#assign partyGroupGv = delegator.findOne("PartyGroup",{"partyId":supplierId},false)/>
            <fo:block font-size="10pt" text-indent="0.1in"> <#if partyGroupGv?has_content>${partyGroupGv.groupName?if_exists}<#else>${supplierPartyId}</#if> </fo:block>
            <#if postalAddress?has_content>
                <fo:block font-size="10pt" text-indent="0.1in">
                    <#-- <#if postalAddress.toName?has_content><fo:block>${postalAddress.toName}</fo:block></#if> -->
                    <#if postalAddress.attnName?has_content><fo:block>${postalAddress.attnName?if_exists}</fo:block></#if>
                    <fo:block>${postalAddress.address1?if_exists}</fo:block>
                    <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
                    <fo:block>
                        <#assign stateGeo = (delegator.findOne("Geo", {"geoId", postalAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
                        ${postalAddress.city}<#if stateGeo?has_content>, ${stateGeo.geoName?if_exists}</#if> ${postalAddress.postalCode?if_exists}
                    </fo:block>
                    <fo:block>
                        <#assign countryGeo = (delegator.findOne("Geo", {"geoId", postalAddress.countryGeoId?if_exists}, false))?if_exists />
                        <#if countryGeo?has_content>${countryGeo.geoName?if_exists}</#if>
                    </fo:block>
                </fo:block>
            </#if>
        <#else>
            <#-- here we just display the name of the vendor, since there is no address -->
            <#assign vendorParty = orderReadHelper.getBillFromParty()>
            <fo:block>
                <fo:inline font-weight="bold">${uiLabelMap.OrderPurchasedFrom}:</fo:inline> ${Static['org.ofbiz.party.party.PartyHelper'].getPartyName(vendorParty)}
            </fo:block>
        </#if>
    </#if>

</#escape>
