package com.ndz.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Executions;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class LoginManagement{
	public static String getUserLoginDetail(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		String userLoginId = request.getParameter("USERNAME");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		List<GenericValue> userLoginSecurityGroupList = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId",userLoginId));
		String groupId = userLoginSecurityGroupList.get(0).getString("groupId");
		if(groupId.equals("HRMS_RECRUIT")){
			return "agency";
		}else{
		return "success";
		}
		
	}

}
