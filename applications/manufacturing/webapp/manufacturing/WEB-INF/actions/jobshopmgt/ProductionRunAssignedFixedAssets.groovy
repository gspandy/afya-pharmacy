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

productionRunId = parameters.productionRunId ?: parameters.workEffortId;

records = [];
context.workEffort = delegator.findOne('WorkEffort',false,[workEffortId : productionRunId]);
tasks = delegator.findByAnd("WorkEffort", [workEffortParentId : productionRunId, workEffortTypeId : "PROD_ORDER_TASK"], ["workEffortId"]);
tasks.each { task ->
    List<GenericValue> assetRecordsList = task.getRelated("WorkEffortFixedAssetAssign");
    for(GenericValue assetRecord : assetRecordsList){
        Map<String, Object> assetRecordsMap = new HashMap<String, Object>();
        assetRecordsMap.put("workEffortId", assetRecord.getString("workEffortId"));
        assetRecordsMap.put("fixedAssetId", assetRecord.getString("fixedAssetId"));
        assetRecordsMap.put("statusId", assetRecord.getString("statusId"));
        assetRecordsMap.put("fromDate", assetRecord.getTimestamp("fromDate"));
        assetRecordsMap.put("thruDate", assetRecord.getTimestamp("thruDate"));
        assetRecordsMap.put("availabilityStatusId", assetRecord.getString("availabilityStatusId"));
        assetRecordsMap.put("allocatedCost", assetRecord.getBigDecimal("allocatedCost"));
        assetRecordsMap.put("comments", assetRecord.getString("comments"));
        records.add(assetRecordsMap);
    }
}

context.records = records;
