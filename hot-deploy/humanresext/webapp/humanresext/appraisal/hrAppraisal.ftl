<form method="post" name="updatePerfReviewByHR" action="<@ofbizUrl>updatePerfReviewByHR</@ofbizUrl>">
<input type="hidden" name="reviewId" value="${parameters.reviewId}"/>

<#if status="PERF_REVIEW_AGREED" || status="PERF_REVIEW_COMPLETE"> 
<table>
	<#if security.hasEntityPermission("HUMANRES","_ADMIN",session)>
	<tr>
		<td class="label">OverAll Rating</td>
		<td><input type="text" name="overallrating" size="25" <#if !security.hasEntityPermission("HUMANRES","_ADMIN",session)>DISABLED</#if>/> </td>
	</tr>
	</#if>
	<tr>
		<td class="label">Comments</td>
		<td><textarea cols=45 rows=3 name="comments" <#if !security.hasEntityPermission("HUMANRES","_ADMIN",session)>DISABLED</#if>><#if perfmap.comments?exists>${perfmap.comments}</#if></textarea></td>
	</tr>
	
	<tr>
		<td class="label">Feedback</td>
		<td><textarea cols=45 rows=3 name="feedback" <#if !security.hasEntityPermission("HUMANRES","_ADMIN",session)>DISABLED</#if>><#if perfmap.feedback?exists>${perfmap.feedback}</#if></textarea></td>
	</tr>
	
	<#if security.hasEntityPermission("HUMANRES","_ADMIN",session)>
	<tr>
		<td colspan="2">
			<input type="submit" value="Close"/>
		</td>
	</tr>
	</#if>
</table>

</#if>
</form>