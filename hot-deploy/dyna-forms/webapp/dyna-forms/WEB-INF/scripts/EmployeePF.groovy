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

String partyId = context.get("partyId");

}