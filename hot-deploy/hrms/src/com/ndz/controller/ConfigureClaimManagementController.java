package com.ndz.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.component.party.ClaimLimitRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.component.party.EmployeePositionTypeRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class ConfigureClaimManagementController extends GenericForwardComposer {
	public void doAfterCompose( Component allocateClaimWindow ) throws Exception {
		super.doAfterCompose(allocateClaimWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CLAIM_TYPE");

		EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");

		Set claimLimitToDisplay = new HashSet();
		Set employeePositionTypeToDisplay = new HashSet();
		Set currencyTypeToDisplay = new HashSet();

		claimLimitToDisplay.add("enumId");
		claimLimitToDisplay.add("description");

		employeePositionTypeToDisplay.add("emplPositionTypeId");
		employeePositionTypeToDisplay.add("description");

		currencyTypeToDisplay.add("uomId");
		currencyTypeToDisplay.add("description");

		List<GenericValue> claimLimit = delegator.findList("Enumeration", condition, claimLimitToDisplay, null, null, false);
		claimLimit.add(0, null);
		Object claimTypeArray = claimLimit.toArray(new GenericValue[claimLimit.size()]);
		SimpleListModel claimLimitList = new SimpleListModel(claimLimit);

		Listbox searchClaimType = (Listbox) allocateClaimWindow.getFellow("allocateClaimType");
		searchClaimType.setModel(claimLimitList);
		searchClaimType.setItemRenderer(new ClaimLimitRenderer());

		List<GenericValue> employeePositionType = delegator.findList("EmplPositionType", null, employeePositionTypeToDisplay, null, null, false);
		employeePositionType.add(0, null);
		Object employeePositionTypeArray = employeePositionType.toArray(new GenericValue[employeePositionType.size()]);
		SimpleListModel employeePositionTypeList = new SimpleListModel(employeePositionType);
		Listbox employeePositionTypeId = (Listbox) allocateClaimWindow.getFellow("allocatePositionType");
		employeePositionTypeId.setModel(employeePositionTypeList);
		employeePositionTypeId.setItemRenderer(new EmployeePositionTypeRenderer());

		List<GenericValue> currency = delegator.findList("Uom", currencyCondition, currencyTypeToDisplay, null, null, false);
		currency.add(0, null);
		Object currencyArray = currency.toArray(new GenericValue[currency.size()]);
		SimpleListModel currencyList = new SimpleListModel(currency);

		Listbox currencyListBox = (Listbox) allocateClaimWindow.getFellow("allocateCurrencyType");
		currencyListBox.setModel(currencyList);
		currencyListBox.setItemRenderer(new CurrencyRenderer());

	}

	public static void allocateClaim( Event event ) {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component allocateClaimWindow = event.getTarget();
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			Listitem claimTypeInput = ((Listbox) allocateClaimWindow.getFellow("allocateClaimType")).getSelectedItem();
			String claimType = (String) claimTypeInput.getValue();
			Listitem emplPosTypeInput = ((Listbox) allocateClaimWindow.getFellow("allocatePositionType")).getSelectedItem();
			String emplPosTypeId = emplPosTypeInput != null ? (String) emplPosTypeInput.getValue() : null;
			BigDecimal amountType = ((Decimalbox) allocateClaimWindow.getFellow("allocateClaimAmount")).getValue();
			Double amount = amountType.doubleValue();
			Listitem currencyType = ((Listbox) allocateClaimWindow.getFellow("allocateCurrencyType")).getSelectedItem();
			String currency = (String) currencyType.getValue();
			String hrComment = ((Textbox) allocateClaimWindow.getFellow("allocateHRComment")).getValue();
			
			Radio emplyoeeTypeGroup = ((Radiogroup) allocateClaimWindow.getFellow("emp_administration")).getSelectedItem();
	        String employeeType = (String)emplyoeeTypeGroup.getValue();

	        Comboitem positionCategoriesList = ((Combobox) allocateClaimWindow.getFellow("positionCategories")).getSelectedItem();
	        String positionCategory = (String) positionCategoriesList.getValue();

			
			Map claimLimit = UtilMisc.toMap("userLogin", userLogin, "claimType", claimType, "amount", amount, "currencyUomId", currency, "emplPositionTypeId", emplPosTypeId, 
					"hr_comment", hrComment ,"employeeType",employeeType,"positionCategory",positionCategory);
			Map result = null;
			Map checkDuplicateClaimLimit = UtilMisc.toMap("claimType", claimType, "emplPositionTypeId", emplPosTypeId,
					"employeeType",employeeType,"positionCategory",positionCategory);
			
			List duplicateClaimList = delegator.findByAnd("ClaimLimit", checkDuplicateClaimLimit);
			if (duplicateClaimList.size() <= 0) {
				LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
				result = dispatcher.runSync("createClaimLimitService", claimLimit);
				String limitId = (String) result.get("limitId");
				Messagebox.show("Claim Limit Successfully Created", "Success", 1, null);
			} else {
				Messagebox.show("Claim Limit already exists", "Error", 1, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void showClaimLimitWindow( Event event, GenericValue gv ) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {
		Window win = (Window) Executions.createComponents("/zul/claimManagement/updateClaimLimit.zul", null, null);
		win.setTitle("Edit Claim Limit");

		Component editLoanLimitWindow = event.getTarget();
		String limitId = gv.getString("limitId");
		String claimType = gv.getString("claimType");
		String emplPosTypeId = gv.getString("emplPositionTypeId");
		String currency = gv.getString("currencyUomId");
		BigDecimal amount = gv.getBigDecimal("amount");
		String updatedBy = gv.getString("updatedBy");
		String comment = gv.getString("hr_comment");
        String employeeType = gv.getString("employeeType");
        String positionCategory = gv.getString("positionCategory");

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityConditionFunction.makeCondition("enumTypeId", EntityComparisonOperator.EQUALS, "CLAIM_TYPE");
		EntityCondition employeePositionTypeCondition = EntityCondition.makeCondition("hasTable", EntityOperator.EQUALS, "N");

		Set claimLimitToDisplay = new HashSet();
		Set employeePositionTypeToDisplay = new HashSet();

		claimLimitToDisplay.add("enumTypeId");
		claimLimitToDisplay.add("enumId");
		claimLimitToDisplay.add("description");

		employeePositionTypeToDisplay.add("emplPositionTypeId");
		employeePositionTypeToDisplay.add("description");

		List<GenericValue> claimTypes = delegator.findList("Enumeration", condition, claimLimitToDisplay, null, null, false);
		List<GenericValue> employeePositionTypes = delegator.findList("EmplPositionType", null, employeePositionTypeToDisplay, null, null, false);

		Label savedLimitId = (Label) win.getFellow("limitId");
		savedLimitId.setValue(limitId);
		Label emplId = (Label) win.getFellow("emplId");
		emplId.setValue(updatedBy);
		
        if("Administrative".equals(employeeType)){
            Radio administrativeEmployeeType = (Radio)win.getFellow("emp_administration_administrative");
            administrativeEmployeeType.setSelected(true);
        }else if("Non-Administrative".equals(employeeType)){
            Radio nonAdministrativeEmployeeType = (Radio)win.getFellow("emp_administration_non_administrative");
            nonAdministrativeEmployeeType.setSelected(true);
        }

        Combobox savedPositionCategory =  (Combobox)win.getFellow("positionCategories");
        savedPositionCategory.setValue(positionCategory);

		Listbox savedClaimType = (Listbox) win.getFellow("claimType");
		Listitem claimTypeItem = new Listitem();
		claimTypeItem.setLabel(claimType);
		claimTypeItem.setValue(claimType);

		for (int i = 0; i < claimTypes.size(); i++) {
			GenericValue claim = claimTypes.get(i);
			String itemLabel = claim.getString("enumId");
			savedClaimType.appendItemApi(claim.getString("description"), itemLabel);
			if (itemLabel.equals(claimType)) {
				savedClaimType.setSelectedIndex(i);
			}
		}

		Listbox savedPosType = (Listbox) win.getFellow("posTypeId");
		Listitem posTypeItem = new Listitem();
		posTypeItem.setParent(savedPosType);
		
		for (int i = 0; i < employeePositionTypes.size(); i++) {
			GenericValue posType = employeePositionTypes.get(i);
			String itemLabel = posType.getString("emplPositionTypeId");
			savedPosType.appendItemApi(posType.getString("description"), itemLabel);
			if (itemLabel.equals(emplPosTypeId)) {
				savedPosType.setSelectedIndex(i+1);
			}
		}
		Textbox savedCurrency = (Textbox) win.getFellow("currency");
		savedCurrency.setValue(currency);

		Textbox hrComment = (Textbox) win.getFellow("hrComment");
		hrComment.setValue(comment);

		EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");
		Set currencyTypeToDisplay = new HashSet();
		currencyTypeToDisplay.add("uomId");
		currencyTypeToDisplay.add("description");
		List<GenericValue> currencyList = delegator.findList("Uom", currencyCondition, currencyTypeToDisplay, null, null, false);

		Listbox currencyListBox = (Listbox) win.getFellow("currencyListBox");

		for (int i = 0; i < currencyList.size(); i++) {
			GenericValue posType = currencyList.get(i);
			String itemLabel = posType.getString("uomId");
			currencyListBox.appendItemApi(posType.getString("description"), itemLabel);
			if (itemLabel.equals(currency)) {
				currencyListBox.setSelectedIndex(i);
			}
		}

		Decimalbox savedClaimAmount = (Decimalbox) win.getFellow("amount");
		savedClaimAmount.setValue(amount);

		win.doModal();
	}

	public static void updateClaimLimit( Event event ) {
		System.out.println("**********Update Claim Limit Event Called******");
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component editClaimLimitWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String employeeId = userLogin.getString("partyId");

			BigDecimal amountType = ((Decimalbox) editClaimLimitWindow.getFellow("amount")).getValue();
			Double amount = amountType.doubleValue();
			Listitem claimTypeInput = ((Listbox) editClaimLimitWindow.getFellow("claimType")).getSelectedItem();
			String claimType = (String) claimTypeInput.getValue();

			// String currency = ((Textbox)
			// editClaimLimitWindow.getFellow("currency")).getValue();
			Listitem posTypeInput = ((Listbox) editClaimLimitWindow.getFellow("posTypeId")).getSelectedItem();
			String posTypeId = UtilValidate.isNotEmpty(posTypeInput) ?  (String) posTypeInput.getValue() : null;
			
			Listitem currencyListBox = ((Listbox) editClaimLimitWindow.getFellow("currencyListBox")).getSelectedItem();
			String currency = (String) currencyListBox.getValue();

			String limitId = ((Label) editClaimLimitWindow.getFellow("limitId")).getValue();

			String hrComment = ((Textbox) editClaimLimitWindow.getFellow("hrComment")).getValue();
			
			Radio emplyoeeTypeGroup = ((Radiogroup) editClaimLimitWindow.getFellow("emp_administration")).getSelectedItem();
	        String employeeType = (String)emplyoeeTypeGroup.getValue();

	        Comboitem positionCategoriesList = ((Combobox) editClaimLimitWindow.getFellow("positionCategories")).getSelectedItem();
	        String positionCategory = (String) positionCategoriesList.getValue();


			
			List oldClaimLimitList = delegator.findByAnd("ClaimLimit", UtilMisc.toMap("limitId", limitId, "claimType", claimType));
			/*List claimInfoList = delegator.findByAnd("EmployeeClaimInfo", UtilMisc.toMap("claimTypeId", claimType,"positionType",posTypeId));
			GenericValue claimInfoGv = EntityUtil.getFirst(claimInfoList);
			BigDecimal balanceAmount = claimInfoGv.getBigDecimal("balanceAmount");
			GenericValue oldClaimLimitGv = EntityUtil.getFirst(oldClaimLimitList);
			BigDecimal oldClaimLimit = oldClaimLimitGv.getBigDecimal("amount");
			BigDecimal newClaimLimit = new BigDecimal(amount);
			BigDecimal diffAmount = new BigDecimal(0);
			if(newClaimLimit.compareTo(oldClaimLimit)>1){
				diffAmount = newClaimLimit.subtract(oldClaimLimit);
			balanceAmount = balanceAmount.add(diffAmount);
			}
			else{
				diffAmount = oldClaimLimit.subtract(newClaimLimit);
				balanceAmount = balanceAmount.subtract(diffAmount);
			}
			if(claimInfoList.size()>0){
			GenericValue newClaimAmountInfoGv = delegator.makeValue("EmployeeClaimInfo", UtilMisc.toMap("claimTypeId", claimType,"positionType",posTypeId,"balanceAmount",balanceAmount));
			delegator.createOrStore(newClaimAmountInfoGv);
			}*/
			Map updateELoanLimit = UtilMisc
					.toMap("userLogin", userLogin, "claimType", claimType, "amount", amount, "currencyUomId", currency, "limitId", limitId,
							"emplPositionTypeId", posTypeId, "hr_comment", hrComment,"employeeType",employeeType,"positionCategory",positionCategory);

			Map result = null;
			List<GenericValue> duplicateClaimList = null;
			Map checkDuplicateClaimLimit = UtilMisc.toMap("claimType", claimType, "emplPositionTypeId", posTypeId,
					"employeeType",employeeType,"positionCategory",positionCategory);
			duplicateClaimList = delegator.findByAnd("ClaimLimit", checkDuplicateClaimLimit);

			if (UtilValidate.isNotEmpty(duplicateClaimList)) {
				String existsPartyLimitId = duplicateClaimList.get(0).getString("limitId");
				if (existsPartyLimitId.equals(limitId)) {
					LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
					result = dispatcher.runSync("updateClaimLimitService", updateELoanLimit);
					System.out.println("\n\n\n\n\n\n Claim Limit\n\n" + result);
					Messagebox.show("Claim Limit Successfully Updated", "Success", 1, null);
				} else {
					Messagebox.show("Claim Limit already exist", "Error", 1, null);
				}
			} else {
				LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
				result = dispatcher.runSync("updateClaimLimitService", updateELoanLimit);
				System.out.println("\n\n\n\n\n\n Claim Limit\n\n" + result);
				Messagebox.show("Claim Limit Successfully Updated", "Success", 1, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void deleteClaimLimit( Event event, GenericValue gv, final Button btn ) throws GenericEntityException, InterruptedException {
		final String claimLimitId = (String) gv.getString("limitId");
		final String claimType = (String)gv.getString("claimType");
		final String positionTypeId = (String)gv.getString("emplPositionTypeId");
		final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent( Event evt ) {
				if ("onYes".equals(evt.getName())) {

					try {
						delegator.removeByAnd("ClaimLimit", UtilMisc.toMap("limitId", claimLimitId));
						delegator.removeByAnd("EmployeeClaimInfo", UtilMisc.toMap("claimTypeId", claimType, "positionType", positionTypeId));
						Events.postEvent("onClick", btn, null);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Messagebox.show("Claim Limit deleted successfully", "Success", 1, null);
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
