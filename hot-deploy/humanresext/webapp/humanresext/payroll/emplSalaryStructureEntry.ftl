<br><br>

<script type="text/javascript" src="&#47;images&#47;calendar_date_select.js" type="text/javascript"></script>
		 
<form action="<@ofbizUrl>CalculateEmplSal</@ofbizUrl>" name="CreateEmplSal" method="post">
	<input type="hidden" name="salaryStructureId" value="${parameters.salaryStructureId}">
	<table class="basic-table">
		<tr>
			<td class="label">Employee Id</td>
			<td class="label"><input type="text" name="partyId">*</td>
			<td>
		      <a href="javascript:call_fieldlookup2(document.CreateEmplSal.partyId,'<@ofbizUrl>LookupPartyName</@ofbizUrl>');">
		      	<img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'>
		      </a>
			</td>
		<tr>

		<#list heads as head>
			<tr>
				<td class="label">${head.hrName}</td>
				<td class="label"><input type="text" name="head_${head.salaryStructureHeadId}">*</td>
				<td>(${head.salaryComputationTypeId}, ${head.salaryHeadTypeId})</td>
			<tr>
		</#list>
		
		<tr>
			<td class="label">From Date</td>
			<td><input type="text" name="fromDate" id="fromDate" /><a href="javascript:call_cal_notime(document.CreateEmplSal.fromDate, document.getElementById('fromDate'));">
				<img src="/images/cal.gif" width="16" height="16" border="0" alt="View Calendar" title="View Calendar"/></a>
			</td>
		</tr>
		<tr>
			<td class="label">Is it an Offer? (Y/N)</td>
			<td><input type="text" name="offerQ" id="offerQ"/>*</td>
		</tr>
		<tr>
			<td class="label">Offer Id</td>
			<td><input type="text" name="offerId" id="offerId"/></td>
			<td>
		      <a href="javascript:call_fieldlookup2(document.CreateEmplSal.offerId,'<@ofbizUrl>LookupOffer</@ofbizUrl>');">
		      	<img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'>
		      </a>*If (Y)es required
			</td>
		</tr>
		<tr>
			<td class="label"></td>
			<td colspan="1" style="text-align:center"><input type="submit" value="Preview"></td>
		</tr>
	</table>
	<br/><br/>
</form>