<#if item.itemTypeId=="NUMERIC">
	<input type="input" name="${fieldName}_N" value="${item.numValue?if_exists}"/>
<#elseif item.itemTypeId=="LONGTEXT">
	<textarea rows=3 cols=45 name="${fieldName}_S">${item.stringValue?if_exists}</textarea>
<#elseif item.itemTypeId=="BOOLEAN">
	<select name="${fieldName}_S">
		<option <#if item.stringValue?exists> <#if item.stringValue=="Y">selected</#if> </#if>>Y</option>
		<option <#if item.stringValue?exists> <#if item.stringValue=="N">selected</#if> </#if>>N</option>
	</select>
<#elseif item.itemTypeId=="DATE">
	<input type="input" name="${fieldName}_S" value="${item.stringValue?if_exists}"/>
    <a href="javascript:call_cal(document.${formName}.${fieldName}_S, null);" title="View Calendar"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="View Calendar"></a></td>
<#elseif item.itemTypeId=="TEXT">
	<input type="input" name="${fieldName}_S" value="${item.stringValue?if_exists}"/>
</#if>