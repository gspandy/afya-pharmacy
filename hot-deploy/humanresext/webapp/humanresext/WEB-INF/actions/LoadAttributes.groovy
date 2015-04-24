 import org.ofbiz.entity.*;
 import org.ofbiz.entity.condition.*;
 import org.ofbiz.entity.util.*;
 import org.ofbiz.base.util.*;
 import org.ofbiz.base.util.collections.*;
 import org.ofbiz.humanresext.util.HumanResUtil;
 
 sectionId = parameters.sectionId;
 
 if(sectionId!=null){	 
	 Map perfGlobalMap = session.getAttribute("perfGlobalMap");
	 context.selectedSection = perfGlobalMap.get(sectionId);
	 parameters.sectionId=sectionId;
 }else{
	 sections = request.getAttribute("perfSections");
	 sectionmap = sections.get(0);
	 sectionId = sectionmap.get("sectionId");
	 context.selectedSection = session.getAttribute("perfGlobalMap").get(sectionId);
	 parameters.sectionId=context.selectedSection.sectionId;
 }
 
 
 EntityCondition condition = EntityCondition.makeConditionDate("fromDate","thruDate");
 ratings = delegator.findByCondition("PerfRating",condition,null,["description"]);
 ratingList = [];
 ratings.each{
	 
	 rating-> ratingmap = [:];
	 			ratingmap.rating=rating.getString("rating");
	 			ratingmap.ratingDesc=rating.getString("description");
	 			ratingList.add(ratingmap);
 }
 context.ratingList =ratingList; 
 reviewId = parameters.reviewId;
 
 if(reviewId==null){
	 parameters.reviewId = request.getAttribute("SELF_PREF_REVIEW_ID");
	 reviewId = parameters.reviewId;

 }
 
 
 attributeList = context.selectedSection.get("attributeList");
 int mgrScore=0;
 int selfScore=0;
 if(attributeList!=null){
	 attributeList.each{
	 attribute ->
	 		ratingStr  = attribute.get("selfRating");
	 		mgrRatingStr = attribute.get("rating");
	 	    weightageStr = context.selectedSection.get("weightage");
	 		int rating =0;
	 		int mgrRating=0;
	 		if(ratingStr!=null)
	 			rating=Integer.parseInt(ratingStr).intValue();
	 		
	 		if(mgrRatingStr!=null)
	 			mgrRating=Integer.parseInt(mgrRatingStr).intValue();
	 		
	 		weightage = Float.parseFloat(weightageStr).floatValue();
	 		mgrScore += (weightage * mgrRating);
	 		selfScore += (weightage * rating);
	 }
 }else{
		ratingStr  = attribute.get("selfRating");
 		mgrRatingStr = attribute.get("rating");
 		weightage = context.selectedSection.get("weightage");
 		rating =0;
 		mgrRating=0;
 		if(ratingStr!=null)
 			rating=Integer.parseInt(ratingStr).intValue();
 		
 		if(mgrRatingStr!=null)
 			mgrRating=Integer.parseInt(mgrRatingStr).intValue();

 		mgrScore = (weightage * mgrRating);
 		selfScore = (weightage * rating);
 
 }
 
 
 context.mgrScore=mgrScore;
 context.selfScore=selfScore;
 
 perfReviewEntity = null;
 
 perfReviewEntity = delegator.findOne("EmplPerfReview",UtilMisc.toMap("emplPerfReviewId",reviewId),false);
 
 if(perfReviewEntity!=null){
	 context.status=perfReviewEntity.getString("statusType");
	 context.rating = perfReviewEntity.getString("overallRating");
	 context.rating = context.rating==null?"_NA_":context.rating;
	 
	 perfmap=[:];
	 perfmap.comments = perfReviewEntity.getString("comments");
	 perfmap.feedback= perfReviewEntity.getString("feedback");
	 context.perfmap = perfmap;
	 context.disagreedComments = perfReviewEntity.getString("disAgreedComments");
 }else{
	 context.status="_NA_";
	 context.disagreedComments="";
 }
 
 
 //load Managers
 if(context.status=="PERF_REVIEWED_BY_MGR" || context.status=="PERF_REVIEW_AGREED" || context.status=="PERF_REVIEW_COMPLETE"){
	 context.reviewerId=request.getAttribute("reviewerId");
	 if(context.reviewerId==null)
		 context.reviewerId=request.getParameter("reviewerId");
	
	 emplPerfReview = session.getAttribute("emplPerfReview");
	 partyId = emplPerfReview.getString("partyId");
	 context.reviewers = org.ofbiz.humanresext.perfReview.PerfReviewWorker.getReviewers(request,reviewId);
 
 }
 
 
