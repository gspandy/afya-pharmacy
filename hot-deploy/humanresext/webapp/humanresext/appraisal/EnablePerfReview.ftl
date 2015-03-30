<form method="post" name="initiateAppraisalForm" action="<@ofbizUrl>createPerfReview</@ofbizUrl>">
<table>
	<tr>
		<td width="50%">
			<span class="label">Perf Review For Time Period</span><br/>
			(Select the time period for which the Appraisal has to be initiated)	
		</td>
		<td>
				Period Start Date
				<input type="text" size="25" name="periodStartDate">
				<a href="javascript:call_cal(document.initiateAppraisalForm.periodStartDate,'${nowTimestamp.toString()}');">
  				<img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
  				<br/> Period Thru Date 
				<input type="text" size="25" name="periodThruDate">                        
  				<a href="javascript:call_cal(document.initiateAppraisalForm.periodThruDate,'${nowTimestamp.toString()}');">
  				<img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
 
		<td width="50%">
		</td>
	</tr>
	
	<tr>
		<td width="50%">
			<span class="label">Appraisal Time Period</span><br/>
			(Start date and End date indicates the Time Period for which the Appraisal is carried out,
			which is editable both by the manager and employee.This represents the Time period for which 
			employee has worked with the manager.)
		</td>
		<td width="50%">
				Start Date 
				<input type="text" size="25" name="perfReviewStartDate">                        
  				<a href="javascript:call_cal(document.initiateAppraisalForm.perfReviewStartDate,'${nowTimestamp.toString()}');">
  				<img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
  				<br/> End Date &nbsp; 
				<input type="text" size="25" name="perfReviewEndDate">                        
  				<a href="javascript:call_cal(document.initiateAppraisalForm.perfReviewEndDate,'${nowTimestamp.toString()}');">
  				<img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
  				
  		</td>
	</tr>
	
	<tr>
		<td colspan="2">
				<span style="align:center"><input type="submit" value="Continue"/></span>
		</td>
	</tr>
</table>
</form>