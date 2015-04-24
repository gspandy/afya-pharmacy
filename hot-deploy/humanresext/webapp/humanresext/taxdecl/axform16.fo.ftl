<#-- Annexure to Form 16 -->
<#-- Author: Pankaj Sachdeva -->
<#escape x as x?xml>
<#assign totSal = 0>
<#assign totPerq = 0>
<#assign nonTax = 0>

<fo:block text-align="center" font-size="12pt" font-weight="bold">ANNEXURE TO FORM NO.16</fo:block>
<fo:block text-align="center" font-size="9pt" font-weight="bold" width="75%">
Details of Salary Paid and Allowance Exempted u/s 10 and 17(2) of Income Tax Act,1961
</fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block>

<fo:table font-size="9pt" table-layout="fixed" font-weight="normal" border-spacing="3pt">
	<fo:table-column width="60%"/>
	<fo:table-column width="40%"/>
    <fo:table-body>
        <fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>1. Name and address of the Employer</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${companyName?if_exists} </fo:block> <fo:block>
					${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} </fo:block> <fo:block>
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>2. Name of Employee :</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${empFullName?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block> &#160;  &#160; Designation :</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${empDesignation?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block> &#160;  &#160; PAN :</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${prefHeader.panNumber?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block> &#160;  &#160; Employee ID :</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${prefHeader.partyId}
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>
<fo:block>&#160;</fo:block>
	
<fo:table font-size="8pt" table-layout="fixed" border-spacing="3px">
	<fo:table-column />
	<fo:table-column width="40%"/>
	<fo:table-column width="20%"/>
	<fo:table-column width="20%"/>
	<fo:table-column width="20%"/>
	<fo:table-header>
        <fo:table-row font-weight="bold" padding="0.1cm" text-align="left">
            <fo:table-cell border="thin solid grey" number-columns-spanned="4"><fo:block>Salary Details for the period :${parameters.fromDate}  to  ${parameters.thruDate}</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>		
		<fo:table-row font-weight="normal" >
        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left" width="275px">
        	<fo:block>1. Salary as per provisions contained in Sec 17(1)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		<#-- List the total of salary components for the period-->
		<#assign salary = parameters.salary>
		<#list salary.getAllButPerqComponents() as salaryComponent>
			<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
	        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
	        		${salaryComponent.getSalaryHeadName()?upper_case}
				</fo:block></fo:table-cell>
	            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
	            	${salaryComponent.getAmount()?string("0.##")}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border-right="thin solid grey"><fo:block>
	            	&#160;
	            </fo:block></fo:table-cell>
	            <fo:table-cell border-right="thin solid grey"><fo:block>
	            	&#160;
	            </fo:block></fo:table-cell>
			</fo:table-row>
			<#assign totSal = totSal + salaryComponent.getAmount()>	
		</#list>		   
		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" ><fo:block>
        		Total Salary (A)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" border-top="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	${totSal?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" padding="0.1cm" border-right="thin solid grey" ><fo:block>
        		Value of perquisite u/s 17(2) </fo:block><fo:block>(as per Form No.12BA wherever applicable)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	0.00
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" padding="0.1cm" border-right="thin solid grey" ><fo:block>
        		Value of perquisite u/s 17(2) </fo:block><fo:block>(as per Form No.12BA wherever applicable)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	0.00
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		<#assign totPerq = totPerq + perqAmount> <#-- Perq Perq17_2 value will come here -->
		<fo:table-row font-weight="normal" >
        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
        		Profit in lieu of Salary u/s 17(3)</fo:block><fo:block>(as per Form No.12BA wherever applicable)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	0.00
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		<#assign totPerq = totPerq + 0.00> <#-- Perq Perq17_3 value will come here -->
		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
        		Total Perquisites (B)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	${totPerq?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>

		<#assign totSal = totSal + totPerq>
		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
        		Gross Salary (A+B)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" border-top="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	${totSal?string("0.00")}
            </fo:block></fo:table-cell>
		</fo:table-row>

		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
        		2. Details of Allowance exempted under </fo:block><fo:block> section 10 and 17(2)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		<#-- Print the non taxable allowances -->
		<#list salary.getExemptionComponents() as exemption>
			<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
	        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
	        		${exemption.getSalaryHeadName()?upper_case}
				</fo:block></fo:table-cell>
	            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
	            	${exemption.getAmount()?string("0.##")}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
	            	&#160;
	            </fo:block></fo:table-cell>
	            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
	            	&#160;
	            </fo:block></fo:table-cell>
			</fo:table-row>
			<#assign nonTax = nonTax + exemption.getAmount()>
		</#list>
		
		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
        		Total (C)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" border-top="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	${nonTax?string("0.00")} <#-- Maximum for an year -->
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		<fo:table-row font-weight="normal" padding="0.1cm" text-align="left">
        	<fo:table-cell border-left="thin solid grey" border-bottom="thin solid grey" border-right="thin solid grey" padding="0.1cm" text-align="left"><fo:block>
        		Balance : (A+B-C)
			</fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" border-bottom="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" border-bottom="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border-right="thin solid grey" border-top="thin solid grey" border-bottom="thin solid grey" padding="0.1cm" text-align="right"><fo:block>
            	${(totSal - nonTax)?string("0.00")} <#-- Maximum for an year -->
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>
			
</#escape>		
		
	



