package org.ofbiz.product.test;

import javolution.util.FastMap;
import org.ofbiz.service.testtools.AcceptanceTest;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.util.Map;

public class ShipmentTransferTest extends AcceptanceTest {

    protected static String shipmentId = "9000";
    protected static String shipmentItemSeqId_1 = "00001", shipmentItemSeqId_2 = "00002", itemIssuanceId_1 = null, itemIssuanceId_2 = null, product_1 = "Xfer-Product-1", product_2 = "Xfer-Product-2";
    protected static String xfer_1 = null, xfer_2 = null;
    protected static String originFacilityId = "Facility-1";
    protected static BigDecimal unitPrice = new BigDecimal(100);
    protected static String currencyUomId = "INR";

    public ShipmentTransferTest(String name) {
        super(name);
    }




    public void testTransferShipment() throws Exception {
        testSetUp("component://product/testdef/data/SampleDataset.xml");
        //SUT start
       /* issueInventoryForShipment();
        setStatusToPackForShipment();
        setStatusToDeliveredForShipment();*/
        verifyOutcome("component://product/testdef/data/ExpectedSampleDataset.xml");
    }


    public void issueInventoryForShipment() throws Exception {
        Map<String, Object> ctx = FastMap.newInstance();
        String inventoryItemId = "9001";
        ctx.put("quantity", "2");
        ctx.put("shipmentId", shipmentId);
        ctx.put("shipmentItemSeqId", shipmentItemSeqId_1);
        ctx.put("userLogin", userLogin);
        ctx.put("cancelQuantity",0);
        ctx.put("inventoryItemId", inventoryItemId);
        Map<String, Object> resp = dispatcher.runSync("createItemIssuance", ctx);
        itemIssuanceId_1 = (String) resp.get("itemIssuanceId");

        GenericValue gv1 = delegator.findOne("ItemIssuance",UtilMisc.toMap("itemIssuanceId",itemIssuanceId_1),true);
        System.out.println(gv1);
        assertEquals(gv1.getString("inventoryItemId"),inventoryItemId);

        inventoryItemId = "9002";
        ctx.put("quantity", "8");
        ctx.put("shipmentId", shipmentId);
        ctx.put("shipmentItemSeqId", shipmentItemSeqId_2);
        ctx.put("userLogin", userLogin);
        ctx.put("inventoryItemId", inventoryItemId);
        ctx.put("cancelQuantity",0);
        resp = dispatcher.runSync("createItemIssuance", ctx);
        itemIssuanceId_2 = (String) resp.get("itemIssuanceId");

        assertNotNull(itemIssuanceId_2);
        GenericValue gv2 = delegator.findOne("ItemIssuance",UtilMisc.toMap("itemIssuanceId",itemIssuanceId_2),true);
        assertEquals(gv2.getString("inventoryItemId"),inventoryItemId);
    }

    public void setStatusToPackForShipment() throws Exception {
        Map<String, Object> ctx = FastMap.newInstance();
        String statusId = "SHIPMENT_PACKED";
        ctx.put("shipmentId", shipmentId);
        ctx.put("userLogin", userLogin);
        ctx.put("statusId", statusId);
        Map<String, Object> resp = dispatcher.runSync("updateShipment", ctx);

    }

    public void setStatusToDeliveredForShipment() throws Exception {
        Map<String, Object> ctx = FastMap.newInstance();
        String statusId = "SHIPMENT_DELIVERED";
        ctx.put("shipmentId", shipmentId);
        ctx.put("userLogin", userLogin);
        ctx.put("statusId", statusId);
        dispatcher.runSync("updateShipment", ctx);

        GenericValue invTransfer = EntityUtil.getFirst(delegator.findByAnd("InventoryTransfer","itemIssuanceId", itemIssuanceId_1));
        xfer_1 = invTransfer.getString("inventoryTransferId");
        GenericValue inv = invTransfer.getRelatedOne("InventoryItem");
        BigDecimal cost_1 = inv.getBigDecimal("unitCost");
        invTransfer = EntityUtil.getFirst(delegator.findByAnd("InventoryTransfer","itemIssuanceId", itemIssuanceId_2));
        xfer_2 = invTransfer.getString("inventoryTransferId");
        inv = invTransfer.getRelatedOne("InventoryItem");
        BigDecimal cost_2 = inv.getBigDecimal("unitCost");
        BigDecimal expected_1 = new BigDecimal(400).divide(new BigDecimal(6800), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).add(new BigDecimal(200)).multiply(new BigDecimal("0.0600"));
        BigDecimal expected_2 = new BigDecimal(6400).divide(new BigDecimal(6800),6,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).add(new BigDecimal(800)).multiply(new BigDecimal("0.0600"));

/*
        assertEquals(0, expected_1.compareTo(cost_1));
        assertEquals(0, expected_2.compareTo(cost_2));
*/
    }

}
