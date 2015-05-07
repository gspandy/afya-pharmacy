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
                            <#if logoImageUrl?has_content><fo:external-graphic src="./framework/images/webapp/images/${logoImageUrl}" overflow="hidden" height="65px" content-height="scale-to-fit"/></#if>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block><fo:leader></fo:leader></fo:block>
                        <fo:block font-size="8pt">
                            <fo:block font-weight="bold">${companyName}</fo:block>
                            <fo:block>${postalAddress.city?if_exists}</fo:block>
                            <#if postalAddress?has_content>
                                <fo:block>
                                <#if postalAddress.address1?has_content>${postalAddress.address1?if_exists}, </#if>
                                <#if postalAddress.address2?has_content>${postalAddress.address2?if_exists}, </#if>
                                </fo:block>
                                <fo:block>
                                    <#if postalAddress.postalCode?has_content>${postalAddress.postalCode?if_exists} - </#if>
                                    <#if governorate?has_content>${governorate?if_exists}, </#if>
                                    <#if countryName?has_content>${countryName?if_exists}.</#if>
                                </fo:block>
                            </#if>

                            <#if sendingPartyTaxId?exists || phone?exists || email?exists || website?exists || eftAccount?exists>
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
                    </fo:table-cell>

                </fo:table-row>

            </fo:table-body>

        </fo:table>

    </fo:block>

</#escape>
