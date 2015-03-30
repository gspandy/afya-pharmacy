<#escape x as x?xml>
<#assign total = 0>
<#assign totalSal = Static["java.math.BigDecimal"].ZERO>
<fo:block text-align="left" font-size="10pt">${offer.offerDate?if_exists} </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="10pt">${candidateFullName?if_exists} </fo:block>
<#if empPostalAddress?has_content>
<fo:block text-align="left" font-size="10pt">${empPostalAddress.address1?if_exists}, ${empPostalAddress.address2?if_exists} </fo:block>
<fo:block text-align="left" font-size="10pt">${empPostalAddress.city?if_exists}, ${empPostalAddress.postalCode?if_exists} </fo:block>
</#if>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="10pt">Dear Mr. ${candidateFullName?if_exists} </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="10pt">Ref: <fo:inline font-weight="bold">OFFER OF EMPLOYMENT AS ${employeePositionType?if_exists} </fo:inline></fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">Management is pleased to inform you that you have been offered employment on permanent and pensionable 
conditions of employment as ${employeePositionType?if_exists}  in the ${departmentName?if_exists} at our Main Plant situated along ${postalAddress.address1?if_exists}
 ${postalAddress.address2?if_exists} under the following terms and conditions;
</fo:block>
<fo:block> &#160; </fo:block>
<fo:block text-align="left" font-size="10pt" margin-left="25pt">	i.&#32;&#32;&#32;&#32; Your entry Salary Scale is :<fo:inline font-weight="bold"> ${employeeGrade?if_exists} 
</fo:inline></fo:block>
<fo:block> &#160; </fo:block>
<fo:block text-align="left" font-size="10pt" margin-left="25pt">	ii.&#32;&#32;&#32;&#32;	Salary Details: 
</fo:block>
<fo:block> &#160; </fo:block>


<fo:table font-size="9pt" table-layout="fixed"  text-align="center" >
	<fo:table-column width="50%"/>
	<fo:table-column width="50%"/>
	
	<fo:table-body>	
	<#list ctc as comps>
		<fo:table-row font-weight="normal">
            <fo:table-cell  text-align="left" margin-left="35pt"><fo:block >
            	${comps.get("hrName")?if_exists}
            </fo:block></fo:table-cell>
            <fo:table-cell  text-align="left" margin-left="35pt"> <fo:block>
            	${comps.get("amount")?string("0.00")} </fo:block> <fo:block>
            </fo:block></fo:table-cell>
		</fo:table-row>
		<#assign totalSal = totalSal.add(comps.getBigDecimal("amount"))>
	</#list>
<fo:table-row font-weight="bold">
            <fo:table-cell  text-align="left" margin-left="35pt"><fo:block >
            	Total:
            </fo:block></fo:table-cell>
            <fo:table-cell  text-align="left" margin-left="35pt"> <fo:block>
            	${totalSal?string("0.00")}  </fo:block> <fo:block>
            </fo:block></fo:table-cell>
		</fo:table-row>
		
	</fo:table-body>
</fo:table>
<fo:block> &#160; </fo:block>
<fo:block text-align="justify" font-size="10pt" margin-left="25pt">iii.&#32;&#32;&#32;&#32;You will be on probation for three (3) months which may be extended for a period not exceeding six (6) 
months. Your confirmation will be communicated to you in writing and will be subject to your satisfactory performance of duty and silicosis test results 
for silicosis examinations which you must undergo prior to reporting for duty and furnish Management with certificate of fitness on commencement of work. 
The cost of medicals will be paid by the company.
 </fo:block>
 <fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">You are free to join a Trade Union of your choice after confirmation of appointment. The company 
currently has two (2) registered Unions namely Mine Workers Union of Zambia (MUZ) and United Mine Workers of Zambia (UMUZ). You are also free not to 
join any of the unions.
 </fo:block>
 <fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">If you join a union, you will be entitled to conditions of service contained in the Collective Agreement 
signed between the Union and Management. You will be expected to pay all statutory obligations as required by law.
 </fo:block>
 <fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt"> This offer is with effect from ${offer.offerDate?if_exists} and is valid for five (5) week days only. If you accept 
this offer and its terms and conditions, you are expected to report to the undersigned for briefing and placement.
We welcome you as a new member to this company and wish you success in your appointment.
</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>

<fo:block text-align="justify" font-size="10pt"> Yours faithfully,</fo:block>
<fo:block text-align="justify" font-size="10pt" font-weight="bold">ZAMBEZI PORTLAND CEMENT LTD </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">${hrFullName}</fo:block>
<fo:block text-align="justify" font-size="10pt" font-weight="bold" text-decoration="underline">HUMAN RESOURCES  &amp;  ADMIN MANAGER </fo:block>
<fo:block text-align="justify" font-size="10pt">Cc: CEO, GM, Mobile Manager, Accounts, Personal File   </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">In acceptance..............................................Signature..............................................Date.............................................. </fo:block>
</#escape>