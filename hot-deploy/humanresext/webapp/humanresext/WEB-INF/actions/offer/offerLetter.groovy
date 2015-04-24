import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.humanresext.util.HumanResUtil;

lofferId = parameters.offerId;
lpartyId = null;
/** Get candidate offer details **/
EntityCondition offerCond = EntityCondition.makeCondition("offerId",EntityOperator.EQUALS, lofferId);
offerList = [];
offerList = delegator.findList("MaxOfferDetail", offerCond, null, null, null, false);
if (offerList.size() != 0) {
	offer = offerList.get(0); 
	context.offer = offer;
	lapplicationId = (String)offer.get("applicationId");
	lpartyId = (String)offer.get("partyId");
	lrequisitionId=(String)offer.get("requsitionId");
print("\n\n\n\n\n\n\n\n\n\n\n\n\n Samir Party Id" +   lpartyId  + "\n\n\n\n\n\n\n\n\n\n\n\n\n");
print("\n\n\n\n\n\n\n\n\n\n\n\n\n Samir ApplicationId" +   lapplicationId  + "\n\n\n\n\n\n\n\n\n\n\n\n\n");
} else {
	ServiceUtil.returnError("No offers found for party : " + lpartyId);
}

/** Get Candidate Application Details **/
EntityCondition appCond = EntityCondition.makeCondition("applicationId", EntityOperator.EQUALS, lapplicationId);
appList = [];
appList = delegator.findList("ApplicationDetail", appCond, null, null, null, false);
if (appList.size() != 0) {
	application = appList.get(0); 
	context.positionId = application.get("emplPositionId");
} else {
	ServiceUtil.returnError("No positions applied by party : " + lpartyId);
}

/** Get Candidate Offer CTC Details **/
ctcList = [] as ArrayList;
ctcList = delegator.findList("OfferSalDetail", offerCond, null, null, null, false);
if(ctcList.size())
context.ctc = ctcList;
print("\n\n\n\n\n\n\n\n\n\n\n\n\n CTC List" +   ctcList + "\n\n\n\n\n\n\n\n\n\n\n\n\n")
context.candidateFullName = HumanResUtil.getFullName(delegator, lpartyId," ");
print("\n\n\n\n\n\n\n\n\n\n\n\n\n Candidate's Full Name:" +   context.candidateFullName + "\n\n\n\n\n\n\n\n\n\n\n\n\n")
context.employeeGrade =  HumanResUtil.getGradeOfEmployeeForOfferLetter(delegator, lrequisitionId);
context.employeePositionType=HumanResUtil.getPositionTypeOfEmployeeForOfferLetter(delegator, lrequisitionId);
context.departmentName=HumanResUtil.getDepartmentNameForOfferLetter(delegator, lrequisitionId);
// Employee address
empAddresses = delegator.findByAnd("PartyContactMechPurpose", [partyId : lpartyId, contactMechPurposeTypeId : "GENERAL_LOCATION"]);
empAddress = null;
if (empAddresses) {
    empAddress = delegator.findByPrimaryKey("PostalAddress", [contactMechId : empAddresses[0].contactMechId]);
}
if (empAddress)	{
   // get the country name and state/province abbreviation
   empCountry = empAddress.getRelatedOneCache("CountryGeo");
   if (empCountry) {
      context.empCountryName = empCountry.geoName;
   }
   empStateProvince = empAddress.getRelatedOneCache("StateProvinceGeo");
   if (empStateProvince) {
       context.empStateProvinceAbbr = empStateProvince.abbreviation;
   }
}
context.empPostalAddress = empAddress;
GenericValue userLogin = session.getAttribute("userLogin");
String hrPartyId = (String)userLogin.getString("partyId");
context.hrFullName = HumanResUtil.getFullName(delegator, hrPartyId," ");
