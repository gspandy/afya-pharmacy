<br><br>
<form action="<@ofbizUrl>CalculateEmplSal</@ofbizUrl>" name="CalculateEmplSal" method="post">
	<input type="hidden" name="salaryStructureId" value="${parameters.salaryStructureId}">
	<table style="border:0px">
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
				<td class="label">${head.name}</td>
				<td class="label"><input type="text" name="head_${head.salaryStructureHeadId}">*</td>
				<td>(${head.salaryComputationTypeId}, ${head.salaryHeadTypeId})</td>
			<tr>
		</#list>
	</table>
	<input type="submit" value="${uiLabelMap.CommonCreate}">
</form>