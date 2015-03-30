<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="trainings" id="row" class="basic-table" defaultsort=4>
  <@display.column title="Training Id" sortable=true>
  		<a href="<@ofbizUrl>ViewTraining?trainingId=${row.trainingId}</@ofbizUrl>" >${row.trainingId}</a>
  </@display.column>
  <@display.column title="Training Type" property="trainingClassTypeId" sortable=true/>
  <@display.column title="Name" property="trainingName" sortable=true/>
  <@display.column title="Estimated Start Date" property="estimatedStartDate" sortable=true/>
  <@display.column title="Estimated Completion Date" property="estimatedCompletionDate" sortable=true/>

  <@display.setProperty name="paging.banner.items_name" value="Trainings"/>
  <@display.setProperty name="paging.banner.placement" value="bottom"/>
</@display.table>
