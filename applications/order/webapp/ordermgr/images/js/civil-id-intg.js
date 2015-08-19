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
                    } else if (attr == "dateOfBirth") {
                        var input = value,
                        datePart = input.match(/\d+/g),
                        year = datePart[0],
                        month = datePart[1],
                        day = datePart[2],
                        dob = day+'/'+month+'/'+year;
                        $("#dateOfBirth").val(dob);
                        $('#dateOfBirth_i18n').val(dob);
                    } else if (attr == "nationality") {
                        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/getNationalityByNationalityCode?nationalityCode=" + value,
                        function (data) {
                            $.each(data, function (attr, value) {
                                if (attr == "nationality") {
                                    var option = $('<option></option>').val(value).text(value);
                                    option.attr('selected', 'selected');
                                    $('#nationality').append(option);
                                }
                            });
                        });
                    } else if (attr == "gender") {
                        var genderInput = value;
                        if(genderInput == "Male" || genderInput == "M")
                            $("#gender").val("M");
                        if(genderInput == "Female" || genderInput == "F")
                            $("#gender").val("F");
                        if(genderInput == "")
                            $("#gender").val("");
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
                    } else if (attr == "uhid") {
                        $("#uhid").val(value);
                    }
                });
            });
    });

    $.getJSON('http://5.9.249.197:7878/afya-portal/anon/getAllNationality', function (data) {
        $.each(data, function (attr, value) {
            var option = $('<option></option>').val(value['nationality']).text(value['nationality']);
            var nationality = value['nationality'];
            if (nationality == "Kuwaiti") {
                option.attr('selected', 'selected');
            }
            $('#nationality').append(option);
        });
    });

    $.getJSON('http://5.9.249.197:7878/afya-portal/anon/getAllNationality', function (data) {
        $.each(data, function (attr, value) {
            var option = $('<option></option>').val(value['nationality']).text(value['nationality']);
            $('#citizenship').append(option);
        });
    });

    $.getJSON('http://5.9.249.197:7878/afya-portal/anon/getAllCities', function (data) {
        $.each(data, function (attr, value) {
            var option = $('<option></option>').val(value['city']).text(value['city']);
            $('#city').append(option);
        });
    });

    $("#city").bind("change", function (e) {
        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/getStateCountryBasedOnCity?city=" + $("#city").val(),
            function (data) {
                $.each(data, function (attr, value) {
                    if (attr == "cityId") {
                        $("#cityId").val(value);
                    } else if (attr == "city") {
                        $("#city").val(value);
                    } else if (attr == "cityCode") {
                        $("#cityCode").val(value);
                    } else if (attr == "stateId") {
                        $("#stateId").val(value);
                    } else if (attr == "state") {
                        $("#state").val(value);
                    } else if (attr == "stateCode") {
                        $("#stateCode").val(value);
                    } else if (attr == "countryId") {
                        $("#countryId").val(value);
                    } else if (attr == "country") {
                        $("#country").val(value);
                    } else if (attr == "countryCode") {
                        $("#countryCode").val(value);
                    }
                });
            });
    });

    $('#scanButton').click(function () {
        $.getJSON("http://localhost:7373/api/convertTextToJson",
        function (data) {
            var civilUserDto = data['civilUserDto'];
            $.each(civilUserDto, function (attr, value) {
                if (attr == "civilId")
                    $('#civilId').val(value);
                else if (attr == "firstName")
                    $('#firstName').val(value);
                else if (attr == "firstName")
                    $('#firstName').val(value);
                else if (attr == "middleName")
                    $('#secondName').val(value);
                else if (attr == "lastName")
                    $('#thirdName').val(value);
                else if (attr == "nationality") {
                    $.getJSON("http://5.9.249.197:7878/afya-portal/anon/getNationalityByNationalityCode?nationalityCode=" + value,
                    function (data) {
                        $.each(data, function (attr, value) {
                            if (attr == "nationality") {
                                var option = $('<option></option>').val(value).text(value);
                                option.attr('selected', 'selected');
                                $('#nationality').append(option);
                            }
                        });
                    });
                } else if (attr == "gender")
                    $('#gender').val(value);
                else if (attr == "dateOfBirth") {
                    $('#dateOfBirth').val(value);
                    $('#dateOfBirth_i18n').val(value);
                } else if (attr == "emailId")
                    $('#emailId').val(value);
                else if (attr == "mobileNumber")
                    $('#mobileNumber').val(value);
                else if (attr == "telephoneNumber")
                    $('#homePhone').val(value);
                else if (attr == "address")
                    $('#address').val(value);
                else if (attr == "city")
                    $('#city').val(value);
                else if (attr == "state")
                    $('#state').val(value);
                else if (attr == "country")
                    $('#country').val(value);
            });
        });
    });

});
