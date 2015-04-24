/**
 * Created by pradyumna on 23-04-2015.
 */

/*

 {"civilId":"318111300014","firstName":"John","middleName":"Joe","lastName":"Frankie","endMostName":"Doe","nationality":"Unknown","gender":"M","dateOfBirth":"2018-11-13","bloodGroup":"AB","rh":"+","emailId":"noone@nowhere.xom","mobileNumber":"98765432","officePhone":"23456789","address":"Block 1 Street 2 Building 123 Floor 1 Flat 2","city":"????? ??????","state":"","country":""}

 */
//318111300014
$(document).ready(function () {

    $("#civilId").bind("change", function (e) {
        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/fetchUserByCivilId?civilId=" + $("#civilId").val(),
            function (data) {
                $.each(data, function (attr, value) {
                    if (attr == "firstName") {
                        $("#firstName").val(value);
                    } else if (attr == "middleName") {
                        $("#secondName").val(value);
                    } else if (attr == "lastName") {
                        $("#thirdName").val(value);
                    } else if (attr == "endMostName") {
                        $("#fourthName").val(value);
                    } else if (attr == "nationality") {
                        $("#nationality").val(value);
                    } else if (attr == "gender") {
                        $("#gender").val(value);
                    } else if (attr == "bloodGroup") {
                        $("#bloodGroup").val(value);
                    } else if (attr == "rh") {
                        $("#rh").val(value);
                    } else if (attr == "emailId") {
                        $("#emailId").val(value);
                    } else if (attr == "mobileNumber") {
                        $("#mobileNumber").val(value);
                    } else if (attr == "officePhone") {
                        $("#officePhone").val(value);
                    } else if (attr == "address") {
                        $("#address").val(value);
                    } else if (attr == "city") {
                        $("#city").val(value);
                    } else if (attr == "state") {
                        $("#state").val(value);
                    } else if (attr == "country") {
                        $("#country").val(value);
                    }
                });
            });
    })
    ;
})
;
