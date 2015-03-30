<#if showEdit?has_content>
	&nbsp;<a href="<@ofbizUrl>issueEntry?issueId=${issue.issueId}&mode=edit</@ofbizUrl>" class="btn btn-success">Edit</a>
</#if>
<table style="width:100%;border:0px">
	<tr>
		<td style="width:30em">
			<div class="screenlet">
				<div class="screenlet-title-bar"><h4>Issue Basic Info</h4></div>
				<div class="screenlet-body">
					<table class="basic-table">
						<tr>
							<td>Severity : </td><td>${issue.issueSeverityCaption?if_exists}</td>
						</tr>
						<tr>
							<td>Status : </td><td>${issue.issueStatusCaption?if_exists}</td>
						</tr>
						<tr>
							<td>Category : </td><td>${issue.issueCategoryCaption?if_exists}</td>
						</tr>
						<tr>
							<td>Sub Category : </td><td>${issue.issueSubCategoryCaption?if_exists}</td>
						</tr>
						<tr>
							<#if issue.assignedTo?has_content>
								<#assign assignedToPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":issue.assignedTo})>
								<#if assignedToPerson?has_content>
									<#assign assignedToPersonName = assignedToPerson.firstName?if_exists + " " + assignedToPerson.lastName?if_exists + " " + assignedToPerson.groupName?if_exists>
									<td>Assigned To : </td><td>${assignedToPersonName?if_exists}</td>
								<#else>
									<td>Assigned To : </td><td>${issue.assignedTo?if_exists}</td>
								</#if>
							<#else>
								<td>Assigned To : </td><td>${issue.assignedTo?if_exists}</td>
							</#if>
						</tr>
						<tr>
							<#if issue.ownerId?has_content>
								<#assign ownerPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":issue.ownerId})>
								<#if ownerPerson?has_content>
									<#assign ownerPersonName = ownerPerson.firstName?if_exists + " " + ownerPerson.lastName?if_exists + " " + ownerPerson.groupName?if_exists>
									<td>Owner : </td><td>${ownerPersonName?if_exists}</td>
								<#else>
									<td>Owner : </td><td>${issue.ownerId?if_exists}</td>
								</#if>
							<#else>
								<td>Owner : </td><td>${issue.ownerId?if_exists}</td>
							</#if>
						</tr>
						<tr>
							<#if issue.lastUpdatedBy?has_content>
								<#assign lastUpdatedByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":issue.lastUpdatedBy})>
								<#if lastUpdatedByPerson?has_content>
									<#assign lastUpdatedByPersonName = lastUpdatedByPerson.firstName?if_exists + " " + lastUpdatedByPerson.lastName?if_exists + " " + lastUpdatedByPerson.groupName?if_exists>
									<td>Last Updated By : </td><td>${lastUpdatedByPersonName?if_exists}</td>
								<#else>
									<td>Last Updated By : </td><td>${issue.lastUpdatedBy?if_exists}</td>
								</#if>
							<#else>
								<td>Last Updated By : </td><td>${issue.lastUpdatedBy?if_exists}</td>
							</#if>
						</tr>
						<tr>
							<td>Last Updated On : </td><td>${issue.lastUpdatedOn?if_exists}</td>
						</tr>
						<tr>
							<#if issue.createdBy?has_content>
								<#assign createdByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":issue.createdBy})>
								<#if createdByPerson?has_content>
									<#assign createdByPersonName = createdByPerson.firstName?if_exists + " " + createdByPerson.lastName?if_exists + " " + createdByPerson.groupName?if_exists>
									<td>Reported By : </td><td>${createdByPersonName?if_exists}</td>
								<#else>
									<td>Reported By : </td><td>${issue.createdBy?if_exists}</td>
								</#if>
							<#else>
								<td>Reported By : </td><td>${issue.createdBy?if_exists}</td>
							</#if>
						</tr>
						<tr>
							<td>Reported On : </td><td>${issue.createdOn?if_exists}</td>
						</tr>
					</table>
				</div>
			</div>
		</td>
	<td>
			<div class="screenlet">
				<div class="screenlet-title-bar"><h4>Issue Details</h4></div>
				<div class="screenlet-body">
					<table class="basic-table" >
						<tr>
							<td><br></td>
						</tr>
						<tr>
							<td><div class="header-row">Summary</div></td>
						</tr>
						<tr>
							<td>${issue.issueSummary?if_exists}</td>
						</tr>
						<tr>
							<td><br></td>
						</tr>
						<tr>
							<td><div class="header-row">Description</div></td>
						</tr>
						<tr>
							<td>${issue.issueDescription?if_exists}</td>
						</tr>
						<#if issue.issueAdditionalInfo?has_content>
						<tr>
							<td><br></td>
						</tr>
						
						<tr>
							<td><div class="header-row">Additional Info</div></td>
						</tr>
						<tr>
							<td>${issue.issueAdditionalInfo?if_exists}</td>
						</tr>
						<tr>
							<td><br></td>
						</tr>
						</#if>
					</table>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="3" style="border:2px">
			<#include "component://issuetracking/webapp/issuetracking/issue/showAttachments.ftl" />
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<#include "component://issuetracking/webapp/issuetracking/issue/showResponses.ftl" />
		</td>
	</tr>
</table>