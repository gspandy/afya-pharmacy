<form action="SaveTransformationRequest" id="createTransformation" name="createTransformation" method="POST">
	<input type="hidden" value="${parameters.facilityId}" name="facilityId" id="facilityId">
	<table width="60%">
		<tr>
			<td class="label">Status</td>
			<td>REQUESTED </td>
			 <td class="label"></td>
			 <td></td>
		</tr>	
		<tr>
			<td class="label">From Product</td>
			<td>
				<@htmlTemplate.lookupField name="fromProductId" id="fromProductId" formName="createTransformation" fieldFormName="LookupProductByFacility" event="onblur" action="javascript: loadQoh(fromProductId, facilityId, fromQoh)" className="required"/><span><font color="red">*</font></span>
				<#--  <input type="text" name="fromProductId" id="fromProductId" onblur="javascript: loadQoh('fromProductId', 'fromQoh');">
				<a href="javascript:call_fieldlookup2(document.createTransformation.fromProductId, 'LookupProduct');" onblur="javascript: loadQoh('fromProductId', 'fromQoh');">
					<img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="${uiLabelMap.CommonClickHereForFieldLookup}"/>
				</a>  -->
			 </td>
			 <td class="label">Quantity on Hand</td>
			 <td><input type="text" name="fromQoh" id="fromQoh" readonly="true"></td>
		</tr>	
		<tr>
			<td class="label">To Product</td>
			<td>
				<@htmlTemplate.lookupField name="toProductId" id="toProductId" formName="createTransformation" fieldFormName="LookupProductByFacility" event="onblur" action="javascript: loadQoh(toProductId, facilityId, toQoh)" className="required"/><span><font color="red">*</font></span>
				<#--  <input type="text" name="toProductId" id="toProductId" onblur="javascript: loadQoh('toProductId', 'toQoh')">
				<a href="javascript:call_fieldlookup2(document.createTransformation.toProductId, 'LookupProduct');" onblur="javascript: loadQoh('toProductId', 'toQoh');">
					<img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="${uiLabelMap.CommonClickHereForFieldLookup}"/>
				</a>  -->
			 </td>
			 <td class="label">Quantity on Hand</td>
			 <td><input type="text" name="toQoh" id="toQoh" readonly="true"></td>
		</tr>	
		<tr>
			<td class="label">Make Converted Quantity</td>
			<td>
				<input type="text" name="quantityRequired" id="quantityRequired" onblur="javascript: loadConversionFactor(fromProductId, toProductId, conversionFactor)" class="required"/><span><font color="red">*</font></span>
			 </td>
			 <td class="label">Conversion Factor</td>
			 <td><input type="text" name="conversionFactor" id="conversionFactor" readonly="true"></td>
		</tr>
<#--	<tr>
			<td class="label">From Location</td>
			<td>
				<input type="text" name="fromLocationSeqId" id="fromLocationSeqId">
				<a href="javascript:call_fieldlookup2(document.createTransformation.fromLocationSeqId, 'LookupFacilityLocation');">
					<img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="${uiLabelMap.CommonClickHereForFieldLookup}"/>
				</a>
			 </td>
		</tr>
		<tr>
			<td class="label">To Location</td>
			<td>
				<input type="text" name="toLocationSeqId" id="toLocationSeqId">
				<a href="javascript:call_fieldlookup2(document.createTransformation.toLocationSeqId, 'LookupFacilityLocation');">
					<img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="${uiLabelMap.CommonClickHereForFieldLookup}"/>
				</a>
			 </td>
		</tr>  		-->
		<tr>
			<td class="label">Scheduled On</td>
			<td>
				<@htmlTemplate.renderDateTimeField name="scheduledOn" value="${value!''}" className="" alert="" 
                title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="scheduledOn" dateType="date" shortDateInput=false 
                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="createTransformation"/>
 
			 </td>
		</tr>
		<tr>
			<td class="label">Estimated Completion</td>
			<td>
				<@htmlTemplate.renderDateTimeField name="estimatedCompletion" value="${value!''}" className="" alert="" 
                title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="estimatedCompletion" dateType="date" shortDateInput=false 
                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="createTransformation"/>
				
			 </td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Transform" class="btn btn-success"></td>
		</tr>
	</table>
	<script>
		var form = document.createTransformation;
		jQuery(form).validate();
	</script>
</form>