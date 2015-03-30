
import java.util.*;
import java.text.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.entity.GenericValue;
import java.sql.Timestamp;
import java.text.*;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.widget.html.*;



routeId = request.getParameter("routeId");
action = request.getParameter("action");
routeName = request.getParameter("routeName");
origin = request.getParameter("origin");
destination = request.getParameter("destination");

List routes = null;
EntityCondition condition = null;
if(routeName){
    condition = EntityCondition.makeCondition("routeName", EntityOperator.LIKE, routeName + "%")
}
routes = delegator.findList("ShipmentRoute", condition, null, null, null, false);

 addRoute = [] as ArrayList;
 for(GenericValue routeList : routes){
        oneRow = [:];
        oneRow.routeId = routeList.routeId;
        oneRow.routeName = routeList.routeName;
        oneRow.origin = routeList.origin;
        oneRow.destination = routeList.destination;
        addRoute.add(oneRow);
}
context.listRoutes = addRoute;
