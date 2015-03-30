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


import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.lang.*;
import java.math.BigDecimal;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import java.util.List;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;


  
paymentTdsTypes = delegator.findList("PaymentTdsType", null, null, null, null, false);
context.paymentTdsTypes = paymentTdsTypes;


List<Map<String,Object>> mainList = new ArrayList<Map<String,Object>>();

 for(GenericValue types : paymentTdsTypes){
	paymentTds = delegator.findByAnd("PaymentTds", [tdsTypeId : types.tdsTypeId,paymentId : payment.paymentId]);
	 for(Map<String,Object> oGroup : paymentTds){
	  Map<String,Object> map = new HashMap<String,Object>();
		map.put("tdsTypeId",oGroup.get("tdsTypeId"));
		map.put("amount",oGroup.get("amount"));
		System.out.println("\n\n\n One"+mainList+"\n\n\n");
		mainList = addIfSameTds(map,mainList);
		System.out.println("\n\n\n Three"+mainList+"\n\n\n");
  }
 }
public List<Map<String,Object>> addIfSameTds(Map map, List<Map<String,Object>> mainList){
boolean isExists = false;
	System.out.println("\n\n\n Two"+map+"\n\n\n");
	if(mainList != null && map != null){
		for(Map<String,Object> groupedMap : mainList){
			if(groupedMap.get("tdsTypeId") != null){
	 			if(groupedMap.get("tdsTypeId").equals(map.get("tdsTypeId"))){
					isExists = true;
					newAmount = groupedMap.get("amount") + map.get("amount");
					groupedMap.put("amount",newAmount);
					break;
				}
			}
		  }
		}
		if(!isExists){
			mainList.add(map);
		}
		return mainList;
	}
context.mainList = mainList;

 



