import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;

import com.smebiz.common.UtilDateTimeSME;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import com.smebiz.common.UtilDateTimeSME;


String partyId = parameters.partyId;
EntityCondition entityCondition = EntityCondition
.makeCondition(UtilMisc.toMap("partyIdTo",partyId));

Date joiningDate = null;
List<GenericValue> recs = null;

Date joiningHalfCalendaryearDate=null;
if(partyId){
	
	recs = delegator.findList("Employment",entityCondition,null,null,null,false);
	GenericValue employee = null;
	if(recs.size()>0){
		employee = recs.get(0);
	}

	if(employee==null){
		context.earnedLeaveList =[];
		return;
	}
	
	Timestamp fromTs= employee.getTimestamp("fromDate");
	joiningDate = new Date(fromTs.getTime());

	EntityCondition cond1 = EntityCondition.makeCondition("fromDate",EntityComparisonOperator.LESS_THAN,joiningDate);
	EntityCondition cond2 = EntityCondition.makeCondition("thruDate",EntityComparisonOperator.GREATER_THAN,joiningDate);
	EntityCondition cond3 = EntityCondition.makeCondition("periodTypeId",EntityComparisonOperator.EQUALS,"HALF_YEAR");
	

	EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(cond1,cond2,cond3));
	recs = delegator.findList("CustomTimePeriod",cond,null,null,null,false);

	if(recs){
		joiningHalfCalendaryearDate = recs.get(0).getDate("fromDate");
	}
}
System.out.println("joiningHalfCalendaryearDate "+joiningHalfCalendaryearDate);

	
if(joiningDate){


	entityCondition = EntityCondition
	.makeCondition(UtilMisc.toMap("partyId",partyId));

	EntityCondition cond2 = EntityCondition
	.makeCondition("fromDate",EntityComparisonOperator.GREATER_THAN_EQUAL_TO,joiningHalfCalendaryearDate);

	
	//"EmplEarnedLeaveRollUpView"
	recs = delegator.findList("EmplEarnedLeaveRollupView",EntityCondition.makeCondition(UtilMisc.toList(entityCondition,cond2)),null,UtilMisc.toList("fromDate"),null,false);

	System.out.println("RECORDS "+recs.size());
	
	earnedLeaveList = [];
	totalLeaves =0;

	int prevEarnedLeaves=0;
	recs.each { 
			rec ->	

					double interval = UtilDateTimeSME.getMonthInterval(joiningDate, rec.thruDate);

					leavesTaken=[];
					EntityCondition leave_cond1 = EntityCondition
					.makeCondition("fromDate",EntityComparisonOperator.GREATER_THAN_EQUAL_TO,new Timestamp(rec.fromDate.getTime()));
					EntityCondition leave_cond2 = EntityCondition
					.makeCondition("thruDate",EntityComparisonOperator.LESS_THAN_EQUAL_TO,new Timestamp(rec.thruDate.getTime()));

					EntityCondition leave_cond = EntityCondition.makeCondition(UtilMisc.toList(leave_cond1,leave_cond2,entityCondition));

					List orderBy = [];
					orderBy.add("fromDate");
					leaves = delegator.findList("EmplLeave",leave_cond,null,orderBy,null,false);

					totalLeaves =0;
					leaves.each{
						leave-> totalLeaves+=leave.paidDays;
					}
					
					System.out.println(" LEAVES ************ "+leaves);

					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTime(rec.fromDate);
					calendar.add(Calendar.DATE,-1);

					Date prevHalfYearEndDate = new Date(calendar.getTime().getTime());
					Date prevHalfYearStartDate = null;
					
					periods = delegator.findByAnd("CustomTimePeriod",UtilMisc.toMap("thruDate",prevHalfYearEndDate,"periodTypeId","HALF_YEAR"));
					int noOfPrevLeaves=0;
					
					if(periods){

						GenericValue customTimePeriod =  periods.getAt(0);
						prevHalfYearStartDate = customTimePeriod.getDate("fromDate");

						String customTimePeriodId = customTimePeriod.get("customTimePeriodId");

						System.out.println("CUSTOM TIMR PERIOD "+customTimePeriodId);
						/**
						 * get the previous half year earned leaves.
						 */ 
						 
						EntityCondition ec1 = EntityCondition
						.makeCondition(UtilMisc.toMap("partyId",partyId));

						EntityCondition ec2 = EntityCondition
						.makeCondition("customTimePeriodId",EntityComparisonOperator.EQUALS,customTimePeriodId);

						forPrevHalfYearEarnLeaves = delegator.findList("EmplEarnedLeaveRollupView",EntityCondition.makeCondition(UtilMisc.toList(ec1,ec2)),null,UtilMisc.toList("fromDate"),null,false);

						if(forPrevHalfYearEarnLeaves.size()>0)
							prevEarnedLeaves = forPrevHalfYearEarnLeaves.get(0).earnedLeaves;
						
						
						EntityCondition prevLeaveCond1 = EntityCondition.makeCondition(UtilMisc.toMap("partyId",partyId));
						EntityCondition prevLeaveCond2 = EntityCondition.makeCondition("fromDate",EntityComparisonOperator.GREATER_THAN_EQUAL_TO,new Timestamp(prevHalfYearStartDate.getTime()));
						EntityCondition prevLeaveCond3 = EntityCondition.makeCondition("thruDate",EntityComparisonOperator.LESS_THAN_EQUAL_TO,new Timestamp(prevHalfYearEndDate.getTime()));

						EntityCondition prevLeaveCond = EntityCondition.makeCondition(UtilMisc.toList(prevLeaveCond1,prevLeaveCond2,prevLeaveCond3));
						
						prevLeaves = delegator.findList("EmplLeave",prevLeaveCond,null,null,null,false);

						if(prevLeaves){
							prevLeaves.each{
								prevLeave ->
								noOfPrevLeaves +=prevLeave.paidDays;
								System.out.println(" *********** PREVIOUS LEAVES  "+prevLeave);
							}
						}
						
						System.out.println(" *********** PREVIOUS LEAVES  "+prevLeaves);
					}

					double col6 = noOfPrevLeaves>15?((noOfPrevLeaves-15)/10):0;
					
					earnedLeaveList.add("from":rec.fromDate,"to":rec.thruDate,"interval":interval,"prevEarnedLeaves":prevEarnedLeaves,
							"leaves":leaves,"noOfPrevLeaves":noOfPrevLeaves,"col6":col6,"totalLeaves":totalLeaves,"earnedLeaves":rec.earnedLeaves);
					
			}
	context.earnedLeaveList=earnedLeaveList;
}