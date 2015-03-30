package com.hrms.training.composer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.event.PagingEvent;

import com.hrms.composer.HrmsComposer;

public class TrainingComposer extends HrmsComposer {

    /**
     *
     */
    private static final long serialVersionUID = 1418269649076270562L;
    private GenericValue selectedTraining;
    private BindingListModel model;
    Listbox trainingListbox;
    private GenericValue trainingRequest;
    private long totalSize;
    private Listbox myTrainingsListBox;
    private String employeeId;
    boolean isAdmin = false;
    
    

    public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    private EntityCondition dateCondition = null;

    public TrainingComposer() {
    }

    public GenericValue getSelectedTraining() {
        return selectedTraining;
    }

    public void setSelectedTraining(GenericValue selectedTraining) {
        this.selectedTraining = selectedTraining;
    }

    public BindingListModel getModel() {
        return model;
    }

    public void setModel(BindingListModel model) {
        this.model = model;
    }

    public GenericValue getTrainingRequest() {
        return trainingRequest;
    }

    public void setTrainingRequest(GenericValue trainingRequest) {
        this.trainingRequest = trainingRequest;
    }

    public boolean checkForAdmin() {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        org.ofbiz.security.Security security = (Security) requestScope.get("security");
        isAdmin = security.hasPermission("HUMANRES_ADMIN", userLogin);
        return isAdmin;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void executeAfterCompose(Component comp) throws Exception {

        EntityListIterator iter = delegator.find("Training", dateCondition, null, null, UtilMisc.toList("-trainingId"), null);
        List<GenericValue> entities = iter.getPartialList(0, 10);
        this.model = new BindingListModelList(entities, false);
        binder.loadAttribute(trainingListbox, "model");
        iter.close();
        totalSize = delegator.findCountByCondition("Training", dateCondition, null, null);
    }

    public void onPaging$pageTraining(ForwardEvent event) throws Exception {
        boolean beganTransaction = TransactionUtil.begin();

        int activePage = ((PagingEvent) event.getOrigin()).getActivePage();
        EntityListIterator iter = delegator.find("Training", dateCondition, null, null, null, null);
        List<GenericValue> entities = iter.getPartialList(activePage * 10, 10);
        model = new BindingListModelList(entities, false);
        binder.loadAttribute(trainingListbox, "model");
        iter.close();
        TransactionUtil.commit(beganTransaction);
    }

    public List<GenericValue> getAllConfirmedAttendees() throws Exception {
        return delegator.findList("TrainingRequestView", EntityCondition.makeCondition(UtilMisc.toMap("trainingId", selectedTraining
                .getString("trainingId"), "statusId", "TRNG_CONFIRMED")), null, null, null, false);

    }

    public void onClick$newTraining(Event event) {
        selectedTraining = delegator.makeValue("Training");
    }

    public void onClick$saveTraining(Event event) throws Exception {
        String trainingId = delegator.getNextSeqId("Training");
        selectedTraining.put("trainingId", trainingId);
        selectedTraining.put("estimatedCompletionDate", new Timestamp(((Date) selectedTraining.get("estimatedCompletionDate")).getTime()));
        selectedTraining.put("estimatedStartDate", new Timestamp(((Date) selectedTraining.get("estimatedStartDate")).getTime()));
        selectedTraining.put("maxTrainees", Long.valueOf(selectedTraining.getInteger("maxTrainees")));
        delegator.createOrStore(selectedTraining);
        EntityListIterator iter = delegator.find("Training", dateCondition, null, null, null, null);
        this.model = new BindingListModelList(iter.getPartialList(0, 10), false);
        // trainingListbox =
        // event.getTarget().getParent().getFellowIfAny("trainingListbox",true);
        binder.loadAttribute(trainingListbox, "model");
        iter.close();
        Messagebox.show("Training Created Successfully", "Success", 1, null);
    }

    public void onClick$updateTraining(Event event) throws Exception {
        selectedTraining.put("estimatedStartDate", new Timestamp(((Date) selectedTraining.get("estimatedStartDate")).getTime()));
        selectedTraining.put("estimatedCompletionDate", new Timestamp(((Date) selectedTraining.get("estimatedCompletionDate")).getTime()));
        selectedTraining.put("maxTrainees",selectedTraining.getInteger("maxTrainees").longValue());
        delegator.store(selectedTraining);
        EntityListIterator iter = delegator.find("Training", dateCondition, null, null, null, null);
        List<GenericValue> entities = iter.getPartialList(0, 10);
        this.model = new BindingListModelList(entities, false);
        binder.loadAttribute(trainingListbox, "model");
        Messagebox.show("Training Updated Successfully", "Success", 1, null);
    }

    public void onClick$applyTraining(Event event) throws Exception {
        if (selectedTraining == null) {
            Messagebox.show("Please select a Training", "Error", 1, null);
            return;
        }
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");
        if(UtilValidate.isNotEmpty(employeeId))
        	partyId=employeeId;
        
        boolean isEmployeeTerminatedOrResigned=HumanResUtil.checkEmployeeTermination(partyId, delegator);
        if(isEmployeeTerminatedOrResigned){
        	 Messagebox.show("Terminated or Resigned employee can not apply for training", "Error", 1, null);
             return;
        }
        GenericValue mgrGV = HumanResUtil.getReportingMangerForParty(partyId, delegator);
        Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
    
        if (mgrGV == null && !isManager) {
            Messagebox.show("No Manager Assigned;Cannot be Applied", "Error", 1, null);
            return;

        }
        /*String mgrPartyId = null;
        if (mgrGV != null)
            mgrPartyId = mgrGV.getString("partyId");
*/
        String mgrPartyId = mgrGV != null ? mgrGV.getString("partyId"):partyId;
        String statudId = mgrGV != null ? "TRNG_REQUEST" : "TRNG_MGR_APPROVED";
          
        GenericValue trainingRequest = delegator.makeValidValue("TrainingRequest", UtilMisc.toMap("partyId", partyId, "trainingId",
                selectedTraining.get("trainingId"), "managerId", mgrPartyId, "statusId",statudId));
        try {
            delegator.create(trainingRequest);
            Messagebox.show("Training Applied Successfully", "Success", 1, null);
        } catch (GenericEntityException ge) {
            Messagebox.show("Already applied for the training", "Error", 1, null);
        }
    }

    public ListitemRenderer getListitemRenderer() {
        return new ListitemRenderer() {
            public void render(final Listitem trainingListitem, Object data) throws Exception {
                GenericValue gv = (GenericValue) data;
                isAdmin = checkForAdmin();
                if (!isAdmin && "TRNG_CANCELLED".equalsIgnoreCase(gv.getString("statusId"))){
                	trainingListitem.setVisible(false);
                	return;
                }
                String trainingId = gv.getString("trainingId");
                new Listcell(gv.getString("trainingId")).setParent(trainingListitem);
                new Listcell(gv.getString("trainingName")).setParent(trainingListitem);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                new Listcell(formatter.format(gv.getTimestamp("estimatedStartDate"))).setParent(trainingListitem);
                new Listcell(formatter.format(gv.getTimestamp("estimatedCompletionDate"))).setParent(trainingListitem);
                Listcell lc = new Listcell("");
                lc.setParent(trainingListitem);
                if (gv.getTimestamp("estimatedStartDate").after(UtilDateTime.nowTimestamp())) {
                    Toolbarbutton button = new Toolbarbutton("Apply");
                    if ("TRNG_CANCELLED".equalsIgnoreCase(gv.getString("statusId")))
                        button.setDisabled(true);
                    button.addEventListener("onClick", new EventListener() {
                        public void onEvent(Event arg0) throws Exception {
                            trainingListitem.setSelected(true);
                            Events.postEvent("onSelect", trainingListitem.getParent(), null);
                            trainingListitem.setSelected(true);
                            TrainingComposer.this.onClick$applyTraining(arg0);
                        }
                    });
                    button.setParent(lc);
                }
                trainingListitem.setValue(data);
            }
        };
    }

    public void onClick$cancel(final Event event) throws Exception {
        if (selectedTraining == null) {
            Messagebox.show("Please select a Training", "Error", 1, null);
            return;
        }
        if (selectedTraining.getTimestamp("estimatedStartDate") == null) {
            Messagebox.show("Please select a Training", "Error", 1, null);
            return;
        }
        if (!selectedTraining.getTimestamp("estimatedStartDate").after(UtilDateTime.nowTimestamp()))
            return;
        Messagebox.show("Do You Want To Cancel?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {

                    try {
                        delegator.storeByCondition("TrainingRequest", UtilMisc.toMap("statusId", "TRNG_CANCELLED"), EntityCondition.makeCondition(
                                "trainingId", selectedTraining.getString("trainingId")));
                        delegator.storeByCondition("Training", UtilMisc.toMap("statusId", "TRNG_CANCELLED"), EntityCondition.makeCondition(
                                "trainingId", selectedTraining.getString("trainingId")));
                        Events.postEvent("onClick", event.getTarget().getFellow("listReqsWin").getParent().getFellow("button2"), null);

                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        Messagebox.show("Training Cancelled Successfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
                        Toolbarbutton toolbarbutton = (Toolbarbutton) trainingListbox.getSelectedItem().getLastChild().getFirstChild();
                        toolbarbutton.setDisabled(true);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        });

        Events.postEvent("onReloadRequest", trainingListbox, null);
    }

}