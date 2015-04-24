package com.nthdimenzion.humanres.cron;

import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.nthdimenzion.humanres.policy.LeavePolicyDelegator;
import com.nthdimenzion.humanres.policy.PolicyDelegator;
import com.smebiz.common.SpringUtil;

public class PolicyJobService {
	
	public static Map<String, Object> executeMonthlyCron(DispatchContext dctx,
			Map<String, ? extends Object> context){
		
		PolicyDelegator delegator = (PolicyDelegator)SpringUtil.getBean("PolicyDelegator");
		System.out.println(" POLICY DELEGATOR "+delegator);
		LeavePolicyDelegator leavePolicyDelegator = delegator.getLeavePolicyDelegator();
		System.out.println(" LEAVE POLICY DELEGATOR "+leavePolicyDelegator);
		leavePolicyDelegator.runMonthEndBatch((GenericDelegator)dctx.getDelegator());
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> allocateEarnedLeave(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException{
		
		String partyId =(String)context.get("partyIdTo");
		System.out.println("********** allocate Earned Leave to Party Id "+partyId);
		PolicyDelegator delegator = (PolicyDelegator)SpringUtil.getBean("PolicyDelegator");
		LeavePolicyDelegator leavePolicyDelegator = delegator.getLeavePolicyDelegator();
		leavePolicyDelegator.allocateEarnedLeave(partyId, (GenericDelegator)dctx.getDelegator());
		
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> executeRollup(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException{
		
		String partyId =(String)context.get("partyIdTo");
		System.out.println("********** allocate Earned Leave to Party Id "+partyId);
		PolicyDelegator delegator = (PolicyDelegator)SpringUtil.getBean("PolicyDelegator");
		LeavePolicyDelegator leavePolicyDelegator = delegator.getLeavePolicyDelegator();
		leavePolicyDelegator.executeRollup( (GenericDelegator)dctx.getDelegator());
		
		return ServiceUtil.returnSuccess();
	}
	
}
