package com.nthdimenzion.humanres.payroll.webapp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;


public class SlabSHAssocController {

	private static final String module = SlabSHAssocController.class.getName();

	public static String execute(HttpServletRequest req, HttpServletResponse resp){

		String payGradeId = req.getParameter("payGradeId");
		String salaryStepId = req.getParameter("salaryStepSeqId");
		String salaryStructureHeadId = req
				.getParameter("salaryStructureHeadId");

		System.out
				.println("salaryStructureHeadId " + salaryStructureHeadId
						+ " salaryStepId " + salaryStepId + " payGradeId "
						+ payGradeId);

		GenericDelegator delegator = (GenericDelegator) req.getAttribute("delegator");//GenericDelegator.getGenericDelegator("default");
		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();

			

			String actionId = delegator.getNextSeqId("PayrollAction");
			
			GenericValue action = delegator.create("PayrollAction", UtilMisc
					.toMap("actionId",actionId,"actionName", payGradeId + " - " + salaryStepId
							+ " - " + salaryStructureHeadId, "operandOne",
							"#GRADE.(" + payGradeId + ")#SALSTEPSEQ("+salaryStepId+")", "operandTwo", "0"));
			
			List<GenericValue> conds = delegator.findByAnd("PayrollCondition",
					UtilMisc.toMap("operandOne", "true", "operandTwo", "true"));

			GenericValue condition = null;
			if (conds.size() > 0) {
				condition = conds.get(0);
			}

			String conditionId = condition.getPkShortValueString();

			System.out.println(" conditionId " + conditionId + " actionId "
					+ actionId);

			String ruleId = delegator.getNextSeqId("PayrollRule");
			
			GenericValue ruleGV = delegator.create("PayrollRule", UtilMisc
					.toMap("ruleId",ruleId,"ruleName", " Association of PayGrade " + payGradeId
							+ " salstep " + salaryStepId));
			delegator.store(ruleGV);

			delegator.create("RuleConditionAction", UtilMisc.toMap("ruleId",
					ruleId, "actionId", actionId, "conditionId", conditionId));

			delegator.create("PayrollHeadRule", UtilMisc.toMap(
					"salaryStructureHeadId", salaryStructureHeadId, "ruleId",
					ruleId));
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// only rollback the transaction if we started one...
				TransactionUtil.rollback(beganTransaction,
						"Error saving abandoned cart info", e);
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
								"Could not commit transaction for entity engine error occurred while saving abandoned cart information",
								module);
			}
		}
		return "success";
	}
}
