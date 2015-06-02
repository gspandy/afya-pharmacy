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

<div class="screenlet">
    <div class="screenlet-body">
        <table width="100%" border="0" cellpadding="1">
        <#-- order name -->
        <#if (orderName?has_content)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>${uiLabelMap.OrderOrderName}</b> </span>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    ${orderName}
                </td>
            </tr>
            <tr><td colspan="7"><hr /></td></tr>
        </#if>
        <#-- order for party -->
        <#if (orderForParty?exists)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>${uiLabelMap.OrderOrderFor}</b> </span>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(orderForParty, false)} [${orderForParty.partyId}]
                </td>
            </tr>
            <tr><td colspan="7"><hr /></td></tr>
        </#if>
        <#if (cart.getPoNumber()?has_content)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>Customer ${uiLabelMap.OrderPONumber}</b> </span>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    ${cart.getPoNumber()}
                </td>
            </tr>
            <tr><td colspan="7"><hr /></td></tr>
        </#if>
        <#if orderTerms?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderOrderTerms}</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    <table>
                        <tr>
                            <td width="35%"><div><b>${uiLabelMap.OrderOrderTermType}</b></div></td>
                            <td width="10%"><div><b>${uiLabelMap.OrderOrderTermValue}</b></div></td>
                            <td width="10%"><div><b>${uiLabelMap.OrderOrderTermDays}</b></div></td>
                            <td width="45%"><div><b>${uiLabelMap.CommonDescription}</b></div></td>
                        </tr>
                        <tr><td colspan="4"><hr /></td></tr>
                        <#assign index=0/>
                        <#list orderTerms as orderTerm>
                        <tr>
                            <td width="35%"><div>${orderTerm.getRelatedOne("TermType").get("description",locale)}</div></td>
                            <td width="10%"><div>${orderTerm.termValue?default("")}</div></td>
                            <td width="10%"><div>${orderTerm.termDays?default("")}</div></td>
                            <td width="45%"><div>${orderTerm.textValue?default("")}</div></td>
                        </tr>
                            <#if orderTerms.size()&lt;index>
                        <tr><td colspan="4"><hr /></td></tr>
                            </#if>
                            <#assign index=index+1/>
                        </#list>
                    </table>
                </td>
            </tr>
            <tr><td colspan="7"><hr /></td></tr>
        </#if>
        <#-- tracking number -->
        <#if trackingNumber?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderTrackingNumber}</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
                    <div>${trackingNumber}</div>
                </td>
            </tr>
            <tr><td colspan="7"><hr /></td></tr>
        </#if>
        
        <#-- Order Date -->   
        <#if (cart.getOrderDate()?has_content && !cart.getPatientInfo()?has_content)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>Order Date</b> </span>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    ${cart.getOrderDate()?if_exists?string("dd/MM/yyyy")}
                </td>
            </tr>
            <#-- <tr><td colspan="7"><hr /></td></tr> -->
        <#elseif !cart.getPatientInfo()?has_content>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>Order Date</b> </span>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    ${nowTimestamp?string("dd/MM/yyyy")}
                </td>
            </tr>
            <#-- <tr><td colspan="7"><hr /></td></tr> -->
        </#if>
        
        <#-- Delivery Date -->   
        <#if (cart.getDeliveryDate()?has_content)>
            <tr>
                <td align="right" valign="top" width="15%">
                    <span>&nbsp;<b>Delivery Date</b> </span>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    ${cart.getDeliveryDate()?if_exists?string("dd/MM/yyyy")}
                </td>
            </tr>
            <tr><td colspan="7"><hr /></td></tr>
        </#if>
        
        <#-- splitting preference
        <tr>
            <td align="right" valign="top" width="15%">
                <div>&nbsp;<b>${uiLabelMap.OrderSplittingPreference}</b></div>
            </td>
            <td width="5">&nbsp;</td>
            <td valign="top" width="80%">
                <div>
                    <#if maySplit?default("N") == "N">${uiLabelMap.FacilityWaitEntireOrderReady}</#if>
                    <#if maySplit?default("Y") == "Y">${uiLabelMap.FacilityShipAvailable}</#if>
                </div>
            </td>
        </tr>
        -->
        <#-- shipping instructions -->
        <#if shippingInstructions?has_content>
            <tr><td colspan="7"><hr /></td></tr>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderSpecialInstructions}</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    <div>${shippingInstructions}</div>
                </td>
            </tr>
        </#if>
        <!-- <#if orderType != "PURCHASE_ORDER" && (productStore.showCheckoutGiftOptions)?if_exists != "N">
            <tr><td colspan="7"><hr /></td></tr>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderGift}</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    <div>
                        <#if isGift?default("N") == "N">${uiLabelMap.OrderThisOrderNotGift}</#if>
                        <#if isGift?default("N") == "Y">${uiLabelMap.OrderThisOrderGift}</#if>
                    </div>
                </td>
            </tr>
            <#if giftMessage?has_content>
            <tr><td colspan="7"><hr /></td></tr>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderGiftMessage}</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    <div>${giftMessage}</div>
                </td>
            </tr>
            </#if>

        </#if> -->
        <#if shipAfterDate?has_content>
            <tr><td colspan="7"><hr /></td></tr>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderShipAfterDate}</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                    <div>${shipAfterDate}</div>
                </td>
            </tr>
        </#if>
        <#if shipBeforeDate?has_content>
            <tr><td colspan="7"><hr /></td></tr>
            <tr>
                <td align="right" valign="top" width="15%">
                    <div>&nbsp;<b>${uiLabelMap.OrderShipBeforeDate}</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td valign="top" width="80%">
                  <div>${shipBeforeDate}</div>
                </td>
            </tr>
        </#if>
        </table>
        <#if orderType == "SALES_ORDER" && cart.getPatientInfo()?has_content>
            <div class="screenlet-body">
                <table width="100%" border="0" cellpadding="1" class="basic-table">
                    <tr>
                        <td align="right" valign="top" width="10%"><span id="afyaId_title">&nbsp;<b>Afya ID :</b> </span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="20%">${cart.getPatientInfo().getAfyaId()?if_exists}</td>
                        <td align="right" valign="top" width="10%"><span id="civilId_title"><b> Civil ID :</b> </span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="20%">${cart.getPatientInfo().getCivilId()?if_exists}</td>
                        <td align="right" valign="top" width="10%"><span id="gender_title"><b> Gender :</b> </span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="20%"><#if "M" == cart.getPatientInfo().getGender()>Male<#elseif "F" == cart.getPatientInfo().getGender()>Female<#else>&#160;&#32;</#if></td>
                    </tr>
                    <tr>
                        <td align="right" valign="top" width="10%"><span id="name_title">&nbsp;<b>Name :</b> </span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="20%">${cart.getPatientInfo().getFirstName()?if_exists} ${cart.getPatientInfo().getSecondName()?if_exists} ${cart.getPatientInfo().getThirdName()?if_exists}</td>
                        <td align="right" valign="top" width="10%"><span id="doctor_title"><b> Doctor :</b> </span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="20%">${cart.getPatientInfo().getDoctorName()?if_exists}</td>
                        <td align="right" valign="top" width="10%"><span id="patientType_title"><b> Patient Type :</b> </span></td>
                        <td width="1%">&nbsp;</td>
                        <td valign="top" width="20%">${cart.getPatientInfo().getPatientType()?if_exists}</td>
                    </tr>
                    <#if "INSURANCE" == cart.getPatientInfo().getPatientType()>
                        <tr>
                            <td align="right" valign="top" width="10%"><span id="patientType_title"><b> Insurance :</b> </span></td>
                            <td width="1%">&nbsp;</td>
                            <td valign="top" width="20%">
                                <#if cart.getPatientInfo().getBenefitId()?has_content>
                                    <#assign benefitPlanId = cart.getPatientInfo().getBenefitId()>

                                    <#assign patientInsuranceList = delegator.findByAnd("PatientInsurance",{"benefitPlanId", benefitPlanId})>
                                    <#assign patientInsurance = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(patientInsuranceList)>

                                    <#assign benefitPlanName = patientInsurance.benefitPlanName>
                                    <#assign benefitPlanId=patientInsurance.benefitPlanId>
                                    <#assign policyNo = patientInsurance.policyNo>
                                    <#assign healthPolicyName = patientInsurance.healthPolicyName>
                                    <#assign healthPolicyId=patientInsurance.healthPolicyId>

                                    ${healthPolicyName?if_exists} - ${policyNo?if_exists} <a href='javascript:void(0);' id="benefitPlanLink">View Plan</a>
                                    <input type="hidden" value="${benefitPlanId}" id="benefitPlanId"/>
                                <#else>
                                    &#160;&#32;
                                </#if>
                            </td>
                            <td align="right" valign="top" width="10%"><span id="patientType_title"><b> Benefit :</b> </span></td>
                            <td width="1%">&nbsp;</td>
                            <td valign="top" width="20%">
                                <#if cart.getPatientInfo().getModuleName()?has_content>
                                    ${cart.getPatientInfo().getModuleName()?if_exists}
                                <#else>
                                    &#160;&#32;
                                </#if>
                            </td>
                            <#-- Order Date -->   
                            <#if (cart.getOrderDate()?has_content)>
                                <td align="right" valign="top" width="10%><span>&nbsp;<b>Order Date:</b> </span></td>
                                <td width="1">&nbsp;</td>
                                <td valign="top" width="20%">
                                    ${cart.getOrderDate()?if_exists?string("dd/MM/yyyy")}
                                </td>
                            <#else>
                                <td align="right" valign="top" width="10%"><span>&nbsp;<b>Order Date:</b> </span></td>
                                <td width="1">&nbsp;</td>
                                <td valign="top" width="20%">
                                    ${nowTimestamp?string("dd/MM/yyyy")}
                                </td>
                            </#if>
                        </tr>
                    <#else>
                        <#-- Order Date -->   
                            <#if (cart.getOrderDate()?has_content)>
                                <td align="right" valign="top" width="10%><span>&nbsp;<b>Order Date:</b> </span></td>
                                <td width="1">&nbsp;</td>
                                <td valign="top" width="20%">
                                    ${cart.getOrderDate()?if_exists?string("dd/MM/yyyy")}
                                </td>
                            <#else>
                                <td align="right" valign="top" width="10%"><span>&nbsp;<b>Order Date:</b> </span></td>
                                <td width="1">&nbsp;</td>
                                <td valign="top" width="20%">
                                    ${nowTimestamp?string("dd/MM/yyyy")}
                                </td>
                            </#if>
                        </tr>
                    </#if>
                </table>
            </div>
        </#if>
    </div>
</div>
