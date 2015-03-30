package org.ofbiz.product.catalog;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

/**
 * @author Nafis Dec 28, 2011
 */
public class AdjustmentTypeGlAccounts {

    public static String createOrderAdjustmentType(HttpServletRequest request, HttpServletResponse response)
            throws GenericEntityException {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String assessableValueCalculation = request.getParameter("assessableValueCalculation");
        String methodOfApportion = request.getParameter("methodOfApportion");
        String taxType = request.getParameter("taxType");
        String applicableOn = request.getParameter("applicableOn");

        if ("Y".equalsIgnoreCase(assessableValueCalculation)) {
            if (UtilValidate.isEmpty(methodOfApportion) || UtilValidate.isEmpty(taxType)) {
                request.setAttribute("_ERROR_MESSAGE_", "Method Of Apportion & Tax Type is mandatory");
                return "error";
            }
        }
        String orderAdjustmentTypeId = request.getParameter("orderAdjustmentTypeId");
        if (UtilValidate.isEmpty(orderAdjustmentTypeId)) {
            orderAdjustmentTypeId = delegator.getNextSeqId("OrderAdjustmentType");
        } else {
            List<GenericValue> orderAdjTypeList = delegator.findByAnd("OrderAdjustmentType",
                    UtilMisc.toMap("orderAdjustmentTypeId", orderAdjustmentTypeId), UtilMisc.toList("orderAdjustmentTypeId"),
                    true);
            if (UtilValidate.isNotEmpty(orderAdjTypeList)) {
                request.setAttribute("_ERROR_MESSAGE_", "Order Adjustment Type Id " + orderAdjustmentTypeId + " already exists.");
                return "error";
            }
        }
        String description = request.getParameter("description");
        String glAccountId = request.getParameter("glAccountId") == "" ? null : request.getParameter("glAccountId");
        if (UtilValidate.isEmpty(description)) {
            request.setAttribute("_ERROR_MESSAGE_", "Description can not be empty.");
            return "error";
        }

        if (UtilValidate.isEmpty(glAccountId)) {
            request.setAttribute("_ERROR_MESSAGE_", "Gl Account can not be empty.");
            return "error";
        }

        if (UtilValidate.isEmpty(applicableOn)) {
            request.setAttribute("_ERROR_MESSAGE_", "Please select Applicable On.");
            return "error";
        }

        GenericValue gv = delegator.makeValue("OrderAdjustmentType", UtilMisc.toMap("orderAdjustmentTypeId",
                orderAdjustmentTypeId, "description", description, "hasTable", "N", "glAccountId", glAccountId,
                "assessableValueCalculation", assessableValueCalculation, "methodOfApportion", methodOfApportion, "taxType",
                taxType, "applicableOn", applicableOn));
        delegator.create(gv);
        
        try {
            dispatcher.runSync("createReturnAdjustmentType",
                UtilMisc.toMap("orderAdjustmentTypeId", orderAdjustmentTypeId,"description",description,"userLogin",userLogin));
            dispatcher.runSync("createInvoiceItemType",
                    UtilMisc.toMap("orderAdjustmentTypeId", orderAdjustmentTypeId,"description",description,"userLogin",userLogin));
        } catch (GenericServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        request.setAttribute("_EVENT_MESSAGE_", "Order Adjustment created successfully");
        return "success";
    }

    public static String updateOrderAdjustmentType(HttpServletRequest request, HttpServletResponse response)
            throws GenericEntityException {

        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        boolean isSelected;
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);

        int rowCount = UtilHttp.getMultiFormRowCount(request);
        for (int i = 0; i < rowCount; i++) {
            String suffix = UtilHttp.MULTI_ROW_DELIMITER + i;
            isSelected = (ctx.containsKey("_rowSubmit" + suffix) && "Y".equalsIgnoreCase((String) ctx.get("_rowSubmit" + suffix)));
            if (!isSelected) {
                continue;
            }
            String assessableValueCalculation = request.getParameter("assessableValueCalculation" + suffix);
            String methodOfApportion = request.getParameter("methodOfApportion" + suffix);
            String taxType = request.getParameter("taxType" + suffix);
            String applicableOn = request.getParameter("applicableOn" + suffix);
            if ("Y".equalsIgnoreCase(assessableValueCalculation)) {
                if (UtilValidate.isEmpty(methodOfApportion) || UtilValidate.isEmpty(taxType)) {
                    request.setAttribute("_ERROR_MESSAGE_", "Method Of Apportion & Tax Type is mandatory");
                    return "error";
                }
            }
            String orderAdjustmentTypeId = request.getParameter("orderAdjustmentTypeId" + suffix);
            String description = request.getParameter("description" + suffix);
            String glAccountId = request.getParameter("glAccountId") == "" ? null : request.getParameter("glAccountId" + suffix);
            if (UtilValidate.isEmpty(description)) {
                request.setAttribute("_ERROR_MESSAGE_", "Description can not be empty.");
                return "error";
            }
            if (UtilValidate.isEmpty(glAccountId)) {
                request.setAttribute("_ERROR_MESSAGE_", "Gl Account can not be empty.");
                return "error";
            }

            if (UtilValidate.isEmpty(applicableOn)) {
                request.setAttribute("_ERROR_MESSAGE_", "Please select Applicable On.");
                return "error";
            }
            GenericValue gv = delegator.makeValue("OrderAdjustmentType", UtilMisc.toMap("orderAdjustmentTypeId",
                    orderAdjustmentTypeId, "description", description, "hasTable", "N", "glAccountId", glAccountId,
                    "assessableValueCalculation", assessableValueCalculation, "methodOfApportion", methodOfApportion, "taxType",
                    taxType, "applicableOn", applicableOn));
            delegator.createOrStore(gv);
        }
        request.setAttribute("_EVENT_MESSAGE_", "Order Adjustment updated successfully");
        return "success";
    }

    public static String deleteOrderAdjustmentType(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String orderAdjustmentTypeId = request.getParameter("orderAdjustmentTypeId");
        try {
            delegator.removeByAnd("OrderAdjustmentType", UtilMisc.toMap("orderAdjustmentTypeId", orderAdjustmentTypeId));
        } catch (GenericEntityException e) {
            System.out.println(e);
            request.setAttribute("_ERROR_MESSAGE_", "The selected Adjustment Type is in use, cannot be deleted.");
            return "error";
        }

        request.setAttribute("_EVENT_MESSAGE_", "Deleted successfully");
        return "success";
    }

    public static String getGlAccountIds(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String accountName = request.getQueryString();

        List<GenericValue> className = null;

        if (accountName.equals("SALES_ACCOUNT")) {
            className = delegator.findByAnd("GlAccount", UtilMisc.toMap("parentGlAccountId", "4160000"));
        }
        if (accountName.equals("PURCHASE_ACCOUNT")) {
            className = delegator.findByAnd("GlAccount", UtilMisc.toMap("parentGlAccountId", "3500000"));
        }

        List<String> classNameList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(className)) {
            for (GenericValue value : className)
                classNameList.add(value.get("glAccountId") + ":" + value.get("accountName"));

            if (classNameList.size() > 0) {
                classNameList.add(0, ":  ");
            }
        }
        request.setAttribute("classNameValue", classNameList);
        return "success";
    }

}
