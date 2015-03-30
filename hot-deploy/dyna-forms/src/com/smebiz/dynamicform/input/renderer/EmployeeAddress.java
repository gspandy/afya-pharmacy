package com.smebiz.dynamicform.input.renderer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanresext.util.HumanResUtil;

import com.smebiz.common.PartyContactHelper;
import com.smebiz.common.PartyUtil;
import com.smebiz.dynamicform.renderer.RendererContext;

public class EmployeeAddress {

	public static final String NEW_LINE = "\n";
	
	public void preRender(RendererContext ctx ) {
				
		String partyId = (String)ctx.get("partyId");
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");

		GenericValue contactValue = null;
		try {
			contactValue = PartyContactHelper.getPostalAddressValueByPurpose(
					partyId, "PRIMARY_LOCATION", true, delegator);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void getFullName(HttpServletRequest req,
			HttpServletResponse res) {
		String partyId = req.getParameter("partyId");
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");
		String fullName = "";
		try {
			fullName = PartyUtil.getFullName(delegator, partyId, ",");
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			res.getWriter().write(fullName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getDesignationForFiscalYear(HttpServletRequest req,HttpServletResponse res) {
		String partyId = req.getParameter("partyId");
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");
		GenericValue partyVal = null;
		try {
			partyVal = HumanResUtil.getLatestEmplPositionFulfillmentForParty(
					partyId, delegator);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String description = null;
		if (partyVal != null) {
			GenericValue positionTypeValue = null;
			try {
				positionTypeValue = partyVal.getRelatedOne("EmplPositionType");
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			description = positionTypeValue.getString("description");
		}
		try {
			res.getWriter().println(description);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
