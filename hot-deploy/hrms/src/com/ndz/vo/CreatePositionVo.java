package com.ndz.vo;

import java.util.Date;

import org.ofbiz.entity.GenericValue;

public class CreatePositionVo {
	
	private String departmenId;
	
	private String requisitionId;
	
	private String budgetId;
	
	private String budgetItemSequenceId;
	
	private String empPositionTypeId;
	
	private String salaryFlag="Y";
	
	private String exemptFlag="N";
	
	private String fullTimeFlag="Y";
	
	private String temporaryFlag="N";
	
	private Date estimatedFromDate = new Date();
	
	private Date estimatedThruDate;
	
	private Date actualThruDate;
	
	private Date actualFromDate;
	
	private String managerId;
	
	private GenericValue locationGv;
	
	private Integer noOfPosition;
	
	
	public Integer getNoOfPosition() {
	return noOfPosition;
	}

	public void setNoOfPosition(Integer noOfPosition) {
	this.noOfPosition = noOfPosition;
	}

	private RequisitionVo requisitionVo;
	
	
	public RequisitionVo getRequisitionVo() {
	return requisitionVo;
	}

	public void setRequisitionVo(RequisitionVo requisitionVo) {
	this.departmenId = requisitionVo.getInitiatorDepartmentId();
	this.budgetId = requisitionVo.getBudgetId();
	this.budgetItemSequenceId = requisitionVo.getBudgetItemSequenceId();
	this.empPositionTypeId = requisitionVo.getPositionTypeGv().getString("emplPositionTypeId");
	this.noOfPosition = requisitionVo.getNoOfPosition();
	this.requisitionVo = requisitionVo;
	}

	public GenericValue getLocationGv() {
	return locationGv;
	}

	public void setLocationGv(GenericValue locationGv) {
	this.locationGv = locationGv;
	}

	public String getManagerId() {
	return managerId;
	}

	public void setManagerId(String managerId) {
	this.managerId = managerId;
	}

	public String getDepartmenId() {
	return departmenId;
	}

	public void setDepartmenId(String departmenId) {
	this.departmenId = departmenId;
	}

	public String getRequisitionId() {
	return requisitionId;
	}

	public void setRequisitionId(String requisitionId) {
	this.requisitionId = requisitionId;
	}

	public String getBudgetId() {
	return budgetId;
	}

	public void setBudgetId(String budgetId) {
	this.budgetId = budgetId;
	}

	public String getBudgetItemSequenceId() {
	return budgetItemSequenceId;
	}

	public void setBudgetItemSequenceId(String budgetItemSequenceId) {
	this.budgetItemSequenceId = budgetItemSequenceId;
	}

	public String getEmpPositionTypeId() {
	return empPositionTypeId;
	}

	public void setEmpPositionTypeId(String empPositionTypeId) {
	this.empPositionTypeId = empPositionTypeId;
	}

	public String getSalaryFlag() {
	return salaryFlag;
	}

	public void setSalaryFlag(String salaryFlag) {
	this.salaryFlag = salaryFlag;
	}

	public String getExemptFlag() {
	return exemptFlag;
	}

	public void setExemptFlag(String exemptFlag) {
	this.exemptFlag = exemptFlag;
	}

	public String getFullTimeFlag() {
	return fullTimeFlag;
	}

	public void setFullTimeFlag(String fullTimeFlag) {
	this.fullTimeFlag = fullTimeFlag;
	}

	public String getTemporaryFlag() {
	return temporaryFlag;
	}

	public void setTemporaryFlag(String temporaryFlag) {
	this.temporaryFlag = temporaryFlag;
	}

	public Date getEstimatedFromDate() {
	return estimatedFromDate;
	}

	public void setEstimatedFromDate(Date estimatedFromDate) {
	this.estimatedFromDate = estimatedFromDate;
	}

	public Date getEstimatedThruDate() {
	return estimatedThruDate;
	}

	public void setEstimatedThruDate(Date estimatedThruDate) {
	this.estimatedThruDate = estimatedThruDate;
	}

	public Date getActualThruDate() {
	return actualThruDate;
	}

	public void setActualThruDate(Date actualThruDate) {
	this.actualThruDate = actualThruDate;
	}

	public Date getActualFromDate() {
	return actualFromDate;
	}

	public void setActualFromDate(Date actualFromDate) {
	this.actualFromDate = actualFromDate;
	}
	
	

}
