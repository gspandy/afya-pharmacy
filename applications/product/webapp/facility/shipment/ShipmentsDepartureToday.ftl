<#if outgoingShipmentList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Shipment</td>
          <td>Shipment Type</td>
          <td>Status</td>
        <#--  <td>Facility</td> -->
        </tr>
        <#assign alt_row = false>
        <#list outgoingShipmentList as outgoingShipment>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="<@ofbizUrl>ViewShipment?shipmentId=${outgoingShipment.shipmentId?if_exists}</@ofbizUrl>" class="btn btn-link">${outgoingShipment.shipmentId?if_exists} ${outgoingShipment.shipmentItemSeqId?if_exists}</a></td>
         	<td>${outgoingShipment.getRelatedOneCache("ShipmentType").get("description",locale)}</td>
            <td>${outgoingShipment.getRelatedOneCache("StatusItem").get("description",locale)}</td>
         <#--   <td>${outgoingShipment.originFacilityId?if_exists}</td> -->
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>