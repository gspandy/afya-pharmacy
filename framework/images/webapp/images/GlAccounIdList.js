function getGlAccountIds(accountType) {
	$.getJSON('getGlAccountIds?'+accountType, function(data) {
    	$('#glAccountId').empty();
        $.each(data.subCat, function(key, val) {
             	var parts = val.split(":");
                $('#glAccountId').append("<option value = " + parts[0] + " > " + parts[1] + " </option>");
      	});
        result = true;
        });
	return false;
}
function getSubTypeVat(accountType) {
		$.getJSON('getClassification?'+accountType, function(data) {
	    	$('#enumId').empty();
	        $.each(data.classNameValue, function(key, val) {
	             	var parts = val.split(":");
	                $('#enumId').append("<option value = " + parts[0] + " > " + parts[1] + " </option>");
	      	});
	        result = true;
	        });
		return false;
}

function getClassification(accountType) {
		getSubTypeVat(accountType);
		$('#enumTypeId').empty();
}

function getClassificationVat(accountType) {
	$.getJSON('getClassificationVat?'+accountType, function(data) {
    	$('#enumTypeId').empty();
        $.each(data.classNameValue, function(key, val) {
             	var parts = val.split(":");
                $('#enumTypeId').append("<option value = " + parts[0] + " > " + parts[1] + " </option>");
      	});
        return true;
        });
	return false;
}
