<div class="screenlet-body">
    <b>
    	Your leave balances for the Year 2008-2009 as of today.<br><br>
    	<table>
	    	<tr>
		    	<td><u>Leave Type</u></td><td><u>Entitled</u></td><td><u>Used</u></td><td><u>Balance</u></td>
	    	</tr>
	    	<#list leaveBalanceByTypeForIndividual as entry>
		    	<tr>
		    		<td>${entry.getLeaveTypeId()}</td><td>${entry.getEntitled()?if_exists}</td><td> ${entry.getUsed()}</td><td> ${entry.getBalance()?if_exists}</td>
		    	</tr>
	    	</#list>
    	</table>
    </b>
</div>
