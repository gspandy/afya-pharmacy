<#include "component://common/webcommon/includes/commonMacros.ftl"/>
<#if salesOrderHeaders?has_content>
<div class="screenlet">
    <div class="screenlet-body">
      <table cellspacing="0" cellpadding="2" class="basic-table">
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderOrder} #</td>
          <td width="15%">${uiLabelMap.OrderOrderBillToParty}</td>
          <td width="25%">${uiLabelMap.OrderProductStore}</td>
          <td width="10%" style="text-align:right;">${uiLabelMap.CommonAmount} &nbsp;&nbsp;</td>
        </tr>
        <#assign alt_row = false>
        <#list salesOrderHeaders as salesOrderHeader>
          <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(salesOrderHeader)>
          <#assign billToParty = orh.getBillToParty()?if_exists>
          <#if billToParty?has_content>
            <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", salesOrderHeader.orderDate, "userLogin", userLogin))/>
            <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
          </#if>
          <#assign productStore = salesOrderHeader.getRelatedOneCache("ProductStore")?if_exists />
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/orderview?orderId=${salesOrderHeader.orderId}" class="btn btn-link">${salesOrderHeader.orderId}</a></td>
            <td>${billTo?if_exists}</td>
            <td><#if productStore?has_content>${productStore.storeName?default(productStore.productStoreId)}</#if></td>
            <td class="align-text"><@ofbizCurrency amount=salesOrderHeader.grandTotal isoCode=salesOrderHeader.currencyUom/></td>
          </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </table>
        
  </div>
</div>
</#if>