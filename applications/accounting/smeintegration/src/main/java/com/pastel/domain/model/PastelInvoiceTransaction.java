package com.pastel.domain.model;

import com.google.common.collect.Lists;
import com.pastel.service.GlAccountSummaryManager;

import java.math.BigDecimal;
import java.util.*;

/**
 * Author: Nthdimenzion
 */

public class PastelInvoiceTransaction implements IPastelTransaction {



    public String partyId;
    public Date transactionDate;
    public String invoiceId;
    public String period;
    private String contraGlAccountId;
    private PastelTransactionType pastelTransactionType;
    public final String contraGlAccountType;

    private Collection<PastelInvoiceTransactionDetail> pastelInvoiceTransactionDetails = Lists.newArrayList();


    public PastelInvoiceTransaction(String partyId, Date transactionDate, String invoiceId, String contraGlAccountId1, PastelTransactionType pastelTransactionType, String contraGlAccountType) {
        this.partyId = partyId;
        this.transactionDate = transactionDate;
        cal.setTime(transactionDate);
        this.period = String.valueOf(cal.get(Calendar.MONTH)+1);
        this.invoiceId = invoiceId;
        this.contraGlAccountId = contraGlAccountId1;
        this.pastelTransactionType = pastelTransactionType;
        this.contraGlAccountType = contraGlAccountType;
    }

    public void addTransactionDetail(PastelInvoiceTransactionDetail pastelInvoiceTransactionDetail){
        this.pastelInvoiceTransactionDetails.add(pastelInvoiceTransactionDetail);
    }

    @Override
    public List<String[]> transformIntoCvsRecord() {
        List<String[]> result = Lists.newArrayList();
        final String formattedTransactionDate = dateFormat.format(transactionDate);
        String[] header = Lists.newArrayList("Header","","","",partyId,period,formattedTransactionDate,invoiceId,"Y","0","","","","","","","","","","0",
                                            formattedTransactionDate,"","","","1.00","","","N").toArray(new String[28]);
        result.add(header);

        for (PastelInvoiceTransactionDetail pastelInvoiceTransactionDetail : pastelInvoiceTransactionDetails) {
            if(!(pastelInvoiceTransactionDetail.glAccountId.equals(SUPPLIER_CONTROL_ACCOUNT) || pastelInvoiceTransactionDetail.glAccountId.equals(CUSTOMER_CONTROL_ACCOUNT))){

                String[] detail = Lists.newArrayList("Detail","0","1",DECIMAL_FORMAT.format(pastelInvoiceTransactionDetail.homeAmountExcludingTax),
                        DECIMAL_FORMAT.format(pastelInvoiceTransactionDetail.homeAmountIncludingTax),"","1","0","0",pastelInvoiceTransactionDetail.glAccountId,
                        pastelInvoiceTransactionDetail.glAccountId,"6","","").toArray(new String[14]);
                result.add(detail);
            }
        }
        return result;
    }


    public void updateGlSummaries(){
        BigDecimal total = BigDecimal.ZERO;
        if(pastelTransactionType.transactionType == JournalEntry.TransactionType.CREDIT) {
            for (PastelInvoiceTransactionDetail pastelInvoiceTransactionDetail : pastelInvoiceTransactionDetails) {
                total = total.add(pastelInvoiceTransactionDetail.homeAmountIncludingTax);
                GlAccountSummaryManager.addDebitAmount(pastelInvoiceTransactionDetail.glAccountId, pastelInvoiceTransactionDetail.homeAmountExcludingTax);
                final BigDecimal taxPaid = pastelInvoiceTransactionDetail.homeAmountIncludingTax.subtract(pastelInvoiceTransactionDetail.homeAmountExcludingTax);
                GlAccountSummaryManager.addDebitAmount(TAX_GL_ACCOUNT_ID, taxPaid);
            }
            GlAccountSummaryManager.addCreditAmount(contraGlAccountId, total);
        }else{
            for (PastelInvoiceTransactionDetail pastelInvoiceTransactionDetail : pastelInvoiceTransactionDetails) {
                total = total.add(pastelInvoiceTransactionDetail.homeAmountIncludingTax);
                GlAccountSummaryManager.addCreditAmount(pastelInvoiceTransactionDetail.glAccountId, pastelInvoiceTransactionDetail.homeAmountExcludingTax);
                final BigDecimal taxPaid = pastelInvoiceTransactionDetail.homeAmountIncludingTax.subtract(pastelInvoiceTransactionDetail.homeAmountExcludingTax);
                GlAccountSummaryManager.addCreditAmount(TAX_GL_ACCOUNT_ID, taxPaid);
            }
            GlAccountSummaryManager.addDebitAmount(contraGlAccountId, total);
        }

    }

    public PastelTransactionType getPastelTransactionType() {
        return pastelTransactionType;
    }
}
