
<!-- delete <a class='subMenuButton' ... /> all  Nafis-->

<#if (security.hasEntityPermission("CRMSFA_LEAD", "_CREATE", session)) && (partySummary?exists)>
  <#assign create_option = "<a href='duplicateLeadForm?partyId="+partySummary.partyId+"'>"+uiLabelMap.CrmDuplicateLead+"</a>"  />
</#if>

<#-- if lead has been converted, then no operations should be done -->
<#if hasBeenConverted?exists>

  <#assign view_converted_options = "<span class='screenlet-title-bar-warning'>${uiLabelMap.CrmLeadHasBeenConverted}</span>"  />
  <#assign view_converted_options = view_converted_options + "<a  href='viewContact?partyId="+parameters.partyId+"'>"+uiLabelMap.CrmViewAsContact+"</a>"  />

<#-- otherwise check for options if update permission exists -->
<#else><#if hasUpdatePermission?exists>

  <#-- a lead can be qualified it has already been assigned -->
  <#if (partySummary.statusId?exists) && (partySummary.statusId == 'PTYLEAD_ASSIGNED')>
    <#assign qualify_options = "<a  href='updateLeadStatus?partyId="+partySummary.partyId+"&statusId=PTYLEAD_QUALIFIED'>"+uiLabelMap.CrmQualifyLead+"</a>"  />
  </#if>

  <#-- a lead can only be converted if it has already been qualified -->
  <#if (partySummary.statusId?exists) && (partySummary.statusId == 'PTYLEAD_QUALIFIED')>
    <#assign convert_options = "<a  href='convertLeadForm?partyId="+partySummary.partyId+"'>"+uiLabelMap.CrmConvertLead+"</a>"  />
  </#if>

  <#assign update_options = "<a  href='updateLeadForm?partyId="+partySummary.partyId+"'>"+uiLabelMap.CommonEdit+"</a>" />

  <#if hasDeletePermission?exists>
    <#assign delete_options = "<a href='deleteLead?leadPartyId=" + partySummary.partyId + "'>" + uiLabelMap.CommonDelete + "</a>"  />
  </#if>

</#if>
</#if>


<div class="screenlet-title-bar">
  <ul>
      <li class="h3">${uiLabelMap.SfaViewLead}</li>
      <#if view_converted_options?has_content>
        <li>${view_converted_options?if_exists}</li>
      </#if>
      <#if convert_options?has_content>
        <li>${convert_options?if_exists}</li>
      </#if>
      <#if qualify_options?has_content>
        <li>${qualify_options?if_exists}</li>
      </#if>
      <#if create_option?has_content>
        <li>${create_option?if_exists}</li>
      </#if>
      <#if delete_options?has_content>
        <li>${delete_options?if_exists}</li>
      </#if>
      <#if update_options?has_content>
        <li>${update_options?if_exists}</li>
      </#if>
  </ul>
</div>

<#-- <div class="screenlet-title-bar">
    <ul>
        <li class="h3">${uiLabelMap.SfaLead}</li>
        <li>${converted_options?if_exists}${create_option?if_exists}${update_options?if_exists}</li>
    </ul>
</div> -->