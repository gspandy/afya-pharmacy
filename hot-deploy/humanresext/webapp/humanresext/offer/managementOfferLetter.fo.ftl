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
<fo:block text-align="left" font-size="10pt" >Ref:<fo:inline font-weight="bold"> OFFER OF FIXED&#45;TERM EMPLOYMENT CONTRACT </fo:inline></fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">Following your application and the subsequent interview you attended for employment, management is pleased to 
offer you a two (2) year contract as  ${employeePositionType?if_exists}   under ${departmentName?if_exists} effective ${offer.offerDate?if_exists} at our 
Main Plant situated at ${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} under the following conditions;
</fo:block>
<fo:block> &#160;  </fo:block>
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

<fo:block text-align="justify" font-size="10pt">This offer is valid for five (5) days only in which your response should be received in writing.
When you take up this appointment, you shall be on probation for three (3) months. The probation may be extended to a total of not more than six (6) months.
Your confirmation shall be subject to satisfactory performance of duty and satisfactory silicosis examinations which you shall undertake prior to reporting 
for duty and furnish management with certificate of fitness on commencement of work. The cost of medicals will be paid by the company. Your confirmation of
appointment shall be communicated to you in writing. During probation, any party can terminate services giving twenty four (24) hours notice, equivalent 
pay in lie of notice.
</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">You shall be entitled to access Medical attention under the company Scheme which shall be provided to your 
spouse, and four members of your family at Ndola Central Hospital for adults and Arthur Davison Children`s Hospital for Children in accordance with the 
terms and conditions of scheme operations. The entitlement shall be limited to a unit of 6 registered family members
</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">The appointment shall be effective upon acceptance of this offer and its terms and conditions, and when you 
take up the appointment.You shall be expected to work 8 hours per day for six days in a week as normal shift, in addition to normal schedule you shall be 
expected to respond as duty calls and requested upon.
</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">You shall report to the ${departmentName?if_exists} Manager for your daily assignments who shall be your immediate 
supervisor. You shall be provided with a copy of your job description.
</fo:block>
<fo:block text-align="justify" font-size="10pt">Upon taking up the appointment, you shall report to the undersigned for briefing and placement, and signing of 
the employment contract which stipulates terms and conditions of your service not stated in this letter. Your position is a middle management position and as such; you are not expected to join the Union.
</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">Your conduct and actions at work shall be regulated by the company Disciplinary code of conduct.
</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="justify" font-size="10pt">We welcome you as a new member of staff and wish you success in you new appointment.
</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="10pt">Yours faithfully,</fo:block>
<fo:block text-align="left" font-size="10pt" font-weight="bold">FOR ZAMBEZI PORTLAND CEMENT LTD</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="10pt">${hrFullName}</fo:block>
<fo:block text-align="left" font-size="10pt" font-weight="bold" text-decoration="underline">HUMAN RESOURCE MANAGER  &amp;  ADMIN</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block text-align="left" font-size="10pt">CC: CEO</fo:block>
<fo:block text-align="left" font-size="10pt">CC: General Manager</fo:block>
<fo:block text-align="left" font-size="10pt">CC: Instrumentation Manager</fo:block>
<fo:block text-align="left" font-size="10pt">CC: Account</fo:block>
<fo:block text-align="left" font-size="10pt">CC: Personal File</fo:block>

</#escape>