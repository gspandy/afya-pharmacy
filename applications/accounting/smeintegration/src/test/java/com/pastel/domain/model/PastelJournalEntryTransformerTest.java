package com.pastel.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.pastel.domain.model.IPastelTransformers.PastelJournalEntryTransformer;
import static com.pastel.domain.model.IPastelTransformers.PastelInvoiceEntryTransformer;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

/**
 * Author: Nthdimenzion
 */

public class PastelJournalEntryTransformerTest {

    String partyId = "AW002";
    String partyId1 = "AW003";
    String inventoryGlAccountId = "7700000";
    String taxGlAccountId = "9500000";
    String COGSGlAccountId = "2000000";
    String accountsPayableGlAccountId = "9000000";
    final String transactionId = "002";

    JournalEntry singleDebitAndCreditEntry;
    JournalEntry twoDebitsAndOneCreditEntry;

    private static final Date EleventhDecember2014 = new Date(114,11,14);

    @Before
    public void setup(){

        JournalEntryItem inventoryAccountDebit = new JournalEntryItem(partyId,inventoryGlAccountId,"INVENTORY_ACCOUNT", new BigDecimal(10.10).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(10.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);
        JournalEntryItem taxAccountDebit = new JournalEntryItem(partyId,taxGlAccountId,"TAX_ACCOUNT", BigDecimal.ONE,BigDecimal.ONE, JournalEntry.TransactionType.DEBIT);

        JournalEntryItem accountsPayableCredit = new JournalEntryItem(partyId,accountsPayableGlAccountId,"ACCOUNTS_PAYABLE", new BigDecimal(11.10).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(11.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);
        JournalEntryItem cogsAccountCredit = new JournalEntryItem(partyId,COGSGlAccountId,"COGS_ACCOUNT", new BigDecimal(10.10).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(10.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);

        singleDebitAndCreditEntry = new JournalEntry(transactionId,OfbizTransactionType.SALES_SHIPMENT,EleventhDecember2014,"invoiceId");
        singleDebitAndCreditEntry.addJournalEntryItem(inventoryAccountDebit);
        singleDebitAndCreditEntry.addJournalEntryItem(cogsAccountCredit);


        twoDebitsAndOneCreditEntry = new JournalEntry(transactionId,OfbizTransactionType.SHIPMENT_RECEIPT,EleventhDecember2014,"invoiceId");
        twoDebitsAndOneCreditEntry.addJournalEntryItem(inventoryAccountDebit);
        twoDebitsAndOneCreditEntry.addJournalEntryItem(taxAccountDebit);
        twoDebitsAndOneCreditEntry.addJournalEntryItem(accountsPayableCredit);

    }


    @Test
    public void givenJournalEntryWithOneDebitAndOneCredit_whenJournalEntriesAreNormalized_itShouldCreateOnePastelJournalEntry(){
        final List<? extends IPastelTransaction> pastelTransactions = Lists.newArrayList(PastelJournalEntryTransformer.apply(Lists.newArrayList(singleDebitAndCreditEntry)));
        String[] result = pastelTransactions.get(0).transformIntoCvsRecord().get(0);
        assertThat(pastelTransactions.size(),is(1));
        String[] entryOne =  {"11","14/12/2014","G",inventoryGlAccountId,"0","","10.10","0","0","","",COGSGlAccountId,"1","1","0","0","0","10.10"};
        assertThat(result, equalTo(entryOne));


    }


    @Test
    public void givenJournalEntryWithTwoDebitAndOneCredit_whenJournalEntriesAreNormalized_itShouldCreateThreePastelJournalEntries(){
        final List<? extends IPastelTransaction> pastelTransactions = Lists.newArrayList(PastelJournalEntryTransformer.apply(Lists.newArrayList(twoDebitsAndOneCreditEntry)));
        List<String[]> results = Lists.newArrayList();
        for (IPastelTransaction pastelTransaction : pastelTransactions) {
            results.addAll(pastelTransaction.transformIntoCvsRecord());
        }
        String[] entryOne =  {"11","14/12/2014","G",inventoryGlAccountId,"0","","10.10","0","0","","",accountsPayableGlAccountId,"1","1","0","0","0","10.10"};
        String[] entryTwo =  {"11","14/12/2014","G",taxGlAccountId,"0","","1.00","0","0","","",accountsPayableGlAccountId,"1","1","0","0","0","1.00"};
        assertThat(results, Matchers.containsInAnyOrder(entryOne,entryTwo));
    }

    @Test
    public void givenJournalEntryWithThreeDebitsAndFiveCredits_whenJournalEntriesAreNormalized_itShouldCreateNormalisedPastelJournalEntries(){

        JournalEntry threeDebitsAndFiveCreditsEntry = new JournalEntry(transactionId,OfbizTransactionType.SHIPMENT_RECEIPT,EleventhDecember2014,"invoiceId");

        JournalEntryItem inventoryAccountDebit = new JournalEntryItem(partyId,inventoryGlAccountId,"INVENTORY_ACCOUNT", new BigDecimal(1000.10).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(1000.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);
        JournalEntryItem taxAccountDebit = new JournalEntryItem(partyId,taxGlAccountId,"TAX_ACCOUNT", new BigDecimal(1200.60).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(1200.60).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);
        JournalEntryItem dummyAccountDebit1 = new JournalEntryItem(partyId,"DUMMY_ACCOUNT1","DUMMY_ACCOUNT1", new BigDecimal(1750.90).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(1750.90).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);

        JournalEntryItem dummyAccount3 = new JournalEntryItem(partyId,"DUMMY_ACCOUNT3","DUMMY_ACCOUNT3", new BigDecimal(800.50).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(800.50).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);
        JournalEntryItem dummyAccount4 = new JournalEntryItem(partyId,"DUMMY_ACCOUNT4","DUMMY_ACCOUNT4", new BigDecimal(900.25).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(900.25).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);
        JournalEntryItem dummyAccount6 = new JournalEntryItem(partyId,"DUMMY_ACCOUNT6","DUMMY_ACCOUNT6", new BigDecimal(600.10).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(600.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);
        JournalEntryItem dummyAccount7 = new JournalEntryItem(partyId,"DUMMY_ACCOUNT7","DUMMY_ACCOUNT7", new BigDecimal(325.10).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(325.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);
        JournalEntryItem dummyAccount8 = new JournalEntryItem(partyId,"DUMMY_ACCOUNT8","DUMMY_ACCOUNT8", new BigDecimal(1325.65).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(1325.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);

        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(inventoryAccountDebit);
        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(taxAccountDebit);
        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(dummyAccountDebit1);

        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(dummyAccount3);
        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(dummyAccount4);
        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(dummyAccount6);
        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(dummyAccount7);
        threeDebitsAndFiveCreditsEntry.addJournalEntryItem(dummyAccount8);


        final List<PastelGeneralJournalTransaction> pastelTransactions = Lists.newArrayList((List<PastelGeneralJournalTransaction>)PastelJournalEntryTransformer.apply(Lists.newArrayList(threeDebitsAndFiveCreditsEntry)));
        verifyPastelJournalEntries(pastelTransactions,threeDebitsAndFiveCreditsEntry);
    }


    @Test
    public void givenAPurchaseInvoiceForThreeProducts_WhenPurchaseInvoiceIsTransformedIntoPastelSupplierInvoiceDocuments_itShouldCreateThreeInvoiceEntriesWithTaxAmount(){
        JournalEntry purchaseInvoiceEntry = new JournalEntry(transactionId,OfbizTransactionType.PURCHASE_INVOICE,EleventhDecember2014,"invoiceId");

        JournalEntryItem product1 = new JournalEntryItem("dummyPartyId","3700000","UNINVOICED_SHIP_RCPT", new BigDecimal(1200.60).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(1200.60).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);
        JournalEntryItem product2 = new JournalEntryItem("dummyPartyId","3700000","UNINVOICED_SHIP_RCPT", new BigDecimal(1750.90).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(1750.90).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);
        JournalEntryItem product3 = new JournalEntryItem("dummyPartyId","3700000","UNINVOICED_SHIP_RCPT", new BigDecimal(1000.10).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(1000.10).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);

        JournalEntryItem tax1 = new JournalEntryItem("dummyTaxPartyId","9500000","TAX_ACCOUNT", new BigDecimal(280.14).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(280.14).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);
        JournalEntryItem tax2 = new JournalEntryItem("dummyTaxPartyId","9500000","TAX_ACCOUNT", new BigDecimal(160.01).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(160.01).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);
        JournalEntryItem tax3 = new JournalEntryItem("dummyTaxPartyId","9500000","TAX_ACCOUNT", new BigDecimal(192.09).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(192.09).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.DEBIT);

        JournalEntryItem accountsPayable = new JournalEntryItem("dummyPartyId","9000000","ACCOUNTS_PAYABLE", new BigDecimal(4583.84).setScale(2, BigDecimal.ROUND_HALF_EVEN),new BigDecimal(4583.84).setScale(2, BigDecimal.ROUND_HALF_EVEN), JournalEntry.TransactionType.CREDIT);

        purchaseInvoiceEntry.addJournalEntryItem(product1);
        purchaseInvoiceEntry.addJournalEntryItem(product2);
        purchaseInvoiceEntry.addJournalEntryItem(product3);
        purchaseInvoiceEntry.addJournalEntryItem(tax1);
        purchaseInvoiceEntry.addJournalEntryItem(tax2);
        purchaseInvoiceEntry.addJournalEntryItem(tax3);
        purchaseInvoiceEntry.addJournalEntryItem(accountsPayable);

        final List<PastelInvoiceTransaction> pastelTransactions = Lists.newArrayList((List<PastelInvoiceTransaction>) PastelInvoiceEntryTransformer.apply(Lists.newArrayList(purchaseInvoiceEntry)));
        verifyPastelPurchaseInvoice(pastelTransactions.get(0));
    }

    private void verifyPastelPurchaseInvoice(PastelInvoiceTransaction pastelInvoiceTransaction) {
        final List<String[]> actualResult = pastelInvoiceTransaction.transformIntoCvsRecord();
        assertThat(actualResult.size(),is(4));
        final String[] header = actualResult.get(0);
        final String[] detail1 = actualResult.get(1);
        final String[] detail2 = actualResult.get(2);
        final String[] detail3 = actualResult.get(3);

        assertArrayEquals(new String[] {"Header","","","","dummyPartyId","11","14/12/2014","invoiceId","Y","0","","","","","","","","","","0","14/12/2014","","","","1.00","","","N"},header);
        assertArrayEquals(new String[] {"Detail","0","1","1000.10","1160.11","","1","0","0","3700000","3700000","6","",""},detail1);
        assertArrayEquals(new String[] {"Detail","0","1","1200.60","1392.69","","1","0","0","3700000","3700000","6","",""},detail2);
        assertArrayEquals(new String[] {"Detail","0","1","1750.90","2031.04","","1","0","0","3700000","3700000","6","",""},detail3);
    }


    public void verifyPastelJournalEntries(Collection<PastelGeneralJournalTransaction> pastelGeneralJournalEntries,JournalEntry journalEntry){
        Map<String,BigDecimal> pastelEntriesGroupedByGlAccount = glAccountGroupedTotals(pastelGeneralJournalEntries);
        Map<String,BigDecimal> journalEntriesGroupedByGlAccount = glAccountGroupedTotals(journalEntry);
        assertThat(pastelEntriesGroupedByGlAccount,is(equalTo(journalEntriesGroupedByGlAccount)));
    }

    private Map<String,BigDecimal> glAccountGroupedTotals(Collection<PastelGeneralJournalTransaction> pastelGeneralJournalEntries) {
        Map<String,BigDecimal> pastelEntriesGroupedByGlAccount = Maps.newHashMap();
        for (PastelGeneralJournalTransaction pastelGeneralJournalTransaction : pastelGeneralJournalEntries) {
            if(pastelEntriesGroupedByGlAccount.containsKey(pastelGeneralJournalTransaction.glAccountNumber)){
                BigDecimal total = pastelEntriesGroupedByGlAccount.get(pastelGeneralJournalTransaction.glAccountNumber).add(pastelGeneralJournalTransaction.homeAmount);
                pastelEntriesGroupedByGlAccount.put(pastelGeneralJournalTransaction.glAccountNumber,total);
            }else{
                pastelEntriesGroupedByGlAccount.put(pastelGeneralJournalTransaction.glAccountNumber, pastelGeneralJournalTransaction.homeAmount);
            }

            if(pastelEntriesGroupedByGlAccount.containsKey(pastelGeneralJournalTransaction.contraGlAccountNumber)){
                BigDecimal total = pastelEntriesGroupedByGlAccount.get(pastelGeneralJournalTransaction.contraGlAccountNumber).add(pastelGeneralJournalTransaction.homeAmount);
                pastelEntriesGroupedByGlAccount.put(pastelGeneralJournalTransaction.contraGlAccountNumber,total);
            }else{
                pastelEntriesGroupedByGlAccount.put(pastelGeneralJournalTransaction.contraGlAccountNumber, pastelGeneralJournalTransaction.homeAmount);
            }
        }
        return pastelEntriesGroupedByGlAccount;
    }


    private Map<String,BigDecimal> glAccountGroupedTotals(JournalEntry journalEntry) {
        Map<String,BigDecimal> journalEntriesGroupedByGlAccount = Maps.newHashMap();
        for (JournalEntryItem journalEntryItem : journalEntry.journalEntryItems) {
            if(journalEntriesGroupedByGlAccount.containsKey(journalEntryItem.glAccountId)){
                BigDecimal total = journalEntriesGroupedByGlAccount.get(journalEntryItem.glAccountId).add(journalEntryItem.homeAmount);
                journalEntriesGroupedByGlAccount.put(journalEntryItem.glAccountId, total);
            }else{
                journalEntriesGroupedByGlAccount.put(journalEntryItem.glAccountId, journalEntryItem.homeAmount);
            }
        }
        return journalEntriesGroupedByGlAccount;
    }


}
