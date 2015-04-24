<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="listIt" id="row" class="basic-table">

  <@display.column title="Party Id" sortable=true>
	<a href="/sfa/control/viewLead?partyId=${row.partyIdTo}">${row.partyIdTo}</a>
  </@display.column>
  <@display.column title="Name" sortable=true>
  	${row.firstName} ${row.lastName} 
  </@display.column>
  <@display.column title="Description" property="partyIdFrom" sortable=true/>

  <@display.setProperty name="paging.banner.items_name" value="Issues"/>
  <@display.setProperty name="paging.banner.placement" value="bottom"/>
</@display.table>
