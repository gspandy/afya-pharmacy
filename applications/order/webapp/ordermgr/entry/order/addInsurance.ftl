<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<script language="JavaScript" type="text/javascript">
  function validate(selection) {
    var memberType = selection.value;
    if(memberType == "SELF") {
      relationship_title.style.display = 'none';
      relationship_dropdownField.style.display = 'none';
      $('#relationship').val("");
      primaryMember_title.style.display = 'none';
      primaryMember_lookupField.style.display = 'none';
      $('#primaryMember').val("");
    }
    if(memberType != "SELF") {
      relationship_title.style.display = '';
      relationship_dropdownField.style.display = '';
      primaryMember_title.style.display = '';
      primaryMember_lookupField.style.display = '';
    }
  }
  function validateInsuranceType(selection) {
    var insuranceType = selection.value;
    if(insuranceType == "GROUP") {
      groupName_title_td.style.display = '';
      groupName_selection_td.style.display = '';
    }
    if(insuranceType != "GROUP") {
      groupName_title_td.style.display = 'none';
      groupName_selection_td.style.display = 'none';
      $('#group').empty();
    }
  }
</script>
<form name="addInsurance" id="addInsurance" method="post" action="<@ofbizUrl>addInsurance</@ofbizUrl>">
    <input type="hidden" name="patientId" id="patientId" value="${patientId}"/>
    <input type="hidden" name="gender" id="gender" value="${gender}"/>
    <input type="hidden" name="groupName" id="groupName"/>
    <input type="hidden" name="healthPolicyId" id="healthPolicyId"/>
    <input type="hidden" name="policyNo" id="policyNo"/>
    <input type="hidden" name="benefitPlanId" id="benefitPlanId"/>
    <input type="hidden" name="benefitPlanName" id="benefitPlanName"/>
    <table cellspacing="0" class="basic-table">
      <tr>
        <td class="label">
          <span id="insuranceType_title">Insurance Type</span>
        </td>
        <td>
          <span class="ui-widget">
            <select name="insuranceType" id="insuranceType" size="1" onchange="javascript:validateInsuranceType(this);">
              <option value="INDIVIDUAL">Individual</option>
              <option value="GROUP" selected="selected">Group</option>
            </select>
          </span>
        </td>
        <td id="groupName_title_td" class="label">
          <span id="groupName_title">Group</span><font color="red"> *</font>
        </td>
        <td id="groupName_selection_td">
          <span class="ui-widget">
            <select name="group" class="required" id="group" size="1"><option> </option></select>
          </span>
        </td>
      </tr>
      <tr>
        <td class="label">
          <span id="memberType_title">Member Type</span>
        </td>
        <td>
          <span class="ui-widget">
            <select name="memberType" id="memberType" size="1" onchange="javascript:validate(this);">
              <option value="SELF" selected="selected">Self</option>
              <option value="DEPENDENT">Dependent</option>
            </select>
          </span>
        </td>
        <td id="relationship_title" class="label" style="display:none">
          <span id="relationship_title">Relationship</span><font color="red"> *</font>
        </td>
        <td id="relationship_dropdownField" style="display:none">
          <span class="ui-widget">
            <select name="relationship" id="relationship" class="required" size="1">
              <option value="">&nbsp;</option>
              <#if gender == "M" || gender == "Male">
                <option value="FATHER">Father</option>
                <option value="SPOUSE">Spouse</option>
                <option value="SON">Son</option>
              <#elseif gender="F" || gender == "Female">
                <option value="MOTHER">Mother</option>
                <option value="SPOUSE">Spouse</option>
                <option value="DAUGHTER">Daughter</option>
              <#else>
                <option value="FATHER">Father</option>
                <option value="MOTHER">Mother</option>
                <option value="SPOUSE">Spouse</option>
                <option value="SON">Son</option>
                <option value="DAUGHTER">Daughter</option>
              </#if>
            </select>
          </span>
        </td>
        <td id="primaryMember_title" class="label" style="display:none">
          <span id="primaryMember_title">Primary Member</span><font color="red"> *</font>
        </td>
        <td id="primaryMember_lookupField" style="display:none">
          <@htmlTemplate.lookupField formName="addInsurance" name="primaryUHID" id="primaryMember" fieldFormName="LookupPatientByMembershipId" className="required"/>
          <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('selectOrderForm_orderId,<@ofbizUrl>LookupPatientByMembershipId</@ofbizUrl>,ajaxLookup=Y&amp;searchValueField=primaryMember', true);</script>
        </td>
      </tr>
      <tr>
        <td class="label">
          <span id="policy_title">Policy</span><font color="red"> *</font>
        </td>
        <td>
          <span class="ui-widget">
            <select name="policy" class="required" id="policy" size="1"><option> </option></select>
          </span>
        </td>
        <td class="label">
          <span id="insurances_title">Insurance</span><font color="red"> *</font>
        </td>
        <td>
          <#-- <span class="ui-widget">
            <select name="insurance" class="required" id="insurances" size="1"><option> </option></select>
          </span> -->
          <input type="text" size="25" readonly="true" name="insuranceName" id="insurance" class="required"/>
        </td>
        <td class="label" id="tpa_title">
          <span id="tpa_title">TPA</span>
        </td>
        <td id="tpa_textField">
          <#-- <span class="ui-widget">
            <select name="tpa" id="tpa" size="1"><option> </option></select>
          </span> -->
          <input type="text" size="25" readonly="true" name="tpa" id="tpa"/>
        </td>
        <td class="label">
          <span id="healthPolicyName_title">Health Plan</span><font color="red"> *</font>
        </td>
        <td>
          <input type="text" size="20" readonly="true" name="healthPolicyName" id="healthPolicyName" class="required"/>
        </td>
      </tr>
      <tr>
        <#-- <td class="label">
          <span id="benefitPlanNameDropDown_title">Benefits</span><font color="red"> *</font>
        </td>
        <td>
          <span class="ui-widget">
            <select name="benefitPlanNameDropDown" class="required" id="benefitPlanNameDropDown" size="1"><option> </option></select>
          </span>
        </td> -->
        <td class="label">
          <span id="uHID_title">Membership ID</span><font color="red"> *</font>
        </td>
        <td>
          <input type="text" name="uHID" class="required" size="25" id="uHID"/>
        </td>
        <#-- <td class="label">
          <span id="policyNo_title">Policy No</span>
        </td>
        <td>
          <input type="text" readonly="true" name="policyNo" size="25" id="policyNo"/>
        </td> -->
        <td class="label">
          <span id="startDate_title">Start Date</span>
        </td>
        <td>
          <input type="text" readonly="true" name="startDate" size="25" id="startDate"/>
        </td>
        <td class="label">
          <span id="endDate_title">End Date</span>
        </td>
        <td>
          <input type="text" readonly="true" name="endDate" size="25" id="endDate"/>
        </td>
      </tr>
      <tr>
        <td colspan="6" style="text-align:center">
          <input type="submit" name="createButton" value="${uiLabelMap.CommonSave}" class="btn btn-success"/>
          <a href="<@ofbizUrl>insuranceList?patientId=${patientId}</@ofbizUrl>" class="btn btn-danger">${uiLabelMap.CommonCancel}</a>
        </td>
      </tr>
    </table>
</form>
<script type="text/javascript">
  var form = document.addInsurance;
  jQuery(form).validate();
</script>