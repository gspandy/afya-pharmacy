<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">Employee Info</div>
    </div>
    
    <div class="screenlet-body">
    		<img src="/smeVisualTheme/images/icon-genericphoto.gif" height="150" width="200"/>
    		<table style="width:100%;border:none">
	    		<tr>
		    		<td>Employee</td><td><a href="<@ofbizUrl>emplPositionView?emplPositionId=${emplPositionId}</@ofbizUrl>" >${empFullName}</a></td>
				</tr>
	    		<tr>
		    		<td>Manager ID</td><td><#if mgrPositionId=="_NA"><a href="<@ofbizUrl>emplPositionView?emplPositionId=${mgrPositionId}</@ofbizUrl>" >${mgrFullName}</a><#else>${mgrFullName}</#if></td>
				</tr>
	    		<tr>
	    			<td>Position</td><td>${positionDetail.positionDesc?if_exists}</td>
				</tr>
	    		<tr>
	    			<td>Department</td><td>${positionDetail.departmentId?if_exists}</td>
				</tr>
			</table>
			
    </div>
</div>
<br/>

<a href="<@ofbizUrl>day</@ofbizUrl>" class="smallSubmit">View Training Calendar</a>
