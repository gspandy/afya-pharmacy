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
<fo:table table-layout="fixed" width="100%" space-after="0.3in">
   <fo:table-column column-width="3in"/>
   <fo:table-column column-width="2.5in"/>
   <fo:table-column column-width="2.5in"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
        <#if orderHeader?has_content>
         </#if>
       		 <fo:block font-size="6pt">File No.:<#if orderHeader?has_content> ${orderHeader.fileNo?if_exists}</#if></fo:block>
             <fo:block font-size="6pt" font-weight="bold" > SALES ORDER NO :<#if orderHeader?has_content>${orderHeader.orderId?if_exists} </#if></fo:block>
        </fo:table-cell>
         <fo:table-cell>
       <fo:block  font-size="7pt" font-weight="bold">
        <fo:block> </fo:block>
		</fo:block>
     </fo:table-cell>
     <fo:table-cell>
       <fo:block font-weight="bold" font-size="7pt">
        <#assign orderDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(orderHeader.orderDate)>
        <fo:block>DATE : <#if orderDate?has_content> ${orderDate?if_exists}</#if></fo:block>
		</fo:block>
     </fo:table-cell>
     </fo:table-row>
      <fo:table-row>
        <fo:table-cell text-align="left" font-size="7pt" >
        <fo:block> Delivery Period </fo:block>
         <#assign orderDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(orderHeader.deliveryDate)>
         <fo:block text-align="left"> <#if orderDate?has_content>${orderDate?if_exists}</#if></fo:block>
   		<fo:block text-align="left"> Mode of Despatch:  <#if orderItemShipGroup?has_content> ${orderItemShipGroup?if_exists}</#if></fo:block>
   		</fo:table-cell>		
		<fo:table-cell text-align="left" font-size="7pt">
        </fo:table-cell> 
   		<fo:table-cell text-align="left" font-size="7pt" >
   		</fo:table-cell>
       </fo:table-row>
         <fo:table-row>
        <fo:table-cell text-align="left" font-size="7pt" >
        <fo:block> NAME OF THE CUSTOMER </fo:block>
        <fo:block text-indent="0.2in"> ${supplier.groupName} </fo:block>
       <#list orderContactMechValueMaps as orderContactMechValueMap>
			    	<#assign contactMech = orderContactMechValueMap.contactMech>
			    	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
			    	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
			        <#assign postalAddress = orderContactMechValueMap.postalAddress>
			        <fo:block text-indent="0.2in">
			            <#if postalAddress?has_content>
			                <#if postalAddress.toName?has_content><fo:block>${postalAddress.toName?if_exists}</fo:block></#if>
			             <#if postalAddress.attnName?has_content><fo:block>${postalAddress.attnName?if_exists}</fo:block></#if>
			                <fo:block>${postalAddress.address1?if_exists}</fo:block>
			                <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
			                <fo:block>
			                    <#assign stateGeo = (delegator.findOne("Geo", {"geoId", postalAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
			                    ${postalAddress.city}<#if stateGeo?has_content>, ${stateGeo.geoName?if_exists}</#if> ${postalAddress.postalCode?if_exists}
			                </fo:block>
			                <fo:block>
			                    <#assign countryGeo = (delegator.findOne("Geo", {"geoId", postalAddress.countryGeoId?if_exists}, false))?if_exists />
			                    <#if countryGeo?has_content>${countryGeo.geoName?if_exists}</#if>
			                </fo:block>
			            </#if>
			        		</fo:block>
			    		</#if>
			    		<#break>
						</#list> 
   		</fo:table-cell>		
   		 <fo:table-cell text-align="left" font-size="7pt" >
   		</fo:table-cell>
   		 <fo:table-cell text-align="left" font-size="7pt" >
			<fo:block font-size="7px">
            <fo:block>PO No:    <#if customerPoNumber?has_content>${customerPoNumber?if_exists}</#if></fo:block>
           <#assign orderDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(orderHeader.orderDate)>
        	<fo:block><#if orderDate?has_content> Date:${orderDate?if_exists}</#if></fo:block>
            </fo:block>                    			   			     
   		</fo:table-cell>	
   		</fo:table-row>
        <fo:table-row>
         <fo:table-cell text-align="left" font-size="7pt">
          <#-- <fo:block>Terms of payment: Rs.  </fo:block> -->
          <fo:block>Paying Officer :<#if orderHeader?has_content> ${orderHeader.payingOfficer?if_exists}</#if></fo:block>
          <#list partyIdent as party>
           <fo:block>Customer Bank :<#if party?has_content> ${party.idValue}</#if></fo:block>
          	</#list> 
   		</fo:table-cell>
   		 <fo:table-cell text-align="left" font-size="7pt" >
   		</fo:table-cell> <fo:table-cell text-align="left" font-size="7pt" >
   		</fo:table-cell>
       </fo:table-row>
     
  </fo:table-body>
</fo:table>
</#escape>
