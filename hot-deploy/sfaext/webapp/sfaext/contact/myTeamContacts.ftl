<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="listIt" id="row" class="basic-table" defaultsort=8 defaultorder="descending" class="basic-table">
  	    <@display.column title="${uiLabelMap.PartyPartyId}">
		  <a href="<@ofbizUrl>viewContact?partyId=${row.partyId}</@ofbizUrl>">${row.partyId}</a>
 		</@display.column>
 		<@display.column title="${uiLabelMap.PartyFirstName}">
		  ${row.firstName}
 		</@display.column>
 		<@display.column title="${uiLabelMap.PartyLastName}">
		  ${row.lastName?if_exists}
 		</@display.column>
</@display.table>			
