package org.ofbiz.order.shoppingcart;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by pradyumna on 05-04-2015.
 */
public class PatientInfo {

    private String patientId;
    private String clinicId;
    private String afyaId;
    private String firstName;
    private String secondName;
    private String thirdName;
    private String fourthName;
    private String mobile;
    private String patientType;
    private String clinicName;
    private String doctorName;
    private Date visitDate;
    private String visitId;
    private String address;
    private String benefitId;
    private String healthPolicyId;
    private String moduleId;
    private String moduleName;

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    PatientInfo(HttpServletRequest request){
        this.patientId=request.getParameter("patientId");
        this.clinicId=request.getParameter("clinicId");
        this.firstName=request.getParameter("firstName");
        this.secondName=request.getParameter("secondName");
        this.thirdName=request.getParameter("thirdName");
        this.fourthName=request.getParameter("fourthName");
        this.clinicName=request.getParameter("clinicName");
        this.mobile=request.getParameter("mobile");
        this.afyaId=request.getParameter("afyaId");
        this.doctorName=request.getParameter("doctorName");
        this.patientType=request.getParameter("patientType");
        this.healthPolicyId=request.getParameter("healthPolicyId");
        this.benefitId=request.getParameter("benefitPlanId");
        this.moduleId=request.getParameter("moduleId");
        this.moduleName=request.getParameter("moduleName");
    }

    public PatientInfo() {

    }

    public String getModuleId() {
        return moduleId;
    }

    public String getPatientId() {
        return patientId;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(String benefitId) {
        this.benefitId = benefitId;
    }

    public String getHealthPolicyId() {
        return healthPolicyId;
    }

    public void setHealthPolicyId(String healthPolicyId) {
        this.healthPolicyId = healthPolicyId;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public String getFourthName() {
        return fourthName;
    }

    public String getModuleName() {
        return moduleName;
    }
}
