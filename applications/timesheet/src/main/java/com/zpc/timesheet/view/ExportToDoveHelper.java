package com.zpc.timesheet.view;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.zpc.timesheet.dto.AttendanceEntryDto;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import com.zpc.timesheet.query.TimesheetFinder;
import org.zkoss.zss.api.Exporter;
import org.zkoss.zss.api.Exporters;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Filedownload;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 10/28/2014.
 */
final public class ExportToDoveHelper {
    static int thruMonth;
    private static String pathToExport;
    static {
        Properties properties = new Properties();
        try {
            properties.load(ExportToDoveHelper.class.getClassLoader().getResourceAsStream("csv.export.path.properties"));
            pathToExport = properties.getProperty("pathToExport");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void exportLeaveRecordsToCSVFormat(List<AttendanceRegisterDto> attendanceRegisters, TimesheetFinder timesheetFinder) {
        List<String[]> records = Lists.newArrayList();
        records.add(new String[]{"EmpNo","FULLNAME","MONEYCODE","MONEYNAME","MONEYTYPE","AccuredDays","DaysToTake",
                "DateTaken","DeptCode","JobCategoryCode","PayPointCode","DivCode","PAYPOINT","DIVISION","DEPARTMENT","JobCategory"});
        for(AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            thruMonth = attendanceRegister.thruDate.monthOfYear().get();
            List<AttendanceEntryDto> attendanceEntryDtoList = attendanceRegister.attendanceEntries;
            for (AttendanceEntryDto attendanceEntryDto : attendanceEntryDtoList) {
                String divisionId = getDivisionIdBasedOnEmployeeId(attendanceEntryDto.employeeId, timesheetFinder);
                records.add(new String[]{attendanceEntryDto.employeeId,
                                         attendanceEntryDto.firstName+" "+attendanceEntryDto.lastName,
                        "","","",calculateAccuredDays(getAllotedLeaves(attendanceEntryDto.employeeId, timesheetFinder),thruMonth),
                        attendanceEntryDto.annualLeave,
                        calculateDateTaken(getAllotedLeaves(attendanceEntryDto.employeeId, timesheetFinder), attendanceEntryDto),
                        attendanceRegister.departmentCode,"",attendanceRegister.departmentCode,getDivisionCode(divisionId, timesheetFinder),
                        attendanceRegister.departmentName,divisionId,attendanceRegister.departmentName,getJobCategoryBasedOnEmployeeId(attendanceEntryDto.employeeId, timesheetFinder)});
            }
        }
        exportToCSVFormat(records, pathToExport+"/Leave Taking.csv");
    }

    private static String getJobCategoryBasedOnEmployeeId(String employeeId, TimesheetFinder timesheetFinder) {
        Preconditions.checkNotNull(employeeId);
        String positionCategory = null;
        List<Map<String,Object>> person = timesheetFinder.getPositionCategoryBasedOnPartyId(employeeId);
        for (Map<String, Object> postion : person) {
            if(postion.get("positionCategory") == null)
                return null;
            else
                positionCategory = postion.get("positionCategory").toString();
        }
        return positionCategory;
    }

    private static String getDivisionIdBasedOnEmployeeId(String employeeId, TimesheetFinder timesheetFinder) {
        String emplPositionId = null;
        List<Map<String,Object>> emplPositionFulfillment = timesheetFinder.getEmployeePositionIdBasedOnEmployeeId(employeeId);
        if(emplPositionFulfillment == null){
            return null;
        }
        for (Map<String, Object> emplPostion : emplPositionFulfillment) {
            if(emplPostion.get("emplPositionId") == null)
                return null;
            else
                emplPositionId = emplPostion.get("emplPositionId").toString();
        }
        return getDivisionIdBasedOnEmplPositionId(emplPositionId, timesheetFinder);
    }

    private static String getDivisionIdBasedOnEmplPositionId(String emplPositionId, TimesheetFinder timesheetFinder) {
        String divisionId = null;
        List<Map<String,Object>> emplPosition = timesheetFinder.getDivisionIdBasedOnEmplPositionId(emplPositionId);
        for (Map<String, Object> emplPostion : emplPosition) {
            if(emplPostion.get("divisionId") == null)
                return null;
            else
                divisionId = emplPostion.get("divisionId").toString();
        }
        return divisionId;
    }

    private static String getDivisionCode(String divisionId, TimesheetFinder timesheetFinder) {
        if(divisionId == null)
            return "";
        String divisionCode = null;
        List<Map<String,Object>> departmentDivision = timesheetFinder.getDivisionBasedOnDivisionId(divisionId);
        for (Map<String, Object> division : departmentDivision) {
            if(division.get("divisionCode") == null)
                return null;
            else
                divisionCode = division.get("divisionCode").toString();
        }
        return divisionCode;
    }

    static String calculateAccuredDays(Double allotedDays, double thruMonth){
        Preconditions.checkNotNull(allotedDays);
        Preconditions.checkNotNull(thruMonth);
        double accured = (allotedDays/12)*thruMonth;
        return String.valueOf(String.valueOf((Math.ceil(accured * 2) / 2)));
    }

    private static String calculateDateTaken(Double allotedDays, AttendanceEntryDto attendanceEntryDto) {
        Preconditions.checkNotNull(allotedDays);
        Preconditions.checkNotNull(attendanceEntryDto);
        double annualLeave = Double.parseDouble(attendanceEntryDto.getAnnualLeave().trim());
        return String.valueOf(allotedDays- annualLeave);
    }

    private static Double getAllotedLeaves(String employeeId, TimesheetFinder timesheetFinder) {
        return timesheetFinder.getAnnualLeaveDetailsOfEmployee(employeeId, "10024");
    }

    private static int getLeaveDetailOfEmployee(String partyId, String leaveType){
        return 25;
    }

    static void exportAbsentDetailsToCSVFormat(List<AttendanceRegisterDto> attendanceRegisters) {
        List<String[]> records = Lists.newArrayList();
        records.add(new String[]{"EmpNo","AbsentDays","SickDays"});
        for(AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            List<AttendanceEntryDto> attendanceEntryDtoList = attendanceRegister.attendanceEntries;
            for (AttendanceEntryDto attendanceEntryDto : attendanceEntryDtoList) {
                records.add(new String[]{attendanceEntryDto.getEmployeeId(), attendanceEntryDto.getAbsent(), attendanceEntryDto.getSickLeave()});
            }
        }
        exportToCSVFormat(records, pathToExport+"/Absentism.csv");
    }

    static void exportTimeCardToCSVFormat(List<AttendanceRegisterDto> attendanceRegisters) {
        List<String[]> records = Lists.newArrayList();
        records.add(new String[]{"EmpNo","Normal","OverTime","Holiday","Night","TLAllowance","AttendAllowance"});
        for(AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            List<AttendanceEntryDto> attendanceEntryDtoList = attendanceRegister.attendanceEntries;
            for (AttendanceEntryDto attendanceEntryDto : attendanceEntryDtoList) {
                records.add(new String[]{attendanceEntryDto.getEmployeeId(), attendanceEntryDto.getPresent(), attendanceEntryDto.getNOverTimeSummary(), attendanceEntryDto.getDOvertimeSummary(),
                        "", "", ""});
            }
        }
        exportToCSVFormat(records,pathToExport+"/TimeCard.csv");
    }
    static void exportToCSVFormat(List<String[]> records, String fileName) {
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(new File(fileName)),',');
            csvWriter.writeAll(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void zipTheExportedFile() {
        try {
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(pathToExport+"/export.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            File dir = new File(pathToExport);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if(files[i].getName().endsWith(".csv")) {
                    FileInputStream fis = new FileInputStream(files[i]);
                    zos.putNextEntry(new ZipEntry(files[i].getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
            }
            zos.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    static void downloadZipExportedFile() {
        FileInputStream inputStream;
        try {
            File dosfile = new File(pathToExport+"/export.zip");
            if (dosfile.exists()) {
                inputStream = new FileInputStream(dosfile);
                Filedownload.save(inputStream, new MimetypesFileTypeMap().getContentType(dosfile), dosfile.getName());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static String extractDetailsOfTimesheetGenerateCSVAndZipThem(List<AttendanceRegisterDto> attendanceRegisters, TimesheetFinder timesheetFinder){
        exportTimeCardToCSVFormat(attendanceRegisters);
        exportAbsentDetailsToCSVFormat(attendanceRegisters);
        exportLeaveRecordsToCSVFormat(attendanceRegisters, timesheetFinder);
        zipTheExportedFile();
        return pathToExport+"/export.zip";
    }

    static String saveConsolidatedSheet(Spreadsheet adminAttendanceRegisterSpreadsheet) {
        ByteArrayOutputStream bout = null;
        FileOutputStream outputStream = null;
        try {
            bout = new ByteArrayOutputStream();
            Book book = adminAttendanceRegisterSpreadsheet.getBook();
            outputStream = new FileOutputStream(pathToExport+"/ConsolidatedAttendanceRegister.xlsx");
            Exporter exporter = Exporters.getExporter();
            exporter.export(book, bout);
            bout.writeTo(outputStream);
            return pathToExport+"/ConsolidatedAttendanceRegister.xlsx";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
