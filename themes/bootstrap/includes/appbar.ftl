<#assign externalKeyParam = "">
<#assign helpTopic="">
<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if
(requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>
<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
<#assign langDir = "rtl">
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="/bootstrap/css/bootstrap-responsive.css" rel="stylesheet">
        <title>Afya Pharmacy: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title>
        <#if layoutSettings.shortcutIcon?has_content>
            <#assign shortcutIcon = layoutSettings.shortcutIcon/>
        <#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>
            <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
        </#if>
        <#if shortcutIcon?has_content>
            <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
        </#if>
        
        <#if layoutSettings.javaScripts?has_content>
            <#--layoutSettings.javaScripts is a list of java scripts. -->
            <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
            <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
            <#list layoutSettings.javaScripts as javaScript>
                <#if javaScriptsSet.contains(javaScript)>
                    <#assign nothing = javaScriptsSet.remove(javaScript)/>
                    <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
                </#if>
            </#list>
        </#if>
        <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
            <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
                <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
            </#list>
        </#if>
        <script src="/bootstrap/images/js/bootstrap.js" type="text/javascript"></script>
    
        <#if layoutSettings.styleSheets?has_content>
            <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
            <#list layoutSettings.styleSheets as styleSheet>
                <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
            </#list>
        </#if>    
        <#if layoutSettings.VT_STYLESHEET?has_content>
            <#list layoutSettings.VT_STYLESHEET as styleSheet>
                <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
            </#list>
        </#if>
        <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
            <#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
            <#list layoutSettings.rtlStyleSheets as styleSheet>
                <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
            </#list>
        </#if>
        <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
            <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
                <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
            </#list>
        </#if>
        <#if layoutSettings.VT_EXTRA_HEAD?has_content>
            <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
                ${extraHead}
            </#list>
        </#if>
    </head>
    <#if layoutSettings.headerImageLinkUrl?exists>
        <#assign logoLinkURL = "${layoutSettings.headerImageLinkUrl}">
    <#else>
        <#assign logoLinkURL = "${layoutSettings.commonHeaderImageLinkUrl}">
    </#if>
    
    <#if person?has_content>
        <#assign userName = person.firstName?if_exists + " " + person.middleName?if_exists + " " + person.lastName?if_exists>
    <#elseif partyGroup?has_content>
        <#assign userName = partyGroup.groupName?if_exists>
    <#else>
        <#assign userName = "">
    </#if>
    
    <#if defaultOrganizationPartyGroupName?has_content>
        <#assign orgName = defaultOrganizationPartyGroupName?if_exists>
    <#else>
        <#assign orgName = "">
    </#if>
    <body>
        <#assign externalKey = Static["org.ofbiz.webapp.control.LoginWorker"].getExternalLoginKey(request)>
        <#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + externalKey?if_exists></#if>
            <#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
            <#assign contextPath = request.getContextPath()>
            <#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>
            <#if userLogin?has_content && company?exists && company.setupComplete?exists>
            <div class="navbar">
                <div class="navbar-inner">
                    <ul class="nav" style="float:none;">
                        <li>
                            <img src="/bootstrap/images/logo.png" height="50px" width="50px" style="height:70px !important;width:100px !important"/>
                            <#-- <img src="/bootstrap/images/logo_02.png" height="40px" width="150px" style="height:40px !important"/> -->
                        </li>
                        <#if userLogin?exists>
                            <#if userLogin.partyId?exists>
                                <li class="dropdown" style="float:right !important;">
                                    <a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/partymgr/control/viewprofile?partyId=${userLogin.partyId}&amp;externalLoginKey=${externalLoginKey?if_exists}">
                                        ${userName}
                                        <b class="caret"></b>
                                    </a>
                                    <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
                                        <li class="first"><a href="<@ofbizUrl>ListLocales</@ofbizUrl>">${uiLabelMap.CommonLanguageTitle} : ${locale.getDisplayName(locale)}</a></li>
                                        <li>
                                            <a style="color:black" title="View Profile" href="/partymgr/control/viewprofile?partyId=${userLogin.partyId?if_exists}&amp;externalLoginKey=${externalLoginKey?if_exists}"><i class="icon-user"></i> View Profile </a>
                                        </li>
                                        <li>
                                            <a style="color:black" title="Log Out" href="/myportal/control/logout"><i class="icon-off"></i> Logout </a>
                                        </li>
                                    </ul>
                                </li>
                            </#if>
                        </#if>
                        <br/>
                        <br/>
                        <#list displayApps as display>
                            <#assign thisApp = display.getContextRoot()>
                            <#assign permission = true>
                            <#assign selected = false>
                            <#assign permissions = display.getBasePermission()>
                            <#list permissions as perm>
                                <#if (perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session) && !authz.hasPermission(session, perm, requestParameters))>
                                <#-- User must have ALL permissions in the base-permission list -->
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
                                <#if layoutSettings.suppressTab?exists && display.name == layoutSettings.suppressTab>
                                    <!-- do not display this component-->
                                <#else>
                                        <li style="float:right !important;"<#if selected> class="active"</#if>><a href="${thisURL}${externalKeyParam}" <#if uiLabelMap?exists> title="${uiLabelMap[display.description]}"><b>${uiLabelMap[display.title]}</b><#else> title="${display.description}"><b>${display.title}</b></#if></a></li>
                                </#if>
                            </#if>
                        </#list>
                    </ul>
                </div>
            </div>
            <script>
                $('.dropdown-toggle').dropdown();
            </script>
        </#if>
        <div class="page-container">
