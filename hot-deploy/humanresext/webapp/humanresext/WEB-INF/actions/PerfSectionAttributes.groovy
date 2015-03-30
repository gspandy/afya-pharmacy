import java.math.BigDecimal
 import java.util.*
 import java.sql.Timestamp
 import org.ofbiz.entity.*
 import org.ofbiz.entity.condition.*
 import org.ofbiz.entity.util.*
 import org.ofbiz.base.util.*
 import org.ofbiz.base.util.collections.*

 
 selectedSection = null;
 sectionList = session.getAttribute("perfSectionList");
 	
 sectionList.each{ section -> 
			if(parameters.sectionId==section.sectionId)
			{
				selectedSection=section;
			}
 	}

	 if(selectedSection==null){
		 selectionSection = sectionList.get(0);
	 }
	 
	 
	 context.selectedSection=selectedSection;
	 
	 if(selectedSection)
	 {
		condition = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewSectionId",selectedSection.sectionId));
	 	attributes = delegator.findByCondition("PerfReviewSectionAttribute",condition,null,null);
	  
	 	attributeList = [];
	 	attributes.each{
		attribute -> attributeList.add(["attrName":attribute.getString("attributeName"),
		                                 "attrId":attribute.getString("attributeId")
		                                 //"subjective":attribute.getString("isSubjective")		                                 
		                                 ]);		 
	 	}	 
	 	if(attributeList){
	 		context.attributeList= attributeList;
	 	}
	 }
 