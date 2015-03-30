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
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="11in" page-width="8.6in"
            margin-top="0.1in" margin-bottom="1in" margin-left="0.1in" margin-right="0.1in">
        <fo:region-body margin-top="0.5in"/>
        <fo:region-before extent="0.5in"/>
        <fo:region-after extent="0.5in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
 <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
        <fo:block font-size="15pt" text-align="center" font-weight="bold">Payment Report</fo:block>
       <#if paymentApplsLists?has_content>
            <fo:table>
                  <fo:table-column column-width="60pt"/>
                  <fo:table-column column-width="79pt"/>
                  <fo:table-column column-width="79pt"/>
                  <fo:table-column column-width="97pt"/>
                  <fo:table-column column-width="113pt"/>
                  <fo:table-column column-width="85pt"/>
                  <fo:table-column column-width="85pt"/>
                <fo:table-header> 
                    <fo:table-row font-weight="bold">
                    <fo:table-cell border-bottom="thin solid grey"><fo:block>Payment</fo:block></fo:table-cell>
                    <fo:table-cell border-bottom="thin solid grey"><fo:block>PartyId From</fo:block></fo:table-cell>
                    <fo:table-cell border-bottom="thin solid grey" text-align="center"><fo:block>PartyId To</fo:block></fo:table-cell>
                    <fo:table-cell border-bottom="thin solid grey" text-align="center"><fo:block>Status</fo:block></fo:table-cell>
                    <fo:table-cell border-bottom="thin solid grey" text-align="right"><fo:block>Payment Type</fo:block></fo:table-cell>
                     <fo:table-cell border-bottom="thin solid grey" text-align="center"><fo:block>Effective Date</fo:block></fo:table-cell>
                    <fo:table-cell border-bottom="thin solid grey" text-align="center"><fo:block>Amount</fo:block></fo:table-cell>
                   </fo:table-row>
               </fo:table-header>
               <fo:table-body>
       <#assign rowColor = "white">
         <#list paymentApplsLists as listIt>
                   <fo:table-row>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
							   <fo:block>${listIt.paymentId?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="3pt" background-color="${rowColor}">
                                <fo:block>${listIt.partyIdFrom?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="3pt" background-color="${rowColor}">
                                <fo:block>${listIt.partyIdTo?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="3pt" background-color="${rowColor}">
                         <fo:block>${listIt.getRelatedOne("StatusItem").get("description",locale)}</fo:block>
                        </fo:table-cell>
                       <fo:table-cell padding="3pt" background-color="${rowColor}">
                        <fo:block>${listIt.getRelatedOne("PaymentType").get("description",locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="3pt" background-color="${rowColor}">
                                <fo:block>${listIt.effectiveDate?string('dd-MM-yyyy')}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="3pt" text-align="right" background-color="${rowColor}">
                                <fo:block font-family="Rupee,Arial"><@ofbizCurrency amount = listIt.amount isoCode=listIt.currencyUomId?if_exists/></fo:block>
                        </fo:table-cell>
                     
                    </fo:table-row>
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
              </#list> 
                </fo:table-body>
      </fo:table>
   <#else>
<fo:block>No records found.</fo:block>
</#if>
     </fo:flow>
        </fo:page-sequence>
</fo:root>
</#escape>
