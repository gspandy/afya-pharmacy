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

    <fo:block font-size="11pt" font-weight="bold" margin-top="5px" text-indent="0.1in">Deliver To:</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <fo:block font-size="10pt" text-indent="0.1in">
        <fo:block>${companyName}</fo:block>
        <#if postalAddress?exists>
            <#if postalAddress?has_content>
                <fo:block>${postalAddress.address1?if_exists}</fo:block>
                <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
                <fo:block>${postalAddress.city?if_exists}, ${countryName?if_exists}.</fo:block>
            </#if>
        <#else>
            <fo:block>${uiLabelMap.CommonNoPostalAddress}</fo:block>
            <fo:block>${uiLabelMap.CommonFor}: ${companyName}</fo:block>
        </#if>
        
        <fo:block><fo:leader></fo:leader></fo:block>

        <#if sendingPartyTaxId?exists || phone?exists || email?exists || website?exists || eftAccount?exists>
            <fo:list-block provisional-distance-between-starts=".5in">
                <#if email?exists>
                    <fo:list-item>
                        <fo:list-item-label>
                            <fo:block>${uiLabelMap.CommonEmail}:</fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block>${email.infoString?if_exists}</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </#if>
                <#if phone?exists>
                    <fo:list-item>
                        <fo:list-item-label>
                            <fo:block>${uiLabelMap.CommonTelephoneAbbr}:</fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()">
                            <fo:block><#if phone.countryCode?exists>+${phone.countryCode} </#if><#if phone.areaCode?exists>${phone.areaCode} </#if>${phone.contactNumber?if_exists}</fo:block>
                        </fo:list-item-body>
                    </fo:list-item>
                </#if>
            </fo:list-block>
        </#if>
    </fo:block>

</#escape>
