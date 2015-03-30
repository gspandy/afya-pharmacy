import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
import javolution.util.FastList;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator()
List paymentList = FastList.newInstance();
paymentList.add(EntityCondition.makeCondition("menuName", EntityOperator.EQUALS, request.getParameter("menuName")));
paymentList.add(EntityCondition.makeCondition("displayNameIsVisible",EntityOperator.EQUALS,"Y"));
List<String> orderByList = new ArrayList<String>();
orderByList.add("menuName");
List menuLists = delegator.findList("Menu", EntityCondition.makeCondition(paymentList, EntityOperator.AND), null, orderByList, null, false);
rows=[];
String userloginId =request.getParameter("userLoginId");
String check =" ";
for( GenericValue menu :menuLists){
	resultMap = [:];
	List userlist=delegator.findByAnd("UserLoginMenuAssoc", UtilMisc.toMap("menuId",menu.getString("menuId"),"userLoginId",request.getParameter("userLoginId"),"thruDate",null));
	if(UtilValidate.isNotEmpty(userlist)){
		if(menu.getString("menuId").equals(userlist.get(0).menuId) && check !=menu.getString("menuItemName")){
			resultMap.menuItemName = menu.getString("menuItemName");
			resultMap.menuId = menu.getString("menuId");
			resultMap.displayNameIsVisible="N";
			resultMap.userLoginId=userloginId;
			rows+=resultMap;
		}
	}
	else{
		if(check !=menu.getString("menuItemName")){
			resultMap.menuItemName = menu.getString("menuItemName");
			resultMap.menuId = menu.getString("menuId");
			resultMap.displayNameIsVisible="Y";
			resultMap.userLoginId=userloginId;
			rows+=resultMap;
		}
	}
	check =menu.getString("menuItemName");
}
if(UtilValidate.isNotEmpty(rows)){
	context.resultList = rows;
}
return "success"