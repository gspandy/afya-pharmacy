<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>

<#if userLogin?has_content>
  <#-- Begin SME Business Basics Specific:Pradyumna - 06/11/2008 - Changed the Application Main Tab section.
  	  The persmission are resolved be calling a new method hasEntityPermission(String[],String,HttpSession)
  	  in Security Class. The div now uses new css class for tab look and feel.
  -->
 <ul id="AppMenuBar" class="MenuBarHorizontal">
     <li><a class="MenuBarItemSubmenu" href="#">Home</a></li>
     <li><a class="MenuBarItemSubmenu" href="#">Profile</a></li>
     <li><a class="MenuBarItemSubmenu" href="#">Applications</a>
        <ul>
      <#list displayApps as display>
        <#assign thisApp = display.getContextRoot()>
        <#assign permission = true>
        <#assign selected = false>
        <#assign permissions = display.getBasePermission()>
        <#list permissions as perm>
          <#if perm != "NONE" && !Static["com.smebiz.security.SmeBizSecurityUtil"].hasEntityPermission(permissions, "_VIEW", session)>
            <#-- User must have ANY 1 permission in the base-permission list -->
            <#assign permission = false>
          </#if>
        </#list>
        <#if permission == true>
          <#if thisApp == contextPath || contextPath + "/" == thisApp>
            <#assign selected = true>
          </#if>
          <#assign thisURL = thisApp>
          <#if thisApp != "/">
            <#assign thisURL = thisURL + "/control/main">
          </#if>
          <#if display.title != "MyPage">
	          <li style="width:15em"><a href="${thisURL}${externalKeyParam}" <#if uiLabelMap?exists> title="${uiLabelMap[display.description]}"><span>${uiLabelMap[display.title]}</span><#else> title="${display.description}"><span>${display.title}</span></#if></a></li>
	  </#if>
	</#if>       
      </#list>
      </ul>
      </li>
    </ul>
</#if>
<br class="clear" />
<script type="text/javascript">
<!--
var AppMenuBar = new Spry.Widget.MenuBar("AppMenuBar", {imgDown:"images/SpryMenuBarDownHover.gif", imgRight:"images/SpryMenuBarRightHover.gif"});
//-->
</script>