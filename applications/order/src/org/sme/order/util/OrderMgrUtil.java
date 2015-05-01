package org.sme.order.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.webapp.control.Infrastructure;

/**
 * @author Nafis Nov 14, 2011
 */
public class OrderMgrUtil {

    public static Boolean checkOrderCreater(String partyId, String orderId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        List<String> orderCreators = new ArrayList<String>();
        orderCreators.add(partyId);
        Boolean salesManager = isSalesManger(partyId);
        if (salesManager) {
            orderCreators.addAll(getManagerRelationship(partyId));
        }
        orderCreators = getUserLoginIds(orderCreators);
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), EntityOperator.AND,
                EntityCondition.makeCondition("createdBy", EntityOperator.IN, orderCreators))));
        List<GenericValue> orderHeaderList = new ArrayList<GenericValue>();
        try {
            orderHeaderList = delegator.findList("OrderHeader", condition, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return orderHeaderList.size() > 0;
    }

    public static Boolean isSalesManger(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_MGR"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }

    public static Boolean isPurchaseManger(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    public static Boolean isQuoteManger(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "QUOTE_MGR"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
   
    public static List<String> getManagerRelationship(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        List<String> orderCreators = new ArrayList<String>();
        List<GenericValue> partyRelationships = null;
        try {
            partyRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdTo", partyId),
                    UtilMisc.toSet("partyIdFrom"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        for (GenericValue pr : partyRelationships) {
            orderCreators.add(pr.getString("partyIdFrom"));
        }
        return orderCreators;
    }

    public static List<String> getUserLoginIds(List<String> partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        List<GenericValue> userLogins = null;
        try {
            userLogins = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", EntityOperator.IN, partyId),
                    UtilMisc.toSet("userLoginId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        for (GenericValue ul : userLogins) {
            partyId.add(ul.getString("userLoginId"));
        }
        return partyId;
    }

    public static Boolean isSalesReprensentative(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    public static Boolean isQuoteReprensentative(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "QUOTE_REP"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    public static Boolean isPurchaseReprensentative(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ORDER_CLERK"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    public static Boolean isRequirementManger(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_MGR"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    public static Boolean isRequirementsRepresentative(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_REP"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    
    public static Boolean isGeneralManager(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "GENERAL_MANAGER"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    
    public static Boolean isRequirementApprover(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_APPROVER"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    
    public static Boolean isRequirementProposer(String partyId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_PROPOSER"))));

        List<GenericValue> partyRole = null;
        try {
            partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return partyRole.size() > 0;
    }
    
    public static String getPartyContactMechId(String partyId, Delegator delegator) throws GenericEntityException {
        if (UtilValidate.isNotEmpty(partyId) && delegator != null) {
            List<GenericValue> values = delegator.findByAnd("PartyAndPostalAddress", UtilMisc.toMap("partyId", partyId));
            if (UtilValidate.isNotEmpty(values)) {
                return values.get(0).getString("contactMechId");
            }
        }
        return null;
    }

    public static String getTotalPackages(Object qtyPerPackages, Object totalqty) {
        if (qtyPerPackages != null && totalqty != null && UtilValidate.isInteger(qtyPerPackages.toString())
                && UtilValidate.isFloat(totalqty.toString())) {
            BigDecimal _qtyPerPackages = new BigDecimal(qtyPerPackages.toString());
            BigDecimal totalqtyBig = new BigDecimal(totalqty.toString());
            BigDecimal _total = totalqtyBig.divide(_qtyPerPackages);
            BigDecimal _total1 = _total.setScale(3, BigDecimal.ROUND_HALF_UP);
            return _total1.toString();
        }
        return "";
    }

    public static String getProductDescription(String productId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        if (UtilValidate.isEmpty(productId))
            return "";
        GenericValue product = null;
        try {
            product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (product != null)
            return product.getString("internalName");
        else
            return "";
    }

    public static String getProductDescription(String productId, GenericDelegator genricDeligator) {
        if (UtilValidate.isEmpty(productId))
            return "";
        GenericValue product = null;
        try {
            product = genricDeligator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (product != null)
            return product.getString("internalName");
        else
            return "";
    }

    public static String getFixedAssetName(String fixedAssetId) {
        GenericDelegator delegator = Infrastructure.getDelegator();
        if (UtilValidate.isEmpty(fixedAssetId))
            return "";
        GenericValue fixedAsset = null;
        try {
            fixedAsset = delegator.findByPrimaryKey("FixedAsset", UtilMisc.toMap("fixedAssetId", fixedAssetId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (fixedAsset != null)
            return fixedAsset.getString("fixedAssetName");
        else
            return "";
    }

    public static GenericValue getOrderItemChange(Object orderId, Object orderItemSeqId) throws Exception {
        Delegator delegator = Infrastructure.getDelegator();
        List<GenericValue> li = null;
        li = delegator.findByAnd("OrderItemChange", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
        return EntityUtil.getFirst(li);
    }
    
    public static String getStatusItemsForReturn(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String returnHeaderTypeId = request.getQueryString();
        List<GenericValue> statusItemList = null;
        List<String> orderByList = new ArrayList<String>();
        orderByList.add("sequenceId");
        if("CUSTOMER_RETURN".equals(returnHeaderTypeId)){
            statusItemList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId","ORDER_RETURN_STTS"),orderByList,true);
        }else{
            statusItemList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId","PORDER_RETURN_STTS"),orderByList,true);
        }
        List<String> statusItemParamList = new ArrayList<String>();
        for(GenericValue gv : statusItemList){
            statusItemParamList.add(gv.getString("statusId") + ":" + gv.getString("description"));
        }
        request.setAttribute("statusItemList", statusItemParamList);
        return "success";
    }
    
    public static String getVoucherType(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String returnHeaderTypeId = request.getQueryString();
        List<GenericValue> vaucherTypeList = null;
        if("CUSTOMER_RETURN".equals(returnHeaderTypeId)){
            vaucherTypeList = delegator.findByAnd("VoucherType", UtilMisc.toMap("voucherParentId","Sales Return"),null,true);
        }else{
            vaucherTypeList = delegator.findByAnd("VoucherType", UtilMisc.toMap("voucherParentId","Purchase Return"),null,true);
        }
        List<String> vaucherTypeParamList = new ArrayList<String>();
        for(GenericValue gv : vaucherTypeList){
            vaucherTypeParamList.add(gv.getString("voucherName") + ":" + gv.getString("voucherName"));
        }
        request.setAttribute("vaucherTypeList", vaucherTypeParamList);
        return "success";
    }
    
    public static List sortOderList(List orderList){
        if(UtilValidate.isEmpty(orderList)){
            return orderList;
        }
        Collections.sort(orderList,new SortOrder());
        return orderList;
    }
    
    private static class SortOrder implements Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            Map val1 = (Map)o1;
            Map val2 = (Map)o2;
            if(val1.get("sourcePercentage") != null && val2.get("sourcePercentage") != null){
                return ((BigDecimal)val1.get("sourcePercentage")).compareTo((BigDecimal)val2.get("sourcePercentage"));
            }else
                return 0;
        }
    }

}
