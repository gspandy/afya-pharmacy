package com.zpc.timesheet.view;

import com.google.common.collect.Lists;
import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import com.zpc.timesheet.application.ExportTimesheetCommand;
import com.zpc.timesheet.dto.*;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.service.SpringApplicationContext;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Messagebox;

import java.util.List;

import static com.zpc.timesheet.view.ExportToDoveHelper.*;

/**
 * Created by Administrator on 9/24/2014.
 */
public class ManageTimesheetVM {

    private TimesheetFinder timesheetFinder;
    private CommandGateway commandGateway;
    @Getter
    @Setter
    private TimesheetDto timesheetDto;

    List<TimesheetDto> timesheets = Lists.newArrayList();
    @Getter
    @Setter
    List<DepartmentDto> departments = Lists.newArrayList();

    @Getter
    @Setter
    List<AttendanceRegisterDto> attendanceRegisters = Lists.newArrayList();

    @Getter
    @Setter
    String recipientName;
    @Getter
    @Setter
    String emailId;
    @Getter
    @Setter
    String cc;
    @Getter
    @Setter
    String bcc;
    String timesheetIdForMailing;

    @Init
    public void init(){
        timesheetFinder = SpringApplicationContext.getBean("timesheetFinder");
        commandGateway = SpringApplicationContext.getCommandGateway();
        refreshTimesheet();
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view){
        Selectors.wireComponents(view, this, false);
    }
    public List<TimesheetDto> getTimesheets() {
        return timesheets;
    }

    public void setTimesheets(List<TimesheetDto> timesheets) {
        this.timesheets = timesheets;
    }

    private void refreshTimesheet() {
        timesheets = timesheetFinder.getAllTimesheets();
    }

    @Command
    @NotifyChange({"departments"})
    public void fetchDepartmentsYetToSubmitAttendanceRegister(@BindingParam("timesheetDto")TimesheetDto timesheetDto){
        departments = timesheetFinder.getAllDepartmentsYetToSubmitAttendanceRegister(timesheetDto.getTimesheetId());
    }

    @Command
    public void verify(@BindingParam("timesheetDto")TimesheetDto timesheetDto){
        Executions.sendRedirect("/zul/verifyTimesheet.zul?timesheetId="+timesheetDto.getTimesheetId());
    }

    @Command
    @NotifyChange({"timesheets"})
    public void export(@BindingParam("timesheetDto")TimesheetDto timesheetDto){
        attendanceRegisters = timesheetFinder.prepareTimesheetView(timesheetDto.getTimesheetId(), OfbizGateway.getPartyId(),(Boolean) OfbizGateway.getTimesheetInfo().get("isAdmin"));
        for (AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            AttendanceEntryDto.setEmployeeNameToAttendanceEntryDto(attendanceRegister.attendanceEntries, attendanceRegister.departmentId, timesheetFinder, attendanceRegister.fromDate.toDate());
        }
        ExportTimesheetCommand exportTimesheetCommand = new ExportTimesheetCommand(timesheetDto.getTimesheetId());
        commandGateway.send(exportTimesheetCommand);
        getPathToExport();
        exportRecordsToCSVFormat();
        Messagebox.show("Records Successfully Exported To Dove Format","Notification",
                new Messagebox.Button[]{Messagebox.Button.OK},null,
                new EventListener<Messagebox.ClickEvent>() {
                    public void onEvent(Messagebox.ClickEvent event) {
                        if ("onOK".equals(event.getName())) {
                        }
                    }
                });
        refreshTimesheet();
        //Executions.sendRedirect("/zul/verifyTimesheet.zul?timesheetId="+timesheetDto.getTimesheetId());
    }

    private void getPathToExport() {
    }


    private void exportRecordsToCSVFormat() {
        extractDetailsOfTimesheetGenerateCSVAndZipThem(attendanceRegisters, timesheetFinder);
        downloadZipExportedFile();
    }

    @Command
    public void update(@BindingParam("timesheetDto")TimesheetDto timesheetDto){
        Executions.sendRedirect("/zul/verifyTimesheet.zul?timesheetId="+timesheetDto.getTimesheetId());
    }

    @Command
    public void getTimesheetId(@BindingParam("timesheetDto")TimesheetDto timesheetDto){
        timesheetIdForMailing = timesheetDto.getTimesheetId();
        attendanceRegisters = timesheetFinder.prepareTimesheetView(timesheetIdForMailing, OfbizGateway.getPartyId(),(Boolean) OfbizGateway.getTimesheetInfo().get("isAdmin"));
        for (AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            AttendanceEntryDto.setEmployeeNameToAttendanceEntryDto(attendanceRegister.attendanceEntries, attendanceRegister.departmentId, timesheetFinder, attendanceRegister.fromDate.toDate());
        }
       // System.out.println(timesheetIdForMailing);
    }
    @Command
    @NotifyChange({"emailId"})
    public void sendMail(@BindingParam("timesheetDto")TimesheetDto timesheetDto){
        //System.out.println(recipientName+"\t"+emailId+"\t"+cc+"\t"+bcc);
        String pathOfZip = extractDetailsOfTimesheetGenerateCSVAndZipThem(attendanceRegisters, timesheetFinder);
        String response = EmailEmployeeDetailsCSV.sendEmail(emailId,recipientName,cc,bcc, pathOfZip);
        if(response.trim().equals("email sent")) {
            Messagebox.show("Email Sent", "Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK}, null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {
                                Executions.sendRedirect("/");
                            }
                        }
                    });
        }
        else{
            Messagebox.show("Email could be sent, please contact support", "Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK}, null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {
                                Executions.sendRedirect("/");
                            }
                        }
                    });
        }
    }

    public Validator getFormValidator(){
        return new AbstractValidator() {
            public void validate(ValidationContext ctx) {
                if (emailId == null || emailId.trim().equals("") || !emailId.matches(".+@.+\\.[a-z]+")) {
                    addInvalidMessage(ctx, "emailIdEmpty", "*Required ");
                }
                if (recipientName == null || recipientName.trim().equals("")) {
                    addInvalidMessage(ctx, "recipientNameEmpty", "*Required ");
                }
            }
        };
    }
}
