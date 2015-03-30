/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Script to build the open order item report using
 * the OrderItemQuantityReportGroupByItem view.
 */


import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.ofbiz.entity.GenericValue;

GenericDelegator delegator = delegator;
Timestamp fromDate = UtilValidate.isNotEmpty(fromDate) ? UtilDateTime.getDayStart(fromDate) : null;
Timestamp thruDate = UtilValidate.isNotEmpty(thruDate) ? UtilDateTime.getDayEnd(thruDate) : null;

String dynamicQuery = "";
if(UtilValidate.isNotEmpty(fromDate)){
	dynamicQuery = dynamicQuery + " AND `INVOICE_DATE`>=? ";
}
if(UtilValidate.isNotEmpty(thruDate)){
	dynamicQuery = dynamicQuery + " AND `INVOICE_DATE`<=? ";
}

String helperName = delegator.getGroupHelperName("org.ofbiz");
Connection conn = ConnectionFactory.getConnection(helperName);
PreparedStatement statement = conn.prepareStatement("SELECT DISTINCT invoiceId,invoiceDate,invoiceTypeId,statusId,billOfEntry,supplierPartyId,supplierName,tPin,geo,country, \n" +
	"\t description,productName,currencyUomId,(itemSubTotal-discountedTotal) as amountBeforeVat,vatAmount FROM \n" +
	"\t (SELECT IF(I.`INVOICE_TYPE_ID` ='PURCHASE_INVOICE',I.`PARTY_ID_FROM`,I.`PARTY_ID`) AS supplierPartyId, \n" +
	"\t (SELECT GROUP_NAME FROM PARTY_GROUP PG WHERE PG.`PARTY_ID`=supplierPartyId) AS supplierName, \n" +
	"\t (SELECT VAT_TIN_NUMBER FROM PARTY_GROUP PGR WHERE PGR.`PARTY_ID`=supplierPartyId) AS tPin, \n" +
	"\t I.`INVOICE_ID` AS invoiceId,I.`INVOICE_DATE` AS invoiceDate,I.`INVOICE_TYPE_ID` AS invoiceTypeId, \n" +
	"\t I.`STATUS_ID` AS statusId,I.`DESCRIPTION` AS description,I.`REFERENCE_NUMBER` AS billOfEntry, \n" +
	"\t (SELECT GROUP_CONCAT(DISTINCT internal_name SEPARATOR ' | ') FROM INVOICE_ITEM II JOIN PRODUCT P ON II.`PRODUCT_ID` = P.`PRODUCT_ID` \n" +
	"\t WHERE II.`INVOICE_ID` = I.`INVOICE_ID` AND II.`INVOICE_ITEM_TYPE_ID` NOT IN ('PITEM_SALES_TAX','PINV_SALES_TAX','SRT_SALES_TAX_ADJ','SRT_EXCISE_TAX_ADJ') \n" +
	"\t AND II.`PRODUCT_ID` IS NOT NULL GROUP BY II.`INVOICE_ID`) AS productName, ATE.`CURRENCY_UOM_ID` AS currencyUomId, \n" +
	"\t (SELECT COALESCE(SUM(amount)) FROM ACCTG_TRANS_ENTRY ATENT WHERE ATENT.`ACCTG_TRANS_ID` = ATE.`ACCTG_TRANS_ID` \n" +
	"\t AND ATENT.`GL_ACCOUNT_ID` NOT IN ('9500000','9000000','9010001','1020106','4240101')) AS itemSubTotal, \n" +
	"\t CASE WHEN ((SELECT COALESCE(SUM(amount)) FROM ACCTG_TRANS_ENTRY ATENT WHERE ATENT.`ACCTG_TRANS_ID` = ATE.`ACCTG_TRANS_ID` \n" +
	"\t AND ATENT.`GL_ACCOUNT_ID` = '1020106') IS NULL) THEN 0.00 ELSE (SELECT COALESCE(SUM(amount)) FROM ACCTG_TRANS_ENTRY ATENT \n" +
	"\t WHERE ATENT.`ACCTG_TRANS_ID` = ATE.`ACCTG_TRANS_ID` AND ATENT.`GL_ACCOUNT_ID` = '1020106') END AS discountedTotal, \n" +
	"\t (SELECT COALESCE(SUM(amount)) FROM ACCTG_TRANS_ENTRY ACTE WHERE ACTE.`ACCTG_TRANS_ID` = ATE.`ACCTG_TRANS_ID` \n" +
	"\t AND ACTE.`GL_ACCOUNT_ID` = '9500000') AS vatAmount, \n" +
	"\t (SELECT PA.`COUNTRY_GEO_ID` FROM POSTAL_ADDRESS PA JOIN PARTY_CONTACT_MECH_PURPOSE PCMP ON PA.`CONTACT_MECH_ID` = PCMP.`CONTACT_MECH_ID` \n" +
	"\t WHERE PCMP.`CONTACT_MECH_PURPOSE_TYPE_ID` = 'GENERAL_LOCATION' AND PCMP.`THRU_DATE` IS NULL AND PCMP.`PARTY_ID` = supplierPartyId GROUP BY PCMP.`PARTY_ID`) AS geo, \n" +
	"\t (SELECT G.`GEO_NAME` FROM GEO G WHERE G.`GEO_ID`=geo) AS country FROM ACCTG_TRANS_ENTRY ATE JOIN ACCTG_TRANS AT ON ATE.`ACCTG_TRANS_ID` = AT.`ACCTG_TRANS_ID` \n" +
	"\t JOIN INVOICE I ON AT.`INVOICE_ID` = I.`INVOICE_ID` AND I.`PARTY_ID_FROM` <> 'ZRA001' \n" + dynamicQuery +
	"\t AND ATE.`GL_ACCOUNT_ID` NOT IN ('9500000','9000000')) T WHERE vatAmount IS NULL AND geo <> 'ZMB' \n" +
	"\t AND invoiceTypeId IN ('PURCHASE_INVOICE','PURC_RTN_INVOICE') AND statusId IN ('INVOICE_READY','INVOICE_PAID');");


int countNo = 1;
if(UtilValidate.isNotEmpty(fromDate))
	statement.setTimestamp(countNo++, fromDate);
if(UtilValidate.isNotEmpty(thruDate))
	statement.setTimestamp(countNo++, thruDate);
	
ResultSet results = statement.executeQuery();
List li = resultSetToArrayList(results);
results.close();
statement.close();
conn.close();


def List resultSetToArrayList(ResultSet rs) throws SQLException{
	ResultSetMetaData md = rs.getMetaData();
	int columns = md.getColumnCount();
	List list = new ArrayList<>();
	while (rs.next()){
		HashMap row = new HashMap(columns);
		for(int i=1; i<=columns;i++){
			println md.getColumnLabel(i);
			row.put(md.getColumnLabel(i),rs.getObject(i));
		}
		list.add(row);
	}

	return list;
}


context.importVatExemptList = li;
