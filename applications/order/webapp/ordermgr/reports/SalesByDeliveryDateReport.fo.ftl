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

  <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>

    <fo:table table-layout="fixed" space-after.optimum="2pt">
      <fo:table-column column-width="proportional-column-width(78)"/>
      <fo:table-column column-width="proportional-column-width(22)"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block/>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block font-size="14pt" number-columns-spanned="2">Date: ${nowTimestamp?string("dd-MM-yyyy")}</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

    <fo:table table-layout="fixed" space-after.optimum="2pt">
      <fo:table-column column-width="proportional-column-width(30)"/>
      <fo:table-column column-width="proportional-column-width(45)"/>
      <fo:table-column column-width="proportional-column-width(25)"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block/>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block font-size="15pt" font-weight="bold">Sales by Delivery Date</fo:block>
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

    <#if orderItemList?has_content>
      <fo:table table-layout="fixed" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(1.1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(1.1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>

        <fo:table-header>
          <fo:table-row font-size="11pt" font-weight="bold" text-align="center">
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Order Id</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Product</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Status</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Customer</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Unit Price</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Quantity</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>EST Delivery Date</fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>

        <fo:table-body>
          <#assign rowColor = "white">
          <#list orderItemList as orderItem>
            <fo:table-row font-size="11pt" text-align="center">
              <fo:table-cell background-color="${rowColor}">
                <fo:block>${orderItem.orderId?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell background-color="${rowColor}">
                <fo:block>${orderItem.itemDescription?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell background-color="${rowColor}">
                <fo:block>${orderItem.orderStatusId?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell background-color="${rowColor}">
                <fo:block>${orderItem.customerName?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell background-color="${rowColor}">
                <fo:block>${orderItem.unitPrice?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell background-color="${rowColor}">
                <fo:block>${orderItem.quantity?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell background-color="${rowColor}">
                  <fo:block>${orderItem.estimatedDeliveryDate?if_exists}</fo:block>
              </fo:table-cell>
            </fo:table-row>

            <#-- toggle the row color -->
            <#if rowColor == "white">
              <#assign rowColor = "#CCCCCC">
            <#else>
              <#assign rowColor = "white">
            </#if>
          </#list>

        </fo:table-body>

      </fo:table>
    <#else>
      <fo:block>No records found.</fo:block>
    </#if>

  <#else>
    <fo:block font-size="14pt">
      ${uiLabelMap.OrderViewPermissionError}
    </fo:block>
  </#if>

</#escape>
