
<table border="1" width="100%" class="formTable" bordercolor="black" cellspacing="0" cellpadding="0"  style="height:200px">
	<thead style="font-weight:bolder;padding-left:5px">
	<tr>
		<td width="50px">Sl No.</td>

		<td width="100px">Account No.</td>

		<td width="200px">Name of Memeber<br/>(in BLOCK letters)</td>

		<td width="250px">Father's Name or Husband's Name in case of Married Woman</td>

		<td width="150px">Date of Leaving Service</td>

		<td width="250px">Reason for Leaving service</td>		

		<td>Remarks</td>
	</tr>
	</thead>
	<tbody>
	<tr>
			<td style="padding:5px">1.</td>
			<td style="padding:5px">${preference.pfAccountNumber?upper_case}</td>
			<td style="padding:5px">${person.lastName?upper_case}, ${person.firstName?upper_case}</td>
			<td style="padding:5px">${person.nomineeName?if_exists}</td>
			<td style="padding:5px">${terminationRec.terminationDate}</td>
			<td style="padding:5px">${terminationRec.reason}</td>
			<td style="padding:5px">&nbsp;</td>
	</tr>
		
	</tbody>
</table>