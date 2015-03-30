package com.hrms.composer;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Panel;
import org.zkoss.zul.api.Datebox;
import org.zkoss.zul.ext.Paginal;

public class SearchCandidateComposer extends HrmsComposer {

	private BindingListModel interviewfeedbacks;
	private Paginal paging;
	private Grid dataGrid;
	private String candidateId;
	private long totalSize;
	
	public BindingListModel getInterviewfeedbacks() {
		return interviewfeedbacks;
	}

	public void setInterviewfeedbacks(BindingListModel interviewfeedbacks) {
		this.interviewfeedbacks = interviewfeedbacks;
	}

	public Paginal getPaging() {
		return paging;
	}

	public void setPaging(Paginal paging) {
		this.paging = paging;
	}

	public Grid getDataGrid() {
		return dataGrid;
	}

	public void setDataGrid(Grid dataGrid) {
		this.dataGrid = dataGrid;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	
	
	protected void executeAfterCompose(Component comp) throws Exception {
		final DynamicViewEntity dve = new DynamicViewEntity();
		dve.setEntityName("EmplPayslip");
		EntityListIterator iter = delegator.findListIteratorByCondition(dve,
				EntityCondition.makeCondition("partyId", candidateId), null, UtilMisc.toList("-payslipId"),null,null);

		totalSize = delegator.findCountByCondition("ApplicationDetail", EntityCondition.makeCondition("partyId", candidateId), null, null);

		interviewfeedbacks = new BindingListModelList(iter.getPartialList(0, paging.getPageSize()), false);
		iter.close();
		paging.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				boolean transactionBegan = TransactionUtil.begin();
				int activePage = paging.getActivePage();
				EntityListIterator iter = delegator.findListIteratorByCondition(dve, EntityCondition.makeCondition("partyId",
						candidateId), null, UtilMisc.toList("-payslipId"),null,null);
				interviewfeedbacks = new BindingListModelList(iter.getPartialList(activePage * paging.getPageSize(), paging
						.getPageSize()), false);
				iter.close();
				TransactionUtil.commit(transactionBegan);
				binder.loadAttribute(dataGrid, "model");
			}
		});
	}

}
