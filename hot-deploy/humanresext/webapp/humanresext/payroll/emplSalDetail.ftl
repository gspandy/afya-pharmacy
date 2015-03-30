<div class="screenlet">
	<div class="screenlet-title-bar">
		<ul><li class="h3">${screenTitle}</li></ul>
	</div>
		<br><br><b>
		<#assign salary = parameters.salary>
		<#assign lofferId = parameters.offerId?if_exists>
		<table style="width:50%; border:0px">
			<tr>
				<td style="text-align:left; width:25%;font-weight:bold;">Components</td>
				<td style="text-align:right;width:25%;font-weight:bold;">
					Amount				
				</td>
			<tr>
			<#list salary.getAllComponents() as salaryComponent>
				<tr>
					<td style="text-align:left;font-weight:normal;">${salaryComponent.getSalaryHeadName()}</td>
					<td style="text-align:right;width:25%;font-weight:normal;"> ${salaryComponent.getAmount()?string("0.##")} ${salaryComponent.getCurrency()} </td>
				<tr>
			</#list>
			<tr>
				<td colspan="2"><hr/></td>
			<tr>
			<tr>
				<td style="text-align:left;font-weight:bold;">Gross Income</td>
				<td style="text-align:right;width:25%;font-weight:bold;">${salary.getGrossAmount()?string("0.##")}</td>
			<tr>
			<tr>
				<td colspan="2"><hr/></td>
			<tr>
			<tr>
				<td style="text-align:left;font-weight:bold;" colspan="2">Exemption</td>				
			<tr>
			<#list salary.getExemptionComponents() as exemption>
					<tr>
						<td style="text-align:left;font-weight:normal;">${exemption.getSalaryHeadName()}</td>
						<td style="text-align:right;width:25%;font-weight:normal;">${exemption.getAmount()?string("0.##")}</td>
					</tr>
			</#list>
			<tr>
				<td colspan="2"><hr/></td>
			<tr>
			<tr>
				<td style="text-align:left;font-weight:bold;">Total Exemption</td>
				<td style="text-align:right;width:25%;font-weight:bold">${salary.getExemptionAmount()?string("0.##")}</td>
			</tr>
			<tr>
				<td colspan="2"><hr/></td>
			<tr>
			<tr>
				<td style="text-align:left;font-weight:bold">NetIncome</td>
				<td style="text-align:right;width:25%;font-weight:bold">${salary.getNetAmount()?string("0.##")}</td>
			<tr>
			<tr>
				<td style="text-align:left;font-weight:bold">Deductions</td>
				<td style="text-align:right;width:25%;font-weight:bold">${salary.getTaxDeductionAmount()?string("0.##")}</td>
			<tr>
			<tr>
				<td colspan="2"><hr/></td>
			<tr>
			<tr>
				<td style="text-align:left;font-weight:bold">Taxable Income</td>
				<td style="text-align:right;width:25%;font-weight:bold">${salary.getTaxableAmount()?string("0.00")}</td>
			<tr>
			<#assign totalTax = 0>
			<tr>
				<td style="text-align:left;font-weight:normal">Actual Tax Amount</td>
				<td style="text-align:right;width:25%;font-weight:normal">${salary.getTaxAmount()?string("0.00")}</td>
			<tr>
			<tr>
				<td style="text-align:left;font-weight:normal">Professional Tax</td>
				<td style="text-align:right;width:25%;font-weight:normal">${salary.getProfessionalTax()?string("0.00")}</td>
			<tr>
			<#assign totalTax = salary.getTaxAmount() + salary.getProfessionalTax()>
			<tr>
				<td style="text-align:left;font-weight:bold">Total Tax</td>
				<td style="text-align:right;width:25%;font-weight:bold">${totalTax?string("0.00")}</td>
			<tr>
			<tr>
				<td colspan="2"><hr/></td>
			</tr>
			<#if parameters.fromDate?exists>
				<tr>
					<td style="text-align:left">From Date</td>
					<td style="text-align:right;width:25%">${parameters.fromDate}</td>
				</tr>
			</#if>
		</table>
		</b>
		<br><br>
		<#assign offerQ = parameters.offerQ?default("N")>
		<#if  offerQ != "Y">
			<span style="text-align:center">
			<#if parameters.mode?exists>
				<#if "editable"==parameters.mode>
				<form action="<@ofbizUrl>CreateEmplSal</@ofbizUrl>" name="CreateEmplSal" method="get">
					<#if parameters.payGradeId?exists>
						<input type="hidden" name="payGradeId" value="${parameters.payGradeId}">
						<input type="hidden" name="salaryStepSeqId" value="${parameters.salaryStepSeqId}">
					</#if>
					<input name="partyId" value="${parameters.partyId}">
					<input name="salaryStructureId" value="${parameters.salaryStructureId}">
					<input type="hidden" name="offerQ" value="${offerQ}">
					<input type="submit" value="${uiLabelMap.CommonCreate}">		
				</form>
				</#if>
			</#if>
			</span>
		</#if>
		<#if offerQ == "Y">
			<span style="text-align:center">
			<#if parameters.mode?exists>
				<#if "editable"==parameters.mode>
				<form action="<@ofbizUrl>createOfferCTC</@ofbizUrl>" name="createOfferCTC" method="get">
					<input type="hidden" name="offerId" value="${lofferId?if_exists}">
					<input type="hidden" name="partyId" value="${parameters.partyId}">
					<input type="submit" value="Create Offer">		
				</form>
				
				</#if>
			</#if>
			</span>
		</#if>
		
		<br><br>
</div>