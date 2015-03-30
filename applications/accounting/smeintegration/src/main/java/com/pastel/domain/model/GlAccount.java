package com.pastel.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

/**
 * Author: Nthdimenzion
 */

public class GlAccount {

    private String glAccountId;
    private BigDecimal debitAmount = BigDecimal.ZERO;
    private BigDecimal creditAmount = BigDecimal.ZERO;
    private boolean isValid = false;

    public GlAccount(String glAccountId) {
        this.glAccountId = glAccountId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlAccount glAccount = (GlAccount) o;

        if (!glAccountId.equals(glAccount.glAccountId)) return false;

        return true;
    }

    public void addDebitValue(BigDecimal debitAmount){
        Preconditions.checkNotNull(debitAmount);
        this.debitAmount = this.debitAmount.add(debitAmount.abs());
    }

    public void addCreditValue(BigDecimal creditAmount){
        Preconditions.checkNotNull(creditAmount);
        this.creditAmount = this.creditAmount.add(creditAmount.abs());
    }

    public boolean validate(BigDecimal debitAmount,BigDecimal creditAmount){
        isValid = this.debitAmount.compareTo(debitAmount)==0 && this.creditAmount.compareTo(creditAmount)==0;
        return isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public int hashCode() {
        return glAccountId.hashCode();
    }

    @Override
    public String toString() {
        return  "glAccountId= " + glAccountId +  ", debitAmount=" + debitAmount + ", creditAmount=" + creditAmount;
    }
}
