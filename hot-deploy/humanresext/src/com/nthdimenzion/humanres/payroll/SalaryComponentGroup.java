package com.nthdimenzion.humanres.payroll;

import java.util.List;
import javolution.util.FastList;


/**
 * @author sandeep
 *
 */
public class SalaryComponentGroup {
	
	private List<SalaryComponentBean> salaryComponents = FastList.newInstance();
	
	private Double total = 0.0;
	
	public List<SalaryComponentBean> getSalaryComponents() {
		return salaryComponents;
	}
	
	public void addSalaryComponent(SalaryComponentBean salaryComponent) {
		salaryComponents.add(salaryComponent);
		total += salaryComponent.getAmount();
	}
	
	public Double getTotal() {
		return total;
	}
}