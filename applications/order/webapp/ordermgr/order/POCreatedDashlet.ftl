
<#if purchaseOrderHeaders?has_content>
<div class="screenlet">
<!--<div style="overflow:auto;height:170px">-->
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderOrder} #</td>
          <td width="15%">Suppliers</td>
          <td width="10%" style="text-align:right;">${uiLabelMap.CommonAmount} &nbsp;&nbsp;</td>
        </tr>
        <#assign alt_row = false>
        <#list purchaseOrderHeaders as purchaseOrderHeader>
          <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(purchaseOrderHeader)>
           <#assign billFromParty = orh.getBillFromParty()?if_exists>
          <#assign billToParty = orh.getBillToParty()?if_exists>
          <#if billToParty?has_content>
            <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", purchaseOrderHeader.orderDate, "userLogin", userLogin))/>
            <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
          </#if>
          <#assign productStore = purchaseOrderHeader.getRelatedOneCache("ProductStore")?if_exists />
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/orderview?orderId=${purchaseOrderHeader.orderId}" class="btn btn-link">${purchaseOrderHeader.orderId}</a></td>
            <td>${billFromParty.groupName?default(billFromParty.partyId)?if_exists}</td>
            <td class="align-text"><@ofbizCurrency amount=purchaseOrderHeader.grandTotal isoCode=purchaseOrderHeader.currencyUom/></td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
  </div>
</div>
</#if>