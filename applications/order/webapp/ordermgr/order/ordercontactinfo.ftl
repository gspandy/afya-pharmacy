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

<#macro partyPostalAddress postalContactMechList contactMechPurposeTypeId contactPostalAddress>
<select name="contactMechId">
    <option value="${contactPostalAddress.contactMechId}">${(contactPostalAddress.address1)?default("")}
        - ${contactPostalAddress.city?default("")}</option>
    <option value="${contactPostalAddress.contactMechId}"></option>
    <#list postalContactMechList as postalContactMech>
        <#assign postalAddress = postalContactMech.getRelatedOne("PostalAddress")?if_exists>
        <#assign partyContactPurposes = postalAddress.getRelated("PartyContactMechPurpose")?if_exists>
        <#list partyContactPurposes as partyContactPurpose>
            <#if postalContactMech.contactMechId?has_content && partyContactPurpose.contactMechPurposeTypeId == contactMechPurposeTypeId>
                <option value="${postalContactMech.contactMechId?if_exists}">${(postalAddress.address1)?default("")}
                    - ${postalAddress.city?default("")}</option>
            </#if>
        </#list>
    </#list>
</select>
</#macro>

<#if displayParty?has_content || orderContactMechValueMaps?has_content>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">&nbsp;${uiLabelMap.OrderContactInformation}</li>
            <li class="collapsed"><a onclick="javascript:toggleScreenlet(this, 'OrderContactInfoScreenletBody_${orderId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Expand">&nbsp;</a></li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body" id="OrderContactInfoScreenletBody_${orderId}" style="display: none;">
        <table class="basic-table" cellspacing='0'>
            <tr>
                <td align="right" valign="top" width="19%"><span class="label">&nbsp;${uiLabelMap.CommonName}</span>
                </td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="80%">
                    <div>
                        <#if displayParty?has_content>
                            <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                        ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
                        </#if>
                        <#if partyId?exists>
                            &nbsp;(<a href="${customerDetailLink}${partyId}&amp;externalLoginKey=${externalLoginKey}"
                                      target="partymgr" class="btn-link">${partyId}</a>)
                            <br/>
                            <#if orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL">
                                <div>
                                    <!-- <a href="<@ofbizUrl>/orderentry?partyId=${partyId}&amp;orderTypeId=${orderHeader.orderTypeId}</@ofbizUrl>" class="btn">${uiLabelMap.OrderNewOrder}</a> -->
                                    <!-- <a href="javascript:document.searchOtherOrders.submit()" class="btn">${uiLabelMap.OrderOtherOrders}</a> -->
                                </div>
                                <form name="searchOtherOrders" method="post"
                                      action="<@ofbizUrl>searchorders</@ofbizUrl>">
                                    <input type="hidden" name="lookupFlag" value="Y"/>
                                    <input type="hidden" name="hideFields" value="Y"/>
                                    <input type="hidden" name="partyId" value="${partyId}"/>
                                    <input type="hidden" name="viewIndex" value="1"/>
                                    <input type="hidden" name="viewSize" value="20"/>
                                </form>
                            </#if>
                        </#if>
                    </div>
                </td>
            </tr>
            <#if partyId?exists>
                <#assign contactMechPurpose = delegator.findByAnd("PartyContactMechPurpose",{"partyId":partyId,"contactMechPurposeTypeId":"PRIMARY_EMAIL"})/>
                <#if !contactMechPurpose?has_content>
                    <tr>
                        <td align="right" valign="top" width="19%">
                            <span class="label">&nbsp; Order Notification Email Address</span>
                        </td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="80%">

                            <form action="confirmationmailedit" name="emailForm">
                                <input type="hidden" name="orderId" value="${orderId}"/>
                                <input type="hidden" name="partyId" value="${partyId}"/>
                                <input type="hidden" name="sendTo" value="dummyemail@domain.com"/>
                            </form>
                            dummyemail@domain.com<br/>
                            (<a href="javascript:onClick();" class="btn btn-link">${uiLabelMap.OrderSendConfirmationEmail}</a>)
                            <script>
                            function onClick(){
                                document.emailForm.submit();
                            }
                            </script>
                        </td>
                    </tr>
                </#if>
            <tr>
            </#if>
            <#list orderContactMechValueMaps as orderContactMechValueMap>
                <#assign contactMech = orderContactMechValueMap.contactMech>
                <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
            <#--<#assign partyContactMech = orderContactMechValueMap.partyContactMech>-->

                <td align="right" valign="top" width="19%">
                    <span class="label">&nbsp;${contactMechPurpose.get("description",locale)}</span>
                </td>
                <td width="1%">&nbsp;</td>
                <td valign="top" width="80%">
                    <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                        <#assign postalAddress = orderContactMechValueMap.postalAddress>
                        <#if postalAddress?has_content>
                            <div>
                                <#if postalAddress.toName?has_content><span
                                        class="label">${uiLabelMap.CommonTo}</span> ${postalAddress.toName}<br/></#if>
                                <#if postalAddress.attnName?has_content><span
                                        class="label">${uiLabelMap.CommonAttn}</span> ${postalAddress.attnName}
                                    <br/></#if>
                            ${postalAddress.address1}<br/>
                                <#if postalAddress.address2?has_content>${postalAddress.address2}<br/></#if>
                            ${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content>
                                , ${postalAddress.stateProvinceGeoId} </#if>
                            ${postalAddress.postalCode?if_exists}<br/>
                            ${postalAddress.countryGeoId?if_exists}<br/>
                                <#if !postalAddress.countryGeoId?exists || postalAddress.countryGeoId == "USA">
                                    <#assign addr1 = postalAddress.address1?if_exists>
                                    <#if (addr1.indexOf(" ") > 0)>
                                        <#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
                                        <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
                                        <!--<a target="_blank" href="${uiLabelMap.CommonLookupWhitepagesAddressLink}" class="btn">${uiLabelMap.CommonLookupWhitepages}</a>-->
                                    </#if>
                                </#if>
                            </div>
                            <!--     <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                  <form name="updateOrderContactMech" method="post" action="<@ofbizUrl>updateOrderContactMech</@ofbizUrl>">
                     <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${contactMechPurpose.contactMechPurposeTypeId?if_exists}"/>
                     <input type="hidden" name="oldContactMechId" value="${contactMech.contactMechId?if_exists}"/>
                     <hr />
                     <div>
                     <@partyPostalAddress postalContactMechList = postalContactMechList?if_exists contactMechPurposeTypeId = contactMechPurpose.contactMechPurposeTypeId?if_exists contactPostalAddress=postalAddress?if_exists/>
                      <input type="submit" value="${uiLabelMap.CommonUpdate}" class="btn btn-primary"/>
                     </div>
                  </form>
                  </#if> -->
                        </#if>
                    <#elseif contactMech.contactMechTypeId == "TELECOM_NUMBER">
                        <#assign telecomNumber = orderContactMechValueMap.telecomNumber>
                        <div>
                        ${telecomNumber.countryCode?if_exists}
                            <#if telecomNumber.areaCode?exists>${telecomNumber.areaCode}
                                -</#if>${telecomNumber.contactNumber}
                        <#--<#if partyContactMech.extension?exists>ext&nbsp;${partyContactMech.extension}</#if>-->
                            <#if !telecomNumber.countryCode?exists || telecomNumber.countryCode == "011" || telecomNumber.countryCode == "1">
                                <a target="_blank" href="${uiLabelMap.CommonLookupAnywhoLink}"
                                   class="btn">${uiLabelMap.CommonLookupAnywho}</a>
                                <a target="_blank" href="${uiLabelMap.CommonLookupWhitepagesTelNumberLink}"
                                   class="btn">${uiLabelMap.CommonLookupWhitepages}</a>
                            </#if>
                        </div>
                    <#elseif contactMech.contactMechTypeId == "EMAIL_ADDRESS">
                        <div>
                        ${contactMech.infoString}<br/>
                            (<a href="<@ofbizUrl>confirmationmailedit?orderId=${orderId}&amp;partyId=${partyId}&amp;sendTo=${contactMech.infoString}</@ofbizUrl>"
                                class="btn btn-link">${uiLabelMap.OrderSendConfirmationEmail}</a>)
                        </div>
                    <#elseif contactMech.contactMechTypeId == "WEB_ADDRESS">
                        <div>
                        ${contactMech.infoString}
                            <#assign openString = contactMech.infoString>
                            <#if !openString?starts_with("http") && !openString?starts_with("HTTP")>
                                <#assign openString = "http://" + openString>
                            </#if>
                            <a target="_blank" href="${openString}" class="btn">(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
                        </div>
                    <#else>
                        <div>
                        ${contactMech.infoString?if_exists}
                        </div>
                    </#if>
                </td>
            </tr>
            </#list>
        </table>
    </div>
</div>
</#if>
