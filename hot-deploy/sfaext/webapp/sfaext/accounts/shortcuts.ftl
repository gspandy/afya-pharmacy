<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.CrmShortcuts}</div></div>
    <div class="screenlet-body">
      <ul class="shortcuts">
        <li><a href="<@ofbizUrl>MyAccounts</@ofbizUrl>">${uiLabelMap.CrmMyAccounts}</a></li>
        <#if security.hasPermission("CRMSFA_TEAM_CREATE",session)>
        	<li><a href="<@ofbizUrl>MyTeamAccounts</@ofbizUrl>">${uiLabelMap.CrmMyTeamAccounts}</a></li>
		</#if>
        <li><a href="<@ofbizUrl>NewAccounts</@ofbizUrl>">${uiLabelMap.CrmCreateAccount}</a></li>
        <li><a href="<@ofbizUrl>FindAccounts</@ofbizUrl>">${uiLabelMap.CrmFindAccounts}</a></li>
      </ul>
    </div>
</div>
