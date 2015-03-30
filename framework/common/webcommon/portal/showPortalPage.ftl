<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<style>
    .basic-table tr td:first-child { /* Style for all cells */
    vertical-align: middle;
    margin-left: 2px;
    height: 25px;
    padding-right: 1.5em;
    text-align: left;
    }
</style>

<#if portalPage?has_content>
<#if portalPage.portalPageId="AccountingPortalPage">
<div style="position: absolute;right: 10px;top: 60px;z-index: 0;">
<table width="100%">
<tr>
<td width="100%" style="text-align:right;font-weight:bold">Organization</td>
<td style="text-align:right">
<select name="organizationPartyId" id="organizationPartyId" onChange="javascript:getOrganization(organizationPartyId);">
<#assign organizationGv = delegator.findOne("PartyGroup", {"partyId" : parameters.organizationPartyId?if_exists}, false)>
    <#if organizationGv?has_content>
        <option selected="selected" value='${organizationGv.partyId?if_exists}'>${organizationGv.groupName?if_exists}</option>
        <option value="${organizationGv.partyId?if_exists}">----</option>
    </#if>
<#assign organizationLists = delegator.findList("PartyAcctgPreference", null,null,null,null,false)>
<#list organizationLists as organizationList>
    <#assign partyNames = delegator.findByAnd("PartyGroup", {"partyId" : organizationList.partyId})>
    <#list partyNames as partyName>
        <option value='${partyName.partyId?if_exists}'>${partyName.groupName?if_exists}</option>
    </#list>
</#list>
</select>
</td>
</table>
</div>
</#if>
<table width="100%">
  <tr>
    <#list portalPageColumns?if_exists as portalPageColumn>
      <td style="vertical-align: top; <#if portalPageColumn.columnWidthPercentage?has_content> width:${portalPageColumn.columnWidthPercentage}%;</#if>" id="portalColumn_${portalPageColumn.columnSeqId}">
      <#assign firstInColumn = true/>
      <#list portalPagePortlets as portlet>
        <#if (!portlet.columnSeqId?has_content && portalPageColumn_index == 0) || (portlet.columnSeqId?if_exists == portalPageColumn.columnSeqId)>
          <#if portlet.screenName?has_content>
            <#assign portletFields = '<input name="portalPageId" value="' + portlet.portalPageId + '" type="hidden"/><input name="portalPortletId" value="' + portlet.portalPortletId + '" type="hidden"/><input name="portletSeqId" value="' + portlet.portletSeqId  + '" type="hidden"/>'>
            <form  method="post" action="<@ofbizUrl>movePortletToPortalPage</@ofbizUrl>" name="movePP_${portlet_index}">${portletFields}<input name="newPortalPageId" value="${portlet.portalPageId}" type="hidden"/>
 
            
            </form>
            
            <div id="portalPortlet_${portlet_index}" class="noClass">
              <#assign idRefreshAttr = delegator.findOne("PortletAttribute", {"portalPageId":portlet.portalPageId, "portalPortletId":portlet.portalPortletId, "portletSeqId": portlet.portletSeqId, "attrName": "divIdRefresh"},true)?if_exists />
              <#if idRefreshAttr?has_content>
                <div id="${idRefreshAttr.attrValue}">
              <#else>
                <div id="${portlet.portalPortletId}_refresh">
              </#if>
              ${setRequestAttribute("portalPageId", portalPage.portalPageId)}
              ${setRequestAttribute("portalPortletId", portlet.portalPortletId)}
              ${setRequestAttribute("portletSeqId", portlet.portletSeqId)}
              ${screens.render(portlet.screenLocation, portlet.screenName)}
              ${screens.setRenderFormUniqueSeq(portlet_index)}
              
              </div>
            </div>
            
            <#-- DragNDrop is only activated, when the portal Page isn't the Default page -->
            <#if portalPage.originalPortalPageId?has_content><script type="text/javascript">setMousePointer("${portlet_index}")</script></#if>
            <#if portalPage.originalPortalPageId?has_content><script type="text/javascript">makeDragable("${portlet_index}");</script></#if>
            <#if portalPage.originalPortalPageId?has_content><script type="text/javascript">makeDroppable("${portlet_index}");</script></#if>
            <form method="post" action="<@ofbizUrl>updatePortalPagePortletAjax</@ofbizUrl>" name="freeMove_${portlet_index}">${portletFields}<input name="columnSeqId" value="${portalPageColumn.columnSeqId}" type="hidden"/><input name="mode" value="RIGHT" type="hidden"/></form>
          </#if>
          <#assign firstInColumn = false/>
        </#if>
      </#list>
      </td>
      <#if portalPageColumn_has_next>
        <td>&nbsp;</td>
      </#if>
    </#list>
    
  </tr>
</table>
<#else/>
<h2>No portal page data found. You may not have the necessary seed or other data for it.</h2>
</#if>
