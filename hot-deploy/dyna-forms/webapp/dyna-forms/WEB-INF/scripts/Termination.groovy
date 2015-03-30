import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilMisc;
import java.util.Calendar;
import java.sql.Date;
import com.nthdimenzion.humanres.payroll.PayrollHelper;
import java.util.Map;

public void run(Map context){
	

GenericDelegator delegator = GenericDelegator
.getGenericDelegator("default");

	String partyId = context.get("partyId")[0];

	System.out.println("partyId "+partyId);
	List terminations = delegator.findByAnd("EmplTermination",UtilMisc.toMap("partyId",partyId));

	GenericValue person = delegator.findOne("Person",false,UtilMisc.toMap("partyId",partyId));

	GenericValue terminationRec = terminations.get(0);

	GenericValue preference = delegator.findOne("Preferences",false,UtilMisc.toMap("partyId",partyId));

	System.out.println("PREFERENCE "+preference);
	
	context.put("terminationRec",terminationRec);
	context.put("person",person);
	context.put("preference",preference);

}