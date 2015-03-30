<form name="taxforms" action="<@ofbizUrl>addTaxFormDetails</@ofbizUrl>" method="POST">
<div class="screenlet">
    <div class="screenlet-title-bar">
        <h3>Tax Form To Issue</h3>
    </div>
<div class="screenlet-body">
			 <table width="100%">
			 		<tr>
			 			<td width="10%">Form to <#if "PURCHASE_ORDER"=cart.orderType>Issue<#else>Receive</#if></td>
			 			<td width="10%">
			 			<select name="formToIssue">
			 					<option></option>
			 					<#--<option value="C Form"  <#if "C Form"==cart.getFormToIssue()?default("")>selected="selected"</#if>>C Form</option>-->
			 					<#--<option value="F Form"  <#if "F Form"==cart.getFormToIssue()?default("")>selected="selected"</#if>>F Form</option>-->
			 					<option value="H Form"  <#if "H Form"==cart.getFormToIssue()?default("")>selected="selected"</#if>>Tax Exempted</option>
                            	<option value="PRICEINCLUDETAX"  <#if "PRICEINCLUDETAX"==cart.getFormToIssue()?default("")>selected="selected"</#if>>Price Inclusive of Tax</option>
						<#--<option value="I Form"  <#if "I Form"==cart.getFormToIssue()?default("")>selected="selected"</#if>>I Form</option>-->
			 				</select>
			 			</td>
			 			<#--<td width="10%">Form Series No</td>
			 			<td width="5%">
			 				<input type="text" size="10" name="formToIssueSeriesNo" value="${cart.formToIssueSeriesNo?if_exists}"/>
			 			</td>
			 			<td width="10%">Form No</td>
			 			<td width="5%">
			 			<input type="text" size="10" name="formToIssueFormNo" value="${cart.formToIssueFormNo?if_exists}"/>
			 			</td>
			 			<td width="5%">Date</td>
			 			<td width="25%">
                		 <@htmlTemplate.renderDateTimeField name="formToIssueDate" value="${value!''}" className="" alert="" 
		                      title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="formToIssueDate" dateType="date" shortDateInput=false 
		                      timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
		                      hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			 			</td>-->
			 		</tr>
                 <tr>
                     <td><input type="submit" class="btn btn-success" value="Save Tax Form"/></td>
                 </tr>
			 </table>
</div>
<#--<div class="screenlet-body">
			 <table  width="100%">
			 		<tr>
			 			<td width="10%">Form to <#if "PURCHASE_ORDER"=cart.orderType>Receive<#else>Issue</#if></td>
			 			<td  width="10%">
			 				<select name="formToReceive">
			 					<option></option>
			 					<option <#if "E1 Form"==cart.getFormToReceive()?default("")>selected="selected"</#if> value="E1 Form">E1 Form</option>
			 					<option <#if "E2 Form"==cart.getFormToReceive()?default("")>selected="selected"</#if> value="E2 Form">E2 Form</option>
			 				</select>
			 			</td>
			 			<td width="10%">Form Series No</td>
			 			<td  width="5%">
			 			<input type="text" size="10" name="formToReceiveSeriesNo" value="${cart.formToReceiveSeriesNo?if_exists}"/></td>
			 			<td width="10%">Form No</td>
			 			<td width="5%">
			 				<input size="10" type="text" name="formToReceiveFormNo"  value="${cart.formToReceiveFormNo?if_exists}"/>
			 			</td>
			 			<td width="5%">Date</td>
			 			<td width="25%">
                		 	<@htmlTemplate.renderDateTimeField name="formToReceiveDate" value="${value!''}" className="" alert="" 
		                      title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="formToReceiveDate" dateType="date" shortDateInput=false 
		                      timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
		                      hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			 			</td>
			 		</tr>
			 		
			 		<tr>
			 			<td><input type="submit" class="btn btn-success" value="Save Tax Form"/></td>
			 		</tr>
			 </table>
</div>-->
</div>
</form>
