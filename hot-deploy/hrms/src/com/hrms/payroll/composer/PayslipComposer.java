package com.hrms.payroll.composer;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Panel;
import org.zkoss.zul.api.Datebox;
import org.zkoss.zul.ext.Paginal;

import com.hrms.composer.HrmsComposer;

import java.sql.Date;
import java.util.List;

public class PayslipComposer extends HrmsComposer {

	private BindingListModel payslips;
	private Panel payslipPanel;
	private Paginal payslipPaginal;
	private Grid dataGrid;
	private String employeeId;
	private long totalSize;
	private Datebox periodFrom;
	private Datebox periodTo;

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public BindingListModel getPayslips() {
		return payslips;
	}

	public void setPayslips(BindingListModel payslips) {
		this.payslips = payslips;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {

		employeeId = userLogin.getString("partyId");
		final DynamicViewEntity dve = new DynamicViewEntity();
		dve.setEntityName("EmplPayslip");
		/*EntityListIterator iter = delegator.findListIteratorByCondition(dve,
				EntityCondition.makeCondition("partyId", employeeId), null, UtilMisc.toList("-payslipId"),null,null);*/

        List<GenericValue> payslipGvs = delegator.findList("EmplPayslip",EntityCondition.makeCondition("partyId", employeeId),UtilMisc.toSet("payslipId"),null,null,false);

		totalSize = delegator.findCountByCondition("EmplPayslip", EntityCondition.makeCondition("partyId", employeeId), null, null);

		payslips = new BindingListModelList(payslipGvs, false);
		/*iter.close();*/
		payslipPaginal.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				boolean transactionBegan = TransactionUtil.begin();
				int activePage = payslipPaginal.getActivePage();
				EntityListIterator iter = delegator.findListIteratorByCondition(dve, EntityCondition.makeCondition("partyId",
						employeeId), null, UtilMisc.toList("-payslipId"),null,null);
				payslips = new BindingListModelList(iter.getPartialList(activePage * payslipPaginal.getPageSize(), payslipPaginal
						.getPageSize()), false);
				iter.close();
				TransactionUtil.commit(transactionBegan);
				binder.loadAttribute(dataGrid, "model");
			}
		});

		Caption caption = (Caption) payslipPanel.getFirstChild();
		caption.setLabel("My Payslips");
		payslipPanel.setVisible(true);
	}

	@SuppressWarnings("deprecation")
	public void onClick$searchPayslip(Event event) throws Exception {
		boolean transactionBegan = TransactionUtil.begin();
		
		
		EntityCondition periodFromCond = EntityCondition.makeCondition(("periodFrom"),
				EntityOperator.GREATER_THAN_EQUAL_TO, new Date(periodFrom.getValue().getTime()));

		EntityCondition periodToCond = EntityCondition.makeCondition(("periodTo"),
				EntityOperator.LESS_THAN_EQUAL_TO, new Date(periodTo.getValue().getTime()));
		
		EntityCondition partyCond=EntityCondition.makeCondition("partyId", employeeId);
		EntityCondition finalCondition=EntityCondition.makeCondition(periodFromCond,periodToCond,partyCond);
		totalSize = delegator.findCountByCondition("EmplPayslip",finalCondition , null, null);
		/*DynamicViewEntity dve = new DynamicViewEntity();
		dve.setEntityName("EmplPayslip");*/
        List<GenericValue> payslipList = delegator.findList("EmplPayslip",finalCondition,null, UtilMisc.toList("-payslipId"),null,false);
		/*EntityListIterator iter = delegator.findListIteratorByCondition(dve,
				finalCondition, null, UtilMisc.toList("-payslipId"),null,null);*/
		payslips = new BindingListModelList(payslipList, false);
		binder.loadAll();
		TransactionUtil.commit(transactionBegan);
		Caption caption = (Caption) payslipPanel.getFirstChild();
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), true);
		caption.setLabel("Payslip of " + person.getString("firstName") + " "+person.getString("lastName"));
		payslipPanel.setVisible(true);
	}

}
