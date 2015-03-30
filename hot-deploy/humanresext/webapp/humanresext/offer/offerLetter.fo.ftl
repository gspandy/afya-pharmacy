<#escape x as x?xml>
<#assign total = 0>
<fo:block text-align="center" font-size="14pt" font-weight="bold" >${companyName?if_exists} </fo:block>
<fo:block text-align="center" font-size="8pt">${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} 
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}</fo:block>
<fo:block text-align="center" font-size="8pt"> Phone: ${phone.contactNumber?if_exists}</fo:block>
<fo:block text-align="center" font-size="8pt">${offer.offerDate?if_exists} </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 

<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> Dear Mr. ${candidateFullName?if_exists} , </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> Based on the discussions we are pleased to offer you a position as an.................... ${positionId?if_exists}. </fo:block>
<fo:block font-size="10pt"> A copy of the offer letter is attached, the hard copy of the letter will be couriered to you. </fo:block> 
<fo:block font-size="10pt"> Wishing you a successful and rewarding career at ${companyName?if_exists}. </fo:block> 
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="12pt"> Regards, </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="12pt">${signatory?if_exists}</fo:block>

<fo:block> &#160;  </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 

<fo:block> &#160;  </fo:block>

<fo:block break-after="page">&#160;</fo:block>

<fo:block text-align="center" font-size="18pt" font-weight="bold" >${companyName?if_exists} </fo:block>
<fo:block text-align="center" font-size="12pt">${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} 
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}</fo:block>
<fo:block text-align="center" font-size="12pt"> Phone: ${phone.contactNumber?if_exists}  </fo:block>
<fo:block text-align="center" font-size="12pt">${offer.offerDate?if_exists} </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 

<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> Dear Mr. ${candidateFullName?if_exists} , </fo:block>
<fo:block font-size="10pt"> Sub: Offer of Appointment ${offer.offerId?if_exists}</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">  1. With reference to the discussions held on ${offer.offerDate?if_exists}, we are pleased to offer you a position as an ${positionId?if_exists}. </fo:block>
<fo:block font-size="10pt">  2. You will be based in ${postalAddress.city?if_exists}. </fo:block> 
<fo:block font-size="10pt">  3. Your "Annual Compensation" is attached herewith as in Annexure-A. </fo:block> 
<fo:block font-size="10pt">  4. Your employment with us will be governed by terms and conditions referred in Annexure-B. </fo:block> 
<fo:block font-size="10pt">  5. You are required to join latest by ${offer.joiningDate?if_exists} and the offer stands withdrawn thereafter, unless the date is extended and communicated to you in writing. </fo:block> 
<fo:block font-size="10pt">  6. In case of further clarifications, please communicate with Mr. ${signatory?if_exists}  Phone: ${phone.contactNumber?if_exists} and quote the reference as above.</fo:block>

<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>

<fo:block font-size="10pt"> For: ${companyName?if_exists} </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">${signatory?if_exists}</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> Encl:   </fo:block>
<fo:block font-size="10pt"> 1. Salary Structure (Annexure A) </fo:block>
<fo:block font-size="10pt"> 2. Terms and Conditions of Employment (Annexure B) </fo:block>
<fo:block font-size="10pt"> 3. Check List (Annexure C) </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 

<fo:block> &#160;  </fo:block>

<fo:block break-after="page">&#160;</fo:block>
<fo:block text-align="center" font-size="18pt" font-weight="bold">${companyName?if_exists} </fo:block>
<fo:block text-align="center" font-size="12pt">${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} 
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}</fo:block>
<fo:block text-align="center" font-size="12pt"> Phone: ${phone.contactNumber?if_exists}  </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 
<fo:block text-align="center" font-size="14pt" font-weight="bold" >ANNEXURE-A</fo:block>
<fo:block text-align="center" font-size="12pt"> ESTIMATED CTC STATEMENT  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
<fo:table font-size="9pt" table-layout="fixed"  height="100%" font-weight="bold">
	<fo:table-column width="50%"/>
	<fo:table-column width="50%"/>
    <fo:table-body>
        <fo:table-row font-weight="normal">
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	NAME OF THE EMPLOYEE
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${candidateFullName?if_exists} </fo:block> <fo:block>
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal">
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	Designation
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${positionId?if_exists} </fo:block> <fo:block>
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal">
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	Date of Start of Employment
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${offer.joiningDate?if_exists} </fo:block> <fo:block>
            </fo:block></fo:table-cell>
		</fo:table-row>
		<fo:table-row font-weight="normal">
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	Employee Id
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${offer.partyId?if_exists} </fo:block> <fo:block>
            </fo:block></fo:table-cell>
		</fo:table-row>
		
		
	</fo:table-body>
</fo:table>	
<fo:block> &#160;  </fo:block>
<fo:block> &#160;  </fo:block>
 
<fo:table font-size="9pt" table-layout="fixed"  text-align="center" >
	<fo:table-column width="50%"/>
	<fo:table-column width="50%"/>
	<fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border="solid black"><fo:block>Salary Head</fo:block></fo:table-cell>
            <fo:table-cell border="solid blacky"><fo:block>Amount (p.a.)</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>	
	<#list ctc as comps>
		<fo:table-row font-weight="normal">
            <fo:table-cell border="thin solid grey" text-align="center"><fo:block >
            	${comps.get("hrName")?if_exists}
            </fo:block></fo:table-cell>
            <fo:table-cell border="thin solid grey" text-align="center"> <fo:block>
            	${comps.get("amount")?string("0.00")} </fo:block> <fo:block>
            </fo:block></fo:table-cell>
		</fo:table-row>
	</#list>
	</fo:table-body>
</fo:table>

<fo:block break-after="page"> &#160; </fo:block>

<fo:block text-align="center" font-size="18pt" font-weight="bold" >${companyName?if_exists} </fo:block>
<fo:block text-align="center" font-size="12pt">${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} 
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}</fo:block>
<fo:block text-align="center" font-size="12pt"> Phone: ${phone.contactNumber?if_exists}  </fo:block>
<fo:block text-align="center" font-size="12pt">${offer.offerDate?if_exists} </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 

<fo:block> &#160;  </fo:block>
<fo:block font-size="12pt" text-align="center" font-weight="bold"> ANNEXURE B </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">1. Employment Agreement </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">(a) Code of Conduct</fo:block> 
<fo:block font-size="10pt">During the period of your employment, you will work honestly, faithfully, diligently and efficiently for the growth of the Company. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">(b) Secrecy </fo:block>
<fo:block font-size="10pt">You are expected to maintain utmost secrecy in regard to the affairs of the Company and shall keep confidential any information, whether written or oral, which relates to internal controls, computer or data processing programs, algorithms, electronic data processing applications, routines, subroutines, techniques or systems, or information concerning the business or financial affairs and methods of operation or proposed methods of operation, accounts, transactions, proposed transactions, security procedures, trade secrets, know-how, or inventions of NthDimenzion or its Affiliate, or any client, agent, contractor or vendor. You shall not disclose the identities and other related information of any of its clients. Breach of this provision shall be treated as a gross violation of the terms herein and your services are liable to be terminated without notice.</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">(c) Conflict of Interest </fo:block>
<fo:block font-size="10pt">Your position with the Company calls for whole time employment and you will devote yourself exclusively to the business of the Company. You will not take up any other work for remuneration (part time or otherwise) or work on advisory capacity or be interested directly or indirectly (except as shareholder or debenture holder) in any other trade or business, during your employment with the Company, without written permission from the Company. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold"> 2. Assignments/Transfer/Deputation </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> Though you have been engaged for a specific position, the Company reserves the right to send you on training / deputation / secondment / transfer / assignments to sister companies, associate companies, clients locations or third parties whether in India or abroad. In such case, the terms and conditions of service applicable to the new assignment will govern you. You shall, only at the request of the Company, enter into a direct agreement or undertaking with any customer to whom you may be assigned/seconded/deputed accepting restrictions as such customer may reasonably require for the protection of its legitimate interests. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold"> 3. Termination of Employment </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> a) Either party can terminate this employment by serving a notice of 60 days on the other. However, if approved by the Company, an associate may surrender leave to his / her credit or pay salary (basic) in lieu of Notice period. Similarly, the Company may pay salary (basic) in lieu of Notice period, if required. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> b) In case of Associates who are governed by service agreements for serving a minimum stipulated period, the associate can exercise option under the clause 3(a) only on their completion of the stipulated service period agreed to and provided therein. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> c) Unauthorized absence or absence without permission from duty for a continuous period of 7 days, would make you lose your lien on employment. In such case your employment shall automatically come to an end without any notice of termination. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> d) You will be governed by the Company's laid down Code of Conduct and if there is any breach of the same or non-performance of contractual obligation or the terms and conditions laid down in this agreement, your service could be terminated without any notice notwithstanding any other terms and conditions stipulated herein. The Company further reserves the right to invoke other legal remedies as it deems fit to protect its legitimate interests </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold"> 4. Statement of Facts </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> It must be specifically understood that this offer is made based on your proficiency on Technical/Professional skills you have declared to possess as per the application, and on the ability to handle any assignment/job independently anywhere in India or overseas. In case, at a later date, any of your statements/particulars furnished are found to be false or misleading, or your performance is not up to the mark or falls short of the minimum standards set by the Company, the Company shall have the right to terminate your services forthwith without giving any notice, notwithstanding any other terms and conditions stipulated herein. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold"> 5. Restraints </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold"> Access to Information </fo:block>
<fo:block font-size="10pt"> Information is available on need to know basis for specified groups. The network file server is segregated to allow individual sectors for projects and units. Access to these are authorized through access privileges approved by unit Mentors or Project Mentors. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">Escalation/Exception Reporting </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> a) A set of areas/jobs to be carried out by each function/department will be decided. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> b) For each area/job - a suitable policy will be formulated/evolved. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> c) For every policy - standards of measurement will be laid down. </fo:block> 
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> d) Goals for year/quarter/month will be periodically reviewed. </fo:block>
<fo:block> &#160;  </fo:block> 
<fo:block font-size="10pt"> e) Deviation if any with regard to policies or standards will be monitored and brought up for discussion in review meetings if such deviation could wait till review meeting. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt"> f) Alternatively, if such deviation will pose a threat and if it is not corrected it will be escalated immediately for corrective action jointly agreed upon and it will be implemented as per schedule. If there is any deviation/modification/amendment it will be further escalated to next level. Authorization Only those authorized by a specific power of attorney may sign legal documents, representing the Company.</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">Authorization </fo:block>
<fo:block font-size="10pt"> Only those authorized by a specific power of attorney may sign legal documents, representing the Company </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">Smoking </fo:block>
<fo:block font-size="10pt">We owe and assure a smoke free environment for our Associates. Barring some areas, the entire office premises including conference rooms, lobbies, is declared as No-Smoking Zone</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">Passwords</fo:block>
<fo:block font-size="10pt">Access to our network, development environment and emails is through individuals’ password. For security reasons it is essential to maintain confidentiality of the same. If the password is forgotten, the Networking &amp; Communication Group is to be contacted to reset and allow you to use a new password. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">Unauthorized Software</fo:block>
<fo:block font-size="10pt">You shall not install, download, copy, duplicate any unauthorized or unlicensed software, programs, games, attachments on to your computer systems. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">7. Overseas Service Agreement</fo:block> 
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">As the Company will be spending substantial amount of time and money for your deputation /secondment abroad, you will be required to sign a deputation agreement with the Company and also execute a Surety Bond on such terms, as the Company may deem appropriate. (This agreement will consist, inter alia, of issues like (i) your commitment to complete the project (ii) your returning to India after completion of the project and serving the Company for a stipulated period). </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">8. Intellectual Property Rights</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">All intellectual property rights, including but not limited to, Patents, Copyrights, Designs, Trademarks and Semiconductor chips developed by you during Office time or using the Company infrastructure, or while performing or discharging official duties shall be the sole and exclusive property of the Company and the same shall be deemed to be work made for hire. You shall execute/sign such documents for the purpose of assigning such Intellectual property, as and when required by the Company. 
The Company reserves the right to proceed legally against you and recover damages, where any such intellectual property is sought to be protected by you independently of the Company. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">9. Jurisdiction </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">Even though the Company may depute you overseas for on-site work or to any other location in India, the jurisdiction concerning any dispute arising out of your employment will be the courts in Bangalore, Karnataka only. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">10. Retirement </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">You shall automatically retire from the services of the Company at the age of 58 years and for the purpose of determining this, the age recorded with the Company shall be considered as final and conclusive. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt" font-weight="bold">11. General </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">The above terms and conditions including those in Annexure - A (Salary break up) are based on Company policies, procedures and other rules currently applicable in India as well as Overseas and are subject to amendments and adjustments from time to time. In all services matters, including those not specifically covered here such as Traveling, Leave, Retirement, Code of Conduct, etc. you will be governed by the rules of the Company as shall be in force from time to time. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">12. You shall be present in the office during normal working hours or during hours expressly designated for you in writing. </fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">13. This offer is purely based on the information / documents provided by you and by accepting the offer, you specifically authorize the Company or any external agency through ${companyName?if_exists} to verify your educational, employment antecedents, your conduct and any other background checks prior to your joining the Company or thereafter. You shall extend your co-operation (if asked for) during such verification without any protest or demur.</fo:block>
<fo:block> &#160;  </fo:block>
<fo:block font-size="10pt">This is to certify that I have gone through wholly and understood all the terms and conditions mentioned in Annexure-B and I hereby accept and agree to abide by them: </fo:block>
<fo:block>&#160;</fo:block>
<fo:block>&#160;</fo:block>
<fo:block font-size="10pt" font-weight="bold"> Name in Full: </fo:block> 
<fo:block>&#160;</fo:block>
<fo:block font-size="10pt" font-weight="bold"> Signature: </fo:block>
<fo:block>&#160;</fo:block>
<fo:block>&#160;</fo:block>
<fo:block font-size="10pt" font-weight="bold"> Address: </fo:block>
<fo:block>&#160;</fo:block>
<fo:block>&#160;</fo:block>
<fo:block>&#160;</fo:block>
<fo:block font-size="10pt" font-weight="bold"> Date: </fo:block>
<fo:block font-size="10pt" font-weight="bold"> Place: </fo:block>

<fo:block break-after="page">&#160;</fo:block>

<fo:block text-align="center" font-size="18pt" font-weight="bold" >${companyName?if_exists} </fo:block>
<fo:block text-align="center" font-size="12pt">${postalAddress.address1?if_exists} ${postalAddress.address2?if_exists} 
					${postalAddress.city?if_exists} ${postalAddress.postalCode?if_exists}</fo:block>
<fo:block text-align="center" font-size="12pt"> Phone: ${phone.contactNumber?if_exists}  </fo:block>
<fo:block text-align="center" font-size="12pt">${offer.offerDate?if_exists} </fo:block>

<fo:block><fo:leader leader-length.minimum="400pt" leader-length.optimum="540pt"
    leader-length.maximum="600pt" leader-pattern="rule" rule-style="solid">
</fo:leader></fo:block> 
<fo:block font-size="12pt" text-align="center" font-weight="bold"> ANNEXURE C </fo:block>
<fo:block font-size="10pt" font-weight="bold">At the time of joining, you are requested to bring the following documents in original, along with one copy of each. </fo:block>
<fo:block>&#160;</fo:block>
<fo:block font-size="10pt">o Xth Certificate &amp; mark sheets</fo:block>
<fo:block font-size="10pt">o XIIth Certificate &amp; mark sheets</fo:block>
<fo:block font-size="10pt">o Degree Certificate &amp; Semester/year-wise Mark sheets</fo:block>
<fo:block font-size="10pt">o Master's Certificate &amp; Semester/year-wise Mark sheets</fo:block>
<fo:block font-size="10pt">o Diploma/PG Diploma Certificate &amp; Transcripts</fo:block>
<fo:block font-size="10pt">o Any other Certificates with supporting documents - if any</fo:block>
<fo:block font-size="10pt">o Your latest Salary Slip / Salary Certificate </fo:block>
<fo:block font-size="10pt">o Your Relieving Letter from your present organization </fo:block>
<fo:block font-size="10pt">o Service Certificate / proof of Employment from the present and all previous Employers </fo:block>
<fo:block font-size="10pt">o Form 16 or Taxable Income Statement duly certified by previous employer (Statement showing deductions &amp; Taxable Income with break-up) </fo:block>
<fo:block font-size="10pt">o PAN Card</fo:block> 
<fo:block>&#160;</fo:block>
<fo:block>&#160;</fo:block>   
<fo:block font-size="10pt" font-weight="bold">Your offer has been made based on the information furnished by you. However, if there is a discrepancy in the copies of the documents/certificates given by you as a proof in support of the above, the Company reserves the right to revoke the offer at any time during your service.</fo:block>
</#escape>