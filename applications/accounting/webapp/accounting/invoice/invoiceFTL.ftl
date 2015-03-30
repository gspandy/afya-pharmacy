<!-- height="842px" -->
<table width="700px" style="border-width: 1px;border-style: solid;font-size:12px;min-height:842px;" CELLPADDING="0" CELLSPACING="0">
	<tr>
		<td style="text-align:right;border-bottom:solid 1px #000;" width="100%" colspan="3">
			ORIGINAL/DUPLICATE/TRIPLICATE/EXTRA COPY
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<table CELLPADDING="0" CELLSPACING="0"><tr><td >
			   <table CELLPADDING="0" CELLSPACING="0" style="border:solid 1px #000;border-left:none;">
				<tr><td style="text-align:center;border-bottom:solid 1px #000;">AUTHENTICATED BY</td><tr>
				<tr><td>
				<br/>
				<br/>
				</td><tr>
				</table>
				
			</td>
			<td valign="top" style="text-align:center;">
				<table CELLPADDING="0" CELLSPACING="0" >
				<tr><td><label> TAX INVOICE </label></td></tr>
				<tr><td><label> ISSUE OF INVOICE UNDER RULE 11 OF THE CENTRAL EXCISE RULES,2002 </label></td</tr>
				<tr><td><block style="font-size:22px;text-transform:uppercase" font-weight="bold">${companyName}</block></td></tr>
				<tr><td style="text-transform:uppercase;">
				<#if postalAddress?exists>
			        <#if postalAddress?has_content>
			            <block>${postalAddress.address1?if_exists}</block>
			            <#if postalAddress.address2?has_content><block>${postalAddress.address2?if_exists}</block></#if>
			            <block>${postalAddress.city?if_exists}, ${stateProvinceAbbr?if_exists} ${postalAddress.postalCode?if_exists}, ${countryName?if_exists}</block>
			        </#if>
			    <#else>
			        <block>${uiLabelMap.CommonNoPostalAddress}</block>
			        <block>${uiLabelMap.CommonFor}: ${companyName}</block>
			    </#if>
				</td></tr>
				</table>
			</td>
			</tr>
			</table>
		</td>
	</tr>
	
	<tr>
	<td style="border-top:solid 1px #000;border-right:solid 1px #000;font-weight:bold;" colspan="2">
	<div style="width:100px;text-align:center;font-size:10px;">
	Telefax : 
	 <#if phone?exists>
		<lable style="font-size:10px;">
		<#if phone.countryCode?exists>${phone.countryCode}-</#if><#if phone.areaCode?exists>${phone.areaCode}-</#if>${phone.contactNumber?if_exists}</lable>
		<#else/>
		&nbsp;
	 </#if>
	 </div>
	</td>
	<td style="tax-align:center;border-top:solid 1px #000;">
	  <div style="text-align:center;font-size:10px;">
	  <#if company.officeSiteName?if_exists>
		<lable style="font-size:10px;"> ${company.officeSiteName?if_exists} </lable>
		<#else>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  </#if>
	  </div>
	</td>
	<tr>
	
	
	<tr>
	<td colspan="2" style="border-top:solid 1px #000;border-right:solid 1px #000;border-bottom:solid 1px #000;font-weight:bold;">
	<div style="width:100px;text-align:center;font-size:10px;">
		TIN NO. : ${company.vatTinNumber?if_exists}
	 </div>
	</td>
	<td style="tax-align:center;border-top:solid 1px #000;border-bottom:solid 1px #000;font-weight:bold;">
	  <div style="text-align:center;font-size:10px;">
	  <#if partyMenuFacUnit?has_content>
		 ECC NO. : ${partyMenuFacUnit.exciseRegistrationNo}
	  </#if>
	  </div>
	</td>
	<tr>

	<!-- End Header 1 -->

	<tr>
	<td colspan="3">																
	<#assign invoiceFormattedDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(invoiceDate)>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;">Name of Excisable Commodities :  Chemicals/Varnishes </td>
		   <td style="border-bottom:solid 1px #000;width:210px;">
		   INVOICE NO. <#if invoice?has_content>${invoice.invoiceId}</#if> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  Dt:${invoiceFormattedDate?if_exists}</td>
		</tr>
	</table>
	<#assign invoiceFormattedDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDate(invoiceDate)>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;">Name and address of the Consignee : </td>
		   <td style="border-bottom:solid 1px #000;width:210px;">
		   P.O. NO: <#list orders as order>${order} </#list> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Dt:${invoiceFormattedDate?if_exists} </td>
		</tr>
	</table>
	<#if billingAddress?has_content>
	<#assign billingPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billingParty.partyId, "compareDate", invoice.invoiceDate, "userLogin", userLogin))/>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;text-transform:uppercase;">${billingPartyNameResult.fullName?default(billingAddress.toName)?default("Billing Name Not Found")} </td>
		   <td style="border-bottom:solid 1px #000;width:210px;">TERMS OF PAYMENT : <#if invoice.paymentDescription?has_content>${invoice.paymentDescription?if_exists?default("")}</#if> </td>
		</tr>
	</table>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		<td style="width:50%;" valign="top">
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;text-transform:uppercase">${billingAddress.address1?if_exists?default("")}</td>
		</tr>
	</table>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;text-transform:uppercase"> 
		   <#if billingAddress.address2?exists>${billingAddress.address2?if_exists?default("")}
		   <#else>
		   	&nbsp;
		   </#if></td>
		</tr>
	</table>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;text-transform:uppercase"> ${billingAddress.city?if_exists?default("")} ${billingAddress.stateProvinceGeoId?if_exists?default("")} ${billingAddress.postalCode?if_exists?default("")}</td>
		</tr>
	</table>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;text-transform:uppercase"> ${uiLabelMap.AccountingNoGenBilAddressFound?if_exists?default("")}${billingParty.partyId?if_exists?default("")}</td>
		</tr>
	</table>
	</#if>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;"> &nbsp;</td>
		</tr>
	</table>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:210px;border-right:solid 1px #000;"> &nbsp;</td>
		</tr>
	</table>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:58px;border-right:solid 1px #000;"> Party's TIN No.:</td>
		   <td style="border-bottom:solid 1px #000;width:162px;border-right:solid 1px #000;"> ${billingParty.vatTinNumber?if_exists}</td>
		</tr>
	</table>
	<table CELLPADDING="0" CELLSPACING="0" width="100%">
		<tr>
		   <td style="border-bottom:solid 1px #000;width:58px;border-right:solid 1px #000;"> Party's CST No.:</td>
		   <td style="border-bottom:solid 1px #000;width:162px;border-right:solid 1px #000;"> ${billingParty.cstNumber?if_exists}</td>
		</tr>
	</table>
	</td>
	<td valign="top" style="width:50%;">
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td colspan="2" style="border-bottom:solid 1px #000;width:210px;">Sent Through</td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">Range(Including Postal Add.)</td>
			<td style="border-bottom:solid 1px #000;width:105px;">Division :</td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">
			<#if partyMenuFacUnit?has_content> ${partyMenuFacUnit.rangeCode?if_exists?default("")} <#else>&nbsp; </#if> </td>
			<td style="border-bottom:solid 1px #000;width:105px;">
			<#if partyMenuFacUnit?has_content> ${partyMenuFacUnit.divisionCode?if_exists?default("")} <#else>&nbsp; </#if> </td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr>
			<td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">
			<#if partyMenuFacUnit?has_content> ${partyMenuFacUnit.rangeName?if_exists?default("")} <#else>&nbsp; </#if> </td>
			<td style="border-bottom:solid 1px #000;width:105px;">
			<#if partyMenuFacUnit?has_content> ${partyMenuFacUnit.divisionName?if_exists?default("")} <#else>&nbsp; </#if> </td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">
			<#if partyMenuFacUnit?has_content> ${partyMenuFacUnit.rangeAddress?if_exists?default("")} <#else>&nbsp; </#if> </td>
			<td style="border-bottom:solid 1px #000;width:105px;">
			<#if partyMenuFacUnit?has_content> ${partyMenuFacUnit.divisionAddress?if_exists?default("")} <#else>&nbsp; </#if> </td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">&nbsp;</td>
			<td style="border-bottom:solid 1px #000;width:105px;">&nbsp;</td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">&nbsp;</td>
			<td style="border-bottom:solid 1px #000;width:105px;">&nbsp;</td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">&nbsp;</td>
			<td style="border-bottom:solid 1px #000;width:105px;">&nbsp;</td></tr>
		</table>
		<table CELLPADDING="0" CELLSPACING="0" width="100%">
			<tr><td style="border-bottom:solid 1px #000;width:105px;border-right:solid 1px #000;">&nbsp;</td>
			<td style="border-bottom:solid 1px #000;width:105px;">&nbsp;</td></tr>
		</table>
	</td>
	</td>	
	<tr>	
	
<!--End of Header 2-->

    <tr>
    <table CELLPADDING="0" CELLSPACING="0">
     <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp; </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;"></td> </tr>
     <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp; </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> <td style="border-bottom:solid 1px #000;"></td> </tr>
     <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> SL. </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">Tariff No.</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> Description and</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> No. of</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> Qty per</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> Total </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> Unit </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> Unit </td> <td style="border-bottom:solid 1px #000;text-align:center;">Amount (in Rs )</td> </tr>
     <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> No. </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">Specification of Goods</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">Packages</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">Packages</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">Quantity</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"></td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">Rate</td> <td style="border-bottom:solid 1px #000;text-align:center;"></td> </tr>
<!-- Product List-->
     <#assign totalAmountWithOutTax = 0>
     <#assign count = 0>
        <#assign currentShipmentId = "">
        <#assign newShipmentId = "">
        <#-- if the item has a description, then use its description.  Otherwise, use the description of the invoiceItemType -->
        <#list invoiceItems as invoiceItem>
            <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
            <#assign product = invoiceItem.getRelatedOne("Product")>
            <#assign quantityUom = product.getString("quantityUomId")>
            <#assign isItemAdjustment = Static["org.ofbiz.common.CommonWorkers"].hasParentType(delegator, "InvoiceItemType", "invoiceItemTypeId", itemType.getString("invoiceItemTypeId"), "parentTypeId", "INVOICE_ADJ")/>

            <#assign taxRate = invoiceItem.getRelatedOne("TaxAuthorityRateProduct")?if_exists>
            <#assign itemBillings = invoiceItem.getRelated("OrderItemBilling")?if_exists>
            <#if itemBillings?has_content>
                <#assign itemBilling = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(itemBillings)>
                <#if itemBilling?has_content>
                    <#assign itemIssuance = itemBilling.getRelatedOne("ItemIssuance")?if_exists>
                    <#if itemIssuance?has_content>
                        <#assign newShipmentId = itemIssuance.shipmentId>
                        <#assign issuedDateTime = itemIssuance.issuedDateTime/>
                    </#if>
                </#if>
            </#if>
            <#if invoiceItem.description?has_content>
                <#assign description=invoiceItem.description>
            <#elseif taxRate?has_content & taxRate.get("description",locale)?has_content>
                <#assign description=taxRate.get("description",locale)>
            <#elseif itemType.get("description",locale)?has_content>
                <#assign description=itemType.get("description",locale)>
            </#if>
            <#if !isItemAdjustment>
				<#assign count = count+1>
			
			<tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> ${count?if_exists} </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;">&nbsp;</td> 
			<td style="border-bottom:solid 1px #000;border-right:solid 1px #000;">${description?if_exists}</td>
			
			<#assign noOfPack = Static["org.sme.order.util.OrderMgrUtil"].getTotalPackages(product.QtyPerPackages,invoiceItem.quantity)> 
			
			<td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">${noOfPack}</td> 
			<td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;">${product.QtyPerPackages?if_exists?default("")}</td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"><#if invoiceItem.quantity?exists>${invoiceItem.quantity?string.number}</#if></td> 
			<td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;"> <#if quantityUom?exists>${quantityUom}</#if> </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:right;"><#if invoiceItem.quantity?exists><@ofbizCurrency amount=invoiceItem.amount?if_exists isoCode=invoice.currencyUomId?if_exists/></#if></td>
			<#assign amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem))>
            <#assign totalAmountWithOutTax = totalAmountWithOutTax + amount > 
			<td style="border-bottom:solid 1px #000;text-align:right;"><@ofbizCurrency amount=(Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceItemTotal(invoiceItem)) isoCode=invoice.currencyUomId?if_exists/></td> </tr>      		
      		
      		</#if>
        </#list>
<!-- End of Product Description -->
       <tr>
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp;</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;">&nbsp;</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;">&nbsp;</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp;</td>
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp; </td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp; </td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp; </td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;">&nbsp;</td> 
       <td style="border-bottom:solid 1px #000;">&nbsp;</td> 
       </tr>
       
       <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> &nbsp; </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> 
       
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> 
 			${company.additionalTaxInfo?if_exists}
	   </td> <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"> </td>
        
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="3">Total Assessable Value</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> 
       <td style="border-bottom:solid 1px #000;text-align:right;"><@ofbizCurrency amount=totalAmountWithOutTax?if_exists isoCode=invoice.currencyUomId?if_exists /></td> </tr>
       
       <#list taxAdjustments as taxAdj>
       <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2"> &nbsp; </td>  
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2"></td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="3">${taxAdj.comments?if_exists}</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:right;">${taxAdj.sourcePercentage?if_exists} <#if taxAdj.sourcePercentage?exists> % </#if> </td> 
       <td style="border-bottom:solid 1px #000;text-align:right;"><@ofbizCurrency amount=taxAdj.amount?if_exists isoCode=invoice.currencyUomId?if_exists /></td> </tr>
       </#list>
       
       <#assign bed = Static["org.ofbiz.accounting.util.Converter"].rupeesInWords(bed)>
       <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2"> Exciseduty <br/>(in words) </td>  
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2"> ${bed?if_exists}</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="3">&nbsp;</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:right;">&nbsp; </td> 
       <td style="border-bottom:solid 1px #000;text-align:right;">&nbsp;</td> </tr>
       <#assign eduCess = Static["org.ofbiz.accounting.util.Converter"].rupeesInWords(eduCess)>
       <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2"> Edu. Cess <br/> (in words) </td>  
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2">${eduCess?if_exists}</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="3">&nbsp;</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:right;">&nbsp; </td> 
       <td style="border-bottom:solid 1px #000;text-align:right;">&nbsp;</td> </tr>
       <#assign shEduCess = Static["org.ofbiz.accounting.util.Converter"].rupeesInWords(shEduCess)>
       <tr><td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2"> S&H Edu.Cess <br/>(in words) </td>  
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="2">${shEduCess?if_exists}</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="3">&nbsp;</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:right;">&nbsp; </td> 
       <td style="border-bottom:solid 1px #000;text-align:right;">&nbsp;</td> </tr>
       
       <#assign currencyInWord = Static["org.ofbiz.accounting.util.Converter"].rupeesInWords(invoiceTotal)>
       <tr> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="4"> Total Invoice Value in Words Rupees: ${currencyInWord?if_exists}</td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;" colspan="3">
       Grand Total
       </td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;"></td> 
       <td style="border-bottom:solid 1px #000;text-align:right;"><@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists /></td> </tr>
       
       <tr> 
       <td style="border-bottom:solid 1px #000;" colspan="9">
       	Appropriate Certificate as Below: <br/>
       	Certificated that the particulars given above are true and correct and the Amount indicated represents the price actually charged and that there is no
       	flow of additional consideration directly or indirectly from the buyer. All Cases are subject to Bangalore jurisdiction. Interest will be Charged at 
       	the Rate of 24% P.A. after a month from the date of Invoice.
       </td> </tr>
       
       <tr>
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;" colspan="3">
       	<table style="width:100%;">
       	<tr>
		  <td style="border-bottom:solid 1px #000;"> 
		  	Date & time of issue of Invoice 
		  	
		  	<br/>
		  	<br/>
		  	<br/>
		  	<#assign invoiceFormattedDate = Static["org.ofbiz.base.util.UtilDateTime"].getFormattedDateTime(invoiceDate)>
		  	${invoiceFormattedDate?if_exists}
		  </td>
		</tr>
		<tr>
		  <td style="border-bottom:solid 1px #000;"> 
		  	Date & time of Removal of Goods :
		  	<br/>
		  	<br/>
		  	<br/>
		  	<br/>
		  </td>
		 </tr>
		 <tr>
		  <td> 
		  	Sales against Form C : ${formToIssue}
		  	<br/>
		  	<br/>
		  </td>       		
       	</tr>
       	</table>
       </td> 
       <td style="border-bottom:solid 1px #000;border-right:solid 1px #000;text-align:center;" colspan="3" valign="top">
       	Received the above goods in good Condition
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	<br/>
       	Receiver's Signature & Seal
       </td> 
       <td style="border-bottom:solid 1px #000;text-align:center;" colspan="3" valign="top">
        For ${companyName}
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>
        Authorized Signatory
        Signature of the owner of his
        Authorized agents
       </td> 
       </tr>
       
       <tr> 
       <td style="text-align:center;" colspan="9">
       	THIS IS A COMPUTER GENERATED INVOICE
       </td> </tr>
    </table>
    </tr>
																	
</table>