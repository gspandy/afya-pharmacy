package com.zpc.timesheet.view;


import org.axonframework.commandhandling.annotation.CommandHandler;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 9/15/2014.
 */
public class CenterLayoutRenderer extends SelectorComposer<Component>{
    public void doAfterCompose(Component component) throws Exception {
        super.doAfterCompose(component);
    }

    @Listen("onCreate=#manageSchedule")
    public void loadManageScheduleOnCreate(Event event){
        renderThePageAtCenterLayout(event);
    }

    @Listen("onCreate=#manageAttendanceRegisterSummary")
    public void loadManageAttendenceOnCreate(Event event){
        renderThePageAtCenterLayout(event);
    }

    @Listen("onClick=#manageSchedule")
    public void loadManageScheduleOnClick(MouseEvent event) {
        renderThePageAtCenterLayout(event);
    }

    @Listen("onClick=#manageAttendanceRegisterSummary")
    public void loadManageAttendenceOnClick(MouseEvent event) {
        renderThePageAtCenterLayout(event);
    }

    @Listen("onClick=#manageTimesheet")
    public void loadManageTimesheetOnClick(MouseEvent event) {
        renderThePageAtCenterLayout(event);
    }

    public void renderThePageAtCenterLayout(Event event){
        Borderlayout borderLayout = (Borderlayout) Path.getComponent("/templateBorderlayout");
        Center center = borderLayout.getCenter();
        center.getChildren().clear();
        String zulFilePathName = "/zul/"+event.getTarget().getId() + ".zul";
        Executions.createComponents(zulFilePathName, center, null);
        Collection<Component> toolbarbuttonList = event.getTarget().getFellows();
        setStyle(toolbarbuttonList, event);
    }

    private void setStyle(Collection<Component> toolbarbuttonList, Event event) {
        for(Component component: toolbarbuttonList){
            if(event.getTarget().getId().equals(component.getId())){
                Toolbarbutton toolbarbutton = (Toolbarbutton)component;
                toolbarbutton.setStyle("color: rgb(216, 119, 0)");
            }
            else{
                Toolbarbutton toolbarbutton = (Toolbarbutton)component;
                toolbarbutton.setStyle("color:#000000");
            }
        }
    }

    public void loadPage(){
        Borderlayout bl = (Borderlayout) Path.getComponent("/templateBorderlayout");
        Center center = bl.getCenter();
        center.getChildren().clear();
        Executions.createComponents("1.zul", center, null);
    }
}
