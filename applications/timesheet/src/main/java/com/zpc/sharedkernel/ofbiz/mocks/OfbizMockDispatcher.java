package com.zpc.sharedkernel.ofbiz.mocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.*;
import org.ofbiz.service.jms.JmsListenerFactory;
import org.ofbiz.service.job.JobManager;
import org.zkoss.lang.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */

final class OfbizMockDispatcher implements LocalDispatcher {


    @Override
    public void disableEcas() {
    }

    @Override
    public void enableEcas() {

    }

    public boolean isEcasDisabled() {
        return false;
    }

    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> args) throws GenericServiceException {
        Map<String, Object> result = new HashMap();
        if (Strings.isEmpty(serviceName))
            return result;
        if (findAllDepartments(serviceName, args)) {
            // // All Departments
            Map dept1 = UtilMisc.toMap("partyIdFrom", "10030", "groupName", "Electronics Department","partyId", "10030");
            Map dept2 = UtilMisc.toMap("partyIdFrom", "10031", "groupName", "IT Department","partyId", "10031");
            Map dept3 = UtilMisc.toMap("partyIdFrom", "10032", "groupName", "HR Department","partyId", "10032");
            List departments = Lists.newArrayList(dept1, dept2, dept3);
            result.put("list", departments);
        } else if (findDepartmentForGivenEmployee(serviceName, args)) {
            // Employee Departments
            Map dept1 = UtilMisc.toMap("partyIdFrom", "10030","groupName","Electronics Department","partyId", "10030");
            Map dept2 = UtilMisc.toMap("partyIdFrom", "10031","groupName","IT Department","partyId", "10031");
            Map dept3 = UtilMisc.toMap("partyIdFrom", "10032","groupName","HR Department","partyId", "10032");
            List departments = Lists.newArrayList(dept1, dept2,dept3);
            result.put("list", departments);
        } else if (findEmployeesOfGivenDepartment(serviceName,args)){
            Map emp1 = UtilMisc.toMap("partyId", "10051","firstName","Sudarshan","lastName","Sreenivasan");
            Map emp2 = UtilMisc.toMap("partyId", "10052","firstName","Mohan","lastName","Sharma");
            Map emp3 = UtilMisc.toMap("partyId", "10053","firstName","Ravi","lastName","Kumar");
            List employees = Lists.newArrayList(emp1, emp2, emp3);
            result.put("list", employees);
        }
/*        else if (serviceName.equals("getUserPermissionDetail")) {
            Map userDetails = new HashMap();
            userDetails.put("isAdmin", false);
            userDetails.put("isManager", true);
            userDetails.put("logoImageUrl", "asd");
            result.put("userDetail", userDetails);
        }*/
        else if(findLeaveLimitForEmployee(serviceName,args)){
            result.put("leaveLimit", Double.valueOf(25));
        }
        else if (findDepartmentByDepartmentId(serviceName,args)){
            Map dept1 = UtilMisc.toMap("departmentId", "10030","departmentName","Electronics Department","departmentCode", "EC");
            Map dept2 = UtilMisc.toMap("departmentId", "10031","departmentName","IT Department","departmentCode", "IT");
            Map dept3 = UtilMisc.toMap("departmentId", "10032","departmentName","HR Department","departmentCode", "HR");
            List departments = Lists.newArrayList(dept1, dept2,dept3);
            result.put("list", departments);
        }

        // IF/ELSE ladder here
        return result;
    }

    private boolean findEmployeesOfGivenDepartment(String serviceName, Map<String, ? extends Object> args) {
        return serviceName.equals("performFindList") && "EmploymentAndPerson".equals(args.get("entityName").toString());
    }

    private boolean findAllDepartments(String serviceName, Map<String, ? extends Object> args) {
        return serviceName.equals("performFindList") && "PartyRoleAndPartyDetail".equals(args.get("entityName").toString());
    }

    private boolean findLeaveLimitForEmployee(String serviceName, Map<String, ? extends Object> args) {
        return serviceName.equals("getEmployeeLeaveLimitForLeaveType");
    }

    private boolean findDepartmentForGivenEmployee(String serviceName, Map<String, ? extends Object> args) {
        final Map inputFields = (Map) args.get("inputFields");
        boolean isRoleTypeIdFromOrgRole = false;
        if (inputFields != null && inputFields.get("roleTypeIdFrom")!=null) {
            isRoleTypeIdFromOrgRole = "ORGANIZATION_ROLE".equals(inputFields.get("roleTypeIdFrom").toString());
        }
        return serviceName.equals("performFindList") && "PartyRelationshipAndDetail".equals(args.get("entityName").toString()) && isRoleTypeIdFromOrgRole;
    }

    private boolean findDepartmentByDepartmentId(String serviceName, Map<String, ? extends Object> args) {
        final Map inputFields = (Map) args.get("inputFields");
        if (inputFields != null && inputFields.get("departmentId")!=null) {
            return serviceName.equals("performFindList") && "DepartmentPosition".equals(args.get("entityName").toString());
        }
        return false;
    }

    @Override
    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> args, int i, boolean b) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        Map<String, Object> result = new HashMap();
        if (Strings.isEmpty(serviceName))
            return result;
        // IF/ELSE ladder here
        return result;
    }

    @Override
    public Map<String, Object> runSync(String serviceName, int i, boolean b, Object... objects) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        throw new UnsupportedOperationException("Limited mock");
    }

    @Override
    public void runSyncIgnore(String s, Map<String, ? extends Object> stringMap) throws GenericServiceException {

    }

    @Override
    public void runSyncIgnore(String s, Map<String, ? extends Object> stringMap, int i, boolean b) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runSyncIgnore(String s, int i, boolean b, Object... objects) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, Map<String, ? extends Object> stringMap, GenericRequester genericRequester, boolean b, int i, boolean b2) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, GenericRequester genericRequester, boolean b, int i, boolean b2, Object... objects) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, Map<String, ? extends Object> stringMap, GenericRequester genericRequester, boolean b) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, GenericRequester genericRequester, boolean b, Object... objects) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, Map<String, ? extends Object> stringMap, GenericRequester genericRequester) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, GenericRequester genericRequester, Object... objects) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, Map<String, ? extends Object> stringMap, boolean b) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, boolean b, Object... objects) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public void runAsync(String s, Map<String, ? extends Object> stringMap) throws ServiceAuthException, ServiceValidationException, GenericServiceException {

    }

    @Override
    public GenericResultWaiter runAsyncWait(String s, Map<String, ? extends Object> stringMap, boolean b) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        return null;
    }

    @Override
    public GenericResultWaiter runAsyncWait(String s, boolean b, Object... objects) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        return null;
    }

    @Override
    public GenericResultWaiter runAsyncWait(String s, Map<String, ? extends Object> stringMap) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        return null;
    }

    @Override
    public void registerCallback(String s, GenericServiceCallback genericServiceCallback) {

    }

    @Override
    public void schedule(String s, String s2, Map<String, ? extends Object> stringMap, long l, int i, int i2, int i3, long l2, int i4) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, String s2, long l, int i, int i2, int i3, long l2, int i4, Object... objects) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, String s2, String s3, Map<String, ? extends Object> stringMap, long l, int i, int i2, int i3, long l2, int i4) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, String s2, String s3, long l, int i, int i2, int i3, long l2, int i4, Object... objects) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, Map<String, ? extends Object> stringMap, long l, int i, int i2, int i3, long l2) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, long l, int i, int i2, int i3, long l2, Object... objects) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, Map<String, ? extends Object> stringMap, long l, int i, int i2, int i3) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, long l, int i, int i2, int i3, Object... objects) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, Map<String, ? extends Object> stringMap, long l, int i, int i2, long l2) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, long l, int i, int i2, long l2, Object... objects) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, Map<String, ? extends Object> stringMap, long l) throws GenericServiceException {

    }

    @Override
    public void schedule(String s, long l, Object... objects) throws GenericServiceException {

    }

    @Override
    public void addRollbackService(String s, Map<String, ? extends Object> stringMap, boolean b) throws GenericServiceException {

    }

    @Override
    public void addRollbackService(String s, boolean b, Object... objects) throws GenericServiceException {

    }

    @Override
    public void addCommitService(String s, Map<String, ? extends Object> stringMap, boolean b) throws GenericServiceException {

    }

    @Override
    public void addCommitService(String s, boolean b, Object... objects) throws GenericServiceException {

    }

    @Override
    public JobManager getJobManager() {
        return null;
    }

    @Override
    public JmsListenerFactory getJMSListeneFactory() {
        return null;
    }

    @Override
    public org.ofbiz.entity.Delegator getDelegator() {
        return null;
    }

    @Override
    public org.ofbiz.security.authz.Authorization getAuthorization() {
        return null;
    }

    @Override
    public org.ofbiz.security.Security getSecurity() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public DispatchContext getDispatchContext() {
        return null;
    }

    @Override
    public void deregister() {

    }
}
