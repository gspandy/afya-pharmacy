import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;

     Timestamp  fromDate = UtilDateTime.getDate(parameters.fromDate);
	 Map param = UtilMisc.toMap("orderId", parameters.orderId,"contentId",parameters.contentId,
		"orderContentTypeId",parameters.orderContentTypeId,"fromDate",fromDate);
	GenericValue gv = delegator.findOne("OrderHeaderContent",true,param);
	delegator.removeValue(gv);
	
return "success"
