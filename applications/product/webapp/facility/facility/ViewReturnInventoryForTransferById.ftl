<h1>Return Inventory Requisition</h1>
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
        <td width="6%" align="right" nowrap="nowrap" class="label">Return Date</td>
        <td width="6%">&nbsp;</td>
        <td width="74%">${inventoryRequisition.requestDate?if_exists?string("dd-MM-yyyy")}</td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Return By</td>
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
          <td>Note</td>
          <td>Location</td>
          <td></td>
        </tr>
        <#assign rowCount = 0>
        <#assign alt_row = false>
        <#list inventoryRequisitionItemList as inventoryRequisitionItem>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <#assign product = delegator.findOne("Product", {"productId" : inventoryRequisitionItem.productId}, true)>
            <td>${product.productId} - ${product.description}</td>
            <td>${inventoryRequisitionItem.quantity?if_exists?number}</td>
            <td>${inventoryRequisitionItem.note?if_exists}</td>
            <td width="25%">
                <@htmlTemplate.lookupField formName="transferform" name="locationSeqId_o_${rowCount}" id="locationSeqId_o_${rowCount}" fieldFormName="LookupFacilityLocation?facilityId=${parameters.facilityId}"/>
            </td>
            <td>
            	<input type="submit" value="Approve" class="btn btn-success" onClick="javascript:approveRequest('${inventoryRequisitionItem.invReqItemId}','${inventoryRequisitionItem.productId}',${rowCount});" />
            </td>
          </tr>
            <#assign alt_row = !alt_row>
            <#assign rowCount = rowCount + 1>
        </#list>
      </table>
  </div>
</div>
</#if>

<!-- <table cellspacing="0" class="basic-table">
    <tr>
            <td>
            <input type="submit" value="Approve" class="btn btn-success" onClick="javascript:approveRequest();" />
            </td>
    </tr>
    <tr>
            <td>
            </td>
    </tr>
</table> -->

</form>
 <script language="JavaScript" type="text/javascript">
    	function approveRequest(invReqItemId,productId,index) {
           var cform = document.transferform;
           cform.action = "<@ofbizUrl>AcceptReturnInventoryTransfer?invReqItemId=</@ofbizUrl>"+invReqItemId+"&seqId="+index+"&productId="+productId;
           cform.submit();  
    	}
   </script>
   