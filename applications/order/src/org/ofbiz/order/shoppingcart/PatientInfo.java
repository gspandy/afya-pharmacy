package org.ofbiz.order.shoppingcart;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pradyumna on 05-04-2015.
 */
public class PatientInfo {

    private String patientId;
    private String afyaId;
    private String civilId;
    private String firstName;
    private String secondName;
    private String thirdName;
    private String fourthName;
    private String gender;
    private Date dateOfBirth;
    private String address;
    private String mobile;
    private String patientType;
    private String visitId;
    private Date visitDate;
    private String clinicId;
    private String clinicName;
    private String doctorName;
    private String benefitId;
    private String hisBenefitId;
    private String healthPolicyId;
    private String moduleId;
    private String moduleName;
    private String isOrderApproved;
    private BigDecimal copay;
    private String copayType;
    private String primaryPayer;

    PatientInfo(HttpServletRequest request) throws ParseException {
        String dob = request.getParameter("dateOfBirth");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.patientId=request.getParameter("patientId");
        this.afyaId=request.getParameter("afyaId");
        this.civilId=request.getParameter("civilId");
        this.firstName=request.getParameter("firstName");
        this.secondName=request.getParameter("secondName");
        this.thirdName=request.getParameter("thirdName");
        this.fourthName=request.getParameter("fourthName");
        this.gender=request.getParameter("gender");
        this.dateOfBirth=new java.sql.Date(dateFormat.parse(dob).getTime());
        this.mobile=request.getParameter("mobile");
        this.patientType=request.getParameter("patientType");
        this.clinicId=request.getParameter("clinicId");
        this.clinicName=request.getParameter("clinicName");
        this.doctorName=request.getParameter("doctorName");
        this.benefitId=request.getParameter("benefitPlanId");
        this.hisBenefitId=request.getParameter("hisBenefitId");
        this.healthPolicyId=request.getParameter("healthPolicyId");
        this.moduleId=request.getParameter("moduleId");
        this.moduleName=request.getParameter("moduleName");
        this.isOrderApproved=request.getParameter("isOrderApproved");
        if (request.getParameter("copay") != null)
            this.copay=new BigDecimal(request.getParameter("copay"));
        this.copayType=request.getParameter("copayType");
        this.primaryPayer=request.getParameter("primaryPayer");
    }

    public PatientInfo() {

    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getFourthName() {
        return fourthName;
    }

    public void setFourthName(String fourthName) {
        this.fourthName = fourthName;
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

	public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
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

    public String getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(String benefitId) {
        this.benefitId = benefitId;
    }

    public String getHisBenefitId() {
        return hisBenefitId;
    }

    public void setHisBenefitId(String hisBenefitId) {
        this.hisBenefitId = hisBenefitId;
    }

    public String getHealthPolicyId() {
        return healthPolicyId;
    }

    public void setHealthPolicyId(String healthPolicyId) {
        this.healthPolicyId = healthPolicyId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

	public String getIsOrderApproved() {
		return isOrderApproved;
	}

	public void setIsOrderApproved(String isOrderApproved) {
		this.isOrderApproved = isOrderApproved;
	}

    public BigDecimal getCopay() {
		return copay;
	}

	public void setCopay(BigDecimal copay) {
		this.copay = copay;
	}

	public String getCopayType() {
		return copayType;
	}

	public void setCopayType(String copayType) {
		this.copayType = copayType;
	}

	public String getPrimaryPayer() {
		return primaryPayer;
	}

	public void setPrimaryPayer(String primaryPayer) {
		this.primaryPayer = primaryPayer;
	}

}
