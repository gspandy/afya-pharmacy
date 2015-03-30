<#if incomingShipmentList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Shipment</td>
          <td>Sending Party</td>
          <td>Status</td>
        </tr>
        <#assign alt_row = false>
        <#list incomingShipmentList as incomingShipment>
        <#assign facilityGv = delegator.findOne("Facility", false ,{"facilityId":incomingShipment.destinationFacilityId})>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="<@ofbizUrl>ViewShipment?shipmentId=${incomingShipment.shipmentId?if_exists}</@ofbizUrl>" class="btn btn-link">${incomingShipment.shipmentId?if_exists} ${incomingShipment.shipmentItemSeqId?if_exists}</a></td>
           	<#if incomingShipment.partyIdFrom?has_content>
           		 <#assign sendingParty = delegator.findOne("PartyNameView",true,"partyId",incomingShipment.partyIdFrom)>
            <td>${sendingParty.firstName?if_exists} ${sendingParty.lastName?if_exists} ${sendingParty.groupName}</td>
             <#else>
             <td> _NA_ </td>
             </#if>
             
            <td>${incomingShipment.getRelatedOneCache("StatusItem").get("description",locale)}</td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>