package org.ofbiz.issuetracking.util;

import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

/**
 * @author sandeep
 *
 */
public class TimeUtil {
	
	public static double violationAmount(Timestamp refernce, Timestamp actual, Number permissibleHoursDiff){
		double permissibleMilliSecondsDiff = permissibleHoursDiff.doubleValue() * 60 * 60 * 1000;
		double actualMilliSecondsDiff = UtilDateTime.getInterval(refernce, actual);
		
		double delta = 0; 
		
		if(permissibleMilliSecondsDiff < actualMilliSecondsDiff){
			delta = actualMilliSecondsDiff - permissibleMilliSecondsDiff;
			delta = delta / (1000 * 60 * 60);
		}
		return delta ;
	}
}
