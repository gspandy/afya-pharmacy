package com.pastel.domain.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.rits.cloning.Cloner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.pastel.domain.model.IPastelTransaction.CUSTOMER_CONTROL_ACCOUNT;
import static com.pastel.domain.model.IPastelTransaction.SUPPLIER_CONTROL_ACCOUNT;
import static com.pastel.domain.model.JournalEntry.TransactionGroup.PRODUCT;
import static com.pastel.domain.model.JournalEntry.TransactionGroup.TAX;

/**
 * Author: Nthdimenzion
 */

/**
 * @TODO sum of debits and credits on a invoice number should not be 0
 * @TODO sum of debits and credits on a payment number should not be 0
 */
public interface IPastelTransformers {

    Function<Collection<JournalEntry>, Collection<? extends IPastelTransaction>> PastelJournalEntryTransformer = new PastelJournalEntryTransformer();
    Function<Collection<JournalEntry>, Collection<? extends IPastelTransaction>> PastelInvoiceEntryTransformer = new PastelInvoiceEntryTransformer();

    Cloner cloner = new Cloner();
    // Add all the transformers here



    class PastelJournalEntryTransformer implements Function<Collection<JournalEntry>, Collection<? extends IPastelTransaction>> {

        @Override
        public Collection<? extends IPastelTransaction> apply(Collection<JournalEntry> journalEntries) {
            Collection<? extends IPastelTransaction> pastelTransactions = Lists.newArrayList();
            for (JournalEntry journalEntry : journalEntries) {
                final Collection transformedTransactions = normaliseJournalEntry(journalEntry);
                pastelTransactions.addAll(transformedTransactions);
            }
            return pastelTransactions;
        }

        /**
         * @param journalEntry
         * @return
         */
        static Collection<? extends IPastelTransaction> normaliseJournalEntry(JournalEntry journalEntry) {
            journalEntry = cloner.deepClone(journalEntry);
            final Collection<PastelGeneralJournalTransaction> pastelJournalEntries = Lists.newArrayList();
            Date transactionDate = journalEntry.transactionDate;
            final List<JournalEntryItem> debitItems = Lists.newArrayList(journalEntry.getDebitItems());
            final List<JournalEntryItem> creditItems = Lists.newArrayList(journalEntry.getCreditItems());
            while (debitItems.size() > 0) {
                JournalEntryItem debitItem = debitItems.get(0);
                JournalEntryItem creditItem = creditItems.get(0);
                BigDecimal homeAmountToSubtract = minOf(debitItem.homeAmount, creditItem.homeAmount);
                BigDecimal amountToSubtract = minOf(debitItem.amount, creditItem.amount);
                debitItem.subtract(homeAmountToSubtract, amountToSubtract);
                creditItem.subtract(homeAmountToSubtract, amountToSubtract);
                if (debitItem.isZero()) {
                    debitItems.remove(debitItem);
                }
                if (creditItem.isZero()) {
                    creditItems.remove(creditItem);
                }

                PastelGeneralJournalTransaction pastelGeneralJournalTransaction = new PastelGeneralJournalTransaction(transactionDate, debitItem.glAccountId, homeAmountToSubtract, creditItem.glAccountId, homeAmountToSubtract);
                pastelGeneralJournalTransaction.post();
                pastelJournalEntries.add(pastelGeneralJournalTransaction);
            }
            return pastelJournalEntries;
        }


        public static BigDecimal minOf(BigDecimal leftSide, BigDecimal rightSide) {
            return BigDecimal.valueOf(Math.min(leftSide.doubleValue(), rightSide.doubleValue())).setScale(2);
        }

    }

    class PastelInvoiceEntryTransformer implements Function<Collection<JournalEntry>, Collection<? extends IPastelTransaction>> {
        public static final String ACCOUNTS_RECEIVABLE = "ACCOUNTS_RECEIVABLE";
        public static final String ACCOUNTS_PAYABLE = "ACCOUNTS_PAYABLE";
        private Logger logger = LoggerFactory.getLogger(PastelInvoiceEntryTransformer.class);


        @Override
        public Collection<? extends IPastelTransaction> apply(Collection<JournalEntry> journalEntries) {
            Collection<IPastelTransaction> pastelTransactions = Lists.newArrayList();
            for (JournalEntry journalEntry : journalEntries) {
                try {
                    validateNumberOfTaxTransactions(journalEntry);
                    final IPastelTransaction transformedTransaction = normalizeJournalEntry(journalEntry);
                    pastelTransactions.add(transformedTransaction);
                } catch (InvalidNumberOfTaxTransactions invalidNumberOfTaxTransactions) {
                    logger.error(invalidNumberOfTaxTransactions.getMessage());
                }
            }
            return pastelTransactions;
        }

        private void validateNumberOfTaxTransactions(JournalEntry journalEntry) throws InvalidNumberOfTaxTransactions {
            List<JournalEntryItem> productTransactions = journalEntry.getJournalEntryItemsForGroup(PRODUCT);
            List<JournalEntryItem> taxTransactions = journalEntry.getJournalEntryItemsForGroup(TAX);
            if (taxTransactions.size() > 0 && (productTransactions.size() != taxTransactions.size()) ) {
                throw new InvalidNumberOfTaxTransactions(journalEntry.transactionId);
            }
        }

        PastelInvoiceTransaction normalizeJournalEntry(JournalEntry journalEntry) {
            journalEntry = cloner.deepClone(journalEntry);
            BigDecimal taxTransactionAmount = BigDecimal.ZERO;
            final String partyId = getPartyId(journalEntry.journalEntryItems);
            List<JournalEntryItem> productTransactions = journalEntry.getJournalEntryItemsForGroup(PRODUCT);
            List<JournalEntryItem> taxTransactions = journalEntry.getJournalEntryItemsForGroup(TAX);
            JournalEntryItem contraAccount = findContraAccount(journalEntry);

            /**
             * @ TODO write code to get transaction from either ACCOUNTS_RECEIVABLE or ACCOUNTS_PAYABLE, use that to pass into the constructor below
             */
            PastelInvoiceTransaction pastelInvoiceTransaction = new PastelInvoiceTransaction(partyId, journalEntry.transactionDate, journalEntry.invoiceId,
                    contraAccount.glAccountId,journalEntry.transactionType.pastelTransactionType, contraAccount.glAccountType);

            for (int index = 0; index < productTransactions.size(); index++) {
                JournalEntryItem productTransaction = productTransactions.get(index);
                if (!CollectionUtils.isEmpty(taxTransactions)) {
                    JournalEntryItem taxTransaction = taxTransactions.get(index);
                    taxTransactionAmount = taxTransaction.homeAmount;
                }
                PastelInvoiceTransactionDetail pastelInvoiceTransactionDetail = new PastelInvoiceTransactionDetail(productTransaction.glAccountId, productTransaction.homeAmount,
                        productTransaction.homeAmount.add(taxTransactionAmount));
                pastelInvoiceTransaction.addTransactionDetail(pastelInvoiceTransactionDetail);
            }
            pastelInvoiceTransaction.updateGlSummaries();
            return pastelInvoiceTransaction;
        }

        /**
         * @TODO SOME code change here to accomadate
         * @param journalEntry
         * @return
         */
        private JournalEntryItem findContraAccount(JournalEntry journalEntry) {
            if(journalEntry.transactionType==OfbizTransactionType.PAYMENT_APPL_CUST || journalEntry.transactionType == OfbizTransactionType.PAYMENT_APPL_SUPP){
                for (JournalEntryItem journalEntryItem : journalEntry.journalEntryItems) {
                    if(journalEntryItem.glAccountId.equals(CUSTOMER_CONTROL_ACCOUNT) || journalEntryItem.glAccountId.equals(SUPPLIER_CONTROL_ACCOUNT)){
                        return journalEntryItem;
                    }
                }

            }
            return journalEntry.getJournalEntryItemsForGroup(getTransactionGroup(journalEntry.transactionType)).get(0);
        }

        static String getPartyId(Collection<JournalEntryItem> journalEntryItems) {
            String partyId = "";
            for (JournalEntryItem journalEntryItem : journalEntryItems) {
                if (ACCOUNTS_PAYABLE.equals(journalEntryItem.glAccountType) || ACCOUNTS_RECEIVABLE.equals(journalEntryItem.glAccountType)) {
                    partyId = journalEntryItem.partyId;
                }
            }
            return partyId;
        }

        public JournalEntry.TransactionGroup getTransactionGroup(OfbizTransactionType ofbizTransactionType){
            if(OfbizTransactionType.PURCHASE_INVOICE == ofbizTransactionType)
                return JournalEntry.TransactionGroup.ACCOUNTS_PAYABLE;
            else
                return JournalEntry.TransactionGroup.ACCOUNTS_RECEIVABLE;

        }



    }

    class InvalidNumberOfTaxTransactions extends Exception {
        public InvalidNumberOfTaxTransactions(String journalTransactionId) {
            super("Invalid Number Of Tax Transactions " + journalTransactionId);
        }
    }

}
