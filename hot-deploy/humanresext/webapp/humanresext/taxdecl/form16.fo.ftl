<#-- Form 16 Introduction Page -->
<#-- Author: Pankaj Sachdeva -->
<#escape x as x?xml>
    
<#assign total = 0>
<#assign perqAmount = 0.0> <#-- Calculate total Perquisites Value -->
<#assign balance = 0>
<#assign MAX80C = 100000>  <#-- Maximum limit of 80C sections     -->
<#assign nonTax = 0>

<fo:block text-align="center" font-size="18pt" font-weight="bold" background-color="gray">${companyName?if_exists} </fo:block>
<fo:block text-align="center" font-size="18pt" font-weight="bold" >Form 16 </fo:block>
<fo:table font-size="9pt" table-layout="fixed"  height="100%" width="50%" align="center">
	<fo:table-column width="30%"/>
	<fo:table-column width="70%"/>
    <fo:table-body>
        <fo:table-row >
            <fo:table-cell text-align="left"><fo:block >Employee Name:</fo:block></fo:table-cell>
            <fo:table-cell text-align="left"> <fo:block>${empFullName?if_exists}</fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell text-align="left"> <fo:block>Employee PAN:</fo:block></fo:table-cell>
            <fo:table-cell text-align="left"><fo:block> ${prefHeader.panNumber?if_exists}</fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell text-align="left"> <fo:block >Employee ID:</fo:block></fo:table-cell>
            <fo:table-cell text-align="left"><fo:block>${prefHeader.partyId}</fo:block></fo:table-cell>
		</fo:table-row>
		
		<fo:table-row >
            <fo:table-cell text-align="left"><fo:block >Form 16 Control Number:</fo:block></fo:table-cell>
            <fo:table-cell text-align="left"><fo:block>${prefHeader.partyId}</fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell text-align="left"><fo:block>Assessment Year:	</fo:block></fo:table-cell>
            <fo:table-cell text-align="left"><fo:block> ${parameters.fromDate} to ${parameters.thruDate}
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>	
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
 </fo:leader>
</fo:block>  

<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="10pt">Note: Digitally Signed Form </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="7pt">This form has been signed and certified using a Digital Signature Certificate as specified under section 119 of
the Income-tax Act, 1961.(Please refer Circular No.2/2007,dated 21-5-2007). </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="7pt">The Digital Signature of the signatory has been affixed in the box provided below.To see the details and validate
the signature,you should click on the box. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="center" font-size="8pt">DIGITAL SIGNATURE </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="center" font-size="8pt">Signed using Digital Signature of ${signatory} </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="center" font-size="7pt">Number of pages: <fo:page-number-citation ref-id="last-page"/> (including this page) <#-- To be changed --> </fo:block>
<fo:block> &#160;  </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 

<fo:block text-align="left" font-size="8pt" font-weight="bold">E-file your income-tax Return: </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="8pt" font-weight="normal">
	You can click the link below, to electronically file your Income-tax return. 
	The link would tranfer your Form Data to the e-filing website of Skorydov (www.myITreturn.com).
	On the website you can enter additional details of your Income 
	and file your return electronically as per the provisions of the Income-Tax Department.
	(You need to be connected to the internet to use this facility)</fo:block>
<fo:block> &#160;  </fo:block>

<fo:block text-align="center" font-size="8pt" font-weight="bold">
	<a target="top" class="linktext" href="http://www.myITreturn.com">Click here to prepare your Income-tax Return</a>
</fo:block>
<fo:block text-align="center" font-size="8pt" font-weight="normal">
	If you cannot open the link above then please visit www.myITreturn.com and follow the instructions mentioned therein.
</fo:block>		

<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>


<fo:block break-after="page">&#160;</fo:block>

<#-- Next Page -->
<fo:block text-align="center" font-size="12pt" font-weight="bold">FORM NO.16</fo:block>	
<fo:block text-align="center" font-size="10pt" font-weight="bold">[See rule 31(1)(a)]</fo:block>
<fo:block text-align="center" font-size="9pt" font-weight="bold">
	Certificate under section 203 of the income-tax Act, 1961 for tax deducted at source from income 
	chargeable under the head "Salaries" 
</fo:block>
<fo:block> &#160;  </fo:block>

<fo:table font-size="9pt" table-layout="fixed"  height="100%" font-weight="bold">
	<fo:table-column width="50%"/>
	<fo:table-column width="50%"/>
	<fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border="thin solid grey"><fo:block>Name and address of the Employer</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Name and designation of the Employee</fo:block></fo:table-cell>      
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <fo:table-row font-weight="normal">
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	${companyName?if_exists} </fo:block> <fo:block>
					${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} </fo:block> <fo:block>
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${empFullName?if_exists} </fo:block> <fo:block>
			${prefHeader.partyId}</fo:block> <fo:block>
			${empDesignation?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>	
<fo:block> &#160;  </fo:block>
<fo:table font-size="9pt" table-layout="fixed"  text-align="center" >
	<fo:table-column width="25%"/>
	<fo:table-column width="25%"/>
	<fo:table-column width="50%"/>
	<fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border="thin solid grey"><fo:block>PAN of the Deductor</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>TAN of the Deductor</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>PAN of the Employee</fo:block></fo:table-cell>            
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <fo:table-row >
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            ${companyPAN?if_exists} &#160;	<#-- To be picked up from database -->
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${companyTAN?if_exists}&#160;<#-- To be picked up from database -->
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${prefHeader.panNumber?if_exists}
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>	

<fo:block> &#160;  </fo:block>

<fo:table font-size="8pt" table-layout="fixed"  >
	<fo:table-column width="15%"/>
	<fo:table-column width="35%"/>
	<fo:table-column width="15%"/>
	<fo:table-column width="15%"/>
	<fo:table-column width="20%"/>
	<fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border="thin solid grey" number-columns-spanned="2"><fo:block>
        		Acknowledgement nos. of all quarterly statements of TDS under sub-section (3) of Sec 200 as provided by TIN-FC/NSDL Website 
					<fo:table font-size="8pt" table-layout="fixed" text-align="center">
						<fo:table-column width="15%"/>
						<fo:table-column width="35%"/>
		                <fo:table-body>
		                    <fo:table-row font-weight="bold" >
		                        <fo:table-cell border="thin solid grey">
			                        <fo:block>Quarter 					                        
			                        </fo:block></fo:table-cell>
		                        <fo:table-cell border="thin solid grey">
		                        <fo:block>Acknowledgment No. 
		                        </fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>	                               
                </fo:block>
             </fo:table-cell>
             <fo:table-cell border="thin solid grey" number-columns-spanned="2" text-align="center"><fo:block>
        		Period
					<fo:table font-size="8pt" table-layout="fixed" >
						<fo:table-column width="15%"/>
						<fo:table-column width="15%"/>
		                <fo:table-body>
		                    <fo:table-row >
		                        <fo:table-cell border-right="thin solid grey" border-top="thin solid grey">
			                        <fo:block>From 					                        
			                        </fo:block></fo:table-cell>
		                        <fo:table-cell border-right="thin solid grey" border-top="thin solid grey">
		                        <fo:block>To 
		                        </fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>	                               
                </fo:block>
             </fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block>Assessment Year</fo:block></fo:table-cell>            
        </fo:table-row>
    </fo:table-header>
    <fo:table-body  text-align="center">   
	    <#assign Qcount = 0>
		<#list acks as acks>
			<#assign Qcount = Qcount + 1>
			<#if (acks.quarter = "Q1")>
				<#assign year = parameters.thruDate.substring(0,4)>
				<#assign next = year?eval + 1>
	 
	        	<fo:table-row >
		            <fo:table-cell border="thin solid grey"><fo:block >Q1</fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
		            	${acks.ackNo?if_exists}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border-right="thin solid grey" text-align="center"> <fo:block>
		            	${parameters.fromDate}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border-right="thin solid grey" text-align="center"> <fo:block>
		            	${parameters.thruDate}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border-right="thin solid grey" text-align="center"> <fo:block>
		            	${year}-${next}
		            </fo:block></fo:table-cell>	          
				</fo:table-row>
			<#else>
				<fo:table-row >
		            <fo:table-cell border="thin solid grey"><fo:block >${acks.quarter}</fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
		            	 ${acks.ackNo?if_exists}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border-right="thin solid grey" text-align="center"> <fo:block>
		            	&#160;
		            </fo:block></fo:table-cell>
		            <fo:table-cell border-right="thin solid grey" text-align="center"> <fo:block>
		            	&#160;
		            </fo:block></fo:table-cell>
		            <fo:table-cell border-right="thin solid grey" text-align="center"> <fo:block>
		            	&#160;
		            </fo:block></fo:table-cell>	          
				</fo:table-row>
			</#if>
		</#list>
		<#if Qcount < 4> <#-- 4th quarter is missing -->
			<fo:table-row >
		        <fo:table-cell border="thin solid grey"><fo:block >Q4</fo:block></fo:table-cell>
		        <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
		        	 Not Available as the last Quarterly Statement is yet to be furnished
		        </fo:block></fo:table-cell>
		        <fo:table-cell border-right="thin solid grey" border-bottom="thin solid grey" text-align="center"> <fo:block>
		        	&#160;
		        </fo:block></fo:table-cell>
		        <fo:table-cell border-right="thin solid grey" border-bottom="thin solid grey" text-align="center"> <fo:block>
		        	&#160;
		        </fo:block></fo:table-cell>
		        <fo:table-cell border-right="thin solid grey" border-bottom="thin solid grey" text-align="center"> <fo:block>
		        	&#160;
		        </fo:block></fo:table-cell>	          
			</fo:table-row>
		</#if>
	</fo:table-body>
</fo:table>

<fo:block>&#160;</fo:block>
<fo:block text-align="center" font-size="9pt">DETAILS OF SALARY PAID AND ANY OTHER INCOME AND TAX DEDUCTED</fo:block>
<fo:block>&#160;</fo:block>
<fo:table font-size="8pt"   width="100%">

	<fo:table-column width="60%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	
	<fo:table-header text-align="center">
        <fo:table-row font-weight="bold">
            <fo:table-cell border="thin solid grey" number-columns-spanned="2" width="70%"><fo:block>
        		Particulars                                
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" ><fo:block>Rs</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" ><fo:block>Rs</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" ><fo:block>Rs</fo:block></fo:table-cell>            
        </fo:table-row>
    </fo:table-header>
    
    <#assign salary = parameters.salary>
    <fo:table-body font-weight="normal" >
    	<fo:table-row >
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	1. GROSS SALARY (As per enclosed annexure)
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="center"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="center"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="center"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>	
		<fo:table-row >
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160;(a) Salary as per provisions contained in section 17(1)
            </fo:block></fo:table-cell>
            <fo:table-cell  padding="0.1cm" text-align="right"> <fo:block>
            	 ${salary.getGrossAmount()?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"  padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>	
		<#list salary.getPerqComponents() as perqs>
			<#assign perqAmount = perqAmount + perqs.getAmount()>
		</#list>
		<#assign total = salary.getGrossAmount() + perqAmount>
		<fo:table-row>
            <fo:table-cell padding="0.1cm" border="thin solid grey" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (b) Value of perquisites under section 17(2) (as per Form No:12BA, wherever applicable)
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 ${perqAmount?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>	
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (c) Profits in lieu of salary under section 17(3) (as per Form No: 12BA, wherever applicable)
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 0.0 <#-- To be changed -->
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>	
		<fo:table-row>
            <fo:table-cell  border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (d) Total
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	${total?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>	
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	2. Less : Allowance to the extent exempt u/s 10 (As per enclosed annexure)
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	${salary.getExemptionAmount()?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign balance = total - salary.getExemptionAmount()>
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	3. Balance (1-2)
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	${balance?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign deductions = 0.0>
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	4. DEDUCTIONS :
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (a) Entertainment Allowance
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 0.00
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign deductions = deductions + 0.0>	
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (b) Tax on Employment
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 ${salary.getProfessionalTax()?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign deductions = deductions + salary.getProfessionalTax()>	
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	5. Aggregate of 4 (a) and (b)
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	${deductions?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	&#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>	
		<#assign balance = balance - deductions>
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	6. INCOME CHARGEABLE UNDER THE HEAD 'SALARIES' ( 3-5 )
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	${balance?string("0.00")}
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"><fo:block>
            	7. Add : Any other income reported by the employee
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign houseIncome = 0.0>
		<#assign otherIncome = 0.0>					
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (a) Income under the Head 'Income from House Property'
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 0.00
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign balance = balance + houseIncome>
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (b) Income under the Head 'Income from Other Sources'
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 0.00
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign balance = balance + otherIncome>
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	8. GROSS TOTAL INCOME ( 6+7 )
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	${balance?string("0.00")}
            </fo:block></fo:table-cell>	          
		</fo:table-row>	
	
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	9. DEDUCTIONS UNDER CHAPTER VI-A
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
		<#assign deduct80 = 0> <#-- Deductions under 80 not to exceed 1000000 -->
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; (A) Sections 80C, 80CCC &amp; 80CCD
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>		
		
		<fo:table-row>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
            	&#160; &#160; (a) Section 80C
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	Gross Amount
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 Deductible Amount
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
            	 &#160;
            </fo:block></fo:table-cell>	          
		</fo:table-row>
	<#-- Section 80C deductions -->
	<#assign count = 0>
	<#assign amount = 0>
	<#list decls as deds>
		<#if deds.categoryName == "Section80C">
			<#if (deds.acceptedValue?default(0) > 0)>
				<#assign count = count + 1>
					<fo:table-row>
			            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
			            	&#160; &#160; &#160; (${count}) ${deds.itemName}
			            </fo:block></fo:table-cell>
			            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			            	${deds.numValue?string("0.00")}
			            </fo:block></fo:table-cell>
			            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			            	 ${deds.acceptedValue?string("0.00")}
			            </fo:block></fo:table-cell>
			            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			            	 &#160;
			            </fo:block></fo:table-cell>	          
					</fo:table-row>
				<#assign deduct80 = deduct80 + deds.acceptedValue?default(0)>
			</#if>
		</#if>	
	</#list>
	
	<#-- Employee Provident Fund is also part of Section 80C -->
	
	<#list salary.getDeductComponents() as deducts>
		<#if deducts.getSalaryHeadName()?contains("Provident")> 
			<#assign count = count + 1>
				<fo:table-row>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2" > <fo:block>
		            	 &#160; &#160; &#160;(${count}) ${deducts.getSalaryHeadName()}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
		            	${deducts.getAmount()?string("0.00")}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
		            	 ${deducts.getAmount()?string("0.00")}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
		            	 &#160;
		            </fo:block></fo:table-cell>	          
				</fo:table-row>
			<#assign deduct80 = deduct80 + deducts.getAmount()>
		 </#if> 
	</#list>
	<#-- Section 80CCC deductions -->
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	&#160; &#160; (b) Section 80CCC
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>	          
	</fo:table-row>	
	<#assign count = 0>
	<#assign amount = 0>
	<#list decls as deds>
		<#if deds.categoryName == "Section80CCC">
			<#if (deds.acceptedValue?default(0) > 0)>
				<#assign count = count + 1>
				<fo:table-row>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
		            	&#160; &#160; (${count}) ${deds.itemName}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
		            	${deds.numValue?string("0.00")}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
		            	 ${deds.acceptedValue?string("0.00")}
		            </fo:block></fo:table-cell>
		            <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
		            	 &#160;
		            </fo:block></fo:table-cell>	          
				</fo:table-row>
				<#assign deduct80 = deduct80 + deds.acceptedValue?default(0)>
			</#if>
		</#if>
	</#list>
	
	<#-- Maximum limit of deductions is 100000 -->
	<#if (deduct80 > MAX80C)>
		<#assign deduct80 = MAX80C>
	</#if>

	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	&#160; &#160; Total (a) + (b) + (c)
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 ${deduct80?string("0.00")}
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<#-- Other Sections -->
	<#assign deduct80NC = 0> <#-- Non 80C deductions -->			
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	&#160; (B) Other Sections under Chapter VI-A
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<fo:table-row>
	    <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>	
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	Gross Amount
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	Qualifying Amount
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	Deductible Amount
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>	          
	</fo:table-row>

	<#assign count = 0>
	<#assign amount = 0>
	<#list decls as deds>
		<#if !(deds.categoryName?starts_with("Section80C"))>
			<#if (deds.acceptedValue?default(0) > 0)>
				<#assign count = count + 1>
				<fo:table-row>
				    <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			        	 (${count}) ${deds.categoryName} ${deds.itemName}
			        </fo:block></fo:table-cell>	
			        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			        	 ${deds.numValue}
			        </fo:block></fo:table-cell>
			        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			        	${deds.acceptedValue?default(0)}
			        </fo:block></fo:table-cell>
			        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			        	${deds.acceptedValue?default(0)}
			        </fo:block></fo:table-cell>
			        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
			        	 &#160;
			        </fo:block></fo:table-cell>	          
				</fo:table-row>
				<#assign deduct80NC = deduct80NC + deds.acceptedValue?default(0)>
			</#if>
		</#if>	
	</#list>
	<fo:table-row>
	    <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>	
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${deduct80NC?string("0.00")}
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<#assign totDed = deduct80NC + deduct80>
	<#if (totDed > balance)>
		<#assign totDed = balance>
	</#if>
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	10. Aggregate of deductible amounts under chapter VI-A
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 ${totDed?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	<#assign balance = balance - totDed>
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	11. Total income ( 8-10 )
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${balance?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	12. Tax on total income
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${salary.getSimpleTax()?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>

	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	13. Surcharge ( on tax computed at S.No 12)
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${salary.getSurcharge()?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>

	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	14. (i) Primary Education Cess (on Tax [12] + Surcharge[13])
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${salary.getEducationCess()?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>

	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	&#160; (ii) Higher Education Cess
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${salary.getHigherEduCess()?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>

	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	15. Tax Payable ( 12+13+14(i)+14(ii) )
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${salary.getTaxAmount()?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	16. Relief under section 89
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	0.00 <#--To be changed -->
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	17. Tax Payable ( 15-16 )
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 &#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${salary.getTaxAmount()?string("0.00")} <#--To be changed -->
        </fo:block></fo:table-cell>	          
	</fo:table-row>

	<#assign totTaxPaid = totTaxPaid - salary.getProfessionalTax()><#-- Professional Tax is not TDS -->
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	18. Less : (a) Tax Deducted at source u / s 192 (1)
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 ${totTaxPaid?string("0.00")}<#-- Put sum of tax from payslips --> 
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<#assign perqEmployerTax = 0.00> <#-- Perq Tax Paid By Employer --> 
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	&#160; (b) Tax paid by the employer on behalf of the employee u/s 192 (1A) on perquisities u/s 17(2)
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	 ${perqEmployerTax?string("0.00")}
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<#assign totTDSPaid = perqEmployerTax + totTaxPaid>
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>	
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${totTDSPaid?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>
	
	<#assign remainingTax = salary.getTaxAmount() - totTDSPaid> <#-- Ideally should be zero if TDS done -->
	<fo:table-row>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="left" number-columns-spanned="2"> <fo:block>
        	19. Balance Tax payable / refundable ( 17-18 )
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	&#160;
        </fo:block></fo:table-cell>
        <fo:table-cell border="thin solid grey" padding="0.1cm" text-align="right"> <fo:block>
        	${remainingTax?string("0.00")}
        </fo:block></fo:table-cell>	          
	</fo:table-row>
												
	</fo:table-body>
</fo:table>

<fo:block>  &#160;  </fo:block>
<fo:block text-align="left" font-weight="bold" font-size="12pt">DETAILS OF TAX DEDUCTED AND DEPOSITED INTO CENTRAL GOVERNMENT ACCOUNT  </fo:block>

<fo:block>  &#160;  </fo:block>
<fo:table font-size="9pt" table-layout="fixed" font-weight="bold" text-align="center">
	<fo:table-column width="5%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="5%"/>
	<fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border="thin solid grey"><fo:block>Sr. No.</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>TDS (Rs)</fo:block></fo:table-cell> 
            <fo:table-cell border="thin solid grey"><fo:block>Surcharge (Rs)</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Education Cess (Rs)</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Higher Education  Cess (Rs)</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Total Tax Deposited (Rs)</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Cheque/DD No. (if any)</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>BSR Code of Bank Branch</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Date on which Tax Deposited</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>TDS (Rs)</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Transfer Vchr/Challan Iden. No.</fo:block></fo:table-cell>
                 
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
    	<#assign totTdsAmount = 0>
		<#list tdsList as tdslit>
	        <fo:table-row font-weight="normal">
	            <fo:table-cell border="thin solid grey"><fo:block >
	            	${tdslit_index + 1}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.tds?string("0.00")}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.surcharge?string("0.00")}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.educationCess?string("0.00")}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.higherEduCess?string("0.00")}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.totTds?string("0.00")}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.chequeNo}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.branchCode}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.paymentDate}
	            </fo:block></fo:table-cell>
	            <fo:table-cell border="thin solid grey" > <fo:block>
	            	${tdslit.challanNo} &#160;
	            </fo:block></fo:table-cell>
			</fo:table-row>
			<#assign totTdsAmount = totTdsAmount + tdslit.totTds>
		</#list>
		<fo:table-row font-weight="normal">
            <fo:table-cell border="thin solid grey"><fo:block >
            	Total
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" font-weight="bold"> <fo:block>
            	${totTdsAmount?string("0.00")}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" > <fo:block>
            	&#160;
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>	


<fo:block>  &#160;  </fo:block>
<fo:block font-size="8pt">
I, ${signatory?if_exists?upper_case} son/daughter of ${signatoryParent?if_exists?upper_case}, working in the capacity of AUTHORISED SIGNATORY, do hereby certify that a
sum of Rs. ${totTdsAmount?string("0.00")} (${Static["com.smebiz.Util.Converter"].rupeesInWords(totTdsAmount?string("0.00"))?upper_case}) has been deducted at source 
and paid to the credit of the Central Government.</fo:block>
<fo:block>  &#160;  </fo:block>
I further certify that the information given above is true and correct based on the books of account, documents and other available records which 
includes the declaration given by the employee. 
<fo:block>  &#160;  </fo:block>
<fo:block>  &#160;  </fo:block>


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

<fo:block break-after="page">&#160;</fo:block>
<#include "form12BA.fo.ftl">	

<fo:block break-after="page">&#160;</fo:block>
<#include "axform16.fo.ftl" >
</#escape>
