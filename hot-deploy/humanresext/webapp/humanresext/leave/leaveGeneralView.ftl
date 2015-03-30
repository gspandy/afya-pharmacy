<table style="width:80%;border:0px">
	<tr>
		<td>
			<div class="screenlet">
				<div class="screenlet-title-bar"><h4>Leave Details</h4></div>
				<div class="screenlet-body">
					<table class="basic-table" border="1px">
						<tr>
							<td class="label">Applied By</td><td>${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,leave.partyId,",")}</td>
						</tr>
						<tr>
							<td class="label">From Date</td><td>${leave.fromDate?if_exists}</td>
						</tr>
						<tr>
							<td class="label">To Date</td><td>${leave.thruDate?if_exists}</td>
						</tr>
						<tr>
							<td class="label">Working Days</td><td>${leave.numberOfWorkingDays?if_exists}</td>
						</tr>
						<tr>
							<td class="label">Leave Type</td><td>${leave.leaveTypeId?if_exists}</td>
						</tr>
						<tr>
							<td class="label">Status</td><td>${leave.leaveStatusId?if_exists}</td>
						</tr>
						<#if leave.addsWhileOnLeave?has_content>
							<tr>
								<td class="label">Address during leave</td>
								<td><textarea disabled cols="40" rows="3">${leave.addsWhileOnLeave}</textarea></td>
							</tr>
						</#if>
						<tr>
							<td class="label"><br>Description </td>
							<td><textarea disabled cols="40" rows="3"${leave.description?if_exists}</textarea></td>
						</tr>
						<#if leave.mgrComment?exists>
							<tr>
								<td class="label"><br>Manager's Comment</td>
								<td><textarea disabled cols="40" rows="3">${leave.mgrComment}</textarea></td>
							</tr>
						</#if>
					</table>
				</div>
			</div>
		</td>
	</tr>
</table>