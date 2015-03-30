import java.sql.ResultSet;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.jdbc.SQLProcessor;



String partyId = parameters.partyId;
String fromDate = parameters.fromDate;
String thruDate = parameters.thruDate;
if(UtilValidate.isEmpty(fromDate))
	return;

if(UtilValidate.isEmpty(thruDate)){
	thruDate = UtilDateTime.nowTimestamp().toString();
}
SQLProcessor sqlProcMain = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));

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

List groupedOpeningBalanceList = new ArrayList();
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
		}
	}
	groupedOpeningBalanceList.add(map);
}


List finalList = new ArrayList();

for(Map openingBalanceMap : groupedOpeningBalanceList){
	Map<String,Object> map = openingBalanceMap;
	map = updateCreditDebitMap(map,thruDate);
	finalList.add(map);
}

Comparator<Map<String, String>> mapComparator = new Comparator<Map<String, String>>() {
	public int compare(Map<String, String> m1, Map<String, String> m2) {
		return m1.get("partyId").compareTo(m2.get("partyId"));
	}
}

Collections.sort(finalList, mapComparator);

context.put("thruDate",thruDate);
context.put("finalList",finalList);


def updateCreditDebitMap(Map<String,Object> map, String thruDate){
	map = getCreditDatils(map,thruDate);
	map = getDebitDatils(map,thruDate);
	return map;
}

def getCreditDatils(Map<String,Object> map,String thruDate){
	SQLProcessor sqlProc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
	String query = buildCreditOrDebitQuery( map.get("partyId"), map.get("currency"),"C",thruDate );
	sqlProc.prepareStatement(query);
	ResultSet result = sqlProc.executeQuery();
	map.put("credit", BigDecimal.ZERO);
	BigDecimal totalAmount = BigDecimal.ZERO;
	while (result.next()){
		if("CUSTOMER_REFUND".equals(result.getString("paymentTypeId")) )
			totalAmount =  totalAmount.add(result.getBigDecimal("amount") == null ? BigDecimal.ZERO : result.getBigDecimal("amount"));
		else
			totalAmount = totalAmount.add(result.getBigDecimal("amount") == null ? BigDecimal.ZERO : result.getBigDecimal("amount").negate());
	}
	map.put("credit", totalAmount );
	sqlProc.close();
	return map;
}

def getDebitDatils(Map<String,Object> map,String thruDate){
	SQLProcessor sqlProc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
	String query = buildCreditOrDebitQuery(map.get("partyId"),map.get("currency"),"D",thruDate );
	sqlProc.prepareStatement(query);
	ResultSet result = sqlProc.executeQuery();
	map.put("debit", BigDecimal.ZERO);
	BigDecimal totalAmount = BigDecimal.ZERO;
	while (result.next()){
		if("SALES_INVOICE".equals(result.getString("invoiceTypeId")))
			totalAmount =  totalAmount.add(result.getBigDecimal("amount") == null ? BigDecimal.ZERO : result.getBigDecimal("amount"));
		else
			totalAmount = totalAmount.add(result.getBigDecimal("amount") == null ? BigDecimal.ZERO : result.getBigDecimal("amount").negate());
	}
	map.put("debit", totalAmount );
	
	sqlProc.close();
	return map;
}

def buildCreditOrDebitQuery(String partyId,String currency,String flag,String thruDate){
	String query = "";
	if("C".equals(flag)){
		query = "SELECT b.orig_amount AS amount,c.payment_type_id AS paymentTypeId FROM acctg_trans AS a JOIN acctg_trans_entry AS b ON a.acctg_trans_id=b.acctg_trans_id " +
				" JOIN payment c ON a.payment_id=c.payment_id WHERE " + 
				" a.party_id = " + "'"  + partyId + "'" + " AND a.transaction_date >= " + "'"  + fromDate + "'" + " AND a.transaction_date <= " + "'" + thruDate + "'" + 
				" AND b.orig_currency_uom_id = " + "'" + currency + "'" + " AND IF(c.payment_type_id='CUSTOMER_REFUND',b.debit_credit_flag='D',b.debit_credit_flag='C')" ;
		query = query + " AND a.payment_id IS NOT NULL AND a.invoice_id IS NULL AND IF(c.payment_type_id='DEBIT_MEMO', b.gl_account_type_id IS NULL,b.gl_account_type_id IS NOT NULL) AND c.status_id <> 'PMNT_CANCELLED' AND c.status_id <> 'PMNT_VOID'"
	}else{
	query = "SELECT b.orig_amount AS amount,c.invoice_type_id AS invoiceTypeId,b.gl_account_type_id AS glAccountType FROM acctg_trans AS a JOIN acctg_trans_entry AS b ON a.acctg_trans_id=b.acctg_trans_id " +
			" JOIN Invoice c ON a.invoice_id=c.invoice_id WHERE " +
			" a.party_id = " + "'"  + partyId + "'" + " AND a.transaction_date >= " + "'"  + fromDate + "'" + " AND a.transaction_date <= " + "'" + thruDate + "'" +
			" AND b.orig_currency_uom_id = " + "'" + currency + "'" + " AND IF(c.invoice_type_id = 'CUST_RTN_INVOICE',b.debit_credit_flag = 'C',b.debit_credit_flag = 'D' )" ;
		query = query + " AND a.invoice_id IS NOT NULL AND a.payment_id IS NULL AND b.gl_account_type_id IS NOT NULL AND c.status_id<>'INVOICE_CANCELLED' AND c.status_id<>'INVOICE_WRITEOFF'"
	}
	return query;          
}

def buildQueryForOpeningBalance(String partyId,String currency,String fromDate){
	String query = "SELECT a.party_id,SUM(b.orig_amount), b.orig_currency_uom_id, b.debit_credit_flag " +
	" FROM acctg_trans AS a JOIN acctg_trans_entry AS b ON a.acctg_trans_id=b.acctg_trans_id WHERE " 
	
	if(UtilValidate.isNotEmpty(partyId))
	 query = query + " a.party_id=" + "'" + partyId.replace("'", "''") + "'" + " AND ";
	
	query = query + "a.invoice_id IS NOT NULL AND a.payment_id IS NULL AND  b.debit_credit_flag = 'C' " +
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
				   		query = query + " a.party_id=" + "'" + partyId.replace("'", "''") + "'";
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



