package com.ndz.controller;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class GlobalHrSettingPerformanceRating extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;

	public void onClick$btnSaveLeaveType(Event event) {

		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component newPerformanceRating = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String rating = ((Textbox) newPerformanceRating.getFellow("txtRating")).getValue();
			String Description = ((Textbox) newPerformanceRating.getFellow("txtBoxDescription")).getValue();
			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "rating", rating, "description", Description);
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();// GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("createPerfRating", context);
			Messagebox.show(Labels.getLabel("MessageDescription_SavedSuccessfully"), "Success", 1, null);
			Component performanceReviewMainComponent = newPerformanceRating.getPage().getFellowIfAny("performanceReviewMain");
			Component searchPanelComponent = null;
			if (performanceReviewMainComponent != null)
				searchPanelComponent = performanceReviewMainComponent.getFellowIfAny("searchPanel");
			if (searchPanelComponent != null)
				Events.postEvent("onClick$searchButton", searchPanelComponent, null);
			newPerformanceRating.detach();
		} catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDescription_Fail"), "Error", 1, null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

	}

	public void delete(final Event event, final String ratingId) throws GenericServiceException, InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {
					try {
						Component window = event.getTarget();
						GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
						GenericDelegator delegator = HrmsInfrastructure.getDelegator();// GenericDelegator.getGenericDelegator("default");

						Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "ratingId", ratingId);

						LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();// GenericDispatcher.getLocalDispatcher("default", delegator);
						dispatcher.runSync("deletePerfRating", context);
						Messagebox.show(Labels.getLabel("MessageDelete_Successful"), "Success", 1, null);
						Events.postEvent("onClick$searchButton", window.getPage().getFellow("performanceReviewMain").getFellow("searchPanel"), null);
					} catch (Exception e) {

						try {
							Messagebox.show(Labels.getLabel("MessageDelete_UnSuccessful"), "Error", 1, null);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});

	}

	public void perRating(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException {
		final Window win = (Window) Executions.createComponents("/zul/GlobalHRSetting/performanceRatingEdit.zul", null, null);
		win.doModal();
		Textbox txtRating = (Textbox) win.getFellow("txtRating");
		txtRating.setValue(gv.getString("rating"));

		Textbox txtBoxDescription = (Textbox) win.getFellow("txtBoxDescription");
		txtBoxDescription.setValue(gv.getString("description"));
		
		Textbox oldTxtRating = (Textbox) win.getFellow("oldTxtRating");
		oldTxtRating.setValue(gv.getString("rating"));

		Textbox oldTxtBoxDescription = (Textbox) win.getFellow("oldTxtBoxDescription");
		oldTxtBoxDescription.setValue(gv.getString("description"));

		Textbox txtRatingId = (Textbox) win.getFellow("txtRatingId");
		txtRatingId.setValue(gv.getString("ratingId"));
	}

	public void onClick$btnEdit(Event event) throws GenericEntityException, GenericServiceException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");

			Component comp = event.getTarget();

			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String reviewSectionId = ((Textbox) comp.getFellow("txtRatingId")).getValue();
			String rating = ((Textbox) comp.getFellow("txtRating")).getValue();

			String description = ((Textbox) comp.getFellow("txtBoxDescription")).getValue();
			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "ratingId", reviewSectionId, "description", description, "rating",
					rating);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			dispatcher.runSync("updatePerfRating", context);
			Messagebox.show(Labels.getLabel("Message_EditSuccessfully"), "Success", 1, null);
			Events.postEvent("onClick$searchButton", comp.getPage().getFellow("performanceReviewMain").getFellow("searchPanel"), null);

			comp.detach();
		} catch (Exception e) {
			try {
				Messagebox.show(Labels.getLabel("Message_EditUnSuccessfully"), "Error", 1, null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}
