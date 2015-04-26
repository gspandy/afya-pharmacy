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
  <div id="orderContent" class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">Order Content</li>
        <li class="collapsed"><a onclick="javascript:toggleScreenlet(this, 'OrderContentScreenletBody_${orderId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Expand">&nbsp;</a></li>
      </ul>
      <br class="clear" />
    </div>
    <div class="screenlet-body" id="OrderContentScreenletBody_${orderId}" style="display: none;">
          ${screens.render("component://order/widget/ordermgr/OrderViewScreens.xml#ContentList")}
      <hr />
      <div class="label">Attach Content</div>
      <form id="uploadOrderContent" name="uploadOrderContent" method="post" enctype="multipart/form-data"  >
        <input type="hidden" name="dataCategoryId" value="PERSONAL"/>
        <input type="hidden" name="contentTypeId" value="DOCUMENT"/>
        <input type="hidden" name="statusId" value="CTNT_PUBLISHED"/>
        <input type="hidden" name="orderId" value="${orderId}" id="contentOrderId"/>
        <input type="file"  name="uploadedFile" id="uploadedFile" size="20" />
        <input type="hidden" name="orderContentTypeId" value="FILE_URL"/>
        <input type="hidden" name="mimeTypeId" id="mimeTypeId" value=""/>
        
		Comments
		<input type="text" name="comments" style="width:30%" rows="2" cols="10"></input>
        <input type="submit" class="btn btn-success"  value="${uiLabelMap.CommonUpload}"  onClick="javascript:getImage(uploadedFile,contentOrderId,lbl,mimeTypeId);" />
        <input type="label" name="lbl" id="lbl"  style="color:red;width:50%;display: none;font-weight:bold" value="Please choose PDF,Text & Image files only"/>
        <script language="JavaScript" type="text/javascript">
        function getImage(uploadedFile,contentOrderId,lbl,mimeTypeId) {
         var lbl = lbl.value;
         var mimeTypeId = mimeTypeId.value;
         var uFile = uploadedFile.value;
         var orderId = contentOrderId.value;
         var cform = document.uploadOrderContent;
         var extention = uFile.substring(uFile.lastIndexOf('.'));
         if(".jpg" == extention || ".bmp" == extention || ".png" == extention || ".gif" == extention || ".JPG" == extention
				|| ".BMP" == extention || ".PNG" == extention || ".GIF" == extention)
		   cform.elements["mimeTypeId"].value = "image/jpeg";
	     if(".txt" == extention)
	       cform.elements["mimeTypeId"].value = "text/plain";
		 if(".pdf" == extention)
		   cform.elements["mimeTypeId"].value = "application/pdf";
         if (".jpg" == extention || ".bmp" == extention || ".png" == extention || ".gif" == extention || ".JPG" == extention
				|| ".BMP" == extention || ".PNG" == extention || ".GIF" == extention|| ".txt" == extention || ".pdf" == extention) {
           cform.action = "<@ofbizUrl>uploadOrderContent</@ofbizUrl>";
    } else{
      cform.action="javascript:void(0);";
      document.getElementById('lbl').style.display="block"
      document.getElementById('lbl').visibilty='visible'
    }
     cform.submit();  
}
</script>
      </form>
    </div>
  </div>
