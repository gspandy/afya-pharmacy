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
String productId = parameters.productId;
String orderStatusId = parameters.orderStatusId;
String customerId = parameters.partyId;
Timestamp fromDate = UtilValidate.isNotEmpty(fromDate) ? UtilDateTime.getDayStart(fromDate) : null;
Timestamp thruDate = UtilValidate.isNotEmpty(thruDate) ? UtilDateTime.getDayEnd(thruDate) : null;

String dynamicQuery = "";
if(UtilValidate.isNotEmpty(productId)){ 
 	dynamicQuery = " AND P.PRODUCT_ID=? ";
}
if(UtilValidate.isNotEmpty(customerId)){
	dynamicQuery = dynamicQuery + " AND PG.PARTY_ID=? ";
}
if(UtilValidate.isNotEmpty(fromDate)){
	dynamicQuery = dynamicQuery + " AND OH.ORDER_DATE>=? ";
}
if(UtilValidate.isNotEmpty(thruDate)){
	dynamicQuery = dynamicQuery + " AND OH.ORDER_DATE<=? ";
}
if(UtilValidate.isNotEmpty(orderStatusId)){
	dynamicQuery = dynamicQuery + " AND OH.STATUS_ID=? ";
}

String helperName = delegator.getGroupHelperName("org.ofbiz");
Connection conn = ConnectionFactory.getConnection(helperName);
PreparedStatement statement = conn.prepareStatement("SELECT P.PRODUCT_ID AS 'PRODUCT_ID', OH.ORDER_DATE as 'ORDER_DATE',OH.STATUS_ID,OH.ORDER_ID,PG.GROUP_NAME,SI.DESCRIPTION as 'ORDER_STATUS',I.INVOICE_DATE,I.INVOICE_ID, \n" +
	"\tII.PRODUCT_ID,U.DESCRIPTION,OIB.AMOUNT,ORDI.QUANTITY AS 'OrderQuantity',SUM(II.QUANTITY) AS 'InvoicedQuantity',ORDI.ORDER_ITEM_SEQ_ID,ORDI.QUANTITY AS ORDER_QUANTITY, \n" +
	"\tP.INTERNAL_NAME,CASE WHEN (ORDI.ORDER_ITEM_TYPE_ID ='PRODUCT_ORDER_ITEM') THEN ORDI.UNIT_PRICE ELSE II.AMOUNT END AS UNIT_PRICE, PS.PRICES_INCLUDE_TAX, ADJ.AMOUNT * (SUM(II.QUANTITY)/ORDI.QUANTITY) AS 'VAT' \n" +
	"\t FROM ORDER_ITEM_BILLING OIB JOIN INVOICE_ITEM II ON OIB.INVOICE_ID = II.INVOICE_ID \n" +
	"\tAND OIB.INVOICE_ITEM_SEQ_ID = II.INVOICE_ITEM_SEQ_ID JOIN INVOICE I ON OIB.INVOICE_ID=I.INVOICE_ID JOIN ORDER_HEADER OH ON OIB.ORDER_ID = OH.ORDER_ID \n" +
	"\tAND OH.ORDER_TYPE_ID = 'SALES_ORDER' JOIN ORDER_ITEM ORDI ON OIB.ORDER_ID = ORDI.ORDER_ID AND OIB.ORDER_ITEM_SEQ_ID = ORDI.ORDER_ITEM_SEQ_ID JOIN ORDER_ROLE R \n" +
	"\tON R.ORDER_ID = OH.ORDER_ID AND R.ROLE_TYPE_ID = 'BILL_TO_CUSTOMER' JOIN PRODUCT P ON ORDI.PRODUCT_ID = P.PRODUCT_ID JOIN ORDER_ADJUSTMENT ADJ ON ADJ.ORDER_ID = OIB.ORDER_ID \n" +
	"\tAND ADJ.ORDER_ITEM_SEQ_ID = OIB.ORDER_ITEM_SEQ_ID JOIN STATUS_ITEM SI ON SI.STATUS_ID = OH.STATUS_ID JOIN PRODUCT_STORE PS ON OH.PRODUCT_STORE_ID=PS.PRODUCT_STORE_ID \n" +
	"\tJOIN PARTY_GROUP PG ON R.PARTY_ID=PG.PARTY_ID JOIN UOM U ON P.QUANTITY_UOM_ID=U.UOM_ID WHERE ADJ.ORDER_ADJUSTMENT_TYPE_ID='SALES_TAX' \n" + 
	dynamicQuery + "\tGROUP BY OH.ORDER_ID,II.INVOICE_ID,ORDI.ORDER_ITEM_SEQ_ID,II.PRODUCT_ID ORDER BY OH.ORDER_DATE DESC;");



int countNo = 1;
if(UtilValidate.isNotEmpty(productId))
	statement.setString(countNo++, productId);
if(UtilValidate.isNotEmpty(customerId))
	statement.setString(countNo++, customerId);
if(UtilValidate.isNotEmpty(fromDate))
	statement.setTimestamp(countNo++, fromDate);
if(UtilValidate.isNotEmpty(thruDate))
	statement.setTimestamp(countNo++, thruDate);
if(UtilValidate.isNotEmpty(orderStatusId))
	statement.setString(countNo++, orderStatusId);
	
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




context.orderItemList = li;
context.fromDate = fromDate;
context.thruDate = thruDate;
