package com.hrms.payroll.composer;

import org.zkoss.zk.ui.Component;

import com.hrms.composer.HrmsComposer;

public class RuleComposer extends HrmsComposer{

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
			System.out.println("*********** "+(comp));
	}
	
}
