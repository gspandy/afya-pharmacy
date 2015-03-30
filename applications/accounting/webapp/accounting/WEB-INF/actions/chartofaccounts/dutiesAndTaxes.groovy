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

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.accounting.util.UtilAccounting;
 
 String glAccountId="";
 String parentGlAccountId="";
 List<String> listOfDutiesTaxes = new ArrayList<String>();
 List<Map<String, Object>> dutiesTaxes = new ArrayList<Map<String, Object>>();
 Map<String, Object> map1 = new HashMap<String, Object>();
 glAccountId = '3100000';
 List glAccountIds = new ArrayList();
 
    listOfDutiesTaxes = UtilAccounting.getAllDecedentGlAccountsRecursive(delegator, glAccountId,"Company",glAccountIds, null);
    listOfDutiesTaxes.remove("3100000");
    for(String glAccount:listOfDutiesTaxes){
     Map<String, Object> map = new HashMap<String, Object>();
     gv = delegator.findOne("GlAccount", [glAccountId : glAccount], false);
      map.put("glAccountId",gv.glAccountId);
      map.put("accountName",gv.accountName);
      map.put("taxType",gv.taxType);
      map.put("parentGlAccountId",gv.parentGlAccountId);
      map.put("subTypeOfDuty",gv.subTypeOfDuty);
      map.put("taxClassId",gv.taxClassId);
      map.put("applyTax",gv.applyTax);
      dutiesTaxes.add(map);
  }
context.dutiesTaxes = dutiesTaxes;

