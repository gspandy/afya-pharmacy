
<script type="text/javascript">

function changeLocation(href,id) {
    if (href && href.length > 0) {
        window.location.href = href;
        return; 
    } else {
       var ctrl = document.getElementById(id);
       if (ctrl && ctrl.value) {
           window.location.href = ctrl.value;
       }
    }
}

</script>

<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.PartyContactInformation}</li>
      <li><a href="<@ofbizUrl>${editContactMechPage}?partyId=${partySummary.partyId}&amp;preContactMechTypeId=POSTAL_ADDRESS&amp;DONE_PAGE=${donePage}</@ofbizUrl>">Create New Address</a></li>
      <li><a href="<@ofbizUrl>${editContactMechPage}?partyId=${partySummary.partyId}&amp;preContactMechTypeId=TELECOM_NUMBER&amp;DONE_PAGE=${donePage}</@ofbizUrl>">Create New Phone Number</a></li>
      <li><a href="<@ofbizUrl>${editContactMechPage}?partyId=${partySummary.partyId}&amp;preContactMechTypeId=EMAIL_ADDRESS&amp;DONE_PAGE=${donePage}</@ofbizUrl>">Create New Email</a></li>
    </ul>
  </div>
  <#-- <div class="subMenuBar">
    <select class="inputBox" id="editContactOption" onchange="javascript:changeLocation(null,'editContactOption')">
        <option value="">Create New</option>
        <option value="${editContactMechPage}?partyId=${partySummary.partyId}&amp;preContactMechTypeId=POSTAL_ADDRESS&amp;DONE_PAGE=${donePage}">Address</option>	
        <option value="${editContactMechPage}?partyId=${partySummary.partyId}&amp;preContactMechTypeId=TELECOM_NUMBER&amp;DONE_PAGE=${donePage}">Phone Number</option>	
        <option value="${editContactMechPage}?partyId=${partySummary.partyId}&amp;preContactMechTypeId=EMAIL_ADDRESS&amp;DONE_PAGE=${donePage}">Email</option>	
    </select>
  </div> -->
</div>
<div class="form">
  <#if contactMeches?has_content>
    <table class="contactTable" style="border:0px">

      <tr>
        <th><span class="tableheadtext">${uiLabelMap.PartyContactType}</span></th>
        <th><span class="tableheadtext">${uiLabelMap.PartyContactInformation}</span></th>
        <th><span class="tableheadtext">${uiLabelMap.CommonPurpose}</span></th>
        <th><span class="tableheadtext">${uiLabelMap.PartyContactSolicitingOk}</span></th>
        <#if hasUpdatePermission?exists>
        <th><span class="tableheadtext">${uiLabelMap.CommonOptions}</span></th>
        </#if>
      </tr>

      <#list userLogins as userLogin>
        <tr>

          <#-- contact type -->

            <td>
              <div class="tabletext"><b>${uiLabelMap.CrmUserLogin}</b>
              </div>
            </td>

            <#-- contact information -->
            <td>
              <div class="tabletext">
                <#if userLogin.userLoginId?has_content>${userLogin.userLoginId}
                  <#if userLogin.enabled?has_content && userLogin.enabled == "N" >
                    <b>(${uiLabelMap.CommonDisabled})</b>
                  <#else>
                    <b>(${uiLabelMap.CommonEnabled})</b>
                  </#if>
                </#if>
              </div>
            </td>

            <#-- purposes -->
            <td>
            </td>

            <td class="contactTableCenter"></td>


            <td>
               <#if hasPassPermission?exists>
                  <a href="<@ofbizUrl>viewPartyPassword?partyId=${partySummary.partyId}&userLoginId=${userLogin.userLoginId}&DONE_PAGE=${donePage}</@ofbizUrl>" class="btn btn-mini btn-success">${uiLabelMap.CommonUpdate}</a>&nbsp;
               </#if>
            </td>

         </tr>
      </#list>

      <#list contactMeches as contactMechMap>
          <#assign contactMech = contactMechMap.contactMech>
          <#assign partyContactMech = contactMechMap.partyContactMech>
          <tr>
            <#-- contact type -->
            <td>
              <div class="tabletext"><b>${contactMechMap.contactMechType.get("description",locale)}</b>
              </div>
            </td>
            <#-- contact information -->
            <td>
              <#if "POSTAL_ADDRESS" == contactMech.contactMechTypeId && contactMechMap.postalAddress?exists>
                  <#assign postalAddress = contactMechMap.postalAddress>
                  <div class="tabletext">
                    <#if postalAddress.toName?has_content><b>${uiLabelMap.PartyAddrToName}:</b> ${postalAddress.toName}<br/></#if>
                    <#if postalAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b> ${postalAddress.attnName}<br/></#if>
                    ${postalAddress.address1?if_exists}<br/>
                    <#if postalAddress.address2?has_content>${postalAddress.address2}<br/></#if>
                    ${postalAddress.city?if_exists},
                    ${postalAddress.stateProvinceGeoId?if_exists}
                    ${postalAddress.postalCode?if_exists}
                    <#if postalAddress.postalCodeExt?has_content>-${postalAddress.postalCodeExt}</#if>
                    <#if postalAddress.directions?has_content><br/>[${postalAddress.directions}]</#if>
                    <#if postalAddress.countryGeoId?default("") == "USA">
                    <#assign query = postalAddress.address1?default("")?replace(" ", "+") + "+" + postalAddress.city?default("")?replace(" ", "+") + "+" + postalAddress.stateProvinceGeoId?default("") + "+" + postalAddress.postalCode?default("")>
                    (<a href="http://maps.google.com/?q=${query}" class="linktext" target="_blank">${uiLabelMap.CrmMapIt}</a>)
                    <#else>
                    <#if postalAddress.countryGeoId?has_content><br/>${postalAddress.countryGeoId}</#if>
                    </#if>
                  </div>
              <#elseif "TELECOM_NUMBER" == contactMech.contactMechTypeId>
                  <#assign telecomNumber = contactMechMap.telecomNumber>
                  <div class="tabletext">
                    ${telecomNumber.countryCode?if_exists}
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode?default("000")}-</#if>${telecomNumber.contactNumber?default("000-0000")}
                    <#if partyContactMech.extension?has_content>${uiLabelMap.PartyContactExt}&nbsp;${partyContactMech.extension}</#if>
                  </div>
                  <#if telecomNumber.askForName?has_content>
                  <div class="tabletext"><span class="tableheadtext">${uiLabelMap.CrmPhoneAskForName}:</span> ${telecomNumber.askForName}</div>
                  </#if>
              <#elseif "EMAIL_ADDRESS" == contactMech.contactMechTypeId>
                  <div class="tabletext">
                    <a href="<@ofbizUrl>writeEmail?contactMechIdTo=${contactMech.contactMechId}&internalPartyId=${parameters.partyId?if_exists}&donePage=${donePage?if_exists}</@ofbizUrl>" class="linktext">${contactMech.infoString?if_exists}</a>&nbsp;
                    </div>
                  </div>
              <#elseif "WEB_ADDRESS" == contactMech.contactMechTypeId>
                  <div class="tabletext">
                    <#assign openAddress = contactMech.infoString?default("")>
                    <#if !openAddress?starts_with("http") && !openAddress?starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                    <a target="_blank" href="${openAddress}" class="linktext">${contactMech.infoString?if_exists}</a>
                  </div>
              <#elseif "SKYPE" == contactMech.contactMechTypeId>
                  <div class="tabletext">
                    <a href="skype:${contactMech.infoString?if_exists}?call" class="linktext">${contactMech.infoString?if_exists}</a>&nbsp;<img src="http://mystatus.skype.com/smallicon/${contactMech.infoString?if_exists}" style="vertical-align:middle"/>
                  </div>
              <#else>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                  </div>
              </#if>
              <div class="tabletext">(${uiLabelMap.CommonUpdated}:&nbsp;${partyContactMech.fromDate?date})</div>
              <#if partyContactMech.thruDate?has_content><div class="tabletext"><b>${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${partyContactMech.thruDate}</b></div></#if>
            </td>

            <#-- purposes -->

            <td>
              <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
                    <div class="tabletext">
                      <#if contactMechPurposeType?has_content>
                        ${contactMechPurposeType.get("description",locale)}
                      <#else>
                        ${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
                      </#if>
                      <#if partyContactMechPurpose.thruDate?has_content>
                      (${uiLabelMap.CommonExpire}: ${partyContactMechPurpose.thruDate})
                      </#if>
                    </div>
              </#list>
            </td>

            <#assign solicit = "">
            <#if (partyContactMech.allowSolicitation?default("") == "Y")><#assign solicit = uiLabelMap.CommonYes></#if>
            <#if (partyContactMech.allowSolicitation?default("") == "N")><#assign solicit = uiLabelMap.CommonNo></#if>
            
            <td class="contactTableCenter"></td>
            <td>
               <#if hasUpdatePermission?exists>
                  <a href="<@ofbizUrl>${editContactMechPage}?partyId=${partySummary.partyId}&contactMechId=${contactMech.contactMechId}&DONE_PAGE=${donePage}</@ofbizUrl>" class="btn btn-mini btn-success">${uiLabelMap.CommonUpdate}</a>&nbsp;
                  <a href="<@ofbizUrl>deleteContactMech?partyId=${partySummary.partyId}&contactMechId=${contactMech.contactMechId}&DONE_PAGE=${donePage}</@ofbizUrl>" class="btn btn-mini btn-danger">${uiLabelMap.CommonExpire}</a>&nbsp;&nbsp;
               </#if>
               <#if "POSTAL_ADDRESS" == contactMech.contactMechTypeId && contactMechMap.postalAddress?exists>
               </#if>
            </td>
          </tr>
      </#list>
    </table>
  <#else>
    <div class="tabletext">${uiLabelMap.PartyNoContactInformation}</div>
  </#if>
</div>