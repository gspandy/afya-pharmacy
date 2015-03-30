<div class="screenlet">
	<table class="basic-table" cellspacing="0" cellpadding="2">
		<tr class="header-row">
			<td>Receivables Aging Summary</td>
			<td style="text-align:right;">Due &nbsp;&nbsp;</td>
			<td style="text-align:right;">Overdue &nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td width="30%">30 days</td>
			<td class="align-text"><label><@ofbizCurrency amount=s30DayAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
			<td class="align-text"><label><@ofbizCurrency amount=overDue30DayAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
		</tr>
		<tr>
			<td width="30%">Between 30 - 60 days</td>
			<td class="align-text"><label><@ofbizCurrency amount=s60DayAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
			<td class="align-text"><label><@ofbizCurrency amount=overDue60DayAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
		</tr>
		<tr>
			<td width="30%"> Between 60 - 90 days</td>
			<td class="align-text"><label><@ofbizCurrency amount=s90DayAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
			<td class="align-text"><label><@ofbizCurrency amount=overDue90DayAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
		</tr>
		<tr>
			<td width="30%"> Above 90 days</td>
			<td class="align-text"><label><@ofbizCurrency amount=s90DayAboveAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
			<td class="align-text"><label><@ofbizCurrency amount=above90DayAmount isoCode=defaultOrganizationPartyCurrencyUomId/></label></td>
		</tr>
	</table>
</div>