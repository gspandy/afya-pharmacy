package com.pastel.service;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.pastel.domain.model.*;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Author: Nthdimenzion
 */

@Component
public class PastelExportService {

    public static final String CASHBOOK = "CASHBOOK";
    public static final String GENERAL_JOURNAL_ENTRIES = "GENERALJOURNALENTRIES";


    private OfbizTransactionFinder ofbizTransactionFinder;

    @Autowired
    public PastelExportService(OfbizTransactionFinder ofbizTransactionFinder) {
        this.ofbizTransactionFinder = ofbizTransactionFinder;
    }

    private PastelExportService() {
    }

    public String export(Date startDate, Date endDate) throws IOException {
        System.out.println("export invoked...");
        initialiseGlAccountSummaries();
        File outputFolder = new File("pasteloutput");
        outputFolder.mkdir();
        removeIfFileAlreadyExists(outputFolder);
        exportTransactions(startDate, endDate, outputFolder);
        exportPaymentsTransactions(startDate, endDate, outputFolder);
        exportPaymentAppliedTransactions(startDate, endDate, outputFolder);
        exportPayroll(startDate, endDate, outputFolder);
        exportOpeningBalances(endDate, outputFolder);
        exportTheCustomerJournals(startDate,outputFolder);
        exportTheSupplierJournals(startDate,outputFolder);
        readFilesRemoveNewLineCharacterFromTheLastLineOfEveryFile(outputFolder.getAbsolutePath());
        return outputFolder.getAbsolutePath();
    }

    private void removeIfFileAlreadyExists(File file) {
        for(File subFile : file.listFiles())
            subFile.delete();
    }

    private void exportOpeningBalances(Date endDate, File outputFolder) throws IOException {
        String customTimePeriodIdOfPreviousFiscalYear = getCustomTimePeriodIdOfPreviousFiscalYear(endDate);
        Date startDate = findFinancialYearStartDate(endDate);
        final List<Map<String, Object>> ofbizGlAccountSummaries = ofbizTransactionFinder.getOfbizGlAccountSummariesForOpeningBalances(String.valueOf(customTimePeriodIdOfPreviousFiscalYear));
        List<PastelGeneralJournalTransaction> pastelGeneralJournalTransactionList = createListOfPastelGeneralJournalTransaction(ofbizGlAccountSummaries, startDate);
        writeOpeningBalances(outputFolder, pastelGeneralJournalTransactionList);
    }

    private List<PastelGeneralJournalTransaction> createListOfPastelGeneralJournalTransaction(List<Map<String, Object>> ofbizGlAccountSummaries, Date startDate) {
        Preconditions.checkNotNull(ofbizGlAccountSummaries);
        Preconditions.checkNotNull(startDate);
        PastelGeneralJournalTransaction pastelGeneralJournalTransactionForDebit = null;
        List<PastelGeneralJournalTransaction> pastelGeneralJournalTransactionList = Lists.newArrayList();
        for(int i = 0; i< ofbizGlAccountSummaries.size(); i++) {
            pastelGeneralJournalTransactionForDebit = new PastelGeneralJournalTransaction(startDate, ofbizGlAccountSummaries.get(i).get("glAccountId").toString(), (BigDecimal)ofbizGlAccountSummaries.get(i).get("amount"), "0000000", (BigDecimal)ofbizGlAccountSummaries.get(i).get("amount"));
            pastelGeneralJournalTransactionList.add(pastelGeneralJournalTransactionForDebit);
        }
        return pastelGeneralJournalTransactionList;
    }

    private String getCustomTimePeriodIdOfPreviousFiscalYear(Date endDate){
        Preconditions.checkNotNull(endDate);
        Date startDate = findFinancialYearStartDate(endDate);
        Date dateOfTenDaysBeforeStartDate = getDateOfTenDaysBeforeFinancialYearStartDate(startDate);
        int customTimePeriodIdOfPreviousFiscalYear = ofbizTransactionFinder.getCustomTimePeriodIdOfPreviousFiscalYear(dateOfTenDaysBeforeStartDate);
        return String.valueOf(customTimePeriodIdOfPreviousFiscalYear);
    }

    private Date getDateOfTenDaysBeforeFinancialYearStartDate(Date startDate) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, -10);
        return calendar.getTime();
    }

    private void initialiseGlAccountSummaries() {
        final List<String> masterGlAccountIds = ofbizTransactionFinder.getMasterGlAccounts();
        Map<String, GlAccount> glAccountMap = Maps.newHashMap();
        for (String masterGlAccountId : masterGlAccountIds) {
            final String cleansedMasterGlAccountId = masterGlAccountId.trim();
            glAccountMap.put(cleansedMasterGlAccountId, new GlAccount(cleansedMasterGlAccountId));
        }
        GlAccountSummaryManager.assignGlAccounts(glAccountMap);
    }

    private String exportTransactions(Date startDate, Date endDate, File outputFolder) throws IOException {
        Long t1 = System.currentTimeMillis();
        Collection<Interval> intervalsByMonthBetweenStartDateAndEndDate = intervalsByMonthBetweenStartDateAndEndDate(startDate, endDate);
        for (Interval interval : intervalsByMonthBetweenStartDateAndEndDate) {
            final Map<OfbizTransactionType, Collection<JournalEntry>> journalEntries = ofbizTransactionFinder.fetchOfbizTransactions(interval.getStart().toDate(), interval.getEnd().toDate());
            if(journalEntries.isEmpty())
                continue;
            final Multimap<PastelTransactionType, IPastelTransaction> pastelTransactionByType = transformIntoPastelTransactions(journalEntries);
            writeIntoCsv(pastelTransactionByType, outputFolder, false);
        }
        Long t2 = System.currentTimeMillis();
        //System.out.println("Time taken by exportTransactions ==========>"+(t2-t1));
        return outputFolder.getAbsolutePath();
    }

    private void exportPaymentsTransactions(Date startDate, Date endDate, File outputFolder) throws IOException {
        Collection<Interval> intervalsByMonthBetweenStartDateAndEndDate = intervalsByMonthBetweenStartDateAndEndDate(startDate, endDate);
        for (Interval interval : intervalsByMonthBetweenStartDateAndEndDate) {
            final Collection<PaymentJournalEntry> paymentJournalEntries = ofbizTransactionFinder.fetchOfbizPaymentTransactions(interval.getStart().toDate(), interval.getEnd().toDate());
            if(CollectionUtils.isEmpty(paymentJournalEntries))
                continue;
            final Map<String, Collection<PaymentJournalEntry>> journalEntries = splitIntoCashbookAndJournalTransactions(paymentJournalEntries);
            if(journalEntries.containsKey(GENERAL_JOURNAL_ENTRIES)) {
                final Collection<IPastelTransaction> pastelGeneralJournalTransactions = transformIntoJournalEntries(journalEntries.get(GENERAL_JOURNAL_ENTRIES));
                writePaymentGeneralJournalTransactions(outputFolder, pastelGeneralJournalTransactions);
            }
            if(journalEntries.containsKey(CASHBOOK)) {
                final Collection<PastelPaymentTransaction> pastelCashbookTransactions = transformIntoCashbookTransactions(journalEntries.get(CASHBOOK));
                writePaymentCashbookTransactions(outputFolder, pastelCashbookTransactions);
            }
        }
        ofbizTransactionFinder.fetchOfbizPaymentTransactions(startDate, endDate);
    }

    private void exportPaymentAppliedTransactions(Date startDate, Date endDate, File outputFolder) throws IOException {
        Collection<Interval> intervalsByMonthBetweenStartDateAndEndDate = intervalsByMonthBetweenStartDateAndEndDate(startDate, endDate);
        for (Interval interval : intervalsByMonthBetweenStartDateAndEndDate) {
            final Collection<JournalEntry> paymentAppliedJournalEntries = ofbizTransactionFinder.fetchOfbizPaymentAppliedTransactions(interval.getStart().toDate(), interval.getEnd().toDate());
            final Collection<PastelInvoiceTransaction> pastelTransactions = (Collection<PastelInvoiceTransaction>) IPastelTransformers.PastelInvoiceEntryTransformer.apply(paymentAppliedJournalEntries);
            writePaymentAppliedTransactionsIntoCsv(pastelTransactions, outputFolder);
        }
    }

    private void exportPayroll(Date startDate, Date endDate, File outputFolder) throws IOException {
        Collection<Interval> intervalsByMonthBetweenStartDateAndEndDate = intervalsByMonthBetweenStartDateAndEndDate(startDate, endDate);
        for(Interval interval : intervalsByMonthBetweenStartDateAndEndDate){
            Collection<JournalEntry> journalEntries = ofbizTransactionFinder.fetchOfbizPayrollTransactions(interval.getStart().toDate(), interval.getEnd().toDate());
            if(journalEntries.isEmpty())
                continue;
            final Collection<? extends IPastelTransaction> pastelTransactions = OfbizTransactionType.DEFAULT.pastelJournalEntryTransformer.apply(journalEntries);
            writePayrollTransactionsIntoCsv(pastelTransactions, outputFolder, true);

        }
    }
    private void writePaymentAppliedTransactionsIntoCsv(Collection<PastelInvoiceTransaction> pastelTransactions, File outputFolder) throws IOException {
        for (PastelInvoiceTransaction pastelTransaction : pastelTransactions) {
            final PastelTransactionType pastelTransactionType = pastelTransaction.getPastelTransactionType();
            final FileWriter fileWriter = new FileWriter(outputFolder + "//" + pastelTransactionType.fileName, true);
            CSVWriter csvOutput = new CSVWriter(fileWriter);
            csvOutput.writeAll(pastelTransaction.transformIntoCvsRecord());
            csvOutput.flush();
            csvOutput.close();
        }
    }

    private Collection<IPastelTransaction> transformIntoJournalEntries(Collection<PaymentJournalEntry> paymentJournalEntries) {
        if (CollectionUtils.isEmpty(paymentJournalEntries)) {
            return Lists.newArrayList();
        }
        final Collection<JournalEntry> journalEntries = ExtractJournalEntries(paymentJournalEntries);
        final Collection<IPastelTransaction> pastelTransactions = (Collection<IPastelTransaction>) IPastelTransformers.PastelJournalEntryTransformer.apply(journalEntries);
        return pastelTransactions;
    }

    private static Collection<JournalEntry> ExtractJournalEntries(Collection<PaymentJournalEntry> paymentJournalEntries) {
        Collection<JournalEntry> journalEntries = Lists.newArrayList();
        for (PaymentJournalEntry paymentJournalEntry : paymentJournalEntries) {
            journalEntries.add(paymentJournalEntry.getJournalEntry());
        }
        return journalEntries;
    }

    private Multimap<PastelTransactionType, IPastelTransaction> transformIntoPastelTransactions(Map<OfbizTransactionType, Collection<JournalEntry>> journalEntries) {
        Multimap<PastelTransactionType, IPastelTransaction> pastelTransactionsByType = HashMultimap.create();
        for (Map.Entry<OfbizTransactionType, Collection<JournalEntry>> ofbizJournalEntry : journalEntries.entrySet()) {
            final OfbizTransactionType ofbizTransactionType = ofbizJournalEntry.getKey();
            final Collection<? extends IPastelTransaction> pastelTransactions = transformIntoPastelTransactions(ofbizTransactionType, ofbizJournalEntry.getValue());
            pastelTransactionsByType.putAll(ofbizTransactionType.pastelTransactionType, pastelTransactions);
        }
        return pastelTransactionsByType;
    }

    private Collection<? extends IPastelTransaction> transformIntoPastelTransactions(OfbizTransactionType key, Collection<JournalEntry> journalEntries) {
        final Collection<? extends IPastelTransaction> pastelTransactions = key.pastelJournalEntryTransformer.apply(journalEntries);
        return pastelTransactions;
    }

    private Collection<PastelPaymentTransaction> transformIntoCashbookTransactions(Collection<PaymentJournalEntry> paymentJournalEntries) {
        final List<String> cashAndBankAccounts = ofbizTransactionFinder.fetchAllCashAndBankAccounts();
        final Collection<PastelPaymentTransaction> pastelPaymentTransactions = Lists.newArrayList();
        Function<PaymentJournalEntry,PastelPaymentTransaction> pastelCashbookTransformer = new PastelCashbookTransformer(cashAndBankAccounts);
        for (PaymentJournalEntry paymentJournalEntry : paymentJournalEntries) {
            final PastelPaymentTransaction pastelTransaction = pastelCashbookTransformer.apply(paymentJournalEntry);
            pastelPaymentTransactions.add(pastelTransaction);
        }
        return pastelPaymentTransactions;
    }

    private void writePaymentCashbookTransactions(File outputFolder, Collection<PastelPaymentTransaction> pastelCashbookTransactions) throws IOException {
        //System.out.println("Before CSv writer cashbook");
        Long t1 = System.currentTimeMillis();
        for (PastelPaymentTransaction pastelCashbookTransaction : pastelCashbookTransactions) {
            if(pastelCashbookTransactions.isEmpty())
                continue;
            String fileName = outputFolder.getAbsolutePath() + "//" +"cashbook_" + pastelCashbookTransaction.glAccountId + "_" + pastelCashbookTransaction.getPaymentType().name()+".csv";
            final FileWriter fileWriter = new FileWriter(fileName, true);
            CSVWriter csvOutput = new CSVWriter(fileWriter);
            csvOutput.writeAll(pastelCashbookTransaction.transformIntoCvsRecord());
            csvOutput.flush();
            csvOutput.close();
        }
        Long t2 = System.currentTimeMillis();
        //System.out.println("Time taken by writePaymentCashbookTransactions ==========>"+(t2-t1));
        //System.out.println("After CSv writer cashbook");
    }

    private void writeOpeningBalances(File outputFolder, Collection<PastelGeneralJournalTransaction> pastelGeneralJournalTransactions) throws IOException {
        boolean flagForAppending = false;
        for (PastelGeneralJournalTransaction pastelGeneralJournalTransaction : pastelGeneralJournalTransactions) {
            String fileName = outputFolder.getAbsolutePath() + "/OpeningBalances.csv";
            final FileWriter fileWriter = new FileWriter(fileName, flagForAppending);
            CSVWriter csvOutput = new CSVWriter(fileWriter);
            csvOutput.writeAll(pastelGeneralJournalTransaction.transformIntoCvsRecord());
            csvOutput.flush();
            csvOutput.close();
            flagForAppending = true;
        }
    }

    private Map<String, Collection<PaymentJournalEntry>> splitIntoCashbookAndJournalTransactions(Collection<PaymentJournalEntry> paymentJournalEntries) {
        final HashMultimap<String, PaymentJournalEntry> generalJournalAndCashbookTransactions = HashMultimap.create();
        for (PaymentJournalEntry paymentJournalEntry : paymentJournalEntries) {
            if (paymentJournalEntry.isFinancialTransactionFromBankAccount() || paymentJournalEntry.isCashAccount()) {
                generalJournalAndCashbookTransactions.put(CASHBOOK, paymentJournalEntry);
            } else {
                generalJournalAndCashbookTransactions.put(GENERAL_JOURNAL_ENTRIES, paymentJournalEntry);
            }
        }
        return generalJournalAndCashbookTransactions.asMap();
    }


    private void writePaymentGeneralJournalTransactions(File outputFolder, Collection<IPastelTransaction> pastelGeneralJournalTransactions) throws IOException {
        final HashMultimap<PastelTransactionType, IPastelTransaction> generalJournalEntries = HashMultimap.create();
        generalJournalEntries.putAll(PastelTransactionType.GENERAL_JOURNAL, pastelGeneralJournalTransactions);
        writeIntoCsv(generalJournalEntries, outputFolder, true);
    }


    private void writeIntoCsv(Multimap<PastelTransactionType, IPastelTransaction> pastelTransactionByType, File outputFolder, boolean isAppend) throws IOException {
        //System.out.println("Before CSv writer");
        Long t1 = System.currentTimeMillis();
        Set<PastelTransactionType> pastelTransactionTypes = pastelTransactionByType.keySet();
        for (PastelTransactionType pastelTransactionType : pastelTransactionTypes) {
            Collection<IPastelTransaction> pastelTransactions = pastelTransactionByType.get(pastelTransactionType);
            if(pastelTransactions.isEmpty())
                continue;
            final FileWriter fileWriter = new FileWriter(outputFolder + "//" + pastelTransactionType.fileName, isAppend);
            CSVWriter csvOutput = new CSVWriter(fileWriter);
            for (IPastelTransaction pastelTransaction : pastelTransactions) {
                csvOutput.writeAll(pastelTransaction.transformIntoCvsRecord());
            }
            csvOutput.close();
        }
        Long t2 = System.currentTimeMillis();
        //System.out.println("Time taken by writeIntoCsv ==========>"+(t2-t1));
        //System.out.println("After CSv writer");
    }

    private void writePayrollTransactionsIntoCsv(Collection<? extends IPastelTransaction> pastelTransactions, File outputFolder, boolean b) throws IOException {
        final FileWriter fileWriter = new FileWriter(outputFolder + "//" + "payroll_journal.csv", b);
        CSVWriter csvOutput = new CSVWriter(fileWriter);
        for (IPastelTransaction pastelTransaction : pastelTransactions) {
            csvOutput.writeAll(pastelTransaction.transformIntoCvsRecord());
        }
        csvOutput.close();
    }


    static Collection<Interval> intervalsByMonthBetweenStartDateAndEndDate(Date startDate, Date endDate) {
        return intervalsByMonthBetweenStartDateAndToday(LocalDate.fromDateFields(startDate), LocalDate.fromDateFields(endDate));

    }

    static Collection<Interval> intervalsByMonthBetweenStartDateAndToday(LocalDate startDate, LocalDate today) {
        Collection<Interval> intervals = Lists.newArrayList();
        while (startDate.isBefore(today) || startDate.isEqual(today)) {
            LocalDate intervalEndDate = startDate.plusDays(1);
            Interval interval = new Interval(startDate.toDate().getTime(), intervalEndDate.toDate().getTime());
            intervals.add(interval);
            startDate = new LocalDate(intervalEndDate);
        }
        return intervals;
    }


    public Date findFinancialYearStartDate(Date transactionsExportedUptoDate) {
        try {
            final Map<String, Object> customPeriodDetails = ofbizTransactionFinder.getCustomPeriodDetails(transactionsExportedUptoDate);
            return (Date) customPeriodDetails.get("startDate");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String findCustomTimePeriodId(Date transactionsExportedUptoDate) {
        final Map<String, Object> customPeriodDetails = ofbizTransactionFinder.getCustomPeriodDetails(transactionsExportedUptoDate);
        return (String) customPeriodDetails.get("customTimePeriodId");
    }

    public boolean createLogForUnmatchedGlAccounts(Date transactionsExportedUptoDate, String outputFolderPath) throws IOException {
        File outputFolder = new File(outputFolderPath);
        boolean validationSucceeded = true;
        final String customTimePeriodId = findCustomTimePeriodId(transactionsExportedUptoDate);
        //System.out.println("id == "+customTimePeriodId);
        final List<Map<String, Object>> ofbizGlAccountSummaries = ofbizTransactionFinder.getOfbizGlAccountSummaries(customTimePeriodId);
        List<String> unmatchedGlAccountList = FindUnmatchedGlAccountsFromOfbizGlAccountSummaries(ofbizGlAccountSummaries);
        if (!unmatchedGlAccountList.isEmpty()) {
            writeIntoCsvUnmatchedGlAccount(unmatchedGlAccountList, outputFolder);
            validationSucceeded = false;
        }
        return validationSucceeded;
    }

    public void writeIntoCsvUnmatchedGlAccount(List<String> unmatchedGlAccountList, File outputFolderPath) throws IOException {
        CSVWriter unmatchedGlAccountsCsv = new CSVWriter(new FileWriter(outputFolderPath + "//mismatch.log"));
        for (String glAccount : unmatchedGlAccountList) {
            unmatchedGlAccountsCsv.writeNext(glAccount.split(","));
        }
        unmatchedGlAccountsCsv.flush();
        unmatchedGlAccountsCsv.close();
    }

    public static List<String> FindUnmatchedGlAccountsFromOfbizGlAccountSummaries(List<Map<String, Object>> ofbizGlAccountSummaries) {
        List<String> unmatchedGlAccountList = Lists.newArrayList();
        for (Map<String, Object> ofbizGlAccountSummary : ofbizGlAccountSummaries) {
            String glAccountId = (String) ofbizGlAccountSummary.get("glAccountId");
            BigDecimal debit = (BigDecimal) ofbizGlAccountSummary.get("debit");
            BigDecimal credit = (BigDecimal) ofbizGlAccountSummary.get("credit");
            final boolean success = GlAccountSummaryManager.validateGlAccount(glAccountId, debit, credit);
            if (!success) {
                final String unmatchedGlAccount = GlAccountSummaryManager.findGlAccountById(glAccountId);
                unmatchedGlAccountList.add(unmatchedGlAccount);
            }
        }
        return unmatchedGlAccountList;
    }


    public void exportTheCustomerJournals(Date startDate,File outputFolder) throws IOException {
        final String ROLE_TYPE= "BILL_TO_CUSTOMER";
        List<CustomerJournal> customerJournalList =  ofbizTransactionFinder.getTheJournalDetailsForGivenRoleType(startDate, ROLE_TYPE);
        writeJournalTransactionsToCSV(outputFolder, customerJournalList, "CUSTOMER_JOURNAL");

    }

    private void exportTheSupplierJournals(Date startDate, File outputFolder) throws IOException {
        final String ROLE_TYPE= "BILL_FROM_VENDOR";
        List<CustomerJournal> supplierJournalList =  ofbizTransactionFinder.getTheJournalDetailsForGivenRoleType(startDate, ROLE_TYPE);
        writeJournalTransactionsToCSV(outputFolder, supplierJournalList, "SUPPLIER_JOURNAL");
    }

    private void writeJournalTransactionsToCSV(File outputFolder, List<CustomerJournal> journalList, String pastelTransactionType) throws IOException {
        final Map<PastelTransactionType, List<CustomerJournal>> journalByTransactionType = Maps.newLinkedHashMap();
        String journalType = "CUSTOMER";
        if ("CUSTOMER_JOURNAL".equals(pastelTransactionType))     {
            journalByTransactionType.put(PastelTransactionType.CUSTOMER_JOURNAL, journalList);
            journalType="CUSTOMER";
        }
        else  if ("SUPPLIER_JOURNAL".equals(pastelTransactionType)){
            journalByTransactionType.put(PastelTransactionType.SUPPLIER_JOURNAL, journalList);
            journalType="SUPPLIER";
        }
        writeIntoCsv(journalByTransactionType, outputFolder, journalType);
    }

    private void writeIntoCsv(Map<PastelTransactionType, List<CustomerJournal>> customerJournalDetails, File outputFolder,String journalType) throws IOException {
        Set<PastelTransactionType> pastelTransactionTypes = customerJournalDetails.keySet();
        for (PastelTransactionType pastelTransactionType : pastelTransactionTypes) {
            List<CustomerJournal> pastelTransactions = customerJournalDetails.get(pastelTransactionType);
            if(pastelTransactions.isEmpty())
                continue;
            final FileWriter fileWriter = new FileWriter(outputFolder + "//" + pastelTransactionType.fileName,false);
            CSVWriter csvOutput = new CSVWriter(fileWriter);
            csvOutput.writeAll(CustomerJournal.transformIntoCsvRecord(pastelTransactions, journalType));
            csvOutput.close();
        }
    }

    private void readFilesRemoveNewLineCharacterFromTheLastLineOfEveryFile(String outputFolder) {
        File directory = new File(outputFolder);
        File[] files = directory.listFiles();
        for(File file : files){
            if(file.getName().endsWith(".csv")){
                readContentsOfTheFileRemovingNewLineCharacterFromLastline(file);
            }
        }
    }

    private void readContentsOfTheFileRemovingNewLineCharacterFromLastline(File file){
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
            writeToFileWithoutNewLineCharacterAtLastLine(sb,file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFileWithoutNewLineCharacterAtLastLine(StringBuilder sb, File file) {
        try(PrintWriter printWriter = new PrintWriter(new FileWriter(file))) {
            printWriter.write(sb.toString());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
