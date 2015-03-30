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

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Debit Balance</fo:block>
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
    <#-- <fo:table table-layout="fixed" space-after.optimum="2pt">
      <fo:table-column column-width="proportional-column-width(5)"/>
      <fo:table-column column-width="proportional-column-width(70)"/>
      <fo:table-column column-width="proportional-column-width(40)"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block/>
          </fo:table-cell>
          <fo:table-cell>
            <#if fromDate?has_content>
              <fo:block number-columns-spanned="2">
                From Date : ${fromDate?if_exists?string("dd/MM/yyyy")}
              </fo:block>
            <#else>
              <fo:block number-columns-spanned="2">
                From Date :
              </fo:block>
            </#if>
          </fo:table-cell>
          <fo:table-cell>
            <#if thruDate?has_content>
              <fo:block number-columns-spanned="2">
                Thru Date : ${thruDate?if_exists?string("dd/MM/yyyy")}
              </fo:block>
            <#else>
              <fo:block number-columns-spanned="2">
                Thru Date :
              </fo:block>
            </#if>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
    <fo:block><fo:leader></fo:leader></fo:block> -->

    <#if invoicePaymentInfoList?has_content>
      <fo:block space-after.optimum="10pt" font-size="8pt">
      <fo:table table-layout="fixed" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(3)"/>
        <fo:table-column column-width="proportional-column-width(1.5)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>

        <fo:table-header>
          <fo:table-row font-weight="bold" text-align="center">
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Invoice ID</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Term Type Id</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Due Date</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Amount</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Paid Amount</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Outstanding Amount</fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>

        <fo:table-body>
          <#assign rowColor = "white">
          <#list invoicePaymentInfoList as invoicePaymentInfo>
            <fo:table-row text-align="center" height="15pt">
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px">${invoicePaymentInfo.invoiceId?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <#if invoicePaymentInfo.termTypeId?has_content>
                  <#assign  termTypeGv = delegator.findOne("TermType",{"termTypeId":invoicePaymentInfo.termTypeId},false)/>
                  <fo:block margin-top="3px">${termTypeGv.description?if_exists}</fo:block>
                <#else>
                  <fo:block></fo:block>
                </#if>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <#if invoicePaymentInfo.dueDate?has_content>
                  <fo:block margin-top="3px">${invoicePaymentInfo.dueDate?if_exists?string("dd/MM/yyyy")}</fo:block>
                <#else>
                  <fo:block></fo:block>
                </#if>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px"><@ofbizCurrency amount=invoicePaymentInfo.amount?default(0)?if_exists /></fo:block>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px"><@ofbizCurrency amount=invoicePaymentInfo.paidAmount?default(0)?if_exists /></fo:block>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px"><@ofbizCurrency amount=invoicePaymentInfo.outstandingAmount?default(0)?if_exists /></fo:block>
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
      </fo:block>
    <#else>
      <fo:block>No records found.</fo:block>
    </#if>

  <#else>
    <fo:block font-size="14pt">
      ${uiLabelMap.OrderViewPermissionError}
    </fo:block>
  </#if>

</#escape>
