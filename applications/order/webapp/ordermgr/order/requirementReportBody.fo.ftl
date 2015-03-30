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

    <fo:table table-layout="fixed" width="100%" border-width="thin" border-color="black">
        <fo:table-column column-width="proportional-column-width(23.5)"/>
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
                    <#assign requiredByDate = requirement.get("requiredByDate")>
                    <fo:block margin-top="3px">${requiredByDate?if_exists?string("MM/dd/yyyy")}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <#if requirement?has_content>
        <fo:table table-layout="fixed" width="100%" border-style="solid" border-width="thin" border-color="black">
            <fo:table-column column-width="proportional-column-width(3)"/>
            <fo:table-column column-width="proportional-column-width(15)"/>
            <fo:table-column column-width="proportional-column-width(5)"/>
            <fo:table-column column-width="proportional-column-width(4)"/>
            <fo:table-column column-width="proportional-column-width(4)"/>
            <fo:table-column column-width="proportional-column-width(2)"/>
            <fo:table-column column-width="proportional-column-width(7)"/>
            <fo:table-column column-width="proportional-column-width(7)"/>

            <fo:table-header font-size="8pt" font-weight="bold">
                <fo:table-row height="15pt">

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Code</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Description</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Item Category</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Quantity</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Unit</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">VAT</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Unit Price</fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-style="solid" border-width="thin" border-color="black">
                        <fo:block text-align="center" margin-top="3px">Amount</fo:block>
                    </fo:table-cell>

                </fo:table-row>
            </fo:table-header>
            <fo:table-body font-size="8pt">
                <fo:table-row space-start=".15in" height="270pt">
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block text-align="center" margin-top="5px">
                            ${requirement.productId}
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <#assign productGv =  delegator.findOne("Product", {"productId":requirement.productId},false)>
                        <fo:block text-align="center" margin-top="5px">
                            ${productGv.internalName?if_exists}
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <#if productCategoryAndMember?has_content>
                            <#assign productCategoryGv = productCategoryAndMember.get(0)/>
                        </#if>
                        <fo:block text-align="center" margin-top="5px"><#if productCategoryAndMember?has_content>${productCategoryGv.categoryName?if_exists}</#if></fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block text-align="center" margin-top="5px">
                            ${requirement.quantity}
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <#assign product = requirement.getRelatedOne("Product")>
                        <#assign quantityUom = product.getString("quantityUomId")>
                        <#assign uomGv = (delegator.findOne("Uom", {"uomId", quantityUom?if_exists}, false))?if_exists />
                        <fo:block text-align="center" margin-top="5px">
                            <#if uomGv?has_content>${uomGv.description}</#if>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block text-align="center" margin-top="5px">S</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block text-align="right" margin-top="5px" margin-right="5px">
                            <@ofbizCurrency amount=supplierProduct.lastPrice isoCode=currencyUomId/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <#assign subTotal = requirement.getBigDecimal("quantity").multiply(supplierProduct.getBigDecimal("lastPrice"))>
                        <fo:block text-align="right" margin-top="5px" margin-right="5px">
                            <@ofbizCurrency amount=subTotal?default(0)?if_exists isoCode=currencyUomId/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </#if>


    <fo:table table-layout="fixed" font-size="9pt" width="100%" border-left-style="solid" border-right-style="solid" border-top-style="none" border-bottom-style="solid" border-width="thin">
        <fo:table-column column-number="1" column-width="proportional-column-width(52.4)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(47.6)"/>
        <fo:table-body>
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="15px">Prepared By: ...........................................  &#160; &#160; &#160; Date: ............................</fo:block>
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
                                        <#-- the sub total amount -->
                                        <@ofbizCurrency amount=subTotal?default(0)?if_exists isoCode=currencyUomId/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="15px">Checked By: ............................................  &#160; &#160; &#160; Date: ...........................</fo:block>
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
                                        <#-- the vat amount -->
                                        <#assign vatAmt = (subTotal.divide(100)).multiply(16)>
                                        <@ofbizCurrency amount=vatAmt?default(0)?if_exists isoCode=currencyUomId/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="15px">Approved By: ...........................................  &#160; &#160; &#160; Date: ...........................</fo:block>
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
                                        <#-- the total amount -->
                                        <#assign totAmt = subTotal.add(vatAmt)>
                                        <@ofbizCurrency amount=totAmt isoCode=currencyUomId/>
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
        <fo:table-column/>
        <fo:table-body>
            <fo:table-row space-start="2.15in" border-width="thin" height="25px">
                <fo:table-cell>
                    <fo:block text-align="center" margin-top="15px" font-weight="bold">High Strength, Super Quality 42.5N</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>

</#escape>
