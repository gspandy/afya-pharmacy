package com.ndz.controller;

import com.ndz.zkoss.UtilService;
import com.ndz.zkoss.util.HrmsInfrastructure;
import javolution.util.FastMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

public class CompanyInformation extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	private static String salaryAttribute = new String();
	private String selectedCurrency;

	public CompanyInformation() {
		super();
	}

	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		GenericValue companyGV = null;
		try {
			companyGV = delegator.findOne("PartyGroup", false, org.ofbiz.base.util.UtilMisc.toMap("partyId", "Company"));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		selectedCurrency = companyGV.getString("currencyType");
		return super.doBeforeCompose(page, parent, compInfo);
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if ("searchPanel".equals(comp.getId())) {
			populateGrid(comp);
		}
	}

	public String getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(String selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}

	private void populateGrid(Component comp) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition Condition = EntityCondition.makeCondition("partyId", "Company");
		List<GenericValue> partyGroupList = delegator.findList("PartyGroup", Condition, null, null, null, false);
		SimpleListModel partyGroupSimpleListModel = new SimpleListModel(partyGroupList);
		Grid gridPerformanceReview = (Grid) comp.getFellow("dataGrid");
		gridPerformanceReview.setModel(partyGroupSimpleListModel);

	}

	public void editCompany(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException,
			GenericEntityException {

		final Window win = (Window) Executions.createComponents("/zul/companyLogo/editCompanyInformation.zul", null, null);
		win.doModal();
		Textbox txtBoxemployeeId = (Textbox) win.getFellow("txtBoxemployeeId");
		txtBoxemployeeId.setValue(gv.getString("partyId"));
		Textbox txtBoxCompanyName = (Textbox) win.getFellow("txtBoxCompanyName");
		txtBoxCompanyName.setValue(gv.getString("groupName"));
		Textbox txtBoxImageUrl = (Textbox) win.getFellow("txtBoxImageUrl");
		txtBoxImageUrl.setValue(gv.getString("logoImageUrl"));
	}

	@SuppressWarnings("deprecation")
	public void onClick$buttonSave(Event event) throws GenericServiceException, GenericEntityException {
		Component comp = event.getTarget();

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		String companyName = (String) ((Textbox) comp.getFellow("companyName")).getValue();
		String officeSiteName = (String) ((Textbox) comp.getFellow("officeSiteName")).getValue();
		String annualRevenueString = (String) ((Textbox) comp.getFellow("annualRevenue")).getValue();
		BigDecimal annualRevenue = new BigDecimal(annualRevenueString);
		String numberOfEmployeesString = (String) ((Textbox) comp.getFellow("numberOfEmployees")).getValue();
		String employerId = (String) ((Textbox) comp.getFellow("employerId")).getValue();
		String agentId = (String) ((Textbox) comp.getFellow("agentId")).getValue();
		Long numberOfEmployees = new Long(numberOfEmployeesString);
		String address1 = (String) ((Textbox) comp.getFellow("address1")).getValue();
		String address2 = (String) ((Textbox) comp.getFellow("address2")).getValue();
		String countryGeoId = (String) ((Listbox) comp.getFellow("countryGeoId")).getSelectedItem().getValue();
		String stateProvinceGeoId = (String) ((Listbox) comp.getFellow("stateProvinceGeoId")).getSelectedItem().getValue();
		String postalCode = (String) ((Textbox) comp.getFellow("postalCode")).getValue();
		String city = (String) ((Textbox) comp.getFellow("city")).getValue();
		String countryCode = (String) ((Textbox) comp.getFellow("countryCode")).getValue();
		String areaCode = (String) ((Textbox) comp.getFellow("areaCode")).getValue();
		String phoneNumber = (String) ((Textbox) comp.getFellow("phoneNumber")).getValue();
		String emailAddress = (String) ((Textbox) comp.getFellow("emailAddress")).getValue();

	
		
		Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "groupName", companyName, "officeSiteName", officeSiteName, "annualRevenue",
				annualRevenue, "numEmployees", numberOfEmployees,"employerId",employerId,"agentId",agentId);
		
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		Map<String, Object> resultPartyId = dispatcher.runSync("createPartyGroup", context);
		String partyId = (String) resultPartyId.get("partyId");
		Map<String, Object> contextPostalAddress = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "address1", address1, "city", city, "postalCode",
				postalCode, "address2", address2, "countryGeoId", countryGeoId, "stateProvinceGeoId", stateProvinceGeoId, "contactMechPurposeTypeId",
				"GENERAL_LOCATION");
		Map<String, Object> resultContactMech = dispatcher.runSync("createPartyPostalAddress", contextPostalAddress);
		String contactMechId = (String) resultContactMech.get("contactMechId");
		Map<String, Object> contextPhoneNumber = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "CountryCode", countryCode, "areaCode",
				areaCode, "contactNumber", phoneNumber, "contactMechPurposeTypeId", "PRIMARY_PHONE");
		dispatcher.runSync("createPartyTelecomNumber", contextPhoneNumber);
		Map<String, Object> contextEmailAddress = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "emailAddress", emailAddress,
				"contactMechPurposeTypeId", "EMAIL_ADDRESS");
		dispatcher.runSync("createPartyEmailAddress", contextEmailAddress);
	}

	public static void updateCompanyDetails(Event event) throws InterruptedException {

		try {
			Map<String, Object> result = null;
			Component editCompanyInformation = event.getTarget();

			result = UtilService.runService("updatePartyGroup", event);
			Messagebox.show("Company Details  Updated", "Success", 1, null);
			UtilService.runService("updatePartyPostalAddress", event);
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			String emailAddress = (String) ((Textbox) editCompanyInformation.getFellow("emailAddress")).getValue();
			String emailContactMechId = (String) ((Label) editCompanyInformation.getFellow("emailContactMechId")).getValue();
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map updateEmailAddress = null;
			updateEmailAddress = UtilMisc.toMap("userLogin", userLogin, "contactMechId", emailContactMechId, "emailAddress", emailAddress);
			dispatcher.runSync("updatePartyEmailAddress", updateEmailAddress);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Media uploadedFile;
	static Properties pathProperties = UtilProperties.getProperties("easyHrmsPath.properties");
	private static final String UPLOADPATH = (String) pathProperties.get("companyLogoImagePath");

	public void setUploadedFile(UploadEvent uploadEvent) throws InterruptedException {
		this.uploadedFile = uploadEvent.getMedia();
		Messagebox.show("Image Uploaded Sucessfully", "success", 1, null);
	}

	@SuppressWarnings("unused")
	public void companyInformationEdit(Event event, String groupId) throws GenericEntityException, IOException, InterruptedException {
		Component comp = event.getTarget();
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		try {
			Map<String, Object> result = null;
			result = UtilService.runService("updatePartyGroup", event);
			String fileName = uploadedFile == null ? null : (uploadedFile.getName());
			Listitem currencyType = ((Listbox) comp.getFellow("allocateCurrencyType")).getSelectedItem();
			String currency = (String) currencyType.getValue();
			if (fileName != null) {
				String actualFileName = null;
				String extention = fileName.substring(fileName.lastIndexOf('.'));
				if (fileName != null) {
					if (extention.equals(".jpg") || extention.equals(".bmp") || extention.equals(".png") || extention.equals(".gif")
							|| extention.equals(".JPG") || extention.equals(".BMP") || extention.equals(".PNG") || extention.equals(".GIF")
							|| extention.equals("jpeg")) {
						/* actualFileName = UPLOADPATH + "companyLogo.jpg"; */

						if (StringUtils.isNotEmpty(System.getProperty("ofbiz.home"))) {
							actualFileName = ((String) System.getProperty("ofbiz.home")).concat("/hot-deploy/hrms/webapp/hrms/images/").concat(
									uploadedFile.getName());
						} else {
							actualFileName = UPLOADPATH + uploadedFile.getName();
						}
						File diskFile = new File(actualFileName);
						if (!diskFile.exists())
							diskFile.createNewFile();
						FileOutputStream outputStream = new FileOutputStream(diskFile);
						IOUtils.copyLarge(uploadedFile.getStreamData(), outputStream);
					}
				} else {
					Messagebox.show("Please Provide A Image File", "Warning", 1, null);
				}
				// GenericValue storeGV = delegator.makeValue("PartyGroup",
				// "partyId", "Company", "logoImageUrl",
				// fileName,"currencyType",currency);
				// storeGV.store();
			}
			GenericValue storeGV = delegator.makeValue("PartyGroup", "partyId", "Company", "logoImageUrl", fileName, "currencyType", currency);
			storeGV.store();
			Comboitem cmb = (Comboitem) (((Combobox) comp.getFellow("salaryFrequencyCombobox")).getSelectedItem());
			if (cmb != null) {
				String salaryAttribute = (String) (cmb).getValue();
				if (UtilValidate.isNotEmpty(salaryAttribute)) {
					GenericValue gv = delegator.makeValue("PartyAttribute",
							UtilMisc.toMap("partyId", groupId, "attrName", "SALARY_FREQUENCY", "attrValue", salaryAttribute));
					delegator.createOrStore(gv);
				}
			}
			String mgrViewSubOrdinatePayroll = (String) ((Combobox) comp.getFellow("mgrViewSubOrdinatePayroll")).getValue();
			GenericValue gv = delegator.makeValue("PartyAttribute",
					UtilMisc.toMap("partyId", groupId, "attrName", "MGRVIEW_SUBORDINATE_PAYROLL", "attrValue", mgrViewSubOrdinatePayroll));
			delegator.createOrStore(gv);
			Events.postEvent(
					"onClick",
					event.getTarget().getFellow("companyProfileEdit").getParent().getFellow("companyWindow").getParent().getFellow("companyMain")
							.getFellow("companyInformation"), null);
			Messagebox.show("Profile Updated Successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void showUpdatePostalAddressWindow(String contactMechId) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

		GenericValue postalAddress = null;
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		postalAddress = delegator.findByPrimaryKey("PostalAddress", org.ofbiz.base.util.UtilMisc.toMap("contactMechId", contactMechId));

		EntityCondition countryCondition = EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "COUNTRY");
		EntityCondition stateCondition = EntityCondition.makeCondition("geoIdFrom", EntityOperator.EQUALS, postalAddress.getString("countryGeoId"));
		Set<String> fieldToSelect = new HashSet<String>();
		fieldToSelect.add("geoName");
		fieldToSelect.add("geoId");

		List<GenericValue> countries = delegator.findList("Geo", countryCondition, fieldToSelect, null, null, false);

		List<GenericValue> states = delegator.findList("GeoAssocAndGeoTo", stateCondition, fieldToSelect, null, null, false);
		Map<String, String> arg = new HashMap<String, String>();
		arg.put("contactMechId", contactMechId);
		arg.put("stateProvinceGeoId", postalAddress.getString("stateProvinceGeoId"));
		arg.put("countryGeoId", postalAddress.getString("countryGeoId"));
		Window win = (Window) Executions.createComponents("/zul/company/editPostalAddress.zul", null, arg);

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

		Listbox savedCountryType = (Listbox) win.getFellow("countryGeoId");

		for (int i = 1; i < countries.size(); i++) {
			GenericValue country = countries.get(i);
			String itemLabel = country.getString("geoId");
			// savedCountryType.appendItemApi(country.getString("geoName"),
			// itemLabel);
			if (itemLabel.equals(postalAddress.getString("countryGeoId"))) {
				savedCountryType.setSelectedIndex(i + 1);
			}
		}

		Listbox savedStateType = (Listbox) win.getFellow("stateProvinceGeoId");

		// for (int i = 1; i < states.size(); i++) {
		// GenericValue state = states.get(i);
		// String itemLabel = state.getString("geoId");
		// savedStateType.appendItemApi(state.getString("geoId"), itemLabel);
		// if (itemLabel.equals(postalAddress.getString("stateProvinceGeoId")))
		// {
		// savedStateType.setSelectedIndex(i);
		// }
		// }

		Textbox postalCodeBox = (Textbox) win.getFellow("postalCode");
		postalCodeBox.setValue(postalAddress.getString("postalCode"));

		win.doModal();

	}

	public static void showUpdateEmailAddressWindow(String contactMechId) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

		GenericValue emailAddress = null;
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		emailAddress = delegator.findByPrimaryKey("ContactMech", org.ofbiz.base.util.UtilMisc.toMap("contactMechId", contactMechId));

		Window win = (Window) Executions.createComponents("/zul/company/editEmailAdd.zul", null, null);

		Textbox oldContactMechId = (Textbox) win.getFellow("oldContactMechId");
		oldContactMechId.setValue(contactMechId);
		Textbox contactMechIdBox = (Textbox) win.getFellow("contactMechId");
		contactMechIdBox.setValue(contactMechId);

		Textbox contactMechTypeId = (Textbox) win.getFellow("contactMechTypeId");
		contactMechTypeId.setValue(emailAddress.getString("contactMechTypeId"));

		Textbox infoString = (Textbox) win.getFellow("emailAddress");
		infoString.setValue(emailAddress.getString("infoString"));

		win.doModal();

	}

	public static void updateEmailAddress(Event event) throws GenericEntityException, InterruptedException {
		try {
			Map<String, Object> result = null;
			result = UtilService.runService("updatePartyEmailAddress", event);
			Messagebox.show("Email Address Updated", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void showUpdateTelecomNumberWindow(String contactMechId, String partyId) throws SuspendNotAllowedException, InterruptedException,
			GenericEntityException {

		GenericValue contactNumber = null;
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		contactNumber = delegator.findByPrimaryKey("TelecomNumber", org.ofbiz.base.util.UtilMisc.toMap("contactMechId", contactMechId));
		Map<String, String> arg = new HashMap<String, String>();
		arg.put("partyId", partyId);
		arg.put("contactMechId", contactMechId);
		Window win = (Window) Executions.createComponents("/zul/company/editPhoneNumber.zul", null, arg);

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

	public static void updatePhoneNumber(Event event) throws InterruptedException {

		try {
			UtilService.runService("updatePartyTelecomNumber", event);
			Messagebox.show("Contact Number Updated", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void createNewPostalAddress(Event event) throws InterruptedException {

		try {
			UtilService.runService("createPartyPostalAddress", event);
			Messagebox.show("Saved Successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public static void createNewPhoneNumber(Event event) throws InterruptedException {

		try {
			UtilService.runService("createPartyTelecomNumber", event);
			Messagebox.show("Saved Successfully", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addPartyAttribute(Event event, Comboitem comboItem, String groupId) throws GenericEntityException {

		salaryAttribute = (String) comboItem.getValue();
	}

	public static void image(UploadEvent uploadEvent) throws GenericServiceException, SuspendNotAllowedException, InterruptedException, IOException,
			GenericEntityException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String partyId = userLogin.getString("partyId");
		int FILE_SIZE = 1000;
		String uploadImagePath = "../images/";
		String uploadImagePathForStore = "./hot-deploy/hrms/webapp/hrms/images/";
		Media uploadedimage = uploadEvent.getMedia();

		// Image imageUpload = (Image)uploadEvent.getTarget();
		// imageUpload.setContent((AImage)uploadedimage);

		String fileName = uploadedimage == null ? null : (uploadImagePath + uploadedimage.getName());
		String extention = fileName.substring(fileName.lastIndexOf('.'));
		if (uploadedimage.getByteData().length < FILE_SIZE * 1024) {
			if (extention.equals(".jpg") || extention.equals(".bmp") || extention.equals(".png") || extention.equals(".gif") || extention.equals(".JPG")
					|| extention.equals(".BMP") || extention.equals(".PNG") || extention.equals(".GIF")) {
				String actualFileName = uploadImagePath + partyId + ".jpg";
				Map<String, Object> rawValue = FastMap.newInstance();
				GenericValue storeValue = null;
				rawValue.putAll(UtilMisc.toMap("partyId", partyId, "imageUrl", actualFileName));
				storeValue = (GenericValue) delegator.makeValue("Person", rawValue);
				delegator.store(storeValue);
				actualFileName = uploadImagePathForStore + partyId + ".jpg";
				File diskFile = new File(actualFileName);
				FileOutputStream outputStream = new FileOutputStream(diskFile);
				outputStream.write(uploadedimage.getByteData());
				Executions.sendRedirect("/control/profile?partyId=" + partyId);
			} else {
				Messagebox.show("Please Provide A Image File", "Warning", 1, null);
			}
		} else {
			Messagebox.show("Image Must Be Less Then 1MB", "Warning", 1, null);
		}

	}

	public static void removeImage(Event event) throws GenericServiceException, SuspendNotAllowedException, InterruptedException, IOException,
			GenericEntityException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String partyId = userLogin.getString("partyId");

		Map<String, Object> rawValue = FastMap.newInstance();
		GenericValue storeValue = null;
		rawValue.putAll(UtilMisc.toMap("partyId", partyId, "imageUrl", null));
		storeValue = (GenericValue) delegator.makeValue("Person", rawValue);
		delegator.store(storeValue);
		Executions.sendRedirect("/control/profile?partyId=" + partyId);

	}

	public static void setCustomSequence(Event event) throws GenericEntityException, InterruptedException {
		Component companyProfileEdit = event.getTarget();
		companyProfileEdit.getDesktop().getExecution().getNativeRequest();
		String prospectPrefix = (String) ((Textbox) companyProfileEdit.getFellow("prospectPrefix")).getValue();
		String prospectSuffix = (String) ((Textbox) companyProfileEdit.getFellow("prospectSuffix")).getValue();
		String prospectStartValue = (String) ((Textbox) companyProfileEdit.getFellow("prospectStartValue")).getValue();
		//String employeePrefix = (String) ((Textbox) companyProfileEdit.getFellow("employeePrefix")).getValue();
		//String employeeSuffix = (String) ((Textbox) companyProfileEdit.getFellow("employeeSuffix")).getValue();
		String employeeStartValue = (String) ((Textbox) companyProfileEdit.getFellow("employeeStartValue")).getValue();
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        if(UtilValidate.isNotEmpty(prospectPrefix)){
            Long prospectStartValueLong = Long.parseLong(prospectStartValue);
            GenericValue gv1 = delegator.makeValue("SequenceValueItem",
                    UtilMisc.toMap("seqName", "PROSPECT", "seqId", prospectStartValueLong, "seqPrefix", prospectPrefix, "suffix", prospectSuffix));
            delegator.createOrStore(gv1);
        }else{
            delegator.removeByAnd("SequenceValueItem", UtilMisc.toMap("seqName", "PROSPECT"));
        }
        if(UtilValidate.isNotEmpty(employeeStartValue)){
            Long employeeStartValueLong = Long.parseLong(employeeStartValue);
            GenericValue gv2 = delegator.makeValue("SequenceValueItem",
				UtilMisc.toMap("seqName", "EMPLOYEE", "seqId", employeeStartValueLong));
		delegator.createOrStore(gv2);
		}else{
			delegator.removeByAnd("SequenceValueItem", UtilMisc.toMap("seqName", "EMPLOYEE"));
		}
	}

	public static void saveCompanyFiscalYear(Event event) throws GenericEntityException, InterruptedException {
		Component companyProfileNew = event.getTarget();
		String fiscalName = (String) ((Textbox) companyProfileNew.getFellow("PeriodName")).getValue();
		java.util.Date fromDate = ((Datebox) companyProfileNew.getFellow("FromDate")).getValue();
		java.util.Date thruDate = ((Datebox) companyProfileNew.getFellow("ThruDate")).getValue();
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Date fromD = new Date(fromDate.getTime());
		Date thruD = new Date(thruDate.getTime());
		String customTimePeriodId = delegator.getNextSeqId("CustomTimePeriodId");
		GenericValue gv = delegator.create("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "fromDate", fromD, "thruDate", thruD,
				"periodName", fiscalName, "periodTypeId", "FISCAL_YEAR"));
		gv.store();
		Messagebox.show("Fiscal Year Successfully Added", "Success", 1, null);
	}

	public static void editCompanyFiscalYear(Event event) throws GenericEntityException, InterruptedException {
		Component companyEditNew = event.getTarget();
		String id = (String) ((Label) companyEditNew.getFellow("customtypeid")).getValue();
		String fiscalName = (String) ((Textbox) companyEditNew.getFellow("PeriodName")).getValue();
		java.util.Date fromDate = ((Datebox) companyEditNew.getFellow("FromDate")).getValue();
		java.util.Date thruDate = ((Datebox) companyEditNew.getFellow("ThruDate")).getValue();
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Date fromD = new Date(fromDate.getTime());
        Date thruD = new Date(thruDate.getTime());
		try {
			GenericValue fiscalGV = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", id), false);
			fiscalGV.put("periodTypeId", "FISCAL_YEAR");
			fiscalGV.put("periodName", fiscalName);
			fiscalGV.put("fromDate", fromD);
			fiscalGV.put("thruDate", thruD);
			fiscalGV.store();
			Messagebox.show("Fiscal Year Updated Successfully", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
