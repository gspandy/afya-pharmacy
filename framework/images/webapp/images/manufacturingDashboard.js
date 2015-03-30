jQuery(document).ready( function() {
    jQuery('#organizationPartyId').change( function() {
    	getOrganization(organizationPartyId);
    });
});


function getOrganization(organizationPartyId) {
    var organizationPartyId = organizationPartyId.value;
    document.location.href = "/manufacturing/control/main?organizationPartyId="+organizationPartyId;
        /*	 $.getJSON('/manufacturing/control/main?organizationPartyId='+organizationPartyId , function(data) {
        });*/
}

