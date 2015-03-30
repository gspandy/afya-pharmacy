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

  <div id="orderContentList">
      <#if orderHeaderContent?has_content>
        <table class="basic-table" cellspacing="0">
         <tr class="header-row">
          <td width="10%">Content</td>
          <td width="15%">Content Name</td>
          <td width="10%">Comments</td>
             <td width="10%">Action</td>
        </tr>
          <#list orderHeaderContent as pContent>
            <#assign content = pContent.getRelatedOne("Content")>
            <#assign contentType = content.getRelatedOneCache("ContentType")>
            <#assign mimeType = content.getRelatedOneCache("MimeType")?if_exists>
            <#assign status = content.getRelatedOneCache("StatusItem")?if_exists>
          <#--  <#assign pcType = pContent.getRelatedOne("OrderHeaderContentType")> -->
            <tr>
            <!--  <td class="button-col"><a href="<@ofbizUrl>EditPartyContents?contentId=${pContent.contentId}&amp;partyContentTypeId=${pContent.orderContentTypeId}&amp;fromDate=${pContent.fromDate}</@ofbizUrl>">${content.contentId}</a></td> -->
              <td>${content.contentId?if_exists}</td>
             <#-- <td>${pcType.description?if_exists}</td> -->
              <td>${content.contentName?if_exists}</td>
            <#--  <td>${(contentType.get("description",locale))?if_exists}</td> 
                  <td>${(mimeType.description)?if_exists}</td> 
                  <td>${(status.get("description",locale))?if_exists}</td>-->
              <td>${pContent.comments?if_exists}</td>
              <td class="button-col">
                <#if (content.contentName?has_content)>
                    <a href="<@ofbizUrl>img?imgId=${content.dataResourceId}</@ofbizUrl>" target="img">${uiLabelMap.CommonView}</a>
                </#if>
                <form name="removeOrderContent_${pContent_index}" method="post" action="<@ofbizUrl>removeOrderHeaderContent</@ofbizUrl>">
                  <input type="hidden" name="orderId" value="${orderId}" />
                  <input type="hidden" name="contentId" value="${pContent.contentId}" />
                  <input type="hidden" name="orderContentTypeId" value= "${pContent.orderContentTypeId}" />
                  <input type="hidden" name="fromDate" value="${pContent.fromDate}" />
                  <a href="javascript:document.removeOrderContent_${pContent_index}.submit()">${uiLabelMap.CommonRemove}</a>
                </form>
              </td>
            </tr>
          </#list>
        </table>
      <#else>
        ${uiLabelMap.PartyNoContent}
      </#if>
  </div>