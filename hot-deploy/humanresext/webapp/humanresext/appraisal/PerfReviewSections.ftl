<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">Performance Review Sections</div>
    </div>
    <div class="screenlet-body">
			<#assign sectionMaps=request.getAttribute("perfSections")>
			<ul>
			<#list sectionMaps as sectionMap>					
				<li <#if parameters.sectionId?exists>
						<#if parameters.sectionId=sectionMap.sectionId>class="selected"</#if> 
					</#if>>
				
				<#if request.getAttribute("managerView")?exists && request.getAttribute("managerView")=="enabled">
					<a href="<@ofbizContentUrl>reviewerView</@ofbizContentUrl>?reviewId=${parameters.reviewId}&sectionId=${sectionMap.sectionId}");">${sectionMap.sectionName}</a></li>			
				<#else>	
					<a href="<@ofbizContentUrl>editPerfReview</@ofbizContentUrl>?reviewId=${parameters.reviewId}&sectionId=${sectionMap.sectionId}");">${sectionMap.sectionName}</a></li>			
				</#if>
			</#list>
			</ul>	
			<br>
	</div>
</div>
<br>




