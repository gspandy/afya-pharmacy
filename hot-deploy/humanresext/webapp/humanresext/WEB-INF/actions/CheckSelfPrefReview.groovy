 import org.ofbiz.entity.*
 import org.ofbiz.entity.condition.*
 import org.ofbiz.entity.util.*
 import org.ofbiz.base.util.*
 import org.ofbiz.base.util.collections.*
 import org.ofbiz.humanresext.util.HumanResUtil;
 
 prefReviewId = session.getAttribute("PERF_REVIEW_ID");
 
 EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewId",prefReviewId,"partyId",userLogin.partyId));
 prefReviews = delegator.findList("EmplPerfReview",condition,null,["-emplPerfReviewId"],null,false); 
 
 if(prefReviewId!=null)
	 context.perfEnabled=true;
 else
	 context.perfEnabled=false;
 
 if(prefReviews!=null && prefReviews.size()>0){
	 context.prefReviewExists=true;
	 GenericValue emplPerfReview = prefReviews.get(0);
	 String emplPrefReviewId=emplPerfReview.getString("emplPerfReviewId");
	 session.setAttribute("emplPerfReview",emplPerfReview);
	 context.reviewId = emplPrefReviewId;
 }else{
	 context.prefReviewExists=false;
 }
 
 context.departmentId=session.getAttribute("departmentId");
 