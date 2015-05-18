package org.ofbiz.order.shoppingcart;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javolution.util.FastList;

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
        Map responseStatus = new HashMap();
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
            GenericValue userLogin = delegator.findOne("UserLogin", true, "userLoginId", "system");

            cart.setOrderType("SALES_ORDER");
            cart.setUserLogin(userLogin, dispatcher);
            cart.setDefaultCheckoutOptions(dispatcher);

            addItemsToCart(dispatcher, cart, prescription.getRows());
            String facilityId = UtilProperties.getPropertyValue(generalPropertiesFiles, FACILITY_ID);
            String destination = UtilProperties.getPropertyValue(generalPropertiesFiles, SHIPPING_LOC_ID);
            cart.setPlacingCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setBillToCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setShipToCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setEndUserCustomerPartyId(CUSTOMER_PARTY_ID);
            cart.setChannelType("AFYA_SALES_CHANNEL");
            cart.setShipmentMethodTypeId("PICKUP");
            cart.setCarrierPartyId("_NA_");
            cart.setShippingOriginContactMechId(1, destination);
            cart.setBillFromVendorPartyId("Company");
            cart.setFacilityId(facilityId);
            cart.setOrderPartyId(CUSTOMER_PARTY_ID);

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
            patientInfo.setPatientType(prescription.getPatientType());
            patientInfo.setAddress(prescription.getAddress());
            patientInfo.setHisBenefitId(prescription.getHisBenefitId());
            patientInfo.setModuleName(prescription.getModuleName());
            patientInfo.setBenefitId(prescription.getBenefitId());
            patientInfo.setIsOrderApproved(prescription.getIsOrderApproved());
            cart.setPatientInfo(patientInfo);
            CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, dispatcher.getDelegator(), cart);
            java.util.Map orderCreate = checkOutHelper.createOrder(userLogin);
            String orderId = (String) orderCreate.get("orderId");
            /*responseStatus.put("statusCode",200);
            responseStatus.put("orderId",orderId);
            responseStatus.put("message","Order successfully placed.");*/
            GenericValue orderRxHeader = delegator.findOne("OrderRxHeader", UtilMisc.toMap("orderId", orderId), false);
            String afyaId = orderRxHeader.getString("afyaId");
            String firstName = orderRxHeader.getString("firstName");
            String thirdName = orderRxHeader.getString("thirdName");
            Date dob = ((Date) orderRxHeader.get("dateOfBirth"));
            List<GenericValue> patientDetails = FastList.newInstance();
            if(afyaId != null || UtilValidate.isNotEmpty(afyaId)) {
                patientDetails = delegator.findByAnd("Patient", UtilMisc.toMap("afyaId", afyaId), null, false);
            }  else {
                patientDetails = delegator.findByAnd("Patient", UtilMisc.toMap("firstName", firstName, "thirdName", thirdName, "dateOfBirth"), null, false);
            }
            
            if(UtilValidate.isEmpty(patientDetails)) {
            
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                List<MediaType> mediaTypes = new ArrayList<MediaType>();
                mediaTypes.add(MediaType.APPLICATION_JSON);
                httpHeaders.setAccept(mediaTypes);
                HttpEntity<String> requestEntity = new HttpEntity<String>(httpHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange("http://5.9.249.197:7878/afya-portal/anon/fetchPatientByAfyaId?afyaId={afyaId}", HttpMethod.GET, requestEntity, String.class, afyaId);
                String repsonseJson = responseEntity.getBody();
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                Map<String, Object> map = new HashMap();
                try {
                    map = mapper.readValue(repsonseJson, map.getClass());
                    String patientId = delegator.getNextSeqId("Patient");
                    String dateOfBirth = (String) map.get("dateOfBirth");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    GenericValue patient = delegator.makeValidValue("Patient", UtilMisc.toMap("patientId", patientId));
                    patient.set("afyaId", map.get("afyaId"));
                    patient.set("civilId", map.get("civilId"));
                    if(map.get("patientType").equals("CASH PAYING")) {
                        patient.set("patientType", "CASH");
                    } else {
                        patient.set("patientType", map.get("patientType"));
                    }
                    patient.set("title", map.get("salutation"));
                    patient.set("firstName", map.get("firstName"));
                    patient.set("secondName", map.get("middleName"));
                    patient.set("thirdName", map.get("lastName"));
                    patient.set("fourthName", map.get("endMostName"));
                    if(map.get("gender").equals("Male")) {
                        patient.set("gender", "M");
                    } else if(map.get("gender").equals("Female")) {
                    	patient.set("gender", "F");
                    } else {
                        patient.set("gender", null);
                    }
                    patient.set("dateOfBirth", new java.sql.Date(format.parse(dateOfBirth).getTime()));
                    patient.set("bloodGroup", map.get("bloodGroup"));
                    patient.set("rH", map.get("rh"));
                    if(map.get("maritalStatus").equals("Annulled")) {
                    	patient.set("maritalStatus", "ANNULLED");
                    } else if(map.get("maritalStatus").equals("Divorced")) {
                    	patient.set("maritalStatus", "DIVORCED");
                    } else if(map.get("maritalStatus").equals("Domestic Partner")) {
                    	patient.set("maritalStatus", "DOMESTIC_PARTNER");
                    } else if(map.get("maritalStatus").equals("Legally Separated")) {
                    	patient.set("maritalStatus", "LEGALLY_SEPARATED");
                    } else if(map.get("maritalStatus").equals("Living Together")) {
                    	patient.set("maritalStatus", "LIVING_TOGETHER");
                    } else if(map.get("maritalStatus").equals("Married")) {
                    	patient.set("maritalStatus", "MARRIED");
                    } else if(map.get("maritalStatus").equals("Other")) {
                    	patient.set("maritalStatus", "OTHER");
                    } else if(map.get("maritalStatus").equals("Separated")) {
                    	patient.set("maritalStatus", "SEPARATED");
                    } else if(map.get("maritalStatus").equals("Single")) {
                    	patient.set("maritalStatus", "SINGLE");
                    } else if(map.get("maritalStatus").equals("Unmarried")) {
                    	patient.set("maritalStatus", "UNMARRIED");
                    } else if(map.get("maritalStatus").equals("Widowed")) {
                    	patient.set("maritalStatus", "WIDOWED");
                    } else {
                    	patient.set("maritalStatus", map.get("maritalStatus"));
                    }
                    patient.set("address1", map.get("address"));
                    patient.set("address2", map.get("additionalAddress"));
                    patient.set("city", map.get("city"));
                    patient.set("governorate", map.get("state"));
                    patient.set("postalCode", map.get("postalCode"));
                    patient.set("country", map.get("country"));
                    patient.set("nationality", map.get("nationality"));
                    patient.set("emailAddress", map.get("emailId"));
                    patient.set("mobilePhone", map.get("mobileNumber"));
                    patient.set("homePhone", map.get("homePhone"));
                    patient.set("officePhone", map.get("officePhone"));
                    patient.set("selectionType", "CIVIL_ID");
                    
                    delegator.create(patient);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            /*responseStatus.put("statusCode",500);
            responseStatus.put("message",e.getMessage());
            e.printStackTrace();
            response.setStatus(500);*/
        }
        return "success";

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
            BigDecimal selectedAmount = BigDecimal.ZERO;
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
            if(visitDate==null){
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
