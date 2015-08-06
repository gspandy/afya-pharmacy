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
<#if contractId?has_content>

  <div class="screenlet" id="referralContract">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">Edit Referral Contract [${uiLabelMap.CommonId}:${contractId}]</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
      <script language="JavaScript" type="text/javascript">
        function validatePaymentMode(selection) {
            var paymentMode = selection.value;
      
            if(paymentMode == "PERCENTAGE_OF_BILL") {
                percentageOnBill_title.style.display = '';
                percentageOnBill_textField.style.display = '';
                addServiceContract.style.display = 'none';
                serviceContractList.style.display = 'none';
                currencyUomId_title.style.display = 'none';
                currencyUomId_selectionField.style.display = 'none';
            } else if(paymentMode == "PERCENTAGE_SERVICE_ITEM") {
                percentageOnBill_title.style.display = 'none';
                percentageOnBill_textField.style.display = 'none';
                addServiceContract.style.display = '';
                paymentPercentage_title.style.display = '';
                paymentPercentage_textField.style.display = '';
                paymentAmount_title.style.display = 'none';
                paymentAmount_textField.style.display = 'none';
                currencyUomId_title.style.display = 'none';
                currencyUomId_selectionField.style.display = 'none';
                serviceContractList.style.display = '';
                $("#percentageOnBill").val("");
            } else {
                percentageOnBill_title.style.display = 'none';
                percentageOnBill_textField.style.display = 'none';
                addServiceContract.style.display = '';
                paymentPercentage_title.style.display = 'none';
                paymentPercentage_textField.style.display = 'none';
                paymentAmount_title.style.display = '';
                paymentAmount_textField.style.display = '';
                currencyUomId_title.style.display = '';
                currencyUomId_selectionField.style.display = '';
                serviceContractList.style.display = '';
                $("#percentageOnBill").val("");
            }
        }
        function validateReferral(selection) {
            var referral = selection.value;
            //alert("referral= "+referral);
            var splitReferral = referral.split(',');
            var referralId = splitReferral[0];
            var referralName = splitReferral[1];
            var clinicId = splitReferral[2];
            var clinicName = splitReferral[3];
            $("#referralId").val(referralId);
            $("#referralName").val(referralName);
            $("#clinicId").val(clinicId);
            $("#clinicName").val(clinicName);
            //alert("referralId = "+referralId+", referralName= "+referralName+", clinicId= "+clinicId+", clinicName= "+clinicName);
        }

        function getDates(contractFromDate,contractThruDate) {
            var contractFromDate = contractFromDate.value == "" ? null : contractFromDate.value;
            var contractThruDate = contractThruDate.value == "" ? null : contractThruDate.value;
            var dt1 ;
            var mon1 ;
            var yr1 ;
            var dt2 ;
            var mon2 ;
            var yr2;
            
            if(contractFromDate!=null) {
                dt1 = parseInt(contractFromDate.substring(8,10),10);
                mon1 = parseInt(contractFromDate.substring(5,7),10);
                yr1 = parseInt(contractFromDate.substring(0,4),10);
            }
            
            if(contractThruDate!=null) { 
                dt2 = parseInt(contractThruDate.substring(8,10),10);
                mon2 = parseInt(contractThruDate.substring(5,7),10);
                yr2 = parseInt(contractThruDate.substring(0,4),10);
            }
            
            contractDate = new Date(yr1,mon1,dt1);
            expiryDate = new Date(yr2,mon2,dt2);
            
            if(contractDate > expiryDate){
                alert('Expiry Date should be Greater than Contract Date.');
                return false;
            }

        }
      </script>
      <form name="editReferralContractForm" id="editReferralContractForm" method="post" action="<@ofbizUrl>updateReferralContract</@ofbizUrl>" class="basic-form">
        <input type="hidden" name="contractId" id="contractId" value="${contractId}"/>
        <input type="hidden" name="referralId" id="referralId" value="${contract.referralId}"/>
        <input type="hidden" name="clinicId" id="clinicId" value="${contract.clinicId}"/>
        <input type="hidden" name="referralName" id="referralName" value="${contract.referralName}"/>
        <table cellspacing="0" class="basic-table">
          <tr>
            <td class="label"><span id="referralType_title">Referral Type</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="referralType" id="referralType" size="1" class="required">
                  <#if contract.referralType?exists>
                    <#if "CONSULTANT" == contract.referralType>
                      <option selected="selected" value="${contract.referralType}">Consultant</option>
                    <#elseif "PHARMACY" == contract.referralType>
                      <option selected="selected" value="${contract.referralType}">Pharmacy</option>
                    </#if>
                    <option value="${contract.referralType}">---</option>
                  </#if>
                  <#-- <option value="">&nbsp;</option>
                  <option value="PHARMACY">Pharmacy</option> -->
                  <option value="CONSULTANT">Consultant</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="referral_title">Referral Name</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="referral" id="referral" size="1" class="required" onchange="javascript:validateReferral(this);">
                  <#if contract.referralName?exists>
                    <#assign referral = contract.referralId+","+contract.referralName+","+contract.clinicId+","+contract.clinicName/>
                    <option selected="selected" value="${referral}">${contract.referralName}</option>
                    <option value="${referral}">---</option>
                  </#if>
                  <option></option>
                </select>
              </span>
            </td>
            <td class="label"><span id="clinicName_title">Clinic</span><font color="red"> *</font></td>
            <td>
              <input type="text" readonly="true" name="clinicName" id="clinicName" size="25" value="${contract.clinicName?if_exists}" class="required"/>
            </td>
          </tr>
          <tr>
            <td class="label"><span id="contractFromDate_title">Contract Date</span><font color="red"> *</font></td>
            <td id="contractFromDate_dateField">
              <#if contract.contractFromDate?exists>
                <@htmlTemplate.renderDateTimeFieldJsMethod name="contractFromDate" id="contractFromDate" value='${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(contract.contractFromDate)}' className="date required" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editReferralContractForm"
                        javaScriptMethod=""/>
              <#else>
                <@htmlTemplate.renderDateTimeField name="contractFromDate" id="contractFromDate" value="${value!''}" className="date required" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editReferralContractForm"/>
              </#if>
            </td>
            <td class="label"><span id="contractThruDate_title">Expiry Date</span><font color="red"> *</font></td>
            <td id="contractThruDate_dateField">
              <#if contract.contractThruDate?exists>
                <@htmlTemplate.renderDateTimeFieldJsMethod name="contractThruDate" id="contractThruDate" value='${Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(contract.contractThruDate)}' className="date required" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editReferralContractForm"
                        javaScriptMethod=""/>
              <#else>
                <@htmlTemplate.renderDateTimeField name="contractThruDate" id="contractThruDate" value="${value!''}" className="date required" alert="" 
                        title="Format: MM/dd/yyyy" size="15" maxlength="10" dateType="date-time" shortDateInput=true 
                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="editReferralContractForm"/>
              </#if>
            </td>
            <td class="label"><span id="contractStatus_title">Contract Status</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="contractStatus" id="contractStatus" class="required">
                  <#if contract.contractStatus?exists>
                    <#if "ACTIVE" == contract.contractStatus>
                      <option selected="selected" value="${contract.contractStatus}">Active</option>
                    <#elseif "INACTIVE" == contract.contractStatus>
                      <option selected="selected" value="${contract.contractStatus}">Inactive</option>
                    <#elseif "EXPIRED" == contract.contractStatus>
                      <option selected="selected" value="${contract.contractStatus}">Expired</option>
                    </#if>
                    <option value="${contract.contractStatus}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                  <option value="EXPIRED">Expired</option>
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td class="label"><span id="paymentMode_title">Payment Mode</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="paymentMode" id="paymentMode" class="required" onchange="javascript:validatePaymentMode(this);">
                  <#if contract.paymentMode?exists>
                    <#if "PERCENTAGE_OF_BILL" == contract.paymentMode>
                      <option selected="selected" value="${contract.paymentMode}">% of Bill Amount</option>
                    <#elseif "PERCENTAGE_SERVICE_ITEM" == contract.paymentMode>
                      <option selected="selected" value="${contract.paymentMode}">% of each Service</option>
                    <#elseif "FIX_AMOUNT_PER_SERVICE" == contract.paymentMode>
                      <option selected="selected" value="${contract.paymentMode}">Fixed Amount for each Service</option>
                    </#if>
                    <option value="${contract.paymentMode}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="PERCENTAGE_OF_BILL">% of Bill Amount</option>
                  <option value="PERCENTAGE_SERVICE_ITEM">% of each Service</option>
                  <option value="FIX_AMOUNT_PER_SERVICE">Fixed Amount for each Service</option>
                </select>
              </span>
            </td>
            <td class="label"><span id="paymentPoint_title">Payment Point</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="paymentPoint" id="paymentPoint" class="required">
                  <#if contract.paymentPoint?exists>
                    <#if "ON_BILL" == contract.paymentPoint>
                      <option selected="selected" value="${contract.paymentPoint}">On Billing</option>
                    <#elseif "ON_PARTIAL_RECEIPT" == contract.paymentPoint>
                      <option selected="selected" value="${contract.paymentPoint}">On Partial Receipt</option>
                    <#elseif "ON_FULL_RECEIPT" == contract.paymentPoint>
                      <option selected="selected" value="${contract.paymentPoint}">On Full Receipt</option>
                    </#if>
                    <option value="${contract.paymentPoint}">---</option>
                  </#if>
                  <option value="">&nbsp;</option>
                  <option value="ON_BILL">On Billing</option>
                  <option value="ON_PARTIAL_RECEIPT">On Partial Receipt</option>
                  <option value="ON_FULL_RECEIPT">On Full Receipt</option>
                </select>
              </span>
            </td>
            <#if contract?has_content && "PERCENTAGE_OF_BILL" == contract.paymentMode>
              <td id="percentageOnBill_title" class="label"><span id="percentageOnBill_title">Percentage</span><font color="red"> *</font></td>
              <td id="percentageOnBill_textField"><input type="text" name="percentageOnBill" id="percentageOnBill" size="8" value="${contract.percentageOnBill?if_exists}" class="percentage required"/></td>
            <#else>
              <td id="percentageOnBill_title" class="label" style="display:none;"><span id="percentageOnBill_title">Percentage</span><font color="red"> *</font></td>
              <td id="percentageOnBill_textField" style="display:none"><input type="text" name="percentageOnBill" id="percentageOnBill" size="8" value="${contract.percentageOnBill?if_exists}" class="percentage required"/></td>
            </#if>
          </tr>
        </table>
        <div class="fieldgroup">
          <div class="fieldgroup-title-bar"></div>
            <div class="fieldgroup-body">
              <table cellspacing="0" class="basic-table">
                <tr>
                  <td style="text-align:center">
                    <input type="submit" name="editButton" value="${uiLabelMap.CommonSave}" class="btn btn-success" onClick="javascript:return getDates(contractFromDate,contractThruDate);"/>
                    <a href="<@ofbizUrl>listReferralContracts</@ofbizUrl>" class="btn btn-danger">${uiLabelMap.CommonCancel}</a>
                  </td>
                </tr>
              </table>
            </div>
          </div>
        </div>
      </form>
      <script type="text/javascript">
        var form = document.editReferralContractForm;
        jQuery(form).validate();
      </script>
    </div>

    <#if contract.paymentMode?exists &&  ("FIX_AMOUNT_PER_SERVICE" == contract.paymentMode || "PERCENTAGE_SERVICE_ITEM" == contract.paymentMode)>
      <div class="screenlet" id="addServiceContract">
        <div class="screenlet-title-bar">
          <ul>
            <li class="h3">Add Referral Service Contract</li>
          </ul>
          <br class="clear"/>
        </div>
        <div class="screenlet-body">
          <form name="addReferralServiceContractForm" id="addReferralServiceContractForm" method="post" action="<@ofbizUrl>addReferralServiceContract</@ofbizUrl>" class="basic-form">
            <input type="hidden" name="contractId" id="contractId" value="${contractId}"/>
            <table cellspacing="0" class="basic-table">
              <tr>
                <td class="label"><span id="productCategoryId_title">Product Category</span><font color="red"> *</font></td>
                <td width="26%">
                  <span class="ui-widget">
                    <#assign productCategoryList = delegator.findByAnd("ProductCategory", {"productCategoryTypeId" : "CATALOG_CATEGORY"})>
                    <select name="productCategoryId" id="productCategoryId" size="1" class="required">
                      <option></option>
                      <#if productCategoryList?has_content>
                        <#list productCategoryList as productCategory>
                          <option value="${productCategory.productCategoryId}">${productCategory.get("categoryName")}</option>
                        </#list>
                      </#if>
                    </select>
                  </span>
                </td>
                <#if "FIX_AMOUNT_PER_SERVICE" == contract.paymentMode>
                  <td id="paymentAmount_title" class="label"><span id="paymentAmount_title">Amount</span><font color="red"> *</font></td>
                  <td id="paymentAmount_textField" width="28%"><input type="text" name="paymentAmount" id="paymentAmount" class="currency required"/></td>
                  <td id="currencyUomId_title" class="label"><span id="currencyUomId_title">Currency</span><font color="red"> *</font></td>
                  <td id="currencyUomId_selectionField">
                    <span class="ui-widget">
                      <select name="currencyUomId" id="currencyUomId" size="1" class="required">
                        <#if currencyUomList?has_content>
                          <#list currencyUomList as currencyUom>
                            <option value="${currencyUom.uomId}" <#if currencyUom.uomId.equals(defaultCurrencyUomId)>selected="selected"</#if>>${currencyUom.get("description")}</option>
                          </#list>
                        </#if>
                      </select>
                    </span>
                  </td>
                  <td id="paymentPercentage_title" class="label" style="display:none;"><span id="paymentPercentage_title">Percentage</span><font color="red"> *</font></td>
                  <td id="paymentPercentage_textField" style="display:none;"><input type="text" name="paymentPercentage" id="paymentPercentage" size="8" class="percentage required"/></td>
                <#elseif "PERCENTAGE_SERVICE_ITEM" == contract.paymentMode>
                  <td id="paymentAmount_title" class="label" style="display:none;"><span id="paymentAmount_title">Amount</span><font color="red"> *</font></td>
                  <td id="paymentAmount_textField" width="28%" style="display:none;"><input type="text" name="paymentAmount" id="paymentAmount" class="currency required"/></td>
                  <td id="currencyUomId_title" class="label" style="display:none;"><span id="currencyUomId_title">Currency</span><font color="red"> *</font></td>
                  <td id="currencyUomId_selectionField" style="display:none;">
                    <span class="ui-widget">
                      <select name="currencyUomId" id="currencyUomId" size="1" class="required">
                        <#if currencyUomList?has_content>
                          <#list currencyUomList as currencyUom>
                            <option value="${currencyUom.uomId}" <#if currencyUom.uomId.equals(defaultCurrencyUomId)>selected="selected"</#if>>${currencyUom.get("description")}</option>
                          </#list>
                        </#if>
                      </select>
                    </span>
                  </td>
                  <td id="paymentPercentage_title" class="label"><span id="paymentPercentage_title">Percentage</span><font color="red"> *</font></td>
                  <td id="paymentPercentage_textField"><input type="text" name="paymentPercentage" id="paymentPercentage" size="8" class="percentage required"/></td>
                <#else>
                  <td id="paymentAmount_title" class="label"><span id="paymentAmount_title">Amount</span><font color="red"> *</font></td>
                  <td id="paymentAmount_textField" width="28%"><input type="text" name="paymentAmount" id="paymentAmount" class="currency required"/></td>
                  <td id="currencyUomId_title" class="label"><span id="currencyUomId_title">Currency</span><font color="red"> *</font></td>
                  <td id="currencyUomId_selectionField">
                    <span class="ui-widget">
                      <select name="currencyUomId" id="currencyUomId" size="1" class="required">
                        <#if currencyUomList?has_content>
                          <#list currencyUomList as currencyUom>
                            <option value="${currencyUom.uomId}" <#if currencyUom.uomId.equals(defaultCurrencyUomId)>selected="selected"</#if>>${currencyUom.get("description")}</option>
                          </#list>
                        </#if>
                      </select>
                    </span>
                  </td>
                  <td id="paymentPercentage_title" class="label"><span id="paymentPercentage_title">Percentage</span><font color="red"> *</font></td>
                  <td id="paymentPercentage_textField"><input type="text" name="paymentPercentage" id="paymentPercentage" size="8" class="percentage required"/></td>
                </#if>
              </tr>
            </table>
            <div class="fieldgroup">
              <div class="fieldgroup-title-bar"></div>
                <div class="fieldgroup-body">
                  <table cellspacing="0" class="basic-table">
                    <tr>
                      <td style="text-align:center">
                        <input type="submit" name="addButton" value="${uiLabelMap.CommonAdd}" class="btn btn-success"/>
                      </td>
                    </tr>
                  </table>
                </div>
              </div>
            </div>
          </form>
          <script type="text/javascript">
            var form = document.addReferralServiceContractForm;
            jQuery(form).validate();
          </script>
        </div>
      </div>
      <div class="screenlet" id="serviceContractList">
        <div class="screenlet-title-bar">
          <ul>
            <li class="h3">Service Contract List</li>
          </ul>
          <br class="clear"/>
        </div>
        <div class="screenlet-body">
          <div>
            <table class="basic-table" cellspacing="0">
              <tr class="header-row">
                <td width="15%">Category Name</td>
                <td width="15%">Payment Amount</td>
                <td width="15%">Payment Percentage</td>
                <td width="10%">Action</td>
              </tr>
              <#if referralContractServiceList?has_content>
                <#list referralContractServiceList as serviceContract>
                  <#assign productCategory = serviceContract.getRelatedOne("ProductCategory")>
                  <tr>
                    <td>${productCategory.categoryName?if_exists}</td>
                    <td>
                      <#if serviceContract.paymentAmount?exists>
                        <@ofbizCurrency amount=serviceContract.paymentAmount?default(0) isoCode=serviceContract.currencyUomId/>
                      </#if>
                    </td>
                    <td><#if serviceContract.paymentPercentage?exists>${serviceContract.paymentPercentage?string("#,##0.000")}</#if></td>
                    
                    <td><a href="<@ofbizUrl>removeReferralServiceContract?contractId=${serviceContract.contractId}&productCategoryId=${serviceContract.productCategoryId}</@ofbizUrl>" onclick="return confirm('Do you want to remove?')" class="btn btn-mini btn-danger">${uiLabelMap.CommonRemove}</a></td>
                  </tr>
                </#list>
              </#if>
            </table>
          </div>
        </div>
      </div>
    <#else>
      <div class="screenlet" id="addServiceContract" style="display:none;">
        <div class="screenlet-title-bar">
          <ul>
            <li class="h3">Add Referral Service Contract</li>
          </ul>
          <br class="clear"/>
        </div>
        <div class="screenlet-body">
          <form name="addReferralServiceContractForm" id="addReferralServiceContractForm" method="post" action="<@ofbizUrl>addReferralServiceContract</@ofbizUrl>" class="basic-form">
            <input type="hidden" name="contractId" id="contractId" value="${contractId}"/>
            <table cellspacing="0" class="basic-table">
              <tr>
                <td class="label"><span id="productCategoryId_title">Product Category</span><font color="red"> *</font></td>
                <td width="26%">
                  <span class="ui-widget">
                    <#assign productCategoryList = delegator.findByAnd("ProductCategory", {"productCategoryTypeId" : "CATALOG_CATEGORY"})>
                    <select name="productCategoryId" id="productCategoryId" size="1" class="required">
                      <option></option>
                      <#if productCategoryList?has_content>
                        <#list productCategoryList as productCategory>
                          <option value="${productCategory.productCategoryId}">${productCategory.get("categoryName")}</option>
                        </#list>
                      </#if>
                    </select>
                  </span>
                </td>
                <td id="paymentAmount_title" class="label"><span id="paymentAmount_title">Amount</span><font color="red"> *</font></td>
                <td id="paymentAmount_textField" width="28%"><input type="text" name="paymentAmount" id="paymentAmount" class="currency required"/></td>
                <td id="currencyUomId_title" class="label"><span id="currencyUomId_title">Currency</span><font color="red"> *</font></td>
                <td id="currencyUomId_selectionField">
                  <span class="ui-widget">
                    <select name="currencyUomId" id="currencyUomId" size="1" class="required">
                      <#if currencyUomList?has_content>
                        <#list currencyUomList as currencyUom>
                          <option value="${currencyUom.uomId}" <#if currencyUom.uomId.equals(defaultCurrencyUomId)>selected="selected"</#if>>${currencyUom.get("description")}</option>
                        </#list>
                      </#if>
                    </select>
                  </span>
                </td>
                <td id="paymentPercentage_title" class="label"><span id="paymentPercentage_title">Percentage</span><font color="red"> *</font></td>
                <td id="paymentPercentage_textField"><input type="text" name="paymentPercentage" id="paymentPercentage" size="8" class="percentage required"/></td>
              </tr>
            </table>
            <div class="fieldgroup">
              <div class="fieldgroup-title-bar"></div>
                <div class="fieldgroup-body">
                  <table cellspacing="0" class="basic-table">
                    <tr>
                      <td style="text-align:center">
                        <input type="submit" name="addButton" value="${uiLabelMap.CommonAdd}" class="btn btn-success"/>
                      </td>
                    </tr>
                  </table>
                </div>
              </div>
            </div>
          </form>
          <script type="text/javascript">
            var form = document.addReferralServiceContractForm;
            jQuery(form).validate();
          </script>
        </div>
      </div>
      <div class="screenlet" id="serviceContractList" style="display:none;">
        <div class="screenlet-title-bar">
          <ul>
            <li class="h3">Service Contract List</li>
          </ul>
          <br class="clear"/>
        </div>
        <div class="screenlet-body">
          <div>
            <table class="basic-table" cellspacing="0">
              <tr class="header-row">
                <td width="15%">Category Name</td>
                <td width="15%">Payment Amount</td>
                <td width="15%">Payment Percentage</td>
                <td width="10%">Action</td>
              </tr>
              <#if referralContractServiceList?has_content>
                <#list referralContractServiceList as serviceContract>
                  <#assign productCategory = serviceContract.getRelatedOne("ProductCategory")>
                  <tr>
                    <td>${productCategory.categoryName?if_exists}</td>
                    <td>
                      <#if serviceContract.paymentAmount?exists>
                        <@ofbizCurrency amount=serviceContract.paymentAmount?default(0) isoCode=serviceContract.currencyUomId/>
                      </#if>
                    </td>
                    <td><#if serviceContract.paymentPercentage?exists>${serviceContract.paymentPercentage?string("#,##0.000")}</#if></td>
                    <td><a href="<@ofbizUrl>removeReferralServiceContract?contractId=${serviceContract.contractId}&productCategoryId=${serviceContract.productCategoryId}</@ofbizUrl>" onclick="return confirm('Do you want to remove?')" class="btn btn-mini btn-danger">${uiLabelMap.CommonRemove}</a></td>
                  </tr>
                </#list>
              </#if>
            </table>
          </div>
        </div>
      </div>
    </#if>

  </div>

<#else>

  <div class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">Create Referral Contract</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
      <script language="JavaScript" type="text/javascript">
        function validatePaymentMode(selection) {
            var paymentMode = selection.value;
      
            if(paymentMode == "PERCENTAGE_OF_BILL") {
                percentageOnBill_title.style.display = '';
                percentageOnBill_textField.style.display = '';
            } else {
                percentageOnBill_title.style.display = 'none';
                percentageOnBill_textField.style.display = 'none';
                $("#percentageOnBill").val("");
            }
        }
        function validateReferral(selection) {
            var referral = selection.value;
            //alert("referral= "+referral);
            var splitReferral = referral.split(',');
            var referralId = splitReferral[0];
            var referralName = splitReferral[1];
            var clinicId = splitReferral[2];
            var clinicName = splitReferral[3];
            $("#referralId").val(referralId);
            $("#referralName").val(referralName);
            $("#clinicId").val(clinicId);
            $("#clinicName").val(clinicName);
            //alert("referralId = "+referralId+", referralName= "+referralName+", clinicId= "+clinicId+", clinicName= "+clinicName);
        }

        function getDates(contractFromDate,contractThruDate) {
            var contractFromDate = contractFromDate.value == "" ? null : contractFromDate.value;
            var contractThruDate = contractThruDate.value == "" ? null : contractThruDate.value;
            var dt1 ;
            var mon1 ;
            var yr1 ;
            var dt2 ;
            var mon2 ;
            var yr2;
            
            if(contractFromDate!=null) {
                dt1 = parseInt(contractFromDate.substring(8,10),10);
                mon1 = parseInt(contractFromDate.substring(5,7),10);
                yr1 = parseInt(contractFromDate.substring(0,4),10);
            }
            
            if(contractThruDate!=null) { 
                dt2 = parseInt(contractThruDate.substring(8,10),10);
                mon2 = parseInt(contractThruDate.substring(5,7),10);
                yr2 = parseInt(contractThruDate.substring(0,4),10);
            }
            
            contractDate = new Date(yr1,mon1,dt1);
            expiryDate = new Date(yr2,mon2,dt2);
            
            if(contractDate > expiryDate){
                alert('Expiry Date should be Greater than Contract Date.');
                return false;
            }

        }
      </script>
      <form name="createReferralContractForm" id="createReferralContractForm" method="post" action="<@ofbizUrl>createReferralContract</@ofbizUrl>" class="basic-form">
        <input type="hidden" name="contractId" id="contractId"/>
        <input type="hidden" name="referralId" id="referralId"/>
        <input type="hidden" name="clinicId" id="clinicId"/>
        <input type="hidden" name="referralName" id="referralName"/>
        <table cellspacing="0" class="basic-table">
          <tr>
            <td class="label"><span id="referralType_title">Referral Type</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="referralType" id="referralType" size="1" class="required">
                  <#-- <option value="">&nbsp;</option> -->
                  <option value="CONSULTANT">Consultant</option>
                  <#-- <option value="PHARMACY">Pharmacy</option> -->
                </select>
              </span>
            </td>
            <td class="label"><span id="referral_title">Referral Name</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="referral" id="referral" size="1" class="required" onchange="javascript:validateReferral(this);">
                  <option></option>
                </select>
              </span>
            </td>
            <td class="label"><span id="clinicName_title">Clinic</span><font color="red"> *</font></td>
            <td>
              <input type="text" readonly="true" name="clinicName" id="clinicName" size="25" class="required"/>
            </td>
          </tr>
          <tr>
            <td class="label"><span id="contractFromDate_title">Contract Date</span><font color="red"> *</font></td>
            <td id="contractFromDate_dateField">
              <@htmlTemplate.renderDateTimeField name="contractFromDate" id="contractFromDate" value="" className="date required" alert="" 
                      title="Format: dd/MM/yyyy" size="15" maxlength="10" dateType="date-time" shortDateInput=true 
                      timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                      hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="createReferralContractForm"/>
            </td>
            <td class="label"><span id="contractThruDate_title">Expiry Date</span><font color="red"> *</font></td>
            <td id="contractThruDate_dateField">
              <@htmlTemplate.renderDateTimeField name="contractThruDate" id="contractThruDate" value="" className="date required" alert="" 
                      title="Format: dd/MM/yyyy" size="15" maxlength="10" dateType="date-time" shortDateInput=true 
                      timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                      hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="createReferralContractForm"/>
            </td>
            <td class="label"><span id="contractStatus_title">Contract Status</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="contractStatus" id="contractStatus" class="required">
                  <option value="">&nbsp;</option>
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                  <#-- <#if contractStatusList?has_content>
                    <#list contractStatusList as contractStatus>
                      <option value="${contractStatus.statusId}">${contractStatus.get("description")}</option>
                    </#list>
                  </#if> -->
                </select>
              </span>
            </td>
          </tr>
          <tr>
            <td class="label"><span id="paymentMode_title">Payment Mode</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="paymentMode" id="paymentMode" class="required" onchange="javascript:validatePaymentMode(this);">
                  <option value="">&nbsp;</option>
                  <option value="PERCENTAGE_OF_BILL">% of Bill Amount</option>
                  <option value="PERCENTAGE_SERVICE_ITEM">% of each Service</option>
                  <option value="FIX_AMOUNT_PER_SERVICE">Fixed Amount for each Service</option>
                  <#-- <#if paymentModeList?has_content>
                    <#list paymentModeList as paymentMode>
                      <option value="${paymentMode.paymentTypeId}">${paymentMode.get("description")}</option>
                    </#list>
                  </#if> -->
                </select>
              </span>
            </td>
            <td class="label"><span id="paymentPoint_title">Payment Point</span><font color="red"> *</font></td>
            <td>
              <span class="ui-widget">
                <select name="paymentPoint" id="paymentPoint" class="required">
                  <option value="">&nbsp;</option>
                  <option value="ON_BILL">On Billing</option>
                  <option value="ON_PARTIAL_RECEIPT">On Partial Receipt</option>
                  <option value="ON_FULL_RECEIPT">On Full Receipt</option>
                  <#-- <#if paymentPointList?has_content>
                    <#list paymentPointList as paymentPoint>
                      <option value="${paymentPoint.paymentTypeId}">${paymentPoint.get("description")}</option>
                    </#list>
                  </#if> -->
                </select>
              </span>
            </td>
            <td id="percentageOnBill_title" class="label" style="display:none;"><span id="percentageOnBill_title">Percentage</span><font color="red"> *</font></td>
            <td id="percentageOnBill_textField" style="display:none"><input type="text" name="percentageOnBill" id="percentageOnBill" class="percentage required"/></td>
          </tr>
        </table>
        <div class="fieldgroup">
          <div class="fieldgroup-title-bar"></div>
            <div class="fieldgroup-body">
              <table cellspacing="0" class="basic-table">
                <tr>
                  <td style="text-align:center">
                    <input type="submit" name="createButton" value="${uiLabelMap.CommonSave}" class="btn btn-success" onClick="javascript:return getDates(contractFromDate,contractThruDate);"/>
                    <a href="<@ofbizUrl>listReferralContracts</@ofbizUrl>" class="btn btn-danger">${uiLabelMap.CommonCancel}</a>
                  </td>
                </tr>
              </table>
            </div>
          </div>
        </div>
      </form>
      <script type="text/javascript">
        var form = document.createReferralContractForm;
        jQuery(form).validate();
      </script>
    </div>
  </div>
  
</#if>