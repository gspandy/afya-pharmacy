<#if sessionAttributes.errorMessageList?has_content>
	<#assign errorMessageList=sessionAttributes.errorMessageList>
</#if>
<#if (errorMessage?has_content || errorMessageList?has_content)>
  <div class="content-messages errorMessage">
    <p>Following Errors Occurred</p>
    <#if errorMessage?has_content>
      <p>${errorMessage}</p>
    </#if>
    <#if errorMessageList?has_content>
      <#list errorMessageList as errorMsg>
        <p>${errorMsg_index}.&nbsp;${errorMsg}</p>
      </#list>
    </#if>
  </div>
</#if>