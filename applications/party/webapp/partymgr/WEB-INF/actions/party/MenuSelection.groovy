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
String check =" ";
if(UtilValidate.isNotEmpty(request.getParameter("menuName"))){
	paymentList.add(EntityCondition.makeCondition("menuName", EntityOperator.EQUALS, request.getParameter("menuName")));
}
paymentList.add(EntityCondition.makeCondition("displayNameIsVisible",EntityOperator.EQUALS,"Y"));
List<String> orderByList = new ArrayList<String>();
orderByList.add("menuName");
List menuLists = delegator.findList("Menu", EntityCondition.makeCondition(paymentList, EntityOperator.AND), null, orderByList, null, false);
String userloginId =request.getParameter("userLoginId");
rows=[];

for( GenericValue menu :menuLists){
	resultMap = [:];
	List userlist=delegator.findByAnd("UserLoginMenuAssoc", UtilMisc.toMap("menuId",menu.getString("menuId"),"userLoginId",request.getParameter("userLoginId"),"thruDate",null));
	if(UtilValidate.isNotEmpty(userlist)){
		if(menu.getString("menuId").equals(userlist.get(0).menuId) && check !=menu.getString("menuName")){
			resultMap.menuName = menu.getString("menuName");
			resultMap.menuId = menu.getString("menuId");
			resultMap.userLoginId=userloginId;
			resultMap.displayNameIsVisible="N";
			rows+=resultMap;
		}
	}
	else{
		if(check !=menu.getString("menuName")){
			resultMap.menuName = menu.getString("menuName");
			resultMap.menuId = menu.getString("menuId");
			resultMap.userLoginId=userloginId;
			resultMap.displayNameIsVisible= menu.getString("displayNameIsVisible");
			rows+=resultMap;
		}
	}
	check =menu.getString("menuName");
}
if(UtilValidate.isNotEmpty(rows)){
	context.resultList = rows;
}
return "success"
