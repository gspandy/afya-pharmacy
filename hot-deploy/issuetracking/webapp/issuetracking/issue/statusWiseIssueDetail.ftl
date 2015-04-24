<style type="text/css">
.basic-table tr th, .basic-table .header-row {
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
<@display.table name="issueList" id="issueRow" class="basic-table header-row" pagesize=20 export=true defaultsort=10 defaultorder="descending">
	<@display.column title="Issue Id" property="issueId" sortable=true>
		<#--<a href="<@ofbizUrl>viewIssueDetail?issueId=${issueRow.issueId}</@ofbizUrl>" >${issueRow.issueId}</a>-->
	</@display.column>
	<@display.column title="Status" property="status" sortable=true/>
	<@display.column title="Severity" property="severity" sortable=true/>
	<@display.column title="Category" property="category" sortable=true/>
	<@display.column title="Sub Category" property="subCategory" sortable=true/>
	<#if issueRow.categoryOwner?has_content>
		<#assign categoryOwnerPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":issueRow.categoryOwner})>
			<#if categoryOwnerPerson?has_content>
				<#assign categoryOwnerName = categoryOwnerPerson.firstName?if_exists + " " + categoryOwnerPerson.lastName?if_exists + " " + categoryOwnerPerson.groupName?if_exists>
				<@display.column title="Owner" value="${categoryOwnerName}" sortable=true/>
			<#else>
				<@display.column title="Owner" property="categoryOwner" sortable=true/>
			</#if>
		<#else>
			<@display.column title="Owner" property="categoryOwner" sortable=true/>
		</#if>
		<#if issueRow.assignedTo?has_content>
			<#assign assignedToPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":issueRow.assignedTo})>
			<#if assignedToPerson?has_content>
				<#assign assignedToName = assignedToPerson.firstName?if_exists + " " + assignedToPerson.lastName?if_exists + " " + assignedToPerson.groupName?if_exists>
				<@display.column title="Assigned To" value="${assignedToName}" sortable=true/>
			<#else>
				<@display.column title="Assigned To" property="assignedTo" sortable=true/>
			</#if>
		<#else>
			<@display.column title="Assigned To" property="assignedTo" sortable=true/>
		</#if>
		<#if issueRow.createdBy?has_content>
			<#assign createdByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":issueRow.lastUpdatedBy})>
			<#if createdByPerson?has_content>
				<#assign createdByName = createdByPerson.firstName?if_exists + " " + createdByPerson.lastName?if_exists + " " + createdByPerson.groupName?if_exists>
				<@display.column title="Reported By" value="${createdByName}" sortable=true/>
			<#else>
				<@display.column title="Reported By" property="createdBy" sortable=true/>
			</#if>
		<#else>
			<@display.column title="Reported By" property="createdBy" sortable=true/>
		</#if>
	<@display.column title="Reported On" property="createdOn" sortable=true/>
	<@display.column title="Updated On" property="lastUpdatedOn" sortable=true/>
	<@display.setProperty name="paging.banner.items_name" value="Issues"/>
	<#-- <@display.column class="button-col align-float">
		<a href="<@ofbizUrl>issueEntry?issueId=${issueRow.issueId}&mode=edit</@ofbizUrl>" >Edit</a>
	</@display.column>-->
</@display.table>

<#if lookupErrorMessage?exists>
	<h3>${lookupErrorMessage}</h3>
</#if>

