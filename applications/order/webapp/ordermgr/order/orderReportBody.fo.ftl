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

    <#-- <fo:table table-layout="fixed" width="100%" border-width="thin" border-color="black">
        <fo:table-column column-width="proportional-column-width(25.5)"/>
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-body font-size="8pt">
            <#assign rowColor = "white">
            <fo:table-row height="15pt">
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-width="thin" border-top-style="none" border-bottom-style="none" border-left-style="none" border-right-style="solid" border-color="black" text-align="center">
                    <#assign company = delegator.findOne("PartyGroup",{"partyId":"Company"},true)>
                    <fo:block margin-top="3px" font-size="10pt">VAT Reg. No. <fo:inline font-weight="bold">${company.vatTinNumber?if_exists}</fo:inline></fo:block>
                </fo:table-cell>
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-width="thin" border-top-style="solid" border-bottom-style="none" border-left-style="none" border-right-style="solid" border-color="black" text-align="center" font-weight="bold">
                    <fo:block margin-top="3px">Terms</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-width="thin" border-top-style="solid" border-bottom-style="none" border-left-style="none" border-right-style="solid" border-color="black" text-align="center" font-weight="bold">
                    <fo:block margin-top="3px">Tax Date</fo:block>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row height="15pt">
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-width="thin" border-top-style="none" border-bottom-style="none" border-left-style="none" border-right-style="solid" border-color="black">
                    <fo:block margin-top="3px"></fo:block>
                </fo:table-cell>
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-width="thin" border-top-style="solid" border-bottom-style="none" border-left-style="none" border-right-style="solid" border-color="black" text-align="center">
                    <fo:block margin-top="3px"></fo:block>
                </fo:table-cell>
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-width="thin" border-top-style="solid" border-bottom-style="none" border-left-style="none" border-right-style="solid" border-color="black" text-align="center">
                    <#assign orderDate = orderHeader.get("orderDate")>
                    <fo:block margin-top="3px">${orderDate?if_exists?string("MM/dd/yyyy")}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table> -->
    <#if orderHeader?has_content>
        <fo:table table-layout="fixed" width="100%" space-after="0.025in" border-style="solid" border-width="thin" border-color="black">
            <fo:table-column column-width="proportional-column-width(6)"/>
            <fo:table-column column-width="proportional-column-width(16)"/>
            <#-- <fo:table-column column-width="proportional-column-width(5)"/> -->
            <fo:table-column column-width="proportional-column-width(4)"/>
            <fo:table-column column-width="proportional-column-width(4)"/>
            <#-- <fo:table-column column-width="proportional-column-width(1.7)"/> -->
            <fo:table-column column-width="proportional-column-width(7)"/>
            <fo:table-column column-width="proportional-column-width(7)"/>

            <fo:table-header font-size="8pt" font-weight="bold">
                <fo:table-row height="15pt">

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Code</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="left" margin-left="10px" margin-top="3px">Description</fo:block>
                    </fo:table-cell>

                    <#-- <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Item Category</fo:block>
                    </fo:table-cell> -->

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Quantity</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Unit</fo:block>
                    </fo:table-cell>

                    <#-- <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">VAT</fo:block>
                    </fo:table-cell> -->

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Unit Price</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Amount</fo:block>
                    </fo:table-cell>

                </fo:table-row>
            </fo:table-header>
            <fo:table-body font-size="8pt">
                <#list orderItemList as orderItem>
                    <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                    <#assign productId = orderItem.productId?if_exists>
                    <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                    <fo:table-row space-start=".15in">
                        <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="center" margin-top="5px">
                                <#if orderItem.productId?has_content>
                                    ${orderItem.productId}
                                <#elseif productId?exists>
                                    ${productId?default("N/A")}
                                <#else>
                                    ${orderItem.productId?default("N/A")}
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="left" margin-left="10px" margin-top="5px">
                                <#if orderItem.productId?has_content>
                                    ${orderItem.itemDescription?if_exists}
                                    <#if orderItem.comments?has_content>
                                        <fo:block/>
                                        <fo:block linefeed-treatment="preserve" margin-top="10px">${orderItem.comments?upper_case}</fo:block>
                                    </#if>
                                <#elseif productId?exists>
                                    ${orderItem.itemDescription?if_exists}
                                    <#if orderItem.comments?has_content>
                                        <fo:block/>
                                        <fo:block linefeed-treatment="preserve" margin-top="10px">${orderItem.comments?upper_case}</fo:block>
                                    </#if>
                                <#elseif orderItemType?exists>
                                    ${orderItem.itemDescription?if_exists}
                                    <#if orderItem.comments?has_content>
                                        <fo:block/>
                                        <fo:block linefeed-treatment="preserve" margin-top="10px">${orderItem.comments?upper_case}</fo:block>
                                    </#if>
                                <#else>
                                    ${orderItem.itemDescription?if_exists}
                                    <#if orderItem.comments?has_content>
                                        <fo:block/>
                                        <fo:block linefeed-treatment="preserve" margin-top="10px">${orderItem.comments?upper_case}</fo:block>
                                    </#if>
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <#-- <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <#assign productCategoryAndMemberGv = delegator.findByAnd("ProductCategoryAndMember", {"productId":orderItem.productId,"productCategoryTypeId":"CATALOG_CATEGORY"})/>
                            <#assign productCategoryGv=[]/>
                            <#if productCategoryAndMemberGv?has_content>
                                <#assign productCategoryGv = productCategoryAndMemberGv.get(0)/>
                            </#if>
                            <fo:block text-align="center" margin-top="5px"><#if productCategoryAndMemberGv?has_content>${productCategoryGv.categoryName?if_exists}</#if></fo:block>
                        </fo:table-cell> -->
                        <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="center" margin-top="5px">${remainingQuantity}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <#assign product = orderItem.getRelatedOne("Product")>
                            <#assign quantityUom = product.getString("quantityUomId")>
                            <#assign uomGv = (delegator.findOne("Uom", {"uomId", quantityUom?if_exists}, false))?if_exists />
                            <fo:block text-align="center" margin-top="5px"> <#if uomGv?has_content>${uomGv.description}</#if> </fo:block>
                        </fo:table-cell>
                        <#-- <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <#assign orderAdjustmentGv = delegator.findByAnd("OrderAdjustment", {"orderId":orderItem.orderId,"orderItemSeqId":orderItem.orderItemSeqId,"orderAdjustmentTypeId":"SALES_TAX"})/>
                            <#if orderAdjustmentGv?has_content>
                                <fo:block text-align="center" margin-top="5px">S</fo:block>
                            <#else>
                                <fo:block text-align="center" margin-top="5px">E</fo:block>
                            </#if>
                        </fo:table-cell> -->
                        <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="right" margin-top="5px" margin-right="5px"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="right" margin-top="5px" margin-right="5px">
                                <#if orderItem.statusId != "ITEM_CANCELLED">
                                    <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
                                <#else>
                                    <@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </#list>
                <#assign height = 100 - (30 * orderItemList.size())/>
                <#if height gt 0>
                    <fo:table-row height="${height}px">
                        <#list 6..1 as i>
                            <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black"></fo:table-cell>
                        </#list>
                    </fo:table-row>
                </#if>
            </fo:table-body>
        </fo:table>
    </#if>

    <fo:table table-layout="fixed" width="100%" space-after="0.025in" border-style="solid" border-width="thin" border-color="black">
        <fo:table-column column-width="proportional-column-width(12)"/>
        <fo:table-column column-width="proportional-column-width(88)"/>
        <fo:table-body font-size="8pt">
            <#assign rowColor = "white">
            <fo:table-row height="20pt">
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="none" border-width="thin" border-color="black" text-align="right" font-weight="bold">
                    <fo:block margin-top="5px">Notes : </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-left-style="none" border-width="thin" border-color="black" text-align="left" margin-left="5px">
                    <#if orderWithoutInternalNotes?has_content>
                        <#list orderWithoutInternalNotes as note>
                            <#if note.noteInfo?has_content>
                                <fo:block margin-top="5px">${note.noteInfo?if_exists}</fo:block>
                            </#if>
                        </#list>
                    <#else>
                        <fo:block margin-top="5px"></fo:block>
                    </#if>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>

    <fo:table table-layout="fixed" width="100%" space-after="0.025in" border-style="solid" border-width="thin" border-color="black">
        <fo:table-column column-width="proportional-column-width(42.24)"/>
        <fo:table-column column-width="proportional-column-width(57.76)"/>
        <fo:table-body font-size="8pt">
            <#assign rowColor = "white">
            <fo:table-row height="20pt">
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-style="solid" border-width="thin" border-color="black" text-align="right" margin-right="30px" font-weight="bold">
                    <fo:block margin-top="5px">Delivery on or before : </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-style="solid" border-width="thin" border-color="black" text-align="left" margin-left="5px">
                    <#assign deliveryDate = orderHeader.get("deliveryDate")>
                    <fo:block margin-top="5px"><#if deliveryDate?has_content>${deliveryDate?string("MM/dd/yyyy")}</#if></fo:block>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row height="20pt">
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-style="solid" border-width="thin" border-color="black" text-align="right" margin-right="30px" font-weight="bold">
                    <fo:block margin-top="5px">Payment Terms : </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="2pt" background-color="${rowColor}" border-style="solid" border-width="thin" border-color="black" text-align="left" margin-left="5px">
                    <#if orderTerms?has_content>
                        <#list orderTerms as orderTerm>
                            <fo:block margin-top="5px"><#if (orderTerm.termTypeId == "FIN_PAYMENT_TERM" || orderTerm.termTypeId == "FIN_PAY_NETDAYS_1" || orderTerm.termTypeId == "FIN_PAY_NETDAYS_2" || orderTerm.termTypeId == "FIN_PAY_NETDAYS_3") && orderTerm.termDays?has_content> ${orderTerm.termDays?if_exists} Days <#else> ${orderTerm.termValue?if_exists} </#if></fo:block>
                            <#if orderTerm.textValue?has_content>
                                <fo:block margin-top="5px" margin-bottom="5px">${orderTerm.textValue?if_exists}</fo:block>
                            </#if>
                        </#list>
                    <#else>
                        <fo:block margin-top="5px"></fo:block>
                    </#if>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>

    <fo:table table-layout="fixed" font-size="9pt" width="100%" space-after="0.15in" border-style="solid" border-width="thin" border-color="black">
        <fo:table-column column-number="1" column-width="proportional-column-width(52.4)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(47.6)"/>
        <fo:table-body>
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="20px">Prepared By: ...........................................  &#160; &#160; &#160; Date: ............................</fo:block>
                </fo:table-cell>
                <fo:table-cell border-style="solid" border-width="thin">
                    <fo:table table-layout="fixed" font-size="10pt" width="100%">
                        <fo:table-column column-number="1" column-width="proportional-column-width(37.4)"/>
                        <fo:table-column column-number="2" column-width="proportional-column-width(62.6)"/>
                        <fo:table-body>
                            <fo:table-row height="35px">
                                <fo:table-cell>
                                    <fo:block text-align="left" margin-top="15px" margin-left="15px">Sub Total</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left-style="solid" border-width="thin">
                                    <fo:block text-align="right" margin-top="15px" margin-right="5px">
                                        <@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="20px">Checked By: ............................................  &#160; &#160; &#160; Date: ...........................</fo:block>
                </fo:table-cell>
                <fo:table-cell border-style="solid" border-width="thin">
                    <fo:table table-layout="fixed" font-size="10pt" width="100%">
                        <fo:table-column column-number="1" column-width="proportional-column-width(37.4)"/>
                        <fo:table-column column-number="2" column-width="proportional-column-width(62.6)"/>
                        <fo:table-body>
                            <fo:table-row height="35px">
                                <fo:table-cell>
                                    <fo:block text-align="left" margin-top="15px" margin-left="15px">Discount</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left-style="solid" border-width="thin">
                                    <fo:block text-align="right" margin-top="15px" margin-right="5px">
                                        <#-- the adjustment total -->
                                        <#assign ordAdjAmt = Static["java.math.BigDecimal"].ZERO>
                                        <#if goupedOrderAdjustment?has_content>
                                            <@ofbizCurrency amount=goupedOrderAdjustment.get(0).amount isoCode=currencyUomId/>
                                        <#else>
                                            <@ofbizCurrency amount=ordAdjAmt isoCode=currencyUomId/>
                                        </#if>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:table-cell>
            </fo:table-row>
            <#-- <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px">Approved By: ...........................................  &#160; &#160; &#160; Date: ...........................</fo:block>
                </fo:table-cell>
                <fo:table-cell border-style="solid" border-width="thin">
                    <fo:table table-layout="fixed" font-size="10pt" width="100%">
                        <fo:table-column column-number="1" column-width="proportional-column-width(37.4)"/>
                        <fo:table-column column-number="2" column-width="proportional-column-width(62.6)"/>
                        <fo:table-body>
                            <fo:table-row height="35px">
                                <fo:table-cell>
                                    <fo:block text-align="left" margin-top="15px" margin-left="15px">VAT Total</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left-style="solid" border-width="thin">
                                    <fo:block text-align="right" margin-top="15px" margin-right="5px">
                                        <!-- the vat total &ndash;&gt;
                                        <#assign vatAmt = Static["java.math.BigDecimal"].ZERO>
                                        <#if invoiceItemsVat?has_content>
                                            <@ofbizCurrency amount=invoiceItemsVat.get(0).amount isoCode=currencyUomId/>
                                        <#else>
                                            <@ofbizCurrency amount=vatAmt isoCode=currencyUomId/>
                                        </#if>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:table-cell>
            </fo:table-row> -->
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="20px">Approved By: ...........................................  &#160; &#160; &#160; Date: ...........................</fo:block>
                </fo:table-cell>
                <fo:table-cell border-style="solid" border-width="thin">
                    <fo:table table-layout="fixed" font-size="10pt" width="100%">
                        <fo:table-column column-number="1" column-width="proportional-column-width(37.4)"/>
                        <fo:table-column column-number="2" column-width="proportional-column-width(62.6)"/>
                        <fo:table-body>
                            <fo:table-row height="35px">
                                <fo:table-cell>
                                    <fo:block text-align="left" margin-top="15px" margin-left="15px">Total</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left-style="solid" border-width="thin">
                                    <fo:block text-align="right" margin-top="15px" margin-right="5px">
                                        <@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
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
