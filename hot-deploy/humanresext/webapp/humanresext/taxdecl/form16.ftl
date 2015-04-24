<#-- Form 16 Introduction Page -->
<#-- Author: Pankaj Sachdeva -->

<html>
<#assign total = 0>
<#assign perqAmount = 0.0> <#-- Calculate total Perquisites Value -->
<#assign balance = 0>
<#assign MAX80C = 100000>  <#-- Maximum limit of 80C sections     -->
<#assign nonTax = 0>

<span style="text-align:center;font-size:32px;">
	<h1>${companyName?if_exists}</h1>
</span>
<hr>
<span style="text-align:center;font-size:32px;">
	<h1>Form 16</h1>
</span>
<hr>
<table style="width:100%;font-weight:bold;font-size:14px;">
	<tr>
		<td style="width:40%;text-align:left;">Employee Name:</td>
		<td style="width:40%;text-align:right;">${empFullName?if_exists}</td>
	</tr>
	<tr>
		<td style="width:40%;text-align:left;">Employee PAN:</td>
		<td style="width:40%;text-align:right;">${prefHeader.panNumber?if_exists} </td> 
	</tr>
	<tr> 
		<td style="width:40%;text-align:left">Employee ID:</td>
		<td style="width:40%;text-align:right">${prefHeader.partyId}</td> 
	</tr>
	<tr> 
		<td style="width:40%;text-align:left">Form 16 Control Number:</td>
		<td style="width:40%;text-align:right">${prefHeader.partyId}</td> 
	</tr>
	<tr> 
		<td style="width:40%;text-align:left">Assessment Year:</td>
		<td style="width:40%;text-align:right">${parameters.fromDate} to ${parameters.thruDate}</td> 
	</tr>
</table>
<br>
<br>
<br>
<hr>
<span style="text-align:left">
	<h3>Note: Digitally Signed Form</h3>
</span> 
<table>
	<tr>This form has been signed and certified using a Digital Signature Certificate as specified under section 119 of
the Income-tax Act, 1961.(Please refer Circular No.2/2007,dated 21-5-2007).
	</tr>
	<br>
	<tr>The Digital Signature of the signatory has been affixed in the box provided below.To see the details and validate
the signature,you should click on the box.
	</tr>
	<tr> <td style="width:100%;text-align:center;">DIGITAL SIGNATURE </td>
	 </tr>
	<tr><td style="width:100%;text-align:center;">Signed using Digital Signature of ${signatory}</td>
	</tr>
	<br>
	<tr>Number of pages: 5 (including this page)</tr> <#-- To be changed -->
</table>
	<hr>
	<br>
<table>
	<tr><td style="width:100%;text-align:left;font-weight:bold;font-size:16px;">E-file your income-tax Return:</td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td>
	You can click the link below, to electronically file your Income-tax return. 
	The link would tranfer your Form Data to the e-filing website of Skorydov (www.myITreturn.com).
	On the website you can enter additional details of your Income 
	and file your return electronically as per the provisions of the Income-Tax Department.
	(You need to be connected to the internet to use this facility)
	</td>
	</tr>
	<br>
	<tr><td style="width:100%;text-align:center;font-weight:bold;font-size:12px;"><a target="top" class="linktext" href="http://www.myITreturn.com">Click here to prepare your Income-tax Return</a></td></tr>
	<tr><td>If you cannot open the link above then please visit www.myITreturn.com and follow the instructions mentioned therein.</td>
	</tr>
</table>
</html>

<#-- Next Page -->
<html>
<span style="text-align:center;font-size:20px;">
	<h1>FORM NO.16</h1>
</span>
<span style="text-align:center;font-size:12px;">
	<h2>[See rule 31(1)(a)]</h2>
</span>
<span style="text-align:center;font-size:10px;">
	<h2>Certificate under section 203 of the income-tax Act, 1961 for tax deducted at source from income <br>
chargeable under the head "Salaries" </h2>
</span>
<br>
<table style="width:100%;border:1px solid black;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:40%;text-align:center;border:1px solid black;font-weight:bold;font-size:12px;">Name and address of the Employer</td>
		<td style="width:40%;text-align:center;border:1px solid black;font-weight:bold;font-size:12px;">Name and designation of the Employee</td>
		
	</tr>
	<tr>
		<td style="width:40%;text-align:center;border:1px solid black;font-size:12px;">${companyName?if_exists} <br> 
					${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} <br>
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}					
		</td>
		<td style="width:40%;text-align:center;border:1px solid black;font-size:12px;">${empFullName?if_exists} <br>
			${prefHeader.partyId}<br>
			${empDesignation?if_exists}
		</td> 
	</tr>
</table>	

<table style="width:100%;border:1px solid black;font-size:12px;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:20%;text-align:center;border:1px solid black;font-weight:bold;">PAN of the Deductor</td>
		<td style="width:20%;text-align:center;border:1px solid black;font-weight:bold;">TAN of the Deductor</td>
		<td style="width:40%;text-align:center;border:1px solid black;font-weight:bold;">PAN of the Employee</td>
	</tr>
	<tr>
		<td style="width:20%;text-align:center;border:1px solid black;">${companyPAN?if_exists}</td>
		<td style="width:20%;text-align:center;border:1px solid black;">${companyTAN?if_exists}</td>
		<td style="width:40%;text-align:center;border:1px solid black;">${prefHeader.panNumber?if_exists}</td>
	</tr>
</table>
	
<table style="width:100%;border:1px solid grey;font-size:12px;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">Acknowledgement nos. of all quarterly statements of TDS 
		under <br>sub-section (3) of Sec 200 as provided by TIN-FC/NSDL Website</td>
		<td style="width:25%;text-align:center;border:1px solid black;">Period</td>
		<td style="width:25%;text-align:center;border:1px solid black;">Assessment Year</td>
	</tr>
</table>
<table style="width:100%;border:1px solid grey;font-size:12px;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:10%;text-align:center;border:1px solid black;">Quarter</td>
		<td style="width:40%;text-align:center;border:1px solid black;">Acknowledgment No.</td>
		<td style="width:12.5%;text-align:center;border:1px solid black;">From</td>
		<td style="width:12.5%;text-align:center;border:1px solid black;">To</td>
		<td style="width:25%;text-align:center;">&nbsp;</td>
	</tr>
	<#assign Qcount = 0>
	<#list acks as acks>
		<#assign Qcount = Qcount + 1>
		<#if (acks.quarter = "Q1")>
			<tr>
				<#assign year = parameters.thruDate.substring(0,4)>
				<#assign next = year?eval + 1>
				<td style="width:10%;text-align:center;border:1px solid black;">Q1</td>
				<td style="width:40%;text-align:center;border:1px solid black;">${acks.ackNo?if_exists}&nbsp;</td>
				<td style="width:12.5%;text-align:center;">${parameters.fromDate}</td>
				<td style="width:12.5%;text-align:center;">${parameters.thruDate}</td>
				<td style="width:25%;text-align:center;">${year}-${next}</td>
			</tr>
		<#else>
			<tr>
				<td style="width:10%;text-align:center;border:1px solid black;">${acks.quarter}</td>
				<td style="width:40%;text-align:center;border:1px solid black;">${acks.ackNo?if_exists}&nbsp;</td>
				<td style="width:12.5%;text-align:center;">&nbsp;</td>
				<td style="width:12.5%;text-align:center;">&nbsp;</td>
				<td style="width:25%;text-align:center;">&nbsp;</td>
			</tr>
		</#if>
	</#list>
	<#if Qcount < 4> <#-- 4th quarter is missing -->
		<tr>
			<td style="width:10%;text-align:center;border:1px solid black;">Q4</td>
			<td style="width:40%;text-align:center;border:1px solid black;">Not Available as the last Quarterly Statement is yet to be furnished</td>
			<td style="width:12.5%;text-align:center;">&nbsp;</td>
			<td style="width:12.5%;text-align:center;">&nbsp;</td>
			<td style="width:25%;text-align:center;">&nbsp;</td>
		</tr>
	</#if>
</table>

<br>
<span style="text-align:center;font-size:12px;">
	<h1>DETAILS OF SALARY PAID AND ANY OTHER INCOME AND TAX DEDUCTED</h1>
</span>
<table style="width:100%;border:1px solid black;font-size:12px;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:50%;text-align:center;border:1px solid black;font-weight:bold;">Particulars</td>
		<td style="width:10%;text-align:center;border:1px solid black;font-weight:bold;">Rs</td>
		<td style="width:10%;text-align:center;border:1px solid black;font-weight:bold;">Rs</td>
		<td style="width:10%;text-align:center;border:1px solid black;font-weight:bold;">Rs</td>
	</tr>
	<#assign salary = parameters.salary>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">1. GROSS SALARY (As per enclosed annexure)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">&nbsp; &nbsp;(a) Salary as per provisions contained in section 17(1)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getGrossAmount()?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<tr>
		<#list salary.getPerqComponents() as perqs>
			<#assign perqAmount = perqAmount + perqs.getAmount()>
		</#list>
		<#assign total = salary.getGrossAmount() + perqAmount>
		<td style="width:50%;text-align:left;border:1px solid black;">&nbsp;&nbsp;(b) Value of perquisites under section 17(2) <br>
(as per Form No:12BA, wherever applicable)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${perqAmount?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">&nbsp; &nbsp;(c) Profits in lieu of salary under section 17(3) <br>
(as per Form No: 12BA,wherever applicable)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">0.0</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">&nbsp; &nbsp;(d) Total</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${total?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">2. Less : Allowance to the extent exempt u/s 10 (As per enclosed annexure)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getExemptionAmount()?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign balance = total - salary.getExemptionAmount()>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">3. Balance (1-2)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;"><hr>${balance?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">4. DEDUCTIONS :</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign deductions = 0.0>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(a) Entertainment Allowance</td>
		<td style="width:10%;text-align:center;border:1px solid black;">0.00</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign deductions = deductions + 0.0>	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(b) Tax on Employment</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getProfessionalTax()?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign deductions = deductions + salary.getProfessionalTax()>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">5 Aggregate of 4 (a) and (b)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${deductions?string("0.##")}<hr></td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign balance = balance - deductions>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">6. INCOME CHARGEABLE UNDER THE HEAD 'SALARIES' ( 3-5 )</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${balance?string("0.##")}</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">7. Add : Any other income reported by the employee</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign houseIncome = 0.0>
	<#assign otherIncome = 0.0>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(a) Income under the Head 'Income from House Property'</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">0.00</td>
	</tr>
	<#assign balance = balance + houseIncome>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(b) Income under the Head 'Income from Other Sources'</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">0.00</td>
	</tr>
	<#assign balance = balance + otherIncome>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">8. GROSS TOTAL INCOME ( 6+7 )</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;"><hr>${balance?string("0.##")}</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">9. DEDUCTIONS UNDER CHAPTER VI-A</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign deduct80 = 0> <#-- Deductions under 80 not to exceed 1000000 -->
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">(A) Sections 80C, 80CCC & 80CCD</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(a) Section 80C</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Gross Amount</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Deductible Amount</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#-- Section 80C deductions -->
	<#assign count = 0>
	<#assign amount = 0>
	<#list decls as deds>
		<#if deds.categoryName == "Section80C">
			<#if (deds.acceptedValue?default(0) > 0)>
				<#assign count = count + 1>
				<tr>
					<td style="width:50%;text-align:left;border:1px solid black;">(${count}) ${deds.itemName}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.numValue?string("0.##")}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.acceptedValue?string("0.##")}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
				</tr>
				<#assign deduct80 = deduct80 + deds.acceptedValue?default(0)>
			</#if>
		</#if>	
	</#list>
	<#-- Employee Provident Fund is also part of Section 80C -->
	<#list salary.getDeductComponents() as deducts>
		<#if deducts.getSalaryHeadName()?contains("Provident")> 
			<#assign count = count + 1>
			<tr>
				<td style="width:50%;text-align:left;border:1px solid black;">(${count}) ${deducts.getSalaryHeadName()}</td>
				<td style="width:10%;text-align:center;border:1px solid black;">${deducts.getAmount()?string("0.##")}</td>
				<td style="width:10%;text-align:center;border:1px solid black;">${deducts.getAmount()?string("0.##")}</td>
				<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
			</tr>
			<#assign deduct80 = deduct80 + deducts.getAmount()>
		 </#if> 
	</#list>
	<#-- Section 80CCC deductions -->
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(b) Section 80CCC</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign count = 0>
	<#assign amount = 0>
	<#list decls as deds>
		<#if deds.categoryName == "Section80CCC">
			<#if (deds.acceptedValue?default(0) > 0)>
				<#assign count = count + 1>
				<tr>
					<td style="width:50%;text-align:left;border:1px solid black;">(${count}) ${deds.itemName}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.numValue?string("0.##")}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.acceptedValue?string("0.##")}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
				</tr>
				<#assign deduct80 = deduct80 + deds.acceptedValue?default(0)>
			</#if>
		</#if>
	</#list>
	
	<#-- Section 80CCD deductions -->
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(c) Section 80CCD</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign count = 0>
	<#assign amount = 0>
	<#list decls as deds>
		<#if deds.categoryName == "Section80CCD">
			<#if (deds.acceptedValue?default(0) > 0)>
			<#assign count = count + 1>
				<tr>
					<td style="width:50%;text-align:left;border:1px solid black;">(${count}) ${deds.itemName}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.numValue?string("0.##")}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.acceptedValue?string("0.##")}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
				</tr>
				<#assign deduct80 = deduct80 + deds.acceptedValue?default(0)>
			</#if>
		</#if>
	</#list>
	<#-- Maximum limit of deductions is 100000 -->
	<#if (deduct80 > MAX80C)>
		<#assign deduct80 = MAX80C>
	</#if>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">Total (a) + (b) + (c)</td>
		<td style="width:10%;text-align:center;border:1px solid black;"><hr></td>
		<td style="width:10%;text-align:center;border:1px solid black;"><hr>${deduct80?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	
	<#-- Other Sections -->
	<#assign deduct80NC = 0> <#-- Non 80C deductions -->
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">(B) Other Sections under Chapter VI-A</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:right;border:1px solid black;">Gross Amount</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Qualifying Amount</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Deductible Amount</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	
	<#assign count = 0>
	<#assign amount = 0>
	<#list decls as deds>
		<#if !(deds.categoryName?starts_with("Section80C"))>
			<#if (deds.acceptedValue?default(0) > 0)>
				<#assign count = count + 1>
				<tr>
					<td style="width:50%;text-align:left;border:1px solid black;">(${count}) ${deds.categoryName} ${deds.itemName} ${deds.numValue}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.acceptedValue?default(0)}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">${deds.acceptedValue?default(0)}</td>
					<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
				</tr>
				<#assign deduct80NC = deduct80NC + deds.acceptedValue?default(0)>
			</#if>
		</#if>	
	</#list>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;"><hr></td>
		<td style="width:10%;text-align:center;border:1px solid black;"><hr>${deduct80NC?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#assign totDed = deduct80NC + deduct80>
	<#if (totDed > balance)>
		<#assign totDed = balance>
	</#if>
	<#--
	<tr>
	 Check if salary bean has correct tax deduction amount
		<td>salary.getTaxDeductionAmount()<br>${salary.getTaxDeductionAmount()}</td>
		<td>salary.getTaxableAmount()<br>${salary.getTaxableAmount()}</td>
	</tr> -->
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">10. Aggregate of deductible amounts under chapter VI-A</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${totDed?string("0.##")}<hr></td>
	</tr>
	<#assign balance = balance - totDed>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">11. Total income ( 8-10 )</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${balance?string("0.##")}</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">12. Tax on total income</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getSimpleTax()?string("0.##")}</td>
	</tr>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">13. Surcharge ( on tax computed at S.No 12)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getSurcharge()?string("0.##")}</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">14. (i) Primary Education Cess (on Tax [12] + Surcharge[13])</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getEducationCess()?string("0.##")}</td>
	</tr>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(ii)Higher Education Cess</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getHigherEduCess()?string("0.##")}<hr></td>
	</tr>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">15. Tax Payable ( 12+13+14(i)+14(ii) )</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getTaxAmount()?string("0.##")}</td>
	</tr>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">16. Relief under section 89</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">0.00</td><#--To be changed -->
	</tr>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">17. Tax Payable ( 15-16 )</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${salary.getTaxAmount()?string("0.##")}</td><#--To be changed -->
	</tr>
	
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">18. Less : (a) Tax Deducted at source u / s 192 (1)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${totTaxPaid?string("0.##")}</td><#-- Put sum of tax from payslips --> 
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	
	<#assign perqEmployerTax = 0.00> <#-- Perq Tax Paid By Employer --> 
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">(b) Tax paid by the employer on behalf of the employee <br>u/s 192 (1A) on perquisities u/s 17(2)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${perqEmployerTax?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
	<#-- Sum Tax -->
	<#assign totTDSPaid = perqEmployerTax + totTaxPaid>
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${totTDSPaid?string("0.##")}<hr></td>
	</tr>
	
	<#assign remainingTax = salary.getTaxAmount() - totTDSPaid> <#-- Ideally should be zero if TDS done -->
	<tr>
		<td style="width:50%;text-align:left;border:1px solid black;font-weight:bold;">19. Balance Tax payable / refundable ( 17-18 )</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${remainingTax?string("0.##")}</td>
	</tr>
</table>
<br>
<br>
<span style="text-align:center;font-weight:bold;">
	<h1>DETAILS OF TAX DEDUCTED AND DEPOSITED INTO CENTRAL GOVERNMENT ACCOUNT</h1>
</span>
<table style="width:100%;border:1px solid black;font-size:12px;" cellspacing="0" cellpadding="0">
	<tr>
		<td style="width:5%;text-align:center;border:1px solid black;">Sr. No.</td>
		<td style="width:10%;text-align:center;border:1px solid black;">TDS (Rs)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Surcharge (Rs)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Education Cess (Rs)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Higher Education <br> Cess (Rs)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Total Tax <br>Deposited (Rs)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Cheque/DD No.<br> (if any)</td>
		<td style="width:10%;text-align:center;border:1px solid black;">BSR Code of <br>Bank Branch</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Date on which <br>Tax Deposited</td>
		<td style="width:10%;text-align:center;border:1px solid black;">Transfer Vchr/ <br>Challan Iden. No.</td>
	</tr>
	<#assign totTdsAmount = 0>
	<#list tdsList as tdslit>
	<tr>
		<td style="width:5%;text-align:center;border:1px solid black;">${tdslit_index + 1}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.tds?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.surcharge?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.educationCess?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.higherEduCess?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.totTds?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.chequeNo}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.branchCode}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.paymentDate}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">${tdslit.challanNo}</td>
	</tr>
	<#assign totTdsAmount = tdslit.totTds>
	</#list>
	<tr>
		<td style="width:5%;text-align:center;border:1px solid black;font-weight:bold;">Total</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;font-weight:bold;">${totTdsAmount?string("0.##")}</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
		<td style="width:10%;text-align:center;border:1px solid black;">&nbsp;</td>
	</tr>
</table>

<br>
I, ${signatory?if_exists?upper_case} son/daughter of ${signatoryParent?if_exists?upper_case}, working in the capacity of AUTHORISED SIGNATORY, do hereby certify that a
sum of Rs. ${totTdsAmount} (${Static["com.smebiz.Util.Converter"].rupeesInWords(totTdsAmount?string("0.##"))?upper_case}) has been deducted at source 
and paid to the credit of the Central Government.
<br>
I further certify that the information given above is true and correct based on the books of account, documents and other available records which 
includes the declaration given by the employee. 
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
<br>
<#include "axform16.ftl" /><br>
<#include "form12BA.ftl" />


