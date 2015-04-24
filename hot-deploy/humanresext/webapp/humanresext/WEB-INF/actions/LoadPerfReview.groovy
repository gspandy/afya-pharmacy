import org.ofbiz.entity.*;
 import org.ofbiz.entity.condition.*;
 import org.ofbiz.entity.util.*;
 import org.ofbiz.base.util.*;
 import org.ofbiz.base.util.collections.*;
 import org.ofbiz.humanresext.util.HumanResUtil;
 import java.util.*;
 
 GenericValue emplPerfReview = delegator.findOne("EmplPerfReview",UtilMisc.toMap("emplPerfReviewId",parameters.reviewId),false);
 
 perfReviewId = emplPerfReview.getString("perfReviewId");
 GenericValue perfReview = delegator.findOne("PerfReviews",UtilMisc.toMap("perfReviewId",perfReviewId),false);
 
 context.perfPeriodFrom = perfReview.getTimestamp("periodStartDate");
 context.perfPeriodTo = perfReview.getTimestamp("periodThruDate");
 
 context.partyId = userLogin.partyId;
 context.overallRating= emplPerfReview.getString("overallRating");
 
 context.emplPerfReview = emplPerfReview;
 entities = [];
 entities = delegator.findList("PerfSectionAndAttributeForReviewer",EntityCondition.makeCondition("emplPerfReviewId",parameters.reviewId),null,null,null,false);
 
 Set sections = new HashSet();
 
 Map reviewerMap = new HashMap();
 Set reviewers = new HashSet();
 
 entities.each{
	 
	 entity->
	 		String reviewerId = entity.getString("reviewerId");	 		
	 		Map perfSectionMap =reviewerMap.get(reviewerId);
	 		if(perfSectionMap==null){
	 			perfSectionMap = new HashMap();
	 			reviewerMap.put(reviewerId,perfSectionMap);
	 			reviewers.add(reviewerId);
	 		}
	 		sectionName = entity.getString("sectionName");
	 		sections.add(sectionName);
	 		if(perfSectionMap.get(sectionName)==null){
	 			sectionmap = new HashMap();
	 			perfSectionMap.put(sectionName,sectionmap);
	 		}
	 		sectionmap = perfSectionMap.get(sectionName);
	 		sectionmap.sectionName=sectionName;
	 		sectionmap.sectionDesc = entity.getString("sectionDesc");
	 		if(sectionmap.get("attributes")==null){
	 			sectionmap.put("attributes",new LinkedList());
	 		}
	 		attributemap = [:];
	 		attributemap.attributeName = entity.getString("attributeName");
	 		attributemap.selfRating = entity.getString("selfRating");
	 		attributemap.appraiserRating = entity.getString("rating");
	 		sectionmap.get("attributes").add(attributemap);
 }
 
 System.out.println(" GLOBAL SECTION MAP ************************* \n"+reviewerMap);
 context.reviewerMap=reviewerMap;
 context.reviewers = reviewers;
 context.sections = sections;