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
   <fo:table-column column-width="5.4in"/>
   <fo:table-column column-width="2in"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
        <#if orderHeader?has_content>
         <#assign orderDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(orderHeader.orderDate)>
         </#if>
        <fo:block font-size="7pt">
               <fo:block> PO No :<#if orderHeader?has_content>${orderHeader.orderId?if_exists} Date:${orderDate?if_exists}</#if></fo:block>
                <fo:block>To  :	 <#if supplierPartyGroup?has_content> ${supplierPartyGroup.groupName?if_exists}</#if></fo:block>
                <fo:block>      <#if supplierAddressGeneral?has_content> ${supplierAddressGeneral.address1?if_exists}</#if></fo:block>
          <#--      <fo:block>      <#if supplierAddressGeneral?has_content> ${supplierAddressGeneral.address2?if_exists}</#if></fo:block>-->
                 <fo:block>      <#if supplierAddressGeneral?has_content> ${supplierAddressGeneral.postalCode?if_exists}</#if></fo:block>
    </fo:block>
        </fo:table-cell>
     <fo:table-cell>
       <fo:block  font-size="7pt">
        <fo:block>1.Our Enquiry No.: <#if orderHeader?has_content> ${orderHeader.enquiryNumber?if_exists}</#if></fo:block>
        <fo:block>2.Your Offer No.:<#if orderHeader?has_content> ${orderHeader.offerNumber?if_exists}</#if></fo:block>
      <#--  <fo:block>/02/2012/R2/25.02.2012</fo:block> -->
        <fo:block>3. P R No.:<#if orderHeader?has_content> ${orderHeader.prNumber?if_exists}</#if></fo:block>
        <fo:block>File No.:<#if orderHeader?has_content> ${orderHeader.fileNo?if_exists}</#if></fo:block>
		</fo:block>
     </fo:table-cell>
     </fo:table-row>
     <fo:table-row  height="20px" space-start=".15in">
      <fo:table-cell border-style="solid" border-width="thin" border-color="black" number-columns-spanned="2">
      <fo:block  font-size="7pt">
         <#--  <fo:block>Dear Sir,</fo:block> -->
      <fo:block text-align="center">Please supply us the following materials as per the conditions overleaf</fo:block>
      </fo:block>
      </fo:table-cell>
     </fo:table-row>
  </fo:table-body>
</fo:table>
</#escape>
