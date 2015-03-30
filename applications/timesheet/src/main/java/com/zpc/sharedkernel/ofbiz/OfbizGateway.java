package com.zpc.sharedkernel.ofbiz;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Session;
import org.ofbiz.entity.*;

import java.util.Map;

import static org.zkoss.zk.ui.Executions.getCurrent;

/**
 * Author: Nthdimenzion
 */

public final class OfbizGateway {
    private static LocalDispatcher _localDispatcher;

    public static Map<String,Object> getUser(){
        return	((Map) getCurrent().getSession().getAttribute("userLogin"));
    }

    public static Delegator getDelegator(){
        return ((GenericValue) getCurrent().getSession().getAttribute("userLogin")).getDelegator();
    }

    public static Map<String,Object> getTimesheetInfo(){
        Security securityFromSession = (Security) getCurrent().getSession().getAttribute("security");
        boolean isAdmin = securityFromSession.hasPermission("HUMANRES_ADMIN", (javax.servlet.http.HttpSession) getCurrent().getSession().getNativeSession());
        boolean isManager = securityFromSession.hasPermission("HUMANRES_MGR", (javax.servlet.http.HttpSession) getCurrent().getSession().getNativeSession());
        boolean isHumanResUser = securityFromSession.hasPermission("HUMANRES_USER", (javax.servlet.http.HttpSession) getCurrent().getSession().getNativeSession());
        Map result = UtilMisc.toMap("isAdmin",isAdmin,"isManager",isManager, "isHumanResUser",isHumanResUser);
        return result;

    }

    public static String getUserId(){
        final Session session = getCurrent().getSession();
        return	(String)((Map) session.getAttribute("userLogin")).get("userLoginId");
    }

    public static String getPartyId(){
        final Session session = getCurrent().getSession();
        return	(String)((Map) session.getAttribute("userLogin")).get("partyId");
    }

    public static LocalDispatcher getDispatcher(){
        LocalDispatcher localDispatcher = (LocalDispatcher) getCurrent().getAttribute("dispatcher");
        if(localDispatcher==null){
            localDispatcher = (LocalDispatcher) getCurrent().getSession().getAttribute("dispatcher");
        }
        if(localDispatcher!=null){
            _localDispatcher= localDispatcher;
        }
        return _localDispatcher;
    }
}
