<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>

<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>

<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>

<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title> | NthDimenzion SME Business Mantra<#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title>
    <#if layoutSettings.shortcutIcon?has_content>
      <#assign shortcutIcon = layoutSettings.shortcutIcon/>
    <#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>    
      <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
    </#if>
    <#if shortcutIcon?has_content>
      <link rel="shortcut icon" href="<@ofbizContentUrl>${shortcutIcon}</@ofbizContentUrl>" />
    </#if>
    <#if layoutSettings.javaScripts?has_content>
        <#--layoutSettings.javaScripts is a list of java scripts. -->
        <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
        <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
        <#list layoutSettings.javaScripts as javaScript>
            <#if javaScriptsSet.contains(javaScript)>
                <#assign nothing = javaScriptsSet.remove(javaScript)/>
                <script type="text/javascript" src="<@ofbizContentUrl>${javaScript}</@ofbizContentUrl>" type="text/javascript"></script>
            </#if>
        </#list>
    </#if>
    <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
        <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
            <script type="text/javascript" src="<@ofbizContentUrl>${javaScript}</@ofbizContentUrl>" type="text/javascript"></script>
        </#list>
    </#if>
    <#if layoutSettings.styleSheets?has_content>
        <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
        <#list layoutSettings.styleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" type="text/css"/>
        </#list>
	<link rel="stylesheet" href="/smeVisualTheme/images/displaytag.css" type="text/css"/>
    </#if>
    <#if layoutSettings.VT_STYLESHEET?has_content>
        <#list layoutSettings.VT_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
        <#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
        <#list layoutSettings.rtlStyleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
        <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    ${layoutSettings.extraHead?if_exists}
    <#if layoutSettings.VT_EXTRA_HEAD?has_content>
        <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
            ${extraHead}
        </#list>
    </#if>
</head>
<#if layoutSettings.headerImageLinkUrl?exists>
  <#assign logoLinkURL = "${layoutSettings.headerImageLinkUrl}">
<#else>
  <#assign logoLinkURL = "${layoutSettings.commonHeaderImageLinkUrl?if_exists}">
</#if>
<body>


  </body>