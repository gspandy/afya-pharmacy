package com.ndz.zkoss;

import com.hrms.composer.RecruitmentComposer;
import com.ndz.zkoss.util.HrmsConstants;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.nthdimenzion.humanres.payroll.PayrollHelper;
import com.nthdimenzion.humanres.payroll.services.PayrollServices;
import com.smebiz.common.UtilDateTimeSME;
import javolution.util.FastMap;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HrmsUtil extends UtilValidate {

    public static GenericDelegator delegator = HrmsInfrastructure.getDelegator();

    public static String getEmployeeId(GenericDelegator delegator,String positionCategory) throws GenericEntityException {
        List employeeSequenceList = delegator.findByAnd("SequenceValueItem", UtilMisc.toMap("seqName", "EMPLOYEE"));
        GenericValue employeeSequenceGv = EntityUtil.getFirst(employeeSequenceList);
        String employeeId = null;
        if (employeeSequenceGv != null) {
            //String prefix = (String) employeeSequenceGv.getString("seqPrefix");
            //String suffix = (String) employeeSequenceGv.getString("suffix");
            String prefix ="";
            if(positionCategory.equals("Expatriates")){
                prefix="E";
            }else if(positionCategory.equals("Management")){
                prefix="M";
            }else if(positionCategory.equals("Employees")){
                prefix="G";
            }
            String sequenceId = delegator.getNextSeqId("EMPLOYEE");
            delegator.getNextSeqId(sequenceId);
            employeeId = prefix + sequenceId;
            return employeeId;
        }
        return null;

    }

    public static String getProspectId(GenericDelegator delegator,String positionCategory) throws GenericEntityException {
        List prospectSequenceList = delegator.findByAnd("SequenceValueItem", UtilMisc.toMap("seqName", "PROSPECT"));
        GenericValue prospectSequenceGv = EntityUtil.getFirst(prospectSequenceList);
        String prospectId = null;
        if (prospectSequenceGv != null) {
            //String prefix = (String) prospectSequenceGv.getString("seqPrefix");
            //String suffix = (String) prospectSequenceGv.getString("suffix");
            String prefix ="";
            if(positionCategory.equals("Expatriates")){
                prefix="E";
            }else if(positionCategory.equals("Management")){
                prefix="M";
            }else if(positionCategory.equals("Employees")){
                prefix="G";
            }
            String sequenceId = delegator.getNextSeqId("EMPLOYEE");
            delegator.getNextSeqId(sequenceId);
            prospectId = prefix + sequenceId;
            return prospectId;
        }
        return null;

    }

    public static String getFullName(Delegator delegator, String partyId) throws GenericEntityException {
        GenericValue personGv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        if (personGv != null) {
            String middleName = " ";
            if (UtilValidate.isNotEmpty(personGv.getString("middleName"))) {
                middleName = " " + personGv.getString("middleName") + " ";
            }
            return (personGv.getString("firstName") + middleName + personGv.getString("lastName"));
        } else {
            return null;
        }
    }


    public static String getFullName(GenericDelegator delegator, String partyId) throws GenericEntityException {
        GenericValue personGv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        if (personGv != null) {
            String middleName = " ";
            if (UtilValidate.isNotEmpty(personGv.getString("middleName"))) {
                middleName = " " + personGv.getString("middleName") + " ";
            }
            return (personGv.getString("firstName") + middleName + personGv.getString("lastName"));
        } else {
            return null;
        }
    }

    public static Double calculateEMI(Double principal, Double interest, Double period) {
        Double emi = 0d;
        Double p = principal;
        Double n = period * 12;
        if (interest.compareTo(0d) == 1) {
            Double r = interest / (100 * 12);
            Double x = 1 + r;
            Double po = Math.pow(x, n);
            emi = (p * r * po) / (po - 1);
        } else {
            emi = p / n;
        }
        return emi;
    }

    public static int dateCompare(Date fromDate, Date thruDate) throws ParseException {
        java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MM-yyyy");
        Date f_Date = formater.parse(formater.format(fromDate));
        Date t_Date = formater.parse(formater.format(thruDate));
        return f_Date.compareTo(t_Date);
    }

    public static Boolean checkSpecialCharacter(String inputString) {
        Pattern p = Pattern.compile("\\W"); // [a-zA-Z_0-9]
        Matcher m = p.matcher(inputString);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static Boolean checkAnyNumber(String inputString) {
        int length = inputString.length();
        for (int i = 0; i < length; i++) {
            char c = inputString.charAt(i);
            boolean value = UtilValidate.isDigit(c);
            if (value) {
                return value;
            }
        }
        return false;

    }

    public static Boolean checkSpaceValidation(String inputString) {
        return inputString.trim().length() <= 0;

    }

    public static Boolean listboxValidation(Listbox inputListbox) {
        Listitem li = (Listitem) inputListbox.getSelectedItem();
        if (li == null) {
            return true;
        } else {
            if (li.getValue() == null) {
                return true;
            } else if (li.getValue().equals("") || li.getLabel().equals("")) {
                return true;
            }
        }
        return false;

    }

    public static Set<Object> getCustomTimePeriod() {
        Set<Object> yearData = new TreeSet<Object>();
        GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
        try {
            List<GenericValue> fiscalYearGVs = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(
                    "periodTypeId", "FISCAL_YEAR"), null, UtilMisc.toList("-periodName"), null, true);
            for (GenericValue yearGv : fiscalYearGVs) {
                java.sql.Date fromDate = yearGv.getDate("fromDate");
                yearData.add(fromDate.getYear() + 1900);
                java.sql.Date thruDate = yearGv.getDate("thruDate");
                yearData.add(thruDate.getYear() + 1900);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return yearData;
    }

    public static void decimalboxValidation(Decimalbox inputDecimalBox, String Message) {
        if (inputDecimalBox.getValue() != null) {
            BigDecimal zero = new BigDecimal(0);
            BigDecimal value = inputDecimalBox.getValue();
            if (value.compareTo(zero) == -1) {
                throw new WrongValueException(inputDecimalBox, Message);
            }
        }
    }

    public static String getStatusItemDescription(String statusId) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId));
        return gv.getString("description") == null ? null : gv.getString("description");
    }

    public static boolean checkAccess(GenericValue userLogin, Security security) throws GenericEntityException {
        String partyIdFor = (String) Executions.getCurrent().getArg().get("partyId");
        if (partyIdFor == null) {
            partyIdFor = Executions.getCurrent().getParameter("partyId");
        }
        if (partyIdFor == null) {
            partyIdFor = (String) Executions.getCurrent().getDesktop().getAttribute("partyId");
        }

        String ownPartyId = (String) userLogin.getString("partyId");
        GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
        java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MM-yyyy");
        boolean isAdmin = security.hasPermission("HUMANRES_ADMIN", userLogin);
        boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
        boolean sameUser = ownPartyId.equalsIgnoreCase(partyIdFor);
        String employeePositionId = null;
        if (sameUser || isAdmin) {
            return true;
        } else if (isManager) {
            List<GenericValue> list1 = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId",
                    partyIdFor));
            if (list1.size() > 0) {
                employeePositionId = list1.get(0).getString("emplPositionId");
            }
            List<GenericValue> positionValues = HumanResUtil.getSubordinatesForParty(ownPartyId, delegator);
            if (positionValues == null) {
                return false;
            }
            List<String> positions = new ArrayList<String>(positionValues.size());
            for (GenericValue position : positionValues) {
                String fromDate = position.getString("fromDate");
                Date from = null;
                try {
                    from = formater.parse(fromDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date today = new Date();
                String throughDate = position.getString("thruDate");
                if (throughDate == null) {
                    if (today.after(from) || today.equals(from) && throughDate == null) {
                        positions.add(position.getString("emplPositionIdManagedBy"));

                    }
                } else {
                    Date through = null;
                    try {
                        through = formater.parse(throughDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if ((today.after(from) || today.equals(from)) && (today.before(through) || today.equals(through))) {
                        positions.add(position.getString("emplPositionIdManagedBy"));
                    }
                }

            }

            if (positions.contains(employeePositionId)) {
                return true;
            }
        }
        return false;
    }

    public String lowerCamelCase(String lowerCaseAndUnderscoredWord, char... delimiterChars) {
        return camelCase(lowerCaseAndUnderscoredWord, false, delimiterChars);
    }

    public static String upperCamelCase(String lowerCaseAndUnderscoredWord, char... delimiterChars) {
        return camelCase(lowerCaseAndUnderscoredWord, true, delimiterChars);
    }

    public static String camelCase(String lowerCaseAndUnderscoredWord, boolean uppercaseFirstLetter,
                                   char... delimiterChars) {
        if (lowerCaseAndUnderscoredWord == null) return null;
        lowerCaseAndUnderscoredWord = lowerCaseAndUnderscoredWord.trim();
        if (lowerCaseAndUnderscoredWord.length() == 0) return "";
        if (uppercaseFirstLetter) {
            String result = lowerCaseAndUnderscoredWord;
            // Replace any extra delimiters with underscores (before the
            // underscores are converted in the next step)...
            if (delimiterChars != null) {
                for (char delimiterChar : delimiterChars) {
                    result = result.replace(delimiterChar, '_');
                }
            }

            // Change the case at the beginning at after each underscore ...
            return replaceAllWithUppercase(result, "(^|_)(.)", 2);
        }
        if (lowerCaseAndUnderscoredWord.length() < 2) return lowerCaseAndUnderscoredWord;
        return "" + Character.toLowerCase(lowerCaseAndUnderscoredWord.charAt(0))
                + camelCase(lowerCaseAndUnderscoredWord, true, delimiterChars).substring(1);
    }

    protected static String replaceAllWithUppercase(String input, String regex, int groupNumberToUppercase) {
        Pattern underscoreAndDotPattern = Pattern.compile(regex);
        Matcher matcher = underscoreAndDotPattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(groupNumberToUppercase).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static Map<String, Object> getPaidUnpaidDays(Map<String, Object> context) throws Exception {

        String partyId = (String) context.get("partyId");
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        double intervalDays = 0;
        Map<String, Object> paramCtx = FastMap.newInstance();
        paramCtx.put("partyId", partyId);
        paramCtx.put("periodFrom", lperiodFrom);
        paramCtx.put("periodTo", lperiodTo);

        GenericValue employment = PayrollServices.getEmployementRecordForParty(delegator, partyId);
        if (employment == null) {
            throw new Exception("No Employment record found for Party " + partyId);
        }
        Date employementDate = new Date(employment.getTimestamp("fromDate").getTime());
        if (employment.getTimestamp("thruDate") != null) {
            Date terminatedDate = new Date(employment.getTimestamp("thruDate").getTime());

            if (terminatedDate.after(lperiodFrom) && terminatedDate.before(lperiodTo)) {
                lperiodTo = terminatedDate;
            }
        }

        if (employementDate.after(lperiodFrom) || employementDate.equals(lperiodFrom)) {
            lperiodFrom = employementDate;
        }

        intervalDays = ((int) ((lperiodTo.getTime() - lperiodFrom.getTime()) / (24 * 60 * 60 * 1000)) + 1);
        Map<String, Object> result = PayrollHelper.getPaidFracYear(paramCtx, delegator);

        Double totalUnpaidDays = (Double) result.get("totalUnpaidDays");

        intervalDays = intervalDays - totalUnpaidDays;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("PaidDays", intervalDays);
        map.put("UnPaidDays", totalUnpaidDays);

        return map;
    }

    public static boolean isRejectedOrWithdrawn(String offerId) throws GenericEntityException {
        EntityCondition con = EntityCondition.makeCondition("offerId", EntityOperator.EQUALS, offerId);
        EntityCondition cn1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "OF_REJECTED");
        EntityCondition cn2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "OF_WITHDRAWN");
        EntityCondition condition = EntityCondition.makeCondition(cn1, EntityOperator.OR, cn2);
        List<GenericValue> offerStatus = delegator.findList("OfferStatus", EntityCondition.makeCondition(condition,
                EntityOperator.AND, con), null, null, null, false);
        if (offerStatus.size() > 0) return true;

        return false;
    }

    public static void checkPrefixSuffix(Textbox prefix, Textbox suffix, Textbox startValue) {
        prefix.setValue(prefix.getValue().trim());
        suffix.setValue(suffix.getValue().trim());
        startValue.setValue(startValue.getValue().trim());
        String prospectPrfx = prefix.getValue();
        if (UtilValidate.isNotEmpty(prospectPrfx)) {
            if (UtilValidate.isInteger(Character.toString(prospectPrfx.charAt(0)))) {
                throw new WrongValueException(prefix, "Prefix must be start with character");
            }
            startValue.setConstraint("no empty:Start Value required");
            startValue.getValue();
            if (!UtilValidate.isInteger(startValue.getValue())) {
                throw new WrongValueException(startValue, "Only Numeric  Value Allowed ");
            }
        }
    }

    public static String getDepartmentName(String departmentId) throws GenericEntityException {
        List<GenericValue> departmentGvs = delegator.findByAnd("DepartmentPosition", UtilMisc.toMap("departmentId",
                departmentId));
        GenericValue departmentGv = UtilValidate.isNotEmpty(departmentGvs) ? departmentGvs.get(0) : null;
        if (departmentGv != null) return departmentGv.getString("departmentName");
        return "";
    }

    public static String getPositionTypeDescription(String positionTypeId) throws GenericEntityException {
        List<GenericValue> positionTypeGvs = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId",
                positionTypeId));
        GenericValue positionTypeGv = UtilValidate.isNotEmpty(positionTypeGvs) ? positionTypeGvs.get(0) : null;
        if (positionTypeGv != null) return positionTypeGv.getString("description");
        return "";
    }

    public static String statusItemDescription(String statusId) throws GenericEntityException {
        List<GenericValue> statusGvs = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", statusId));
        GenericValue statusGv = UtilValidate.isNotEmpty(statusGvs) ? statusGvs.get(0) : null;
        if (statusGv != null) return statusGv.getString("description");
        return "";
    }

    public static Timestamp addDaysToTimestamp(Date applicationDate, Long noticePeriod) {
        return new Timestamp(applicationDate.getTime() + (24L * 60L * 60L * 1000L * noticePeriod));
    }

    public static String getEmployeeName(String positionId) throws GenericEntityException {
        List<GenericValue> gvs = delegator.findByAnd("EmplPositionFulfillment", UtilMisc
                .toMap("emplPositionId", positionId));
        if (UtilValidate.isEmpty(gvs)) return "";
        GenericValue gv = gvs.get(0);
        return getFullName(delegator, gv.getString("partyId"));
    }

    public static String getEnumDescription(String enumId) throws GenericEntityException {
        List<GenericValue> gvs = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", enumId));
        if (UtilValidate.isEmpty(gvs)) return "";
        return gvs.get(0).getString("description");

    }

    public static boolean isUser() {
        if (!isRequisitionAdmin() && !isHod() && !isManager() && !isSystemAdmin()) return true;
        return false;
    }

    public static boolean isHod() {
        GenericValue employeePosIdGv = null;
        try {
            employeePosIdGv = HumanResUtil.getEmplPositionForParty(getUserLogin().getString("partyId"), delegator);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String positionnTypeId = employeePosIdGv != null ? employeePosIdGv.getString("emplPositionTypeId") : "";
        if ("HOD".equalsIgnoreCase(positionnTypeId)) return true;
        return false;
    }

    public static boolean isSystemAdmin() {
        Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        return security.hasPermission("HUMANRES_ADMIN", getUserLogin());
    }

    public static boolean isManager() {
        Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        return security.hasPermission("HUMANRES_MGR", getUserLogin());
    }

    public static boolean isRequisitionAdmin() {
        boolean isReqAdmin = false;

        List<GenericValue> userLoginSecurityGroupGv = null;
        try {
            userLoginSecurityGroupGv = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId",
                    getUserLogin().getString("userLoginId"), "groupId", "HUMANRES_MGR","isRequisitionAdmin","Y"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        // if (UtilValidate.isNotEmpty(userLoginSecurityGroupGv) && "Y".equalsIgnoreCase(userLoginSecurityGroupGv.get(0).getString("isRequisitionAdmin")))
        if (UtilValidate.isNotEmpty(userLoginSecurityGroupGv))
            isReqAdmin = true;

        return isReqAdmin;
    }

    public static GenericValue getUserLogin() {
        return (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
    }

    public static GenericValue getEmployeePositionTypeGv(String employeePositionTypeId) {
        List<GenericValue> gvs = null;
        try {
            gvs = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", employeePositionTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(gvs)) return gvs.get(0);
        return null;
    }

    public static GenericValue getCurrencyTypeGv(String currencyTypeId) {
        List<GenericValue> gvs = null;
        try {
            gvs = delegator.findByAnd("Uom", UtilMisc.toMap("uomId", currencyTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(gvs)) return gvs.get(0);
        return null;
    }

    public static GenericValue getCurrencyBaseLineGv(String baseLineId) {
        List<GenericValue> gvs = null;
        try {
            gvs = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", baseLineId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(gvs)) return gvs.get(0);
        return null;
    }

    public static GenericValue getEmployeePositionGv(String emplPositionId) {
        List<GenericValue> gvs = null;
        try {
            gvs = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(gvs)) return gvs.get(0);
        return null;
    }

    public static String getRequisitionStatusDescription(String statusId) {
        return UtilValidate.isEmpty(statusId) ? " " : HrmsConstants.REQUISITION_ADMIN_APPROVE_STATUS
                .equalsIgnoreCase(statusId) ? "Requisition Admin Approved" : HrmsConstants.REQUISITION_ADMIN_REJECT_STATUS
                .equalsIgnoreCase(statusId) ? "Requisition Admin Rejected" : HrmsConstants.REQUISITION_HOD_APPROVE_STATUS
                .equalsIgnoreCase(statusId) ? "HOD Approved" : HrmsConstants.REQUISITION_HOD_REJECT_STATUS
                .equalsIgnoreCase(statusId) ? "HOD Rejected" : statusId;
    }

    public static List<GenericValue> getLocationsOfDepartment(String departmentId) {
        List<GenericValue> locationGvs = null;
        List<String> locationIds = new ArrayList<String>();
        try {
            String departmentName = getDepartmentName(departmentId);
            List<GenericValue> departementGvs = delegator.findByAnd("DepartmentPosition", UtilMisc.toMap("departmentName",
                    departmentName));
            for (GenericValue gv : departementGvs)
                locationIds.add(gv.getString("locationId"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        try {
            locationGvs = delegator.findList("Location", EntityCondition.makeCondition("locationId",
                    EntityComparisonOperator.IN, locationIds), null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return locationGvs;
    }

    public static GenericValue getPersonLocation(GenericValue userLogin) {
        String locationId = null;
        GenericValue locationGv = null;
        try {
            GenericValue personGv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", userLogin
                    .getString("partyId")));
            locationId = personGv.getString("locationId");
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        try {
            locationGv = delegator.findByPrimaryKey("Location", UtilMisc.toMap("locationId", locationId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return locationGv;
    }

    public static String getLocationName(String locationId) {
        GenericValue locationGv = null;
        try {
            locationGv = delegator.findByPrimaryKeyCache("Location", UtilMisc.toMap("locationId", locationId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return locationGv!=null?locationGv.getString("locationName"):"";
    }

    public static GenericValue getLocationGv(String locationId) {
        GenericValue locationGv = null;
        try {
            locationGv = delegator.findByPrimaryKeyCache("Location", UtilMisc.toMap("locationId", locationId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return locationGv;
    }

    public static String getUomDescription(String uomId) {
        GenericValue gv = null;
        try {
            gv = delegator.findByPrimaryKeyCache("Uom", UtilMisc.toMap("uomId", uomId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return gv.getString("description");
    }

    public static void processRequisition(final RecruitmentComposer recruitmentComposer, final String processMessage, String warningMessage)
            throws InterruptedException {
        Messagebox.show(warningMessage, "Warning", Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(Event evt) throws Exception {
                        if ("onYes".equals(evt.getName())) {
                            try {
                                recruitmentComposer.saveRequisition();
                                Messagebox.show("Requisition Id" + " " + recruitmentComposer.getRequisitionId() + " "
                                                + processMessage, "Success", Messagebox.OK, "", 1,
                                        new org.zkoss.zk.ui.event.EventListener() {
                                            public void onEvent(Event evt) {
                                                if (isRequisitionAdmin()
                                                        && HrmsConstants.REQUISITION_ADMIN_APPROVE_STATUS
                                                        .equalsIgnoreCase(recruitmentComposer.getRequisitionVo()
                                                                .getStatusId()))
                                                    Executions.getCurrent().sendRedirect(
                                                            "/control/viewProcessingRequisition?requisitionId="
                                                                    + recruitmentComposer.getRequisitionId());
                                                else if (HrmsConstants.REQUISITION_SAVE_STATUS.equalsIgnoreCase(recruitmentComposer.getRequisitionVo().getStatusId()))
                                                    Executions.getCurrent().sendRedirect("/control/editRequisition?requisitionId=" + recruitmentComposer.getRequisitionId());
                                                else
                                                    Executions.getCurrent().sendRedirect("/control/requisitionManagement");
                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }
                });
    }

    public static String checkTrainingSeatAvailability(Map<String, Object> map) {

        String statusId = map.get("statusId").toString();
        if ("TRNG_CANCELLED".equals(statusId)) {
            try {
                EntityCondition cn1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRNG_WAITING");
                EntityCondition cn2 = EntityCondition.makeCondition("trainingId", EntityOperator.EQUALS, map
                        .get("trainingId"));
                EntityCondition cn3 = EntityCondition.makeCondition(cn1, EntityOperator.AND, cn2);
                EntityCondition condition = EntityCondition.makeCondition(cn3, EntityOperator.AND, EntityCondition
                        .makeCondition("partyId", EntityOperator.NOT_EQUAL, map.get("partyId")));
                List<GenericValue> listTrainingReq = delegator.findList("TrainingRequest", condition, null, null, null,
                        false);
                for (GenericValue trainingReqRow : listTrainingReq) {
                    trainingReqRow.set("statusId", "TRNG_CONFIRMED");
                    delegator.store(trainingReqRow);
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List trainingList = delegator.findByAnd("TrainingRequest", UtilMisc.toMap("statusId", "TRNG_CONFIRMED",
                        "trainingId", map.get("trainingId")));
                Long count = UtilValidate.isNotEmpty(trainingList) ? Long.valueOf(trainingList.size()) : 0l;
                List<GenericValue> list = delegator.findByAnd("Training", UtilMisc.toMap("trainingId", map
                        .get("trainingId")));
                if (UtilValidate.isNotEmpty(list)) {
                    if (count.intValue() >= new Integer(list.get(0).getString("maxTrainees")).intValue()) {
                        return "error";
                    }
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }
        return "success";

    }

    public static GenericValue getEmplPositionGvFor(String requisitionId) {
        List<GenericValue> gvs = null;
        try {
            gvs = delegator.findByAnd("EmplPosition", UtilMisc.toMap("requisitionId", requisitionId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(gvs))
            return gvs.get(0);
        return null;
    }

    public static String getBudgetType(String budgetTypeId) {

        List<GenericValue> gvs = null;
        try {
            gvs = delegator.findByAnd("BudgetType", UtilMisc.toMap("budgetTypeId", budgetTypeId));
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(gvs))
            return gvs.get(0).getString("description");
        return null;
    }

    public static String getBudgetItemType(String budgetItemTypeId) {
        List<GenericValue> gvs = null;
        try {
            gvs = delegator.findByAnd("BudgetItemType", UtilMisc.toMap("budgetItemTypeId", budgetItemTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(gvs))
            return gvs.get(0).getString("description");
        return null;
    }

    public static boolean checkLeaveType(String leaveTypeId, String positionId) throws GenericEntityException {
        List<GenericValue> emplLeaveList = delegator.findByAnd("EmplLeave", org.ofbiz.base.util.UtilMisc.toMap("leaveTypeId", leaveTypeId));
        if (emplLeaveList.size() > 0) {
            GenericValue gv = HumanResUtil.getEmplPositionForParty(emplLeaveList.get(0).getString("partyId"), delegator);
            String emplPositionId = gv.getString("emplPositionTypeId");
            if (emplPositionId.equals(positionId))
                return true;
        }
        return false;
    }

    public static BigDecimal getAccrualDays(String partyId) throws GenericEntityException {
        DecimalFormat df = new DecimalFormat("#.##");
        Date currentDate = new Date();
        Timestamp montStart = UtilDateTime.getMonthStart(new Timestamp(currentDate.getTime()));
        GenericValue positionTypeGv = HumanResUtil.getEmplPositionForParty(partyId, delegator);
        BigDecimal totalEarned = new BigDecimal(getTotalEarnedLeave(positionTypeGv.getString("emplPositionTypeId")));
        BigDecimal totalMonth = new BigDecimal(12);
        BigDecimal perDayEarnedLeave = totalEarned.divide(totalMonth);
        GenericValue fiscalTimePeriod = getCurrentFiscalYear();
        if (fiscalTimePeriod != null) {
            Date fiscalYearFromDate = fiscalTimePeriod.getDate("fromDate");
            Date joiningDate = getEmployeeJoinigDate(partyId);
            java.sql.Timestamp fiscalYearFromDateTimeStampDate = new Timestamp(fiscalYearFromDate.getTime());
            java.sql.Timestamp joiningDateTimeStampDate = new Timestamp(joiningDate.getTime());
            int intervalInMonth = 0;
            if (joiningDateTimeStampDate.after(fiscalYearFromDateTimeStampDate)) {
                intervalInMonth = ((Double) UtilDateTime.getIntervalInMonths(fiscalYearFromDateTimeStampDate, joiningDateTimeStampDate)).intValue();
                if (intervalInMonth > 0) {
                    Timestamp previousMonth = UtilDateTime.getDayEnd(montStart, -1l);
                    Double month = UtilDateTimeSME.getMonthInterval(joiningDateTimeStampDate, new Date(previousMonth.getTime()));
                    BigDecimal accrualLeave = perDayEarnedLeave.multiply(new BigDecimal(month));
                    String accValStr = df.format(accrualLeave);
                    return new BigDecimal(accValStr);
                } else {

                    Timestamp previousMonth = UtilDateTime.getDayEnd(montStart, -1l);
                    BigDecimal totalDaysJoin = new BigDecimal(UtilDateTime.getIntervalInDays(fiscalYearFromDateTimeStampDate, joiningDateTimeStampDate));
                    TimeZone zone = TimeZone.getDefault();
                    Locale local = Locale.getDefault();
                    BigDecimal totalDaysInMonth = new BigDecimal(UtilDateTimeSME.daysBetween(UtilDateTime.getMonthStart(montStart), UtilDateTime.getMonthEnd(montStart, zone, local)));
                    BigDecimal accrualLeave = (perDayEarnedLeave.divide(totalDaysInMonth)).multiply(totalDaysInMonth.subtract(totalDaysJoin));
                    String accValStr = df.format(accrualLeave);
                    return new BigDecimal(accValStr);
                }


            } else {
                if (fiscalYearFromDate.getMonth() == currentDate.getMonth() || joiningDate.getMonth() == currentDate.getMonth()) {
                    return BigDecimal.ZERO;
                } else {
                    Timestamp previousMonth = UtilDateTime.getDayEnd(montStart, -1l);
                    double month = UtilDateTimeSME.getMonthInterval(fiscalYearFromDate, new Date(previousMonth.getTime()));
                    BigDecimal accrualLeave = perDayEarnedLeave.multiply(new BigDecimal(month));
                    if (accrualLeave.compareTo(BigDecimal.ZERO) < 0)
                        accrualLeave = BigDecimal.ZERO;
                    String accValStr = df.format(accrualLeave);
                    return new BigDecimal(accValStr);
                }
            }
        } else
            return BigDecimal.ZERO;

    }

    public static GenericValue getCurrentFiscalYear() throws GenericEntityException {
        Date currentDate = new Date();
        EntityCondition cn1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, currentDate);
        EntityCondition cn2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, currentDate);
        EntityCondition con = EntityCondition.makeCondition(cn1, EntityOperator.AND, cn2);
        List<GenericValue> customTimePeriod = delegator.findList("CustomTimePeriod", con, null, null, null, false);
        if (UtilValidate.isNotEmpty(customTimePeriod)) {
            return customTimePeriod.get(0);
        } else
            return null;

    }

    public static Date getEmployeeJoinigDate(String partyId) throws GenericEntityException {
        EntityCondition cn1 = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId);
        List<GenericValue> empList = delegator.findList("Employment", cn1, null, null, null, false);
        if (UtilValidate.isNotEmpty(empList)) {
            return empList.get(0).getTimestamp("fromDate");
        } else
            return null;
    }

    public static int getTotalEarnedLeave(String positionType) throws GenericEntityException {
        List<GenericValue> emplLeaveLimList = delegator.findByAnd("EmplLeaveLimit", UtilMisc.toMap("emplPosTypeId", positionType, "leaveTypeId", "LT_EARNED"));
        if (UtilValidate.isNotEmpty(emplLeaveLimList)) {
            return emplLeaveLimList.get(0).getDouble("numDays").intValue();
        }
        return 0;
    }

    public static boolean isManagerViewSubOrdinatePayroll() throws GenericEntityException {
        List<GenericValue> mgrPartyAtt = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("partyId", "Company", "attrName", "MGRVIEW_SUBORDINATE_PAYROLL"));
        GenericValue gv = EntityUtil.getFirst(mgrPartyAtt);
        if (gv != null && "Yes".equalsIgnoreCase(gv.getString("attrValue"))) {
            return true;
        } else {
            return false;
        }
    }

    public static List<GenericValue> getSubOrdinatesLeaveInfo(String partyId, String applyEmployeeId, Timestamp fromDate, Timestamp thruDate, String statusId) throws GenericEntityException {
        GenericValue emplPositionGv = HumanResUtil.getEmplPositionForParty(partyId, delegator);
        if (UtilValidate.isEmpty(applyEmployeeId)) {
            return null;
        }
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        if (emplPositionGv != null) {
            conditionList.add(EntityCondition.makeCondition("mgrPositionId", EntityOperator.EQUALS, emplPositionGv.getString("emplPositionId")));
            if (fromDate != null)
                conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
            if (thruDate != null)
                conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
            conditionList.add(EntityCondition.makeCondition("leaveStatusId", EntityOperator.NOT_EQUAL, "LT_SAVED"));
             /*conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,applyEmployeeId));*/
            if (UtilValidate.isNotEmpty(statusId))
                conditionList.add(EntityCondition.makeCondition("leaveStatusId", EntityOperator.EQUALS, statusId));
            return delegator.findList("EmplLeave", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

        } else {
            return null;
        }
    }

    public static String getEmployeeNameByPartyId(String partyId) {
        org.ofbiz.entity.GenericValue personGV = null;
        try {
            personGV = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (personGV != null)
            return personGV.getString("firstName") + " " + personGV.getString("lastName");
        else
            return "";
    }

    public static List<GenericValue> getLeaveStatus() throws GenericEntityException {
        EntityCondition condition1 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "LT_SAVED");
        EntityCondition condition2 = EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "LEAVE_STATUS");
        EntityCondition condition = EntityCondition.makeCondition(condition1, condition2);
        return delegator.findList("StatusItem", condition, null, null, null, false);
    }

    public static String getGeoDescriptionByGeoId(String geoId) {
        GenericValue geoValue = null;
        try {
            geoValue = delegator.findByPrimaryKey("Geo", UtilMisc.toMap("geoId", geoId));
            return geoValue.getString("description");
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getAge(java.sql.Date dateOfBirth) {
        java.sql.Date now = new java.sql.Date(new Date().getTime());
        int intervalInDays = UtilDateTime.getIntervalInDays(dateOfBirth, now);
        int age = (intervalInDays / 365);
        if (age <= 0) {
            age = 1;
        }
        return age;
    }


    public static GenericValue getEmployeePositionFulfillment(String emplPositionId) throws GenericEntityException {
        List<GenericValue> emplPositionFulfillmentList = delegator.findByAnd("EmplPositionFulfillment", org.ofbiz.base.util.UtilMisc.toMap("emplPositionId", emplPositionId, "thruDate", null));
        GenericValue emplPositionFulfillment = EntityUtil.getFirst(emplPositionFulfillmentList);
        return emplPositionFulfillment;
    }

    public static Double getNumberOfDaysBetweenTwoDates(Date fromDate, Date thruDate) {
        int days = Days.daysBetween(new DateTime(fromDate), new DateTime(thruDate)).getDays();
        return Double.valueOf(days);
    }

    public static List getTrainingDetailsForTheEmployee(String partyId) {
        List<GenericValue> trainingGvList = null;
        try {
            trainingGvList = delegator.findList("TrainingRequestView", EntityCondition.makeCondition("partyId", partyId), null, null, null, false);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return trainingGvList;
    }

    public static Map getStatusIdDescriptionMap(String statusTypeId) {

        Map statusIdDesc = new HashMap();
        try {
            List<GenericValue> statusItemGvList = delegator.findList("StatusItem", EntityCondition.makeCondition("statusTypeId", statusTypeId), null, null, null, false);
            for (GenericValue statusItemGv : statusItemGvList) {
                String statusId = statusItemGv.getString("statusId");
                String desc = statusItemGv.getString("description");
                statusIdDesc.put(statusId, desc);
            }

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return statusIdDesc;
    }

    public static List getEmployeeLeaveInfoByComparingTwoEntity(String emplPosTypeIdOfParty, String partyId) {
        List<GenericValue> leaveLimitList = null;
        List emplLeaveInfoModel = new ArrayList();
        try {
            GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
            Map currentYearAndPreviousYearLeave = getCurrentAndPreviousYearStartAndEndDate();
            Timestamp currentYearStart = (Timestamp) currentYearAndPreviousYearLeave.get("currentYearStartDate");
            Timestamp currentYearEnd = (Timestamp) currentYearAndPreviousYearLeave.get("currentYearEndDate");
            Timestamp previousYearStart  = (Timestamp) currentYearAndPreviousYearLeave.get("previousYearStartDate");
            Timestamp previousYearEnd  = (Timestamp) currentYearAndPreviousYearLeave.get("previousYearEndDate");
            leaveLimitList = delegator.findByAnd("EmployeeLeaveLimitDetail", UtilMisc.toMap(
                    "employeeType", personGV.getString("employeeType")
                    , "positionCategory", personGV.getString("positionCategory"),
                    "beginYear",currentYearStart,
                    "endYear",currentYearEnd
            ),null,false);
            for (GenericValue leaveLimit : leaveLimitList) {
                Map emplLeaveInfoColumnMap = null;
                Map previousYearLeaveInfo =null;
                String emplPosTypeId = leaveLimit.getString("emplPosTypeId");
                String leaveTypeId = leaveLimit.getString("leaveTypeId");
                Timestamp beginYear = leaveLimit.getTimestamp("beginYear");
                Timestamp endYear = leaveLimit.getTimestamp("endYear");
                if(leaveLimit.getString("emplPosTypeId").trim().length()!=0 && !leaveLimit.getString("emplPosTypeId").equals(emplPosTypeIdOfParty)){
                   continue;
                }
                if(leaveLimit.getString("emplPosTypeId").trim().length()==0 && isLeaveLimitPriorityIsLower(leaveLimitList,emplPosTypeIdOfParty,leaveTypeId,beginYear,endYear)){
                    continue;
                }
                EntityCondition equalsConditions = EntityCondition.makeCondition(UtilMisc.toMap(
                        "leaveType", leaveTypeId,
                        "partyId", partyId,
                        "beginYear", beginYear,
                        "endYear", endYear
                ), EntityOperator.AND);
                List emplLeaveInfoGv = delegator.findList("EmployeeLeaveDetail",equalsConditions, null, null, null, false);
                if (UtilValidate.isNotEmpty(emplLeaveInfoGv)) {
                    GenericValue emplLeaveInfo = EntityUtil.getFirst(emplLeaveInfoGv);
                    emplLeaveInfoColumnMap = UtilMisc.toMap("description", emplLeaveInfo.getString("description"), "leaveLimit", emplLeaveInfo.getString("leaveLimit"),"fromDate",beginYear,"endDate",endYear);
                } else {
                    emplLeaveInfoColumnMap = UtilMisc.toMap("description", leaveLimit.getString("description"), "leaveLimit", leaveLimit.getString("numDays"),"fromDate",beginYear,"endDate",endYear);
                }

                equalsConditions = EntityCondition.makeCondition(UtilMisc.toMap(
                        "leaveType", leaveTypeId,
                        "partyId", partyId,
                        "beginYear", previousYearStart,
                        "endYear", previousYearEnd
                ), EntityOperator.AND);

                List previousYearEmplLeaveInfoGv = delegator.findList("EmployeeLeaveDetail",equalsConditions, null, null, null, false);
                if (UtilValidate.isNotEmpty(previousYearEmplLeaveInfoGv)) {
                    GenericValue emplLeaveInfo = EntityUtil.getFirst(previousYearEmplLeaveInfoGv);
                    previousYearLeaveInfo = UtilMisc.toMap("description", emplLeaveInfo.getString("description"), "leaveLimit", emplLeaveInfo.getString("leaveLimit"),"fromDate",previousYearStart,"endDate",previousYearEnd);
                } else {
                    List previousYearLeaveLimitList = delegator.findByAnd("EmployeeLeaveLimitDetail", UtilMisc.toMap(
                            "employeeType", personGV.getString("employeeType")
                            , "positionCategory", personGV.getString("positionCategory"),
                            "beginYear",previousYearStart,
                            "endYear",previousYearEnd
                    ),null,false);
                    GenericValue previousYearLeaveLimit = EntityUtil.getFirst(previousYearLeaveLimitList);
                    if (UtilValidate.isNotEmpty(previousYearLeaveLimit)){
                        previousYearLeaveInfo = UtilMisc.toMap("description", previousYearLeaveLimit.getString("description"), "leaveLimit", previousYearLeaveLimit.getString("numDays"),"fromDate",previousYearStart,"endDate",previousYearEnd);
                    }else{
                        previousYearLeaveInfo = UtilMisc.toMap("description", leaveLimit.getString("description"), "leaveLimit", "0.0","fromDate",previousYearStart,"endDate",previousYearEnd);
                    }

                }
                if(UtilValidate.isNotEmpty(previousYearLeaveInfo)){
                    emplLeaveInfoModel.add(previousYearLeaveInfo);
                }
                emplLeaveInfoModel.add(emplLeaveInfoColumnMap);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return emplLeaveInfoModel;
    }

    public static boolean isLeaveLimitPriorityIsLower(List<GenericValue> leaveLimitList,String positionTypeId,String leaveTypeId,Timestamp beginYear,Timestamp endYear){
        for(GenericValue leaveLimit:leaveLimitList){
            if(leaveLimit.getString("leaveTypeId").equals(leaveTypeId) &&
                     leaveLimit.getString("emplPosTypeId").equals(positionTypeId) &&
                    leaveLimit.getTimestamp("beginYear").equals(beginYear) &&
                    leaveLimit.getTimestamp("endYear").equals(endYear)) {
                return true;
            }
        }
        return false;
    }
    public static Map<String, String> getEnumIdDescription(String enumTypeId) {
        Map<String, String> enumIdDescription = new HashMap<String, String>();
        try {
            List<GenericValue> enumerationGvList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", enumTypeId), null, null, null, false);
            for (GenericValue enumerationGv : enumerationGvList) {
                String enumId = enumerationGv.getString("enumId");
                String desc = enumerationGv.getString("description");
                enumIdDescription.put(enumId, desc);
            }

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return enumIdDescription;

    }

    public static ListModelList getGrades(){
        return  new ListModelList(Arrays.asList(new String[] {"G1A","G1B","G1C","G2A","G2B","G2C","G3A","G3B","G3C","G4A","G4B","G4C","G5A",
        		"M3","M5A","M5AB","M5B","M5D","M6","M6A","M6B","M6D","M6EA","M7","M7A","M7AA","M7AB","M7B","M7D","M7DA","M7E",
        		"ZPCG 3","ZPCG 4","ZPCG 5","ZPCG 6","ZPCG 7","ZPCM 7A","ZPCM 7AA","ZPCM 7AAA","ZPCM 7C"}));
    }
    
       

    public static List getListOfDivisionIdOfADepartment(final String departmentId){
        List divisionId =new ArrayList<GenericValue>();
        try {
            List<GenericValue> departmentDivisionGvList = delegator.findByAnd("DepartmentDivision",UtilMisc.toMap("departmentId",departmentId),null,false);
            for(GenericValue departmentDivisionGv :departmentDivisionGvList){
                divisionId.add(departmentDivisionGv.getString("divisionId"));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return divisionId;
    }

    public static List<GenericValue> getEmployeeLoanInfo(String emplPosTypeIdOfParty, String partyId) throws GenericEntityException{
    	
    	GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
    	List <GenericValue> finalLoanLimitList= null;
    	Map<String,GenericValue> loanLimitMap = new HashMap<String,org.ofbiz.entity.GenericValue>();
    	List<GenericValue> loanLimitList =  delegator.findByAnd("EmployeeLoanLimitDetail", UtilMisc.toMap("employeeType",personGV.getString("employeeType"),"positionCategory",personGV.getString("positionCategory")));
    	if(UtilValidate.isNotEmpty(loanLimitList)){
    		for(GenericValue loanLimit : loanLimitList){
    			if(!loanLimitMap.containsKey(loanLimit.getString("loanType")) && (UtilValidate.isEmpty(loanLimit.getString("emplPositionTypeId")) || emplPosTypeIdOfParty.equals(loanLimit.getString("emplPositionTypeId")))){
    				loanLimitMap.put(loanLimit.getString("loanType"),loanLimit);
    			}
    	 		else if(loanLimitMap.containsKey(loanLimit.getString("loanType")) && emplPosTypeIdOfParty.equals(loanLimit.getString("emplPositionTypeId"))){
    				loanLimitMap.put(loanLimit.getString("loanType"),loanLimit);
    			}
    		}
    	}
    	finalLoanLimitList = new ArrayList<GenericValue>( loanLimitMap.values());
    return finalLoanLimitList;
}
    
public static List<GenericValue> getEmployeeClaimInfo(String emplPosTypeIdOfParty, String partyId) throws GenericEntityException{
   
		GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
    	List <GenericValue> finalClaimLimitList= null;
    	Map<String,GenericValue> claimLimitMap = new HashMap<String,org.ofbiz.entity.GenericValue>();
    	List<GenericValue> claimLimitList =  delegator.findByAnd("EmployeeClaimLimitDetail", UtilMisc.toMap("employeeType",personGV.getString("employeeType"),"positionCategory",personGV.getString("positionCategory")));
    	if(UtilValidate.isNotEmpty(claimLimitList)){
    		for(GenericValue claimLimit : claimLimitList){
    			if(!claimLimitMap.containsKey(claimLimit.getString("claimType")) && (UtilValidate.isEmpty(claimLimit.getString("emplPositionTypeId")) || emplPosTypeIdOfParty.equals(claimLimit.getString("emplPositionTypeId")))){
    				claimLimitMap.put(claimLimit.getString("claimType"),claimLimit);
    			}
    	 		else if(claimLimitMap.containsKey(claimLimit.getString("claimType")) && emplPosTypeIdOfParty.equals(claimLimit.getString("emplPositionTypeId"))){
    				claimLimitMap.put(claimLimit.getString("claimType"),claimLimit);
    			}
    		}
    	}
    	finalClaimLimitList = new ArrayList<GenericValue>( claimLimitMap.values());
    return finalClaimLimitList;
}

	public static String getCeoEmailId(Delegator delegator)throws GenericEntityException, GenericServiceException, InterruptedException{
			List  personList=delegator.findByAnd("Person", UtilMisc.toMap("isCeo","Y"));
			if(UtilValidate.isEmpty(personList)){
					Messagebox.show("CEO is not configured for the organization", "Error",1, null);
					return null;
			}
			GenericValue personGv=(GenericValue)personList.get(0);
			String partyId=personGv.getString("partyId");
			List<GenericValue> contactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
			if (UtilValidate.isEmpty(contactMechPurposes)) {
				Messagebox.show("Email cannot be sent;Primary Email for CEO is not configured", "Error", 1, null);
				return null;
			}
    String contactMechId = contactMechPurposes.get(0).getString("contactMechId");
    for (GenericValue contactMechPurposeGv : contactMechPurposes) {
        if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(contactMechPurposeGv.getString("contactMechId"))) {
            contactMechId = contactMechPurposeGv.getString("contactMechId");
        }
    }
    List<GenericValue> contactMechGvs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "contactMechId", contactMechId));
    GenericValue contactMechGv = (GenericValue)contactMechGvs.get(0);
    if(UtilValidate.isNotEmpty(contactMechGv.getString("infoString")))
    	return contactMechGv.getString("infoString");
    else{
    	Messagebox.show("Email cannot be sent;Primary Email for CEO is not configured", "Error", 1, null);
       return null;
    }
}

public static Map<String,Timestamp> getCurrentAndPreviousYearStartAndEndDate(){
    final Timestamp currentYearStartDate = UtilDateTime.getYearStart(new Timestamp(new Date().getTime()));
    final Timestamp previousYearStartDate = UtilDateTime.getYearStart(UtilDateTime.getPreviousYear(currentYearStartDate));
    final Timestamp currentYearEndDate = UtilDateTime.getDayStart(UtilDateTime.getYearEnd(currentYearStartDate, TimeZone.getDefault(), Locale.getDefault()));
    final Timestamp previousYearEndDate = UtilDateTime.getDayStart(UtilDateTime.getYearEnd(previousYearStartDate, TimeZone.getDefault(), Locale.getDefault()));
    Map timestamps = new HashMap(){{
        put("currentYearStartDate",currentYearStartDate);
        put("currentYearEndDate",currentYearEndDate);
        put("previousYearStartDate",previousYearStartDate);
        put("previousYearEndDate",previousYearEndDate);
    }};
    return timestamps;
}


    public  static boolean isPreviousYearLeave(GenericValue employeeLeaveInfo){
        Map timestamps = getCurrentAndPreviousYearStartAndEndDate();
        Timestamp previousYearStartDate = (Timestamp) timestamps.get("previousYearStartDate");
        Timestamp previousYearEndDate = (Timestamp) timestamps.get("previousYearEndDate");
        return  previousYearStartDate.equals(employeeLeaveInfo.getTimestamp("beginYear")) && previousYearEndDate.equals(employeeLeaveInfo.getTimestamp("endYear"));

    }

    public static Map getPartyContactMechValueMaps(Delegator delegator, String partyId) throws GenericEntityException {
        List<GenericValue> partyContactMechList = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId),null,false);
        Map contactMechMap = new HashMap();
        for (GenericValue partyContactMech:partyContactMechList){
            GenericValue contactMech = delegator.getRelatedOne("ContactMech", partyContactMech,false);
            if(contactMech.getString("contactMechTypeId").equals("EMAIL_ADDRESS")){
                contactMechMap.put("email", contactMech.getString("infoString"));
            }else if(contactMech.getString("contactMechTypeId").equals("TELECOM_NUMBER")){
                List<GenericValue> partyContactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMech.getString("contactMechId")), null, false);
                for(GenericValue partyContactMechPurpose:partyContactMechPurposes){
                    if("EMERGENCY_PHONE_NUMBER".equals(partyContactMechPurpose.getString("contactMechPurposeTypeId"))){
                        GenericValue contactNumber = contactMech.getRelatedOne("TelecomNumber", false);
                        contactMechMap.put("emergencyPhoneNumber", contactNumber.getString("contactNumber"));
                    }else{
                        GenericValue contactNumber = contactMech.getRelatedOne("TelecomNumber", false);
                        if(UtilValidate.isNotEmpty(contactNumber)){
                            contactMechMap.put("phoneNumber", contactNumber.getString("contactNumber"));
                        }
                    }
                }
            }
        }
        return contactMechMap;
    }
}


