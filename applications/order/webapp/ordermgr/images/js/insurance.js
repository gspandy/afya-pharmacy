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

          /*  $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getHealthPoliciesByPayer?payerId=" + payerId,
                function (data) {
                    $.each(data, function (attr, value) {
                        alert(value);
                        var option = $('<option></option>').val(value['id']).text(value['policyName']);
                        $('#benefitPlanNameDropDown').append(option);
                    });
                });*/
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
        if($('#benefitPlanIdLink').length==0) {
            $(this).parent().append("<span id='benefitPlanIdLink'><a href=javascript:void(0);>View Plan Details</a></span>");
        }
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
        populateTable(benefitPlanId);
    });


    $('#benefitPlanIdLink').live('click', function (event){
        populateTable($('#benefitPlanId').val());
    });

    $('#benefitPlanLink').bind('click', function (event){
        populateTable($('#benefitPlanId').val());
    });

    $('#benefitPlanId').bind('click', function (event){
        populateTable($('#benefitPlanId').val());
    });

    function populateTable(benefitPlanId){
        $.getJSON('http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getServiceOrModuleDataByBenefitId?benefitId='+benefitPlanId, function(data) {
            var moduleDetails = data['moduleDetails'];
            var moduleHtml='';
            $.each(moduleDetails, function (index, moduleDetail) {
                moduleHtml=moduleHtml+'<tr><td>'+moduleDetail.moduleName+'</td><td>'
                +text(moduleDetail.sumInsured)+'</td>'
                +decodeBoolean(moduleDetail.authorization)+'<td>'
                +moduleDetail.deductibleAmount+'</td><td>'
                +percentage(moduleDetail.deductiblePercentage)+'</td><td>'
                +moduleDetail.copayAmount+'</td><td>'
                +percentage(moduleDetail.copayPercentage)+'</td><td>'
                +text(moduleDetail.computeBy)+'</td>'
                +decodeBoolean(moduleDetail.authorizationInclusiveConsultation)+''
                +decodeBoolean(moduleDetail.authorizationRequiredConsultation)+'</tr>';
            });
            $('#moduleTable').find('tbody').html(moduleHtml);

            var serviceDetails = data['associatedServiceDetailsOfTheModule'];
            var serviceHtml='';
            $.each(serviceDetails, function (index, serviceDetail) {
                serviceHtml=serviceHtml+'<tr><td>'+serviceDetail.serviceName+'</td><td>'
                +serviceDetail.moduleName+'</td>'
                +decodeBoolean(serviceDetail.authorization)+'<td>'
                +serviceDetail.deductibleAmount+'</td><td>'+
                percentage(serviceDetail.deductiblePercentage)+'</td><td>'
                +serviceDetail.copayAmount+'</td><td>'
                +percentage(serviceDetail.copayPercentage)+'</td><td>'
                +serviceDetail.individualLimitAmount+'</td><td>'
                +text(serviceDetail.numberOfCases)+'</td></tr>';
            });
            $('#serviceTable').find('tbody').html(serviceHtml);
        });

        $('#myModal').modal({
            keyboard: true
        });
    }

    function text(content){
        if(content==undefined || content=='null')
        return '';
        else return content;
    }

    function percentage(content){
        if(content==undefined || content=='null')
            return '';
        else
            return content+'%';
    }

    function decodeBoolean(auth){
        if(auth==1)
            return "<td class='success' style='text-align:center'><span class='icon-ok'></span></td>";
        else
            return "<td class='error' style='text-align:center'><span class='icon-remove'></span></td>";

    }

});
