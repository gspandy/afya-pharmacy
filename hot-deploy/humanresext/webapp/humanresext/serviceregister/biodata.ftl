<style>
	table{
		width:100%;
		border:1px solid #000;
	}

	td{
		font-size:1em;
	}

	.h1{
		font-size:1em;
	}

	.sectionText{
		font-size:1.5em;
		text-align:center;
		width:100%;
	}

</style>


<table width="100%" align="center">
	<tr border="1">
		<td width="90%" style="text-align:center"><h1> "Form 18" </h1></td>
		<td rowspan="3" style="text-align:right">
			<table width="100%">
			<tr><td>&nbsp;&nbsp;</td></tr>
			<tr><td>&nbsp;&nbsp;</td></tr>
			<tr><td>&nbsp;&nbsp;</td></tr>
			</table>
		</td>
	</tr>
	<tr>
		<td width="90%"  style="text-align:center"> (See Rules 119 and 397)  </td>
	</tr>
	<tr>
		<td width="90%"  style="text-align:center"> <h1> 1. BIO - DATA </h1> </td>
	</tr>
</table>

<table cellspacing="10px" cellpadding="15px">
	<tr>

		<td width="50%">1. (a) Name of the Government servant (in Block letters)</td>
		<td> ${person.salutation?if_exists}  ${person.firstName} ${person.lastName}</td>
	</tr>
	
	<tr>
		<td> (b) Residence </td>
		<td>	

			<#if primaryLocation??>
				<#assign address =primaryLocation>
				${address.attnName?if_exists}<br/>
				${address.address1?if_exists}<br/>
				${address.address2?if_exists}<br/>
				City: ${address.city?if_exists}<br/>
				State: ${address.stateProvinceGeoId}<br/>
				Country: ${address.countryGeoId} <br/>
				Postal Code: ${address.postalCode?if_exists}
			</#if>
		</td>
	</tr>

	<tr>
		<td> 2. (a) Father's Name (in BLOCK Letters) </td>
		<td>	${person.fatherName?if_exists} </td>
	</tr>
	<tr>
		<td> (b) Residence </td>
		<td>
			<#if parentLocation??>
				<#assign address = parentLocation>
				${address.attnName?if_exists}<br/>
				${address.address1?if_exists}<br/>
				${address.address2?if_exists}<br/>
				City: ${address.city?if_exists}<br/>
				State: ${address.stateProvinceGeoId}<br/>
				Country: ${address.countryGeoId} <br/>
				Postal Code: ${address.postalCode?if_exists}
			</#if>
		</td>
		
	</tr>

	<tr>
		<td> 3. (a) Husband's/Wife's Name (in BLOCK Letters) </td>
		<td> ${person.spouseName?if_exists}</td>
	</tr>
	<tr>
		<td> (b) Residence </td>
		<td>
			
			<#if spouseLocation??>
			<#assign address=spouseLocation>
				${address.attnName?if_exists}<br/>
				${address.address1?if_exists}<br/>
				${address.address2?if_exists}<br/>
				City: ${address.city?if_exists}<br/>
				State: ${address.stateProvinceGeoId}<br/>
				Country: ${address.countryGeoId} <br/>
				Postal Code: ${address.postalCode?if_exists}
			</#if>
		</td>
		
	</tr>
</table>

<br/><br/>
<div id="joint_photo" style="width:55%;height:250px;border:1px solid red;text-align:center">
	<span>
		Joint Photograph of Government Servant with Spouse
	</span>
</div>
<br/><br/>

* To be Attested by the Head of the Office before Pasting.<br/><br/>

Note:- Photograph should be renewed after 10 years of Service of Government servant.<br/>



<table width="100%" cellspacing="10px" cellpadding="15px">
	<tr>
		<td width="50%">4.  Nationality (if not a citizen of India number and date of eligibility certificate) </td>
		<td>${person.nationality?if_exists}</td>
	</tr>

	<tr>
		<td>5. Whether a member of Backward Tribe or Backward classes, Schedule Caste/Tribe if so indicate the caste/tribe or class (Original Cerificate
		issued by the competent authority in this respect shall be pasted in the service book.)</td>
		<td>
			${person.caste?if_exists}
		</td>
	</tr>

	<tr>
		<td>6. Date of birth by the Christian Era and if possible also in Saka Era(both in words and figures)</td>
		<td>
			${person.birthDate?if_exists}
		</td>
	</tr>


	<tr>
		<td>
		7 & 8. Eduational Qualifications
		</td>
		<td/>
	</tr>
	
	<tr>
		<td/>
		<td>
			Description |  Title  | Status  | From Date |  Thru Date 
		</td>
	</tr>
	<#assign counter=1>
	<#list partyQuals as qual>
	<tr>
		<td/>
		
		<td>
		   ${counter}.  ${qual.qualificationDesc?if_exists} ${qual.title?if_exists} ${qual.statusId?if_exists} ${qual.fromDate?if_exists?string("yyyy-mm-dd")} ${qual.thruDate?if_exists?string("yyyy-mm-dd")}
		</td>
		<#assign counter=counter+1>
	</tr>
	</#list>


	<tr>
		<td>9. Exact height by measurement (without shoes)</td>
		<td>${person.height?if_exists}</td>
	</tr>

	<tr>
		<td>10. Personal mark of identification</td>
		<td>${person.identificationMark?if_exists}</td>
	</tr>

	<tr>
		<td>11. Permanent Home Address</td>
		<td>
			<#if permanentLocation??>
				<#assign address = permanentLocation>
				${address.attnName?if_exists}<br/>
				${address.address1?if_exists}<br/>
				${address.address2?if_exists}<br/>
				City: ${address.city?if_exists}<br/>
				State: ${address.stateProvinceGeoId}<br/>
				Country: ${address.countryGeoId} <br/>
				Postal Code: ${address.postalCode?if_exists}
			</#if>
		</td>
	</tr>

	<tr>
		<td>12. Home Town or Village(for Leave Travel Concession) </td>
		<td>
			<#if ltcLocation??>
				<#assign address = ltcLocation>
				${address.attnName?if_exists}<br/>
				${address.address1?if_exists}<br/>
				${address.address2?if_exists}<br/>
				City: ${address.city?if_exists}<br/>
				State: ${address.stateProvinceGeoId}<br/>
				Country: ${address.countryGeoId} <br/>
				Postal Code: ${address.postalCode?if_exists}
			</#if>
		</td>
	</tr>

	<tr>
		<td>13. Signature or left hand Thumb </td>
		<td></td>
	</tr>

	<tr>
		<td>14. Signature and Designation  of attesting Officer (with date) </td>
		<td></td>
	</tr>
</table>