
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


String isDisplay = "N";
if(menuItemName){
List menuList = delegator.findByAnd("Menu",UtilMisc.toMap("menuItemName",menuItemName));
	if(menuList){
		isDisplay = menuList.get(0).getString("displayNameIsVisible");
	}
}
context.isDisplay = isDisplay;
