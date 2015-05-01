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
  <fo:block font-size="9pt" font-weight="bold">
    <fo:table table-layout="fixed" width="100%">
      <fo:table-column/>
      <fo:table-body>
        <fo:table-row text-align="center" border-bottom-style="solid" border-bottom-width="thin" height="30px">
          <fo:table-cell>
            <fo:block margin-top="15px">Invoice</fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row border-bottom-style="solid" border-bottom-width="thin" height="30px">
          <fo:table-cell>
            <fo:table table-layout="fixed" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(30)"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block margin-top="15px" margin-left="5px">Date</fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <#assign invoiceFormattedDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(invoiceDate)>
                    <fo:block margin-top="15px" margin-right="30px" text-align="right">${invoiceDate?if_exists?string("dd/MM/yyyy")}</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row border-bottom-style="solid" border-bottom-width="thin" height="30px">
          <fo:table-cell>
            <fo:table table-layout="fixed" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(30)"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell border-bottom-width="thin">
                    <fo:block margin-top="15px" margin-left="5px">Page</fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block margin-top="15px" margin-right="30px" text-align="right">1</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row height="30px">
          <fo:table-cell>
            <fo:table table-layout="fixed" width="100%">
              <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
              <fo:table-column column-number="2" column-width="proportional-column-width(30)"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block margin-top="15px" margin-left="5px">Document No</fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block margin-top="15px" margin-right="30px" text-align="right"><#if invoice?has_content>${invoice.invoiceId}</#if></fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </fo:block>
</#escape>
