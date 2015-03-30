package com.hrms.training.composer;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Listbox;

import com.hrms.composer.HrmsComposer;

public class TrainingWkflowComposer extends HrmsComposer {

	private List<GenericValue> statusList;
	private GenericValue selectedStatusItem;
	private Listbox statusListbox;
	private Listbox statusDetailsListbox;
	private List statusDetailList;

	public List<GenericValue> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<GenericValue> statusList) {
		this.statusList = statusList;
	}

	public BindingListModel getStatusDetailList() {
		return new BindingListModelList(statusDetailList, false);
	}

	public GenericValue getSelectedStatusItem() {
		return selectedStatusItem;
	}

	public void setSelectedStatusItem(GenericValue newStatusItem) {
		this.selectedStatusItem = newStatusItem;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		statusList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "TRNG_STATUS"));
		System.out.println(statusList.size());
		binder.loadAttribute(statusListbox, "model");
		statusListbox.addEventListener("onSelect", getListboxEventListener());
	}

	public BindingListModel getModel() {
		return new BindingListModelList(statusList, false);
	}

	public void onClick$updateAllStatus(Event event) throws Exception {
		delegator.storeAll(statusList);
		binder.loadAttribute(statusListbox, "model");
	}

	public void onClick$newStatus(Event event) throws Exception {
		selectedStatusItem = delegator.makeValidValue("StatusItem", "statusTypeId", "TRNG_STATUS");
	}

	public void onSave$saveStatus(Event event) throws Exception {
		delegator.create(selectedStatusItem);
		statusList = delegator.findList("StatusItem", EntityCondition.makeCondition("statusTypeId", "TRNG_STATUS"), null, UtilMisc
				.toList("-createdStamp"), null, false);
		binder.loadAttribute(statusListbox, "model");
	}

	public EventListener getListboxEventListener() throws Exception {
		return new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				String statusId = selectedStatusItem.getString("statusId");
				Map<String, Object> results = dispatcher.runSync("getStatusValidChangeToDetails", UtilMisc.toMap("userLogin", userLogin,
						"statusId", statusId));
				statusDetailList = (List) results.get("statusValidChangeToDetails");
				AnnotateDataBinder binder = new AnnotateDataBinder(statusDetailsListbox);
				binder.loadAttribute(statusDetailsListbox, "model");
			}
		};
	}

	public void onClick$newTransition(Event event) {
		statusDetailList.add(0, delegator.makeValidValue("StatusValidChange", UtilMisc.toMap("statusId", selectedStatusItem
				.getString("statusId"))));
		AnnotateDataBinder binder = new AnnotateDataBinder(statusDetailsListbox);
		binder.loadAttribute(statusDetailsListbox, "model");
	}

}
