package org.ofbiz.accounting.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.webapp.control.Infrastructure;
import org.ofbiz.webapp.control.LoginWorker;

import freemarker.ext.beans.BeanModel;

public class Converter {
	private static double getPlace( String number ){
		switch( number.length() ){
			case 1:
				return DefinePlace.UNITS;
			case 2:
				return DefinePlace.TENS;
			case 3:
				return DefinePlace.HUNDREDS;
			case 4:
				return DefinePlace.THOUSANDS;
			case 5:
				return DefinePlace.TENTHOUSANDS;
			case 6:
				return DefinePlace.LAKHS;
			case 7:
				return DefinePlace.TENLAKHS;
			case 8:
				return DefinePlace.CRORES;
			case 9:
				return DefinePlace.TENCRORES;
		}//switch
		return 0.0;
	}// getPlace

	private static String getWord( int number ){
		switch( number ){
			case 1:
				return "One";
			case 2:
				return "Two";
			case 3:
				return "Three";
			case 4:
				return "Four";
			case 5:
				return "Five";
			case 6:
				return "Six";
			case 7:
				return "Seven";
			case 8:
				return "Eight";
			case 9:
				return "Nine";
			case 0:
				return "Zero";
			case 10:
				return "Ten";
			case 11:
				return "Eleven";
			case 12:
				return "Tweleve";
			case 13:
				return "Thirteen";
			case 14:
				return "Forteen";
			case 15:
				return "Fifteen";
			case 16:
				return "Sixteen";
			case 17:
				return "Seventeen";
			case 18:
				return "Eighteen";
			case 19:
				return "Ninteen";
			case 20:
				return "Twenty";
			case 30:
				return "Thirty";
			case 40:
				return "Forty";
			case 50:
				return "Fifty";
			case 60:
				return "Sixty";
			case 70:
				return "Seventy";
			case 80:
				return "Eighty";
			case 90:
				return "Ninty";
			case 100:
				return "Hundred";
		} //switch
		return "";
	} //getWord

	private static String cleanNumber( String number ){
		String cleanedNumber = "";
		
		cleanedNumber = number.replace( '.', ' ' ).replaceAll( " ", "" );
		cleanedNumber = cleanedNumber.replace( ',', ' ' ).replaceAll( " ", "" );
		if( cleanedNumber.startsWith( "0" ) )
			cleanedNumber = cleanedNumber.replaceFirst( "0", "" );
		
		return cleanedNumber;
	} //cleanNumber

	public static String convertNumber( String number ){
		number = cleanNumber( number );
		double num = 0.0;
		try{
			num = Double.parseDouble( number );
		}catch( Exception e ){
			return "Invalid Number Sent to Convert";
		} //catch

		String returnValue = "";
		while( num > 0 ){
			number = "" + (int)num;
			double place = getPlace(number);
			if( place == DefinePlace.TENS || place == DefinePlace.TENTHOUSANDS || place == DefinePlace.TENLAKHS || place == DefinePlace.TENCRORES ){
				int subNum = Integer.parseInt( number.charAt(0) + "" + number.charAt(1) );
			
				if( subNum >= 21 && (subNum%10) != 0 ){
					returnValue += getWord( Integer.parseInt( "" + number.charAt(0) ) * 10 ) + " " + getWord( subNum%10 ) ;
				} //if
				else{
					returnValue += getWord(subNum);
				}//else
			
				if( place == DefinePlace.TENS ){
					num = 0;
				}//if
				else if( place == DefinePlace.TENTHOUSANDS ){
					num -= subNum * DefinePlace.THOUSANDS;
					returnValue += " Thousand ";
				}//if
				else if( place == DefinePlace.TENLAKHS ){
					num -= subNum * DefinePlace.LAKHS;
					returnValue += " Lakh ";
				}//if
				else if( place == DefinePlace.TENCRORES ){
					num -= subNum * DefinePlace.CRORES;
					returnValue += " Crore ";
				}//if
			}//if
			else {
				int subNum = Integer.parseInt( "" + number.charAt(0) );
			
				returnValue += getWord( subNum );
				if( place == DefinePlace.UNITS ){
					num = 0;
				}//if
				else if( place == DefinePlace.HUNDREDS ){
					num -= subNum * DefinePlace.HUNDREDS;
					returnValue += " Hundred ";
				}//if
				else if( place == DefinePlace.THOUSANDS ){
					num -= subNum * DefinePlace.THOUSANDS;
					returnValue += " Thousand ";
				}//if
				else if( place == DefinePlace.LAKHS ){
					num -= subNum * DefinePlace.LAKHS;
					returnValue += " Lakh ";
				}//if
				else if( place == DefinePlace.CRORES ){
					num -= subNum * DefinePlace.CRORES;
					returnValue += " Crore ";
				}//if
			}//else
		}//while
		return returnValue;
	}//convert number

	/** precision is how many decimal places to convert to words. By default 2 **/
	public static String rupeesInWords(String number, String delimiter, Integer precision){
		String answer = null;
		if (delimiter == null) {
			delimiter = ".";
		}
		if (precision == null) {
			precision = 2;
		}
		int delimiterPos = number.indexOf(delimiter);
		try{
		BigDecimal decimalNumber = new BigDecimal(number);
			if(decimalNumber.compareTo(BigDecimal.ZERO) < 1){
				answer = "Rupees Zero and Paise Zero Only";
				return answer;
			}
		}catch(Exception e){
			
		}
		if (delimiterPos > 0){
			String left = number.substring(0, delimiterPos);
			String right = number.substring(delimiterPos + 1,  delimiterPos + 1 + precision);
			System.out.println(right);
			if(UtilValidate.isNotEmpty(convertNumber(right)))
				answer = "Rupees " + convertNumber(left) + " and Paise " + convertNumber(right) + " Only";
			else
				answer = "Rupees " +  convertNumber(left) + " Only";
		} else {
			answer = "Rupees " +  convertNumber(number) + " Only";
		}
		return answer;
	}
	
	public static String kwatchaInWords(String number, String delimiter, Integer precision){
		String answer = null;
		if (delimiter == null) {
			delimiter = ".";
		}
		if (precision == null) {
			precision = 2;
		}
		int delimiterPos = number.indexOf(delimiter);
		try{
		BigDecimal decimalNumber = new BigDecimal(number);
			if(decimalNumber.compareTo(BigDecimal.ZERO) < 1){
				answer = "Zero Kwacha Only";
				return answer;
			}
		}catch(Exception e){
			
		}
		if (delimiterPos > 0){
			String left = number.substring(0, delimiterPos);
			String right = number.substring(delimiterPos + 1,  delimiterPos + 1 + precision);
			System.out.println(right);
			if(UtilValidate.isNotEmpty(convertNumber(right)))
				answer = capitalizeFirstLetter(EnglishNumberToWords.convert(left)) + " kwacha and " + EnglishNumberToWords.convert(right) + " ngwee only";
			else
				answer = capitalizeFirstLetter(EnglishNumberToWords.convert(left)) + " kwacha only";
		} else {
			answer = capitalizeFirstLetter(EnglishNumberToWords.convert(number)) + " kwacha only";
		}
		return answer;
	}
	
	private static String capitalizeFirstLetter(String original){
	    if(original.length() == 0)
	        return original;
	    return original.substring(0, 1).toUpperCase() + original.substring(1);
	}
	
	@Deprecated
	/*
		Please use currencyInWords method
	*/
	public static String rupeesInWords(Object number){
		if(number == null)
			return "";
		return rupeesInWords(number.toString(),".",2);
	}
	
	public static String currencyInWords(Object number,String uomId) throws GenericEntityException{
		Double amount = new Double(number.toString());
		GenericDelegator delegator = Infrastructure.getDelegator();
		GenericValue partyAcctgPref = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", "Company"));
		String language = partyAcctgPref.get("language") == null ? "en" : (String) partyAcctgPref.get("language");
        Locale locale = new Locale(language, uomId);
        String amountInWord = UtilFormatOut.formatSpelledOutAmount(amount, locale);
        if("ZMW".equals(uomId))
        	uomId = "ZMK";
        try{
        	amountInWord = capitalizeFirstLetter(amountInWord) + " " + Currency.getInstance(uomId).getDisplayName();
        }catch(Exception e){
        	amountInWord = capitalizeFirstLetter(amountInWord);
        }
		return amountInWord;
	}
	
	
	public static void main(String[] args){
		com.ibm.icu.text.NumberFormat nf = new com.ibm.icu.text.RuleBasedNumberFormat(Locale.CANADA, com.ibm.icu.text.RuleBasedNumberFormat.SPELLOUT);
		String amount = nf.format(new BigDecimal(30085000.75));
		System.out.println("\n" + Currency.getInstance("AED").getDisplayName());
	}
	
	
	
	/** precision is how many decimal places to convert to words. By default 2 **/
	public static String rupeesInWordsDubai(String number, String delimiter, Integer precision){
		String answer = null;
		if (delimiter == null) {
			delimiter = ".";
		}
		if (precision == null) {
			precision = 2;
		}
		int delimiterPos = number.indexOf(delimiter);
		try{
		BigDecimal decimalNumber = new BigDecimal(number);
			if(decimalNumber.compareTo(BigDecimal.ZERO) < 1){
				answer = "Dirham Zero and Fils Zero Only";

				return answer;
			}
		}catch(Exception e){
			
		}
		if (delimiterPos > 0){
			String left = number.substring(0, delimiterPos);
			String right = number.substring(delimiterPos + 1,  delimiterPos + 1 + precision);
			System.out.println(right);
			
			if(UtilValidate.isNotEmpty(convertNumber(right)))
				answer = "Dirham " + convertNumber(left) + " and Fils " + convertNumber(right) + " Only";
			else
				answer = "Dirham " +  convertNumber(left) + " Only";
		} else {
			answer = "Dirham " +  convertNumber(number) + " Only";
		}
		return answer;
	}
			

	public static String rupeesInWordsDubai(Object number){
		if(number == null)
			return "";
		return rupeesInWordsDubai(number.toString(),".",2);
	}

	
	

class DefinePlace{
	public static final double UNITS = 1;
	public static final double TENS = 10 * UNITS;
	public static final double HUNDREDS = 10 * TENS;
	public static final double THOUSANDS = 10 * HUNDREDS;
	public static final double TENTHOUSANDS = 10 * THOUSANDS;
	public static final double LAKHS = 10 * TENTHOUSANDS;
	public static final double TENLAKHS = 10 * LAKHS;
	public static final double CRORES = 10 * TENLAKHS;
	public static final double TENCRORES = 10 * CRORES;
} //class
}


