package org.ofbiz.accounting.ledger.dovepayrollimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 11/6/14
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */

public class JournalEntry {
    public Timestamp transactionDate;
    public List<JournalEntryItem> items;

    private static BigDecimal totalDebit;
    private static BigDecimal totalCredit;
    private static BigDecimal difference;
    public JournalEntry(Timestamp transactionDate, List<JournalEntryItem> items) {
        if (!JournalEntry.isBalanced(items)) {
            throw new IllegalArgumentException(
                    "The total credit and debit amount(s) are not balanced. The total debit is "+totalDebit +" and the total credit is "+totalCredit +". The difference is "+difference);
        }
        this.items = items;
        this.transactionDate = transactionDate;
    }

    public static boolean isBalanced(List<JournalEntryItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;
        for (JournalEntryItem item : items) {
            if (item.type == TransactionType.DEBIT.transactionType) {
                total = total.subtract(BigDecimal.valueOf(item.amount));
                totalDebits = totalDebits.add(BigDecimal.valueOf(item.amount));
            } else {
                total = total.add(BigDecimal.valueOf(item.amount));
                totalCredits =  totalCredits.add(BigDecimal.valueOf(item.amount));
            }
        }
        totalCredit = totalCredits;
        totalDebit = totalDebits;
        difference = total;
        return total.compareTo(BigDecimal.ZERO)==0 ? true : false;
    }

}
