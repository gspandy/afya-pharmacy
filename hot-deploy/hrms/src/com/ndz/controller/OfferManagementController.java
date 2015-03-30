package com.ndz.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.Executions;
import com.ndz.zkoss.UtilService;
import com.ndz.zkoss.util.EmployeePositionService;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.ndz.zkoss.util.MailUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Filedownload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.utils.Log4jConfigurator;


public class OfferManagementController extends GenericForwardComposer {

	public static void generateOffer(Event event, String requisitionId) throws GenericServiceException,
			InterruptedException, GenericEntityException {

	Component generateOfferWindow = event.getTarget();
	GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
	GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

	String userLoginId = userLogin.getString("partyId");
	String partyId = (String) ((Textbox) generateOfferWindow.getFellow("partyId")).getValue();
	String applicationId = (String) ((Textbox) generateOfferWindow.getFellow("applicationId")).getValue();
	String mgrPositionId = (String) ((Textbox) generateOfferWindow.getFellow("mgrPositionId")).getValue();
	Date offerDateType = (Date) ((Datebox) generateOfferWindow.getFellow("offerDate")).getValue();
	java.sql.Date offerDate = new java.sql.Date(offerDateType.getTime());
	Date joiningDateType = (Date) ((Datebox) generateOfferWindow.getFellow("joiningDate")).getValue();
	java.sql.Date joiningDate = new java.sql.Date(joiningDateType.getTime());

	Listitem statusId = (Listitem) ((Listbox) generateOfferWindow.getFellow("offerStatus")).getSelectedItem();
	// String offerStatus = (String) ((Textbox) generateOfferWindow
	// .getFellow("offerStatus")).getValue();

	String offerStatus = (String) statusId.getValue();

	String comment = (String) ((Textbox) generateOfferWindow.getFellow("hr_comment")).getValue();
	
	Map generateOfferMap = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "applicationId", applicationId,
			"mgrPositionId", mgrPositionId, "offerDate", offerDate, "joiningDate", joiningDate,"requisitionId",requisitionId);
	
	
	LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
	Map result = dispatcher.runSync("createOfferService", generateOfferMap);
	String offerId = (String) result.get("offerId");
	Map generateOfferStatusMap = UtilMisc.toMap("userLogin", userLogin, "offerId", offerId, "statusId", offerStatus,
			"hr_comment", comment, "updatedBy", userLoginId, "requisitionId", requisitionId);
	dispatcher.runSync("createOfferStatusService", generateOfferStatusMap);
	List candidateResumeList = delegator.findByAnd("PartyResume", UtilMisc.toMap("partyId", partyId));
	GenericValue candidateResumeGv = null;
	candidateResumeGv = EntityUtil.getFirst(candidateResumeList);
	String resumeId = candidateResumeGv.getString("resumeId");
	Map resumeMap = UtilMisc.toMap("userLogin", userLogin, "resumeId", resumeId, "resumeStatus", "2");
	dispatcher.runSync("updatePartyResume", resumeMap);
	Messagebox.show("Offer  " + (offerId) + "  Generated Successfully", "Success", 1, null);
	}

	public static void processOffer(Event event, String applicationId, boolean isAdmin, Listitem statusItem)
			throws InterruptedException, GenericEntityException {

	try {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
				"userLogin");
		String employeeId = (String) userLogin.get("partyId");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		List employmentAppList = null;
		employmentAppList = delegator.findByAnd("EmploymentApp", org.ofbiz.base.util.UtilMisc.toMap("applicationId",
				applicationId));
		GenericValue employmentAppGv = org.ofbiz.entity.util.EntityUtil.getFirst(employmentAppList);
		String initiatorId = null;
		String statusId = (String) statusItem.getValue();
		GenericValue statusGv = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId));
		String status = (String) statusGv.getString("description");

		if (!isAdmin) {
			if (employmentAppGv != null) {
				String requisitionId = (String) employmentAppGv.getString("requisitionId");

				GenericValue employeeRequisitionGv = delegator.findByPrimaryKey("EmployeeRequisition",
						org.ofbiz.base.util.UtilMisc.toMap("requisitionId", requisitionId));
				initiatorId = (String) employeeRequisitionGv.getString("partyId");
			}
			if (!(employeeId.equals(initiatorId))) {
				Messagebox.show("You are not authorized to process the offer", "Error", 1, null);
				return;
			}
		}
		Component authorizeOfferWindow = event.getTarget();
		UtilService.runService("processOfferService", event);
		Events.postEvent("onClick$searchPerCompany", authorizeOfferWindow.getPage().getFellow("searchPanel"), null);

		if (status.equals("Offer Withdrawn")) {
			Messagebox.show(status, "Success", 1, null);
		} else {
			Messagebox.show("Offer " + status, "Success", 1, null);
		}

	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}

	public static void createOfferCTC(Event event) throws GenericServiceException, InterruptedException {
	Component addEmplSalStructure = event.getTarget();
	GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
	HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
	GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
	String partyId = (String) ((Textbox) addEmplSalStructure.getFellow("partyId")).getValue();
	String offerId = (String) ((Textbox) addEmplSalStructure.getFellow("offerId")).getValue();
	Date fromDateType = (Date) ((Datebox) addEmplSalStructure.getFellow("fromDate")).getValue();
	String salaryBean = (String) ((Textbox) addEmplSalStructure.getFellow("salaryBean")).getValue();
	java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());
	Map createOfferMap = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "offerId", offerId, "fromDate",
			fromDate, "salary", request.getSession().getAttribute("salary"));
	LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
	dispatcher.runSync("createOfferCTCService", createOfferMap);
	Messagebox.show("Offer created", "Success", 1, null);

	}

	@SuppressWarnings("unchecked")
	public static void createEmployment(Event event, GenericValue Gv) throws GenericEntityException,GenericServiceException, InterruptedException {

	GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
	String offerId = (String) Gv.getString("offerId");
	String prospectId = null;
	String applicationId = null;
	GenericValue offerHeadGv = delegator.findByPrimaryKey("OfferHead", UtilMisc.toMap("offerId", offerId));
	if (offerHeadGv != null) {
		prospectId = (String) offerHeadGv.getString("partyId");
		applicationId = (String) offerHeadGv.getString("applicationId");
	}
	boolean hasSalaryStructure = hasSalaryStructureFor(prospectId);
	if(!hasSalaryStructure){
		return;
	}
	List employmentList = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", prospectId));
	if (employmentList.size() > 0) {
		Messagebox.show("Prospect is Already Added As Employee", "Error", 1, null);
		return;
	}
	GenericValue employmentAppGv = delegator.findByPrimaryKey("EmploymentApp", UtilMisc.toMap("applicationId",applicationId));
	String requisitionId = null;
	if (employmentAppGv != null) {
		requisitionId = (String) employmentAppGv.getString("requisitionId");
	}
	GenericValue reqGv = delegator.findByPrimaryKey("EmployeeRequisition", UtilMisc.toMap("requisitionId",requisitionId));
	long balancePos = 0L;
	if (reqGv != null) balancePos = reqGv.getLong("balancePositionToFulfill");
	if (balancePos == 0L) {
		Messagebox.show("Prospect cannot be make employee as position got closed", "Error", Messagebox.OK, "", 1,
				new org.zkoss.zk.ui.event.EventListener() {
					public void onEvent(Event evt) {
					}
				});
		return;
	}

	GenericValue employeeRequisitionGv = delegator.findByPrimaryKey("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId));
	Long noOfPositionRequested = null;
	String departmentId = null;
	String grade=null;
	String positionCategory=null;
	String employeeType=null;
	if (employeeRequisitionGv != null) {
		noOfPositionRequested = (Long) employeeRequisitionGv.getLong("balancePositionToFulfill");
		departmentId = (String) employeeRequisitionGv.getString("reqRaisedByDept");
		grade=(String)employeeRequisitionGv.getString("grade");
		positionCategory=employeeRequisitionGv.getString("positionCategory");
		employeeType=employeeRequisitionGv.getString("employeeType");
	}
	Long noOfPosition = null;
	List partyContactMech = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", prospectId));
	GenericValue partyContactMechGv = EntityUtil.getFirst(partyContactMech);
	String contactMechId = null;
	if (partyContactMechGv != null) {
		contactMechId = (String) partyContactMechGv.getString("contactMechId");
	}
	List contactMechList = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechId", contactMechId));
	GenericValue contactMechGv = EntityUtil.getFirst(contactMechList);
	String emailAddress = null;
	if (contactMechGv != null) {
		emailAddress = (String) contactMechGv.getString("infoString");
	}
	List emplPositionIdList = delegator.findByAnd("EmplPosition", UtilMisc.toMap("requisitionId", requisitionId));
	GenericValue emplPositionIdGv = EntityUtil.getFirst(emplPositionIdList);
	String emplPosId = null;
	for (int i = 0; i < emplPositionIdList.size(); i++) {
		if (emplPositionIdGv != null) {
			//emplPosId = (String) emplPositionIdGv.getString("emplPositionId");
			emplPositionIdGv=(GenericValue)emplPositionIdList.get(i);
			emplPosId = (String) emplPositionIdGv.getString("emplPositionId");
		}
		List posFulfillmentList = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("emplPositionId",emplPosId));
		if (posFulfillmentList.size() <= 0) break;
	}
	
	String userName = null;
	if (emplPosId != null) {
		Map prospectRoleMap = UtilMisc.toMap("userLogin", userLogin, "partyId", prospectId, "roleTypeId", "EMPLOYEE");
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		dispatcher.runSync("createPartyRole", prospectRoleMap);
		Map posFulfillmentMap = UtilMisc.toMap("userLogin", userLogin, "emplPositionId", emplPosId, "partyId",
				prospectId, "fromDate", UtilDateTime.nowTimestamp());
		dispatcher.runSync("createEmplPositionFulfillment", posFulfillmentMap);
		EmployeePositionService.updateEmployeePositionStatus(emplPosId);
		Map employmentDetails = UtilMisc.toMap("userLogin", userLogin, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
				"roleTypeIdTo", "EMPLOYEE", "partyIdFrom", departmentId, "partyIdTo", prospectId, "fromDate",
				UtilDateTime.nowTimestamp());
		Map employmentResult = null;
		employmentResult = dispatcher.runSync("createEmployment", employmentDetails);
		GenericValue personGv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", prospectId));
		if (employeeRequisitionGv != null) {
			personGv.put("grades", grade);
			personGv.put("positionCategory",positionCategory);
			personGv.put("employeeType", employeeType);
			delegator.store(personGv);
		}
		
		String firstName = (String) personGv.getString("firstName");
		String lastName = (String) personGv.getString("lastName");
		userName = firstName + prospectId;
		userName = userName.toLowerCase();
		String password = firstName + "hrms";
		Map userLoginMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userName, "currentPassword", password,	"currentPasswordVerify", password, "partyId", prospectId);
		dispatcher.runSync("createUserLogin", userLoginMap);

		Map assignSecurityGroup = UtilMisc.toMap("userLoginId", userName, "groupId", "HUMANRES_USER", "fromDate",UtilDateTime.nowTimestamp());

		delegator.create("UserLoginSecurityGroup", assignSecurityGroup);
		assignSecurityGroup = UtilMisc.toMap("userLoginId", userName, "groupId", "HRMS", "fromDate", UtilDateTime.nowTimestamp());
		delegator.create("UserLoginSecurityGroup", assignSecurityGroup);

		Map mailContentMap = new HashMap();
		mailContentMap.put("sendTo", emailAddress);
		mailContentMap.put("templateName", "createEmployeeEmail.ftl");

		Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
		mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
		mailContentMap.put("subject", "Welcome To" + " " + mailProp.get("companyName"));
		mailContentMap.put("templateData", UtilMisc.toMap("userName", userName, "password", password, "firstName",
				firstName, "lastName", lastName, "teamFrom", mailProp.get("teamFrom"), "website", mailProp
						.get("website"), "companyName", mailProp.get("companyName"), "companyWebsite", mailProp
						.get("companyWebsite")));

		((Map) mailContentMap.get("templateData")).putAll(mailProp);

		dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
		noOfPosition = noOfPositionRequested - 1;
		Map updateRequisition = UtilMisc.toMap("requisitionId", requisitionId, "balancePositionToFulfill", noOfPosition);
		employeeRequisitionGv.putAll(updateRequisition);
		employeeRequisitionGv.store();
		if (noOfPosition == 0) {
			Map updateRequisitionStatus = UtilMisc.toMap("requisitionId", requisitionId, "statusId", "Closed");
			employeeRequisitionGv.putAll(updateRequisitionStatus);
			employeeRequisitionGv.store();
		}
	}
	Map<String, String> emplStatus = UtilMisc.toMap("partyId", prospectId, "createdBy", userLogin.getString("partyId"),"statusId", "ESI000");
	delegator.create("EmplStatus", emplStatus);
	Messagebox.show("Prospect Added As Employee and Login Credential Has been Sent to Prospect Mail", "Success", 1,	null);
	updateProspectReportingStructDate(emplPosId);

	}

	public static void updateProspectReportingStructDate(String emplPosId) throws GenericEntityException,GenericServiceException, InterruptedException {

	List ProspectReportingStructList = null;
	String emplPositionIdReportingTo = null;
	String emplPositionIdManagedBy = null;
	GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
	ProspectReportingStructList = delegator.findByAnd("EmplPositionReportingStruct", UtilMisc.toMap(
			"emplPositionIdManagedBy", emplPosId));
	GenericValue prospectReportingStructListGv = EntityUtil.getFirst(ProspectReportingStructList);
	emplPositionIdReportingTo = prospectReportingStructListGv.getString("emplPositionIdReportingTo");
	emplPositionIdManagedBy = prospectReportingStructListGv.getString("emplPositionIdManagedBy");

	delegator.removeValue(prospectReportingStructListGv);
	delegator.create("EmplPositionReportingStruct", UtilMisc.toMap("emplPositionIdReportingTo",
			emplPositionIdReportingTo, "emplPositionIdManagedBy", emplPositionIdManagedBy, "fromDate", UtilDateTime
					.nowTimestamp()));
	}

	public ListitemRenderer getListItemRenderer() {
	return new ListitemRenderer() {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		public void render(Listitem listitem, Object object) throws Exception {
		GenericValue gv = (GenericValue) object;
		listitem.setValue(gv);
		Listcell firstNameListCell = new Listcell(gv.getString("offerId"));
		firstNameListCell.setParent(listitem);
		Listcell lastNameListcell = new Listcell(gv.getString("partyId"));
		lastNameListcell.setParent(listitem);
		Listcell partyIdListcell = new Listcell(com.ndz.zkoss.HrmsUtil.getFullName(delegator, gv.getString("partyId")));
		partyIdListcell.setParent(listitem);
		Listcell applicationIdListCell = new Listcell(gv.getString("applicationId"));
		applicationIdListCell.setParent(listitem);
		Listcell offerDateListcell = new Listcell(gv.getString("offerDate"));
		offerDateListcell.setParent(listitem);
		}
	};
	}

	public static boolean hasSalaryStructureFor(String partyId) throws GenericEntityException {
	GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	EntityCondition condition = EntityCondition.makeCondition("partyId", EntityComparisonOperator.EQUALS, partyId);
	if (UtilValidate.isNotEmpty(delegator.findList("OfferSal", condition, null, null, null, false)))

	return true;
	try {
		Messagebox.show("Salary Structure not Attached for the Candidate. Please Attach Salary Structure to Generate Offer Letter. ","Error", 1, null);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
	}
	
	public boolean hasOrganizationGeneralLocationAddress(){
	GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
	List<GenericValue> generalLocationGvs = null;
	try {
		generalLocationGvs = delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechPurposeTypeId", "GENERAL_LOCATION","partyId","Company"));
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	if(UtilValidate.isNotEmpty(generalLocationGvs))
		return true;
	return false;
	}
	
	public boolean hasOrganizationPrimaryEmail(){
	GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	List<GenericValue> generalLocationGvs = null;
	try {
		generalLocationGvs = delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL","partyId","Company"));
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	if(UtilValidate.isNotEmpty(generalLocationGvs))
		return true;
	return false;
	}
	
	public boolean hasOrganizationPrimaryPhone(){
	GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	List<GenericValue> generalLocationGvs = null;
	try {
		generalLocationGvs = delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_PHONE","partyId","Company"));
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	if(UtilValidate.isNotEmpty(generalLocationGvs))
		return true;
	return false;
	}

   public static String getCeoEmailId(Delegator delegator)throws GenericEntityException, GenericServiceException, InterruptedException{
	    	 List  personList=delegator.findByAnd("Person", UtilMisc.toMap("isCeo","Y"));
	     	if(UtilValidate.isEmpty(personList)){
	     		Messagebox.show("CEO is not configured for the organization", "Error",1, null);
	 			return null;
	     	}
	     	GenericValue personGv=(GenericValue)personList.get(0);
	     	String partyId=personGv.getString("partyId");
	     	List<GenericValue> contactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
	         if (UtilValidate.isEmpty(contactMechPurposes)) {
	             Messagebox.show("Email cannot be sent;Primary Email for CEO is not configured", "Error", 1, null);
	             return null;
	         }
	         String contactMechId = contactMechPurposes.get(0).getString("contactMechId");
	         for (GenericValue contactMechPurposeGv : contactMechPurposes) {
	             if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(contactMechPurposeGv.getString("contactMechId"))) {
	                 contactMechId = contactMechPurposeGv.getString("contactMechId");
	             }
	         }
	         List<GenericValue> contactMechGvs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "contactMechId", contactMechId));
	         GenericValue contactMechGv = (GenericValue)contactMechGvs.get(0);
	         if(UtilValidate.isNotEmpty(contactMechGv.getString("infoString")))
	         	return contactMechGv.getString("infoString");
	         else{
	         	Messagebox.show("Email cannot be sent;Primary Email for CEO is not configured", "Error", 1, null);
	            return null;
	         }
 	 }
	   
   public static void sendInitializeOfferMailToCEO(String requisitionId, Delegator delegator,List performanceNoteList,
	    		String candidateId) throws GenericEntityException, GenericServiceException, InterruptedException  {
	    GenericValue requisitionGv = delegator.findOne("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId), false);
		String ceoMailId=getCeoEmailId(delegator);
	    if(UtilValidate.isEmpty(ceoMailId))
	        	return;
	    GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
	    Delegator delegator2 = userLogin.getDelegator();
	    LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();
	        
	    
	    GenericValue performanceNoteGv=null;
	    List<GenericValue> performanceNoteGvList = delegator2.findByAnd("PerformanceNote", UtilMisc.toMap("partyId",candidateId));
        if (UtilValidate.isNotEmpty(performanceNoteGvList)) {
            performanceNoteGv=performanceNoteGvList.get(0);
        }
        boolean result = MailUtil.sendInitiateOfferMailToCEO(userLogin.getString("partyId"), delegator2, "ceoInitiateOfferEmail.ftl", ceoMailId,
	    UtilMisc.toMap("requisitionGv", requisitionGv, "performanceNoteGv", performanceNoteGv,"performanceList", performanceNoteList),dispatcher,"Initiate Offer Request");
	    if (result) {
	            Messagebox.show("Mail Sent Successfully", "Success", 1, null);
	        }
    }
   public  byte[] attachmentData=null;
   public Media mediaFile=null;
   
     public void uploadOfferLetter(Media media) throws IOException,InterruptedException {
      String fileName = media.getName();
      this.mediaFile=media;
		 if (!fileName.endsWith(".docx") && !fileName.endsWith(".doc")) {
			 Messagebox.show("Please select document file","Error", 1, null);
				return;
		}
		 this.attachmentData = IOUtils.toByteArray(this.mediaFile.getStreamData());
		if(UtilValidate.isNotEmpty(this.attachmentData)) {
			Messagebox.show("Offer letter uploaded successfully", "Success", 1, null);
		}
     return ;
   }

   
   
   public  void downloadOfferLetter(String offerId) throws GenericEntityException, IOException,  InterruptedException {
	   InputStream is=null;		
	   GenericDelegator delegator = HrmsInfrastructure.getDelegator();
	   GenericValue offerGv=delegator.findOne("OfferHead",UtilMisc.toMap("offerId",offerId),false);
	   String expatriatesOfferLetterFileName=null;
  		
	   if(this.attachmentData!=null){
		   try{
	   			is=convertToPDF(this.attachmentData);	
	   			
	   			byte[] expatriatesOfferLetter=IOUtils.toByteArray(is);
	   			offerGv.put("expatriatesOfferLetter", expatriatesOfferLetter);
	   			offerGv.put("expatriatesOfferLetterFileName",this.mediaFile.getName());
		   		delegator.store(offerGv);
		   		byte[]content=offerGv.getBytes("expatriatesOfferLetter");
		   		is = new ByteArrayInputStream(content);
		   		expatriatesOfferLetterFileName=offerGv.getString("expatriatesOfferLetterFileName");
		   }catch(Exception e){
			   try{
	    		   Messagebox.show("The contains of file is corrupted or not supported","Error",1,null);
	    		   return;
	    	   }catch(Exception e1){
	    		   e1.printStackTrace();
	    	   }
	    	   e.printStackTrace();
		   }
		   		
	   		}else{
	   			if(offerGv.getBytes("expatriatesOfferLetter")==null || offerGv.getString("expatriatesOfferLetterFileName")==null){
	   			 Messagebox.show("Please select file from browse","Error", 1, null);
					return;
	   			}
		   		byte[]content=offerGv.getBytes("expatriatesOfferLetter");
		   		is = new ByteArrayInputStream(content);
		   		expatriatesOfferLetterFileName=offerGv.getString("expatriatesOfferLetterFileName");
		   }
	   	Filedownload.save(is,"application/pdf",expatriatesOfferLetterFileName+".pdf");
	   	return;
}
	 
   private  InputStream convertToPDF(byte[] content) throws Exception{
	   Docx4jProperties.getProperties().setProperty("docx4j.Log4j.Configurator.disabled", "true");
	   Log4jConfigurator.configure();            
	   org.docx4j.convert.out.pdf.viaXSLFO.Conversion.log.setLevel(Level.OFF);
	   InputStream iStream=null;
    	   String fileName=this.mediaFile.getName();
           InputStream is = null;
           is = new ByteArrayInputStream(content);
         
           WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);
           PdfSettings pdfSettings = new PdfSettings();
           File tmpFile = File.createTempFile(fileName,".pdf");
           OutputStream out = new FileOutputStream(tmpFile);
           PdfConversion converter = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion( wordMLPackage);
           converter.output(out, pdfSettings);
           iStream = new FileInputStream(tmpFile);
         
       return iStream;
   }


}    
	    
	    

