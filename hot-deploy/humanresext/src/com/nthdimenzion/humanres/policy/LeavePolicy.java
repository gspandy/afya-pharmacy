package com.nthdimenzion.humanres.policy;

import java.util.List;

import org.ofbiz.entity.GenericValue;

public class LeavePolicy {

	public enum LeavePolicyAttribute {

		EL_PER_MONTH {
			public void setValue(LeavePolicy policy, Number num) {
				policy.setEarnedLeavesPerMonth(num.floatValue());
			}
		},
		EL_PER_YEAR {
			public void setValue(LeavePolicy policy, Number num) {
				policy.setEarnedLeavesPerYear(num.floatValue());
			}
		},
		EL_MAX {
			public void setValue(LeavePolicy policy, Number num) {
				policy.setEarnedLeaveLimit(num.intValue());
			}
		},
		EL_YEARS {
			public void setValue(LeavePolicy policy, Number num) {
				policy.setMaxYears(num.intValue());
			}
		};

		public abstract void setValue(LeavePolicy policy, Number num);
	}

	

	private float earnedLeavesPerMonth;
	private float earnedLeavesPerYear;
	private float earnedLeaveLimit;
	private int maxYears;

	public float getEarnedLeavesPerMonth() {
		return earnedLeavesPerMonth;
	}

	public void setEarnedLeavesPerMonth(float earnedLeavesPerMonth) {
		this.earnedLeavesPerMonth = earnedLeavesPerMonth;
	}

	public float getEarnedLeavesPerYear() {
		return earnedLeavesPerYear;
	}

	public void setEarnedLeavesPerYear(float earnedLeavesPerYear) {
		this.earnedLeavesPerYear = earnedLeavesPerYear;
	}

	public float getEarnedLeaveLimit() {
		return earnedLeaveLimit;
	}

	public void setEarnedLeaveLimit(float earnedLeaveLimit) {
		this.earnedLeaveLimit = earnedLeaveLimit;
	}

	public int getMaxYears() {
		return maxYears;
	}

	public void setMaxYears(int maxYears) {
		this.maxYears = maxYears;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" Leaves Per Month " + earnedLeavesPerMonth);
		builder.append(" Leaves Per Year " + earnedLeavesPerYear);
		builder.append(" Leaves Limit " + earnedLeaveLimit);
		builder.append(" Leaves Max Years " + earnedLeavesPerYear);
		return builder.toString();
	}
}
