

<script>
	function submitForm(formObj){
		var productId = $F('productId');
		if(type(productId)!='undefined'){
			formObj.action = formObj.action+"&productId="+productId;
			formObj.submit();
			return true;
		}
		return false;
	}
</script>

<form method="post" action="/catalog/control/EditProductInventoryItems?showAllFacilities=Y&amp;externalLoginKey=${externalLoginKey}" style="margin: 0;" name="EditProductForm">
	<table border="0" style="width:100%">
		<!-- <input type="text" size="20" maxlength="20" id="productId" name="productId" value=""/> -->
		<#--  <span class="field-lookup">
			<input type="text" name="productId" size="20" maxlength="20" id="productId"  value="${productId?if_exists}" style=""/>
				<a href="javascript:call_fieldlookup2(document.EditProductForm.productId,'LookupProduct');"></a>
		<span>  -->
		<tr>
			<td>
				<div>${uiLabelMap.ProductEditProductWithProductId}:&nbsp;&nbsp;&nbsp;</div>
				<@htmlTemplate.lookupField formName="EditProductForm" name="productId" id="productId" fieldFormName="LookupProduct"/>
			</td>
		</tr>
		<tr>
			<td><input type="submit" onclick="return submitForm(this.form);" value="View Inventory Summary" class="btn btn-primary"/></td>
		</tr>
	</table>
</form>

<br/>