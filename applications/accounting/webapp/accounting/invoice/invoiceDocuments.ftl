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

       <ul class="nav nav-pills">
            <#if invoice?has_content>
               <li><a href="<@ofbizUrl>invoiceOverview?invoiceId=${invoice.invoiceId?if_exists}</@ofbizUrl>"><i class="icon-search"></i>Overview</a></li>
            </#if>
            <#if invoice?has_content && ("PURCHASE_INVOICE"==invoice.invoiceTypeId || "CUST_RTN_INVOICE"==invoice.invoiceTypeId)>
                <li><a href="<@ofbizUrl>newInvoice?invoiceId=${invoice.invoiceId?if_exists}</@ofbizUrl>"><i class="icon-edit"></i>Edit</a></li>
            <#elseif invoice?has_content && "SALES_INVOICE"==invoice.invoiceTypeId>
                <li><a href="<@ofbizUrl>NewReceivableInvoice?invoiceId=${invoice.invoiceId?if_exists}</@ofbizUrl>"><i class="icon-edit"></i>Edit</a></li>
            </#if>
            <#if invoice?has_content && invoice.statusId != "INVOICE_READY" && invoice.statusId != "INVOICE_PAID" && invoice.statusId != "INVOICE_CANCELLED" && invoice.statusId != "INVOICE_WRITEOFF">
                <li><a href="<@ofbizUrl>editInvoiceApplications?invoiceId=${invoice.invoiceId?if_exists}</@ofbizUrl>"><i class="icon-th-large"></i>Apply Payments</a></li>
            </#if>
            <#-- <#if invoice?has_content && invoice.statusId != "INVOICE_APPROVED" && invoice.statusId != "INVOICE_SENT" && invoice.statusId != "INVOICE_READY" && invoice.statusId != "INVOICE_PAID" && invoice.statusId != "INVOICE_CANCELLED" && invoice.statusId != "INVOICE_WRITEOFF">
                <li><a href="<@ofbizUrl>invoiceRoles?invoiceId=${invoice.invoiceId?if_exists}</@ofbizUrl>"><i class="icon-user"></i>Roles</a></li>
            </#if>
            <#if invoice?has_content>
                <li><a href="<@ofbizUrl>CommissionRun</@ofbizUrl>">Commission Run</a></li>
            </#if> -->
       </ul>

 <div style="float:right">
 <#if invoice?has_content>
    <#if "SALES_INVOICE"==invoice.invoiceTypeId><a target="_blank" href="<@ofbizUrl>invoice.pdf?invoiceId=${parameters.invoiceId?if_exists}</@ofbizUrl>" class="btn">PDF</a></#if>
    <a href="<@ofbizUrl>sendPerEmail?invoiceId=${parameters.invoiceId?if_exists}</@ofbizUrl>" class="btn"><i class="icon-envelope"></i>Send via email</a>
    <a href="<@ofbizUrl>copyInvoice?invoiceIdToCopyFrom=${parameters.invoiceId?if_exists}</@ofbizUrl>" class="btn">Copy Invoice</a>
    <#-- <#if invoice?has_content && invoice.statusId != "INVOICE_IN_PROCESS">
        <a href="<@ofbizUrl>generateTallyTransactionXml?invoiceId=${invoice.invoiceId?if_exists}</@ofbizUrl>" class="btn">Generate Tally XML</a>
    </#if> -->
 </#if>
 </div>