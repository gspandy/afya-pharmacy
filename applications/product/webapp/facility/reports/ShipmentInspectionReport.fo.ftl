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

  <fo:block font-size="15pt" text-align="center" font-weight="bold">Shipment Inspection</fo:block>
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
  <fo:block><fo:leader></fo:leader></fo:block>

  <#if shipmentInspectionList?has_content>
    <fo:block space-after.optimum="10pt" font-size="8pt">
      <fo:table table-layout="fixed" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>

        <fo:table-header>
          <fo:table-row font-weight="bold" text-align="center">
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Shipment</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Shipment Received Date</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Order No.</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Supplier</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Inspection Result</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Comments</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Inspected By</fo:block>
            </fo:table-cell>
            <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid">
              <fo:block>Approved By</fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>

        <fo:table-body>
          <#assign rowColor = "white">
          <#list shipmentInspectionList as shipmentInspection>
            <fo:table-row text-align="center" height="15pt">
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px">${shipmentInspection.shipmentId?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <#if shipmentInspection.datetimeReceived?has_content>
                  <fo:block margin-top="3px">${shipmentInspection.datetimeReceived?string("dd/MM/yyyy")}</fo:block>
                <#else>
                  <fo:block />
                </#if>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px">${shipmentInspection.purchaseOrderId?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px">${shipmentInspection.supplierName?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px">${shipmentInspection.inspectionResult?if_exists}</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="2pt" background-color="${rowColor}">
                <fo:block margin-top="3px">${shipmentInspection.inspectionNote?if_exists}</fo:block>
              </fo:table-cell>
              <#if shipmentInspection.inspectedBy?has_content>
                <fo:table-cell padding="2pt" background-color="${rowColor}">
                  <#assign inspectedParty = delegator.findOne("Person", {"partyId",shipmentInspection.inspectedBy},false)>
                  <#if inspectedParty?has_content>
                    <fo:block margin-top="3px">${inspectedParty.firstName?if_exists} ${inspectedParty.middleName?if_exists} ${inspectedParty.lastName?if_exists}</fo:block>
                  <#else>
                    <fo:block />
                  </#if>
                </fo:table-cell>
              <#else>
                <fo:table-cell padding="2pt" background-color="${rowColor}">
                  <fo:block />
                </fo:table-cell>
              </#if>
              <#if shipmentInspection.approvedBy?has_content>
                <fo:table-cell padding="2pt" background-color="${rowColor}">
                  <#assign approvedParty = delegator.findOne("Person", {"partyId",shipmentInspection.approvedBy},false)>
                  <#if approvedParty?has_content>
                    <fo:block margin-top="3px">${approvedParty.firstName?if_exists} ${approvedParty.middleName?if_exists} ${approvedParty.lastName?if_exists}</fo:block>
                  <#else>
                    <fo:block />
                  </#if>
                </fo:table-cell>
              <#else>
                <fo:table-cell padding="2pt" background-color="${rowColor}">
                  <fo:block />
                </fo:table-cell>
              </#if>
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

</#escape>
