import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;
import javolution.util.*;
import org.ofbiz.common.FindServices;
import org.ofbiz.entity.model.ModelEntity;

List conds = new ArrayList();
conds.clear();
Map<String, Object> inPutMap = parameters;

currentStatusId = parameters.currentStatusId;
viewReportFor =  inPutMap.viewReportFor;
viewEnitytName = "WorkEffortProdGoodsAvgCost";

inPutMap.currentStatusId = "PRUN_CLOSED";
inPutMap.currentStatusId_op = "equals";

    Map resMap =  dispatcher.runSync("performFindList", UtilMisc.toMap("inputFields",inPutMap,"entityName",viewEnitytName,"viewSize",1000));
     if(UtilValidate.isNotEmpty(resMap.get('list')))
     		println((resMap.get('list')).size());
	context.productionRunList = resMap.get('list');
