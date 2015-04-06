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

<html>
<head>
</head>
<body>
  <div>Hi ${firstName?if_exists}&nbsp;${lastName?if_exists} , </div>
    <br />
  <div>${uiLabelMap.EcommerceThisEmailIsInResponseToYourRequestToHave} 
  	<#if useEncryption>${uiLabelMap.EcommerceANew}<#else>${uiLabelMap.EcommerceYour}</#if> ${uiLabelMap.EcommercePasswordSentToYou}
  </div>
  <div>
      <#if useEncryption>
          ${uiLabelMap.EcommerceNewPasswordMssgEncryptionOn}
      <#else>
          ${uiLabelMap.EcommerceNewPasswordMssgEncryptionOff}
      </#if>
      "${password}".
  </div>
      <br />
  <table>
  <tr>
  	<td>
  		Regards
  	</td>
  </tr>
  <tr>
  	<td>
  SME Business Mantra Team
  	</td>
  </tr>
  <tr>
  	<td>
  		www.nthdimenzion.com
  	</td>
  </tr>
  </table>
</body>
</html>