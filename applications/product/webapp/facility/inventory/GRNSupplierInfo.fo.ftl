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
    <#assign shipmentReceiptAndItems = delegator.findByAnd("ShipmentReceiptAndItem", {"orderId":parameters.purchaseOrderId, "shipmentId":parameters.shipmentId})>
    <#assign orderId = shipmentReceiptAndItems.get(0).get("orderId")>
    <#assign shipmentId = shipmentReceiptAndItems.get(0).get("shipmentId")>
    <#assign shipmentGv = delegator.findOne("Shipment", {"shipmentId",shipmentId},false)>
    <#assign partyGroupGv = delegator.findOne("PartyGroup", {"partyId":shipmentGv.partyIdFrom}, false)>

    <fo:block font-size="11pt" font-weight="bold" margin-top="5px" margin-bottom="5px" text-indent="0.1in">Supplier:</fo:block>
    <#if shipmentReceiptAndItems?has_content>
        <#if orderId?has_content>
            <#assign orderContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getOrderContactMechValueMaps(delegator, orderId)>
            <#if orderContactMechValueMaps?has_content>
                <#list orderContactMechValueMaps as orderContactMechValueMap>
                    <#assign contactMech = orderContactMechValueMap.contactMech>
                    <#assign orderContactMech = orderContactMechValueMap.orderContactMech>
                    <#if orderContactMech?has_content && orderContactMech.contactMechPurposeTypeId == "SHIP_ORIG_LOCATION">
                        <#assign contactMechId = orderContactMech.contactMechId>
                        <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                            <#assign originPostalAddress = orderContactMechValueMap.postalAddress>
                                <fo:block font-size="8pt" margin-bottom="5px" text-indent="0.1in"> 
                                    <#if originPostalAddress?has_content && originPostalAddress.contactMechId == contactMechId>
                                        ${partyGroupGv.groupName?if_exists}
                                        <#if originPostalAddress?has_content>
                                            <fo:block>${originPostalAddress.city?if_exists}</fo:block>
                                            <fo:block>
                                                <#if originPostalAddress.address1?has_content>${originPostalAddress.address1?if_exists}, </#if>
                                                <#if originPostalAddress.address2?has_content>${originPostalAddress.address2?if_exists}, </#if>
                                            </fo:block>
                                            <fo:block>
                                                <#if originPostalAddress.postalCode?has_content>${originPostalAddress.postalCode?if_exists} - </#if>
                                                <#assign stateGeo = (delegator.findOne("Geo", {"geoId", originPostalAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
                                                <#if stateGeo?has_content>${stateGeo.geoName?if_exists}, </#if>
                                                <#assign countryGeo = (delegator.findOne("Geo", {"geoId", originPostalAddress.countryGeoId?if_exists}, false))?if_exists />
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
                                    <#else>
                                        <fo:block margin-top="2px" margin-bottom="3px" border-bottom="dotted">${partyGroupGv.groupName?if_exists}</fo:block>
                                    </#if>
                                </fo:block>
                            </#if>
                        </#if>
                    </#list>
                </#if>
            </#if>
        </#if>

</#escape>
