<br>
<link rel="stylesheet" href="/virtual3c/style.css" type="text/css"/>
<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
	<div class="screenlet" >
		<div class="screenlet-title-bar" >
				<ul>
					<li class="h3">Assigned Issues</li>
					<li class="expanded"><a onclick="javascript:toggleScreenlet(this, 'assignedIssuesScreenlet', 'Expand', 'Collapse');" title="Collapse">&nbsp</a></li>
				<ul>
		</div>
		<div class="screenlet-body" id="assignedIssuesScreenlet">
			<table style="border:0px;width:100%">
				<tr>
		<td>
		    <@display.table name="assignedIssues" id="row" pagesize=10 class="basic-table" defaultsort=8 defaultorder="descending">
			  <@display.column title="Issue Id" sortable=true>
					<a href="<@ofbizUrl>viewIssueDetail?issueId=${row.issueId}&showEdit=true</@ofbizUrl>" >${row.issueId}</a>
			  </@display.column>
			  <@display.column title="Status" property="status" sortable=true/>
			  <@display.column title="Severity" property="severity" sortable=true/>
			  <@display.column title="Category" property="category" sortable=true/>
			  <@display.column title="Sub Category" property="subCategory" sortable=true/>
			  <@display.column title="Owner" property="categoryOwner" sortable=true/>
			  <@display.column title="Updated By" property="lastUpdatedBy" sortable=true/>
			  <@display.column title="Updated On" property="lastUpdatedOn" sortable=true/>
			  <@display.column title="Reported By" property="createdBy" sortable=true/>
			  <@display.column title="Reported On" property="createdOn" sortable=true/>
			  <@display.column class="button-col align-float">
					<a href="<@ofbizUrl>issueEntry?issueId=${row.issueId}&mode=edit</@ofbizUrl>" >Edit</a>
			  </@display.column>
			</@display.table>
		</td>
					<td><br><br><br>
					    <@display.table name="assignedIssuesDB" id="row" class="basic-table">
						  <@display.column title="Status">
						  		<a href="<@ofbizUrl>statusWiseIssueDetail?issueStatusId=${row.issueStatusId}&field=assignedTo</@ofbizUrl>" >${row.status}</a>
						  </@display.column>
						  <@display.column title="Count" property="count"/>
						</@display.table>
					</td>
				</tr>
			</table>
		</div>	
	</div>	

	<div class="screenlet">
		<div class="screenlet-title-bar" >
				<ul>
					<li class="h3">My Issues</li>
					<li class="expanded"><a onclick="javascript:toggleScreenlet(this, 'myIssuesScreenlet', 'Expand', 'Collapse');" title="Collapse">&nbsp</a></li>
				<ul>
		</div>
		<div class="screenlet-body" id="myIssuesScreenlet">
			<table style="border:0px;width:100%" >
				<tr>
					<td>
					    <@display.table name="myIssues" id="row" class="basic-table" pagesize=10 defaultsort=9 defaultorder="descending">
						  <@display.column title="Issue Id" sortable=true>
						  		<a href="<@ofbizUrl>viewIssueDetail?issueId=${row.issueId}&showEdit=true</@ofbizUrl>" >${row.issueId}</a>
						  </@display.column>
						  <@display.column title="Status" property="status" sortable=true/>
						  <@display.column title="Severity" property="severity" sortable=true/>
						  <@display.column title="Category" property="category" sortable=true/>
						  <@display.column title="Sub Category" property="subCategory" sortable=true/>
						  <@display.column title="Owner" property="categoryOwner" sortable=true/>
						  <@display.column title="Assigned To" property="assignedTo" sortable=true/>
						  <@display.column title="Updated By" property="lastUpdatedBy" sortable=true/>
						  <@display.column title="Updated On" property="lastUpdatedOn" sortable=true/>
						  <@display.column title="Reported On" property="createdOn" sortable=true/>
						  <@display.column class="button-col align-float">
						  		<a href="<@ofbizUrl>issueEntry?issueId=${row.issueId}&mode=edit</@ofbizUrl>" >Edit</a>
						  </@display.column>
						</@display.table>
					</td>
					<td><br><br><br>
					    <@display.table name="myIssuesDB" id="row" class="basic-table">
						  <@display.column title="Status">
						  		<a href="<@ofbizUrl>statusWiseIssueDetail?issueStatusId=${row.issueStatusId}&field=createdBy</@ofbizUrl>" >${row.status}</a>
						  </@display.column>
						  <@display.column title="Count" property="count"/>
						</@display.table>
					</td>
				</tr>
			</table>
		</div>	
	</div>	
	
	<div class="screenlet">
		<div class="screenlet-title-bar" >
				<ul>
					<li class="h3">Owned Issues</li>
					<li class="expanded"><a onclick="javascript:toggleScreenlet(this, 'ownedIssuesScreenlet', 'Expand', 'Collapse');" title="Collapse">&nbsp</a></li>
				<ul>
		</div>
		<div class="screenlet-body" id="ownedIssuesScreenlet">
			<table style="border:0px;width:100%">
				<tr>
					<td>
					    <@display.table name="ownedIssues" id="row" pagesize=10 class="basic-table" defaultsort=8 defaultorder="descending" class="basic-table">
						  <@display.column title="Issue Id" sortable=true>
						  		<a href="<@ofbizUrl>viewIssueDetail?issueId=${row.issueId}&showEdit=true</@ofbizUrl>" >${row.issueId}</a>
						  </@display.column>
						  <@display.column title="Status" property="status" sortable=true/>
						  <@display.column title="Severity" property="severity" sortable=true/>
						  <@display.column title="Category" property="category" sortable=true/>
						  <@display.column title="Sub Category" property="subCategory" sortable=true/>
						  <@display.column title="Assigned To" property="assignedTo" sortable=true/>
						  <@display.column title="Updated By" property="lastUpdatedBy" sortable=true/>
						  <@display.column title="Updated On" property="lastUpdatedOn" sortable=true/>
						  <@display.column title="Reported By" property="createdBy" sortable=true/>
						  <@display.column title="Reported On" property="createdOn" sortable=true/>
						  <@display.column class="button-col align-float">
						  		<a href="<@ofbizUrl>issueEntry?issueId=${row.issueId}&mode=edit</@ofbizUrl>" >Edit</a>
						  </@display.column>
						</@display.table>
					</td>
					<td><br><br><br>
					    <@display.table name="ownedIssuesDB" id="row" class="basic-table">
						  <@display.column title="Status">
						  		<a href="<@ofbizUrl>statusWiseIssueDetail?issueStatusId=${row.issueStatusId}&field=categoryOwner</@ofbizUrl>" >${row.status}</a>
						  </@display.column>
						  <@display.column title="Count" property="count"/>
						</@display.table>
					</td>
				</tr>
			</table>
		</div>	
	</div>	
	