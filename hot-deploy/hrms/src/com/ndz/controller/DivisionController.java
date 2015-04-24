package com.ndz.controller;

import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.util.HrmsInfrastructure;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 28-Oct-14.
 */
public class DivisionController extends HrmsComposer {

    private Map division = UtilMisc.toMap("divisionCode",null,"divisionName",null);

    public Map getDivision() {
        return division;
    }

    public void setDivision(Map division) {
        this.division = division;
    }

    public void createDivision(Window divisionWindow){
        division.put("divisionId",delegator.getNextSeqId("Division"));
        GenericValue divisionGv = delegator.makeValidValue("Division",division);
        try {
            List<GenericValue> divisionGvExistingList = delegator.findByAnd("Division",UtilMisc.toMap("divisionName",division.get("divisionName")),null,false);
            if(divisionGvExistingList.size()>0){
                Messagebox.show("Duplicate Division name", "Error", 1, null);
                return;
            }
            delegator.create(divisionGv);
            Messagebox.show("Division created successfully", "Success", 1, null);
            divisionWindow.detach();
        } catch (GenericEntityException e) {
            try {
                Messagebox.show("Duplicate Division code", "Error", 1, null);
                return;
            }catch (InterruptedException e1){
                e1.printStackTrace();
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void editDivision(GenericValue division,Window editDivision){
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        String divisionId = ((Textbox)editDivision.getFellow("divisionId")).getValue();
        String divisionCode = ((Textbox)editDivision.getFellow("divisionCode")).getValue();
        String divisionName = ((Textbox)editDivision.getFellow("divisionName")).getValue();
        String divisionCodeOriginal = division.getString("divisionCode");
        GenericValue divisionGv = delegator.makeValidValue("Division",UtilMisc.toMap("divisionId",divisionId,"divisionCode",divisionCode,"divisionName",divisionName));
        try {
            List<GenericValue> divisionGvExistingList = delegator.findByAnd("Division",UtilMisc.toMap("divisionName",divisionName),null,false);
            GenericValue divisionGvExisting = EntityUtil.getFirst(divisionGvExistingList);
            if(divisionGvExistingList.size()>0 && !divisionGvExisting.getString("divisionCode").equals(divisionCodeOriginal)){
                Messagebox.show("Duplicate Division name", "Error", 1, null);
                return;
            }
            delegator.store(divisionGv);
            Messagebox.show("Division Updated successfully", "Success", 1, null);
            Events.postEvent("onClick", Path.getComponent("/searchPanel/searchButton"), null);
            editDivision.detach();
        } catch (GenericEntityException e) {
            try {
                Messagebox.show("Duplicate Division code", "Error", 1, null);
                return;
            }catch (InterruptedException e1){
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDivision(Event event, final GenericValue gv, final Button btn) throws InterruptedException {
        final Component searchPanel = event.getTarget();
        final String divisionId = gv.getString("divisionId");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();

        Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {
                    try {
                        delegator.removeByAnd("DepartmentDivision", UtilMisc.toMap("divisionId", divisionId));
                        delegator.removeValue(gv);
                        Events.postEvent("onClick", btn, null);
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    }
                    try {
                        Messagebox.show("Division Deleted Successfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        });
    }

    public static SimpleListModel getAllDivisions(){
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        List<GenericValue> divisionGv=new ArrayList<GenericValue>();
        try {
            divisionGv = delegator.findList("Division",null,null,null,null,false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return new SimpleListModel(divisionGv.toArray());
    }


}
