package com.zpc.timesheet.view;

import com.google.common.collect.Lists;
import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import com.zpc.timesheet.application.SaveAttendanceRegisterCommand;
import com.zpc.timesheet.application.SubmitAttendanceRegisterCommand;
import com.zpc.timesheet.dto.AttendanceEntryDto;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import com.zpc.timesheet.dto.LegendsDto;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.common.service.SpringApplicationContext;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.model.SSheet;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.util.List;
import java.util.Map;

import static com.zpc.timesheet.view.SpreadSheetHelper.*;
import static com.zpc.timesheet.view.SpreadSheetHelper.setScheduleNameInSheet;

/**
 * Author: Nthdimenzion
 */
@ToString
public class ManageAttendanceRegisterVM {



    private TimesheetFinder timesheetFinder;
    private CommandGateway commandGateway;
    String managerId;
    Boolean isAdmin;
    Boolean isManager;
    @Getter
    Boolean isTimeKeeper;

    private int lastColumnIndex = 0;

    private int lastRowIndex;

    @Getter
    @Setter
    List<AttendanceRegisterDto> attendanceRegisters = Lists.newArrayList();

    String attendanceRegisterId;
    @Wire
    Spreadsheet attendanceRegisterSpreadsheet;
    @Wire
    Window attendanceRegisterWin;
    private AttendanceRegisterDto attendanceRegisterDto;
    @Getter
    @Setter
    Boolean setDisableSaveButton = true;


    private final LocalDispatcher dispatcher = OfbizGateway.getDispatcher();
    private final Map<String,Object> userLogin = OfbizGateway.getUser();


    @Init
    public void init() {
        timesheetFinder = SpringApplicationContext.getBean("timesheetFinder");
        commandGateway = SpringApplicationContext.getCommandGateway();
        managerId = OfbizGateway.getPartyId();
        isAdmin = (Boolean) OfbizGateway.getTimesheetInfo().get("isAdmin");
        isManager = (Boolean) OfbizGateway.getTimesheetInfo().get("isManager");
        isTimeKeeper = timesheetFinder.CheckIfTimekeeper(managerId);
        if(isManager && isTimeKeeper){
            isTimeKeeper = false;
        }
        this.attendanceRegisterId = Executions.getCurrent().getParameter("attendanceRegisterId");
    }

    private void CheckIfTimeKeeper(Boolean isHumanResUser) {
        String partyId = OfbizGateway.getPartyId();
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        refreshAttendanceRegister();
    }

    private void refreshAttendanceRegister() {
        attendanceRegisterDto = timesheetFinder.prepareAttendanceRegisterView(attendanceRegisterId, managerId, isAdmin);
        if(attendanceRegisterDto.isEmptyRegister()){
            Messagebox.show("No employees associated to department", "Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK}, null,
                    new org.zkoss.zk.ui.event.EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {}}});
            return;
        }
        setMaximumvisibleRowsForSheet(attendanceRegisterDto.attendanceEntries.size(), attendanceRegisterSpreadsheet);
        copyTemplateRowForEveryEmployee(timesheetFinder.getEmployeesInDepartment(attendanceRegisterDto.departmentId,attendanceRegisterDto.fromDate.toDate()).size(), attendanceRegisterSpreadsheet.getSelectedSheet());
        hideCellsWhichExceedsDateRange(attendanceRegisterDto, attendanceRegisterSpreadsheet.getSelectedSheet());
        setScheduleNameInSheet(attendanceRegisterDto, attendanceRegisterSpreadsheet.getSelectedSheet());
        setDepartmentName(attendanceRegisterDto,attendanceRegisterSpreadsheet.getSelectedSheet());
        lastColumnIndex = placeDayEntryHeaders(attendanceRegisterDto.fetchDayEntries(), attendanceRegisterSpreadsheet.getSelectedSheet());
        AttendanceEntryDto.setEmployeeNameToAttendanceEntryDto(attendanceRegisterDto.attendanceEntries, attendanceRegisterDto.departmentId, timesheetFinder, attendanceRegisterDto.fromDate.toDate());
        lastRowIndex = addEmployeesToAttendanceSheet(attendanceRegisterDto.attendanceEntries, attendanceRegisterSpreadsheet.getSelectedSheet());
        setCommentsInSheet(attendanceRegisterDto, attendanceRegisterSpreadsheet.getSelectedSheet());
        setDayEntryForEmployee(attendanceRegisterSpreadsheet.getSelectedSheet(),attendanceRegisterDto);
    }

    private void setEmployeeNameToAttendanceEntryDto(List<AttendanceEntryDto> attendanceEntryDtos, String departmentId) {
        List<Map<String, Object>> employees = timesheetFinder.getEmployeesInDepartment(departmentId, null);
        for(Map mapOfEmployeeDetails : employees){
            for(AttendanceEntryDto attendanceEntryDto : attendanceEntryDtos){
                if(((String)mapOfEmployeeDetails.get("employeeId")).equals(attendanceEntryDto.getEmployeeId())){
                    attendanceEntryDto.setFirstName((String)mapOfEmployeeDetails.get("firstName"));
                    attendanceEntryDto.setLastName((String) mapOfEmployeeDetails.get("lastName"));
                }
            }
        }
    }

    public boolean saveAttendanceRegister(){
        boolean success = populateAttendanceRegisterDto();
        SaveAttendanceRegisterCommand saveAttendanceRegisterCommand = new SaveAttendanceRegisterCommand(attendanceRegisterDto.timesheetId,Lists.newArrayList(attendanceRegisterDto),managerId);
        if(success)
            commandGateway.sendAndWait(saveAttendanceRegisterCommand);
        return success;
    }

    private boolean populateAttendanceRegisterDto() {
        final SSheet selectedSheet = attendanceRegisterSpreadsheet.getSelectedSheet().getInternalSheet();
        boolean result = populateDayEntries(selectedSheet,attendanceRegisterDto,findLastRowIndex(),findLastColumnIndex(), LegendsDto.dropDownValuesOfSheetCells);
        if(result){
            populateSummaries(selectedSheet,attendanceRegisterDto);
        }
        return result;
    }

    @Command
    public void save() {
        if(saveAttendanceRegister()){
            Messagebox.show("Records Successfully Saved","Notification",
                    new Messagebox.Button[] {Messagebox.Button.OK},null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {
                                attendanceRegisterSpreadsheet.invalidate();
                            }
                        }
                    });
        }
    }



    private int findLastColumnIndex() {
        return lastColumnIndex + 3;
    }

    private int findLastRowIndex() {
        return lastRowIndex;
    }

    @Command
    public void submit(){
        Messagebox.show("Do you want to submit the schedule",
                "Warning", Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION,
                new EventListener<Event>(){
                    public void onEvent(Event e){
                        if(Messagebox.ON_YES.equals(e.getName())){
                            //saveAttendanceRegister();
                            boolean success = populateAttendanceRegisterDto();
                            if(!success){
                                return;
                            }
                            SubmitAttendanceRegisterCommand submitAttendanceRegisterCommand = new SubmitAttendanceRegisterCommand(attendanceRegisterDto.getTimesheetId(),managerId,attendanceRegisterDto);
                            commandGateway.send(submitAttendanceRegisterCommand);
                            Executions.sendRedirect("/");
                        }else if(Messagebox.ON_NO.equals(e.getName())){}
                    }
                }
        );
    }

    @Command
    public void redirect(){
        Executions.sendRedirect("/");
    }
}
