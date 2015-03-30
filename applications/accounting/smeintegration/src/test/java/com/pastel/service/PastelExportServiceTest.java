package com.pastel.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pastel.domain.model.GlAccount;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

/**
 * Created by Administrator on 12/3/2014.
 */
public class PastelExportServiceTest {

    private List<Map<String, Object>> ofbizGlAccountSummaries = Lists.newArrayList();
    private List<String> expectedListOfUnmatchedGlAccounts = Lists.newArrayList();
    private Map<String,GlAccount> glAccountMap = Maps.newHashMap();

    private GlAccount glAccountFirst = new GlAccount("1234");
    private GlAccount glAccountSecond = new GlAccount("12345");
    private GlAccount glAccountThird = new GlAccount("123456");

    @Before
    public void setup(){
        Map<String, Object> firstMapOfGlAccount = Maps.newHashMap();
        firstMapOfGlAccount.put("glAccountId","1234");
        firstMapOfGlAccount.put("debit",new BigDecimal(1234));
        firstMapOfGlAccount.put("credit",new BigDecimal(1234));
        Map<String, Object> secondMapOfGlAccount = Maps.newHashMap();
        secondMapOfGlAccount.put("glAccountId","12345");
        secondMapOfGlAccount.put("debit",new BigDecimal(12345));
        secondMapOfGlAccount.put("credit",new BigDecimal(12345));
        Map<String, Object> thirdMapOfGlAccount = Maps.newHashMap();
        thirdMapOfGlAccount.put("glAccountId","123456");
        thirdMapOfGlAccount.put("debit",new BigDecimal(123456));
        thirdMapOfGlAccount.put("credit",new BigDecimal(123456));

        ofbizGlAccountSummaries.add(firstMapOfGlAccount);
        ofbizGlAccountSummaries.add(secondMapOfGlAccount);
        ofbizGlAccountSummaries.add(thirdMapOfGlAccount);

        expectedListOfUnmatchedGlAccounts.add("glAccountId= " + "12345" +  ", debitAmount=" + new BigDecimal(12345) + ", creditAmount=" + new BigDecimal(12345));
        expectedListOfUnmatchedGlAccounts.add("glAccountId= " + "123456" +  ", debitAmount=" + new BigDecimal(123456) + ", creditAmount=" + new BigDecimal(123456));

        glAccountMap.put("1234",glAccountFirst);
        glAccountMap.put("12345", glAccountSecond);
        glAccountMap.put("123456", glAccountThird);

        GlAccountSummaryManager.assignGlAccounts(glAccountMap);

        addCreditDebitToGlAccount(glAccountFirst, new BigDecimal(1234), new BigDecimal(1234));
        addCreditDebitToGlAccount(glAccountSecond, new BigDecimal(12345), new BigDecimal(12345));
        addCreditDebitToGlAccount(glAccountThird, new BigDecimal(123456), new BigDecimal(123456));
    }

    @Test
    public void givenListOfOfbizGlAccountSummaries_whenAllGlAccountsHavingMatchingAmounts_thenItShouldReturnEmptyUnmatchedGlAccountList(){
        List<String> actualListOfUnmatchedGlAccounts = PastelExportService.FindUnmatchedGlAccountsFromOfbizGlAccountSummaries(ofbizGlAccountSummaries);
        assertThat(actualListOfUnmatchedGlAccounts.size(), is(0));
        assertThat(ofbizGlAccountSummaries.size(), is(glAccountMap.size()));
        assertNotEquals(actualListOfUnmatchedGlAccounts, is(expectedListOfUnmatchedGlAccounts));
    }

    /*@Test
    public void givenListOfOfbizGlAccountSummaries_whenNumberOfGlAccountsInSummariesIsEqualToNumberOfGlAccountInMemory_thenItShouldReturnTheNumberOfGlAccountsAsEqual(){
        assertThat(ofbizGlAccountSummaries.size(), is(glAccountMap.size()));
    }*/

    /*@Test
    public void givenListOfMapOfOfbizGlAccountSummariesContainingThreeEntries_whenNumberOfGlAccountIsGreaterThenThree_itShouldReturnEntriesInOfbizGlAccountSummariesShouldBeSameAsNumberOfGlAccounts(){
        GlAccount glAccount = new GlAccount("1234567");
        glAccountMap.put("1234567", glAccount);
        assertNotEquals(ofbizGlAccountSummaries.size(), is(glAccountMap.size()));
    }*/

    @Test(expected=NullPointerException.class)
    public void givenListOfbizGlAccountSummaries_whenAddingNullAmountTOGlAccount_itShouldThrowNullPointerException(){
        addCreditDebitToGlAccount(glAccountFirst, null, new BigDecimal(1234));
        expectedListOfUnmatchedGlAccounts.clear();
        expectedListOfUnmatchedGlAccounts.add(prepareExpectedListOfUnmatchedGlAccounts("1234",new BigDecimal(1234), new BigDecimal(2468)));
        List<String> actualListOfUnmatchedGlAccounts = PastelExportService.FindUnmatchedGlAccountsFromOfbizGlAccountSummaries(ofbizGlAccountSummaries);
        assertThat(actualListOfUnmatchedGlAccounts.size(), is(expectedListOfUnmatchedGlAccounts.size()));
        assertThat(actualListOfUnmatchedGlAccounts, is(expectedListOfUnmatchedGlAccounts));

    }

    @Test
    public void givenListOfbizGlAccountSummaries_whenSomeAmountIsDebitFromAndCreditToGLAccount_thenItShouldReturnOneUnmatchedGlAccount(){
        addCreditDebitToGlAccount(glAccountFirst, new BigDecimal(1234), new BigDecimal(12345));
        expectedListOfUnmatchedGlAccounts.clear();
        expectedListOfUnmatchedGlAccounts.add(prepareExpectedListOfUnmatchedGlAccounts("1234",new BigDecimal(2468), new BigDecimal(1234)));
        List<String> actualListOfUnmatchedGlAccounts = PastelExportService.FindUnmatchedGlAccountsFromOfbizGlAccountSummaries(ofbizGlAccountSummaries);
        assertThat(actualListOfUnmatchedGlAccounts.size(), is(expectedListOfUnmatchedGlAccounts.size()));
        assertNotEquals(actualListOfUnmatchedGlAccounts, is(expectedListOfUnmatchedGlAccounts));
    }

    @Test
    public void givenListOfbizGlAccountSummaries_whenNegativeDebitAmountIsAddedToAGlAccount_thenItShouldReturnOneUnmatchedGlAccountAndItShouldAddTheAmountToTheExistingDebitAmountInsteadOfSubstractingIt(){
        addCreditDebitToGlAccount(glAccountFirst, new BigDecimal(1234), new BigDecimal(-1234));
        expectedListOfUnmatchedGlAccounts.clear();
        expectedListOfUnmatchedGlAccounts.add(prepareExpectedListOfUnmatchedGlAccounts("1234",new BigDecimal(2468), new BigDecimal(0)));
        List<String> actualListOfUnmatchedGlAccounts = PastelExportService.FindUnmatchedGlAccountsFromOfbizGlAccountSummaries(ofbizGlAccountSummaries);
        assertNotEquals(actualListOfUnmatchedGlAccounts.size(), is(0));
        assertNotEquals(actualListOfUnmatchedGlAccounts, is(expectedListOfUnmatchedGlAccounts));
    }

    @Test
    public void givenListOfbizGlAccountSummaries_whenNegativeDebitAmountIsAddedToAGlAccount_thenItShouldAddTheAmountToTheExistingDebitAmountInsteadOfSubstractingIt(){
        addCreditDebitToGlAccount(glAccountFirst, new BigDecimal(1234), new BigDecimal(-1234));
        expectedListOfUnmatchedGlAccounts.clear();
        expectedListOfUnmatchedGlAccounts.add(prepareExpectedListOfUnmatchedGlAccounts("1234",new BigDecimal(2468), new BigDecimal(2468)));
        List<String> actualListOfUnmatchedGlAccounts = PastelExportService.FindUnmatchedGlAccountsFromOfbizGlAccountSummaries(ofbizGlAccountSummaries);
        assertThat(actualListOfUnmatchedGlAccounts.size(), is(1));
        assertThat(actualListOfUnmatchedGlAccounts, is(expectedListOfUnmatchedGlAccounts));
    }


    public void addCreditDebitToGlAccount(GlAccount glAccount,BigDecimal credit, BigDecimal debit){
        glAccount.addCreditValue(credit);
        glAccount.addDebitValue(debit);
    }

    public String prepareExpectedListOfUnmatchedGlAccounts(String glAccountId,BigDecimal credit, BigDecimal debit){
        return "glAccountId= " + glAccountId +  ", debitAmount=" + debit + ", creditAmount=" + credit;
    }
}
