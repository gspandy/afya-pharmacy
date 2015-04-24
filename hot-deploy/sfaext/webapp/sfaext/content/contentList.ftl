
<#-- A parametrized list of content attachments. -->

<#assign addContentUrlTarget = "">
<#assign uploadContentTarget = "">
<#assign updateContentTarget = "">
<#assign downloadLink = "">
<#assign objectIdParam = "">
<#if !donePage?exists>
  <#assign donePage = parameters.donePage>
</#if>
<#if donePage == "viewAccount">
  <#assign objectIdParam = "partyId=" + parameters.partyId?default("")>
  <#assign addContentUrlTarget = "addContentUrlForAccount?" + objectIdParam>
  <#assign uploadContentTarget = "uploadContentForAccount?" + objectIdParam>
  <#assign updateContentTarget = "updateContentForAccountForm?" + objectIdParam>
  <#assign downloadLink = "downloadPartyContent?" + objectIdParam>
<#elseif donePage == "viewContact">
  <#assign objectIdParam = "partyId=" + parameters.partyId?default("")>
  <#assign addContentUrlTarget = "addContentUrlForContact?" + objectIdParam>
  <#assign uploadContentTarget = "uploadContentForContact?" + objectIdParam>
  <#assign updateContentTarget = "updateContentForContactForm?" + objectIdParam>
  <#assign downloadLink = "downloadPartyContent?" + objectIdParam>
<#elseif donePage == "viewLead">
  <#assign objectIdParam = "partyId=" + parameters.partyId?default("")>
  <#assign addContentUrlTarget = "addContentUrlForLead?" + objectIdParam>
  <#assign uploadContentTarget = "uploadContentForLead?" + objectIdParam>
  <#assign updateContentTarget = "updateContentForLeadForm?" + objectIdParam>
  <#assign downloadLink = "downloadPartyContent?" + objectIdParam>
<#elseif donePage == "viewOpportunity">
  <#assign objectIdParam = "salesOpportunityId=" + parameters.salesOpportunityId?default("")>
  <#assign addContentUrlTarget = "addContentUrlForOpportunity?" + objectIdParam>
  <#assign uploadContentTarget = "uploadContentForOpportunity?" + objectIdParam>
  <#assign updateContentTarget = "updateContentForOpportunityForm?" + objectIdParam>
  <#assign downloadLink = "downloadOpportunityContent?" + objectIdParam>
<#elseif donePage == "viewActivity">
  <#assign objectIdParam = "workEffortId=" + parameters.workEffortId?default("")>
  <#assign addContentUrlTarget = "addContentUrlForActivity?" + objectIdParam>
  <#assign uploadContentTarget = "uploadContentForActivity?" + objectIdParam>
  <#assign updateContentTarget = "updateContentForActivityForm?" + objectIdParam>
  <#assign downloadLink = "downloadActivityContent?" + objectIdParam>
</#if>

<style type="text/css">
  .button-bar ul li {
    text-transform: none;
  }
</style>

<div class="screenlet-title-bar">
<ul>
  <li class="h3">${uiLabelMap.CrmContentList}</li>
  <li>
    <#if hasUpdatePermission?exists && hasUpdatePermission>
	  <a href="<@ofbizUrl>${uploadContentTarget}&DONE_PAGE=${donePage}</@ofbizUrl>">${uiLabelMap.CrmUploadFile}</a>
    </#if>
  </li>
</ul>
</div>

<#-- <div class="screenlet">
  <div class="screenlet-title-bar">${uiLabelMap.CrmContentList}</div>
  <#if hasUpdatePermission?exists && hasUpdatePermission>
  <div class="subMenuBar"><a class="subMenuButton" href="<@ofbizUrl>${uploadContentTarget}&DONE_PAGE=${donePage}</@ofbizUrl>">${uiLabelMap.CrmUploadFile}</a></div>
  </#if>
</div> -->

<div class="form">
<table class="crmsfaListTable">
  <tbody>
    <tr class="crmsfaListTableHeader">
      <td class="tableheadtext">${uiLabelMap.CommonName}</td>
      <td class="tableheadtext">${uiLabelMap.CommonDescription}</td>
      <td class="tableheadtext">${uiLabelMap.ProductCreatedDate}</td>
      <#if hasUpdatePermission?exists && hasUpdatePermission>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      </#if>
    </tr>
    <#if content?exists && content.size() != 0>
    <#assign rowCount = 0>
    <#list content as item>
      <#assign rowClass = "rowLightGray">
      <#if rowCount % 2 == 0><#assign rowClass = "rowWhite"></#if>

      <#assign hyperlink = false>
      <#assign file = false>
      <#assign contentEntity = delegator.findByPrimaryKey("Content",Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId",item.contentId))>
      <#assign data = contentEntity.getRelatedOne("DataResource")>
      <#if data?exists && data.objectInfo?has_content>
        <#if contentEntity.contentTypeId == "HYPERLINK"><#assign hyperlink = true>
        <#elseif contentEntity.contentTypeId == "FILE"><#assign file = true>
        </#if>
      </#if>

    <tr class="${rowClass}">
      <td class="tabletext">
        <#if hyperlink><a target="top" class="linktext" href="${data.objectInfo}"></#if>
        <#if file><a class="linktext" href="<@ofbizUrl>${downloadLink}&contentId=${contentEntity.contentId}</@ofbizUrl>"></#if>
        ${contentEntity.contentName?if_exists}
        <#if (hyperlink || file)></a></#if>
      </td>
      <td class="tabletext">${contentEntity.description?if_exists}</td>
      <td class="tabletext">${contentEntity.createdDate}</td>
      <#if hasUpdatePermission?exists && hasUpdatePermission>
      <td class="tabletext"><a href="<@ofbizUrl>${updateContentTarget}&contentId=${contentEntity.contentId}&contentTypeId=${contentEntity.contentTypeId}</@ofbizUrl>" class="btn btn-success">${uiLabelMap.CommonEdit}</a>
      <a href="<@ofbizUrl>removeContent?contentId=${contentEntity.contentId}&${objectIdParam}&donePage=${donePage}</@ofbizUrl>" class="btn btn-danger">${uiLabelMap.CommonRemove}</a></td>
      </#if>
    </tr>

      <#assign rowCount = rowCount + 1>
    </#list>
    </#if>

  </tbody>
</table>
</div>