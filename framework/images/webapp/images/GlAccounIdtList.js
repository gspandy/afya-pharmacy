function getGlAccountIds(accountType) {
	var glAccOptions = null;
	var result = false;
	var optionList = [];
	 new Ajax.Request('getGlAccountIds?'+accountType, {
	        asynchronous: false,
	        onSuccess: function(transport) {
	            var data = transport.responseText.evalJSON(true);
	            glAccOptions = data.glAccValue;
	            glAccOptions.each( function(glAccOption) {
               	var parts = glAccOption.split(":");
                optionList.push("<option value = " + parts[1] + " > " + parts[0] + " </option>");
                 });
                $('glAccountId').update(optionList);
                result = true;
	                   
	        }, requestHeaders: {Accept: 'application/x-json'}
	    });
	 return result;
}
