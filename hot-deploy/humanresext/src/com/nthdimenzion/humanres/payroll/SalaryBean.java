package com.nthdimenzion.humanres.payroll;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.nthdimenzion.humanres.payroll.calc.ExemptionCalc;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.calc.TaxCalulator;
import com.nthdimenzion.humanres.payroll.calc.TaxDeclarationCalculator;

/**
 * @author sandeep
 * @date Apr-09-09 Pankaj FromDate and ThruDate added to identify the period for
 *       which the CTC is valid
 */

public class SalaryBean {

	private final Map<String, SalaryComponentGroup> headTypeComponents = FastMap.newInstance();

	private final List<ExemptionComponentBean> exemptionComponents = FastList.newInstance();

	private static final String C_Benefits = "Benefits";
	private static final String C_Allowances = "Allowances";
	private static final String C_Perqs = "Perquisites";
	private static final String C_Provident = "Provident";

	private Double grossAmount = 0.0;

	public Double exemptionAmount = 0.0;

	private final Double netAmount = 0.0;

	private Double taxDeductionAmount = 0.0; // It is Tax Declaration Amount
	// i.e. about investments

	public Double taxableAmount = 0.0;

	public Double taxAmount = 0.0;

	private Double professionalTax = 2400D; // Rs 200 p.m.

	private Double perqAmount = 0.0;// Value of Perquisites given to employee

	private final String partyId;

	private Double simpleTax = 0D;

	private Double surcharge = 0D;

	private Double educationCess = 0D;

	private Double higherEduCess = 0D;

	GenericValue fiscalYear = null;

	Date yearBegin = null; // Start of Financial Year

	Date yearEnd = null; // End Date of Financial Year

	private Date fromDate = null;

	private Date thruDate = null;

	private BigDecimal extraIncome = new BigDecimal(0);

	public SalaryBean(String partyId, Date fromDate, Date thruDate,GenericDelegator delegator) {
		this.partyId = partyId;
		this.fromDate = fromDate;
		try {
			if (fromDate != null) {
				fiscalYear = PayrollHelper.getFiscalYear(fromDate,delegator);
			} else {
				fiscalYear = PayrollHelper.getCurrentFiscalYear(delegator);
			}
			yearBegin = fiscalYear!=null? fiscalYear.getDate("fromDate"):null;
			yearEnd = fiscalYear!=null?fiscalYear.getDate("thruDate"):null;
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		this.thruDate = thruDate;
		if (thruDate == null) {
			this.thruDate = yearEnd;
		}
	}

	public void addSalaryComponent(SalaryComponentBean salaryComponent,GenericDelegator delegator) throws GenericEntityException {
		String headType = salaryComponent.getSalaryHeadTypeId();
		SalaryComponentGroup group = headTypeComponents.get(headType);
		if (group == null) {
			group = new SalaryComponentGroup();
			headTypeComponents.put(headType, group);
		}
		group.addSalaryComponent(salaryComponent);

		/** Deductions are handled differently for tax **/

		/**
		 * Perquisites are handled differently for tax So only Benefits and
		 * Allowances should be added
		 * **/
		if (salaryComponent.getSalaryHeadTypeId().equalsIgnoreCase(C_Benefits)
				|| salaryComponent.getSalaryHeadTypeId().equalsIgnoreCase(C_Allowances)) {
			grossAmount += salaryComponent.getAmount();
		} else if (salaryComponent.getSalaryHeadTypeId().equalsIgnoreCase(C_Perqs)) {
			perqAmount += salaryComponent.getAmount(); // Its a Perq so increase
			// only the perqAmount
		} else if (salaryComponent.getSalaryHeadName().indexOf(C_Provident) >= 0) { // But
			// EPF is to be added to gross even though it is a deduction
			grossAmount += salaryComponent.getAmount();
		} else { // Deductions are totaled as exemptionAmount
			// taxDeductionAmount +=salaryComponent.getAmount();
		}
		
		System.out.println("@@@@@@SalaryBean salaryComponent.getSalaryHeadTypeId() :" + salaryComponent.getSalaryHeadTypeId());
		
	}

	public Map<String, SalaryComponentGroup> getGroupWiseSalaryComponents() {
		return headTypeComponents;
	}

	public List<SalaryComponentBean> getAllComponents() {
		List<SalaryComponentBean> salaryComponentList = FastList.newInstance();
		for (Map.Entry<String, SalaryComponentGroup> entry : headTypeComponents.entrySet())
			salaryComponentList.addAll(entry.getValue().getSalaryComponents());
		return salaryComponentList;
	}

	/** List of all perquisites given to employee **/
	public List<SalaryComponentBean> getPerqComponents() {
		List<SalaryComponentBean> perqComponentList = FastList.newInstance();
		for (Map.Entry<String, SalaryComponentGroup> entry : headTypeComponents.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("Perquisites")) {
				System.out.println("@@@@@@@@@@entry.getValue().getSalaryComponents() :" + entry.getValue().getSalaryComponents());
				perqComponentList.addAll(entry.getValue().getSalaryComponents());
			}
		}
		return perqComponentList;
	}

	/** List of all deductions available to employee **/
	public List<SalaryComponentBean> getDeductComponents() {
		List<SalaryComponentBean> deductComponentList = FastList.newInstance();
		for (Map.Entry<String, SalaryComponentGroup> entry : headTypeComponents.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("Deductions")) {
				System.out.println("@@@@@@@DEDUCTIONS : entry.getValue().getSalaryComponents() :" + entry.getValue().getSalaryComponents());
				deductComponentList.addAll(entry.getValue().getSalaryComponents());
			}
		}
		return deductComponentList;
	}

	public List<SalaryComponentBean> getAllButPerqComponents() {
		List<SalaryComponentBean> allComponentList = getAllComponents();
		List<SalaryComponentBean> perqComponentList = getPerqComponents();
		allComponentList.removeAll(perqComponentList);
		return allComponentList;
	}

	// Populate Exemptions and Tax
	public void populateDerivedValues(GenericDelegator delegator) throws GenericEntityException {
		GenericValue emplValue = null;
		try {
			emplValue = delegator.findOne("Party", false, "partyId", this.partyId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PayrollContext payrollCtx = PayrollContext.getInstance(emplValue);
		populateExemptionComponent(payrollCtx);

		// taxDeducitonAmount = Sum of all accepted Investments
		taxDeductionAmount = TaxDeclarationCalculator.calculate(emplValue).doubleValue();
		
		taxableAmount = Math.max(getNetAmount() - taxDeductionAmount,0);
		
		BigDecimal monthlyProfessionalTax = TaxCalulator.calculateMonthlyProfessionalTax(new BigDecimal(getGrossAmount()/12),delegator);
		taxableAmount=taxableAmount.doubleValue()-monthlyProfessionalTax.multiply(new BigDecimal(12)).doubleValue();
		System.out.println(" Taxable Amount "+taxableAmount);
		payrollCtx.putAll(UtilTax.getTaxContext(partyId, taxableAmount,delegator));
		Map<String, Object> context = FastMap.newInstance();
        thruDate= thruDate==null ? new Date():thruDate;
		context.put("fromDate", new java.sql.Date(fromDate.getTime()));
		context.put("thruDate", new java.sql.Date(thruDate.getTime()));
		Map<String, Double> taxMap = TaxCalulator.calculateTax(context,delegator);
		professionalTax = monthlyProfessionalTax.doubleValue();
		simpleTax = (taxMap.get("simpleTax") == null ? 0D : taxMap.get("simpleTax"));
		surcharge = (taxMap.get("surcharge") == null ? 0D : taxMap.get("surcharge"));
		educationCess = (taxMap.get("educationCess")  == null ? 0D : taxMap.get("educationCess"));
		higherEduCess = (taxMap.get("higherEduCess")  == null ? 0D : taxMap.get("higherEduCess"));
		taxAmount = (taxMap.get("taxAmount")  == null ? 0D : taxMap.get("taxAmount"));

	}

	private void populateExemptionComponent(PayrollContext context) {
		ResolverManager resolver = PayrollContext.getResolver();
		Map<String, Double> salaryHeadMap = FastMap.newInstance();
		for (SalaryComponentBean bean : getAllComponents()) {
			salaryHeadMap.put(bean.getSalaryHeadId(), bean.getAmount());
		}
		resolver.getResolver("SALARYHEAD").setLookupContext(salaryHeadMap);
		for (SalaryComponentBean component : getAllComponents()) {
			Number number = null;
			try {
				number = ExemptionCalc.calculateExemption((GenericValue) context.get(PayrollContext.USER_LOGIN), component.getSalaryHeadId());
				Double salaryHeadAmount = salaryHeadMap.get(component.getSalaryHeadId());
				if (salaryHeadAmount != null) {
					number = Math.min(number.doubleValue(), salaryHeadAmount.doubleValue());
				}
			} catch (GenericEntityException e) {

			}
			if (number != null) {
				ExemptionComponentBean bean = new ExemptionComponentBean();
				bean.setAmount(number.doubleValue());
				bean.setSalaryHeadId(component.getSalaryHeadId());
				bean.setSalaryHeadName(component.getSalaryHeadName());
				exemptionAmount = exemptionAmount + number.doubleValue();
				exemptionComponents.add(bean);
			}
		}
	}

	public List<ExemptionComponentBean> getExemptionComponents() {
		return exemptionComponents;
	}

	public Double getGrossAmount() {
		return grossAmount;
	}
    
	public void setGrossAmount(Double grossAmount) {
		this.grossAmount = grossAmount;
	}

	public String getGrossAmountAsString() {
		NumberFormat formatter = DecimalFormat.getInstance();
		// formatter.setRoundingMode(RoundingMode.HALF_UP);
		formatter.setGroupingUsed(true);
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		return formatter.format(grossAmount);
	}

	public Double getMonthlyGrossAmount() {
		return grossAmount;
	}

	public Double getExemptionAmount() {
		return exemptionAmount;
	}

	public Double getNetAmount() {
		// return grossAmount - exemptionAmount; Changed as value of Perqs has
		// been added
		return (grossAmount + perqAmount - exemptionAmount + extraIncome.doubleValue());
	}

	public Double getTaxDeductionAmount() {
		return taxDeductionAmount;
	}

	public Double getTaxableAmount() {
		return taxableAmount;
	}

	public String getPartyId() {
		return partyId;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public Double getSimpleTax() {
		return simpleTax;
	}

	public Double getSurcharge() {
		return surcharge;
	}

	public Double getEducationCess() {
		return educationCess;
	}

	public Double getHigherEduCess() {
		return higherEduCess;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getThruDate() {
		return thruDate;
	}

	public Date getYearBegin() {
		return yearBegin;
	}

	public Date getYearEnd() {
		return yearEnd;
	}

	public Double getProfessionalTax() {
		return professionalTax;
	}

	public void setExtraIncome(BigDecimal extraIncome) {
		this.extraIncome = extraIncome;
	}

}