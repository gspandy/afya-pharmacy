import org.ofbiz.base.util.http.*;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

String voucherName = parameters.voucherName;
if(UtilValidate.isEmpty(voucherName.trim())){
	request.setAttribute("_ERROR_MESSAGE_", " Voucher Name cannot be empty.  ");
	return "error";
}
GenericValue voucherType = delegator.makeValue("VoucherType");
voucherType.setNextSeqId();
voucherType.voucherParentId=parameters.voucherParentId;
voucherType.voucherName=parameters.voucherName;
delegator.create(voucherType);

return "success";