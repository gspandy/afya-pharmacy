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
<#assign shipmentReceiptAndItems = delegator.findByAnd("ShipmentReceiptAndItem", {"orderId":parameters.purchaseOrderId, "shipmentId":parameters.shipmentId})>
<#assign orderId = shipmentReceiptAndItems.get(0).get("orderId")>
<#assign shipmentId = shipmentReceiptAndItems.get(0).get("shipmentId")>
<#assign shipmentGv = delegator.findOne("Shipment", {"shipmentId",shipmentId},false)>
<#assign partyGroupGv = delegator.findOne("PartyGroup", {"partyId":shipmentGv.partyIdFrom}, false)>

        <#if shipmentReceiptAndItems?has_content>
            <fo:table table-layout="fixed" space-after.optimum="2pt">
                <fo:table-column column-width="proportional-column-width(65)"/>
                <fo:table-column column-width="proportional-column-width(35)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                           <fo:block/>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="13pt">Date Time: ${shipmentReceiptAndItems.get(0).get("datetimeReceived")?string("dd/MM/yyyy HH:mm:ss")}</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
            
            <fo:table table-layout="fixed" space-after.optimum="2pt">
                <fo:table-column column-width="proportional-column-width(65)"/>
                <fo:table-column column-width="proportional-column-width(35)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                           <fo:block/>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="13pt">Shipment No: ${shipmentId?if_exists}</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </#if>
        
        <#-- blank line -->
        <fo:table table-layout="fixed" space-after.optimum="2pt">
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-body>
                <fo:table-row height="12.5px" space-start=".15in">
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        
        <fo:table table-layout="fixed" space-after.optimum="2pt">
            <fo:table-column column-width="proportional-column-width(35)"/>
            <fo:table-column column-width="proportional-column-width(40)"/>
            <fo:table-column column-width="proportional-column-width(30)"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-size="15pt" font-weight="bold">GOODS RECEIVED NOTE</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block/>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        
        <#-- blank line -->
        <fo:table table-layout="fixed" space-after.optimum="2pt">
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-body>
                <fo:table-row height="20px" space-start=".15in">
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        
        <#if shipmentReceiptAndItems?has_content>
            <#if orderId?has_content>
                <#assign orderContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getOrderContactMechValueMaps(delegator, orderId)>
                <#if orderContactMechValueMaps?has_content>
                    <#list orderContactMechValueMaps as orderContactMechValueMap>
                        <#assign contactMech = orderContactMechValueMap.contactMech>
                        <#assign orderContactMech = orderContactMechValueMap.orderContactMech>
                        <#if orderContactMech?has_content && orderContactMech.contactMechPurposeTypeId == "SHIP_ORIG_LOCATION">
                            <#assign contactMechId = orderContactMech.contactMechId>
                            <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                                <#assign originPostalAddress = orderContactMechValueMap.postalAddress>
                                
                                <fo:table table-layout="fixed" space-after.optimum="2pt">
                                    <fo:table-column column-width="proportional-column-width(0.25)"/>
                                    <fo:table-column column-width="proportional-column-width(0.75)"/>
                                    <fo:table-column column-width="proportional-column-width(1)"/>
                                    <fo:table-body>
                                        <fo:table-row font-size="10pt">
                                            <fo:table-cell padding="2pt" margin-left="5px">
                                                <fo:block font-weight="bold">Supplier :</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block>
                                                    <#if originPostalAddress?has_content && originPostalAddress.contactMechId == contactMechId>
                                                        <fo:block margin-top="2px" margin-bottom="3px" border-bottom="dotted">${partyGroupGv.groupName?if_exists}</fo:block>
                                                        <#if originPostalAddress.attnName?has_content>
                                                            <fo:block margin-top="7px" margin-bottom="3px" border-bottom="dotted"><fo:inline font-weight="bold">${uiLabelMap.CommonAttn}: </fo:inline>${originPostalAddress.attnName?if_exists}</fo:block>
                                                        </#if>
                                                        <fo:block margin-top="7px" margin-bottom="3px" border-bottom="dotted">${originPostalAddress.address1?if_exists}</fo:block>
                                                        <#if originPostalAddress.address2?has_content>
                                                            <fo:block margin-top="7px" margin-bottom="3px" border-bottom="dotted">${originPostalAddress.address2?if_exists}</fo:block>
                                                        </#if>
                                                        <fo:block margin-top="7px" margin-bottom="3px" border-bottom="dotted">
                                                            ${originPostalAddress.city?if_exists}<#if originPostalAddress.stateProvinceGeoId?has_content>, ${originPostalAddress.stateProvinceGeoId}</#if>
                                                            ${originPostalAddress.postalCode?if_exists} ${originPostalAddress.countryGeoId?if_exists}
                                                        </fo:block>
                                                    <#else>
                                                        <fo:block margin-top="2px" margin-bottom="3px" border-bottom="dotted">${partyGroupGv.groupName?if_exists}</fo:block>
                                                    </#if>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2pt" margin-left="5px">
                                                <fo:block />
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            </#if>
                        </#if>
                    </#list>
                </#if>
            </#if>
            
            <#-- blank line -->
            <fo:table table-layout="fixed" space-after.optimum="2pt">
                <fo:table-column/>
                <fo:table-column/>
                <fo:table-body>
                    <fo:table-row height="20px" space-start=".15in">
                        <fo:table-cell>
                            <fo:block />
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block />
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
            
            <fo:block space-after.optimum="10pt" font-size="10pt">
                <#-- <fo:table table-layout="fixed" border-color="black">
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(2.5)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <fo:table-column column-width="proportional-column-width(2)"/>
                    <fo:table-body>
                        <fo:table-row font-weight="bold" text-align="center">
                            <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                            <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                            <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                            <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                            <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                            <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                            <fo:table-cell height="20pt"><fo:block style="color:#FFF"></fo:block></fo:table-cell>
                            <fo:table-cell background-color="#FFF" height="20pt" display-align="center" border-top-style="solid" border-left-style="solid" border-right-style="solid"><fo:block>${uiLabelMap.OrderAmount}</fo:block></fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table> -->
                <fo:table table-layout="fixed" border-style="solid" border-color="black">
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(2.5)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <#-- <fo:table-column column-width="proportional-column-width(0.5)"/> -->
                    <fo:table-header>
                        <fo:table-row font-weight="bold" text-align="center">
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Invoice No.</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Order No.</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Item Description</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Unit</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Accepted Qty</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Rejected Qty</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block margin-right="15px" text-align="right">${uiLabelMap.OrderUnitPrice}</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block margin-right="15px" text-align="right">${uiLabelMap.OrderAmount}</fo:block></fo:table-cell>
                            <#-- <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>K</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid"><fo:block>n</fo:block></fo:table-cell> -->
                        </fo:table-row>
                    </fo:table-header>
                    <#assign  totalAmount = Static["java.math.BigDecimal"].ZERO>
                    <fo:table-body>
                        <#assign rowColor = "white">
                        <#list shipmentReceiptAndItems as item>
                            <fo:table-row text-align="center" height="15pt">
                                <fo:table-cell background-color="${rowColor}">
                                    <#assign orderItemGvs = delegator.findByAnd("OrderHeaderAndItems",{"orderId":item.orderId,"orderItemSeqId":item.orderItemSeqId})/>
                                    <#assign orderItemgv = orderItemGvs.get(0)/>
                                    <#assign receivedRowItems = delegator.findByAnd("ShipmentReceiptAndItem", {"shipmentId":item.shipmentId, "receiptId":item.receiptId, "productId":item.productId})/>
                                    <#assign purchaseOrderId = receivedRowItems.get(0).get("orderId")>
                                    <#assign shipmentReceiptId = receivedRowItems.get(0).get("receiptId")>
                                    <#assign orderItemBillingList = delegator.findByAnd("OrderItemBilling", {"orderId":purchaseOrderId, "shipmentReceiptId":shipmentReceiptId})>
                                    <#assign orderItemBillingGv = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderItemBillingList)>
                                    <fo:block margin-top="3px"><#if orderItemBillingGv?has_content>${orderItemBillingGv.invoiceId?if_exists}</#if></fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="${rowColor}">
                                    <fo:block margin-top="3px">${item.orderId?if_exists}</fo:block>
                                </fo:table-cell>
                                <#assign productGv =  delegator.findOne("Product", {"productId":item.productId},false)>
                                <#assign uomGv = delegator.findOne("Uom", {"uomId":productGv.quantityUomId}, false)>
                                <fo:table-cell background-color="${rowColor}">
                                    <fo:block margin-top="3px">${productGv.internalName?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="${rowColor}">
                                    <fo:block margin-top="3px">${uomGv.description?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="${rowColor}">
                                    <fo:block margin-top="3px">${item.quantityAccepted?if_exists?string.number}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="${rowColor}">
                                    <fo:block margin-top="3px">${item.quantityRejected?if_exists?default(0)?string.number}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="${rowColor}">
                                    <fo:block margin-top="3px" margin-right="10px" text-align="right"><@ofbizCurrency amount=orderItemgv.unitPrice?default(0)?if_exists isoCode=orderItemgv.currencyUom?if_exists /></fo:block>
                                </fo:table-cell>
                                <#assign sumAmount = item.getBigDecimal("quantityAccepted").multiply(orderItemgv.getBigDecimal("unitPrice"))>
                                <fo:table-cell background-color="${rowColor}">
                                    <fo:block margin-top="3px" margin-right="10px" text-align="right"><@ofbizCurrency amount=sumAmount?default(0)?if_exists isoCode=orderItemgv.currencyUom?if_exists /></fo:block>
                                </fo:table-cell>
                                <#-- <#assign amt = sumAmount.toString()>
                                <#assign kwacha = amt.substring(0,amt.indexOf("."))>
                                <#assign frc = amt.substring(amt.indexOf(".")+1)>
                                <#assign ngwee = frc.substring(0, 2)>
                                <fo:table-cell background-color="${rowColor}" border-left-style="solid">
                                    <fo:block margin-top="3px">${kwacha?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="${rowColor}" border-left-style="solid">
                                    <fo:block margin-top="3px">${ngwee?if_exists}</fo:block>
                                </fo:table-cell> -->
                                <#assign totalAmount = totalAmount.add(sumAmount)>
                            </fo:table-row>
                            <#-- toggle the row color -->
                            <#if rowColor == "white">
                                <#assign rowColor = "#CCCCCC">
                            <#else>
                                <#assign rowColor = "white">
                            </#if>
                        </#list>
                        <fo:table-row text-align="center" height="15pt">
                            <#assign totAmt = totalAmount.toString()>
                            <#-- <#assign totKwacha = totAmt.substring(0,totAmt.indexOf("."))>
                            <#assign frac = totAmt.substring(totAmt.indexOf(".")+1)>
                            <#assign totNgwee = frac.substring(0, 2)> -->
                            <fo:table-cell number-columns-spanned="6" background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
                                <fo:block></fo:block>
                            </fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
                                <fo:block font-weight="bold" margin-right="15px" text-align="right">Total</fo:block>
                            </fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid">
                                <fo:block margin-right="10px" text-align="right"><@ofbizCurrency amount=totAmt?default(0)?if_exists isoCode=orderItemgv.currencyUom?if_exists /></fo:block>
                            </fo:table-cell>
                            <#-- <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid">
                                <fo:block>${totKwacha?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-left-style="solid">
                                <fo:block>${totNgwee?if_exists}</fo:block>
                            </fo:table-cell> -->
                    </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:block>
            
            <#-- blank line -->
            <fo:table table-layout="fixed" space-after.optimum="2pt">
                <fo:table-column/>
                <fo:table-column/>
                <fo:table-body>
                    <fo:table-row height="20px" space-start=".15in">
                        <fo:table-cell>
                            <fo:block />
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block />
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
            
            <fo:table table-layout="fixed" space-after.optimum="2pt">
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(1.2)"/>
                <fo:table-column column-width="proportional-column-width(1.2)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                            <#assign receivedParty = delegator.findOne("Person", {"partyId",shipmentGv.receivedBy},false)>
                            <#if receivedParty?has_content>
                                <#-- <fo:block font-size="10pt" border-bottom="dotted">Received by : ${receivedParty.firstName?if_exists} ${receivedParty.middleName?if_exists} ${receivedParty.lastName?if_exists}</fo:block> -->
                                <fo:table table-layout="fixed" space-after.optimum="2pt">
                                    <fo:table-column column-width="proportional-column-width(0.5)"/>
                                    <fo:table-column column-width="proportional-column-width(1.45)"/>
                                    <fo:table-column column-width="proportional-column-width(0.05)"/>
                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt">Received by : </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt" border-bottom="dotted">${receivedParty.firstName?if_exists} ${receivedParty.middleName?if_exists} ${receivedParty.lastName?if_exists}</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block />
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            <#else>
                                <fo:block font-size="10pt">Received by : ............................</fo:block>
                            </#if>
                        </fo:table-cell>
                        <fo:table-cell>
                            <#assign inspectedParty = delegator.findOne("Person", {"partyId",shipmentGv.inspectedBy},false)>
                            <#if inspectedParty?has_content>
                                <#-- <fo:block font-size="10pt" border-bottom="dotted">Checked by : ${inspectedParty.firstName?if_exists} ${inspectedParty.middleName?if_exists} ${inspectedParty.lastName?if_exists}</fo:block> -->
                                <fo:table table-layout="fixed" space-after.optimum="2pt">
                                    <fo:table-column column-width="proportional-column-width(0.5)"/>
                                    <fo:table-column column-width="proportional-column-width(0.65)"/>
                                    <fo:table-column column-width="proportional-column-width(0.05)"/>
                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt">Checked by : </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt" border-bottom="dotted">${inspectedParty.firstName?if_exists} ${inspectedParty.middleName?if_exists} ${inspectedParty.lastName?if_exists}</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block />
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            <#else>
                                <fo:block font-size="10pt">Checked by : ............................</fo:block>
                            </#if>
                        </fo:table-cell>
                        <fo:table-cell>
                            <#assign approvedParty = delegator.findOne("Person", {"partyId",shipmentGv.approvedBy},false)>
                            <#assign gmApprovalStatus = shipmentGv.get("approvalStatus")>
                            <#if approvedParty?has_content && gmApprovalStatus=="GM_APPROVED">
                                <#-- <fo:block font-size="10pt" border-bottom="dotted">Approved by : ${approvedParty.firstName?if_exists} ${approvedParty.middleName?if_exists} ${approvedParty.lastName?if_exists}</fo:block> -->
                                <fo:table table-layout="fixed" space-after.optimum="2pt">
                                    <fo:table-column column-width="proportional-column-width(0.55)"/>
                                    <fo:table-column column-width="proportional-column-width(0.6)"/>
                                    <fo:table-column column-width="proportional-column-width(0.05)"/>
                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt">Approved by : </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt" border-bottom="dotted">${approvedParty.firstName?if_exists} ${approvedParty.middleName?if_exists} ${approvedParty.lastName?if_exists}</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block />
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            <#elseif approvedParty?has_content && gmApprovalStatus=="GM_REJECTED">
                                <fo:table table-layout="fixed" space-after.optimum="2pt">
                                    <fo:table-column column-width="proportional-column-width(0.55)"/>
                                    <fo:table-column column-width="proportional-column-width(0.6)"/>
                                    <fo:table-column column-width="proportional-column-width(0.05)"/>
                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt">Rejected by : </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block font-size="10pt" border-bottom="dotted">${approvedParty.firstName?if_exists} ${approvedParty.middleName?if_exists} ${approvedParty.lastName?if_exists}</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block />
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            <#else>
                                <fo:block font-size="10pt">Approved by : ............................</fo:block>
                            </#if>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        <#else>
            <fo:block font-size="14pt">
                ERROR: No goods found received for this shipment
            </fo:block>
        </#if>

</#escape>
