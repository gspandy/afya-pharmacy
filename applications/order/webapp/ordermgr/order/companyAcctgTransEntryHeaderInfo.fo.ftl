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

  <fo:block font-size="8pt" font-family="Calibri">

    <fo:table table-layout="fixed" width="100%">

      <fo:table-column column-number="1" column-width="proportional-column-width(16)"/>
      <fo:table-column column-number="2" column-width="proportional-column-width(3.5)"/>
      <fo:table-column column-number="3" column-width="proportional-column-width(22)"/>
      <fo:table-column column-number="3" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="4" column-width="proportional-column-width(22)"/>
      <fo:table-column column-number="3" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="5" column-width="proportional-column-width(18)"/>

      <fo:table-body>

        <fo:table-row>

          <fo:table-cell>
            <fo:block number-columns-spanned="2">
              <#if logoImageUrl?has_content><fo:external-graphic src="./framework/images/webapp/images/${logoImageUrl}" overflow="hidden" height="65px" content-height="scale-to-fit"/></#if>
            </fo:block>
          </fo:table-cell>

          <fo:table-cell>
            <fo:block number-columns-spanned="2">
              <fo:external-graphic src="./framework/images/webapp/images/head_office.png" overflow="hidden" height="65px" content-height="scale-to-fit"/>
            </fo:block>
          </fo:table-cell>

          <fo:table-cell>
            <fo:block/>
            <fo:block font-weight="bold">${companyName}</fo:block>
            <#if postalAddress?exists>
              <#if postalAddress?has_content>
                <fo:block>${postalAddress.address1?if_exists}</fo:block>
                <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
                <fo:block>P.O. Box ${postalAddress.postalCode?if_exists} - ${postalAddress.city?if_exists}, ${stateProvinceAbbr?if_exists}, ${countryName?if_exists}</fo:block>
              </#if>
            </#if>
          </fo:table-cell>

          <#-- <#if phone?exists || fax?exists || emailwebsite?exists> -->
            <fo:table-cell>
              <fo:block number-columns-spanned="2">
                <fo:external-graphic src="./framework/images/webapp/images/vertical_line_bar.png" overflow="hidden" height="65px" content-height="scale-to-fit"/>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell>
              <fo:block/>
              <#-- <#if phone?exists> -->
                <fo:block>tel. <#if phone.countryCode?exists>+${phone.countryCode} </#if><#if phone.areaCode?exists>${phone.areaCode} </#if>${phone.contactNumber?if_exists}</fo:block>
              <#-- </#if>
              <#if fax?exists> -->
                <fo:block>fax <#if fax.countryCode?exists>+${fax.countryCode} </#if><#if fax.areaCode?exists>${fax.areaCode} </#if>${fax.contactNumber?if_exists}</fo:block>
              <#-- </#if>
              <#if email?exists> -->
                <fo:block>e-mail: <#if email?has_content> ${email.infoString?if_exists} </#if> </fo:block>
              <#-- </#if> -->
            </fo:table-cell>
          <#-- </#if> -->

          <#-- <#if company?exists> -->
            <fo:table-cell>
              <fo:block number-columns-spanned="2">
                <fo:external-graphic src="./framework/images/webapp/images/vertical_line_bar.png" overflow="hidden" height="65px" content-height="scale-to-fit"/>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell>
              <fo:block/>
              <#-- <#if company.registrationNumber?exists> -->
                <fo:block>Company Reg No: ${company.registrationNumber?if_exists}</fo:block>
              <#-- </#if>
              <#if company.vatTinNumber?exists> -->
                <fo:block>VAT Reg No: ${company.vatTinNumber?if_exists}</fo:block>
              <#-- </#if>
              <#if company.tPinNumber?exists> -->
                <fo:block>TPIN No: ${company.cstNumber?if_exists}</fo:block>
              <#-- </#if> -->
            </fo:table-cell>
          <#-- </#if> -->

        </fo:table-row>

      </fo:table-body>

    </fo:table>

    <fo:leader leader-length="100%" leader-pattern="rule" alignment-baseline="middle" rule-thickness="1pt" color="#959595"/>

  </fo:block>

</#escape>
