<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>

<#if requestAttributes.uiLabelMap?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if username != "">
  <#assign focusName = false>
<#else>
  <#assign focusName = true>
</#if>

<#if !errorMessage?has_content>
  <#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>
<#if !errorMessageList?has_content>
  <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !eventMessage?has_content>
  <#assign eventMessage = requestAttributes._EVENT_MESSAGE_?if_exists>
</#if>
<#if !eventMessageList?has_content>
  <#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_?if_exists>
</#if>

<#if (errorMessage?has_content || errorMessageList?has_content)>
  <div class="content-messages errorMessage">
    <#if errorMessage?has_content>
      <p>${errorMessage}</p>
    </#if>
    <#if errorMessageList?has_content>
      <#list errorMessageList as errorMsg>
         <p>${errorMsg}</p>
      </#list>
    </#if>
    <#if eventMessage?has_content>
      <p>${eventMessage}</p>
    </#if>
    <#if eventMessageList?has_content>
      <#list eventMessageList as eventMsg>
        <p>${eventMsg}</p>
      </#list>
    </#if>
  </div>
</#if>

<link href="/bootstrap/style.css" type="text/css" rel="stylesheet" />

  <div class="bg_wrap">
    <div class="header"> Afya Pharmacy </div>
    <div class="login_section">
     <div class="login_form_section">
      <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
     	 User Name <br />
         <input type="text" name="USERNAME" value="${username}" size="20" class="login_input" /><br /><br />
      	 Password<br />
         <input type="password" name="PASSWORD" value="" size="20" class="login_input" /><br /><br />
         <#if ("Y" == useMultitenant)>
         Tenant ID <br />
         <input type="text" name="tenantId" value="${parameters.tenantId?if_exists}" size="20" class="login_input" /><br /><br />
      	</#if>
      	<!-- <#if displayApps?has_content>
      		My Application <br />
      		<select name="mySelectedApp">
      			<#list displayApps as display>
      				<#assign thisApp = display.getContextRoot()>
      				<option value="${thisApp}">
						<#if display.title?exists>${display.title}
							<#else> ${display.description}
						</#if>
					</option>
		        </#list>
		    </select>
      	</#if> -->
      	<input type="image" src="/bootstrap/images/login_key.png" align="right" /><br /><br />
      	<input type="hidden" name="JavaScriptEnabled" value="N" />
      </form>
        <div><a href="<@ofbizUrl>forgotPassword</@ofbizUrl>">Forgot your Password?</a></div>
    </div>
  </div>
  <div class="footer">
  <p>
  ${nowTimestamp?datetime?string.short} -
  <a href="<@ofbizUrl>ListTimezones</@ofbizUrl>">${timeZone.getDisplayName(timeZone.useDaylightTime(), Static["java.util.TimeZone"].LONG, locale)}</a>
  </p><br /><br />
  <p>
  Copyright &copy; 2009-${nowTimestamp?string("yyyy")} NthDimenzion Solutions (P) Limited - <a href="http://www.nthdimenzion.com" target="_blank">www.nthdimenzion.com </a>
  </p><br />
  </div>
</div>

<script language="JavaScript" type="text/javascript">
  document.loginform.JavaScriptEnabled.value = "Y";
  <#if focusName>
    document.loginform.USERNAME.focus();
  <#else>
    document.loginform.PASSWORD.focus();
  </#if>
</script>
