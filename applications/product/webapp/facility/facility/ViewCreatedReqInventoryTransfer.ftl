<h1>Inventory Requisition Detail</h1>
<form method="post" id="transferform" name="transferform" style="margin: 0;" action="requisitionIssueOrReceive">
<table cellspacing="0" class="basic-table">
    <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
    <input type="hidden" name="invRequisitionId" value="${inventoryRequisition.invRequisitionId}" />
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%"align="right"><span class="label">Type</span></td>
        <td width="6%">&nbsp;</td>
        <td width="74%">${inventoryRequisition.requestType}</td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%"align="right"><span class="label">Status</span></td>
        <td width="6%">&nbsp;</td>
        <td width="74%">${inventoryRequisition.status}</td>
    </tr>
     <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">From Department</td>
        <td width="6%">&nbsp;</td>
             <#assign facility = delegator.findOne("Facility", {"facilityId" : inventoryRequisition.facilityIdFrom}, true)>
	         <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : facility.ownerPartyId}, true)>
	         <td width="74%">${partyGroup.groupName}</td>
         
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">To Department</td>
        <td width="6%">&nbsp;</td>
         <#assign facility = delegator.findOne("Facility", {"facilityId" : inventoryRequisition.facilityIdTo}, true)>
         <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : facility.ownerPartyId}, true)>
         <td width="74%">${partyGroup.groupName}</td>
    </tr>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Requested By</td>
        <td width="6%">&nbsp;</td>
        <#assign person = delegator.findOne("Person", {"partyId" : inventoryRequisition.createdByPartyId}, true)>
        <td width="74%">${person.firstName} ${person.lastName}</td>
    </tr>
    <#if inventoryRequisition.requestDate?has_content>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Requested Date</td>
        <td width="6%">&nbsp;</td>
        <td>${inventoryRequisition.requestDate?if_exists?string("dd-MM-yyyy")}</td>
    </tr>
    </#if>
    <#if inventoryRequisition.transferDate?has_content>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Transfered Date</td>
        <td width="6%">&nbsp;</td>
        <td>${inventoryRequisition.transferDate?if_exists?string("dd-MM-yyyy")}</td>
    </tr>
    </#if>
<#if inventoryRequisition.status == "Requested">
    <#else> 
<#if inventoryRequisition.status == "GM Approved">
    <#else> 
<#if inventoryRequisition.status == "GM Canceled">
   <#else> 
    <#assign partyRoleReqIssuerGv = delegator.findOne("PartyRole", {"partyId" : userLogin.partyId,"roleTypeId" : "REQ_ISSUER"}, false)?default({})>
    
    <#if inventoryRequisition.issuedBy?has_content>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Issued By</td>
        <td width="6%">&nbsp;</td>
        <td>
        <#assign person = delegator.findOne("Person", {"partyId" : inventoryRequisition.issuedBy}, true)?default({})>
         ${person.firstName} ${person.lastName}
        </td>
    </tr>
    <#else>
    <#if partyRoleReqIssuerGv?has_content>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Issued By <span><font color="red">*</font></span></td>
        <td width="6%">&nbsp;</td>
        <td>
         <@htmlTemplate.lookupField formName="transferform" name="issuedBy" id="issuedBy" 
         	fieldFormName="LookupPersonByFacilityDepartment?facilityIdParam=${inventoryRequisition.facilityIdFrom}&partyRoleParam=REQ_ISSUER" 
         	value="${userLogin.partyId}" className="required" readonly="true"/>
        </td>
    </tr>
    </#if>
    
    </#if>
    
    
    <#if inventoryRequisition.issuedTo?has_content>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Issued To</td>
        <td width="6%">&nbsp;</td>
        <td>
         <#assign person = delegator.findOne("Person", {"partyId" : inventoryRequisition.issuedTo}, true)?default({})>
         ${person.firstName} ${person.lastName}
        </td>
    </tr>
    <#else>
    <#if partyRoleReqIssuerGv?has_content>
    <tr>
        <td width="14%">&nbsp;</td>
        <td width="6%" align="right" nowrap="nowrap" class="label">Issued To <span><font color="red">*</font></span></td>
        <td width="6%">&nbsp;</td>
        <td>
         <#-- <@htmlTemplate.lookupField formName="transferform" name="issuedTo" id="issuedTo" 
            fieldFormName="LookupPersonByFacilityDepartment?facilityIdParam=${inventoryRequisition.facilityIdTo}&partyRoleParam=REQ_RECEIVER" 
            className="required" readonly="true"/> -->
         <@htmlTemplate.lookupField formName="transferform" name="issuedTo" id="issuedTo" 
            fieldFormName="LookupPerson" 
            className="required" readonly="true"/>
        </td>
    </tr>
    </#if>
    </#if>
   
 </#if>
</#if>
</#if>
   
    
</table>
<#if inventoryRequisitionItemList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Product</td>
          <td>Uom</td>
          <td>Order Quantity</td>
          <td>Accepted Quantity</td>
          <td>Rejected Quantity</td>
          <td>ATP/QOH</td>
          <td>Cost Center</td>
          <td>Note</td>
        </tr>
        <#assign alt_row = false>
        <#list inventoryRequisitionItemList as inventoryRequisitionItem>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <#assign product = delegator.findOne("Product", {"productId" : inventoryRequisitionItem.productId}, true)>
            <td>${product.productId} - ${product.description?if_exists}</td>
            <#assign productUomView = (delegator.findOne("ProductUomView", {"productId" : inventoryRequisitionItem.productId}, true))!>
            <#if productUomView?has_content>
            	<td> ${productUomView.description?if_exists} </td>
            	<#else>
            	<td></td>
            </#if>
            <td>${inventoryRequisitionItem.quantity?if_exists?number}</td>
            <td>${inventoryRequisitionItem.acceptedQuantity?if_exists}</td>
            <#if inventoryRequisitionItem.acceptedQuantity?has_content>
            <td>${inventoryRequisitionItem.quantity?if_exists?number - inventoryRequisitionItem.acceptedQuantity?if_exists?number}</td>
            <#else>
            <td></td>
            </#if>
            <td>
            <#assign atpQohMap=Static["org.ofbiz.product.product.ProductWorker"].getAtpQoh(dispatcher,userLogin,inventoryRequisitionItem.productId,inventoryRequisition.facilityIdFrom)/>
             ${atpQohMap.atp?if_exists}/${atpQohMap.qoh?if_exists}
            </td>
            <#if inventoryRequisitionItem.glAccountCategoryId?has_content>
		        <#assign glAccountCategory = delegator.findOne("GlAccountCategory", {"glAccountCategoryId" : inventoryRequisitionItem.glAccountCategoryId}, true)>
		        <td>${glAccountCategory.description}</td>
	        	<#else>
	        	<td> </td>
       		</#if>
       		<td>${inventoryRequisitionItem.note?if_exists}</td>
          </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>

<#if partyRoleReqIssuerGv?has_content>
	<#if inventoryRequisition.issuedBy?has_content>
	<#else>
		<input name="submitBtn" id="submitBtn" type="submit" value="Issue" class="btn btn-success" onClick="javascript:issueRequisition();" />
	</#if>
</#if>

<#assign partyRoleGM = delegator.findOne("PartyRole", {"partyId" : userLogin.partyId,"roleTypeId" : "GENERAL_MANAGER"}, false)?default({})>
<#if partyRoleGM?has_content && "Requested" == inventoryRequisition.status>
<table>
<tr>
    <td><input name="approveBtn" id="approveBtn" type="submit" value="Approve" class="btn btn-success" onClick="javascript:(document.transferform.action = 'requisitionConfirm');" /></td>
	<td><div style="margin-left: 10px;"><input name="rejectBtn" id="rejectBtn" type="submit" value="Cancel" class="btn btn-success" onClick="javascript:(document.transferform.action = 'requisitionCancel');" /></div></td>
</tr>
</table>
</#if>

<script language="JavaScript" type="text/javascript">
        function issueRequisition() {
           var cform = document.transferform;
		   jQuery(cform).validate();
    	}
    	
</script>

</form>
   