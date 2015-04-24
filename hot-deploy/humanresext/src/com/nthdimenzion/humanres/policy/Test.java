package com.nthdimenzion.humanres.policy;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.ofbiz.base.util.UtilDateTime;

import com.smebiz.common.UtilDateTimeSME;

public class Test {

static	Calendar calendar = GregorianCalendar.getInstance();

		public static void main(String[] args){
			
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 01);
			calendar.set(Calendar.MONTH, 3);
			calendar.set(Calendar.YEAR, 2009);
			
			Date fromDate = new Date(calendar.getTime().getTime());
			System.out.println("Input "+fromDate );
			calendar.add(Calendar.DATE,-1);
			fromDate = new Date(calendar.getTime().getTime());
			System.out.println(" fromDate "+fromDate);
			
		}
		
		
		
}
