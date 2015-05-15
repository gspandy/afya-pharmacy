package org.ofbiz.order.order;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

/**
 * Created by pradyumna on 23-04-2015.
 */
public class PatientServices {


    private static final String PORTAL_URL= UtilProperties.getPropertyValue("general.properties","portal.server.url","5.9.249.197:7878");

    public static Map registerPatientOnPortal(DispatchContext ctx, Map context) {

        Delegator delegator = ctx.getDelegator();
        try {
            String patientId = (String) context.get("patientId");
            GenericValue patientRecordGv = delegator.findOne("Patient", false, "patientId", patientId);
            
            Map<String, Object> patientRecord = FastMap.newInstance();
            patientRecord.put("civilId", patientRecordGv.getString("civilId"));
            patientRecord.put("patientId", patientRecordGv.getString("patientId"));
            patientRecord.put("patientType", patientRecordGv.getString("patientType"));
            patientRecord.put("salutation", patientRecordGv.getString("title"));
            patientRecord.put("firstName", patientRecordGv.getString("firstName"));
            patientRecord.put("lastName", patientRecordGv.getString("thirdName"));
            patientRecord.put("gender", patientRecordGv.getString("gender"));
            patientRecord.put("dateOfBirth", patientRecordGv.getDate("dateOfBirth"));
            patientRecord.put("bloodGroup", patientRecordGv.getString("bloodGroup"));
            patientRecord.put("rh", patientRecordGv.getString("rH"));
            patientRecord.put("address", patientRecordGv.getString("address1"));
            patientRecord.put("postalCode", patientRecordGv.getString("postalCode"));
            patientRecord.put("maritalStatus", patientRecordGv.getString("maritalStatus"));
            patientRecord.put("nationality", patientRecordGv.getString("nationality"));
            patientRecord.put("city", patientRecordGv.getString("city"));
            patientRecord.put("state", patientRecordGv.getString("governorate"));
            patientRecord.put("country", patientRecordGv.getString("country"));
            patientRecord.put("emailId", patientRecordGv.getString("emailAddress"));
            patientRecord.put("passport", patientRecordGv.getString("passport"));
            patientRecord.put("homePhone", patientRecordGv.getString("homePhone"));
            patientRecord.put("officePhone", patientRecordGv.getString("officePhone"));
            patientRecord.put("isdCode", patientRecordGv.getString("isdCode"));
            patientRecord.put("mobileNumber", patientRecordGv.getString("mobilePhone"));
            
            //Map returnVal = new HashMap();
            //returnVal.put("afyaId", "11111111");
            ObjectMapper gson = new ObjectMapper();
            //.setDateFormat("yyyy-MM-dd").create();
            String patientJsonString = gson.writeValueAsString(patientRecord);
            System.out.println(patientJsonString);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            List<MediaType> mediaTypes = new ArrayList<MediaType>();
            mediaTypes.add(MediaType.APPLICATION_JSON);
            headers.setAccept(mediaTypes);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<String>(patientJsonString, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://5.9.249.197:7878/afya-portal/anon/patient/retrieveAfyaId", HttpMethod.POST, requestEntity, String.class);
            String afyaId = responseEntity.getBody();
            patientRecordGv.set("afyaId", afyaId);
            delegator.store(patientRecordGv);
            return UtilMisc.toMap("afyaId", afyaId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServiceUtil.returnFailure();
    }


}
