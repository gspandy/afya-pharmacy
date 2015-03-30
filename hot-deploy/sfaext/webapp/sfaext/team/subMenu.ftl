<#if hasTeamDeactivatePermission?exists>
<#assign deactivateLink = "<a href='deactivateTeam?partyId=" + team.partyId + "'>" + uiLabelMap.CrmDeactivateTeam + "</a>">
</#if>
<#if hasTeamUpdatePermission?exists>
<#assign updateLink = "<a href='updateTeamForm?partyId=" + team.partyId + "'>" + uiLabelMap.CommonEdit + "</a>">
</#if>

<div class="screenlet-title-bar">
    <ul>
        <li class="h3">${uiLabelMap.CrmTeam}</li>
        <#if deactivateLink?has_content>
            <li>${deactivateLink?if_exists}</li>
        </#if>
        <#if updateLink?has_content>
            <li>${updateLink?if_exists}</li>
        </#if>
    </ul>
    <#if teamDeactivated?exists><span class="screenlet-title-bar-warning">${uiLabelMap.CrmTeamDeactivated}</span></#if>
</div>

<#-- <div class="screenlet">
    <div class="screenlet-title-bar">${uiLabelMap.CrmTeam}
        <#if teamDeactivated?exists><span class="subSectionWarning">${uiLabelMap.CrmTeamDeactivated}</span></#if>
    </div>
    <div class="subMenuBar">${updateLink?if_exists}${deactivateLink?if_exists}</div>
</div> -->