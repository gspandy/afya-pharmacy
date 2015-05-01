<p>&nbsp;</p>
<script language="javascript">
function validateQtyDone()
{
	var qtyBundle= parseInt(document.TransformationTxn.quantityDone.value);
	var qtyBundleRaw = document.getElementById("pendingQty").value;
	var bundle = true;
	var qtyPending = 0;

	if(qtyBundleRaw.indexOf(',') != -1)
	{
		qtyBundleRaw = qtyBundleRaw.replace(/,/g,'');
		qtyPending= parseInt(qtyBundleRaw);
	}
	else 
	{
		qtyPending = parseInt(qtyBundleRaw);
	}

	if(isNaN(qtyBundle) || qtyBundle <= 0)
	{
		alert('Invalid Value Specified. Only positive numeric value is allowed.');
		bundle = false;
		document.TransformationTxn.quantityDone.focus();
	}

	if(qtyBundle > qtyPending)
	{
		alert('The Quantity to be Done cannot be greater than the pending quantity.');
		bundle= false;
		document.TransformationTxn.quantityDone.focus();
	}

	return bundle;
	
}
</script>
<input type="hidden" name="pendingQty" id ="pendingQty" value="${productTransfer.pendingQuantity}"/>
<table>
	<tr>
		<td valign="top">
			<table>
				<tr>
					<td class="label" style="width: 150px;">Product Transfer Id</td>
					<td style="width: 150px;">${productTransfer.productTransferId}</td>
				</tr>	
				<tr>
					<td class="label" style="width: 150px;">Status</td>
					<td style="width: 150px;">${productTransfer.status}</td>
				</tr>	
				<tr>
					<td class="label">From Product</td>
					<td>${productTransfer.fromProductId}</td>
				</tr>	
				<tr>
					<td class="label">To Product</td>
					<td>${productTransfer.toProductId}</td>
				</tr>	
				
				<tr>
					<td class="label">Pending Quantity</td>
					<td>${productTransfer.pendingQuantity}</td>
				</tr>
				<tr>
					<td class="label">Scheduled On</td>
					<#if productTransfer.scheduledOn?exists>
					<td>${productTransfer.scheduledOn}</td>
					</#if>
				</tr>
				<tr>
					<td class="label">Estimated Completion</td>
					<#if productTransfer.estimatedCompletion?exists>
					<td>${productTransfer.estimatedCompletion}</td>
					</#if>
				</tr>
				<tr>
					<td class="label">Actual Completion</td>
					<#if productTransfer.actualCompletion?exists>
					<td>${productTransfer.actualCompletion}</td>
					</#if>
				</tr>
			</table>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td valign="top">
			<h3>Source Locations</h3><br/>
			<#list fromLocations as fromLocation>
				${fromLocation.inventoryItemId} <span><font color="red">*</font></span><br/>
			</#list>
			
			<form method="post" action="AddProductTransferLocation" name="addSrcLocation">
				<input type="hidden" name="productTransferId" value=${productTransfer.productTransferId}>
				<input type="hidden" name="type" value="SRC">
				<input type="hidden" value="${parameters.facilityId}" name="facilityId">
				<@htmlTemplate.lookupField name="inventoryItemId" id="inventoryItemId" formName="addSrcLocation" fieldFormName="LookupInventoryItem?productId=${productTransfer.fromProductId}" className="required"/>
				<#--  <input type="text" name="inventoryItemId" id="inventoryItemId">
				<a href="javascript:call_fieldlookup2(document.addSrcLocation.inventoryItemId, 'LookupInventoryItem?productId=${productTransfer.fromProductId}');">
					<img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="${uiLabelMap.CommonClickHereForFieldLookup}"/>
				</a>  -->
				&nbsp;&nbsp;
				<#if productTransfer.status != 'COMPLETED'>
					<input type="submit" value="Add" class="btn btn-success"/>
				</#if>
			</form>
			<script>
				var form = document.addSrcLocation;
				jQuery(form).validate();
			</script>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td valign="top">
			<h3>Destination Locations</h3><br/>
			<#list toLocations as toLocation>
				${toLocation.locationId} <span><font color="red">*</font></span><br/>
			</#list>
			
			<form method="post" action="AddProductTransferLocation" name="addDestLocation">
				<input type="hidden" name="productTransferId" value=${productTransfer.productTransferId}>
				<input type="hidden" name="type" value="DEST">
				<input type="hidden" value="${parameters.facilityId}" name="facilityId">
				<@htmlTemplate.lookupField name="locationId" id="locationId" formName="addDestLocation" fieldFormName="LookupFacilityLocation" className="required"/>
				<#--  <input type="text" name="locationId" id="locationId">
				<a href="javascript:call_fieldlookup2(document.addDestLocation.locationId, 'LookupFacilityLocation');">
					<img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="${uiLabelMap.CommonClickHereForFieldLookup}"/>
				</a>  -->
				&nbsp;&nbsp;
				<#if productTransfer.status != 'COMPLETED'>
					<input type="submit" value="Add" class="btn btn-success">
				</#if>
			</form>
			<script>
				var form = document.addDestLocation;
				jQuery(form).validate();
			</script>
		</td>
	</tr>
</table>
<br/><br/>