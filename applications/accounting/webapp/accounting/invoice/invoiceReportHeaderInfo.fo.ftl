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
<fo:block font-size="8pt">
<fo:table table-layout="fixed" width="100%">
<fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
<fo:table-column column-number="2" column-width="proportional-column-width(50)"/>
<fo:table-body>
<!-- <fo:table-row>
  <fo:table-cell>
     <fo:block number-columns-spanned="2" font-weight="bold">${invoice.getRelatedOne("InvoiceType").get("description",locale)}</fo:block>
  </fo:table-cell>
</fo:table-row> -->

<fo:table-row border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">

  <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
  <fo:block >Invoice No</fo:block>
  <fo:block font-weight="bold"><#if invoice?has_content>${invoice.invoiceId}</#if></fo:block>
  </fo:table-cell>
  
  <fo:table-cell >
  <fo:block >Dated</fo:block>
  <#assign invoiceFormattedDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(invoiceDate)>
  <fo:block font-weight="bold">${invoiceFormattedDate?if_exists}</fo:block>
  </fo:table-cell>
  
</fo:table-row>
<fo:table-row border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">
  <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
  <fo:block >Delivery Note</fo:block>
  <fo:block font-weight="bold" >
  <#if invoice.deliverNote?has_content>
  ${invoice.deliverNote}
  <#else>
  <fo:block />
  </#if>
  </fo:block>
  </fo:table-cell>
  
  <fo:table-cell >
  <fo:block >Mode/Terms of Payment</fo:block>
  <fo:block font-weight="bold">
  <#if invoice.paymentDescription?has_content>${invoice.paymentDescription}
  <#else>
  <fo:block />
  </#if>
  </fo:block>
  </fo:table-cell>
  
</fo:table-row>

<fo:table-row border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">

  <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
  <fo:block >Supplier's Ref.</fo:block>
  <fo:block font-weight="bold"><#if invoice.supplierReference?has_content>${invoice.supplierReference}</#if></fo:block>
  </fo:table-cell>
  
  <fo:table-cell >
  <fo:block >Other Reference(s)</fo:block>
  <fo:block font-weight="bold"><#if invoice.otherReference?has_content>${invoice.otherReference}</#if></fo:block>
  </fo:table-cell>
  
</fo:table-row>

<fo:table-row border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">

  <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
  <fo:block >Buyer's Order No.</fo:block>
  <fo:block font-weight="bold"><#list orders as order>${order} </#list></fo:block>
  </fo:table-cell>
  
  <fo:table-cell >
  <fo:block >Dated</fo:block>
  <#assign invoiceFormattedDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(invoiceDate)>
  <fo:block font-weight="bold">${invoiceFormattedDate?if_exists}</fo:block>
  </fo:table-cell>
  
</fo:table-row>

<fo:table-row border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">

  <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
  <fo:block >Dispatched Document No.</fo:block>
  <fo:block font-weight="bold"><#if invoice.dispatchDocumentNo?has_content>${invoice.dispatchDocumentNo}</#if></fo:block>
  </fo:table-cell>
  <#assign dispatchDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(invoice.dispatchDocumentDate)>
  <fo:table-cell >
  <fo:block >Dated</fo:block>
  <fo:block font-weight="bold"><#if invoice.dispatchDocumentDate?has_content>${dispatchDate?if_exists}</#if></fo:block>
  </fo:table-cell>
  
</fo:table-row>

<fo:table-row border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">

  <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
  <fo:block >Dispatched Through</fo:block>
  <fo:block font-weight="bold"><#if invoice.dispatchThrough?has_content>${invoice.dispatchThrough}</#if></fo:block>
  </fo:table-cell>
  
  <fo:table-cell >
  <fo:block >Destination</fo:block>
  <fo:block font-weight="bold"><#if invoice.destination?has_content>${invoice.destination}</#if></fo:block>
  </fo:table-cell>
  
</fo:table-row>
</fo:table-body>
</fo:table>
<#if "SALES_INVOICE"==invoice.invoiceTypeId>
  <fo:table table-layout="fixed" width="100%" margin-left="3px">
    <fo:table-column />
    <fo:table-body>
      <fo:table-row >
        <fo:table-cell height="70px">
          <fo:block><fo:leader></fo:leader></fo:block>
          <fo:block font-size="10pt" font-weight="bold">BANK TRANSFER DETAILS</fo:block>
          <fo:block font-size="9pt">BENEFICIARY: ZAMBEZI PORTLAND LTD</fo:block>
          <fo:block font-size="9pt">BANK: FINANCE BANK (Z) LTD</fo:block>
          <fo:block font-size="9pt">A/C: (ZMW) 0025493780012</fo:block>
          <fo:block font-size="9pt" margin-left="15px">: (USD) 0025493780001</fo:block>
          <fo:block font-size="9pt">BRANCH: PRESIDENT AVENUE NDOLA</fo:block>
          <fo:block font-size="9pt">SWIFT CODE: ZFBAZMLU</fo:block>
          <fo:block font-size="9pt">BRANCH CODE: 110102</fo:block>
          <fo:block font-size="9pt">COUNTRY: ZAMBIA</fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
<#else>
  <fo:table table-layout="fixed" width="100%">
    <fo:table-column />
    <fo:table-body>
      <fo:table-row >
        <fo:table-cell height="70px">
          <fo:block >Terms of Delivery</fo:block>
          <fo:block font-weight="bold">
            <#if invoice.description?has_content>
              ${invoice.description}
            <#else>
              <fo:block />
            </#if>
          </fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
</#if>
</fo:block>
</#escape>
