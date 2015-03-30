package com.pastel.domain.model;

import com.google.common.collect.Lists;
import com.pastel.service.GlAccountSummaryManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

public class PastelGeneralJournalTransaction implements IPastelTransaction {

    String period;
    Date transactionDate;
    String glAccountNumber;
    BigDecimal amount;
    String contraGlAccountNumber;
    /**
     * home amount and amount will always be the same
     */
    BigDecimal exchangeRate = BigDecimal.ONE;
    BigDecimal homeAmount;

    public PastelGeneralJournalTransaction(Date transactionDate, String glAccountNumber, BigDecimal amount, String contraGlAccountNumber, BigDecimal homeAmount) {
        this.transactionDate = transactionDate;
        cal.setTime(transactionDate);
        this.period = String.valueOf(cal.get(Calendar.MONTH)+1);
        this.glAccountNumber = glAccountNumber;
        this.amount = amount;
        this.contraGlAccountNumber = contraGlAccountNumber;
        this.homeAmount = homeAmount;
    }

    @Override
    public List<String[]> transformIntoCvsRecord() {
        final String formattedTransactionDate = dateFormat.format(transactionDate);
        String[] row = Lists.newArrayList(period,formattedTransactionDate,"G",glAccountNumber,"0","",DECIMAL_FORMAT.format(amount),"0","0","","",contraGlAccountNumber,exchangeRate.toString(),
                exchangeRate.toString(),"0","0","0",DECIMAL_FORMAT.format(homeAmount)).toArray(new String[18]);
        final ArrayList<String[]> result = new ArrayList<String[]>();
        result.add(row);
        return result;
    }

    public void post(){
        GlAccountSummaryManager.addDebitAmount(glAccountNumber, homeAmount.abs());
        GlAccountSummaryManager.addCreditAmount(contraGlAccountNumber, homeAmount.abs());
    }

    public void postPaymentTransaction(PastelPaymentTransaction.PaymentType paymentType) {
        if(paymentType == PastelPaymentTransaction.PaymentType.PAYMENT){
            GlAccountSummaryManager.addCreditAmount(contraGlAccountNumber,homeAmount.abs());
            GlAccountSummaryManager.addDebitAmount(glAccountNumber, homeAmount.abs());
        }else{
            GlAccountSummaryManager.addDebitAmount(contraGlAccountNumber,homeAmount.abs());
            GlAccountSummaryManager.addCreditAmount(glAccountNumber,homeAmount.abs());
        }

    }
}
