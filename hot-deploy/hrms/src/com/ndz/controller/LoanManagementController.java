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
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

import com.ndz.component.party.ClaimStatusRenderer;
import com.ndz.component.party.ClaimTypeRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class LoanManagementController extends GenericForwardComposer {
	
	public void doAfterCompose(Component searchLoanWindow) throws Exception {
		super.doAfterCompose(searchLoanWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("enumTypeId",
				EntityOperator.EQUALS, "ELOAN_TYPE");
		EntityCondition loanStatusCondition = EntityCondition.makeCondition("statusTypeId",
				EntityOperator.EQUALS, "CLAIM_STATUS");
		

		Set loanTypeToDisplay = new HashSet();
		Set loanStatusToDisplay = new HashSet();
		
		loanTypeToDisplay.add("enumTypeId");
		loanTypeToDisplay.add("description");

		loanStatusToDisplay.add("statusId");
		loanStatusToDisplay.add("description");
		loanStatusToDisplay.add("statusCode");

		List loanType = delegator.findList("Enumeration", condition,
				loanTypeToDisplay, null, null, false);
		Object loanTypeArray = loanType.toArray(new GenericValue[loanType
				.size()]);
		SimpleListModel loanTypeList = new SimpleListModel(loanType);

		Listbox searchLoanType = (Listbox) searchLoanWindow
				.getFellow("searchLoanType");
		searchLoanType.setModel(loanTypeList);
		searchLoanType.setItemRenderer(new ClaimTypeRenderer());
		
		
		List loanStatusType = delegator.findList("StatusItem", loanStatusCondition,
				loanStatusToDisplay, null, null, false);
		Object loanStatusTypeArray = loanStatusType.toArray(new GenericValue[loanStatusType
				.size()]);
		SimpleListModel loanStatusTypeList = new SimpleListModel(loanStatusType);

		Listbox searchLoanStatusType = (Listbox) searchLoanWindow
				.getFellow("searchLoanStatus");
		searchLoanStatusType.setModel(loanStatusTypeList);
		searchLoanStatusType.setItemRenderer(new ClaimStatusRenderer());

		
	}

}
