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

/**
 * Created by pradyumna on 23-04-2015.
 */
public class PatientServices {


    private static final String PORTAL_URL= UtilProperties.getPropertyValue("general.properties","portal.server.url","5.9.249.197:7878");

    public static Map registerPatientOnPortal(DispatchContext ctx, Map context) {

        Delegator delegator = ctx.getDelegator();
        try {
            String patientId = (String) context.get("patientId");
            GenericValue patientRecord = delegator.findOne("Patient", false, "patientId", patientId);
            Map returnVal = new HashMap();
            returnVal.put("afyaId", "11111111");
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
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://"+PORTAL_URL+"/afya-portal/anon/patient/retrieveAfyaId", HttpMethod.POST, requestEntity, String.class);
            String afyaId = responseEntity.getBody();
            patientRecord.set("afyaId", afyaId);
            delegator.store(patientRecord);
            return UtilMisc.toMap("afyaId", afyaId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServiceUtil.returnFailure();
    }



}
