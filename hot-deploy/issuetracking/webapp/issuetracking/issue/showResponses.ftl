<style type="text/css">
.basic-table tr th,.basic-table .header-row1 {
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
<#if comments?has_content>
	<#assign display=JspTaglibs["/WEB-INF/displaytag.tld"]/>
	<div class="screenlet">
		<div class="screenlet-title-bar"><h4>Issue History</h4></div>
		<div class="screenlet-body">
			<table class="basic-table">
				<@display.table name="comments" id="commentRow" defaultsort=3 defaultorder="descending" style="width:100%" class="basic-table header-row1">
					<@display.column title="Issue Status" property="issueStatusCaption" sortable=false />
					<#if commentRow.commentedBy?has_content>
						<#assign commentedByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":commentRow.commentedBy})>
						<#if commentedByPerson?has_content>
							<#assign commentedByName = commentedByPerson.firstName?if_exists + " " + commentedByPerson.lastName?if_exists + " " + commentedByPerson.groupName?if_exists>
							<@display.column title="Updated By" value="${commentedByName}" sortable=false/>
						<#else>
							<@display.column title="Updated By" property="commentedBy" sortable=false/>
						</#if>
					<#else>
						<@display.column title="Updated By" property="commentedBy" sortable=false/>
					</#if>
					<@display.column title="Response" property="response" sortable=false/>
					<@display.column title="Updated Date" property="createdStamp" sortable=false/>
				</@display.table>
			</table>
		</div>
	</div>
</#if>
