<#--
 * Copyright (c) 2006 - 2007 Open Source Strategies, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Honest Public License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Honest Public License for more details.
 * 
 * You should have received a copy of the Honest Public License
 * along with this program; if not, write to Funambol,
 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
-->
<#-- Copyright (c) 2005-2006 Open Source Strategies, Inc. -->

<#if session.getAttribute("shoppingCart")?exists>
    <#assign orderLabel = uiLabelMap.OpentapsResumeOrder>
<#else>
    <#assign orderLabel = uiLabelMap.OpentapsCreateOrder>
</#if>

<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.CrmShortcuts}</div></div>
    <div class="screenlet-body">
      <ul class="shortcuts">
        <li><a href="<@ofbizUrl>main</@ofbizUrl>" >${uiLabelMap.CrmMyCalendar}</a></li>
        <li><a href="<@ofbizUrl>CreateLead</@ofbizUrl>">${uiLabelMap.CrmCreateLead}</a></li>
        <li><a href="<@ofbizUrl>NewAccounts</@ofbizUrl>">${uiLabelMap.CrmCreateAccount}</a></li>
        <li><a href="<@ofbizUrl>CreateContact</@ofbizUrl>">${uiLabelMap.CrmCreateContact}</a></li>
        <li><a href="<@ofbizUrl>EditOpportunity</@ofbizUrl>">${uiLabelMap.CrmCreateOpportunity}</a></li>
        <li><a href="<@ofbizUrl>EditEvent?workEffortTypeId=EVENT</@ofbizUrl>">${uiLabelMap.CrmCreateNewEvent}</a></li>
      </ul>
    </div>
</div>
