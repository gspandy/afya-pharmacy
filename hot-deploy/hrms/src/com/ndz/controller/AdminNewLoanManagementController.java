package com.ndz.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

import com.ndz.component.party.ClaimTypeRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.component.party.EmployeePositionTypeRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class AdminNewLoanManagementController extends GenericComposer{
	
	public void doAfterCompose(Component newLoanWindow) throws Exception {
		super.doAfterCompose(newLoanWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("enumTypeId",
				EntityOperator.EQUALS, "ELOAN_TYPE");
		EntityCondition employeePositionTypeCondition = EntityCondition.makeCondition(
				"hasTable", EntityOperator.EQUALS, "N");
		EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId",
				EntityOperator.EQUALS, "CURRENCY_MEASURE");

		Set loanTypeToDisplay = new HashSet();
		Set employeePositionTypeToDisplay = new HashSet();
		Set currencyTypeToDisplay = new HashSet();

		loanTypeToDisplay.add("enumTypeId");
		loanTypeToDisplay.add("description");

		employeePositionTypeToDisplay.add("emplPositionTypeId");
		employeePositionTypeToDisplay.add("description");
		
		currencyTypeToDisplay.add("uomId");
		currencyTypeToDisplay.add("description");

		List loanType = delegator.findList("Enumeration", condition,
				loanTypeToDisplay, null, null, false);
		Object loanTypeArray = loanType.toArray(new GenericValue[loanType
				.size()]);
		SimpleListModel loanLimitList = new SimpleListModel(loanType);

		Listbox applyLoanType = (Listbox) newLoanWindow
				.getFellow("applyLoanType");
		applyLoanType.setModel(loanLimitList);
		applyLoanType.setItemRenderer(new ClaimTypeRenderer());

		List employeePositionType = delegator.findList("EmplPositionType",
				employeePositionTypeCondition, employeePositionTypeToDisplay, null, null,
				false);
		Object employeePositionTypeArray = employeePositionType
				.toArray(new GenericValue[employeePositionType.size()]);
		SimpleListModel employeePositionTypeList = new SimpleListModel(
				employeePositionType);
		Listbox employeePositionTypeId = (Listbox) newLoanWindow
				.getFellow("newLoanPositionId");
		employeePositionTypeId.setModel(employeePositionTypeList);
		employeePositionTypeId.setItemRenderer(new EmployeePositionTypeRenderer());
		
		List currency = delegator.findList("Uom", currencyCondition,
				currencyTypeToDisplay, null, null, false);
		Object currencyArray = currency.toArray(new GenericValue[currency
				.size()]);
		SimpleListModel currencyList = new SimpleListModel(currency);

		Listbox currencyListBox = (Listbox) newLoanWindow
				.getFellow("applyLoanCurrency");
		currencyListBox.setModel(currencyList);
		currencyListBox.setItemRenderer(new CurrencyRenderer());

	}

}
