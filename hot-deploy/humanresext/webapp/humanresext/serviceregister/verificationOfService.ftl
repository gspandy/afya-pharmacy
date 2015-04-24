

<table border="1" cellpadding="2px" style="border:1px solid #000;">

	<caption> <h1> IV. History and Verfication Of Service </h1> </caption>

	<tr>
		<td width="20px" rowspan="2"> Sl No </td>
		<td colspan="2" width="150px" style="text-align:center"> Period </td>
		<td rowspan="2"  width="300px"> Post,Scale of pay and office (with station) </td>
		<td colspan="2" width="300px" style="text-align:center">Pay</td>
		<td rowspan="2" width="300px"> Events affecting cols. 4-6 (vide instruction 10)</td>
		<td rowspan="2" width="200px"> Signature and designation of attesting officer(with date) </td>
		<td rowspan="2" width="200px"> Signature and designation of verifying officer (with date)  </td>
		<td rowspan="2" width="200px"> Signature of the Government servant </td>
		<td rowspan="2" width="100px"> Remarks </td>
	</tr>

	<tr>
		<td width="75px">From</td>
		<td width="75px">To</td>
		<td width="150px">Substantive</td>
		<td width="150px">Officiating</td>
	</tr>
	
	<#assign counter=1>
	<#list payGrades as payGrade>
	<tr>
		<td>${(payGrade_index+1)}</td>

		<td></td>
		<td></td>
		<td>${payGrade.payGradeName} - ${payGrade.description}</td>
		<td>
			<#assign amount=Static["com.nthdimenzion.humanres.payroll.PayrollHelper"].getValueFromGrade(delegator,payGrade.payGradeId,payGrade.salaryStepSeqId,payGrade.revision)>
			
			${(amount/12)?string("0.##")}
			
			</td>
		<td>${(amount/12)?string("0.##")}</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	</#list>

<tbody/>

</table>