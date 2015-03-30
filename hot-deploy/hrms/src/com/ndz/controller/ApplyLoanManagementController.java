package com.ndz.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.python.antlr.ast.Str;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.api.Comboitem;
import com.ndz.component.party.ClaimTypeRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.CurrencyFormatter;
import com.ndz.zkoss.util.HrmsInfrastructure;

@SuppressWarnings("serial")
public class ApplyLoanManagementController extends GenericForwardComposer {

    @SuppressWarnings({ "deprecation", "unchecked" })
    public void doAfterCompose(Component applyLoanWindow) throws Exception {
        super.doAfterCompose(applyLoanWindow);

        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        EntityCondition condition = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "ELOAN_TYPE");
        Set loanTypeToDisplay = new HashSet();
        loanTypeToDisplay.add("enumTypeId");
        loanTypeToDisplay.add("enumId");
        loanTypeToDisplay.add("description");
        List loanType = delegator.findList("Enumeration", condition, loanTypeToDisplay, null, null, false);
        loanType.add(0, null);
        @SuppressWarnings("unused")
        Object loanTypeArray = loanType.toArray(new GenericValue[loanType.size()]);
        SimpleListModel loanTypeList = new SimpleListModel(loanType);

        Listbox searchLoanType = (Listbox) applyLoanWindow.getFellow("applyLoanType");
        searchLoanType.setModel(loanTypeList);
        searchLoanType.setItemRenderer(new ClaimTypeRenderer());

    }
    private static void getCurrency(Component comp,String loanType,String emplPositionId, GenericValue personGV) throws GenericEntityException{
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        List<GenericValue> eLoanLimit = delegator.findByAnd("ELoanLimit", UtilMisc.toMap("emplPositionTypeId",emplPositionId,"loanType",loanType,
        		"positionCategory",personGV.getString("positionCategory"),"employeeType",personGV.getString("employeeType")));
        if(UtilValidate.isEmpty(eLoanLimit)){
        	eLoanLimit = delegator.findByAnd("ELoanLimit", UtilMisc.toMap("loanType",loanType,
            		"positionCategory",personGV.getString("positionCategory"),"employeeType",personGV.getString("employeeType")));
        }
        GenericValue eLoanLimitGV = EntityUtil.getFirst(eLoanLimit);
        if(UtilValidate.isNotEmpty(eLoanLimitGV)){
            String currencyUomId = eLoanLimitGV.getString("currencyUomId");

            List<GenericValue> currency = delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId","CURRENCY_MEASURE","uomId",currencyUomId));
            String currencyDesc = currency.get(0).getString("description");
            Label applyLoanCurrencyLabel = (Label) comp.getFellow("applyLoanCurrencyLabel");
            applyLoanCurrencyLabel.setValue(currencyDesc);
            Label applyLoanCurrencyValue = (Label) comp.getFellow("applyLoanCurrencyValue");
            applyLoanCurrencyValue.setValue(currencyUomId);
        }else{
            Label applyLoanCurrencyLabel = (Label) comp.getFellow("applyLoanCurrencyLabel");
            applyLoanCurrencyLabel.setValue("");
            Label applyLoanCurrencyValue = (Label) comp.getFellow("applyLoanCurrencyValue");
            applyLoanCurrencyValue.setValue("");
        }
    }
    public static void loanTypeCurrency(Component comp,String employeeId) throws GenericEntityException{
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        GenericValue empPositionGv = HumanResUtil.getEmplPositionForParty(UtilValidate.isEmpty(employeeId)?userLogin.getString("partyId"):employeeId, delegator);
        String emplPositionId = empPositionGv == null?null:empPositionGv.getString("emplPositionTypeId");
        String loanType = (String) ((Listbox) comp).getSelectedItem().getValue();
        GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", UtilValidate.isEmpty(employeeId)?userLogin.getString("partyId"):employeeId), false);
        getCurrency(comp, loanType, emplPositionId,personGV);
    }


    public static void applyLoan(Event event) {

        String msg = "";
        String loanType = null;
        java.sql.Date fromDate = null;
        try {

            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            final Component applyLoanWindow = event.getTarget();
            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

            String employeeId=null;
            Boolean empBoxVisible=((Combobox) applyLoanWindow.getFellow("employeeBox")).isVisible();
            if(empBoxVisible){
                Comboitem requestingEmployeeId = ((Combobox) applyLoanWindow.getFellow("employeeBox")).getSelectedItem();
                employeeId = requestingEmployeeId!=null?(String) requestingEmployeeId.getValue():userLogin.getString("partyId");
            }
            else{
                employeeId = userLogin.getString("partyId");
            }

            Listitem loanTypeInput = ((Listbox) applyLoanWindow.getFellow("applyLoanType")).getSelectedItem();
            loanType = (String) loanTypeInput.getValue();
            String emplPositionId =  HumanResUtil.getEmplPositionForParty(employeeId, delegator).getString("emplPositionTypeId");
            if (loanType == null)
                msg = msg + "\n Loan Type is Missing";
            Date fromDateInput = (Date) ((Datebox) applyLoanWindow.getFellow("applyLoanFromDate")).getValue();

            Date thruDateInput = (Date) ((Datebox) applyLoanWindow.getFellow("applyLoanThruDate")).getValue();

            fromDate = new java.sql.Date(fromDateInput.getTime());
            java.sql.Date thruDate = new java.sql.Date(thruDateInput.getTime());
            if (fromDate == null)
                msg = msg + "\n From Date is Missing";
            //Listitem currencyInput = ((Listbox) applyLoanWindow.getFellow("applyLoanCurrency")).getSelectedItem();

            String currency = (String) ((Label) applyLoanWindow.getFellow("applyLoanCurrencyValue")).getValue();
            if (currency == null)
                msg = msg + "\n Currency  is Missing";
            Integer amountType = ((Intbox) applyLoanWindow.getFellow("applyLoanAmount")).getValue();
            Double amount = amountType.doubleValue();

            String applyClaimHR_COMMENT = (String) ((Textbox) applyLoanWindow.getFellow("applyClaimHR_COMMENT")).getValue();
            if (amount == null)
                msg = msg + "\n Amount  is Missing";
            Integer applyLoanTenureYear = ((Intbox) applyLoanWindow.getFellow("applyLoanTenureYear")).getValue();
            Double l_applyLoanTenureYear =  applyLoanTenureYear.doubleValue();

            String managerPositionId1 = null;
            managerPositionId1 = HumanResUtil.getManagerPositionIdForParty(employeeId, delegator);
            
        	Security security = (Security) Executions.getCurrent().getAttributes().get("security");
            boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
        
            if (managerPositionId1 != null || isManager) {
            	String mgrPositionId=managerPositionId1;
            	if(managerPositionId1 == null){
            		GenericValue empPos=HumanResUtil.getEmplPositionForParty(employeeId, delegator);
            		mgrPositionId=empPos==null?null:empPos.getString("emplPositionId");
            	}
            final Map context = UtilMisc.toMap("userLogin", userLogin,"partyId",employeeId, "loanType", loanType, "fromDate", fromDate, "thruDate", thruDate, "loanAmount", amount,"currencyUomId", currency,
                        "mgrPositionId",mgrPositionId,"employeeComment",applyClaimHR_COMMENT,"hr_period",l_applyLoanTenureYear);
                
             boolean isEmployeeTerminatedOrResigned=HumanResUtil.checkEmployeeTermination(employeeId, delegator);
                if(isEmployeeTerminatedOrResigned){
                	 Messagebox.show("Terminated or Resigned employee can not apply for loan", "Error", 1, null);
                     return;
                }
                
                GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), false);
                String positionCategory = personGV.getString("positionCategory");
                String employeeType = personGV.getString("employeeType");
               
                List<GenericValue> eLoanLimitlist = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
              		 "loanType",loanType,"emplPositionTypeId",emplPositionId,"employeeType",employeeType,"positionCategory",positionCategory));
               if(UtilValidate.isEmpty(eLoanLimitlist)){
              	 eLoanLimitlist = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
                  		 "loanType",loanType,"employeeType",employeeType,"positionCategory",positionCategory));
               }
          
               if(UtilValidate.isEmpty(eLoanLimitlist)){
                    Messagebox.show("Loan Limit Does Not Exist, Can't Apply","Error",1,null);
                    return;
                }
                Map result = null;
               boolean isLoanLimitExceeded=false;
               GenericValue emplLoanLimitGv = EntityUtil.getFirst(eLoanLimitlist);
                GenericValue emplLoanInfoGv = delegator.findOne("EmployeeLoanInfo",UtilMisc.toMap("partyId",employeeId,"loanTypeId",loanType),false);
                if(UtilValidate.isNotEmpty(emplLoanInfoGv)){
                    if(emplLoanInfoGv.getDouble("balanceAmount").equals(0.0) || amount > emplLoanInfoGv.getDouble("balanceAmount")){
                    	isLoanLimitExceeded=true;
                    }
                }else{
                    if(emplLoanLimitGv.getDouble("loanAmount")<amount){
                    	isLoanLimitExceeded=true;
                    }
                }
              
                EntityCondition employmentCondition1 = EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,employeeId);
                EntityCondition employmentCondition2 = EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,new Timestamp(fromDate.getTime()));
                EntityCondition employmentCondition = EntityCondition.makeCondition(employmentCondition1,EntityOperator.AND,employmentCondition2);

                List<GenericValue> employmentList = delegator.findList("Employment", employmentCondition, null, null, null, false);
                if (UtilValidate.isEmpty(employmentList)) {
                    Messagebox.show("Loan Can not Be Applied Behind The Joining Date", "Warning", 1, null);
                    return;
                }

                EntityCondition loanCondition1 = EntityCondition.makeCondition("loanType",EntityOperator.EQUALS,loanType);
                EntityCondition loanCondition7 = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,employeeType);
                EntityCondition loanCondition8 = EntityCondition.makeCondition("positionCategory",EntityOperator.EQUALS,positionCategory);
                EntityCondition loanCondition9 = EntityCondition.makeCondition(loanCondition7,EntityOperator.AND,loanCondition8);
                          
                EntityCondition loanCondition3 = EntityCondition.makeCondition(loanCondition1,EntityOperator.AND,loanCondition9);
                EntityCondition loanCondition4 = EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime()));
                EntityCondition loanCondition5 = EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime()));
                EntityCondition loanCondition6 = EntityCondition.makeCondition(loanCondition4,EntityOperator.AND,loanCondition5);

                EntityCondition loanCondition=EntityCondition.makeCondition(loanCondition3,EntityOperator.AND,loanCondition6);
                List<GenericValue> loanList = delegator.findList("ELoanLimit", loanCondition, null, null, null, false);
                if (UtilValidate.isEmpty(loanList)) {
                    Messagebox.show("Loan must be applied within specified date range", "Error", 1, null);
                    return;
                }

                final LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
               
                if(isLoanLimitExceeded){
                	Messagebox.show("You do not have sufficient loan limit. \n  Do you want to apply?", "Warning", Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
                            new EventListener() {
                                public void onEvent(Event evt) throws GenericServiceException, InterruptedException {
                                    if ("onYes".equals(evt.getName())) {
                                    	createEmployeeLoan(dispatcher,context);
                                    	java.lang.Object component = org.zkoss.zk.ui.Path.getComponent("/searchPanel/searchButton");
                                    	if (component != null)
                                    		org.zkoss.zk.ui.event.Events.postEvent("onClick",(org.zkoss.zk.ui.Component) component,null);
                                    }
                                    else{
                                    	return;
                                    }
                                    
                                }});
                }else{
                	createEmployeeLoan(dispatcher,context);
                }
                
           /*     result = dispatcher.runSync("createELoanService", context);

                String loanId = (String) result.get("loanId");

                System.out.println("******Created with Loan Id********" + loanId);

                // Events.postEvent("onClick$searchButton",
                // applyLoanWindow.getPage()
                // .getFellow("searchPanel"), null);

                Messagebox.show(Labels.getLabel("HRMS_LOANSUCCESSMESSAGE"), "Success", 1, null);
*/                
                
            }else {
                Messagebox.show("Cannot Apply;Manager has not been Assigned", "Error", 1, null);
                return;
            }

        } catch (Exception e) {
            // if (!msg.equals(""))
            try {
                if (loanType == null)
                    msg = msg + "\n Loan Type is Missing";
                if (fromDate == null)
                    msg = msg + "\n From Date is Missing";
                Messagebox.show(msg + "\n");
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    
    public static void createEmployeeLoan(LocalDispatcher dispatcher,Map context) throws GenericServiceException, InterruptedException{
    	Map result = null;
    	result=dispatcher.runSync("createELoanService", context);
        String loanId = (String) result.get("loanId");
        System.out.println("******Created with Loan Id********" + loanId);
        Messagebox.show(Labels.getLabel("HRMS_LOANSUCCESSMESSAGE"), "Success", 1, null);

    }

    public static void showLoanWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

        Component approveClaimWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String employeeComm = "";
        //String employeeID = userLogin.getString("partyId");
        String employeeID = gv.getString("partyId");
        String mgrPositionID=gv.getString("mgrPositionId");
        String userLoginID = userLogin.getString("userLoginId");
        String statusId = gv.getString("statusId");
        String loanId = gv.getString("loanId");
        Date fromDate = gv.getDate("fromDate");
        Date thruDate = gv.getDate("thruDate");
        Double hrperiod = gv.getDouble("hr_period");
        Integer s_hrperiod = new Integer(0);
        if(hrperiod != null){
            s_hrperiod = hrperiod.intValue();
        }
        if(gv.getString("employeeComment") != null)
            employeeComm = gv.getString("employeeComment");
        Integer amount = new Integer(0);
        BigDecimal amountType = gv.getBigDecimal("loanAmount");
        if (amountType != null) {
            amount = amountType.intValue();
        }

        Map arg = new HashMap();
        arg.put("gv", gv);
        Window win = (Window) Executions.createComponents("/zul/loanManagement/updateLoan.zul", null, arg);
        Label employeeId = (Label) win.getFellow("employeeId");
        employeeId.setValue(employeeID);
        Label mgrPositionId=(Label) win.getFellow("mgrPositionId");
        mgrPositionId.setValue(mgrPositionID);
        Label savedLoanId = (Label) win.getFellow("loanId");
        savedLoanId.setValue(loanId);
        Label loanStatus = (Label) win.getFellow("loanStatus");
        loanStatus.setValue(statusId);
        Label viewloanStatus = (Label) win.getFellow("viewloanStatus");
        List<GenericValue> statuItemList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", loanStatus.getValue()));
        viewloanStatus.setValue(statuItemList.get(0).getString("description"));
        Datebox savedFromDate = (Datebox) win.getFellow("fromDate");
        savedFromDate.setValue(fromDate);
        Datebox savedThruDate = (Datebox) win.getFellow("thruDate");
        savedThruDate.setValue(thruDate);
        Intbox savedLoanAmount = (Intbox) win.getFellow("amount");
        savedLoanAmount.setValue(amount);
        Textbox applyClaimHR_COMMENT = (Textbox) win.getFellow("applyClaimHR_COMMENT");
        applyClaimHR_COMMENT.setValue(employeeComm);
        Intbox applyLoanTenureYear = (Intbox) win.getFellow("applyLoanTenureYear");
        applyLoanTenureYear.setValue(s_hrperiod);
        win.doModal();

    }

    public static String updateLoan(Event event , boolean isSaveOnly) {

        try {
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            final Component EditLoanWindow = event.getTarget();
            //final GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
            final GenericDelegator delegator = HrmsInfrastructure.getDelegator();
            //String employeeId = userLogin.getString("partyId");
            String employeeId = ((Label) EditLoanWindow.getFellow("employeeId")).getValue();
            Date fromDateInput = (Date) ((Datebox) EditLoanWindow.getFellow("fromDate")).getValue();
            Date thruDateInput = (Date) ((Datebox) EditLoanWindow.getFellow("thruDate")).getValue();
            java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());
            java.sql.Date thruDate = new java.sql.Date(thruDateInput.getTime());
            Integer amountType = ((Intbox) EditLoanWindow.getFellow("amount")).getValue();
            Double amount = amountType.doubleValue();
            String currency = (String) ((Label) EditLoanWindow.getFellow("applyLoanCurrencyValue")).getValue();
            Listitem savedLoanType = (Listitem) ((Listbox) EditLoanWindow.getFellow("loanType")).getSelectedItem();
            String loanType = (String) savedLoanType.getValue();
            String loanId = ((Label) EditLoanWindow.getFellow("loanId")).getValue();
            Integer applyLoanTenureYear = ((Intbox) EditLoanWindow.getFellow("applyLoanTenureYear")).getValue();
            Double l_applyLoanTenureYear = applyLoanTenureYear.doubleValue();
            Map updateELoan = UtilMisc.toMap("userLogin", userLogin, "loanType", loanType, "fromDate", fromDate, "thruDate", thruDate, "loanAmount", amount,"currencyUomId", currency,
                    "loanId", loanId,"hr_period",l_applyLoanTenureYear);
            Map result = null;
            if(UtilValidate.isEmpty(currency)){
                Messagebox.show("Loan Limit Does Not Exist, Can't Apply","Error",1,null);
                return "error";
            }

            String emplPositionId =  HumanResUtil.getEmplPositionForParty(employeeId, delegator).getString("emplPositionTypeId"); 
            
           
            GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), false);
            String positionCategory = personGV.getString("positionCategory");
            String employeeType = personGV.getString("employeeType");
           
            List<GenericValue> eLoanLimitlist = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
          		 "loanType",loanType,"emplPositionTypeId",emplPositionId,"employeeType",employeeType,"positionCategory",positionCategory));
           if(UtilValidate.isEmpty(eLoanLimitlist)){
          	 eLoanLimitlist = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
              		 "loanType",loanType,"employeeType",employeeType,"positionCategory",positionCategory));
           }
         
           if(UtilValidate.isEmpty(eLoanLimitlist)){
                Messagebox.show("Loan Limit Does Not Exist, Can't Apply","Error",1,null);
                return "error";
            }
           boolean isLoanLimitExceeded=false;
           GenericValue emplLoanLimitGv = EntityUtil.getFirst(eLoanLimitlist);
            GenericValue emplLoanInfoGv = delegator.findOne("EmployeeLoanInfo",UtilMisc.toMap("partyId",employeeId,"loanTypeId",loanType),false);
            if(UtilValidate.isNotEmpty(emplLoanInfoGv)){
                if(emplLoanInfoGv.getDouble("balanceAmount").equals(0.0) || amount > emplLoanInfoGv.getDouble("balanceAmount")){
                	isLoanLimitExceeded=true;
                }
            }else{
                if(emplLoanLimitGv.getDouble("loanAmount")<amount){
                	isLoanLimitExceeded=true;
                }
            }

            
            
            
            EntityCondition employmentCondition1 = EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,userLogin.getString("partyId"));
            EntityCondition employmentCondition2 = EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,new Timestamp(fromDate.getTime()));
            EntityCondition employmentCondition = EntityCondition.makeCondition(employmentCondition1,EntityOperator.AND,employmentCondition2);

            List<GenericValue> employmentList = delegator.findList("Employment", employmentCondition, null, null, null, false);
            if (UtilValidate.isEmpty(employmentList)) {
                Messagebox.show("Loan Can not Be Applied Behind The Joining Date", "Warning", 1, null);
                return "error";
            }

            GenericValue loanHeadGv=delegator.findByPrimaryKey("ELoanHead",UtilMisc.toMap("loanId",loanId));
            String partyId=loanHeadGv.getString("partyId");
       //     String emplPositionId =  HumanResUtil.getEmplPositionForParty(partyId, delegator).getString("emplPositionTypeId");
            
            
            EntityCondition loanCondition1 = EntityCondition.makeCondition("loanType",EntityOperator.EQUALS,loanType);
            EntityCondition loanCondition7 = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,employeeType);
            EntityCondition loanCondition8 = EntityCondition.makeCondition("positionCategory",EntityOperator.EQUALS,positionCategory);
            EntityCondition loanCondition9 = EntityCondition.makeCondition(loanCondition7,EntityOperator.AND,loanCondition8);
                      
            EntityCondition loanCondition3 = EntityCondition.makeCondition(loanCondition1,EntityOperator.AND,loanCondition9);
            EntityCondition loanCondition4 = EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime()));
            EntityCondition loanCondition5 = EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,new java.sql.Date(fromDate.getTime()));
            EntityCondition loanCondition6 = EntityCondition.makeCondition(loanCondition4,EntityOperator.AND,loanCondition5);

            EntityCondition loanCondition=EntityCondition.makeCondition(loanCondition3,EntityOperator.AND,loanCondition6);
           
            List<GenericValue> loanList = delegator.findList("ELoanLimit", loanCondition, null, null, null, false);
            if (UtilValidate.isEmpty(loanList)) {
                Messagebox.show("Loan must be applied within specified date range", "Error", 1, null);
                return "error";
            }

            final LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            final Map context=updateELoan;
            final String loanValue=loanId;
            final boolean saveOnly=isSaveOnly;
            if(isLoanLimitExceeded){
            	Messagebox.show("You do not have sufficient loan limit. \n  Do you want to apply?", "Warning", Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
                        new EventListener() {
                            public void onEvent(Event evt) throws GenericServiceException, InterruptedException, GenericEntityException {
                                if ("onYes".equals(evt.getName())) {
                                	updateEmployeeLoan(dispatcher,context,delegator,loanValue,EditLoanWindow,saveOnly);
                                	java.lang.Object c = org.zkoss.zk.ui.Path.getComponent("/searchPanel/searchButton");
    								org.zkoss.zk.ui.event.Events.postEvent("onClick",(org.zkoss.zk.ui.Component) c,null);
                                }
                                else{
                                	return;
                                }
                                
                            }});
            }else{
            	updateEmployeeLoan(dispatcher,context,delegator,loanValue,EditLoanWindow,saveOnly);
            }
            
/*
            
            
            result = dispatcher.runSync("updateELoanService", updateELoan);

            List<GenericValue> eloanHeadGV = delegator.findByAnd("ELoanStatus", UtilMisc.toMap("loanId", loanId));
            String lsID = eloanHeadGV.get(0).getString("lsId");
            String applyClaimHR_COMMENT = ((Textbox) EditLoanWindow.getFellow("applyClaimHR_COMMENT")).getValue();
            GenericValue loanStatusGV = delegator.makeValue("ELoanStatus", UtilMisc.toMap("lsId",lsID,"employeeComment", applyClaimHR_COMMENT,"updateDate",UtilDateTime.nowTimestamp()));
            loanStatusGV.store();
            if(isSaveOnly)
                Messagebox.show(Labels.getLabel("HRMS_LOANUPDATESUCCESSMESSAGE"), "Success", 1, null);
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";

    }

    public static void updateEmployeeLoan(LocalDispatcher dispatcher,Map context,GenericDelegator delegator, String loanId,Component EditLoanWindow
    		,boolean isSaveOnly) throws GenericServiceException, InterruptedException, GenericEntityException{
    
        Map result=null;
        result = dispatcher.runSync("updateELoanService", context);
        List<GenericValue> eloanHeadGV = delegator.findByAnd("ELoanStatus", UtilMisc.toMap("loanId", loanId));
        String lsID = eloanHeadGV.get(0).getString("lsId");
        String applyClaimHR_COMMENT = ((Textbox) EditLoanWindow.getFellow("applyClaimHR_COMMENT")).getValue();
        GenericValue loanStatusGV = delegator.makeValue("ELoanStatus", UtilMisc.toMap("lsId",lsID,"employeeComment", applyClaimHR_COMMENT,"updateDate",UtilDateTime.nowTimestamp()));
        loanStatusGV.store();
        if(isSaveOnly)
            Messagebox.show(Labels.getLabel("HRMS_LOANUPDATESUCCESSMESSAGE"), "Success", 1, null);
        else
        	submitEmployeeLoan(EditLoanWindow);
    }

    
    
    
    public static void submitLoan(Event event) {
        String status = updateLoan(event,false);
      //  if("error".equals(status))
       //     return;
    /*    try {
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            Component EditLoanWindow = event.getTarget();
            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
            String loanId = ((Label) EditLoanWindow.getFellow("loanId")).getValue();
            String applyClaimHR_COMMENT = ((Textbox) EditLoanWindow.getFellow("applyClaimHR_COMMENT")).getValue();
            Map submitLoan = UtilMisc.toMap("userLogin", userLogin, "loanId", loanId, "statusType", "1","employeeComment",applyClaimHR_COMMENT);
            Map result = null;
            LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher("default", delegator);
            result = dispatcher.runSync("submitELoanService", submitLoan);
            Events.postEvent("onClick$searchButton", EditLoanWindow.getPage().getFellow("searchPanel"), null);
            EditLoanWindow.detach();
            Messagebox.show(Labels.getLabel("HRMS_LOANSUBMITMESSAGE"), "Success", 1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }
    
    public static void submitEmployeeLoan(Component EditLoanWindow){
    	 try {
             GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
             //Component EditLoanWindow = event.getTarget();
             GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
             String loanId = ((Label) EditLoanWindow.getFellow("loanId")).getValue();
             String applyClaimHR_COMMENT = ((Textbox) EditLoanWindow.getFellow("applyClaimHR_COMMENT")).getValue();
             String employeeId = ((Label) EditLoanWindow.getFellow("employeeId")).getValue();
             String mgrPositionId = ((Label) EditLoanWindow.getFellow("mgrPositionId")).getValue();
             
             Map submitLoan = UtilMisc.toMap("userLogin", userLogin, "loanId", loanId, "statusType", "1","employeeComment",applyClaimHR_COMMENT,
            	"mgrPositionId", mgrPositionId,"partyId",employeeId);
             Map result = null;
             LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher("default", delegator);
             result = dispatcher.runSync("submitELoanService", submitLoan);
             Events.postEvent("onClick$searchButton", EditLoanWindow.getPage().getFellow("searchPanel"), null);
             EditLoanWindow.detach();
             Messagebox.show(Labels.getLabel("HRMS_LOANSUBMITMESSAGE"), "Success", 1, null);
         } catch (Exception e) {
             e.printStackTrace();
         }


    }
    public static boolean checkTenureInYears(String loanType,String emplPositionId,String tenureInYears, String partyId) throws GenericEntityException{
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        
        GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
        List<GenericValue> eLoanLimitlist = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
         		 "loanType",loanType,"emplPositionTypeId",emplPositionId,"employeeType",personGV.getString("employeeType"),
         		 "positionCategory",personGV.getString("positionCategory")));
        if(UtilValidate.isEmpty(eLoanLimitlist)){
        	eLoanLimitlist = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
             		 "loanType",loanType,"employeeType",personGV.getString("employeeType"),"positionCategory",personGV.getString("positionCategory")));
        }    
        if(UtilValidate.isNotEmpty(eLoanLimitlist)){
            GenericValue eLoanLimitGV = EntityUtil.getFirst(eLoanLimitlist);
            Integer period = eLoanLimitGV.getDouble("hr_period").intValue();
            Integer tiy = Integer.parseInt(tenureInYears);
            if (period < tiy){
                return true;
            }
        }
        return false;
    }
    public static void showApproveLoanWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

        Component approveClaimWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

        String employeeID = gv.getString("partyId");
        String userLoginID = userLogin.getString("userLoginId");
        String statusId = gv.getString("statusId");
        String loanId = gv.getString("loanId");
        String loanType = gv.getString("loanType");
        java.sql.Date fromDate = gv.getDate("fromDate");
        java.sql.Date thruDate = gv.getDate("thruDate");
        String hrComment = gv.getString("hr_comment");
        String employeeComm = gv.getString("employeeComment");
        String currency = gv.getString("currencyUomId");
        String mgrPositionID=gv.getString("mgrPositionId");
        
        BigDecimal amountType = gv.getBigDecimal("loanAmount");
        BigInteger biAmountType = new BigInteger("0");
        if(amountType != null)
            biAmountType = amountType.toBigInteger();
        String amount = biAmountType.toString();
        BigDecimal acceptedAmountType = gv.getBigDecimal("acceptedAmount");

        String posType = (HumanResUtil.getEmplPositionForParty(employeeID, delegator)).getString("emplPositionTypeId");
        

        GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeID), false);
        String positionCategory = personGV.getString("positionCategory");
        String employeeType = personGV.getString("employeeType");
        List<GenericValue> loanLimitList = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
         		 "loanType",loanType,"emplPositionTypeId",posType,"employeeType",employeeType,"positionCategory",positionCategory));
        if(UtilValidate.isEmpty(loanLimitList)){
        	loanLimitList = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
             		 "loanType",loanType,"employeeType",employeeType,"positionCategory",positionCategory));
        }
        GenericValue loanLimitGv = EntityUtil.getFirst(loanLimitList);
        BigDecimal loanLimit = new BigDecimal(0);
        Integer biLoanLimit = null;
        String  strLoanLimit = new String();
        Double tenureinYears = 0d;
        Double interestLimit = 0d;
        if (loanLimitGv != null) {
            loanLimit = loanLimitGv.getBigDecimal("loanAmount");
            biLoanLimit = loanLimit.intValue();
            strLoanLimit = biLoanLimit.toString();
            tenureinYears = loanLimitGv.getDouble("hr_period");
            interestLimit = loanLimitGv.getDouble("interest");
        }
        Integer acceptedAmount = 0;
        if (acceptedAmountType != null) {
            acceptedAmount = acceptedAmountType.intValue();
        }
        BigDecimal rejectedAmountType = gv.getBigDecimal("rejectedAmount");
        Integer rejectedAmount = null;
        String rejectedAmountStr = new String();
        if (rejectedAmountType != null) {
            rejectedAmount = rejectedAmountType.intValue();
            rejectedAmountStr = rejectedAmount.toString();
        }
        Double emIType = gv.getDouble("emi");
        String EMI = null;
        if (emIType != null) {
            EMI = emIType.toString();
        }
        BigDecimal interestType = gv.getBigDecimal("interest");
        BigDecimal periodType = gv.getBigDecimal("hr_period");
        Integer period = null;
        if (periodType != null) {
            period = periodType.intValue();
        }
        Map map = new HashMap();
        map.put("currencyId", currency);
        map.put("statusId", statusId);
        Window win = (Window) Executions.createComponents("/zul/loanManagement/processLoan.zul", null, map);
        win.setTitle("Process Loan");
        ((Label) win.getFellow("loanLimit")).setValue(strLoanLimit);
        ((Label) win.getFellow("tenure")).setValue(tenureinYears.toString());
        ((Label) win.getFellow("roi")).setValue(CurrencyFormatter.format(interestLimit));
        Label employeeId = (Label) win.getFellow("employeeId");
        employeeId.setValue(employeeID);
        Label mgrPositionId=(Label) win.getFellow("mgrPositionId");
        mgrPositionId.setValue(mgrPositionID);
        Label employeeName = (Label) win.getFellow("employeeName");
        employeeName.setValue(HrmsUtil.getFullName(delegator, employeeID));
        Label savedLoanId = (Label) win.getFellow("loanId");
        savedLoanId.setValue(loanId);
        Textbox savedLoanType = (Textbox) win.getFellow("loanType");
        savedLoanType.setValue(loanType);
        GenericValue enummerationGV = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", loanType));
        Label loanTypeView = (Label) win.getFellow("loanTypeView");
        loanTypeView.setValue(enummerationGV.getString("description"));

        final Label loanStatus = (Label) win.getFellow("loanStatus");
        loanStatus.setValue(statusId);

        Label loanStatusView = (Label) win.getFellow("loanStatusView");
        GenericValue statusItem = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId));
        loanStatusView.setValue(statusItem.getString("description"));

        Datebox savedFromDate = (Datebox) win.getFellow("fromDate");
        savedFromDate.setValue(fromDate);
        Datebox savedThruDate = (Datebox) win.getFellow("thruDate");
        savedThruDate.setValue(thruDate);
        Label savedLoanAmount = (Label) win.getFellow("amount");
        savedLoanAmount.setValue(amount);
        Intbox savedAcceptedAmount = (Intbox) win.getFellow("acceptedAmount");
        savedAcceptedAmount.setValue(acceptedAmount);
        Label savedRejectedAmount = (Label) win.getFellow("rejectedAmount");
        savedRejectedAmount.setValue(rejectedAmountStr);
        Label savedEMI = (Label) win.getFellow("EMI");
        savedEMI.setValue(EMI);
        Label EMIVisible = (Label) win.getFellow("EMIVisible");
        EMIVisible.setValue(EMI);
        Decimalbox savedInterest = (Decimalbox) win.getFellow("interest");
        if(interestType == null)
            interestType = new BigDecimal(0);
        savedInterest.setValue(interestType);
        Intbox savedPeriod = (Intbox) win.getFellow("period");
        savedPeriod.setValue(period);

        Textbox comment = (Textbox) win.getFellow("comment");
        comment.setValue(hrComment);
        Textbox hiddenEmployeeComment = (Textbox) win.getFellow("hiddenEmployeeComment");
        hiddenEmployeeComment.setValue(employeeComm);

        Textbox employeeComment=(Textbox) win.getFellow("employeeComment");
        employeeComment.setValue(employeeComm);
		/* Check for the User Permission */
        String hasPermission = ((Label) win.getFellow("permissionId")).getValue();
        Boolean isAdmin = new Boolean(hasPermission);

        String hasPermissionForBoth = ((Label) win.getFellow("permissionIdForBoth")).getValue();
        Boolean both = new Boolean(hasPermissionForBoth);

       // if (!isAdmin) {
        if((!isAdmin || both) && "EL_SUBMITTED".equals(loanStatus.getValue())){
            Button approveClaimButton = (Button) win.getFellow("approveLoan");
            approveClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    loanStatus.setValue("EL_MGR_APPROVED");
                    ApplyLoanManagementController.processLoan(event, "1", "2");
                }
            });

            Button rejectClaimButton = (Button) win.getFellow("rejectLoan");
            rejectClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    loanStatus.setValue("EL_MGR_REJECTED");
                    ApplyLoanManagementController.processLoan(event, "1", "1");
                }
            });
      //  } else {
        }else if(isAdmin && ("EL_MGR_APPROVED".equals(loanStatus.getValue()))) {
            Button approveClaimButton = (Button) win.getFellow("approveLoan");
            approveClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    loanStatus.setValue("EL_ADM_APPROVED");
                    ApplyLoanManagementController.processLoan(event, "1", "2");
                }
            });

            Button rejectClaimButton = (Button) win.getFellow("rejectLoan");
            rejectClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    loanStatus.setValue("EL_ADM_REJECTED");
                    ApplyLoanManagementController.processLoan(event, "1", "2");
                }
            });

        }
        win.doModal();

    }

    public static void processLoan(final Event event, final String statusType, final String adminStatusType) throws InterruptedException {

        Messagebox.show("Loan Approved/Rejected Can't Be Modified. \n Do You Want To Continue?", "Warning", Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
                new EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            try {
                                Component approveLoanWindow = event.getTarget();
                                GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                                GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

                                String statusId = (String) ((Label) approveLoanWindow.getFellow("loanStatus")).getValue();
                                Double amount = null;
                                Double acceptedAmount = new Double("0");
                                Double rejectedAmount = new Double("0");
                                Double interestAmount = new Double("0");
                                Double period = new Double("0");
                                Double EMI = new Double("0");
                            
                                String loanAmountType = (String) ((Label) approveLoanWindow.getFellow("amount")).getValue();
                                if(!(loanAmountType.equals("")))
                                    amount = new Double(loanAmountType);
                                Integer acceptedAmountType = ((Intbox) approveLoanWindow.getFellow("acceptedAmount"))!=null?
                                		((Intbox) approveLoanWindow.getFellow("acceptedAmount")).getValue():null;
                                if(("EL_ADM_APPROVED".equals(statusId) || "EL_MGR_APPROVED".equals(statusId))
                                		&& (acceptedAmountType != null && acceptedAmountType <= 0)){
                                	Messagebox.show("Accepted Amount Must be Greater than Zero", "Error", 1, null);
                                    	return;
                               }
                                if(acceptedAmountType != null)
                                    acceptedAmount = new Double(acceptedAmountType);                   
                             
                               String rejectedAmountType = (String) ((Label) approveLoanWindow.getFellow("rejectedAmount")).getValue();
                                if(!(rejectedAmountType.equals("")))
                                    rejectedAmount = new Double(rejectedAmountType);
                                BigDecimal interestAmountType = ((Decimalbox) approveLoanWindow.getFellow("interest")).getValue();
                                if(interestAmountType != null)
                                    interestAmount = interestAmountType.doubleValue();

                                Integer periodType = ((Intbox) approveLoanWindow.getFellow("period")).getValue();
                                if(!(periodType.equals("")))
                                    period = periodType.doubleValue();

                                String EMIType = (String) ((Label) approveLoanWindow.getFellow("EMI")).getValue();
                                if(!(EMIType.equals("")))
                                    EMI = new Double(EMIType);
                                String hr_comment = (String) ((Textbox) approveLoanWindow.getFellow("comment")).getValue();
                                String adminComment = (String) ((Textbox) approveLoanWindow.getFellow("adminComment")).getValue();
                                String hiddenEmployeeComment = (String) ((Textbox) approveLoanWindow.getFellow("hiddenEmployeeComment")).getValue();
                                String loanId = (String) ((Label) approveLoanWindow.getFellow("loanId")).getValue();
                                String currency = (String) ((Label) approveLoanWindow.getFellow("applyLoanCurrencyValue")).getValue();
                                String mgrPositionId=(String) ((Label) approveLoanWindow.getFellow("mgrPositionId")).getValue();
                                String partyId = null;
                                String loanType = null;
                                String emplPosTypeIdOfParty = null;
                                GenericValue loanGv = null;
                                if ("EL_ADM_APPROVED".equals(statusId) || "EL_MGR_APPROVED".equals(statusId)) {
                                    @SuppressWarnings("unused")
                                    List loanList = delegator.findByAnd("ELoanHead", UtilMisc.toMap("loanId", loanId));
                                    loanGv = EntityUtil.getFirst(loanList);
                                    partyId = (String) loanGv.getString("partyId");
                                    loanType = (String) loanGv.getString("loanType");
                                    GenericValue emplPosTypeIdOfPartyGv = HumanResUtil.getEmplPositionForParty(partyId, delegator);
                                    if (emplPosTypeIdOfPartyGv != null) {
                                        emplPosTypeIdOfParty = emplPosTypeIdOfPartyGv.getString("emplPositionTypeId");
                                    }
                                    List loanLimitList = delegator.findByAnd("ELoanLimit", UtilMisc.toMap("emplPositionTypeId", emplPosTypeIdOfParty, "loanType", loanType));
                                    //if (loanLimitList.size() <= 0) {
                                    //Messagebox.show("Loan Limit not exist,cannot be processed", "Error", 1, null);
                                    //	return;
                                    //}

                                }
                                java.sql.Date fromDate = null;
                                java.sql.Date thruDate = null;
                                Date fDate = (Date) ((Datebox) approveLoanWindow.getFellow("fromDate")).getValue();
                                if (fDate != null)
                                    fromDate = new java.sql.Date(fDate.getTime());
                                Date tDate = (Date) ((Datebox) approveLoanWindow.getFellow("thruDate")).getValue();
                                if (tDate != null)
                                    thruDate = new java.sql.Date(tDate.getTime());
                                Map loanMap = UtilMisc.toMap("partyId", partyId, "loanTypeId", loanType);

                                if ("EL_ADM_APPROVED".equals(statusId)) {
                                    List loanInfoList = delegator.findByAnd("EmployeeLoanInfo", loanMap);
                                    GenericValue employeeLoanInfoGv = EntityUtil.getFirst(loanInfoList);
                                    
                                    
                                    GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
                                    String positionCategory = personGV.getString("positionCategory");
                                    String employeeType = personGV.getString("employeeType");
                                    List<GenericValue> loanLimitInfoList = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
                                     		 "loanType",loanType,"emplPositionTypeId",emplPosTypeIdOfParty,"employeeType",employeeType,"positionCategory",positionCategory));
                                    if(UtilValidate.isEmpty(loanLimitInfoList)){
                                    	loanLimitInfoList = delegator.findByAnd("ELoanLimit",UtilMisc.toMap(
                                       		 "loanType",loanType,"employeeType",employeeType,"positionCategory",positionCategory));
                                    }
                                    GenericValue loanLimitInfoGv = EntityUtil.getFirst(loanLimitInfoList);
                                    BigDecimal loanLimit = loanLimitInfoGv.getBigDecimal("loanAmount");
                                    BigDecimal previousAmount = new BigDecimal(0);
                                    BigDecimal balanceAmount = new BigDecimal(0);
                                    BigDecimal availedAmount = new BigDecimal(0);
                                    BigDecimal approvedAmount = new BigDecimal(acceptedAmount);
                                    if (employeeLoanInfoGv != null) {
                                        availedAmount = employeeLoanInfoGv.getBigDecimal("availedAmount");
                                    }
                                    availedAmount = availedAmount.add(approvedAmount);
                                    balanceAmount = loanLimit.subtract(availedAmount);
                                    if (balanceAmount.doubleValue() <= 0.0) {
                                        balanceAmount = new BigDecimal("0");
                                        //Messagebox.show("Balanced Loan Limit Exceeds ;cannot be processed", "Error", 1, null);
                                        //return;
                                    }
                                    GenericValue claimInfoGv = delegator.makeValue("EmployeeLoanInfo", UtilMisc.toMap("loanTypeId", loanType, "balanceAmount", balanceAmount, "availedAmount", availedAmount,
                                            "positionType", emplPosTypeIdOfParty, "partyId", partyId, "managerId", mgrPositionId));
                                    delegator.createOrStore(claimInfoGv);
                                }
                                Map approveLoan = UtilMisc.toMap("userLogin", userLogin, "statusId", statusId, "loanAmount", amount, "acceptedAmount", acceptedAmount, "rejectedAmount", rejectedAmount, "interest",
                                        interestAmount, "loanId", loanId, "hr_period", period,"employeeComment",hiddenEmployeeComment,"hr_comment", hr_comment,"adminComment",adminComment,"emi", EMI, "statusType", statusType, "adminStatusType", adminStatusType, "fromDate", fromDate,
                                        "thruDate", thruDate,"currencyUomId",currency);
                                LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
                                dispatcher.runSync("processELoanService", approveLoan);
                                Events.postEvent("onClick", approveLoanWindow.getFellow("approveLoanWindow").getFellow("hiddenCloseButton"), null);
                                Messagebox.show("Loan Processed Successfully", "Success", 1, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    public static void deleteLoan(Event event, GenericValue gv, final Button btn) throws GenericEntityException, InterruptedException {

        Component searchPanel = event.getTarget();
        final String loanId = gv.getString("loanId");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

        Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {

                    try {


                        List loans = delegator.findByAnd("ELoanStatus", UtilMisc.toMap("loanId", loanId));
                        if (loans != null && loans.size() > 0) {
                            delegator.removeByAnd("ELoanStatus", UtilMisc.toMap("loanId", loanId));
                            delegator.removeByAnd("ELoanHead", UtilMisc.toMap("loanId", loanId));
                            Events.postEvent("onClick", btn, null);
                        }

                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        Messagebox.show(Labels.getLabel("HRMS_LOANDELETEMESSAGE"), "Success", 1, null);
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

    public static void showEMIInfo(Event event,GenericValue employeeLoanDetail) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

        Window win = (Window) Executions.createComponents("/zul/loanManagement/emiInfo.zul", null, UtilMisc.toMap(
                "employeeId",employeeLoanDetail.getString("partyId"),
                "loanId",employeeLoanDetail.getString("loanId")));
        win.doModal();

    }




}
