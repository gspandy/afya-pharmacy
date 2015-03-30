import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

EntityCondition structureIdCondition = EntityCondition.makeCondition("salaryStructureId", parameters.salaryStructureId);
EntityCondition nonFormulaCondition = EntityCondition.makeCondition("salaryComputationTypeId", EntityOperator.NOT_EQUAL , "FORMULA");
EntityCondition condition = EntityCondition.makeCondition(structureIdCondition, nonFormulaCondition);

context.heads = delegator.findList("SalaryStructureHeadDetail", condition, null, null, null, true);

structureIdCondition = EntityCondition.makeCondition("salaryStructureId", parameters.salaryStructureId);
EntityCondition slabCondition = EntityCondition.makeCondition("salaryComputationTypeId", EntityOperator.EQUALS , "SLAB");
condition = EntityCondition.makeCondition(structureIdCondition, slabCondition);


context.slabs = delegator.findList("SalaryStructureHeadDetail", condition, null, null, null, true);

if(context.slabs.size()>0){
	context.hasSlabs=true;
}else{
	context.hasSlabs=false;
}