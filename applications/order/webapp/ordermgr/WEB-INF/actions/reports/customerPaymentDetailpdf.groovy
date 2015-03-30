import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtilProperties;



String partyId = parameters.partyId;
String fromDate = parameters.fromDate;
String thruDate = parameters.thruDate;
if(UtilValidate.isEmpty(thruDate) && UtilValidate.isNotEmpty(fromDate)){
	thruDate = UtilDateTime.nowTimestamp().toString();
}
serverUrl = EntityUtilProperties.getPropertyValue("general.properties", "server.url", delegator);
context.serverUrl = serverUrl;
context.put("thruDate",thruDate);

if(UtilValidate.isEmpty(fromDate))
	return;

SQLProcessor sqlProcMain = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));

Comparator<Map<String, Object>> mapComparator = new Comparator<Map<String, Object>>() {
	public int compare(Map<String, Object> m1, Map<String, Object> m2) {
		return ((String) m1.get("transactionDate")).compareTo( (String) m2.get("transactionDate"));
	}
}

List<String> customerIds = new ArrayList<String>();
if(UtilValidate.isEmpty(partyId)){
	String customerQuery = getCustomerPartyIdQuery();
	sqlProcMain.prepareStatement(customerQuery);
	ResultSet cutomerResult = sqlProcMain.executeQuery();
	while (cutomerResult.next()){
		customerIds.add(cutomerResult.getString(1));
	}
}


String mainQuery = buildQuery(customerIds,partyId,fromDate,thruDate);
sqlProcMain.prepareStatement(mainQuery);
ResultSet mainResult = sqlProcMain.executeQuery();
List mainList = new ArrayList();
while (mainResult.next()){
	Map<String,Object> map = new HashMap<String,Object>();
	map.put("partyId", mainResult.getString(1));
	map.put("currency", mainResult.getString(2));

	String openingBalanceQuery = buildQueryForOpeningBalance(mainResult.getString(1),mainResult.getString(2),fromDate);
	sqlProcMain.prepareStatement(openingBalanceQuery);
	ResultSet resultSetOpeningBalance = sqlProcMain.executeQuery();
	while (resultSetOpeningBalance.next()){
		map.put("amount", resultSetOpeningBalance.getBigDecimal(2) == null ? BigDecimal.ZERO : resultSetOpeningBalance.getBigDecimal(2) );
		map.put("debiCredit", resultSetOpeningBalance.getString(4));
	}
	mainList.add(map);
}


Map<Map,List> groupedOpeningBalanceMap = new HashMap<Map, List>();
for(Map mainMap : mainList){
	String currencyKey = mainMap.get("currency");
	String mapPartyId = mainMap.get("partyId");
	Map<String,Object> map = new HashMap<String,Object>();
	BigDecimal debit = BigDecimal.ZERO;
	BigDecimal credit = BigDecimal.ZERO;
	for(Map obMap : mainList){
		if( currencyKey.equals(obMap.get("currency")) && mapPartyId.equals(obMap.get("partyId")) ){
			if( "D".equals(obMap.get("debiCredit")) )
			 debit = (BigDecimal) obMap.get("amount");
			if( "C".equals(obMap.get("debiCredit")) )
			 credit = (BigDecimal) obMap.get("amount");
			map.put("partyId", obMap.get("partyId"));
			map.put("amount", credit.subtract(debit));
			map.put("currency", currencyKey);
			List itemDetailList = getItemDetail(obMap.get("partyId"),currencyKey,"C",thruDate);
			Collections.sort(itemDetailList, mapComparator);
			groupedOpeningBalanceMap.put(map, itemDetailList);
		}
	}
}


sqlProcMain.close();
context.put("groupedOpeningBalanceMap",groupedOpeningBalanceMap);

def getItemDetail(String partyId,String currency,String flag,String thruDate){
	SQLProcessor sqlProc1 = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
	String query = buildCreditOrDebitQuery(partyId,currency,flag,thruDate);
	sqlProc1.prepareStatement(query);
	ResultSet result = sqlProc1.executeQuery();
	List itemDetailList = new ArrayList();
	while ( result.next() ){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("transactionDate", result.getString(1));
		map.put("transactionType", result.getString(2));
		map.put("transactionId", result.getString(3));
		if("CUSTOMER_PAYMENT".equals(result.getString(2)) || "CUSTOMER_DEPOSIT".equals(result.getString(2)) || "CUST_RTN_INVOICE".equals(result.getString(2)) 
			|| "DEBIT_MEMO".equals(result.getString(2)) || "INTEREST_RECEIPT".equals(result.getString(2)) )
			map.put("transactionAmount", result.getBigDecimal(5).negate() );
		else
			map.put("transactionAmount", result.getBigDecimal(5) );
		itemDetailList.add(map);
	}
	sqlProc1.close();
	return itemDetailList;
}

def buildCreditOrDebitQuery(String partyId,String currency,String flag,String thruDate){
	String query = "SELECT b.transaction_date,c.invoice_type_id,c.invoice_id,b.party_id,orig_amount FROM `acctg_trans_entry` A " +
    " JOIN acctg_trans B ON a.acctg_trans_id=b.acctg_trans_id " +
    " JOIN Invoice c ON b.invoice_id=c.invoice_id WHERE a.party_id=" + "'" + partyId + "'" + " AND IF(c.invoice_type_id='CUST_RTN_INVOICE',debit_credit_flag='C',debit_credit_flag='D')"+
    " AND b.transaction_date >= " + "'" + fromDate + "'" + " AND b.transaction_date <= " + "'" + thruDate + "'" + "AND a.orig_currency_uom_id = " + "'" + currency + "'" +
	" AND c.status_id<>'INVOICE_CANCELLED' AND c.status_id<>'INVOICE_WRITEOFF' AND b.payment_id IS NULL" +
    " UNION " +
    " SELECT  b.transaction_date, c.payment_type_id,c.payment_id, c.party_id_from,a.orig_amount FROM `acctg_trans_entry` A " +
    " JOIN acctg_trans B ON a.acctg_trans_id=b.acctg_trans_id " +
    " JOIN payment c ON b.payment_id=c.payment_id WHERE b.party_id=" + "'" + partyId + "'" + " AND debit_credit_flag='C'"+
    " AND b.transaction_date >= " + "'" + fromDate + "'" + " AND b.transaction_date <= " + "'" + thruDate + "'" + "AND a.orig_currency_uom_id = " + "'" + currency + "'" +
	" AND c.status_id <> 'PMNT_CANCELLED' AND c.status_id <> 'PMNT_VOID' AND b.invoice_id IS NULL";
	return query;
}

def buildQueryForOpeningBalance(String partyId,String currency,String fromDate){
	String query = "SELECT a.party_id,SUM(b.orig_amount), b.orig_currency_uom_id, b.debit_credit_flag " +
	" FROM acctg_trans AS a JOIN acctg_trans_entry AS b ON a.acctg_trans_id=b.acctg_trans_id WHERE " 
	
	if(UtilValidate.isNotEmpty(partyId))
	 query = query + " a.party_id=" + "'" + partyId.replace("'", "''") + "'" + " AND ";
	
	query = query + "a.invoice_id IS NOT NULL AND a.payment_id IS NULL AND b.debit_credit_flag = 'C' " +
    " AND  a.transaction_date < " + "'" + fromDate + "'" + " AND b.orig_currency_uom_id = " + "'" + currency + "'" +
	" GROUP BY b.orig_currency_uom_id " +
	" UNION " +
	" SELECT a.party_id,SUM(b.orig_amount), b.orig_currency_uom_id, b.debit_credit_flag " +
	" FROM `acctg_trans` AS a JOIN acctg_trans_entry AS b ON a.acctg_trans_id=b.acctg_trans_id WHERE " 
	if(UtilValidate.isNotEmpty(partyId))
		query = query + " a.party_id=" + "'" + partyId.replace("'", "''") + "'" + " AND ";
	
	query = query + "a.payment_id IS NOT NULL AND a.invoice_id IS NULL AND b.debit_credit_flag = 'D' " +
    " AND  a.transaction_date < " + "'" + fromDate + "'" + " AND b.orig_currency_uom_id = " + "'" + currency + "'" +
	" GROUP BY b.orig_currency_uom_id " ;
	return query;
}

def buildQuery(List<String> customerIds,String partyId,String fromDate,String thruDate){
	String query = "SELECT a.party_id, b.orig_currency_uom_id FROM acctg_trans AS a JOIN acctg_trans_entry AS b ON a.acctg_trans_id = b.acctg_trans_id WHERE "
				   if(UtilValidate.isNotEmpty(partyId))
						   query = query + " a.party_id=" + "'" + partyId.replace("'", "''") + "'" ;
				   if(UtilValidate.isNotEmpty(customerIds)){
					   String sb = "(";
					   int i = 1;
					   for(String custId : customerIds){
						  sb = sb + "'" + custId.replace("'", "''") + "'";
						  if(i != customerIds.size())
							  sb = sb + ",";
						  i++;
					   }
					   sb = sb + ")";
					   query = query + " a.party_id IN " + sb ;
					  }
				 query = query + " GROUP BY b.orig_currency_uom_id,a.party_id ";
	return query;
}

def getCustomerPartyIdQuery(){
	return "SELECT party_id FROM party_role WHERE role_type_id='CUSTOMER'";
}