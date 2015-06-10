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
<#if patientId?has_content>
<script language="JavaScript" type="text/javascript">
    function validateGender(selection) {
        var titleValue = selection.value;
        
        if(titleValue == "Mr" || titleValue == "Sr")
            $("#gender").val("M");
        if(titleValue == "Ms" || titleValue == "Miss" || titleValue == "Mrs" || titleValue == "Sra")
            $("#gender").val("F");
        if(titleValue == "Dr" || titleValue == "")
            $("#gender").val("");
    }
</script>
  <form name="editPatientForm" id="editPatientForm" method="post" action="<@ofbizUrl>updatePatient</@ofbizUrl>" class="basic-form">
    <input type="hidden" name="patientId" id="patientId" value="${patientId}"/>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar"></div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <#if "CIVIL_ID" == patient.selectionType>
              <td class="label" id="civilId_title" width="5%"><span id="civilId_title">Civil ID</span></td>
              <td id="civilId_textfield"><input type="text" name="civilId" id="civilId" size="15" maxlength="12" class="civilidrange" value="${patient.civilId?if_exists}"/></td>
            </#if>
            <#if "PASSPORT" == patient.selectionType>
              <td class="label" id="passport_title" width="7%"><span id="passport_title">Passport &#47; VISA</span></td>
              <td id="passport_textField"><input type="text" name="passport" id="passport" size="25" value="${patient.passport?if_exists}"/></td>
              <td class="label" id="expiryDate_title" width="5%"><span id="expiryDate_title">Expiry Date</span></td>
              <td id="expiryDate_dateField" colspan="4">
                <#if patient.expiryDate?exists>
                  <@htmlTemplate.renderDateTimeFieldJsMethod name="expiryDate" value='${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(patient.expiryDate)}' className="date" alert="" 
                          title="Format: MM/dd/yyyy" size="15" maxlength="10" id="expiryDate" dateType="date-time" shortDateInput=true 
                          timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                          hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editPatientForm"
                          javaScriptMethod=""/>
                <#else>
                  <@htmlTemplate.renderDateTimeField name="expiryDate" value="${value!''}" className="date" alert="" 
                          title="Format: MM/dd/yyyy" size="15" maxlength="10" id="item1" dateType="date-time" shortDateInput=true 
                          timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                          hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editPatientForm"/>
                </#if>
              </td>
            </#if>
          </tr>
        </table>
      </div>
    </div>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar">Basic Information</div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <td class="label"><span id="select_title">Afya ID</span></td>
            <td colspan="13">${patient.afyaId?if_exists}</td>
          <tr>
            <td class="label"><span id="title_title">Title</span></td>
            <td>
              <span class="ui-widget">
                <select name="title" id="title" onchange="javascript:validateGender(this);">
                  <#if patient.title?exists>
                    <#if "Mr" == patient.title>
                      <option selected="selected" value="${patient.title}">Mr</option>
                    <#elseif "Mrs" == patient.title>
                      <option selected="selected" value="${patient.title}">Mrs</option>
                    <#elseif "Miss" == patient.title>
                      <option selected="selected" value="${patient.title}">Miss</option>
                    <#elseif "Ms" == patient.title>
                      <option selected="selected" value="${patient.title}">Ms</option>
                    <#elseif "Dr" == patient.title>
                      <option selected="selected" value="${patient.title}">Dr</option>
                    <#elseif "Sr" == patient.title>
                      <option selected="selected" value="${patient.title}">Sr</option>
                    <#elseif "Sra" == patient.title>
                      <option selected="selected" value="${patient.title}">Sra</option>
                    </#if>
                    <option value="${patient.title}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="Mr">Mr</option>
                  <option value="Mrs">Mrs</option>
                  <option value="Miss">Miss</option>
                  <option value="Ms">Ms</option>
                  <option value="Dr">Dr</option>
                  <option value="Sr">Sr</option>
                  <option value="Sra">Sra</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="firstName_title">First Name</span><font color="red"> *</font></td>
            <td><input type="text" name="firstName" id="firstName" size="25" value="${patient.firstName?if_exists}" class="required"/></td>
            <td class="label"><span id="secondName_title">Middle Name</span></td>
            <td><input type="text" name="secondName" id="secondName" size="25" value="${patient.secondName?if_exists}"/></td>
            <td class="label"><span id="thirdName_title">Last Name</span><font color="red"> *</font></td>
            <td colspan="4"><input type="text" name="thirdName" id="thirdName" size="25" value="${patient.thirdName?if_exists}" class="required"/></td>
          </tr>
          <tr>
            <td class="label"><span id="dateOfBirth_title">Date Of Birth</span><font color="red"> *</font></td>
            <td id="dateOfBirth_dateField">
              <#if patient.dateOfBirth?exists>
                <@htmlTemplate.renderDateTimeFieldJsMethod name="dateOfBirth" id="dateOfBirth" value='${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(patient.dateOfBirth)}' className="date required" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" id="dateOfBirth" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editPatientForm"
                        javaScriptMethod=""/>
              <#else>
                <@htmlTemplate.renderDateTimeField name="dateOfBirth" id="dateOfBirth" value="${value!''}" className="date required" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" id="item1" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editPatientForm"/>
              </#if>
            </td>
            <td class="label"><span id="gender_title">Gender</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="gender" id="gender" size="1" class="required">
                  <#if patient.gender?exists>
                    <#if "M" == patient.gender>
                      <option selected="selected" value="${patient.gender}">Male</option>
                    <#elseif "F" == patient.gender>
                      <option selected="selected" value="${patient.gender}">Female</option>
                    </#if>
                    <option value="${patient.gender}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="M">Male</option>
                  <option value="F">Female</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="maritalStatus_title">Marital Status</span></td>
            <td>
              <span class="ui-widget">
                <select name="maritalStatus" id="maritalStatus" size="1">
                  <#if patient.maritalStatus?exists>
                    <#if "ANNULLED" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Annulled</option>
                    <#elseif "DIVORCED" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Divorced</option>
                    <#elseif "DOMESTIC_PARTNER" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Domestic Partner</option>
                    <#elseif "LEGALLY_SEPARATED" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Legally Separated</option>
                    <#elseif "LIVING_TOGETHER" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Living Together</option>
                    <#elseif "MARRIED" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Married</option>
                    <#elseif "OTHER" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Other</option>
                    <#elseif "SEPARATED" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Separated</option>
                    <#elseif "SINGLE" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Single</option>
                    <#elseif "UNMARRIED" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Unmarried</option>
                    <#elseif "WIDOWED" == patient.maritalStatus>
                      <option selected="selected" value="${patient.maritalStatus}">Widowed</option>
                    </#if>
                    <option value="${patient.maritalStatus}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="ANNULLED">Annulled</option>
                  <option value="DIVORCED">Divorced</option>
                  <option value="DOMESTIC_PARTNER">Domestic Partner</option>
                  <option value="LEGALLY_SEPARATED">Legally Separated</option>
                  <option value="LIVING_TOGETHER">Living Together</option>
                  <option value="MARRIED">Married</option>
                  <option value="OTHER">Other</option>
                  <option value="SEPARATED">Separated</option>
                  <option value="SINGLE">Single</option>
                  <option value="UNMARRIED">Unmarried</option>
                  <option value="WIDOWED">Widowed</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="nationality_title">Nationality</span></td>
            <td><input type="text" name="nationality" id="nationality" size="25" value="${patient.nationality?if_exists}"/></td>
            <#-- <td class="label"><span id="bloodGroup_title">Blood Group</span></td>
            <td>
              <span class="ui-widget">
                <select name="bloodGroup" id="bloodGroup">
                  <#if patient.bloodGroup?exists>
                    <#if "A" == patient.bloodGroup>
                      <option selected="selected" value="${patient.bloodGroup}">A</option>
                    <#elseif "B" == patient.bloodGroup>
                      <option selected="selected" value="${patient.bloodGroup}">B</option>
                    <#elseif "AB" == patient.bloodGroup>
                      <option selected="selected" value="${patient.bloodGroup}">AB</option>
                    <#elseif "O" == patient.bloodGroup>
                      <option selected="selected" value="${patient.bloodGroup}">O</option>
                    </#if>
                    <option value="${patient.bloodGroup}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="A">A</option>
                  <option value="B">B</option>
                  <option value="AB">AB</option>
                  <option value="O">O</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="rh_title">R H</span></td>
            <td colspan="7">
              <span class="ui-widget">
                <select name="rH" id="rh">
                  <#if patient.rH?exists>
                    <#if "&#43;" == patient.rH>
                      <option selected="selected" value="${patient.rH}">&#43;</option>
                    <#elseif "-" == patient.rH>
                      <option selected="selected" value="${patient.rH}">-</option>
                    </#if>
                    <option value="${patient.rH}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="&#43;">&#43;</option>
                  <option value="-">-</option>
                </select>
              </span>
            </td> -->
          </tr>
          <tr>
            <#-- <td class="label"><span id="religion_title">Religion</span></td>
            <td><input type="text" name="religion" id="religion" size="25" value="${patient.religion?if_exists}"/></td> -->
            <td class="label"><span id="patientType_title">Patient Type</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="patientType" id="patientType" size="1" class="required">
                  <#if patient.patientType?exists>
                    <#if "CASH" == patient.patientType>
                      <option selected="selected" value="${patient.patientType}">Cash</option>
                    <#elseif "INSURANCE" == patient.patientType>
                      <option selected="selected" value="${patient.patientType}">Insurance</option>
                    <#elseif "CORPORATE" == patient.patientType>
                      <option selected="selected" value="${patient.patientType}">Corporate</option>
                    </#if>
                    <option value="${patient.patientType}">---</option>
                  </#if>
                  <option value="CASH">Cash</option>
                  <option value="INSURANCE">Insurance</option>
                  <option value="CORPORATE">Corporate</option>
                </select>
              </span>
            </td>
            <td class="label">
              <span id="isStaff_title">Staff</span>
            </td>
            <td>
              <span class="ui-widget">
                <select name="isStaff" id="isStaff" size="1">
                  <#if patient.isStaff?exists>
                    <#if "Yes" == patient.isStaff>
                      <option selected="selected" value="${patient.isStaff}">Yes</option>
                    <#elseif "No" == patient.isStaff>
                      <option selected="selected" value="${patient.isStaff}">No</option>
                    </#if>
                    <option value="${patient.patientType}">---</option>
                  </#if>
                  <option value="Yes">Yes</option>
                  <option value="No" selected="selected">No</option>
                </select>
              </span>
            </td>
          </tr>
        </table>
      </div>
    </div>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar">Communication Details</div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <td class="label"><span id="emailId_title">Email</span></td>
            <td><input type="text" name="emailAddress" id="emailId" size="25" value="${patient.emailAddress?if_exists}"/></td>
            <td class="label"><span id="homePhone_title">Home Phone</span></td>
            <td><input type="text" name="homePhone" id="homePhone" size="25" value="${patient.homePhone?if_exists}"/></td>
            <td class="label"><span id="officePhone_title">Office Phone</span></td>
            <td><input type="text" name="officePhone" id="officePhone" size="25" value="${patient.officePhone?if_exists}"/></td>
            <td class="label"><span id="mobileNumber_title">Mobile Phone</span><font color="red"> *</font></td>
            <td><input type="text" name="isdCode" id="isdCode" size="10" value="${patient.isdCode?if_exists}"/></td>
            <td><input type="text" name="mobilePhone" id="mobileNumber" size="25" value="${patient.mobilePhone?if_exists}" class="required"/></td>
          </tr>
          <tr>
            <td class="label"><span id="address1_title">Address1</span></td>
            <td><textarea name="address1" id="address" cols="25" rows="3" style="width: 190px; height: 65px;">${patient.address1?if_exists}</textarea></td>
            <td class="label"><span id="address2_title">Address2</span></td>
            <td><textarea name="address2" id="additionalAddress" cols="25" rows="3" style="width: 190px; height: 65px;">${patient.address2?if_exists}</textarea></td>
            <td class="label"><span id="city_title">City</span></td>
            <td>
              <span class="ui-widget">
                <select name="city" id="city" size="1">
                  <#if patient.city?exists>
                    <option selected="selected" value="${patient.city}">${patient.city}</option>
                    <option value="${patient.city}">---</option>
                  </#if>
                  <option></option>
                </select>
              </span>
            </td>
            <td class="label"><span id="postalCode_title">Postal Code</span></td>
            <td><input type="text" name="postalCode" id="postalCode" size="15" value="${patient.postalCode?if_exists}"/></td>
          </tr>
          <tr>
            <td class="label"><span id="state_title">Governorate</span></td>
            <td><input type="text" readonly="true" name="governorate" id="state" size="25" value="${patient.governorate?if_exists}"/></td>
            <td class="label"><span id="country_title">Country</span></td>
            <td colspan="4">
              <input type="text" readonly="true" name="country" id="country" size="25" value="${patient.country?if_exists}"/>
            </td>
            <#-- <td colspan="4" id="country_selectionField">
              <select name="country" id="country">
                <#if patient.country?exists>
                  <#assign defaultCountryGeoId = (patient.country)>
                  <option selected="selected" value="${defaultCountryGeoId}">
                <#else>
                  <#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
                  <option selected="selected" value="${defaultCountryGeoId}">
                </#if>
                  <#assign countryGeo = delegator.findByPrimaryKey("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId))>
                  ${countryGeo.get("geoName",locale)}
                </option>
                <option value="${defaultCountryGeoId}">---</option>
                ${screens.render("component://common/widget/CommonScreens.xml#countries")}
              </select>
            </td> -->
          </tr>
        </table>
      </div>
    </div>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar"></div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <#-- <td class="">&nbsp;</td> -->
            <td style="text-align:center">
              <input type="submit" name="createButton" value="${uiLabelMap.CommonSave}" class="btn btn-success"/>
              <a href="<@ofbizUrl>findPatients</@ofbizUrl>" class="btn btn-danger">${uiLabelMap.CommonCancel}</a>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </form>
  <script type="text/javascript">
    var form = document.editPatientForm;
    jQuery(form).validate();
  </script>
<#else>
<script language="JavaScript" type="text/javascript">
    function validate(selection) {
        var selectionType = selection.value;
        
        if(selectionType == "CIVIL_ID") {
            //civilId_title.style.display = '';
            civilId_textfield.style.display = '';
            //passport_title.style.display = 'none';
            passport_textField.style.display = 'none';
            expiryDate_title.style.display = 'none';
            expiryDate_dateField.style.display = 'none';
        }
        if(selectionType != "CIVIL_ID") {
            //civilId_title.style.display = 'none';
            civilId_textfield.style.display = 'none';
            //passport_title.style.display = '';
            passport_textField.style.display = '';
            expiryDate_title.style.display = '';
            expiryDate_dateField.style.display = '';
        }
    }
    function validateGender(selection) {
        var titleValue = selection.value;
        
        if(titleValue == "Mr" || titleValue == "Sr")
            $("#gender").val("M");
        if(titleValue == "Ms" || titleValue == "Miss" || titleValue == "Mrs" || titleValue == "Sra")
            $("#gender").val("F");
        if(titleValue == "Dr" || titleValue == "")
            $("#gender").val("");
    }
</script>
  <form name="patientRegistrationForm" id="patientRegistrationForm" method="post" action="<@ofbizUrl>registerPatient</@ofbizUrl>" class="basic-form">
    <input type="hidden" name="patientId" id="patientId"/>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar"></div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <td class=""><span id="select_title">&nbsp;</span></td>
            <td width="12%">
              <span class="ui-widget">
                <select name="selectionType" id="selectionType" onchange="javascript:validate(this);">
                  <option value="CIVIL_ID">Civil ID</option>
                  <option value="PASSPORT">Passport &#47; VISA</option>
                </select>
              </span>
            </td>
            <#-- <td class="" id="civilId_title"><span id="civilId_title">Civil ID</span></td> -->
            <td id="civilId_textfield"><input type="text" name="civilId" id="civilId" size="15" maxlength="12" class="civilidrange"/></td>
            <#-- <td class="" id="passport_title" style="display:none"><span id="passport_title">Passport &#47; VISA</span></td> -->
            <td id="passport_textField" style="display:none"><input type="text" name="passport" id="passport" size="25"/></td>
            <td id="expiryDate_title" class="label" width="7%" style="display:none"><span id="expiryDate_title">Expiry Date</span></td>
            <td id="expiryDate_dateField" colspan="4" style="display:none">
              <@htmlTemplate.renderDateTimeField name="expiryDate" className="date" alert="" 
                      title="Format: MM/dd/yyyy" size="15" maxlength="10" id="item1" dateType="date-time" shortDateInput=true 
                      timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                      hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="patientRegistrationForm"/>
            </td>
          </tr>
        </table>
      </div>
    </div>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar">Basic Information</div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <td class="label"><span id="title_title">Title</span></td>
            <td>
              <span class="ui-widget">
                <select name="title" id="title" onchange="javascript:validateGender(this);">
                  <option value="">&nbsp;</option>
                  <option value="Mr">Mr</option>
                  <option value="Mrs">Mrs</option>
                  <option value="Miss">Miss</option>
                  <option value="Ms">Ms</option>
                  <option value="Dr">Dr</option>
                  <option value="Sr">Sr</option>
                  <option value="Sra">Sra</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="firstName_title">First Name</span><font color="red"> *</font></td>
            <td><input type="text" name="firstName" id="firstName" size="25" class="required"/></td>
            <td class="label"><span id="secondName_title">Middle Name</span></td>
            <td><input type="text" name="secondName" id="secondName" size="25"/></td>
            <td class="label"><span id="thirdName_title">Last Name</span><font color="red"> *</font></td>
            <td colspan="4"><input type="text" name="thirdName" id="thirdName" size="25" class="required"/></td>
          </tr>
          <tr>
            <td class="label"><span id="dateOfBirth_title">Date Of Birth</span><font color="red"> *</font></td>
            <td id="dateOfBirth">
              <@htmlTemplate.renderDateTimeField name="dateOfBirth" id="dateOfBirth" value="" className="date required" alert="" 
                      title="Format: dd/MM/yyyy" size="15" maxlength="10" id="transferDate" dateType="date" shortDateInput=true 
                      timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                      hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="patientRegistrationForm"/>
            </td>
            <td class="label"><span id="gender_title">Gender</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="gender" id="gender" size="1" class="required">
                  <option value="">&nbsp;</option>
                  <option value="M">Male</option>
                  <option value="F">Female</option>
                </select>
              </span>
            </td>
            <#-- <td class="label"><span id="bloodGroup_title">Blood Group</span></td>
            <td>
              <span class="ui-widget">
                <select name="bloodGroup" id="bloodGroup" selected="selected" value="${patient.bloodgroup}" size="1">
                  <option value="">&nbsp;</option>
                  <option value="A">A</option>
                  <option value="B">B</option>
                  <option value="AB">AB</option>
                  <option value="O">O</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="rh_title">R H</span></td>
            <td colspan="7">
              <span class="ui-widget">
                <select name="rH" id="rh" size="1">
                  <option value="">&nbsp;</option>
                  <option value="&#43;">&#43;</option>
                  <option value="-">-</option>
                </select>
              </span>
            </td> -->
            <td class="label"><span id="maritalStatus_title">Marital Status</span></td>
            <td>
              <span class="ui-widget">
                <select name="maritalStatus" id="maritalStatus" size="1">
                  <option value="">&nbsp;</option>
                  <option value="ANNULLED">Annulled</option>
                  <option value="DIVORCED">Divorced</option>
                  <option value="DOMESTIC_PARTNER">Domestic Partner</option>
                  <option value="LEGALLY_SEPARATED">Legally Separated</option>
                  <option value="LIVING_TOGETHER">Living Together</option>
                  <option value="MARRIED">Married</option>
                  <option value="OTHER">Other</option>
                  <option value="SEPARATED">Separated</option>
                  <option value="SINGLE">Single</option>
                  <option value="UNMARRIED">Unmarried</option>
                  <option value="WIDOWED">Widowed</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="nationality_title">Nationality</span></td>
            <td><input type="text" name="nationality" id="nationality" size="25"/></td>
          </tr>
          <tr>
            <#-- <td class="label"><span id="religion_title">Religion</span></td>
            <td><input type="text" name="religion" id="religion" size="25"/></td> -->
            <td class="label"><span id="patientType_title">Patient Type</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="patientType" id="patientType" size="1" class="required">
                  <option value="CASH">Cash</option>
                  <option value="INSURANCE">Insurance</option>
                  <option value="CORPORATE">Corporate</option>
                </select>
              </span>
            </td>
            <td class="label">
              <span id="isStaff_title">Staff</span>
            </td>
            <td>
              <span class="ui-widget">
                <select name="isStaff" id="isStaff" size="1">
                  <option value="Yes">Yes</option>
                  <option value="No" selected="selected">No</option>
                </select>
              </span>
            </td>
          </tr>
        </table>
      </div>
    </div>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar">Communication Details</div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <td class="label"><span id="emailId_title">Email</span></td>
            <td><input type="text" name="emailAddress" id="emailId" size="25"/></td>
            <td class="label"><span id="homePhone_title">Home Phone</span></td>
            <td><input type="text" name="homePhone" id="homePhone" size="25"/></td>
            <td class="label"><span id="officePhone_title">Office Phone</span></td>
            <td><input type="text" name="officePhone" id="officePhone" size="25"/></td>
            <td class="label"><span id="mobileNumber_title">Mobile Phone</span><font color="red"> *</font></td>
            <td><input type="text" name="isdCode" id="isdCode" value="00965" size="10"/></td>
            <td><input type="text" name="mobilePhone" id="mobileNumber" size="25" class="required"/></td>
          </tr>
          <tr>
            <td class="label"><span id="address1_title">Address1</span></td>
            <td><textarea name="address1" id="address" cols="25" rows="3" style="width: 190px; height: 65px;"></textarea></td>
            <td class="label"><span id="address2_title">Address2</span></td>
            <td><textarea name="address2" id="additionalAddress" cols="25" rows="3" style="width: 190px; height: 65px;"></textarea></td>
            <td class="label"><span id="city_title">City</span></td>
            <td>
              <span class="ui-widget">
                <select name="city" id="city" size="1">
                  <option></option>
                </select>
              </span>
            </td>
            <td class="label"><span id="postalCode_title">Postal Code</span></td>
            <td><input type="text" name="postalCode" id="postalCode" size="15"/></td>
          </tr>
          <tr>
            <td class="label"><span id="state_title">Governorate</span></td>
            <td><input type="text" readonly="true" name="governorate" id="state" size="25"/></td>
            <td class="label"><span id="country_title">Country</span></td>
            <td colspan="4">
              <input type="text" readonly="true" name="country" id="country" size="25"/>
            </td>
            <#-- <td colspan="4">
              <select name="country" id="country">
                ${screens.render("component://common/widget/CommonScreens.xml#countries")}
                <#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
                <option selected="selected" value="${defaultCountryGeoId}">
                  <#assign countryGeo = delegator.findByPrimaryKey("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId))>
                  ${countryGeo.get("geoName",locale)}
                </option>
              </select> -->
            </td>
          </tr>
        </table>
      </div>
    </div>
    <div class="fieldgroup">
      <div class="fieldgroup-title-bar"></div>
      <div class="fieldgroup-body">
        <table cellspacing="0" class="basic-table">
          <tr>
            <#-- <td class="">&nbsp;</td> -->
            <td style="text-align:center">
              <input type="submit" name="createButton" value="${uiLabelMap.CommonSave}" class="btn btn-success"/>
              <a href="<@ofbizUrl>findPatients</@ofbizUrl>" class="btn btn-danger">${uiLabelMap.CommonCancel}</a>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </form>
  <script type="text/javascript">
    var form = document.patientRegistrationForm;
    jQuery(form).validate();
  </script>
</#if>