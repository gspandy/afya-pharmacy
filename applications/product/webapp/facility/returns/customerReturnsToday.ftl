<#if retunHeaderList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Return</td>
          <td>Status</td>
          <td>Return accepted By</td>
          <td>Customer</td>
          <td>To Party</td>
        </tr>
        <#assign alt_row = false>
        <#list retunHeaderList as retunHeader>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/returnMain?returnId=${retunHeader.returnId?if_exists}&amp;externalLoginKey=${externalLoginKey}" target="_blank" class="btn-link">${retunHeader.returnId?if_exists}</a></td>
            <td>${retunHeader.getRelatedOneCache("StatusItem").get("description",locale)}</td>
         	<td>  
         	 <#assign returnPersonLists = delegator.findByAnd("Person", {"partyId" : retunHeader.createdBy})>
                 <#list returnPersonLists as returnPersonList>
            ${returnPersonList.firstName?if_exists}  ${returnPersonList.lastName?if_exists}
              </#list>
         	</td>
         	<td>
         	<#assign partyGroupToPartyLists = delegator.findByAnd("PartyGroup", {"partyId" : retunHeader.fromPartyId})>
                 <#list partyGroupToPartyLists as partyGroupToPartyList>
            ${partyGroupToPartyList.groupName?if_exists} 
              </#list>
         	</td>
         	<td>
         	<#assign partyGroupLists = delegator.findByAnd("PartyGroup", {"partyId" : retunHeader.toPartyId})>
                 <#list partyGroupLists as partyGroupList>
            ${partyGroupList.groupName?if_exists} 
              </#list>
         </td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>