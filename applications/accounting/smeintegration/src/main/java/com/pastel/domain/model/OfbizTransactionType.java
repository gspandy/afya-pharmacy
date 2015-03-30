package com.pastel.domain.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import static com.pastel.domain.model.PastelTransactionType.*;
import static com.pastel.domain.model.IPastelTransformers.*;

/**
 * Author: Nthdimenzion
 */

public enum OfbizTransactionType {

    DEFAULT(GENERAL_JOURNAL, PastelJournalEntryTransformer),
    SALES_SHIPMENT(GENERAL_JOURNAL, PastelJournalEntryTransformer),
    SHIPMENT_RECEIPT(GENERAL_JOURNAL, PastelJournalEntryTransformer),
    PURCHASE_INVOICE(SUPPLIER_DOCUMENT, PastelInvoiceEntryTransformer),
    SALES_INVOICE(CUSTOMER_DOCUMENT, PastelInvoiceEntryTransformer),
    PAYMENT_APPL_SUPP(SUPPLIER_DOCUMENT, PastelInvoiceEntryTransformer),
    PAYMENT_APPL_CUST(CUSTOMER_DOCUMENT, PastelInvoiceEntryTransformer);


    public static List<String> PaymentTransactions = Lists.newArrayList("INCOMING_PAYMENT","OUTGOING_PAYMENT");
    public static List<String> PayrollTransactions = Lists.newArrayList("SALARY");

    public static List<String> Transactions = Lists.newArrayList(SALES_SHIPMENT.name(),SHIPMENT_RECEIPT.name(),PURCHASE_INVOICE.name()
            ,SALES_INVOICE.name(),"AMORTIZATION","CAPITALIZATION","CLAIM","EXTERNAL_ACCTG_TRANS"
            ,"INTERNAL_ACCTG_TRANS","INVENTORY","ITEM_VARIANCE","MANUFACTURING","PERIOD_CLOSING","REQ_ISSUE"
            ,"SALES_SHIPMENT","SHIPMENT_RECEIPT");


    // public static List<String> Transactions = Lists.newArrayList(PURCHASE_INVOICE.name());


    // public static List<String> Transactions = Lists.newArrayList(SALES_INVOICE.name());

    public PastelTransactionType pastelTransactionType;
    public final Function<Collection<JournalEntry>, Collection<? extends IPastelTransaction>> pastelJournalEntryTransformer;

    OfbizTransactionType(PastelTransactionType generalJournal, Function<Collection<JournalEntry>, Collection<? extends IPastelTransaction>> pastelJournalEntryTransformer) {
        pastelTransactionType = generalJournal;
        this.pastelJournalEntryTransformer = pastelJournalEntryTransformer;
    }

    public static OfbizTransactionType getEnum(String name) {
        for (OfbizTransactionType e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return DEFAULT;
    }


}
