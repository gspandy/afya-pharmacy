import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.math.BigDecimal;
import javolution.util.*;

List entConds = new ArrayList();
Map mapSaleProd = FastMap.newInstance();

timeStampFrom =  UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-90);

entConds.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampFrom));
entConds.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
entConds.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
//entConds.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));


EntityCondition cond  = EntityCondition.makeCondition(entConds);
soldProductList = delegator.findList("OrderItemQuantityAmtAvgReportGrpByProduct", cond, null, null, null, false);

soldProductList.each{
	pd -> odersItm  = pd.getAllFields();
	
	if(pd.quantityOrdered != 0)
		odersItm.avgSalePrice = (pd.priceTotal/pd.quantityOrdered);
	else 
		odersItm.avgSalePrice = pd.priceTotal;
	
	mapSaleProd[pd.productId]=odersItm;
		
}

entConds.clear();

entConds.add(EntityCondition.makeCondition("actualCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampFrom));
entConds.add(EntityCondition.makeCondition("actualCompletionDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
entConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN,mapSaleProd.keySet()));
statusList = ["PRUN_COMPLETED","PRUN_CLOSED"];
entConds.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, statusList));

cond  = EntityCondition.makeCondition(entConds);

manuProductList = delegator.findList("WorkEffortAndGoodsAvgCost", cond, null, null, null, false);

List prodsSoldWithbestMargin = FastList.newInstance();

manuProductList.each{
	mpd -> TreeMap	manuProds = mpd.getAllFields();
	
	
	
	manuProds.quantityProduced = manuProds.quantityProduced == null ? 0.0 : manuProds.quantityProduced;
	manuProds.actualTotalCost = manuProds.actualTotalCost == null ? BigDecimal.ZERO : manuProds.actualTotalCost;
	
	println(" Quatity Produced and Total Cost of Manu ====>" + manuProds.quantityProduced +"--" + manuProds.actualTotalCost);
	
	if(manuProds.quantityProduced != 0)
		manuProds.avgMfgCost =  new BigDecimal(manuProds.actualTotalCost/manuProds.quantityProduced);
	else
		manuProds.avgMfgCost =  new BigDecimal(manuProds.actualTotalCost);
		
	soldProd = mapSaleProd[manuProds.productId];
	BigDecimal margin = BigDecimal.ZERO;
	
	if((manuProds.avgMfgCost).compareTo(BigDecimal.ZERO)!= 0)		
		margin = ((soldProd.avgSalePrice - manuProds.avgMfgCost)/manuProds.avgMfgCost)*100;
	else
		margin = new BigDecimal(100);
	
	manuProds.avgSalePrice = (soldProd.avgSalePrice).setScale(2,BigDecimal.ROUND_HALF_UP);
	
	manuProds.avgMfgCost = (manuProds.avgMfgCost).setScale(2,BigDecimal.ROUND_HALF_UP);
	
	manuProds.maginValue = margin.setScale(2,BigDecimal.ROUND_HALF_UP);
	
 	prodsSoldWithbestMargin.add(manuProds);
 	
}

/*Collections.sort(prodsSoldWithbestMargin, new Comparator<TreeMap>(){ 
@Override
public int compare(TreeMap o1, TreeMap o2){ 
 return (o2.maginValue).compareTo(o1.maginValue);
}

});*/

context.prodsSoldWithbestMargin = prodsSoldWithbestMargin;
context.timeStampFrom = timeStampFrom; 
