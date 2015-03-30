function getQuoteType(quoteTypeId,productStoreId) {
	if ("PURCHASE_QUOTE" == quoteTypeId) {
		jQuery("#productStoreId").attr("disabled", true);
	}
	 else
	{
		 jQuery("#productStoreId").attr("disabled", false);
	 }
}

function validateInitialAccountAndInitialLeadFields(accountPartyId, leadPartyId) {
	if ((accountPartyId == "" && leadPartyId == "") || (accountPartyId != "" && leadPartyId != "")) {
		alert('Please specify an account or a lead (not both).');
		return false;
	}
}
