/**
 * Created by pradyumna on 24-04-2015.
 */
$(document).ready(function () {

    $.getJSON('http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/allTPAPayers', function (data) {
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

    $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getHISModules", function (data){
        $.each(data, function (attr, value) {
            var option = $('<option></option>').val(value['hisModuleId']).text(value['hisBenefitName']);
            $('#hisBenefitId').append(option);
        });
    });


    $('#tpa').change(function () {
        var payerId = $(this).val();
        $('#insurances').empty();
        var option = $('<option></option>').val(null).text('');
        $('#insurances').append(option);
        if (payerId) {
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
        if (payerId) {
            $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/fetchListOfGroupNamesByPayer?payerId=" + payerId,
                function (data) {
                    $.each(data, function (attr, value) {
                        var option = $('<option></option>').val(value['groupId']).text(value['groupName']);
                        $('#groupName').append(option);
                    });
                });
        }
    });

    $('#patientInsurance').change(function () {
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
                        $('#endDate').val(value);
                    } else if (attr == "benefits") {
                        var i = 0;
                        for (; i < value.length; i++) {
                            var option = $('<option></option>').val(value[i]['benefitPlanId']).text(value[i]['benefitPlan']);
                            $('#benefitPlanNameDropDown').append(option);
                        }
                    } else if (attr == "healthPolicy") {
                        $('#healthPolicyName').val(value['healthPolicyName']);
                        $('#healthPolicyId').val(value['healthPolicyId']);
                    }
                });
            });
    });

    $('.insuranceList tr').bind('click', function (event){
        event.preventDefault();
        event.stopPropagation();
        var rowIndex=this.rowIndex;
        var benefitPlanId = $(this).find("input[name|='benefitPlanId']").val();
        $.getJSON('http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getServiceOrModuleDataByBenefitId?benefitId='+benefitPlanId, function(data) {
            var moduleDetails = data['moduleDetails'];
            var moduleHtml='';
            $.each(moduleDetails, function (index, moduleDetail) {
                moduleHtml=moduleHtml+'<tr><td>'+moduleDetail.moduleName+'</td><td></td>'+moduleDetail.authorization+'</td><td>'+moduleDetail.copayAmount+'</td><td>'+
                    moduleDetail.copayPercentage+'</td><td>'+moduleDetail.deductibleAmount+'</td><td>'+moduleDetail.deductiblePercentage+'</td></tr>';
            });
            $('#moduleTable').find('tbody').html(moduleHtml);
            var serviceHtml='';
            var serviceDetails = data['associatedServiceDetailsOfTheModule'];
            $.each(serviceDetails, function (index, serviceDetail) {
                serviceHtml=serviceHtml+'<tr><td>'+serviceDetail.serviceId+'</td><td></td>'+serviceDetail.authorization+'</td><td>'+serviceDetail.copayAmount+'</td><td>'+
                serviceDetail.copayPercentage+'</td><td>'+serviceDetail.deductibleAmount+'</td><td>'+serviceDetail.deductiblePercentage+'</td></tr>';
            });
            $('#serviceTable').find('tbody').html(serviceHtml);
        });

        $('#myModal').modal({
            keyboard: false
        });

    });
});



