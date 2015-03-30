package com.pastel.service;

import com.google.common.collect.Maps;
import com.pastel.domain.model.GlAccount;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */

public final class GlAccountSummaryManager {

    private static Map<String, GlAccount> glAccounts = Maps.newHashMap();

    public static void assignGlAccounts(Map<String, GlAccount> glAccounts) {
        GlAccountSummaryManager.glAccounts = glAccounts;
    }

    public static void addDebitAmount(String glAccountId, BigDecimal amount) {
        if (glAccounts.get(glAccountId) != null)
            glAccounts.get(glAccountId).addDebitValue(amount);
        else {
            throw new RuntimeException("SOMETHING WRONG HERE IN DEBIT BALANCES" + glAccountId);
        }
    }


    public static void addCreditAmount(String glAccountId, BigDecimal amount) {
        if (glAccounts.get(glAccountId) != null)
            glAccounts.get(glAccountId).addCreditValue(amount);
        else {
            throw new RuntimeException("SOMETHING WRONG HERE IN CREDIT BALANCES" + glAccountId);
        }
    }

    public static Map<String,GlAccount> getGlAccounts(){
        return glAccounts;
    }

    public static boolean validateGlAccount(String glAccountId, BigDecimal debit, BigDecimal credit) {
        return glAccounts.get(glAccountId).validate(debit,credit);

    }

    public static String findGlAccountById(String glAccountId){
        return glAccounts.get(glAccountId).toString();

    }
}
