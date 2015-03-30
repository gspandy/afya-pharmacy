package com.zpc.timesheet.view;

import com.google.common.collect.Lists;
import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import com.zpc.timesheet.application.ExportTimesheetCommand;
import com.zpc.timesheet.application.SaveTimesheetCommand;
import com.zpc.timesheet.application.VerifyTimesheetCommand;
import com.zpc.timesheet.dto.AttendanceEntryDto;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import com.zpc.timesheet.dto.LegendsDto;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.common.service.SpringApplicationContext;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.util.List;
import java.util.Map;

import static com.zpc.timesheet.view.ExportToDoveHelper.*;
import static com.zpc.timesheet.view.SpreadSheetHelper.*;

/**
 * Created by Administrator on 9/25/2014.
 */
public class VerifyTimesheetVM {

    private CommandGateway commandGateway;

    private TimesheetFinder timesheetFinder;

    @Wire
    private Spreadsheet adminAttendanceRegisterSpreadsheet;

    @Wire
    Window attendanceRegisterWin;

    @Getter
    @Setter
    List<AttendanceRegisterDto> attendanceRegisters = Lists.newArrayList();

    String managerId;
    Boolean isAdmin;
    String timesheetId;

    private int lastColumnIndex = 0;
    private int lastRowIndex;
    @Getter
    String timesheetStatus="";
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

    private int START_DEPT_COLUMN_INDEX = 2;
    private int START_DAY_ENTRY_COLUMN_INDEX = 5;
    private int START_DEPT_ROW_INDEX = 6;
    private int START_COLUMN_INDEX_OF_EACH_DEPT = 2;
    private int START_DAY_ENTRY_COLUMN_INDEX_OF_EACH_DEPT = 5;
    private int START_ROW_INDEX_OF_EACH_DEPT = 6;
    private int TOTAL_NUMBER_OF_DAY_ENTRY_COLUMNS = 116;
    List<Map<String, Object>> departments;

    private final  LocalDispatcher dispatcher = OfbizGateway.getDispatcher();
    private final Map<String,Object> userLogin = OfbizGateway.getUser();


    @Init
    public void init(){
        managerId = OfbizGateway.getPartyId();
        isAdmin = (Boolean) OfbizGateway.getTimesheetInfo().get("isAdmin");
        commandGateway = SpringApplicationContext.getCommandGateway();
        timesheetFinder = SpringApplicationContext.getBean("timesheetFinder");
        timesheetId = Executions.getCurrent().getParameter("timesheetId");
        timesheetStatus = timesheetFinder.getTimesheetStatusById(timesheetId);
        departments = timesheetFinder.getDepartmentsManagedBy(managerId, isAdmin);
    }


    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view){
        Selectors.wireComponents(view, this, false);
        refreshTimesheet(timesheetId);
        //buildConsolidatedAllDepartmentSheet();
    }


    private void refreshTimesheet(String timesheetId) {
        timesheetStatus = timesheetFinder.getTimesheetStatusById(timesheetId);
        attendanceRegisters = timesheetFinder.prepareTimesheetView(timesheetId,managerId,isAdmin);
        int totalEmployeesAcrossDepartments = 0;
        for (AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            AttendanceEntryDto.setEmployeeNameToAttendanceEntryDto(attendanceRegister.attendanceEntries, attendanceRegister.departmentId,timesheetFinder, attendanceRegister.fromDate.toDate());
            refreshAttendanceRegister(attendanceRegister);
            totalEmployeesAcrossDepartments = totalEmployeesAcrossDepartments + attendanceRegister.getAttendanceEntries().size();
        }
        adminAttendanceRegisterSpreadsheet.setMaxVisibleRows(totalEmployeesAcrossDepartments);
    }


    private void refreshAttendanceRegister(AttendanceRegisterDto attendanceRegister) {
        if(attendanceRegister.isEmptyRegister()){
            return;
        }
        final Sheet sheet = adminAttendanceRegisterSpreadsheet.getBook().getSheet(AttendanceRegisterDto.getSheetMatchingDepartmentName(attendanceRegister));
        //copyTemplateRowForEveryEmployee(attendanceRegister.attendanceEntries.size(), sheet);
        setFormulaeToCellsOfSheet(attendanceRegister.attendanceEntries.size(), sheet);
        hideCellsWhichExceedsDateRange(attendanceRegister, sheet);
        setScheduleNameInSheet(attendanceRegister, sheet);
        setDepartmentName(attendanceRegister, sheet);
        lastColumnIndex = placeDayEntryHeaders(attendanceRegister.fetchDayEntries(), sheet);
        lastRowIndex = addEmployeesToAttendanceSheet(attendanceRegister.attendanceEntries,sheet);
        setCommentsInSheet(attendanceRegister,sheet);
        setDayEntryForEmployee(sheet,attendanceRegister);
    }

    private int findLastRowIndex() {
        return lastRowIndex;
    }

    private int findLastColumnIndex() {
        return lastColumnIndex + 3;
    }

    public boolean saveAttendanceRegister(AttendanceRegisterDto attendanceRegisterDto){
        final Sheet selectedSheet = adminAttendanceRegisterSpreadsheet.getBook().getSheet(AttendanceRegisterDto.getSheetMatchingDepartmentName(attendanceRegisterDto));
        boolean result = populateDayEntries(selectedSheet.getInternalSheet(), attendanceRegisterDto, findLastRowIndex(), findLastColumnIndex(), LegendsDto.dropDownValuesOfSheetCells);
        if(result) {
            populateSummaries(selectedSheet.getInternalSheet(), attendanceRegisterDto);
           /* Long startTime = System.currentTimeMillis();
            SaveAttendanceRegisterCommand saveAttendanceRegisterCommand = new SaveAttendanceRegisterCommand(attendanceRegisterDto.timesheetId, Lists.newArrayList(attendanceRegisterDto), managerId);
            commandGateway.sendAndWait(saveAttendanceRegisterCommand);
            Long endTime = System.currentTimeMillis();
            System.out.println("<=========Time taken by SaveAttendanceRegisterCommand===========>"+(endTime-startTime));*/
        }
        return result;
    }



    @Command
    @NotifyChange({"attendanceRegisters","timesheetStatus"})
    public void verifyTimesheet(){
        for (AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            saveAttendanceRegister(attendanceRegister);
        }
        fireSaveTimesheetCommand();
        VerifyTimesheetCommand verifyTimesheetCommand = new VerifyTimesheetCommand(timesheetId, OfbizGateway.getUserId());
        commandGateway.sendAndWait(verifyTimesheetCommand);
        Messagebox.show("Records Successfully Verified","Notification",
                new Messagebox.Button[]{Messagebox.Button.OK},null,
                new EventListener<Messagebox.ClickEvent>() {
                    public void onEvent(Messagebox.ClickEvent event) {
                        if ("onOK".equals(event.getName())) {
                            Executions.sendRedirect("/");
                        }
                    }
                });
    }

    @Command
    @NotifyChange({"timesheetStatus"})
    public void exportTimesheet(){
        for (AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            saveAttendanceRegister(attendanceRegister);
        }
        fireSaveTimesheetCommand();
        ExportTimesheetCommand exportTimesheetCommand = new ExportTimesheetCommand(timesheetId);
        commandGateway.send(exportTimesheetCommand);
        exportRecordsToCSVFormat();
        Messagebox.show("Records Successfully Exported To Dove Format","Notification",
                new Messagebox.Button[]{Messagebox.Button.OK},null,
                new EventListener<Messagebox.ClickEvent>() {
                    public void onEvent(Messagebox.ClickEvent event) {
                        if ("onOK".equals(event.getName())) {
                            Executions.sendRedirect("/");
                        }
                    }
                });
    }

    private void exportRecordsToCSVFormat() {
        extractDetailsOfTimesheetGenerateCSVAndZipThem(attendanceRegisters, timesheetFinder);
        downloadZipExportedFile();
    }

    @Command
    @NotifyChange({"attendanceRegisters","timesheetStatus"})
    public void save(){
        boolean result = true;
        for (AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            result = saveAttendanceRegister(attendanceRegister);
            if(result == false){
                return;
            }
        }
        if(result) {
            fireSaveTimesheetCommand();
            Messagebox.show("Records Successfully Saved", "Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK}, null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {
                                attendanceRegisterWin.invalidate();
                            }
                        }
                    });
        }
    }

    private void fireSaveTimesheetCommand(){
        SaveTimesheetCommand saveTimesheetCommand = new SaveTimesheetCommand(timesheetId,attendanceRegisters, managerId);
        commandGateway.sendAndWait(saveTimesheetCommand);
    }

    @Command
    public void redirect(){
        Executions.sendRedirect("/");
    }

    @Command
    public void redirectToConsolidatedSheet(){
        Executions.getCurrent().sendRedirect("/zul/consolidatedAttendanceregister.zul?timesheetId="+ timesheetId,"popUpWin");
    }

    public void buildConsolidatedAllDepartmentSheet(){
        Long startTime = System.currentTimeMillis();
        List<Sheet> listOfSheets = setSheetNamesToList();
        Sheet sheetContainingAllDetails = listOfSheets.get(0);
        createTemplateForSheetNamedAll(departments, listOfSheets);
        int i =1;
        Long sTime = System.currentTimeMillis();
        placeDayEntryHeaders(attendanceRegisters.get(0).fetchDayEntries(), sheetContainingAllDetails);
        Long eTime = System.currentTimeMillis();
        //System.out.println("<=========Time taken by placeDayEntryHeaders of consolidatedsheet===========>"+(eTime-sTime));
        //System.out.println("For consolidatedsheet setScheduleNameInSheet================================>");
        setScheduleNameInSheet(attendanceRegisters.get(0),sheetContainingAllDetails);
        //System.out.println("For consolidatedsheet hideCellsWhichExceedsDateRange========================>");
        hideCellsWhichExceedsDateRange(attendanceRegisters.get(0), sheetContainingAllDetails);
        for(AttendanceRegisterDto attendanceRegisterDto : attendanceRegisters){
            Sheet currentSheet = listOfSheets.get(i++);
            setDepartmentIdAndEmpNameEntryForAllDept(currentSheet, sheetContainingAllDetails, attendanceRegisterDto.attendanceEntries.size(), START_DEPT_COLUMN_INDEX, START_DEPT_ROW_INDEX, START_COLUMN_INDEX_OF_EACH_DEPT, START_ROW_INDEX_OF_EACH_DEPT);
            setDayEntryForEachEmployeeInSheetNamedAll(sheetContainingAllDetails, currentSheet, attendanceRegisterDto.attendanceEntries.size(), START_DAY_ENTRY_COLUMN_INDEX, START_DEPT_ROW_INDEX, START_DAY_ENTRY_COLUMN_INDEX_OF_EACH_DEPT, START_ROW_INDEX_OF_EACH_DEPT);
            START_DEPT_ROW_INDEX = START_DEPT_ROW_INDEX + attendanceRegisterDto.attendanceEntries.size();
        }
        Ranges.range(sheetContainingAllDetails).protectSheet("");
        Long endTime = System.currentTimeMillis();
        //System.out.println("<=========Time taken by buildConsolidatedAllDepartmentSheet===========>"+(endTime-startTime));
    }

    private void setDayEntryForEachEmployeeInSheetNamedAll(Sheet sheetContainingAllDetails, Sheet currentSheet, int numberOfEmployeedInDept, int START_DAY_ENTRY_COLUMN_INDEX, int START_DEPT_ROW_INDEX, int START_DAY_ENTRY_COLUMN_INDEX_OF_EACH_DEPT, int START_ROW_INDEX_OF_EACH_DEPT) {
        for(int i=0; i < numberOfEmployeedInDept; i++){
            for(int j=0; j<TOTAL_NUMBER_OF_DAY_ENTRY_COLUMNS; j++){
                sheetContainingAllDetails.getInternalSheet().getCell(START_DEPT_ROW_INDEX,START_DAY_ENTRY_COLUMN_INDEX).setFormulaValue((getFormulaForCell(currentSheet.getSheetName(),START_DAY_ENTRY_COLUMN_INDEX,START_ROW_INDEX_OF_EACH_DEPT)));
/*
                Range rangeForDayEntry = Ranges.range(sheetContainingAllDetails, makeCellAddress(START_DAY_ENTRY_COLUMN_INDEX,START_DEPT_ROW_INDEX));
                rangeForDayEntry.setCellEditText(getFormulaForCell(currentSheet.getSheetName(),START_DAY_ENTRY_COLUMN_INDEX,START_ROW_INDEX_OF_EACH_DEPT));
*/
                START_DAY_ENTRY_COLUMN_INDEX = START_DAY_ENTRY_COLUMN_INDEX+1;
            }
            START_DAY_ENTRY_COLUMN_INDEX = 5;
            START_DEPT_ROW_INDEX = START_DEPT_ROW_INDEX + 1;
            START_ROW_INDEX_OF_EACH_DEPT = START_ROW_INDEX_OF_EACH_DEPT + 1;
        }
    }

    private void createTemplateForSheetNamedAll(List<Map<String, Object>> departments, List<Sheet> listOfSheets) {
        int startIndex = 7;
        for(Map<String, Object> mapOfDepartments : departments) {
            List<Map<String, Object>> employeesList = timesheetFinder.getEmployeesInDepartment(mapOfDepartments.get("departmentId").toString(), null);
            //copyTemplateRowForEveryEmployee(employeesList.size(), listOfSheets.get(0),startIndex);
            startIndex = startIndex + employeesList.size();
        }
        setFormulaeToCellsOfSheet(startIndex,listOfSheets.get(0));
    }

    private void setDepartmentIdAndEmpNameEntryForAllDept(Sheet currentSheet,Sheet sheetContainingAllDetails , int noOfEmployeesInDept, int START_DEPT_COLUMN_INDEX,
                                                          int START_DEPT_ROW_INDEX, int START_COLUMN_INDEX_OF_EACH_DEPT, int START_ROW_INDEX_OF_EACH_DEPT) {
        for(int i =0;i<noOfEmployeesInDept;i++) {
            sheetContainingAllDetails.getInternalSheet().getCell(START_DEPT_ROW_INDEX,START_DEPT_COLUMN_INDEX).setFormulaValue(getFormulaForCell(currentSheet.getSheetName(), START_COLUMN_INDEX_OF_EACH_DEPT, START_ROW_INDEX_OF_EACH_DEPT));

            /*Range rangeOfDeptId = Ranges.range(sheetContainingAllDetails, makeCellAddress(START_DEPT_COLUMN_INDEX, START_DEPT_ROW_INDEX));
            rangeOfDeptId.setCellEditText(getFormulaForCell(currentSheet.getSheetName(), START_COLUMN_INDEX_OF_EACH_DEPT, START_ROW_INDEX_OF_EACH_DEPT));*/

            sheetContainingAllDetails.getInternalSheet().getCell(START_DEPT_ROW_INDEX,START_DEPT_COLUMN_INDEX+1).setFormulaValue(getFormulaForCell(currentSheet.getSheetName(), START_COLUMN_INDEX_OF_EACH_DEPT+1, START_ROW_INDEX_OF_EACH_DEPT));
/*
            Range rangeForEmpFirstName = Ranges.range(sheetContainingAllDetails, makeCellAddress(START_DEPT_COLUMN_INDEX + 1, START_DEPT_ROW_INDEX));
            rangeForEmpFirstName.setCellEditText(getFormulaForCell(currentSheet.getSheetName(), START_COLUMN_INDEX_OF_EACH_DEPT + 1, START_ROW_INDEX_OF_EACH_DEPT));
*/

            sheetContainingAllDetails.getInternalSheet().getCell(START_DEPT_ROW_INDEX,START_DEPT_COLUMN_INDEX+2).setFormulaValue(getFormulaForCell(currentSheet.getSheetName(), START_COLUMN_INDEX_OF_EACH_DEPT+2, START_ROW_INDEX_OF_EACH_DEPT));
/*
            Range rangeForEmpLastName = Ranges.range(sheetContainingAllDetails, makeCellAddress(START_DEPT_COLUMN_INDEX + 2, START_DEPT_ROW_INDEX));
            rangeForEmpLastName.setCellEditText(getFormulaForCell(currentSheet.getSheetName(), START_COLUMN_INDEX_OF_EACH_DEPT + 2, START_ROW_INDEX_OF_EACH_DEPT));
*/

            START_DEPT_ROW_INDEX = START_DEPT_ROW_INDEX+1;
            START_ROW_INDEX_OF_EACH_DEPT = START_ROW_INDEX_OF_EACH_DEPT+1;
        }
    }

    public List<Sheet> setSheetNamesToList(){
        List<Sheet> listOfSheets = Lists.newArrayList();
        int noOfSheets  = adminAttendanceRegisterSpreadsheet.getBook().getNumberOfSheets();
        for(int i =0;i<noOfSheets;i++ ){
            listOfSheets.add(adminAttendanceRegisterSpreadsheet.getBook().getSheetAt(i));
        }
        return listOfSheets;
    }

    public static String getFormulaForCell(String sheet, int srcCellRow, int srcCellCol){
        StringBuilder br = new StringBuilder();
        br.append("'" + sheet + "'");
        br.append("!"+makeCellAddress(srcCellRow, srcCellCol));
        return br.toString();
    }

    @Command
    public void emailConsolidatedSheet(){
        String pathOfConsolidatedSheet = saveConsolidatedSheet(adminAttendanceRegisterSpreadsheet);
        //System.out.println(recipientName+"\t"+emailId+"\t"+cc+"\t"+bcc);
        String response = EmailEmployeeDetailsCSV.emailConsolidatedSheet(emailId, recipientName, cc, bcc, pathOfConsolidatedSheet);
        if(response.trim().equals("email sent")) {
            Messagebox.show("Email Sent", "Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK}, null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {
                                Executions.sendRedirect("/zul/verifyTimesheet.zul?timesheetId="+timesheetId);
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
                                Executions.sendRedirect("/zul/verifyTimesheet.zul?timesheetId="+timesheetId);
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
