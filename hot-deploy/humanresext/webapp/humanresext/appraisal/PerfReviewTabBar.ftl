<div class="button-style-1">
		
		<#if request.getAttribute("managerView")?exists && request.getAttribute("managerView")=="enabled">
				<#if status=="PERF_REVIEW_IN_PROGRESS" || status=="PERF_REVIEW_PENDING" || status="PERF_REVIEW_DISAGREE">
						<a href="<@ofbizContentUrl>submitReview?reviewId=${parameters.reviewId}</@ofbizContentUrl>" class="smallSubmit">Submit</a>
						<a href="javascript:document.perfReviewForm.submit();" class="smallSubmit">Save</a>
			 	</#if>
		<#elseif status=="PERF_IN_PROGRESS">
			<a href="<@ofbizContentUrl>submitPerfReview?reviewId=${parameters.reviewId}</@ofbizContentUrl>" class="smallSubmit">Submit</a>
			<a href="javascript:document.perfReviewForm.submit();" class="smallSubmit">Save</a>
		</#if>
		
		
		<#if status=="PERF_REVIEWED_BY_MGR">
			<a href="<@ofbizContentUrl>markAgreed?reviewId=${parameters.reviewId}</@ofbizContentUrl>" class="smallSubmit">Agree</a>
			<a href="javascript:submitForm(document.perfReviewForm,'<@ofbizContentUrl>markDisAgreed</@ofbizContentUrl>');" class="smallSubmit">Disagree</a>
		</#if>
		
		<#if status=="PERF_REVIEW_COMPLETE">
				<a href="<@ofbizContentUrl>PerformanceReview.pdf?reviewId=${parameters.reviewId}</@ofbizContentUrl>" class="smallSubmit">Print</a>
		</#if>
		
	
</div>


<br/>