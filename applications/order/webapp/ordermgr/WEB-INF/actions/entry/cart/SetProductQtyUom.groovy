import org.ofbiz.base.util.UtilValidate;


productId = parameters.add_product_id;

if(UtilValidate.isNotEmpty(productId)) {
	product = delegator.findOne("Product", ["productId" : productId], true);
	if(UtilValidate.isNotEmpty(product) && UtilValidate.isNotEmpty(product.quantityUomId) && product.quantityUomId != null) {
		qtyUomId = product.quantityUomId;
		uom = delegator.findOne("Uom", ["uomId" : qtyUomId], true);
		quantityUom = uom.description;
		request.setAttribute("qtyUom", quantityUom);
		return "success";
	} else {
		request.setAttribute("qtyUom", "");
		return "success";
	}
} else {
	request.setAttribute("qtyUom", "");
	return "success";
}
