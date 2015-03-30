
	
	<form method="post" name="initiateAppraisalForm" action="<@ofbizUrl>associateTemplates</@ofbizUrl>">
	
	<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
    <@display.table name="positions" id="position" class="basic-table">
	  <@display.column title="Employee Position">${position.desc}</@display.column>
	  <@display.column title="Templates">
	  		<select name="position_${position.id}">
	  				<#if positionMap.get(position.id)?exists>
	  				<#list positionMap.get(position.id) as template>
	  						<option value="${template}">${template}</option>
	  				</#list>
	  				<#else>
	  						<option value="">--------</option>
	  				</#if>
	  		</select>
	  </@display.column>	
	</@display.table>
		
		<input type="submit" value="Continue"/>
	</form>