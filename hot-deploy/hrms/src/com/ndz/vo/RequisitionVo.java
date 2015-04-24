package com.ndz.vo;

import java.util.Date;

import org.ofbiz.entity.GenericValue;

import com.ndz.zkoss.HrmsUtil;

public class RequisitionVo {
	
	private String replacementPositionId;
	
	private GenericValue positionTypeGv;
	
	private String requirementType;
	
	private Integer noOfPosition;
	
	private Integer minimumExprience =0;
	
	private Integer maximumExprience =0;
	
	private Date startDate;
	
	private Date endDate;
	
	private GenericValue currencyTypeGv;
	
	private GenericValue currencyBaseLineGv;
	
	private Integer minimumSalary;
	
	private Integer maximumSalary;
	
	private String jobTitle;
	
	private String jobDescription;
	
	private String roleDetails;
	
	private String qualifications;
	
	private String softSkills;
	
	private String justificationForHiring;
	
	private String approverPositionId;
	
	private String initiatorDepartmentId;
	
	private String statusId;
	
	private GenericValue requisitionGv;
	
	private String requisitionRaisedBy;
	
	private String requisitionType="New";
	
	private String budgetId;
	
	private String budgetItemSequenceId;
	
	private String adminComment;
	
	private String hodComment;
	
	private GenericValue maxCurrencyLineGv;
	
	private GenericValue locationGv;
	
	private String managerId;
	
	private String grade;
	
	private String positionCategory;
	
	private String employeeType;
	
	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public String getPositionCategory() {
		return positionCategory;
	}

	public void setPositionCategory(String positionCategory) {
		this.positionCategory = positionCategory;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getManagerId() {
	return managerId;
	}

	public void setManagerId(String managerId) {
	this.managerId = managerId;
	}

	public GenericValue getLocationGv() {
	return locationGv;
	}

	public void setLocationGv(GenericValue locationGv) {
	this.locationGv = locationGv;
	}

	public GenericValue getMaxCurrencyLineGv() {
	return maxCurrencyLineGv;
	}

	public void setMaxCurrencyLineGv(GenericValue maxCurrencyLineGv) {
	this.maxCurrencyLineGv = maxCurrencyLineGv;
	}

	public String getAdminComment() {
	return adminComment;
	}

	public void setAdminComment(String adminComment) {
	this.adminComment = adminComment;
	}

	public String getHodComment() {
	return hodComment;
	}

	public void setHodComment(String hodComment) {
	this.hodComment = hodComment;
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

	public String getRequisitionType() {
	return requisitionType;
	}

	public void setRequisitionType(String requisitionType) {
	this.requisitionType = requisitionType;
	}

	public String getRequisitionRaisedBy() {
	return requisitionRaisedBy;
	}

	public void setRequisitionRaisedBy(String requisitionRaisedBy) {
	this.requisitionRaisedBy = requisitionRaisedBy;
	}

	public GenericValue getRequisitionGv() {
	return requisitionGv;
	}

	public void setRequisitionGv(GenericValue requisitionGv) {
	this.approverPositionId = requisitionGv.getString("approverPositionId");
	this.requisitionRaisedBy = requisitionGv.getString("partyId");
	this.statusId = requisitionGv.getString("statusId");
	this.noOfPosition = requisitionGv.getLong("numberOfPosition").intValue();
	this.qualifications = requisitionGv.getString("qualification");
	this.minimumExprience = requisitionGv.getLong("minExprience").intValue();
	this.maximumExprience = requisitionGv.getLong("maxExprience").intValue();
	this.jobTitle = requisitionGv.getString("jobTitle");
	this.jobDescription = requisitionGv.getString("jobDescription");
	this.roleDetails = requisitionGv.getString("roleDetails");
	this.softSkills = requisitionGv.getString("softSkills");
	this.justificationForHiring = requisitionGv.getString("justificationForHiring");
	this.minimumSalary = requisitionGv.getBigDecimal("minimumSalary").intValue();
	this.maximumSalary = requisitionGv.getBigDecimal("maximumSalary").intValue();
	this.initiatorDepartmentId = requisitionGv.getString("reqRaisedByDept");
	this.startDate = new Date(requisitionGv.getTimestamp("fromDate").getTime());
	this.endDate = new Date(requisitionGv.getTimestamp("thruDate").getTime());
	this.hodComment = requisitionGv.getString("hodComment");
	this.adminComment = requisitionGv.getString("adminComment");
	this.requisitionType = requisitionGv.getString("requisitionType");
	this.replacementPositionId = requisitionGv.getString("replacementpositionId");
	this.positionTypeGv = HrmsUtil.getEmployeePositionTypeGv(requisitionGv.getString("positionType"));
	this.currencyTypeGv = HrmsUtil.getCurrencyTypeGv(requisitionGv.getString("uomId"));
	this.currencyBaseLineGv = HrmsUtil.getCurrencyBaseLineGv(requisitionGv.getString("enumId"));
	this.maxCurrencyLineGv = this.currencyBaseLineGv;
	this.requirementType = requisitionGv.getString("requirementType");
	this.locationGv = HrmsUtil.getLocationGv(requisitionGv.getString("locationId"));
	this.requisitionGv = requisitionGv;
	this.budgetId = requisitionGv.getString("budgetId");
	this.budgetItemSequenceId = requisitionGv.getString("budgetItemSeqId");
	this.managerId = requisitionGv.getString("managerId");
	this.grade=requisitionGv.getString("grade");
	this.positionCategory=requisitionGv.getString("positionCategory");
	this.employeeType=requisitionGv.getString("employeeType");
	}

	public String getStatusId() {
	return statusId;
	}

	public void setStatusId(String statusId) {
	this.statusId = statusId;
	}

	public String getInitiatorDepartmentId() {
	return initiatorDepartmentId;
	}

	public void setInitiatorDepartmentId(String initiatorDepartmentId) {
	this.initiatorDepartmentId = initiatorDepartmentId;
	}

	public String getReplacementPositionId() {
	return replacementPositionId;
	}

	public void setReplacementPositionId(String replacementPositionId) {
	this.replacementPositionId = replacementPositionId;
	}

	public GenericValue getPositionTypeGv() {
	return positionTypeGv;
	}

	public void setPositionTypeGv(GenericValue positionTypeGv) {
	this.positionTypeGv = positionTypeGv;
	}

	public String getRequirementType() {
	return requirementType;
	}

	public void setRequirementType(String requirementType) {
	this.requirementType = requirementType;
	}

	public Integer getNoOfPosition() {
	return noOfPosition;
	}

	public void setNoOfPosition(Integer noOfPosition) {
	this.noOfPosition = noOfPosition;
	}

	public Integer getMinimumExprience() {
	return minimumExprience;
	}

	public void setMinimumExprience(Integer minimumExprience) {
	this.minimumExprience = minimumExprience;
	}

	public Integer getMaximumExprience() {
	return maximumExprience;
	}

	public void setMaximumExprience(Integer maximumExprience) {
	this.maximumExprience = maximumExprience;
	}

	public Date getStartDate() {
	return startDate;
	}

	public void setStartDate(Date startDate) {
	this.startDate = startDate;
	}

	public Date getEndDate() {
	return endDate;
	}

	public void setEndDate(Date endDate) {
	this.endDate = endDate;
	}

	public GenericValue getCurrencyTypeGv() {
	return currencyTypeGv;
	}

	public void setCurrencyTypeGv(GenericValue currencyTypeGv) {
	this.currencyTypeGv = currencyTypeGv;
	}

	public GenericValue getCurrencyBaseLineGv() {
	return currencyBaseLineGv;
	}

	public void setCurrencyBaseLineGv(GenericValue currencyBaseLineGv) {
	this.currencyBaseLineGv = currencyBaseLineGv;
	}

	public Integer getMinimumSalary() {
	return minimumSalary;
	}

	public void setMinimumSalary(Integer minimumSalary) {
	this.minimumSalary = minimumSalary;
	}

	public Integer getMaximumSalary() {
	return maximumSalary;
	}

	public void setMaximumSalary(Integer maximumSalary) {
	this.maximumSalary = maximumSalary;
	}

	public String getJobTitle() {
	return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
	this.jobTitle = jobTitle;
	}

	public String getJobDescription() {
	return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
	this.jobDescription = jobDescription;
	}

	public String getRoleDetails() {
	return roleDetails;
	}

	public void setRoleDetails(String roleDetails) {
	this.roleDetails = roleDetails;
	}

	public String getQualifications() {
	return qualifications;
	}

	public void setQualifications(String qualifications) {
	this.qualifications = qualifications;
	}

	public String getSoftSkills() {
	return softSkills;
	}

	public void setSoftSkills(String softSkills) {
	this.softSkills = softSkills;
	}

	public String getJustificationForHiring() {
	return justificationForHiring;
	}

	public void setJustificationForHiring(String justificationForHiring) {
	this.justificationForHiring = justificationForHiring;
	}

	public String getApproverPositionId() {
	return approverPositionId;
	}

	public void setApproverPositionId(String approverPositionId) {
	this.approverPositionId = approverPositionId;
	}
	

}
