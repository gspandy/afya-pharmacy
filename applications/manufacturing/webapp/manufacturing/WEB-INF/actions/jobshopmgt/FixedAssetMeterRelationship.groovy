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

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.widget.html.HtmlFormWrapper;

fixedAssetId = parameters.fixedAssetId;
productionRunId = parameters.productionRunId;
workEffortId = parameters.workEffortId;
fromDate = parameters.fromDate;

conditions = [];
if(UtilValidate.isNotEmpty(fixedAssetId)){
    conditions.add(EntityCondition.makeCondition("fixedAssetId",fixedAssetId));
}
if(UtilValidate.isNotEmpty(productionRunId)){
    conditions.add(EntityCondition.makeCondition("productionRunId",productionRunId));
}

if(UtilValidate.isNotEmpty(workEffortId)){
    conditions.add(EntityCondition.makeCondition("workEffortId",workEffortId));
}
fixedAssetMeterList = delegator.findList("FixedAssetMeter", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
if(fixedAssetMeterList.isEmpty()) {
    return "success";
}
else {
    request.setAttribute("_ERROR_MESSAGE_", "Can not be deleted, please delete the related records in Stoppages and Electric Consumption.");
    return "error";
}

context.workEffortId = workEffortId;
context.fixedAssetId = fixedAssetId;
context.fromDate = fromDate;
context.productionRunId = productionRunId;
