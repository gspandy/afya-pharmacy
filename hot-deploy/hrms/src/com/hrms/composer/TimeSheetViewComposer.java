package com.hrms.composer;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;

import com.hrms.composer.TimeEntryComposer.TimeEntryRenderer;

public class TimeSheetViewComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;
	Window timeEntryWindow = null;
	List defaultData = null;
	AnnotateDataBinder binder = null;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent()
				.getAttributes().get("delegator");

		initViewMode(comp);
	}

	public void initViewMode(Component comp) throws GenericEntityException {

		timeEntryWindow = (Window) comp;
		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent()
				.getAttributes().get("delegator");
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");

		String timesheetId = (String) Executions.getCurrent().getArg().get(
				"timesheetId");
		String employeeId = (String)Executions.getCurrent().getArg().get("emplPartyId");
		defaultData = delegator.findByAnd("TimeEntry", UtilMisc.toMap("partyId", employeeId,"timesheetId",timesheetId));
		Grid appendEditTimeSheetGrid = (Grid) timeEntryWindow.getFellow("appendEditTimeSheetGrid",true);
		binder.loadAttribute(appendEditTimeSheetGrid, "model");
		
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
	
	public BindingListModel getModel() {
		return new BindingListModelList(defaultData, false);
	}

}
