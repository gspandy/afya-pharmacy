package com.zpc.timesheet.view;

import com.google.common.collect.Lists;
import com.zpc.timesheet.dto.AttendanceEntryDto;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import com.zpc.timesheet.dto.DayEntryDto;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Font;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.model.SSheet;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Messagebox;

import java.util.List;
import java.util.Set;

/**
 * Author: Nthdimenzion
 */

final class SpreadSheetHelper {

    static final int HEADER_START_ROW_INDEX = 4;
    static final int HEADER_WIDTH = 4;
    static final int NO_OF_TIME_ENTRIES_PER_DAY = 4;
    static final int DAY_HEADER_COLUMN_START = 5;
    static final int HEADER_END_ROW_INDEX = 5;

    static final String DEPARTMENT_NAME_COLUMN_START = "C";
    static final String DEPARTMENT_NAME_COLUMN_END = "E";
    static final String TOTAL_DAYS_COLUMN= "A";

    static final String EMPLOYEE_COLUMN_START = "C";
    static final int EMPLOYEE_START_ROW_INDEX = 6;
    static final int SHIFT_ONE_INDEX = 0;
    static final int SHIFT_TWO_INDEX = 1;
    static final int NOVERTIME_INDEX = 2;
    static final int DOVERTIME_INDEX = 3;
    static final int EMPLOYEE_ID_COLUMN_INDEX = 2;
    static final int DAY_ENTRIES_COLUMN_START_INDEX = 5;
    static final int SUMMARIES_ENTRIES_COLUMN_START_INDEX = 122;
    static final int SUMMARIES_ENTRIES_COLUMN_END_INDEX = 149;

    static final String EXCEL_LAST_COLUMN_INDEX ="EU";
    static final String SUMMATION_COLUMN_START_INDEX = "DS";
    static final int NUMBER_OF_EXTRA_ROWS_TO_BE_MADE_VISIBLE = 10;
    static final int START_INDEX_LISTOFFORMULAE = 0;

    static void  setDepartmentName(AttendanceRegisterDto attendanceRegisterDto, Sheet selectedSheet) {
        /*Range destination = Ranges.range(selectedSheet, makeRangeAddress(DEPARTMENT_NAME_COLUMN_START, HEADER_START_ROW_INDEX, DEPARTMENT_NAME_COLUMN_END, HEADER_END_ROW_INDEX));
        destination.setCellValue(attendanceRegisterDto.departmentName);*/
        selectedSheet.getInternalSheet().getCell(makeCellAddress(DEPARTMENT_NAME_COLUMN_START,HEADER_START_ROW_INDEX)).setStringValue(attendanceRegisterDto.departmentCode+" - "+attendanceRegisterDto.departmentName);
    }

    static void setScheduleNameInSheet(AttendanceRegisterDto attendanceRegisterDto, Sheet selectedSheet){
        /*Range rangeForSettingScheduleNameToSheet = Ranges.range(selectedSheet, makeCellAddress(EMPLOYEE_ID_COLUMN_INDEX + 1, EMPLOYEE_ID_COLUMN_INDEX));
        CellOperationUtil.applyFontBoldweight(rangeForSettingScheduleNameToSheet, Font.Boldweight.BOLD);
        rangeForSettingScheduleNameToSheet.setCellValue(attendanceRegisterDto.getDescription().toUpperCase() + "(" + attendanceRegisterDto.getFromDate().toString("yyyy/MM/dd") + "-" + attendanceRegisterDto.getThruDate().toString("yyyy/MM/dd") + ")");*/
        selectedSheet.getInternalSheet().getCell(makeCellAddress(EMPLOYEE_ID_COLUMN_INDEX + 1, EMPLOYEE_ID_COLUMN_INDEX)).setStringValue(attendanceRegisterDto.getDescription().toUpperCase() + "[" + attendanceRegisterDto.getFromDate().toString("dd MMM yyyy") + " - " + attendanceRegisterDto.getThruDate().toString("dd MMM yyyy") + "]");
    }


    static void setCommentsInSheet(AttendanceRegisterDto attendanceRegisterDto, Sheet selectedSheet) {
        int rowIndexOfComments = EMPLOYEE_START_ROW_INDEX-1;
        int columnIndexOfComments = SHIFT_ONE_INDEX;
        for(AttendanceEntryDto attendanceEntryDto : attendanceRegisterDto.attendanceEntries){
            selectedSheet.getInternalSheet().getCell(rowIndexOfComments,columnIndexOfComments+1).setStringValue(attendanceEntryDto.comments);
            rowIndexOfComments = rowIndexOfComments+1;
        }
    }

    static void hideCellsWhichExceedsDateRange(AttendanceRegisterDto attendanceRegisterDto, Sheet selectedSheet) {
        int numberOfDaysInSchedule = Days.daysBetween(attendanceRegisterDto.getFromDate(), attendanceRegisterDto.getThruDate()).getDays()+2;
        int START_COLUMN_INDEX = (numberOfDaysInSchedule*4)+1;
        int START_ROW_INDEX = 6;
        int END_COLUMN_INDEX = 120;
        int END_ROW_INDEX = 20;
        Range rangeOfCellsWhichExceedsDateRange = Ranges.range(selectedSheet, makeRangeAddress(START_COLUMN_INDEX,START_ROW_INDEX,END_COLUMN_INDEX,END_ROW_INDEX));
        rangeOfCellsWhichExceedsDateRange = rangeOfCellsWhichExceedsDateRange.toColumnRange();
        rangeOfCellsWhichExceedsDateRange.setHidden(true);
    }

    static int placeDayEntryHeaders(List<DayEntryDto> dayEntryDtos, final Sheet selectedSheet) {
        int columnIndex = DAY_HEADER_COLUMN_START;
        selectedSheet.getInternalSheet().getCell(makeCellAddress(DAY_HEADER_COLUMN_START, 2)).setDateValue(dayEntryDtos.get(0).getLastDateOfMonth(dayEntryDtos.get(0).date).toDate());
        for (DayEntryDto dayEntryDto : dayEntryDtos) {
            if((dayEntryDto.getLastDateOfMonth(dayEntryDto.date)).equals(dayEntryDto.getLastDateOfMonth(dayEntryDto.date).dayOfMonth().withMaximumValue())) {
                selectedSheet.getInternalSheet().getCell(makeCellAddress(columnIndex+HEADER_WIDTH, 2)).setDateValue(dayEntryDto.getLastDateOfMonth(dayEntryDto.date).plusDays(1).toDate());
            }
            columnIndex = columnIndex + HEADER_WIDTH;
        }
        return columnIndex;
    }

    static int addEmployeesToAttendanceSheet(List<AttendanceEntryDto> attendanceEntries, final Sheet selectedSheet) {
        int employeeRowIndex = EMPLOYEE_START_ROW_INDEX;
        for (AttendanceEntryDto attendanceEntry : attendanceEntries) {
            //System.out.println("\n\n\n"+attendanceEntry+"sheet name :"+selectedSheet.getSheetName()+"\n\n");
            selectedSheet.getInternalSheet().getCell(makeCellAddress(EMPLOYEE_ID_COLUMN_INDEX, employeeRowIndex)).setStringValue(attendanceEntry.employeeId);
            selectedSheet.getInternalSheet().getCell(makeCellAddress(EMPLOYEE_ID_COLUMN_INDEX + 1, employeeRowIndex)).setStringValue(attendanceEntry.firstName);
            selectedSheet.getInternalSheet().getCell(makeCellAddress(EMPLOYEE_ID_COLUMN_INDEX + 2, employeeRowIndex)).setStringValue(attendanceEntry.lastName);
            employeeRowIndex++;
        }
        return employeeRowIndex;

    }

    static void copyTemplateRowForEveryEmployee(int numOfAttendanceEntries, Sheet selectedSheet) {
        copyTemplateRowForEveryEmployee(numOfAttendanceEntries,selectedSheet,EMPLOYEE_START_ROW_INDEX);
    }

    static void copyTemplateRowForEveryEmployee(int numOfAttendanceEntries, Sheet selectedSheet,int employeeStartsFromIndex) {
        final int startIndex = employeeStartsFromIndex;
        final Range templateAttendanceRow = Ranges.range(selectedSheet, makeRangeAddress(CellReference.convertColStringToIndex(TOTAL_DAYS_COLUMN), EMPLOYEE_START_ROW_INDEX, CellReference.convertColStringToIndex(EXCEL_LAST_COLUMN_INDEX), EMPLOYEE_START_ROW_INDEX));
        final Range copyIntoRange = Ranges.range(selectedSheet, makeRangeAddress(CellReference.convertColStringToIndex(TOTAL_DAYS_COLUMN), startIndex, CellReference.convertColStringToIndex(EXCEL_LAST_COLUMN_INDEX), numOfAttendanceEntries + startIndex-1));
        CellOperationUtil.paste(templateAttendanceRow, copyIntoRange);
    }

    public static void setDayEntryForEmployee(Sheet selectedSheet, AttendanceRegisterDto attendanceRegisterDto) {
        int columnNumber = DAY_ENTRIES_COLUMN_START_INDEX;
        int rowNumber = EMPLOYEE_START_ROW_INDEX-1;
        List<List<DayEntryDto>> listOfDayEntryDtos = attendanceRegisterDto.fetchDayEntriesDtoOfAllEmployees();
        for (List<DayEntryDto> dayEntryDtos : listOfDayEntryDtos) {
            for (DayEntryDto dayEntryDto : dayEntryDtos) {
                if(StringUtils.isNotEmpty(dayEntryDto.getShiftOne()) && StringUtils.isNumeric(dayEntryDto.getShiftOne().substring(0,1))){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber).setNumberValue(Double.valueOf(dayEntryDto.getShiftOne()));
                }else if(StringUtils.isNotEmpty(dayEntryDto.getShiftOne()) && StringUtils.isAlpha(dayEntryDto.getShiftOne())){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber).setStringValue(dayEntryDto.getShiftOne());
                }

                if(StringUtils.isNotEmpty(dayEntryDto.getShiftTwo()) && StringUtils.isNumeric(dayEntryDto.getShiftTwo().substring(0,1))){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber+1).setNumberValue(Double.valueOf(dayEntryDto.getShiftTwo()));
                }else if(StringUtils.isNotEmpty(dayEntryDto.getShiftTwo()) && StringUtils.isAlpha(dayEntryDto.getShiftTwo())){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber+1).setStringValue(dayEntryDto.getShiftTwo());
                }

                if(StringUtils.isNotEmpty(dayEntryDto.getNOvertime()) && StringUtils.isNumeric(dayEntryDto.getNOvertime().substring(0,1))){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber+2).setNumberValue(Double.valueOf(dayEntryDto.getNOvertime()));
                }else if(StringUtils.isNotEmpty(dayEntryDto.getNOvertime()) && StringUtils.isAlpha(dayEntryDto.getNOvertime())){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber+2).setStringValue(dayEntryDto.getNOvertime());
                }

                if(StringUtils.isNotEmpty(dayEntryDto.getDOvertime()) && StringUtils.isNumeric(dayEntryDto.getDOvertime().substring(0,1))){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber+3).setNumberValue(Double.valueOf(dayEntryDto.getDOvertime()));
                }else if(StringUtils.isNotEmpty(dayEntryDto.getDOvertime()) && StringUtils.isAlpha(dayEntryDto.getDOvertime())){
                    selectedSheet.getInternalSheet().getCell(rowNumber,columnNumber+3).setStringValue(dayEntryDto.getDOvertime());
                }
                columnNumber = columnNumber + HEADER_WIDTH;
            }
            columnNumber = DAY_ENTRIES_COLUMN_START_INDEX;
            rowNumber = rowNumber+1;
        }
    }

    static boolean populateDayEntries(SSheet selectedSheet, AttendanceRegisterDto attendanceRegisterDto, int lastRowIndex, int alastColumnIndex, Set<String> dropDownValuesOfSheetCells) {
        List<String> listOfCellRef = Lists.newArrayList();
        boolean returnValue = true;
        int startIndexOfRow = EMPLOYEE_START_ROW_INDEX;
        int rowIndex = 0;
        int lastColumnIndex = getLastColumnIndex(attendanceRegisterDto);
        for (int i = rowIndex; i < attendanceRegisterDto.attendanceEntries.size(); i++) {
            String[] timeEntries = new String[NO_OF_TIME_ENTRIES_PER_DAY];
            for (int column = DAY_ENTRIES_COLUMN_START_INDEX, dayEntryIndex = 0; column <= lastColumnIndex; column++) {
                Object value = selectedSheet.getCell(makeCellAddress(column, startIndexOfRow)).getValue();
                int timeEntryIndex = (column - 1) % NO_OF_TIME_ENTRIES_PER_DAY;
                if(value != null && !(dropDownValuesOfSheetCells.contains(value.toString()))){
                    addCellRefToList(listOfCellRef,column, startIndexOfRow, attendanceRegisterDto);
                }
                else if (value != null && dropDownValuesOfSheetCells.contains(value.toString())) {
                    timeEntries[timeEntryIndex] = value.toString();
                }
                else {
                    timeEntries[timeEntryIndex] = null;
                }
                if (completedCollectingShiftEntriesForTheDay(timeEntryIndex)) {
                    populateDayEntry(startIndexOfRow, timeEntries, dayEntryIndex,selectedSheet,attendanceRegisterDto);
                    timeEntries = new String[NO_OF_TIME_ENTRIES_PER_DAY];
                    dayEntryIndex++;
                }
            }
            startIndexOfRow = startIndexOfRow + 1;
        }
        if(listOfCellRef.size()>0){
            displayErrorMessagePopUp(listOfCellRef);
            returnValue = false;
        }
        return returnValue;
    }

    private static int getLastColumnIndex(AttendanceRegisterDto attendanceRegisterDto) {
        LocalDate fromDate = attendanceRegisterDto.fromDate;
        LocalDate thruDate = attendanceRegisterDto.thruDate;
        int noOfDays = ((Days.daysBetween(fromDate, thruDate.plusDays(1)).getDays())*4)+5;
        return noOfDays;
    }

    private static void addCellRefToList(List<String> listOfCellRef, int column, int rowIndex, AttendanceRegisterDto attendanceRegisterDto) {
        listOfCellRef.add(attendanceRegisterDto.departmentName+"-{"+makeCellAddress(column,rowIndex)+"}");
    }

    private static void displayErrorMessagePopUp(List<String> cellRefList){
        Messagebox.show("Invalid input at cell"+cellRefList, "Notification",
                new Messagebox.Button[]{Messagebox.Button.OK}, null,
                new EventListener<Messagebox.ClickEvent>() {
                    public void onEvent(Messagebox.ClickEvent event) {
                        if ("onOK".equals(event.getName())) {}}});
    }


    private static boolean completedCollectingShiftEntriesForTheDay(int timeEntryIndex) {
        return timeEntryIndex == 3;
    }

    static void populateDayEntry(int rowIndex, String[] timeEntries, int dayEntryIndex, SSheet selectedSheet, AttendanceRegisterDto attendanceRegisterDto) {
        String employeeId = null;
        if((employeeId = selectedSheet.getCell(makeCellAddress(EMPLOYEE_ID_COLUMN_INDEX, rowIndex)).getValue().toString())!=null) {
            final AttendanceEntryDto attendanceEntryForEmployee = AttendanceEntryDto.findAttendanceEntryForEmployee(employeeId, attendanceRegisterDto.attendanceEntries);
            final DayEntryDto dayEntryDto = attendanceEntryForEmployee.dayEntryDtos.get(dayEntryIndex);
            dayEntryDto.shiftOne = timeEntries[SHIFT_ONE_INDEX];
            dayEntryDto.shiftTwo = timeEntries[SHIFT_TWO_INDEX];
            dayEntryDto.nOvertime = timeEntries[NOVERTIME_INDEX];
            dayEntryDto.dOvertime = timeEntries[DOVERTIME_INDEX];
        }
    }

    private static int headerColumnEndIndex(int columnIndex) {
        return columnIndex + HEADER_WIDTH;
    }

    static void populateSummaries(SSheet selectedSheet, AttendanceRegisterDto attendanceRegisterDto) {
        int columnNumber = SUMMARIES_ENTRIES_COLUMN_START_INDEX;
        int rowNumber = EMPLOYEE_START_ROW_INDEX;
        int columnOfComments = SHIFT_ONE_INDEX;
        List<AttendanceEntryDto> listOfAttendanceEntryDto = attendanceRegisterDto.getAttendanceEntries();
        for(AttendanceEntryDto attendanceEntryDto : listOfAttendanceEntryDto){
            setSummariesIntoAttendanceEntryDto(selectedSheet, attendanceEntryDto, columnNumber, rowNumber);
            setCommentsAndTotalDaysIntoAttendanceEntryDto(selectedSheet, attendanceEntryDto, columnOfComments, rowNumber);
            columnNumber = SUMMARIES_ENTRIES_COLUMN_START_INDEX;
            columnOfComments = SHIFT_ONE_INDEX;
            rowNumber = rowNumber+1;
        }
    }

    private static void setCommentsAndTotalDaysIntoAttendanceEntryDto(SSheet selectedSheet, AttendanceEntryDto attendanceEntryDto, int columnOfComments, int rowNumber) {
        attendanceEntryDto.setTotalDays(selectedSheet.getCell(makeCellAddress(columnOfComments,rowNumber)).getValue().toString());
        if(selectedSheet.getCell(makeCellAddress(columnOfComments+1,rowNumber)).getValue()!=null)
            attendanceEntryDto.setComments(selectedSheet.getCell(makeCellAddress(columnOfComments+1,rowNumber)).getValue().toString());
        if(selectedSheet.getCell(makeCellAddress(columnOfComments+1,rowNumber)).getValue()==null)
            attendanceEntryDto.setComments("");
    }

    static void setSummariesIntoAttendanceEntryDto(SSheet selectedSheet, AttendanceEntryDto attendanceEntryDto, int columnNumber, int rowNumber){
        attendanceEntryDto.setNOverTimeSummary(selectedSheet.getCell(makeCellAddress(columnNumber,rowNumber)).getValue().toString());
        attendanceEntryDto.setDOvertimeSummary(selectedSheet.getCell(makeCellAddress(columnNumber+1,rowNumber)).getValue().toString());
        attendanceEntryDto.setAbsent(selectedSheet.getCell(makeCellAddress(columnNumber+2,rowNumber)).getValue().toString());
        attendanceEntryDto.setSuspension(selectedSheet.getCell(makeCellAddress(columnNumber+3,rowNumber)).getValue().toString());
        attendanceEntryDto.setAbsentAndSuspension(selectedSheet.getCell(makeCellAddress(columnNumber+4,rowNumber)).getValue().toString());
        attendanceEntryDto.setCompLeave(selectedSheet.getCell(makeCellAddress(columnNumber+5,rowNumber)).getValue().toString());
        attendanceEntryDto.setAnnualLeave(selectedSheet.getCell(makeCellAddress(columnNumber+6,rowNumber)).getValue().toString());
        attendanceEntryDto.setAnnualPassageLeave(selectedSheet.getCell(makeCellAddress(columnNumber+7,rowNumber)).getValue().toString());
        attendanceEntryDto.setSpecialLeave(selectedSheet.getCell(makeCellAddress(columnNumber+8,rowNumber)).getValue().toString());
        attendanceEntryDto.setSickLeave(selectedSheet.getCell(makeCellAddress(columnNumber+9,rowNumber)).getValue().toString());
        attendanceEntryDto.setMaternityLeave(selectedSheet.getCell(makeCellAddress(columnNumber+10,rowNumber)).getValue().toString());
        attendanceEntryDto.setOtherUnpaidLeave(selectedSheet.getCell(makeCellAddress(columnNumber+11,rowNumber)).getValue().toString());
        attendanceEntryDto.setPresent(selectedSheet.getCell(makeCellAddress(columnNumber+12,rowNumber)).getValue().toString());
        attendanceEntryDto.setPresentOnFreeDay(selectedSheet.getCell(makeCellAddress(columnNumber+13,rowNumber)).getValue().toString());
        attendanceEntryDto.setAwayOnDuty(selectedSheet.getCell(makeCellAddress(columnNumber+14,rowNumber)).getValue().toString());
        attendanceEntryDto.setStandby(selectedSheet.getCell(makeCellAddress(columnNumber+15,rowNumber)).getValue().toString());
        attendanceEntryDto.setFreedays(selectedSheet.getCell(makeCellAddress(columnNumber+16,rowNumber)).getValue().toString());
        attendanceEntryDto.setTerminated(getStringValueFromSheet(selectedSheet,columnNumber+17, rowNumber));
        attendanceEntryDto.setResigned(getStringValueFromSheet(selectedSheet,columnNumber+18, rowNumber));
        attendanceEntryDto.setTransferred(getStringValueFromSheet(selectedSheet,columnNumber+19, rowNumber));
        attendanceEntryDto.setDeserted(getStringValueFromSheet(selectedSheet,columnNumber+20, rowNumber));
        attendanceEntryDto.setWrongDepartment(getStringValueFromSheet(selectedSheet,columnNumber+21, rowNumber));
        attendanceEntryDto.setExcessFreeDays(selectedSheet.getCell(makeCellAddress(columnNumber+22,rowNumber)).getValue().toString());
        attendanceEntryDto.setSDPresent(selectedSheet.getCell(makeCellAddress(columnNumber+23,rowNumber)).getValue().toString());
        attendanceEntryDto.setSDPresentOnFreeDay(selectedSheet.getCell(makeCellAddress(columnNumber+24,rowNumber)).getValue().toString());
        attendanceEntryDto.setSDTransferredAndPresent(selectedSheet.getCell(makeCellAddress(columnNumber+25,rowNumber)).getValue().toString());
        attendanceEntryDto.setSDWrongDepartmentAndPresent(selectedSheet.getCell(makeCellAddress(columnNumber+26,rowNumber)).getValue().toString());
        attendanceEntryDto.setTotalShiftDifferential(selectedSheet.getCell(makeCellAddress(columnNumber+27,rowNumber)).getValue().toString());
    }

    public static String getStringValueFromSheet(SSheet selectedSheet , int columnNumber, int rowNumber){
        return selectedSheet.getCell(makeCellAddress(columnNumber,rowNumber)).getValue().toString().startsWith("org.zkoss.zss.model.ErrorValue") ? null : selectedSheet.getCell(makeCellAddress(columnNumber,rowNumber)).getValue().toString();
    }
    public static String makeRangeAddress(int startColumnIndex, int startRowIndex, int endColumnIndex, int endRowIndex) {
        String rangeStartColumn = makeCellAddress(CellReference.convertNumToColString(startColumnIndex), startRowIndex);
        String rangeEndColumn = makeCellAddress(CellReference.convertNumToColString(endColumnIndex), endRowIndex);
        return rangeStartColumn.concat(":").concat(rangeEndColumn);
    }


    public static String makeRangeAddress(String startColumnName, int startRowIndex, String endColumnName, int endRowIndex) {
        String rangeStartColumn = makeCellAddress(startColumnName, startRowIndex);
        String rangeEndColumn = makeCellAddress(endColumnName, endRowIndex);
        return rangeStartColumn.concat(":").concat(rangeEndColumn);
    }

    public static String makeCellAddress(int columnIndex, int rowIndex) {
        return CellReference.convertNumToColString(columnIndex).concat(String.valueOf(rowIndex));
    }


    public static String makeCellAddress(String columnName, int rowIndex) {
        return columnName.concat(String.valueOf(rowIndex));
    }

    public static void setMaximumvisibleRowsForSheet(int size, Spreadsheet attendanceRegisterSpreadsheet) {
        attendanceRegisterSpreadsheet.setMaxVisibleRows(size + NUMBER_OF_EXTRA_ROWS_TO_BE_MADE_VISIBLE);
    }

    public static void setFormulaeToCellsOfSheet(int noOfRows, Sheet sheet){
        List<String> listOfFormulae = getListOfFormulae(sheet);
        setFormulaToFirstColumn(sheet, noOfRows, listOfFormulae);
        setFormulaToSummaryColumn(sheet, noOfRows, listOfFormulae);
    }

    private static void setFormulaToSummaryColumn(Sheet sheet, int noOfRows, List<String> listOfFormulae) {
        int START_INDEX = EMPLOYEE_START_ROW_INDEX;
        int START_INDEX_COLUMN = SUMMARIES_ENTRIES_COLUMN_START_INDEX;
        for(int i = SUMMARIES_ENTRIES_COLUMN_START_INDEX,k = START_INDEX_LISTOFFORMULAE; i<=SUMMARIES_ENTRIES_COLUMN_END_INDEX;i++,k++){
            for(int j = SHIFT_ONE_INDEX; j<noOfRows-1; j++) {
                sheet.getInternalSheet().getCell(START_INDEX,START_INDEX_COLUMN).setFormulaValue(listOfFormulae.get(k).replaceAll("6", String.valueOf(START_INDEX+1)));
                START_INDEX = START_INDEX+1;
            }
            START_INDEX_COLUMN = START_INDEX_COLUMN+1;
            START_INDEX = EMPLOYEE_START_ROW_INDEX;
        }
    }

    private static void setFormulaToFirstColumn(Sheet sheet, int noOfRows, List<String> listOfFormulae) {
        int START_INDEX = EMPLOYEE_START_ROW_INDEX;
/*        Range rangeForFirstColumn = Ranges.range(sheet, makeCellAddress(TOTAL_DAYS_COLUMN, EMPLOYEE_START_ROW_INDEX));
        String formulaOfFirstColumn = rangeForFirstColumn.getCellEditText().replace("=","").trim();*/
        String formula = sheet.getInternalSheet().getCell(makeCellAddress(TOTAL_DAYS_COLUMN, EMPLOYEE_START_ROW_INDEX)).getFormulaValue();
        for(int i = SHIFT_ONE_INDEX;i<noOfRows-1;i++) {
            sheet.getInternalSheet().getCell(START_INDEX,CellReference.convertColStringToIndex(TOTAL_DAYS_COLUMN)).setFormulaValue(formula.replaceAll("6",String.valueOf(START_INDEX+1)));
            START_INDEX = START_INDEX+1;
        }
    }

    private static List<String> getListOfFormulae(Sheet sheet) {
        List<String> listOfFormulae = Lists.newArrayList();
        for(int i = SUMMARIES_ENTRIES_COLUMN_START_INDEX; i<=SUMMARIES_ENTRIES_COLUMN_END_INDEX; i++) {
            Range range = Ranges.range(sheet, makeCellAddress(i, 6));
            listOfFormulae.add(range.getCellEditText().replace("=","").trim());
        }
        return listOfFormulae;
    }
}
