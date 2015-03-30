package com.pastel.domain.model;

import java.util.Collection;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

public class PastelPaymentTransaction implements IPastelTransaction{

    public enum PaymentType{
        RECEIPT,PAYMENT
    }

    public String glAccountId;

    private final JournalEntry journalEntry;

    private PaymentType paymentType;

    private IPastelTransaction pastelTransaction;


    public PastelPaymentTransaction(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }

    public void assignPastelGeneralJournal(IPastelTransaction pastelGeneralJournal){
        this.pastelTransaction = pastelGeneralJournal;
    }


    public void assignBankOrCashAccountAndPaymentType(List<String> cashAndBankAccounts) {
        final Collection<JournalEntryItem> creditItems = journalEntry.getCreditItems();
        for (JournalEntryItem creditItem : creditItems) {
            if(cashAndBankAccounts.contains(creditItem.glAccountId)){
                this.glAccountId = creditItem.glAccountId;
                this.paymentType = PaymentType.PAYMENT;
            }
        }

        for (JournalEntryItem debitItem : journalEntry.getDebitItems()) {
            if(cashAndBankAccounts.contains(debitItem.glAccountId)){
                this.glAccountId = debitItem.glAccountId;
                this.paymentType = PaymentType.RECEIPT;
            }
        }
    }

    @Override
    public List<String[]> transformIntoCvsRecord() {
        return pastelTransaction.transformIntoCvsRecord();
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
}
