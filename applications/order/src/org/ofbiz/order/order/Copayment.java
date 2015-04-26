package org.ofbiz.order.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by pradyumna on 26-04-2015.
 */
public class Copayment {
    private BigDecimal totalCopayAmount;

    private BigDecimal totalDeductableAmount;

    private BigDecimal totalAuthorizationAmount;

    @JsonProperty(value = "serviceDetails")
    private List<CopaymentDetail> serviceDetails;

    private CopaymentDetail moduleDetails;


    public BigDecimal getTotalCopayAmount() {
        return totalCopayAmount;
    }

    public void setTotalCopayAmount(BigDecimal totalCopayAmount) {
        this.totalCopayAmount = totalCopayAmount;
    }

    public BigDecimal getTotalDeductableAmount() {
        return totalDeductableAmount;
    }

    public void setTotalDeductableAmount(BigDecimal totalDeductableAmount) {
        this.totalDeductableAmount = totalDeductableAmount;
    }

    public BigDecimal getTotalAuthorizationAmount() {
        return totalAuthorizationAmount;
    }

    public void setTotalAuthorizationAmount(BigDecimal totalAuthorizationAmount) {
        this.totalAuthorizationAmount = totalAuthorizationAmount;
    }

    public List<CopaymentDetail> getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(List<CopaymentDetail> serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public CopaymentDetail getModuleDetails() {
        return moduleDetails;
    }

    public void setModuleDetails(CopaymentDetail moduleDetails) {
        this.moduleDetails = moduleDetails;
    }
}
