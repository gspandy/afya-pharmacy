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

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>

<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "secondary")>


<div id="footer">
    <p>Copyright (c) 2008 NthDimenzion Solutions (P) Limited - <a href="http://www.nthdimenzion.com/" target="_blank">www.nthdimenzion.com</a><br />
    Powered by <a href="http://www.nthdimenzion.com/" target="_blank">SME Business Basics</a>
    </p>
</div>


<#if layoutSettings.VT_FTR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_FTR_JAVASCRIPT as javaScript>
        <script type="text/javascript" src="<@ofbizContentUrl>${javaScript}</@ofbizContentUrl>" type="text/javascript"></script>
    </#list>
</#if>
</body>
</html>
