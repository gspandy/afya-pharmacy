<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
<@display.table name="myLeaves" class="basic-table"  id="row" pagesize=10 defaultsort=2 defaultorder="descending">
	  <@display.column title="Applied By" >
	  		<a href="<@ofbizUrl>leaveGeneralView?partyId=${row.partyId}&fromDate=${row.fromDate}</@ofbizUrl>" >
	  				${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,row.partyId,",")}</a>
	  		</a>
	  </@display.column>
	  <@display.column title="From " property="fromDate" sortable=true/>
	  <@display.column title="To" property="thruDate" sortable=true/>
	  <@display.column title="Status" property="leaveStatusId" sortable=true/>
	  <@display.column title="Leave Type" property="leaveTypeId"/>		 
	
	  <@display.column title="Action" style="text-align:center;">
	  		<#if row.leaveStatusId=="APPROVED">
		  		<a  class="smallSubmit" href="<@ofbizUrl>cancelLeave?${row.partyId}&${row.fromDate}</@ofbizUrl>" >CANCEL</a>
	  		</#if>
	  </@display.column>
	  <@display.setProperty name="paging.banner.one_item_found" value=""/>
	  <@display.setProperty name="paging.banner.onepage" value=""/>
	  <@display.setProperty name="basic.empty.showtable" value="true" />
	  <@display.setProperty name="basic.msg.empty_list" value="No Leaves to display." />
	  <@display.setProperty name="basic.msg.empty_list_row" value=""/>
	  <@display.setProperty name="paging.banner.items_name" value="leaves" />
</@display.table>