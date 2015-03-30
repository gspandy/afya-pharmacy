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

    <#-- do not display columns associated with values specified in the request, ie constraint values -->
    <#assign showProductStore = !parameters.productStoreId?has_content>

    <#assign showOriginFacility = !parameters.originFacilityId?has_content>
    <#assign showTerminal = !parameters.terminalId?has_content>
    <#assign showStatus = !parameters.statusId?has_content>

    <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Purchases by Product</fo:block>
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

        <#if orderPurchaseProductSummaryList?has_content>
            <#if !showProductStore><fo:block font-size="10pt">${uiLabelMap.CommonFor} ${uiLabelMap.ProductProductStore}: ${parameters.productStoreId}</fo:block></#if>
            <#if !showOriginFacility><fo:block font-size="10pt">${uiLabelMap.CommonFor} ${uiLabelMap.FormFieldTitle_originFacilityId}: ${parameters.originFacilityId}</fo:block></#if>
            <#if !showTerminal><fo:block font-size="10pt">${uiLabelMap.CommonFor} ${uiLabelMap.FormFieldTitle_terminalId}: ${parameters.terminalId}</fo:block></#if>
            <#if !showStatus><fo:block font-size="10pt">${uiLabelMap.CommonFor} ${uiLabelMap.CommonStatus}: ${parameters.statusId}</fo:block></#if>
            <#if parameters.fromOrderDate?has_content><fo:block font-size="10pt">${uiLabelMap.CommonFromDate}: ${parameters.fromOrderDate} (${uiLabelMap.OrderDate} &gt;= ${uiLabelMap.CommonFrom})</fo:block></#if>
            <#if parameters.thruOrderDate?has_content><fo:block font-size="10pt">${uiLabelMap.CommonThruDate}: ${parameters.thruOrderDate} (${uiLabelMap.OrderDate} &lt;= ${uiLabelMap.CommonThru})</fo:block></#if>
            <fo:block><fo:leader></fo:leader></fo:block>
            <fo:block space-after.optimum="10pt" font-size="10pt">
                <fo:table table-layout="fixed" border-style="solid" border-color="black">
                    <#if showProductStore><fo:table-column column-width="proportional-column-width(1)"/></#if>
                    <!--<#if showOriginFacility><fo:table-column column-width="proportional-column-width(3)"/></#if>-->
                    <!--<#if showTerminal><fo:table-column column-width="proportional-column-width(3)"/></#if>-->
                    <#if showStatus><fo:table-column column-width="proportional-column-width(2)"/></#if>
                    <fo:table-column column-width="proportional-column-width(2)"/>
                    <fo:table-column column-width="proportional-column-width(4)"/>
                    <fo:table-column column-width="proportional-column-width(2)"/>
                    <fo:table-column column-width="proportional-column-width(2)"/>
                    <fo:table-header>
                        <fo:table-row font-weight="bold" text-align="center">
                            <#if showProductStore><fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.FormFieldTitle_productStoreId}</fo:block></fo:table-cell></#if>
                            <!--<#if showOriginFacility><fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.FormFieldTitle_facilityId}</fo:block></fo:table-cell></#if>-->
                            <!--<#if showTerminal><fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.FormFieldTitle_terminalId}</fo:block></fo:table-cell></#if>-->
                            <#if showStatus><fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.CommonStatus}</fo:block></fo:table-cell></#if>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.FormFieldTitle_productId}</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.ProductProductName}</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Quantity</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Quantity Cancelled</fo:block></fo:table-cell>
                        </fo:table-row>
                    </fo:table-header>
                    <fo:table-body>
                        <#assign rowColor = "white">
                        <#list orderPurchaseProductSummaryList as orderPurchaseProductSummary>
                            <fo:table-row text-align="center" height="15pt">
                                <#if showProductStore>
                                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                                        <fo:block margin-top="3px">${orderPurchaseProductSummary.productStoreId?if_exists}</fo:block>
                                    </fo:table-cell>
                                </#if>
                                <!--<#if showOriginFacility>
                                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                                        <fo:block margin-top="3px">${orderPurchaseProductSummary.originFacilityId?if_exists}</fo:block>
                                    </fo:table-cell>
                                </#if>-->
                                <!--<#if showTerminal>
                                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                                        <fo:block margin-top="3px">${orderPurchaseProductSummary.terminalId?if_exists}</fo:block>
                                    </fo:table-cell>
                                </#if>-->
                                <#if showStatus>
                                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                                        <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : orderPurchaseProductSummary.statusId}, true)>
                                        <fo:block margin-top="3px">${statusItem.description?if_exists}</fo:block>
                                    </fo:table-cell>
                                </#if>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block margin-top="3px">${orderPurchaseProductSummary.productId?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block margin-top="3px">${orderPurchaseProductSummary.internalName?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block margin-top="3px">${orderPurchaseProductSummary.quantity?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block margin-top="3px">${orderPurchaseProductSummary.cancelQuantity?if_exists}</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <#-- toggle the row color -->
                            <#if rowColor == "white">
                                <#assign rowColor = "#D4D0C8">
                            <#else>
                                <#assign rowColor = "white">
                            </#if>
                        </#list>
                    </fo:table-body>
                </fo:table>
            </fo:block>
        
        <#else>
            <fo:block font-size="14pt">
                ${uiLabelMap.OrderNoPurchaseProduct}
            </fo:block>
        </#if>

    <#else>
        <fo:block font-size="14pt">
            ${uiLabelMap.OrderViewPermissionError}
        </fo:block>
    </#if>

</#escape>
