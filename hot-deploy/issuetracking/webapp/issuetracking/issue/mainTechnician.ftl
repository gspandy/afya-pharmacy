<style type="text/css">
.top-table{
    border-color: -moz-use-text-color #DADADA #DADADA;
    border-right: 1px solid #DADADA;
    border-style: none solid solid;
    border-width: 1px;
    margin-top: 8px;
    width: 100%;
}
</style>

<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<div class="screenlet" style="width:20%;">
	<div class="screenlet-title-bar">
		<table style="width:100%;  align:center">
			<li class="h3">Issue Status Summary</li>
			<tr>
				<td valign="top">
					<#if assignedIssuesDB?has_content>
						<@display.table name="assignedIssuesDB" id="row" class="top-table">
							<@display.column title="Status">
								<a href="<@ofbizUrl>statusWiseIssueDetail?issueStatusId=${row.issueStatusId}&field=assignedTo</@ofbizUrl>" style="color:#ffffff" >${row.status}</a>
							</@display.column>
							<@display.column title="Count" property="count"/>
						</@display.table>
					</#if>
				</td>
			</tr>
		</table>
	</div>
</div>
<table style="border:0px;width:100%;margin-top:100px;">
	<tr>
		<td valign="top">
			<@display.table name="assignedIssues" id="row" class="basic-table" defaultsort=8 defaultorder="descending">
				<@display.column title="Issue Id" sortable=true>
					<a href="<@ofbizUrl>viewIssueDetail?issueId=${row.issueId}&showEdit=true</@ofbizUrl>" >${row.issueId}</a>
				</@display.column>
				<@display.column title="Status" property="status" sortable=true/>
				<@display.column title="Severity" property="severity" sortable=true/>
				<@display.column title="Category" property="category" sortable=true/>
				<#if row.categoryOwner?has_content>
					<#assign categoryOwnerPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":row.categoryOwner})>
					<#if categoryOwnerPerson?has_content>
						<#assign categoryOwnerName = categoryOwnerPerson.firstName?if_exists + " " + categoryOwnerPerson.lastName?if_exists + " " + categoryOwnerPerson.groupName?if_exists>
						<@display.column title="Owner" value="${categoryOwnerName}" sortable=true/>
					<#else>
						<@display.column title="Owner" property="categoryOwner" sortable=true/>
					</#if>
				<#else>
					<@display.column title="Owner" property="categoryOwner" sortable=true/>
				</#if>
				<#if row.lastUpdatedBy?has_content>
					<#assign lastUpdatedByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":row.lastUpdatedBy})>
					<#if lastUpdatedByPerson?has_content>
						<#assign lastUpdatedByName = lastUpdatedByPerson.firstName?if_exists + " " + lastUpdatedByPerson.lastName?if_exists + " " + lastUpdatedByPerson.groupName?if_exists>
						<@display.column title="Updated By" value="${lastUpdatedByName}" sortable=true/>
					<#else>
						<@display.column title="Updated By" property="lastUpdatedBy" sortable=true/>
					</#if>
				<#else>
					<@display.column title="Updated By" property="lastUpdatedBy" sortable=true/>
				</#if>
				<#if row.createdBy?has_content>
					<#assign createdByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":row.createdBy})>
					<#if createdByPerson?has_content>
						<#assign createdByName = createdByPerson.firstName?if_exists + " " + createdByPerson.lastName?if_exists + " " + createdByPerson.groupName?if_exists>
						<@display.column title="Reported By" value="${createdByName}" sortable=true/>
					<#else>
						<@display.column title="Reported By" property="createdBy" sortable=true/>
					</#if>
				<#else>
					<@display.column title="Reported By" property="createdBy" sortable=true/>
				</#if>
				<@display.column title="Updated On" property="lastUpdatedOn" sortable=true/>
				<@display.column title="Reported On" property="createdOn" sortable=true/>
				<@display.column class="button-col align-float">
					<a href="<@ofbizUrl>issueEntry?issueId=${row.issueId}&mode=edit</@ofbizUrl>" >Edit</a>
				</@display.column>
				<@display.setProperty name="paging.banner.items_name" value="Issues"/>
				<@display.setProperty name="paging.banner.placement" value="bottom"/>
			</@display.table>
		</td>
	</tr>
</table>