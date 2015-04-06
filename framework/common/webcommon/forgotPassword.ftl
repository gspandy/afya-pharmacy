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

<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>

<center>
  <div class="screenlet login-screenlet">
    <div class="screenlet-title-bar">
      <h3>${uiLabelMap.CommonForgotYourPassword}?</h3>
    </div>
    <div class="screenlet-body">
      <form method="post" action="<@ofbizUrl>forgotPassword${previousParams?if_exists}</@ofbizUrl>" name="forgotpassword">
        <table class="basic-table" cellspacing="0">
          
          <tr>
            <td class="label">${uiLabelMap.CommonUsername}</td>
            <td><input type="text" size="20" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"/></td>
          </tr>
          
          <#if ("Y" == useMultitenant)>
          <tr>
            <td class="label">Tenant ID</td>
            <td>
         		<input type="text"  name="tenantId" value="${parameters.tenantId?if_exists}" size="20" class="login_input" /><br /><br />
            </td>
          </tr>
          </#if>
          
          <tr>
            <td colspan="2" align="center">
              <input type="submit"   name="GET_PASSWORD_HINT" class="buttontext"  value="${uiLabelMap.CommonGetPasswordHint}"/>&nbsp;&nbsp;&nbsp;<input type="submit" name="EMAIL_PASSWORD" class="buttontext" value="${uiLabelMap.CommonEmailPassword}"/>
            </td>
          </tr>
          <td colspan="2" align="center">
          <a href='<@ofbizUrl>authview</@ofbizUrl>' class="buttontext">${uiLabelMap.CommonGoBack}</a>
            </td>
        </table>
        <!-- <input type="submit" href='<@ofbizUrl>main</@ofbizUrl>' class="btn btn-primary" value="${uiLabelMap.CommonGoBack}"/> -->
       <#-- <a href='<@ofbizUrl>authview</@ofbizUrl>' class="btn btn-primary">${uiLabelMap.CommonGoBack}</a>-->
        <input type="hidden" name="JavaScriptEnabled" value="N"/>
      </form>
    </div>
  </div>
</center>