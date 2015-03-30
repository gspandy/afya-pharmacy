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

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import java.util.List;

GenericValue userLogin = session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");
shipmentId = request.getParameter("shipmentId");
facilityId = request.getParameter("facilityId");
purchaseOrderId = request.getParameter("purchaseOrderId");


//List shipmentReceipts = delegator.findByAnd("ShipmentReceipt",UtilMisc.toMap("shipmentId",shipmentId));
List shipmentReceipts = delegator.findList("ShipmentReceiptAndItem", EntityCondition.makeCondition([shipmentId: shipmentId, facilityId: facilityId, orderId: purchaseOrderId]), null, null, null, false);

//Shipment Details
shipmentDetails = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
String grnStatus = shipmentDetails.getString("approvalStatus");

//GM Approval Status List
approvalStatus = delegator.findByAnd("StatusItem", [statusTypeId : "GM_APPROVAL_STATUS"], ["statusId", "description"]);
context.shipmentReceipts = shipmentReceipts;
context.approvalStatus = approvalStatus;
context.grnStatus = grnStatus;

