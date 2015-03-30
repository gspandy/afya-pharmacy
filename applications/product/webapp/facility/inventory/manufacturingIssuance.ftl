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
<h1>${title}</h1>
            <form method="post" action="<@ofbizUrl>InventoryItemIssuance?<#if parameters.productIdFinishGood?exists>productIdFinishGood=${parameters.productIdFinishGood}</#if></@ofbizUrl>" name="transferform" id="transferform" style="margin: 0;">
            <script language="JavaScript" type="text/javascript">
                function setNow(field) { eval('document.transferform.' + field + '.value="${nowTimestamp}"'); }
            </script>

            <table cellspacing="0" class="basic-table">
            <input type="hidden" name="inventoryItemId" value="${inventoryItemId?if_exists}" />
            <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
            <input type="hidden" name="locationSeqId" value="${(inventoryItem.locationSeqId)?if_exists}" />
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%"align="right"><span class="label">${uiLabelMap.ProductInventoryItemId}</span></td>
                <td width="6%">&nbsp;</td>
                <td width="74%">${inventoryItemId}</td>
            </tr>
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductInventoryItemTypeId}</td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                <#if inventoryItemType?exists>
                    ${(inventoryItemType.get("description",locale))?if_exists}
                </#if>
                </td>
            </tr>
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">${uiLabelMap.ProductProductId}</span></td>
                <td width="6%">&nbsp;</td>
                <td width="74%">
                    <#if inventoryItem?exists && (inventoryItem.productId)?exists>
                        <a name="productId" href="/catalog/control/EditProduct?productId=${(inventoryItem.productId)?if_exists}" class="btn-link">${(inventoryItem.productId)?if_exists}</a>
                        <input hidden="true" name="issuanceProductId" value="${(inventoryItem.productId)?if_exists}"/>
                    </#if>
                </td>
            </tr>
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">${uiLabelMap.CommonStatus}</span></td>
                <td width="6%">&nbsp;</td>
                <td width="74%">${(inventoryStatus.get("description",locale))?default("--")}</td>
            </tr>

            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">${uiLabelMap.ProductComments}</span></td>
                <td width="6%">&nbsp;</td>
                <td width="74%">${(inventoryItem.comments)?default("--")}</td>
            </tr>

            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">${uiLabelMap.ProductSerialAtpQoh}</span></td>
                <td width="6%">&nbsp;</td>
                <#if inventoryItem?exists && inventoryItem.inventoryItemTypeId.equals("NON_SERIAL_INV_ITEM")>
                    <td width="74%">
                        ${(inventoryItem.availableToPromiseTotal)?if_exists}&nbsp;
                        /&nbsp;${(inventoryItem.quantityOnHandTotal)?if_exists}
                    </td>
                <#elseif inventoryItem?exists && inventoryItem.inventoryItemTypeId.equals("SERIALIZED_INV_ITEM")>
                    <td width="74%">${(inventoryItem.serialNumber)?if_exists}</td>
                <#elseif inventoryItem?exists>
                    <td class="alert" width="74%">${uiLabelMap.ProductErrorType} ${(inventoryItem.inventoryItemTypeId)?if_exists} ${uiLabelMap.ProductUnknownSpecifyType}.</td>
                </#if>
            </tr>
        <tr>
            <td width="14%">&nbsp;</td>
            <td colspan="3"><hr /></td>
        </tr>
        <tr>
           <td width="14%">&nbsp;</td>
            <td width="6%" align="right" nowrap="nowrap"><span class="label">Issue Date</span></td>
            <td width="6%">&nbsp;</td>
	            <td width="74%">
                <@htmlTemplate.renderDateTimeField name="issuanceDate" value="${value!''}" className="date" alert="" 
                title="Format: MM/dd/yyyy" size="15" maxlength="10" id="thruDate" dateType="date-Time" shortDateInput=true 
                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="transferform"/>
	                <font color=red>*</font>
	                <span class="tooltip">Required</span>
	                </a>
	            </td>
        </tr>
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">Fixed Asset</span></td>
                <td width="6%">&nbsp;</td>
                <#if manufacturingIssuance?exists>
		            <td width="74%">
			            <label>${(manufacturingIssuance.fixedAssetId)?if_exists}</label>
		            </td>
            	<#else>
	                <td width="74%">
	                    <@htmlTemplate.lookupField formName="transferform" name="fixedAssetId" id="locationSeqIdTo" fieldFormName="LookupFixedAsset"/>
	                </td>
	            </#if>
            </tr>
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">Product</span></td>
                <td width="6%">&nbsp;</td>
                <#if manufacturingIssuance?exists>
		            <td width="74%">
			            <label>${(manufacturingIssuance.productId)?if_exists}</label>
		            </td>
            	<#else>
                <td width="74%">
                	<#if productIdFinishGood?has_content>
                	<div style="margin-left:6px;">
                	<label>${fmProductDes?if_exists}</label>
                	</div>
                	<input type="hidden" name="productId" value="${productIdFinishGood?if_exists}" />
                	<#else>
                  	<@htmlTemplate.lookupField formName="transferform" name="productId" id="productId" fieldFormName="LookupProduct"/>
                  	</#if>
                </td>
                </#if>
            </tr>
             <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">Serial Number</span></td>
                <td width="6%">&nbsp;</td>
                <#if manufacturingIssuance?exists>
		            <td width="74%">
			            <label>${(manufacturingIssuance.serialNumber)?if_exists}</label>
		            </td>
            	<#else>
                <td width="74%">
                	<input type="text" name="serialNumber" />
                </td>
                </#if>
            </tr>
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">Quantity To Issue</span></td>
                <td width="6%">&nbsp;</td>
                <#if manufacturingIssuance?exists>
		            <td width="74%">
			            <label>${(manufacturingIssuance.issuanceQty)?if_exists}</label>
		            </td>
            	<#else>
                <td width="74%">
                	<input type="text" name="issuanceQty" />
                </td>
                </#if>
            </tr>
            <tr>
                <td width="14%">&nbsp;</td>
                <td width="6%" align="right" nowrap="nowrap"><span class="label">Comments</span></td>
                <td width="6%">&nbsp;</td>
                <#if manufacturingIssuance?exists>
		            <td width="74%">
			            <label>${(manufacturingIssuance.description)?if_exists}</label>
		            </td>
            	<#else>
                <td width="74%">
                	<input type="text" name="description" size="60" maxlength="250" />
                </td>
                </#if>
            </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
            <#if !manufacturingIssuance?exists>
            	<td colspan="1"><input type="submit" value="Issue" class="btn btn-success"/></td>
            </#if>
        </tr>
        </table>
        <script>
      		var form = document.transferform;
     		jQuery(form).validate();
      </script>
        </form>
