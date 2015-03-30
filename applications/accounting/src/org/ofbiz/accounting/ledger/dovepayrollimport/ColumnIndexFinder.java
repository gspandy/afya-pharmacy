package org.ofbiz.accounting.ledger.dovepayrollimport;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 11/6/14
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColumnIndexFinder {

    public long moneyNameColumnIndex;
    public long moneyCodeColumnIndex;
    public long creditAmountColumnIndex;
    public long debitAmountColumnIndex;

    public static ColumnIndexFinder getTheColumnIndexFromCSVWithGivenRow(String csvRow){
        String[] csvCells  = csvRow.split(",");
        boolean isCellFound=false;
        for (String csvCell : csvCells){
            if (Arrays.asList("money code", "money name", "incomes (debit)", "deductions (credit)").contains(csvCell.toLowerCase())){
                isCellFound = true;
                break;
            }
        }
        if (!isCellFound){
            return null;
        }

        ColumnIndexFinder indexer = new ColumnIndexFinder();
        long columnIndex = 0;
        for (String csvCell : csvCells){
            columnIndex++;
            if (StringUtils.equalsIgnoreCase(csvCell, "Money Code")){
                indexer.moneyCodeColumnIndex = columnIndex;
            }
            else  if (StringUtils.equalsIgnoreCase(csvCell,"Money Name")){
                indexer.moneyNameColumnIndex = columnIndex;
            }
            else  if (StringUtils.equalsIgnoreCase(csvCell,"INCOMES (Debit)")){
                indexer.debitAmountColumnIndex = columnIndex;
            }
            else  if (StringUtils.equalsIgnoreCase(csvCell,"DEDUCTIONS (Credit)")){
                indexer.creditAmountColumnIndex = columnIndex;
            }
        }
        return indexer;
    }
}
