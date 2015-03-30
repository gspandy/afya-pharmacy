package com.nthdimenzion.humanres.payroll;

import java.util.Date;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;



/**
 * @author sandeep
 * @date Apr-09-09 pankaj
 * FromDate and ThruDate added to identify the period for which the CTC is valid
 */
public class SalaryComponentBean {

	private static String module = "SalaryComponentBean";
	private String salaryHeadId;

	private String salaryHeadName;

	private String currency;

	private String salaryHeadTypeId;

	private double amount;

	private Date fromDate = UtilDateTime.nowDate(); //By Default fromDate is from Today

	private Date thruDate = null; //By Default thruDate is empty
	
	public SalaryComponentBean() {}

	public SalaryComponentBean(String salaryStructureHeadId, Double amount, Date fromDate, Date thruDate,GenericDelegator delegator) throws GenericEntityException {
		EntityCondition condition = EntityCondition.makeCondition("salaryStructureHeadId", EntityOperator.EQUALS, salaryStructureHeadId);
		List<GenericValue> salaryComponents = delegator.findList("SalaryStructureHeadDetail",
				condition, null, null, null, false);
		salaryComponents = EntityUtil.filterByDate(salaryComponents); //Only get the active salary structure head
		if (salaryComponents.size() != 1) {
			Debug.log("There are more/less than 1 active records for this salary structure head", module);
			return;
		}
		GenericValue salaryComponent =salaryComponents.get(0); //delegator.findOne("SalaryStructureHeadDetail", false, "salaryStructureHeadId", salaryStructureHeadId,"fromDate", fromDate);
		parse(salaryComponent);
		setAmount(amount);
		setFromDate(fromDate);
	//	setThruDate(thruDate);
	}

	public SalaryComponentBean(GenericValue emplSalDetail) {
		if (emplSalDetail == null)
			return;
		if (!"EmplSalDetail".equalsIgnoreCase(emplSalDetail.getEntityName()))
			throw new IllegalArgumentException("Cannot create SalaryComponentBean from " + emplSalDetail.getEntityName());
		parse(emplSalDetail);
		setAmount(emplSalDetail.getDouble("amount"));
		setFromDate((Date)emplSalDetail.get("fromDate"));
		//setFromDate((Date)emplSalDetail.get("thruDate"));
	}

	public SalaryComponentBean(String salaryStructureHeadId, String partyId, Date fromDate, Date thruDate,GenericDelegator delegator) throws GenericEntityException {
		EntityCondition partyC = EntityCondition.makeCondition("partyId",
				partyId);
		EntityCondition salaryC = EntityCondition.makeCondition("salaryStructureHeadId",
				salaryStructureHeadId);
		EntityCondition fromC = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
		List<EntityCondition> conditionL = null;
		conditionL = UtilMisc.toList(partyC, salaryC, fromC);
		
		if (thruDate != null) {
			EntityCondition thruC = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate);
			conditionL.add(thruC);
		}		
		
		EntityConditionList conditionList = EntityCondition.makeCondition(conditionL, EntityOperator.AND); 
		List<GenericValue> salaryComponents = delegator.findList("EmplSalDetail", conditionList, null, null, null, false);
		salaryComponents = EntityUtil.filterByDate(salaryComponents);
		//GenericValue salaryComponent = delegator.findOne("EmplSalDetail", false, "salaryStructureHeadId", salaryStructureHeadId, "partyId", partyId,);
		GenericValue sC = salaryComponents.get(0);
		parse(sC);
		setAmount(sC.getDouble("amount"));
		setFromDate(sC.getDate("fromDate"));
		setThruDate(sC.getDate("thruDate"));
	}
	
	public SalaryComponentBean(String salaryStructureHeadId, String partyId,GenericDelegator delegator) throws GenericEntityException {
		EntityCondition partyC = EntityCondition.makeCondition("partyId",
				partyId);
		EntityCondition salaryC = EntityCondition.makeCondition("salaryStructureHeadId",
				salaryStructureHeadId);
		
		List<EntityCondition> conditionL = null;
		conditionL = UtilMisc.toList(partyC, salaryC);
		EntityConditionList conditionList = EntityCondition.makeCondition(conditionL, EntityOperator.AND); 
		List<GenericValue> salaryComponents = delegator.findList("EmplSalDetail", conditionList, null, null, null, false);
		salaryComponents = EntityUtil.filterByDate(salaryComponents);
		//GenericValue salaryComponent = delegator.findOne("EmplSalDetail", false, "salaryStructureHeadId", salaryStructureHeadId, "partyId", partyId,);
		GenericValue sC = salaryComponents.get(0);
		parse(sC); //Only one record for Employee CTC should be current at any time
		setAmount(sC.getDouble("amount"));
		setFromDate(sC.getDate("fromDate"));
		setThruDate(sC.getDate("thruDate"));
	}

	private void parse(GenericValue gv) {
		try {
			salaryHeadId = gv.getString("salaryHeadId");
			salaryHeadName = gv.getString("hrName");
			currency = gv.getString("currencyUomId");
			salaryHeadTypeId = gv.getString("salaryHeadTypeId");
			Object d = gv.get("fromDate");
			if (d != null) {
				fromDate = (Date)d;
			}
			d = gv.get("thruDate");
			if (d != null) {
				thruDate = (Date)d;
			}
		} catch (Exception e) {
				Debug.logInfo("Exception in parsing salarycomponent bean: " + gv + " and the exception is: " + e.toString(), module);
		}
	}

	public String getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public String getSalaryHeadName() {
		return salaryHeadName;
	}

	public void setSalaryHeadName(String salaryHeadName) {
		this.salaryHeadName = salaryHeadName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSalaryHeadTypeId() {
		return salaryHeadTypeId;
	}

	public void setSalaryHeadTypeId(String salaryHeadType) {
		this.salaryHeadTypeId = salaryHeadType;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	
	public Date getThruDate() {
		return thruDate;
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public void setThruDate(Date thruDate) {
		this.thruDate = thruDate;
	}
	
	public String toString() {
		return getSalaryHeadName() + getAmount();
	}
}