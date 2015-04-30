/**
 * Created by pradyumna on 24-04-2015.
 */

$(document).ready(function () {


    $.getJSON('http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/allTPAPayers', function (data){
        $.each(data, function (attr, value) {
            var option = $('<option></option>').val(value['payerId']).text(value['insuranceName']);
            $('#tpa').append(option);
        });
    });

    $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getInsuranceDetailsOfTpa",
        function (data) {
            $.each(data, function (attr, value) {
                var option = $('<option></option>').val(value['payerId']).text(value['insuranceName']);
                $('#insurances').append(option);
            });
        });


    $('#tpa').change( function () {
        var payerId = $(this).val();
        $('#insurances').empty();
        if(payerId) {
            $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getInsuranceDetailsOfTpa?payerId=" + payerId,
                function (data) {
                    $.each(data, function (attr, value) {
                            var option = $('<option></option>').val(value['payerId']).text(value['insuranceName']);
                            $('#insurances').append(option);
                    });
                });
        }
    });


    $('#insurances').change(function () {
        var payerId = $(this).val();
        $('#groupName').empty();
        var option = $('<option></option>').val(null).text('');
        $('#groupName').append(option);
        if(payerId) {
            $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/fetchListOfGroupNamesByPayer?payerId=" + payerId,
                function (data) {
                    $.each(data, function (attr, value) {
                        var option = $('<option></option>').val(value['groupId']).text(value['groupName']);
                        $('#groupName').append(option);
                    });
                });
        }
    });

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


   /* $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/fetchListOfGroupNamesByClinicId?clinicId=1",
        function (data) {
            $.each(data, function (attr, value) {
                var i = 0;
                var option = $('<option></option>').val(value['groupId']).text(value['groupName']);
                $('#groupName').append(option);
            });
        });*/


    $('#benefitPlanNameDropDown').change(function () {
        $('#benefitPlanId').val($(this).val());
        ($(this).find('option:selected').text());
        $('#benefitPlanName').val($(this).find('option:selected').text());
    });

    $('#moduleId').change(function () {
        $('#moduleName').val($(this).find('option:selected').text());
    });

    $('#groupName').change(function () {
        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPlanDetailsForGroupId?groupId=" + $(this).val(),
            function (data) {
                $.each(data, function (attr, value) {
                    if (attr == "policyNumber") {
                        $('#policyNo').val(value);
                    }
                    else if (attr == "planStartDate") {
                        $('#startDate').val(value);
                    }
                    else if (attr == "planEndDate") {
                        alert(' endDate '+value);
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
                });
            });
    });
});
