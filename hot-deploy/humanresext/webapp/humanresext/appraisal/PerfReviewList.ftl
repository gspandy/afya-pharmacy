<#assign perfEnabled = session.getAttribute("PERF_REVIEW_ENABLED")>
<#if security.hasEntityPermission("HUMANRES","_ADMIN",session)>
		 <#if !perfEnabled>
        	<a href="<@ofbizContentUrl>enablePerfReview</@ofbizContentUrl>"" class="smallSubmit"> Enable Appraisal </a>          	
		</#if>
		<a href="<@ofbizContentUrl>FindPerfReviewTemplates</@ofbizContentUrl>" class="smallSubmit"> ${uiLabelMap.HumanResAddPerfReviewTemplate} </a> 
		
</#if>

		<#if perfEnabled>
			<#if reviewId?exists>
			<#else>
				<a href="<@ofbizContentUrl>newPerfReview</@ofbizContentUrl>" class="smallSubmit"> Self Appraisal </a>
			</#if>
		</#if>

    <div class="screenlet-body">
			
			<#if security.hasEntityPermission("HUMANRES", "_ADMIN", session)>
		  	
          	
          	
          	<#elseif security.hasEntityPermission("HUMANRES", "_MGR", session)>
          		 <#if !perfEnabled>
          	 		Employee Performance Review is not enabled.
          	 	 </#if>
          	<#else>
          	 <#if !perfEnabled>
          	 		Employee Performance Review is not enabled.
          	 </#if>
          	
          	</#if>
    </div>
<br/>