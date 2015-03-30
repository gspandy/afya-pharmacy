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

import com.ndz.component.party.ClaimLimitRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.component.party.EmployeePositionTypeRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class AdminNewClaimManagementController extends GenericComposer {

	public void doAfterCompose(Component newClaimWindow) throws Exception {
		super.doAfterCompose(newClaimWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("enumTypeId",
				EntityOperator.EQUALS, "CLAIM_TYPE");
		EntityCondition employeePositionTypeCondition = EntityCondition.makeCondition(
				"hasTable", EntityOperator.EQUALS, "N");
		EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId",
				EntityOperator.EQUALS, "CURRENCY_MEASURE");

		Set claimLimitToDisplay = new HashSet();
		Set employeePositionTypeToDisplay = new HashSet();
		Set currencyTypeToDisplay = new HashSet();

		claimLimitToDisplay.add("enumTypeId");
		claimLimitToDisplay.add("description");

		employeePositionTypeToDisplay.add("emplPositionTypeId");
		employeePositionTypeToDisplay.add("description");

		currencyTypeToDisplay.add("uomId");
		currencyTypeToDisplay.add("description");

		List claimLimit = delegator.findList("Enumeration", condition,
				claimLimitToDisplay, null, null, false);
		Object claimTypeArray = claimLimit.toArray(new GenericValue[claimLimit
				.size()]);
		SimpleListModel claimLimitList = new SimpleListModel(claimLimit);

		Listbox searchClaimType = (Listbox) newClaimWindow
				.getFellow("applyClaimType");
		searchClaimType.setModel(claimLimitList);
		searchClaimType.setItemRenderer(new ClaimLimitRenderer());

		List employeePositionType = delegator.findList("EmplPositionType",
				employeePositionTypeCondition, employeePositionTypeToDisplay,
				null, null, false);
		Object employeePositionTypeArray = employeePositionType
				.toArray(new GenericValue[employeePositionType.size()]);
		SimpleListModel employeePositionTypeList = new SimpleListModel(
				employeePositionType);
		Listbox employeePositionTypeId = (Listbox) newClaimWindow
				.getFellow("newClaimPositionId");
		employeePositionTypeId.setModel(employeePositionTypeList);
		employeePositionTypeId
				.setItemRenderer(new EmployeePositionTypeRenderer());

		List currency = delegator.findList("Uom", currencyCondition,
				currencyTypeToDisplay, null, null, false);
		Object currencyArray = currency.toArray(new GenericValue[currency
				.size()]);
		SimpleListModel currencyList = new SimpleListModel(currency);

		Listbox currencyListBox = (Listbox) newClaimWindow
				.getFellow("applyClaimCurrency");
		currencyListBox.setModel(currencyList);
		currencyListBox.setItemRenderer(new CurrencyRenderer());

	}

}
