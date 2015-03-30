<#if inventoryRequisitionList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Request ID</td>
          <td>Status</td>
          <td>To Department</td>
          <td>Requested Date</td>
        </tr>
        <#assign alt_row = false>
        <#list inventoryRequisitionList as inventoryRequisition>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="<@ofbizUrl>RedirectReqInventoryTransfer?invRequisitionId=${inventoryRequisition.invRequisitionId?if_exists}</@ofbizUrl>" class="btn btn-link"> ${inventoryRequisition.invRequisitionId?if_exists} </a></td>
            <td>${inventoryRequisition.status?if_exists}</td>
            <#assign facility = delegator.findOne("Facility", {"facilityId" : inventoryRequisition.facilityIdFrom}, true)>
            <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : facility.ownerPartyId}, true)>
            <td>${partyGroup.groupName}</td>
            <td>${inventoryRequisition.requestDate?if_exists?string("dd-MM-yyyy")}</td>
          </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>