package org.ofbiz.order.test;

import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.testtools.AcceptanceTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */

public class SalesOrderAcceptanceTest extends AcceptanceTest {

    public SalesOrderAcceptanceTest(String name) {
        super(name);
    }

    public void testSalesOrder() throws GenericServiceException, GenericEntityException {
        testSetUp("component://order/testdef/data/SalesOrderDataSetForDropShip.xml");

        Map ctx = UtilMisc.toMap("partyId", "DemoCustomer", "orderTypeId", "SALES_ORDER", "currencyUom", "USD", "productStoreId", "9000");
        List orderPaymentInfo = new ArrayList();


        GenericValue orderContactMech = delegator.makeValue("OrderContactMech", UtilMisc.toMap("contactMechId", "9015", "contactMechPurposeTypeId", "BILLING_LOCATION"));
        GenericValue orderPaymentPreference = delegator.makeValue("OrderPaymentPreference", UtilMisc.toMap("paymentMethodId", "9015", "paymentMethodTypeId", "CREDIT_CARD",
                "statusId", "PAYMENT_NOT_AUTH", "overflowFlag", "N", "maxAmount", new BigDecimal("49.26")));
        orderPaymentInfo.add(orderPaymentPreference);
        ctx.put("orderPaymentInfo", orderPaymentInfo);

        List orderItemShipGroupInfo = new ArrayList();
        orderContactMech.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
        orderItemShipGroupInfo.add(orderContactMech);

        GenericValue orderItemShipGroup = delegator.makeValue("OrderItemShipGroup", UtilMisc.toMap("carrierPartyId", "UPS", "contactMechId", "9015", "isGift", "N",
                "shipGroupSeqId", "00001", "shipmentMethodTypeId", "NEXT_DAY"));
        orderItemShipGroupInfo.add(orderItemShipGroup);

        GenericValue orderItemShipGroupAssoc = delegator.makeValue("OrderItemShipGroupAssoc", UtilMisc.toMap("orderItemSeqId", "00001", "quantity", BigDecimal.ONE, "shipGroupSeqId", "00001"));
        orderItemShipGroupInfo.add(orderItemShipGroupAssoc);

        GenericValue orderAdjustment = null;
        orderAdjustment = delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentTypeId", "SHIPPING_CHARGES", "shipGroupSeqId", "00001", "amount", new BigDecimal("12.45")));
        orderItemShipGroupInfo.add(orderAdjustment);

        List orderAdjustments = new ArrayList();
        orderAdjustments.add(orderAdjustment);
        ctx.put("orderAdjustments", orderAdjustments);


        List orderItems = new ArrayList();
        GenericValue orderItem = delegator.makeValue("OrderItem", UtilMisc.toMap("orderItemSeqId", "00001", "orderItemTypeId", "PRODUCT_ORDER_ITEM", "prodCatalogId", "DemoCatalog", "productId", "DemoProduct", "quantity", BigDecimal.ONE, "selectedAmount", BigDecimal.ZERO));
        orderItem.set("isPromo", "N");
        orderItem.set("isModifiedPrice", "N");
        orderItem.set("unitPrice", new BigDecimal("38.4"));
        orderItem.set("unitListPrice", new BigDecimal("48.0"));
        orderItem.set("statusId", "ITEM_CREATED");

        orderItems.add(orderItem);
        ctx.put("orderItems", orderItems);

        List orderTerms = new ArrayList();
        ctx.put("orderTerms", orderTerms);
        ctx.put("orderItemShipGroupInfo", orderItemShipGroupInfo);

        GenericValue OrderContactMech = delegator.makeValue("OrderContactMech", FastMap.newInstance());
        OrderContactMech.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
        OrderContactMech.set("contactMechId", "10000");
        List orderContactMechs = new ArrayList();
        orderContactMechs.add(OrderContactMech);




        ctx.put("placingCustomerPartyId", "DemoCustomer");
        ctx.put("endUserCustomerPartyId", "DemoCustomer");
        ctx.put("shipToCustomerPartyId", "DemoCustomer");
        ctx.put("billToCustomerPartyId", "DemoCustomer");
        ctx.put("billFromVendorPartyId", "Company");

        ctx.put("userLogin", userLogin);

        Map resp = dispatcher.runSync("storeOrder", ctx);

        String orderId = (String) resp.get("orderId");
        String statusId = (String) resp.get("statusId");
        assertNotNull(orderId);
        assertNotNull(statusId);

        verifyOutcome("component://order/testdef/data/ExpectedSalesOrderDataSetForDropShip.xml");


    }
}
