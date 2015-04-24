<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead"><#if selectedSection?exists>${selectedSection.sectionName}<#else>Performance Review</#if></div>
    </div>
    <div class="screenlet-body">
    		
    		
    	<#-- Show the Self Appraisal Form Only if a Section is selected. --->	
    	<#if selectedSection?exists>
    		<form action="<@ofbizContentUrl>saveReview</@ofbizContentUrl>" method="post" name="perfReviewForm">
    		<input type="hidden" name="selectedSectionId" value="${parameters.sectionId}"/>
    		<input type="hidden" name="sectionId" value="${parameters.sectionId}"/>
    		<input type="hidden" name="reviewId" value="${parameters.reviewId}"/>
    		<input type="hidden" name="isReviewed" value="Y"/>
    		<#if selectedSection.get("attributeList")?exists>
			<table style="width:100%">
				<thead>
						${selectedSection.description} <br><br>
						<span class="label">Weightage ${selectedSection.weightage}</span>
				</thead>
	    		<tr>
	    			<th>Description</th>
	    			<th>Rating & Comments</th>
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
						<select disabled>
						 <#list ratingList as row>
						 			 <option value="${row.rating}" <#if attribute.selfRating?exists><#if attribute.selfRating==row.rating> selected="selected"</#if></#if>>${row.ratingDesc}</option>
						 </#list>
						 </select>
						 <br><br>
						 <span class="label">Comments</span><br/>
						 <textarea  cols="45" rows="2" disabled><#if attribute.selfComment?exists>${attribute.selfComment}</#if></textarea>
					</td>
					
					<td>						
						<span class="label"> Manager Ratings </span><br>
						<select name="perf_attr_${attribute.attributeId}">
						 <#list ratingList as row>
						 			 <option value="${row.rating}" <#if attribute.rating?exists><#if attribute.rating==row.rating> selected="selected"</#if></#if>>${row.ratingDesc}</option>
						 </#list>
						 </select>
						<ul>
	 						 <li class="expanded"><a onclick="javascript:toggleScreenlet(this, '${attribute.attributeId}', 'Expand', 'Collapse');" title="Collapse">Click to close Rating Description</a></li>
						</ul>
						<div id="${attribute.attributeId}" style="width:60%;wrap:true">
							${attribute.attributeDesc?if_exists}
						</div>
						<br><br>
						 <span class="label">Comments</span><br/>
						 <textarea name="perf_attr_${attribute.attributeId}_comments" cols="45" rows="2"><#if attribute.reviewerComment?exists>${attribute.reviewerComment}</#if></textarea>
					</td>
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
						<select disabled>
						 <#list ratingList as row>
						 			 <option value="${row.rating}" <#if selectedSection.selfRating?exists><#if selectedSection.selfRating==row.rating> selected="selected"</#if></#if>>${row.ratingDesc}</option>
						 </#list>
						 </select>
					</td>
				</tr>
				<tr>
					<td class="label">Describe</td>
					<td>
						<TEXTAREA COLS=100 ROWS=10 disabled><#if selectedSection.selfComment?exists>${selectedSection.selfComment}</#if></TEXTAREA>
					</td>
				</tr>
				
				<tr>
					<td class="label">						
						 Manager Rating		
					</td>
					<td>	
						<select name="perf_sec_${selectedSection.id}">
						 <#list ratingList as row>
						 			 <option value="${row.rating}" <#if selectedSection.appraiserRating?exists><#if selectedSection.appraiserRating==row.rating> selected="selected"</#if></#if>>${row.ratingDesc}</option>
						 </#list>
						 </select>
					</td>
				</tr>
				<tr>
					<td class="label">Comments</td>
					<td>
						<TEXTAREA NAME="perf_sec_${selectedSection.id}_comments" COLS=100 ROWS=3><#if selectedSection.appraiserComment?exists>${selectedSection.appraiserComment}</#if></TEXTAREA>
					</td>
				</tr>
				</table>
			
			</#if>
			</form>
			
		<#else>
				Please select the Performance Review Section from the Left Bar to continue.
			
		</#if>
		
			<#if status=="PERF_REVIEW_DISAGREE"> 
				<table>
				<tr>
				<td class="label">Reasons to Disagree</td>
				<td>
				<textarea disabled cols=45 rows=3>${disagreedComments?if_exists}</textarea>
				</td>
				</tr>
				</table>					
			</#if>
    </div>
</div>
<br/>