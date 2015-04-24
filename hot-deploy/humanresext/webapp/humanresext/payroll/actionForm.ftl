<form action="<@ofbizUrl>CreatePayrollAction</@ofbizUrl>" name="ActionForm" method="post">
	<table style="border:0px">
		<tr>
			<td class="label" style="width:20%">Name </td>
		 	<td><input type="text" name="actionName" /></td>
		</tr>
		<tr>
			<td class="label">Operand One</td>
		 	<td><input type="text" name="operandOne"  size="35"/>
		      <a name="lkup" href="javascript:call_fieldlookup2(document.ActionForm.operandOne,'<@ofbizUrl>LookupControl</@ofbizUrl>');">
		      	<img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'>
		      </a>
		 	</td>
		</tr>
		<tr>
			<td class="label">Operand Two</td>
		 	<td><input type="text" name="operandTwo"  size="35"/>
		      <a href="javascript:call_fieldlookup2(document.ActionForm.operandTwo,'<@ofbizUrl>LookupControl</@ofbizUrl>');"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
		 	</td>
		</tr>
		<tr>
			<td class="label">Operator </td>
		 	<td>
		      <select name="operatorId">
		        <#list arithmaticOperators as operator>
		          <option>${operator.operatorId}</option>
		        </#list>
		      </select>
		 	</td>
		</tr>
		
		<tr>
		 	<td class="label">&nbsp;</td>
		 	<td> <input type="submit" value="${uiLabelMap.CommonSubmit}"/></td>
		</tr>
	</table>
</form>
<br/>

