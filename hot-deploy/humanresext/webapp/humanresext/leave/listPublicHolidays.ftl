<br>
<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<#if publicHolidayList?has_content>
	<div class="screenlet" >
		<div class="screenlet-title-bar" >
			<ul>
				<li class="h3">${uiLabelMap.HumanResPublicHolidays} Calendar</li>
			<ul>
		</div>
		<div class="screenlet-body" id="publicHolidayListScreenlet">
		    <@display.table name="publicHolidayList" id="row" class="basic-table">
			  <@display.column title="Holiday Name" property="description" sortable=true/>
			  <@display.column title="Date " property="onDate" sortable=true/>
			</@display.table>
		</div>	
	</div>	
</#if>
