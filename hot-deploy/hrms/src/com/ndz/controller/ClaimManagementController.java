package com.ndz.controller;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ndz.zkoss.util.MailUtil;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;
import org.zkoss.zul.api.Comboitem;

import com.ndz.component.party.ClaimTypeRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.zkoss.util.CurrencyFormatter;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class ClaimManagementController extends GenericForwardComposer {
	
    public void doAfterCompose(Component applyClaimWindow) throws Exception {
        super.doAfterCompose(applyClaimWindow);

        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        EntityCondition condition = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CLAIM_TYPE");
        EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");

        Set<String> claimTypeToDisplay = new HashSet();
        Set currencyTypeToDisplay = new HashSet();

        claimTypeToDisplay.add("enumTypeId");
        claimTypeToDisplay.add("enumId");
        claimTypeToDisplay.add("description");

        currencyTypeToDisplay.add("uomId");
        currencyTypeToDisplay.add("description");

        List<GenericValue> claimType = delegator.findList("Enumeration", condition, claimTypeToDisplay, null, null, false);
        claimType.add(0, null);
        Object claimTypeArray = claimType.toArray(new GenericValue[claimType.size()]);
        SimpleListModel claimTypeList = new SimpleListModel(claimType);

        Listbox applyClaimType = (Listbox) applyClaimWindow.getFellow("applyClaimType");
        applyClaimType.setModel(claimTypeList);
        applyClaimType.setItemRenderer(new ClaimTypeRenderer());

        List<GenericValue> currency = delegator.findList("Uom", currencyCondition, currencyTypeToDisplay, null, null, false);
        currency.add(0, null);
        Object currencyArray = currency.toArray(new GenericValue[currency.size()]);
        SimpleListModel currencyList = new SimpleListModel(currency);

        Listbox currencyListBox = (Listbox) applyClaimWindow.getFellow("applyClaimCurrency");
        currencyListBox.setModel(currencyList);
        currencyListBox.setItemRenderer(new CurrencyRenderer());
    }

    private static byte[] attachmentData;

    private static String attachmentName;

    public static void uploadClaimReceipt(UploadEvent uploadEvent, Component component) throws IOException {
        Media uploadedFile = uploadEvent.getMedia();
        attachmentName = uploadedFile.getName();
        ((Label) component).setValue(attachmentName + " " + "Receipt Uploaded");
        if (uploadedFile.isBinary())
            ClaimManagementController.attachmentData = IOUtils.toByteArray(uploadedFile.getStreamData());
        else {
            ClaimManagementController.attachmentData = IOUtils.toByteArray(uploadedFile.getReaderData());
        }

    }


    public static void applyClaim(Event event) {

        String claimId = null;
        Messagebox messageBox = new Messagebox();
        try {
            final Component applyClaimWindow = event.getTarget();
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
           final GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
            String employeeId = null;
            Boolean mgnrBoxVisible = ((Combobox) applyClaimWindow.getFellow("managerBox")).isVisible();
            if (mgnrBoxVisible) {
                Comboitem requestingEmployeeId = ((Combobox) applyClaimWindow.getFellow("managerBox")).getSelectedItem();
                employeeId = requestingEmployeeId != null ? (String) requestingEmployeeId.getValue() : userLogin.getString("partyId");
            } else
                employeeId = userLogin.getString("partyId");
            GenericValue emplPositionGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
            String emplPosType = emplPositionGv == null ? null : emplPositionGv.getString("emplPositionTypeId");
            Listitem claimTypeInput = ((Listbox) applyClaimWindow.getFellow("applyClaimType")).getSelectedItem();
            String claimType = (String) claimTypeInput.getValue();

            Date fromDateInput = (Date) ((Datebox) applyClaimWindow.getFellow("applyClaimFromDate")).getValue();
            Date thruDateInput = (Date) ((Datebox) applyClaimWindow.getFellow("applyClaimThruDate")).getValue();

            java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());
            java.sql.Date thruDate = new java.sql.Date(thruDateInput.getTime());

            Listitem currencyInput = ((Listbox) applyClaimWindow.getFellow("applyClaimCurrency")).getSelectedItem();
            String currency = (String) currencyInput.getValue();

            BigDecimal amountInDecimal = ((Decimalbox) applyClaimWindow.getFellow("applyClaimAmount")).getValue();
            Double amount = amountInDecimal.doubleValue();
            Double receipts = null;
            String applyClaimReceiptType = ((Textbox) applyClaimWindow.getFellow("applyClaimReceipts")).getValue();
            if (applyClaimReceiptType != "") {
                receipts = new Double(applyClaimReceiptType);
            }
            String applyClaimHR_COMMENT = ((Textbox) applyClaimWindow.getFellow("applyClaimHR_COMMENT")).getValue();
            String managerPositionId1 = null;
            managerPositionId1 = HumanResUtil.getManagerPositionIdForParty(employeeId, delegator);
            
            Security security = (Security) Executions.getCurrent().getAttributes().get("security");
            boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
        
            
            
            if (managerPositionId1 != null || isManager) {
            	String mgrPositionId=managerPositionId1;
            	if(managerPositionId1 == null){
            		GenericValue empPos=HumanResUtil.getEmplPositionForParty(employeeId, delegator);
            		mgrPositionId= empPos==null?null:empPos.getString("emplPositionId");
            	}
            	
               final Map context = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "receipts", receipts, "claimType", claimType, "beginDate", fromDate, "endDate", thruDate,
                        "amount", amount, "currencyUomId", currency, "admincurrencyUomId", currency, "mgrPositionId", mgrPositionId, "employeeComment",
                        applyClaimHR_COMMENT);
                Map result = null;
                List<GenericValue> emplClaimInfo = delegator.findByAnd("ClaimLimit", UtilMisc.toMap("claimType", claimType, "emplPositionTypeId", emplPosType));
                // EntityCondition employmentCondition =
                // EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,fromDate);
                boolean isEmployeeTerminatedOrResigned=HumanResUtil.checkEmployeeTermination(employeeId, delegator);
                if(isEmployeeTerminatedOrResigned){
                	 Messagebox.show("Terminated or Resigned employee can not apply for claim", "Error", 1, null);
                     return;
                }
                
                GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), false);
            	String positionCategory = personGV.getString("positionCategory");
            	String employeeType = personGV.getString("employeeType");
            	
                List<GenericValue> emplClaimLimitInfo = delegator.findByAnd("ClaimLimit", 
                		UtilMisc.toMap("claimType", claimType, "emplPositionTypeId", emplPosType,"employeeType",employeeType,"positionCategory",positionCategory));
               if(UtilValidate.isEmpty(emplClaimLimitInfo)){
            	   emplClaimLimitInfo = delegator.findByAnd("ClaimLimit", 
                   		UtilMisc.toMap("claimType", claimType,"employeeType",employeeType,"positionCategory",positionCategory));
               }             
              if (UtilValidate.isEmpty(emplClaimLimitInfo)) {
                    Messagebox.show("Claim Limit Does Not Exist, Cannot Apply", "Error", 1, null);
                    return;
                }
                boolean isClaimLimitExceeded=false;
                GenericValue emplClaimLimitGv = EntityUtil.getFirst(emplClaimLimitInfo);
                GenericValue emplClaimInfoGv = delegator.findOne("EmployeeClaimInfo",UtilMisc.toMap("partyId",employeeId,"claimTypeId",claimType),false);
                if(UtilValidate.isNotEmpty(emplClaimInfoGv)){
                    if(emplClaimInfoGv.getDouble("balanceAmount").equals(0.0) || amount > emplClaimInfoGv.getDouble("balanceAmount")){
                    	isClaimLimitExceeded=true;
                    }
                }else{
                    if(emplClaimLimitGv.getDouble("amount")<amount){
                    	isClaimLimitExceeded=true;
                    }
                }
             
                EntityCondition employmentCondition1 = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId);
                EntityCondition employmentCondition2 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(fromDate.getTime()));
                EntityCondition employmentCondition = EntityCondition.makeCondition(employmentCondition1, EntityOperator.AND, employmentCondition2);

                List<GenericValue> employmentList = delegator.findList("Employment", employmentCondition, null, null, null, false);
                if (UtilValidate.isEmpty(employmentList)) {
                    Messagebox.show("Claim Can not Be Applied Behind The Joining Date", "Error", 1, null);
                    return;
                }

               final LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
                
                
                
                if(isClaimLimitExceeded){
                	Messagebox.show("You do not have sufficient claim limit. \n  Do you want to apply ?", "Warning", Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
                            new EventListener() {
                                public void onEvent(Event evt) throws GenericServiceException, InterruptedException, GenericEntityException {
                                    if ("onYes".equals(evt.getName())) {
                                    	createEmployeeClaim(dispatcher,context,delegator,applyClaimWindow);
                                    	java.lang.Object component = org.zkoss.zk.ui.Path.getComponent("/searchPanel/searchButton");
                                    	if(component != null)
                                    		org.zkoss.zk.ui.event.Events.postEvent("onClick",(org.zkoss.zk.ui.Component) component,null);
                                    }
                                    else{
                                    	return;
                                    }
                                    
                                }});
                }else{
                	createEmployeeClaim(dispatcher,context,delegator,applyClaimWindow);
                }
            
       
                              
/*                result = dispatcher.runSync("createClaimService", context);

                claimId = (String) result.get("claimId");

                GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
                claimGv.put("attachment", attachmentData);
                claimGv.put("attachmentName", attachmentName);
                delegator.store(claimGv);
                Messagebox.show(Labels.getLabel("Hrms_ClaimSavedSuccessfullywithClaimID"), "Success", 1, null);
                attachmentData = null;
                attachmentName = null;
                applyClaimWindow.detach();
                
                */
            } else {
                messageBox.show("Cannot Apply;Manager has not been Assigned", "Error", 1, null);
                return;
            }
        } catch (Exception e) {
            try {
                messageBox.show(Labels.getLabel("Hrms_ClaimCouldnotbesavedSomeparameterismissing"), "Success", 1, null);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

    }
    
    
    public static void createEmployeeClaim(LocalDispatcher dispatcher,Map context,GenericDelegator delegator, Component applyClaimWindow) throws GenericServiceException, InterruptedException, GenericEntityException{
    	Map result=null;
    	String claimId=null;
        result = dispatcher.runSync("createClaimService", context);
        claimId = (String) result.get("claimId");

        GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
        claimGv.put("attachment", attachmentData);
        claimGv.put("attachmentName", attachmentName);
        delegator.store(claimGv);
        Messagebox.show(Labels.getLabel("Hrms_ClaimSavedSuccessfullywithClaimID"), "Success", 1, null);
        attachmentData = null;
        attachmentName = null;
        applyClaimWindow.detach();
 }

    

    public static void submitClaim(Event event,boolean isSaveOnly) {
        String status = updateClaim1(event,isSaveOnly);
     /*   if(!"success".equals(claimUpdateStatus))
            return;
        try {
            Component editClaimWindow = event.getTarget();
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            String claimId = (String) ((Label) editClaimWindow.getFellow("claimId")).getValue();
            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            Map submitClaim = null;
            submitClaim = UtilMisc.toMap("userLogin", userLogin, "claimId", claimId, "statusType", "1");
            dispatcher.runSync("submitClaimService", submitClaim);
            Events.postEvent("onClick$searchButton", editClaimWindow.getPage().getFellow("searchPanel"), null);
            Messagebox.show(Labels.getLabel("Hrms_ClaimSuccessfullySubmitted"), "Success", 1, null);
            GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
            if (attachmentData != null && attachmentName != null) {
                claimGv.put("attachment", attachmentData);
                claimGv.put("attachmentName", attachmentName);
                delegator.store(claimGv);
                attachmentData = null;
                attachmentName = null;
            }
            editClaimWindow.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
    
    public static void submitEmployeeClaim(Component editClaimWindow){
    	try {
            //Component editClaimWindow = event.getTarget();
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            String claimId = (String) ((Label) editClaimWindow.getFellow("claimId")).getValue();
            String employeeId = ((Label) editClaimWindow.getFellow("employeeId")).getValue();
            String mgrPositionId = ((Label) editClaimWindow.getFellow("mgrPositionId")).getValue();
           
            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            Map submitClaim = null;
            submitClaim = UtilMisc.toMap("userLogin", userLogin, "claimId", claimId, "statusType", "1",
            		"mgrPositionId", mgrPositionId,"partyId",employeeId);
            dispatcher.runSync("submitClaimService", submitClaim);
            Events.postEvent("onClick$searchButton", editClaimWindow.getPage().getFellow("searchPanel"), null);
            Messagebox.show(Labels.getLabel("Hrms_ClaimSuccessfullySubmitted"), "Success", 1, null);
            GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
            if (attachmentData != null && attachmentName != null) {
                claimGv.put("attachment", attachmentData);
                claimGv.put("attachmentName", attachmentName);
                delegator.store(claimGv);
                attachmentData = null;
                attachmentName = null;
            }
            editClaimWindow.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void submitClaimTest(Event event) {
        try {
            Component editClaimWindow = event.getTarget();
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            String claimId = (String) ((Label) editClaimWindow.getFellow("claimId")).getValue();

            String employeeId = null;
            Boolean mgnrBoxVisible = ((Combobox) editClaimWindow.getFellow("managerBox")).isVisible();
            if (mgnrBoxVisible) {
                Comboitem requestingEmployeeId = ((Combobox) editClaimWindow.getFellow("managerBox")).getSelectedItem();
                employeeId = requestingEmployeeId != null ? (String) requestingEmployeeId.getValue() : userLogin.getString("partyId");
            } else
                employeeId = userLogin.getString("partyId");
            Listitem savedClaimType = (Listitem) ((Listbox) editClaimWindow.getFellow("claimType")).getSelectedItem();
            String claimType = (String) savedClaimType.getValue();
            Date fromDateInput = (Date) ((Datebox) editClaimWindow.getFellow("fromDate")).getValue();
            Date thruDateInput = (Date) ((Datebox) editClaimWindow.getFellow("thruDate")).getValue();
            java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());
            java.sql.Date thruDate = new java.sql.Date(thruDateInput.getTime());

            EntityCondition cn1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId);
            EntityCondition cn2 = EntityCondition.makeCondition("claimType", EntityOperator.EQUALS, claimType);
            EntityCondition cn3 = EntityCondition.makeCondition("beginDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
            EntityCondition cn4 = EntityCondition.makeCondition("endDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate);
            EntityCondition mk1 = EntityCondition.makeCondition(cn1, cn2);
            EntityCondition mk2 = EntityCondition.makeCondition(cn3, cn4);
            EntityCondition makeCondition = EntityCondition.makeCondition(mk1, mk2);
            List<GenericValue> claimHeadList = delegator.findList("ClaimHead", makeCondition, null, null, null, false);
            if (claimHeadList.size() > 1) {
                for (GenericValue gv : claimHeadList) {
                    EntityCondition con = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "CL_SAVED");
                    List<GenericValue> claimStatusList = delegator.findList("ClaimStatus", con, null, null, null, false);
                    if (UtilValidate.isNotEmpty(claimStatusList)) {
                        Messagebox.show("Claim Already Submitted On This Date", "Warning", 1, null);
                        return;
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    @SuppressWarnings("deprecation")
    public void showClaimWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericServiceException,
            GenericEntityException {
        Component approveClaimWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");
        String employeeID = gv.getString("partyId");
        String mgrPositionID = gv.getString("mgrPositionId");
        GenericValue personGV = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", employeeID));
        String firstName = personGV.getString("firstName");
        String lastName = personGV.getString("lastName");
        String fullName = firstName + " " + lastName;

        String userLoginID = userLogin.getString("userLoginId");
        String statusId = gv.getString("statusId");
        String claimId = gv.getString("claimId");
        String claimType = gv.getString("claimType");
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
        String fromDate = sdf.format(gv.getDate("beginDate"));
        String thruDate = sdf.format(gv.getDate("endDate"));
        Date tDateHidden = gv.getDate("endDate");
        String comment = gv.getString("hr_comment");
        String employeeComment = gv.getString("employeeComment");
        String adminCom = gv.getString("adminComment");
        BigInteger intfxRate = null;
        BigDecimal fxRate = null;
        String amount = "";
        String claimLimitCurrencyUomId = new String();
        Double fxRateType = gv.getDouble("fxRate");
        if (fxRateType != null)
            fxRate = new BigDecimal(com.ndz.zkoss.util.CurrencyFormatter.format(fxRateType));
        Date reimbDate = gv.getDate("reimbDate");
        String currency = gv.getString("admincurrencyUomId");

        BigDecimal amountType = gv.getBigDecimal("amount");
        if (amountType != null)
            amount = CurrencyFormatter.format(amountType.doubleValue());

        BigDecimal acceptedAmount = gv.getBigDecimal("acceptedAmount");

        BigDecimal rejectedAmount = gv.getBigDecimal("rejectedAmount");

        BigDecimal receiptsType = gv.getBigDecimal("receipts");
        GenericValue employeePosType = HumanResUtil.getEmplPositionForParty(employeeID, delegator);
        String emplPositionType = (String) employeePosType.getString("emplPositionTypeId");
        
        List<GenericValue> findClaimLimitList = delegator.findByAnd("ClaimLimit", 
        		UtilMisc.toMap("claimType", claimType, "emplPositionTypeId", emplPositionType,"employeeType",personGV.getString("employeeType"),
        				"positionCategory",personGV.getString("positionCategory")));
        if(UtilValidate.isEmpty(findClaimLimitList)){
    	   findClaimLimitList = delegator.findByAnd("ClaimLimit", 
           		UtilMisc.toMap("claimType", claimType,"employeeType",personGV.getString("employeeType"),"positionCategory", personGV.getString("positionCategory")));
        }
        GenericValue findClaimLimitGv = EntityUtil.getFirst(findClaimLimitList);
        BigDecimal claimLimitType = null;
        if (findClaimLimitGv != null) {
            claimLimitType = findClaimLimitGv.getBigDecimal("amount");
            claimLimitCurrencyUomId = findClaimLimitGv.getString("currencyUomId");
        }
        String claimLimit = "0";
        if (claimLimitType != null) {
            claimLimit = CurrencyFormatter.format(claimLimitType.doubleValue());
        }
        String receipts = null;
        if (receiptsType != null) {
            BigInteger receiptsTypeBigInt = receiptsType.toBigInteger();
            receipts = receiptsTypeBigInt.toString();
        }
        // Map<String,Object> map = new HashMap<String, Object>();
        // map.put("amountType", amountType);
        // map.put("currencyAlloted",findClaimLimitGv.getString("currencyUomId"));
        GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
        Window win = (Window) Executions.createComponents("/zul/claimManagement/approveClaim.zul", null, gv);
        win.setTitle("Process Claim");
        Label employeeId = (Label) win.getFellow("employeeId");
        employeeId.setValue(employeeID);
        
        Label mgrPositionId = (Label) win.getFellow("mgrPositionId");
        mgrPositionId.setValue(mgrPositionID);
        
        Label claimLimitLabel = (Label) win.getFellow("claimLimit");
        claimLimitLabel.setValue(claimLimit);
        Label claimLimitCurrency = (Label) win.getFellow("claimLimitCurrency");
        claimLimitCurrency.setValue(claimLimitCurrencyUomId);
        Label employeeName = (Label) win.getFellow("employeeName");
        employeeName.setValue(fullName);
        Label approveClaimId = (Label) win.getFellow("claimId");
        approveClaimId.setValue(claimId);

        Listbox savedClaimType = (Listbox) win.getFellow("claimType");
        EntityCondition condition = EntityConditionFunction.makeCondition("enumTypeId", EntityComparisonOperator.EQUALS, "CLAIM_TYPE");
        Set claimLimitToDisplay = new HashSet();
        claimLimitToDisplay.add("enumTypeId");
        claimLimitToDisplay.add("enumId");
        claimLimitToDisplay.add("description");
        List<GenericValue> claimTypes = delegator.findList("Enumeration", condition, claimLimitToDisplay, null, null, false);
        for (int i = 0; i < claimTypes.size(); i++) {
            GenericValue claim = claimTypes.get(i);
            String itemLabel = claim.getString("enumId");
            savedClaimType.appendItemApi(claim.getString("description"), itemLabel);
            if (itemLabel.equals(claimType)) {
                savedClaimType.setSelectedIndex(i);
            }
        }
        Label claimTypeValue = (Label) win.getFellow("claimTypeLabel");
        claimTypeValue.setValue(savedClaimType.getSelectedItem().getLabel());

        //new Label(sdf.format(gv.getDate("beginDate"))).setParent(row);
        Label approveFromDate = (Label) win.getFellow("fromDate");
        approveFromDate.setValue(fromDate);
        Label approveThruDate = (Label) win.getFellow("thruDate");
        approveThruDate.setValue(thruDate);
        final Label claimStatus = (Label) win.getFellow("claimStatus");
        claimStatus.setValue(statusId);

        Listbox currencyListBox = (Listbox) win.getFellow("currency");
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
        }
        // Textbox approveCurrency = (Textbox) win.getFellow("currency");
        // approveCurrency.setValue(currency);

        Datebox reimDate = (Datebox) win.getFellow("reimDate");
        reimDate.setValue(reimbDate);
        Label approveAmount = (Label) win.getFellow("amount");
        approveAmount.setValue(amount);
        Label amountCurrency = (Label) win.getFellow("amountCurrency");
        amountCurrency.setValue(gv.getString("currencyUomId"));
        Label approveReceipts = (Label) win.getFellow("receipts");
        approveReceipts.setValue(receipts);

        Decimalbox acceptedAmountText = (Decimalbox) win.getFellow("acceptedAmount");
        acceptedAmountText.setValue(acceptedAmount);

        Decimalbox rejectedAmountText = (Decimalbox) win.getFellow("rejectedAmount");
        rejectedAmountText.setValue(rejectedAmount);

        Decimalbox fixRate = (Decimalbox) win.getFellow("fixRate");
        fixRate.setValue(fxRate);
        Textbox employeeCommentTextBox = (Textbox) win.getFellow("employeeComment");
        employeeCommentTextBox.setValue(employeeComment);
        Textbox hr_comment = (Textbox) win.getFellow("comment");
        hr_comment.setValue(comment);
        Textbox adminComment = (Textbox) win.getFellow("adminComment");
        adminComment.setValue(adminCom);
        Datebox thruDateBox = (Datebox) win.getFellow("thruDateBox");
        thruDateBox.setValue(tDateHidden);

		/* Check for the User Permission */
        String hasPermission = ((Label) win.getFellow("permissionId")).getValue();
        Boolean isAdmin = new Boolean(hasPermission);
        String hasBothPermission = ((Label) win.getFellow("permissionIdForBoth")).getValue();
        Boolean both = new Boolean(hasBothPermission);

       // if (!isAdmin) {
        if((!isAdmin || both) && "CL_SUBMITTED".equals(claimStatus.getValue())){
            Button approveClaimButton = (Button) win.getFellow("approveClaimButton");
            approveClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    claimStatus.setValue("CL_MGR_APPROVED");
                    ClaimManagementController.processClaim(event, "1", "2");
                }
            });

            Button rejectClaimButton = (Button) win.getFellow("rejectClaimButton");
            rejectClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    claimStatus.setValue("CL_MGR_REJECTED");
                    ClaimManagementController.processClaim(event, "1", "1");
                }
            });
      //  } else {
        }else if(isAdmin && ("CL_MGR_APPROVED".equals(claimStatus.getValue()))) {

            Button approveClaimButton = (Button) win.getFellow("approveClaimButton");
            approveClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    claimStatus.setValue("CL_ADM_APPROVED");
                    ClaimManagementController.processClaim(event, "1", "2");
                }
            });

            Button rejectClaimButton = (Button) win.getFellow("rejectClaimButton");
            rejectClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    claimStatus.setValue("CL_ADM_REJECTED");
                    ClaimManagementController.processClaim(event, "1", "2");
                }
            });

        }
        GenericValue claimStatusGv = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", claimStatus.getValue()));
        Label claimStatusDescription = (Label) win.getFellow("claimStatusDescription");
        claimStatusDescription.setValue(claimStatusGv.getString("description"));
        win.doModal();
    }


    public static void downloadAttachment(String claimId, GenericDelegator delegator) throws GenericEntityException, IOException,
            InterruptedException {
        GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
        if (claimGv.get("attachment") != null) {
            byte[] attachmentData = (byte[]) claimGv.get("attachment");
            AImage image = new AImage(claimGv.getString("attachmentName"), attachmentData);
            Filedownload.save(image.getByteData(), null, claimGv.getString("attachmentName"));
        } else
            Messagebox.show("No Receipt Found", "Warning", 1, null);

    }


    public static void removeAttachment(Label label, String claimId, Delegator delegator) throws InterruptedException, GenericEntityException {
        attachmentData = null;
        attachmentName = null;
        if (UtilValidate.isNotEmpty(claimId)) {
            GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
            claimGv.put("attachment", attachmentData);
            claimGv.put("attachmentName", attachmentName);
            delegator.store(claimGv);
        }
        Messagebox.show("Attachment Removed Successfully", "Success", 1, null);
        label.setValue(null);

    }

    public static String getCfoEmailId(Delegator delegator)throws GenericEntityException, GenericServiceException, InterruptedException{
    	 List  personList=delegator.findByAnd("Person", UtilMisc.toMap("isCfo","Y"));
     	if(UtilValidate.isEmpty(personList)){
     		Messagebox.show("CFO is not configured for this department", "Error",1, null);
 			return null;
     	}
     	GenericValue personGv=(GenericValue)personList.get(0);
     	String partyId=personGv.getString("partyId");
     	List<GenericValue> contactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
         if (UtilValidate.isEmpty(contactMechPurposes)) {
             Messagebox.show("Email cannot be sent;Primary Email for CFO is not configured", "Error", 1, null);
             return null;
         }
         String contactMechId = contactMechPurposes.get(0).getString("contactMechId");
         for (GenericValue contactMechPurposeGv : contactMechPurposes) {
             if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(contactMechPurposeGv.getString("contactMechId"))) {
                 contactMechId = contactMechPurposeGv.getString("contactMechId");
             }
         }
         List<GenericValue> contactMechGvs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "contactMechId", contactMechId));
         GenericValue contactMechGv = (GenericValue)contactMechGvs.get(0);
         if(UtilValidate.isNotEmpty(contactMechGv.getString("infoString")))
         	return contactMechGv.getString("infoString");
         else{
         	Messagebox.show("Email cannot be sent;Primary Email for CFO is not configured", "Error", 1, null);
            return null;
         }

    	
    }
    public static void sendMailToCFO(String claimId, Delegator delegator) throws GenericEntityException, GenericServiceException, InterruptedException  {
        GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
        String cfoMailId=getCfoEmailId(delegator);
        if(UtilValidate.isEmpty(cfoMailId))
            return;
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        Delegator delegator2 = userLogin.getDelegator();
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();
        GenericValue personGv = delegator2.findOne("Person", UtilMisc.toMap("partyId", claimGv.getString("partyId")), false);
        boolean result = MailUtil.sendClaimApprovalMailToCFO(userLogin.getString("partyId"), delegator2, "claimNotificationEmail.ftl", cfoMailId, UtilMisc.toMap("claimGv", claimGv, "personGv", personGv), dispatcher);
        if (result) {
            Messagebox.show("Mail Sent Successfully", "Success", 1, null);
        }
}

    public static void processClaim(final Event event, final String statusType, final String adminStatusType) throws InterruptedException {
        Messagebox.show("Claim Approved/Rejected Can't Be Modified. \n Do You Want To Continue?", "Warning", Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {
                    try {
                        Component approveClaimWindow = event.getTarget();
                        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

                        String statusId = (String) ((Label) approveClaimWindow.getFellow("claimStatus")).getValue();

                        String claimAmountType = (String) ((Label) approveClaimWindow.getFellow("hiddenAmount")).getValue();
                        String currencyUomId = (String) ((Listbox) approveClaimWindow.getFellow("currency")).getSelectedItem().getValue();
                        String mgrPositionId=(String) ((Label) approveClaimWindow.getFellow("mgrPositionId")).getValue();
                        Double amount = new Double(claimAmountType);
                        BigDecimal acceptedAmountType = ((Decimalbox) approveClaimWindow.getFellow("acceptedAmount")).getValue();
                        Double acceptedAmount = 0.0;
                        if (acceptedAmountType != null) {
                            acceptedAmount = acceptedAmountType.doubleValue();
                        }
                        BigDecimal rejectedAmountType = ((Decimalbox) approveClaimWindow.getFellow("rejectedAmount")).getValue();
                        Double rejectedAmount = 0.0;
                        if (rejectedAmountType != null) {
                            rejectedAmount = rejectedAmountType.doubleValue();
                        }
                        java.util.Date reimbDateType = (java.util.Date) ((Datebox) approveClaimWindow.getFellow("reimDate")).getValue();
                        // int d = reimbDateType.getDay();
                        // if((d==0)||(d==6)){
                        // Datebox datebox =(Datebox)
                        // approveClaimWindow.getFellow("reimDate");
                        // datebox.setDisabled(true);
                        // }
                        Date currentDate = new Date();
                        java.sql.Date reimDate = new java.sql.Date(currentDate.getTime());
                        if (reimbDateType != null) {
                            reimDate = new java.sql.Date(reimbDateType.getTime());
                        }
                        String hr_comment = (String) ((Textbox) approveClaimWindow.getFellow("comment")).getValue();
                        String adminComment = (String) ((Textbox) approveClaimWindow.getFellow("adminComment")).getValue();
                        String claimId = (String) ((Label) approveClaimWindow.getFellow("claimId")).getValue();
                        Double fixRate = new Double("0.0");
                        BigDecimal fixRateType = (BigDecimal) ((Decimalbox) approveClaimWindow.getFellow("fixRate")).getValue();
                        if (fixRateType != null) {
                            fixRate = fixRateType.doubleValue();
                        }
                        List claimList = null;
                        GenericValue claimGv = null;
                        @SuppressWarnings("unused")
                        String emplPosTypeIdOfParty = null;
                        claimList = delegator.findByAnd("ClaimHead", UtilMisc.toMap("claimId", claimId));
                        claimGv = EntityUtil.getFirst(claimList);
                        String partyId = (String) claimGv.getString("partyId");
                        String claimType = (String) claimGv.getString("claimType");
                        GenericValue emplPosTypeIdOfPartyGv = HumanResUtil.getEmplPositionForParty(partyId, delegator);
                        if (emplPosTypeIdOfPartyGv != null) {
                            emplPosTypeIdOfParty = emplPosTypeIdOfPartyGv.getString("emplPositionTypeId");
                        }

                        Map claimMap = UtilMisc.toMap("partyId", partyId, "claimTypeId", claimType);
                        List claimInfoList = delegator.findByAnd("EmployeeClaimInfo", claimMap);
                        GenericValue employeeClaimInfoGv = EntityUtil.getFirst(claimInfoList);
                        
                        GenericValue personGV = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
                        List<GenericValue> claimLimitInfoList = delegator.findByAnd("ClaimLimit", 
                        		UtilMisc.toMap("claimType", claimType, "emplPositionTypeId", emplPosTypeIdOfParty,"employeeType",personGV.getString("employeeType"),
                        				"positionCategory",personGV.getString("positionCategory")));
                        if(UtilValidate.isEmpty(claimLimitInfoList)){
                        	claimLimitInfoList = delegator.findByAnd("ClaimLimit", 
                           		UtilMisc.toMap("claimType", claimType,"employeeType",personGV.getString("employeeType"),"positionCategory", personGV.getString("positionCategory")));
                        }
                         
                        GenericValue claimLimitInfoGv = null;
                        claimLimitInfoGv = EntityUtil.getFirst(claimLimitInfoList);
                        BigDecimal claimLimit = new BigDecimal(0);
                        if (claimLimitInfoGv != null) {
                            claimLimit = claimLimitInfoGv.getBigDecimal("amount");
                        }

                        BigDecimal availedAmount = new BigDecimal(0);
                        BigDecimal balanceAmount = new BigDecimal(0);
                        BigDecimal approvedAmount = new BigDecimal(acceptedAmount);

                        if ("CL_ADM_APPROVED".equals(statusId)) {
                            List claimLimitList = delegator.findByAnd("ClaimLimit",
                                    UtilMisc.toMap("emplPositionTypeId", emplPosTypeIdOfParty, "claimType", claimType));
                            // if (claimLimitList.size() <= 0) {
                            // Messagebox.show("Claim Limit not exist;cannot be processed",
                            // "Error", 1, null);
                            // return;
                            // }
                            if (employeeClaimInfoGv != null) {
                                availedAmount = employeeClaimInfoGv.getBigDecimal("availedAmount");
                            }
                            availedAmount = availedAmount.add(approvedAmount);
                            balanceAmount = claimLimit.subtract(availedAmount);
                            // if (balanceAmount.doubleValue() <= 0.0) {
                            // Messagebox.show("Claim Limit Exceeds;cannot be processed",
                            // "Error", 1, null);
                            // return;
                            // }
                            GenericValue claimInfoGv = delegator.makeValue("EmployeeClaimInfo", UtilMisc.toMap("claimTypeId", claimType,
                                    "balanceAmount", balanceAmount, "availedAmount", availedAmount, "positionType", emplPosTypeIdOfParty, "partyId",
                                    partyId, "managerId", mgrPositionId));
                            delegator.createOrStore(claimInfoGv);
                        }

                        Map approveClaim = UtilMisc.toMap("userLogin", userLogin, "statusId", statusId, "amount", amount, "acceptedAmount",
                                acceptedAmount, "rejectedAmount", rejectedAmount, "claimId", claimId, "hr_comment", hr_comment, "reimbDate", reimDate,
                                "fxRate", fixRate, "statusType", statusType, "adminStatusType", adminStatusType, "adminComment", adminComment,
                                "admincurrencyUomId", currencyUomId);
                        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
                        dispatcher.runSync("processClaimService", approveClaim);
                        Messagebox.show("Claim Processed Successfully", "Success", 1, null);
                        Events.postEvent("onClick", Path.getComponent("/searchPanel//searchPerCompany"), null);
                        Component comp = approveClaimWindow.getFellow("approveClaimWindow");
                        comp.detach();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public static void showUpdateClaimWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

        Component approveClaimWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

        //String employeeID = userLogin.getString("partyId");
        String employeeID = gv.getString("partyId");
        String mgrPositionID=gv.getString("mgrPositionId");
        String userLoginID = userLogin.getString("userLoginId");
        GenericValue gv1 = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", gv.getString("statusId")));
        String statusId = gv1.getString("description");
        String claimId = gv.getString("claimId");
        String employeeComment = gv.getString("employeeComment");
        Integer i_receipts = gv.getDouble("receipts").intValue();
        String receipts = i_receipts.toString();
        java.sql.Date fromDate = gv.getDate("beginDate");
        java.sql.Date thruDate = gv.getDate("endDate");
        BigDecimal amountType = gv.getBigDecimal("amount");
        Map arg = new HashMap();
        arg.put("gv", gv);
        Window win = (Window) Executions.createComponents("/zul/claimManagement/updateClaim.zul", null, arg);
        Label employeeId = (Label) win.getFellow("employeeId");
        employeeId.setValue(employeeID);
        Label mgrPositionId=(Label) win.getFellow("mgrPositionId");
        mgrPositionId.setValue(mgrPositionID);
        Label savedLoanId = (Label) win.getFellow("claimId");
        savedLoanId.setValue(claimId);
        Label loanStatus = (Label) win.getFellow("claimStatus");
        loanStatus.setValue(statusId);
        Datebox savedFromDate = (Datebox) win.getFellow("fromDate");
        savedFromDate.setValue(fromDate);
        Datebox savedThruDate = (Datebox) win.getFellow("thruDate");
        savedThruDate.setValue(thruDate);
        Decimalbox savedLoanAmount = (Decimalbox) win.getFellow("amount");
        savedLoanAmount.setValue(amountType.toString());
        Textbox applyClaimHR_COMMENT = (Textbox) win.getFellow("applyClaimHR_COMMENT");
        applyClaimHR_COMMENT.setValue(employeeComment);
        Textbox applyClaimReceipts = (Textbox) win.getFellow("applyClaimReceipts");
        applyClaimReceipts.setValue(receipts);
        win.doModal();

    }

    public static void updateClaim(Event event,boolean isSaveOnly) {
         try {
            Component editClaimWindow = event.getTarget();
            String status=updateClaim1(event,isSaveOnly);
            Events.postEvent("onClick$searchButton", editClaimWindow.getPage().getFellow("searchPanel"), null);
         //   if("success".equals(claimUpdateStatus))
         //  	Messagebox.show(Labels.getLabel("Hrms_ClaimSuccessfullyUpdated"), "Success", 1, null);
            editClaimWindow.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String updateClaim1(Event event,boolean isSaveOnly) {
         try {
        	final Component editClaimWindow = event.getTarget();
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            String claimId = (String) ((Label) editClaimWindow.getFellow("claimId")).getValue();
            //String employeeId = userLogin.getString("partyId");
            String employeeId = (String) ((Label) editClaimWindow.getFellow("employeeId")).getValue();
            Listitem savedClaimType = (Listitem) ((Listbox) editClaimWindow.getFellow("claimType")).getSelectedItem();
            String claimType = (String) savedClaimType.getValue();
            String claimStatus = (String) ((Label) editClaimWindow.getFellow("claimStatus")).getValue();
            Listitem currencyType = (Listitem) ((Listbox) editClaimWindow.getFellow("currency")).getSelectedItem();
            String currency = (String) currencyType.getValue();
            String amountType = ((Decimalbox) editClaimWindow.getFellow("amount")).getValue().toString();
            Double amount = new Double(amountType);
            Date fromDateInput = (Date) ((Datebox) editClaimWindow.getFellow("fromDate")).getValue();
            Date thruDateInput = (Date) ((Datebox) editClaimWindow.getFellow("thruDate")).getValue();

            GenericValue emplPositionGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
            String emplPosType = emplPositionGv == null ? null : emplPositionGv.getString("emplPositionTypeId");

            java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());
            java.sql.Date thruDate = new java.sql.Date(thruDateInput.getTime());
            String applyClaimHR_COMMENT = (String) ((Textbox) editClaimWindow.getFellow("applyClaimHR_COMMENT")).getValue();
            String applyClaimReceipts = (String) ((Textbox) editClaimWindow.getFellow("applyClaimReceipts")).getValue();
            Double l_receipts = Double.valueOf(applyClaimReceipts);
            final LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            Map updateClaim = null;
                        
            GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), false);
        	String positionCategory = personGV.getString("positionCategory");
        	String employeeType = personGV.getString("employeeType");
        	
            List<GenericValue> emplClaimInfo = delegator.findByAnd("ClaimLimit",
            		UtilMisc.toMap("claimType", claimType, "emplPositionTypeId", emplPosType, "employeeType",employeeType,"positionCategory",positionCategory));
           
            if (UtilValidate.isEmpty(emplClaimInfo)) {
            	emplClaimInfo = delegator.findByAnd("ClaimLimit",
              		UtilMisc.toMap("claimType", claimType,"employeeType",employeeType,"positionCategory",positionCategory));
            }
           if (UtilValidate.isEmpty(emplClaimInfo)) {
                Messagebox.show("Claim Limit Does Not Exist, Cannot Apply", "Error", 1, null);
                return "error";
            }
           boolean isClaimLimitExceeded=false;
            GenericValue emplClaimLimitGv = EntityUtil.getFirst(emplClaimInfo);
            GenericValue emplClaimInfoGv = delegator.findOne("EmployeeClaimInfo",UtilMisc.toMap("partyId",employeeId,"claimTypeId",claimType),false);
            if(UtilValidate.isNotEmpty(emplClaimInfoGv)){
                if(emplClaimInfoGv.getDouble("balanceAmount").equals(0.0) || amount > emplClaimInfoGv.getDouble("balanceAmount")){
                	isClaimLimitExceeded=true;
                }
            }else{
                if(emplClaimLimitGv.getDouble("amount")<amount){
                	isClaimLimitExceeded=true;
                }
            }
         
            

            EntityCondition employmentCondition1 = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId"));
            EntityCondition employmentCondition2 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(fromDate.getTime()));
            EntityCondition employmentCondition = EntityCondition.makeCondition(employmentCondition1, EntityOperator.AND, employmentCondition2);

            List<GenericValue> employmentList = delegator.findList("Employment", employmentCondition, null, null, null, false);
            if (UtilValidate.isEmpty(employmentList)) {
                Messagebox.show("Claim Can not Be Submitted Behind The Joining Date", "Error", 1, null);
                return "error";
            }

            updateClaim = UtilMisc.toMap("userLogin", userLogin, "claimId", claimId, "claimType", claimType, "beginDate", fromDate, "endDate", thruDate,
                    "amount", amount, "currencyUomId", currency, "admincurrencyUomId", currency, "employeeComment", applyClaimHR_COMMENT, "receipts",
                    l_receipts);
            updateClaim.remove("attachment");
            final Map context=updateClaim;
            final String claimValue=claimId;
            final boolean saveOnly=isSaveOnly;
            if(isClaimLimitExceeded){
            	Messagebox.show("You do not have sufficient claim limit. \n  Do you want to apply ?", "Warning", Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
                        new EventListener() {
                            public void onEvent(Event evt) throws GenericServiceException, InterruptedException, GenericEntityException {
                                if ("onYes".equals(evt.getName())) {
                                	updateEmployeeClaim(dispatcher,context,delegator,claimValue,editClaimWindow,saveOnly);
                                	java.lang.Object c = org.zkoss.zk.ui.Path.getComponent("/searchPanel/searchButton");
    								org.zkoss.zk.ui.event.Events.postEvent("onClick",(org.zkoss.zk.ui.Component) c,null);
                                }
                                else{
                                	java.lang.Object c = org.zkoss.zk.ui.Path.getComponent("/searchPanel/searchButton");
    								org.zkoss.zk.ui.event.Events.postEvent("onClick",(org.zkoss.zk.ui.Component) c,null);
                                	return;
                                }
                                
                            }});
            }else{
            	updateEmployeeClaim(dispatcher,context,delegator,claimValue,editClaimWindow,saveOnly);
            }
   /*
            dispatcher.runSync("updateClaimService", updateClaim);

            GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
            if (attachmentData != null && attachmentName != null) {
                claimGv.put("attachment", attachmentData);
                claimGv.put("attachmentName", attachmentName);
                delegator.store(claimGv);
                attachmentData = null;
                attachmentName = null;
            }
*/        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    public static void updateEmployeeClaim(LocalDispatcher dispatcher,Map context,GenericDelegator delegator, String claimId,
    		Component editClaimWindow,boolean isSaveOnly) throws GenericServiceException, InterruptedException, GenericEntityException{
 	    dispatcher.runSync("updateClaimService", context);
        GenericValue claimGv = delegator.findOne("ClaimHead", UtilMisc.toMap("claimId", claimId), false);
        if (attachmentData != null && attachmentName != null) {
            claimGv.put("attachment", attachmentData);
            claimGv.put("attachmentName", attachmentName);
            delegator.store(claimGv);
            attachmentData = null;
            attachmentName = null;
        }
        if(isSaveOnly)
        	Messagebox.show(Labels.getLabel("Hrms_ClaimSuccessfullyUpdated"), "Success", 1, null);
        else
        	submitEmployeeClaim(editClaimWindow);
 }


    
    public static void deleteClaim(Event event, GenericValue gv, final Button btn) throws GenericEntityException, InterruptedException {

        Component searchPanel = event.getTarget();
        final String claimId = gv.getString("claimId");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);

        Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {

                    try {

                        List claims = delegator.findByAnd("ClaimStatus", UtilMisc.toMap("claimId", claimId));
                        if (claims != null && claims.size() > 0) {
                            delegator.removeByAnd("ClaimStatus", UtilMisc.toMap("claimId", claimId));
                            delegator.removeByAnd("ClaimHead", UtilMisc.toMap("claimId", claimId));
                            Events.postEvent("onClick", btn, null);
                        }

                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        Messagebox.show(Labels.getLabel("HRMS_CLAIMDELETEMESSAGE"), "Success", 1, null);
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
