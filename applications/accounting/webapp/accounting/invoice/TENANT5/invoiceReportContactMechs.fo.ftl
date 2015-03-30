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
   <fo:table-column column-width="1.9in"/>
   <fo:table-column column-width="1.85in"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
        <fo:block font-size="7pt">
               <fo:block>Consignee:</fo:block>
       <#if billingAddress?has_content>
        <#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billingParty.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
        <fo:block font-weight="bold">${billingPartyNameResult.fullName?default(billingAddress.toName)?default("Billing Name Not Found")}</fo:block>
        <#if billingAddress.attnName?exists>
            <fo:block>${billingAddress.attnName}</fo:block>
        </#if>
            <fo:block>${billingAddress.address1?if_exists}</fo:block>
        <#if billingAddress.address2?exists>
            <fo:block>${billingAddress.address2}</fo:block>
        </#if>
        <fo:block>${billingAddress.city?if_exists} ${billingAddress.stateProvinceGeoId?if_exists} ${billingAddress.postalCode?if_exists}</fo:block>
    <#else>
        <fo:block>${uiLabelMap.AccountingNoGenBilAddressFound}${billingParty.partyId}</fo:block>
    </#if>
    </fo:block>
        </fo:table-cell>
     <fo:table-cell text-align="center">
       <fo:block  font-size="7pt">Pre-Authenticated</fo:block>
     </fo:table-cell>
     </fo:table-row>
      <fo:table-row>
      <fo:table-cell border-top-style="solid" border-top-width="thin" border-top-color="black">
        <fo:block font-size="7pt">
               <fo:block>Buyer(if other than consignee):</fo:block>
                  </fo:block>
       </fo:table-cell>
       <fo:table-cell border-top-style="solid" border-top-width="thin" border-top-color="black" border-left-style="solid" border-left-width="thin" border-left-color="black">
        <fo:block font-size="7pt">
               <fo:block>CST No:0704 C 007792</fo:block><br/>
                <fo:block>TIN:32070477925</fo:block><br/>
                 <fo:block>C Excise Reg.No:AAACK9971 B X M 002</fo:block><br/>
                  <fo:block>P.L.A No.:220/T 168/75</fo:block><br/>
                   <fo:block>Range:Kolenchery,682 311</fo:block><br/>
                     <fo:block>Division:Muvattupuzha,686 661</fo:block><br/>
                       <fo:block>CH...Hdg.and sub.Hdg.No.8504.3300/7308.9010</fo:block><br/>
                         <fo:block>Service Tax Reg.No.C.EX/KLCY/30/CA1/2003-2004</fo:block>
               
                  </fo:block>
       </fo:table-cell>
      </fo:table-row>
  </fo:table-body>
</fo:table>
</#escape>
