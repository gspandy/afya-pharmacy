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
Timestamp fromDate = UtilValidate.isNotEmpty(fromDate) ? UtilDateTime.getDayStart(fromDate) : null;
Timestamp thruDate = UtilValidate.isNotEmpty(thruDate) ? UtilDateTime.getDayEnd(thruDate) : null;

String dynamicQuery = "";
if(UtilValidate.isNotEmpty(productId)){ 
 	dynamicQuery = " AND OI.PRODUCT_ID=? ";
}
if(UtilValidate.isNotEmpty(fromDate)){
	dynamicQuery = dynamicQuery + " AND OH.ORDER_DATE>=? ";
}
if(UtilValidate.isNotEmpty(thruDate)){
	dynamicQuery = dynamicQuery + " AND OH.ORDER_DATE<=? ";
}

String helperName = delegator.getGroupHelperName("org.ofbiz");
Connection conn = ConnectionFactory.getConnection(helperName);
PreparedStatement statement = conn.prepareStatement("SELECT OI.`PRODUCT_ID` AS productId, \n" +
	"\t (SELECT internal_name FROM PRODUCT P WHERE OI.`PRODUCT_ID`=P.`PRODUCT_ID` AND OH.`ORDER_ID`=OI.`ORDER_ID` AND OH.`ORDER_TYPE_ID`='SALES_ORDER' AND OH.`STATUS_ID`='ORDER_COMPLETED') AS productName, \n" +
	"\t SUM(OIB.`QUANTITY`) AS quantity, SUM(OIB.`QUANTITY`*OIB.`AMOUNT`) AS totalAmount \n" +
	"\t FROM order_header OH JOIN order_item OI ON OH.`ORDER_ID`=OI.`ORDER_ID` \n" +
	"\t JOIN order_item_billing OIB ON OI.`ORDER_ID`=OIB.`ORDER_ID` AND OI.`ORDER_ITEM_SEQ_ID`=OIB.`ORDER_ITEM_SEQ_ID` \n" +
	"\t WHERE OH.`ORDER_TYPE_ID`='SALES_ORDER' AND OH.`STATUS_ID`='ORDER_COMPLETED' \n" +
	dynamicQuery + "\t GROUP BY OI.`PRODUCT_ID` \n" +
	"\t ORDER BY OI.`PRODUCT_ID`;");


int countNo = 1;
if(UtilValidate.isNotEmpty(productId))
	statement.setString(countNo++, productId);
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
	   for(int i=1;i<=columns;i++){
		   println md.getColumnLabel(i);
		   row.put(md.getColumnLabel(i),rs.getObject(i));
	   }
		list.add(row);
	}
  
   return list;
}


context.productSalesItemList = li;
context.fromDate = fromDate;
context.thruDate = thruDate;
