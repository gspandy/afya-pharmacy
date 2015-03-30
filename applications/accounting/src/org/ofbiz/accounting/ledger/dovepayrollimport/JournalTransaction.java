package org.ofbiz.accounting.ledger.dovepayrollimport;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 11/6/14
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class JournalTransaction {

    private  Properties properties = new Properties();
    public Timestamp journalTransactionDate;

    public JournalEntry readTheJournalListingAndMapToJournalEntry(BufferedReader reader){
        List<JournalEntryItem> journalEntryItems =  readTheJournalListingFromTheCSVFile(reader);
        JournalEntry journalEntry = new JournalEntry(journalTransactionDate,journalEntryItems);
        return journalEntry;
    }


    public List<JournalEntryItem> readTheJournalListingFromTheCSVFile(BufferedReader reader) {
        boolean isIndexFound = false;
        boolean isRowFound = false;
        String row = "";
        List<JournalEntryItem> journalEntryList = new ArrayList<JournalEntryItem>();
        try {
            ColumnIndexFinder columnIndexFinder = null;
            while ((row = reader.readLine()) != null) {
                if (!isIndexFound){
                    String transactionDate =  JournalEntryUtil.getTheTransactionalDate(row);
                    if (transactionDate!="" && !transactionDate.isEmpty()){
                        journalTransactionDate = JournalEntryUtil.getDateFormat(transactionDate);
                    }
                    columnIndexFinder =  ColumnIndexFinder.getTheColumnIndexFromCSVWithGivenRow(row);
                    if (columnIndexFinder != null){
                        isIndexFound   = true;
                    }
                }
                if (isRowFound){
                    JournalEntryItem journalEntryItem =  mapEachCSVRowToJournalEntryItem(row, columnIndexFinder);
                    if (journalEntryItem.accountId != null && journalEntryItem.accountId!=""){
                        journalEntryList.add(journalEntryItem);
                    }
                }
                isRowFound = isIndexFound;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return journalEntryList;
    }

    public JournalEntryItem mapEachCSVRowToJournalEntryItem(String csvData, ColumnIndexFinder indexer) throws IOException {
        properties =  UtilProperties.getProperties("payrollcode.properties");
        long columnIndex = 0;
        JournalEntryItem journalEntryItem = new JournalEntryItem();

        String[] stringArray  = csvData.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String csvCell : stringArray){
            columnIndex++;
            if (indexer.moneyCodeColumnIndex==columnIndex && !csvCell.isEmpty()){
                journalEntryItem.accountId = csvCell;
                if (properties.getProperty(csvCell) == null){
                    throw new IllegalArgumentException("The GlAccount id mapping is not available for money code "+csvCell);
                }
                journalEntryItem.glAccountId = properties.getProperty(csvCell);
            }
            else if (indexer.moneyNameColumnIndex==columnIndex &&  !csvCell.isEmpty()){
                journalEntryItem.moneyName = csvCell;
            }
            else if (indexer.creditAmountColumnIndex==columnIndex &&  !csvCell.isEmpty()){
                String finalString = StringUtils.replaceEachRepeatedly(csvCell,new String[]{"\""},new String[]{""});
                if(isTransactionAmountIsInFormat(finalString.replaceAll(",", "").trim())){
                    journalEntryItem.amount = new BigDecimal(finalString.replaceAll(",", "").trim(),MathContext.DECIMAL128).doubleValue();
                    journalEntryItem.type = TransactionType.CREDIT.transactionType;
                }
            }
            else if (indexer.debitAmountColumnIndex==columnIndex &&  !csvCell.isEmpty()){
                String actualValue = StringUtils.replaceEachRepeatedly(csvCell,new String[]{"\""},new String[]{""});
                if(isTransactionAmountIsInFormat(actualValue.replaceAll(",", "").trim())){
                    journalEntryItem.amount = new BigDecimal(actualValue.replaceAll(",", "").trim(),MathContext.DECIMAL128).doubleValue();
                    journalEntryItem.type = TransactionType.DEBIT.transactionType;
                }
            }
        }
        return getTheJournalEntryItemWithGlAccountIdForTheGivenMoneyCode(journalEntryItem);
    }

    public boolean isTransactionAmountIsInFormat(String csvData){
        Pattern pattern = Pattern.compile("((\\+|-)?([0-9]+)(\\.[0-9]+)?)|((\\+|-)?\\.?[0-9]+)");
        Matcher  matcher = pattern.matcher(csvData);
        if (matcher.matches())
            return true;
        else
            return false;
    }

    public JournalEntryItem getTheJournalEntryItemWithGlAccountIdForTheGivenMoneyCode(JournalEntryItem journalEntryItem){
        if (journalEntryItem.accountId == null) {
            return journalEntryItem;
        }
        journalEntryItem.glAccountId = properties.getProperty(journalEntryItem.accountId);
        if (Arrays.asList("D002", "D003", "D009", "D012").contains(journalEntryItem.accountId)) {
            if ("D002".equals(journalEntryItem.accountId)){
                assignTheGlAccountIdForGivenMoneyCodeAndMoneyName(journalEntryItem, "TOTAL NAPSA", "EMPYR NAPSA");
            }
            else if ("D003".equals(journalEntryItem.accountId)){
                assignTheGlAccountIdForGivenMoneyCodeAndMoneyName(journalEntryItem, "PENSION CONTRIBUTION", "EMPYR PENSION CONTRIBUTION");
            }
            else if ("D009".equals(journalEntryItem.accountId)){
                assignTheGlAccountIdForGivenMoneyCodeAndMoneyName(journalEntryItem, "CANTEEN", "EMPYR CANTEEN");
            }
            else if ("D012".equals(journalEntryItem.accountId)){
                assignTheGlAccountIdForGivenMoneyCodeAndMoneyName(journalEntryItem, "OTHER DEDUCTIONS", "IN LIEU OF NOTICE");
            }
        }
        return journalEntryItem;
    }

    private void assignTheGlAccountIdForGivenMoneyCodeAndMoneyName(JournalEntryItem journalEntryItem, String moneyName, String moneyNameForOtherMoneyCode){
        String[] glAccountIds = properties.getProperty(journalEntryItem.accountId).split(",");
        if (glAccountIds.length == 2) {
            if (moneyName.equals(journalEntryItem.moneyName)) {
                journalEntryItem.glAccountId = glAccountIds[0];
            } else if (moneyNameForOtherMoneyCode.equals(journalEntryItem.moneyName)) {
                journalEntryItem.glAccountId = glAccountIds[1];
            }
        }
    }
}
