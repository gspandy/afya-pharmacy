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
import org.ofbiz.base.util.*

shipmentId = request.getParameter("shipmentId");
println 'Shipment Id ******************************';

inventoryItemId = request.getParameter("inventoryItemId");
println 'Inventory Item Id '+   inventoryItemId;

if (!shipmentId) {
    shipmentId = context.shipmentId;
}

shipment = null;
if (shipmentId) {
    shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
}

if (shipment) {
    shipmentItems = shipment.getRelated("ShipmentItem", null, ['shipmentItemSeqId']);
    if (shipmentItems) {
        shipmentItems.each { shipmentItem ->
            itemIssuances = shipmentItem.getRelated("ItemIssuance");
            if(itemIssuances.quantity<shipmentItem.quantity){
                println 'shipmentItem.quantity '+shipmentItem.quantity;
                dispatcher.runSync("createItemIssuance",["shipmentId":shipmentId,"shipmentItemSeqId":shipmentItem.shipmentItemSeqId,
                        "userLogin":userLogin,"inventoryItemId":inventoryItemId,"quantity":shipmentItem.quantity]);
            }
        }
    }
}
context.shipmentId = shipmentId;
return "success";