package org.ofbiz.accounting.util;

import com.pastel.PastelExport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.ofbiz.accounting.TransactionHelper;
import org.ofbiz.accounting.ledger.dovepayrollimport.JournalEntry;
import org.ofbiz.accounting.ledger.dovepayrollimport.JournalEntryItem;
import org.ofbiz.accounting.ledger.dovepayrollimport.JournalTransaction;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



/**
 * Created by ASUS on 13-Nov-14.
 */
public class DoveImport {

    public static String uploadDoveImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader bufferedReader = request.getReader();
        GenericValue salaryGv =null;
        String message = null;
        try {
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            if (bufferedReader.readLine() == null) {
                request.setAttribute("_EVENT_MESSAGE_", "File cannot be empty");
            }
            JournalEntry journalEntry = new JournalTransaction().readTheJournalListingAndMapToJournalEntry(bufferedReader);
            String salaryId = delegator.getNextSeqId("Salary");
            Map<String,Object> salaryDetailsMap = UtilMisc.toMap("salaryId", salaryId, "transactionDate", journalEntry.transactionDate,"status","IMPORTED");
            salaryGv = delegator.makeValue("Salary",salaryDetailsMap);
            delegator.create(salaryGv);
            if (isDovePayrollImported(journalEntry, delegator,salaryId)) {
                request.setAttribute("_EVENT_MESSAGE_", "Imported successfully");
                message = "success";
            }
            else {
                message = "error";
            }
        }catch (Exception ex){
            if (salaryGv!=null){
                try {
                    salaryGv.remove();
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }
            }
            request.setAttribute("_EVENT_MESSAGE_", ex.getMessage());
            message = "error";
        }
        return message;
    }

    public static boolean isDovePayrollImported(JournalEntry journalEntry, Delegator delegator,String salaryId) throws Exception {
        for (JournalEntryItem journalEntryItem : journalEntry.items) {
            String salaryItemId = delegator.getNextSeqId("SalaryItem");
            Map<String,Object>  journalEntryItemMap =   UtilMisc.toMap("salaryItemId", salaryItemId, "salaryId",salaryId,"payrollCode",journalEntryItem.accountId,"glAccountId",journalEntryItem.glAccountId,"transactionType",journalEntryItem.type,"amount",new BigDecimal(journalEntryItem.amount),"payrollName",journalEntryItem.moneyName);
            GenericValue salaryItemGv =  delegator.makeValue("SalaryItem", journalEntryItemMap);
            delegator.create(salaryItemGv);
        }
        return true;
    }

    public static Map<String,Object> commitDoveTransaction(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        String salaryId = (String)context.get("salaryId");
        GenericValue salaryGv = delegator.findOne("Salary", UtilMisc.toMap("salaryId", salaryId), false);
        List<GenericValue> salaryItemGvs =  delegator.findByAnd("SalaryItem", UtilMisc.toMap("salaryId", salaryId));
        List<Map<String,Object>> journalEntryItemsList = new ArrayList<Map<String, Object>>();
        String currency = TransactionHelper.getCurrencyUomIdForCompany(delegator, "Company");
        for (GenericValue salaryItemGv : salaryItemGvs) {
            Map<String, Object> journalEntryItemMap = TransactionHelper.createTransactionEntry(salaryItemGv.getBigDecimal("amount").doubleValue(), salaryItemGv.get("glAccountId").toString(), null, salaryItemGv.get("transactionType").toString(), currency, "salary of "+salaryGv.getTimestamp("transactionDate"), "Company","");
            journalEntryItemsList.add(journalEntryItemMap);
        }
        Boolean isTransactionEntriesCreated = TransactionHelper.createTransactionAndTransactionEntriesForSalary(dctx, delegator, journalEntryItemsList, salaryGv.getTimestamp("transactionDate"), "SALARY",salaryId);
        if (isTransactionEntriesCreated){
            salaryGv.put("status","COMMITTED");
            delegator.store(salaryGv);
            return ServiceUtil.returnSuccess("Committed Successfully");
        }
        else {
            return ServiceUtil.returnFailure("Failed to Commit");
        }
    }

    public static Map<String,Object> deleteDovePayrollDetails(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        String salaryId = (String)context.get("salaryId");
        int deleteSelectedSalaryDetails = delegator.removeByAnd("Salary", UtilMisc.toMap("salaryId", salaryId));
        int deleteSelectedSalaryItemDetails =   delegator.removeByAnd("SalaryItem", UtilMisc.toMap("salaryId", salaryId));
        if (deleteSelectedSalaryDetails !=0 && deleteSelectedSalaryItemDetails!=0){
            return ServiceUtil.returnSuccess("Deleted Successfully");
        }
        else {
            return ServiceUtil.returnFailure("Failed to delete");
        }
    }

    public static String pastelExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OutputStream outputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            System.out.println("pastelExport in doveimport....");
            response.setContentType("application/zip");
            response.addHeader("Content-Disposition", "attachment; filename =" + "pastelExport.zip");
            outputStream = response.getOutputStream();
            zipOutputStream = new ZipOutputStream(outputStream);
            String exportThruDate =  request.getParameter("thruDate");
            System.out.println("pastelExport exportThruDate"+exportThruDate);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            /*LocalDate fromDate = LocalDate.parse(exportThruDate);
            System.out.println("pastelExport fromDate"+fromDate.toDate());*/
            String filePathName = PastelExport.Export(df.parse(exportThruDate));
            File file = new File(filePathName);
            File[] fileArray = file.listFiles();
            for (File files : fileArray) {
                if (isMisMatchLogFileFound(files)){
                    System.out.println(file.getName()+" file has been mailed ");
                }
                InputStream inputStream = FileUtils.openInputStream(files);
                ZipEntry zipEntry = new ZipEntry(files.getName());
                zipOutputStream.putNextEntry(zipEntry);
                IOUtils.copy(inputStream, zipOutputStream);
                zipOutputStream.closeEntry();
            }
            outputStream.flush();
        }
        catch (Exception ex){
            return "error";
        }
        if(zipOutputStream!=null){
            zipOutputStream.flush();
            zipOutputStream.finish();
            zipOutputStream.close();
        }
        outputStream.close();
        response.flushBuffer();
        return "success";
    }

    private static boolean isMisMatchLogFileFound(File file){
        boolean isMisMatchFileFound = false;
        if (file.getName().equals("mismatch.log")){
            isMisMatchFileFound =  true;
        }
        return isMisMatchFileFound;
    }

    private static Date dateFormat(String thruDate) throws ParseException {
        Date exportThruDate = new SimpleDateFormat("yyyy-MM-dd").parse(thruDate);
        return exportThruDate;
    }

}
