/*******************************************************************************
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
 *******************************************************************************/
package org.ofbiz.manufacturing.jobshopmgt;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductionRunEvents {

    public static final String module = ProductionRunEvents.class.getName();

    public static String productionRunDeclareAndProduce(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        Map parameters = UtilHttp.getParameterMap(request);

        BigDecimal quantity = null;
        try {
            quantity = new BigDecimal((String)parameters.get("quantity"));
        } catch (NumberFormatException nfe) {
            String errMsg = "Invalid format for quantity field. Please provide quantity in digits";
            Debug.logError(nfe, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        Collection<Map<String, Object>> componentRows = UtilHttp.parseMultiFormData(parameters);
        Map componentsLocationMap = FastMap.newInstance();
        for (Map<String, Object>componentRow : componentRows) {
            Timestamp fromDate = null;
            try {
                fromDate = Timestamp.valueOf((String)componentRow.get("fromDate"));
            } catch (IllegalArgumentException iae) {
                String errMsg = "Invalid format for date field: " + iae.toString();
                Debug.logError(iae, errMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            GenericPK key = delegator.makePK("WorkEffortGoodStandard", UtilMisc.toMap("workEffortId", (String)componentRow.get("productionRunTaskId"),
                                                                                                                "productId", (String)componentRow.get("productId"),
                                                                                                                "fromDate", fromDate,
                                                                                                                "workEffortGoodStdTypeId", "PRUNT_PROD_NEEDED"));
            componentsLocationMap.put(key, UtilMisc.toMap("locationSeqId", (String)componentRow.get("locationSeqId"),
                                                          "secondaryLocationSeqId", (String)componentRow.get("secondaryLocationSeqId"),
                                                          "failIfItemsAreNotAvailable", (String)componentRow.get("failIfItemsAreNotAvailable")));
        }

        try {
            Map inputMap = UtilMisc.toMap("workEffortId", parameters.get("workEffortId"), "inventoryItemTypeId", parameters.get("inventoryItemTypeId"));
            inputMap.put("componentsLocationMap", componentsLocationMap);
            inputMap.put("quantity", quantity);
            inputMap.put("lotId", parameters.get("lotId"));
            inputMap.put("userLogin", userLogin);
            Map result = dispatcher.runSync("productionRunDeclareAndProduce", inputMap);
        } catch (GenericServiceException e) {
            String errMsg = "Error issuing materials: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        return "success";
    }

    public static String getDependentUoms(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String productMeterTypeId = request.getQueryString();

        if("ELECTRIC_METER".equals(productMeterTypeId)) {
            EntityCondition condition = EntityCondition.makeCondition("uomTypeId", "POWER_MEASURE");
            List<GenericValue> meterUoms = null;
            try {
                meterUoms = delegator.findList("Uom", condition, null, null, null, true);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            List<String> uom = FastList.newInstance();
            for (GenericValue value : meterUoms)
                uom.add(value.get("uomId") + ":" + value.get("description") + " [" + value.get("abbreviation") + "] ");
            if (uom.size() > 0) {
                uom.add(0, ":  ");
            }
            request.setAttribute("meterUom", uom);
        }

        if("STOPPAGE".equals(productMeterTypeId)) {
            EntityCondition condition = EntityCondition.makeCondition("uomTypeId", "SHUTDOWN_MEASURE");
            List<GenericValue> meterUoms = null;
            try {
                meterUoms = delegator.findList("Uom", condition, null, null, null, true);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            List<String> uom = FastList.newInstance();
            for (GenericValue value : meterUoms)
                uom.add(value.get("uomId") + ":" + value.get("description") + " [" + value.get("abbreviation") + "] ");
            if (uom.size() > 0) {
                uom.add(0, ":  ");
            }
            request.setAttribute("meterUom", uom);
        }

        return "success";
    }

    public static String getDependentFixedAssets(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String workEffortId = request.getQueryString();

        GenericValue workEffort = null;
        workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);

        if(UtilValidate.isNotEmpty(workEffort)) {
            GenericValue fixedAsset = null;
            fixedAsset = delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId", workEffort.getString("fixedAssetId")), false);
            request.setAttribute("routingTaskFixedAsset", fixedAsset);
        }

        return "success";
    }
}
