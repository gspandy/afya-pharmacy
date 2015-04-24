<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">Summary</div>
    </div>
    <div style="overflow:auto">
		    <table style="border:none" width="50px">
		    	<tr>
		    		<td class="label" style="text-align:left">Status</td>
		    		<td>${status}</td>
		    	</tr>
		    	<tr>
		    		<td class="label" style="text-align:left">Self</td>
		    		<td>${selfScore}</td>
		    	</tr>
		    	<#if status!="PERF_IN_PROGRESS">
		    	<tr>
		    		<td class="label" style="text-align:left">Manager</td>
		    		<td>${mgrScore}</td>
		    	</tr>
		    	</#if>
		    	<#if status="PERF_REVIEW_COMPLETE">
		    	<tr>
		    		<td class="label" style="text-align:left">Rating</td>
		    		<td>${rating}</td>
		    	</tr>
		    	</#if>
		    </table>
    </div>
</div>