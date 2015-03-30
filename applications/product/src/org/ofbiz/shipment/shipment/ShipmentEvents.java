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
package org.ofbiz.shipment.shipment;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * ShippingEvents - Events used for processing shipping fees
 */
public class ShipmentEvents {

    public static final String module = ShipmentEvents.class.getName();

    public static String viewShipmentPackageRouteSegLabelImage(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String shipmentId = request.getParameter("shipmentId");
        String shipmentRouteSegmentId = request.getParameter("shipmentRouteSegmentId");
        String shipmentPackageSeqId = request.getParameter("shipmentPackageSeqId");

        GenericValue shipmentPackageRouteSeg = null;
        try {
            shipmentPackageRouteSeg = delegator.findByPrimaryKey("ShipmentPackageRouteSeg", UtilMisc.toMap("shipmentId", shipmentId, "shipmentRouteSegmentId", shipmentRouteSegmentId, "shipmentPackageSeqId", shipmentPackageSeqId));
        } catch (GenericEntityException e) {
            String errorMsg = "Error looking up ShipmentPackageRouteSeg: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }
    	
        if (shipmentPackageRouteSeg == null) {
            request.setAttribute("_ERROR_MESSAGE_", "Could not find ShipmentPackageRouteSeg where shipmentId=[" + shipmentId + "], shipmentRouteSegmentId=[" + shipmentRouteSegmentId + "], shipmentPackageSeqId=[" + shipmentPackageSeqId + "]");
            return "error";
        }

        byte[] bytes = shipmentPackageRouteSeg.getBytes("labelImage");
        if (bytes == null || bytes.length == 0) {
            request.setAttribute("_ERROR_MESSAGE_", "The ShipmentPackageRouteSeg was found where shipmentId=[" + shipmentId + "], shipmentRouteSegmentId=[" + shipmentRouteSegmentId + "], shipmentPackageSeqId=[" + shipmentPackageSeqId + "], but there was no labelImage on the value.");
            return "error";
        }

        // TODO: record the image format somehow to make this block nicer.  Right now we're just trying GIF first as a default, then if it doesn't work, trying PNG.
        // It would be nice to store the actual type of the image alongside the image data.
        try {
            UtilHttp.streamContentToBrowser(response, bytes, "image/gif");
        } catch (IOException e1) {
            try {
                UtilHttp.streamContentToBrowser(response, bytes, "image/png");
            } catch (IOException e2) {
                String errorMsg = "Error writing labelImage to OutputStream: " + e2.toString();
                Debug.logError(e2, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                return "error";
            }
        }

        return "success";
    }

    public static String checkForceShipmentReceived(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");

        String shipmentId = request.getParameter("shipmentIdReceived");
        String forceShipmentReceived = request.getParameter("forceShipmentReceived");
        if (UtilValidate.isNotEmpty(shipmentId) && "Y".equals(forceShipmentReceived)) {
            try {
                Map<String, Object> inputMap = UtilMisc.<String, Object>toMap("shipmentId", shipmentId, "statusId", "PURCH_SHIP_RECEIVED");
                inputMap.put("userLogin", userLogin);
                dispatcher.runSync("updateShipment", inputMap);
            } catch (GenericServiceException gse) {
                String errMsg = "Error updating shipment [" + shipmentId + "]: " + gse.toString();
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        }
        return "success";
    }
  
    public static String editRoute(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	        List toBeStored = new LinkedList();
	   GenericValue route = delegator.makeValue("ShipmentRoute");
	   String routeId = request.getParameter("routeId");
       if (UtilValidate.isEmpty(routeId)) {
           try {
        	   routeId = delegator.getNextSeqId("ShipmentRoute");
           } catch (IllegalArgumentException e) {
               return "Error";
           }
       }
		String routeName = request.getParameter("routeName");
		String origin = request.getParameter("origin");
		String destination = request.getParameter("destination");
		if(UtilValidate.isEmpty(routeName) || UtilValidate.isEmpty(origin) || UtilValidate.isEmpty(destination)){
			request.setAttribute("_ERROR_MESSAGE_", " Fill all the require fields for Route ");
			return "error";
		}
		if(UtilValidate.areEqual(origin, destination)){
			request.setAttribute("_ERROR_MESSAGE_", " Origin and Destination are not same ");
			return "error";
			
		}
		    route.set("routeId", routeId);	        
	        route.set("routeName", routeName);	        
	        route.set("origin", origin);
	        route.set("destination", destination);
	         toBeStored.add(route);
	      try{ 
		delegator.storeAll(toBeStored);
	      }
	      catch (GenericEntityException e) {
              request.setAttribute("_ERROR_MESSAGE_", "Route Id is Required");
              return "error";
          }
	      
	    request.setAttribute("routeId",routeId);
	    request.setAttribute("_EVENT_MESSAGE_", "Route ID" +" " +routeId + " " + "is Created Successfully");
    	return "success";
    }
    public static String editShipmentPlan(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		 
		  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		  GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		  Locale locale = UtilHttp.getLocale(request);		
		String picklistId;
		String orderId = request.getParameter("orderId");
		List<GenericValue> pickListBin;
		GenericValue pickList;
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		pickListBin = delegator.findByAnd("PicklistBin", UtilMisc.toMap("primaryOrderId", orderId));
		for(GenericValue gv:pickListBin){
			picklistId = gv.getString("picklistId");
			pickList = delegator.findByPrimaryKey("Picklist", UtilMisc.toMap("picklistId", picklistId));
			String statusId = pickList.getString("statusId");
				if ("PICKLIST_INPUT".equals(statusId)) {
					request.setAttribute("_ERROR_MESSAGE_", "Order is already picked. Please pack through Auto Shipment");
					return "error";
				}
		}
		return "success";
		}
}

