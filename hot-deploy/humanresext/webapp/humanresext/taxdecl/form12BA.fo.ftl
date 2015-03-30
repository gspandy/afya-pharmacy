<#-- Form 12BA -->
<#-- Author: Pankaj Sachdeva -->
<#escape x as x?xml>
<#assign totSal = 0>
<#assign totPerq = 0>
<#assign nonTax = 0>

<fo:block text-align="center" font-size="12pt" font-weight="bold">FORM NO. 12BA</fo:block>
<fo:block text-align="center" font-size="10pt" font-weight="bold">{See Rule 26A(2)(b)}</fo:block>
<fo:block text-align="center" font-size="9pt" font-weight="bold" >
Statement showing particulars of perquisites, other fringe benefits or 
amenities and profits in lieu of salary with value thereof
</fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block>

<fo:table font-size="9pt" table-layout="fixed"  height="100%" font-weight="normal">
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
        <fo:table-cell><fo:block>2. TAN :</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${TAN?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>3. TDS Assessment Range of the Employer :</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${TDSRange?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>4. Name of Employee :</fo:block></fo:table-cell>
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
		
		<#assign Isdirector = "No">
		<#if empDesignation?if_exists?contains("DIRECTOR")>
			<#assign Isdirector = "Yes">
		</#if>
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>5. Is the Employee a Director or a person with substantial </fo:block><fo:block> 
				&#160;  &#160; interest in the company (Where the employer is a company) :
			</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${Isdirector}
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		<#assign salary = parameters.salary>
		<fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>6. Income under the Head "Salaries" of the Employee :</fo:block><fo:block>
				&#160;  &#160; (Other than from perquisites)
			</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${salary.getGrossAmount()?string("0.##")}
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		<!-- <#assign fromYear = parameters.fromDate.substring(0,4)>
		<#assign endYear = parameters.thruDate.substring(0,4)> -->
		
		<#assign fromYear = parameters.fromDate>
		<#assign endYear = parameters.thruDate>
		
        <fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>7. Financial Year :
			</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	${fromYear} - ${endYear}
            </fo:block></fo:table-cell>
		</fo:table-row>  

        <fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>&#160;
			</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>&#160;
            </fo:block></fo:table-cell>
		</fo:table-row> 
		
        <fo:table-row font-weight="normal" text-align="left">
        	<fo:table-cell><fo:block>8. Valuation of perquisites:
			</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>   
	</fo:table-body>
</fo:table>

<fo:table font-size="8pt" table-layout="fixed" >
	<fo:table-column width="5%"/>
	<fo:table-column width="30%"/>
	<fo:table-column width="20%"/>
	<fo:table-column width="25%"/>
	<fo:table-column width="20%"/>
	<fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block>Sl. No.</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="left"><fo:block>Nature of perquisite</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block>Value of perquisite as per rules(Rs.)</fo:block></fo:table-cell> 
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block>Amount,if any paid by employee(Rs.)</fo:block></fo:table-cell> 
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block>Amount of Taxable perquisite(Rs.)</fo:block></fo:table-cell>            
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
	    <#assign totTaxablePerq = 0>
		<#list salary.getPerqComponents() as preqs>
			<#assign taxablePerq = 0>
	        <fo:table-row >
	            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
	            (${preqs_index + 1})
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
	            	${preqs.getSalaryHeadName()}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
	            	${preqs.getAmount()?string("0.##")}
	            </fo:block></fo:table-cell>
	       	    <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
	            	0.00<#-- Assuming nothing is paid by employee -->
	            </fo:block></fo:table-cell>
	            
	            <#assign taxablePerq = (preqs.getAmount() - 0)> <#--Assuming amount paid by employee is zero -->
	            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
	            	${taxablePerq?string("0.##")}
	            </fo:block></fo:table-cell>
			</fo:table-row>
			<#assign totTaxablePerq = totTaxablePerq + taxablePerq>
		</#list>
		<fo:table-row >
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	Total Value of Perquisites
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	${totTaxablePerq}
            </fo:block></fo:table-cell>
       	    <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	0.00<#-- Assuming nothing is paid by employee -->
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	${totTaxablePerq?string("0.##")}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	Value of profits for in lieu of salary as per s. 17(3)
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	0.00
            </fo:block></fo:table-cell>
       	    <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	0.00
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="left"> <fo:block>
            	0.00
            </fo:block></fo:table-cell>
		</fo:table-row>	
	</fo:table-body>
</fo:table>	

<fo:block> &#160;  </fo:block>
<fo:table font-size="9pt" table-layout="fixed"  font-weight="normal" text-align="left">
	<fo:table-column width="40%"/>
	<fo:table-column width="60%"/>
    <fo:table-body>
        <fo:table-row >
            <fo:table-cell><fo:block >
            	9. Details of Tax
            </fo:block></fo:table-cell>
       		<fo:table-cell><fo:block >
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block >
            	&#160; &#160; a) Tax Deducted from Salary of Employee u/s 192(1) :
            </fo:block></fo:table-cell>
       		<fo:table-cell><fo:block >
            	${salary.getTaxAmount()?string("0.##")}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block >
            	&#160; &#160; b) Tax Paid by Employer on behalf of Employee u/s 192(1A) :
            </fo:block></fo:table-cell>
       		<fo:table-cell><fo:block >
            	0.00<#-- Assuming Employer does not pay tax -->
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block >
            	&#160; &#160; c) Total Tax Paid :
            </fo:block></fo:table-cell>
       		<fo:table-cell><fo:block >
            	${salary.getTaxAmount()?string("0.##")}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block >
            	&#160; &#160; d) Date of Payment into Goverment Treasury :
            </fo:block></fo:table-cell>
       		<fo:table-cell><fo:block >
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>

<fo:block> &#160;  </fo:block>
<fo:block text-align="center" font-size="9pt" font-weight="bold">DECLARATION BY EMPLOYER</fo:block>	
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="8pt" font-weight="normal">
I, ${signatory?if_exists?upper_case} son/daughter of ${signatoryParent?if_exists?upper_case}, 
working in the capacity of AUTHORISED SIGNATORY, do hereby declare on behalf of M/s. ${companyName?if_exists?upper_case} 
that the information given above is true and correct and based on the books of accounts , documents and other relevant records or 
information available with us and the details of value of each such perquisite are in accordance with section 17 
and rules framed thereunder  and that such information  is true and correct.
</fo:block>	

<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>

<fo:table font-size="8pt" table-layout="fixed" text-align="left" height="100%">
	<fo:table-column width="30%"/>
	<fo:table-column width="30%"/>
	<fo:table-column width="30%"/>
    <fo:table-body>
        <fo:table-row >
           <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	 For &#160; ${companyName?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	 This form is digitally signed.
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	 Signature of the person responsible for deduction of tax
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row ><fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row ><fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row ><fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
        </fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block>
            	Place: ${postalAddress.city?if_exists}
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	Full Name: ${signatory?if_exists?upper_case}
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell><fo:block>
            	Date: ${parameters.thruDate}
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	Designation: Authorised Signatory
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>

</#escape>


