<#assign item =result.item/>
<#-- <a class="btn btn-success" href="<@ofbizUrl>EditEvent?crmEventId=${item.crmEventId}</@ofbizUrl>">${uiLabelMap.CommonUpdate}</a> -->

<div class="screenlet-title-bar">
	<ul>
		<li class="h3">${uiLabelMap.CrmViewEvent}</li>
		<li><a href="<@ofbizUrl>EditEvent?crmEventId=${item.crmEventId}</@ofbizUrl>">${uiLabelMap.CommonUpdate}</a></li>
	</ul>
</div>

<table cellspacing="0" class="basic-table">
	<tr>
		<td class="label">Event Name</td>
		<td><#if item?has_content>${item.eventName}</#if></td>
	</tr>
	
	<tr>
		<td class="label">Description</td>
		<td><#if item?has_content>${item.description}</#if></td>
	</tr>

	<tr>
		<td class="label">Estimated Start Date</td>
		<td><#if item?has_content>${item.estimatedStartDate}</#if></td>
	</tr>
	
	<tr>
		<td class="label">Estimated Completion Date</td>
		<td><#if item?has_content>${item.estimatedCompletionDate}</#if></td>
	</tr>
</table>