package com.ndz.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.UtilService;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class BudgetManagementController extends GenericForwardComposer {

	public static void createBudgetType( Event event ) throws InterruptedException {

		try {
			UtilService.runService("createBudgetType", event);
			Messagebox.show("Saved successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateBudgetType( Event event ) throws InterruptedException {

		try {
			UtilService.runService("updateBudgetType", event);
			Messagebox.show("Updated successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteBudgetType( final Event event, final GenericValue gv ) throws InterruptedException {
		Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent( Event evt ) {
				if ("onYes".equals(evt.getName())) {


					try {

						Component searchPanel = event.getTarget();
						GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
						GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
						String budgetTypeId = gv.getString("budgetTypeId");
						Map map = UtilMisc.toMap("userLogin", userLogin, "budgetTypeId", budgetTypeId);
						LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
						dispatcher.runSync("deleteBudgetType", map);
						Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
						Messagebox.show("Deleted successfully", "Success", 1, null);
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void createBudgetItemType( Event event ) throws InterruptedException {

		try {
			UtilService.runService("createBudgetItemType", event);
			Messagebox.show("Saved successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateBudgetItemType( Event event ) throws InterruptedException {

		try {
			UtilService.runService("updateBudgetItemType", event);
			Messagebox.show("Updated successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteBudgetItemType( final Event event, final GenericValue gv ) throws InterruptedException {
		Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent( Event evt ) {
				if ("onYes".equals(evt.getName())) {
					try {
						Component searchPanel = event.getTarget();
						GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
						GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
						String budgetItemTypeId = gv.getString("budgetItemTypeId");
						Map map = UtilMisc.toMap("userLogin", userLogin, "budgetItemTypeId", budgetItemTypeId);
						LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
						dispatcher.runSync("deleteBudgetItemType", map);
						Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
						try {
							Messagebox.show("deleted successfully", "Success", 1, null);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void createBudget( Event event ) throws InterruptedException {

		try {
			UtilService.runService("createBudget", event);
			Messagebox.show("Saved successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateBudget( Event event ) throws InterruptedException {

		try {
			UtilService.runService("updateBudget", event);
			Messagebox.show("Updated successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void deleteBudget( Event event, GenericValue gv ) throws InterruptedException {

		final Component searchPanel = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		String budgetId = gv.getString("budgetId");
		final Map map = UtilMisc.toMap("userLogin", userLogin, "budgetId", budgetId);
		final LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent( Event evt ) {
				if ("onYes".equals(evt.getName())) {
					try {

						Map result=dispatcher.runSync("deleteBudget", map);
						if (result.get("errorMessage")==null)
						{

						Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
						Messagebox.show("Deleted successfully", "Success", 1, null);
						}
						else{
							Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
							Messagebox.show("The selected Budget is in use; can't be deleted", "Error", 1, null);
						}
					} catch (Exception e) {

					}
				}
			}
		});
	}

	public static void createBudgetItem( Event event ) throws InterruptedException, GenericEntityException {

		try {
			Component addBudgetItemWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			Listitem budgetIdItem = (Listitem) ((Listbox) addBudgetItemWindow.getFellow("budgetId")).getSelectedItem();
			String budgetId = (String) budgetIdItem.getValue();
			Listitem budgetTypeItem = (Listitem) ((Listbox) addBudgetItemWindow.getFellow("budgetItemTypeId")).getSelectedItem();
			String budgetItemType = (String) budgetTypeItem.getValue();
			String amountType = (String) ((Textbox) addBudgetItemWindow.getFellow("amount")).getValue();
			BigDecimal amount = new BigDecimal(amountType);
			String purpose = (String) ((Textbox) addBudgetItemWindow.getFellow("purpose")).getValue();
			String justification = (String) ((Textbox) addBudgetItemWindow.getFellow("justification")).getValue();
			String budgetItemSeqId = delegator.getNextSeqId("BudgetItem");
			Map map = UtilMisc.toMap("budgetId", budgetId, "budgetItemSeqId", budgetItemSeqId, "budgetItemTypeId", budgetItemType, "amount", amount, "purpose", purpose, "justification", justification);
			GenericValue budgetItem = delegator.makeValue("BudgetItem", map);
			delegator.create(budgetItem);
			Messagebox.show("Created successfully", "Success", 1, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void updateBudgetItem( Event event ) throws InterruptedException {

		try {
			Component editBudgetItemWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			Listitem budgetIdItem = (Listitem) ((Listbox) editBudgetItemWindow.getFellow("budgetId")).getSelectedItem();
			String budgetId = (String) budgetIdItem.getValue();
			Listitem budgetTypeItem = (Listitem) ((Listbox) editBudgetItemWindow.getFellow("budgetItemTypeId")).getSelectedItem();
			String budgetItemType = (String) budgetTypeItem.getValue();
			String amountType = (String) ((Textbox) editBudgetItemWindow.getFellow("amount")).getValue();
			String budgetItemSeqId = (String) ((Textbox) editBudgetItemWindow.getFellow("budgetItemSeqId")).getValue();
			BigDecimal amount = new BigDecimal(amountType);
			String purpose = (String) ((Textbox) editBudgetItemWindow.getFellow("purpose")).getValue();
			String justification = (String) ((Textbox) editBudgetItemWindow.getFellow("justification")).getValue();
			Map map = UtilMisc.toMap("budgetItemTypeId", budgetItemType, "amount", amount, "purpose", purpose, "justification", justification);
			GenericValue budgetItemGV = null;
			List<GenericValue> budgetItemListGV = delegator.findByAnd("BudgetItem", UtilMisc.toMap("budgetId", budgetId, "budgetItemSeqId", budgetItemSeqId));
			budgetItemGV = EntityUtil.getFirst(budgetItemListGV);
			budgetItemGV.putAll(map);
			budgetItemGV.store();
			Messagebox.show("Updated successfully", "Success", 1, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void deleteBudgetItem( Event event, GenericValue gv ) throws InterruptedException {
			final Component searchPanel = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			String budgetId = gv.getString("budgetId");
			String budgetItemSeqId = gv.getString("budgetItemSeqId");
			final Map map = UtilMisc.toMap("userLogin", userLogin, "budgetId", budgetId, "budgetItemSeqId", budgetItemSeqId);
			final LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
				public void onEvent( Event evt ) {
					if ("onYes".equals(evt.getName())) {
						try {

							Map result=dispatcher.runSync("deleteBudgetItem", map);
							if (result.get("errorMessage")==null)
							{

							Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
							Messagebox.show("Deleted successfully", "Success", 1, null);
							}
							else{
								Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
								Messagebox.show("The selected Budget is in use; can't be deleted ", "Error", 1, null);
							}
						} catch (Exception e) {

						}
					}
				}
			});

	}

	public static void createEnumeration( Event event ) throws InterruptedException {
		try {
			UtilService.runService("createEnumeration", event);
			Messagebox.show("Added Successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void editEnumeration( Event event, GenericValue gv, Map map ) throws GenericServiceException, SuspendNotAllowedException, InterruptedException {
		final Window win = (Window) Executions.createComponents("/zul/GlobalHRSetting/editEnumeration.zul", null, map);
		win.doModal();

		Textbox enumId = (Textbox) win.getFellow("enumId");
		enumId.setValue(gv.getString("enumId"));
		Label enumIdLabel = (Label) win.getFellow("enumIdLabel");
		enumIdLabel.setValue(gv.getString("enumId"));
		Textbox enumCode = (Textbox) win.getFellow("enumCode");
		enumCode.setValue(gv.getString("enumCode"));
		Textbox sequenceId = (Textbox) win.getFellow("sequenceId");
		sequenceId.setValue(gv.getString("sequenceId"));
		Textbox description = (Textbox) win.getFellow("description");
		description.setValue(gv.getString("description"));

	}

	public static void updateEnumeration( Event event ) throws GenericEntityException, GenericServiceException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");

			Component searchEnumeration = event.getTarget();

			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String enumId = ((Textbox) searchEnumeration.getFellow("enumId")).getValue();
			String enumCode1 = ((Textbox) searchEnumeration.getFellow("enumCode")).getValue();
			String sequenceId1 = ((Textbox) searchEnumeration.getFellow("sequenceId")).getValue();
			String description1 = ((Textbox) searchEnumeration.getFellow("description")).getValue();
			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "enumCode", enumCode1,"sequenceId" ,sequenceId1, "description", description1);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			//dispatcher.runSync("updateEnumeration", context);
			UtilService.runService("updateEnumeration", event);
			//Events.postEvent("onClick$searchButton", searchEnumeration.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Updated Successfully", "Success", 1, null);
			searchEnumeration.detach();
		} catch (Exception e) {

		}
	}




	public void delete( final Event event, final String enumId ) throws GenericServiceException, InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent( Event evt ) {
				if ("onYes".equals(evt.getName())) {
					try {
						Component comp = event.getTarget();
						GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
						GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
						delegator.removeByAnd("Enumeration", UtilMisc.toMap("enumId",enumId));
						Messagebox.show("Deleted Successfully", "Success", 1, null);
						Events.postEvent("onClick$searchPerCompany", comp.getPage().getFellow("searchPanel"), null);
					} catch (Exception e) {

					}
				}
			}
		});

	}

}
