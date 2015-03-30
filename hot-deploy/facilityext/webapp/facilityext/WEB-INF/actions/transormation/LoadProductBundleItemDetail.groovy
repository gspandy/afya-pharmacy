import java.util.List;
import org.ofbiz.entity.condition.EntityCondition;

String productBundleId = parameters.productBundleId;
String makeSku = parameters.makeSku;

EntityCondition condition = EntityCondition.makeCondition("makeSku", makeSku);

println " condition  " + condition;

List productBundleItems = delegator.findList("ProductBundleItemDetail", condition, null, null, null, false);
context.productBundleItems = productBundleItems;