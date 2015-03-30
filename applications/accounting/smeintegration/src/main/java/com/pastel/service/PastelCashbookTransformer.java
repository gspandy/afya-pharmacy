package com.pastel.service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.pastel.domain.model.*;
import com.rits.cloning.Cloner;

import java.util.Date;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

public class PastelCashbookTransformer implements Function<PaymentJournalEntry, PastelPaymentTransaction> {

    final List<String> cashAndBankAccounts;
    final Cloner cloner = new Cloner();

    public PastelCashbookTransformer(List<String> cashAndBankAccounts) {
        this.cashAndBankAccounts = cashAndBankAccounts;
    }

    @Override
    public PastelPaymentTransaction apply(PaymentJournalEntry paymentJournalEntry) {
        paymentJournalEntry = cloner.deepClone(paymentJournalEntry);
        final JournalEntry journalEntry = paymentJournalEntry.getJournalEntry();
        Date transactionDate = journalEntry.transactionDate;
        PastelPaymentTransaction pastelPaymentTransaction = new PastelPaymentTransaction(journalEntry);
        pastelPaymentTransaction.assignBankOrCashAccountAndPaymentType(cashAndBankAccounts);
        Preconditions.checkState(journalEntry.getDebitItems().size() == 1);
        Preconditions.checkState(journalEntry.getCreditItems().size() == 1);
        PastelGeneralJournalTransaction pastelGeneralJournalTransaction = null;
        if (pastelPaymentTransaction.getPaymentType() == PastelPaymentTransaction.PaymentType.RECEIPT) {
            final JournalEntryItem cashOrBankEntry = journalEntry.getDebitItems().iterator().next();
            final JournalEntryItem debitItem = journalEntry.getCreditItems().iterator().next();
            pastelGeneralJournalTransaction = new PastelGeneralJournalTransaction(transactionDate, debitItem.glAccountId, debitItem.homeAmount.negate(),cashOrBankEntry.glAccountId, cashOrBankEntry.homeAmount.negate());

        } else if (pastelPaymentTransaction.getPaymentType() == PastelPaymentTransaction.PaymentType.PAYMENT) {
            Preconditions.checkState(journalEntry.getCreditItems().size() == 1);
            final JournalEntryItem cashOrBankEntry = journalEntry.getCreditItems().iterator().next();
            final JournalEntryItem debitItem = journalEntry.getDebitItems().iterator().next();
            pastelGeneralJournalTransaction = new PastelGeneralJournalTransaction(transactionDate, debitItem.glAccountId, debitItem.homeAmount, cashOrBankEntry.glAccountId, cashOrBankEntry.homeAmount);
        }
        pastelGeneralJournalTransaction.postPaymentTransaction(pastelPaymentTransaction.getPaymentType());
        pastelPaymentTransaction.assignPastelGeneralJournal(pastelGeneralJournalTransaction);
        return pastelPaymentTransaction;
    }
}
