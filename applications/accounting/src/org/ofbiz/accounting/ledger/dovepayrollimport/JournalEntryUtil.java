package org.ofbiz.accounting.ledger.dovepayrollimport;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 11/6/14
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class JournalEntryUtil {

    public static String getTheTransactionalDate(String csvData){
        String[] strings =  csvData.split(",");
        String transactionDate = "";
        int length = strings.length;
        for (String string : strings){
            if (string.indexOf("Journal Listing")>=0){
                transactionDate = strings[length-2]+","+strings[length-1];
            }
        }
        return transactionDate;
    }

    public static Timestamp getDateFormat(String dateInString){
        String formattedDate = StringUtils.replaceEachRepeatedly(dateInString,new String[]{"\""},new String[]{""});
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy.");
        Date date = null;
        try {
            date= formatter.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getCurrentTimeStamp(date);
    }

    public static Timestamp getCurrentTimeStamp(Date date){
        return new Timestamp(date.getTime());
    }

    public static String indianFormat(BigDecimal n) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        if (n.doubleValue() < 100000) {
            return formatter.format(n.setScale(2, 1).doubleValue());
        }
        StringBuffer returnValue = new StringBuffer();
        String value = n.setScale(2, 1).toString();
        String intPart = value.substring(0, value.indexOf("."));
        String decimalPart = value.substring(value.indexOf("."), value.length());
        formatter.applyPattern("#,##");
        returnValue.append(formatter.format(new BigDecimal(intPart).doubleValue() / 1000)).append(",");
        returnValue.append(intPart.substring(intPart.length() - 3, intPart.length())).append(decimalPart);
        return returnValue.toString();
    }

}
