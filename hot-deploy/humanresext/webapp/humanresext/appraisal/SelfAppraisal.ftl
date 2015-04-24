<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead"><#if selectedSection?exists>${selectedSection.sectionName}<#else>Performance Review</#if></div>
    </div>
    <div class="screenlet-body">
    	<script language="JavaScript" type="text/javascript">
    		function submitReviewerId(opt){
    			if(opt.value!='---'){
    				document.perfReviewForm.action="<@ofbizContentUrl>editPerfReview</@ofbizContentUrl>";
    				document.perfReviewForm.submit();
    			}
    		}
    	</script>
    		
 		<form action="<@ofbizContentUrl>savePerfReview</@ofbizContentUrl>" method="post" name="perfReviewForm">
    	<#-- Show the Self Appraisal Form Only if a Section is selected. --->	
    	<#if selectedSection?exists>
    		<input type="hidden" name="selectedSectionId" value="${parameters.sectionId}"/>
    		<input type="hidden" name="sectionId" value="${parameters.sectionId}"/>
    		<input type="hidden" name="reviewId" value="${parameters.reviewId}"/>
    		
    		<#if selectedSection.get("attributeList")?exists>
			<table style="width:100%">
				<thead>
						${selectedSection.description} <br><br>
						<span class="label">Weightage ${selectedSection.weightage}</span>
				</thead>
	    		<tr>
	    			<th>Description</th>
	    			<th>Rating & Comments</th>
	    			<#if status=="PERF_REVIEW_AGREED" || status=="PERF_REVIEWED_BY_MGR" || status=="PERF_REVIEW_COMPLETE">
						<th> Manager Rating & Comments  <br/>
						<#if reviewers?exists>
						<span class="label"> Select Manager </span>
						<select name="reviewerId" onChange="javascript:submitReviewerId(this);">
							<option default>---</option>
							<#list reviewers as reviewer>
								<option value="${reviewer}" <#if reviewerId?exists><#if reviewer==reviewerId> selected </#if></#if>>${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,reviewer," ")}</option>
							</#list>
						</select>
						<br>
					</#if>	
						</th>	
	    			</#if>
	    		</tr>

    		<#assign i=0>
    		<#assign rowClass=""/>
    		<#list selectedSection.get("attributeList") as attribute>
    			<#if i%2==0>
    				<#assign rowClass="even">
    			</#if>		
    			
				<tr class="${rowClass}">
					<td class="label" style="width:25%">${attribute.attributeName}</td>
					<td>						
						<span class="label"> Employee Ratings </span><br>
						<select name="perf_attr_${attribute.attributeId}" <#if status!="PERF_IN_PROGRESS"> disabled </#if>>
						 <#list ratingList as row>
						 			 <option value="${row.rating}" <#if attribute.selfRating?exists><#if attribute.selfRating==row.rating> selected="selected"</#if></#if>>${row.ratingDesc}</option>
						 </#list>
						 </select>
						<ul>
	 						 <li class="collapse"><a onclick="javascript:toggleScreenlet(this, '${attribute.attributeId}', 'Expand', 'Collapse');" title="Collapse">Rating Description</a></li>
						</ul>
						<div id="${attribute.attributeId}" style="width:40%;wrap:true">
							${attribute.attributeDesc?if_exists}
						</div>
						<br><br>
						 <span class="label">Comments</span><br/>
						 <textarea <#if status!="PERF_IN_PROGRESS"> disabled </#if> name="perf_attr_${attribute.attributeId}_comments" cols="45" rows="2"><#if attribute.selfComment?exists>${attribute.selfComment}</#if></textarea>
					</td>
					<#if status=="PERF_REVIEW_AGREED" || status=="PERF_REVIEWED_BY_MGR"  || status=="PERF_REVIEW_COMPLETE">
	    			<td>						
						<span class="label"> Manager Ratings </span><br>
						<select disabled>
						 <#list ratingList as row>
						 	<option value="${row.rating}" <#if attribute.rating?exists><#if attribute.rating==row.rating> selected="selected"</#if></#if>>${row.ratingDesc}</option>
						 </#list>
						 </select>
						 <span class="label">Comments</span><br/>
						 <textarea disabled cols="45" rows="2"><#if attribute.reviewerComment?exists>${attribute.reviewerComment}</#if></textarea>
					</td>
	
	    			</#if>
				</tr>
				<#assign i = i+1>
				<#assign rowClass=""/>
			</#list>
			
			</table>
			<#else>
			<table style="width:100%">
				<thead>
						${selectedSection.description} <br><br>
						<span class="label">Weightage ${selectedSection.weightage}</span>
				</thead>
	    		<tr>
					<td class="label">						
						 Empoyee Rating		
					</td>
					<td>	
						<select name="perf_sec_${selectedSection.sectionId}" <#if status!="PERF_IN_PROGRESS"> disabled </#if>>
						 <#list ratingList as row>
						 			 <option value="${row.rating}">${row.ratingDesc}</option>
						 </#list>
						 </select>
					</td>
				</tr>
				<tr>
					<td class="label">Describe</td>
					<td>
						<TEXTAREA NAME="perf_sec_${selectedSection.sectionId}_comments" <#if status!="PERF_IN_PROGRESS"> disabled </#if> COLS=100 ROWS=10><#if selectedSection.selfComment?exists>${selectedSection.selfComment}</#if></TEXTAREA>
					</td>
				</tr>
	    		<#if status=="PERF_REVIEW_AGREED" || status=="PERF_REVIEWED_BY_MGR"  || status=="PERF_REVIEW_COMPLETE">
	    		<tr>
					<td class="label">						
						 Manager Rating		
					</td>
					<td>	
						<select disabled>
						 <#list ratingList as row>
						 			 <option value="${row.rating}" <#if selectedSection.rating?exists><#if selectedSection.rating==row.rating> selected="selected"</#if></#if>>${row.ratingDesc}</option>
						 </#list>
						 </select>
					</td>
				</tr>
				<tr>
					<td class="label">Comments</td>
					<td>
						<TEXTAREA disabled COLS=100 ROWS=3> <#if selectedSection.reviewerComment?exists>${selectedSection.reviewerComment}</#if></TEXTAREA>
					</td>
				</tr>
				</#if>
				</table>
			</#if>
			
			<#if status=="PERF_REVIEWED_BY_MGR"> 
				<table>
				<tr>
				<td class="label">Reasons to Disagree if any</td>
				<td>
				<textarea name="disagreedComments" cols=45 rows=3></textarea>
				</td>
				</tr>
				</table>					
			</#if>
						
		<#else>
				Please select the Performance Review Section from the Left Bar to continue.
			
		</#if>
		</form>
    </div>
</div>
<br/>