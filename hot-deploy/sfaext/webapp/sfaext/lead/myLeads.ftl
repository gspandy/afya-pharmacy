<style type="text/css">
.top-table{
	border-color: -moz-use-text-color #DADADA #DADADA;
	border-right: 1px solid #DADADA;
	border-style: none solid solid;
	border-width: 1px;
	margin-top: 8px;
	width: 100%;
	min-height:100px;
}

.basic-table tr th,.basic-table .header-row {
	background-color: #F2F2F2;
	border: 0.1em solid #CCCCCC;
	color: #555555;
	font-weight: bold;
	height: 25px;
	padding-left: 10px;
	text-align: left;
	width: auto;
}
</style>

<div id="table">
<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="listIt" id="row" class="basic-table" export=true defaultsort=2 defaultorder="descending">
  <@display.column title="Lead Id" sortable=true headerClass="first" class="first">
	<a href="<@ofbizUrl>viewLead?partyId=${row.partyIdFrom}</@ofbizUrl>">${row.partyIdFrom}</a>
  </@display.column>
  <@display.column title="First Name" sortable=true>
  	${row.firstName?if_exists}
  </@display.column>
  
  <@display.column title="Last Name" sortable=true>
  	${row.lastName?if_exists}
  </@display.column>

  <@display.column title="Company Name" sortable=true>
  	${row.companyName?if_exists}
  </@display.column>

  <@display.column title="Address">
	${row.primaryAddress1?if_exists} ${row.primaryAddress2?if_exists}
  </@display.column>

  <@display.column title="State/Province" property="stateProvinceGeoId" sortable=true/>
  <@display.column title="City" property="primaryCity" sortable=true/>
  <@display.column title="Phone Number">
	${row.primaryCountryCode?if_exists} ${row.primaryAreaCode?if_exists} ${row.primaryContactNumber?if_exists}
  </@display.column>
  <@display.column title="Email" property="primaryEmail" sortable=true headerClass="last" class="last"/>
 


  <@display.setProperty name="paging.banner.items_name" value="Leads"/>
  <@display.setProperty name="paging.banner.placement" value="bottom"/>
</@display.table>

</div>
