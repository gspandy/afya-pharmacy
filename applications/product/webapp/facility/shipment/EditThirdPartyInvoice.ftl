<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">Add Third Party</li>
        </ul>
        <br class="clear"/>
    </div>
    
    <div class="screenlet-body">
     <form action="<@ofbizUrl>addThirdParty</@ofbizUrl>" method="post" name="addThirdParty">
     <table cellspacing="0" class="basic-table">
        <input type="hidden" name="shipmentId" value="${shipmentId}"/>
        <tr>
        	<td>Third Party Id</td>
        	<td>
        	<@htmlTemplate.lookupField  value="${requestParameters.partyId?if_exists}" formName="addThirdParty" name="partyId" id="partyId" fieldFormName="LookupTransporterAgent"/> 
        	</td>
        </tr>
        <tr>
            <td/>
         <td >
         	<div class="buttontext">
            	<input type="submit" value="Add"/>
        	</div>
         </td>
        </tr>
     </table>
     </form>
    </div>
    
    <form action="<@ofbizUrl>addThirdParty</@ofbizUrl>" method="post" name="listThirdParty">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">List of Third Party</li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">
    <table cellspacing="0" class="basic-table">
      <tr class="header-row">
       <td valign="top">Third Party Name</td>
       <td valign="top">Applied Invoices</td>
       <td valign="top">Show Invoices</td>
       <td valign="top">Action</td>
      </tr>
	    <#list shipmentInvoices as thirdPartyInvoice>
	    <tr>
		     <#assign partyGroup = delegator.findOne("PartyGroup",{"partyId":thirdPartyInvoice.partyId},false)?default({})>
		     <td> 
		     	${thirdPartyInvoice.partyId} - ${partyGroup.groupName?if_exists}
		     </td>
		     
		     <td>
		      <#if thirdPartyInvoice.invoiceIds?has_content>
			      <#assign invoiceIdList = Static["java.util.Arrays"].asList(thirdPartyInvoice.invoiceIds.split(","))>
			      <#list invoiceIdList as invoiceId>
			      <#assign invoiceIdTrim = invoiceId.trim()>
			      	<a href="/accounting/control/invoiceOverview?invoiceId=${invoiceIdTrim}" target="_blank">${invoiceId}</a>
			      </#list>
		      </#if>
		     </td>
		     
		     <td> 
		     <#if thirdPartyInvoice.invoiceIds?has_content == false>
			     <@htmlTemplate.modelWindow size="0" formName="listThirdParty" name="dc" fieldFormName="showInvoices" 
			     	targetParameterIter='"${thirdPartyInvoice.partyId}","${shipmentId}","${thirdPartyInvoice.shipInvId}"'/> 
		     </#if>
		     </td>
		     <td>
		     	<#if thirdPartyInvoice.invoiceIds?has_content == false>
		     		<a href="javascript:document.remove_${thirdPartyInvoice.shipInvId}.submit();" class="btn btn-mini btn-danger">${uiLabelMap.CommonRemove}</a>
		     	</#if>
		     </td>
	    </tr>
	    </#list>
    </table>
    </div>
    </form>
    <#list shipmentInvoices as thirdPartyInvoice>
	    <form name="remove_${thirdPartyInvoice.shipInvId}" method="post" action="<@ofbizUrl>removeThirdParty</@ofbizUrl>">
		    <input name="shipInvId" type="hidden" value="${thirdPartyInvoice.shipInvId}"/>
		    <input name="shipmentId" type="hidden" value="${parameters.shipmentId}"/>
		</form>
	</#list>
</div>