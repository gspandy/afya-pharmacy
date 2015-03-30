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
              <fo:block><fo:leader></fo:leader></fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </#if>
  </fo:block>
</#escape>
