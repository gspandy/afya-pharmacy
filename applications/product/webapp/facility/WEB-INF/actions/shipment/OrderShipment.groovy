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

import org.ofbiz.entity.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.GeneralException;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.widget.html.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.util.*


action = request.getParameter("action");
mode = request.getParameter("mode");
facilityId = request.getParameter("facilityId");
shipmentId = request.getParameter("shipmentId");
orderId = request.getParameter("orderId");


if (!shipmentId) {
    shipmentId = context.shipmentId;
}
action = request.getParameter("action");
// **********************
// Show orders in Shipment which are Reserved
// ********************
List ordersInShipment ;
 ordersNew = [] as ArrayList;
if(shipmentId){
    findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    ordersInShipment = delegator.findList("OrderShipment", EntityCondition.makeCondition([shipmentId : shipmentId]), UtilMisc.toSet("orderId"), null, findOptions, false);
}

List ordersPicked = [];
for(GenericValue order : ordersInShipment){
   findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
	if(!ordersPicked.contains(order.getString("orderId")))
		ordersNew.addAll(delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition([orderId : order.orderId]), UtilMisc.toSet("orderId"), null, findOptions, false));
		}

String result=null;
List pick1 = []as ArrayList;
List pick4 = [] as ArrayList;
for(GenericValue order1 : ordersNew){
List pick = [];
pick = delegator.findList("PicklistAndBinAndItem", EntityCondition.makeCondition([orderId : order1.orderId,shipmentId:shipmentId]), null, null, null, false);
if (pick.size()>0 ) {
result="Yes";
pick1.add(result);
pick4.add(true);
}
else{
result="No";
pick1.add(result);
pick4.add(false);
}
}
Map pick2 = [:];
for(int i=0 ; i < pick1.size() ; i++){
	pick2.put(ordersNew.get(i).getString("orderId"),pick1.get(i));
}
Map pick5 = [:];
for(int i=0 ; i < pick4.size() ; i++){
	pick5.put(ordersNew.get(i).getString("orderId"),pick4.get(i));
}
context.pick3=pick2;
context.pick6=pick5;
context.ordersNew = ordersNew;
request.setAttribute("facilityId", facilityId);
request.setAttribute("shipmentId", shipmentId);
request.setAttribute("mode", mode);
return "success";




