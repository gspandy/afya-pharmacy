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

<div class="screenlet order-info">
  <div class="screenlet-title-bar">
    <ul>
      <#if orderHeader.externalId?has_content>
        <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
      </#if>
      <#assign roleTypeIds = "">
      <#if partyRoles?has_content>
        <#assign roleTypeIds = partyRoles.roleTypeId>
      </#if>
      <#assign userLoginIds = "">
      <#if userLogin?has_content>
        <#assign userLoginIds = userLogin.userLoginId>
      </#if>
      <#assign orderType = orderHeader.getRelatedOne("OrderType")/>
        <li class="h3">&nbsp;${orderType?if_exists.get("description", locale)?default(uiLabelMap.OrderOrder)}&nbsp;#
            <a href="<@ofbizUrl>orderview?orderId=${orderId}</@ofbizUrl>">${orderId}</a>
            <#if orderHeader.orderTypeId == "PURCHASE_ORDER">
                [&nbsp;<a href="<@ofbizUrl>order.pdf?orderId=${orderId}&amp;orderTypeId=${orderType?if_exists.get("orderTypeId", locale)}</@ofbizUrl>" target="_blank">PDF</a>&nbsp;]
            </#if>
        </li>
        <li class="collapsed"><a onclick="javascript:toggleScreenlet(this, 'OrderInfoScreenletBody_${orderId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Expand">&nbsp;</a></li>
          <#if currentStatus.statusId == "ORDER_APPROVED" && orderHeader.orderTypeId == "SALES_ORDER">
            <!-- <li class="h3"><a href="javascript:document.PrintOrderPickSheet.submit()">${uiLabelMap.FormFieldTitle_printPickSheet}</a>
              <form name="PrintOrderPickSheet" method="post" action="<@ofbizUrl>orderPickSheet.pdf</@ofbizUrl>">
                <input type="hidden" name="facilityId" value="${storeFacilityId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderHeader.orderId?if_exists}"/>
                <input type="hidden" name="maxNumberOfOrdersToPrint" value="1"/>
              </form>
            </li> -->
          </#if>
          <#assign isSalesMgrOrMgr=false>
          <#if currentStatus.statusId == "ORDER_CREATED" || currentStatus.statusId == "ORDER_PROCESSING">
          <#-- <#if roleTypeIds='SALES_MGR' || roleTypeIds='MANAGER'> -->
            <#assign partyRoles = delegator.findByAnd("PartyRole", {"partyId" : parameters.userLogin.partyId})>
            <#list partyRoles as partyRole>
              <#if partyRole.roleTypeId='SALES_MGR' || partyRole.roleTypeId='MANAGER'> 
                <#assign isSalesMgrOrMgr=true>
              </#if>
            </#list>
            
            <#if isSalesMgrOrMgr>
              <li><a href="javascript:document.OrderApproveOrder.submit()">${uiLabelMap.OrderApproveOrder}</a>
              <form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_APPROVED"/>
                <input type="hidden" name="setItemStatus" value="Y"/>
                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
              </form>
              </li>
            </#if>
            
            <#elseif currentStatus.statusId == "ORDER_APPROVED">
              <li><a href="javascript:document.OrderHold.submit()">${uiLabelMap.OrderHold}</a>
              <form name="OrderHold" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_HOLD"/>
                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
              </form>
              </li>
            <#elseif currentStatus.statusId == "ORDER_HOLD">
              <li><a href="javascript:document.OrderApproveOrder.submit()">${uiLabelMap.OrderApproveOrder}</a>
              <form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_APPROVED"/>
                <input type="hidden" name="setItemStatus" value="Y"/>
                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
              </form>
              </li>
            </#if>
            <#if currentStatus.statusId != "ORDER_COMPLETED" && currentStatus.statusId != "ORDER_CANCELLED">
              <li><a href="javascript:document.OrderCancel.submit()">${uiLabelMap.OrderCancelOrder}</a>
              <form name="OrderCancel" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
                <input type="hidden" name="setItemStatus" value="Y"/>
                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
              </form>
              </li>
            </#if>
            <#if setOrderCompleteOption>
              <li><a href="javascript:document.OrderCompleteOrder.submit()">${uiLabelMap.OrderCompleteOrder}</a>
              <form name="OrderCompleteOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_COMPLETED"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
              </form>
              </li>
            </#if>
          <#-- <#if currentStatus.statusId == "ORDER_COMPLETED">
            <li>
              <a title="Available on order completion." href="javascript:document.GenerateTallyXml.submit()">Generate Tally XML</a>
              <form name="GenerateTallyXml" method="post" action="<@ofbizUrl>generateTallyXml</@ofbizUrl>">
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
              </form>
            </li>
            </#if>-->
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body" id="OrderInfoScreenletBody_${orderId}" style="display: none;">
        <table class="basic-table" cellspacing='0'>
            <#if orderHeader.orderName?has_content>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderOrderName} </td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">${orderHeader.orderName}</td>
            </tr>
            <tr><td colspan="3"><hr /></td></tr>
            </#if>
            <#-- order status history -->
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderStatusHistory} </td>
              <td width="5%">&nbsp;</td>
              <td width="80%" valign="top"<#if currentStatus.statusCode?has_content> class="${currentStatus.statusCode}"</#if>>
                <span class="current-status"><b>${uiLabelMap.OrderCurrentStatus} : </b>${currentStatus.get("description",locale)}</span>
                <#if orderHeaderStatuses?has_content>
                  <hr />
                  <#list orderHeaderStatuses as orderHeaderStatus>
                    <#assign loopStatusItem = orderHeaderStatus.getRelatedOne("StatusItem")>
                    <#assign userlogin = orderHeaderStatus.getRelatedOne("UserLogin")>
                    <table class="basic-table" cellspacing="0">
                      <tr>
                        <td width="80px">${loopStatusItem.get("description",locale)}</td>
                        <td width="2px">-</td>
                        <td width="80px">${orderHeaderStatus.statusDatetime?string("dd/MM/yyyy")}</td>
                        <#-- <td width="80px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeaderStatus.statusDatetime, "", locale, timeZone)!}</td> -->
                        <#-- <td width="80px">${orderHeaderStatus.statusDatetime?default("0000-00-00 00:00:00")?string}</td> -->
                        <td width="80px">${uiLabelMap.CommonBy}</td><td width="1%">-</td>
                        <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}
                        <#assign person = delegator.findOne("Person", {"partyId" : orderHeaderStatus.statusUserLogin}, true)>
                          <td width="3%">[${person.firstName?if_exists}  ${person.lastName?if_exists}]</td>-->
                        <td width="80px">[${orderHeaderStatus.statusUserLogin?if_exists}]</td>
                      </tr>
                    </table>
                  </#list>
                </#if>
              </td>
            </tr>
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderDateOrdered} </td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">${orderHeader.orderDate?if_exists?string("dd/MM/yyyy")}</td>
              <#-- <td valign="top" width="80%">${orderHeader.orderDate.toString()}</td> -->
              <#-- <td valign="top" width="80%">${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(orderHeader.orderDate)}</td> -->
            </tr>
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.CommonCurrency} </td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%"><#if orderHeader.currencyUom?exists && "KWD" == orderHeader.currencyUom>KD<#else>${orderHeader.currencyUom?default("???")}</#if></td>
            </tr>
            <#if orderHeader.internalCode?has_content>
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderInternalCode}</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">${orderHeader.internalCode}</td>
            </tr>
            </#if>
            <!-- <tr><td colspan="3"><hr /></td></tr> -->
            <!-- <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderSalesChannel}</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                  <#if orderHeader.salesChannelEnumId?has_content>
                    <#assign channel = orderHeader.getRelatedOne("SalesChannelEnumeration")>
                    ${(channel.get("description",locale))?default("N/A")}
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
              </td>
            </tr> -->
          
          <#if orderHeader.orderTypeId != "PURCHASE_ORDER">
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderProductStore} </td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                  <#if orderHeader.productStoreId?has_content>
                    <a href="/catalog/control/EditProductStore?productStoreId=${orderHeader.productStoreId}&amp;externalLoginKey=${externalLoginKey}" 
                      target="catalogmgr" class="btn-link">${orderHeader.productStoreId}</a> 
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
              </td>
            </tr>
            <tr><td colspan="3"><hr /></td></tr>
            
            <tr>
              <td align="right" valign="top" width="15%" class="label">Customer PO # </td>
              <td width="1%">&nbsp;</td>
              <td valign="top" width="80%">
                <#if customerPoNumber?has_content>
                  ${customerPoNumber}
                <#else>
                  ${uiLabelMap.CommonNotSet}
                </#if>
              </td>
            </tr>
            <tr><td colspan="3"><hr /></td></tr>
          </#if>
          
          <#if orderHeader.orderTypeId == "PURCHASE_ORDER">
            <tr><td colspan="3"><hr /></td></tr>
            <!-- <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderOriginFacility}</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                  <#if orderHeader.originFacilityId?has_content>
                    <a href="/facility/control/EditFacility?facilityId=${orderHeader.originFacilityId}${externalKeyParam}" target="facilitymgr" class="btn-link">${orderHeader.originFacilityId}</a>
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
              </td>
            </tr>
            <tr><td colspan="3"><hr /></td></tr> -->
          </#if>
          
         <#if "SALES_ORDER" == orderHeader.orderTypeId>  
           <tr>
             <td align="right" valign="top" width="15%" class="label">&nbsp;Delivery Date </td>
             <td width="5%">&nbsp;</td>
             <td valign="top" width="80%">
               <#if orderHeader.deliveryDate?has_content>
                 ${orderHeader.deliveryDate?string("dd/MM/yyyy")}
                 <#-- ${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(orderHeader.deliveryDate)} -->
               <#else>
                 ${uiLabelMap.CommonNotSet}
               </#if>
             </td>
           </tr>
           <tr><td colspan="3"><hr /></td></tr>
         </#if>
            
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.CommonCreatedBy}</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                  ${orderHeader.createdBy?if_exists}
              </td>
            </tr>
            <#if orderItem.cancelBackOrderDate?exists>
              <tr><td colspan="3"><hr /></td></tr>
              <tr>
                <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.FormFieldTitle_cancelBackOrderDate}</td>
                <td width="5%">&nbsp;</td>
                <td valign="top" width="80%">${orderItem.cancelBackOrderDate?if_exists?string("dd/MM/yyyy")}</td>
              </tr>
            </#if>
            
            <#if orderHeader.formToIssue?has_content>
            
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Form to Issue</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToIssue?if_exists}
              </td>
            </tr>
            <#-- <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Form Series No</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToIssueSeriesNo?if_exists}
              </td>
            </tr>
            
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Form No</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToIssueFormNo?if_exists}
              </td>
            </tr>
            
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Issue Form Date</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToIssueDate?if_exists}
              </td>
            </tr> -->
            
            </#if>
            
            
            <#if orderHeader.formToReceive?has_content>
            
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Form to Receive </td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToReceive?if_exists}
              </td>
            </tr>
            
            <#-- <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Form Series No</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToReceiveFormNo?if_exists}
              </td>
            </tr>
            
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Form No</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToReceiveSeriesNo?if_exists}
              </td>
            </tr>
            
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;Form Date</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                ${orderHeader.formToReceiveDate?if_exists}
              </td>
            </tr> -->
            
            </#if>
            
            <#if distributorId?exists>
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderDistributor}</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                <#assign distPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", distributorId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                ${distPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
              </td>
            </tr>
            </#if>
            <#if affiliateId?exists>
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderAffiliate}</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                <#assign affPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", affiliateId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                ${affPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
              </td>
            </tr>
            </#if>
            <#if orderContentWrapper.get("IMAGE_URL")?has_content>
            <tr><td colspan="3"><hr /></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.OrderImage}</td>
              <td width="5%">&nbsp;</td>
              <td valign="top" width="80%">
                  <a href="<@ofbizUrl>viewimage?orderId=${orderId}&amp;orderContentTypeId=IMAGE_URL</@ofbizUrl>" target="_orderImage" class="buttontext">${uiLabelMap.OrderViewImage}</a>
              </td>
            </tr>
            </#if>
            
            <!-- <#if "SALES_ORDER" == orderHeader.orderTypeId>
              <tr><td colspan="3"><hr /></td></tr>
              <tr>
                <td align="right" valign="top" width="15%" class="label">&nbsp;${uiLabelMap.FormFieldTitle_priority}</td>
                <td width="5%">&nbsp;</td>
                <td valign="top" width="80%">
                  <form action="setOrderReservationPriority" method="post" name="setOrderReservationPriority">
                    <input type = "hidden" name="orderId" value="${orderId}"/>
                    <select name="priority">
                      <option value="1" <#if (orderHeader.priority)?if_exists == "1">selected="selected" </#if>>${uiLabelMap.CommonHigh}</option>
                      <option value="2" <#if (orderHeader.priority)?if_exists == "2">selected="selected" <#elseif !(orderHeader.priority)?has_content>selected="selected"</#if>>${uiLabelMap.CommonNormal}</option>
                      <option value="3" <#if (orderHeader.priority)?if_exists == "3">selected="selected" </#if>>${uiLabelMap.CommonLow}</option>
                    </select>
                    <input type="submit" class="btn btn-primary" value="${uiLabelMap.FormFieldTitle_reserveInventory}"/>
                  </form>
                </td>
              </tr>
              <tr><td colspan="3"><hr /></td></tr>
            </#if> -->
            
            <#if orderHeader.orderTypeId == "PURCHASE_ORDER">
              <#if orderHeader.isViewed?has_content && orderHeader.isViewed == "Y">
                <tr><td colspan="3"><hr /></td></tr>
                <tr>
                  <td class="label">${uiLabelMap.OrderViewed}</td>
                  <td width="5%"></td>
                  <td valign="top" width="80%">
                    ${uiLabelMap.CommonYes}
                  </td>
                </tr>
              <#else>
                <tr><td colspan="3"><hr /></td></tr>
                <tr id="isViewed">
                  <td class="label">${uiLabelMap.OrderMarkViewed}</td>
                  <td width="5%"></td>
                  <td valign="top" width="80%">
                    <form id="orderViewed" action="">
                      <input type="checkbox" name="checkViewed" onclick="javascript:markOrderViewed();"/>
                      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                      <input type="hidden" name="isViewed" value="Y"/><span><font color="red">&nbsp;*Once checked can not be reverted*</font></span>
                    </form>
                  </td>
                </tr>
                <tr id="viewed" style="display: none;">
                  <td class="label">${uiLabelMap.OrderViewed}</td>
                  <td width="5%"></td>
                  <td valign="top" width="80%">
                    ${uiLabelMap.CommonYes} 
                  </td>
                </tr>
              </#if>
            </#if>
            
            <!-- Earlier it was Mark View for both PO & SO , now it changed to Contract Reviewed for SO -->
            <#if orderHeader.orderTypeId == "SALES_ORDER">
              <#if orderHeader.isViewed?has_content && orderHeader.isViewed == "Y">
                <tr><td colspan="3"><hr /></td></tr>
                <tr>
                  <td class="label">Contract Reviewed</td>
                  <td width="5%"></td>
                  <td valign="top" width="80%">
                    ${uiLabelMap.CommonYes}
                  </td>
                </tr>
              <#else>
                <tr><td colspan="3"><hr /></td></tr>
                <tr id-name="isViewed" id="isViewed">
                  <td class="label">Contract Reviewed</td>
                  <td width="5%"></td>
                  <td valign="top" width="80%">
                    <form id="orderViewed" action="">
                      <input type="checkbox" name="checkViewed" onclick="javascript:markOrderViewed();"/>
                      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                      <input type="hidden" name="isViewed" value="Y"/><span><font color="red">&nbsp;*Once checked can not be reverted*</font></span>
                    </form>
                  </td>
                </tr>
                <tr id-name="viewed" id="viewed" style="display: none;">
                  <td class="label">Contract Reviewed</td>
                  <td width="5%"></td>
                  <td valign="top" width="80%">
                    ${uiLabelMap.CommonYes}
                  </td>
                </tr>
              </#if>
            </#if>
        </table>
    </div>
</div>
