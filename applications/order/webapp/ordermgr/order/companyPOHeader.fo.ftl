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

    <fo:block font-size="8pt" font-family="Calibri">

        <fo:table table-layout="fixed" width="100%">

            <fo:table-column column-number="1" column-width="proportional-column-width(75)"/>
            <fo:table-column column-number="2" column-width="proportional-column-width(25)"/>

            <fo:table-body>

                <fo:table-row>

                    <fo:table-cell>
                        <fo:block number-columns-spanned="2">
                            <#if logoImageUrl?has_content><fo:external-graphic src="./framework/images/webapp/images/${logoImageUrl}" overflow="hidden" height="65px" content-height="scale-to-fit"/></#if>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block><fo:leader></fo:leader></fo:block>
                        <fo:block font-weight="bold" font-size="16px">Purchase Order</fo:block>
                        <fo:block font-size="14px">${orderId?if_exists}</fo:block>
                    </fo:table-cell>

                </fo:table-row>

            </fo:table-body>

        </fo:table>

    </fo:block>

</#escape>
