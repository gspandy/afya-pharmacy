package com.ndz.controller;

import com.hrms.composer.HrmsComposer;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

/**
 * Created by ASUS on 16-Oct-14.
 */
@SuppressWarnings("deprecation")
public class EmiLoanInfoController extends HrmsComposer {

    private List<Map> employeeLoanEmiInfoList =  new ArrayList();
    String employeeId = (String) Executions.getCurrent().getArg().get("employeeId");
    String loanId = (String) Executions.getCurrent().getArg().get("loanId");

    @Override
    protected void executeAfterCompose(Component comp) throws Exception {
        List orderBy = Arrays.asList(new String[]{"fromDate"});
        List<GenericValue> EmployeeLoanEmiInfoGv = delegator.findByAnd("EmployeeLoanEmiInfo", UtilMisc.toMap("employeeId",employeeId,"loanId",loanId,"toBePaid","Y"),orderBy,false);

        for(GenericValue employeeLoanEmiInfo:EmployeeLoanEmiInfoGv){
            employeeLoanEmiInfoList.add(genericValueToMap(employeeLoanEmiInfo));
        }
    }


    public void addNewEmi(){
        Map emiGv = employeeLoanEmiInfoList.get(0);
        Map newEmi= UtilMisc.toMap("loanId", emiGv.get("loanId"), "amount", emiGv.get("amount"), "loanName", emiGv.get("loanName"), "employeeId", employeeId, "loanType", emiGv.get("loanType"), "toBePaid", "Y", "fromDate", null, "thruDate", null, "disabled", false);
        employeeLoanEmiInfoList.add(0,newEmi);
    }


    public void removeEmi(Map itemToRemove){
        employeeLoanEmiInfoList.remove(itemToRemove);
    }

    public List<Map> getEmployeeLoanEmiInfoList() {
        return employeeLoanEmiInfoList;
    }

    public Map genericValueToMap(GenericValue gv){
        Map gvMap = new HashMap();
        for(String key :gv.keySet()){
            if(!(key.equals("lastUpdatedStamp")||key.equals("lastUpdatedTxStamp")||key.equals("createdStamp")||key.equals("createdTxStamp"))){
                gvMap.put(key,gv.get(key));
            }
        }
        gvMap.put("disabled",true);
        return gvMap;
    }

    public void updateEmi(Window loanEmiInfoWindow) throws InterruptedException {
        try {
            GenericValue loanHeadGv = delegator.findOne("ELoanHead",UtilMisc.toMap("loanId",loanId),false);
            BigDecimal loanAmount = (UtilValidate.isNotEmpty(loanHeadGv.getBigDecimal("acceptedAmount")))?loanHeadGv.getBigDecimal("acceptedAmount"):loanHeadGv.getBigDecimal("loanAmount");
            if(!validate(loanAmount)){
                return;
            }
            for(Map employeeLoanEmiInfo:employeeLoanEmiInfoList){
                String emiId = (String)employeeLoanEmiInfo.get("emiId");
                Date fromDate = new Date(((java.util.Date)employeeLoanEmiInfo.get("fromDate")).getTime());
                Date thruDate = new Date(((java.util.Date)employeeLoanEmiInfo.get("thruDate")).getTime());
                employeeLoanEmiInfo.put("fromDate",fromDate);
                employeeLoanEmiInfo.put("thruDate",thruDate);
                GenericValue employeeLoanEmiInfoGv = delegator.makeValidValue("EmployeeLoanEmiInfo",employeeLoanEmiInfo);
                if(UtilValidate.isEmpty(emiId)){
                    employeeLoanEmiInfoGv.put("emiId",delegator.getNextSeqId("EmployeeLoanEmiInfo"));
                    delegator.create(employeeLoanEmiInfoGv);
                }else{
                    delegator.store(employeeLoanEmiInfoGv);
                }
            }
            Messagebox.show("EMI updated successfully", "Success", 1, null);
            loanEmiInfoWindow.detach();

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public boolean validate(BigDecimal loanAmount) throws InterruptedException {
        BigDecimal totalLoanAmount = BigDecimal.ZERO;
        for(Map employeeLoanEmiInfo:employeeLoanEmiInfoList){
            Date fromDate = null;
            Date thruDate = null;
            if(UtilValidate.isNotEmpty(employeeLoanEmiInfo.get("fromDate"))&& UtilValidate.isNotEmpty(employeeLoanEmiInfo.get("thruDate"))){
                fromDate = new Date(((java.util.Date)employeeLoanEmiInfo.get("fromDate")).getTime());
                thruDate = new Date(((java.util.Date)employeeLoanEmiInfo.get("thruDate")).getTime());
            }
            BigDecimal amount = (BigDecimal)employeeLoanEmiInfo.get("amount");
            if(((String)employeeLoanEmiInfo.get("toBePaid")).equals("Y")){
                if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)){
                    if(UtilDateTime.isPastMonth(fromDate) || UtilDateTime.isPastMonth(thruDate)){
                        Messagebox.show("From Date or Thru Date of row number "+employeeLoanEmiInfoList.indexOf(employeeLoanEmiInfo)+" is past", "Error", 1, null);
                        return false;
                    }
                    if(thruDate.before(fromDate)){
                        Messagebox.show("Thru Date is before From Date in row number "+employeeLoanEmiInfoList.indexOf(employeeLoanEmiInfo), "Error", 1, null);
                        return false;
                    }
                }else{
                    Messagebox.show("From Date or Thru Date of row number "+employeeLoanEmiInfoList.indexOf(employeeLoanEmiInfo)+" is empty", "Error", 1, null);
                    return false;
                }
                if(UtilValidate.isNotEmpty(amount)){
                    totalLoanAmount = totalLoanAmount.add(amount);
                }else{
                    Messagebox.show("EMI amount of row number "+employeeLoanEmiInfoList.indexOf(employeeLoanEmiInfo)+" is empty", "Error", 1, null);
                    return false;
                }
            }
        }
        totalLoanAmount =totalLoanAmount.setScale(0,BigDecimal.ROUND_FLOOR);
        if(loanAmount.compareTo(totalLoanAmount)!=0){
            Messagebox.show("Loan Amount ["+loanAmount+"] is not equal to the total of the EMIs["+totalLoanAmount+"]. Please add or adjust accordingly", "Error", 1, null);
            return false;
        }
        return true;
    }
}
