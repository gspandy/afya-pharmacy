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

  <fo:table table-layout="fixed" width="100%" space-before="0.2in" border-style="solid" border-width="thin" border-color="black">
    <fo:table-column column-width="15mm" border-style="solid" border-width="thin" border-color="black"/>
    <fo:table-column column-width="80.5mm" />
    <fo:table-column column-width="23mm" />
    <fo:table-column column-width="27mm" />
    <fo:table-column column-width="15mm" />
    <fo:table-column column-width="30mm" />

    <fo:table-header height="8px">
      <fo:table-row>
        
        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center">SL No.</fo:block>
        </fo:table-cell>
        
        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center">Description of Goods</fo:block>
        </fo:table-cell>
        
        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center">Quantity</fo:block>
        </fo:table-cell>
        
        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block  text-align="center">Rate</fo:block>
        </fo:table-cell>
        
        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block  text-align="center">per</fo:block>
        </fo:table-cell>
        
        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center">Amount</fo:block>
        </fo:table-cell>
      
      </fo:table-row>
    </fo:table-header>


    <fo:table-body font-size="8pt" height="18px">
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
                
                <fo:table-row height="13px" space-start=".15in">
                
                    <fo:table-cell>
                        <fo:block text-align="left">${count?if_exists} </fo:block>
                    </fo:table-cell>
                    
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block text-align="left" font-weight="bold">${description?if_exists}</fo:block>
                    </fo:table-cell>
                    
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block text-align="right" font-weight="bold"> <#if invoiceItem.quantity?exists>${invoiceItem.quantity?string.number}</#if> </fo:block>
                    </fo:table-cell>
                    
                    <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block> <#if invoiceItem.quantity?exists><@ofbizCurrency amount=invoiceItem.amount?if_exists isoCode=invoice.currencyUomId?if_exists/></#if> </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block> <#if uomGv?has_content>${uomGv.description}</#if> </fo:block>
                    </fo:table-cell>
                    
                    <#assign amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem))>
                    <#assign totalAmountWithOutTax = totalAmountWithOutTax + amount >
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold"> <@ofbizCurrency amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem)) isoCode=invoice.currencyUomId?if_exists/> </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </#if>
        </#list>
        <fo:table-row height="18px" space-start=".15in">
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            
             <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" border-top-style="solid" 
                     border-top-width="thin" border-top-color="black">
                <fo:block >
                <@ofbizCurrency amount=totalAmountWithOutTax?if_exists isoCode=invoice.currencyUomId?if_exists />
                </fo:block>
            </fo:table-cell>
       </fo:table-row>
        
        <#list invoiceItemsTax as orderAdjInvGrouped>
            <fo:table-row height="12px" space-start=".15in">
                <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                    <fo:block text-align="left"></fo:block>
                </fo:table-cell>
                <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                    <fo:block text-align="right" font-weight="bold">${orderAdjInvGrouped.description?if_exists}</fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                    <fo:block>  </fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                    <fo:block> </fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                    <fo:block></fo:block>
                </fo:table-cell>
                <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                    <fo:block font-weight="bold"> <@ofbizCurrency amount=orderAdjInvGrouped.amount?if_exists isoCode=invoice.currencyUomId?if_exists /> </fo:block>
                </fo:table-cell>
            </fo:table-row>
        </#list>  
        <#-- blank line -->
        
        <fo:table-row height="100px" space-start=".15in">
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
             <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block />
            </fo:table-cell>
        </fo:table-row>
        
        <#-- the grand total -->
        <fo:table-row border-top-style="solid" border-top-width="thin" border-top-color="black">
            <fo:table-cell number-columns-spanned="3">
                <fo:block/>
            </fo:table-cell>
            <fo:table-cell number-columns-spanned="2" text-align="center">
                <fo:block font-weight="bold" >${uiLabelMap.AccountingTotalCapital?if_exists}</fo:block>
            </fo:table-cell>
            <fo:table-cell text-align="right" font-weight="bold">
                <fo:block><@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists /></fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row height="7px">
           <fo:table-cell number-columns-spanned="6">
              <fo:block/>
           </fo:table-cell>
        </fo:table-row>

        <fo:table-row height="210px" border-top-style="solid" border-top-width="thin" border-top-color="black">
            <fo:table-cell number-columns-spanned="6">
                <fo:block text-align="right"  font-weight="bold">E. &amp; O.E</fo:block>
                <fo:block >Amount Chargeable (in words)</fo:block>
                <#assign currencyInWord = Static["org.ofbiz.accounting.util.Converter"].currencyInWords(invoiceTotal,invoice.currencyUomId)>
                <fo:block  font-weight="bold">${currencyInWord?if_exists}</fo:block>
                <fo:block margin-top="120px">
                    <fo:table table-layout="fixed" width="100%" space-before="0.2in">
                        <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
                        <fo:table-column column-number="2" column-width="proportional-column-width(50)"/>
                        <fo:table-body>
                            <fo:table-row space-start="2.15in" >
                                <fo:table-cell>
                                    <fo:block text-align="left">Company's VAT TIN : ${company.vatTinNumber?if_exists}</fo:block>
                                    <fo:block text-align="left">Company's CST No: ${company.cstNumber?if_exists}</fo:block>
                                </fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="left"></fo:block>
                    </fo:table-cell>
                 </fo:table-row>
                 <fo:table-row>
                                <fo:table-cell>
                                    <fo:block text-align="left" margin-top="6px">
                                        Declaration
                                    </fo:block>
                                    <fo:block text-align="left" margin-top="6px">
                                        <!-- <#if invoice.invoiceMessage?has_content><fo:block>${invoice.invoiceMessage}</fo:block></#if> -->
                                        <fo:block>We declare that this invoice shows the actual price of the goods described and that all particulars are true and correct.</fo:block>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-style="solid" border-width="thin" border-color="black" height="60px" >
                                    <fo:block text-align="right" font-weight="bold">
                                        For 
                                        <#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billingParty.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
                                        ${billingPartyNameResult.fullName?default("Billing Name Not Found")}
                                    </fo:block>
                                    <fo:block text-align="right" margin-top="40px">
                                        Authorized Signatory
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:block>
            
            </fo:table-cell>
        </fo:table-row>
    
    </fo:table-body>
  </fo:table>
</#escape>
