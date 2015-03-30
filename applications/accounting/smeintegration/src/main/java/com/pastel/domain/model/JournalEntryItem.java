package com.pastel.domain.model;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

/**
 * Author: Nthdimenzion
 */

public class JournalEntryItem {

    public String partyId;
    public String glAccountId;
    public String glAccountType;
    public BigDecimal homeAmount;
    public BigDecimal amount;
    public JournalEntry.TransactionType transactionType;
    public String finTransId;

    public JournalEntryItem(String partyId, String glAccountId, String glAccountType, BigDecimal homeAmount, BigDecimal amount, JournalEntry.TransactionType transactionType) {
        this.partyId = partyId;
        this.glAccountId = glAccountId;
        this.glAccountType = glAccountType;
        this.homeAmount = homeAmount;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public boolean isZero(){
        return homeAmount.intValue() == 0;
    }

    public void subtract(BigDecimal homeAmount,BigDecimal amount){
        Preconditions.checkArgument(isEqualToOrLessThan(homeAmount));
        this.homeAmount = this.homeAmount.subtract(homeAmount);
        this.amount = this.amount.subtract(amount);
    }

    private boolean isEqualToOrLessThan(BigDecimal homeAmount) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JournalEntryItem that = (JournalEntryItem) o;

        if (!glAccountId.equals(that.glAccountId)) return false;
        if (!glAccountType.equals(that.glAccountType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = glAccountId.hashCode();
        result = 31 * result + glAccountType.hashCode();
        return result;
    }

    public void setFinTransId(String finTransId) {
        this.finTransId = finTransId;
    }

}
