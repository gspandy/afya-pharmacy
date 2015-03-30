package com.pastel.domain.model;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 1/23/15
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class CustomerJournal {

    public String customerId;
    public String transactionType;
    public BigDecimal amount;
    public Date transactionDate;

    static DecimalFormat DECIMAL_FORMAT =  new DecimalFormat("0.00");
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    static Calendar cal = Calendar.getInstance();

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = getFurnishedParytId(customerId);
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (this.transactionType.equals("C"))
            this.amount = amount.negate();
        else
            this.amount=amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public static List<String[]> transformIntoCsvRecord(List<CustomerJournal> customerJournal,String journalType){
        ArrayList<String[]> result = new ArrayList<String[]>();
        for (CustomerJournal row : customerJournal){
            String period =  String.valueOf(cal.get(Calendar.MONTH)+1);
            final String formattedTransactionDate = dateFormat.format(row.transactionDate);
            String[] csvRecord = Lists.newArrayList(period,formattedTransactionDate,journalType.equals("CUSTOMER")?"D":"C",row.customerId,"","",DECIMAL_FORMAT.format(row.amount),"","","","",
                    journalType.equals("CUSTOMER")?IPastelTransaction.CUSTOMER_JOURNAL_CONTRA_ACCOUNT:IPastelTransaction.SUPPLIER_JOURNAL_CONTRA_ACCOUNT,BigDecimal.ONE.toString(),"","","","","").toArray(new String[18]);
            result.add(csvRecord);
        }
        return result;
    }

    public String getFurnishedParytId(String partyId){
        if(partyId == null)
            return partyId;
        if (partyId.matches("-?\\d+")) {
            if(partyId.trim().length() == 1)
                partyId =  "00"+partyId;
            if(partyId.trim().length() == 2)
                partyId =  "0"+partyId;
            return partyId;
        }
        else
            return partyId.trim();
    }
}