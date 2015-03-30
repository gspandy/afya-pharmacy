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
    <#if orderHeader?has_content>
        <fo:table border-spacing="2pt" border-style="solid" border-width="thin" border-color="black">
            <fo:table-column column-width="10mm"  border-style="solid" border-width="thin" border-color="black"/>
            <fo:table-column column-width="30mm"/>
            <fo:table-column column-width="58mm"/>
            <fo:table-column column-width="30mm"/>
             <fo:table-column column-width="30mm"/>
              <fo:table-column column-width="30mm"/>
            <fo:table-header  height="8px" font-size="8px">
                <fo:table-row border-style="solid" border-width="thin" border-color="black">
                    <fo:table-cell text-align="center"  border-style="solid" border-width="thin" border-color="black">
                        <fo:block font-weight="bold">SL No.</fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center"  border-style="solid" border-width="thin" border-color="black">
                        <fo:block font-weight="bold">Item No.</fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center"  border-style="solid" border-width="thin" border-color="black">
                        <fo:block font-weight="bold">Description Of Material</fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center"  border-style="solid" border-width="thin" border-color="black">
                        <fo:block font-weight="bold">Quantity</fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center"  border-style="solid" border-width="thin" border-color="black">
                        <fo:block font-weight="bold">Unit</fo:block>
                        <#-- <fo:block font-weight="bold">Kg./Mtr./Ltr./Nos./m3</fo:block> -->
                    </fo:table-cell>
                    <fo:table-cell text-align="center"  border-style="solid" border-width="thin" border-color="black">
                        <fo:block font-weight="bold">Rate</fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-header>
                       <#assign count = 0>
            <fo:table-body font-size="10pt" height="20px"  border-style="solid" border-width="thin" border-color="black">
                
                <fo:table-row  height="20px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
              		  </fo:table-row>
              		  
                <#list orderItemList as orderItem>
                <#assign count = count+1>
                    <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                    <#assign productId = orderItem.productId?if_exists>
                    <#assign product = orderItem.getRelatedOne("Product")>
                    <#assign quantityUom = product.getString("quantityUomId")>
                    <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                    <#assign itemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
                    <fo:table-row>
					<fo:table-cell  text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
					<fo:block>${count}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
					<fo:block>${orderItem.productId}</fo:block>
					</fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="left">
                                <#if orderItem.supplierProductId?has_content>
                                    ${orderItem.supplierProductId} - ${orderItem.itemDescription?if_exists}
                                <#elseif productId?exists>
                                    ${orderItem.productId?default("N/A")} - ${orderItem.itemDescription?if_exists}
                                <#elseif orderItemType?exists>
                                    ${orderItemType.get("description",locale)} - ${orderItem.itemDescription?if_exists}
                                <#else>
                                    ${orderItem.itemDescription?if_exists}
                                </#if>
                            </fo:block>
                        </fo:table-cell >
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block>${remainingQuantity}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block><#if quantityUom?exists>${quantityUom}</#if></fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="right">
                                <#if orderItem.statusId != "ITEM_CANCELLED">
                                    <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
                                <#else>
                                    <@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    
                      <fo:table-row  height="10px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
              		  </fo:table-row>
              		  
                    <#if itemAdjustment != 0>
                        <fo:table-row margin-top="110px">
                         <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                            <fo:table-cell number-columns-spanned="1" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                                <fo:block text-indent="0.2in" text-align="right">
                                    <fo:inline font-style="italic">${uiLabelMap.OrderAdjustments}</fo:inline>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                             <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="right">  <@ofbizCurrency amount=itemAdjustment isoCode=currencyUomId/> </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#if>
                </#list>
                
                <fo:table-row  height="10px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
               </fo:table-row>
              		  
                <fo:table-row>
                <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                    <fo:block font-weight="bold" text-align="right">Item SubTotal</fo:block>
                    </fo:table-cell>
                             <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                    <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                        <fo:block text-align="right"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                
                <fo:table-row  height="10px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
               </fo:table-row>
               
                <#list orderAdjustments as orderHeaderAdjustment>
                    <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                    <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                    <#if adjustmentAmount != 0>
                        <fo:table-row margin-top="110px">
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                            <fo:table-cell number-columns-spanned="1" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                                <fo:block text-align="right">
                                    ${orderHeaderAdjustment.get("comments",locale)} 
                                </fo:block>
                            </fo:table-cell>
                         <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        	<fo:block> </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        	<fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                                <fo:block text-align="right"><@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#if>
                </#list>
                
                <fo:table-row  height="10px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
               </fo:table-row>
               
                <#-- notes -->
              
                <#-- summary of order amounts -->
                
                <#if otherAdjAmount != 0>
                    <fo:table-row margin-top="200px" >
            		  <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="right">${uiLabelMap.OrderTotalOtherOrderAdjustments}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="right"><@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/> </fo:block>
                            </fo:table-cell>
                    </fo:table-row>
                </#if>
       		<#--	<#if shippingAmount != 0>
                    <fo:table-row margin-top="200px">
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-weight="bold">${uiLabelMap.OrderTotalShippingAndHandling}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/> </fo:block>
                            </fo:table-cell>
                    </fo:table-row>
                </#if> -->  
                
                <fo:table-row  height="10px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
               </fo:table-row>
               
               <#-- <#if taxAmount != 0>
                    <fo:table-row margin-top="200px" >
                    	<fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                        <fo:table-cell text-align="center" number-columns-spanned="1" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-weight="bold" text-align="right">${uiLabelMap.OrderTotal}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                          <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                            <fo:block text-align="right"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>   
                    </fo:table-row>
                </#if> -->
                
                <fo:table-row  height="10px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
               </fo:table-row>
               
               
                <#if grandTotal != 0>
                    <fo:table-row  margin-top="110px">
                     <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" background-color="#EEEEEE" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-weight="bold" text-align="right">Total Due</fo:block>
                        </fo:table-cell>
                             <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                        <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block text-align="right"><@ofbizCurrency amount=grandTotal isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </#if>
                
                <fo:table-row  height="10px">
                    		<fo:table-cell  text-align="right" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                    		<fo:block font-size="8">                 </fo:block>
                     		</fo:table-cell>
                    		 <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                  </fo:block>
                            </fo:table-cell>
                    		 <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                   </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                    </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block font-size="8">                      </fo:block>
                            </fo:table-cell>
               </fo:table-row>
               
                <#-- notes -->
                <#if orderNotes?has_content>
                    <#if showNoteHeadingOnPDF>
                        <fo:table-row margin-top="110px">
                          <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                            <fo:table-cell number-columns-spanned="3" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                                <fo:block font-weight="bold">${uiLabelMap.OrderNotes}</fo:block>
                                <fo:block>
                                    <fo:leader leader-length="19cm" leader-pattern="rule"/>
                                </fo:block>
                            </fo:table-cell>
                              <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#if>
                    <#list orderNotes as note>
                        <#if (note.internalNote?has_content) && (note.internalNote != "Y")>
                             <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                        <fo:block> </fo:block>
                        </fo:table-cell>
                            <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                            <fo:table-row>
                                <fo:table-cell number-columns-spanned="1" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                                    <fo:block>${note.noteInfo?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell number-columns-spanned="2" text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black" >
                                    <#if note.noteParty?has_content>
                                        <#assign notePartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", note.noteParty, "compareDate", note.noteDateTime, "lastNameFirst", "Y", "userLogin", userLogin))/>
                                        <fo:block>${uiLabelMap.CommonBy}: ${notePartyNameResult.fullName?default("${uiLabelMap.OrderPartyNameNotFound}")}</fo:block>
                                    </#if>
                                </fo:table-cell>
                                <fo:table-cell number-columns-spanned="1">
                                    <fo:block>${uiLabelMap.CommonAt}: ${note.noteDateTime?string?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" border-right-style="solid" border-right-width="thin" border-right-color="black">
                            <fo:block> </fo:block>
                            </fo:table-cell>
                            </fo:table-row>
                        </#if>
                    </#list>
                </#if>
            </fo:table-body>
        </fo:table>
    </#if>
</#escape>
