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

<@display.table name="opportunitiesListIt" id="row" class="basic-table" export=true defaultsort=2 defaultorder="descending">

	<@display.column title="${uiLabelMap.SfaOpportunityName}" sortable=true headerClass="first" class="first">
		<a href="<@ofbizUrl>ViewOpportunity</@ofbizUrl>?salesOpportunityId=${row.salesOpportunityId}">${row.salesOpportunityId}</a>
	</@display.column>
	
	<@display.column title="${uiLabelMap.SfaInitialStage}" sortable=true property="opportunityStageId"/>
	<@display.column title="${uiLabelMap.SfaEstimatedAmount}" sortable=true property="estimatedAmount"/>
	<@display.column title="${uiLabelMap.SfaCloseDate}" property="estimatedCloseDate" headerClass="last" class="last"/>
	
	<@display.setProperty name="paging.banner.items_name" value="Opportunities"/>
	<@display.setProperty name="paging.banner.placement" value="bottom"/>
</@display.table>
