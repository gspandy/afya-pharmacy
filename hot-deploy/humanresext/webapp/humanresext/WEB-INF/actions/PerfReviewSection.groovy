/**
 * 
 */
 import java.math.BigDecimal
 import java.util.*
 import java.sql.Timestamp
 import org.ofbiz.entity.*
 import org.ofbiz.entity.condition.*
 import org.ofbiz.entity.util.*
 import org.ofbiz.base.util.*
 import org.ofbiz.base.util.collections.*

 
 sectionList = session.getAttribute("perfSectionList");

if(sectionList==null){
	
	EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLogin.partyId));
	 fulfillments = delegator.findByCondition("EmplPositionFulfillment",condition,null,["fromDate"]);
	 
	 fulfillment = null;
	 if(fulfillments){
		 fulfillment = fulfillments.get(0);
	 }
	 
	 positionEntity = fulfillment.getRelatedOne("EmplPosition");
	 emplPositionTypeId = positionEntity.getString("emplPositionTypeId");
	 System.out.println("Position "+emplPositionTypeId);
	 
 condition = EntityCondition.makeCondition(UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId));
 templates = delegator.findByCondition("PerfReviewTemplate",condition,null,null);
  
 template = null;
 if(templates){
	 template = templates.get(0);
 }
 
 
 perfTemplateId = template.getString("perfTemplateId");
 
 condition = EntityCondition.makeCondition(UtilMisc.toMap("perfTemplateId", perfTemplateId));
 sections = delegator.findByCondition("PerfReviewTemplateToSectionView",condition,null,null);
 
 
 sectionList=[];
 if(sections){
	 sections.each{ 
		 
		 section -> 
		 	sectionList.add([ "sectionId" : section.perfReviewSectionId, "sectionName":section.sectionName])
		 
	 }
 }
 
 session.setAttribute("perfSectionList",sectionList);
 
}

 context.sectionList=sectionList;
 currentSection = sectionList.get(0);

 if(parameters.sectionId==null){
	 parameters.sectionId=currentSection.sectionId;
 }
 
 