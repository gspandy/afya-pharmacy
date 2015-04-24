<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.CrmShortcuts}</div></div>
    <div class="screenlet-body">
      <ul class="shortcuts">
        <li><a href="<@ofbizUrl>myOpportunities</@ofbizUrl>">${uiLabelMap.CrmMyOpportunities}</a></li>
         <#if security.hasPermission("CRMSFA_TEAM_CREATE",session)>
        	<li><a href="<@ofbizUrl>teamsOpportunities</@ofbizUrl>">${uiLabelMap.CrmTeamsOpportunities}</a></li>
        </#if>
        <li><a href="<@ofbizUrl>EditOpportunity</@ofbizUrl>">${uiLabelMap.CrmCreateOpportunity}</a></li>
        <li><a href="<@ofbizUrl>FindOpportunity</@ofbizUrl>">${uiLabelMap.CrmFindOpportunity}</a></li>
      </ul>
    </div>
</div>
