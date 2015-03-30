<#-- Employee Payslip -->
<#-- Author: Pankaj Sachdeva -->
<#escape x as x?xml>
<#assign displayTaxAmount =0>
<#assign grossEarning =0>
<#assign curr=defCurrencyType>
<#assign totalLoanAmount =0>
<fo:block text-align="center" font-size="12pt" font-weight="bold" >
<fo:external-graphic src="url(${logoImageUrl?if_exists})"
content-height="3em" content-width="3em"/>
</fo:block>
<fo:block text-align="center" font-size="12pt" font-weight="bold" >
${companyName}
</fo:block>
<fo:block text-align="center" font-size="10pt" font-weight="bold">Payslip For Period ${payslipHeader.periodFrom} - ${payslipHeader.periodTo}</fo:block>
<fo:block text-align="center" font-size="10pt" font-weight="bold">&#160;</fo:block>

<#if prefHeader?exists>
<fo:block text-align="center" font-size="9pt" font-weight="bold">Employee Details</fo:block>

<fo:table font-size="8pt" table-layout="fixed" height="100%" font-weight="normal" >

<fo:table-column width="25%"/>
<fo:table-column width="25%"/>
<fo:table-column width="25%"/>
<fo:table-column width="25%"/>
<#assign partyName=prefHeader.firstName + " " + prefHeader.lastName>
<fo:table-body>
<fo:table-row font-weight="normal" >
<fo:table-cell border="thin solid grey" text-align="left" padding-left="2%"><fo:block>Employee Id</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>
${prefHeader.partyId}
</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="left" padding-left="2%"><fo:block>Bank Name</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>
${prefHeader.bankName}
</fo:block></fo:table-cell>
</fo:table-row>
<fo:table-row font-weight="normal">
<fo:table-cell border="thin solid grey" text-align="left" padding-left="2%"><fo:block>Employee Name</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>
${partyName}
</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="left" padding-left="2%"><fo:block>Bank Account Number</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>
${prefHeader.bankAccountNumber?if_exists}
</fo:block></fo:table-cell>
</fo:table-row>
<fo:table-row font-weight="normal" >
<fo:table-cell border="thin solid grey" text-align="left" padding-left="2%"><fo:block>Designation</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>
${designation?if_exists}
</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" padding-left="2%"><fo:block>PAN</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>
${prefHeader.panNumber?if_exists}
</fo:block></fo:table-cell>
</fo:table-row>
 <fo:table-row font-weight="normal" >
        	<fo:table-cell border="thin solid grey" text-align="left"  padding-left="2%"><fo:block>Paid Days</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="right"  padding-right="2%"><fo:block>
            	${paiddays?default(0)}
            </fo:block></fo:table-cell>
              <fo:table-cell border="thin solid grey" text-align="left"  padding-left="2%"><fo:block>UnPaidDays</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>${unpaiddays?default(0)}
            </fo:block></fo:table-cell>
 </fo:table-row>


<fo:table-row font-weight="normal" text-align="left">
<fo:table-cell border="thin solid grey" padding-left="2%"><fo:block>PF Account Number</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right" padding-right="2%"><fo:block>
${prefHeader.pfAccountNumber?if_exists}
</fo:block></fo:table-cell>
</fo:table-row>

</fo:table-body>
</fo:table>
</#if>

<fo:block> &#160;</fo:block>
<fo:block> &#160;</fo:block>
<fo:table font-size="8pt" table-layout="fixed" width="100%">
<fo:table-column width="50%"/>
<fo:table-column width="50%"/>
<fo:table-header>
<fo:table-row font-weight="bold" >
<fo:table-cell border="thin solid grey" text-align="center"><fo:block>Earnings</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="center"><fo:block>Deductions</fo:block></fo:table-cell>
</fo:table-row>
</fo:table-header>
<fo:table-body>
<fo:table-row border="thin solid grey">
<fo:table-cell><fo:block >
<fo:table font-size="8pt" table-layout="fixed" width="100%">
<fo:table-column width="50%" text-align="left"/>
<fo:table-column width="50%" text-align="right"/>
<fo:table-body>
<#list payslipItems as payslip>
<#if payslip.isCr=="Y">
<#assign grossEarning=grossEarning+payslip.amount>
<fo:table-row border="thin solid grey" >
<fo:table-cell text-align="left" padding-left="2%"><fo:block >
${payslip.hrName}
</fo:block></fo:table-cell>
<fo:table-cell text-align="right" padding-right="2%"> <fo:block>
${payslip.amount?string("#,#00.00")}
</fo:block></fo:table-cell>
</fo:table-row>
</#if>
</#list>
<!--<fo:table-row >
<fo:table-cell border="thin solid grey" text-align="center"><fo:block >
Total Earnings
</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
${grossEarning?string("#,#00.00")}
</fo:block></fo:table-cell>
</fo:table-row> -->
</fo:table-body>
</fo:table>
</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
<fo:table font-size="8pt" table-layout="fixed" width="100%">
<fo:table-column width="50%"/>
<fo:table-column width="50%"/>
<fo:table-body>
<#list payslipItems as payslip>
<#if payslip.isCr !="Y">
<#assign displayTaxAmount= displayTaxAmount + payslip.amount>
<fo:table-row border="thin solid grey">
<fo:table-cell text-align="left" padding-left="2%"><fo:block >
${payslip.hrName}
</fo:block></fo:table-cell>
<fo:table-cell text-align="right" padding-right="2%"> <fo:block>
${payslip.amount?string("#,#00.00")}
</fo:block></fo:table-cell>
</fo:table-row>
</#if>
</#list>

<#list taxDeductions as taxDeducted>
<#assign displayTaxAmount = displayTaxAmount + taxDeducted.amount>
<fo:table-row border="thin solid grey">
<fo:table-cell text-align="left" padding-left="2%"><fo:block >
${taxDeducted.taxType}
</fo:block></fo:table-cell>
<fo:table-cell text-align="right" padding-right="2%"> <fo:block>
${taxDeducted.amount?string("#,#00.00")}
</fo:block></fo:table-cell>
</fo:table-row>
</#list>

</fo:table-body>
</fo:table>

<#assign totalDeduction=displayTaxAmount+totalLoanAmount>
<!--<fo:table font-size="8pt" table-layout="fixed" width="100%">
<fo:table-column width="50%"/>
<fo:table-column width="50%"/>
<fo:table-body>

<fo:table-row >
<fo:table-cell border="thin solid grey" text-align="left"><fo:block >
Total Deductions
</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="right"> <fo:block>
${totalDeduction?string("#,#00.00")}
</fo:block></fo:table-cell>
</fo:table-row>
</fo:table-body>
</fo:table> -->
</fo:block>
</fo:table-cell>
</fo:table-row>
<fo:table-row >
<fo:table-cell border="thin solid grey" text-align="center"><fo:block >
<fo:table font-size="8pt" table-layout="fixed" width="100%">
<fo:table-column width="50%"/>
<fo:table-column width="50%"/>
<fo:table-body>
<fo:table-row border="thin solid grey" font-weight="bold">
<fo:table-cell text-align="left" padding-left="2%"><fo:block >
Total Earnings
</fo:block></fo:table-cell>
<fo:table-cell text-align="right" padding-right="2%"> <fo:block>
${grossEarning?string("#,#00.00")}
</fo:block></fo:table-cell>
</fo:table-row>
</fo:table-body>
</fo:table>
</fo:block></fo:table-cell>
<fo:table-cell border="thin solid grey" text-align="center"><fo:block >
<fo:table font-size="8pt" table-layout="fixed" width="100%">
<fo:table-column width="50%"/>
<fo:table-column width="50%"/>
<fo:table-body>
<fo:table-row border="thin solid grey" font-weight="bold">
<fo:table-cell text-align="left" padding-left="2%"><fo:block >
Total Deductions
</fo:block></fo:table-cell>
<fo:table-cell text-align="right" padding-right="2%"> <fo:block>
${totalDeduction?string("#,#00.00")}
</fo:block></fo:table-cell>
</fo:table-row>
</fo:table-body>
</fo:table>
</fo:block></fo:table-cell>
</fo:table-row>
</fo:table-body>
</fo:table>

<fo:block> &#160;</fo:block>
<fo:block> &#160;</fo:block>
<fo:block text-align="center" font-size="9pt" font-weight="bold">
Net Salary = ${grossEarning?string("#,#00.00")}(Earnings) - ${totalDeduction?string("#,#00.00")}
(Deductions)=${(grossEarning-totalDeduction)?string("#,#00.00")} ${defCurrencyType}</fo:block>
<fo:block> &#160;</fo:block>
<fo:block> &#160;</fo:block>
<fo:block> &#160;</fo:block>

<fo:block text-align="center" font-size="7pt" font-weight="normal">This payslip is computer generated. Hence does not require a signature.</fo:block>
</#escape>


