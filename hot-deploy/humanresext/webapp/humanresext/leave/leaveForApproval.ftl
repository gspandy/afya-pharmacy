<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>

<@display.table name="assignedLeaves" id="row" pagesize=10 class="basic-table" defaultsort=2 defaultorder="descending">
		  <@display.column title="Applied By" >
		  		<a href="<@ofbizUrl>leaveMgrView?partyId=${row.partyId}&fromDate=${row.fromDate}</@ofbizUrl>" >
		  		${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,row.partyId,",")}</a>
		  </@display.column>
		  <@display.column title="From " property="fromDate" sortable=true/>
		  <@display.column title="To" property="thruDate" sortable=true/>
		  <@display.column title="Status" property="leaveStatusId" sortable=true/>
		  <@display.column title="Leave Type" property="leaveTypeId"/>
		  <@display.setProperty name="basic.msg.empty_list" value="No Leaves to approve." />
		  <@display.setProperty name="paging.banner.items_name" value="leaves" />
</@display.table>