<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="listIt" id="row" class="basic-table" defaultsort=8 defaultorder="descending" class="basic-table">
  	    <@display.column title="${uiLabelMap.CrmAccountId}">
		  <a href="<@ofbizUrl>viewAccount?partyId=${row.partyId?if_exists}</@ofbizUrl>">${row.partyId?if_exists}</a>
 		</@display.column>
 		<@display.column title="${uiLabelMap.AccountName}">
		  ${row.groupName?if_exists}
 		</@display.column>
</@display.table>			
