import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

salaryStructureId = parameters.salaryStructureId;
structure = delegator.findOne("SalaryStructure", false, "salaryStructureId", parameters.salaryStructureId);
geoId = structure.get("geoId"); 

EntityCondition assocCondition = EntityCondition.makeCondition("salaryStructureId", salaryStructureId);
addedSalaryHeadAssocs = delegator.findList("SalaryStructureHead", assocCondition, null, null, null, false);
EntityCondition geoCondition = EntityCondition.makeCondition("geoId", geoId);
allSalaryHeads = delegator.findList("SalaryHead", geoCondition, null, null, null, false);

addedSalaryHeads = [];
for(int i=0 ; i < addedSalaryHeadAssocs.size() ; ++i){
	salaryHead = addedSalaryHeadAssocs.get(i).getRelatedOne("SalaryHead");
	addedSalaryHeads.add(salaryHead);
	allSalaryHeads.remove(salaryHead);
}

context.unaddedSalaryHeads = allSalaryHeads;
context.addedSalaryHeads = addedSalaryHeads;
context.fromDate = parameters.fromDate;