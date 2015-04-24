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

<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="listIt" id="row" class="basic-table" export=true defaultsort=8 defaultorder="descending">
	
<@display.column title="${uiLabelMap.CrmAccountName}" headerClass="first" class="first">
		<a href="<@ofbizUrl>viewAccount?partyId=${row.partyId?if_exists}</@ofbizUrl>"> ${row.groupName} (${row.partyId}) </a>
</@display.column>

<@display.column title="${uiLabelMap.CommonCity}">
		${row.primaryCity?if_exists} ${row.primaryStateProvinceGeoId?if_exists}
</@display.column>

<@display.column title="${uiLabelMap.CrmPrimaryEmail}">
		${row.primaryEmail?if_exists}
</@display.column>


<@display.column title="${uiLabelMap.CrmPrimaryPhone}" headerClass="last" class="last">
		 ${row.primaryCountryCode?if_exists} ${row.primaryAreaCode?if_exists} ${row.primaryContactNumber?if_exists}
</@display.column>

</@display.table>			
