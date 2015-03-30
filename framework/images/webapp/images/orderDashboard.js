jQuery(document).ready( function() {
    jQuery('#organizationPartyId').change( function() {
    	getOrganization(organizationPartyId);
    });
});


function getOrganization(organizationPartyId) {
    var organizationPartyId = organizationPartyId.value;
    document.location.href = "/ordermgr/control/main?organizationPartyId="+organizationPartyId;
        /*	 $.getJSON('/ordermgr/control/main?organizationPartyId='+organizationPartyId , function(data) {
        });*/
}

