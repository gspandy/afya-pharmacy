<#-- Form 6A Consolidated PF -->
<#-- Author: Pankaj Sachdeva -->

<#assign satutRate = "12%">
<#assign months = ["Mar. Paid in Apr.", "May", "June", "July", "August", "September", "October", "November", "December", "January", "February", "Feb. Paid in Mar.", "Arrears if any"]>
<#assign yrTotal = 0><#-- Total EPF amount of Employer -->
<#-- Comment -->
<#assign comments = ["Date of leaving serivce"," ", "Reason for leaving service", " ", 
"Certified that the total amount of contribution indicated in this card i.e Rs.", " ",	
"Col.3( b ) + Col.4( c ) has already been remitted in",  	
								"full in E.P.F. A/c No.1    (Provindent Fund.", 	
								"Contributions A/c) and in Account No.10",	
								"(Employee's Pension Fund Contributions account vide notebelow)", " ", " ", " "] >
								
<#-- <fo:flow flow-name="xsl-region-body"> -->

<fo:block text-align="center" font-size="8pt">(FOR UNEXEMPTED ESTABLISHMENTS' ONLY) </fo:block>
<fo:block text-align="center" font-size="10pt">	(FORM 3-A Revised) </fo:block>
<fo:block text-align="center" font-size="12pt">	THE EMPLOYEES' PROVIDENT FUND SCHEME, 1952</fo:block>
<fo:block text-align="center" font-size="8pt"> [PARAGRAPH 35 &amp; 42] &#160; AND</fo:block>
<fo:block text-align="center" font-size="12pt">THE EMPLOYEES PENSION SCHEME, 1995</fo:block>
<fo:block text-align="center" font-size="8pt">[PARAGRAPH 19]</fo:block>
	<fo:block> &#160;</fo:block>
	<fo:block> &#160;</fo:block>
<fo:block text-align="left" font-size="8pt">
Contribution Card for the Currency Period From  ${parameters.fromDate} 
 to ${parameters.thruDate} </fo:block>

	<fo:block> &#160;</fo:block>
<fo:table font-size="8pt" table-layout="fixed" text-align="left" height="100%">
	<fo:table-column width="40%"/>
	<fo:table-column width="60%"/>
    <fo:table-body>
        <fo:table-row >
            <fo:table-cell>
                <fo:block>1. Account No. : ${prefHeader.pfAccountNumber?if_exists} 					                        
                </fo:block></fo:table-cell>
            <fo:table-cell >
            	<fo:block>4. Name &amp; Address of the Establishment : ${companyName?if_exists} ${postalAddress.city?if_exists} 
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell>
                <fo:block>2. Name/Surname: ${empFullName} 					                        
                </fo:block></fo:table-cell>
            <fo:table-cell >
            	<fo:block>5. Satutory rate of contribution : ${satutRate} 
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell>
                <fo:block>3. Father's/husband Name:  					                        
                </fo:block></fo:table-cell>
            <fo:table-cell >
            	<fo:block>6. Voluntary higher rate of employees contribution if any:  NIL <#-- Should be changed -->
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>	            
	<fo:block> &#160;</fo:block>
<fo:block text-align="left" font-size="8pt"> &#160; </fo:block>
<fo:table font-size="6pt" table-layout="fixed" text-align="center">
	<fo:table-column width="5%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="10%"/>
	<fo:table-column width="15%"/>										
    <fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border="thin solid grey"><fo:block>MONTH</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Amount of Wages</fo:block></fo:table-cell>
             <fo:table-cell border="thin solid grey" number-columns-spanned="5">
            	<fo:block>CONTRIBUTIONS    
				<fo:table font-size="6pt" table-layout="fixed" text-align="center" >
					<fo:table-column width="40%"/>
					<fo:table-column width="60%"/>
	                <fo:table-body>
	                    <fo:table-row font-weight="bold" >
	                        <fo:table-cell border="thin solid grey" number-columns-spanned="2">
		                        <fo:block>Worker Share 	
		                        <fo:table font-size="6pt" table-layout="fixed" text-align="center" >
									<fo:table-column width="50%"/>
									<fo:table-column width="50%"/>
					                <fo:table-body>
					                    <fo:table-row font-weight="bold" >
					                        <fo:table-cell border="thin solid grey">
						                        <fo:block>EPF 
						                        	<fo:table font-size="6pt" table-layout="fixed" text-align="center" >
														<fo:table-column width="100%"/>
														<fo:table-body>
										                    <fo:table-row font-weight="bold" >
										                        <fo:table-cell border="thin solid grey">
											                        <fo:block>a 					                        
											                        </fo:block>
											                    </fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>	  							                        
						                        </fo:block>
						                    </fo:table-cell>
					                        <fo:table-cell border="thin solid grey">
					                        	<fo:block>Total
					                        		<fo:table font-size="6pt" table-layout="fixed" text-align="center" >
														<fo:table-column width="100%"/>
														<fo:table-body>
										                    <fo:table-row font-weight="bold" >
										                        <fo:table-cell border="thin solid grey">
											                        <fo:block>b 					                        
											                        </fo:block>
											                    </fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>	   
					                        	</fo:block>
					                        </fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>	  				                        
		                        </fo:block>
		                    </fo:table-cell>
	                        <fo:table-cell border="thin solid grey" number-columns-spanned="3">
	                        <fo:block>Employer's Share 
		                        <fo:table font-size="6pt" table-layout="fixed" text-align="center" >
									<fo:table-column width="33.3%"/>
									<fo:table-column width="33.3%"/>
									<fo:table-column width="33.3%"/>
					                <fo:table-body>
					                    <fo:table-row font-weight="bold" >
					                        <fo:table-cell border="thin solid grey" >
						                        <fo:block>EPF
						                        	<fo:table font-size="6pt" table-layout="fixed" text-align="center" >
														<fo:table-column width="100%"/>
														<fo:table-body>
										                    <fo:table-row font-weight="bold" >
										                        <fo:table-cell border="thin solid grey">
											                        <fo:block>a 					                        
											                        </fo:block>
											                    </fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>	   					                        
						                    	</fo:block></fo:table-cell>
					                        <fo:table-cell border="thin solid grey" >
					                        	<fo:block>EPS
					                        		<fo:table font-size="6pt" table-layout="fixed" text-align="center" >
														<fo:table-column width="100%"/>
														<fo:table-body>
										                    <fo:table-row font-weight="bold" >
										                        <fo:table-cell border="thin solid grey">
											                        <fo:block>b 					                        
											                        </fo:block>
											                    </fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>	   
					                        	</fo:block></fo:table-cell>
					                        <fo:table-cell border="thin solid grey" >
					                        	<fo:block>Total
					                        		<fo:table font-size="6pt" table-layout="fixed" text-align="center" >
														<fo:table-column width="100%"/>
														<fo:table-body>
										                    <fo:table-row font-weight="bold" >
										                        <fo:table-cell border="thin solid grey">
											                        <fo:block>c 					                        
											                        </fo:block>
											                    </fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>	   
					                        	</fo:block></fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>	  
	                        </fo:block></fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>	                               
            	</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>*Refund of Advances</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Break in membership reckonable service</fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey"><fo:block>Remarks</fo:block></fo:table-cell>
      </fo:table-row>      
    </fo:table-header>
    <fo:table-body>
        <#assign rowColor = "white">
        <fo:table-row>
                    <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                        <fo:block>1</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                        <fo:block>2</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" number-columns-spanned="2">
                        <fo:block>3</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" number-columns-spanned="3">
                        <fo:block>4</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                        <fo:block>5</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                        <fo:block>6</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                        <fo:block>7</fo:block>
                    </fo:table-cell>
            	</fo:table-row>
            	
        <#list PfPayments as pfs>
            <fo:table-row>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>${months[pfs_index]} </fo:block>
                </fo:table-cell>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>${pfs.Wages?default("")}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>${pfs.employeeAmount?string("0.##")}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>${pfs.employeeAmount?string("0.##")}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>${pfs.epfDiff?string("0.##")}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>${pfs.pensionAmount?string("0.##")}</fo:block>
                </fo:table-cell>
                <#assign employer = pfs.epfDiff + pfs.pensionAmount>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>${employer?string("0.##")}</fo:block>
                </fo:table-cell>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>0</fo:block>	<#-- Refund of Advances defaulted to zero -->
                </fo:table-cell>
                <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                    <fo:block>&#160;</fo:block>	<#-- Break in membership reckonable service to null -->
                </fo:table-cell>
                <fo:table-cell border-right="thin solid grey" padding="2pt" background-color="white">
                    <fo:block> ${comments[pfs_index]} &#160;
					</fo:block>
                </fo:table-cell>
            </fo:table-row>
            <#-- toggle the row color -->
            <#if rowColor == "white">
                <#assign rowColor = "#eeeeee">
            <#else>
                <#assign rowColor = "white">
            </#if>
            <#assign yrTotal = yrTotal + employer> <#-- Add current month's PF -->        
        </#list> 
        <#-- Total -->
        <fo:table-row>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block font-weight="bold">Total</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>${yrTotal?string("0.00")}</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell>
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell> 
            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                <fo:block>&#160;</fo:block>
            </fo:table-cell>
    	</fo:table-row>         
   </fo:table-body>
</fo:table>

	<fo:block> &#160;</fo:block>
	<fo:block text-align="left" font-size="7pt">Certified that the  difference  between  the total of the  contributions  shown  under the columns (3) &amp; (4) of the above table and that 
	arrived  at  on  the  total  wages  shown  in column (2) at the prescribed rate is solely due to the rounding off the contributions to be  
	nearest rupee under the rules. </fo:block>
	<fo:block text-align="left" font-size="7pt"> * Contributions for the month of March paid in April
	</fo:block>
	<fo:block> &#160;</fo:block>
	<fo:block text-align="left" font-size="8pt">Date: </fo:block>
	<fo:block text-align="right" font-size="8pt">Signature of Employer (with office seal)</fo:block>
	<fo:block> &#160;</fo:block>
	<fo:block text-align="left" font-size="7pt"> In respect of the Form ( 3A )  sent to the Regional Office during the course of the currency period for the purpose of final settlement 
		of the accounts of the members who had left service, details of date and reasons for leaving service and also a certificate as shown  
		in the Remarks Column should be added.
	</fo:block>

	



