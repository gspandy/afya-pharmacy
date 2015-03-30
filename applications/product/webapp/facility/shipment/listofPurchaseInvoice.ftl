
<form name="addInvoices" id="addInvoices"> 
<input type="hidden" name="partyId" value="${partyId}"/>
<input type="hidden" name="shipmentId" value="${shipmentId}"/>
<input type="hidden" name="shipInvId" value="${shipInvId}"/>
<table cellspacing="0" class="basic-table">
	<tr class="header-row">
       <td valign="top">Invoice Id</td>
       <td valign="top">Party Id</td>
       <td valign="top">Date</td>
       <td valign="top">Total Amount</td>
    </tr>
	<#list invoices as invoice>
	 <tr>
	  <td> <input type="checkbox" name="invoiceIds" value="${invoice.invoiceId}" />
        ${invoice.invoiceId}
       </td>
	  <td>${invoice.partyIdFrom}</td>
	  <td>${invoice.invoiceDate}</td>
	  <td style="text-align:right;">
	  <#assign invoiceItems = delegator.findByAnd("InvoiceItem",{"invoiceId":invoice.invoiceId},null,false)>
	  <#assign amountTotal = Static["java.math.BigDecimal"].ZERO>
	  <#list invoiceItems as invoiceItem>
	  	<#assign amountTotal = amountTotal.add(invoiceItem.getBigDecimal("amount"))/>
          <#assign amountTotal = amountTotal.multiply(invoiceItem.getBigDecimal("quantity")?default(1))/>
	  </#list>
	   <@ofbizCurrency amount=amountTotal isoCode=invoice.currencyUomId/> 
	  </td>
	 </tr>
	</#list>
	<tr>
		<td colspan="4"></td>
    </tr>
	<tr>
		<td> 
		</td>
		<td colspan="2">
		</td>
    </tr>
</table>
</form>

<#if invoices?has_content>
				<input type="submit" value="Apply" onClick="javascript:applyInvoices();" class="buttontext"/> 
			</#if>

<script type="text/javascript" src="/images/prototypejs/prototype.js">
   </script>
 <script language="JavaScript" type="text/javascript">
	 closeLookup();
	 function applyInvoices(){
		 var chk = document.getElementsByName('invoiceIds');
		 var isChk = false;
		 for (i=0; i<chk.length; i++) {
		   if (chk[i].checked) {
		     isChk = true;
		   } 
		 }
		 if(!isChk){
		 	alert("Please select atleast one Invoice");
		 	return;
		 }
		 if (confirm("Once Applied cannot be reverted")) {
	        var cform = document.addInvoices;
	        cform.action = "<@ofbizUrl>addInvoices</@ofbizUrl>";
	        cform.submit();
	    }
    
	 	   
	 }
 </script>

