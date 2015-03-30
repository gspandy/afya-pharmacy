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
    <#-- list of orders -->
    <#-- list of terms -->

    <fo:table table-layout="fixed" width="100%" space-before="0in" >
    <fo:table-column column-width="2.6in"/>
    <fo:table-column column-width="2.8in"/>
    <fo:table-column column-width="2in"/>
   
     <fo:table-body font-size="8pt"  height="20px" border-style="solid" border-width="thin" border-color="black">
   			    <fo:table-row>
                     		<fo:table-cell number-columns-spanned="3" text-align="center"   >
                    		<fo:block font-size="7">Notes: Guarantee 18 months from the date of sale from any manufacturing defects / defects in operational functions.  </fo:block>
                     		</fo:table-cell>
                    		 
                </fo:table-row>
       <fo:table-row height="20px"  border-top-style="solid" border-top-width="thin" border-top-color="black">
       <fo:table-cell>
        <#assign orderDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(orderHeader.deliveryDate)>
          <fo:block text-align="left">Delivery:<#if orderDate?has_content> ${orderDate?if_exists}</#if></fo:block>
        </fo:table-cell> 
        <fo:table-cell> </fo:table-cell>
         <fo:table-cell> </fo:table-cell>
      </fo:table-row>
      
        <fo:table-row height="30px"  border-top-style="solid" border-top-width="thin" border-top-color="black">
       <fo:table-cell  number-columns-spanned="3">
          <fo:block text-align="left">Terms Of Payment: <#if termsGroup?has_content>${termsGroup?if_exists}</#if></fo:block>
          <fo:block text-align="left"><#if termType?has_content>${termType.description?if_exists}</#if></fo:block>
        </fo:table-cell> 
      </fo:table-row>
      
       <fo:table-row height="20px"  border-top-style="solid" border-top-width="thin" border-top-color="black">
       <fo:table-cell  number-columns-spanned="3">
          <fo:block text-align="left">Despatch:<#if orderItemShipGroup?has_content> ${orderItemShipGroup?if_exists}</#if></fo:block>
        </fo:table-cell> 
      </fo:table-row>
      <fo:table-row height="40px"  border-top-style="solid" border-top-width="thin" border-top-color="black">
       <fo:table-cell number-columns-spanned="3">
           <fo:block text-align="right"><#if partyGroup?has_content>${partyGroup.groupName?if_exists}</#if></fo:block>
        </fo:table-cell> 
      </fo:table-row>
      <fo:table-row height="10px"  border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black">
       <fo:table-cell> </fo:table-cell>
         <fo:table-cell> </fo:table-cell>
       <fo:table-cell>
           <fo:block text-align="center">HOD(Materials)</fo:block>
        </fo:table-cell> 
      </fo:table-row>
       <fo:table-row height="10px"  border="0px">
       <fo:table-cell number-columns-spanned="3">
           <fo:block text-align="left">Accounts/QAD/Stores/docket/OfficeCopy</fo:block>
        </fo:table-cell> 
      </fo:table-row>
</fo:table-body>

</fo:table>
</#escape>
