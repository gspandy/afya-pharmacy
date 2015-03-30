<#if incomingShipmentList?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td>Shipment</td>
          <td>Shipment Type</td>
          <td>Status</td>
          <td>Arrival Date</td>
        </tr>
        <#assign alt_row = false>
        <#list incomingShipmentList as incomingShipment>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="<@ofbizUrl>ViewShipment?shipmentId=${incomingShipment.shipmentId?if_exists}</@ofbizUrl>" class="btn btn-link">${incomingShipment.shipmentId?if_exists} ${incomingShipment.shipmentItemSeqId?if_exists}</a></td>
         	<td>${incomingShipment.getRelatedOneCache("ShipmentType").get("description",locale)}</td>
            <td>${incomingShipment.getRelatedOneCache("StatusItem").get("description",locale)}</td>
          <#--  <#assign dateFormat = Static["java.text.DateFormat"].LONG>
            <#assign estimatedArrivalDate = Static["java.text.DateFormat"].getDateInstance(dateFormat,locale).format(incomingShipment.get("estimatedArrivalDate"))>
            <td>${estimatedArrivalDate?if_exists}</td>-->
             <td>${incomingShipment.estimatedArrivalDate?string("dd-MM-yyyy")}</td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
      </div>
  </div>
</#if>