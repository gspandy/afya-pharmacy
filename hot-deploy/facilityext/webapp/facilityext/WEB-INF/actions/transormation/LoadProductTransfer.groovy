import org.ofbiz.entity.condition.*;

String productTransferId = parameters.productTransferId;
if(!productTransferId)
	productTransferId = request.getAttribute("productTransferId");

context.productTransfer = delegator.findOne("ProductTransfer", false, "productTransferId", productTransferId);

context.productTransferId = productTransferId;
EntityCondition ptCondition = EntityCondition.makeCondition("productTransferId", productTransferId);
context.transferTxns = delegator.findList("ProductTransferTxn", ptCondition, null, null, null, false);

EntityCondition fromLocationCondition = EntityCondition.makeCondition("type", "SRC");
EntityCondition condition = EntityCondition.makeCondition(ptCondition, fromLocationCondition);
context.fromLocations = delegator.findList("ProductTransferLocation", condition, null, null, null, false);

EntityCondition toLocationCondition = EntityCondition.makeCondition("type", "DEST");
condition = EntityCondition.makeCondition(ptCondition, toLocationCondition);
context.toLocations = delegator.findList("ProductTransferLocation", condition, null, null, null, false);
