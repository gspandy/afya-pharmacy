<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">Announcements</div>
    </div>
    <div class="screenlet-body" style="overflow:auto">
    	<table style="border:none">
        <#list announcements as announcement>
        <#if announcement.desc?exists>
			<tr>
				<td>${announcement.announceid}</td>
				<td>${announcement.desc}</td>
			</tr>
		</#if>
		</#list>    	
		</table>
    </div>
</div>
<br/>

<#if security.hasEntityPermission("HUMANRES", "_MGR", session) || security.hasEntityPermission("HUMANRES", "_ADMIN", session)>
<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">Pending Actions</div>
    </div>
    <div class="screenlet-body">
	    <b>
	  		<a href="<@ofbizUrl>SearchLeave</@ofbizUrl>" >
		    	${numberOfLeavesPendingApproval} Leaves needs to be approved by you as of today.<br><br>
	    	</a>
	    </b>
    </div>
</div>
</#if>              

<br/>
