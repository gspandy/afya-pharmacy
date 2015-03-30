package com.pastel.domain.model;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

public class JournalEntry {

    public String transactionId;
    public OfbizTransactionType transactionType;
    public Date transactionDate;
    public String invoiceId;
    public Collection<JournalEntryItem> journalEntryItems = Lists.newArrayList();
    private Multimap<TransactionType, JournalEntryItem> journalEntriesGroupedByType = LinkedListMultimap.create();
    private Multimap<TransactionGroup, JournalEntryItem> journalEntriesGroupedByGlAccountType = LinkedListMultimap.create();

    public enum TransactionGroup {
        PRODUCT("PRODUCT"),TAX("TAX_ACCOUNT"),ACCOUNTS_RECEIVABLE("ACCOUNTS_RECEIVABLE"),ACCOUNTS_PAYABLE("ACCOUNTS_PAYABLE");
        private final String value;

        TransactionGroup(String value) {
            this.value = value;
        }

        public static TransactionGroup getEnum(String name) {
            for (TransactionGroup e : values()) {
                if (e.value.equals(name)) {
                    return e;
                }
            }
            return PRODUCT;
        }

        }

    public enum TransactionType {

        DEBIT("D"),

        CREDIT("C");

        TransactionType(String transactionValue) {
            this.transactionValue = transactionValue;
        }

        public String transactionValue;

        public static TransactionType getEnum(String name) {
            for (TransactionType e : values()) {
                if (e.transactionValue.equals(name)) {
                    return e;
                }
            }
            return null;
        }

    }



    Ordering<JournalEntryItem> orderByAmount = new Ordering<JournalEntryItem>() {
        public int compare(JournalEntryItem left, JournalEntryItem right) {
            return left.homeAmount.compareTo(right.homeAmount);
        }
    };
    public JournalEntry(String transactionId, OfbizTransactionType transactionType, Date transactionDate, String invoiceId) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.invoiceId = invoiceId;
    }

    public void addJournalEntryItem(JournalEntryItem journalEntryItem) {
        this.journalEntryItems.add(journalEntryItem);
        journalEntriesGroupedByType.put(journalEntryItem.transactionType, journalEntryItem);
        journalEntriesGroupedByGlAccountType.put(TransactionGroup.getEnum(journalEntryItem.glAccountType),journalEntryItem);
    }

    public Collection<JournalEntryItem> getDebitItems() {
        return journalEntriesGroupedByType.get(TransactionType.DEBIT);
    }

    public Collection<JournalEntryItem> getCreditItems() {
        return journalEntriesGroupedByType.get(TransactionType.CREDIT);
    }

    public List<JournalEntryItem> getJournalEntryItemsForGroup(TransactionGroup transactionGroup){
            final List<JournalEntryItem> journalEntryItems = Lists.newArrayList(journalEntriesGroupedByGlAccountType.get(transactionGroup));
        Collections.sort(journalEntryItems, orderByAmount);
        return journalEntryItems;
    }

    public Optional<String> getFinTransAccountId(){
        return GetFinTransAccountId(this);
    }


    static Optional<String> GetFinTransAccountId(JournalEntry journalEntry){
        Optional<String> finTransAccountId = Optional.absent();
        for (JournalEntryItem journalEntryItem : journalEntry.journalEntryItems) {
            if(!Strings.isNullOrEmpty(journalEntryItem.finTransId)){
                finTransAccountId = Optional.of(journalEntryItem.finTransId);
                break;
            }
        }
        return finTransAccountId;
    }


}
