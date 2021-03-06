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

  <fo:table table-layout="fixed" width="100%" border-style="solid" border-width="thin" border-color="black">
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
           <#-- <fo:table-cell>
                <fo:block margin-top="10px">Receipt No:</fo:block>
            </fo:table-cell>-->
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
  </fo:table>

  <fo:table table-layout="fixed" width="100%" space-before="0.1in" border-style="solid" border-width="thin" border-color="black">
    <fo:table-column column-width="15.5mm" />
    <fo:table-column column-width="65mm" />
    <fo:table-column column-width="15mm" />
    <fo:table-column column-width="17mm" />
    <fo:table-column column-width="24mm" />
    <fo:table-column column-width="24mm" />
    <fo:table-column column-width="30mm" />

    <fo:table-header font-size="8pt" font-weight="bold" height="8px">
      <fo:table-row>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center" margin-top="3px">Code</fo:block>
        </fo:table-cell>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center" margin-top="3px">Description</fo:block>
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

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block  text-align="center" margin-top="3px">Disc%</fo:block>
        </fo:table-cell>

        <fo:table-cell border-style="solid" border-width="thin" border-color="black">
          <fo:block text-align="center" margin-top="3px">Nett Price</fo:block>
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
              <fo:block text-align="center" margin-top="5px">${description?if_exists}</fo:block>
            </fo:table-cell>
            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block text-align="right" margin-top="5px" margin-right="7px"> <#if invoiceItem.quantity?exists>${invoiceItem.quantity?string.number}</#if> </fo:block>
            </fo:table-cell>
            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block margin-top="5px"> <#if uomGv?has_content>${uomGv.description}</#if> </fo:block>
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block margin-right="3px" margin-top="5px"> <#if invoiceItem.quantity?exists><@ofbizCurrency amount=invoiceItem.amount?if_exists isoCode=invoice.currencyUomId?if_exists/></#if> </fo:block>
            </fo:table-cell>
            <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
              <fo:block margin-right="3px" margin-top="5px"></fo:block>
            </fo:table-cell>
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
          <#assign invoiceItemDiscountAmount=0.00/>
          <#if invoiceItemAdjustmentsGv?has_content>
            <#list invoiceItemAdjustmentsGv as invoiceItemAdjustment>
              <#assign invoiceItemDiscountAmount=invoiceItemDiscountAmount+invoiceItemAdjustment.amount?default(0)/>
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
              <fo:table-cell text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black">
                <fo:block margin-right="3px" margin-top="5px"></fo:block>
              </fo:table-cell>
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
          <#list 7..1 as i>
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
        <fo:table-cell border-bottom-style="solid" border-width="thin">
          <fo:block text-align="left" margin-top="3px" margin-left="3px">VAT Based on Minimum Taxable Value &amp; Therefore Computed on The RRP</fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="solid" border-width="thin">
            <fo:table table-layout="fixed" font-size="10pt" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(43.15)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(56.85)"/>
              <fo:table-body>
                <fo:table-row height="30px">
                  <fo:table-cell>
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Sub Total</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-left-style="solid" border-width="thin">
                    <fo:block text-align="right" margin-top="10px" margin-right="3px">
                        <#assign amount=Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceNoTaxTotal(invoice)/>
                        <@ofbizCurrency amount=(amount+discountAmount)?default(0) isoCode=invoice.currencyUomId?if_exists/>
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
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Discount @ &#160; &#160; 0.00%</fo:block>
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
          <fo:block text-align="left" margin-left="3px" margin-top="10px">Checked By: _____________________________</fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="solid" border-width="thin">
            <fo:table table-layout="fixed" font-size="10pt" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(43.15)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(56.85)"/>
              <fo:table-body>
                <fo:table-row height="25px">
                  <fo:table-cell>
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Amount Excl Tax</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-left-style="solid" border-width="thin">
                    <fo:block text-align="right" margin-top="10px" margin-right="3px">
                        <@ofbizCurrency amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceNoTaxTotal(invoice)) isoCode=invoice.currencyUomId?if_exists/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row space-start="2.15in" height="25px">
        <fo:table-cell>
          <fo:block text-align="left" margin-left="3px">Received in good order</fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="solid" border-width="thin">
            <fo:table table-layout="fixed" font-size="10pt" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(43.15)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(56.85)"/>
              <fo:table-body>
                <fo:table-row space-start="2.15in" border-width="thin" height="25px">
                  <fo:table-cell>
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Vat :</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-left-style="solid" border-width="thin">
                    <fo:block text-align="right" margin-top="10px" margin-right="3px">
                      <#-- the vat total -->
                      <#assign amount=Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTaxTotal(invoice)/>
                      <@ofbizCurrency amount=(amount)?default(0) isoCode=invoice.currencyUomId?if_exists/>
                      <#-- <#assign vatAmt = Static["java.math.BigDecimal"].ZERO>
                      <#if invoiceItemsVat?has_content>
                        <#list invoiceItemsVat as orderAdjInvVat16Grouped>
                          <#assign vatAmt = vatAmt + orderAdjInvVat16Grouped.amount?default(0)>
                        </#list>
                      </#if>
                      <@ofbizCurrency amount=vatAmt?if_exists isoCode=invoice.currencyUomId?if_exists /> -->
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row space-start="2.15in" height="25px">
        <fo:table-cell>
          <fo:block text-align="left" margin-left="3px">Signed _______________________ &#160; &#160; &#160; Date _______________</fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="solid" border-width="thin">
            <fo:table table-layout="fixed" font-size="10pt" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(43.15)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(56.85)"/>
              <fo:table-body>
                <fo:table-row space-start="2.15in" border-width="thin" height="25px">
                  <fo:table-cell>
                    <fo:block text-align="left" margin-top="10px" margin-left="3px">Total</fo:block>
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
  <fo:table table-layout="fixed" font-size="10pt" width="100%">
    <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
    <fo:table-column column-number="2" column-width="proportional-column-width(50)"/>
    <fo:table-body>
      <fo:table-row space-start="2.15in" border-width="thin" height="25px">
        <fo:table-cell>
          <fo:block text-align="left" margin-top="5px" font-weight="bold">High Strength, Super Quality 42.5N</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block text-align="left" margin-top="5px" font-weight="bold">Ultimate Strength, Super Quality 32.5N</fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
</#escape>
