/**
 * Created by pradyumna on 24-04-2015.
 */
$(document).ready(function () {

    /*$.getJSON('http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/allTPAPayers', function (data) {
        $.each(data, function (attr, value) {
            var option = $('<option></option>').val(value['payerId']).text(value['insuranceName']);
            $('#tpa').append(option);
        });
    });*/

    $('#insuranceType').change(function () {
        $('#policy').empty();
        var insuranceType = $(this).val();
        if (insuranceType == "INDIVIDUAL") {
            var option = $('<option></option>').val(null).text('');
            $('#policy').append(option);
            $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPolicyForIndividual",
            function (data) {
                $.each(data, function (attr, value) {
                    var healthPolicyId = value['healthPolicyId'];
                    var payerId = value['payerId'];
                    var policyNo = value['policyNo'];
                    var comma = ",";
                    var policyAndPayerId = healthPolicyId+comma+payerId+comma+policyNo;
                    //alert(policyAndPayerId);
                    var option = $('<option></option>').val(policyAndPayerId).text(value['policyNo']);
                    $('#policy').append(option);
                });
            });
        }
        if (insuranceType == "GROUP") {
            $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getAllGroups",
            function (data) {
                var option = $('<option></option>').val(null).text('');
                $('#group').append(option);
                $.each(data, function (attr, value) {
                    var groupId = value['groupId'];
                    var groupName = value['groupName'];
                    var comma = ",";
                    var group = groupId+comma+groupName;
                    var option = $('<option></option>').val(group).text(value['groupName']);
                    $('#group').append(option);
                });
            });
        }
    });



    $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getAllGroups",
            function (data) {
                $.each(data, function (attr, value) {
                    var groupId = value['groupId'];
                    var groupName = value['groupName'];
                    var comma = ",";
                    var group = groupId+comma+groupName;
                    var option = $('<option></option>').val(group).text(value['groupName']);
                    $('#group').append(option);
                });
            });

    /*$.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getInsuranceDetailsOfTpa",
        function (data) {
            $.each(data, function (attr, value) {
                var option = $('<option></option>').val(value['payerId']).text(value['insuranceName']);
                $('#insurances').append(option);
            });
        });*/

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
        if($('#benefitPlanIdLink').length==0) {
            $(this).parent().append("<span id='benefitPlanIdLink'><a href=javascript:void(0);>View Plan Details</a></span>");
        }
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

    $('#hisBenefitId').change(function () {
        $('#moduleName').val($(this).find('option:selected').text());
    });


    $('#policy').change(function () {
        var policyAndPayerId = $(this).val();
        //alert("Inside Policy Function: "+policyAndPayerId);
        var policy = policyAndPayerId.split(',');
        var healthPolicyId = policy[0];
        //alert("healthPolicyId= "+healthPolicyId);
        var payerId = policy[1];
        //alert("payerId= "+payerId);
        var policyNo = policy[2];
        //alert("policyNo= "+policyNo);
        var relationship = $('#relationship').val();
        var gender = $('#gender').val();
        //alert("gender= "+gender);
        if(relationship == "")
            var dependent = "SELF";
        else
            var dependent = relationship;
        //alert("dependent= "+dependent);

        $('#policyNo').val(policyNo);

        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPayerById?payerId=" + payerId,
        function (data) {
            $.each(data, function (attr, value) {
                var payerId = value['payerId'];
                var payerType = value['payerType'];
                var insuranceCode = value['insuranceCode'];
                var insuranceName = value['insuranceName'];
                if(data) {
                    if (payerType == "INSURANCE") {
                        $("#insurance").val(insuranceName);
                        tpa_title.style.display = 'none';
                        tpa_textField.style.display = 'none';
                        $('#tpa').val("");

                        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPlanDetailsForPolicyId?policyId=" + healthPolicyId + "&dependent=" + dependent + "&gender=" + gender,
                        function (data) {
                            $.each(data, function (attr, value) {
                                if (attr == "planStartDate") {
                                    var input = value,
                                        datePart = input.match(/\d+/g),
                                        year = datePart[0],
                                        month = datePart[1],
                                        day = datePart[2],
                                        startDate = day+'/'+month+'/'+year;
                                        $('#startDate').val(startDate);
                                } else if (attr == "planEndDate") {
                                    var input = value,
                                        datePart = input.match(/\d+/g),
                                        year = datePart[0],
                                        month = datePart[1],
                                        day = datePart[2],
                                        endDate = day+'/'+month+'/'+year;
                                        $('#endDate').val(endDate);
                                }
                            });
                            console.log(JSON.stringify(data));
                            var benefitPlan = data['benefits'];
                            var benefitPlanDetails = benefitPlan[0];
                            $.each(benefitPlanDetails, function (attr, value) {
                                if (attr == "benefitPlanId") {
                                    $('#benefitPlanId').val(value);
                                    if($('#benefitPlanIdLink').length==0) {
                                        $('#healthPolicyName').parent().append("<span id='benefitPlanIdLink'><a href=javascript:void(0);>View Plan Details</a></span>");
                                    }
                                } else if (attr == "benefitPlan")
                                    $('#benefitPlanName').val(value);
                            });
                            var healthPolicyDetails = data['healthPolicy'];
                            $.each(healthPolicyDetails, function (attr, value) {
                                if (attr == "healthPolicyId")
                                    $('#healthPolicyId').val(value);
                                else if (attr == "healthPolicyName")
                                    $('#healthPolicyName').val(value);
                            });
                        });
                    } else {
                        $("#insurance").val(insuranceName);
                        tpa_title.style.display = '';
                        tpa_textField.style.display = '';
                        $('#tpa').val(insuranceName);

                        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPlanDetailsForPolicyId?policyId=" + healthPolicyId + "&dependent=" + dependent + "&gender=" + gender,
                        function (data) {
                            $.each(data, function (attr, value) {
                                if (attr == "planStartDate") {
                                    var input = value,
                                        datePart = input.match(/\d+/g),
                                        year = datePart[0],
                                        month = datePart[1],
                                        day = datePart[2],
                                        startDate = day+'/'+month+'/'+year;
                                        $('#startDate').val(startDate);
                                } else if (attr == "planEndDate") {
                                    var input = value,
                                        datePart = input.match(/\d+/g),
                                        year = datePart[0],
                                        month = datePart[1],
                                        day = datePart[2],
                                        endDate = day+'/'+month+'/'+year;
                                        $('#endDate').val(endDate);
                                }
                            });
                            console.log(JSON.stringify(data));
                            var benefitPlan = data['benefits'];
                            var benefitPlanDetails = benefitPlan[0];
                            $.each(benefitPlanDetails, function (attr, value) {
                                if (attr == "benefitPlanId") {
                                    $('#benefitPlanId').val(value);
                                    if($('#benefitPlanIdLink').length==0) {
                                        $('#healthPolicyName').parent().append("<span id='benefitPlanIdLink'><a href=javascript:void(0);>View Plan Details</a></span>");
                                    }
                                } else if (attr == "benefitPlan")
                                    $('#benefitPlanName').val(value);
                            });
                            var healthPolicyDetails = data['healthPolicy'];
                            $.each(healthPolicyDetails, function (attr, value) {
                                if (attr == "healthPolicyId")
                                    $('#healthPolicyId').val(value);
                                else if (attr == "healthPolicyName")
                                    $('#healthPolicyName').val(value);
                            });
                        });
                    }
                }
            });
        });

        /*$.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getHealthPolicyById?id=" + healthPolicyId,
        function (data) {
            $.each(data, function (attr, value) {
                $('#healthPolicyName').val(value['policyName']);
            });
        });*/
    });

    $('#group').change(function () {
        var group = $(this).val();
        var splitGroup = group.split(',');
        var groupId = splitGroup[0];
        var groupName = splitGroup[1];
        var relationship = $('#relationship').val();
        var gender = $('#gender').val();
        if(relationship == "")
            var dependent = "SELF";
        else
            var dependent = relationship;

        $('#groupId').val(groupId);
        $('#groupName').val(groupName);

        $.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPolicyByGroupId?groupId=" + groupId,
                function (data) {
                    $('#policy').empty();
                    var option = $('<option></option>').val(null).text('');
                    $('#policy').append(option);
                    $.each(data, function (attr, value) {
                        var healthPolicyId = value['healthPolicyId'];
                        var payerId = value['payerId'];
                        var policyNo = value['policyNo'];
                        var comma = ",";
                        var policyAndPayerId = healthPolicyId+comma+payerId+comma+policyNo;
                        //alert(policyAndPayerId);
                        var option = $('<option></option>').val(policyAndPayerId).text(value['policyNo']);
                        $('#policy').append(option);
                    });
                });

        /*$.getJSON("http://5.9.249.197:7878/afya-portal/anon/insuranceMaster/getPlanDetailsForGroupId?groupId=" + groupId + "&dependent=" + dependent + "&gender=" + gender,
            function (data) {
                $.each(data, function (attr, value) {
                    if (attr == "policyNumber") {
                        $('#policyNo').val(value);
                    }
                    else if (attr == "planStartDate") {
                        var input = value,
                        datePart = input.match(/\d+/g),
                        year = datePart[0],
                        month = datePart[1],
                        day = datePart[2],
                        startDate = day+'/'+month+'/'+year;
                        $('#startDate').val(startDate);
                    }
                    else if (attr == "planEndDate") {
                        var input = value,
                        datePart = input.match(/\d+/g),
                        year = datePart[0],
                        month = datePart[1],
                        day = datePart[2],
                        endDate = day+'/'+month+'/'+year;
                        $('#endDate').val(endDate);
                    } else if (attr == "benefits") {
                        var i = 0;
                        $('#benefitPlanNameDropDown').empty();
                        var option = $('<option></option>').val(null).text('');
                        $('#benefitPlanNameDropDown').append(option);
                        for (; i < value.length; i++) {
                            var option = $('<option></option>').val(value[i]['benefitPlanId']).text(value[i]['benefitPlan']);
                            $('#benefitPlanNameDropDown').append(option);
                        }
                    } else if (attr == "healthPolicy") {
                        $('#healthPolicyName').val(value['healthPolicyName']);
                        $('#healthPolicyId').val(value['healthPolicyId']);
                    }
                });
            });*/
    });

    $('.insuranceList tr').bind('click', function (event){
        //event.preventDefault();
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
            console.log(JSON.stringify(data));
            var moduleDetails = data['moduleDetails'];
            var moduleHtml='';
            $.each(moduleDetails, function (index, moduleDetail) {
                moduleHtml=moduleHtml+'<tr><td>'+moduleDetail.moduleName+'</td><td style="text-align:right">'
                +amount(moduleDetail.sumInsured)+'</td>'
                +decodeBoolean(moduleDetail.authorization)+'<td style="text-align:right">'
                +amount(moduleDetail.deductibleAmount)+'</td><td style="text-align:right">'
                +amount(moduleDetail.deductiblePercentage)+'</td><td style="text-align:right">'
                +amount(moduleDetail.copayAmount)+'</td><td style="text-align:right">'
                +amount(moduleDetail.copayPercentage)+'</td><td>'
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
                +decodeBoolean(serviceDetail.authorization)+'<td style="text-align:right">'
                +amount(serviceDetail.deductibleAmount)+'</td><td style="text-align:right">'
                +amount(serviceDetail.deductiblePercentage)+'</td><td style="text-align:right">'
                +amount(serviceDetail.copayAmount)+'</td><td style="text-align:right">'
                +amount(serviceDetail.copayPercentage)+'</td><td style="text-align:right">'
                +amount(serviceDetail.individualLimitAmount)+'</td><td style="text-align:right">'
                +text(serviceDetail.numberOfCases)+'</td></tr>';
            });
            $('#serviceTable').find('tbody').html(serviceHtml);
        });

        $('#myModal').modal({
            keyboard: true
        });
    }


    function amount(content){
        if(content==undefined || content=='null')
            return '0.000';
        else return parseFloat(content).toFixed(3);
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
