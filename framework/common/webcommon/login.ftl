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

<#-- <div class="bg_wrap">
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
</div> -->

<html>
	<head>
		<title>Afya Pharmacy: Login</title>
		<link href="/bootstrap/login_style.css" type="text/css" rel="stylesheet" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } </script>
		<!--webfonts-->
		<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
		<!--//webfonts-->
		<style type="text/css">
		<!--
			body {
				background-color: #FFFFFF;
			}
		-->
		</style>
	</head>
	<body>
		<div class="app-location">
	  		<div class="location"><img src="/bootstrap/images/logo_afya.png" class="img-responsive" alt="" /></div>
				<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
					<input type="text" class="text"  name="USERNAME" value="User Name" onFocus="this.value = '';" onBlur="if (this.value == '') {this.value = 'User Name';}" />
					<input type="password" name="PASSWORD" value="Password" onFocus="this.value = '';" onBlur="if (this.value == '') {this.value = 'Password';}" />
					<#if ("Y" == useMultitenant)>
						<input type="tenant-text" class="login_input" name="tenantId" value="${parameters.tenantId?if_exists}" onFocus="this.value = '';" onBlur="if (this.value == '') {this.value = '';}" />
					</#if>
					<div class="submit">
						<input type="submit" value="Sign in">
					</div>
					<div class="clear"></div>
					<input type="hidden" name="JavaScriptEnabled" value="N" />
					<div class="center" style="font-size: 1.2em;">
						<a href="<@ofbizUrl>forgotPassword</@ofbizUrl>">Forgot your Password?</a>
						<!--<h4><a href="#">New here? Sign Up</a></h4>-->
						<div class="clear"></div>
					</div>
				</form>
			</div>
			<!--start-copyright-->
	   		<!--<div class="copy-right"></div>-->
			<!--//end-copyright-->
		</div>
	</body>
</html>

<script language="JavaScript" type="text/javascript">
  document.loginform.JavaScriptEnabled.value = "Y";
  <#if focusName>
    document.loginform.USERNAME.focus();
  <#else>
    document.loginform.PASSWORD.focus();
  </#if>
</script>
