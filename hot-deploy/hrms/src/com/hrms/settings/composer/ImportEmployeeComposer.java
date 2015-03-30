package com.hrms.settings.composer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericServiceException;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Window;

import au.com.bytecode.opencsv.CSVReader;

import com.hrms.composer.HrmsComposer;
import com.ibm.icu.text.SimpleDateFormat;

public class ImportEmployeeComposer extends HrmsComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6617187103526537434L;

	Listbox importBox;

	List<String> employeeFieldNames = Arrays.asList("partyId", "title", "firstName", "middleName", "lastName", "gender", "birthDate",
			"emergencyContactNumber", "bloodGroup", "nationality", "address1", "address2", "city", "stateProvinceGeoId", "countryCode",
			"emailAddress", "postalCode", "countryGeoId", "areaCode", "contactNumber", "positionType", "departmentId", "locationId",
			"role", "managerId", "joiningDate");

	List<Map<String, String>> allRowsAsMap = null;

	Map<String, String> reportingEmployeeMap = new HashMap<String, String>();
	private BindingListModelList fieldModel;

	private BindingListModelList rowModel;

	public void setFileUploadMedia(Media media) {
		if (media == null)
			return;
		CSVReader reader = new CSVReader(new InputStreamReader(media.getStreamData()));
		List<String[]> allRows = new ArrayList<String[]>();
		try {
			allRows = reader.readAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> columnsToImport = new ArrayList<String>(Arrays.asList(allRows.remove(0)));
		allRowsAsMap = convertToDataMap(columnsToImport, allRows);

		columnsToImport.add("<Skip this column>");
		fieldModel = new BindingListModelList(columnsToImport, false);
		List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
		for (String s : employeeFieldNames) {
            Map<String,String> map = new HashMap<String, String>();
            if (columnsToImport.contains(s)) {
                map.put("columnId",s);
                map.put("selectedFieldId",s);
				rows.add(map);
            }
			else{
                map.put("columnId",s);
                map.put("selectedFieldId","<Skip this column>");
				rows.add(map);
            }
		}

		rowModel = new BindingListModelList(rows, false);
		binder.loadAttribute(importBox, "model");
	}

	public BindingListModelList getFieldModel() {
		return fieldModel;
	}

	public void setFieldModel(BindingListModelList fieldModel) {
		this.fieldModel = fieldModel;
	}

	public BindingListModelList getRowModel() {
		return rowModel;
	}

	public void setRowModel(BindingListModelList rowModel) {
		this.rowModel = rowModel;
	}

	private List<Map<String, String>> convertToDataMap(List<String> columnsToImport, List<String[]> allRows) {
		List<Map<String, String>> l = new ArrayList<Map<String, String>>();
		for (String[] row : allRows) {
			Map<String, String> m = new HashMap<String, String>();
			int index = 0;
			for (String s : columnsToImport) {
				m.put(s, row[index++]);
			}
			l.add(m);
		}
		return l;
	}

	public List<String> getEmployeeFieldNames() {
		return employeeFieldNames;
	}

	public void setEmployeeFieldNames(List<String> employeeFieldNames) {
		this.employeeFieldNames = employeeFieldNames;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
	}

	@SuppressWarnings("unchecked")
	public void startImport(Event event) throws Exception {

		/*Window progressWin = (Window) event.getTarget().getFellowIfAny("progressWin", true);
		progressWin.setVisible(false);
		Progressmeter meter = (Progressmeter) progressWin.getFellowIfAny("progressbar");
		progressWin.doModal();*/

		Map<String, Object> m = new HashMap<String, Object>();
		if(allRowsAsMap == null){
			Messagebox.show("Upload File Before Import", "Error", 1, null);
			return;
		}
		int numerOfRecords = allRowsAsMap.size();
		int counter = 1;
		for (int row = 0; row < allRowsAsMap.size(); row++) {
			boolean beganTransaction = TransactionUtil.begin();
			Map inputMap = allRowsAsMap.get(row);
			for (int col = 0; col < rowModel.getSize(); col++) {
				Map mapping = (Map) rowModel.getElementAt(col);
				String columnData = (String) inputMap.get(mapping.get("selectedFieldId"));
				if (columnData != null && columnData.startsWith("<"))
					continue;

				if (columnData != null && columnData.contains("/")) {
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					try {
						System.out.println(mapping.get("columnId") + " Data "+columnData+" After format" + df.parse(columnData));
						m.put((String) mapping.get("columnId"), new java.sql.Date(df.parse(columnData).getTime()));
					} catch (ParseException pe) {
					}
				} else {
					m.put((String) mapping.get("columnId"), columnData);
				}

			}
			ModelEntity entityDef = delegator.getModelReader().getModelEntity("Person");
			List<String> fieldNames = entityDef.getAllFieldNames();
			Map<String, Object> context = createServiceContext(fieldNames, m);
			context.put("userLogin", userLogin);
			Map results = dispatcher.runSync("createPerson", context);
			if (results.get("errorMessage") != null){
				Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
				return;
			}
			String partyId = (String) results.get("partyId");

			entityDef = delegator.getModelReader().getModelEntity("PostalAddress");
			fieldNames = entityDef.getAllFieldNames();
			context = createServiceContext(fieldNames, m);
			context.put("partyId", partyId);
			context.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
			context.put("userLogin", userLogin);
			results = dispatcher.runSync("createPartyPostalAddress", context);
			if (results.get("errorMessage") != null){
				Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
				return;
			}
			entityDef = delegator.getModelReader().getModelEntity("TelecomNumber");
			fieldNames = entityDef.getAllFieldNames();
			context = createServiceContext(fieldNames, m);
			context.put("partyId", partyId);
			context.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
			context.put("userLogin", userLogin);
			results = dispatcher.runSync("createPartyTelecomNumber", context);
			if (results.get("errorMessage") != null){
				Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
				return;
			}

			context.clear();
			context.put("partyId", partyId);
			context.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
			context.put("emailAddress", m.get("emailAddress"));
			context.put("userLogin", userLogin);
			results = dispatcher.runSync("createPartyEmailAddress", context);
			if (results.get("errorMessage") != null){
				Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
				return;
			}
			
			// access permission

						boolean isEmployeeOnly = ((String) m.get("role")).toUpperCase().equals("EMPLOYEE");
						context.clear();
						context.put("userLogin", userLogin);
						context.put("partyId", partyId);
						context.put("roleTypeId", "EMPLOYEE");
						results = dispatcher.runSync("createPartyRole", context);
						if (results.get("errorMessage") != null){
							Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
							return;
						}
						if (!isEmployeeOnly) {
							context.clear();
							context.put("userLogin", userLogin);
							context.put("partyId", partyId);
							context.put("roleTypeId", ((String) m.get("role")).toUpperCase());
							results=dispatcher.runSync("createPartyRole", context);
							if (results.get("errorMessage") != null){
								Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
								return;
							}
						}

			// Create Employment Link
			context.clear();
			context.put("userLogin", userLogin);
			context.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
			context.put("roleTypeIdTo", "EMPLOYEE");
			context.put("partyIdFrom", m.get("departmentId"));
			context.put("partyIdTo", partyId);
			Date joiningDate = ((Date) m.get("joiningDate"));
			if (joiningDate == null)
				joiningDate = UtilDateTime.nowDate();

			context.put("fromDate", new java.sql.Date(joiningDate.getTime()));
			results = dispatcher.runSync("createEmployment", context);
			if (results.get("errorMessage") != null){
				Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
				return;
			}

			// Associate with Department
			GenericValue positionGV = HumanResUtil.getPositionIdForPositionType((String) m.get("departmentId"), (String) m
					.get("positionType"),delegator);
			String positionId = null;
			if (positionGV == null) {
				positionId = createNewPosForPositionType((String) m.get("departmentId"), (String) m.get("positionType"));
			} else {
				positionId = positionGV.getString("emplPositionId");
			}
			context.clear();
			context.put("userLogin", userLogin);
			context.put("emplPositionId", positionId);
			context.put("partyId", partyId);
			context.put("fromDate", UtilDateTime.nowTimestamp());
			results = dispatcher.runSync("createEmplPositionFulfillment", context);
			if (results.get("errorMessage") != null){
				Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
				return;
			}

			positionGV = HumanResUtil.getLatestEmplPositionFulfillmentForParty((String) m.get("managerId"), delegator);

			String managerPosId = positionGV == null ? null : positionGV.getString("emplPositionId");

			positionGV = HumanResUtil.getLatestEmplPositionFulfillmentForParty((String) m.get("partyId"), delegator);
			String emplPosId = positionGV == null ? null : positionGV.getString("emplPositionId");

			if (managerPosId != null)
				createReportingStructure(emplPosId, managerPosId);
			else {
                Map<String,String> map = new HashMap<String, String>();
                map.put((String) m.get("partyId"),(String) m.get("managerId"));
				/*reportingEmployeeMap.putAll(UtilMisc.toMap((String) m.get("partyId"), (String) m.get("managerId")));*/
                reportingEmployeeMap.putAll(map);
			}
			//meter.setValue(new Double((counter++ / numerOfRecords) * 100).intValue());
			TransactionUtil.commit(beganTransaction);
		}

		for (Map.Entry<String, String> entry : reportingEmployeeMap.entrySet()) {
			String partyId = entry.getKey();
			String mgrId = entry.getValue();
			GenericValue managerpositionGV = HumanResUtil.getLatestEmplPositionFulfillmentForParty(mgrId, delegator);
			String managerPosId = managerpositionGV == null ? null : managerpositionGV.getString("emplPositionId");
			GenericValue emplpositionGV = HumanResUtil.getLatestEmplPositionFulfillmentForParty(partyId, delegator);
			String empPosId = emplpositionGV == null ? null : emplpositionGV.getString("emplPositionId");
			if (managerPosId != null && empPosId != null)
				createReportingStructure(empPosId, managerPosId);
		}
		Messagebox.show("Employee Imported Successfully", "Success", 1, null);	
	}

	private String createNewPosForPositionType(String departmentId, String positionTypeId) throws GenericServiceException {
		return (String) dispatcher.runSync(
				"createEmplPosition",
				UtilMisc.toMap("userLogin", userLogin, "partyId", departmentId, "emplPositionTypeId", positionTypeId, "statusId",
						"EMPL_POS_ACTIVE", "salaryFlag", "Y", "exemptFlag", "N", "fulltimeFlag", "Y")).get("emplPositionId");
	}

	private Map<String, Object> createServiceContext(List<String> fieldNames, Map<String, Object> m) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		for (String f : fieldNames) {
			if (m.get(f) != null) {
				returnMap.put(f, m.get(f));
			}
		}
		return returnMap;
	}

	protected void createReportingStructure(String emplPosId, String managerPosId) throws GenericServiceException, InterruptedException {
		Map results = dispatcher.runSync("createEmplPositionReportingStruct", UtilMisc.toMap("userLogin", userLogin,
				"emplPositionIdReportingTo", managerPosId, "emplPositionIdManagedBy", emplPosId, "primaryFlag", "Y", "fromDate",
				UtilDateTime.nowTimestamp(), "thruDate", null));
		if (results.get("errorMessage") != null) {
			Messagebox.show((String) results.get("errorMessage"), "Error", 1, null);
			return;
		}
	}
}
