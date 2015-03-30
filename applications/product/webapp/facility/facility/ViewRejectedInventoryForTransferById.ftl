<h1>View Rejected Inventory Requisition</h1>
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
        <td width="6%" align="right" nowrap="nowrap" class="label">Rejected Date</td>
        <td width="6%">&nbsp;</td>
        <td width="74%">${inventoryRequisition.requestDate?if_exists?string("dd-MM-yyyy")}</td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Rejected By</td>
        <td width="6%">&nbsp;</td>
        <#assign person = delegator.findOne("Person", {"partyId" : inventoryRequisition.createdByPartyId}, true)>
        <td width="74%">${person.firstName} ${person.lastName}</td>
    </tr>
</table>
<#if inventoryRequisitionItemList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Product</td>
          <td>Quantity</td>
          <td>Reason of Rejection</td>
          <td style="width:250px;">Action</td>
        </tr>
        <#assign alt_row = false>
        <#list inventoryRequisitionItemList as inventoryRequisitionItem>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <#assign product = delegator.findOne("Product", {"productId" : inventoryRequisitionItem.productId}, true)>
            <td>${product.productId} - ${product.description}</td>
            <td>${inventoryRequisitionItem.quantity?if_exists?number}</td>
            <#if inventoryRequisitionItem.rejectionId?exists>
            	<#assign rejectionReason = delegator.findOne("RejectionReason", {"rejectionId" : inventoryRequisitionItem.rejectionId}, true)>
            	<td>${rejectionReason.description?if_exists}</td>
            	<#else>
            	<td></td>
            </#if>
            <td>
               <a href='javascript:;' onclick="javascript:approveRequest('${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.invRequisitionId}');" class="btn-link">Receive</a>
               <#if inventoryRequisitionItem.rejectionId?exists>
	               <#if inventoryRequisitionItem.rejectionId == "SRJ_DAMAGED">
	               	/
                	<a href='javascript:;' onclick="javascript:redirectRequest('${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.invRequisitionId}');" class="btn-link">Create Physical Adjustment</a>
	               </#if>
               </#if>
            </td>
          </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>
</form>
 <script language="JavaScript" type="text/javascript">
        function approveRequest(inventoryRequisitionItem,invRequisitionId) {
           var cform = document.transferform;
           cform.action = "<@ofbizUrl>ReceiveRejectedInventory?id=</@ofbizUrl>" + inventoryRequisitionItem+"&invRequisitionId="+invRequisitionId;
           cform.submit();  
    	}
    	function redirectRequest(inventoryRequisitionItem,invRequisitionId) {
           var cform = document.transferform;
           cform.action = "<@ofbizUrl>RejectedInventoryReqPhysicalAdjustment?id=</@ofbizUrl>" + inventoryRequisitionItem+"&invRequisitionId="+invRequisitionId;
           cform.submit();  
    	}
   </script>
   