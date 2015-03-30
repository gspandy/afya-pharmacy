<script>
function submitToDefault(){
	document.getElementById("verifyOptionForm").action='<@ofbizUrl>issuanceItems</@ofbizUrl>';
	document.getElementById("verifyOptionForm").submit();
}
function submitToSampleCustomer(){
	document.getElementById("verifyOptionForm").action='<@ofbizUrl>FindSampleToCustomer</@ofbizUrl>';
	document.getElementById("verifyOptionForm").submit();
}

function submitToIssueSubContract(){
	document.getElementById("verifyOptionForm").action='<@ofbizUrl>FindIssueToSubContract</@ofbizUrl>';
	document.getElementById("verifyOptionForm").submit();
}
</script>

<#assign issueMode="${issueMode?if_exists}"/>
<form action="<@ofbizUrl>issuanceItems</@ofbizUrl>" id="verifyOptionForm" name="verifyOptionForm">
    <input type="hidden" name="facilityId" value="${parameters.facilityId}"/>
    <input type="radio" name="issueMode" value="Default" <#if issueMode="Default">checked</#if> onClick="submitToDefault();"/>Default
	<input type="radio" name="issueMode" value="SC" <#if issueMode="SC">checked</#if> onClick="submitToSampleCustomer();"/>Sample to Customer
	<input type="radio" name="issueMode" value="ISC" <#if issueMode="ISC">checked</#if> onClick="submitToIssueSubContract();"/>Issue to Sub Contract
</form>
