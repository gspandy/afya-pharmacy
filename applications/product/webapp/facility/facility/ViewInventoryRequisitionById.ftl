<h1>View Inventory Requisition</h1>
<form method="post" id="transferform" name="transferform" style="margin: 0;">
<table cellspacing="0" class="basic-table">
    <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%"align="right"><span class="label">From Department</span></td>
        <td width="6%">&nbsp;</td>
        <#assign facility = delegator.findOne("Facility", {"facilityId" : inventoryRequisition.facilityIdTo}, true)>
        <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : facility.ownerPartyId}, true)>
        <td width="74%">${partyGroup.groupName}</td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Requested Date</td>
        <td width="6%">&nbsp;</td>
        <td width="74%">${inventoryRequisition.requestDate?if_exists?string("dd-MM-yyyy")}</td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Requested By</td>
        <td width="6%">&nbsp;</td>
        <#assign person = delegator.findOne("Person", {"partyId" : inventoryRequisition.createdByPartyId}, true)>
        <td width="74%">${person.firstName} ${person.lastName}</td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Transfer Date </td>
        <td width="6%">&nbsp;</td>
        <td width="74%">
			<@htmlTemplate.renderDateTimeField name="transferDate" value="${value!''}" className="date" alert="" 
					                title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="15" maxlength="10" id="transferDate" dateType="date-time" shortDateInput=false 
					                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
					                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="transferform"/>
					                <label style="color:red;">*</label>
		</td>
    </tr>
    
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Sender Note</td>
        <td width="6%">&nbsp;</td>
        <td width="74%"><input type="text" size="70"  name="senderNote"/></td>
    </tr>
    
</table>
<#if inventoryRequisitionItemList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Product</td>
          <td>Uom</td>
          <td>Quantity</td>
          <td>Cost Center</td>
          <td>Returnable</td>
          <td>Note</td>
          <td>ATP/QOH</td>
        </tr>
        <#assign alt_row = false>
        <#list inventoryRequisitionItemList as inventoryRequisitionItem>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <#assign product = delegator.findOne("Product", {"productId" : inventoryRequisitionItem.productId}, true)>
            <td>${product.productId} - ${product.description}</td>
            <#assign productUomView = (delegator.findOne("ProductUomView", {"productId" : inventoryRequisitionItem.productId}, true))!>
            <#if productUomView?has_content>
            	<td> ${productUomView.description?if_exists} </td>
            	<#else>
            	<td></td>
            </#if>
            <td>${inventoryRequisitionItem.quantity?if_exists?number}</td>
            <#if inventoryRequisitionItem.glAccountCategoryId?has_content>
		      <#assign glAccountCategoryList = delegator.findByAnd("GlAccountCategory",null,null,false)>
		        <td>
		        	<select size="1" onchange="javascript:updateGlAccCategory(this,'${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.productId}');">
                    <option></option>
                    <#list glAccountCategoryList as next>
                      <option <#if ((next.glAccountCategoryId)?default("") == "${inventoryRequisitionItem.glAccountCategoryId}")> selected="selected"</#if>
                       value="${next.glAccountCategoryId}">${next.get("description",locale)?default(next.glAccountCategoryId)}</option>
                    </#list>
                  </select>
		        </td>
	        	<#else>
	        	<td> </td>
       		</#if>
       		<td>
	         <#-- <select size="1" onchange="javascript:updateReturnable(this,${inventoryRequisitionItem.invReqItemId},${inventoryRequisitionItem.productId});">
                	<option <#if ("No" == "${inventoryRequisitionItem.returnable}")> selected="selected"</#if> value="No">No</option>
                    <option <#if ("Yes" == "${inventoryRequisitionItem.returnable}")> selected="selected"</#if> value="Yes">Yes</option>
              </select> -->
              ${inventoryRequisitionItem.returnable}
	        </td>
            <td>${inventoryRequisitionItem.note?if_exists}</td>
            <td>${inventoryRequisitionItem.atp?if_exists}/${inventoryRequisitionItem.qoh?if_exists}</td>
          </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>
<table cellspacing="0" class="basic-table">
    <tr>
        <#if isAtpQohAvailable>
            <td ><input name="submitBtn" id="submitBtn" type="submit" value="Approve" class="btn btn-success" 
            onClick="javascript:approveRequest();" /></td>
        <#else>
            <td ><input name="submitBtn" id="submitBtn" type="submit" value="Approve" class="btn btn-success"  disabled/></td>
        </#if>
    </tr>
    <tr>
        <td colspan="3">&nbsp;</td>
    </tr>
</table>
</form>
 <script language="JavaScript" type="text/javascript">
        function approveRequest() {
           var cform = document.transferform;
           cform.action = "<@ofbizUrl>ApproveReqInventoryTransfer?requestId=${inventoryRequisition.invRequisitionId}</@ofbizUrl>";
           cform.submit();  
    	}
    	
    	function updateGlAccCategory(selection,invReqItemId,productId){
		 	new Ajax.Updater(submitBtn, '/facility/control/UpdateCostCenter',{parameters: {invReqItemId:invReqItemId,glAccountCategory:selection.value,productId:productId}});
	 	}
	 	
	 	function updateReturnable(selection,invReqItemId,productId){
		 	new Ajax.Updater(submitBtn, '/facility/control/UpdateCostCenter',{parameters: {invReqItemId:invReqItemId,returnable:selection.value,productId:productId}});
	 	}
	 	
   </script>
   