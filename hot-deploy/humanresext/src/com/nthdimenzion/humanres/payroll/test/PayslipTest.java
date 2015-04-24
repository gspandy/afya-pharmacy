package com.nthdimenzion.humanres.payroll.test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import junit.framework.TestCase;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;

public class PayslipTest extends TestCase {

	protected LocalDispatcher dispatcher = null;
	protected GenericValue userLogin = null;

	public PayslipTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("test");
		dispatcher = GenericDispatcher.getLocalDispatcher("test-dispatcher", delegator);
		userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
		ResolverManager mgr = PayrollContext.getInstance(userLogin).getResolver();
	}

	@Override
	protected void tearDown() throws Exception {

	}

	public static final NumberFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#.0#################");

	public void testPayslip() throws Exception {
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("test");
		EntityCondition ec3 = EntityCondition.makeCondition("itemGroupId", EntityOperator.EQUALS, "DEDUCTIONS");
		EntityCondition ec2 = EntityCondition.makeCondition(UtilMisc.toMap("partyId", "10050", "validTaxDeclId", "10000"));

		EntityCondition whereEntityCondition = EntityCondition.makeCondition(ec2, ec3);
		System.out.println(" WHERE ENTITY CONDITION " + whereEntityCondition);
		List<GenericValue> taxDeclValues = delegator.findList("EmplTaxItemView", whereEntityCondition, null, null, null, false);

		BigDecimal declAmount = BigDecimal.ZERO;
		for (GenericValue taxDecl : taxDeclValues) {
			double a = taxDecl.getDouble("numValue");
			String s = DEFAULT_DECIMAL_FORMAT.format(a);
			BigDecimal bd = new BigDecimal(s);
			BigDecimal maxAmount = taxDecl.getBigDecimal("maxAmount");
			int ret = bd.compareTo(maxAmount);
			System.out.println(bd + " compare value " + maxAmount + " RESULT = " + ret);
			if (ret == -1 || ret == 0) {
				declAmount = declAmount.add(bd);
			}
		}
		System.out.println("TOTAL DEDUCTIONS " + declAmount);
	}
}
