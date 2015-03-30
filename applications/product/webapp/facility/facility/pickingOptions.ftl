<script>
function submitToAuto(){
	document.getElementById("verifyOptionForm").action='<@ofbizUrl>PicklistOptions</@ofbizUrl>';
	document.getElementById("verifyOptionForm").submit();
}

function submitToManual(){
	document.getElementById("verifyOptionForm").action='<@ofbizUrl>FindOrdersFromShipment</@ofbizUrl>';
	document.getElementById("verifyOptionForm").submit();
}
</script>

<#assign mode="${requestParameters.mode}"/>
<form action="<@ofbizUrl>FindOrdersFromShipment</@ofbizUrl>" id="verifyOptionForm" name="verifyOptionForm">
    <input type="hidden" name="facilityId" value="${facilityId}"/>
	<input type="radio" name="mode" value="Auto" <#if mode="Auto">checked</#if> onClick="submitToAuto();"/>Auto Shipment
	<input type="radio" name="mode" value="Manual" <#if mode="Manual">checked</#if> onClick="submitToManual();"/>Manual Shipment
</form>
