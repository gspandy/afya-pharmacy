<style type="text/css">

.srvCell{
border: 1px solid #660000;

}
</style>

<table width="100%" border="1" cellpadding="2px" style="border:1px solid #000;">

		<tr>
			<th colspan="2" >
				Particulars of service in the calendar half year.	
			</th>
			
			<th rowspan=2">
				Completed months of service in the calendar half year.
			</th>
			
			<th  rowspan=2">
				E.L. credited at the beginning of the half year.
			</th>
			
			<th  rowspan=2">
				No.of days of EOL availed of or period treated as diesnon or non duty
				during the previous calendar half year.
			</th>
			
			<th  rowspan=2">
				E.L. to be deducted(1/10 of the period in Col.5) subject to a maximum of 15 days
			</th>
			<th  rowspan=2">
				Total E.L. at credit in days Col.4+11-6)
			</th>
			
			<th colspan="3" style="text-align:center">Leave Taken</th>
			
			<th  rowspan=2">Balance of E.L. at the end of calendar half year col 7-10 </th>
		</tr>
		<tr>
			<th>From</th>
			<th>To</th>
			
			<th>From</th>
			<th>To</th>
			<th>No.of Days</th>
		</tr>
		<tr>
			<td>1</td>
			<td>2</td>
			<td>3</td>
			<td>4</td>
			<td>5</td>
			<td>6</td>
			<td>7</td>
			<td>8</td>
			<td>9</td>
			<td>10</td>
			<td>11</td>
		</tr>
		<#assign prevELBalance=0>
		<#assign prevHalfYearEL=0>
		<#list earnedLeaveList as rec>
		<tr>
			<td >${rec.from?string("yyyy-MM-dd")}</td>
			<td >${rec.to?string("yyyy-MM-dd")}</td>
			<td >${rec.interval?string("0.##")}</td>
			<td >
					<#assign col4=(rec.earnedLeaves-prevHalfYearEL)>
					${col4?string("0.##")}
			</td>
			<td >${rec.noOfPrevLeaves}</td>
			<td >
					${rec.col6}
			</td>
			<td >
				<#assign col7 = (col4+prevELBalance-rec.col6)>
				${col4} + (${prevELBalance})-${rec.col6}=${col7?string("0.##")} </td>
			<td  colspan="3" style="text-align:center">
				<table width="100%" border="0">
					<#list rec.leaves as leave>
					<tr>
						<td>
							${leave.fromDate?string("yyyy-MM-dd") }
						</td>
						<td>
							${leave.thruDate?string("yyyy-MM-dd")}
						</td>
						<td>
							${leave.paidDays}
						</td>
						
					</tr>
					</#list>
				</table>
			</td>
			<td >
				${col7} - (${rec.totalLeaves}) = ${col7 - rec.totalLeaves}
				<#assign prevELBalance=col7 - rec.totalLeaves>
				<#assign prevHalfYearEL=rec.earnedLeaves>
			</td>
		</tr>
		</#list>
</table>
