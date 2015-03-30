<form method="post" id="transferform" name="transferform" style="margin: 0;">
<#if requestItemList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Product</td>
          <td>Quantity</td>
          <td>Cost Center</td>
          <td>Returnable</td>
          <td>Note</td>
          <td>ATP/QOH</td>
          <td>Action</td>
        </tr>
        <#assign alt_row = false>
        <#assign rowCount = 0>
        <#list requestItemList as inventoryRequisitionItem>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <#assign product = delegator.findOne("Product", {"productId" : inventoryRequisitionItem.productId}, true)>
            <td>${product.productId} - ${product.description}</td>
            <#assign productUomView = (delegator.findOne("ProductUomView", {"productId" : inventoryRequisitionItem.productId}, true))!>
            <#if productUomView?has_content>
		     <td>
		     <#if inventoryRequisitionItem.invReqItemId?has_content>
		        <input type="text" name="modifyQty_o_${rowCount}" id="modifyQty_o_${rowCount}" value="${inventoryRequisitionItem.quantity?if_exists?number}" 
            	onchange="javascript:updateQuantity(${rowCount},'${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.productId}');">  </input>    ${productUomView.description?if_exists}
            	<#else>
            	<input type="text" name="modifyQty_o_${rowCount}" id="modifyQty_o_${rowCount}" value="${inventoryRequisitionItem.quantity?if_exists?number}" 
            	onchange="javascript:updateQuantity(${rowCount},null,'${inventoryRequisitionItem.productId}');">  </input>    ${productUomView.description?if_exists}
            </#if>	
            	</td>
            <#else>
            	<td>
            	<#if inventoryRequisitionItem.invReqItemId?has_content>
            	<input type="text" name="modifyQty_o_${rowCount}" id="modifyQty_o_${rowCount}" value="${inventoryRequisitionItem.quantity?if_exists?number}" 
            	onchange="javascript:updateQuantity(${rowCount},'${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.productId}');">  </input>
            	<#else>
            	<input type="text" name="modifyQty_o_${rowCount}" id="modifyQty_o_${rowCount}" value="${inventoryRequisitionItem.quantity?if_exists?number}" 
            	onchange="javascript:updateQuantity(${rowCount},null,'${inventoryRequisitionItem.productId}');">  </input>
            	</#if>
            	</td>
            </#if>
            <#assign glAccountCategoryList = (delegator.findByAnd("GlAccountCategory", {"glAccountCategoryTypeId" : "COST_CENTER"},null, true))!>
            <#assign glAccountCategory = (delegator.findOne("GlAccountCategory", {"glAccountCategoryId" : inventoryRequisitionItem.glAccountCategoryId}, true))!>
            <#if glAccountCategory?has_content>
            	<td>
            	 <#if inventoryRequisitionItem.invReqItemId?has_content>
            	 <select name="glAccountCategory_o_${rowCount}" size="1" onchange="javascript:updateGlAccCategory(this,${rowCount},'${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.productId}');">
                    <option></option>
                    <#list glAccountCategoryList as next>
                      <option <#if ((next.glAccountCategoryId)?default("") == "${inventoryRequisitionItem.glAccountCategoryId}")> selected="selected"</#if>
                       value="${next.glAccountCategoryId}">${next.get("description",locale)?default(next.glAccountCategoryId)}</option>
                    </#list>
                  </select>
                  <#else>
                  <select name="glAccountCategory_o_${rowCount}" size="1" onchange="javascript:updateGlAccCategory(this,${rowCount},null,'${inventoryRequisitionItem.productId}');">
                    <option></option>
                    <#list glAccountCategoryList as next>
                      <option <#if ((next.glAccountCategoryId)?default("") == "${inventoryRequisitionItem.glAccountCategoryId}")> selected="selected"</#if>
                       value="${next.glAccountCategoryId}">${next.get("description",locale)?default(next.glAccountCategoryId)}</option>
                    </#list>
                  </select>
                  </#if>
                  </td>
            	<#else>
            	<td></td>
            </#if>
            
            <td>
            	 <#-- <select name="returnable_o_${rowCount}" size="1" onchange="javascript:updateReturnable(this,${rowCount},${inventoryRequisitionItem.invReqItemId},${inventoryRequisitionItem.productId});">
                    <option <#if ("No" == "${inventoryRequisitionItem.returnable}")> selected="selected"</#if> value="No">No</option>
                    <option <#if ("Yes" == "${inventoryRequisitionItem.returnable}")> selected="selected"</#if> value="Yes">Yes</option>
                 </select> -->
                 ${inventoryRequisitionItem.returnable}
            </td>
            
            <td>
            <#if inventoryRequisitionItem.invReqItemId?has_content>
            	<input type="text" name="note_o_${rowCount}" id="note_o_${rowCount}" value="${inventoryRequisitionItem.note?if_exists}" onchange="javascript:updateNote(${rowCount},'${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.productId}');"/>
            	<#else>
            	<input type="text" name="note_o_${rowCount}" id="note_o_${rowCount}" value="${inventoryRequisitionItem.note?if_exists}" onchange="javascript:updateNote(${rowCount},null,'${inventoryRequisitionItem.productId}');"/>
            </#if> 
            </td>
            <td>
            <#assign atpQohMap=Static["org.ofbiz.product.product.ProductWorker"].getAtpQoh(dispatcher,userLogin,inventoryRequisitionItem.productId,headerMap.facilityIdFrom)/>
             ${atpQohMap.atp?if_exists}/${atpQohMap.qoh?if_exists}
            </td>
            <td>
            	<input type="submit" value="Remove" class="btn btn-danger btn-mini" onClick="javascript:removeRequestItem('${inventoryRequisitionItem.productId}');" />
            </td>
          </tr>
            <#assign alt_row = !alt_row>
            <#assign rowCount = rowCount + 1>
        </#list>
      </table>
  </div>
</div>
</#if>
</form>

<script language="JavaScript" type="text/javascript">

	   function updateQuantity(index,invReqItemId,productId) {
	        var modifyQty = "modifyQty_o_" + index;
	        new Ajax.Updater($(modifyQty), '/facility/control/UpdateInvRequisitionItem',{parameters: {invReqItemId:invReqItemId,modifyQty:$(modifyQty).value,productId:productId}});
	    }
	    
	   function updateNote(index,invReqItemId,productId) {
	        var note = "note_o_" + index;
	        new Ajax.Updater($(note), '/facility/control/UpdateInvRequisitionItem',{parameters: {invReqItemId:invReqItemId,note:$(note).value,productId:productId}});
	    }
	    
	    function updateGlAccCategory(selection,index,invReqItemId,productId){
		 	var glAccountCategory = "glAccountCategory_o_" + index; 
		 	new Ajax.Updater(glAccountCategory, '/facility/control/UpdateInvRequisitionItem',{parameters: {invReqItemId:invReqItemId,glAccountCategory:selection.value,productId:productId}});
	 	}
	 	
	 	function updateReturnable(selection,index,invReqItemId,productId){
		 	var returnable = "returnable_o_" + index; 
		 	new Ajax.Updater(returnable, '/facility/control/UpdateInvRequisitionItem',{parameters: {invReqItemId:invReqItemId,returnable:selection.value,productId:productId}});
	 	}
    
	    
    	function removeRequestItem(productId) {
 		   var cform = document.transferform;
           cform.action = "<@ofbizUrl>ClearRequestInventoryProduct?productId=</@ofbizUrl>"+productId;
           cform.submit();
    	}
    	
</script>
