<#assign shoppingCart = session.getAttribute("shoppingCart")/>

<div class="screenlet-body">
<form action="/ordermgr/control/addAdjustment" method="POST" name="checkoutsetupform" id="checkoutsetupform">
<table  class="basic-table hover-bar" cellspacing="0">
	<thead>
		<tr class="header-row-2">
			<th> Product - Description </th>
			<th> Quantity </th>
			<th> UOM </th>
			<th> Price </th>
			<th> Total </th>
			<th style="text-align:center" colspan="3" width="40%"> Adjustments </th>
		</tr>
		<tr class="header-row-2">
			<th colspan="5" >&nbsp;</th>
			<th  width="20%">Adjustment Type</th>
			<th>Source %</th>
			<th>Amount</th>
		</tr>
	</thead>
	<tbody>
		<#if shoppingCart.items()?has_content>
		<#list shoppingCart.items() as cartLine>
			<tr>
				<td>
					<a target="EditProduct" href="/catalog/control/EditProduct?productId=${cartLine.getProductId()}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${cartLine.getProductId()}</a>
					${cartLine.getName()?default("")}<br/>
					<i>${cartLine.getDescription()?if_exists}</i><br/>
					Comments : ${cartLine.getItemComment()?if_exists}
				</td>
				<td>${cartLine.quantity}</td>
				<td>
					<#assign productId = cartLine.getProductId()>
					<#assign product = delegator.findOne("Product", {"productId":productId}, false)>
					<#assign uomGv = delegator.findOne("Uom", {"uomId":product.quantityUomId}, false)>
					<#if uomGv?has_content>
						${uomGv.description?if_exists}
					</#if>
				</td>
				<td><@ofbizCurrency amount="${cartLine.displayPrice}" isoCode=shoppingCart.currency/></td> 
				<td><@ofbizCurrency amount="${cartLine.getDisplayItemSubTotal()}" isoCode=shoppingCart.currency/></td>
				<td>
					<select name="adjustmentTypeId_o_${cartLine_index}">
						<#list adjustmentTypes as adj>
							<option value="${adj.orderAdjustmentTypeId}">${adj.description}</option>
						</#list>
					</select>
				</td>
				<td>
					<input type="text" size="4" class="adjustment" name="sourcePercentage_o_${cartLine_index}"/>
				</td>
				<td>
					<input type="text" size="4" class="adjustment" name="adjAmount_o_${cartLine_index}"/>
					<input type="hidden" name="_rowSubmit_o_${cartLine_index}" value="Y"/>
				</td>
			</tr>
		</#list>
		</#if>
		<tr>
			<td  colspan="7" align="center">
				<input type="submit" class="btn btn-success" value="Save Adjustments"/>
			</td>
		</tr>
	</tbody>
</table>
<script type="text/javascript">
	jQuery("#checkoutsetupform").validate({
		submitHandler:
		function(form) {
			form.submit();
		}
	});
</script>
</form>
    <table  class="basic-table hover-bar" cellspacing="5" cellpadding="5">
        <thead>
        <tr class="header-row-2">
            <th> Product - Description </th>
            <th> UOM </th>
            <th  width="20%">Adjustment Type</th>
            <th>Source %</th>
            <th>Amount</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <#if shoppingCart.items()?has_content>
            <#list shoppingCart.items() as cartLine>
                <#list cartLine.adjustments as adjustment>
            <tr>
                <td>
                    <a target="EditProduct" href="/catalog/control/EditProduct?productId=${cartLine.getProductId()}&amp;externalLoginKey=${externalLoginKey}" class="btn-link">${cartLine.getProductId()}</a>
                </td>
                <td>
                    <#assign productId = cartLine.getProductId()>
                    <#assign product = delegator.findOne("Product", {"productId":productId}, false)>
                    <#assign uomGv = delegator.findOne("Uom", {"uomId":product.quantityUomId}, false)>
                    <#if uomGv?has_content>
                        ${uomGv.description?if_exists}
                    </#if>
                </td>
                <td>
                    ${adjustment.comments}
                </td>
                <td>

                    <#if adjustment.sourcePercentage?exists>${adjustment.sourcePercentage}</#if>
                </td>
                <td>
                    <#if !adjustment.sourcePercentage?exists>
                        <#if adjustment.amount?exists>${adjustment.amount}</#if>
                    </#if>
                </td>
                <td>
                    <a href="deleteAdjustment?ci=${cartLine_index}&amp;adj_idx=${adjustment_index}" class="btn btn-danger btn-mini">Delete</a>
                </td>
            </tr>
                </#list>
            </#list>
        </#if>
        </tbody>
        </table>
</div>
