/**
 * Created by Naren on 27-07-2015.
*/

$(document).ready(function () {

    $.getJSON('http://localhost:7878/afya-portal/anon/getAllDoctors', function (data) {
        $.each(data, function (attr, value) {
        	var referralId = value['providerId'];
        	var firstName = value['firstName'];
        	var lastName = value['lastName'];
        	var referralName = firstName + " " + lastName;
        	var clinicId = value['clinicId'];
            var clinicName = value['clinicName'];
            var comma = ",";
            var referral = referralId+comma+referralName+comma+clinicId+comma+clinicName;
            var option = $('<option></option>').val(referral).text(referralName);
            $('#referral').append(option);
        });
    });

});