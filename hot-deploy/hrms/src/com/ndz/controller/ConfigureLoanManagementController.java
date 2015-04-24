package com.ndz.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.component.party.ClaimTypeRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.component.party.EmployeePositionTypeRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class ConfigureLoanManagementController extends GenericForwardComposer {

    private static final Listbox loanType = null;

    @SuppressWarnings({"unchecked", "deprecation"})
    public void doAfterCompose(Component allocateLoanWindow) throws Exception {
        super.doAfterCompose(allocateLoanWindow);

        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        EntityCondition condition = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "ELOAN_TYPE");

        EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");

        Set loanTypeToDisplay = new HashSet();
        Set employeePositionTypeToDisplay = new HashSet();
        Set currencyTypeToDisplay = new HashSet();

        loanTypeToDisplay.add("enumTypeId");
        loanTypeToDisplay.add("enumId");
        loanTypeToDisplay.add("description");

        employeePositionTypeToDisplay.add("emplPositionTypeId");
        employeePositionTypeToDisplay.add("description");

        currencyTypeToDisplay.add("uomId");
        currencyTypeToDisplay.add("description");

        List<GenericValue> loanType = delegator.findList("Enumeration", condition, loanTypeToDisplay, null, null, false);
        loanType.add(0, null);
        @SuppressWarnings("unused")
        Object loanTypeArray = loanType.toArray(new GenericValue[loanType.size()]);
        SimpleListModel loanLimitList = new SimpleListModel(loanType);

        Listbox searchloanType = (Listbox) allocateLoanWindow.getFellow("allocateLoanType");
        searchloanType.setModel(loanLimitList);
        searchloanType.setItemRenderer(new ClaimTypeRenderer());

        List<GenericValue> employeePositionType = delegator.findList("EmplPositionType", null, employeePositionTypeToDisplay, null, null, false);
        employeePositionType.add(0, null);
        Object employeePositionTypeArray = employeePositionType.toArray(new GenericValue[employeePositionType.size()]);
        SimpleListModel employeePositionTypeList = new SimpleListModel(employeePositionType);
        Listbox employeePositionTypeId = (Listbox) allocateLoanWindow.getFellow("allocatePositionType");
        employeePositionTypeId.setModel(employeePositionTypeList);
        employeePositionTypeId.setItemRenderer(new EmployeePositionTypeRenderer());

		/*List<GenericValue> currency = delegator.findList("Uom", currencyCondition, currencyTypeToDisplay, null, null, false);
        currency.add(0, null);
		Object currencyArray = currency.toArray(new GenericValue[currency.size()]);
		SimpleListModel currencyList = new SimpleListModel(currency);

		Listbox applyLoanCurrency = (Listbox) allocateLoanWindow.getFellow("allocateCurrencyType");
		applyLoanCurrency.setModel(currencyList);
		applyLoanCurrency.setItemRenderer(new CurrencyRenderer());*/

    }

    public static void allocateLoanLimit(Event event) {

        try {
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            Component allocateLoanWindow = event.getTarget();
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

            Listitem loanTypeInput = ((Listbox) allocateLoanWindow.getFellow("allocateLoanType")).getSelectedItem();
            String loanType = (String) loanTypeInput.getValue();

            Listitem emplPosTypeInput = ((Listbox) allocateLoanWindow.getFellow("allocatePositionType")).getSelectedItem();
            String emplPosTypeId = UtilValidate.isNotEmpty(emplPosTypeInput) ? (String) emplPosTypeInput.getValue() : null;
            Integer amountType = ((Intbox) allocateLoanWindow.getFellow("allocateLoanAmount")).getValue();
            Double amount = amountType.doubleValue();
            //Listitem currencyType = ((Listbox) allocateLoanWindow.getFellow("allocateCurrencyType")).getSelectedItem();
            //String currency = (String) currencyType.getValue();
            String currency = (String) ((Textbox) allocateLoanWindow.getFellow("allocateCurrencyType")).getValue();
            String hrComment = ((Textbox) allocateLoanWindow.getFellow("allocateHRComment")).getValue();

            Date fromDateInput = (Date) ((Datebox) allocateLoanWindow.getFellow("allocateFromDate")).getValue();

            Date thruDateInput = (Date) ((Datebox) allocateLoanWindow.getFellow("allocateThruDate")).getValue();

            BigDecimal interestType = ((Decimalbox) allocateLoanWindow.getFellow("interestPerAnnum")).getValue();
            Double interest = interestType.doubleValue();

            Integer periodType = ((Intbox) allocateLoanWindow.getFellow("tenureOfLoan")).getValue();
            Double period = periodType.doubleValue();

            String exprienceType = ((Textbox) allocateLoanWindow.getFellow("exprienceRequired")).getValue();
            Double exprience = new Double(exprienceType);

            java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());
            java.sql.Date thruDate = new java.sql.Date(thruDateInput.getTime());
            Date currSysDate = new Date();
            java.sql.Date currDate = new java.sql.Date(currSysDate.getTime());
            
            Radio emplyoeeTypeGroup = ((Radiogroup) allocateLoanWindow.getFellow("emp_administration")).getSelectedItem();
	        String employeeType = (String)emplyoeeTypeGroup.getValue();

	        Comboitem positionCategoriesList = ((Combobox) allocateLoanWindow.getFellow("positionCategories")).getSelectedItem();
	        String positionCategory = (String) positionCategoriesList.getValue();


            Map loanLimit = UtilMisc.toMap("userLogin", userLogin, "loanType", loanType, "loanAmount", amount, "currencyUomId", currency, "emplPositionTypeId", emplPosTypeId, "hr_comment", hrComment, "interest",
                    interest, "hr_period", period, "expYrs", exprience, "fromDate", fromDate, "thruDate", thruDate,"employeeType",employeeType,"positionCategory",positionCategory);
            Map result = null;
            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

            List<GenericValue> checkLoanLimitList = null;
            if(UtilValidate.isEmpty(emplPosTypeId)){
            	checkLoanLimitList = delegator.findByAnd("ELoanLimit", UtilMisc.toMap("loanType",loanType,
                        "employeeType",employeeType,
                        "positionCategory",positionCategory),null,false);
            }else{
            	checkLoanLimitList = delegator.findByAnd("ELoanLimit", UtilMisc.toMap("loanType",loanType,
                        "emplPositionTypeId",emplPosTypeId,
                        "employeeType",employeeType,
                        "positionCategory",positionCategory),null,false);
            }
            Timestamp providedFromDate = new Timestamp(fromDateInput.getTime());
            Timestamp providedThruDate = new Timestamp(thruDateInput.getTime());
            
            for(GenericValue emplLoanLimit :checkLoanLimitList){
                Timestamp existingFromDate = new Timestamp(emplLoanLimit.getDate("fromDate").getTime());
                Timestamp existingThruDate = new Timestamp(emplLoanLimit.getDate("thruDate").getTime());
                if(existingFromDate.equals(providedFromDate) || existingThruDate.equals(providedThruDate)){
                    Messagebox.show("Allocate Loan Limit Already Exists in the Range","Error",1,null);
                    return;
                }else if(UtilDateTime.isTimestampWithinRange(providedThruDate,existingFromDate,existingThruDate)){
                    Messagebox.show("Allocate Loan Limit Already Exists in the Range","Error",1,null);
                    return;
                }else if(UtilDateTime.isTimestampWithinRange(providedFromDate,existingFromDate,existingThruDate)){
                    Messagebox.show("Allocate Loan Limit Already Exists in the Range","Error",1,null);
                    return;
                }
            }
            result = dispatcher.runSync("createELoanLimitService", loanLimit);
            String limitId = (String) result.get("limitId");
            Messagebox.show("Loan Limit allocated successfully", "Success", 1, null);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showLoanLimitWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

        Component editLoanLimitWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

        String employeeID = userLogin.getString("partyId");
        String userLoginID = userLogin.getString("userLoginId");
        String loanId = gv.getString("limitId");
        String loanType = gv.getString("loanType");
        String emplPosTypeId = gv.getString("emplPositionTypeId");
        String currency = gv.getString("currencyUomId");
        String hrComment = gv.getString("hr_comment");
        Double amountType = gv.getDouble("loanAmount");
        Integer amount = amountType.intValue();
        String updatedBy = gv.getString("updatedBy");
        BigDecimal interest = gv.getBigDecimal("interest");
        java.sql.Date fromDateType = gv.getDate("fromDate");
        java.sql.Date thruDateType = gv.getDate("thruDate");
        Double periodType = gv.getDouble("hr_period");
        String employeeType = gv.getString("employeeType");
        String positionCategory = gv.getString("positionCategory");

        Integer period = null;
        if (periodType != null) {
            period = periodType.intValue();
        }
        Double exp = gv.getDouble("expYrs");
        String exprience = null;
        if (exp != null) {
            exprience = exp.toString();
        }

        Window win = (Window) Executions.createComponents("/zul/loanManagement/updateLoanLimit.zul", null, null);
        win.setTitle("Edit Loan Limit");

        Label employeeId = (Label) win.getFellow("employeeId");
        employeeId.setValue(employeeID);
        EntityCondition employeePositionTypeCondition = EntityCondition.makeCondition("hasTable", EntityOperator.EQUALS, "N");
        Set employeePositionTypeToDisplay = new HashSet();
        employeePositionTypeToDisplay.add("emplPositionTypeId");
        employeePositionTypeToDisplay.add("description");
        List<GenericValue> employeePositionTypes = delegator.findList("EmplPositionType", null, employeePositionTypeToDisplay, null, null, false);

        Listbox posTypeId = (Listbox) win.getFellow("posTypeId");
        Listitem posTypeItem = new Listitem();
		posTypeItem.setParent(posTypeId);
		
       for (int i = 0; i < employeePositionTypes.size(); i++) {
            GenericValue posType = employeePositionTypes.get(i);
            String itemLabel = posType.getString("emplPositionTypeId");
            posTypeId.appendItemApi(posType.getString("description"), itemLabel);
            if (itemLabel.equals(emplPosTypeId)) {
                posTypeId.setSelectedIndex(i+1);
            }
        }

        Label savedLoanId = (Label) win.getFellow("limitId");
        savedLoanId.setValue(loanId);
        EntityCondition condition = EntityConditionFunction.makeCondition("enumTypeId", EntityComparisonOperator.EQUALS, "ELOAN_TYPE");
        
        
        if("Administrative".equals(employeeType)){
            Radio administrativeEmployeeType = (Radio)win.getFellow("emp_administration_administrative");
            administrativeEmployeeType.setSelected(true);
        }else if("Non-Administrative".equals(employeeType)){
            Radio nonAdministrativeEmployeeType = (Radio)win.getFellow("emp_administration_non_administrative");
            nonAdministrativeEmployeeType.setSelected(true);
        }

        Combobox savedPositionCategory =  (Combobox)win.getFellow("positionCategories");
        savedPositionCategory.setValue(positionCategory);


        Set loanLimitToDisplay = new HashSet();

        loanLimitToDisplay.add("enumTypeId");
        loanLimitToDisplay.add("enumId");
        loanLimitToDisplay.add("description");

        List<GenericValue> loanTypes = delegator.findList("Enumeration", condition, loanLimitToDisplay, null, null, false);

        Listbox savedloanType = (Listbox) win.getFellow("loanType");

        for (int i = 0; i < loanTypes.size(); i++) {
            GenericValue claim = loanTypes.get(i);
            String itemLabel = claim.getString("enumId");
            savedloanType.appendItemApi(claim.getString("description"), itemLabel);
            if (itemLabel.equals(loanType)) {
                savedloanType.setSelectedIndex(i);
            }
        }

		/*Listbox currencyListBox = (Listbox) win.getFellow("currencyListBox");

		EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");
		Set currencyTypeToDisplay = new HashSet();
		currencyTypeToDisplay.add("uomId");
		currencyTypeToDisplay.add("description");
		List<GenericValue> currencyList = delegator.findList("Uom", currencyCondition, currencyTypeToDisplay, null, null, false);

		for (int i = 0; i < currencyList.size(); i++) {
			GenericValue posType = currencyList.get(i);
			String itemLabel = posType.getString("uomId");
			currencyListBox.appendItemApi(posType.getString("description"), itemLabel);
			if (itemLabel.equals(currency)) {
				currencyListBox.setSelectedIndex(i);
			}
		}*/
        Datebox savedFromDate = (Datebox) win.getFellow("fromDate");
        savedFromDate.setValue(fromDateType);

        Datebox savedThruDate = (Datebox) win.getFellow("thruDate");
        savedThruDate.setValue(thruDateType);

        Intbox savedLoanAmount = (Intbox) win.getFellow("amount");
        savedLoanAmount.setValue(amount);
        Decimalbox savedInterest = (Decimalbox) win.getFellow("interest");
        savedInterest.setValue(interest);
        Intbox savedPeriod = (Intbox) win.getFellow("period");
        savedPeriod.setValue(period);
        Textbox savedExprience = (Textbox) win.getFellow("exprience");
        savedExprience.setValue(exprience);
        Textbox allocateHRComment = (Textbox) win.getFellow("allocateHRComment");
        allocateHRComment.setValue(hrComment);
        win.doModal();
    }

    public static void updateLoanLimit(Event event) {

        try {
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            Component editLoanLimitWindow = event.getTarget();
            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
            String employeeId = userLogin.getString("partyId");

            Integer amountType = ((Intbox) editLoanLimitWindow.getFellow("amount")).getValue();
            Double amount = amountType.doubleValue();

            BigDecimal interestType = ((Decimalbox) editLoanLimitWindow.getFellow("interest")).getValue();
            Double interest = interestType.doubleValue();

            Integer periodType = ((Intbox) editLoanLimitWindow.getFellow("period")).getValue();
            Double period = periodType.doubleValue();

            Date fromDateType = (Date) ((Datebox) editLoanLimitWindow.getFellow("fromDate")).getValue();
            Date thruDateType = (Date) ((Datebox) editLoanLimitWindow.getFellow("thruDate")).getValue();

            java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());
            java.sql.Date thruDate = new java.sql.Date(thruDateType.getTime());

            String exprienceType = ((Textbox) editLoanLimitWindow.getFellow("exprience")).getValue();
            Double exprience = new Double(exprienceType);
            //Listitem currencyListBox = ((Listbox) editLoanLimitWindow.getFellow("currencyListBox")).getSelectedItem();
            //String currency = (String) currencyListBox.getValue();
            String currency = (String) ((Textbox) editLoanLimitWindow.getFellow("currencyListBox")).getValue();
            Listitem loanTypeListBox = ((Listbox) editLoanLimitWindow.getFellow("loanType")).getSelectedItem();
            String loanType = (String) loanTypeListBox.getValue();

            Listitem posTypeIdBox = ((Listbox) editLoanLimitWindow.getFellow("posTypeId")).getSelectedItem();
            String posTypeId = UtilValidate.isNotEmpty(posTypeIdBox) ? (String) posTypeIdBox.getValue() : null;

            String comment = ((Textbox) editLoanLimitWindow.getFellow("allocateHRComment")).getValue();
            
            Radio emplyoeeTypeGroup = ((Radiogroup) editLoanLimitWindow.getFellow("emp_administration")).getSelectedItem();
	        String employeeType = (String)emplyoeeTypeGroup.getValue();

	        Comboitem positionCategoriesList = ((Combobox) editLoanLimitWindow.getFellow("positionCategories")).getSelectedItem();
	        String positionCategory = (String) positionCategoriesList.getValue();


            String limitId = ((Label) editLoanLimitWindow.getFellow("limitId")).getValue();
            Map updateELoanLimit = UtilMisc.toMap("userLogin", userLogin, "loanType", loanType, "loanAmount", amount, "currencyUomId", currency, "limitId", limitId, "hr_period", period, "interest", interest,
                    "emplPositionTypeId", posTypeId, "expYrs", exprience, "fromDate", fromDate, "thruDate", thruDate, "hr_comment", comment,
                    "partyId", userLogin.getString("partyId"),"employeeType",employeeType,"positionCategory",positionCategory);
            Map result = null;
            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            List<GenericValue> checkELoanLimit = delegator.findByAnd("ELoanLimit", UtilMisc.toMap("emplPositionTypeId", posTypeId, "loanType", loanType
            		,"employeeType",employeeType,"positionCategory",positionCategory));
            
          /*  if (UtilValidate.isNotEmpty(checkELoanLimit)) {
                String existslimitId = checkELoanLimit.get(0).getString("limitId");
                if (existslimitId.equals(limitId)) {
                    result = dispatcher.runSync("updateELoanLimitService", updateELoanLimit);
                    Messagebox.show("Loan Limit updated successfully", "Success", 1, null);
                    Events.postEvent("onClick$searchPerCompany", editLoanLimitWindow.getPage().getFellow("searchPanel"), null);
                    editLoanLimitWindow.detach();
                } else {
                    Events.postEvent("onClick$searchPerCompany", editLoanLimitWindow.getPage().getFellow("searchPanel"), null);
                    Messagebox.show("Allocate Loan Limit Already Exists", "Error", 1, null);
                }

            } else {
                result = dispatcher.runSync("updateELoanLimitService", updateELoanLimit);
                Messagebox.show("Loan Limit updated successfully", "Success", 1, null);
                Events.postEvent("onClick$searchPerCompany", editLoanLimitWindow.getPage().getFellow("searchPanel"), null);
                editLoanLimitWindow.detach();
            }
*/
      if (UtilValidate.isNotEmpty(checkELoanLimit)) {
            	Timestamp providedFromDate = new Timestamp(fromDate.getTime());
            	Timestamp providedThruDate = new Timestamp(thruDate.getTime());
            	for(GenericValue checkELoanLimitGv :checkELoanLimit){
                    String existslimitId = checkELoanLimitGv.getString("limitId");
                    Timestamp existingFromDate = new Timestamp(checkELoanLimitGv.getDate("fromDate").getTime());
                    Timestamp existingThruDate = new Timestamp(checkELoanLimitGv.getDate("thruDate").getTime());
                    if(!existslimitId.equals(limitId) && (UtilDateTime.isTimestampWithinRange(providedFromDate,existingFromDate,existingThruDate)
                            || UtilDateTime.isTimestampWithinRange(providedThruDate,existingFromDate,existingThruDate) || existingFromDate.equals(providedThruDate)
                            || existingThruDate.equals(providedFromDate))) {
                        Events.postEvent("onClick$searchPerCompany", editLoanLimitWindow.getPage().getFellow("searchPanel"), null);
                        Messagebox.show("Allocate Loan Limit Already Exists", "Error", 1, null);
                        return;
                    }
                }
            	 result = dispatcher.runSync("updateELoanLimitService", updateELoanLimit);
                 Messagebox.show("Loan Limit updated successfully", "Success", 1, null);
                 Events.postEvent("onClick$searchPerCompany", editLoanLimitWindow.getPage().getFellow("searchPanel"), null);
                 editLoanLimitWindow.detach();
           }
          else {
                result = dispatcher.runSync("updateELoanLimitService", updateELoanLimit);
                Messagebox.show("Loan Limit updated successfully", "Success", 1, null);
                Events.postEvent("onClick$searchPerCompany", editLoanLimitWindow.getPage().getFellow("searchPanel"), null);
                editLoanLimitWindow.detach();
            }  
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteLoanLimit(Event event, GenericValue gv, final Button btn) throws InterruptedException, GenericEntityException {

        final String loanLimitId = (String) gv.getString("limitId");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

        Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {

                    try {
                        delegator.removeByAnd("ELoanLimit", UtilMisc.toMap("limitId", loanLimitId));
                        Events.postEvent("onClick", btn, null);
                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        Messagebox.show("Loan Limit deleted successfully", "Success", 1, null);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        });

    }
}
