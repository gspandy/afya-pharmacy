package org.ofbiz.order.shoppingcart;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.stats.VisitHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Created by Naren on 02-04-2015.
 */
public class AfyaSalesOrderController {

    public static final String module = AfyaSalesOrderController.class.getName();
    public static final String resource_error = "OrderErrorUiLabels";

    private static final String PRODUCT_STORE_ID = "store.id.default";
    private static final String generalPropertiesFiles = "general.properties";
    private static final String currencyPropName = "currency.uom.id.default";
    private static final String FACILITY_ID = "facility.id.default";
    private static final String CUSTOMER_PARTY_ID = "10000";
    private static final String SHIPPING_LOC_ID = "default.customer.contact.mech.default";

    public static final int scale = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    public static final BigDecimal ZERO = (BigDecimal.ZERO).setScale(scale, rounding);
    public static final BigDecimal percentage = (new BigDecimal("0.01")).setScale(scale, rounding);

    public static String createSalesOrderForPrescription(HttpServletRequest request, HttpServletResponse response) {
        //Map responseStatus = new HashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String orderId = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above
            Prescription prescription = mapper.readValue(request.getInputStream(), Prescription.class);

            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            Delegator delegator = (GenericDelegator) request.getAttribute("delegator");
            Locale locale = UtilHttp.getLocale(request);
            String currencyUom = UtilProperties.getPropertyValue(generalPropertiesFiles, currencyPropName);
            String productStoreId = UtilProperties.getPropertyValue(generalPropertiesFiles, PRODUCT_STORE_ID);

            ShoppingCart cart = new org.ofbiz.order.shoppingcart.ShoppingCart(delegator, productStoreId, locale, currencyUom);
            //GenericValue userLogin = delegator.findOne("UserLogin", true, "userLoginId", "system");
            GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
            HttpSession session = request.getSession();

            if (UtilValidate.isEmpty(userLogin)) {
                userLogin = (GenericValue)session.getAttribute("userLogin");
            }

            // remove this whenever creating an order so quick reorder cache will
            // refresh/recalc
            session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

            boolean areOrderItemsExploded = explodeOrderItems(delegator, cart);

            // get the TrackingCodeOrder List
            /*
             * Changes made for not to use marketing service
             * TrackingCodeEvents.java Since marketing module is deleted in this
             */
            List trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);

            String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
            String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
            String visitId = VisitHandler.getVisitId(session);
            String webSiteId = CatalogWorker.getWebSiteId(request);

            cart.setOrderType("SALES_ORDER");
            cart.setUserLogin(userLogin, dispatcher);
            cart.setDefaultCheckoutOptions(dispatcher);

            addItemsToCart(dispatcher, cart, prescription.getRows());
            String doctor = prescription.getDoctorName();
            String clinic = prescription.getClinicName();
            String clinicId = prescription.getClinicId();
            BigDecimal referralAmount = ZERO;

            //Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            List<EntityExpr> exprs = FastList.newInstance();
            exprs.add(EntityCondition.makeCondition("referralName", EntityOperator.EQUALS, doctor));
            exprs.add(EntityCondition.makeCondition("clinicName", EntityOperator.EQUALS, clinic));
            exprs.add(EntityCondition.makeCondition("clinicId", EntityOperator.EQUALS, clinicId));
            exprs.add(EntityCondition.makeCondition("contractStatus", EntityOperator.EQUALS, "ACTIVE"));
            //exprs.add(EntityCondition.makeCondition("contractFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
            //exprs.add(EntityCondition.makeCondition("contractThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp));
            List<GenericValue> referralList = delegator.findList("ReferralContract", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);

            if (UtilValidate.isNotEmpty(referralList))
                referralAmount = getReferralAmount(dispatcher, delegator, doctor, clinic, clinicId, prescription.getRows());
            /*String facilityId = UtilProperties.getPropertyValue(generalPropertiesFiles, FACILITY_ID);
            String destination = UtilProperties.getPropertyValue(generalPropertiesFiles, SHIPPING_LOC_ID);
            cart.setPlacingCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setBillToCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setShipToCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setEndUserCustomerPartyId(CUSTOMER_PARTY_ID);*/
            cart.setChannelType("AFYA_SALES_CHANNEL");
            cart.setShipmentMethodTypeId("PICKUP");
            cart.setCarrierPartyId("_NA_");
            //cart.setShippingOriginContactMechId(1, destination);
            cart.setBillFromVendorPartyId("Company");
            //cart.setFacilityId(facilityId);
            cart.setOrderPartyId("admin");

            PatientInfo patientInfo = new PatientInfo();
            patientInfo.setAfyaId(prescription.getAfyaId());
            patientInfo.setCivilId(prescription.getCivilId());
            patientInfo.setThirdName(prescription.getLastName());
            patientInfo.setFirstName(prescription.getFirstName());
            patientInfo.setGender(prescription.getGender());
            patientInfo.setDateOfBirth(prescription.getDateOfBirth());
            patientInfo.setMobile(prescription.getMobile());
            patientInfo.setClinicId(prescription.getClinicId());
            patientInfo.setClinicName(prescription.getClinicName());
            patientInfo.setDoctorName(prescription.getDoctorName());
            patientInfo.setVisitDate(prescription.getVisitDate());
            patientInfo.setVisitId(prescription.getVisitId());
            if (prescription.getPatientType().equals("CASH PAYING")) {
                patientInfo.setPatientType("CASH");
            } else {
                patientInfo.setPatientType(prescription.getPatientType());
            }
            patientInfo.setAddress(prescription.getAddress());
            patientInfo.setHisBenefitId(prescription.getHisBenefitId());
            patientInfo.setModuleName(prescription.getModuleName());
            patientInfo.setBenefitId(prescription.getBenefitId());
            patientInfo.setIsOrderApproved(prescription.getIsOrderApproved());
            patientInfo.setIsOrderFromClinic("Y");
            patientInfo.setMobileNumberVisibleForDelivery(String.valueOf(prescription.getMobileNumberVisibleForDelivery()));

            if (prescription.getCorporateCopay() != null)
                patientInfo.setCopay(prescription.getCorporateCopay());

            patientInfo.setCopayType(prescription.getCorporateCopayType());
            patientInfo.setPrimaryPayer(prescription.getCorporatePrimaryPayer());

            if (referralAmount != ZERO)
                patientInfo.setReferralAmount(referralAmount);

            cart.setPatientInfo(patientInfo);

            String afyaId = prescription.getAfyaId();
            String firstName = prescription.getFirstName();
            String thirdName = prescription.getLastName();
            java.sql.Date dob = UtilDateTime.toSqlDate(prescription.getDateOfBirth());

            List<GenericValue> patientDetails = FastList.newInstance();
            if (afyaId != null || UtilValidate.isNotEmpty(afyaId)) {
                patientDetails = delegator.findByAnd("Patient", UtilMisc.toMap("afyaId", afyaId), null, false);
            }  else {
                patientDetails = delegator.findByAnd("Patient", UtilMisc.toMap("firstName", firstName, "thirdName", thirdName, "dateOfBirth", dob), null, false);
            }

            if (UtilValidate.isEmpty(patientDetails)) {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                List<MediaType> mediaTypes = new ArrayList<MediaType>();
                mediaTypes.add(MediaType.APPLICATION_JSON);
                httpHeaders.setAccept(mediaTypes);
                HttpEntity<String> requestEntity = new HttpEntity<String>(httpHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange("http://5.9.249.197:7878/afya-portal/anon/fetchPatientByAfyaId?afyaId={afyaId}", HttpMethod.GET, requestEntity, String.class, afyaId);
                String repsonseJson = responseEntity.getBody();
                if(repsonseJson != null) {
                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    Map<String, Object> map = new HashMap<String, Object>();
                    try {
                        map = mapper.readValue(repsonseJson, map.getClass());
                        String dateOfBirth = (String) map.get("dateOfBirth");

                        String patientId = delegator.getNextSeqId("Patient");
                        GenericValue patient = delegator.makeValidValue("Patient", UtilMisc.toMap("patientId", patientId));
                        patient.set("afyaId", map.get("afyaId"));
                        patient.set("civilId", map.get("civilId"));
                        if (map.get("patientType") == null || ("CASH PAYING").equals(map.get("patientType"))) {
                            patient.set("patientType", "CASH");
                        } else {
                            patient.set("patientType", map.get("patientType"));
                        }
                        patient.set("title", map.get("salutation"));
                        patient.set("firstName", map.get("firstName"));
                        patient.set("secondName", map.get("middleName"));
                        patient.set("thirdName", map.get("lastName"));
                        patient.set("fourthName", map.get("endMostName"));
                        if (("Male").equals(map.get("gender"))) {
                            patient.set("gender", "M");
                        } else if (("Female").equals(map.get("gender"))) {
                            patient.set("gender", "F");
                        } else {
                            patient.set("gender", null);
                        }
                        if(dateOfBirth != null)
                            patient.set("dateOfBirth", new java.sql.Date(format.parse(dateOfBirth).getTime()));
                        patient.set("bloodGroup", map.get("bloodGroup"));
                        patient.set("rH", map.get("rh"));

                        if (map.get("maritalStatus") != null){

                            if (("Annulled").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "ANNULLED");
                            } else if (("Divorced").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "DIVORCED");
                            } else if (("Domestic Partner").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "DOMESTIC_PARTNER");
                            } else if (("Legally Separated").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "LEGALLY_SEPARATED");
                            } else if (("Living Together").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "LIVING_TOGETHER");
                            } else if (("Married").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "MARRIED");
                            } else if (("Other").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "OTHER");
                            } else if (("Separated").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "SEPARATED");
                            } else if (("Single").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "SINGLE");
                            } else if (("Unmarried").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "UNMARRIED");
                            } else if (("Widowed").equals(map.get("maritalStatus"))) {
                                patient.set("maritalStatus", "WIDOWED");
                            } else {
                                patient.set("maritalStatus", map.get("maritalStatus"));
                            }

                        }

                        patient.set("address1", map.get("address"));
                        patient.set("address2", map.get("additionalAddress"));
                        patient.set("city", map.get("city"));
                        patient.set("governorate", map.get("state"));
                        patient.set("postalCode", map.get("postalCode"));
                        patient.set("country", map.get("country"));
                        patient.set("nationality", map.get("nationality"));
                        patient.set("emailAddress", map.get("emailId"));
                        patient.set("isdCode", map.get("isdCode"));
                        patient.set("mobilePhone", map.get("mobileNumber"));
                        patient.set("homePhone", map.get("homePhone"));
                        patient.set("officePhone", map.get("officePhone"));
                        patient.set("selectionType", "CIVIL_ID");

                        delegator.create(patient);
                        Debug.logError(patient.toString(), module);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Debug.logError("Inside Patient Creation Error", module);
                    }
                }
            }

            CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, dispatcher.getDelegator(), cart);
            Map<String, Object> orderCreate = checkOutHelper.createOrder(userLogin, distributorId, affiliateId, trackingCodeOrders, areOrderItemsExploded, visitId, webSiteId);
            //Map<String, Object> orderCreate = checkOutHelper.createOrder(userLogin);
            orderId = (String) orderCreate.get("orderId");
            /*responseStatus.put("statusCode",200);
            responseStatus.put("orderId",orderId);
            responseStatus.put("message","Order successfully placed.");*/

            if (UtilValidate.isNotEmpty(referralList)) {
                String contractPaymentId = delegator.getNextSeqId("ReferralContractPayment");
                GenericValue referralPayment = delegator.makeValidValue("ReferralContractPayment", UtilMisc.toMap("contractPaymentId", contractPaymentId));
                referralPayment.set("orderId", orderId);
                referralPayment.set("referralName", prescription.getDoctorName());
                referralPayment.set("clinicId", prescription.getClinicId());
                referralPayment.set("clinicName", prescription.getClinicName());
                referralPayment.set("referralPayableAmount", referralAmount);
                referralPayment.set("payableCurrencyUomId", currencyUom);
                referralPayment.set("paymentStatusId", "REF_PMNT_PENDING");
                delegator.create(referralPayment);
                Debug.logError(referralPayment.toString(), module);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            GenericValue ordHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            String currencyUomId = ordHeader.getString("currencyUom");
            BigDecimal orderSubTotal = getOrderSubTotal(dispatcher, delegator, orderId);

            OrderHeader orderDetail = new OrderHeader();
            orderDetail.setOrderId(orderId);
            orderDetail.setCurrencyUom(currencyUomId);
            orderDetail.setTotalAmount(orderSubTotal);

            try {
                request.setCharacterEncoding("utf8");
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                objectMapper.writeValue(out, orderDetail);
                Debug.logError(orderDetail.toString(), module);
            } catch (Exception e) {
                e.printStackTrace();
                Debug.logError("Inside Response Error", module);
            }

            /*RxOrder Confirmation Notification Mail*/
            String patientName = prescription.getFirstName() + " " + prescription.getLastName();
            System.out.println("Sending Order Rx Confirmation Notification Mail to " + patientName);
            dispatcher.runSync("sendOrderConfirmation", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));

        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError("Inside Main Try Error", module);
            /*responseStatus.put("statusCode",500);
            responseStatus.put("message",e.getMessage());
            e.printStackTrace();
            response.setStatus(500);*/
        }

        return "success";
    }

    private static BigDecimal getOrderSubTotal(LocalDispatcher dispatcher, Delegator delegator, String orderId) {
        List<GenericValue> orderItemsList = null;

        BigDecimal orderSubTotal = ZERO;

        EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("orderId", orderId),
                EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ITEM_CREATED","ITEM_APPROVED"))),
                EntityOperator.AND);

        try {
            orderItemsList = delegator.findList("OrderItem", condition, null, null, null, false);
        } catch (GenericEntityException e1) {
            e1.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(orderItemsList) || orderItemsList != null) {

            for (GenericValue oi : orderItemsList) {

                BigDecimal quantity = oi.getBigDecimal("quantity").setScale(scale, rounding);
                BigDecimal unitPrice = oi.getBigDecimal("unitPrice").setScale(scale, rounding);
                BigDecimal itemSubTotal = quantity.multiply(unitPrice).setScale(scale, rounding);

                orderSubTotal = orderSubTotal.add(itemSubTotal);
            }
        }

        return orderSubTotal;
    }

    private static BigDecimal getReferralAmount(LocalDispatcher dispatcher, Delegator delegator, String doctor, String clinic, String clinicId, List<LineItem> rxLineItems) throws GenericEntityException, GenericServiceException {
        BigDecimal referralAmount = ZERO;
        List<Map<String, Object>> orderItemList = new ArrayList<>();
        for (LineItem eachRxRow : rxLineItems) {
            Map<String, Object> orderItem = new LinkedHashMap<>();
            String productName = eachRxRow.getTradeName();
            GenericValue productGV = fetchMatchingProduct(dispatcher.getDelegator(), productName);
            Map<String, Object> result = dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", productGV));
            System.out.println(" calculateProductPrice " + result);
            String productId = productGV.getString("productId");
            BigDecimal quantity = eachRxRow.getQuantity();
            BigDecimal unitPrice = (BigDecimal) result.get("defaultPrice");
            BigDecimal subTotal = quantity.multiply(unitPrice);
            orderItem.put("productId", productId);
            orderItem.put("subTotal", subTotal);
            orderItemList.add(orderItem);
        }
        if (UtilValidate.isNotEmpty(orderItemList)) {
            Iterator<Map<String, Object>> oi = orderItemList.iterator();
            Map<String, List<GenericValue>> orderItemAndCategoryMapping = new HashMap<String, List<GenericValue>>();

            while (oi.hasNext()) {
                Map<String, Object> orderLineItem = oi.next();
                try {
                    GenericValue productGv = delegator.findOne("Product", UtilMisc.toMap("productId", orderLineItem.get("productId")), false);
                    String primaryProductCategoryId = productGv.getString("primaryProductCategoryId");
                    GenericValue productCategory = delegator.findOne("ProductCategory", false, "productCategoryId", primaryProductCategoryId);
                    if (productCategory != null) {
                        String mappedProductCategories = productCategory.getString("productCategoryId");
                        if (mappedProductCategories != null) {
                            if (mappedProductCategories.indexOf(",") != -1) {
                                for (String categoryId : mappedProductCategories.split(",")) {
                                    List oiList = (List) orderItemAndCategoryMapping.get(categoryId);
                                    if (oiList == null) {
                                        oiList = new ArrayList();
                                    }
                                    oiList.add(orderLineItem);
                                    orderItemAndCategoryMapping.put(categoryId, oiList);
                                }
                            } else {
                                List oiList = (List) orderItemAndCategoryMapping.get(mappedProductCategories);
                                if (oiList == null) {
                                    oiList = new ArrayList();
                                }
                                oiList.add(orderLineItem);
                                orderItemAndCategoryMapping.put(mappedProductCategories, oiList);
                            }
                        }
                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }
            }
            //Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            List<EntityExpr> exprs = FastList.newInstance();
            exprs.add(EntityCondition.makeCondition("referralName", EntityOperator.EQUALS, doctor));
            exprs.add(EntityCondition.makeCondition("clinicName", EntityOperator.EQUALS, clinic));
            exprs.add(EntityCondition.makeCondition("clinicId", EntityOperator.EQUALS, clinicId));
            exprs.add(EntityCondition.makeCondition("contractStatus", EntityOperator.EQUALS, "ACTIVE"));
            //exprs.add(EntityCondition.makeCondition("contractFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
            //exprs.add(EntityCondition.makeCondition("contractThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp));
            List<GenericValue> referralList = delegator.findList("ReferralContract", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
            if (UtilValidate.isNotEmpty(referralList)) {
                GenericValue referralContract = EntityUtil.getFirst(referralList);
                if ("PERCENTAGE_OF_BILL".equals(referralContract.getString("paymentMode"))) {
                    BigDecimal percentageOnBill = referralContract.getBigDecimal("percentageOnBill");
                    for (Map<String, Object> orderItem : orderItemList) {
                        referralAmount = referralAmount.add((BigDecimal) orderItem.get("subTotal"));
                    }
                    referralAmount = referralAmount.multiply(percentageOnBill).setScale(scale, rounding).multiply(percentage);
                } else if ("PERCENTAGE_SERVICE_ITEM".equals(referralContract.getString("paymentMode"))) {
                    Map<String,Object> categoryMap = new LinkedHashMap<String, Object>();
                    BigDecimal lineItemSubTotal = ZERO;

                    for (Map.Entry<String, List<GenericValue>> oic: orderItemAndCategoryMapping.entrySet()) {
                        String categoryId = oic.getKey();
                        List<GenericValue> values = oic.getValue();
                        for (Map<String, Object> value : values) {
                            BigDecimal subTotal = new BigDecimal(value.get("subTotal").toString()).setScale(scale, rounding);
                            lineItemSubTotal = lineItemSubTotal.add(subTotal);
                        }
                        categoryMap.put(categoryId,lineItemSubTotal);
                    }
                    if (categoryMap != null) {
                        for (Map.Entry<String, Object> category : categoryMap.entrySet()) {
                            String contractId = referralContract.getString("contractId");
                            String categoryId = category.getKey();
                            GenericValue contractServiceGv = delegator.findOne("ReferralContractService", UtilMisc.toMap("contractId", contractId, "productCategoryId", categoryId),false);
                            if (contractServiceGv != null && contractServiceGv.getBigDecimal("paymentPercentage") != null) {
                                BigDecimal productCategoryTotal = new BigDecimal(category.getValue().toString()).setScale(scale, rounding);
                                BigDecimal paymentPercentage = contractServiceGv.getBigDecimal("paymentPercentage");
                                BigDecimal categoryAmount = productCategoryTotal.multiply(paymentPercentage).setScale(scale, rounding).multiply(percentage);
                                referralAmount = referralAmount.add(categoryAmount).setScale(scale, rounding);
                            }
                        }
                    }
                } else if ("FIX_AMOUNT_PER_SERVICE".equals(referralContract.getString("paymentMode"))) {
                    if (orderItemAndCategoryMapping != null) {
                        Set<String> categoryIds = orderItemAndCategoryMapping.keySet();
                        for (String categoryId : categoryIds) {
                            String contractId = referralContract.getString("contractId");
                            GenericValue contractServiceGv = delegator.findOne("ReferralContractService", UtilMisc.toMap("contractId", contractId, "productCategoryId", categoryId),false);
                            if (contractServiceGv != null && contractServiceGv.getBigDecimal("paymentAmount") != null) {
                                BigDecimal paymentAmount = contractServiceGv.getBigDecimal("paymentAmount");
                                referralAmount = referralAmount.add(paymentAmount);
                            }
                        }
                    }
                } else {
                    referralAmount = ZERO;
                }
            }
        }
        return referralAmount;
    }

    public static boolean explodeOrderItems(Delegator delegator, ShoppingCart cart) {
        if (cart == null)
            return false;
        GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
        if (productStore == null || productStore.get("explodeOrderItems") == null) {
            return false;
        }
        return productStore.getBoolean("explodeOrderItems").booleanValue();
    }

    private static void addItemsToCart(LocalDispatcher dispatcher, ShoppingCart cart, List<LineItem> rxLineItems) throws Exception {

        int cartIndex = 0;
        ShoppingCart.ShoppingCartItemGroup itemGroup = cart.getItemGroupByNumber("0001");
        for (LineItem eachRxRow : rxLineItems) {
            String productName = eachRxRow.getTradeName();
            GenericValue productGV = fetchMatchingProduct(dispatcher.getDelegator(), productName);
            Map<String, Object> result = dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", productGV));
            System.out.println(" calculateProductPrice " + result);
            BigDecimal quantity = eachRxRow.getQuantity();
            BigDecimal unitPrice = (BigDecimal) result.get("defaultPrice");
            BigDecimal selectedAmount = ZERO;
            String itemType = "PRODUCT_ORDER_ITEM";
            boolean triggerExternalOpsBool = false;
            boolean triggerPriceRulesBool = false;
            boolean skipInventoryChecks = true;
            boolean skipProductChecks = false;
            ShoppingCartItem cartItem = ShoppingCartItem.makeItem(cartIndex++, productGV.getString("productId"), selectedAmount, quantity, unitPrice,
                    null, null, null, null, null, null, null, null, null, itemType, itemGroup, dispatcher, cart, triggerExternalOpsBool, triggerPriceRulesBool, null, skipInventoryChecks, skipProductChecks);
            cartItem.setItemComment(eachRxRow.getDetails());
            cartItem.setHomeService(eachRxRow.isHomeService());
            //cart.setItemShipGroupQty(cartItem, quantity, 1);
        }
    }

    private static GenericValue fetchMatchingProduct(Delegator delegator, String productName) throws GenericEntityException {
        List<GenericValue> values = delegator.findList("Product", EntityCondition.makeCondition("internalName", productName), null, null, null, true);
        return EntityUtil.getFirst(values);
    }

    public static String fetchItemsByRxOrder(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (GenericDelegator) request.getAttribute("delegator");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above
        Map<String,String> map = null;
        try {
            map = mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String orderId = map.get("orderId");
        String currencyUom = UtilProperties.getPropertyValue(generalPropertiesFiles, currencyPropName);
        BigDecimal orderSubTotal = ZERO;

        List<GenericValue> orderItemsList = null;
        List<Map<String, Object>> rxOrderItemList = FastList.newInstance();
        Map<String,Object> rxOrderMap = new LinkedHashMap<String, Object>();

        EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("orderId", orderId),
                EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ITEM_CREATED","ITEM_APPROVED"))),
                EntityOperator.AND);

        try {
            orderItemsList = delegator.findList("OrderItem", condition, null, null, null, false);
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            currencyUom = orderHeader.getString("currencyUom");
        } catch (GenericEntityException e1) {
            e1.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(orderItemsList)) {

            for (GenericValue oi : orderItemsList) {

                Map<String, Object> orderItem = FastMap.newInstance();
                BigDecimal quantity = oi.getBigDecimal("quantity").setScale(scale, rounding);
                BigDecimal unitPrice = oi.getBigDecimal("unitPrice").setScale(scale, rounding);
                BigDecimal itemSubTotal = quantity.multiply(unitPrice).setScale(scale, rounding);

                orderItem.put("drugName", oi.getString("itemDescription"));
                orderItem.put("frequency", oi.getString("comments"));
                orderItem.put("quantity", quantity);
                orderItem.put("unitPrice", unitPrice);
                orderItem.put("itemSubTotal", itemSubTotal);
                orderItem.put("currencyUom", currencyUom);
                rxOrderItemList.add(orderItem);

                orderSubTotal = orderSubTotal.add(itemSubTotal);

            }

            Map<String, Object> amountPayable = FastMap.newInstance();
            amountPayable.put("netPayableAmount", orderSubTotal);
            amountPayable.put("currencyUom", currencyUom);

            rxOrderMap.put("orderId", orderId);
            rxOrderMap.put("orderItems", rxOrderItemList);
            rxOrderMap.put("amountPayable", amountPayable);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            try {
                request.setCharacterEncoding("utf8");
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                objectMapper.writeValue(out, rxOrderMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return "success";
    }

    public static String receiveActivePrescriptionPayment(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = UtilHttp.getLocale(request);
        Delegator delegator = (GenericDelegator) request.getAttribute("delegator");

        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        HttpSession session = request.getSession();

        if(userLogin==null){
            userLogin = (GenericValue)session.getAttribute("userLogin");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above
        Map<String,String> map = null;
        try {
            map = mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String orderId = map.get("orderId");
        BigDecimal totalAmount = new BigDecimal(map.get("totalAmount")).setScale(scale, rounding);
        String currencyUom = UtilProperties.getPropertyValue(generalPropertiesFiles, currencyPropName);

        Map<String,Object> paymentStatusMap = new LinkedHashMap<String, Object>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try {
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            currencyUom = orderHeader.getString("currencyUom");

            EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("orderId", orderId),
                    EntityCondition.makeCondition("paymentMethodTypeId", "CASH"),
                    EntityCondition.makeCondition("statusId", "PAYMENT_NOT_RECEIVED")),
                    EntityOperator.AND);
            List<GenericValue> orderPaymentPrefs = delegator.findList("OrderPaymentPreference", condition, null, null, null, false);
            if (UtilValidate.isNotEmpty(orderPaymentPrefs)) {
                GenericValue orderPaymentPref = EntityUtil.getFirst(orderPaymentPrefs);
                orderId = orderPaymentPref.getString("orderId");
                String paymentPreferenceId = orderPaymentPref.getString("orderPaymentPreferenceId");

                String paymentPrefHistoryId = delegator.getNextSeqId("OrderPaymentPreferenceHistory");
                GenericValue orderPaymentPrefHistory = delegator.makeValidValue("OrderPaymentPreferenceHistory", UtilMisc.toMap(
                            "paymentPrefHistoryId", paymentPrefHistoryId));

                orderPaymentPrefHistory.set("paymentPrefHistoryId", paymentPrefHistoryId);
                orderPaymentPrefHistory.set("paymentPreferenceId", paymentPreferenceId);
                orderPaymentPrefHistory.set("orderId", orderId);
                orderPaymentPrefHistory.set("paymentMethodTypeId", "CREDIT_CARD");
                orderPaymentPrefHistory.set("currencyUomId", currencyUom);
                orderPaymentPrefHistory.set("amount", totalAmount);
                orderPaymentPrefHistory.set("statusId", "PMNT_RECEIVED");
                orderPaymentPrefHistory.set("receivedDate", UtilDateTime.nowTimestamp());
                orderPaymentPrefHistory.set("receivedBy", userLogin.get("partyId"));

                delegator.create(orderPaymentPrefHistory);


                /* For OrderPaymentPreference updation */
                GenericValue opp = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", paymentPreferenceId), false);

                /*(List<GenericValue> orderPaymentPrefHistoryList = delegator.findList("OrderPaymentPreferenceHistory", EntityCondition.makeCondition(
                            EntityCondition.makeCondition(UtilMisc.toMap("paymentPreferenceId",paymentPreferenceId,"orderId", orderId))), null, null,null, false);

                if (UtilValidate.isNotEmpty(orderPaymentPrefHistoryList)) {
                    BigDecimal totalAmountReceived = ZERO;
                    for (GenericValue orderPaymentPrefHist : orderPaymentPrefHistoryList) {
                        totalAmountReceived = totalAmountReceived.add(orderPaymentPrefHist.getBigDecimal("amount")).setScale(taxDecimals, taxRounding);
                    }
                    if (totalAmountReceived.compareTo(totalAmount) == 0)
                        opp.set("statusId", "PAYMENT_RECEIVED");
                }*/

                opp.set("amountReceived", totalAmount);
                opp.set("statusId", "PAYMENT_RECEIVED");
                delegator.store(opp);

                try {
                    request.setCharacterEncoding("utf8");
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    String successMsg = "Amount " + currencyUom + " " + totalAmount + " paid successfully for the Order '" + orderId + "'.";
                    paymentStatusMap.put("status", "success");
                    paymentStatusMap.put("message", successMsg);
                    objectMapper.writeValue(out, paymentStatusMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    request.setCharacterEncoding("utf8");
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    String errorMsg = "Amount " + currencyUom + " " + totalAmount + " can not paid for the Order '" + orderId + "'. Please contact customer service.";
                    paymentStatusMap.put("status", "error");
                    paymentStatusMap.put("message", errorMsg);
                    objectMapper.writeValue(out, paymentStatusMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            Debug.logError(e, "Problems getting payment preference for order: " + orderId, module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "checkhelper.problems_getting_payment_preference", locale));
            return "error";
        }

        return "success";
    }

    public static String completeActivePrescriptionOrder(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        HttpSession session = request.getSession();

        if(userLogin==null){
            userLogin = (GenericValue)session.getAttribute("userLogin");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above
        Map<String,String> map = null;
        try {
            map = mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String orderId = map.get("orderId");
        BigDecimal patientPayable = ZERO;
        String productStoreId = UtilProperties.getPropertyValue(generalPropertiesFiles, PRODUCT_STORE_ID);
        Map<String,Object> orderStatusMap = new LinkedHashMap<String, Object>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try {
            List<EntityExpr> exprs = FastList.newInstance();
            exprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"));
            List<GenericValue> orderPmntPrefHistList = delegator.findList("OrderPaymentPreferenceHistory", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
            if (UtilValidate.isNotEmpty(orderPmntPrefHistList)) {
                for (GenericValue orderPmntPrefHist : orderPmntPrefHistList) {
                    patientPayable = patientPayable.add(orderPmntPrefHist.getBigDecimal("amount"));
                }
                patientPayable = patientPayable.setScale(scale, rounding);
            }
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            if (orderHeader != null)
                productStoreId = orderHeader.getString("productStoreId");
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
            if (productStore != null && productStore.getString("reserveInventory").equals("Y"))
                try {
                    dispatcher.runSync("quickShipEntireOrder", UtilMisc.toMap("orderId", orderId, "patientPayable", patientPayable, "userLogin", userLogin));
                    try {
                        request.setCharacterEncoding("utf8");
                        response.setContentType("application/json");
                        PrintWriter out = response.getWriter();
                        String successMsg = "Order '" + orderId + "' completed successfully.";
                        orderStatusMap.put("status", "success");
                        orderStatusMap.put("message", successMsg);
                        objectMapper.writeValue(out, orderStatusMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (GenericServiceException e) {
                    e.printStackTrace();
                }
                
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return "success";
    }

    public static String cancelActivePrescriptionOrder(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        HttpSession session = request.getSession();

        if(userLogin==null){
            userLogin = (GenericValue)session.getAttribute("userLogin");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // 1.8 and above
        Map<String,String> map = null;
        try {
            map = mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String orderId = map.get("orderId");
        String statusId = "ORDER_CANCELLED";
        String setItemStatus = "Y";
        String productStoreId = UtilProperties.getPropertyValue(generalPropertiesFiles, PRODUCT_STORE_ID);

        Map<String,Object> orderStatusMap = new LinkedHashMap<String, Object>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try {
            /*RxOrder Cancellation Service Call*/
            System.out.println("Cancelling Active Prescription Order '" + orderId + "'.");
            dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("orderId", orderId, "statusId", statusId, "setItemStatus", setItemStatus, "userLogin", userLogin));
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }

        try {
            request.setCharacterEncoding("utf8");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            String successMsg = "Order '" + orderId + "' cancelled successfully.";
            orderStatusMap.put("status", "success");
            orderStatusMap.put("message", successMsg);
            objectMapper.writeValue(out, orderStatusMap);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            GenericValue orderRxHeader = delegator.findOne("OrderRxHeader", UtilMisc.toMap("orderId", orderId), false);
            if (orderRxHeader != null) {
                String patientName = orderRxHeader.getString("firstName") + " " + orderRxHeader.getString("thirdName");
                /*RxOrder Calcellation Notification Mail*/
                //System.out.println("Sending Order Rx Calcellation Notification Mail to " + patientName);
                //dispatcher.runSync("sendOrderConfirmation", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
            }
        } catch (GenericEntityException e2) {
            e2.printStackTrace();
        }

        return "success";
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

    static class OrderHeader {
        private String orderId;
        private String currencyUom;
        private BigDecimal totalAmount;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getCurrencyUom() {
            return currencyUom;
        }

        public void setCurrencyUom(String currencyUom) {
            this.currencyUom = currencyUom;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

    }

    static class Prescription {

        private String clinicId;
        private String afyaId;
        private List<LineItem> rows;
        private String civilId;
        private String visitId;
        private Date visitDate;
        private String firstName;
        private String lastName;
        private String gender;
        private Date dateOfBirth;
        private String mobile;
        private String patientType;
        private String clinicName;
        private String doctorName;
        private String address;
        private String hisBenefitId;
        private String moduleName;
        private String benefitId;
        private String isOrderApproved;
        private BigDecimal corporateCopay;
        private String corporateCopayType;
        private String corporatePrimaryPayer;
        private Boolean mobileNumberVisibleForDelivery;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

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

        public String getCivilId() {
            return civilId;
        }

        public void setCivilId(String civilId) {
            this.civilId = civilId;
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
            if (visitDate == null){
                return new Date();
            }
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

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Date getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getHisBenefitId() {
            return hisBenefitId;
        }

        public void setHisBenefitId(String hisBenefitId) {
            this.hisBenefitId = hisBenefitId;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getBenefitId() {
            return benefitId;
        }

        public void setBenefitId(String benefitId) {
            this.benefitId = benefitId;
        }

        public String getIsOrderApproved() {
            return isOrderApproved;
        }

        public void setIsOrderApproved(String isOrderApproved) {
            this.isOrderApproved = isOrderApproved;
        }

        public BigDecimal getCorporateCopay() {
            return corporateCopay;
        }

        public void setCorporateCopay(BigDecimal corporateCopay) {
            this.corporateCopay = corporateCopay;
        }

        public String getCorporateCopayType() {
            return corporateCopayType;
        }

        public void setCorporateCopayType(String corporateCopayType) {
            this.corporateCopayType = corporateCopayType;
        }

        public String getCorporatePrimaryPayer() {
            return corporatePrimaryPayer;
        }

        public void setCorporatePrimaryPayer(String corporatePrimaryPayer) {
            this.corporatePrimaryPayer = corporatePrimaryPayer;
        }

        public Boolean getMobileNumberVisibleForDelivery() {
            return mobileNumberVisibleForDelivery;
        }

        public void setMobileNumberVisibleForDelivery(Boolean mobileNumberVisibleForDelivery) {
            this.mobileNumberVisibleForDelivery = mobileNumberVisibleForDelivery;
        }

        @Override
        public String toString() {
            return "Prescription{" +
                    "clinicId='" + clinicId + '\'' +
                    ", afyaId='" + afyaId + '\'' +
                    ", civilId='" + civilId + '\'' +
                    ", rows=" + rows +
                    ", visitId='" + visitId + '\'' +
                    ", visitDate=" + visitDate +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", gender='" + gender + '\'' +
                    ", dateOfBirth=" + dateOfBirth +
                    ", mobile='" + mobile + '\'' +
                    ", patientType='" + patientType + '\'' +
                    ", isOrderApproved='" + isOrderApproved + '\'' +
                    ", clinicName='" + clinicName + '\'' +
                    ", doctorName='" + doctorName + '\'' +
                    ", address='" + address + '\'' +
                    '}';
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

        @Override
        public String toString() {
            return "LineItem{" +
                    "tradeName='" + tradeName + '\'' +
                    ", quantity=" + quantity +
                    ", details='" + details + '\'' +
                    ", homeService=" + homeService +
                    ", genericName='" + genericName + '\'' +
                    '}';
        }
    }
}
