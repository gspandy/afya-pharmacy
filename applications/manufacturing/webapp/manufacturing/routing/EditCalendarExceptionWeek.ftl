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

<#if techDataCalendar?has_content>
<div class="screenlet">
  <div class="screenlet-title-bar">
    <h3>${uiLabelMap.ManufacturingEditCalendarExceptionWeekFor}&nbsp;
    <#if (techDataCalendar.description)?has_content>"${(techDataCalendar.get("description",locale))}"</#if>
    [${uiLabelMap.CommonId} ${techDataCalendar.calendarId?if_exists}]</h3>
  </div>
  <div class="screenlet-body">
    ${listCalendarExceptionWeekWrapper.renderFormString(context)}
  </div>
</div>
    <#if calendarExceptionWeek?has_content>
    <div class="screenlet">
      <div class="screenlet-title-bar">
        <h3>${uiLabelMap.PageTitleEditCalendarExceptionWeek}</h3>
      </div>
      <div class="screenlet-body">
        ${updateCalendarExceptionWeekWrapper.renderFormString(context)}
      </div>
    </div>
    </#if>
    <div class="screenlet">
      <div class="screenlet-title-bar">
        <h3>${uiLabelMap.PageTitleAddCalendarExceptionWeek}</h3>
      </div>
      <div class="screenlet-body">
	<form method="post"  action="/manufacturing/control/CreateCalendarExceptionWeek"  id="AddCalendarExceptionWeek" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="AddCalendarExceptionWeek">
	<input type="hidden" name="calendarId" value="${techDataCalendar.calendarId?if_exists}"/>
	 <table cellspacing="0" class="basic-table">
	  <tr>
	   <td class="label">Description</td>
	   <td><input type="text" name="description" size="25" id="AddCalendarExceptionWeek_description" autocomplete="off"/>
	</td>
	  </tr>
	  <tr>
	   <td class="label">Start of Exception Date</td>
	   <td>

	   <span class="view-calendar">
	   <input type="text" name="exceptionDateStart" title="Format: yyyy-MM-dd" size="10" maxlength="10" id="AddCalendarExceptionWeek_exceptionDateStart"/>
		 <script type="text/javascript">
             jQuery("#AddCalendarExceptionWeek_exceptionDateStart").datepicker({
                showTime: false,
		dateOnly:true,
                showOn: 'button',
                buttonImage: '',
                buttonText: '',
                buttonImageOnly: false,
                dateFormat: 'yy-mm-dd'
              });
      </script> * </span>

	   <#-- <a href="javascript:call_cal_notime(document.AddCalendarExceptionWeek.exceptionDateStart,'2012-07-12');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="View Calendar" title="View Calendar"/> </a> -->

	   </td>
	  </tr>
	  <tr>
	   <td class="label">Calendar Week ID</td>
	   <td><select name="calendarWeekId" id="AddCalendarExceptionWeek_calendarWeekId" size="1">

	   <option value="DEFAULT">8hours&#47;days</option><option value="SUPPLIER">8hours&#47;days, currently the Re-Order Process convert day to mms with 8h&#47;days</option></select></td>
	  </tr>
	  <tr>
	   <td class="label">&nbsp;</td>
	   <td colspan="4"><input type="submit" name="submitButton" value="Add" class="btn btn-success"/></td>
	  </tr>
	 </table>
	</form>
      <#--   ${addCalendarExceptionWeekWrapper.renderFormString(context)} -->
      </div>
    </div>
</#if>