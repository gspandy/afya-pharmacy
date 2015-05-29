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
  <#-- list of orders -->

  <#-- list of terms -->

  <#-- <fo:table table-layout="fixed" width="100%" border-style="solid" border-width="thin" border-color="black">
    <fo:table-column />
    <fo:table-column />
    <fo:table-column />
    <fo:table-column />
    <fo:table-body font-size="8pt">
        <fo:table-row space-start=".15in" font-weight="bold">
            <fo:table-cell>
                <fo:block margin-top="10px" margin-left="5px">Account</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="10px">Loading Slip No:</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="10px">Tax Exempt</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block></fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row space-start=".15in">
            <fo:table-cell>
                <fo:block margin-top="10px" margin-bottom="10px" margin-left="5px">${billingParty.partyId}</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="10px" margin-bottom="10px">${shipmentId?if_exists}</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="10px" margin-bottom="10px">
                    <#if orderHeaderGV?has_content>
                        <#assign orderAdjustmentGv = delegator.findByAnd("OrderAdjustment", {"orderId":orderHeaderGV.orderId,"orderAdjustmentTypeId":"SALES_TAX"})/>
                        <#if orderAdjustmentGv?has_content>
                            N
                        <#else>
                            Y
                        </#if>
                    </#if>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="10px" margin-bottom="10px" margin-right="20px" text-align="right">
                    <#if orderHeaderGV?has_content>
                        <#assign orderAdjustmentGv = delegator.findOne("ProductStore", {"productStoreId":orderHeaderGV.productStoreId}, true)/>
                        <#if orderAdjustmentGv?has_content && orderAdjustmentGv.pricesIncludeTax == "Y">
                            Inclusive
                        <#else>
                            Exclusive
                        </#if>
                    <#else>
                        Exclusive
                    </#if>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
  </fo:table> -->

  <fo:table table-layout="fixed" width="100%" border-style="solid" border-width="thin" border-color="black">
    <fo:table-column column-number="1" column-width="proportional-column-width(17)" />
    <fo:table-column column-number="2" column-width="proportional-column-width(3)" />
    <fo:table-column column-number="3" column-width="proportional-column-width(30)" />
    <fo:table-column column-number="4" column-width="proportional-column-width(17)" />
    <fo:table-column column-number="5" column-width="proportional-column-width(3)" />
    <fo:table-column column-number="6" column-width="proportional-column-width(30)" />
    <fo:table-body font-size="8pt">
      <fo:table-row space-start=".15in" height="60px">
        <#if orderId?has_content>
          <#assign orderRxHeader = delegator.findOne("OrderRxHeader", {"orderId" : orderId}, true)>
          <#if orderRxHeader?has_content>
            <fo:table-cell>
              <#-- <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="5px">Patient Details:</fo:block> -->
              <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="5px">Afya ID</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Civil ID</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Patient Name</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Gender</fo:block>
              <#if orderRxHeader.patientType?exists && "INSURANCE"==orderRxHeader.patientType>
                <fo:block font-weight="bold" margin-left="5px">Patient Type</fo:block>
              </#if>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block font-weight="bold" margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <#if orderRxHeader.patientType?exists && "INSURANCE"==orderRxHeader.patientType>
                <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              </#if>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><#if orderRxHeader.afyaId?exists>${orderRxHeader.afyaId?if_exists}<#else>&#160;&#32;</#if></fo:block>
              <fo:block margin-left="3px">
                <#if orderRxHeader.afyaId?exists>
                  <#assign patientList = delegator.findByAnd("Patient", {"afyaId":orderRxHeader.afyaId})/>
                  <#if patientList?has_content>
                    <#assign patient = patientList.get(0)?if_exists>
                    <#if patient.civilId?exists>${patient.civilId?if_exists}<#else>&#160;&#32;</#if>
                  <#else>
                    <#if orderRxHeader.civilId?exists>${orderRxHeader.civilId?if_exists}<#else>&#160;&#32;</#if>
                  </#if>
                <#else>
                  <#if orderRxHeader.civilId?exists>${orderRxHeader.civilId?if_exists}<#else>&#160;&#32;</#if>
                </#if>
              </fo:block>
              <fo:block margin-left="3px">${orderRxHeader.firstName?if_exists} ${orderRxHeader.thirdName?if_exists}</fo:block>
              <fo:block margin-left="3px">
                <#if orderRxHeader.afyaId?exists>
                  <#assign patientList = delegator.findByAnd("Patient", {"afyaId":orderRxHeader.afyaId})/>
                  <#if patientList?has_content>
                    <#assign patient = patientList.get(0)?if_exists>
                    <#if patient.gender?exists && ("M" == patient.gender || "Male" == patient.gender)>Male<#elseif patient.gender?exists && ("F" == patient.gender || "Female" == patient.gender)>Female<#else>&#160;&#32;</#if>
                  <#else>
                    <#if orderRxHeader.gender?exists && ("M" == orderRxHeader.gender || "Male" == orderRxHeader.gender)>Male<#elseif orderRxHeader.gender?exists && ("F" == orderRxHeader.gender || "Female" == orderRxHeader.gender)>Female<#else>&#160;&#32;</#if>
                  </#if>
                <#else>
                  <#if orderRxHeader.gender?exists && ("M" == orderRxHeader.gender || "Male" == orderRxHeader.gender)>Male<#elseif orderRxHeader.gender?exists && ("F" == orderRxHeader.gender || "Female" == orderRxHeader.gender)>Female<#else>&#160;&#32;</#if>
                </#if>
              </fo:block>
              <#if orderRxHeader.patientType?exists && "INSURANCE"==orderRxHeader.patientType>
                <fo:block margin-left="3px"><#if orderRxHeader.patientType?exists>${orderRxHeader.patientType?if_exists}<#else>&#160;&#32;</#if></fo:block>
              </#if>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block> -->
              <fo:block font-weight="bold" margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="5px">Doctor</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Invoice Date</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Invoice No</fo:block>
              <#if orderRxHeader.patientType?exists && "INSURANCE"!=orderRxHeader.patientType>
                <fo:block font-weight="bold" margin-left="5px">Patient Type</fo:block>
              </#if>
              <#if orderRxHeader.patientType?exists && "INSURANCE"==orderRxHeader.patientType>
                <#assign patientInsuranceList = delegator.findByAnd("PatientInsurance", {"benefitPlanId" : orderRxHeader.benefitPlanId?if_exists})>
                <#if patientInsuranceList?has_content>
                  <#assign patientInsurance = patientInsuranceList.get(0)?if_exists>
                  <fo:block font-weight="bold" margin-left="5px">Patient Insurance</fo:block>
                </#if>
              </#if>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block font-weight="bold" margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <#if orderRxHeader.patientType?exists && "INSURANCE"!=orderRxHeader.patientType>
                <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              </#if>
              <#if orderRxHeader.patientType?exists && "INSURANCE"==orderRxHeader.patientType>
                <#assign patientInsuranceList = delegator.findByAnd("PatientInsurance", {"benefitPlanId" : orderRxHeader.benefitPlanId?if_exists})>
                <#if patientInsuranceList?has_content>
                  <#assign patientInsurance = patientInsuranceList.get(0)?if_exists>
                  <fo:block font-weight="bold" margin-left="3px">:</fo:block>
                </#if>
              </#if>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><#if orderRxHeader.doctorName?exists>${orderRxHeader.doctorName?if_exists}<#else>&#160;&#32;</#if></fo:block>
              <fo:block margin-left="3px"><#if invoiceDate?has_content>${invoiceDate?if_exists?string("dd/MM/yyyy")}<#else>&#160;&#32;</#if></fo:block>
              <fo:block margin-left="3px"><#if invoice?has_content>${invoice.invoiceId}<#else>&#160;&#32;</#if></fo:block>
              <#if orderRxHeader.patientType?exists && "INSURANCE"!=orderRxHeader.patientType>
                <fo:block margin-left="3px"><#if orderRxHeader.patientType?exists>${orderRxHeader.patientType?if_exists}<#else>&#160;&#32;</#if></fo:block>
              </#if>
              <#if orderRxHeader.patientType?exists && "INSURANCE"==orderRxHeader.patientType>
                <#assign patientInsuranceList = delegator.findByAnd("PatientInsurance", {"benefitPlanId" : orderRxHeader.benefitPlanId?if_exists})>
                <#if patientInsuranceList?has_content>
                  <#assign patientInsurance = patientInsuranceList.get(0)?if_exists>
                  <fo:block margin-left="3px">${patientInsurance.healthPolicyName?if_exists} - ${patientInsurance.policyNo?if_exists}</fo:block>
                </#if>
              </#if>
            </fo:table-cell>
            
          <#else>
            <fo:table-cell>
              <#-- <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="5px">Patient Details:</fo:block> -->
              <fo:block font-weight="bold" margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="5px">Afya ID</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Civil ID</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Patient Name</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Gender</fo:block>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block font-weight="bold" margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px">&#160;&#32;</fo:block>
              <fo:block margin-left="3px">&#160;&#32;</fo:block>
              <fo:block margin-left="3px">&#160;&#32;</fo:block>
              <fo:block margin-left="3px">&#160;&#32;</fo:block>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block> -->
              <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="5px">Doctor</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Invoice Date</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Invoice No</fo:block>
              <fo:block font-weight="bold" margin-left="5px">Patient Type</fo:block>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block font-weight="bold" margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
              <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            </fo:table-cell>
            
            <fo:table-cell>
              <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
              <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
              <fo:block margin-left="3px">&#160;&#32;</fo:block>
              <fo:block margin-left="3px"><#if invoiceDate?has_content>${invoiceDate?if_exists?string("dd/MM/yyyy")}<#else>&#160;&#32;</#if></fo:block>
              <fo:block margin-left="3px"><#if invoice?has_content>${invoice.invoiceId}<#else>&#160;&#32;</#if></fo:block>
              <fo:block margin-left="3px">&#160;&#32;</fo:block>
            </fo:table-cell>
            
          </#if>
        <#else>
          <fo:table-cell>
            <#-- <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
            <fo:block font-weight="bold" margin-left="5px">Patient Details:</fo:block> -->
            <fo:block font-weight="bold" margin-left="5px"><fo:leader></fo:leader></fo:block>
            <fo:block font-weight="bold" margin-left="5px">Afya ID</fo:block>
            <fo:block font-weight="bold" margin-left="5px">Civil ID</fo:block>
            <fo:block font-weight="bold" margin-left="5px">Patient Name</fo:block>
            <fo:block font-weight="bold" margin-left="5px">Gender</fo:block>
          </fo:table-cell>
          
          <fo:table-cell>
            <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
            <fo:block font-weight="bold" margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
          </fo:table-cell>
          
          <fo:table-cell>
            <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
            <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block margin-left="3px">&#160;&#32;</fo:block>
            <fo:block margin-left="3px">&#160;&#32;</fo:block>
            <fo:block margin-left="3px">&#160;&#32;</fo:block>
            <fo:block margin-left="3px">&#160;&#32;</fo:block>
          </fo:table-cell>
          
          <fo:table-cell>
            <#-- <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block>
            <fo:block margin-left="5px"><fo:leader></fo:leader></fo:block> -->
            <fo:block font-weight="bold" margin-left="5px"><fo:leader></fo:leader></fo:block>
            <fo:block font-weight="bold" margin-left="5px">Doctor</fo:block>
            <fo:block font-weight="bold" margin-left="5px">Invoice Date</fo:block>
            <fo:block font-weight="bold" margin-left="5px">Invoice No</fo:block>
            <fo:block font-weight="bold" margin-left="5px">Patient Type</fo:block>
          </fo:table-cell>
          
          <fo:table-cell>
            <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
            <fo:block font-weight="bold" margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
            <fo:block font-weight="bold" margin-left="3px">:</fo:block>
          </fo:table-cell>
          
          <fo:table-cell>
            <#-- <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block> -->
            <fo:block margin-left="3px"><fo:leader></fo:leader></fo:block>
            <fo:block margin-left="3px">&#160;&#32;</fo:block>
            <fo:block margin-left="3px"><#if invoiceDate?has_content>${invoiceDate?if_exists?string("dd/MM/yyyy")}<#else>&#160;&#32;</#if></fo:block>
            <fo:block margin-left="3px"><#if invoice?has_content>${invoice.invoiceId}<#else>&#160;&#32;</#if></fo:block>
            <fo:block margin-left="3px">&#160;&#32;</fo:block>
          </fo:table-cell>
          
        </#if>
      </fo:table-row>
    </fo:table-body>
  </fo:table>

  <fo:table table-layout="fixed" width="100%" space-before="0.1in" border-style="solid" border-width="thin" border-color="black">
    <fo:table-column column-width="15.5mm" />
    <fo:table-column column-width="80mm" />
    <fo:table-column column-width="18mm" />
    <fo:table-column column-width="23mm" />
    <fo:table-column column-width="24mm" />
    <#-- <fo:table-column column-width="24mm" /> -->
    <fo:table-column column-width="30mm" />

    <fo:table-header font-size="8pt" font-weight="bold" height="8px">
      <fo:table-row>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center" margin-top="3px">Code</fo:block>
        </fo:table-cell>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="left" margin-left="10px" margin-top="3px">Description</fo:block>
        </fo:table-cell>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center" margin-top="3px">Quantity</fo:block>
        </fo:table-cell>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block  text-align="center" margin-top="3px">Unit</fo:block>
        </fo:table-cell>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block  text-align="center" margin-top="3px">Unit Price</fo:block>
        </fo:table-cell>

        <#-- <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block  text-align="center" margin-top="3px">Disc%</fo:block>
        </fo:table-cell> -->

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center" margin-top="3px">Gross Price</fo:block>
        </fo:table-cell>

      </fo:table-row>
    </fo:table-header>

    <fo:table-body font-size="8pt" height="20px">
      <#assign totalAmountWithOutTax = 0>
      <#assign count = 0>
      <#assign currentShipmentId = "">
      <#assign newShipmentId = "">
      <#-- if the item has a description, then use its description.  Otherwise, use the description of the invoiceItemType -->
      <#list invoiceItems as invoiceItem>

        <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
        <#assign product = invoiceItem.getRelatedOne("Product")>
        <#assign quantityUom = product.getString("quantityUomId")>
        <#assign uomGv = (delegator.findOne("Uom", {"uomId", quantityUom?if_exists}, false))?if_exists />
        <#assign isItemAdjustment = Static["org.ofbiz.common.CommonWorkers"].hasParentType(delegator, "InvoiceItemType", "invoiceItemTypeId", itemType.getString("invoiceItemTypeId"), "parentTypeId", "INVOICE_ADJ")/>

        <#assign taxRate = invoiceItem.getRelatedOne("TaxAuthorityRateProduct")?if_exists>
        <#assign invoiceItemBilling = delegator.findOne("InvoiceItem", {"invoiceId" : invoiceItem.invoiceId ,"invoiceItemSeqId" :invoiceItem.invoiceItemSeqId }, true)>
        <#assign itemBillings = invoiceItemBilling.getRelated("OrderItemBilling")?if_exists>
        <#if itemBillings?has_content>
          <#assign itemBilling = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(itemBillings)>
          <#if itemBilling?has_content>
            <#assign itemIssuance = itemBilling.getRelatedOne("ItemIssuance")?if_exists>
            <#if itemIssuance?has_content>
              <#assign newShipmentId = itemIssuance.shipmentId>
              <#assign issuedDateTime = itemIssuance.issuedDateTime/>
            </#if>
          </#if>
        </#if>
        <#if invoiceItem.description?has_content>
          <#assign description=invoiceItem.description>
        <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
          <#assign description=taxRate.get("description",locale)>
        <#elseif itemType.get("description",locale)?has_content>
          <#assign description=itemType.get("description",locale)>
        </#if>
        <#if !isItemAdjustment>
          <#assign count = count+1>
          <fo:table-row space-start=".15in">
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block text-align="center" margin-top="5px">${product.productId?if_exists} </fo:block>
            </fo:table-cell>
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block text-align="left" margin-left="10px" margin-top="5px">${description?if_exists}</fo:block>
            </fo:table-cell>
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block text-align="center" margin-top="5px" margin-right="7px"> <#if invoiceItem.quantity?exists>${invoiceItem.quantity?string.number}</#if> </fo:block>
            </fo:table-cell>
            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block margin-top="5px"> <#if uomGv?has_content>${uomGv.description}</#if> </fo:block>
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block margin-right="3px" margin-top="5px"> <#if invoiceItem.quantity?exists><@ofbizCurrency amount=invoiceItem.amount?if_exists isoCode=invoice.currencyUomId?if_exists/></#if> </fo:block>
            </fo:table-cell>
            <#-- <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block margin-right="3px" margin-top="5px"></fo:block>
            </fo:table-cell> -->
            <fo:table-cell text-align="right" margin-right="3px">
              <#assign amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem))>
              <#assign totalAmountWithOutTax = totalAmountWithOutTax+amount >
              <fo:block margin-top="5px">
                <#assign discountAmount=Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getDiscountAmount(invoice)/>
                <@ofbizCurrency amount=amount isoCode=invoice.currencyUomId?if_exists/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
          <#assign invoiceItemAdjustmentsGv = delegator.findByAnd("InvoiceItem", {"invoiceId":invoiceItem.invoiceId,"productId":invoiceItem.productId,"invoiceItemTypeId":"INVOICE_ITM_ADJ"})/>
          <#assign invoiceItemDiscountAmount=0.000/>
          <#if invoiceItemAdjustmentsGv?has_content>
            <#list invoiceItemAdjustmentsGv as invoiceItemAdjustment>
              <#assign invoiceItemDiscountAmount=invoiceItemDiscountAmount+invoiceItemAdjustment.amount?default(0.000)/>
            </#list>
            <fo:table-row space-start=".15in" height="30px">
              <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block text-align="center" margin-top="5px"></fo:block>
              </fo:table-cell>
              <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block text-align="center" margin-top="5px">Discount Allowed</fo:block>
              </fo:table-cell>
              <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block text-align="right" margin-top="5px" margin-right="10px"></fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block margin-top="5px"></fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block margin-right="3px" margin-top="5px"></fo:block>
              </fo:table-cell>
              <#-- <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block margin-right="3px" margin-top="5px"></fo:block>
              </fo:table-cell> -->
              <fo:table-cell text-align="right" margin-right="3px">
                <fo:block margin-top="5px"><@ofbizCurrency amount=invoiceItemDiscountAmount isoCode=invoice.currencyUomId?if_exists/></fo:block>
              </fo:table-cell>
            </fo:table-row>
          </#if>
        </#if>
      </#list>
      <#assign height = 185 - (25 * (invoiceItems.size()?default(0)))/>
      <#if height gt 0>
        <fo:table-row height="${height}px">
          <#list 6..1 as i>
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black"></fo:table-cell>
          </#list>
        </fo:table-row>
      </#if>
    </fo:table-body>
  </fo:table>

  <fo:table table-layout="fixed" font-size="9pt" width="100%" border-left-style="solid" border-right-style="solid" border-top-style="none" border-bottom-style="solid" border-width="thin">
    <fo:table-column column-number="1" column-width="proportional-column-width(50.15)"/>
    <fo:table-column column-number="2" column-width="proportional-column-width(49.85)"/>
    <fo:table-body>
      <fo:table-row space-start="2.15in">
        <fo:table-cell>
          <fo:block text-align="left" margin-left="3px" margin-top="20px">Checked By: _____________________________</fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="solid" border-width="thin">
            <fo:table table-layout="fixed" font-size="10pt" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(43.15)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(56.85)"/>
              <fo:table-body>
                <fo:table-row height="30px">
                  <fo:table-cell>
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Gross Total</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-left-style="solid" border-width="thin">
                    <fo:block text-align="right" margin-top="10px" margin-right="3px">
                        <#assign amount=Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceNoTaxTotal(invoice)/>
                        <@ofbizCurrency amount=(amount+discountAmount)?default(0.000) isoCode=invoice.currencyUomId?if_exists/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row space-start="2.15in" height="25px">
        <fo:table-cell>
          <fo:block text-align="left" margin-left="3px" margin-top="10px"></fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="solid" border-width="thin">
            <fo:table table-layout="fixed" font-size="10pt" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(43.15)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(56.85)"/>
              <fo:table-body>
                <fo:table-row height="25px">
                  <fo:table-cell>
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Discount</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-left-style="solid" border-width="thin">
                    <fo:block text-align="right" margin-top="10px" margin-right="3px">
                        <@ofbizCurrency amount=discountAmount isoCode=invoice.currencyUomId?if_exists/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row space-start="2.15in" height="25px">
        <fo:table-cell>
          <fo:block text-align="left" margin-left="3px">Sign _______________________ &#160; &#160; &#160; Date _______________</fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="solid" border-width="thin">
            <fo:table table-layout="fixed" font-size="10pt" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(43.15)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(56.85)"/>
              <fo:table-body>
                <fo:table-row space-start="2.15in" border-width="thin" height="25px">
                  <fo:table-cell>
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Net Total</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-left-style="solid" border-width="thin">
                    <fo:block text-align="right" margin-top="10px" margin-right="3px">
                      <#-- the grand total -->
                      <@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists />
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
</#escape>
