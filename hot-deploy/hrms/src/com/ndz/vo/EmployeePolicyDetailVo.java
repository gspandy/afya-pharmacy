package com.ndz.vo;

import com.ndz.zkoss.HrmsUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author: NthDimenzion
 * @since 1.0
 */
public class EmployeePolicyDetailVo {

    private String relationshipName;

    private String policyId;

    private String memberName;

    private int age;

    private String gender;

    private String primaryBenificiary;

    private String policyHolder;

    private String memberId;

    private String insuranceId;

    private String insuranceCompany;

    private String policyNumber;

    private String validFrom;

    private String validThru;

    private boolean validToHaveDependant;

    private boolean active;

    public static EmployeePolicyDetailVo createEmployeePolicyDetailVo(GenericValue employeePolicyGv, Delegator delegator, GenericValue activePrimaryPolicyGv) throws Exception {
        EmployeePolicyDetailVo employeePolicyDetailVo = new EmployeePolicyDetailVo();
        employeePolicyDetailVo = populatePersonalDetail(employeePolicyDetailVo, employeePolicyGv, delegator);
        employeePolicyDetailVo.policyId = employeePolicyGv.getString("policyId");
        employeePolicyDetailVo.memberId = employeePolicyGv.getString("memberId");
        employeePolicyDetailVo.insuranceId = employeePolicyGv.getString("insuranceId");
        employeePolicyDetailVo.insuranceCompany = employeePolicyGv.getString("insuranceCompany");
        employeePolicyDetailVo.policyHolder = employeePolicyGv.getString("policyHolder");
        employeePolicyDetailVo.policyNumber = employeePolicyGv.getString("policyNumber");
        employeePolicyDetailVo.validFrom = UtilDateTime.formatDate(employeePolicyGv.getDate("validFrom"));
        employeePolicyDetailVo.validThru = UtilDateTime.formatDate(employeePolicyGv.getDate("validThru"));
        employeePolicyDetailVo.primaryBenificiary = employeePolicyGv.getString("primaryBenificiary");
        employeePolicyDetailVo.validToHaveDependant = UtilValidate.isEmpty(employeePolicyGv.getString("dependantId"));
        Date nowDate = new Date(UtilDateTime.getDayEnd(new Timestamp(new java.util.Date().getTime())).getTime());
        employeePolicyDetailVo.active = (activePrimaryPolicyGv != null && (employeePolicyGv.getDate("validThru").after(nowDate)));
        return employeePolicyDetailVo;
    }

    private static EmployeePolicyDetailVo populatePersonalDetail(EmployeePolicyDetailVo employeePolicyDetailVo, GenericValue employeePolicyGv, Delegator delegator) throws Exception {
        if (UtilValidate.isEmpty(employeePolicyGv.getString("dependantId"))) {
            employeePolicyDetailVo.relationshipName = "Self/Employee";
            GenericValue personGv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", employeePolicyGv.getString("employeeId")));
            employeePolicyDetailVo.memberName = HrmsUtil.getFullName(delegator, employeePolicyGv.getString("employeeId"));
            employeePolicyDetailVo.age = HrmsUtil.getAge(personGv.getDate("birthDate"));
            employeePolicyDetailVo.gender = "M".equals(personGv.getString("gender")) ? "Male" : "Female";
        } else if (UtilValidate.isNotEmpty(employeePolicyGv.getString("dependantId"))) {
            GenericValue dependantGv = delegator.findByPrimaryKey("EmployeeDependant", UtilMisc.toMap("dependantId", employeePolicyGv.getString("dependantId")));
            employeePolicyDetailVo.memberName = dependantGv.getString("dependantName");
            employeePolicyDetailVo.age = HrmsUtil.getAge(dependantGv.getDate("dateOfBirth"));
            employeePolicyDetailVo.gender = dependantGv.getString("gender");
            String relationshipTypeId = dependantGv.getString("relationshipType");
            GenericValue relationshipTypeGv = delegator.findByPrimaryKey("PartyRelationshipType", UtilMisc.toMap("partyRelationshipTypeId", relationshipTypeId));
            employeePolicyDetailVo.relationshipName = relationshipTypeGv.getString("partyRelationshipName");
        }
        return employeePolicyDetailVo;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getMemberName() {
        return memberName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getPrimaryBenificiary() {
        return primaryBenificiary;
    }

    public String getPolicyHolder() {
        return policyHolder;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public String getValidThru() {
        return validThru;
    }

    public boolean isValidToHaveDependant() {
        return validToHaveDependant;
    }

    public boolean isActive() {
        return active;
    }
}
