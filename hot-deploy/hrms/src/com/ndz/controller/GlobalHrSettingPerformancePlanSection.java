package com.ndz.controller;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class GlobalHrSettingPerformancePlanSection extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;

	public void onClick$btnSave(Event event) {

		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession()
					.getAttribute("userLogin");

			Component addNewPerformancePlanSections = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String SectionName = ((Textbox) addNewPerformancePlanSections.getFellow("txtSectionName")).getValue();
			System.out.println("********SectionName********" + SectionName);
			String Description = ((Textbox) addNewPerformancePlanSections.getFellow("txtBoxDescription")).getValue();
			System.out.println("********Description********" + Description);

			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "sectionName", SectionName,
					"description", Description);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("createPerfReviewSection", context);
			Messagebox.show("Created Successfully", "Success", 1, null);
			Component performanceReviewMainComponent = addNewPerformancePlanSections.getPage().getFellowIfAny(
					"performanceReviewMain");
			Component searchPanelComponent = null;
			if (performanceReviewMainComponent != null)
				searchPanelComponent = performanceReviewMainComponent.getFellowIfAny("searchPanel");
			if (searchPanelComponent != null)
				Events.postEvent("onClick$searchButton", searchPanelComponent, null);
			addNewPerformancePlanSections.detach();
		} catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDescription_Fail"), "Error", 1, null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void delete(final Event event, final String perfReviewSectionId) throws GenericServiceException,
			InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {
							try {
								Component window = event.getTarget();
								GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop()
										.getSession().getAttribute("userLogin");
								GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

								Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin,
										"perfReviewSectionId", perfReviewSectionId);

								LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

								Map result = dispatcher.runSync("deletePerfReviewSection", context);
								if (result.get("errorMessage") == null) {
									Messagebox.show(Labels.getLabel("MessageDelete_Successful"), "Success", 1, null);
									Events.postEvent("onClick$searchButton",
											window.getPage().getFellow("performanceReviewMain")
													.getFellow("searchPanel"), null);

								} else {
									Messagebox.show("The Selected Section is in Use; Can't be Deleted.", "Error", 1,
											null);
								}

							} catch (Exception e) {
							}
						}
					}
				});

	}

	public void resPonsibilityTypesEdit(Event event, GenericValue gv) throws GenericServiceException,
			SuspendNotAllowedException, InterruptedException {
		final Window win = (Window) Executions.createComponents("/zul/GlobalHRSetting/performancePlanSectionEdit.zul",
				null, null);
		win.doModal();
		Textbox txtDescription = (Textbox) win.getFellow("txtBoxDescription");
		txtDescription.setValue(gv.getString("description"));

		Textbox txtBoxSectionName = (Textbox) win.getFellow("txtBoxSectionName");
		txtBoxSectionName.setValue(gv.getString("sectionName"));

		Textbox oldTxtBoxSectionName = (Textbox) win.getFellow("oldTxtBoxSectionName");
		oldTxtBoxSectionName.setValue(gv.getString("sectionName"));

		Textbox txtBoxSectionId = (Textbox) win.getFellow("txtBoxSectionId");
		txtBoxSectionId.setValue(gv.getString("perfReviewSectionId"));
	}

	public void onClick$btnEditPerformancePlanSection(Event event) throws GenericEntityException,
			GenericServiceException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession()
					.getAttribute("userLogin");

			Component comp = event.getTarget();

			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String sectionId = ((Textbox) comp.getFellow("txtBoxSectionId")).getValue();
			String description = ((Textbox) comp.getFellow("txtBoxDescription")).getValue();
			String sectionName = ((Textbox) comp.getFellow("txtBoxSectionName")).getValue();

			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "perfReviewSectionId", sectionId,
					"description", description, "sectionName", sectionName);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			dispatcher.runSync("updatePerfReviewSection", context);
			Messagebox.show(Labels.getLabel("Message_EditSuccessfully"), "Success", 1, null);
			Events.postEvent("onClick$searchButton",
					comp.getPage().getFellow("performanceReviewMain").getFellow("searchPanel"), null);

			comp.detach();
		} catch (Exception e) {
			try {
				Messagebox.show(Labels.getLabel("Message_EditUnSuccessfully"), "Error", 1, null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
