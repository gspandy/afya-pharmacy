package com.ndz.controller;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.GenericValueRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class SkillManagementController extends GenericForwardComposer {
	
	private static final long serialVersionUID = 1L;

	public void doAfterCompose(Component createEmployeeSkills) throws Exception {
		super.doAfterCompose(createEmployeeSkills);
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");


		 List<GenericValue> skillTypes = delegator.findList("SkillType", null, null, null, null, false);
		 
		 SimpleListModel skillTypesList = new SimpleListModel(skillTypes);

		Listbox listboxSkillTypeId = (Listbox) createEmployeeSkills.getFellow("listboxSkillTypeId");
		
		listboxSkillTypeId.setModel(skillTypesList);
		listboxSkillTypeId.setItemRenderer(new GenericValueRenderer(new String[]{"skillTypeId", "description"}));
		
	}

	public void onEvent(Event event) {
		String claimId = null;
		Messagebox messageBox = new Messagebox();
		try {
			System.out.println("***********OnEventCalled************");
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			System.out.println("*******userLogin Object*********" + userLogin);
			Component createEmployeeSkills = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String employeeId = userLogin.getString("partyId");
			System.out.println("********Party Id********" + employeeId);

			Listitem skillTypeId = ((Listbox) createEmployeeSkills
				.getFellow("listboxSkillTypeId")).getSelectedItem();
			String skillType = (String) skillTypeId.getValue();
			System.out.println("********skillTypeId********" + skillType);
			String yearsofexperiance = ((Textbox) createEmployeeSkills
					.getFellow("textBoxYearsofExperience")).getValue();
			
			System.out.println("********yearsofexperiance********" + yearsofexperiance);

			Listitem listRating = ((Listbox) createEmployeeSkills
					.getFellow("listBoxRating")).getSelectedItem();
			String Rating = (String) listRating.getValue();
			System.out.println("********Rating********" + Rating);
			String Level = (String) ((Listbox) createEmployeeSkills
					.getFellow("listBoxSkillLevel")).getSelectedItem()
					.getValue();
			System.out.println("********Rating********" + Level);

			Map context = UtilMisc.toMap("userLogin",userLogin,"partyId", employeeId, "skillTypeId",
					skillType, "yearsExperience", yearsofexperiance, "rating",
					Rating, "skillLevel", Level);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map<String, Object> result = dispatcher.runSync("createPartySkill", context);
			Events.postEvent("onClick$searchButton",createEmployeeSkills.getPage().getFellow("searchPanel") , null);
			messageBox.show("My Skills Saved Successfully","Success",1,null );
			createEmployeeSkills.detach();
		} catch (Exception e) {
			try {
				messageBox
						.show("My Skills Could not be saved:Some parameter is missing","Success",1,null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
