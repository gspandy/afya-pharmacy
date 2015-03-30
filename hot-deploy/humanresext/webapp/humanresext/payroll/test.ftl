
<form method="POST" action="<@ofbizUrl>SalaryHeadController</@ofbizUrl>">

<table>
<tr>
<td class="label">Select Salary Head</td>
<td>
	<select id="salaryHead" name="computationType" onchange="javascript:form.submit();">
		<option value="---" selected>-------</option>
		<option value="Slab">Basic</option>
		<option value="Formula">HRA</option>
	</select>
</td>
</tr>
</table>
</form>
