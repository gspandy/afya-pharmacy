<#-- Form 6A Consolidated PF -->
<#-- Author: Pankaj Sachdeva -->

<#escape x as x?xml>
<#assign satutRate = "12%">
<#assign space = "&#160;">
<#assign months = ["Mar. Paid in Apr.", "May", "June", "July", "August", "September", "October", "November", "December", "January", "February", "Feb. Paid in Mar.", "Arrears if any"]>

<#-- <fo:flow flow-name="xsl-region-body"> -->

<fo:block text-align="center" font-size="8pt">(FOR UNEXEMPTED ESTABLISHMENTS' ONLY) </fo:block>
<fo:block text-align="center" font-size="10pt">	FORM 6-A</fo:block>
<fo:block text-align="center" font-size="12pt">	THE EMPLOYEES' PROVIDENT FUND SCHEME, 1952</fo:block>
<fo:block text-align="center" font-size="8pt">( PARAGRAPH 43 ) &#160; AND</fo:block>
<fo:block text-align="center" font-size="12pt">THE EMPLOYEES PENSION SCHEME, 1995</fo:block>
<fo:block text-align="center" font-size="8pt">[PARAGRAPH 20 (4)]</fo:block>

<fo:block> &#160;  </fo:block>

<fo:block text-align="left" font-size="7pt">
	Annual statement of contribution for the Currency period from &#160; 1st April ${parameters.fromDate.substring(0,4)} &#160; to &#160; 31st March ${parameters.thruDate.substring(0,4)} 
</fo:block>

<fo:table font-size="7pt" table-layout="fixed" text-align="left" height="100%">
	<fo:table-column width="40%"/>
	<fo:table-column width="60%"/>
    <fo:table-body>
        <fo:table-row >
            <fo:table-cell>
                <fo:block>Name &amp; Address of the Establishment : ${companyName?if_exists} ${postalAddress.city?if_exists} 		                        
                </fo:block></fo:table-cell>
            <fo:table-cell >
            	<fo:block>Satutory rate of contribution : ${satutRate} 
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row >
            <fo:table-cell>
                <fo:block>Code No. of the Establishment : ${codeNo?default("")} 					                        
                </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
            	 No. of members voluntarily contributing at a higher rate : 0 <#-- By default zero at higher rate of PF -->
            </fo:block></fo:table-cell>
		</fo:table-row>
	</fo:table-body>
</fo:table>	


<fo:block> &#160;  </fo:block>

	<fo:table font-size="6pt" table-layout="fixed" text-align="center">
					<fo:table-column  />
					<fo:table-column width="3%"/>
					<fo:table-column width="3%"/>
					<fo:table-column width="10%"/>
					<fo:table-column width="20%"/>
					<fo:table-column width="18%"/>
					<fo:table-column width="15%"/>
					<fo:table-column width="12%"/>
					<fo:table-column width="8%"/>
					<fo:table-column width="8%"/>
					<fo:table-column width="6%"/>										
	                <fo:table-header>
	                    <fo:table-row font-weight="bold">
	                        <fo:table-cell border="thin solid grey"><fo:block>Sl. No.  (1)</fo:block></fo:table-cell>
	                        <fo:table-cell border="thin solid grey"><fo:block>Account  No.  (2)</fo:block></fo:table-cell>
	                        <fo:table-cell border="thin solid grey"><fo:block>Name of member  (in block letters)  (3)</fo:block></fo:table-cell>
	                        <fo:table-cell border="thin solid grey"><fo:block>Wages, retaining  allowance  (if any) &amp; DA including  cash value of food  concession paid during the  currency period.  (4)</fo:block></fo:table-cell>
	                        <fo:table-cell border="thin solid grey"><fo:block>Amount of worker's  contributions deducted  from the wages  EPF  (5)</fo:block></fo:table-cell>
	                        <fo:table-cell border="thin solid grey" number-columns-spanned="2">
	                        	<fo:block>Employer's Contribution w.e.f. 16-11-1995    
								<fo:table font-size="6pt" table-layout="fixed" text-align="center" height="100%">
									<fo:table-column width="15%"/>
									<fo:table-column width="12%"/>
					                <fo:table-body>
					                    <fo:table-row font-weight="bold" >
					                        <fo:table-cell border="thin solid grey" height="100%">
						                        <fo:block>EPF difference between 12% &amp; 8 1/3 % (6) 					                        
						                        </fo:block></fo:table-cell>
					                        <fo:table-cell border="thin solid grey"><fo:block>Pension Fund 12% &amp; 8 1/3 % (7) </fo:block></fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>	                               
	                        	</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell border="thin solid grey"><fo:block>Refund of Advance (8)</fo:block></fo:table-cell>
	                        <fo:table-cell border="thin solid grey"><fo:block>Rate of higher voluntary contribution (if any)</fo:block></fo:table-cell>
	                        <fo:table-cell border="thin solid grey"><fo:block>Remarks (10)</fo:block></fo:table-cell>
	                    </fo:table-row>
	                </fo:table-header>
	                <fo:table-body>
	                    <#assign rowColor = "white">
	                    <#list consolidatedPF as pfs>
	                        <fo:table-row>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
	                                <fo:block>${pfs_index + 1}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
	                                <fo:block>Account No</fo:block>
	                            </fo:table-cell>
	                            <#assign name = Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator, pfs.partyId, null)>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>${name?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>${pfs.wages?string("0.##")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>${pfs.employeeAmount?string("0.##")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>${pfs.epfDiff?string("0.##")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>${pfs.pensionAmount?string("0.##")}7</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>${pfs.refund?string("0.##")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>&#160;</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
	                                <fo:block>${pfs.remarks?default("")}</fo:block>
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <#-- toggle the row color -->
	                        <#if rowColor == "white">
	                            <#assign rowColor = "#eeeeee">
	                        <#else>
	                            <#assign rowColor = "white">
	                        </#if>        
	                    </#list>          
	               </fo:table-body>
	</fo:table>
<#-- Consolidated Contributions for each month -->
<fo:block text-align="left" font-size="8pt"> &#160;  </fo:block>
	<fo:table font-size="6pt" table-layout="fixed" text-align="center">
			<fo:table-column width="3%"/>
			<fo:table-column width="7%"/>
			<fo:table-column width="15%"/>
			<fo:table-column width="10%"/>
			<fo:table-column width="10%"/>
			<fo:table-column width="10%"/>
			<fo:table-column width="10%"/>
			<fo:table-column width="10%"/>
			<fo:table-column width="25%"/>										
            <fo:table-header>
                <fo:table-row font-weight="bold">
                    <fo:table-cell border="thin solid grey"><fo:block>Sl. No.</fo:block></fo:table-cell>
                    <fo:table-cell border="thin solid grey"><fo:block>Month</fo:block></fo:table-cell>
                     <fo:table-cell border="thin solid grey" number-columns-spanned="2">
                    	<fo:block>Amount Remitted    
						<fo:table font-size="6pt" table-layout="fixed" text-align="center" height="100%">
							<fo:table-column width="15%"/>
							<fo:table-column width="10%"/>
			                <fo:table-body>
			                    <fo:table-row font-weight="bold" >
			                        <fo:table-cell border="thin solid grey" height="100%">
				                        <fo:block>EPF Contributions including refund of advances A/c No. 1 					                        
				                        </fo:block></fo:table-cell>
			                        <fo:table-cell border="thin solid grey">
			                        <fo:block>Pension Fund Contributions A/c No.10 
			                        </fo:block></fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>	                               
                    	</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey" number-columns-spanned="2">
                    	<fo:block>(Admn. Charges Rs. at 0.65% of wages)    
						<fo:table font-size="6pt" table-layout="fixed" text-align="center" height="100%">
							<fo:table-column width="10%"/>
							<fo:table-column width="10%"/>
			                <fo:table-body>
			                    <fo:table-row font-weight="bold" >
			                        <fo:table-cell border="thin solid grey" height="100%">
				                        <fo:block>DLI Contribution A/c No. 21 					                        
				                        </fo:block></fo:table-cell>
			                        <fo:table-cell border="thin solid grey">
			                        <fo:block>Adm. Charges A/c No. 21 
			                        </fo:block></fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>	                               
                    	</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="thin solid grey"><fo:block>EDLI ADM.Charges 0.01% A/c No. 22</fo:block></fo:table-cell>
                    <fo:table-cell border="thin solid grey"><fo:block>Cols. 5, 6, 7 Rs. Aggregate() contributions()</fo:block></fo:table-cell>
     
                    <fo:table-cell border-top="thin solid grey" border-right="thin solid grey"><fo:block>I. Total number of contribution cards 
                        enclosed (Form 3A.........................)
						II. Certified that form 3A duly
						completed, of all the members listed
						in this statement are enclosed. The
						F3A already sent during the course
						of the currency period for the final
						settlement of the concerned
						member's account vide 'Remarks'
						furnished against the names of the
						respective members above, are also
						enclosed.</fo:block></fo:table-cell>
                </fo:table-row>
            </fo:table-header>
            <fo:table-body>
                <#assign rowColor = "white">
                
                <#assign epf = 0>
                <#assign dli = 0>
                <#assign admCharge = 0>
                <#assign admDli = 0> 
                <#assign totCont = 0> <#-- Employee + Employer Contribution -->
                
                <#assign totalEPF = 0>
                <#assign totalPension = 0>
                <#assign totalDli = 0>
                <#assign totalAdmCharge = 0>
                <#assign totalDliAdmCharge = 0>
                <#assign aggregate = 0>
                
                <#list monthWisePF as mpfs>
                    <fo:table-row>
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                            <fo:block>${mpfs_index + 1}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                            <fo:block>${mpfs.periodFrom} </fo:block>
                        </fo:table-cell>  
                
                        <#assign epf = mpfs.epfDiff + mpfs.refund> 
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${epf?string("0.##")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${mpfs.pensionAmount?string("0.##")}</fo:block>
                        </fo:table-cell>
                       
                        <#assign dli = mpfs.wages * 0.005> <#-- Employers contribution to EDLI  is 0.5%-->
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${dli?string("0.##")} </fo:block>
                        </fo:table-cell>
                       
                        <#assign admCharge = mpfs.wages * 0.0065> <#-- Admin Charges is 0.65% -->
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block> ${admCharge?string("0.##")}  </fo:block>
                        </fo:table-cell>
                        
                        <#assign admDli = mpfs.wages * 0.0001> <#-- Employers contribution to EDLI Admin Charges is 0.01% -->
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block> ${admDli?string("0.##")} </fo:block>
                        </fo:table-cell>
                        <#assign totCont = mpfs.employeeAmount + mpfs.pensionAmount + mpfs.epfDiff>
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${totCont?string("0.##")}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right="thin solid grey" padding="2pt" background-color="white" text-align="left">
                            <fo:block>&#160;</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <#-- toggle the row color -->
                    <#if rowColor == "white">
                        <#assign rowColor = "#eeeeee">
                    <#else>
                        <#assign rowColor = "white">
                    </#if>
                      
                    <#assign totalEPF = totalEPF + epf>
                	<#assign totalPension = totalPension + mpfs.pensionAmount>
                	<#assign totalDli = totalDli + dli>
                	<#assign totalAdmCharge = totalAdmCharge + admCharge>
                	<#assign totalDliAdmCharge = totalDliAdmCharge + admDli>
                	<#assign aggregate = aggregate + totCont>
                	       
                </#list>  
                <#-- Totals -->
                <fo:table-row>
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                            <fo:block>Total</fo:block>
                        </fo:table-cell>
                        
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}">
                            <fo:block>&#160; </fo:block>
                        </fo:table-cell>  
                                    
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${totalEPF?string("0.##")}</fo:block>
                        </fo:table-cell>
                        
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${totalPension?string("0.##")}</fo:block>
                        </fo:table-cell>
                      
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${totalDli?string("0.##")} </fo:block>
                        </fo:table-cell>
                       
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block> ${totalAdmCharge?string("0.##")}  </fo:block>
                        </fo:table-cell>
                      
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block> ${totalDliAdmCharge?string("0.##")} </fo:block>
                        </fo:table-cell>
                        
                        <fo:table-cell border="thin solid grey" padding="2pt" background-color="${rowColor}" text-align="left">
                            <fo:block>${aggregate?string("0.##")}</fo:block>
                        </fo:table-cell>
                        
                        <fo:table-cell border-right="thin solid grey" padding="2pt" background-color="white" text-align="left">
                            <fo:block>&#160;</fo:block>
                        </fo:table-cell>
                </fo:table-row>       
           </fo:table-body>
	</fo:table>
	<fo:block> &#160;</fo:block>
	<fo:block> &#160;</fo:block>
	<fo:block text-align="left" font-size="7pt">Reconciliation of Remittances</fo:block>
	<fo:block text-align="right" font-size="8pt">Signature of Employer (with office seal)</fo:block>
	<fo:block> &#160;</fo:block>
	<fo:block text-align="left" font-size="6pt"> NOTE:- (1) The names of all members, including those who had left service during the currency period, should be included in this statement. Where the Form 3-A in respect of such members had left
service, the fact should be stated against the members in the 'Remarks' column. </fo:block>
	<fo:block text-align="left" font-size="6pt">(2) In case of substantial variation in the wages/contribution of any members as compared to those shown in previous months statement, the reason should be explained adequately in the
'Remarks' column.</fo:block>
	<fo:block text-align="left" font-size="6pt">(3) In respect of those members who have not opted for Pension Fund their entire employers contribution @ 81/3% or 10% as the case may be shown under column No. 6.
	</fo:block>
</#escape>
	



