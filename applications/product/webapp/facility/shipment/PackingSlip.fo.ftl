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

    <#if hasPermission>
        
        
        <fo:table table-layout="fixed" space-after.optimum="2pt">
            <fo:table-column column-width="proportional-column-width(83)"/>
            <fo:table-column column-width="proportional-column-width(18)"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                       <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell>
                       <fo:block font-size="14pt" number-columns-spanned="2">DL NO: ${shipmentId?if_exists}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        
        <fo:table table-layout="fixed" space-after.optimum="2pt">
            <fo:table-column column-width="proportional-column-width(37)"/>
            <fo:table-column column-width="proportional-column-width(30)"/>
            <fo:table-column column-width="proportional-column-width(33)"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-size="15pt" font-weight="bold">DELIVERY NOTE</fo:block>
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

        <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(1.2)"/>
                <fo:table-column column-width="proportional-column-width(0.2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(1.2)"/>
                <fo:table-column column-width="proportional-column-width(0.2)"/>
                <fo:table-column column-width="proportional-column-width(1.5)"/>
                <fo:table-body>
                    <fo:table-row border-bottom-style="solid" border-bottom-color="grey">
                        <fo:table-cell padding="2pt" margin-left="5px">
                            <fo:block font-weight="bold" margin-top="10px" margin-bottom="10px">Delivery To</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block font-weight="bold" margin-top="10px" margin-bottom="10px"> : </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block margin-top="10px" margin-bottom="10px">
                                <#if destinationPostalAddress?has_content>
                                  <fo:block>${destinationPostalAddress.toName?if_exists}</fo:block>
                                  <#if destinationPostalAddress.attnName?has_content>
                                    <fo:block><fo:inline font-weight="bold">${uiLabelMap.CommonAttn}: </fo:inline>${destinationPostalAddress.attnName?if_exists}</fo:block>
                                  </#if>
                                  <fo:block>${destinationPostalAddress.address1?if_exists}</fo:block>
                                  <fo:block>${destinationPostalAddress.address2?if_exists}</fo:block>
                                  <fo:block>
                                    ${destinationPostalAddress.city?if_exists}<#if destinationPostalAddress.stateProvinceGeoId?has_content>, ${destinationPostalAddress.stateProvinceGeoId}</#if>
                                    ${destinationPostalAddress.postalCode?if_exists} ${destinationPostalAddress.countryGeoId?if_exists}
                                  </fo:block>
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block margin-top="10px" margin-bottom="10px" />
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block margin-top="10px" margin-bottom="10px" />
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block margin-top="10px" margin-bottom="10px" />
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell padding="2pt" margin-left="5px" height="25pt">
                            <fo:block font-weight="bold" margin-top="10px">Shipment No.</fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="25pt">
                            <fo:block font-weight="bold" margin-top="10px"> : </fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="25pt" border-right-style="solid" border-right-color="grey">
                            <fo:block margin-top="10px">${shipmentId?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" margin-left="5px" height="25pt">
                            <fo:block font-weight="bold" margin-top="10px">Shipment Date</fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="25pt">
                            <fo:block font-weight="bold" margin-top="10px"> : </fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="25pt">
                            <fo:block margin-top="10px">${shipment.createdStamp?if_exists?string("dd-MM-yyyy")}</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row border-top-style="solid" border-top-color="grey">
                        <fo:table-cell padding="2pt" margin-left="5px" height="25pt">
                            <fo:block font-weight="bold" margin-top="10px">Ordered Quantity</fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="20pt">
                            <fo:block font-weight="bold" margin-top="10px"> : </fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="20pt" border-right-style="solid" border-right-color="grey">
                            <fo:block margin-top="10px">${quantityRequested?default(0)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" margin-left="5px" height="25pt">
                            <fo:block font-weight="bold" margin-top="10px">Shipped Quantity</fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="25pt">
                            <fo:block font-weight="bold" margin-top="10px"> : </fo:block>
                        </fo:table-cell>
                        <fo:table-cell height="25pt">
                            <fo:block margin-top="10px">${quantityShipped?default(0)}</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:block>
        

        <fo:block><fo:leader/></fo:block>
        <fo:block font-size="11pt" font-weight="bold">DESCRIPTION OF MATERIALS / SERVICE</fo:block>
        <fo:block><fo:leader/></fo:block>

        <fo:block><fo:leader/></fo:block>
        <fo:block font-size="11pt" font-weight="bold">Order No :   ${shipment.primaryOrderId?default("N/A")}</fo:block>
        <fo:block><fo:leader/></fo:block>


        <#if packages?has_content>
            
            <fo:block space-after.optimum="10pt" font-size="10pt">
                <fo:table table-layout="fixed" border-style="solid" border-color="black">
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(2)"/>
                    <fo:table-column column-width="proportional-column-width(2)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1.5)"/>
                    <fo:table-header>
                        <fo:table-row font-weight="bold" text-align="center">
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Sl No.</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.ProductProduct}</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Machine</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.ProductPackedQty}</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Qty UOM</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.ProductPackedWeight}</fo:block></fo:table-cell>
                        </fo:table-row>
                    </fo:table-header>
                    <fo:table-body>
                        <#assign rowColor = "white">
                        <#list packages as package>
                            <#list package as line>
                                <fo:table-row text-align="center" height="15pt">
                                    <fo:table-cell background-color="${rowColor}">
                                        <fo:block margin-top="3px">${line.shipmentPackageSeqId}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell background-color="${rowColor}">
                                        <#if line.product?has_content>
                                            <fo:block margin-top="3px">${line.product.internalName?default("Internal Name Not Set!")}</fo:block>
                                        <#else>
                                            <fo:block margin-top="3px">${line.getClass().getName()}&nbsp;</fo:block>
                                        </#if>
                                    </fo:table-cell>
                                    <fo:table-cell background-color="${rowColor}">
                                        <fo:block margin-top="3px">${line.machine?if_exists}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell background-color="${rowColor}">
                                        <fo:block margin-top="3px">${line.quantityInPackage?default(0)}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell background-color="${rowColor}">
                                        <fo:block margin-top="3px">${line.uom?if_exists}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell background-color="${rowColor}">
                                        <fo:block margin-top="3px">${line.packedWeight?if_exists}</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </#list>
                                
                            <#-- toggle the row color -->
                            <#if rowColor == "white">
                                <#assign rowColor = "#CCCCCC">
                            <#else>
                                <#assign rowColor = "white">
                            </#if>
                                
                        </#list> <#-- packages -->
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
                <fo:table-column column-width="proportional-column-width(0.22)"/>
                <fo:table-column column-width="proportional-column-width(0.83)"/>
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>Prepared by :</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block border-bottom="dotted">${party.firstName?if_exists}  ${party.middleName?if_exists} ${party.lastName?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block/>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
            
            <fo:block><fo:leader/></fo:block>
            
            <fo:table table-layout="fixed" space-after.optimum="2pt">
                <fo:table-column />
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>Received by : ...............................................................</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>

        <#else>
            <fo:block font-size="14pt">
                ${uiLabelMap.ProductErrorNoPackagesFoundForShipment}
            </fo:block>
        </#if>

    <#else>
        <fo:block font-size="14pt">
            ${uiLabelMap.ProductFacilityViewPermissionError}
        </fo:block>
    </#if>

</#escape>
