<table style="width:100%;border:0px">
	<tr>
		<td>
			<#include "component://humanresext/webapp/humanresext/leave/leaveGeneralView.ftl" />
		</td>
		<td>&nbsp;</td>
		<td>
			<form method="post" action="">
				<input type="hidden" name="partyId" value="${leave.partyId}"/>
				<input type="hidden" name="fromDate" value="${leave.fromDate}"/>
				<table style="border:0px">
					<tr>
						<br><br><b>&nbsp;&nbsp;&nbsp;Approval/Rejection Reason</b>
					</tr>
					<tr>
						<td> 
							<input type="text" name="mgrComment" style="width:500px;height:80px"><br><br>
						</td>
					</tr>
					<tr>
						<td>
							<br><br>&nbsp;
							<input type="submit" value="Approve" onclick="this.form.action = '<@ofbizUrl>approveLeave</@ofbizUrl>';return true;">
							&nbsp;
							<input type="submit" value="Reject" onclick="this.form.action = '<@ofbizUrl>rejectLeave</@ofbizUrl>';return true;">
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<div class="screenlet">
				<div class="screenlet-title-bar"><h4>Leave applied by others during the same period</h4></div>
				<div class="screenlet-body">
				    <#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
				    <@display.table name="impactingLeaves" id="row" style="width:100%">
					  <@display.column title="Applied By" >
					  		<a href="<@ofbizUrl>leaveMgrView?partyId=${row.partyId}&fromDate=${row.fromDate}</@ofbizUrl>" >
								${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,row.partyId,",")}					  		
							</a>
					  </@display.column>
					  <@display.column title="Status" property="leaveStatusId" sortable=true/>
					  <@display.column title="Leave Type" property="leaveTypeId" sortable=true/>
					  <@display.column title="From Date" property="fromDate" sortable=true/>
					  <@display.column title="To Date" property="thruDate" sortable=true/>
					</@display.table>
				</div>
			</div>
		</td>
	</tr>   
</table>