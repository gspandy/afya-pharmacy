package org.ofbiz.humanresext.perfReview;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanresext.PerfReviewStatus;
import org.ofbiz.service.DispatchContext;

public class PerfReviewServices {

	public static final String module = PerfReviewServices.class.getName();
	public static final String resource = "HumanResExtErrLabels";

	public static Map<String, Object> createEmplPerfReview(DispatchContext ctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException {

		Map<String, Object> result = FastMap.newInstance();
		GenericDelegator delegator = (GenericDelegator) ctx.getDelegator();

		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");

		String perfReviewId = (String) context.get("perfReviewId");
		GenericValue emplPerfReviewValue = null;
		String selfPrefReviewId = delegator.getNextSeqId("EmplPerfReview");
		Map<String, String> emplPerfReviewData = UtilMisc.toMap(
				"emplPerfReviewId", selfPrefReviewId, "partyId", partyId,
				"perfReviewId", perfReviewId, "statusType",
				PerfReviewStatus.PERF_IN_PROGRESS.toString());
		try {
			delegator.create("EmplPerfReview", emplPerfReviewData);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result.put("emplPerfReview", emplPerfReviewValue);
		return result;

	}
}
