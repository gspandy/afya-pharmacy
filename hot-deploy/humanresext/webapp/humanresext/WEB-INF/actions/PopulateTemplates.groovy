 import org.ofbiz.entity.*
 import org.ofbiz.entity.condition.*
 import org.ofbiz.entity.util.*
 import org.ofbiz.base.util.*
 import org.ofbiz.base.util.collections.*
 import org.ofbiz.humanresext.util.HumanResUtil;
 import java.util.Calendar;
 import java.sql.Timestamp;
 
 
 Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
 
 EntityCondition cond = EntityCondition.makeCondition("emplPositionTypeId",EntityOperator.NOT_EQUAL,"_NA_");
 List<GenericValue> positionValues = delegator.findList("EmplPositionType",cond,null,null,null,true);
 
 positions = [];
 
 positionValues.each{
	 	positionValue->
	 		position=[:];
	 		position.id=positionValue.getString("emplPositionTypeId");
	 		position.desc=positionValue.getString("description");
	 		positions.add(position);	 
 }
 
 
 Map positionMap = new HashMap();

 positions.each{
	 	position->
	 		 EntityCondition condition = EntityCondition.makeCondition(["emplPositionTypeId":position.id]);
	 		 templates = delegator.findByCondition("PerfReviewTemplate",condition,null,null);
	 		 
	 		 templates.each{
	 			 template ->
	 			 		if(positionMap.get(position.id)==null){
	 			 			templates=[];
	 			 		}else{
	 			 			templates = positionMap.get(position.id);
	 			 		}
	 			 		templates.add(template.getString("perfTemplateId"));
	 			 		positionMap.put(position.id,templates);
	 		 }
 
 }
 
 context.positionMap=positionMap;
 context.positions = positions;
 System.out.println(positionMap);