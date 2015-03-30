import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp;
import java.util.Locale;
import java.util.TimeZone;

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
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import java.text.SimpleDateFormat;
import java.util.List;

import org.ofbiz.entity.GenericValue;

try{
orderTypeId = parameters.orderTypeId;
String customerId = parameters.partyId;

// search by orderTypeId is mandatory
conditions = [EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, orderTypeId)];

if (fromDate) {
    conditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
}
if (thruDate) {
    conditions.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
} else {
    // search all orders that are not completed, cancelled or rejected
    conditions.add(
            EntityCondition.makeCondition([
                    EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"),
                    EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED")
                    ], EntityOperator.AND)
            );
}
// item conditions
conditions.add(EntityCondition.makeCondition("orderItemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
conditions.add(EntityCondition.makeCondition("orderItemStatusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));

// get the results as an entity list iterator
allConditions = EntityCondition.makeCondition( conditions, EntityOperator.AND );
fieldsToSelect = ["orderId", "orderDate", "productId", "quantityOrdered","orderItemSeqId"] as Set;
fieldsToSelect.add("shipBeforeDate");
fieldsToSelect.add("shipAfterDate");
fieldsToSelect.add("itemDescription");
findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
listIt = delegator.find("OrderItemOrderHeaderGroupBy", allConditions, null, fieldsToSelect, ["orderDate DESC"], null);
List orderItemList = [];
listIt.each { listValue ->
    String orderId = listValue.orderId;
    List orderRoleGvs = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER"));
    String customerName = "";
    String partyId = "";
    if(UtilValidate.isNotEmpty(orderRoleGvs)){
        GenericValue orderRoleGv = orderRoleGvs.get(0);
        partyId = orderRoleGv.getString("partyId");
        GenericValue partyGroupGv = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId",partyId), false);
        customerName = partyGroupGv.getString("groupName");
    }
    productId = listValue.productId;
    orderDate = listValue.orderDate;
	
    BigDecimal quantityOrdered = listValue.quantityOrdered;
	
    BigDecimal quantityIssued = getTotalIssue(orderId,listValue.orderItemSeqId);
	if(UtilValidate.isEmpty(quantityIssued))
		quantityIssued = BigDecimal.ZERO;
		
	BigDecimal balanceQuantity = quantityOrdered.subtract(quantityIssued);
	
	
    itemDescription = listValue.itemDescription;
    shipAfterDate = listValue.shipAfterDate;
    shipBeforeDate = listValue.shipBeforeDate;
    orderItemMap = [orderDate : orderDate,
                    orderId : orderId,
                    customerName : customerName,
                    productId : productId,
                    itemDescription : itemDescription,
                    quantityOrdered : quantityOrdered,
                    quantityIssued : quantityIssued,
                    shipAfterDate : shipAfterDate,
                    shipBeforeDate : shipBeforeDate,
					balanceQuantity:balanceQuantity
                    ];
                if( balanceQuantity.compareTo(BigDecimal.ZERO) > 0 &&  (UtilValidate.isEmpty(customerId) || customerId.equalsIgnoreCase(partyId)) ){
                    orderItemList.add(orderItemMap);
                }
}

listIt.close();
context.orderItemList = orderItemList;
context.fromDate = fromDate;
context.thruDate = thruDate;

}catch(Exception e){
    e.printStackTrace();
}

private BigDecimal getTotalIssue(String orderId,String orderItemSeqId){
	String helperName = delegator.getGroupHelperName("org.ofbiz");    // gets the helper (localderby, localmysql, localpostgres, etc.) for your entity group org.ofbiz
	Connection conn = ConnectionFactory.getConnection(helperName);
	BigDecimal totalQuantity=BigDecimal.ZERO;
	PreparedStatement statement = conn.prepareStatement( "SELECT SUM(quantity) AS itemQuantity FROM item_issuance WHERE order_id=? AND order_item_seq_id=? GROUP BY order_item_seq_id;");

	statement.setString(1, orderId);
	statement.setString(2, orderItemSeqId);
	ResultSet results = statement.executeQuery();
	if(results.next()){
		totalQuantity = results.getBigDecimal(1);
	}
	results.close();
	statement.close();
	return totalQuantity;
}