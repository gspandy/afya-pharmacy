package org.ofbiz.humanresext.leave;

/**
 * @author sandeep
 * 
 */
public class LeaveTypeCountWrapper {
	private final String leaveTypeId;
	private Number entitled = 0;
	private Number used = 0;
	private Number balance = 0;

	public LeaveTypeCountWrapper(String leaveTypId, Number entitled) {
		this.leaveTypeId = leaveTypId;
		this.entitled = this.balance = entitled;
	}

	public void setUsed(Number used) {
		// This was bombing if there is there now leaves which are cancelled or
		// rejected.

		if (used != null) {
			this.used = used;
			this.balance = this.balance.intValue() - this.used.intValue();
		}
	}

	public String getLeaveTypeId() {
		return leaveTypeId;
	}

	public Number getEntitled() {
		return entitled;
	}

	public Number getUsed() {
		return used;
	}

	public Number getBalance() {
		return balance;
	}
}