/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

jQuery(document).ready( function() {
    jQuery('#returnHeaderTypeId').change( function() {
        changeStatusCorrespondingToHeaderType();
        changeVoucherCorrespondingToHeaderType();
    });
});


function changeStatusCorrespondingToHeaderType() {
	var result = false;
    var subCategoryOptions = null;
    var listOptions = [];
	    $.getJSON('/ordermgr/control/getStatusItemsForReturn?'+jQuery('#returnHeaderTypeId').val(), function(data) {
	    $('#statusId').empty();
		    	$.each(data.statusItemList, function(key, val) {
		     	var parts = val.split(":");
		        $('#statusId').append("<option value = " + parts[0] + " > " + parts[1] + " </option>");
		  	});
	    result = true;
	        
	    });
    return result;
}

function changeVoucherCorrespondingToHeaderType() {
	var result = false;
    var subCategoryOptions = null;
    var listOptions = [];
    $('#voucherType').empty();
	    $.getJSON('/ordermgr/control/getVoucherType?'+jQuery('#returnHeaderTypeId').val(), function(data) {
		    $.each(data.vaucherTypeList, function(key, val) {
		     	var parts = val.split(":");
		        $('#voucherType').append("<option value = " + parts[0] + " > " + parts[1] + " </option>");
		  	});
	    result = true;
	    
	    if("VENDOR_RETURN" ==  jQuery('#returnHeaderTypeId').val()){
	    	$('#voucherType').append("<option value = 'Debit Note' >Debit Note</option>");
	    }else{
	    	$('#voucherType').append("<option value = 'Credit Note' >Credit Note</option>");
	    }
	    
	    });
    return result;
}
    