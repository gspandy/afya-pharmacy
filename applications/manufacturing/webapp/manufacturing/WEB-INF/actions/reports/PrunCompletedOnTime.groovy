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
Map<String, ?> inPutMap = parameters;
Map<String, Object> filedMap = FastMap.newInstance();

currentStatusId = parameters.currentStatusId;
viewReportFor =  inPutMap.viewReportFor;
viewEnitytName = "WorkEffProductGoodsFac";

context.mainHeader = "Production Run(s)";

ModelEntity modelEntity = delegator.getModelEntity(viewEnitytName);
productionRunList = [];
 EntityCondition ec =null;
 
if(viewReportFor.equals("ON_TIME_RUN")){
  inPutMap.currentStatusId = ["PRUN_COMPLETED","PRUN_CLOSED"];
  inPutMap.currentStatusId_op = "in";  
  context.mainHeader = "Production Run(s) Completed On Time";
  List<EntityCondition> tmpList = FindServices.createConditionList(inPutMap, modelEntity.getFieldsUnmodifiable(), filedMap, delegator, context);    
  tmpList.add(EntityCondition.makeConditionWhere(" WE.estimated_Completion_Date >= WE.actual_Completion_Date and WE.actual_Completion_Date is not null and WE.estimated_Completion_Date is not null"));
  ec = EntityCondition.makeCondition(tmpList);
}


if(viewReportFor.equals("PENDING_RUN")){
   inPutMap.currentStatusId = ["PRUN_RUNNING", "PRUN_CREATED", "PRUN_CONFIRMED", "PRUN_SCHEDULED"];
   inPutMap.currentStatusId_op = "in";
    context.mainHeader = "Production Run(s) Pending";
    List<EntityCondition> tmpList = FindServices.createConditionList(inPutMap, modelEntity.getFieldsUnmodifiable(), filedMap, delegator, context); 
    ec = EntityCondition.makeCondition(tmpList);    
}

if(viewReportFor.equals("GENERAL_RUN")){
    List<EntityCondition> tmpList = FindServices.createConditionList(inPutMap, modelEntity.getFieldsUnmodifiable(), filedMap, delegator, context); 
    ec = EntityCondition.makeCondition(tmpList);
}
  /* Map resMap =  dispatcher.runSync("performFindList", UtilMisc.toMap("inputFields",inPutMap,"entityName",viewEnitytName,"viewSize",1000));
     if(UtilValidate.isNotEmpty(resMap.get('list')))
     		println((resMap.get('list')).size());
   */
     
   fieldToSel = ["facilityName", "productId", , "finishedGood", "quantityUomId", "status", "plannedQuantity", "quantityProduced", "workEffortName", "estimatedStartDate", "actualStartDate" , "estimatedCompletionDate", "actualCompletionDate"];
   
   productionRunList = delegator.findList(viewEnitytName, ec, UtilMisc.toSet(fieldToSel),null,null,false); 
    		
   context.productionRunList = productionRunList;

   