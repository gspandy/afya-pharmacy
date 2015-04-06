package org.ofbiz.order.shoppingcart;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by pradyumna on 05-04-2015.
 */
public class PatientInfo {

    private String clinicId;
    private String afyaId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String patientType;
    private String clinicName;
    private String doctorName;
    private Date visitDate;
    private String visitId;
    private String address;

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
        this.clinicId=request.getParameter("clinicId");
        this.firstName=request.getParameter("patientFirstName");
        this.lastName=request.getParameter("patientLastName");
        this.clinicName=request.getParameter("clinicName");
        this.mobile=request.getParameter("mobile");
        this.afyaId=request.getParameter("afyaId");
        this.doctorName=request.getParameter("doctorName");
        this.patientType=request.getParameter("patientType");
    }

    public PatientInfo() {

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
}
