package com.hrms.composer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericServiceException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zkplus.databind.BindingListModelSet;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.MessageUtils;

public class TimesheetComposer extends HrmsComposer {

	Window mainWindow = null;

	Listbox timesheetGrid = null;
	EntityCondition defaultCondition = null;

	List defaultData = null;

	ListModel fiscalYears = null;
	ListModel months = null;

	ListModel years = null;

	public ListModel getYears() {
		return years;
	}

	public void setYears(BindingListModel years) {
		this.years = years;
	}

	String selectedFiscalYear = null;
	String selectedMonth = null;

	public String getSelectedFiscalYear() {
		return selectedFiscalYear;
	}

	public void setSelectedFiscalYear(String selectedFiscalYear) {
		this.selectedFiscalYear = selectedFiscalYear;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public ListModel getFiscalYears() {
		return fiscalYears;
	}

	public void setFiscalYears(BindingListModel fiscalYears) {
		this.fiscalYears = fiscalYears;
	}

	public ListModel getMonths() {
		return months;
	}

	public void setMonths(BindingListModel months) {
		this.months = months;
	}

	public TimesheetComposer() {
		List<String> data = new ArrayList<String>();
		List<String> allMonths = UtilDateTime.getMonthNames(Locale.getDefault());
		for (String month : allMonths) {
			data.add(month);
		}
		Set<Object> yearData = HrmsUtil.getCustomTimePeriod();
		months = new BindingListModelList(data, false);
		fiscalYears = new BindingListModelSet(yearData, false);
		years = new BindingListModelSet(yearData, false);
	}

	public TimesheetComposer(String mode) {
		this();
	}

	private static final long serialVersionUID = -7168038920421109736L;

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		initViewMode(comp);
	}

	private void initViewMode(Component comp) throws GenericEntityException {

		mainWindow = (Window) comp;
		timesheetGrid = (Listbox) mainWindow.getFellow("timesheetGrid", true);
		// By Default get current Fiscal Time sheets

		Date fromDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		Date thruDate = UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale
				.getDefault());

		defaultCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate",
				EntityComparisonOperator.GREATER_THAN_EQUAL_TO, new Timestamp(fromDate.getTime())), EntityCondition
				.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, new Timestamp(thruDate
						.getTime())), EntityCondition.makeCondition("partyId", userLogin.getString("partyId")));

		defaultData = delegator.findList("Timesheet", defaultCondition, null, null, null, false);
		if (defaultData.size() == 0) {
			Listfoot gridFoot = (Listfoot) timesheetGrid.getFellowIfAny("timesheetGridFooter", true);
			gridFoot.setVisible(true);
		}

		selectedFiscalYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		selectedMonth = UtilDateTime.getMonthNames(Locale.getDefault()).get(Calendar.getInstance().get(Calendar.MONTH));
		binder.loadAttribute(timesheetGrid, "model");

	}

	public void onClick$fetchTimesheet(Event event) {
		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		Date filterDateFrom = new Date();
		Date filterDateTo = new Date();
		try {
			filterDateFrom = formatter.parse("01-" + selectedMonth + "-" + selectedFiscalYear);
			Calendar cal = Calendar.getInstance();
			cal.setTime(filterDateFrom);
			int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			filterDateTo = formatter.parse(lastDay + "-" + selectedMonth + "-" + selectedFiscalYear);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate",
				EntityComparisonOperator.GREATER_THAN_EQUAL_TO, new Timestamp(filterDateFrom.getTime())),
				EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, new Timestamp(
						filterDateTo.getTime())), EntityCondition.makeCondition("partyId", userLogin
						.getString("partyId")));
		try {
			defaultData = delegator.findList("Timesheet", condition, null, null, null, false);

			if (defaultData.size() == 0) {
				Listfoot gridFoot = (Listfoot) timesheetGrid.getFellowIfAny("timesheetGridFooter", true);
				gridFoot.setVisible(true);
			} else {
				Listfoot gridFoot = (Listfoot) timesheetGrid.getFellowIfAny("timesheetGridFooter", true);
				gridFoot.setVisible(false);
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		binder.loadAttribute(timesheetGrid, "model");

	}

	public BindingListModel getModel() {
		return new BindingListModelList(defaultData, false);
	}

	public void onClick$createTimesheet(Event event) throws Exception {
		GenericValue mgrGV = HumanResUtil.getReportingMangerForParty(userLogin.getString("partyId"), delegator);
		if (mgrGV != null) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("userLogin", userLogin);
			context.put("partyId", userLogin.getString("partyId"));

			String timesheetId = null;
			Map results = new HashMap();
			try {
				results = dispatcher.runSync("createTimesheetForThisWeek", context);
				timesheetId = (String) results.get("timesheetId");
				if ("error".equals(results.get("responseMessage"))) {
					MessageUtils.showErrorMessage(((List) results.get("errorMessageList")).get(0).toString());
					return;
				}
				Map newContext = UtilMisc.toMap("userLogin", userLogin, "partyId", mgrGV.getString("partyId"),
						"timesheetId", timesheetId, "roleTypeId", "MANAGER");
				dispatcher.runSync("createTimesheetRole", newContext);
			} catch (GenericServiceException e) {
				MessageUtils.showErrorMessage();
				return;
			}

			List<GenericValue> timeEntries = new ArrayList<GenericValue>();
			try {
				GenericValue timesheet = delegator.findOne("Timesheet", false, "timesheetId", timesheetId);

				defaultData = UtilMisc.toList(timesheet);
				Timestamp fromDate = timesheet.getTimestamp("fromDate");
				Timestamp thruDate = timesheet.getTimestamp("thruDate");

				Timestamp nextDate = null;
				do {
					nextDate = UtilDateTime.getNextDayStart(nextDate == null ? fromDate : nextDate);
					Map newContext = UtilMisc.toMap("userLogin", userLogin, "partyId", userLogin.getString("partyId"),
							"timesheetId", timesheetId, "fromDate", fromDate, "thruDate", nextDate);
					timeEntries.add(delegator.findOne("TimeEntry", false, "timeEntryId", (String) dispatcher.runSync(
							"createTimeEntry", newContext).get("timeEntryId")));
					fromDate = nextDate;
				} while (nextDate.before(thruDate));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			binder.loadAttribute(timesheetGrid, "model");
			System.out.println(" Time Entries " + timeEntries);
		} else {
			Messagebox.show("Cannot create Timesheet as you are not reporting to any Manager.", "Error", 1, null);
			return;
		}
	}

	public void onClick$createCustomTimesheet(Event event) throws GenericEntityException, InterruptedException {
		GenericValue mgrGV = HumanResUtil.getReportingMangerForParty(userLogin.getString("partyId"), delegator);
		if (mgrGV != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			Date startDate = new Date();
			Date endDate = new Date();
			Date sDate = new Date();
			Date tDate = new Date();
			try {
				startDate = formatter.parse("01-" + selectedMonth + "-" + selectedFiscalYear);
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				endDate = formatter.parse(lastDay + "-" + selectedMonth + "-" + selectedFiscalYear);

				List<String> timesheetIds = new ArrayList<String>();
				do {
					Timestamp from = UtilDateTime.getWeekStart(new Timestamp(startDate.getTime()));
					if (from.before(new Timestamp(startDate.getTime()))) {
						from = new Timestamp(startDate.getTime());
					}

					Timestamp thru = UtilDateTime.getWeekEnd(new Timestamp(startDate.getTime()));
					if (thru.after(new Timestamp(endDate.getTime()))) {
						thru = new Timestamp(endDate.getTime());
					}

					Map<String, Object> context = new HashMap<String, Object>();
					context.put("userLogin", userLogin);
					context.put("partyId", userLogin.getString("partyId"));

					if (thru.after(endDate))
						thru = new Timestamp(endDate.getTime());

					context.put("fromDate", from);
					context.put("thruDate", thru);
					String timesheetId = null;
					try {
						Map results = dispatcher.runSync("createTimesheet", context);
						timesheetId = (String) results.get("timesheetId");

						Map newContext = UtilMisc.toMap("userLogin", userLogin, "partyId", mgrGV.getString("partyId"),
								"timesheetId", timesheetId, "roleTypeId", "MANAGER");
						dispatcher.runSync("createTimesheetRole", newContext);
						timesheetIds.add(timesheetId);
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startDate = UtilDateTime.addDaysToTimestamp(thru, 1);
					sDate = formatter.parse(formatter.format(startDate));
					tDate = formatter.parse(formatter.format(endDate));
				} while (sDate.before(tDate) || sDate.equals(tDate));

				EntityCondition condition = EntityCondition.makeCondition("timesheetId", EntityComparisonOperator.IN,
						UtilMisc.toList(timesheetIds));

				System.out.println(" Condition " + condition);
				defaultData = delegator.findList("Timesheet", condition, null, null, null, false);

				for (Iterator iter = defaultData.iterator(); iter.hasNext();) {
					GenericValue timesheet = (GenericValue) iter.next();
					String timesheetId = timesheet.getString("timesheetId");
					Timestamp fromDate = timesheet.getTimestamp("fromDate");
					Timestamp thruDate = timesheet.getTimestamp("thruDate");
					Timestamp nextDate = null;
					do {
						nextDate = UtilDateTime.getNextDayStart(fromDate);
						Map newContext = UtilMisc.toMap("userLogin", userLogin, "partyId", userLogin
								.getString("partyId"), "timesheetId", timesheetId, "fromDate", fromDate, "thruDate",
								nextDate);
						System.out.println(" From Date " + fromDate + "********* " + nextDate);
						delegator.findOne("TimeEntry", false, "timeEntryId", (String) dispatcher.runSync(
								"createTimeEntry", newContext).get("timeEntryId"));
						fromDate = nextDate;
					} while (nextDate.before(thruDate) || nextDate.equals(thruDate));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Listfoot gridFoot = (Listfoot) timesheetGrid.getFellowIfAny("timesheetGridFooter", true);
			gridFoot.setVisible(false);
			binder.loadAttribute(timesheetGrid, "model");
		} else {
			Messagebox.show("Cannot create Timesheet as you are not reporting to any Manager.", "Error", 1, null);
			return;
		}

	}

	public ListitemRenderer getTimesheetRenderer() {
		return new TimesheetRenderer();
	}

	public static class TimesheetRenderer implements ListitemRenderer {

		public void render(Listitem row, Object data) throws Exception {
			final GenericValue timeEntry = (GenericValue) data;
			if (!(timeEntry.getString("statusId").equals("TIMESHEET_APPROVED"))) {
				row.setTooltiptext("Double click to update Time Sheet");
			}
			if (timeEntry.getString("statusId").equals("TIMESHEET_APPROVED")) {
				row.setTooltiptext("Double click to view Time Sheet");
			}
			Listcell listcell = new Listcell(timeEntry.getString("timesheetId"));
			listcell.setParent(row);
			new Listcell(UtilDateTime.formatDate(UtilDateTime.getDayStart(timeEntry.getTimestamp("fromDate"))))
					.setParent(row);
			new Listcell(UtilDateTime.formatDate(UtilDateTime.getDayEnd(timeEntry.getTimestamp("thruDate"))))
					.setParent(row);
			if (timeEntry.getRelatedOne("StatusItem") != null)
				new Listcell(timeEntry.getRelatedOne("StatusItem").getString("description")).setParent(row);
			if (!(timeEntry.getString("statusId").equals("TIMESHEET_APPROVED"))) {
				row.addEventListener("onDoubleClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						Window window = (Window) Executions.createComponents("/zul/timesheet/editTimesheet.zul", null,
								UtilMisc.toMap("timesheetId", timeEntry.getString("timesheetId")));
						Component comp = event.getTarget().getFellowIfAny("timeEntry", true);
						Component appendDivComp = event.getTarget()
								.getFellowIfAny("appendEditTimeSheetWindowDiv", true);
						Component childComp = appendDivComp.getFirstChild();
						if (comp != null)
							comp.detach();
						if (childComp != null)
							childComp.detach();
						window.setId("timeEntryWindow");
						window.doEmbedded();
						window.focus();
						window.setParent(appendDivComp);
					}
				});
			}
			if (timeEntry.getString("statusId").equals("TIMESHEET_APPROVED")) {
				row.addEventListener("onDoubleClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						Window window = (Window) Executions.createComponents("/zul/timesheet/viewApproveTimeSheet.zul",
								null, UtilMisc.toMap("timesheetId", timeEntry.getString("timesheetId")));
						Component comp = event.getTarget().getFellowIfAny("timeEntry", true);
						Component appendDivComp = event.getTarget()
								.getFellowIfAny("appendEditTimeSheetWindowDiv", true);
						Component childComp = appendDivComp.getFirstChild();
						if (comp != null)
							comp.detach();
						if (childComp != null)
							childComp.detach();
						window.setId("timeEntryWindow");
						window.doEmbedded();
						window.focus();
						window.setParent(appendDivComp);
					}
				});
			}
			row.setValue(data);
		}
	}

	public void onClick$submitForApproval(Event event) throws InterruptedException, GenericEntityException {
		Set<Listitem> items = timesheetGrid.getSelectedItems();
		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
		for (Listitem li : items) {
			GenericValue timesheet = ((GenericValue) li.getValue());
			EntityCondition employmentCondition1 = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS,
					timesheet.getString("partyId"));
			EntityCondition employmentCondition2 = EntityCondition.makeCondition("fromDate",
					EntityOperator.LESS_THAN_EQUAL_TO, timesheet.getTimestamp("thruDate"));
			EntityCondition employmentCondition = EntityCondition.makeCondition(employmentCondition1,
					EntityOperator.AND, employmentCondition2);
			List<GenericValue> employmentList = delegator.findList("Employment", employmentCondition, null, null, null,
					false);
			if (UtilValidate.isEmpty(employmentList)) {
				Messagebox.show("Timesheet prior to Joining Date cannot be submitted.", "Error", 1, null);
				return;
			}

			String timesheetId = timesheet.getString("timesheetId");
			List<GenericValue> timeEntryList = delegator.findByAnd("TimeEntry", UtilMisc.toMap("partyId", userLogin
					.getString("partyId"), "timesheetId", timesheetId));
			for (GenericValue gv : timeEntryList) {
				BigDecimal hour = gv.getBigDecimal("hours");
				if (hour == null) {
					Messagebox.show("Please fill Time Entry related to Timesheet " + " " + timesheetId, "Error", 1,
							null);
					return;
				}
			}
			if (timesheet.getString("statusId").equals("TIMESHEET_APPROVED")) {
				Messagebox.show("Approved timesheet cannot be submitted.", "Error", 1, null);
				return;
			}
			timesheet.put("statusId", "TIMESHEET_COMPLETED");
			delegator.store(timesheet);
		}
		if(items.size()>0){
		Messagebox.show("All timesheet(s) submitted successfully.", "Success", 1, null);
		binder.loadAttribute(timesheetGrid, "model");
		}else{
			Messagebox.show("No timesheet selected.Please select Timesheet and click on submit", "Error", 1, null);	
		}
		
	}

}
