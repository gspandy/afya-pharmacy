<h1>View Inventory Requisition</h1>
<form method="post" id="transferform" name="transferform" style="margin: 0;">
<table cellspacing="0" class="basic-table">
    <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%"align="right"><span class="label">From Department</span></td>
        <td width="6%">&nbsp;</td>
        <#assign facility = delegator.findOne("Facility", {"facilityId" : inventoryRequisition.facilityIdFrom}, true)>
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
        <td width="6%" align="right" nowrap="nowrap" class="label">Transfer Date</td>
        <td width="6%">&nbsp;</td>
        <td width="74%">${inventoryRequisition.transferDate?if_exists?string("dd-MM-yyyy")}</td>
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
        <td width="6%" align="right" nowrap="nowrap" class="label">Approved By</td>
        <td width="6%">&nbsp;</td>
        <#assign person = delegator.findOne("Person", {"partyId" : inventoryRequisition.approvedByPartyId}, true)>
        <td width="74%">${person.firstName} ${person.lastName}</td>
    </tr>
	<tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Sender Note</td>
        <td width="6%">&nbsp;</td>
        <td width="74%">${inventoryRequisition.senderNote}</td>
    </tr>    
</table>
<#if inventoryRequisitionItemList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Product</td>
          <td>Quantity</td>
          <td>Accepted Quantity</td>
          <td>Rejected Quantity</td>
          <td>Cost Center</td>
          <td>Returnable</td>
          <td>Reason of Rejection</td>
        </tr>
        <#assign alt_row = false>
        <#assign rowCount = 0>
        <#list inventoryRequisitionItemList as inventoryRequisitionItem>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <#assign product = delegator.findOne("Product", {"productId" : inventoryRequisitionItem.productId}, true)>
            <td>${product.productId} - ${product.description}</td>
            <td>${inventoryRequisitionItem.quantity?if_exists?number}</td>
            <td>
            <input type="text" name="acceptedQty_o_${rowCount}" id="acceptedQty_o_${rowCount}" 
            	onchange="javascript:setAcceptedQuantity(this,${rowCount},${inventoryRequisitionItem.invReqItemId},${inventoryRequisitionItem.quantity});" 
            	value="${inventoryRequisitionItem.quantity?if_exists?number}" /> 
            </td>
            
            <td><label name="rejectedQty_o_${rowCount}" id="rejectedQty_o_${rowCount}"> 0 </label> </td>
            
            <#if inventoryRequisitionItem.glAccountCategoryId?has_content>
		        <#assign glAccountCategory = delegator.findOne("GlAccountCategory", {"glAccountCategoryId" : inventoryRequisitionItem.glAccountCategoryId}, true)>
		        <td>${glAccountCategory.description}</td>
	        	<#else>
	        	<td> </td>
       		</#if>
            <td>${inventoryRequisitionItem.returnable?if_exists}</td>
            <td>
            <#assign rejectReasons = delegator.findList("RejectionReason", null, null, null, null, false)>
                 <select name="rejectionId_o_${rowCount}" size="1" onchange="javascript:setRejectionReason(this,${rowCount},${inventoryRequisitionItem.invReqItemId});">
                    <option></option>
                    <#list rejectReasons as nextRejection>
                      <option value="${nextRejection.rejectionId}">${nextRejection.get("description",locale)?default(nextRejection.rejectionId)}</option>
                    </#list>
                  </select>
             </td>
          </tr>
            <#assign alt_row = !alt_row>
            <#assign rowCount = rowCount + 1>
        </#list>
      </table>
  </div>
</div>
</#if>
<table cellspacing="0" class="basic-table">
    <tr>
            <td>
            <input type="submit" id="accept" name="accept" value="Accept" class="btn btn-success" onClick="javascript:approveRequest(this);" />
            </td>
    </tr>
    <tr>
            <td>
            </td>
    </tr>
</table>
</form>
<script type="text/javascript" src="/images/prototypejs/prototype.js">
   </script>
 <script language="JavaScript" type="text/javascript">
 
	 function setAcceptedQuantity(selection,index,invReqItemId,quantity) {
	        var acceptedQty = "acceptedQty_o_" + index;
	        var rejectedQty = "rejectedQty_o_" + index;
	        var aceeptedQtyInt = parseInt($(acceptedQty).value);
	        
	        var quantityInt = parseInt(quantity);
	        if(quantityInt < aceeptedQtyInt){
	        	alert("Accepted Quantity can not be greater than Order Quantity ");
	        	return;
	        }
	        
	        document.getElementById(rejectedQty).innerHTML = quantityInt - aceeptedQtyInt;
	        
	        new Ajax.Updater($(acceptedQty), '/facility/control/UpdatedAcceptedQty',{parameters: {invReqItemId:invReqItemId,acceptedQty:$(acceptedQty).value}});
	    }
	 
	 function setRejectionReason(selection,index,invReqItemId){
	 	var rjectionReason = "rejectionId_o_" + index; 
	 	new Ajax.Updater(rjectionReason, '/facility/control/UpdatedRejectionMsg',{parameters: {invReqItemId:invReqItemId,rejectionReasonId:selection.value}});
	 }
    
        function approveRequest(accept) {
           accept.disabled = true;
           var cform = document.transferform;
           cform.action = "<@ofbizUrl>AcceptReqInventoryTransfer?requestId=${inventoryRequisition.invRequisitionId}</@ofbizUrl>";
           cform.submit();  
    	}
   </script>
   