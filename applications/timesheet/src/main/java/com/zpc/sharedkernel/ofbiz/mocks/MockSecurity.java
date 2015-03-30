package com.zpc.sharedkernel.ofbiz.mocks;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 10/7/2014.
 */
public class MockSecurity implements org.ofbiz.security.Security {
    @Override
    public Delegator getDelegator() {
        return null;
    }

    @Override
    public void setDelegator(Delegator delegator) {

    }

    @Override
    public Iterator<GenericValue> findUserLoginSecurityGroupByUserLoginId(String s) {
        return null;
    }

    @Override
    public boolean securityGroupPermissionExists(String s, String s2) {
        return false;
    }

    @Override
    public boolean hasPermission(String permission, HttpSession httpSession) {
        if("HUMANRES_ADMIN".equals(permission))
            return true;
        else
            return false;
    }

    @Override
    public boolean hasPermission(String permission, GenericValue userLogin) {
        if("HUMANRES_ADMIN".equals(permission))
            return true;
        else
            return false;
    }

    @Override
    public boolean hasEntityPermission(String s, String s2, HttpSession httpSession) {
        return false;
    }

    @Override
    public boolean hasEntityPermission(String s, String s2, GenericValue genericValue) {
        return false;
    }

    @Override
    public boolean hasRolePermission(String s, String s2, String s3, String s4, HttpSession httpSession) {
        return false;
    }

    @Override
    public boolean hasRolePermission(String s, String s2, String s3, String s4, GenericValue genericValue) {
        return false;
    }

    @Override
    public boolean hasRolePermission(String s, String s2, String s3, List<String> strings, GenericValue genericValue) {
        return false;
    }

    @Override
    public boolean hasRolePermission(String s, String s2, String s3, List<String> strings, HttpSession httpSession) {
        return false;
    }

    @Override
    public void clearUserData(GenericValue genericValue) {

    }
}
