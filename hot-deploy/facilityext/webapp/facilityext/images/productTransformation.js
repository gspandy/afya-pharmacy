function loadQoh(productIdElem, facilityIdElem, elementId) {
    var productId = $(productIdElem).value;
    var facilityId = $(facilityIdElem).value;
    new Ajax.Request('LoadQoh?productId='+productId+'&facilityId='+facilityId, {
        asynchronous: false,
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            $(elementId).value = data.qoh; 
        }, requestHeaders: {Accept: 'application/x-json'}
    });
}

function loadQohByProductId(productId, elementId) {
    new Ajax.Request('LoadQoh?'+productId, {
        asynchronous: false,
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            $(elementId).value = data.qoh; 
        }, requestHeaders: {Accept: 'application/x-json'}
    });
}

function loadConversionFactor(fromProductIdElem, toProductIdElem, elementId) {
	var fromProductId = $(fromProductIdElem).value;
	var toProductId = $(toProductIdElem).value;
	if(fromProductId == null || toProductId == null)
		return;
    new Ajax.Request('LoadConversionFactor?fromProductId='+fromProductId+'&toProductId='+toProductId, {
        asynchronous: false,
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            $(elementId).value = data.conversionFactor; 
        }, requestHeaders: {Accept: 'application/x-json'}
    });
}

function populateBundleData(productBundleId, quantityRequired, status, makeSku, tagCode ){
	try{
		window.opener.document.getElementById('quantityRequired').value = quantityRequired;
		window.opener.document.getElementById('productBundleId').value = productBundleId;
		window.opener.document.getElementById('status').value = status;
		window.opener.document.getElementById('makeSku').value = makeSku;
		loadQohByProductId(makeSku, window.opener.document.getElementById('qoh'));
	}catch (e) {
		alert(e);
	}
	set_value(tagCode);
}