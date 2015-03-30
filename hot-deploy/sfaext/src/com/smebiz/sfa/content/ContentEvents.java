package com.smebiz.sfa.content;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

import com.smebiz.sfa.common.UtilMessage;
import com.smebiz.sfa.security.CrmsfaSecurity;

public class ContentEvents {

    public static final String module = ContentEvents.class.getName();

    public static String downloadPartyContent(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String contentId = request.getParameter("contentId");
        String partyId = request.getParameter("partyId");

        try {
            GenericValue dataResource = getDataResource(request);
            if (dataResource == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", UtilMisc.toMap("contentId", contentId), locale, module);
            }

            // get the module corresponding to the internal party
            String module = CrmsfaSecurity.getSecurityModuleOfInternalParty(partyId, delegator);
            if (module == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }

            // ensure association exists between our party and content (ignore role because we're using module to check for security)
            List conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("contentId", EntityOperator.EQUALS, contentId),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                    EntityUtil.getFilterByDateExpr()
                    );
            GenericValue association = EntityUtil.getFirst( delegator.findList("ContentRole", EntityCondition.makeCondition(conditions),null,null,null,false) );
            if (association == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }

            // check if userLogin has permission to view
            if (!CrmsfaSecurity.hasPartyRelationSecurity(security, module, "_VIEW", userLogin, partyId)) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }

            return serveDataResource(request, response, dataResource);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, module);
        }
    }

    public static String downloadCaseContent(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String contentId = request.getParameter("contentId");
        String custRequestId = request.getParameter("custRequestId");

        try {
            GenericValue dataResource = getDataResource(request);
            if (dataResource == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", UtilMisc.toMap("contentId", contentId), locale, module);
            }

            // ensure association exists between case and content
            List conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("contentId", EntityOperator.EQUALS, contentId),
                    EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
                    EntityUtil.getFilterByDateExpr()
                    );
            GenericValue association = EntityUtil.getFirst( delegator.findByAnd("CustRequestContent", conditions) );
            if (association == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }

            // check if the userLogin has case view permission
            /*if (!CrmsfaSecurity.hasCasePermission(security, "_VIEW", userLogin, custRequestId)) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }*/

            return serveDataResource(request, response, dataResource);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, module);
        }
    }

    public static String downloadOpportunityContent(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String contentId = request.getParameter("contentId");
        String salesOpportunityId = request.getParameter("salesOpportunityId");

        try {
            GenericValue dataResource = getDataResource(request);
            if (dataResource == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", UtilMisc.toMap("contentId", contentId), locale, module);
            }

            // ensure association exists between case and content
            List conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("contentId", EntityOperator.EQUALS, contentId),
                    EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId),
                    EntityUtil.getFilterByDateExpr()
                    );
            GenericValue association = EntityUtil.getFirst( delegator.findList("SalesOpportunityContent", EntityCondition.makeCondition(conditions),null,null,null,false ) );
            if (association == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }

            // check if the userLogin has case view permission
            if (!CrmsfaSecurity.hasOpportunityPermission(security, "_VIEW", userLogin, salesOpportunityId)) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }

            return serveDataResource(request, response, dataResource);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, module);
        }
    }

    public static String downloadActivityContent(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String contentId = request.getParameter("contentId");
        String workEffortId = request.getParameter("workEffortId");

        try {
            GenericValue dataResource = getDataResource(request);
            if (dataResource == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", UtilMisc.toMap("contentId", contentId), locale, module);
            }

            // ensure association exists between case and content
            List conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("contentId", EntityOperator.EQUALS, contentId),
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                    EntityUtil.getFilterByDateExpr()
                    );
            GenericValue association = EntityUtil.getFirst( delegator.findByAnd("WorkEffortContent", conditions) );
            if (association == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }

            // check if the userLogin has case view permission
            /*if (!CrmsfaSecurity.hasActivityPermission(security, "_VIEW", userLogin, workEffortId)) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, module);
            }*/

            return serveDataResource(request, response, dataResource);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, module);
        }
    }


    // get the contentId and verify that we have something to download
    private static GenericValue getDataResource(HttpServletRequest request) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        GenericValue content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", request.getParameter("contentId")));
        if (content == null) return null;

        GenericValue dataResource = content.getRelatedOne("DataResource");
        if (dataResource == null || !"LOCAL_FILE".equals(dataResource.get("dataResourceTypeId"))) return null;
        return dataResource;
    }

    // find the file and write it to the client stream
    private static String serveDataResource(HttpServletRequest request, HttpServletResponse response, GenericValue dataResource) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        ServletContext application = session.getServletContext();
        Map input = UtilMisc.toMap("contentId", request.getParameter("contentId"));
        try {
            String fileLocation = dataResource.getString("objectInfo");
            String fileName = dataResource.getString("dataResourceName");

            // the file name needs to be UTF8 urlencoded for the content disposition HTTP header
            fileName = "=?UTF-8?B?" + new String(Base64.base64Encode(fileName.getBytes("UTF-8")), "UTF-8") + "?=";

            if (UtilValidate.isEmpty(fileLocation)) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", input, locale, module);
            }

            // test if the file exists here, due to strange bugs with DataResourceWorker.streamDataResource
            File file = new File(fileLocation);
            if (!file.exists()) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", input, locale, module);
            }

            // Set the headers so that the browser treats content as a download (this could be changed to use the mimeTypeId of the content for in-browser display)
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName+"\"");

            // write the file to the client browser
            OutputStream os = response.getOutputStream();
            DataResourceWorker.streamDataResource(os, delegator, dataResource.getString("dataResourceId"), "", application.getInitParameter("webSiteId"), UtilHttp.getLocale(request), application.getRealPath("/"));
            os.flush();
            return "success";
        } catch (GeneralException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, module);
        } catch (IOException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, module);
        }
    }
}
