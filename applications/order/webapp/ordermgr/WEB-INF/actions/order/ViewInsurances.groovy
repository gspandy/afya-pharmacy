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

import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.base.util.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


String facilityType = "PHARMACY";
String tenantId = "default_pharmacy";
if(UtilValidate.isNotEmpty(delegator.getDelegatorTenantId()))
    tenantId = delegator.getDelegatorTenantId();

ObjectMapper mapper = new ObjectMapper();
mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above

RestTemplate restTemplate = new RestTemplate();
HttpHeaders httpHeaders = new HttpHeaders();
httpHeaders.setContentType(MediaType.APPLICATION_JSON);
List<MediaType> mediaTypes = new ArrayList<MediaType>();
mediaTypes.add(MediaType.APPLICATION_JSON);
httpHeaders.setAccept(mediaTypes);
HttpEntity<String> requestEntity = new HttpEntity<String>(httpHeaders);
ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:7878/afya-portal/anon/getListOfInsuranceForGivenTenant?tenantId={tenantId}&facilityType={facilityType}", HttpMethod.GET, requestEntity, String.class, tenantId, facilityType);
String repsonseJson = responseEntity.getBody();
mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
List<Map<String, Object>> viewInsuranceList = new ArrayList<>();
viewInsuranceList = (List<Map<String, Object>>) mapper.readValue(repsonseJson, viewInsuranceList.getClass());
context.viewInsuranceList = viewInsuranceList;
