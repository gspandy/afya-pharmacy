package com.nthdimenzion.humanres.policy;

public class PolicyDelegator {
	
	private LeavePolicyDelegator leavePolicyDelegator;
	
	public void setLeavePolicyDelegator(LeavePolicyDelegator leavePolicyDelegator) {
		this.leavePolicyDelegator = leavePolicyDelegator;
	}

	public LeavePolicyDelegator getLeavePolicyDelegator(){
		return this.leavePolicyDelegator;
	}
}
