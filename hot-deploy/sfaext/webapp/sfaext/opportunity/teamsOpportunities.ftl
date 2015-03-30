<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="listIt" id="row" class="basic-table" export=true defaultsort=2 defaultorder="descending">

	<@display.column title="Opportunity Id" sortable=true>
		<a href="/sfa/control/ViewOpportunity?salesOpportunityId=${row.salesOpportunityId}">${row.salesOpportunityId}</a>
	</@display.column>
	  
	<@display.column title="Opportunity Name" sortable=true property="opportunityName"/>
	<@display.column title="Account ID" sortable=true>
		<a href="<@ofbizUrl>viewprofile?partyId=${row.partyIdTo?if_exists}</@ofbizUrl>">${row.partyIdTo?if_exists}</a>
	</@display.column>
	<@display.column title="Estimated Amount" sortable=true property="estimatedAmount"/>
  	<@display.column title="Initial Stage" property="opportunityStageId"/>
  	<@display.column title="Close Date" property="estimatedCloseDate"/>
  
  <@display.setProperty name="paging.banner.items_name" value="Opportunities"/>
  <@display.setProperty name="paging.banner.placement" value="bottom"/>
</@display.table>
