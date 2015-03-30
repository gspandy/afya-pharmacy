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
<#if partyGroup?has_content>
<fo:table table-layout="fixed" width="100%" space-before="0.2in">
     <fo:table-column column-width="5.8in"/>
     <fo:table-column column-width="5in"/>			
	            <fo:table-body >
	            
				<fo:table-row space-start="5">
                    <fo:table-cell>
                        <fo:block font-size="8px">KMMS/F.06/C/11</fo:block>
                        <fo:block font-size="7px">
                			<fo:block> TIN: ${partyGroup.vatTinNumber?if_exists}</fo:block>
                			<fo:block>CST: ${partyGroup.cstNumber?if_exists}</fo:block>
                			</fo:block>
                		<fo:block font-size="7px">
                		
                 			<fo:block>C Excise Reg No: <#if partyMenufactureUnit?has_content>${partyMenufactureUnit.exciseRegistrationNo?if_exists}</#if></fo:block>
                  			<fo:block>ECC Code:  ${partyGroup.eccCode?if_exists}</fo:block>
                   			<fo:block>Range:<#if partyMenufactureUnit?has_content> ${partyMenufactureUnit.rangeCode?if_exists}</#if></fo:block>
                     		<fo:block>Division:<#if partyMenufactureUnit?has_content>  ${partyMenufactureUnit.divisionCode?if_exists}</#if></fo:block>
                         	<fo:block>Service Tax Reg No.: ${partyGroup.serviceTaxNumber?if_exists}</fo:block>
                           </fo:block>
                    </fo:table-cell>
            
                    <fo:table-cell>
                    <fo:block font-size="7px">
                 		<#--<fo:block>Telephone : 0484-2787705,2787707</fo:block>-->
                  			<fo:block>Telephone :<#if partyPhoneNo?has_content> ${partyPhoneNo.countryCode?if_exists}-${partyPhoneNo.areaCode?if_exists}-${partyPhoneNo.contactNumber?if_exists}</#if></fo:block>
                   			<fo:block>Materials :<#if partyPhoneSecNo?has_content> ${partyPhoneSecNo.countryCode?if_exists}-${partyPhoneSecNo.areaCode?if_exists}- ${partyPhoneSecNo.contactNumber?if_exists}</#if></fo:block> 
                         	<#--<fo:block>E-mail: kelkochi@md5.vsnl.net.in</fo:block><br/>-->
                         	<fo:block>Fax  : <#if partyFax?has_content>${partyFax.countryCode?if_exists}-${partyFax.areaCode?if_exists}-${partyFax.contactNumber?if_exists}</#if></fo:block>
                         	<fo:block>E-mail:<#if partyMail?has_content>${partyMail.infoString?if_exists}</#if></fo:block>
                         	<fo:block>Website  :${partyGroup.officeSiteName?if_exists}</fo:block>
                           </fo:block>
                    </fo:table-cell>
                   
                 </fo:table-row>
                
                  <fo:table-row>
                    <fo:table-cell  margin-left="110px" >
                    <fo:block text-align="center">
                       <fo:block font-size="10px" font-weight="bold"> ${partyGroup.groupName?if_exists}</fo:block>
                       <fo:block font-size="7px">(A Government of Kerala Undertaking)</fo:block>
                        <fo:block font-size="8px"><#if partyAddressGeneral?has_content>${partyAddressGeneral.address1?if_exists}-${partyAddressGeneral.postalCode?if_exists} </#if></fo:block>
                        
                        <fo:block font-size="8px" font-weight="bold" text-decoration="underline">PURCHASE ORDER</fo:block>
                        </fo:block>
                     </fo:table-cell>
                 </fo:table-row>
                </fo:table-body>
</fo:table>
</#if>
</#escape>
