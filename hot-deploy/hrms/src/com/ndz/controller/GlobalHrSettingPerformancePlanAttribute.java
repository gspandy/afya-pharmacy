package com.ndz.controller;

import java.util.List;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class GlobalHrSettingPerformancePlanAttribute extends GenericForwardComposer {

	private String selectedSection;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if ("addNewPerformancePlanAttributes".equals(comp.getId()))
			// return;
			populate(comp);
	}
	
	private void populate(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		List<GenericValue> sectionNameType = delegator.findList(
				"PerfReviewSection", null, null, null, null, false);
		sectionNameType.add(0, null);
		SimpleListModel sectionName = new SimpleListModel(sectionNameType);
		Listbox listBoxSectionName = (Listbox) root.getFellowIfAny("listBoxSectionName");
		if(listBoxSectionName != null){
		listBoxSectionName.setModel(sectionName);
		listBoxSectionName.setItemRenderer(getListitemrender());
		}
	}

	public ListitemRenderer getListitemrender() {
		return new ListitemRenderer() {
			public void render(Listitem listitem, Object object) throws Exception {
				if (object == null)
					return;
				GenericValue gv = (GenericValue) object;
				listitem.setLabel(gv.getString("sectionName"));
				listitem.setValue(gv.getString("perfReviewSectionId"));
				listitem.setSelected(selectedSection != null
						&& selectedSection.equalsIgnoreCase(gv.getString("sectionName")));
			}
		};
	}

	public void onClick$btnSave(Event event) {

		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession()
					.getAttribute("userLogin");

			Component addNewPerformancePlanAttributes = event.getTarget();
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//(GenericDelegator) userLogin.getDelegator();

			Listitem sectionNameId = ((Listbox) addNewPerformancePlanAttributes.getFellow("listBoxSectionName"))
					.getSelectedItem();
			String sectionName = (String) sectionNameId.getValue();

			String attributeName = ((Textbox) addNewPerformancePlanAttributes.getFellow("txtAttributeName")).getValue();
			System.out.println("********attributeName********" + attributeName);
			String Description = ((Textbox) addNewPerformancePlanAttributes.getFellow("txtBoxDescription")).getValue();
			System.out.println("********Description********" + Description);

			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "perfReviewSectionId", sectionName,
					"attributeName", attributeName, "description", Description);

			LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("createPerfReviewSectionAttribute", context);
			Messagebox.show("Saved Successfully", "Success", 1, null);
			Component searchPanelComponent = null;
			Component performanceReviewMainComponent = addNewPerformancePlanAttributes.getPage().getFellowIfAny(
					"performanceReviewMain");
			if (performanceReviewMainComponent != null)
				searchPanelComponent = performanceReviewMainComponent.getFellowIfAny("searchPanel");
			if (searchPanelComponent != null)
				Events.postEvent("onClick$searchButton", searchPanelComponent, null);
			addNewPerformancePlanAttributes.detach();
		} catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDescription_Fail"), "Error", 1, null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private static final long serialVersionUID = 1L;

	public void delete(final Event event, final String attributeId, final String perfReviewSectionId)
			throws GenericServiceException, InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {
							try {
								Component window = event.getTarget();
								GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop()
										.getSession().getAttribute("userLogin");
								GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

								Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "attributeId",
										attributeId, "perfReviewSectionId", perfReviewSectionId);

								LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
								Map<String, Object> abc = dispatcher.runSync("deletePerfReviewSectionAttribute",
										context);
								Messagebox.show(Labels.getLabel("MessageDelete_Successful"), "Success", 1, null);
								Events.postEvent("onClick$searchButton",
										window.getPage().getFellow("performanceReviewMain").getFellow("searchPanel"),
										null);
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

	public void planAttribute(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException,
			InterruptedException, GenericEntityException {

		String sectionName = "";
		GenericValue sectionGv = gv.getRelatedOne("PerfReviewSection");
		if (sectionGv != null) {
			sectionName = sectionGv.getString("sectionName");
		}
		final Window win = (Window) Executions.createComponents(
				"/zul/GlobalHRSetting/performancePlanAttributesEdit.zul", null,
				UtilMisc.toMap("gv", gv, "sectionName", sectionName));
		win.doModal();

		Textbox txtDescription = (Textbox) win.getFellow("txtBoxDescription");
		txtDescription.setValue(gv.getString("description"));

		Textbox txtBoxAttributeName = (Textbox) win.getFellow("txtBoxAttributeName");
		txtBoxAttributeName.setValue(gv.getString("attributeName"));

		Textbox txtattributeId = (Textbox) win.getFellow("txtattributeId");
		txtattributeId.setValue(gv.getString("attributeId"));

		Label txtperfReviewSectionId = (Label) win.getFellow("txtperfReviewSectionId");
		txtperfReviewSectionId.setValue(gv.getDelegator().getRelatedOne("PerfReviewSection", gv).getString("sectionName"));
	}

	public void editPerfPlanAtt(Event event,GenericValue gv) throws GenericEntityException, GenericServiceException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession()
					.getAttribute("userLogin");

			Component comp = event.getTarget().getFellow("addNewPerformancePlanAttributes");

			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String reviewSectionId = gv.getString("perfReviewSectionId");
			String attributeId = ((Textbox) comp.getFellow("txtattributeId")).getValue();

			String description = ((Textbox) comp.getFellow("txtBoxDescription")).getValue();

			String attributeName = ((Textbox) comp.getFellow("txtBoxAttributeName")).getValue();

			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "attributeId", attributeId,
					"description", description, "perfReviewSectionId", reviewSectionId, "attributeName", attributeName);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			dispatcher.runSync("updatePerfReviewSectionAttribute", context);
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

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
}
