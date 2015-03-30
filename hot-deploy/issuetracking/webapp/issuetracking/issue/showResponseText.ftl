
<table style="border:none;width:100%">
		<#list comments as comment>
		<tr>
			<td style="font-weight:bold"> ${Static["org.ofbiz.issuetracking.util.IssuetrackingUtil"].getFullName(delegator,comment.commentedBy," ")?if_exists} ${comment.createdStamp?if_exists}</td>
		</tr>
		<tr>
			<td>${comment.response?if_exists}</td>
		</tr>	
		<tr>
			<td><hr/></td>
		</tr>
		</#list>
</table>