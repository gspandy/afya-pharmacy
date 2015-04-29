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
        <#assign fromPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", returnHeader.fromPartyId, "compareDate", returnHeader.entryDate, "userLogin", userLogin))/>
        <#assign toPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", returnHeader.toPartyId, "compareDate", returnHeader.entryDate, "userLogin", userLogin))/>
        <#assign isFromCompany=false>
        <#assign isToCompany=false>
        <#assign companyIdTo = returnHeader.toPartyId>
        <#assign companyIdFrom = returnHeader.fromPartyId>
          <#if 'VENDOR_RETURN' = returnHeader.returnHeaderTypeId>
	          <#assign isFromCompany = true>
	      </#if>
	      <#if 'CUSTOMER_RETURN' = returnHeader.returnHeaderTypeId>
	          <#assign isToCompany = true>
	      </#if>
        <fo:table>
          <fo:table-column column-width="3.50in"/>
          <fo:table-column column-width="1.00in"/>
          <fo:table-column column-width="2.75in"/>
          <fo:table-body>
          <fo:table-row>
            <fo:table-cell>
            <fo:table border-style="solid" border-width="0.2pt" height="1in">
              <fo:table-column column-width="3.50in"/>
              <fo:table-body>
                <fo:table-row><fo:table-cell border-style="solid" border-width="0.2pt" padding="1mm"><fo:block font-weight="bold">${uiLabelMap.OrderReturnFromAddress}</fo:block></fo:table-cell></fo:table-row>
                <fo:table-row><fo:table-cell padding="1mm">
	              <fo:block>
                    <#if fromPartyNameResult.fullName?has_content>${fromPartyNameResult.fullName}<#else/><#if postalAddressTo?exists><#if (postalAddressTo.toName)?has_content>${postalAddressTo.toName}</#if></#if></#if>
                  </fo:block>
                  <fo:block>
                    <#if contactOrgin?has_content && (contactOrgin.attnName)?has_content>${contactOrgin.attnName}</#if>
                  </fo:block>
                  <fo:block>
	                <#if contactOrgin?has_content && (contactOrgin.address1)?has_content>${contactOrgin.address1?if_exists}</#if>
	                <#if contactOrgin?has_content && (contactOrgin.address2)?has_content> ${contactOrgin.address2?if_exists}</#if>
	                <#if contactOrgin?has_content && (contactOrgin.city)?has_content>${contactOrgin.city?if_exists}</#if>
	                <#if contactOrgin?has_content>
	                  <#assign stateDescription1 = delegator.findOne("Geo", {"geoId" : contactOrgin.stateProvinceGeoId?if_exists}, true)>
	                  <#if (stateDescription1)?has_content>${stateDescription1.geoName?if_exists?lower_case}</#if>
	                </#if>
	                <#if contactOrgin?has_content && (contactOrgin.postalCode)?has_content>${contactOrgin.postalCode?if_exists}</#if>
                    <#if contactOrgin?has_content>
	                  <#assign countryDescription1 = delegator.findOne("Geo", {"geoId" : contactOrgin.countryGeoId}, true)>
	                  <#if (countryDescription1)?has_content>${countryDescription1.geoName?if_exists}</#if>
	                </#if>
                  </fo:block>
                </fo:table-cell></fo:table-row>
              </fo:table-body>
            </fo:table>
            </fo:table-cell>

            <fo:table-cell/>

            <fo:table-cell>
            <fo:table border-style="solid" border-width="0.2pt" height="1in">
              <fo:table-column column-width="2.75in"/>
              <fo:table-body>
                <fo:table-row><fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt"><fo:block font-weight="bold">${uiLabelMap.OrderReturnToAddress}</fo:block></fo:table-cell></fo:table-row>
                <fo:table-row><fo:table-cell padding="1mm">
                  <fo:block>
	                <#if toPartyNameResult.fullName?has_content>${toPartyNameResult.fullName?if_exists}</#if><br/>
	              </fo:block>
	              <fo:block>
	                <#if contactDestination?has_content && (contactDestination.attnName)?has_content>${contactDestination.attnName?if_exists}</#if>
	              </fo:block>
	              <fo:block>
	                <#if contactDestination?has_content && (contactDestination.address1)?has_content>${contactDestination.address1?if_exists}</#if>
	                <#if contactDestination?has_content && (contactDestination.address2)?has_content> ${contactDestination.address2?if_exists}</#if>
	                <#if contactDestination?has_content && (contactDestination.city)?has_content>${contactDestination.city?if_exists}</#if>
	                <#if contactDestination?has_content>
	                  <#assign stateDescription = delegator.findOne("Geo", {"geoId" : contactDestination.stateProvinceGeoId?if_exists}, true)>
	                  <#if (stateDescription)?has_content>${stateDescription.geoName?if_exists?lower_case}</#if>
	                </#if>
	                <#if contactDestination?has_content && (contactDestination.postalCode)?has_content>${contactDestination.postalCode?if_exists}</#if>
	                <#if contactDestination?has_content>
	                  <#assign countryDescription = delegator.findOne("Geo", {"geoId" : contactDestination.countryGeoId}, true)>
	                  <#if (countryDescription)?has_content>${countryDescription.geoName?if_exists}</#if>
	                </#if>
	              </fo:block>
                </fo:table-cell></fo:table-row>
              </fo:table-body>
            </fo:table>
            </fo:table-cell>
          </fo:table-row>
          </fo:table-body>
          </fo:table>
          <fo:block space-after="10pt"/>
</#escape>
