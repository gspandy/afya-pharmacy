package com.hrms.payroll.composer;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;


import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.util.CurrencyFormatter;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.ndz.zkoss.util.MessageUtils;
import com.nthdimenzion.humanres.payroll.SalaryBean;
import com.nthdimenzion.humanres.payroll.events.PayrollEvents;

public class ViewSalaryStructureComposer extends HrmsComposer {

	private static final long serialVersionUID = 1L;
	private String employeeId;
	public String exemptionAmount;
	public String taxAmount;
	public String taxableAmount;
	
	private BigDecimal prevGrossAmount;
	
	private BigDecimal prevTaxPaid;


	private SalaryBean salaryStructure;
	Panel salaryStructurePanel;
	Label taxAmountLabel;
	Label exemptionAmountLabel;
	Label taxableAmountLabel;
	Label taxLabel;
	Label netAmountLabel;
	public void onSelect$employeeLookup(Event event) throws Exception {
		salaryStructurePanel.setVisible(false);
		if (employeeId == null) {
			MessageUtils.showInfoMessage("Not a valid Employee Id");
			return;
		}
		Caption caption = (Caption) salaryStructurePanel.getFirstChild();
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), true);
		caption.setLabel("Salary Structure of " + person.getString("firstName") +" "+ person.getString("lastName"));

		salaryStructure = PayrollEvents.populateSalaryStructureForParty(employeeId, delegator);
		System.out.println("\n\n\n Employee Exemption :"+salaryStructure.getExemptionAmount());
		if (CollectionUtils.isEmpty(salaryStructure.getAllComponents())) {
			Component footer = salaryStructurePanel.getFellowIfAny("salStructureFoot", true);
			if (footer != null)
				footer.setVisible(false);
			MessageUtils.showInfoMessage("No Salary Strucuture found for Employee Id " + employeeId);
			return;
		}
		salaryStructurePanel.setVisible(true);
		exemptionAmount = CurrencyFormatter.format(salaryStructure.exemptionAmount);
		taxAmount = CurrencyFormatter.format(salaryStructure.getTaxDeductionAmount());
		taxableAmount = CurrencyFormatter.format(salaryStructure.taxableAmount);
		taxableAmountLabel.setValue(taxableAmount);
		taxAmountLabel.setValue(taxAmount);
		exemptionAmountLabel.setValue(exemptionAmount);
		double netGrossSalary=salaryStructure.taxableAmount-salaryStructure.taxAmount;
		String salaryGross=CurrencyFormatter.format(netGrossSalary);
		netAmountLabel.setValue(salaryGross);
		taxLabel.setValue(CurrencyFormatter.format(salaryStructure.taxableAmount - netGrossSalary));
		
		
	}

	public SalaryBean getSalaryStructure() {
		return salaryStructure;
	}

	public void setSalaryStructure(SalaryBean salaryStructure) {
		this.salaryStructure = salaryStructure;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {

		salaryStructure = PayrollEvents.populateSalaryStructureForParty(userLogin.getString("partyId"), delegator);

		if (CollectionUtils.isEmpty(salaryStructure.getAllComponents())) {
			Component footer = salaryStructurePanel.getFellowIfAny("salStructureFoot", true);
			if (footer != null)
				footer.setVisible(false);

			MessageUtils.showInfoMessage("No Salary Strucuture found for you.");
		}
		Caption caption = (Caption) salaryStructurePanel.getFirstChild();
		caption.setLabel("My Salary Structure");
		salaryStructurePanel.setVisible(true);
		Double d=salaryStructure.exemptionAmount;
		exemptionAmount = CurrencyFormatter.format(salaryStructure.exemptionAmount);
		taxAmount = CurrencyFormatter.format(salaryStructure.getTaxDeductionAmount());
		taxableAmount = CurrencyFormatter.format(salaryStructure.taxableAmount);
		taxableAmountLabel.setValue(taxableAmount);
		taxAmountLabel.setValue(taxAmount);
		exemptionAmountLabel.setValue(exemptionAmount);
		double netGrossSalary=salaryStructure.taxableAmount-(salaryStructure.taxAmount==null ? 0 : salaryStructure.taxAmount);
		String salaryGross=CurrencyFormatter.format(netGrossSalary);
		netAmountLabel.setValue(salaryGross);
		taxLabel.setValue(CurrencyFormatter.format(salaryStructure.taxableAmount - netGrossSalary));
	}

	public static String getFormatingAmount(String amount){
		Double d = new Double(amount);
		return CurrencyFormatter.format(d);
		
	}
	
	public BigDecimal getPrevGrossAmount() {
		return prevGrossAmount;
	}

	public void setPrevGrossAmount(BigDecimal prevGrossAmount) {
		this.prevGrossAmount = prevGrossAmount;
	}

	public BigDecimal getPrevTaxPaid() {
		return prevTaxPaid;
	}

	public void setPrevTaxPaid(BigDecimal prevTaxPaid) {
		this.prevTaxPaid = prevTaxPaid;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
	public void onClick$Save(Event event){
		
		prevGrossAmount = ((Decimalbox)event.getTarget().getFellow("prevgross")).getValue();
		prevTaxPaid = ((Decimalbox)event.getTarget().getFellow("prevtax")).getValue();
		String partyId = ((Textbox)event.getTarget().getFellow("partyId")).getValue();
		
		GenericDelegator delegator  = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		
			GenericValue prevSalInfoGv = delegator.makeValue("EmplPrevSalInfo", UtilMisc.toMap("partyId", partyId,"prevGrossAmount",prevGrossAmount,"prevTaxPaid",prevTaxPaid));
		
		try {
			delegator.createOrStore(prevSalInfoGv);
			try {
				MessageUtils.showInfoMessage("Previous Salary Information Saved Successfully");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
	}
}
