package com.nthdimenzion.humanres.payroll.test;

import junit.framework.TestCase;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.expr.Expression;

public class RuleTest extends TestCase {

	protected GenericValue userLogin = null;
	GenericDelegator delegator = null;
	Expression exp1;
	Expression exp2;
	Expression exp3;

	public RuleTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {}

	public void testRule() throws Exception {
		GenericValue userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
		ResolverManager resolver = PayrollContext.getInstance(userLogin, true).getResolver();
		resolver.resolve("#RULE.(10012)#");
		/*try {
			Number result = ExemptionCalc.calculateExemption(delegator, "10049");
			Debug.logInfo(" *********** RESULT ****************" + result, "RuleTest");
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
