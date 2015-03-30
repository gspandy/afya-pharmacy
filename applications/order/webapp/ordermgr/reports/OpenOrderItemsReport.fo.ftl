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
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
            margin-top="0.1in" margin-bottom="1in" margin-left="0.1in" margin-right="0.1in">
        <fo:region-body margin-top="0.5in"/>
        <fo:region-before extent="0.5in"/>
        <fo:region-after extent="0.5in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>

 <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
        <fo:block font-size="15pt" text-align="center" font-weight="bold">Open Order Items</fo:block>
         <fo:block >.</fo:block>
         <tr><td></td></tr>
        <#if orderItemList?has_content>
            <fo:table>
                <fo:table-column column-width="120pt"/>
                <fo:table-column column-width="300pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="80pt"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold">
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Order Id</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Product Description</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Order Qty</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Qty Shipped</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Balance Qty</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Price</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Order Date</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list orderItemList as orderItem>
                        <fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItem.orderId?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItem.itemDescription?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItem.quantityOrdered?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItem.quantityIssued?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItem.quantityOpen?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItem.retailPrice?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItem.orderDate?string('dd-MM-yyyy')}</fo:block>
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
            </#if>
        </fo:flow>
        </fo:page-sequence>
<#else>
    <fo:block font-size="14pt">
        ${uiLabelMap.OrderViewPermissionError}
    </fo:block>
</#if>

</fo:root>
</#escape>
