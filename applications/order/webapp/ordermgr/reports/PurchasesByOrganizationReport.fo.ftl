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
    <#assign showToParty = !parameters.toPartyId?has_content>
    <#assign showFromParty = !parameters.fromPartyId?has_content>

    <#if security.hasEntityPermission("ORDERMGR", "_PURCHASE_VIEW", session)>

        <fo:block font-size="15pt" text-align="center" font-weight="bold">Purchases by Supplier</fo:block>
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

        <#if productReportList?has_content>
            <#if !showToParty><fo:block font-size="10pt">${uiLabelMap.CommonFor}: ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, toPartyId, false)}</fo:block></#if>
            <#if !showFromParty><fo:block font-size="10pt">${uiLabelMap.CommonFrom}: ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, fromPartyId, false)}</fo:block></#if>
            <fo:block font-size="10pt">${uiLabelMap.FormFieldTitle_orderStatusId}:
                <#if parameters.orderStatusId?has_content>${parameters.orderStatusId}<#else>${uiLabelMap.CommonAny}</#if>
            </fo:block>
            <#if parameters.fromOrderDate?has_content><fo:block font-size="10pt">${uiLabelMap.CommonFromDate}: ${parameters.fromOrderDate} (${uiLabelMap.OrderDate} &gt;= ${uiLabelMap.CommonFrom})</fo:block></#if>
            <#if parameters.thruOrderDate?has_content><fo:block font-size="10pt">${uiLabelMap.CommonThruDate}: ${parameters.thruOrderDate} (${uiLabelMap.OrderDate} &lt;= ${uiLabelMap.CommonThru})</fo:block></#if>
            <fo:block><fo:leader></fo:leader></fo:block>
            <fo:block space-after.optimum="10pt" font-size="10pt">
                <fo:table table-layout="fixed" border-style="solid" border-color="black">
                    <fo:table-column column-width="proportional-column-width(3)"/>
                    <fo:table-column column-width="proportional-column-width(3)"/>
                    <fo:table-column column-width="proportional-column-width(3)"/>
                    <fo:table-header>
                        <fo:table-row font-weight="bold" text-align="center">
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>${uiLabelMap.ProductProduct}</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="70px">${uiLabelMap.OrderQuantityPurchase}</fo:block></fo:table-cell>
                            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block text-align="right" margin-right="50px">${uiLabelMap.OrderValuePurchase}</fo:block></fo:table-cell>
                        </fo:table-row>
                    </fo:table-header>
                    <fo:table-body>
                        <#assign rowColor = "white">
                        <#list productReportList as productReport>
                            <fo:table-row text-align="center" height="15pt">
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block margin-top="3px">${productReport.internalName?if_exists} (${productReport.productId?if_exists})</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block text-align="right" margin-right="70px" margin-top="3px">${productReport.quantity?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <#assign totalPrice = productReport.quantity?if_exists * productReport.unitPrice?if_exists>
                                    <fo:block text-align="right" margin-right="50px" margin-top="3px"><@ofbizCurrency amount=totalPrice/></fo:block>
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
                ${uiLabelMap.OrderNoOrderFound}.
            </fo:block>
        </#if>

    <#else>
        <fo:block font-size="14pt">
            ${uiLabelMap.OrderViewPermissionError}
        </fo:block>
    </#if>

</#escape>
