package com.nthdimenzion.humanres.payroll;

public class ExemptionComponentBean {

	public String getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	private String salaryHeadId;
	private Double amount;
	private String salaryHeadName;

	public String getSalaryHeadName() {
		return salaryHeadName;
	}

	public void setSalaryHeadName(String salaryHeadName) {
		this.salaryHeadName = salaryHeadName;
	}
}
