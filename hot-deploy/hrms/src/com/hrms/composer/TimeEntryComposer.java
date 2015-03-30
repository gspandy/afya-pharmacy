package com.hrms.composer;

import java.util.Iterator;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;

public class TimeEntryComposer extends GenericForwardComposer{

	Window timeEntryWindow = null;
	Grid timeEntriesGrid = null;
	static List<GenericValue> defaultData = null;
	AnnotateDataBinder binder = null;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		boolean beganTrans = false;
		if (!TransactionUtil.isTransactionInPlace()) {
			beganTrans = TransactionUtil.begin();
		}
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent()
				.getAttributes().get("delegator");

		initEditMode(comp);
		if (beganTrans) {
			TransactionUtil.commit();
		}
	}

	private void initEditMode(Component comp) throws GenericEntityException {
		timeEntryWindow = (Window) comp;

		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent()
				.getAttributes().get("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) Executions.getCurrent()
				.getAttributes().get("dispatcher");

		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");

		String timesheetId = (String) Executions.getCurrent().getArg().get(
				"timesheetId");
		defaultData = delegator.findList("TimeEntry", EntityCondition
				.makeCondition("timesheetId", timesheetId), null, null, null,
				false);

		timeEntriesGrid = (Grid) timeEntryWindow.getFellow("timeEntriesGrid",
				true);
		binder.loadAttribute(timeEntriesGrid, "model");

	}
	
	public RowRenderer getTimeEntryRenderer() {
		return new TimeEntryRenderer();
	}

	public static class TimeEntryRenderer implements RowRenderer {

		public void render(Row row, Object data) throws Exception {
			GenericValue timeEntry = (GenericValue) data;
			new Label(timeEntry.getString("timeEntryId")).setParent(row);
			new Label(timeEntry.getString("fromDate")).setParent(row);
			new Label(timeEntry.getString("hours")).setParent(row);
		}
	}
	

	public void onClick$saveTimeEntries(Event event) throws Exception {
		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent()
				.getAttributes().get("delegator");
		for (Iterator iter = defaultData.iterator(); iter.hasNext();) {
			GenericValue gv = (GenericValue) iter.next();
			System.out.println(gv);
			gv.put("hours", new Double(gv.get("hours") == null ? "0" : gv.get(
					"hours").toString()));
			delegator.store(gv);
		}
		Messagebox.show("Timesheet saved successfully, Needs to be submitted ", Labels
				.getLabel("HRMS_SUCCESS"), 1, null);
	}
	
	public BindingListModel getModel() {
		return new BindingListModelList(defaultData, false);
	}
}
