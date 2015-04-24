<#if hasUpdatePermission?exists>
  <#assign updateLink><a href="updateContactForm?partyId=${partySummary.partyId}" title="${uiLabelMap.CommonEdit}">${uiLabelMap.CommonEdit}</a></#assign>
</#if>

<#if isDeactivateLinkRendered?default(false)>  
  <#assign deactivateLink><a href="deactivateContact?partyId=${partySummary.partyId}" title=${uiLabelMap.CrmDeactivateContact}>${uiLabelMap.CrmDeactivateContact}</a></#assign>
</#if>

<#if isAssignedToMeLinkRendered?default(false)>
  <#assign assignLink><a href="assignContactToParty?partyId=${parameters.partyId}&roleTypeId=CONTACT">${uiLabelMap.OpentapsAssignToMe}</a></#assign>
</#if>

<#if isUnassignLinkRendered?default(false)>
  <#assign unassignLink><a href="unassignPartyFromContact?partyId=${parameters.partyId}&roleTypeId=CONTACT">${uiLabelMap.OpentapsUnassign}</a></#assign>
</#if>

<div class="screenlet-title-bar">
  <ul>
	<li class="h3">${uiLabelMap.SfaExtViewContact}</li>
	<#if assignLink?has_content>
	  <li>${assignLink?if_exists}</li>
	</#if>
	<#if unassignLink?has_content>
	  <li>${unassignLink?if_exists}</li>
	</#if>
	<#if deactivateLink?has_content>
	  <li>${deactivateLink?if_exists}</li>
	</#if>
	<#if updateLink?has_content>
	  <li>${updateLink?if_exists}</li>
	</#if>
  </ul>
  <#if contactDeactivated?exists><span class="screenlet-title-bar-warning">${uiLabelMap.CrmContactDeactivated} ${contactDeactivatedDate}</span></#if>
</div>

<#-- <div class="screenlet">
    <div class="screenlet-title-bar">${uiLabelMap.SfaExtViewContact}
        <#if contactDeactivated?exists><span class="subSectionWarning">${uiLabelMap.CrmContactDeactivated} ${contactDeactivatedDate}</span></#if>
    </div>
    <div class="subMenuBar">${assignLink?if_exists}${unassignLink?if_exists}${updateLink?if_exists}${deactivateLink?if_exists}</div>
</div> -->
