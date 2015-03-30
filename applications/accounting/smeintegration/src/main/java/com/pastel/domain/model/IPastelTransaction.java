package com.pastel.domain.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

public interface IPastelTransaction {

    public static final String TAX_GL_ACCOUNT_ID = "9500000";
    public static final String CUSTOMER_CONTROL_ACCOUNT = "8000000";
    public static final String SUPPLIER_CONTROL_ACCOUNT = "9000000";
    public static final String CUSTOMER_SUSPENSE_ACCOUNT = "9990100";
    public static final String SUPPLIER_SUSPENSE_ACCOUNT = "9990200";
    public static final String CUSTOMER_JOURNAL_CONTRA_ACCOUNT = "9990100";
    public static final String SUPPLIER_JOURNAL_CONTRA_ACCOUNT = "9990200";


    static Calendar cal = Calendar.getInstance();

    DecimalFormat DECIMAL_FORMAT =  new DecimalFormat("0.00");

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    List<String[]> transformIntoCvsRecord();
}
