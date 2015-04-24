<style type="text/css">
.top-table{
    border-color: -moz-use-text-color #DADADA #DADADA;
    border-right: 1px solid #DADADA;
    border-style: none solid solid;
    border-width: 1px;
    margin-top: 8px;
    width: 100%;
    min-height:100px;
}

.basic-table tr th,.basic-table .header-row {
    background-color: #F2F2F2;
    border: 0.1em solid #CCCCCC;
    color: #555555;
    font-weight: bold;
    height: 25px;
    padding-left: 10px;
    text-align: left;
    width: auto;
}
</style>

<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<div class="screenlet" style="width:20%;">
	<div class="screenlet-title-bar">
		<table style="width:100%;align:center" >
			<li class="h3">Issue Status Summary</li>
			<tr>
				<td valign="top" >
					<#if myIssuesDB?has_content>
						<@display.table name="myIssuesDB" id="row" class="top-table">
							<@display.column title="Status">
								<a href="<@ofbizUrl>statusWiseIssueDetail?issueStatusId=${row.issueStatusId}&field=createdBy</@ofbizUrl>" style="color:#ffffff" >${row.status}</a>
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
			<@display.table name="myIssues" id="row" class="basic-table header-row" defaultsort=8 defaultorder="descending">
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
				<#if row.assignedTo?has_content>
					<#assign assignedToPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":row.assignedTo})>
					<#if assignedToPerson?has_content>
						<#assign assignedToName = assignedToPerson.firstName?if_exists + " " + assignedToPerson.lastName?if_exists + " " + assignedToPerson.groupName?if_exists>
						<@display.column title="Assigned To" value="${assignedToName}" sortable=true/>
					<#else>
						<@display.column title="Assigned To" property="assignedTo" sortable=true/>
					</#if>
				<#else>
					<@display.column title="Assigned To" property="assignedTo" sortable=true/>
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
				<@display.column title="Updated On" property="lastUpdatedOn" sortable=true/>
				<@display.column title="Reported On" property="createdOn" sortable=true/>
				<@display.column class="btn btn-link">
					<a href="<@ofbizUrl>issueEntry?issueId=${row.issueId}&mode=edit</@ofbizUrl>" >Edit</a>
				</@display.column>
				<@display.setProperty name="paging.banner.items_name" value="Issues"/>
				<@display.setProperty name="paging.banner.placement" value="bottom"/>
			</@display.table>
		</td>
	</tr>
</table>

