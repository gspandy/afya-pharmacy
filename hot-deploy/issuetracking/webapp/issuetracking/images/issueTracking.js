function getSubCategoryOptions(categoryId) {
	var result = false;
    var subCategoryOptions = null;
    var optionList = [];
    $.getJSON('getSubCategoryOptions?'+categoryId, function(data) {
    			
    	$('#issueSubCategoryId').empty();
    	
        $.each(data.subCat, function(key, val) {
             	var parts = val.split(":");
                $('#issueSubCategoryId').append("<option value = " + parts[0] + " > " + parts[1] + " </option>");
      	});
        result = true;
        
        });
    return result;
}
