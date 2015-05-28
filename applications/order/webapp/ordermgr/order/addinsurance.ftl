<form method="post" action="/ordermgr/control/addInsurance" id="AddInsurance" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="AddInsurance">
    <input type="hidden" name="patientId" value="10010" id="AddInsurance_patientId"/>
    <input type="hidden" name="healthPolicyId" id="healthPolicyId"/>
    <input type="hidden" name="benefitPlanId" id="benefitPlanId"/>
    <input type="hidden" name="benefitPlanName" id="benefitPlanName"/>
    <table cellspacing="0" class="basic-table">
        <tr>
            <td class="">
                <span id="tpa_title">TPA</span></td>
            <td><span class="ui-widget">
<select name="tpa"
        id="tpa" size="1">
    <option value="Y"></option>
</select>
</span></td>
            <td class="label">
                <span id="insurances_title">Insurance</span></td>
            <td>
                <span class="ui-widget">
                    <select name="insurance" id="insurances" size="1">
                    <option value="Y"></option>
                </select>
                </span>
            </td>
            <td class="label">
                <span id="groupName_title">Group Name</span></td>
            <td>
                <span class="ui-widget">
                    <select name="groupName" id="groupName" size="1">
                        <option value="Y"></option>
                    </select>
                </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <span id="policyNo_title">Policy No</span></td>
            <td colspan="7">
                <input type="text" name="policyNo"
                       size="25" id="policyNo" autocomplete="off"/></td>
        </tr>
        <tr>
            <td class="label">
                <span id="startDate_title">Start Date</span></td>
            <td colspan="7">
                <input type="text" name="startDate"
                       size="25" id="startDate" autocomplete="off"/></td>
        </tr>
        <tr>
            <td class="label">
                <span id="endDate_title">End Date</span></td>
            <td colspan="7">
                <input type="text" name="endDate"
                       size="25" id="endDate" autocomplete="off"/></td>
        </tr>
        <tr>
            <td class="label">
                <span id="healthPolicyName_title">Health Plan</span><font color="red">*</font></td>
            <td colspan="7">
                <input type="text" name="healthPolicyName" class="required"
                       size="25" id="healthPolicyName" autocomplete="off"/></td>
        </tr>
        <tr>
            <td class="label">
                <span id="benefitPlanNameDropDown_title">Select Benefit Plan</span></td>
            <td colspan="7">
                <span class="ui-widget">
                    <select name="benefitPlanNameDropDown" id="benefitPlanNameDropDown" size="1">
                        <option value="Y"></option>
                    </select>
                    <a href="#" id="benefitPlanLink">View Details</a>
                </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <span id="hisBenefitId_title">Select Benefit</span></td>
            <td colspan="7">
                <span class="ui-widget">
                    <select name="hisBenefitId" id="hisBenefitId" size="1">
                        <option value="Y"></option>
                    </select>
                </span>
            </td>
        </tr>
        <tr>
            <td class="label">
                <span id="uhid_title">Membership ID</span><font color="red">*</font></td>
            <td colspan="7">
                <input type="text" name="uHID" class="required" size="25" id="uhid" autocomplete="off"/>
            </td>
        </tr>
        <tr>
            <td class="">&nbsp;</td>
            <td colspan="4">
                <input type="submit" class="btn btn-success" name="createButton" value="Add Insurance"/>
            </td>
        </tr>
    </table>
</form>
<script type="text/javascript">
    jQuery("#AddInsurance").validate({
        submitHandler: function (form) {
            form.submit();
        }
    });
</script>
