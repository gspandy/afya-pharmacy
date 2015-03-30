package com.pastel.domain.model;

import com.google.common.base.Optional;

import java.util.Collection;

/**
 * Author: Nthdimenzion
 */

public class PaymentJournalEntry {

    private JournalEntry journalEntry;
    private boolean isFinancialTransaction = false;
    private boolean isFinancialTransactionFromBankAccount = false;
    private boolean isCashAccount = false;

    public PaymentJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
        initialise();
    }

    private void initialise() {
        final Optional<String> finTransAccountId = journalEntry.getFinTransAccountId();
        isFinancialTransaction = finTransAccountId.isPresent();
    }

    public Collection<JournalEntryItem> getDebitItems() {
        return journalEntry.getDebitItems();
    }

    public Collection<JournalEntryItem> getCreditItems() {
        return journalEntry.getCreditItems();
    }

    public Optional<String> getFinTransAccountId() {
        return journalEntry.getFinTransAccountId();
    }

    public boolean isFinancialTransaction() {
        return isFinancialTransaction;
    }

    public boolean isFinancialTransactionFromBankAccount() {
        return isFinancialTransactionFromBankAccount;
    }

    public void setFinancialTransactionFromBankAccount(boolean isBankAccount) {
        this.isFinancialTransactionFromBankAccount = isBankAccount;
    }

    public JournalEntry getJournalEntry() {
        return journalEntry;
    }

    public boolean isCashAccount() {
        return isCashAccount;
    }

    public void setCashAccount(boolean isCashAccount) {
        this.isCashAccount = isCashAccount;
    }
}
