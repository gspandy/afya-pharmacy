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

<#assign shoppingCartOrderType = "">
<#assign shoppingCartProductStore = "NA">
<#assign shoppingCartChannelType = "">
 <#assign type = parameters.type?if_exists>
<#if shoppingCart?exists>
  <#assign shoppingCartOrderType = shoppingCart.getOrderType()>
  <#assign shoppingCartProductStore = shoppingCart.getProductStoreId()?default("NA")>
  <#assign shoppingCartChannelType = shoppingCart.getChannelType()?default("")>
<#else>
<#-- allow the order type to be set in parameter, so only the appropriate section (Sales or Purchase Order) shows up -->
  <#if parameters.orderTypeId?has_content>
    <#assign shoppingCartOrderType = parameters.orderTypeId>
  </#if>
</#if>
<#assign roleType = "">
<#if roleTypeNew?has_content>
 <#list roleTypeNew as roleTypeNewList>
 <#assign roleType = roleTypeNewList.roleTypeId>
   </#list>
</#if>
<#assign userLoginIds = "">
<#if userLogin?has_content>
 <#assign userLoginIds = userLogin.userLoginId>
</#if>
<#assign roleTypePurchaseOrder = "">
<#if roleTypePurchase?has_content>
<#list roleTypePurchase as roleTypePurchaseList>
 <#assign roleTypePurchaseOrder = roleTypePurchaseList.roleTypeId>
   </#list>
</#if>
<!-- Sales Order Entry -->
<#if roleType?has_content || userLoginIds='admin'>
<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<#if shoppingCartOrderType != "PURCHASE_ORDER" && type == "SALES_ORDER">
<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.OrderSalesOrder}<#if shoppingCart?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if></li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
      <form method="post" action="<@ofbizUrl>initorderentry?type=${parameters.type}</@ofbizUrl>" name="salesentryform" >
      <input type="hidden" name="originOrderId" value="${parameters.originOrderId?if_exists}"/>
      <input type="hidden" name="finalizeMode" value="type"/>
      <input type="hidden" name="orderMode" value="SALES_ORDER"/>
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
       <tr>
          <td>&nbsp;</td>
          <td class="field-title" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>Order Flow</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
 				<select name="orderFlow">
      				<option value="Simple">Simple</option>
      				<option value="Advance">Advanced</option>
      			</select>
             </div>
          </td>
        </tr>
        <tr>
          <td >&nbsp;</td>
          <td class="field-title" width="300" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.ProductProductStore}</div></td>
          <td >&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="productStoreId"<#if sessionAttributes.orderMode?exists> disabled</#if>>
                <#assign currentStore = shoppingCartProductStore>
                <#if defaultProductStore?has_content>
                   <option value="${defaultProductStore.productStoreId}">${defaultProductStore.storeName?if_exists}</option>
                   <option value="${defaultProductStore.productStoreId}">----</option>
                </#if>
                <#list productStores as productStore>
                  <option value="${productStore.productStoreId}"<#if productStore.productStoreId == currentStore> selected="selected"</#if>>${productStore.storeName?if_exists}</option>
                </#list>
              </select>
              <#if sessionAttributes.orderMode?exists>${uiLabelMap.OrderCannotBeChanged}</#if>
            </div>
          </td>
        </tr>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <input value='${parameters.userLogin.userLoginId}' name="userLoginId" type="hidden"/>
         <tr>
          <td>&nbsp;</td>
          <td class="field-title" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.OrderCustomer} <span><font color="red">*</font></div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField name="partyId" formName="salesentryform" fieldFormName="LookupCustomerName" value='${thisPartyId?if_exists}' className="required" id="partyId" />
              <span class="tooltip">${uiLabelMap.CommonRequired}</span></span>
            </div>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td class="field-title" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>Voucher Type</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
             <select name="soVoucherType">
             <option value="Sales">Sales</option>
                <#list voucherTypeSO as voucherType>
                  <option value="${voucherType.voucherName}"> ${voucherType.voucherName} </option>
                </#list>
              </select>
               <span class="tooltip">${uiLabelMap.CommonRequired}</span>
            </div>
          </td>
        </tr>
        <tr>
        		<td colspan="3"></td>
        		<td>
        			<input type="submit" class="btn btn-success" value="${uiLabelMap.CommonContinue}"/>
        		</td>
        </tr>
      </table>
      <script>
      		var form = document.salesentryform;
     		jQuery(form).validate();
      </script>
      </form>
  </div>
</div>
</#if>
</#if>
</#if>
<br />
<!-- Purchase Order Entry -->
<#if roleTypePurchaseOrder?has_content || userLoginIds='admin'>
<#if security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session)>
  <#if shoppingCartOrderType != "SALES_ORDER" && type =="PURCHASE_ORDER">
  <div class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">${uiLabelMap.OrderPurchaseOrder}<#if shoppingCart?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if></li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
      <form method="post" name="poentryform" action="<@ofbizUrl>initorderentry?type=${parameters.type}</@ofbizUrl>">
      <input type='hidden' name='finalizeMode' value='type'/>
      <input type='hidden' name='orderMode' value='PURCHASE_ORDER'/>
      <table width="100%" border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
       <tr>
          <td>&nbsp;</td>
          <td class="field-title" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>Order Flow</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
 				<select name="orderFlow">
      				<option value="Simple">Simple</option>
      				<option value="Advance">Advanced</option>
      			</select>
             </div>
          </td>
        </tr>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <tr>
          <td>&nbsp;</td>
          <td class="field-title" width="300" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.OrderOrderEntryInternalOrganization}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="billToCustomerPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> disabled</#if>>
                  <#list organizationList as organization>
                  <#assign organizationName = organization.groupName/>
                  <option value="${organization.partyId}">${organization.partyId} - ${organizationName}</option>
                  </#list>
              </select>
            </div>
          </td>
        </tr>
        <input value='${parameters.userLogin.userLoginId}' name="userLoginId" type="hidden"/>
        <#-- <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.PartySupplier}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="supplierPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> disabled</#if>>
                <option value="">${uiLabelMap.OrderSelectSupplier}</option>
                <#list suppliers as supplier>
                  <option value="${supplier.partyId}" <#if supplier.partyId == thisPartyId> selected="selected"</#if>>[${supplier.partyId}] - ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(supplier, true)}</option>
                </#list>
              </select>
               <span class="tooltip">${uiLabelMap.CommonRequired}</span>
            </div>
          </td>
        </tr> -->
        <tr>
          <td>&nbsp;</td>
          <td class="field-title" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.PartySupplier} <span><font color="red">*</font></div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField name="supplierPartyId" formName="poentryform" fieldFormName="LookupSupplierName" className="required" id="supplierPartyId" />
              <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('supplierPartyId,<@ofbizUrl>LookupSupplierName</@ofbizUrl>,ajaxLookup=Y&amp;searchValueField=supplierPartyId', true);</script>
              <span class="tooltip">${uiLabelMap.CommonRequired}</span></span>
            </div>
          </td>
        </tr>
        
        <tr>
          <td>&nbsp;</td>
          <td class="field-title" align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>Voucher Type</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
            
             <select name="poVoucherType">
             	 <option value="Purchase">Purchase</option>
                <#list voucherTypePO as voucherType>
                  <option value="${voucherType.voucherName}" > ${voucherType.voucherName} </option>
                </#list>
              </select>
               <span class="tooltip">${uiLabelMap.CommonRequired}</span>
            </div>
          </td>
        </tr>
        <tr>
        		<td colspan="3"></td>
        		<td>
        			<input type="submit" class="btn btn-success" value="${uiLabelMap.CommonContinue}"/>
        		</td>
        </tr>
      </table>
      <script>
      		var form = document.poentryform;
     		jQuery(form).validate();
      </script>
      </form>
    </div>
  </div>
  </#if>
</#if>
</#if>
