import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.base.util.UtilMisc;

public void run(Map context){


GenericDelegator delegator = GenericDelegator
	.getGenericDelegator("default");
	String[] args = (String[])context.get("arguments");
	String partyId = context.get("partyId");

	System.out.println(context.get("Request"));
	
	System.out.println(UtilMisc.toMap("partyId",partyId,"attributeId",args[0],"certificateId",args[1]));
	
	GenericValue certDetail = delegator.findOne("EmplCertificate",UtilMisc.toMap("partyId",partyId,"attributeId",args[1],"certificateId",args[0]),false);
	System.out.println(certDetail);

	if(certDetail!=null)
		context.put("result",certDetail.getString("attributeValue"));
	else
		context.put("result","No Data");
}