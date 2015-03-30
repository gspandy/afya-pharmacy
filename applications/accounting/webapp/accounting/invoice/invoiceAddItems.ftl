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

<ul class="nav nav-pills">
	<#if invoice?has_content>
	<div style="float:right">
		<a class="btn btn-link" id="add_item"><i class="icon-plus"></i>Add Invoice Item</a>
	</div>
	</#if>
</ul>
<div id="list_addItem" style="display:none">
	<div class="screenlet">
		<div class="screenlet-title-bar">
			<ul>
				<li class="h3">Add a new invoice Item</li>
			</ul>
			<br class="clear" />
		</div>
		<div id="_col" class="screenlet-body">
			<form method="post" action="/accounting/control/createInvoiceItem" id="CreateInvoiceItem" class="basic-form"
				onsubmit="javascript:submitFormDisableSubmits(this)" name="CreateInvoiceItem">
				<input type="hidden" name="invoiceId" value="${invoice.invoiceId}" id="CreateInvoiceItem_invoiceId" />
				<table cellspacing="0" class="basic-table">
					<tr>
						<td class=""><div>${uiLabelMap.AccountingInvoiceItemType}</div></td>
						<td>
							<select name="invoiceItemTypeId" id="CreateInvoiceItem_Invoice Item Type" size="1">
								<#list invoiceItemTypes as invItmType>
									<option value="${invItmType.invoiceItemTypeId}">${invItmType.get("description",locale)?default(invItmType.invoiceItemTypeId)}</option>
								</#list>
							</select>
						</td>
						<td class=""><div>Override Gl Account Id</div></td>
						<td>
							<@htmlTemplate.lookupField formName="CreateInvoiceItem" name="overrideGlAccountId" id="CreateInvoiceItem_overrideGlAccountId" fieldFormName="LookupGlAccOrgAndClass"/>
							<script language="JavaScript" type="text/javascript">ajaxAutoCompleter('CreateInvoiceItem_overrideGlAccountId,<@ofbizUrl>LookupGlAccOrgAndClass</@ofbizUrl>,ajaxLookup=Y&amp;searchValueField=CreateInvoiceItem_overrideGlAccountId', true);</script>
						</td>
						<#-- <td class="">
							<span id="CreateInvoiceItem_Override Gl Account Id_title">Override Gl Account Id</span>
						</td>
						<td>
							<span class="ui-widget">
								<select name="overrideGlAccountId" id="CreateInvoiceItem_Override Gl Account Id"
									size="1">
									<option value="">&nbsp;</option>
									<#list glAccountOrganizationAndClassList as glAcc>
										<option value="${glAcc.glAccountId}">${glAcc.glAccountId?if_exists} ${glAcc.accountName?if_exists}</option>
									</#list>
								</select>
							</span>
						</td> -->
					</tr>
					<tr>
						<td class=""><div>${uiLabelMap.ProductProductId}</div></td>
						<td>
							<@htmlTemplate.lookupField formName="CreateInvoiceItem" name="productId" id="CreateInvoiceItem_productId" fieldFormName="LookupProduct"/>
							<script language="JavaScript" type="text/javascript">ajaxAutoCompleter('CreateInvoiceItem_productId,<@ofbizUrl>LookupProduct</@ofbizUrl>,ajaxLookup=Y&amp;searchValueField=CreateInvoiceItem_productId', true);</script>
						</td>
						<td class=""><div>${uiLabelMap.FormFieldTitle_productFeatureId}</div></td>
						<td>
							<@htmlTemplate.lookupField formName="CreateInvoiceItem" name="productFeatureId" id="CreateInvoiceItem_productFeatureId" fieldFormName="LookupProductFeature"/>
							<script language="JavaScript" type="text/javascript">ajaxAutoCompleter('CreateInvoiceItem_productFeatureId,<@ofbizUrl>LookupProductFeature</@ofbizUrl>,ajaxLookup=Y&amp;searchValueField=CreateInvoiceItem_productFeatureId', true);</script>
						</td>
					</tr>
					<tr>
						<td class=""><div>${uiLabelMap.ProductQuantity}</div></td>
						<td>
							<input type="text" name="quantity" size="10" id="CreateInvoiceItem_Quantity"/>
						</td>
						<td class="">
							<span id="CreateInvoiceItem_uomId_title">U O M</span>
						</td>
						<td>
							<span class="ui-widget">
								<select name="uomId" id="CreateInvoiceItem_uomId" size="1">
									<option value="">&nbsp;</option>
									<#list uomMeasure as uomGen>
										<option value="${uomGen.uomId}">${uomGen.description?if_exists} - ${uomGen.abbreviation?if_exists}</option>
									</#list>
								</select>
							</span>
						</td>
					</tr>
					<tr>
						<td class=""><div>${uiLabelMap.AccountingUnitPrice}</div></td>
						<td>
							<input type="text" name="amount" size="10" id="CreateInvoiceItem_UnitPrice"/>
						</td>
						<td class="">
							<span id="CreateInvoiceItem_projectName_title">Project</span>
						</td>
						<td>
						<select name="projectName" id="CreateInvoiceItem_projectName">
								<option value="">&nbsp;</option>
								<#list invoiceProjectList as project>
									<option value="${project.workEffortId}">${project.get("workEffortName",locale)?default(project.workEffortId)}</option>
								</#list>
						</select>
						</td>
					</tr>
					<tr>
						<td class=""><div>${uiLabelMap.ProductInventoryItemId}</div></td>
						<td>
							<input type="text" name="inventoryItemId" size="25" id="CreateInvoiceItem_InventoryItemId"/>
						</td>
						<td class=""><div>Description</div></td>
						<td>
							<textarea name="description" cols="60" rows="3" id="CreateInvoiceItem_Description"></textarea>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td colspan="4">
							<input type="button" class="btn btn-success" name="addButton" id="add_btn" value="Add" 
								onclick="ajaxSubmitFormUpdateAreas('CreateInvoiceItem', 'EditInvoiceItemsId,/accounting/control/EditInvoiceItems,invoiceId=${invoiceId},CreateInvoiceItemId,/accounting/control/CreateInvoiceItem,invoiceId=${invoiceId}')" />
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</div>
</div>
<script language="JavaScript" type="text/javascript">
	jQuery('#add_item').click(function() {
	if ($('#list_addItem').css('display') == 'none')
	jQuery('#list_addItem').show(); });
	jQuery('#add_btn').click(function() {
	jQuery('#CreateInvoiceItem').each(function(){
	this.reset(); });
	jQuery('#list_addItem').hide(); });
</script>
