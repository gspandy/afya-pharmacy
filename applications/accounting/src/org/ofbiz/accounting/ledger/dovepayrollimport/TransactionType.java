package org.ofbiz.accounting.ledger.dovepayrollimport;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 11/13/14
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TransactionType {

    DEBIT("D"),

    CREDIT("C");

    TransactionType(String transactionType){
        this.transactionType = transactionType;
    }

    public String transactionType;
}
