package com.ndz.controller;

import com.ndz.zkoss.util.HrmsInfrastructure;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

import java.sql.Timestamp;
import java.util.*;

public class CreateDepartmentController extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	public void doAfterCompose(Component createDepartment) throws Exception {
		super.doAfterCompose(createDepartment);
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, "HOD");
		Set groupFieldToDisplay = new HashSet();
		groupFieldToDisplay.add("emplPositionId");
		groupFieldToDisplay.add("partyId");
		groupFieldToDisplay.add("emplPositionTypeId");
		List employeeDepartmentGroup = delegator.findList("EmplPosition", condition, groupFieldToDisplay, null, null, false);
		Object employeeDepartmentGroupArray = employeeDepartmentGroup.toArray(new GenericValue[employeeDepartmentGroup.size()]);

		SimpleListModel employeeDepartmentList = new SimpleListModel(employeeDepartmentGroup.toArray());

		Listbox employeeDepartment = (Listbox) createDepartment.getFellow("dataGrid");
		employeeDepartment.setModel(employeeDepartmentList);
		// employeeDepartment.setItemRenderer(new GenericValueRenderer(new
		// String[] { "emplPositionId", "partyId" }));
	}

	public static void createDepartment(Event event) throws InterruptedException {
		Boolean beganTransaction = false;
        Boolean isDuplicateCodeExists = false;
		try {
			beganTransaction = TransactionUtil.begin();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			Component createDepartment = event.getTarget();
			String departmentIdName = null;
			String departmentName = ((Textbox) createDepartment.getFellow("departmentName")).getValue();
			String departmentId = "Dep-" + delegator.getNextSeqId("DepartmentPosition");
			String headPositionId = ((Bandbox) createDepartment.getFellow("headPositionId")).getValue();
			Date fromDateInput = (Date) ((Datebox) createDepartment.getFellow("createDepartmentFromDate")).getValue();
			java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());
			Listitem locationItem = (Listitem) ((Listbox) createDepartment.getFellow("locationId")).getSelectedItem();
			String location = (String) locationItem.getValue();
            String departmentCode = ((Textbox) createDepartment.getFellow("departmentCode")).getValue();

            Set<Listitem> divisionIdSelected = new HashSet<Listitem>();
            divisionIdSelected =  ((Listbox) createDepartment.getFellow("divisionId")).getSelectedItems();

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map context = UtilMisc.toMap("userLogin", userLogin, "partyId", departmentId, "groupName", departmentName);
			Map result = null;
			result = dispatcher.runSync("createPartyGroup", context);
			if (((String) result.get("responseMessage")).equalsIgnoreCase("error")) {
				Messagebox.show("Department Id Already Exist", "Error", 1, null);
				return;
			}
			String partyId = (String) result.get("partyId");

			Map<String, Object> employmentContext = new HashMap<String, Object>();
            employmentContext.put("userLogin",userLogin);
            employmentContext.put("partyId",partyId);
            employmentContext.put("roleTypeId","ORGANIZATION_ROLE");
			Map<String, Object> partyRoleResult = dispatcher.runSync("createPartyRole", employmentContext);

			Map<String, Object> employeeRelationship = UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", "Company", "partyIdTo", partyId, "roleTypeIdFrom",
					"INTERNAL_ORGANIZATIO", "roleTypeIdTo", "ORGANIZATION_ROLE", "partyRelationshipTypeId", "GROUP_ROLLUP", "fromDate", UtilDateTime.nowTimestamp());
			Map relationshipResult = dispatcher.runSync("createPartyRelationship", employeeRelationship);

			Map<String, Object> createDepartmentMap = new HashMap<String, Object>();
			createDepartmentMap.put("departmentId", departmentId);
			createDepartmentMap.put("departmentName", departmentName);
			createDepartmentMap.put("locationId", location);
			createDepartmentMap.put("departmentPositionId", headPositionId);
			createDepartmentMap.put("fromDate", UtilDateTime.toTimestamp(fromDate));
            createDepartmentMap.put("departmentCode", departmentCode);
            List<GenericValue> DepartmentPositionGv = delegator.findByAnd("DepartmentPosition",UtilMisc.toMap("departmentCode",departmentCode),null,false);
            if(DepartmentPositionGv.size()>0){
                isDuplicateCodeExists=true;
            }
		    delegator.create("DepartmentPosition", createDepartmentMap);
            for(Listitem item:divisionIdSelected){
                String divisionId = (String) item.getValue();
                GenericValue departmentDivision = delegator.makeValidValue("DepartmentDivision",UtilMisc.toMap("departmentId",departmentId,"divisionId",divisionId));
                try{
                    delegator.create(departmentDivision);
                }catch (GenericEntityException e){
                    Messagebox.show("Duplicate division-department", "Error", 1, null);
                    return;
                }

            }

			Messagebox.show("Department Created Successfully", "Success", 1, null);

			Component componentPath = Path.getComponent("/searchPanel//searchButton");

			if (componentPath != null)
				Events.postEvent("onClick", componentPath, null);
			createDepartment.getFellow("createDepartment").detach();
			if (beganTransaction)
				TransactionUtil.commit();
		} catch (Exception e) {
			if (beganTransaction)
				try {
					TransactionUtil.rollback();
                    if(isDuplicateCodeExists){
                        Messagebox.show("Duplicate department code", "Error", 1, null);
                    }else{
                        Messagebox.show("Duplicate department name for the selected location already exists", "Error", 1, null);
                    }
				} catch (GenericTransactionException e1) {
					e1.printStackTrace();
				}
			e.printStackTrace();
		}
	}

	public static void editDepartment(Event event) throws GenericEntityException, InterruptedException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Component editDepartmentWindow = event.getTarget();
		String departmentName = (String) ((Textbox) editDepartmentWindow.getFellow("departmentName")).getValue();
		String departmentId = (String) ((Label) editDepartmentWindow.getFellow("departmentId")).getValue();
		String posId = (String) ((Bandbox) editDepartmentWindow.getFellow("headPositionId")).getValue();
		Listitem locationItem = (Listitem) ((Listbox) editDepartmentWindow.getFellow("deptLocation")).getSelectedItem();
		String location = (String) locationItem.getValue();
		Date fromDateType = (Date) ((Datebox) editDepartmentWindow.getFellow("fromDate")).getValue();
		Timestamp fromDate = new Timestamp(fromDateType.getTime());
		GenericValue requisitionGV = delegator.findByPrimaryKey("DepartmentPosition", UtilMisc.toMap("departmentId", departmentId));
		Map locationMap = null;
		locationMap = UtilMisc.toMap("departmentId", departmentId, "departmentName", departmentName, "departmentPositionId", posId, "fromDate", fromDate,
				"locationId", location);
		requisitionGV.putAll(locationMap);
        Set<Listitem> divisionIdSelected = new HashSet<Listitem>();
        divisionIdSelected =  ((Listbox) editDepartmentWindow.getFellow("divisionId")).getSelectedItems();

        List<Listitem> divisionIdNotSelected = ((Listbox) editDepartmentWindow.getFellow("divisionId")).getItems();
		try {
			requisitionGV.store();
		} catch (GenericEntityException e) {
			Messagebox.show("Duplicate department name for the selected location", "Error", 1, null);
			return;
		}
        for(Listitem listitem:divisionIdNotSelected){
            String divisionId = (String) listitem.getValue();
            GenericValue departmentDivision = delegator.makeValidValue("DepartmentDivision",UtilMisc.toMap("departmentId",departmentId,"divisionId",divisionId));
            try {
                delegator.removeValue(departmentDivision);
            }catch (GenericEntityException e){
                Messagebox.show("Error While removing division", "Error", 1, null);
                return;
            }
        }
        for(Listitem item:divisionIdSelected){
            String divisionId = (String) item.getValue();
            GenericValue departmentDivision = delegator.makeValidValue("DepartmentDivision",UtilMisc.toMap("departmentId",departmentId,"divisionId",divisionId));
            try{
                delegator.createOrStore(departmentDivision);
            }catch (GenericEntityException e){
                Messagebox.show("Duplicate division-department", "Error", 1, null);
                return;
            }

        }
		Messagebox.show("Updated Successfully", "Success", 1, null);
		Events.postEvent("onClick",Path.getComponent("/searchPanel/searchButton"),null);
		editDepartmentWindow.getFellow("editDepartmentWindow").detach();

	}

	public static void deleteDepartment(Event event, GenericValue gv, final Button btn) throws InterruptedException {
		final Component searchPanel = event.getTarget();
		final String departmentId = gv.getString("departmentId");
		final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {

					try {
						List employmentList = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdFrom", departmentId),null,false);
						if (employmentList.size() > 0){
                            Messagebox.show("Cannot be Deleted;Employee is already associated with it", "Error", 1, null);
                            return;
                        }
                        if(isDepartmentIsAssociatedWithDivision(departmentId,delegator)){
                            Messagebox.show("Cannot be Deleted;Division is already associated with it", "Error", 1, null);
                            return;
                        }
						delegator.removeByAnd("DepartmentPosition", UtilMisc.toMap("departmentId", departmentId));
						delegator.removeByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", departmentId));
						Events.postEvent("onClick", btn, null);

					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
					try {
						Messagebox.show("Department Deleted Successfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
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

    public static boolean isDepartmentIsAssociatedWithDivision(String departmentId,Delegator delegator) throws GenericEntityException {
        List<GenericValue> departmentDivision =  delegator.findByAnd("DepartmentDivision",UtilMisc.toMap("departmentId",departmentId),null,false);
        if(departmentDivision.size()>0){
            return true;
        }
        return false;
    }
}
