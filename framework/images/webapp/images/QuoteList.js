function getQuoteTypes(quoteTypeId) {
	if (accountType == "VAT") {
		getSubTypeVat("VAT");
	} else {
		getSubTypeVat(accountType);

		var AccOptions = null;
		var result = false;
		var optionList = [];
		new Ajax.Request('getQuoteTypes?' + quoteTypeId, {
			asynchronous : false,
			onSuccess : function(transport) {
				var data = transport.responseText.evalJSON(true);
				AccOptions = data.classNameValue;
				AccOptions.each(function(AccOption) {
					var parts = AccOption.split(":");
					optionList.push("<option value = " + parts[0] + " > "
							+ parts[1] + " </option>");
				});
				$('enumTypeId').update(optionList);
				result = true;

			},
			requestHeaders : {
				Accept : 'application/x-json'
			}
		});
	}
	return result;
}


function getProductStore(productStoreId) {

	var AccOptions = null;
	var result = false;
	var optionList = [];
	new Ajax.Request('getQuote?' + quoteTypeId, {
		asynchronous : false,
		onSuccess : function(transport) {
			var data = transport.responseText.evalJSON(true);
			AccOptions = data.classNameValue;
			AccOptions.each(function(AccOption) {
				var parts = AccOption.split(":");
				optionList.push("<option value = " + parts[0] + " > "
						+ parts[1] + " </option>");
			});
			$('enumTypeId').update(optionList);
			result = true;

		},
		requestHeaders : {
			Accept : 'application/x-json'
		}
	});
	return result;
}
