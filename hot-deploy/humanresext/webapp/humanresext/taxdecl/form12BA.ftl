<#-- Form 12BA -->
<#-- Author: Pankaj Sachdeva -->
<html>
<#assign totSal = 0>
<#assign totPerq = 0>
<#assign nonTax = 0>

<span style="text-align:center">
	<h1>FORM NO. 12BA</h1>
</span>
<span style="text-align:center">
	<h2>{See Rule 26A(2)(b)}</h2>
</span>

<span style="text-align:center">
	<h2>Statement showing particulars of perquisites, other fringe benefits or<br> 
		amenities and profits in lieu of salary with value thereof
	</h2>
</span>
<hr>
<table style="width:100%;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:40%;text-align:left">1. Name and address of the Employer</td>
		<td style="width:40%;text-align:right">${companyName?if_exists} <br> 
					${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} <br>
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists} 			
		</td>
	</tr>
	<tr>
		<td style="width:40%;text-align:left">2. TAN :</td>
		<td style="width:40%;text-align:right">${TAN?if_exists} </td> 
	</tr>
	<tr>
		<td style="width:40%;text-align:left">3. TDS Assessment Range of the Employer :</td>
		<td style="width:40%;text-align:right">${TDSRange?if_exists} </td> 
	</tr>
	
	<tr>
		<td style="width:40%;text-align:left">4. Name of Employee :</td>
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
	<#assign Isdirector = "No">
	<#if empDesignation?if_exists?contains("DIRECTOR")>
		<#assign Isdirector = "Yes">
	</#if>
	<tr> 
		<td style="width:40%;text-align:left">Is the Employee a Director or a person with substantial <br>
			interest in the company (Where the employer is a company) :</td>
		<td style="width:40%;text-align:right">${Isdirector}</td> 
	</tr>
	<#assign salary = parameters.salary>
	<tr> 
		<td style="width:40%;text-align:left">6. Income under the Head "Salaries" of the Employee :<br>
(Other than from perquisites)</td>
		<td style="width:40%;text-align:right">${salary.getGrossAmount()?string("0.##")}</td> 
	</tr>
	<#assign fromYear = parameters.fromDate.substring(0,4)>
	<#assign endYear = parameters.thruDate.substring(0,4)>
	<tr> 
		<td style="width:40%;text-align:left">7. Financial Year :</td>
		<td style="width:40%;text-align:right">${fromYear} - ${endYear}</td> 
	</tr>
	
	<br>
	<tr> 
		<td style="width:100%;text-align:left">8. Valuation of perquisites:</td>
	</tr>
	<table style="width:100%;border:1px solid black;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:5%;text-align:left;border:1px solid black;">Sr. No.</td>
		<td style="width:10%;text-align:left;border:1px solid black;">Nature of perquisite</td>
		<td style="width:10%;text-align:right;border:1px solid black;">Value of perquisite<br>as per rules(Rs.)</td>
		<td style="width:10%;text-align:right;border:1px solid black;">Amount,if any paid by<br>employee(Rs.)</td>
		<td style="width:10%;text-align:right;border:1px solid black;">Amount of Taxable<br>perquisite(Rs.)</td>
	</tr>
	<#assign totTaxablePerq = 0>
	<#list salary.getPerqComponents() as preqs>
		<#assign taxablePerq = 0>
		<tr>
			<td style="width:5%;text-align:left;border:1px solid black;">(${preqs_index + 1})</td>
			<td style="width:10%;text-align:left;border:1px solid black;">${preqs.getSalaryHeadName()}</td>
			<td style="width:10%;text-align:right;border:1px solid black;">${preqs.getAmount()?string("0.##")}</td>
			<td style="width:10%;text-align:right;border:1px solid black;">0.00</td><#-- Assuming nothing is paid by employee -->
			<#assign taxablePerq = (preqs.getAmount() - 0)> <#--Assuming amount paid by employee is zero -->
			<td style="width:10%;text-align:right;border:1px solid black;">${taxablePerq?string("0.##")}</td>
		</tr>
		<#assign totTaxablePerq = totTaxablePerq + taxablePerq>
	</#list>
	<tr>
		<td style="width:5%;text-align:left;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:left;border:1px solid black;">Total Value of Perquisites</td>
		<td style="width:10%;text-align:right;border:1px solid black;">${totTaxablePerq}</td>
		<td style="width:10%;text-align:right;border:1px solid black;">0.00</td>
		<td style="width:10%;text-align:right;border:1px solid black;">${totTaxablePerq}</td>
	</tr>
	<tr>
		<td style="width:5%;text-align:left;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:left;border:1px solid black;">Value of profits for in lieu of salary as per s. 17(3)</td>
		<td style="width:10%;text-align:right;border:1px solid black;">0.00</td>
		<td style="width:10%;text-align:right;border:1px solid black;">0.00</td>
		<td style="width:10%;text-align:right;border:1px solid black;">0.00</td>
	</tr>
</table>
<table style="width:100%;" cellspacing="0" cellpadding="0">
	<tr> 
		<td style="width:40%;text-align:left">9. Details of Tax</td>
	</tr>
	<tr> 
		<td style="width:40%;text-align:left">a) Tax Deducted from Salary of Employee u/s 192(1) :</td>
		<td style="width:40%;text-align:left">${salary.getTaxAmount()?string("0.##")}</td>
	</tr>
	<tr> 
		<td style="width:40%;text-align:left">b) Tax Paid by Employer on behalf of Employee u/s 192(1A) :</td>
		<td style="width:40%;text-align:left">0.00</td><#-- Assuming Employer does not pay tax -->
	</tr>
	<tr> 
		<td style="width:40%;text-align:left">c) Total Tax Paid :</td>
		<td style="width:40%;text-align:left">${salary.getTaxAmount()?string("0.##")}</td>
	</tr>
	<tr> 
		<td style="width:40%;text-align:left">d) Date of Payment into Goverment Treasury :</td>
		<td style="width:40%;text-align:left">&nbsp;</td>
	</tr>
</table>
<span style="text-align:center">
	<h1>DECLARATION BY EMPLOYER</h1>
</span>
<br>
I, ${signatory?if_exists?upper_case} son/daughter of ${signatoryParent?if_exists?upper_case}, 
working in the capacity of AUTHORISED SIGNATORY, do hereby declare on behalf of M/s. ${companyName?if_exists?upper_case} <br>
That the information given above is true and correct and based on the books of accounts , documents and other relevant records or 
information available with us and the details of value of each such perquisite are in accordance with section 17 
and rules framed thereunder  and that such information  is true and correct.
<br>
<br>
<br>
<table style="width:100%;">
	<tr>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;"><i>For</i> &nbsp;<b>${companyName?if_exists}<b> </td>
	</tr>
	<tr>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;">This form is digitally signed.</td>
	</tr>
	<tr>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;">Signature of the person responsible for deduction of tax</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td style="width:30%;text-align:left;">Place: ${postalAddress.city?if_exists}</td>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;">Full Name: ${signatory?if_exists?upper_case}</td>
	</tr>
	<tr>
		<td style="width:30%;text-align:left;">Date: ${parameters.thruDate}</td>
		<td style="width:30%;text-align:left;">&nbsp;</td>
		<td style="width:30%;text-align:left;">Designation: Authorised Signatory</td>
	</tr>
</table>
</html>


