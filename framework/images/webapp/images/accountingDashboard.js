jQuery(document).ready( function() {
    jQuery('#organizationPartyId').change( function() {
    	getOrganization(organizationPartyId);
    });
});


function getOrganization(organizationPartyId) {
    var organizationPartyId = organizationPartyId.value;
    document.location.href = "/accounting/control/dashboard?organizationPartyId="+organizationPartyId;
        /*	 $.getJSON('/accounting/control/dashboard?organizationPartyId='+organizationPartyId , function(data) {
        });*/
}

