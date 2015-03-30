<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<div>
<table cellspacing="30">
 	

     <td><h1>Shipment Reports</h1></td>

  </tr>
  
  <tr valign="top">

<td> 
<ul>
<li> <h2>Inbound</h2></li>
<li>  <a href="<@ofbizUrl>PurchaseInboundReport.xls?ename=PurchaseOrderItemFact&facilityId=${parameters.facilityId}</@ofbizUrl>">Purchase Inbound</a></li>
<li><a href="<@ofbizUrl>ShipmentArrivalsReport.xls?ename=PurchaseOrderItemFact&facilityId=${parameters.facilityId}</@ofbizUrl>">Shipment Arrivals</a></li>
</ul>
</td>


<td>
<ul>
<li><h2>  Outbound </h2></li>
<li>  <a href="<@ofbizUrl>SalesOutboundReport.xls?ename=SalesOrderItemFact&facilityId=${parameters.facilityId}</@ofbizUrl>">Sales Outbound</a></li>
<li>  <a href="<@ofbizUrl>manufecIssuance?facilityId=${parameters.facilityId}</@ofbizUrl>">Manufacturing Issuance</a></li>
<li>  <a href="<@ofbizUrl>ProductInventoryPositionReports?facilityId=${parameters.facilityId}</@ofbizUrl>">Product Inventory Position</a></li>
<li>  <a href="<@ofbizUrl>StoreIssuanceNote?facilityId=${parameters.facilityId}</@ofbizUrl>">Stores Issuance Note</a></li>
</ul>
</td>

<td>
<ul>
<li><h2>  Inventory </h2></li>
<li>  <a href="<@ofbizUrl>InventorySummaryReport?facilityId=${parameters.facilityId}</@ofbizUrl>">Inventory Summary</a></li>
<!--<li>  <a href="<@ofbizUrl>InventoryByExpireDateReport</@ofbizUrl>">Inventory By Expire Date</a></li>-->
<li>  <a href="<@ofbizUrl>PhysicalInventorySummaryReport.xls?facilityId=${parameters.facilityId}</@ofbizUrl>">Physical Inventory Summary</a></li>
<li>  <a href="<@ofbizUrl>ABCAnalysis?facilityId=${parameters.facilityId}</@ofbizUrl>">ABC Analysis</a></li>

</ul>
</td>
</table >
</div>
