package com.nthdimenzion.humanres.payroll.webapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.smebiz.common.UtilDateTimeSME;

public class SalaryHeadOptionsController {

	private static final Map<String, String> responseMap = new HashMap<String, String>();
	static{
		responseMap.put(null, "none");
		responseMap.put("LUMPSUMP", "lumpsump");
		responseMap.put("SLAB", "slab");
		responseMap.put("FORMULA", "formula");
		responseMap.put("MONTHLY_LUMPSUMP", "monthlylumpsump");
	}
	
	public static String execute(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");//GenericDelegator.getGenericDelegator("default");
		String salaryStructureHeadId = request.getParameter("salaryStructureHeadId");
		//String infromDate = (String)request.getParameter("fromDate");
//		System.out.println("@@@@@@@@@@@ infromDate :" + infromDate);
		//infromDate = infromDate + " 00:00:00";
		//java.sql.Date vfromDate = new java.sql.Date(UtilDateTimeSME.toDateYMD(infromDate.trim(), "-").getTime());
		String computationType = null;
		
		if(StringUtils.isNotBlank(salaryStructureHeadId)){
			EntityCondition condition = EntityCondition.makeCondition("salaryStructureHeadId", salaryStructureHeadId);
			List<GenericValue> salStruct = delegator.findList("SalaryStructureHead", condition, null, null, null, false);
/**
			GenericValue structureHead = delegator.findOne("SalaryStructureHead", false, "salaryStructureHeadId", salaryStructureHeadId); //, "fromDate", vfromDate);**/
			GenericValue structureHead = salStruct.get(0);//Get the first salary structure matching the id
			GenericValue head = structureHead.getRelatedOne("SalaryHead");
			computationType = head.getString("salaryComputationTypeId");
		}
		return responseMap.get(computationType);
	}
}