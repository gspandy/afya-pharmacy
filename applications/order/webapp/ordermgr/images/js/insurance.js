/**
 * Created by pradyumna on 24-04-2015.
 */

$(document).ready(function () {


    $('#patientInsurance').change( function () {
        var benefitPlanId = $(this).val();
        $('#benefitPlanId').val($(this).val());
        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getModulesByBenefitId?benefitId=" + benefitPlanId,
            function (data) {
                $.each(data, function (attr, value) {
                    var option = $('<option></option>').val(value['moduleId']).text(value['moduleName']);
                    $('#moduleId').append(option);
                });
            });
    });


    $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/fetchListOfGroupNamesByClinicId?clinicId=1",
        function (data) {
            $.each(data, function (attr, value) {
                var i = 0;
                var option = $('<option></option>').val(value['groupId']).text(value['groupName']);
                $('#groupName').append(option);
            });
        });


    $('#benefitPlanNameDropDown').change(function () {
        $('#benefitPlanId').val($(this).val());
        alert($(this).find('option:selected').text());
        $('#benefitPlanName').val($(this).find('option:selected').text());
    });

    $('#moduleId').change(function () {
        alert($('#moduleId').val());
        $('#moduleName').val($(this).find('option:selected').text());
        alert($('#moduleName').val());
    });

    $('#groupName').change(function () {

        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPlanDetailsForGroupId?groupId=" + $(this).val(),
            function (data) {
                $.each(data, function (attr, value) {
                    if (attr == "tpaDetails") {
                        $('#tpa').val(value['insuranceName']);
                    }
                    else if (attr == "insuranceForTpa") {
                        var i = 0;
                        for (; i < value.length; i++) {
                            var option = $('<option></option>').val(value[i]['id']).text(value[i]['insuranceName']);
                            $('#insurances').append(option);
                        }
                    } else if (attr == "policyNumber") {
                        $('#policyNo').val(value);
                    }
                    else if (attr == "startDate") {
                        $('#startDate').val(value);
                    }
                    else if (attr == "endDate") {
                        $('#endDate').val(value);
                    } else if (attr == "benefits") {
                        var i = 0;
                        for (; i < value.length; i++) {
                            var option = $('<option></option>').val(value[i]['benefitPlanId']).text(value[i]['benefitPlan']);
                            $('#benefitPlanNameDropDown').append(option);
                        }
                    }else if(attr=="healthPolicy"){
                        $('#healthPolicyName').val(value['healthPolicyName']);
                        $('#healthPolicyId').val(value['healthPolicyId']);
                    }
                    console.log(JSON.stringify(value));
                });
            });
    });
});