package com.hrms.payroll.composer;

import groovy.sql.InOutParameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.job.JobManager;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zkplus.databind.BindingListModelSet;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.ndz.zkoss.util.MessageUtils;


public class InitiatePayrollComposer extends HrmsComposer {

	private List<String> frequencies;
	private final List<String> allMonths = UtilDateTime.getMonthNames(Locale.getDefault());
	private BindingListModelList departmentModel;
	private BindingListModelList frequencyModel;

	private String orgSalaryFrequency;

	List<Map<String, Object>> inValidEmployees;

	public List<Map<String, Object>> getInValidEmployees() {
		return inValidEmployees;
	}

	public void setInValidEmployees(List<Map<String, Object>> inValidEmployees) {
		this.inValidEmployees = inValidEmployees;
	}

	private boolean linkToLMS;

	public boolean isLinkToLMS() {
		return linkToLMS;
	}

	public void setLinkToLMS(String linkToLMS) {
		this.linkToLMS = Boolean.parseBoolean(linkToLMS);
	}

	private String selectedEmployeeId;

	public String getSelectedEmployeeId() {
		return selectedEmployeeId;
	}

	public void setSelectedEmployeeId(String selectedEmployeeId) {
		this.selectedEmployeeId = selectedEmployeeId;
	}

	private String selectedDepartmentId;
	private String selectedFrequency;
	private Integer selectedYear;
	private Window InitiatePayrollWindow;
	private List<String> employeeIds;
	private BindingListModel adhocSalaryHeads;
	Listbox adhocSalaryHeadListbox;
	private List<GenericValue> allRules;
	private Date fromPeriod;
	private Date toPeriod;
	private Date startDate;
	private List<GenericValue> payrollJobs;
	private ListModel years;

	public ListModel getYears() {
		return years;
	}

	public void setYears(BindingListModelList years) {
		this.years = years;
	}

	public List<GenericValue> getPayrollJobs() {
		return payrollJobs;
	}

	public void setPayrollJobs(List<GenericValue> payrollJobs) {
		this.payrollJobs = payrollJobs;
	}

	public List<String> getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(List<String> employeeIds) {
		this.employeeIds = employeeIds;
	}

	public BindingListModelList getDepartmentModel() {
		return departmentModel;
	}

	public void setDepartmentModel(BindingListModelList departmentModel) {
		this.departmentModel = departmentModel;
	}

	public BindingListModelList getFrequencyModel() {
		return frequencyModel;
	}

	public void setFrequencyModel(BindingListModelList frequencyModel) {
		this.frequencyModel = frequencyModel;
	}

	public String getSelectedDepartmentId() {
		return selectedDepartmentId;
	}

	public void setSelectedDepartmentId(String selectedDepartmentId) {
		this.selectedDepartmentId = selectedDepartmentId;
	}

	public String getSelectedFrequency() {
		return selectedFrequency;
	}

	public void setSelectedFrequency(String selectedFrequency) {
		this.selectedFrequency = selectedFrequency;
	}

	public Integer getSelectedYear() {
		return selectedYear;
	}

	public void setSelectedYear(Integer selectedYear) {
		this.selectedYear = selectedYear;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		InitiatePayrollWindow = (Window) comp;
		orgSalaryFrequency = com.nthdimenzion.humanres.payroll.PayrollHelper.getSalaryFrequencyForCompany(delegator);
		List<String> departments = getAllDepartments();
		if ("BIWEEKLY".equals(orgSalaryFrequency)) {
			frequencies = new ArrayList<String>(24);
			for (int i = 0; i < allMonths.size(); i++) {
				frequencies.add("01-" + allMonths.get(i));
				frequencies.add("15-" + allMonths.get(i));
			}
		} else {
			frequencies = allMonths;
		}
		// TODO put a time constraint
		List<String> orderBy = UtilMisc.toList("-toPeriod");
		payrollJobs = delegator.findList("PayrollJob", null, null, orderBy, null, false);
		frequencyModel = new BindingListModelList(frequencies, false);
		departmentModel = new BindingListModelList(departments, false);
		Set<Object> yearData = HrmsUtil.getCustomTimePeriod();
		years = new BindingListModelSet(yearData, false);
		binder.loadAll();
	}

	private List<String> getAllDepartments() {
		EntityCondition condition = EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO");
		Set<String> fieldsToSelect = new HashSet<String>();
		fieldsToSelect.add("partyIdTo");
		fieldsToSelect.add("partyIdFrom");
		List<String> departments = new ArrayList<String>();
		try {
			List<GenericValue> l = delegator.findList("PartyRelationship", condition, fieldsToSelect, null, null, false);
			for (GenericValue g : l) {
				departments.add(g.getString("partyIdTo"));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		departments.add(0, null);
		departments.add(1, "All");
		return departments;
	}

	public void onClick$proceed(Event event) throws Exception {

		//if (selectedFrequency == null || selectedYear == null || (employeeIds == null && selectedDepartmentId == null))
			//return;
		fromPeriod = getFromPeriod(selectedFrequency, String.valueOf(selectedYear.intValue()));
		toPeriod = getEndPeriod(fromPeriod);

		EntityCondition pjec1 = EntityCondition.makeCondition("fromPeriod", new java.sql.Date(fromPeriod.getTime()));
		EntityCondition pjec2 = EntityCondition.makeCondition("toPeriod", new java.sql.Date(toPeriod.getTime()));
		EntityCondition pjec3 = EntityCondition.makeCondition("status", EntityComparisonOperator.NOT_IN, Arrays.asList("FINISHED"));

		List oldJobs = delegator.findList("PayrollJob", EntityCondition.makeCondition(pjec1, pjec2, pjec3), null, null, null, false);
		if (oldJobs.size() > 0) {
			MessageUtils.showErrorMessage("Payroll Job already exists.");
			return;
		}
		List<GenericValue> emplPayslipList = delegator.findByAnd("EmplPayslip", UtilMisc.toMap("periodFrom",new java.sql.Date(fromPeriod.getTime()),"periodTo",new java.sql.Date(toPeriod.getTime()),"partyId",selectedEmployeeId));
		if(emplPayslipList.size() > 0){
			Messagebox.show("Payslip already generated for this month","Error",1,null);
			return;
		}
		List<String> eligibleEmployees = new LinkedList<String>();
		inValidEmployees = new ArrayList<Map<String, Object>>();
		if (selectedEmployeeId != null) {
			eligibleEmployees.add(selectedEmployeeId);
		} else {
			eligibleEmployees = getEmployeeListForDepartment(selectedDepartmentId, fromPeriod, toPeriod);
		}

		employeeIds = new LinkedList<String>();
		for (Iterator<String> iter = eligibleEmployees.iterator(); iter.hasNext();) {
			Integer selectedMonth = fromPeriod.getMonth() + 1;
			String employeeId = iter.next();
			List employeeSalaryStructureList = delegator.findByAnd("PartySalaryStructure", UtilMisc.toMap("partyId", employeeId));
			GenericValue employeeStructureGv = EntityUtil.getFirst(employeeSalaryStructureList);
			java.sql.Date fromDate = null;
			boolean isSalaryCredited = false;
			int attachedSalaryStructureMonth = 0;
			int attachedSalaryStructureYear=0;
			if (employeeStructureGv != null) {
				fromDate = employeeStructureGv.getDate("fromDate");
				attachedSalaryStructureMonth = fromDate.getMonth() + 1;
				attachedSalaryStructureYear=fromDate.getYear()+1900;
			}
			EntityCondition ec2 = null;
			if ((selectedMonth == attachedSalaryStructureMonth)&&(selectedYear==attachedSalaryStructureYear)) {
				Date fromPeriod1 = new Date(fromDate.getTime());
				isSalaryCredited = isSalaryCredited(employeeId, fromPeriod, toPeriod);
				ec2 = EntityCondition.makeCondition("fromDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(
						fromPeriod1.getTime()));
			} else {
				isSalaryCredited = isSalaryCredited(employeeId, fromPeriod, toPeriod);
				ec2 = EntityCondition.makeCondition("fromDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(
						fromPeriod.getTime()));
			}
			EntityCondition ec1 = EntityCondition.makeCondition("partyId", employeeId);
			
			long count = delegator.findCountByCondition("EmplSal", EntityConditionList.makeCondition(ec1, ec2), null, null);

			boolean isTerminated = isEmployeeTerminated(employeeId, fromPeriod, toPeriod);
			
			
			/*if ((count == 0 || isTerminated || isSalaryCredited ) && !((selectedMonth == attachedSalaryStructureMonth)&& !isSalaryCredited))*/
			/*add by bikash*/if(count==0 || isTerminated){
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("employeeId",employeeId);
                map.put("Reason", count == 0 ? "No Salary Structure found.": isTerminated ? "Employee is Terminated." : isSalaryCredited ? "Salary is already credited." :"");
                inValidEmployees.add(map);
				/*inValidEmployees.add(UtilMisc.toMap("employeeId", employeeId, "Reason", count == 0 ? "No Salary Structure found."
						: isTerminated ? "Employee is Terminated." : isSalaryCredited ? "Salary is already credited." :""));*/
			} else {
				employeeIds.add(employeeId);
			}
		}

		List<GenericValue> allAdhocHeads = delegator.findList("SalaryHead", EntityCondition.makeCondition("salaryHeadTypeId", "Adhoc"),
				null, null, null, true);
		List<Map> adhocSalaryHeadList = new ArrayList<Map>();
		for (GenericValue each : allAdhocHeads) {
			adhocSalaryHeadList.add(UtilMisc.toMap("salaryHeadId", each.getString("salaryHeadId"), "name", each.getString("hrName"),
					"computationType",each.getString("salaryComputationTypeId")));
		}

		adhocSalaryHeads = new BindingListModelList(adhocSalaryHeadList, false);

		allRules = delegator.findList("PayrollRule", null, null, null, null, true);

		Map m = new HashMap();
		m.put("composer", this);
		Window window = (Window) Executions.createComponents("/zul/payRollManagement/hrmsPartyInitiatePayroll.zul", InitiatePayrollWindow
				.getParent(), m);
		InitiatePayrollWindow.detach();
		adhocSalaryHeadListbox = (Listbox) window.getFellowIfAny("adhocSalaryHeadListbox", true);
		if (adhocSalaryHeadList.size() == 0) {
			Panel panel = (Panel) window.getFellowIfAny("adhocSalaryPanel", true);
			panel.setVisible(false);
		}
	}

	private boolean isSalaryCredited(String employeeId, Date fromPeriod, Date toPeriod) throws Exception {

		EntityCondition ec1 = EntityCondition.makeCondition("partyId", employeeId);

		EntityCondition ec2 = EntityCondition.makeCondition("periodFrom", EntityComparisonOperator.GREATER_THAN_EQUAL_TO,
				new java.sql.Date(fromPeriod.getTime()));

		EntityCondition ec3 = EntityCondition.makeCondition("periodTo", EntityComparisonOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(
				toPeriod.getTime()));

		List payslips = delegator.findList("EmplPayslip", EntityCondition.makeCondition(ec1, ec2, ec3), UtilMisc.toSet("payslipId"), null,
				null, false);

		return payslips.size() > 0;
	}

	private boolean isEmployeeTerminated(String employeeId, Date fromPeriod, Date toPeriod) throws Exception {
		EntityCondition ec1 = EntityCondition.makeCondition("partyIdTo", employeeId);

		EntityCondition ec2 = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.NOT_EQUAL, null);
		EntityCondition ec3 = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, new Timestamp(
				toPeriod.getTime()));
		EntityCondition ec4 = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, new Timestamp(
				fromPeriod.getTime()));
		long count = delegator.findCountByCondition("Employment", EntityCondition.makeCondition(ec1, ec2, ec3, ec4), null,null);
		if (count == 1)
			return true;
		else
			return false;
	}

	public BindingListModel getAdhocSalaryHeads() {
		return adhocSalaryHeads;
	}

	public void setAdhocSalaryHeads(BindingListModel adhocSalaryHeads) {
		this.adhocSalaryHeads = adhocSalaryHeads;
	}

	public List<GenericValue> getAllRules() {
		return allRules;
	}

	public void setAllRules(List<GenericValue> allRules) {
		this.allRules = allRules;
	}

	private Date getFromPeriod(String selectedFrequency, String year) {
		String startDay = "1";
		String month = selectedFrequency;
		if ("BIWEEKLY".equals(orgSalaryFrequency)) {
			startDay = selectedFrequency.substring(0, 2);
			month = selectedFrequency.substring(3);
		}
		return parseDate(startDay, month, year);
	}

	private Date getEndPeriod(Date fromPeriod) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromPeriod);
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		if ("BIWEEKLY".equals(orgSalaryFrequency)) {
			lastDay = lastDay / 2;
		}
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		return parseDate(String.valueOf(lastDay), allMonths.get(month), String.valueOf(year));
	}

	private Date parseDate(String startDay, String month, String year) {
		Date d = parseDateWithFormatter("dd-MMMMM-yyyy", startDay, month, year);
		return d;
	}

	private Date parseDateWithFormatter(String pattern, String startDay, String month, String year) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern == null ? "dd-MMMMM-yyyy" : pattern);
		int selectedyear = new Integer(year); 
		Date d = null;
		try {
			d = formatter.parse(startDay + "-" + month + "-" + selectedyear);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	private List<String> getEmployeeListForDepartment(String selectedDepartmentId, Date fromPeriod, Date toPeriod) throws Exception {

		List<GenericValue> l = delegator.findByAnd("Employment", "All".equals(selectedDepartmentId) ? null : UtilMisc.toMap("partyIdFrom",
				selectedDepartmentId));

		List<String> employeeList = new ArrayList<String>();
		for (GenericValue gv : l) {
			employeeList.add(gv.getString("partyIdTo"));
		}
		return employeeList;
	}

	public ListitemRenderer getAdhocListboxRenderer() {
		return new AdhocSalaryRenderer();
	}

	class AdhocSalaryRenderer implements ListitemRenderer {

		public void render(final Listitem li, final Object data) throws Exception {
			final Map<String, Object> each = (Map<String, Object>) data;

			Listcell cell_1 = new Listcell();
			cell_1.setParent(li);
			new Label((String) each.get("salaryHeadId")).setParent(cell_1);
			Listcell cell_2 = new Listcell();
			cell_2.setParent(li);
			new Label((String) each.get("name")).setParent(cell_2);
			Listcell cell_3 = new Listcell();
			cell_3.setParent(li);
			
			String computationType = (String)each.get("computationType");
			if("FORMULA".equals(computationType)){
			Combobox ruleBox = new Combobox();
			ruleBox.setModel(new BindingListModelList(allRules, false));
			ruleBox.setItemRenderer(new ComboitemRenderer() {
				public void render(Comboitem ci, Object data) throws Exception {
					GenericValue gv = (GenericValue) data;
					String ruleId = gv.getString("ruleId");
					ci.setValue(ruleId);
					ci.setLabel(gv.getString("ruleName"));
				}
			});
			each.put("rulebox", ruleBox);
			ruleBox.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event event) throws Exception {
					String ruleId = (String) ((Combobox) event.getTarget()).getSelectedItem().getValue();
					each.put("ruleId", ruleId);
				}
			});
			ruleBox.setParent(cell_3);
			}else{
				Doublebox lumpsumAmount = new Doublebox();
				lumpsumAmount.setFormat("#,#00.00#");
				lumpsumAmount.addEventListener("onSelect", new EventListener() {
					public void onEvent(Event event) throws Exception {
						Double amount = ((Doublebox) event.getTarget()).getValue();
						each.put("lumpsumpAmount", amount);
					}
				});
				each.put("rulebox", lumpsumAmount);
				lumpsumAmount.setParent(cell_3);
			}
			li.setValue(each);
		}

	}

	public void onClick$attachRule(Event event) throws GenericEntityException, InterruptedException {
		Listbox adhocSalaryHeads = (Listbox) event.getTarget().getFellowIfAny("adhocSalaryHeadListbox", true);
		Set<Listitem> selectedItems = adhocSalaryHeads.getSelectedItems();
		if(UtilValidate.isEmpty(selectedItems)){
			Messagebox.show("Please Select Any of One Adhoc Salary Heads","Warning",1,null);
			return;
		}	
		for (Iterator<Listitem> iter = selectedItems.iterator(); iter.hasNext();) {
			Listitem li = iter.next();
			Map selectedItem = (Map) li.getValue();
			Component comp = (Component)selectedItem.get("rulebox");
			if(comp instanceof Combobox){
			Combobox rulebox = (Combobox)comp;
			if (rulebox != null) {
				Comboitem ruleItem = rulebox.getSelectedItem();
				if (ruleItem == null)
					throw new WrongValueException(rulebox, "Select a Rule.");
			}
			org.ofbiz.entity.GenericValue gv = delegator.makeValidValue("SalaryHeadRule", UtilMisc.toMap("salaryHeadId",
					(String) selectedItem.get("salaryHeadId"), "ruleId", (String) selectedItem.get("ruleId")));
			delegator.removeByCondition("SalaryHeadRule", EntityCondition.makeCondition(UtilMisc.toMap("salaryHeadId",
					(String) selectedItem.get("salaryHeadId"))));
			delegator.createOrStore(gv);
			}else if(comp instanceof Doublebox){
				Doublebox amountBox = (Doublebox)comp;
				if(amountBox.getValue() == null)
					 throw new WrongValueException(amountBox,"Amount required");
				selectedItem.put("lumpsumAmount", amountBox.getValue());
				li.setValue(selectedItem);
			}
		}
		Messagebox.show("Added Successfully","Success",1,null);
	}

	public void onClick$runPayroll(Event event) throws Exception {
		adhocSalaryHeadListbox = (Listbox) event.getTarget().getFellowIfAny("adhocSalaryHeadListbox", true);
		List<Map> adhocSalaryHeads = new ArrayList<Map>();
		Set<Listitem> selectedItems = adhocSalaryHeadListbox.getSelectedItems();
		for (Listitem li : selectedItems) {
			Map m = (Map) li.getValue();
			String adhocSalaryHead = (String) m.get("salaryHeadId");
			Double lumpsumAmount = (Double) m.get("lumpsumAmount");
			System.out.println("\n\n\n Saveing Job Parameter Lumpsum Amount "+lumpsumAmount);
			adhocSalaryHeads.add(UtilMisc.toMap("salaryHeadId",adhocSalaryHead,"lumpsum",lumpsumAmount));
		}

		long startTime = startDate.getTime();
		System.out.println(startDate + "***** Start TIme " + startTime);
		Map<String, Object> serviceContext = new HashMap<String, Object>();
		serviceContext.put("userLogin", super.userLogin);
		serviceContext.put("periodFrom", new java.sql.Date(this.fromPeriod.getTime()));
		serviceContext.put("periodTo", new java.sql.Date(this.toPeriod.getTime()));
		serviceContext.put("linkToLMS", this.isLinkToLMS() ? "Y" : "N");
		
		Listbox eligibleEmployeeBox = (Listbox) event.getTarget().getFellowIfAny("eligibleEmployeeBox", true);
		Set<Listitem> selectedEmployees = eligibleEmployeeBox.getSelectedItems();
		Set<String> employeeIdList = new HashSet<String>();
		for (Listitem li : selectedEmployees) {
			String partyId = (String) li.getValue();
			employeeIdList.add(partyId);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(employeeIdList);
		oos.flush();
		Blob blob = new SerialBlob(baos.toByteArray());

		baos = new ByteArrayOutputStream(512);
		oos = new ObjectOutputStream(baos);
		oos.writeObject(adhocSalaryHeads);
		oos.flush();
		
		Blob adhocSalHeadLstBlob = new SerialBlob(baos.toByteArray());

		boolean beganTransaction = TransactionUtil.begin();
		String jobId = delegator.getNextSeqId("PayrollJob");
		GenericValue payrollJob = delegator.makeValidValue("PayrollJob", UtilMisc.toMap("jobId", jobId, "employeeIdList", blob,
				"fromPeriod", new java.sql.Date(this.fromPeriod.getTime()), "toPeriod", new java.sql.Date(this.toPeriod.getTime()),
				"status", "SCHEDULED", "adhocSalaryHeadList", adhocSalHeadLstBlob));
		delegator.create(payrollJob);
		serviceContext.put("jobId", jobId);
		JobManager manager = JobManager.registeredManagers.get(delegator.getDelegatorName());
		Calendar cal = Calendar.getInstance();
		cal.setTime(toPeriod);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		manager.schedule(jobId, "pool", "initiatePayrollJob", serviceContext, startTime, 6, 3600 * 24 * 1000, 1, cal.getTimeInMillis(), 0);
		TransactionUtil.commit(beganTransaction);
		baos.close();
		oos.close();
		Messagebox.show("Payroll Job Scheduled", "Success", Messagebox.OK, "", 1, new EventListener() {
			public void onEvent(Event evt) throws GenericEntityException {
				Executions.sendRedirect("/control/MyPayRoll?id=initiatePayroll");
			}
		});
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public static void deletePayRollJob(Event event, GenericValue gv, final Component comp) throws InterruptedException {
		final String payrollJobId = gv.getString("jobId");
		final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {

							try {
								delegator.removeByAnd("PayrollJob", UtilMisc.toMap("jobId", payrollJobId));
								delegator.removeByAnd("JobSandbox", UtilMisc.toMap("jobName", payrollJobId));
								Events.postEvent("onClick", comp, null);

							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Messagebox.show("PayRoll Job Deleted Successfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
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

	public static void generateSifFile(Event event, GenericValue gv, final Component comp) throws GenericEntityException, SerialException, SQLException{
		Date fromPeriod = gv.getDate("fromPeriod");
		Date toPeriod = gv.getDate("toPeriod");	
		
	    HashSet<String>	empIdList= (HashSet)gv.get("employeeIdList");
		
		final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		final String COMMA = ",";
		final String NEWLINE = "\r\n";
		
		GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", "Company") ,false);
		String emplrId = partyGroup.getString("employerId");
		String agentId = partyGroup.getString("agentId");
		String bankCode = partyGroup.getString("bankCode");
		EntityCondition ec1 = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO, fromPeriod);
		EntityCondition ec2 = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, toPeriod);
		EntityCondition empList = EntityCondition.makeCondition("partyId", EntityOperator.IN, empIdList);
		EntityCondition ec = EntityCondition.makeCondition(ec1, ec2, empList);
		List<GenericValue> paySlips = delegator.findList("EmplPayslipPerson", ec, null, null,null, false);
		
		EntityCondition ec3 = EntityCondition.makeCondition("salaryHeadTypeId", EntityOperator.EQUALS, "Adhoc");
		HashSet<String> salAdhocHeadfields = new HashSet<String>();
		salAdhocHeadfields.add("salaryHeadId");		
		List<GenericValue> salHeadAdhocTypes = delegator.findList("SalaryHead", ec3,  salAdhocHeadfields, null,null, false);
		
		List<String> adhcosTypes =  new ArrayList<String>();
		
		for(GenericValue salHeadAdhocType : salHeadAdhocTypes ){
			adhcosTypes.add(salHeadAdhocType.getString("salaryHeadId"));
		}
		StringBuffer content = new StringBuffer();
		BigDecimal totalSalary = BigDecimal.ZERO;
		DecimalFormat twoDeci = new DecimalFormat("#.##");
		for(GenericValue paySlip : paySlips){			
			EntityCondition e1 = EntityCondition.makeCondition("payslipId", EntityOperator.EQUALS, paySlip.getString("payslipId"));
			EntityCondition e2 = EntityCondition.makeCondition("salaryHeadId", EntityOperator.IN, adhcosTypes);
			EntityCondition e3 = EntityCondition.makeCondition(e1,e2);
			HashSet<String> salFlds = new HashSet<String>();
			salFlds.add("amount");
			List<GenericValue> paySalHeads= delegator.findList("EmplPayslipHeadGroup", e3, salFlds, null,null, false);
			double variableComp = 0.00;
			double fixedComp = 0.00;
			for(GenericValue paySalHead : paySalHeads){
				variableComp = variableComp + ((paySalHead.getDouble("amount")==null ? 0.00 : (double)paySalHead.getDouble("amount")));
			}				
			double  headTotal = (paySlip.getDouble("headTotal") == null ? 0.00 : (double)paySlip.getDouble("headTotal"));
			fixedComp = headTotal - variableComp;	
			totalSalary = totalSalary.add(new BigDecimal(headTotal));
			
			Integer numbOfDays = ((int)((paySlip.getDate("periodTo").getTime() - paySlip.getDate("periodFrom").getTime())/(24*60*60*1000)))+1;
			String numOfDaysVal= getNumberWithZeroPrefix(numbOfDays.toString(), 4);
		
			
			EntityCondition e5 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, paySlip.getString("partyId"));
			EntityCondition e6 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(fromPeriod.getTime()));
			EntityCondition e7 = EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(toPeriod.getTime()));
			EntityCondition lComb = EntityCondition.makeCondition(e5,e6,e7);			
			HashSet<String> leaveFlds = new HashSet<String>();
			leaveFlds.add("paidDays");
			leaveFlds.add("unpaidDays");leaveFlds.add("leaveId");leaveFlds.add("partyId");
			List<GenericValue>  emplLeaves=  delegator.findList("EmplLeave", lComb, leaveFlds, null,null, false);
			BigDecimal totalLeave = BigDecimal.ZERO;
			for(GenericValue emplLeave : emplLeaves){
				//totalLeave =  totalLeave.add(new BigDecimal((emplLeave.getDouble("paidDays")==null? 0 : emplLeave.getDouble("paidDays"))));
				totalLeave =  totalLeave.add(new BigDecimal((emplLeave.getDouble("unpaidDays")==null? 0: emplLeave.getDouble("unpaidDays"))));
			}	
			
			content.append("EDR");
			content.append(COMMA);
			content.append(getNumberWithZeroPrefix(paySlip.getString("partyId"),14));
			content.append(COMMA);
			content.append(agentId);
			content.append(COMMA);
			content.append(getNumberWithZeroPrefix(paySlip.getString("employeeAccWithAgent")==null ? "": paySlip.getString("employeeAccWithAgent"),9));
			content.append(COMMA);
			content.append(fromPeriod);
			content.append(COMMA);
			content.append(toPeriod);
			content.append(COMMA);
			content.append(numOfDaysVal);
			content.append(COMMA);			
			content.append(decimalFormatNumb(twoDeci.format(fixedComp)));
			content.append(COMMA);			
			content.append(decimalFormatNumb(twoDeci.format(variableComp)));
			content.append(COMMA);
			content.append(getNumberWithZeroPrefix(totalLeave.toString(),4));
			content.append(NEWLINE);	
		}
		Date date =  UtilDateTime.nowDate();
		String salMonth = "";
		salMonth  += fromPeriod.toString().substring(0, 4);
		salMonth  = fromPeriod.toString().substring(5, 7)+salMonth;
		String createdDate = (new java.sql.Date(date.getTime())).toString();
		String creatime = UtilDateTime.nowTimestamp().toString();
		creatime = creatime.substring(creatime.indexOf(' ')+1,creatime.indexOf('.'));
		String edrCnt = twoDeci.format(paySlips.size());
		creatime = creatime.replaceAll(":", "");
		content.append("SCR");
		content.append(COMMA);
		content.append(getNumberWithZeroPrefix(emplrId == null ? "" : emplrId.toString(),13));
		content.append(COMMA);
		content.append(getNumberWithZeroPrefix(bankCode == null ? "" : bankCode.toString(),9));
		content.append(COMMA);
		content.append(createdDate);
		content.append(COMMA);
		content.append(creatime.substring(0,2)+creatime.substring(2,4));
		content.append(COMMA);		
		content.append(salMonth);
		content.append(COMMA);
		content.append(decimalFormatNumb(edrCnt));
		content.append(COMMA);
		content.append(decimalFormatNumb(twoDeci.format(totalSalary)));
		content.append(COMMA);
		content.append(Labels.getLabel("HRMS_COMP_CURRENCY"));
		content.append(COMMA);		
		edrCnt = getNumberWithZeroPrefix(new Integer(paySlips.size()).toString(),10);
		content.append(emplrId+salMonth+"-"+edrCnt);
		
		String filename = emplrId+createdDate.substring(2,4)+createdDate.substring(5,7)+createdDate.substring(8,10)+creatime+".sif";
		Filedownload.save(content.toString().getBytes(), "text/csv", filename);		
	}	
	public static  String getNumberWithZeroPrefix(String val, int numbZeroPrefix){
	String tmpVal="";
	for(int i=0; (numbZeroPrefix-val.length())>i; i++)	
		tmpVal = "0"+tmpVal;	
	tmpVal = tmpVal+val;
	return tmpVal;
	}
	
	public static String decimalFormatNumb(String value){
		if(value.indexOf('.')<0)
			return value+".00";
		if(((value.length()-1) - value.indexOf('.'))==1)
			return value+"0";
		return value;
	}
}