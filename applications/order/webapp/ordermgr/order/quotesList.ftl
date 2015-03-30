    <#if quotesList?has_content>
      <table class="basic-table hover-bar" cellspacing='0'>
        <tr class="header-row">
          <td width="10%">Quote Id</td>
          <td width="15%">Quote Name</td>
          <td width="15%">Quote Type Id</td>
          <td width="25%">Quote Issue Date</td>
          <td width="10%">Status</td>
          <td width="20%">Product Store Id</td>
          <td width="10%">Description</td>
        </tr>
        <#assign alt_row = false>
        <#list quotesList as quotes>
          <#assign status = quotes.getRelatedOneCache("StatusItem")>
          <#assign quoteType = quotes.getRelatedOneCache("QuoteType")>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/ViewQuote?quoteId=${quotes.quoteId}" class="btn btn-link">${quotes.quoteId}</a></td>
            <td>${quotes.quoteName?if_exists}</td>
            <td>${quoteType.description?if_exists}</td>
            <td>${(quotes.issueDate?string("dd-MM-yyyy"))?if_exists}</td>
            <td>${status.description?if_exists}</td>
            <td>${quotes.productStoreId?if_exists}</td>
            <td>${quotes.description?if_exists}</td>
        </#list>
      </table>
    </#if>
