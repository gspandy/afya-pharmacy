<form>
<table>
	<#list policyDetails as pd>
	<tr>
		<td style="text-align:right"><b>${pd.displayName?if_exists}</b></td>
		<#if pd.dataType=="S">
		<td><input type="text" name="${pd.attrName}" value="${pd.attrStrValue?if_exists}"/></td>
		</#if>
		<#if pd.dataType=="N">
		<td><input type="text" name="${pd.attrName}" value="${pd.attrNumValue?if_exists}"/></td>
		</#if>
	</tr>
	</#list>
</table>
</form>	