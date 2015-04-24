package com.ndz.controller;

import com.ndz.zkoss.UtilService;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.smebiz.common.UtilDateTimeSME;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class UpdateEmployeeProfileController extends GenericForwardComposer {

	public static void updatePersonalInformation(Event event) {

		try {
			Component updateProfileWindow = event.getTarget();
			updateProfileWindow.getDesktop().getExecution().getNativeRequest();

			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			BigDecimal height = null;
			BigDecimal weight = null;
			BigDecimal yrsOfExprience = null;
			String employeeId = (String) ((Label) updateProfileWindow.getFellow("employeeId")).getValue();
            GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId",employeeId),false);
			
			//String employeeAccWithAgent = (String) ((Textbox) updateProfileWindow.getFellow("employeeAccWithAgent")).getValue();
			String firstName = (String) ((Textbox) updateProfileWindow.getFellow("firstName")).getValue();
			String middleName = (String) ((Textbox) updateProfileWindow.getFellow("middleName")).getValue();
			String lastName = (String) ((Textbox) updateProfileWindow.getFellow("lastName")).getValue();
			String title = (String) ((Textbox) updateProfileWindow.getFellow("title")).getValue();
			String suffix = (String) ((Textbox) updateProfileWindow.getFellow("suffix")).getValue();
			String nickName = (String) ((Textbox) updateProfileWindow.getFellow("nickName")).getValue();
			String memberId = (String) ((Textbox) updateProfileWindow.getFellow("memberId")).getValue();
			Date birthDateType = (Date) ((Datebox) updateProfileWindow.getFellow("birthDate")).getValue();
			Timestamp birthDate = new Timestamp(birthDateType.getTime());
			String countryCode = (String) ((Textbox) updateProfileWindow.getFellow("countryCode")).getValue();
			//String gender = (String) ((Textbox) updateProfileWindow.getFellow("gender")).getValue();
			String gender = (String) ((Listbox)updateProfileWindow.getFellow("genderlistBox")).getSelectedItem().getValue();
			String heightType = (String) ((Textbox) updateProfileWindow.getFellow("height")).getValue();
			if (!(heightType.equals(""))) {
				height = new BigDecimal(heightType);
			} else {
				height = null;
			}
			String weightType = (String) ((Textbox) updateProfileWindow.getFellow("weight")).getValue();
			if (!(weightType.equals(""))) {
				weight = new BigDecimal(weightType);
			} else {
				weight = null;
			}
			String emergencyContact = (String) ((Textbox) updateProfileWindow.getFellow("emergencyContact")).getValue();
			String passportNumber = (String) ((Textbox) updateProfileWindow.getFellow("passportNumber")).getValue();
			Date passportExpireD = (Date) ((Datebox) updateProfileWindow.getFellow("passportExpireDate")).getValue();
			Timestamp passportExpireDate = null;
			if (passportExpireD != null)
				passportExpireDate = new Timestamp(passportExpireD.getTime());
			String maritalStatus = (String) ((Textbox) updateProfileWindow.getFellow("maritalStatus")).getValue();
			String bloodGroup = (String) ((Textbox) updateProfileWindow.getFellow("bloodGroup")).getValue();
			String nationality = (String) ((Textbox) updateProfileWindow.getFellow("nationality")).getValue();
			String yrsOfExprienceType = (String) ((Textbox) updateProfileWindow.getFellow("yrsOfExprience")).getValue();
			if (!(yrsOfExprienceType.equals(""))) {
				yrsOfExprience = new BigDecimal(yrsOfExprienceType);
			} else {
				yrsOfExprience = null;
			}

			String comments = (String) ((Textbox) updateProfileWindow.getFellow("comments")).getValue();
			String occupation = (String) ((Textbox) updateProfileWindow.getFellow("occupation")).getValue();
			String nomineeName = (String) ((Textbox) updateProfileWindow.getFellow("nomineeName")).getValue();
			String fatherName = (String) ((Textbox) updateProfileWindow.getFellow("fatherName")).getValue();
			String spouceName = (String) ((Textbox) updateProfileWindow.getFellow("spouceName")).getValue();
			String identificationMark = (String) ((Textbox) updateProfileWindow.getFellow("identificationMark")).getValue();
			String residenceStatusEnumId = (String) ((Textbox) updateProfileWindow.getFellow("residenceStatusEnumId")).getValue();
			String employmentStatusEnumId = (String) ((Textbox) updateProfileWindow.getFellow("employmentStatusEnumId")).getValue();
			String vehicleType = (String) ((Textbox) updateProfileWindow.getFellow("vehicleType")).getValue();
			String vehicleNumber = (String) ((Textbox) updateProfileWindow.getFellow("vehicleNumber")).getValue();
			String socialSecurityNumber=((Textbox) updateProfileWindow.getFellow("addEmployee_socialSecurityNumber")).getValue();
			String nrcNo=((Textbox) updateProfileWindow.getFellow("addEmployee_NRCNo")).getValue();
			Comboitem gradeComboItem=((Combobox) updateProfileWindow.getFellow("addEmployee_grades")).getSelectedItem();
	        String grade=gradeComboItem!=null? (String)gradeComboItem.getValue():(String)personGv.get("grades");
	        Radio empType = ((Radiogroup) updateProfileWindow.getFellow("emp_administration")).getSelectedItem();
	        String employeeType = empType!=null? empType.getValue():personGv.getString("employeeType");
	        Comboitem positionCatg=((Combobox) updateProfileWindow.getFellow("positionCategories")).getSelectedItem();
	        String positionCategory= positionCatg!=null? (String)positionCatg.getValue():personGv.getString("positionCategory");
	        String qualification=((Textbox) updateProfileWindow.getFellow("empQualification")).getValue();
            String napsaNo=((Textbox) updateProfileWindow.getFellow("empNapsaNo")).getValue();
            //String gang=((Textbox) updateProfileWindow.getFellow("empGang")).getValue();
			
			Map updatePerson = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "salutation", null, "firstName",
					firstName, "middleName", middleName, "lastName", lastName, "personalTitle", title, "suffix", suffix, "nickname",
					nickName, "memberId", memberId, "gender", gender, "birthDate", birthDate!=null? new java.sql.Date(birthDate.getTime()):null, "countryCode", countryCode, "height", null,
					"weight", null, "emergencyContactNo", emergencyContact, "passportNumber", passportNumber, "passportExpireDate",
                    passportExpireDate!=null? new java.sql.Date(passportExpireDate.getTime()):null, "totalYearsWorkExperience", null, "comments", comments, "employmentStatusEnumId",
					employmentStatusEnumId, "residenceStatusEnumId", residenceStatusEnumId, "occupation", occupation, "nationality",
					nationality, "bloodGroup", bloodGroup, "nomineeName", nomineeName, "fatherName", fatherName, "spouseName", spouceName,
					"identificationMark", identificationMark, "maritalStatus", maritalStatus, "vehicleNumber", vehicleNumber, "vehicleType", 
					vehicleType,"nrcNo",nrcNo,"socialSecurityNumber",socialSecurityNumber,"grades",grade,"employeeType",employeeType,
					"positionCategory",positionCategory,"qualification",qualification,"napsaNo",napsaNo);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("updatePerson", updatePerson);
			final String partyId = (String) ((Label) updateProfileWindow.getFellow("employeeId")).getValue();
			Messagebox.show("Updated Successfully", "Success", Messagebox.OK, Messagebox.NONE, new EventListener() {
				public void onEvent(Event evt) {
					if ("onOK".equals(evt.getName())) {

						try {
							Executions.getCurrent().sendRedirect("/control/profile?partyId=" + partyId);

						} catch (Exception e) {

						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void updateEmailAddress(Event event) {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Component comp=event.getTarget();
			String contactMechId = ((Textbox)comp.getFellow("contactMechId")).getRawText();
			String partyId=((Textbox)comp.getFellow("partyId")).getRawText();
			String contactMechPurposeTypeIdString = (String) ((Listbox) comp.getFellow("contactMechPurposeTypeId")).getSelectedItem()
					.getValue();
			String emailAddress=((Textbox)comp.getFellow("emailAddress")).getRawText();
            GenericValue contactGV = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId",contactMechId));
           // contactGV .put("contactMechId",contactMechId);
           // contactGV .put("contactMechTypeId",contactMechPurposeTypeIdString);
            contactGV .put("infoString",emailAddress);
            contactGV.store();
			Map purposeMap = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechId", contactMechId,
					"contactMechPurposeTypeId", contactMechPurposeTypeIdString);
			Map map = UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId);
			List contactMechPurposeList = delegator.findByAnd("PartyContactMechPurpose", map);
			for (int i = 0; i < contactMechPurposeList.size(); i++) {
				delegator.removeByAnd("PartyContactMechPurpose", map);
			}
			dispatcher.runSync("createPartyContactMechPurpose", purposeMap);
			Messagebox.show("Email Address Updated", "Success", 1, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void addPostalAddress(Event event) throws GenericEntityException, InterruptedException {
		try {
			Map result = null;
			result = UtilService.runService("createPartyPostalAddress", event);
			Messagebox.show("Postal Address Added", "Success", 1, null);

		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updatePostalAddress(Event event) throws InterruptedException, GenericEntityException {

		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map result = null;
			result = UtilService.runService("updatePartyPostalAddress", event);
			Component comp = event.getTarget();
			String contactMechNew = (String) result.get("contactMechId");
			String partyId = (String) ((Textbox) comp.getFellow("partyId")).getValue();
			String contactMechPurposeTypeIdString = (String) ((Listbox) comp.getFellow("contactMechPurposeTypeId")).getSelectedItem()
					.getValue();
			Map purposeMap = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechId", contactMechNew,
					"contactMechPurposeTypeId", contactMechPurposeTypeIdString);
			Map map = UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechNew);
			List contactMechPurposeList = delegator.findByAnd("PartyContactMechPurpose", map);
			for (int i = 0; i < contactMechPurposeList.size(); i++) {
				delegator.removeByAnd("PartyContactMechPurpose", map);
			}
			dispatcher.runSync("createPartyContactMechPurpose", purposeMap);
			Messagebox.show("Updated Successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addContactMechPurpose(Event event) {

		try {
			UtilService.runService("createPartyContactMechPurpose", event);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addPhoneNumber(Event event) throws InterruptedException {

		try {
			UtilService.runService("createPartyTelecomNumber", event);
			Messagebox.show("Contact Number Added", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addEmployeeSkill(Event event) throws InterruptedException {
		Component addSkillWindow = event.getTarget();
		addSkillWindow.getDesktop().getExecution().getNativeRequest();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		String partyId = (String) ((Textbox) addSkillWindow.getFellow("partyId")).getValue();
		Comboitem skillTypeIdInput = ((Combobox) addSkillWindow.getFellow("skillTypeId")).getSelectedItem();
		String skillTypeId = (String) skillTypeIdInput.getValue();
		Integer yearsExperience = (Integer) ((Spinner) addSkillWindow.getFellow("yearsExperience")).getValue();
		Integer rating = (Integer) ((Spinner) addSkillWindow.getFellow("rating")).getValue();
		if (rating == null)
			rating = 0;
		if (yearsExperience == null)
			yearsExperience = 0;
		Comboitem skillLevelInput = ((Combobox) addSkillWindow.getFellow("skillLevel")).getSelectedItem();
		String skillLevelType = (String) skillLevelInput.getValue();
		Long skillLevel = new Long(skillLevelType);
		Map addSkill = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "skillTypeId", skillTypeId, "yearsExperience",
				yearsExperience, "rating", rating, "skillLevel", skillLevel);

		try {
			dispatcher.runSync("createPartySkill", addSkill);
			Messagebox.show("Skill Added", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void showUpdateTelecomNumberWindow(String contactMechId, String partyId) throws GenericEntityException,
			SuspendNotAllowedException, InterruptedException {

		GenericValue contactNumber = null;
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		contactNumber = delegator.findByPrimaryKey("TelecomNumber", org.ofbiz.base.util.UtilMisc.toMap("contactMechId", contactMechId));
		Map arg = new HashMap();
		arg.put("partyId", partyId);
		arg.put("contactMechId", contactMechId);
		Window win = (Window) Executions.createComponents("/zul/employeeProfile/editPhoneNumber.zul", null, arg);

		Textbox contactMechIdBox = (Textbox) win.getFellow("contactMechId");
		contactMechIdBox.setValue(contactMechId);

		Textbox countryCodeBox = (Textbox) win.getFellow("countryCode");
		countryCodeBox.setValue(contactNumber.getString("countryCode"));

		Textbox areaCodeBox = (Textbox) win.getFellow("areaCode");
		areaCodeBox.setValue(contactNumber.getString("areaCode"));

		Textbox contactNumberBox = (Textbox) win.getFellow("contactNumber");
		contactNumberBox.setValue(contactNumber.getString("contactNumber"));

		win.doModal();

	}

	public static void showUpdateSkillWindow(Event even, GenericValue gv) throws GenericEntityException, SuspendNotAllowedException,
			InterruptedException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String skillTypeId = gv.getString("skillTypeId");
		String partyId = gv.getString("partyId");
		String yearsExperience = gv.getString("yearsExperience");
		String rating = gv.getString("rating");
		String skillLevelType = gv.getString("skillLevel");

		Set fieldToSelect = new HashSet();

		fieldToSelect.add("skillTypeId");
		fieldToSelect.add("description");

		List<GenericValue> skills = delegator.findList("SkillType", null, fieldToSelect, null, null, false);

		Window win = (Window) Executions.createComponents("/zul/employeeProfile/editEmployeeSkill.zul", null, null);

		Textbox savedPartyId = (Textbox) win.getFellow("partyId");
		savedPartyId.setValue(partyId);

		Spinner SavedYearsExperience = (Spinner) win.getFellow("yearsExperience");
		SavedYearsExperience.setRawValue(yearsExperience);

		Spinner SavedRating = (Spinner) win.getFellow("rating");
		SavedRating.setRawValue(rating);

		Listbox savedSkillType = (Listbox) win.getFellow("skillTypeId");
		Listitem skillTypeItem = new Listitem();
		skillTypeItem.setLabel(skillTypeId);
		skillTypeItem.setValue(skillTypeId);

		for (int i = 0; i < skills.size(); i++) {
			GenericValue skill = skills.get(i);
			String itemLabel = skill.getString("skillTypeId");
			savedSkillType.appendItemApi(skill.getString("description"), itemLabel);
			if (itemLabel.equals(skillTypeId)) {
				savedSkillType.setSelectedIndex(i);
			}
		}

		win.doModal();

	}

	public static void showUpdateTrainingWindow(Event event, GenericValue gv) throws GenericEntityException, SuspendNotAllowedException,
			InterruptedException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String trainingTypeId = gv.getString("trainingClassTypeId");
		Timestamp fromDateType = gv.getTimestamp("fromDate");
		Timestamp thruDateType = gv.getTimestamp("thruDate");
		String partyId = gv.getString("partyId");

		Set fieldToSelect = new HashSet();

		fieldToSelect.add("trainingClassTypeId");

		List<GenericValue> trainings = delegator.findList("TrainingClassType", null, fieldToSelect, null, null, false);
		Map party = new HashMap();
		Window win = (Window) Executions.createComponents("/zul/employeeProfile/editEmployeeTraining.zul", null, null);

		Listbox savedSkillType = (Listbox) win.getFellow("trainingClassTypeId");
		Listitem skillTypeItem = new Listitem();
		skillTypeItem.setLabel(trainingTypeId);
		skillTypeItem.setValue(trainingTypeId);

		Datebox fromDate = (Datebox) win.getFellow("fromDate");
		fromDate.setValue(fromDateType);
		Datebox thruDate = (Datebox) win.getFellow("thruDate");
		thruDate.setValue(thruDateType);
		Textbox txtBox = (Textbox) win.getFellow("partyId");
		txtBox.setValue(partyId);
		for (int i = 0; i < trainings.size(); i++) {
			GenericValue training = trainings.get(i);
			String itemLabel = training.getString("trainingClassTypeId");
			savedSkillType.appendItemApi(training.getString("trainingClassTypeId"), itemLabel);
			if (itemLabel.equals(trainingTypeId)) {
				savedSkillType.setSelectedIndex(i);
			}
		}

		win.doModal();

	}

	public static void updateEmployeeTraining(Event event) throws InterruptedException {

		try {

			Component editTrainingWindow = event.getTarget();
			editTrainingWindow.getDesktop().getExecution().getNativeRequest();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			String partyId = (String) ((Textbox) editTrainingWindow.getFellow("partyId")).getValue();
			Listitem trainingTypeIdInput = ((Listbox) editTrainingWindow.getFellow("trainingClassTypeId")).getSelectedItem();
			String trainingTypeId = (String) trainingTypeIdInput.getValue();
			Date fromDateType = (Date) ((Datebox) editTrainingWindow.getFellow("fromDate")).getValue();
			Timestamp fromDate = new Timestamp(fromDateType.getTime());
			Date thruDateType = (Date) ((Datebox) editTrainingWindow.getFellow("thruDate")).getValue();
			Timestamp thruDate = new Timestamp(thruDateType.getTime());
			Map addTraining = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "trainingClassTypeId", trainingTypeId, "fromDate",
					fromDate, "thruDate", thruDate);
			dispatcher.runSync("updatePersonTraining", addTraining);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void updateEmployeeSkill(Event event, String skillTypeId) throws InterruptedException, GenericEntityException,
			GenericServiceException {

		Component editSkillWindow = event.getTarget();
		editSkillWindow.getDesktop().getExecution().getNativeRequest();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		String partyId = (String) ((Textbox) editSkillWindow.getFellow("partyId")).getValue();
		Listitem skillLevelItem = (Listitem) ((Listbox) editSkillWindow.getFellow("skillLevel")).getSelectedItem();
		String skillLevelType = (String) skillLevelItem.getValue();
		Long skillLevel = new Long(skillLevelType);
		Integer yearsExperienceType = ((Spinner) editSkillWindow.getFellow("yearsExperience")).getValue();
		Long yearsExperience = new Long(yearsExperienceType);
		Integer ratingType = ((Spinner) editSkillWindow.getFellow("rating")).getValue();
		Long rating = new Long(ratingType);
		Map skillMap = UtilMisc.toMap("partyId", partyId, "skillTypeId", skillTypeId, "skillLevel", skillLevel, "yearsExperience",
				yearsExperience, "rating", rating);
		GenericValue skillGV = null;
		List<GenericValue> skillListGV = delegator.findByAnd("PartySkill", UtilMisc.toMap("partyId", partyId, "skillTypeId", skillTypeId));
		skillGV = EntityUtil.getFirst(skillListGV);
		skillGV.putAll(skillMap);
		skillGV.store();
		Messagebox.show("Skill Updated", "Success", 1, null);

	}

	public static void deleteSkill(Event event, GenericValue gv) throws GenericServiceException, InterruptedException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		String employeeId = gv.getString("partyId");
		String skillTypeId = gv.getString("skillTypeId");

		Map deleteSkill = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "skillTypeId", skillTypeId);

		dispatcher.runSync("deletePartySkill", deleteSkill);
		Events.postEvent("onClick", Path.getComponent("/profileWindow//skillMenuId"), null);
		Messagebox.show("Skill Deleted", "Success", 1, null);
	}

	public static void deleteTraining(Event event, GenericValue gv) throws GenericServiceException, InterruptedException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		String employeeId = gv.getString("partyId");
		String trainingClassTypeId = gv.getString("trainingClassTypeId");
		Timestamp fromDateType = gv.getTimestamp("fromDate");
		Timestamp thruDateType = gv.getTimestamp("thruDate");

		Map deleteSkill = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "trainingClassTypeId", trainingClassTypeId,
				"fromDate", fromDateType, "thruDate", thruDateType);

		dispatcher.runSync("deletePersonTraining", deleteSkill);
		Messagebox.show("Training Deleted", "Success", 1, null);

	}

	public static void addRoleType(Event event) throws InterruptedException {

		try {
			Map result=UtilService.runService("createRoleType", event);
			if (result.get("errorMessage")==null){
			Messagebox.show("Role Type Added", "Success", 1, null);
			}
			else{
				Messagebox.show("Role Type Id already exists", "Error", 1, null);
				return;
			}
			Component addRoleTypeWindow = event.getTarget().getFellow("addRoleTypeWindow");
			Component comp = addRoleTypeWindow.getPage().getFellowIfAny("searchPanel");
			if(comp != null){
				Events.postEvent("onClick", addRoleTypeWindow.getPage().getFellow("searchPanel").getFellow("searchButton"),null);
			}
			addRoleTypeWindow.detach();
		} catch (GenericServiceException e) {
		}
	}

	public static void editRoleType(Event event) throws GenericEntityException, GenericServiceException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component comp = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String roleTypeId = ((Textbox) comp.getFellow("txtBoxRoleTypeId")).getValue();
			String description = ((Textbox) comp.getFellow("txtBoxDescription")).getValue();
			GenericValue gvStore = (GenericValue) delegator.makeValue("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId, "description",
					description));
			delegator.store(gvStore);
			Messagebox.show("Updated successfully", "Success", 1, null);
		} catch (Exception e) {
		}
	}

	public static void deleteRoleType(final Event event, final String roleTypeId) throws GenericServiceException, InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {
							try {
								Component comp = event.getTarget();
								GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
								delegator.removeByAnd("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId));
								Events.postEvent("onClick", comp.getFellow("searchPanel").getFellow("searchButton"), null);
								Messagebox.show("Deleted successfully","Success",1,null);
							} catch (Exception e) {

							}
						}
					}
				});

	}

	public static void addPartyRole(Event event) throws InterruptedException, GenericEntityException {

		try {
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			Component comp = event.getTarget();
			String roleTypeId = (String) ((Listbox) comp.getFellow("roleTypeId")).getSelectedItem().getValue();
			String partyId = (String) ((Textbox) comp.getFellow("partyId")).getValue();
			List<GenericValue> partyRoleList = delegator.findByAnd("PartyRole", UtilMisc
					.toMap("partyId", partyId, "roleTypeId", roleTypeId));
			if (UtilValidate.isEmpty(partyRoleList)) {
				UtilService.runService("createPartyRole", event);
				Messagebox.show("Role Assigned To Employee", "Success", 1, null);
			} else {
				Messagebox.show("Role Type Already Exist", "Error", 1, null);
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void updatePhoneNumber(Event event) throws InterruptedException, GenericEntityException {

		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map result = null;
			result = UtilService.runService("updatePartyTelecomNumber", event);
			Component comp = event.getTarget();
			String contactMechNew = (String) result.get("contactMechId");
			String partyId = (String) ((Textbox) comp.getFellow("partyId")).getValue();
			String contactMechPurposeTypeIdString = (String) ((Listbox) comp.getFellow("contactMechPurposeTypeId")).getSelectedItem()
					.getValue();
			Map purposeMap = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechId", contactMechNew,
					"contactMechPurposeTypeId", contactMechPurposeTypeIdString);
			Map map = UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechNew);
			List contactMechPurposeList = delegator.findByAnd("PartyContactMechPurpose", map);
			for (int i = 0; i < contactMechPurposeList.size(); i++) {
				delegator.removeByAnd("PartyContactMechPurpose", map);
			}
			dispatcher.runSync("createPartyContactMechPurpose", purposeMap);
			Messagebox.show("Contact Number Updated", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addEmployeeTraining(Event event) throws InterruptedException, GenericEntityException {

		try {

			Component addTrainingWindow = event.getTarget();
			addTrainingWindow.getDesktop().getExecution().getNativeRequest();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			String partyId = (String) ((Textbox) addTrainingWindow.getFellow("partyId")).getValue();
			Listitem trainingTypeIdInput = ((Listbox) addTrainingWindow.getFellow("trainingClassTypeId")).getSelectedItem();
			String trainingTypeId = (String) trainingTypeIdInput.getValue();
			Date fromDateType = (Date) ((Datebox) addTrainingWindow.getFellow("fromDate")).getValue();
			Timestamp fromDate = new Timestamp(fromDateType.getTime());
			Date thruDateType = (Date) ((Datebox) addTrainingWindow.getFellow("thruDate")).getValue();
			Timestamp thruDate = new Timestamp(thruDateType.getTime());
			Map addTraining = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "trainingClassTypeId", trainingTypeId, "fromDate",
					fromDate, "thruDate", thruDate);

			List<GenericValue> personTrainingList = delegator.findByAnd("PersonTraining", UtilMisc.toMap("trainingClassTypeId",
					trainingTypeId, "fromDate", fromDate, "thruDate", thruDate));
			if (UtilValidate.isEmpty(personTrainingList)) {
				dispatcher.runSync("createPersonTraining", addTraining);
				Messagebox.show("Training Added For Employee", "Success", 1, null);
			} else {
				Messagebox.show("Employee Training Already Exists", "Success", 1, null);
			}

		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addCompanyTraining(Event event) throws InterruptedException {

		try {

			Component addCompanyTrainingWindow = event.getTarget();
			addCompanyTrainingWindow.getDesktop().getExecution().getNativeRequest();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			String workEffortTypeId = (String) ((Textbox) addCompanyTrainingWindow.getFellow("workEffortTypeId")).getValue();
			String trainingName = (String) ((Textbox) addCompanyTrainingWindow.getFellow("trainingName")).getValue();
			String description = (String) ((Textbox) addCompanyTrainingWindow.getFellow("description")).getValue();
			Listitem trainingTypeIdInput = ((Listbox) addCompanyTrainingWindow.getFellow("trainingClassTypeId")).getSelectedItem();
			String trainingTypeId = (String) trainingTypeIdInput.getValue();
			Listitem currentStatusIdInput = ((Listbox) addCompanyTrainingWindow.getFellow("currentStatusId")).getSelectedItem();
			String currentStatusId = (String) currentStatusIdInput.getValue();
			Date fromDateType = (Date) ((Datebox) addCompanyTrainingWindow.getFellow("estimatedStartDate")).getValue();
			Timestamp fromDate = new Timestamp(fromDateType.getTime());
			Date thruDateType = (Date) ((Datebox) addCompanyTrainingWindow.getFellow("estimatedCompletionDate")).getValue();
			Timestamp thruDate = new Timestamp(thruDateType.getTime());
			Map addTraining = UtilMisc.toMap("userLogin", userLogin, "workEffortTypeId", workEffortTypeId, "description", description,
					"trainingClassTypeId", trainingTypeId, "currentStatusId", currentStatusId, "trainingName", trainingName,
					"estimatedStartDate", fromDate, "estimatedCompletionDate", thruDate);
			dispatcher.runSync("addCompanyTraining", addTraining);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void updateCompanyTraining(Event event) throws InterruptedException {

		try {

			Component addCompanyTrainingWindow = event.getTarget();
			addCompanyTrainingWindow.getDesktop().getExecution().getNativeRequest();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			String workEffortTypeId = (String) ((Textbox) addCompanyTrainingWindow.getFellow("workEffortTypeId")).getValue();
			String trainingId = (String) ((Textbox) addCompanyTrainingWindow.getFellow("trainingId")).getValue();
			String trainingName = (String) ((Textbox) addCompanyTrainingWindow.getFellow("trainingName")).getValue();
			String description = (String) ((Textbox) addCompanyTrainingWindow.getFellow("description")).getValue();
			String trainingTypeId = ((Textbox) addCompanyTrainingWindow.getFellow("trainingClassTypeId")).getValue();
			String currentStatusId = ((Textbox) addCompanyTrainingWindow.getFellow("currentStatusId")).getValue();
			Date fromDateType = (Date) ((Datebox) addCompanyTrainingWindow.getFellow("estimatedStartDate")).getValue();
			Timestamp fromDate = new Timestamp(fromDateType.getTime());
			Date thruDateType = (Date) ((Datebox) addCompanyTrainingWindow.getFellow("estimatedCompletionDate")).getValue();
			Timestamp thruDate = new Timestamp(thruDateType.getTime());
			Map addTraining = UtilMisc.toMap("userLogin", userLogin, "workEffortTypeId", workEffortTypeId, "trainingId", trainingId,
					"description", description, "trainingClassTypeId", trainingTypeId, "currentStatusId", currentStatusId, "trainingName",
					trainingName, "estimatedStartDate", fromDate, "estimatedCompletionDate", thruDate);
			dispatcher.runSync("updateCompanyTraining", addTraining);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void deleteRole(Event event, final GenericValue gv, final String partyId, final Div div) throws GenericEntityException,
			InterruptedException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		final GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Messagebox.show("Do You Want To Delete this Record?", "Warning?", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {

							String roleTypeId = gv.getString("roleTypeId");
							Map<String, String> deleteRole = new HashMap();
							deleteRole.put("roleTypeId", roleTypeId);
							deleteRole.put("partyId", partyId);
							try {
								delegator.removeByAnd("PartyRole", deleteRole);
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Toolbarbutton tlBarBtn = (Toolbarbutton) div.getFellow("roleInformationBtn");
							Events.postEvent("onClick", tlBarBtn, null);
							try {
								Messagebox.show("Role Deleted", "Success", 1, null);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				});
	}

	public static void showUpdatePostalAddressWindow(String contactMechId, String partyId) throws SuspendNotAllowedException,
			InterruptedException, GenericEntityException {

		GenericValue postalAddress = null;
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		postalAddress = delegator.findByPrimaryKey("PostalAddress", org.ofbiz.base.util.UtilMisc.toMap("contactMechId", contactMechId));

		EntityCondition countryCondition = EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "COUNTRY");
		EntityCondition stateCondition = EntityCondition.makeCondition("geoIdFrom", EntityOperator.EQUALS, postalAddress.getString("countryGeoId"));
		Set fieldToSelect = new HashSet();
		fieldToSelect.add("geoName");
		fieldToSelect.add("geoId");

		List<GenericValue> countries = delegator.findList("Geo", countryCondition, fieldToSelect, null, null, false);

		List<GenericValue> states = delegator.findList("GeoAssocAndGeoTo", stateCondition, fieldToSelect, null, null, false);
		Map arg = new HashMap();
		arg.put("partyId", partyId);
		arg.put("contactMechId", contactMechId);
		arg.put("stateProvinceGeoId", postalAddress.getString("stateProvinceGeoId"));
		arg.put("countryGeoId", postalAddress.getString("countryGeoId"));
		Window win = (Window) Executions.createComponents("/zul/employeeProfile/editPostalAddress.zul", null, arg);

		Textbox contactMechIdBox = (Textbox) win.getFellow("contactMechId");
		contactMechIdBox.setValue(contactMechId);

		Textbox toNameBox = (Textbox) win.getFellow("toName");
		toNameBox.setValue(postalAddress.getString("toName"));

		Textbox attnNameBox = (Textbox) win.getFellow("attnName");
		attnNameBox.setValue(postalAddress.getString("attnName"));

		Textbox address1Box = (Textbox) win.getFellow("address1");
		address1Box.setValue(postalAddress.getString("address1"));

		Textbox address2Box = (Textbox) win.getFellow("address2");
		address2Box.setValue(postalAddress.getString("address2"));

		Textbox cityBox = (Textbox) win.getFellow("city");
		cityBox.setValue(postalAddress.getString("city"));

		Textbox postalCodeBox = (Textbox) win.getFellow("postalCode");
		postalCodeBox.setValue(postalAddress.getString("postalCode"));

		Listbox countryGeoIdListItem = (Listbox) win.getFellow("countryGeoId");
		String postalAddressCountryGeoId = postalAddress.getString("countryGeoId");
		for (int i = 0; i < countries.size(); i++) {
			String geoId = countries.get(i).getString("geoId");
			if (geoId.equals(postalAddressCountryGeoId)) {
				countryGeoIdListItem.setSelectedIndex(i + 1);
			}

		}
		/*
		 * int stateIndex = 0; Listbox stateProvinceGeoIdListItem = (Listbox)
		 * win.getFellow("stateProvinceGeoId"); String
		 * postalAddressstateProvinceGeoId =
		 * postalAddress.getString("stateProvinceGeoId"); for (int i = 0; i <
		 * states.size(); i++) { String geoId =
		 * states.get(i).getString("geoId"); if
		 * (geoId.equals(postalAddressstateProvinceGeoId)) { //
		 * stateProvinceGeoIdListItem.setSelectedIndex(i+1); stateIndex = i+1; }
		 *
		 * }
		 */
		win.doModal();

	}

	public static void updatePassword(Event event) throws InterruptedException, GenericServiceException {
		try {
			UtilService.runService("updatePassword", event);
			Messagebox.show("Updated Successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Component userPasswordWindow = event.getTarget();
		userPasswordWindow.getDesktop().getExecution().getNativeRequest();
		String userLoginId = (String) ((Textbox) userPasswordWindow.getFellow("userLoginId")).getValue();
		Listitem enableItem = (Listitem) ((Listbox) userPasswordWindow.getFellow("enabled")).getSelectedItem();
		String enabled = (String) enableItem.getValue();
		Date disabledDate = null;
		if("N".equalsIgnoreCase(enabled))
			disabledDate = new Date();
		Timestamp disabledDateTime = null;
		if (disabledDate != null) {
			disabledDateTime = new Timestamp(disabledDate.getTime());
		}
		String failedLogin = (String) ((Textbox) userPasswordWindow.getFellow("failedLogins")).getValue();
		Long failedLogins = new Long(failedLogin);
		Map passwordMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginId, "enabled", enabled, "disabledDateTime",
				disabledDateTime, "successiveFailedLogins", failedLogins);
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		dispatcher.runSync("updateUserLoginSecurity", passwordMap);
	}

	public static void addSecurityGroup(Event event) throws GenericServiceException, GenericEntityException, InterruptedException {

		Component addSecurityGroupWindow = event.getTarget();
		addSecurityGroupWindow.getDesktop().getExecution().getNativeRequest();

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		Listitem groupIdItem = (Listitem) ((Listbox) addSecurityGroupWindow.getFellow("groupId")).getSelectedItem();
		String groupId = (String) groupIdItem.getValue();

		String userLoginId = (String) ((Textbox) addSecurityGroupWindow.getFellow("userLoginId")).getValue();
		Date fromDateType = (Date) ((Datebox) addSecurityGroupWindow.getFellow("fromDate")).getValue();
		Timestamp fromDate = new Timestamp(fromDateType.getTime());
		Timestamp thruDate = null;
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		List userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
		GenericValue userLoginGv = EntityUtil.getFirst(userLoginList);
		String partyId = null;
		if (userLoginGv != null) {
			partyId = (String) userLoginGv.getString("partyId");
		}
		String isRequisitionAdmin = "N";
		EntityCondition condition1 = EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", userLoginId, "thruDate", null));
		EntityCondition condition2 = EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "HRMS");
		EntityCondition condition = EntityCondition.makeCondition(condition1, condition2);
		List<GenericValue> checkUserLoginSecurityGroup = delegator.findList("UserLoginSecurityGroup", condition, null, null, null, false);
		if (UtilValidate.isNotEmpty(checkUserLoginSecurityGroup)) {
			String oldGroupId = checkUserLoginSecurityGroup.get(0).getString("groupId");
			Timestamp thruDateNew = UtilDateTime.addDaysToTimestamp(fromDate, -1);
			Timestamp oldFromDate = checkUserLoginSecurityGroup.get(0).getTimestamp("fromDate");
			GenericValue userLoginSecGV = delegator.makeValue("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId,
					"fromDate", oldFromDate, "groupId", oldGroupId, "thruDate", null));
		//	delegator.store(userLoginSecGV);
		}
		if (groupId.equals("Requisition_Admin")) {
			groupId = "HUMANRES_MGR";
			isRequisitionAdmin = "Y";
			Map assignRole = UtilMisc.toMap("userLogin", userLogin, "roleTypeId", "Requisition_Admin", "partyId", partyId);
			dispatcher.runSync("createPartyRole", assignRole);
		}
		if (groupId.equals("HUMANRES_MGR")) {
			Map assignRole = UtilMisc.toMap("userLogin", userLogin, "roleTypeId", "MANAGER", "partyId", partyId);
			dispatcher.runSync("createPartyRole", assignRole);
		}



		Map userSecurityPermissionDetailMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginId, "fromDate", fromDate,
				"groupId", groupId, "thruDate", thruDate, "isRequisitionAdmin", isRequisitionAdmin);
		dispatcher.runSync("addUserLoginToSecurityGroup", userSecurityPermissionDetailMap);
		// UtilMessagesAndPopups.showMessage("Security Group Permission Added");
		Messagebox.show("Security Group Added Successfully", "Success", 1, null);
	}

	public static void editSecurityGroup(Event event) throws GenericServiceException {

		Component editSecurityGroupWindow = event.getTarget();
		editSecurityGroupWindow.getDesktop().getExecution().getNativeRequest();

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		Listitem groupIdItem = (Listitem) ((Listbox) editSecurityGroupWindow.getFellow("groupId")).getSelectedItem();
		String groupId = (String) groupIdItem.getValue();

		String userLoginId = (String) ((Textbox) editSecurityGroupWindow.getFellow("userLoginId")).getValue();
		Date fromDateType = (Date) ((Datebox) editSecurityGroupWindow.getFellow("fromDate")).getValue();
		Timestamp fromDate = new Timestamp(fromDateType.getTime());
		Date thruDateType = (Date) ((Datebox) editSecurityGroupWindow.getFellow("thruDate")).getValue();
		Timestamp thruDate = null;
		if (thruDateType != null) {
			thruDate = new Timestamp(thruDateType.getTime());
		}
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		Map userSecurityPermissionDetailMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginId, "fromDate", fromDate,
				"groupId", groupId, "thruDate", thruDate);
		dispatcher.runSync("updateUserLoginToSecurityGroup", userSecurityPermissionDetailMap);

	}

	public static void removeSecurityGroup(Event event, final GenericValue gv, final String userLoginId) throws GenericServiceException,
			InterruptedException {

		final Component comp = event.getTarget();
		comp.getDesktop().getExecution().getNativeRequest();
		Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {

							GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
									"userLogin");
							GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

							String groupId = gv.getString("groupId");
							Timestamp fromDate = gv.getTimestamp("fromDate");
							Map groupMap = UtilMisc.toMap("userLogin", userLogin, "groupId", groupId, "fromDate", fromDate, "userLoginId",
									userLoginId);
							LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
							try {
								dispatcher.runSync("removeUserLoginFromSecurityGroup", groupMap);
							} catch (GenericServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Events.postEvent("onClick", comp.getFellow("roleWindow").getParent().getParent().getFellow("securityGroupBtn"),
									null);
							try {
								Messagebox.show("Security Group Removed", "Success", 1, null);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				});

	}

	public static void addEmployeeResponsibility(Event event) throws GenericServiceException, InterruptedException, GenericEntityException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Component addPartyResponsibilityWindow = event.getTarget();
		addPartyResponsibilityWindow.getDesktop().getExecution().getNativeRequest();
		String emplPositionId = (String) ((Textbox) addPartyResponsibilityWindow.getFellow("positionId")).getValue();
		createPositionResponsibility(emplPositionId, delegator, addPartyResponsibilityWindow);
		Messagebox.show("Responsibility Added Successfully", "Success", 1, null);

	}

	public static void createPositionResponsibility(String emplPositionId, GenericDelegator delegator, Component comp)
			throws GenericEntityException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		Set lstitem = new HashSet();
		lstitem = (Set) ((Listbox) comp.getFellow("responsibilityTypeId")).getSelectedItems();
		Iterator it = lstitem.iterator();
		Object item = null;
		Date fromDateType = (Date) ((Datebox) comp.getFellow("fromDate")).getValue();
		Timestamp fromDate = new Timestamp(fromDateType.getTime());
		Date thruDateType = (Date) ((Datebox) comp.getFellow("thruDate")).getValue();
		Timestamp thruDate = thruDateType!=null? new Timestamp(thruDateType.getTime()):null;
		String responsibilitySubType = null;
		responsibilitySubType = (String) ((Textbox) comp.getFellow("responsibilitySubType")).getValue();
		GenericValue positionGv = delegator.makeValidValue("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId,"additionalResponsibility",responsibilitySubType));
		delegator.createOrStore(positionGv);
		while (it.hasNext()) {
			item = it.next();
			Listitem selectedItem = (Listitem) item;
			String responsibilityType = (String) selectedItem.getValue();
			Map positionResponsibility = UtilMisc.toMap("emplPositionId", emplPositionId, "responsibilityTypeId", responsibilityType,
					"fromDate", fromDate, "thruDate", thruDate);
			try {
				GenericValue responsibilityGv = delegator.makeValidValue("EmplPositionResponsibility", positionResponsibility);
				delegator.createOrStore(responsibilityGv);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if (responsibilitySubType != null) {

				String responsibilitySubTypeId = delegator.getNextSeqId("PosResponsibilitySubType");

				delegator.create("PosResponsibilitySubType", UtilMisc.toMap("responsibilitySubTypeId", responsibilitySubTypeId,
						"emplPositionId", emplPositionId, "responsibilityTypeId", responsibilityType, "responsibilitySubType",
						responsibilitySubType));

			}
		}

	}

	public static void deleteEmployeeResponsibility(Event event, final GenericValue gv) throws GenericServiceException,
			InterruptedException {
		final Component comp = event.getTarget();
		Messagebox.show("Remove This Responsibility Type?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {
							GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
									"userLogin");
							GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

							String responsibilityTypeId = gv.getString("responsibilityTypeId");
							Timestamp fromDate = gv.getTimestamp("fromDate");
							String emplPositionId = gv.getString("emplPositionId");
							Map groupMap = UtilMisc.toMap("userLogin", userLogin, "responsibilityTypeId", responsibilityTypeId, "fromDate",
									fromDate, "emplPositionId", emplPositionId);
							LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
							try {
								dispatcher.runSync("deleteEmplPositionResponsibility", groupMap);
							} catch (GenericServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {
								Events.postEvent("onClick", comp.getFellow("hiddenButton"), null);
								Messagebox.show("Responsibility Deleted Successfully", "Success", 1, null);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
	}

	public static void updatePositionFulfillment(Event event) throws Exception {

		boolean isExist = false;
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Component editPositionFulfillmentWindow = event.getTarget();
		updateEmployeePosition(editPositionFulfillmentWindow, delegator, userLogin);
		updateEmployeeDepartment(editPositionFulfillmentWindow, delegator, userLogin);
		Messagebox.show("Updated Successfully", "Success", 1, null);

	}
	
private static void updateEmployeeDepartment(Component editPositionFulfillmentWindow,GenericDelegator delegator,GenericValue userLogin)	throws Exception{
	
	Listitem departmentNameItem = (Listitem) ((Listbox) editPositionFulfillmentWindow.getFellow("departmentName")).getSelectedItem();
	String newDepartmentId = null;
	if (departmentNameItem != null) {
		newDepartmentId = (String) departmentNameItem.getValue();
	}

	String employeeId = (String) ((Textbox) editPositionFulfillmentWindow.getFellow("employeeId")).getValue();
	GenericValue emplPositionGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
	String depId = emplPositionGv.getString("partyId");
	String positionId = emplPositionGv.getString("emplPositionId");
	if(!depId.equals(newDepartmentId)){
		emplPositionGv.put("partyId", newDepartmentId);
		delegator.store(emplPositionGv);
		updateEmploymentAtDepartmentChange(delegator, newDepartmentId, employeeId);
		addReportingStructureAtDepartmentChange(delegator, userLogin, newDepartmentId, positionId);
	}
}

private static void updateEmploymentAtDepartmentChange(GenericDelegator delegator,String newDepartmentId, String employeeId) throws Exception{
	List<GenericValue> employmentGvs = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo",employeeId,"thruDate",null));
	if(UtilValidate.isNotEmpty(employmentGvs)){
		GenericValue existingEmploymentGv = EntityUtil.getFirst(employmentGvs);
		
		Timestamp d_fromDate = null;
		Timestamp d_thruDate = null;

        Date currDateType = new Date();     
        Timestamp currDate = new Timestamp(currDateType.getTime());
       
        Date cuurDate = UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(new java.util.Date()));
        if (existingEmploymentGv != null) {
            d_fromDate = existingEmploymentGv.getTimestamp("fromDate");
            if (cuurDate.compareTo(UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(d_fromDate))) == 0)
                d_thruDate = UtilDateTime.getTimestamp(cuurDate.getTime());
            else
                d_thruDate = UtilDateTimeSME.substractDaysToTimestamp(currDate, 1);
        }
        existingEmploymentGv.put("thruDate", d_thruDate);
        delegator.store(existingEmploymentGv);
        
        Date fromDateType = new Date();
        Timestamp fromDate = new Timestamp(fromDateType.getTime());
    	
        GenericValue newEmploymentGv = delegator.makeValidValue("Employment", UtilMisc.toMap("roleTypeIdFrom",existingEmploymentGv.getString("roleTypeIdFrom"),"roleTypeIdTo",
				existingEmploymentGv.getString("roleTypeIdTo"),"partyIdFrom",newDepartmentId,"partyIdTo",
				existingEmploymentGv.getString("partyIdTo"),"fromDate",fromDate));
        delegator.createOrStore(newEmploymentGv);
	}
}

private static void addReportingStructureAtDepartmentChange(GenericDelegator delegator,GenericValue userLogin,String newDepartmentId, String positionId)
throws Exception{
	
	GenericValue departmentPositionGv = delegator.findByPrimaryKey("DepartmentPosition",UtilMisc.toMap("departmentId",newDepartmentId));
	String departmentPositionId = departmentPositionGv.getString("departmentPositionId");
	
	List<GenericValue> existingReportingStructureList = delegator.findByAnd("EmplPositionReportingStruct",UtilMisc.toMap("emplPositionIdManagedBy", positionId, "thruDate", null));
    if (existingReportingStructureList.size() > 0) {
        GenericValue existingReportingStructGv = EntityUtil.getFirst(existingReportingStructureList);
        String emplPositionIdReportingTo = existingReportingStructGv.getString("emplPositionIdReportingTo");
        Timestamp existingfromDate = existingReportingStructGv.getTimestamp("fromDate");
        Timestamp d_fromDate = null;
        Timestamp d_thruDate = null;
        Date currDateType = new Date();     
        Timestamp currDate = new Timestamp(currDateType.getTime());
        Date cuurDate = UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(new java.util.Date()));
        if (existingReportingStructGv != null) {
            d_fromDate = existingReportingStructGv.getTimestamp("fromDate");
            if (cuurDate.compareTo(UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(d_fromDate))) == 0)
                d_thruDate = UtilDateTime.getTimestamp(cuurDate.getTime());
            else
                d_thruDate = UtilDateTimeSME.substractDaysToTimestamp(currDate, 1);
        }

        existingReportingStructGv.putAll(UtilMisc.toMap("emplPositionIdReportingTo", emplPositionIdReportingTo, "emplPositionIdManagedBy",
                positionId, "primaryFlag", "Y", "fromDate", existingfromDate, "thruDate", d_thruDate));
        delegator.store(existingReportingStructGv);
    }
    Date fromDateType = new Date();
    Timestamp fromDate = new Timestamp(fromDateType.getTime());
	Map reportingStructureDetails = UtilMisc.toMap("userLogin", userLogin, "emplPositionIdReportingTo", departmentPositionId, "emplPositionIdManagedBy",
                positionId, "primaryFlag", "Y", "fromDate", fromDate, "thruDate", null);
    LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
    dispatcher.runSync("createEmplPositionReportingStruct", reportingStructureDetails);

}

private static void updateEmployeePosition(Component editPositionFulfillmentWindow,GenericDelegator delegator,GenericValue userLogin) throws Exception	{
	String emplPositionId = (String) ((Bandbox) editPositionFulfillmentWindow.getFellow("searchPanel")).getValue();
	boolean isExist = com.ndz.controller.EmployeeManagementController.checkValidPositionId(emplPositionId, delegator);
	List emplPositionList = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId));
	GenericValue emplPositionGv = EntityUtil.getFirst(emplPositionList);
	String deptId = (String) emplPositionGv.getString("partyId");

	Listitem positionTypeItem = (Listitem) ((Listbox) editPositionFulfillmentWindow.getFellow("positionType")).getSelectedItem();
	String positionType = null;
	if (positionTypeItem != null) {
		positionType = (String) positionTypeItem.getValue();
	}
	String employeeId = (String) ((Textbox) editPositionFulfillmentWindow.getFellow("employeeId")).getValue();
	List emplPosFullfillmentList = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", employeeId));
	GenericValue emplPosFullfillmenGv = EntityUtil.getFirst(emplPosFullfillmentList);
	Timestamp fromDate = null;
	if (emplPosFullfillmenGv != null) {
		fromDate = emplPosFullfillmenGv.getTimestamp("fromDate");
	}
	LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
	Map updatePositionTypeMap = UtilMisc.toMap("userLogin", userLogin, "emplPositionId", emplPositionId, "emplPositionTypeId",
			positionType);
	dispatcher.runSync("updateEmplPosition", updatePositionTypeMap);
	Map positionMap = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "emplPositionId", emplPositionId, "fromDate",
			fromDate, "thruDate", UtilDateTime.nowTimestamp());
	Map newPositionFulfillmentMap = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "emplPositionId", emplPositionId,
			"fromDate", UtilDateTime.nowTimestamp());
	dispatcher.runSync("updateEmplPositionFulfillment", positionMap);
	dispatcher.runSync("createEmplPositionFulfillment", newPositionFulfillmentMap);
}

	public void updateConfirmationDate(final Component editPositionFulfillmentWindow,final Component toolBarBtn) throws Exception{
		final GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		final GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		final String employeeId = (String) ((Textbox) editPositionFulfillmentWindow.getFellow("employeeId")).getValue();
		final Date confirmationDate = ((Datebox) editPositionFulfillmentWindow.getFellow("confirmationDate")).getValue();
		Messagebox.show("Do you want to confirm Employment?", "Warning", Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {
							Timestamp confirmationDateInTimeStamp = new Timestamp(confirmationDate.getTime());
								try {
									updateEmployeePosition(editPositionFulfillmentWindow, delegator, userLogin);
									List<GenericValue> employmentGvs = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo",employeeId));
									GenericValue employmentGv = EntityUtil.getFirst(employmentGvs);
									employmentGv.put("confirmationDate", confirmationDateInTimeStamp);
									delegator.store(employmentGv);
									Messagebox.show("Employment confirmation date updated", "Success", 1, null);
									Events.postEvent(Events.ON_CLICK,toolBarBtn,null);
									editPositionFulfillmentWindow.detach();
									} catch (Exception e) {
										e.printStackTrace();
									} 
					}}});
	}

	public static void showUpdateEmailAddressWindow(String contactMechId, String partyId) throws SuspendNotAllowedException,
			InterruptedException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Map arg = new HashMap();
		arg.put("contactMechId", contactMechId);
		arg.put("partyId", partyId);

		Window win = (Window) Executions.createComponents("/zul/employeeProfile/editEmailAddress.zul", null, arg);
		win.doModal();
	}

	public static void createNewEmailAddress(Event event) throws InterruptedException {

		try {
			UtilService.runService("createPartyEmailAddress", event);
			Messagebox.show("Saved Successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getskillType(String skillTypeId) throws GenericEntityException {

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		List skillTypeList = delegator.findByAnd("SkillType", UtilMisc.toMap("skillTypeId", skillTypeId));
		GenericValue skillTypeGv = null;
		skillTypeGv = EntityUtil.getFirst(skillTypeList);
		if (skillTypeGv != null)
			return skillTypeGv.getString("description");
		return skillTypeId;

	}

	public static void showEditSkillWindow(String skillTypeId, String partyId) throws SuspendNotAllowedException, InterruptedException {

		Map arg = new HashMap();
		arg.put("skillTypeId", skillTypeId);
		arg.put("partyId", partyId);
		Window win = (Window) Executions.createComponents("/zul/employeeProfile/editEmployeeSkill.zul", null, arg);
		win.doModal();
	}

	public static void SkillDelete(final String skillTypeId, final String partyId, Event event) throws SuspendNotAllowedException,
			InterruptedException {
		final Component comp = event.getTarget();
		Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {
					GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
					try {
						delegator.removeByAnd("PartySkill", UtilMisc.toMap("skillTypeId", skillTypeId, "partyId", partyId));
						Events.postEvent("onClick", comp.getFellow("personSkillWindow").getParent().getFellow("skillInformationBtn"), null);
						Messagebox.show("Deleted Successfully", "Success", 1, null);
					} catch (GenericEntityException e) {
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

	public static String getContactMechPurPoseType(String contactMechId) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		List partyContactMechPurposeList = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("contactMechId", contactMechId));
		GenericValue partyContactMechPurposeGv = EntityUtil.getFirst(partyContactMechPurposeList);
		String contactMechPurPoseTypeId = null;
		if (partyContactMechPurposeGv != null) {
			contactMechPurPoseTypeId = (String) partyContactMechPurposeGv.getString("contactMechPurposeTypeId");
		}
		List contactMechPurposeTypeList = delegator.findByAnd("ContactMechPurposeType", UtilMisc.toMap("contactMechPurposeTypeId",
				contactMechPurPoseTypeId));
		GenericValue contactMechPurposeGv = EntityUtil.getFirst(contactMechPurposeTypeList);
		String contactMechPurpose = null;
		if (contactMechPurposeGv != null)
			return contactMechPurposeGv.getString("description");
		return null;
	}

	public static void createUserLogin(Event event, Toolbarbutton toolbarbtn) throws GenericEntityException, InterruptedException,
			GenericServiceException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Component createuserloginwindow = event.getTarget();
		String isRequisitionAdmin = "N";
		String employeeId = (String) ((Textbox) createuserloginwindow.getFellow("employeeId")).getValue();
		List checkuserloginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", employeeId));
		if (checkuserloginList.size() > 0) {
			Messagebox.show("Userlogin Id exist for this employee;Cannot create another", "Error", 1, null);
			return;
		}
		Listitem securityGroupItem = (Listitem) ((Listbox) createuserloginwindow.getFellow("securityGroup")).getSelectedItem();
		String securityGroup = (String) securityGroupItem.getValue();
		if (securityGroup.equals("Requisition_Admin")) {
			isRequisitionAdmin = "Y";
		}
		String userloginid = (String) ((Textbox) createuserloginwindow.getFellow("addEmployee_userLoginId")).getValue();
		String password = (String) ((Textbox) createuserloginwindow.getFellow("addEmployee_password")).getValue();
		String verifypassword = (String) ((Textbox) createuserloginwindow.getFellow("addEmployee_verifyPassword")).getValue();
		Map userLoginMap = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "userLoginId", userloginid, "currentPassword",
				password, "currentPasswordVerify", verifypassword, "enabled", "Y");
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		dispatcher.runSync("createUserLogin", userLoginMap);
		Map assignSecurityGroup = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userloginid, "groupId", securityGroup, "fromDate",
				UtilDateTime.nowTimestamp(), "isRequisitionAdmin", isRequisitionAdmin);
		@SuppressWarnings("unused")
		Map assignSecurityGroupResult = null;
		assignSecurityGroupResult = dispatcher.runSync("addUserLoginToSecurityGroup", assignSecurityGroup);

		assignSecurityGroup = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userloginid, "groupId", "HRMS", "fromDate",
				UtilDateTime.nowTimestamp());
		assignSecurityGroupResult = null;
		assignSecurityGroupResult = dispatcher.runSync("addUserLoginToSecurityGroup", assignSecurityGroup);
		Messagebox.show("User Login created successfully", "Success", 1, null);
		Events.postEvent("onClick", toolbarbtn, null);

	}

	public static void disableEmployee(Event event) throws GenericEntityException, InterruptedException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();
		Component disableWindow = event.getTarget();
		final String partyId = ((Textbox) disableWindow.getFellow("partyId")).getValue();

		String positionId = null;
		String department = null;

		GenericValue postionGv = HumanResUtil.getEmplPositionForParty(partyId, delegator);
		if(UtilValidate.isNotEmpty(postionGv)){
			positionId = postionGv.getString("emplPositionId");
			List emplPositionList = delegator.findByAnd("EmplPosition",org.ofbiz.base.util.UtilMisc.toMap("emplPositionId",positionId),null,false);
			GenericValue emplPositionGv = EntityUtil.getFirst(emplPositionList);
			department = emplPositionGv.getString("partyId");
		}
		updateUserLogin(delegator,partyId);
		updateEmployeePositionFullfillment(delegator,partyId);

		//Map map = UtilMisc.toMap("partyIdTo",partyId,"partyIdFrom",department,"roleTypeIdTo","EMPLOYEE","roleTypeIdFrom","ORGANIZATION_ROLE");
		EntityCondition roleTypeIdFrom = EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"ORGANIZATION_ROLE");
		EntityCondition roleTypeIdTo = EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.IN, Arrays.asList("EMPLOYEE","MANAGER"));
		EntityCondition partyIdTo = EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyId);
		EntityCondition partyIdFrom = EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,department);
		List conditions = Arrays.asList(roleTypeIdFrom,roleTypeIdTo,partyIdTo,partyIdFrom);
		updateEmployment(delegator,conditions);
		updatePartyRelationship(delegator,conditions);
		updatePersonWithDisabledState(delegator,partyId);
		Messagebox.show("Disabled Successfully", "Success", Messagebox.OK, Messagebox.NONE, new EventListener() {
			public void onEvent(Event evt) {
				if ("onOK".equals(evt.getName())) {

					try {
						Executions.getCurrent().sendRedirect("/control/profile?partyId=" + partyId);

					} catch (Exception e) {

					}
				}
			}
		});

	}
	private static void updatePersonWithDisabledState(GenericDelegator delegator,String partyId) throws GenericEntityException {
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
		if (UtilValidate.isNotEmpty(person)) {
			person.put("isDisabled","Y");
			person.store();
		}
	}
	private static void updateUserLogin(GenericDelegator delegator,String partyId) throws GenericEntityException {
		Timestamp now = new Timestamp(new Date().getTime());
		List userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId),null,false);
		GenericValue userLoginGv = EntityUtil.getFirst(userLoginList);
		if (UtilValidate.isNotEmpty(userLoginGv)) {
			userLoginGv.put("disabledDateTime",null);
			userLoginGv.put("enabled","N");
			userLoginGv.store();
		}
	}

	private static void updateEmployeePositionFullfillment(GenericDelegator delegator,String partyId) throws GenericEntityException {
		Timestamp now = new Timestamp(new Date().getTime());
		EntityCondition partyIdCond = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
		EntityCondition thruDateCond = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
		List conditions = Arrays.asList(partyIdCond,thruDateCond);
		List<GenericValue> emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition(conditions, EntityOperator.AND),null,null,null,false);
		for(GenericValue emplPositionFulfillment:emplPositionFulfillmentList){
			emplPositionFulfillment.put("thruDate",now);
			emplPositionFulfillment.store();
		}
	}

	private static void updateEmployment(GenericDelegator delegator,List conditions) throws GenericEntityException {
		Timestamp now = new Timestamp(new Date().getTime());
		List<GenericValue> employmentList = delegator.findList("Employment",EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		for(GenericValue employment:employmentList){
			employment.put("thruDate",now);
			employment.store();
		}
	}

	private static void updatePartyRelationship(GenericDelegator delegator,List conditions) throws GenericEntityException {
		Timestamp now = new Timestamp(new Date().getTime());
		List<GenericValue> partyRelationshipList = delegator.findList("PartyRelationship",EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		for(GenericValue partyRelationship:partyRelationshipList){
			partyRelationship.put("thruDate",now);
			partyRelationship.store();
		}
	}
}
