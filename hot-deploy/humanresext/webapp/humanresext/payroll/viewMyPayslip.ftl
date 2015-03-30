<br>
<span style="text-align:center">
	<h2>Payslip For Period ${payslipHeader.periodFrom} - ${payslipHeader.periodTo}</h2>
</span>
<br>

<table class="basic-table">
<tr>
<#if prefHeader?exists>
<table style="width:100%;border:1px solid black;">
	<caption><h2>Employee Details</h2></caption>
	
	<tbody>
			<#assign partyName=prefHeader.firstName + " " + prefHeader.lastName>
			<tr>
				<td>Party Id</td>
				<td style="width:40%;text-align:right">${prefHeader.partyId}</td>
				<td>Name</td>
				<td style="width:40%;text-align:right">${partyName}</td>
			</tr>
			<tr>
				<td>Bank Name</td>
				<td style="width:40%;text-align:right">${prefHeader.bankName}</td>
				<td>Bank Account Number</td>
				<td style="width:40%;text-align:right">${prefHeader.bankAccountNumber?if_exists}</td>
			</tr>
			<tr>
				<td>PAN</td>
				<td style="width:40%;text-align:right">${prefHeader.panNumber?if_exists}</td>
			</tr>
	</tbody>
</table>
</#if>
</tr>
</table>

<#assign displayTaxAmount =0>
<#assign grossEarning =0>
<#assign curr="">
<#assign totalLoanAmount =0>

<table class="basic-table">
<tr style="border:1px solid black;">
<td style="width:50%">
<table style="width:100%">
	<caption><h2>Earnings</h2></caption>
	<thead>
		<td><h3>Salary Head</h3></td>
		<td style="width:40%;text-align:right"><h3>Amount</h3></td>
	</thead>
	
	<tbody>
		<#list payslipItems as payslip>
			<#assign curr=payslip.currencyUomId>	
			<#if payslip.isCr=="Y">
			<#assign grossEarning=grossEarning+payslip.amount>
			<tr>
				<td>${payslip.name}</td>
				<td style="width:40%;text-align:right">${payslip.amount?string("0.##")} ${curr}</td>
			</tr>
			</#if>
		</#list>
		<tr>
			<td colspan="2"><hr/></td>
		</tr>
		<tr>
				<td><h3>Total Earnings</h3></td>
				<td style="width:40%;text-align:right">${grossEarning?string("0.##")}</td>
		</tr>
	</tbody>
</table>
</td>
<td style="width:50%">

<table style="width:100%">
	<caption><h2>Deductions</h2></caption>
	<thead>
		<td><h3>Salary Head</h3></td>
		<td style="width:40%;text-align:right"><h3>Amount</h3></td>
	</thead>	
	<tbody>
		<#list payslipItems as payslip>	
			<#if payslip.isCr!="Y">
			<#assign displayTaxAmount= displayTaxAmount + payslip.amount>
			<tr>
				<td>${payslip.name}</td>
				<td style="width:40%;text-align:right">${payslip.amount?string("0.##")} ${curr}</td>
			</tr>
			</#if>
		</#list>
		
		<#list taxDeductions as taxDeducted>	
			<#assign displayTaxAmount = displayTaxAmount + taxDeducted.amount>
			<tr>
					<td>${taxDeducted.taxType}</td>
					<td style="width:40%;text-align:right">${taxDeducted.amount?string("0.##")} ${curr}</td>
			</tr>		
		</#list> 
		<#-- 	
		<tr>
				<td>Professional Tax</td>
				<td style="width:40%;text-align:right">${professionalTax?if_exists?string("0.##")} ${curr}</td>
		</tr>
		
		<#assign displayTaxAmount = displayTaxAmount + totalTax?if_exists>
		<tr>
				<td>Tax Deducted</td>
				<td style="width:40%;text-align:right">${displayTaxAmount?string("0.##")} ${curr}</td>
		</tr>
		-->	
		<tr>
			<td colspan="2"><hr/></td>
		</tr>
		<tr>
			<td colspan="2">
				<#if loansAvailable>
					<table style="width:100%">
						<caption><h2>Loans</h2></caption>
						<thead>
							<td><h3>Description</h3></td>
							<td style="width:40%;text-align:right"><h3>Amount</h3></td>
						</thead>	
						<tbody>
							<#list loans as loan>	
								<#assign totalLoanAmount = totalLoanAmount + loan.deductionAmount>
								<tr>
									<td>${loan.description}</td>
									<td style="width:40%;text-align:right">${loan.deductionAmount?string("0.##")} ${curr}</td>
								</tr>
							</#list>
						</tbody>
					</table>
				</#if>
			</td>
		</tr>
		
		<tr>
				<td><h3>Total Deductions</h3></td>
				<#assign totalDeduction=displayTaxAmount+totalLoanAmount>
				<td style="width:40%;text-align:right">${totalDeduction?string("0.##")} ${curr}</td>
		</tr>
	</tbody>
</table>
</td>
</tr>
<tfoot>
	<tr>
		<td colspan="2" style="text-align:center">
		 <h2><br> Net Salary = ${grossEarning?string("0.##")}(Earnings) - ${totalDeduction?string("0.##")} (Deductions)=${(grossEarning-totalDeduction)?string("0.##")} ${curr}</h2> </td>
	</tr>
</tfoot>
</table>





