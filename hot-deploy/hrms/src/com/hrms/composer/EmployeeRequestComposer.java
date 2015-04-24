package com.hrms.composer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.security.Security;
import org.ofbiz.webapp.control.Infrastructure;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class EmployeeRequestComposer extends GenericForwardComposer {

    private static final long serialVersionUID = 1L;

    AnnotateDataBinder binder = null;
    Window asignWindow = null;
    Listbox requestTypeListbox = null;
    BindingListModel model;
    List requestTypeList = new ArrayList();
    boolean isAdmin = false;
    GenericDelegator delegator = null;
    String employeeId = null;
    Toolbarbutton toolbarButton = null;

    public Toolbarbutton getToolbarButton() {
        return toolbarButton;
    }

    public void setToolbarButton(Toolbarbutton toolbarButton) {
        this.toolbarButton = toolbarButton;
    }

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        binder = new AnnotateDataBinder(comp);
        asignWindow = (Window) comp;
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        this.employeeId = (String) userLogin.getString("partyId");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//Infrastructure.getDelegator();
        this.requestTypeList = getRequsetTypeToAssign(delegator, employeeId);
        binder.loadAttribute(requestTypeListbox, "model");
    }

    public boolean checkForAdmin() {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        org.ofbiz.security.Security security = (Security) requestScope.get("security");
        isAdmin = security.hasPermission("HUMANRES_ADMIN", userLogin);
        return isAdmin;
    }

    public BindingListModel getModel() {
        return new BindingListModelList(this.requestTypeList, false);
    }

    protected List<GenericValue> getRequsetTypeToAssign(GenericDelegator delegator, String employeeId) throws GenericEntityException {

        List<GenericValue> requestTypesForAssign = new ArrayList<GenericValue>();
        try {
            isAdmin = checkForAdmin();
            if (isAdmin) {
                requestTypesForAssign = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("statusId", "REQUEST_SUBMITTED"));
            } else {
                requestTypesForAssign = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("partyId", employeeId));
                //requestTypesForAssign = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("asignedPartyId", employeeId));
            }
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return requestTypesForAssign;

    }

    public void assignRequestToEmployee(Set<Listitem> selectedItems, String asignedPartyId) throws InterruptedException {
        String requestId = null;
        for (Listitem li : selectedItems) {
            GenericValue requestType = ((GenericValue) li.getValue());
            GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            String employeeId = (String) userLogin.getString("partyId");
            try {
                delegator.storeByCondition("PartyRequestType", UtilMisc.toMap("asignedPartyId", asignedPartyId, "assignedBy", employeeId),
                        EntityCondition.makeCondition("requestTypeId", requestType.getString("requestTypeId")));
                if (requestType != null)
                    requestId = (String) requestType.getString("requestId");
                delegator.create("PartyReqResponse", UtilMisc.toMap("responseId", delegator.getNextSeqId("PartyReqResponse"), "requestId",
                        requestId, "updatedBy", employeeId, "statusTypeId", "REQUEST_ASSIGN"));
            } catch (GenericEntityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Messagebox.show("Request Assigned successfully", "Success", 1, null);

    }

    public void onClick$assignButton() throws GenericEntityException, InterruptedException {

        Set<Listitem> items = requestTypeListbox.getSelectedItems();
        if (items.isEmpty()) {
            try {
                Messagebox.show("Select a Request Type", "Error", 1, null);
                return;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Window window = (Window) Executions.createComponents("/zul/employeeRequest/assignedEmployeeWindow.zul", null, UtilMisc.toMap("selectedRequest", items, "toolbarButton", toolbarButton));
        window.doModal();

    }

    public void onClick$searchByRequestType() throws GenericEntityException {

        Listitem requestTypeItem = (Listitem) ((Listbox) asignWindow.getFellow("requestTypeIdListBox")).getSelectedItem();
        String requestType = null;
        if (requestTypeItem != null) {
            requestType = (String) requestTypeItem.getValue();
        }
        if (isAdmin) {
            if (requestTypeItem != null) {
                this.requestTypeList = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("requestTypeId", requestType));
            } else {
                this.requestTypeList = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("statusId", "REQUEST_SUBMITTED"));
            }
        } else {
            if (requestTypeItem != null) {
                this.requestTypeList = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("requestTypeId", requestType, "partyId",
                        employeeId));
            } else {
                this.requestTypeList = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("statusId", "REQUEST_SUBMITTED", "partyId",
                        employeeId));
            }
        }
//		binder.loadAttribute(requestTypeListbox, "model");
    }


    public List getRequestTypeList() {
        return requestTypeList;
    }

    public ListitemRenderer getRenderer() {
        return new ListitemRenderer() {
            public void render(Listitem listItem, Object data) throws Exception {
                final GenericValue requestTypeGv = (GenericValue) data;

                isAdmin = checkForAdmin();
                EntityCondition condn = EntityCondition.makeCondition("requestId", EntityOperator.EQUALS, requestTypeGv.getString("requestId"));
                List<GenericValue> partyReqResponseList = delegator.findList("PartyReqResponse", condn, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
                String statusTypeId = new String();
                statusTypeId = partyReqResponseList.get(0) == null ? new String() : partyReqResponseList.get(0).getString("statusTypeId");
                if (isAdmin) {
                    listItem.setTooltiptext("Double Click To View/Respond");
                    new Listcell(requestTypeGv.getString("requestId")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("firstName") + " " + requestTypeGv.getString("lastName")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("description")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("requestMsg")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("requestDescription")).setParent(listItem);

                    //new Listcell(requestTypeGv.getString("statusId")).setParent(listItem);
                    GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
                    GenericValue priorityGv = delegator.findByPrimaryKey("PriorityType", UtilMisc.toMap("priorityTypeId", requestTypeGv
                            .getString("priority")));
                    String priorityType = null;
                    if (priorityGv != null)
                        priorityType = (String) priorityGv.getString("description");
                    if (priorityType != null)
                        new Listcell(priorityType).setParent(listItem);
                    else
                        new Listcell(" ").setParent(listItem);

                    String assignedPartyId = requestTypeGv.getString("asignedPartyId");
                    String asignedTo = HrmsUtil.getFullName(delegator, assignedPartyId);
                    if (assignedPartyId != null)
                        new Listcell(asignedTo).setParent(listItem);
                    else
                        new Listcell("").setParent(listItem);
                    if (partyReqResponseList != null) {
                        if (statusTypeId.equals("REQUEST_REOPENED"))
                            new Listcell("Reopened").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_RESOLVED"))
                            new Listcell("Resolved").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_WITHDRAWN"))
                            new Listcell("Withdrawn").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_CLOSED"))
                            new Listcell("Closed").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_SUBMITTED"))
                            new Listcell("Submitted").setParent(listItem);
                    } else {
                        new Listcell("").setParent(listItem);
                    }
                    listItem.addEventListener("onDoubleClick", new EventListener() {
                        public void onEvent(Event event) throws Exception {
                            Clients.evalJavaScript("window.open('/hrms/control/viewRequest?requestId="
                                    + requestTypeGv.getString("requestId") + "')");
                        }
                    });

                } else {
                    listItem.setTooltiptext("Double Click To View/Respond");
                    new Listcell(requestTypeGv.getString("requestId")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("firstName") + " " + requestTypeGv.getString("lastName")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("description")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("requestMsg")).setParent(listItem);
                    new Listcell(requestTypeGv.getString("requestDescription")).setParent(listItem);
                    GenericValue priorityGv = delegator.findByPrimaryKey("PriorityType", UtilMisc.toMap("priorityTypeId", requestTypeGv
                            .getString("priority")));

                    String priorityType = null;
                    if (priorityGv != null)
                        priorityType = (String) priorityGv.getString("description");
                    if (priorityType != null)
                        new Listcell(priorityType).setParent(listItem);
                    else
                        new Listcell(" ").setParent(listItem);

                    String assignedPartyId = requestTypeGv.getString("asignedPartyId");
                    String asignedTo = HrmsUtil.getFullName(delegator, assignedPartyId);

                    if (assignedPartyId != null)
                        new Listcell(asignedTo).setParent(listItem);
                    else
                        new Listcell("").setParent(listItem);
                    if (partyReqResponseList != null) {
                        if (statusTypeId.equals("REQUEST_REOPENED"))
                            new Listcell("Reopened").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_RESOLVED"))
                            new Listcell("Resolved").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_WITHDRAWN"))
                            new Listcell("Withdrawn").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_CLOSED"))
                            new Listcell("Closed").setParent(listItem);
                        if (statusTypeId.equals("REQUEST_SUBMITTED"))
                            new Listcell("Submitted").setParent(listItem);

                    } else
                        new Listcell("").setParent(listItem);
                    listItem.addEventListener("onDoubleClick", new EventListener() {
                        public void onEvent(Event event) throws Exception {
                            Clients.evalJavaScript("window.open('/hrms/control/viewRequest?requestId="
                                    + requestTypeGv.getString("requestId") + "')");
                        }
                    });
                }
                listItem.setValue(data);
            }
        };

    }

    public static void createRequestType(Event event) throws GenericEntityException, InterruptedException {

        Component createRequsetTypeWindow = event.getTarget();

        String requsetTypeId = (String) ((Textbox) createRequsetTypeWindow.getFellow("requestTypeIdTextBox")).getValue();
        String description = (String) ((Textbox) createRequsetTypeWindow.getFellow("descriptionTextBox")).getValue();
        String purpose = (String) ((Textbox) createRequsetTypeWindow.getFellow("purposeTextBox")).getValue();
        Comboitem priorityItem = (Comboitem) ((Combobox) createRequsetTypeWindow.getFellow("priorityListBox")).getSelectedItem();
        String priority = (String) priorityItem.getValue();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        try {
            delegator.create("PartyRequestType", UtilMisc.toMap("requestTypeId", requsetTypeId, "description", description, "priority",
                    priority, "requestTypePurpose", purpose));
        } catch (GenericEntityException e) {
            Messagebox.show("Request Type already exists;cannot create new record", "Error", 1, null);
            return;
        }

        Messagebox.show("Request Type Created", Labels.getLabel("HRMS_SUCCESS"), 1, null);
        createRequsetTypeWindow.detach();
    }

    public static void editRequestType(Event event) throws GenericEntityException, InterruptedException {

        Component editRequsetTypeWindow = event.getTarget();
        editRequsetTypeWindow.getDesktop().getExecution().getNativeRequest();

        String requsetTypeId = (String) ((Textbox) editRequsetTypeWindow.getFellow("requestTypeIdTextBox")).getValue();
        String description = (String) ((Textbox) editRequsetTypeWindow.getFellow("descriptionTextBox")).getValue();
        String purpose = (String) ((Textbox) editRequsetTypeWindow.getFellow("purposeTextBox")).getValue();
        Listitem priorityItem = (Listitem) ((Listbox) editRequsetTypeWindow.getFellow("priorityListBox")).getSelectedItem();
        String priority = (String) priorityItem.getValue();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        GenericValue requestTypeGv = delegator.makeValue("PartyRequestType", UtilMisc.toMap("requestTypeId", requsetTypeId, "description",
                description, "priority", priority, "requestTypePurpose", purpose));
        delegator.createOrStore(requestTypeGv);

        Messagebox.show("Request Type Updated", Labels.getLabel("HRMS_SUCCESS"), 1, null);
    }

    public static void deleteRequestType(Event event, GenericValue gv, final Button btn) throws InterruptedException {
        final Component searchPanel = event.getTarget();
        final String requestTypeId = gv.getString("requestTypeId");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

        Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {

                            try {
                                List requestList = delegator.findByAnd("PartyRequest", UtilMisc.toMap("requestTypeId", requestTypeId));
                                if (requestList.size() > 0)
                                    try {
                                        Messagebox.show("Cannot be Deleted;Request Type is in use", "Error", 1, null);
                                        return;
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                delegator.removeByAnd("PartyRequestType", UtilMisc.toMap("requestTypeId", requestTypeId));
                                Events.postEvent("onClick", btn, null);

                            } catch (GenericEntityException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            try {
                                Messagebox.show("Request Type deleted successfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
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

    public void postPartyRequest(Event event) throws GenericEntityException, InterruptedException, IOException {
        Component postRequestWindow = event.getTarget();

        Listitem requestTypeIdItem = (Listitem) ((Listbox) postRequestWindow.getFellow("requestTypeIdListBox")).getSelectedItem();
        String requestTypeId = (String) requestTypeIdItem.getValue();
        String requestMessage = (String) ((Textbox) postRequestWindow.getFellow("requestTextBox")).getValue();
        String requestDescription = (String) ((Textbox) postRequestWindow.getFellow("requestDescriptionTextBox")).getValue();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String employeeId = (String) userLogin.getString("partyId");
        String requestId = delegator.getNextSeqId("PartyRequest");
        String statusTypeId = "REQUEST_SUBMITTED";
        delegator.create("PartyRequest", UtilMisc.toMap("requestId", requestId, "partyId", employeeId, "requestMsg", requestMessage,
                "requestTypeId", requestTypeId, "requestDescription", requestDescription, "statusId", statusTypeId));
        createPartyRequestResponse(delegator, requestId, employeeId, requestMessage, statusTypeId);
        Messagebox.show("Request Submitted", Labels.getLabel("HRMS_SUCCESS"), 1, null);

    }

    private Media uploadedFile;

    private static final String UPLOADPATH = "./hot-deploy/hrms/webapp/hrms/requestAttachments/";

    public void setUploadedFile(UploadEvent uploadEvent, Component component) {
        this.uploadedFile = uploadEvent.getMedia();
        String name = this.uploadedFile.getName();
        ((Label) component).setValue(name + " " + "Attachment Uploaded");
    }

    public void createPartyRequestResponse(GenericDelegator delegator, String requestId, String partyId, String responseMessage,
                                           String statusTypeId) throws GenericEntityException, IOException {
        String fileName = writeFileToDisk(requestId);
        delegator.create("PartyReqResponse", UtilMisc.toMap("responseId", delegator.getNextSeqId("PartyReqResponse"), "requestId",
                requestId, "updatedBy", partyId, "responseMessage", responseMessage, "attachemntName", fileName, "statusTypeId",
                statusTypeId));
    }

    public void createRequestResponse(Event event, GenericDelegator delegator, String requestId, String partyId,
                                      String responseMessage, String statusTypeId) throws GenericEntityException, IOException {
        String fileName = writeFileToDisk(requestId);
        delegator.create("PartyReqResponse", UtilMisc.toMap("responseId", delegator.getNextSeqId("PartyReqResponse"), "requestId",
                requestId, "updatedBy", partyId, "responseMessage", responseMessage, "attachemntName", fileName, "statusTypeId",
                statusTypeId));
    }

    public String writeFileToDisk(String requestId) throws IOException {
        if (uploadedFile == null)
            return null;
        String fileName = uploadedFile == null ? null : (UPLOADPATH + uploadedFile.getName());
        String actualFileName = null;
        String extention = null;
        if (fileName != null) {
            extention = fileName.substring(fileName.lastIndexOf('.'));
            actualFileName = UPLOADPATH + requestId + extention;
            File diskFile = new File(actualFileName);
            FileOutputStream outputStream = new FileOutputStream(diskFile);
            if (uploadedFile.isBinary())
                IOUtils.copyLarge(uploadedFile.getStreamData(), outputStream);
            else {
                outputStream.write(IOUtils.toByteArray(uploadedFile.getReaderData()));
            }
        }

        return uploadedFile.getName();
    }

    public static void downloadAttachment(Event event, String requestId) throws GenericEntityException, FileNotFoundException,
            InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

        List<GenericValue> partyRequestResponseList = delegator.findByAnd("PartyReqResponse", UtilMisc.toMap("requestId", requestId));
        String downloadFileNameWithPath = null;
        for (GenericValue partyResponseAttachemnt : partyRequestResponseList) {
            if (UtilValidate.isEmpty(partyResponseAttachemnt.getString("attachemntName"))) {
                Messagebox.show("No Attachment Found", "Warning", 1, null);
                return;
            }
            downloadFileNameWithPath = "./hot-deploy/hrms/webapp/hrms/requestAttachments/" + partyResponseAttachemnt.getString("attachemntName");
            break;
        }
        String actualFileName = null;
        if (downloadFileNameWithPath != null)
            actualFileName = UPLOADPATH + requestId + downloadFileNameWithPath.substring(downloadFileNameWithPath.lastIndexOf('.'));
        InputStream inputStream = null;
        if (actualFileName != null) {
            inputStream = new FileInputStream(actualFileName);
            Filedownload.save(inputStream, null, actualFileName);
        } else
            Messagebox.show("No Attachment Found", "Warning", 1, null);

    }

}
