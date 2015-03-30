<br>
<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<table class="basic-table"><tr><td>
<div class="screenlet" >
	<div class="screenlet-title-bar" >
		<ul>
			<li class="h3">My Leaves</li>
			<li class="expanded"><a onclick="javascript:toggleScreenlet(this, 'myLeavesScreenlet', 'Expand', 'Collapse');" title="Collapse">&nbsp</a></li>
		<ul>
	</div>
	    <@display.table name="myLeaves" id="row" pagesize=10 class="basic-table" defaultsort=4 defaultorder="descending">
		  <@display.column title="Applied By" >
		  		<a href="<@ofbizUrl>leaveGeneralView?partyId=${row.partyId}&fromDate=${row.fromDate}</@ofbizUrl>" >${row.partyId}</a>
		  </@display.column>
		  <@display.column title="Status" property="leaveStatusId" sortable=true/>
		  <@display.column title="Leave Type" property="leaveTypeId"/>
		  <@display.column title="From " property="fromDate" sortable=true/>
		  <@display.column title="To" property="thruDate" sortable=true/>
		  <@display.setProperty name="paging.banner.one_item_found" value=""/>
		  <@display.setProperty name="paging.banner.onepage" value=""/>
		  <@display.setProperty name="basic.empty.showtable" value="true" />
		  <@display.setProperty name="basic.msg.empty_list" value="No Leaves to display." />
		  <@display.setProperty name="basic.msg.empty_list_row" value=""/>
		  <@display.setProperty name="paging.banner.items_name" value="leaves" />
		  
		</@display.table>
</div>	
</td></tr></table>
<#if security.hasEntityPermission("HUMANRES", "_MGR", session)>

<table class="basic-table"><tr><td>
<div class="screenlet" >
	<div class="screenlet-title-bar" >
		<ul>
			<li class="h3"> Leaves – Pending Approval</li>
			<li class="expanded"><a onclick="javascript:toggleScreenlet(this, 'assignedLeavesScreenlet', 'Expand', 'Collapse');" title="Collapse">&nbsp</a></li>
		<ul>
	</div>
	<div class="screenlet-body" id="assignedLeavesScreenlet >
	    <@display.table name="assignedLeaves" id="row" pagesize=10 class="basic-table" defaultsort=2 defaultorder="descending">
		  <@display.column title="Applied By" >
		  		<a href="<@ofbizUrl>leaveMgrView?partyId=${row.partyId}&fromDate=${row.fromDate}</@ofbizUrl>" >${row.partyId}</a>
		  </@display.column>
		  <@display.column title="From " property="fromDate" sortable=true/>
		  <@display.column title="To" property="thruDate" sortable=true/>
		  <@display.column title="Leave Type" property="leaveTypeId"/>
		  <@display.setProperty name="basic.msg.empty_list" value="No Leaves to approve." />
		  <@display.setProperty name="paging.banner.items_name" value="leaves" />
		</@display.table>
	</div>	
</div>	
</td></tr></table>
</#if>

