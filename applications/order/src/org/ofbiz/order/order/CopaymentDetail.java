package org.ofbiz.order.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by pradyumna on 26-04-2015.
 */
public class CopaymentDetail {

    private String serviceId;
    private String moduleId;
    private BigDecimal copayAmount;
    private BigDecimal copayPercentage;
    @JsonProperty(value = "deductableAmount")
    private BigDecimal deductibleAmount;
    @JsonProperty(value = "deductablePercentage")
    private BigDecimal deductiblePercentage;
    private boolean authorization=true;
    private boolean authorizationInclusiveConsultation=false;
    private BigDecimal authorizationAmount;
    private BigDecimal authorizationRequiredConsultation;
    private String computeBy;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public BigDecimal getCopayAmount() {
        if(copayAmount==null) copayAmount=BigDecimal.ZERO;
        return copayAmount;
    }

    public void setCopayAmount(BigDecimal copayAmount) {
        this.copayAmount = copayAmount;
    }

    public BigDecimal getCopayPercentage() {
        if(copayPercentage==null) copayPercentage=BigDecimal.ZERO;
        return copayPercentage;
    }

    public void setCopayPercentage(BigDecimal copayPercentage) {
        this.copayPercentage = copayPercentage;
    }

    public BigDecimal getDeductibleAmount() {
        if(deductibleAmount==null) deductibleAmount=BigDecimal.ZERO;
        return deductibleAmount;
    }

    public void setDeductibleAmount(BigDecimal deductibleAmount) {
        this.deductibleAmount = deductibleAmount;
    }

    public BigDecimal getDeductiblePercentage() {
        if(deductiblePercentage==null) deductiblePercentage=BigDecimal.ZERO;
        return deductiblePercentage;
    }

    public void setDeductiblePercentage(BigDecimal deductiblePercentage) {
        this.deductiblePercentage = deductiblePercentage;
    }

    public BigDecimal getAuthorizationAmount() {
        return authorizationAmount;
    }

    public void setAuthorizationAmount(BigDecimal authorizationAmount) {
        this.authorizationAmount = authorizationAmount;
    }

    public BigDecimal getAuthorizationRequiredConsultation() {
        return authorizationRequiredConsultation;
    }

    public void setAuthorizationRequiredConsultation(BigDecimal authorizationRequiredConsultation) {
        this.authorizationRequiredConsultation = authorizationRequiredConsultation;
    }

    public String getComputeBy() {
        return computeBy;
    }

    public void setComputeBy(String computeBy) {
        this.computeBy = computeBy;
    }

    public boolean isAuthorization() {
        return authorization;
    }

    public void setAuthorization(boolean authorization) {
        this.authorization = authorization;
    }

    public boolean isAuthorizationInclusiveConsultation() {
        return authorizationInclusiveConsultation;
    }

    public void setAuthorizationInclusiveConsultation(boolean authorizationInclusiveConsultation) {
        this.authorizationInclusiveConsultation = authorizationInclusiveConsultation;
    }
}
