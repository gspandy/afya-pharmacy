package org.ofbiz.humanresext.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.humanresext.PerfReviewStatus;
import org.ofbiz.humanresext.perfReview.ManagerNotFoundException;
import org.ofbiz.service.LocalDispatcher;

public class SelfPerfReviewEvents {
    public static final String module = "SelfPerfReviewEvents";

    public static String initiateSelfAppraisal(HttpServletRequest request,
                                               HttpServletResponse response) throws GenericEntityException {

        LocalDispatcher dispatcher = (LocalDispatcher) request
                .getAttribute("dispatcher");
        HttpSession session = request.getSession();
        Map<String, Object> context = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) session
                .getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");
        context.put("partyId", partyId);
        context.put("perfReviewId", session.getAttribute("PERF_REVIEW_ID"));
        context.put("statusType", PerfReviewStatus.PERF_IN_PROGRESS.toString());
        context.put("userLogin", userLogin);
        context.put("request", request);
        Map<String, Object> resultMap = null;
        try {
            resultMap = dispatcher.runSync("createEmplPerfReview", context);
        } catch (Exception e) {

        }
        if ("error".equals(resultMap.get("responseMessage"))) {
            request.setAttribute("_ERROR_MESSAGE_",
                    "User does not have a Valid Position.");
            return "error";
        }

        GenericValue emplPerfReview = (GenericValue) resultMap
                .get("emplPerfReview");
        session.setAttribute("emplPerfReview", emplPerfReview);
        request.setAttribute("reviewId", resultMap.get("emplPerfReviewId"));
        populateSelfAppraisal(request, response);
        return "success";
    }

    private static Collection<Map<String, String>> convertToDataMap(
            Map<String, Object> parameters, boolean reviewFlag) {

        Set<Map.Entry<String, Object>> entries = parameters.entrySet();
        Collection<Map<String, String>> dataList = FastSet.newInstance();
        Map<String, Map<String, String>> dataMap = FastMap.newInstance();

        for (Map.Entry<String, Object> parameter : entries) {
            String value = (String) parameter.getValue();

            // If blank or NONE do no process it.
            if (StringUtils.isBlank(value) && "NONE".equals(value)) {
                continue;
            }

            String key = parameter.getKey();
            // If its an parameter name starts with perf_attr it means
            // self rating and comments are for that attribute.
            if (key.startsWith("perf_attr_")) {
                String attributeId = null;
                attributeId = key.substring(10);
                if (key.endsWith("_comments"))
                    attributeId = attributeId.substring(0, attributeId
                            .indexOf("_comments"));

                Map<String, String> map = dataMap.get(attributeId);
                if (map == null) {
                    map = FastMap.newInstance();
                    dataMap.put(attributeId, map);
                }
                if (key.endsWith("_comments")) {

                    map.put(reviewFlag ? "reviewerComment" : "selfComment",
                            StringUtils.trim(value));
                } else
                    map.put(reviewFlag ? "rating" : "selfRating", value);

                map.put("attributeId", attributeId);
            } else if (key.startsWith("perf_sec_") && !key.endsWith("comments")) {
                key = key.substring(9);
                Map<String, String> map = dataMap.get(key);
                if (map == null) {
                    map = FastMap.newInstance();
                    dataMap.put(key, map);
                }
                map.put("attributeId", "_NA_");
                map.put(reviewFlag ? "rating" : "selfRating", StringUtils
                        .trim(value));
                map.put(reviewFlag ? "reviewerComment" : "selfComment",
                        (String) parameters
                                .get("perf_sec_" + key + "_comments"));
            }

        }
        dataList = dataMap.values();
        return dataList;
    }

    public static String saveSelfReviewData(HttpServletRequest request,
                                            HttpServletResponse response) throws GenericEntityException {

        Map<String, Object> parameters = UtilHttp.getParameterMap(request);

        Collection<Map<String, String>> dataList = convertToDataMap(parameters,
                false);

        GenericValue userLogin = (GenericValue) request.getSession()
                .getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");

        GenericDelegator delegator = (GenericDelegator) request
                .getAttribute("delegator");

        String selfPrefReviewId = request.getParameter("reviewId");

        updatePerfReviewStatus(request, selfPrefReviewId,
                PerfReviewStatus.PERF_IN_PROGRESS);

        if (selfPrefReviewId == null) {
            request.setAttribute("_ERROR_MESSAGE_",
                    "Cannot save data as EmplPerfReview ID is missing.");
            return "error";
        }

        String sectionId = (String) parameters.get("selectedSectionId");
        List<GenericValue> GVList = new FastList<GenericValue>();
        for (Map<String, String> data : dataList) {
            data.put("emplPerfReviewId", selfPrefReviewId);
            data.put("perfReviewSectionId", sectionId);
            GenericValue value = delegator.makeValue("EmplPerfReviewAttribute",
                    data);
            GVList.add(value);
        }

        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
            delegator.storeAll(GVList);
        } catch (GenericEntityException e) {
            try {
                // only roll back the transaction if we started one...
                TransactionUtil.rollback(beganTransaction,
                        "Error Saving Section Attribute Values", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "Could not rollback transaction: "
                        + e2.toString(), module);
            }
        } finally {
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug
                        .logError(
                                e,
                                "Could not commit transaction for entity"
                                        + " engine error occurred while saving abandoned cart information",
                                module);
            }
        }
        return "success";
    }

    public static void addError(HttpServletRequest request, String message) {
        List<String> errors = (List<String>) request
                .getAttribute("_ERROR_MESSAGE_LIST_");
        if (errors == null) {
            errors = FastList.newInstance();
            request.setAttribute("_ERROR_MESSAGE_LIST_", errors);
        }
        errors.add(message);
    }

    private static void updatePerfReviewStatus(HttpServletRequest request,
                                               String id, PerfReviewStatus status) throws GenericEntityException {

        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session
                .getAttribute("userLogin");

        String partyId = userLogin.getString("partyId");
        GenericDelegator delegator = (GenericDelegator) request
                .getAttribute("delegator");
        GenericValue template = delegator.findOne("EmplPerfReview", UtilMisc
                .toMap("emplPerfReviewId", id), false);
        template.set("statusType", status.toString());
        boolean beganTransaction = false;
        try {
            delegator.store(template);

            if (status == PerfReviewStatus.PERF_REVIEW_PENDING
                    || status == PerfReviewStatus.PERF_REVIEW_DISAGREE) {
                EntityCondition condition = EntityCondition
                        .makeCondition(UtilMisc.toMap("emplPerfReviewId", id));
                List<GenericValue> values = delegator
                        .findList("EmplPerfReviewers", condition, null, null,
                                null, false);
                for (GenericValue entity : values) {
                    entity.set("statusType",
                            PerfReviewStatus.PERF_REVIEW_PENDING.toString());
                }
                delegator.storeAll(values);
            }
        } catch (GenericEntityException ge) {
            request.setAttribute("_ERROR_MESSAGE_",
                    "Could not Update the Performance Review");

            try {
                // only roll back the transaction if we started one...
                TransactionUtil.rollback(beganTransaction,
                        "Error Saving Section Attribute Values", ge);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "Could not rollback transaction: "
                        + e2.toString(), module);
            }
        } finally {
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug
                        .logError(
                                e,
                                "Could not commit transaction for entity"
                                        + " engine error occurred while saving abandoned cart information",
                                module);
            }
        }
    }

    public static String saveReviewerData(HttpServletRequest request,
                                          HttpServletResponse response) throws GenericEntityException {

        Map<String, Object> parameters = UtilHttp.getParameterMap(request);

        Collection<Map<String, String>> dataList = convertToDataMap(parameters,
                true);

        GenericValue userLogin = (GenericValue) request.getSession()
                .getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");

        GenericDelegator delegator = (GenericDelegator) request
                .getAttribute("delegator");

        String selfPrefReviewId = request.getParameter("reviewId");
        updatePerfReviewStatus(request, selfPrefReviewId,
                PerfReviewStatus.PERF_REVIEW_IN_PROGRESS);

        String sectionId = (String) parameters.get("selectedSectionId");
        List<GenericValue> GVList = new FastList<GenericValue>();
        for (Map<String, String> data : dataList) {
            data.put("emplPerfReviewId", selfPrefReviewId);
            data.put("perfReviewSectionId", sectionId);
            data.put("reviewerId", partyId);
            GenericValue value = delegator.makeValue("EmplPerfReviewers", data);
            GVList.add(value);
        }
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
            delegator.storeAll(GVList);
        } catch (GenericEntityException e) {
            try {
                // only roll back the transaction if we started one...
                TransactionUtil.rollback(beganTransaction,
                        "Error Saving Section Attribute Values", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "Could not rollback transaction: "
                        + e2.toString(), module);
            }
        } finally {
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug
                        .logError(
                                e,
                                "Could not commit transaction for entity"
                                        + " engine error occurred while saving abandoned cart information",
                                module);
            }
        }
        return "success";
    }

    private static Map createFormData(List<GenericValue> perfValues,
                                      boolean isReviewData) {
        List<Map<String, String>> sections = FastList.newInstance();
        Map<String, Object> sectionMap = FastMap.newInstance();
        for (GenericValue entity : perfValues) {

            String sectionId = entity.getString("perfReviewSectionId");
            Map<String, Object> sectionmap = (Map<String, Object>) sectionMap
                    .get(sectionId);
            if (sectionmap == null) {
                sectionmap = new HashMap<String, Object>();
                sectionmap.put("sectionId", sectionId);
                sectionmap.put("sectionName", entity.getString("sectionName"));
                sectionmap.put("description", entity.getString("sectionDesc"));
                sectionmap.put("weightage", entity.getString("weightage"));
                Map map = new HashMap();
                map.put("sectionId", sectionId);
                map.put("sectionName", (String) sectionmap.get("sectionName"));
                map.put("description", (String) sectionmap.get("description"));
                map.put("weightage", (String) sectionmap.get("weightage"));
                sections.add(map);
                sectionMap.put(sectionId, sectionmap);
            }

            List<Map<String, String>> attributeList = (List<Map<String, String>>) sectionmap
                    .get("attributeList");
            if (attributeList == null) {
                attributeList = FastList.newInstance();
            }

            String attributeId = entity.getString("attributeId");
            if (attributeId != null) {
                if ("_NA_".equals(attributeId)) {
                    sectionmap.put("selfComment", entity
                            .getString("selfComment"));
                    sectionmap
                            .put("selfRating", entity.getString("selfRating"));
                    sectionmap.put("rating", entity.getString("rating"));
                    if (isReviewData) {
                        sectionmap.put("reviewerComment", entity
                                .getString("reviewerComment"));
                    }
                    continue;
                }
                Map<String, String> attributeMap = new HashMap<String, String>();
                attributeMap.put("attributeId", attributeId);
                attributeMap.put("attributeName", entity
                        .getString("attributeName"));
                attributeMap.put("attributeDesc", entity
                        .getString("attributeDesc"));
                attributeMap.put("selfRating", entity.getString("selfRating"));
                attributeMap
                        .put("selfComment", entity.getString("selfComment"));
                if (isReviewData) {
                    attributeMap.put("rating", entity.getString("rating"));
                    attributeMap.put("reviewerComment", entity
                            .getString("reviewerComment"));
                }
                attributeList.add(attributeMap);
                sectionmap.put("attributeList", attributeList);
            }
        }
        sectionMap.put("perfSections", sections);
        return sectionMap;
    }

    public static String populateSelfAppraisal(HttpServletRequest request,
                                               HttpServletResponse response) throws GenericEntityException {

        HttpSession session = request.getSession();

        String reviewId = request.getParameter("reviewId");
        if (reviewId == null) {
            reviewId = (String) request.getAttribute("reviewId");
        }
        GenericDelegator delegator = (GenericDelegator) request
                .getAttribute("delegator");

        GenericValue userLogin = (GenericValue) session
                .getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");

        GenericValue emplPerfReview = (GenericValue) session
                .getAttribute("emplPerfReview");
        if (emplPerfReview == null) {
            EntityCondition condition = EntityCondition.makeCondition(UtilMisc
                    .toMap("perfReviewId", reviewId));
            emplPerfReview = delegator.findOne("EmplPerfReview", UtilMisc
                    .toMap("emplPerfReviewId", reviewId), false);
            session.setAttribute("emplPerfReview", emplPerfReview);
        }

        if (!partyId.equals(emplPerfReview.getString("partyId"))) {
            partyId = emplPerfReview.getString("partyId");
        }

        Map<String, Object> sectionMap = null;
        String reviewerId = request.getParameter("reviewerId");
        // If the Reivew is reviewed by Manager
        if (PerfReviewStatus.PERF_REVIEWED_BY_MGR.toString().equals(
                emplPerfReview.getString("statusType"))
                || PerfReviewStatus.PERF_REVIEW_AGREED.toString().equals(
                emplPerfReview.getString("statusType"))
                || PerfReviewStatus.PERF_REVIEW_COMPLETE.toString().equals(
                emplPerfReview.getString("statusType"))) {
            List<String> reviewers = null;
            try {
                reviewers = org.ofbiz.humanresext.perfReview.PerfReviewWorker
                        .getReviewers(request, reviewId);
            } catch (ManagerNotFoundException mnfe) {
                return "error";
            }
            request.setAttribute("reviewers", reviewers);
            if (reviewerId == null && reviewers != null) {
                reviewerId = reviewers.get(0);
                request.setAttribute("reviewerId", reviewerId);
            } else {
                // No Reviewer Found
                request.setAttribute("reviewerId", "_NA_");
            }
            EntityCondition entityCondition = EntityCondition
                    .makeCondition(UtilMisc.toMap("emplPerfReviewId", reviewId,
                            "reviewerId", reviewerId));
            List<GenericValue> perfValues = delegator.findList(
                    "PerfSectionAndAttributeForReviewer", entityCondition,
                    null, null, null, false);
            sectionMap = createFormData(perfValues, true);
        } else {
            EntityCondition entityCondition = EntityCondition
                    .makeCondition(UtilMisc.toMap("emplPerfReviewId", reviewId));
            List<GenericValue> perfValues = delegator.findList(
                    "PerfSectionAndAttribute", entityCondition, null, null,
                    null, false);
            sectionMap = createFormData(perfValues, false);
        }
        request.setAttribute("perfSections", sectionMap.get("perfSections"));
        request.getSession().setAttribute("perfGlobalMap", sectionMap);
        return "success";
    }

    public static String markAsReviewed(HttpServletRequest request,
                                        HttpServletResponse response) throws GenericEntityException {

        String reviewId = request.getParameter("reviewId");
        try {
            HttpSession session = request.getSession();
            GenericValue userLogin = (GenericValue) session
                    .getAttribute("userLogin");

            String partyId = userLogin.getString("partyId");

            GenericDelegator delegator = (GenericDelegator) request
                    .getAttribute("delegator");

            EntityCondition condition = EntityCondition
                    .makeCondition(UtilMisc.toMap("emplPerfReviewId", reviewId,
                            "reviewerId", partyId));
            List<GenericValue> values = delegator.findList("EmplPerfReviewers",
                    condition, null, null, null, false);
            for (GenericValue entity : values) {
                entity.set("statusType", PerfReviewStatus.PERF_REVIEWED_BY_MGR
                        .toString());
                delegator.store(entity);
            }

            condition = EntityCondition.makeCondition(UtilMisc.toMap(
                    "emplPerfReviewId", reviewId));

            EntityCondition havingCondition = EntityCondition.makeCondition(
                    "statusType", EntityComparisonOperator.NOT_EQUAL,
                    PerfReviewStatus.PERF_REVIEWED_BY_MGR.toString());
            condition = EntityCondition.makeCondition(UtilMisc.toList(
                    condition, havingCondition));
            List<GenericValue> reviews = delegator.findList(
                    "EmplPerfReviewers", condition, null, null, null, false);
            if (reviews.size() == 0) {
                updatePerfReviewStatus(request, reviewId,
                        PerfReviewStatus.PERF_REVIEWED_BY_MGR);
            }

        } catch (GenericEntityException ge) {
            request.setAttribute("_ERROR_MESSAGE_",
                    "Could not Update the Performance Review");
            request.setAttribute("managerView", "enabled");
            return "error";
        }
        return "success";
    }

    public static String populateReviewerForm(HttpServletRequest request,
                                              HttpServletResponse response) throws GenericEntityException {

        HttpSession session = request.getSession();
        request.setAttribute("managerView", "enabled");
        String reviewId = request.getParameter("reviewId");
        GenericDelegator delegator = (GenericDelegator) request
                .getAttribute("delegator");

        GenericValue userLogin = (GenericValue) session
                .getAttribute("userLogin");
        String partyId = userLogin.getString("partyId");
        EntityCondition entityCondition = EntityCondition
                .makeCondition(UtilMisc.toMap("emplPerfReviewId", reviewId,
                        "reviewerId", partyId));

        List<GenericValue> perfValues = delegator.findList(
                "PerfSectionAndAttributeForReviewer", entityCondition, null,
                null, null, false);

        Map<String, Object> sectionMap = createFormData(perfValues, true);
        System.out.println("SECTION MAP " + sectionMap);
        request.setAttribute("perfSections", sectionMap.remove("perfSections"));
        request.getSession().setAttribute("perfGlobalMap", sectionMap);

        return "success";

    }

    public static String markAsSubmitted(HttpServletRequest request,
                                         HttpServletResponse response) throws GenericEntityException {

        String reviewId = request.getParameter("reviewId");
        try {
            updatePerfReviewStatus(request, reviewId,
                    PerfReviewStatus.PERF_REVIEW_PENDING);
        } catch (GenericEntityException ge) {
            request.setAttribute("_ERROR_MESSAGE_",
                    "Could not Update the Performance Review");
            return "error";
        }
        return "success";
    }

    public static String markAgreed(HttpServletRequest request,
                                    HttpServletResponse response) throws GenericEntityException {

        String reviewId = request.getParameter("reviewId");
        try {
            updatePerfReviewStatus(request, reviewId,
                    PerfReviewStatus.PERF_REVIEW_AGREED);
        } catch (GenericEntityException ge) {
            request.setAttribute("_ERROR_MESSAGE_",
                    "Could not Update the Performance Review");
            request.setAttribute("managerView", "enabled");
            return "error";
        }
        return "success";
    }

    public static String markDisAgreed(HttpServletRequest request,
                                       HttpServletResponse response) throws GenericEntityException {

        String disagreedComments = request.getParameter("disagreedComments");
        if (StringUtils.isBlank(disagreedComments)) {
            request.setAttribute("_ERROR_MESSAGE_", "Please provide a reason.");
            return "error";
        }
        String id = request.getParameter("reviewId");
        try {
            GenericDelegator delegator = (GenericDelegator) request
                    .getAttribute("delegator");
            GenericValue template = delegator.findOne("EmplPerfReview",
                    UtilMisc.toMap("emplPerfReviewId", id), false);
            template.set("disAgreedComments", disagreedComments);
            delegator.store(template);
            updatePerfReviewStatus(request, id,
                    PerfReviewStatus.PERF_REVIEW_DISAGREE);
        } catch (GenericEntityException ge) {
            request.setAttribute("_ERROR_MESSAGE_",
                    "Could not Update the Performance Review");
            request.setAttribute("managerView", "enabled");
            return "error";
        }
        return "success";
    }

    public static String updatePerfReviewByHR(HttpServletRequest request,
                                              HttpServletResponse response) throws GenericEntityException {

        String id = request.getParameter("reviewId");
        GenericDelegator delegator = (GenericDelegator) request
                .getAttribute("delegator");
        GenericValue template = delegator.findOne("EmplPerfReview", UtilMisc
                .toMap("emplPerfReviewId", id), false);
        template.set("statusType", PerfReviewStatus.PERF_REVIEW_COMPLETE
                .toString());

        String comments = request.getParameter("comments");
        String feedback = request.getParameter("feedback");
        String overallrating = request.getParameter("overallrating");
        template.set("comments", comments);
        template.set("feedback", feedback);
        template.set("overallRating", overallrating);
        delegator.store(template);
        return "success";
    }
}
