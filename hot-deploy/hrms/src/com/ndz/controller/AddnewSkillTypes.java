package com.ndz.controller;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.util.resource.Labels;

import com.ndz.zkoss.UtilService;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class AddnewSkillTypes extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if (!"searchPanel".equals(comp.getId()))
			return;

	}

	public void onClick$btnSave(Event event) throws GenericEntityException {

		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");

			Component addNewSkillTypes = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String SkillId = ((Textbox) addNewSkillTypes.getFellow("txtBoxSkillTypeId")).getValue();
			String description = ((Textbox) addNewSkillTypes.getFellow("txtBoxDescription")).getValue();
			Map<String, Object> getValue = FastMap.newInstance();
			GenericValue putValue = null;
			getValue.putAll(UtilMisc.toMap("skillTypeId", SkillId, "description", description));
			putValue = (GenericValue) delegator.create("SkillType", getValue);
			Messagebox.show("Saved successfully", "Success", 1, null);
			addNewSkillTypes.detach();
		} catch (Exception e) {
			try {
				Messagebox.show("Skill Type Id already exists", "Error", 1, null);

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

	}

	public void delete(final Event event, final String skillTypeId) throws GenericServiceException, InterruptedException {
		Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {
							try {
								Component addNewSkillTypes = event.getTarget();
								GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
										"userLogin");
								GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

								Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "skillTypeId", skillTypeId);

								LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
								Map result = dispatcher.runSync("deleteSkillType", context);

								if (result.get("errorMessage") == null) {
									Events.postEvent("onClick$searchButton", addNewSkillTypes.getPage().getFellow("searchPanel"), null);
									System.out.println(result + "\n\n\n\n");
									Messagebox.show("Deleted successfully", "Success", 1, null);
								} else {
									Events.postEvent("onClick$searchButton", addNewSkillTypes.getPage().getFellow("searchPanel"), null);
									Messagebox.show("The selected skill type is in use; can't be deleted", "Error", 1, null);
								}

							} catch (Exception e) {

							}
						}
					}
				});

	}

	public void SkillTypesEdit(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException,
			InterruptedException {
		final Window win = (Window) Executions.createComponents("/zul/GlobalHRSetting/SkillTypesEdit.zul", null, null);
		win.doModal();
		Textbox txtDescription = (Textbox) win.getFellow("txtBoxDescription");
		txtDescription.setValue(gv.getString("description"));
		Textbox txtSkillTypeId = (Textbox) win.getFellow("txtBoxSkillTypeId");
		txtSkillTypeId.setValue(gv.getString("skillTypeId"));

	}

	public void onClick$btnEdit(Event event) throws GenericEntityException, GenericServiceException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");

			Component editSkillTypes = event.getTarget();

			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String skillTypeId = ((Textbox) editSkillTypes.getFellow("txtBoxSkillTypeId")).getValue();
			String description = ((Textbox) editSkillTypes.getFellow("txtBoxDescription")).getValue();

			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "skillTypeId", skillTypeId, "description", description);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("updateSkillType", context);
			Events.postEvent("onClick$searchButton", editSkillTypes.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Updated successfully", "Success", 1, null);
			editSkillTypes.detach();
		} catch (Exception e) {
		}
	}

}
