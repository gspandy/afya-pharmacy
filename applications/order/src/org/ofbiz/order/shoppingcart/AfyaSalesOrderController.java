package org.ofbiz.order.shoppingcart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.ContextFilter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by pradyumna on 02-04-2015.
 */
public class AfyaSalesOrderController {

    private static final String PRODUCT_STORE_ID = "store.id.default";
    private static final String generalPropertiesFiles = "general.properties";
    private static final String currencyPropName = "currency.uom.id.default";
    private static final String FACILITY_ID = "facility.id.default";
    private static final String CUSTOMER_PARTY_ID = "10000";
    private static final String SHIPPING_LOC_ID = "default.customer.contact.mech.default";

    public static String createSalesOrderForPrescription(HttpServletRequest request, HttpServletResponse response) {
        try {


            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above
            Prescription prescription = mapper.readValue(request.getInputStream(), Prescription.class);


            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            Delegator delegator = (GenericDelegator) request.getAttribute("delegator");

            String tenantId=request.getParameter("tenantId");
            String delegatorName = delegator.getDelegatorBaseName() + "#" + tenantId;
            try {
                // after this line the delegator is replaced with the new
                // per-tenant delegator
                delegator = DelegatorFactory.getDelegator(delegatorName);
                dispatcher = ContextFilter.makeWebappDispatcher(request.getServletContext(), delegator);
            } catch (Exception e) {

            }

            Locale locale = UtilHttp.getLocale(request);
            String currencyUom = UtilProperties.getPropertyValue(generalPropertiesFiles, currencyPropName);
            String productStoreId = UtilProperties.getPropertyValue(generalPropertiesFiles, PRODUCT_STORE_ID);
            String facilityId = UtilProperties.getPropertyValue(generalPropertiesFiles, FACILITY_ID);
            String destination = UtilProperties.getPropertyValue(generalPropertiesFiles, SHIPPING_LOC_ID);

            ShoppingCart cart = new org.ofbiz.order.shoppingcart.ShoppingCart(delegator, productStoreId, locale, currencyUom);
            GenericValue userLogin = delegator.findOne("UserLogin", true, "userLoginId", "system");

            cart.setOrderType("SALES_ORDER");
            cart.setUserLogin(userLogin, dispatcher);
            cart.setProductStoreId(productStoreId);

            cart.setOrderPartyId(CUSTOMER_PARTY_ID);

            addItemsToCart(dispatcher, cart, prescription.getRows());
            cart.setDefaultCheckoutOptions(dispatcher);
            cart.setChannelType("AFYA_SALES_CHANNEL");
            cart.setShipmentMethodTypeId("PICKUP");
            cart.setCarrierPartyId("_NA_");
            cart.setShippingOriginContactMechId(1, destination);
            cart.setBillFromVendorPartyId("Company");
            cart.setFacilityId(facilityId);
            cart.setPlacingCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setBillToCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setShipToCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setEndUserCustomerPartyId(CUSTOMER_PARTY_ID);


            CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, dispatcher.getDelegator(), cart);
            java.util.Map orderCreate = checkOutHelper.createOrder(userLogin);
            String orderId = (String) orderCreate.get("orderId");

            Map presciptionData = UtilMisc.toMap("orderId", orderId, "visitId", prescription.getVisitId(), "clinicId", prescription.getClinicId(),
                    "afyaId", prescription.getAfyaId(), "patientFirstName", prescription.getFirstName(), "patientLastName", prescription.getLastName(),
                    "visitDate", UtilDateTime.toSqlDate(prescription.getVisitDate()), "patientType", prescription.getPatientType(),
                    "mobileNumber", prescription.getMobile());
            GenericValue genericValue = delegator.makeValidValue("OrderRxHeader",presciptionData);
            delegator.create(genericValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    private static void addItemsToCart(LocalDispatcher dispatcher, ShoppingCart cart, List<LineItem> rxLineItems) throws Exception {

        int cartIndex = 0;
        ShoppingCart.ShoppingCartItemGroup itemGroup = cart.getItemGroupByNumber("0001");
        for (LineItem eachRxRow : rxLineItems) {
            String productName = eachRxRow.getTradeName();
            GenericValue productGV = fetchMatchingProduct(dispatcher.getDelegator(), productName);
            Map result = dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", productGV));
            System.out.println(" calculateProductPrice " + result);
            BigDecimal quantity = eachRxRow.getQuantity();
            BigDecimal unitPrice = (BigDecimal) result.get("defaultPrice");
            BigDecimal selectedAmount = unitPrice;
            String itemType = "PRODUCT_ORDER_ITEM";
            boolean triggerExternalOpsBool = false;
            boolean triggerPriceRulesBool = false;
            boolean skipInventoryChecks = true;
            boolean skipProductChecks = false;
            ShoppingCartItem cartItem = ShoppingCartItem.makeItem(cartIndex++, productGV.getString("productId"), selectedAmount, quantity, unitPrice
                    , null, null, null, null, null, null, null, null, null, itemType, itemGroup, dispatcher, cart, triggerExternalOpsBool, triggerPriceRulesBool, null, skipInventoryChecks, skipProductChecks);
            cartItem.setItemComment(eachRxRow.getDetails());
            cartItem.setHomeService(eachRxRow.isHomeService());
//            cart.setItemShipGroupQty(cartItem, quantity, 1);
        }
    }

    private static GenericValue fetchMatchingProduct(Delegator delegator, String productName) throws GenericEntityException {
        List<GenericValue> values = delegator.findList("Product", EntityCondition.makeCondition("internalName", productName), null, null, null, true);
        return EntityUtil.getFirst(values);
    }

    public static String testJsonPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above
        Prescription prescription = mapper.readValue(request.getInputStream(), Prescription.class);

        try { // Send back to client
            OutputStream out = response.getOutputStream();
            mapper.writeValue(out, prescription);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    static class Prescription {

        private String clinicId;
        private String afyaId;
        private List<LineItem> rows;
        private String visitId;
        private Date visitDate;
        private String firstName;
        private String lastName;
        private String mobile;
        private String patientType;
        private String clinicName;
        private String doctorName;

        public String getClinicName() {
            return clinicName;
        }

        public void setClinicName(String clinicName) {
            this.clinicName = clinicName;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        public String getPatientType() {
            return patientType;
        }

        public void setPatientType(String patientType) {
            this.patientType = patientType;
        }

        public String getClinicId() {
            return clinicId;
        }

        public void setClinicId(String clinicId) {
            this.clinicId = clinicId;
        }

        public String getAfyaId() {
            return afyaId;
        }

        public void setAfyaId(String afyaId) {
            this.afyaId = afyaId;
        }

        public List<LineItem> getRows() {
            return rows;
        }

        public void setRows(List<LineItem> rows) {
            this.rows = rows;
        }

        public String getVisitId() {
            return visitId;
        }

        public void setVisitId(String visitId) {
            this.visitId = visitId;
        }

        public Date getVisitDate() {
            return visitDate;
        }

        public void setVisitDate(Date visitDate) {
            this.visitDate = visitDate;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    static class LineItem {
        private String tradeName;
        private BigDecimal quantity;
        private String details;
        private boolean homeService;
        private String genericName;

        public String getGenericName() {
            return genericName;
        }

        public void setGenericName(String genericName) {
            this.genericName = genericName;
        }

        public String getTradeName() {
            return tradeName;
        }

        public void setTradeName(String tradeName) {
            this.tradeName = tradeName;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public boolean isHomeService() {
            return homeService;
        }

        public void setHomeService(boolean homeService) {
            this.homeService = homeService;
        }
    }
}
