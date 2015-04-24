package com.smebiz.employmentApp;

import static org.ofbiz.humanresext.util.HumanResUtil.getEmailAddress;
import static org.ofbiz.humanresext.util.HumanResUtil.getReportingMangerForParty;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;

public class ApplicationService {

	public static final String module = ApplicationService.class.getName();

	public static Map<String, Object> createApplication(DispatchContext dctx,
			Map<String, Object> context) throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		// String lupdatedBy =
		// (String)((GenericValue)context.get("userLogin")).get("partyId");
		String lapplyingPartyId = (String) context.get("applyingPartyId");
		String lreferredByPartyId = (String) context.get("referredByPartyId");
		Timestamp lapplicationDate = (Timestamp) context.get("applicationDate");
		/* Added for HRMS while Creating the Prospect in Requisition*/
		Timestamp afterConvinientTime = (Timestamp) context.get("afterConvinientTime");
		Timestamp beforeConvinientTime = (Timestamp) context.get("beforeConvinientTime");
		String lstatusId = (String) context.get("statusId");
		String requisitionId = (String) context.get("requisitionId");
		String fileName = (String) context.get("fileName");
		String lemplPositionId = (String) context.get("emplPositionId");
		String lemploymentAppSourceTypeId = (String) context
				.get("employmentAppSourceTypeId");
		Map<String, Object> appVals = FastMap.newInstance();
		appVals.putAll(UtilMisc.toMap("applyingPartyId", lapplyingPartyId,"requisitionId",
				requisitionId, "fileName", fileName,
				"referredByPartyId", lreferredByPartyId, "applicationDate",
				lapplicationDate, "emplPositionId", lemplPositionId,
				"employmentAppSourceTypeId", lemploymentAppSourceTypeId,
				"statusId", lstatusId,"beforeConvinientTime",beforeConvinientTime,"afterConvinientTime",afterConvinientTime));

		GenericValue appGV = delegator.makeValue("EmploymentApp", appVals);
		String lapplicationId = delegator.getNextSeqId("EmploymentApp");
		appGV.set("applicationId", lapplicationId);
		try {
			appGV.create();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil
					.returnError("An Error occurred creating the Application record"
							+ e.getMessage());
		}

		Map<String, Object> result = ServiceUtil
				.returnSuccess("Application created");

		result.put("applicationId", lapplicationId);
		return result;
	}

}
