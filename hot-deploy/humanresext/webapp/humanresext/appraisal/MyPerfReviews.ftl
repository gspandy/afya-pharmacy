<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>

<@display.table name="listIt" id="row" export=true class="basic-table" defaultsort=4 defaultorder="descending">
		  <@display.setProperty name="paging.banner.items_name" value="Performance Reviews" />
		    <@display.column title="${uiLabelMap.HumanResEmplPerfReviewId}">
			<a href="<@ofbizUrl>editPerfReview?reviewId=${row.emplPerfReviewId}</@ofbizUrl>">${row.emplPerfReviewId}</a>		  
		  </@display.column>	
		  <@display.column title="${uiLabelMap.HumanResOverallRating}" property="overallRating" sortable=true/>
		  <@display.column title="${uiLabelMap.HumanResPerfReviewStatus}" property="statusType" sortable=true/>
		  <@display.column title="${uiLabelMap.HumanResSubmittedOn}" property="lastUpdatedStamp" sortable=true/>
  			
</@display.table>


