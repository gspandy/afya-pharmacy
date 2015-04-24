import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;

EntityCondition condition1 = EntityCondition.makeCondition("toPartyId","_NA_");
EntityCondition condition2 = EntityCondition.makeCondition("toPartyId",userLogin.getString("partyId"));

conditions =[condition1,condition2];

EntityCondition entityCondition = EntityCondition.makeCondition(conditions,EntityOperator.OR);
announcements = delegator.findList("Announcement",entityCondition,null,["-announcementId"],null,false);

announcementList = [];
announcements.each{
	announcement ->
			announcemap =[:];
			announcemap.announceid=announcement.getString("announcementId");
			announcemap.desc=announcement.getString("announcement");
			announcementList.add(announcemap);
}
//System.out.println(" ACCOUNCEMENTS "+announcementList);
context.announcements=announcementList;
