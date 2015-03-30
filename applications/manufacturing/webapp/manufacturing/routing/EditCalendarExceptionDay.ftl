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
    <h3>${uiLabelMap.ManufacturingEditCalendarExceptionDayFor}&nbsp;
    <#if (techDataCalendar.description)?has_content>"${(techDataCalendar.get("description",locale))}"</#if>
    [${uiLabelMap.CommonId} ${techDataCalendar.calendarId?if_exists}]</h3>
  </div>
  <div class="screenlet-body">
    ${listCalendarExceptionDayWrapper.renderFormString(context)}
  </div>
</div>
<#if calendarExceptionDay?has_content>
<div class="screenlet">
  <div class="screenlet-title-bar">
    <h3>${uiLabelMap.PageTitleEditCalendarExceptionWeek}</h3>
  </div>
  <div class="screenlet-body">
    ${updateCalendarExceptionDayWrapper.renderFormString(context)}
  </div>
</div>
</#if>
<div class="screenlet">
  <div class="screenlet-title-bar">
    <h3>${uiLabelMap.PageTitleAddCalendarExceptionWeek}</h3>
  </div>
  <div class="screenlet-body">
  <#--  ${addCalendarExceptionDayWrapper.renderFormString(context)} -->
  <form method="post"  action="/manufacturing/control/CreateCalendarExceptionDay"  id="AddCalendarExceptionDay" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="AddCalendarExceptionDay">
<input type="hidden" name="calendarId" value="${techDataCalendar.calendarId?if_exists}"/>
 <table cellspacing="0" class="basic-table">
  <tr>
   <td class="label">Description</td>
   <td><input type="text" name="description" size="25" id="AddCalendarExceptionDay_description" autocomplete="off"/>
</td>
  </tr>
  <tr>
   <td class="label">Exception Date Start Time</td>
   <td>
   <span class="view-calendar"><input type="text" name="exceptionDateStartTime" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="AddCalendarExceptionDay_exceptionDateStartTime"/>
   <script type="text/javascript">
             jQuery("#AddCalendarExceptionDay_exceptionDateStartTime").datetimepicker({
                showSecond: true,
                timeFormat: 'hh:mm:ss',
                stepHour: 1,
                stepMinute: 5,
                stepSecond: 10,
                showOn: 'button',
                buttonImage: '',
                buttonText: '',
                buttonImageOnly: false,
                dateFormat: 'yy-mm-dd'
              });
      </script> * </span>
 <#--  <a href="javascript:call_cal(document.AddCalendarExceptionDay.exceptionDateStartTime,'2012-07-12%2016&#58;53&#58;28.775');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="View Calendar" title="View Calendar"/></a>*</td>  -->
  </tr>
  <tr>
   <td class="label">Manufacturing Calendar Capacity</td>
   <td><input type="text" name="exceptionCapacity" size="25" id="AddCalendarExceptionDay_exceptionCapacity" autocomplete="off"/>
</td>
  </tr>
  <tr>
   <td class="label">&nbsp;</td>
   <td colspan="4"><input type="submit" name="submitButton" value="Add" class="btn btn-success"/></td>
  </tr>
 </table>
</form>
  </div>
</div>
</#if>