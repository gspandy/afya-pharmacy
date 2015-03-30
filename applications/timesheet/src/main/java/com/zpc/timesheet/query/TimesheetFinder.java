package com.zpc.timesheet.query;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import com.zpc.sharedkernel.ofbiz.adapters.OfbizResultTransformer;
import com.zpc.timesheet.domain.model.Timesheet;
import com.zpc.timesheet.dto.*;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.GenericServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Messagebox;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Author: Nthdimenzion
 */

@Finder
@Component
public class TimesheetFinder {

    Logger log = LoggerFactory.getLogger(TimesheetFinder.class);

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    static final String  TIMESHEET_STATUS ="RELEASED";

    public static final String queryForListOfSchedules=" SELECT id,description,from_date as fromDate,status,to_date AS toDate " +
            " FROM schedule " +
            " order by to_date DESC" ;

    public static final String queryForListOfTimesheets =" SELECT s.description,s.from_date as fromDate,t.status,s.to_date AS toDate, t.id AS timesheetId" +
            " FROM timesheet_entry AS t JOIN schedule AS s ON t.schedule_id = s.id order by s.to_date" ;

    public static final String queryFindAttendanceRegistersByDepartment = " SELECT atr.id AS attendanceRegisterId ,atr.department_id AS departmentId,s.from_date AS fromDate,s.to_date AS thruDate,s.description AS description" +
            " ,atr.status AS status " +
            " FROM attendance_register atr JOIN timesheet_entry ts ON atr.TIMESHEET_ID = ts.ID JOIN schedule s ON ts.schedule_id = s.id  " +
            " WHERE atr.department_id IN (:departments) " +
            " ORDER BY s.to_date DESC";

    public static final String queryFindAttendanceRegistersById = " SELECT atr.id AS attendanceRegisterId ,atr.department_id AS departmentId,s.from_date AS fromDate,s.to_date AS thruDate,s.description AS description" +
            " ,atr.status AS status " +
            " FROM attendance_register atr JOIN timesheet_entry ts ON atr.TIMESHEET_ID = ts.ID JOIN schedule s ON ts.schedule_id = s.id  " +
            " WHERE atr.id = :attendanceRegisterId ";

    public static final String queryFindAttendanceEntriesForGivenAttendanceRegister =" SELECT ID AS attendanceEntryId,eae.employee_id AS employeeId,eae.comments AS comments,eae.n_over_time AS nOvertimeSummary,eae.d_overtime AS dOvertimeSummary\n" +
            ",SDPRESENT AS SDPresent, SDPRESENT_ON_FREE_DAY AS SDPresentOnFreeDay, SDTRANSFERRED_AND_PRESENT AS SDTransferredAndPresent, SDWRONG_DEPARTMENT_AND_PRESENT AS SDWrongDepartmentAndPresent, \n" +
            "ABSENT AS absent,SUSPENSION AS suspension, ABSENT_AND_SUSPENSION AS absentAndSuspension,COMP_LEAVE AS compLeave,ANNUAL_LEAVE AS annualLeave,\n" +
            " ANNUAL_PASSAGE_LEAVE AS annualPassageLeave,SPECIAL_LEAVE AS specialLeave,SICK_LEAVE AS sickLeave, MATERNITY_LEAVE AS maternityLeave,TOTAL_DAYS AS totalDays,OTHER_UNPAID_LEAVE AS otherUnpaidLeave, \n" +
            " PRESENT AS present, PRESENT_ON_FREE_DAY AS presentOnFreeDay, AWAY_ON_DUTY AS awayOnDuty, STANDBY AS standby,FREEDAYS AS freedays, EXCESS_FREE_DAYS AS excessFreeDays, TOTAL_SHIFT_DIFFERENTIAL as totalShiftDifferential," +
            " TERMINATED_DATE AS terminated_date, RESIGNED AS resigned, TRANSFERRED AS transferred, DESERTED AS deserted, WRONG_DEPARTMENT as wrongDepartment,eade.d_overtime dOvertime,\n" +
            " eade.n_overtime AS nOvertime,eade.DATE AS DATE,eade.shift_one AS shiftOne,eade.SHIFT_TWO AS shiftTwo FROM employee_attendance_entry eae JOIN employeeattendanceentry_day_entries eade \n" +
            " ON eae.ID = eade.EMPLOYEEATTENDANCEENTRY WHERE eae.attendance_register_id = :attendanceRegisterId ORDER BY  eae.employee_id,eade.date";

    public static final String queryTimesheetIdForGivenAttendanceRegister = " SELECT timesheet_id FROM attendance_register WHERE id = :attendanceRegisterId";

    public static final String queryToFetchScheduleBasedOnScheduleId = "SELECT s.status FROM SCHEDULE AS s WHERE s.id = :scheduleId";

    public static final String queryFindAttendanceRegistersByTimesheet = "SELECT id FROM attendance_register WHERE timesheet_id = :timesheetId";

    public static final String queryFindTimesheetStatusById = "SELECT status FROM timesheet_entry WHERE id = :timesheetId";

    public static final String queryFindTimesheetByScheduleId = "SELECT id FROM timesheet_entry WHERE SCHEDULE_ID = :scheduleId";

    public static final String queryToFindDepartmentIdYetToSubmitAttendanceRegister = "SELECT department_id AS departmentId, status FROM attendance_register where timesheet_id = :timesheetId";


    @Autowired
    public TimesheetFinder(DataSource dataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String,Object>> getDepartmentsManagedBy(String managerId,boolean isAdmin){
        Preconditions.checkNotNull(managerId);
        boolean isTimeKeeper = CheckIfTimekeeper(managerId);
        Map result = Maps.newHashMap();
        try {
            if(isAdmin && isTimeKeeper){
                isTimeKeeper = false;
            }
            /*if(!isAdmin && isTimeKeeper){
                isTimeKeeper = false;
            }*/
            if(isTimeKeeper){
                Map employeeDepartment = UtilMisc.toMap("userLogin", OfbizGateway.getUser(), "viewIndex", 0, "viewSize", 1000,
                        "inputFields", UtilMisc.toMap("roleTypeIdFrom", "ORGANIZATION_ROLE", "roleTypeIdTo", "EMPLOYEE", "partyIdTo", managerId, "thruDate", null),
                        "entityName", "PartyRelationshipAndDetail");
                result = OfbizGateway.getDispatcher().runSync("performFindList", employeeDepartment);
                //System.out.println("\n\n\n\n==============>getDepartmentsManagedBy result(timekeeper)=======>\n"+result+"\n\n\n\n");
                if (result.get("listSize") == 0) {
                    Messagebox.show("No departments associated", "Notification",
                            new Messagebox.Button[]{Messagebox.Button.OK}, null,
                            new org.zkoss.zk.ui.event.EventListener<Messagebox.ClickEvent>() {
                                public void onEvent(Messagebox.ClickEvent event) {
                                    if ("onOK".equals(event.getName())) {
                                        return;
                                    }
                                }
                            });
                }
            }
            else {
                if (isAdmin) {
                    Map allDepartment = UtilMisc.toMap("userLogin", OfbizGateway.getUser(), "viewIndex", 0, "viewSize", 1000, "inputFields", UtilMisc.toMap("roleTypeId", "ORGANIZATION_ROLE"), "entityName", "PartyRoleAndPartyDetail");
                    result = OfbizGateway.getDispatcher().runSync("performFindList", allDepartment);
                    //System.out.println("\n\n\n\n==============>getDepartmentsManagedBy result(admin)=======>\n"+result+"\n\n\n\n");
                } else {
                    Map employeeDepartment = UtilMisc.toMap("userLogin", OfbizGateway.getUser(), "viewIndex", 0, "viewSize", 1000,
                            "inputFields", UtilMisc.toMap("roleTypeIdFrom", "ORGANIZATION_ROLE", "roleTypeIdTo", "MANAGER", "partyIdTo", managerId, "thruDate", null),
                            "entityName", "PartyRelationshipAndDetail");
                    result = OfbizGateway.getDispatcher().runSync("performFindList", employeeDepartment);
                    //System.out.println("\n\n\n\n==============>getDepartmentsManagedBy result(manager)=======>\n"+result+"\n\n\n\n");
                    if (result.get("listSize") == 0) {
                        Messagebox.show("No departments associated", "Notification",
                                new Messagebox.Button[]{Messagebox.Button.OK}, null,
                                new org.zkoss.zk.ui.event.EventListener<Messagebox.ClickEvent>() {
                                    public void onEvent(Messagebox.ClickEvent event) {
                                        if ("onOK".equals(event.getName())) {
                                            return;
                                        }
                                    }
                                });
                    }
                }
            }
        }catch (GenericServiceException exception){
            throw new RuntimeException(exception);
        }
        return OfbizResultTransformer.TransformOfbizDepartmentMap(result, isAdmin, isTimeKeeper);

    }

    public boolean CheckIfTimekeeper(String partyId) {
        Map result = Maps.newHashMap();
        boolean isTimekeeper = true;
        try {
            Map partyRole = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),"viewIndex",0,"viewSize",1000,"inputFields", UtilMisc.toMap("partyId", partyId,"roleTypeId","TIME_KEEPER"), "entityName", "PartyRole");
            result = OfbizGateway.getDispatcher().runSync("performFindList", partyRole);
            //System.out.println("partyRole==========="+result+"\n\n\n\n\n");
            if (result.get("listSize") == 0){
                isTimekeeper = false;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return isTimekeeper;
    }

    public List<Map<String, Object>> getEmployeesInDepartment(String departmentId, Date startDate) {
        Map result = Maps.newHashMap();
        List<String> orderBy = Lists.newLinkedList();
        Set<String> hods = Sets.newHashSet();
        orderBy.add("partyId");
        Map allEmployeeOfSelectedDepartment = UtilMisc.toMap("userLogin", OfbizGateway.getUser(), "viewIndex", 0, "viewSize", 1000,
                "inputFields", UtilMisc.toMap("partyIdFrom", departmentId, "thruDate:", null), "entityName", "EmploymentAndPerson","orderBy","partyIdTo");
        try {
            result = OfbizGateway.getDispatcher().runSync("performFindList",allEmployeeOfSelectedDepartment);
            hods = getHodOfTheDepartment(departmentId);
            //System.out.println("\n\n\n HODS"+hods+"\n\n");
            //System.out.println("\n\n\n\n==============>getEmployeesInDepartment=======>\n"+result+"\n\n\n\n");
        } catch (GenericServiceException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("\n\nTransformedMap=============="+OfbizResultTransformer.TransformOfbizEmployeeMap(result)+"\n\n\n");
        return OfbizResultTransformer.TransformOfbizEmployeeMap(result, startDate, hods);
    }

    private Set<String> getHodOfTheDepartment(String departmentId) {
        Preconditions.checkNotNull(departmentId);
        Map result = Maps.newHashMap();
        try {
            Map hod = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),"viewIndex", 0, "viewSize", 1000,"inputFields", UtilMisc.toMap("emplPositionTypeId", "HOD"), "entityName", "EmplPositionEmplPositionFulfillment","orderBy","partyId");
            result = OfbizGateway.getDispatcher().runSync("performFindList", hod);
            //System.out.println("\n\n\n\nhod=============="+result+" \n\n");
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return OfbizResultTransformer.TransformEmplPositonMap(result);
    }

    public List<Map<String,Object>> getDepartmentBasedOnDepartmentId(String departmentId){
        Preconditions.checkNotNull(departmentId);
        Map result = Maps.newHashMap();
        try {
            Map department = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),"viewIndex",0,"viewSize",1000,"inputFields", UtilMisc.toMap("departmentId", departmentId), "entityName", "DepartmentPosition");
            result = OfbizGateway.getDispatcher().runSync("performFindList", department);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        //System.out.println("\n\n\n\ndepartment============>"+result);
        return OfbizResultTransformer.TransformDepartmentMapFetchedBasedOnDepartmentId(result);
    }

    public Double getAnnualLeaveDetailsOfEmployee(String partyId, String leaveType) {
        Map result = Maps.newHashMap();
        try {
            result = OfbizGateway.getDispatcher().runSync("getEmployeeLeaveLimitForLeaveType",UtilMisc.toMap("employeeId", partyId, "leaveTypeId", leaveType));
            //System.out.println("\n\n\ngetEmployeeLeaveLimitForLeaveType========>"+result+"\n"+partyId+"\n\n");
        }catch (GenericServiceException e) {
            e.printStackTrace();
        }
        if((Double)result.get("leaveLimit") == null)
           return Double.valueOf(0) ;
        else
            return (Double)result.get("leaveLimit");
    }

    public List<Map<String,Object>> getEmployeePositionIdBasedOnEmployeeId(String employeeId){
        Preconditions.checkNotNull(employeeId);
        Map result = Maps.newHashMap();
        try {
            Map emplPositionFulfillment = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),"viewIndex",0,"viewSize",1000,"inputFields", UtilMisc.toMap("partyId", employeeId), "entityName", "EmplPositionFulfillment");
            result = OfbizGateway.getDispatcher().runSync("performFindList", emplPositionFulfillment);
            //System.out.println("\n\n\nemplPositionFulfillment=========>"+result);
            if(result.get("listSize") == 0){
                return null;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return OfbizResultTransformer.TransformEmplPositionFulfillmentMapFetchedBasedOnDepartmentId(result);
    }

    public List<Map<String,Object>> getDivisionBasedOnDivisionId(String divisionId){
        Preconditions.checkNotNull(divisionId);
        Map result = Maps.newHashMap();
        try {
            Map division = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),"viewIndex",0,"viewSize",1000,"inputFields", UtilMisc.toMap("divisionId", divisionId), "entityName", "Division");
            result = OfbizGateway.getDispatcher().runSync("performFindList", division);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        //System.out.println("\n\n\n\ndivision============>"+result);
        return OfbizResultTransformer.TransformDivisionMapFetchedBasedOnDepartmentId(result);
    }

    public List<Map<String,Object>> getDivisionIdBasedOnEmplPositionId(String emplPositionId){
        Preconditions.checkNotNull(emplPositionId);
        Map result = Maps.newHashMap();
        try {
            Map emplPosition = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),"viewIndex",0,"viewSize",1000,"inputFields", UtilMisc.toMap("emplPositionId", emplPositionId), "entityName", "EmplPosition");
            result = OfbizGateway.getDispatcher().runSync("performFindList", emplPosition);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        //System.out.println("\n\n\n\nemplPosition============>"+result);
        return OfbizResultTransformer.TransformEmplPositionMapFetchedBasedOnDepartmentId(result);
    }

    public List<Map<String, Object>> getPositionCategoryBasedOnPartyId(String employeeId) {
        Preconditions.checkNotNull(employeeId);
        Map result = Maps.newHashMap();
        try {
            Map person = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),"viewIndex",0,"viewSize",1000,"inputFields", UtilMisc.toMap("partyId", employeeId), "entityName", "Person");
            result = OfbizGateway.getDispatcher().runSync("performFindList", person);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        //System.out.println("\n\n\n\nPerson============>"+result);
        return OfbizResultTransformer.TransformPersonMapFetchedBasedPartyId(result);
    }

    public List<AttendanceRegisterDto> prepareTimesheetView(String timesheetId,String managerId, boolean isAdmin){
        List<String> attendanceRegisterIds = namedParameterJdbcTemplate.queryForList(queryFindAttendanceRegistersByTimesheet, Collections.singletonMap("timesheetId", timesheetId), String.class);
        List<AttendanceRegisterDto> timesheet = Lists.newArrayList();
        for (String attendanceRegisterId : attendanceRegisterIds) {
            AttendanceRegisterDto attendanceRegisterDto = prepareAttendanceRegisterView(attendanceRegisterId,managerId,isAdmin);
            timesheet.add(attendanceRegisterDto);
        }
        return timesheet;
    }

    public AttendanceRegisterDto prepareAttendanceRegisterView(String attendanceRegisterId, String managerId, boolean isAdmin) {
        AttendanceRegisterDto attendanceRegister = getAttendanceRegister(attendanceRegisterId);
        String timeSheetId = getTimesheetId(attendanceRegisterId);
        attendanceRegister.timesheetId = timeSheetId;
        if(isAdmin) {
            final List<Map<String, Object>> departments = getDepartmentsManagedBy(managerId, isAdmin);
            attendanceRegister = enhanceWithDepartmentName(Lists.newArrayList(attendanceRegister), departments).get(0);
            setDepartmentCodeToAttendanceRegister(attendanceRegister);
        }else {
            setDepartmentNameToAttendanceRegister(attendanceRegister);
        }
        /*final List<Map<String, Object>> departments = getDepartmentsManagedBy(managerId, isAdmin);
        attendanceRegister = enhanceWithDepartmentName(Lists.newArrayList(attendanceRegister), departments).get(0);*/
        return attendanceRegister;
    }

    private String getTimesheetId(String attendanceRegisterId) {
        return namedParameterJdbcTemplate.queryForObject(queryTimesheetIdForGivenAttendanceRegister,Collections.singletonMap("attendanceRegisterId",attendanceRegisterId),String.class);
    }

    private AttendanceRegisterDto getAttendanceRegister(final String attendanceRegisterId) {
        final AttendanceRegisterDto attendanceRegister = namedParameterJdbcTemplate.queryForObject(queryFindAttendanceRegistersById, Collections.singletonMap("attendanceRegisterId", attendanceRegisterId), new BeanPropertyRowMapper<>(AttendanceRegisterDto.class));
        attendanceRegister.attendanceEntries = getAttendanceEntries(attendanceRegister, attendanceRegister.getDepartmentId());
        return attendanceRegister;
    }

    private List<AttendanceEntryDto> getAttendanceEntries(final AttendanceRegisterDto attendanceRegister, final String departmentId) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("attendanceRegisterId",attendanceRegister.attendanceRegisterId);
        final List<AttendanceEntryDto> attendanceEntryDtos = namedParameterJdbcTemplate.query(queryFindAttendanceEntriesForGivenAttendanceRegister, parameterSource, new AttendanceEntryResultExtractor());
        List<AttendanceEntryDto> listOfAttendanceEntryDtos = findIfAnyNewEmplyeeAddedAfterTimesheetGeneratedAndAddToAttendanceRegister(attendanceEntryDtos, departmentId, attendanceRegister);
        attendanceEntryDtos.addAll(listOfAttendanceEntryDtos);
        return attendanceEntryDtos;
    }

    private List<AttendanceEntryDto> findIfAnyNewEmplyeeAddedAfterTimesheetGeneratedAndAddToAttendanceRegister(List<AttendanceEntryDto> attendanceEntryDtos, String departmentId, AttendanceRegisterDto attendanceRegister){
        List<AttendanceEntryDto> listOfAttendanceEntryDtos = new ArrayList<AttendanceEntryDto>();
        final Set<String> employeeIdsOfEmployeeInAttendenceEntry = extractEmployeeIds(attendanceEntryDtos);
        final List<Map<String, Object>> employees = getEmployeesInDepartment(departmentId, attendanceRegister.fromDate.toDate());
        final Set<String> employeeIdsFromOfbiz = Timesheet.extractEmployeeIds(employees);
        List<DayEntryDto> listOfEmptyDayEntryDto = createListOfEmptyDayEntryDtos(attendanceRegister);
        employeeIdsFromOfbiz.removeAll(employeeIdsOfEmployeeInAttendenceEntry);
        for(Map<String, Object> employee : employees) {
            for (String empIdOfNewEmp: employeeIdsFromOfbiz) {
                if(((String)employee.get("employeeId")).equals(empIdOfNewEmp)){
                    AttendanceEntryDto attendanceEntryDto = new AttendanceEntryDto();
                    attendanceEntryDto.setEmployeeId(empIdOfNewEmp);
                    attendanceEntryDto.setFirstName((String) employee.get("firstName"));
                    attendanceEntryDto.setLastName((String) employee.get("lastName"));
                    attendanceEntryDto.setDayEntryDtos(listOfEmptyDayEntryDto);
                    listOfAttendanceEntryDtos.add(attendanceEntryDto);
                }
            }
        }
        return listOfAttendanceEntryDtos;
    }

    private List<DayEntryDto> createListOfEmptyDayEntryDtos(AttendanceRegisterDto attendanceRegisterDto){
        List<DayEntryDto> listOfEmptyDayEntryDto = new ArrayList<>();
        int noOfDaysBetweenSchedule = Days.daysBetween(attendanceRegisterDto.fromDate, attendanceRegisterDto.thruDate.plusDays(1)).getDays();
        LocalDate date = attendanceRegisterDto.fromDate;
        for(int i=0; i<noOfDaysBetweenSchedule; i++){
            DayEntryDto dayEntryDto = new DayEntryDto();
            dayEntryDto.date = date.toString();
            date = date.plusDays(1);
            listOfEmptyDayEntryDto.add(dayEntryDto);
        }
        return listOfEmptyDayEntryDto;
    }

    public static Set<String> extractEmployeeIds(List<AttendanceEntryDto> attendanceEntryDtos){
        final Set<String> employeeIds = Sets.newHashSet();
        for (AttendanceEntryDto attendanceEntryDto : attendanceEntryDtos) {
            employeeIds.add(attendanceEntryDto.getEmployeeId());
        }
        return employeeIds;
    }

    public String getTimesheetStatusById(String timesheetId) {
        return namedParameterJdbcTemplate.queryForObject(queryFindTimesheetStatusById,Collections.singletonMap("timesheetId",timesheetId),String.class);
    }

    public String getTimesheetIdForGivenSchedule(String scheduleId) {
        return namedParameterJdbcTemplate.queryForObject(queryFindTimesheetByScheduleId,Collections.singletonMap("scheduleId",scheduleId),String.class);
    }

    private class AttendanceEntryResultExtractor implements ResultSetExtractor<List<AttendanceEntryDto>> {
        Map<String,AttendanceEntryDto> attendanceEntryDtoMap = Maps.newHashMap();
        @Override
        public List<AttendanceEntryDto> extractData(ResultSet rs) throws SQLException, DataAccessException {
            while(rs.next()){
                AttendanceEntryDto attendanceEntryDto = attendanceEntryDtoMap.get(rs.getString("attendanceEntryId"));
                if(attendanceEntryDto == null){
                    attendanceEntryDto = new AttendanceEntryDto();
                    attendanceEntryDto.attendanceEntryId = rs.getString("attendanceEntryId");
                    attendanceEntryDto.employeeId = rs.getString("employeeId");
                    attendanceEntryDto.comments = rs.getString("comments");
                    attendanceEntryDto.SDPresent = rs.getString("SDPresent");
                    attendanceEntryDto.SDPresentOnFreeDay = rs.getString("SDPresentOnFreeDay");
                    attendanceEntryDto.SDTransferredAndPresent = rs.getString("SDTransferredAndPresent");
                    attendanceEntryDto.SDWrongDepartmentAndPresent= rs.getString("SDWrongDepartmentAndPresent");
                    attendanceEntryDto.absent = rs.getString("absent");
                    attendanceEntryDto.nOverTimeSummary = rs.getString("nOverTimeSummary");
                    attendanceEntryDto.dOvertimeSummary= rs.getString("dOvertimeSummary");
                    attendanceEntryDto.suspension = rs.getString("suspension");
                    attendanceEntryDto.absentAndSuspension = rs.getString("absentAndSuspension");
                    attendanceEntryDto.compLeave = rs.getString("compLeave");
                    attendanceEntryDto.annualLeave = rs.getString("annualLeave");
                    attendanceEntryDto.annualPassageLeave = rs.getString("annualPassageLeave");
                    attendanceEntryDto.specialLeave = rs.getString("specialLeave");
                    attendanceEntryDto.sickLeave = rs.getString("sickLeave");
                    attendanceEntryDto.maternityLeave = rs.getString("maternityLeave");
                    attendanceEntryDto.otherUnpaidLeave = rs.getString("otherUnpaidLeave");
                    attendanceEntryDto.present = rs.getString("present");
                    attendanceEntryDto.presentOnFreeDay = rs.getString("presentOnFreeDay");
                    attendanceEntryDto.awayOnDuty = rs.getString("awayOnDuty");
                    attendanceEntryDto.standby = rs.getString("standby");
                    attendanceEntryDto.freedays = rs.getString("freedays");
                    attendanceEntryDto.excessFreeDays = rs.getString("excessFreeDays");
                    attendanceEntryDto.totalDays = rs.getString("totalDays");
                    attendanceEntryDto.totalShiftDifferential = rs.getString("totalShiftDifferential");
                    attendanceEntryDto.terminated = rs.getString("terminated_date");
                    attendanceEntryDto.resigned = rs.getString("resigned");
                    attendanceEntryDto.transferred = rs.getString("transferred");
                    attendanceEntryDto.deserted = rs.getString("deserted");
                    attendanceEntryDto.wrongDepartment = rs.getString("wrongDepartment");
                    attendanceEntryDtoMap.put(attendanceEntryDto.attendanceEntryId,attendanceEntryDto);
                }
                DayEntryDto dayEntryDto = new DayEntryDto();
                dayEntryDto.date = rs.getString("date");
                dayEntryDto.dOvertime = rs.getString("dOvertime");
                dayEntryDto.nOvertime= rs.getString("nOverTime");
                dayEntryDto.shiftOne= rs.getString("shiftOne");
                dayEntryDto.shiftTwo= rs.getString("shiftTwo");
                attendanceEntryDto.dayEntryDtos.add(dayEntryDto);
            }

            return Lists.newArrayList(attendanceEntryDtoMap.values());
        }
    }

    public List<ScheduleDto> getAllSchedules(){
        List<ScheduleDto> rows  = namedParameterJdbcTemplate.query(queryForListOfSchedules,new BeanPropertyRowMapper<>(ScheduleDto.class));
        return rows;
    }

    public String getScheduleBasedOnScheduleId(String scheduleId) {
        return namedParameterJdbcTemplate.queryForObject(queryToFetchScheduleBasedOnScheduleId,Collections.singletonMap("scheduleId", scheduleId),String.class);
    }

    public List<AttendanceRegisterDto> attendanceRegisterSummary(String managerId,boolean isAdmin){
        final List<Map<String, Object>> allManagedDepartments = getDepartmentsManagedBy(managerId, isAdmin);
        if(allManagedDepartments.size()==0){
            return null;
        }
        final List<String> departmentIds = extractDepartmentIds(allManagedDepartments);
        final List<AttendanceRegisterDto> attendanceRegisters = namedParameterJdbcTemplate.query(queryFindAttendanceRegistersByDepartment, Collections.singletonMap("departments", departmentIds), new BeanPropertyRowMapper(AttendanceRegisterDto.class));
        final List<AttendanceRegisterDto> outputAttendanceRegisters =  enhanceWithDepartmentNameBasedOnDepartmentId(attendanceRegisters);
        //final List<AttendanceRegisterDto> outputAttendanceRegisters = enhanceWithDepartmentName(attendanceRegisters, allManagedDepartments);
        //sortAttendanceRegistersByDepartmentName(outputAttendanceRegisters);
        return outputAttendanceRegisters;
    }

    private void sortAttendanceRegistersByDepartmentName(List<AttendanceRegisterDto> outputAttendanceRegisters) {
        Collections.sort(outputAttendanceRegisters, new Comparator<AttendanceRegisterDto>() {
            @Override
            public int compare(AttendanceRegisterDto o1, AttendanceRegisterDto o2) {
                if(o1.departmentName != null && o2.departmentName != null) {
                    return o1.departmentName.compareTo(o2.departmentName);
                }
                else
                    return 0;
            }
        });
    }


    static List<String> extractDepartmentIds(List<Map<String,Object>> departments){
        List<String> departmentIds = Lists.newArrayList();
        for (Map<String, Object> department : departments) {
            departmentIds.add((String) department.get("departmentId"));
        }
        return departmentIds;
    }

    static List<AttendanceRegisterDto> enhanceWithDepartmentName(List<AttendanceRegisterDto> attendanceRegisterDtos,List<Map<String,Object>> departments){
        for (AttendanceRegisterDto attendanceRegisterDto : attendanceRegisterDtos) {
            final String departmentId = attendanceRegisterDto.departmentId;

            final Map<String, Object> department = Iterables.find(departments, new Predicate<Map<String, Object>>() {
                @Override
                public boolean apply(Map<String, Object> department) {
                    return department.get("departmentId").equals(departmentId);
                }
            });

            attendanceRegisterDto.departmentName = (String) department.get("departmentName");
        }
        return attendanceRegisterDtos;
    }

    public List<TimesheetDto> getAllTimesheets() {
        List<TimesheetDto> listOfTimesheetDtos  = namedParameterJdbcTemplate.query(queryForListOfTimesheets,new BeanPropertyRowMapper<>(TimesheetDto.class));
        return listOfTimesheetDtos;
    }

    public List<DepartmentDto> getAllDepartmentsYetToSubmitAttendanceRegister(String timesheetId) {
        List<DepartmentDto> departmentDtos = Lists.newArrayList();
        final List<Map<String, Object>> departments = getDepartmentsManagedBy(OfbizGateway.getUserId(), (Boolean)OfbizGateway.getTimesheetInfo().get("isAdmin"));
        List<DepartmentDto> departmentDtoList = namedParameterJdbcTemplate.query(queryToFindDepartmentIdYetToSubmitAttendanceRegister, Collections.singletonMap("timesheetId", timesheetId),new BeanPropertyRowMapper<>(DepartmentDto.class));
        for(DepartmentDto departmentDto : departmentDtoList){
            final String departmentId = departmentDto.getDepartmentId();
            final Map<String, Object> department = Iterables.find(departments, new Predicate<Map<String, Object>>() {
                @Override
                public boolean apply(Map<String, Object> department) {
                    return department.get("departmentId").equals(departmentId);
                }
            });
            departmentDto.setDepartmentName((String) department.get("departmentName"));
            setSubmissionStatusBasedOnAttendanceRegisterStatus(departmentDto);
            departmentDtos.add(departmentDto);
        }
        sortDepartmentDtosByDepartmentId(departmentDtos);
        return departmentDtos;
    }

    private void sortDepartmentDtosByDepartmentId(List<DepartmentDto> departmentDtos) {
        Collections.sort(departmentDtos, new Comparator<DepartmentDto>() {
            @Override
            public int compare(DepartmentDto o1, DepartmentDto o2) {
                return o1.getDepartmentId().compareTo(o2.getDepartmentId());
            }
        });
    }

    private void setSubmissionStatusBasedOnAttendanceRegisterStatus(DepartmentDto departmentDto) {
        String status = departmentDto.getStatus();
        if(status.equals("NEW")|| status.equals("SAVED")){
            departmentDto.setStatus("NOT SUBMITTED");
        }
        else {
            departmentDto.setStatus("SUBMITTED");
        }
    }

    private List<AttendanceRegisterDto> enhanceWithDepartmentNameBasedOnDepartmentId(List<AttendanceRegisterDto> attendanceRegisters) {
        for (AttendanceRegisterDto attendanceRegisterDto : attendanceRegisters) {
            setDepartmentNameToAttendanceRegister(attendanceRegisterDto);
        }
        return attendanceRegisters;
    }

    private void setDepartmentNameToAttendanceRegister(AttendanceRegisterDto attendanceRegisterDto) {
        final String departmentId = attendanceRegisterDto.departmentId;
        Preconditions.checkNotNull(departmentId);
        List<Map<String, Object>> departments = getDepartmentBasedOnDepartmentId(departmentId);
        Preconditions.checkNotNull(departments);
        for (Map<String, Object> department : departments) {
            attendanceRegisterDto.departmentName = (String) department.get("departmentName");
            attendanceRegisterDto.departmentCode = (String) department.get("departmentCode");
        }
        //System.out.println("\n\n\n\ndepartmentId============>"+departmentId+"\n List<Map<String, Object>> departments ======================>"+departments+"\n attendanceRegisterDto===========>"+attendanceRegisterDto);
    }

    private void setDepartmentCodeToAttendanceRegister(AttendanceRegisterDto attendanceRegisterDto) {
        final String departmentId = attendanceRegisterDto.departmentId;
        Preconditions.checkNotNull(departmentId);
        List<Map<String, Object>> departments = getDepartmentBasedOnDepartmentId(departmentId);
        Preconditions.checkNotNull(departments);
        for (Map<String, Object> department : departments) {
            attendanceRegisterDto.departmentCode = (String) department.get("departmentCode");
        }
        //System.out.println("\n\n\n\ndepartmentId============>"+departmentId+"\n List<Map<String, Object>> departments ======================>"+departments+"\n attendanceRegisterDto===========>"+attendanceRegisterDto);
    }
}
