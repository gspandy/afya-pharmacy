<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.CrmShortcuts}</div></div>
    <div class="screenlet-body">
	  
      <ul class="shortcuts">
        <li><a href="<@ofbizUrl>MyLeads</@ofbizUrl>">${uiLabelMap.CrmMyLeads}</a></li>
        <#if security.hasPermission("CRMSFA_TEAM_CREATE",session)>
        	<li><a href="<@ofbizUrl>MyTeamLeads</@ofbizUrl>">${uiLabelMap.CrmMyTeamLeads}</a></li>
		</#if>
        <li><a href="<@ofbizUrl>CreateLead</@ofbizUrl>">${uiLabelMap.CrmCreateLead}</a></li>
        <li><a href="<@ofbizUrl>CreateLeadFromVCard</@ofbizUrl>">${uiLabelMap.PageTitleCreateLeadFromVCard}</a></li>
        <li><a href="<@ofbizUrl>FindLeads</@ofbizUrl>">${uiLabelMap.CrmFindLeads}</a></li>
        <#if security.hasPermission("CRMSFA_LEAD_DELETE",session)>
          	<li><a href="<@ofbizUrl>MergeLeads</@ofbizUrl>">${uiLabelMap.CrmMergeLeads}</a></li>
        </#if>	
      </ul>
    </div>
</div>
