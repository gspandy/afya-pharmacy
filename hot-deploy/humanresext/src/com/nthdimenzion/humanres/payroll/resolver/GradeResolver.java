package com.nthdimenzion.humanres.payroll.resolver;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanresext.util.HumanResUtil;

import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.expr.Field;

public class GradeResolver extends AbstractResolver {

	private static String COMP_NAME = "GRADE";

	public String getComponentName() {
		return COMP_NAME;
	}

	public Field<Double> resolve(String key) {
		PayrollContext workingMemory = PayrollContext.getInstance();
		GenericValue userLogin = (GenericValue) workingMemory.get("userLogin");
		GenericDelegator delegator = (GenericDelegator) workingMemory.get("delegator");
		String partyId = userLogin.getString("partyId");
		
		try{
		GenericValue party = delegator.findOne("Party",true,UtilMisc.toMap("partyId",partyId));
		
		GenericValue position = HumanResUtil.getEmplPositionForParty(party, delegator);
		
		String emplPositionTypeId = position.getString("emplPositionTypeId");
		
		System.out.println("Employee Position Type"+emplPositionTypeId);
		
		//TODO Need to filter base on from date and thru date
		List<GenericValue> positionRateTypes = delegator.findByAnd("EmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeId",emplPositionTypeId));
		
		if(positionRateTypes.size()>0){
			GenericValue positionRateType = positionRateTypes.get(0);
			String payGradeId = positionRateType.getString("payGradeId");
			String salaryStepSeqId = positionRateType.getString("salaryStepSeqId");
			GenericValue salaryStep = delegator.findOne("SalaryStep", false, UtilMisc.toMap("payGradeId",payGradeId,"salaryStepSeqId",salaryStepSeqId));
			return new Field<Double>(salaryStep.getDouble("min"));
		}
		
		}catch(GenericEntityException ge){
			
		}
		
		
		return null;
	}

	@Override
	public Object resolve(String key, GenericDelegator delegator) {
		// TODO Auto-generated method stub
		return null;
	}

}
