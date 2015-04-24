<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.CrmShortcuts}</div></div>
    <div class="screenlet-body">
      <ul class="shortcuts">
      	<li><a href="<@ofbizUrl>MyContacts</@ofbizUrl>">${uiLabelMap.CrmMyContacts}</a></li>
        <#if security.hasPermission("CRMSFA_TEAM_CREATE",session)>
        	<li><a href="<@ofbizUrl>MyTeamContacts</@ofbizUrl>">${uiLabelMap.CrmMyTeamContacts}</a></li>
		</#if>
        <li><a href="<@ofbizUrl>CreateContact</@ofbizUrl>">${uiLabelMap.CrmCreateContact}</a></li>
        <li><a href="<@ofbizUrl>CreateContactFromVCard</@ofbizUrl>">${uiLabelMap.PageTitleCreateContactFromVCard}</a></li>
        <li><a href="<@ofbizUrl>FindContacts</@ofbizUrl>">${uiLabelMap.CrmFindContacts}</a></li>
        <li><a href="<@ofbizUrl>MergeContacts</@ofbizUrl>">${uiLabelMap.CrmMergeContacts}</a></li>
      </ul>
    </div>
</div>
