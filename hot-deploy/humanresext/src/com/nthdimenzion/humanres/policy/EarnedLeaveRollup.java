package com.nthdimenzion.humanres.policy;

import java.util.List;

import org.ofbiz.entity.GenericValue;

public enum EarnedLeaveRollup {

	FISCAL_YEAR {
		@Override
		public void rollup(List<GenericValue> records) {
			// TODO Auto-generated method stub
		}
	};

	public abstract void rollup(List<GenericValue> records);
}
