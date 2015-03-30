package com.pastel.domain.model;

/**
 * Author: Nthdimenzion
 */

public enum PastelTransactionType {
    GENERAL_JOURNAL ("general_journal.csv",null),SUPPLIER_DOCUMENT("supplier_document.csv", JournalEntry.TransactionType.CREDIT),
    CUSTOMER_DOCUMENT("customer_document.csv",JournalEntry.TransactionType.DEBIT),
    CUSTOMER_JOURNAL("customer_journal.csv",null) ,
    SUPPLIER_JOURNAL("supplier_journal.csv",null) ;

    public final String fileName;
    public final JournalEntry.TransactionType transactionType;

    PastelTransactionType(String fileName, JournalEntry.TransactionType transactionType) {
        this.fileName = fileName;
        this.transactionType = transactionType;
    }

}
