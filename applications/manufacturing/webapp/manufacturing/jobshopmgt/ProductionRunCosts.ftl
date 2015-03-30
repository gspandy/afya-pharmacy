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

<#list taskCosts as taskCost>
<div class="screenlet">
  <div class="screenlet-title-bar">
    
    <#assign task = taskCost.task>
    <#assign costUom = taskCost.costUomId?if_exists>
    <#assign costsForm = taskCost.costsForm>
         <#if task.workEffortTypeId?if_exists == "PROD_ORDER_HEADER">
             <#assign mainTask = task >
            <h3> ${uiLabelMap.ManufacturingActualCosts} ${task.workEffortName?if_exists} [${task.workEffortId}] [Production Run Overhead Costs]</h3>           
           <#else>
           <h3> ${uiLabelMap.ManufacturingActualCosts} ${task.workEffortName?if_exists} [${task.workEffortId}] </h3>
         </#if>   
    ${costsForm.renderFormString(context)}
  </div>
</#list>
<div class="screenlet">
<div class="screenlet-title-bar">
  	<h3> Production Run Cost </h3>
</div>
<#if mainTask?exists>
<table cellspacing="0" class="basic-table hover-bar">
  <tr class="header-row">
  	 <td>Production Run Id</td>
   
   <td colspan="7" align="right">Cost</td>
   <td  align="right">Cost Uom Id</td>
  
  </tr>
  <tr >
   <td>${mainTask.workEffortId}</td>
   <td colspan="7" align="right">${mainTask.actualTotalCost?if_exists}</td>
   <td >${mainTask.moneyUomId?if_exists}</td>
  </tr>
 </tbody></table>
</#if>
</div>