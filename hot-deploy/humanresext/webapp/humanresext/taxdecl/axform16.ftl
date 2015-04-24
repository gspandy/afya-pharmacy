<#-- Annexure to Form 16 -->
<html>
<#assign totSal = 0>
<#assign totPerq = 0>
<#assign nonTax = 0>

<span style="text-align:center">
	<h1>ANNEXURE TO FORM NO.16</h1>
</span>
<span style="text-align:center">
	<h2>Details of Salary Paid and Allowance Exempted u/s 10 and 17(2) of Income Tax Act,1961</h2>
</span>
<hr>
<table style="width:100%;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:40%;text-align:left">1. Name and address of the Employer</td>
		<td style="width:40%;text-align:right">${companyName?if_exists} <br> 
					${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} 
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists} ${postalAddress.countryGeoId?if_exists}					
		</td>
	</tr>
	<tr>
		<td style="width:40%;text-align:left">2. Name of Employee :</td>
		<td style="width:40%;text-align:right">${empFullName?if_exists} </td> 
	</tr>
	<tr>
		<td style="width:40%;text-align:left">Designation :</td>
		<td style="width:40%;text-align:right">${empDesignation?if_exists} </td> 
	</tr>
	<tr>
		<td style="width:40%;text-align:left">PAN :</td>
		<td style="width:40%;text-align:right">${prefHeader.panNumber?if_exists} </td> 
	</tr>	
	<tr> 
		<td style="width:40%;text-align:left">Employee ID :</td>
		<td style="width:40%;text-align:right">${prefHeader.partyId}</td> 
	</tr>
	<br>
	<tr> 
		<td style="width:100%;text-align:left">Salary Details for the period :${parameters.fromDate}  to  ${parameters.thruDate}</td>
	</tr>
	<table style="width:100%;border:1px solid black;" cellspacing="0" cellpadding="0">
	<#--list innerFields as tblCols -->
		<tr> 
			<td style="width:40%;text-align:left">1. Salary as per provisions contained in Sec 17(1)</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<#-- List the total of salary components for the period-->
		<#assign salary = parameters.salary>
		<#list salary.getAllButPerqComponents() as salaryComponent>
			<tr>
				<td style="width:40%;text-align:left;">${salaryComponent.getSalaryHeadName()}</td>
				<td style="width:40%;text-align:right;">${salaryComponent.getAmount()?string("0.##")}</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<#assign totSal = totSal + salaryComponent.getAmount()>
			</tr>
		</#list>		
		<tr> 
			<td style="width:40%;text-align:left;">Total Salary (A)</td>
			<td>&nbsp;</td>
			<td style="width:40%;text-align:right;"><hr>${totSal?string("0.00")}</td>
			<td>&nbsp;</td>
		</tr>
		<tr> 
			<td style="width:40%;text-align:left">Value of perquisite u/s 17(2) <br>(as per Form No.12BA wherever applicable)</td>
			<td style="width:40%;text-align:right">${perqAmount?string("0.##")}</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<#assign totPerq = totPerq + perqAmount> <#-- Perq Perq17_2 value will come here -->
		<tr> 
			<td style="width:40%;text-align:left">Profit in lieu of Salary u/s 17(3)<br>(as per Form No.12BA wherever applicable)</td>
			<td style="width:40%;text-align:right">0.00</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<#assign totPerq = totPerq + 0.00> <#-- Perq Perq17_3 value will come here -->
		<tr> 
			<td style="width:40%;text-align:left">Total Perquisites (B)</td>
			<td>&nbsp;</td>
			<td style="width:40%;text-align:right">${totPerq?string("0.00")}<hr></td>
			<td>&nbsp;</td>
		</tr>
		<#assign totSal = totSal + totPerq>
		<tr> 
			<td style="width:40%;text-align:left">Gross Salary (A+B)</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td style="width:40%;text-align:right">${totSal?string("0.00")}</td>
			
		</tr>
		<tr> 
			<td style="width:40%;text-align:left">2. Details of Allowance exempted under section 10 and 17(2)</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<br>
		</tr>
		<#-- Print the non taxable allowances -->
		<#list salary.getExemptionComponents() as exemption>
			<tr>
				<td>${exemption.getSalaryHeadName()}</td>
				<td>${exemption.getAmount()?string("0.##")}</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<#assign nonTax = nonTax + exemption.getAmount()>
			</tr>
		</#list>
		<tr>
			<td style="width:40%;text-align:left">Total (C)</td>
			<td>&nbsp;</td>
			<td style="width:40%;text-align:right"><hr>${nonTax?string("0.00")}</td> <#-- Maximum for an year -->
			<td>&nbsp;</td>
		</tr>
		<tr> 
			<td style="width:40%;text-align:left">Balance : (A+B-C)</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td style="width:40%;text-align:right"><hr>${(totSal - nonTax)?string("0.00")}</td> <#-- Maximum for an year -->
		</tr>
	</table>
</table>		
</html>



