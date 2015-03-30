package com.pastel.domain.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: Nthdimenzion
 */

public class PastelInvoiceTransactionDetail  {

    public String glAccountId;
    public BigDecimal homeAmountExcludingTax;
    public BigDecimal homeAmountIncludingTax;

    public PastelInvoiceTransactionDetail(String glAccountId, BigDecimal homeAmountExcludingTax, BigDecimal homeAmountIncludingTax) {
        this.glAccountId = glAccountId;
        this.homeAmountExcludingTax = homeAmountExcludingTax;
        this.homeAmountIncludingTax = homeAmountIncludingTax;
    }
}
