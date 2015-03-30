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
if(UtilValidate.isNotEmpty(invoiceId)){
	dynamicQuery = dynamicQuery + " AND invoiceId=? ";
}
if(UtilValidate.isNotEmpty(invoiceTypeId)){
	dynamicQuery = dynamicQuery + " AND invoiceTypeId=? ";
} else {
	dynamicQuery = dynamicQuery + " AND invoiceTypeId IN ('PURCHASE_INVOICE','PURC_RTN_INVOICE') ";
}
if(UtilValidate.isNotEmpty(statusId)){
	dynamicQuery = dynamicQuery + " AND statusId=? ";
} else {
	dynamicQuery = dynamicQuery + " AND statusId IN ('INVOICE_READY','INVOICE_PAID') ";
}
if(UtilValidate.isNotEmpty(partyIdFrom)){
	dynamicQuery = dynamicQuery + " AND partyIdFrom=? ";
}
if(UtilValidate.isNotEmpty(partyId)){
	dynamicQuery = dynamicQuery + " AND partyId=? ";
}
if(UtilValidate.isNotEmpty(fromDate)){
	dynamicQuery = dynamicQuery + " AND `INVOICE_DATE`>=? ";
}
if(UtilValidate.isNotEmpty(thruDate)){
	dynamicQuery = dynamicQuery + " AND `INVOICE_DATE`<=? ";
}

String helperName = delegator.getGroupHelperName("org.ofbiz");
Connection conn = ConnectionFactory.getConnection(helperName);
PreparedStatement statement = conn.prepareStatement("SELECT invoiceId,invoiceTypeId,invoiceDate,statusId,description,partyIdFrom,partyId,currencyUomId FROM \n" +
	"\t (SELECT IF(I.INVOICE_TYPE_ID ='PURCHASE_INVOICE',I.`PARTY_ID_FROM`,I.`PARTY_ID`) AS supplierPartyId,I.`INVOICE_ID` AS invoiceId, \n" +
	"\t I.`INVOICE_DATE` AS invoiceDate,I.`INVOICE_TYPE_ID` AS invoiceTypeId,I.`STATUS_ID` AS statusId,I.`DESCRIPTION` AS description, \n" +
	"\t I.`PARTY_ID_FROM` AS partyIdFrom,I.`PARTY_ID` AS partyId,I.`CURRENCY_UOM_ID` AS currencyUomId, \n" +
	"\t (SELECT PA.`COUNTRY_GEO_ID` FROM POSTAL_ADDRESS PA JOIN PARTY_CONTACT_MECH_PURPOSE PCMP ON PA.`CONTACT_MECH_ID` = PCMP.`CONTACT_MECH_ID` WHERE \n" +
	"\t PCMP.`CONTACT_MECH_PURPOSE_TYPE_ID` = 'GENERAL_LOCATION' AND PCMP.`THRU_DATE` IS NULL AND PCMP.`PARTY_ID` = supplierPartyId GROUP BY PCMP.`PARTY_ID`) AS supplierGeo, \n" +
	"\t (SELECT PA.`COUNTRY_GEO_ID` FROM POSTAL_ADDRESS PA JOIN PARTY_CONTACT_MECH_PURPOSE PCMP ON PA.`CONTACT_MECH_ID` = PCMP.`CONTACT_MECH_ID` WHERE \n" +
	"\t PCMP.`CONTACT_MECH_PURPOSE_TYPE_ID` = 'GENERAL_LOCATION' AND PCMP.`THRU_DATE` IS NULL AND PCMP.`PARTY_ID` = 'Company' GROUP BY PCMP.`PARTY_ID`) AS companyGeo FROM INVOICE I) T \n" +
	"\t WHERE companyGeo <> supplierGeo \n" + dynamicQuery + "\t ;");


int countNo = 1;
if(UtilValidate.isNotEmpty(invoiceId))
statement.setString(countNo++, invoiceId);
if(UtilValidate.isNotEmpty(invoiceTypeId))
statement.setString(countNo++, invoiceTypeId);
if(UtilValidate.isNotEmpty(statusId))
statement.setString(countNo++, statusId);
if(UtilValidate.isNotEmpty(partyIdFrom))
statement.setString(countNo++, partyIdFrom);
if(UtilValidate.isNotEmpty(partyId))
statement.setString(countNo++, partyId);
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


context.listIt = li;
