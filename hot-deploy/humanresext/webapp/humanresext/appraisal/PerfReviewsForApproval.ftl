

<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>

<@display.table name="listForApproval" id="row" export=true class="basic-table" defaultsort=4 defaultorder="descending">
		  <@display.setProperty name="paging.banner.items_name" value="Performance Reviews" />
		   <@display.column title="${uiLabelMap.HumanResEmplPerfReviewId}">
			<a href="<@ofbizUrl>reviewerView?reviewId=${row.emplPerfReviewId}</@ofbizUrl>">${row.emplPerfReviewId}</a>		  
		  </@display.column>
		  <@display.column title="${uiLabelMap.HumanResEmployee}" sortable=true>
				${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,row.partyId," ")}		  
		  </@display.column>
		  <@display.column title="${uiLabelMap.HumanResPerfReviewStatus}" property="statusType" sortable=true/>
		  <@display.column title="${uiLabelMap.HumanResSubmittedOn}" property="lastUpdatedStamp" sortable=true/>
		 
</@display.table>
