package com.zpc.timesheet.view;

import com.google.common.collect.Lists;
import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.common.service.SpringApplicationContext;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

import java.util.List;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */

public class ManageAttendanceRegisterSummaryVM {

    private TimesheetFinder timesheetFinder;
    private CommandGateway commandGateway;
    String managerId;
    Boolean isAdmin;

    @Getter
    @Setter
    List<AttendanceRegisterDto> attendanceRegisterSummaries = Lists.newArrayList();


    private final LocalDispatcher dispatcher = OfbizGateway.getDispatcher();
    private final Map<String,Object> userLogin = OfbizGateway.getUser();

    @Init
    public void init(){
        timesheetFinder= SpringApplicationContext.getBean("timesheetFinder");
        commandGateway = SpringApplicationContext.getCommandGateway();
        managerId = OfbizGateway.getPartyId();
        isAdmin = (Boolean) OfbizGateway.getTimesheetInfo().get("isAdmin");
        refreshAttendanceRegisterSummaries();
    }

    private void refreshAttendanceRegisterSummaries() {
        attendanceRegisterSummaries = timesheetFinder.attendanceRegisterSummary(managerId, isAdmin);
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view){
        Selectors.wireComponents(view, this, false);
    }


    @Command
    public void updateAttendanceRegister(@BindingParam("attendanceRegister") AttendanceRegisterDto attendanceRegisterDto){
        System.out.println("attendanceRegisterDto -- > " + attendanceRegisterDto);
    }

}
