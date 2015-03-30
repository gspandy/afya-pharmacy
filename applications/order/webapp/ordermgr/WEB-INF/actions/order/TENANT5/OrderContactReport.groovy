import java.math.BigDecimal;
import java.util.*;
import java.security.Policy.Parameters;
import java.sql.Timestamp;

import javax.naming.Context;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import javolution.util.FastList;

partyGroup =null;
partyId="Company";
supplier= orderReadHelper.getSupplierAgent()

partyGroup = delegator.findByPrimaryKey("PartyGroup", [partyId : partyId]);
supplierPartyGroup = delegator.findByPrimaryKey("PartyGroup", [partyId : supplier.partyId]);
context.partyGroup = partyGroup;

context.supplierPartyGroup=supplierPartyGroup;

partyMenufactureUnit =null;

partyMenufactureUnit=delegator.findByPrimaryKey("PartyMenufacturerUnit", [partyId : partyId]);

context.partyMenufactureUnit=partyMenufactureUnit;

List partyPhone = FastList.newInstance();

partyPhone.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));

partyPhone.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"PHONE_WORK"));

partyPhone.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null));

List partyPhoneList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(partyPhone, EntityOperator.AND), null, null, null, false);

///print "\n\n\n partyAddressList"+partyPhoneList+"\n\n\n"
if(UtilValidate.isNotEmpty(partyPhoneList))
	partycontactMechId=partyPhoneList.get(0).contactMechId;
//print "\n\n\n partyPhone"+partycontactMechId+"\n\n\n"
partyPhoneNo=delegator.findByPrimaryKey("TelecomNumber", [contactMechId : partycontactMechId]);
// print "\n\n\n partyPhone"+partyPhoneNo+"\n\n\n"

context.partyPhoneNo=partyPhoneNo;
List partyEmailAddress = FastList.newInstance();

partyEmailAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));

partyEmailAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"PRIMARY_EMAIL"));

partyEmailAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null));
List partyEmailAddressList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(partyEmailAddress, EntityOperator.AND), null, null, null, false);

// print "\n\n\n partyAddressList"+partyEmailAddressList+"\n\n\n"
if(UtilValidate.isNotEmpty(partyEmailAddressList))
	partycontactMechId=partyEmailAddressList.get(0).contactMechId;
//  print "\n\n\n partyPhone"+partycontactMechId+"\n\n\n"
partyMail=delegator.findByPrimaryKey("ContactMech", [contactMechId : partycontactMechId]);
//  print "\n\n\n partyMail"+partyMail+"\n\n\n"
context.partyMail=partyMail;

context.partyPhoneNo=partyPhoneNo;

List partyFaxAddress = FastList.newInstance();

partyFaxAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));

partyFaxAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"FAX_NUMBER"));

partyFaxAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null));

List partyFaxAddressList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(partyFaxAddress, EntityOperator.AND), null, null, null, false);



partyFax = null;
if(UtilValidate.isNotEmpty(partyFaxAddressList)){
	partycontactMechId=partyFaxAddressList.get(0).contactMechId;
partyFax=delegator.findByPrimaryKey("TelecomNumber", [contactMechId : partycontactMechId]);
}

context.partyFax=partyFax;

List partyPhoneSec = FastList.newInstance();

partyPhoneSec.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));

partyPhoneSec.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"PHONE_WORK"));

partyPhoneSec.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null));

List partyPhoneSecList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(partyPhoneSec, EntityOperator.AND), null, null, null, false);


if(UtilValidate.isNotEmpty(partyPhoneSecList))
	partycontactMechId=partyPhoneSecList.get(0).contactMechId;
//  print "\n\n\n partyPhone"+partycontactMechId+"\n\n\n"
partyPhoneSecNo=delegator.findByPrimaryKey("TelecomNumber", [contactMechId : partycontactMechId]);
// print "\n\n\n partyPhone"+partyPhoneSecNo+"\n\n\n"

context.partyPhoneSecNo=partyPhoneSecNo;
// contact info General Address
List partyAddress = FastList.newInstance();

partyAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));

partyAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"GENERAL_LOCATION"));

partyAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null));

List partyAddressList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(partyAddress, EntityOperator.AND), null, null, null, false);


if(UtilValidate.isNotEmpty(partyAddressList))
	partycontactMechId=partyAddressList.get(0).contactMechId;

partyAddressGeneral=delegator.findByPrimaryKey("PostalAddress", [contactMechId : partycontactMechId]);
//print "\n\n\n partyAddressGeneral"+partyAddressGeneral.getString("address1")+"\n\n\n"

context.partyAddressGeneral=partyAddressGeneral;


if(UtilValidate.isNotEmpty(supplier)){

	List supplierAddress = FastList.newInstance();

	supplierAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplier.partyId));

	supplierAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"SHIPPING_LOCATION"));

	supplierAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null));

	List supplierAddressList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(supplierAddress, EntityOperator.AND), null, null, null, false);
	supplierAddressGeneral=null;
	if(UtilValidate.isNotEmpty(supplierAddressList)){
		partycontactMechId=supplierAddressList.get(0).contactMechId;

	supplierAddressGeneral=delegator.findByPrimaryKey("PostalAddress", [contactMechId : partycontactMechId]);
	}
	context. supplierAddressGeneral= supplierAddressGeneral;
}


//despatch
orderItemShipGroup =null;
orderidshipping= parameters.orderId;

orderItemShipGroup = delegator.findByAnd("OrderItemShipGroup", ["orderId" : orderidshipping])
//List supplierAddressList = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(supplierAddress, EntityOperator.EQUALS), null, null, null, false);

description=null;
if(UtilValidate.isNotEmpty(orderItemShipGroup)){
	description= orderItemShipGroup.get(0).shippingInstructions;
}
print "\n\n\n partyGroup"+partyGroup+"\n\n\n"
context.orderItemShipGroup=description;

//terms and payment
if(UtilValidate.isEmpty(orderidshipping)){
	orderidshipping=0;
}
termsGroup = delegator.findByAnd("OrderTerm", ["orderId" : orderidshipping])

text=null;
if(UtilValidate.isNotEmpty(termsGroup)){
	text=termsGroup.get(0).textValue;
}
context.termsGroup=text;
typeId=null;

if(UtilValidate.isNotEmpty(termsGroup))
	typeId=termsGroup.get(0).termTypeId;

termType = delegator.findByPrimaryKey("TermType", ["termTypeId" :typeId ]);
if(UtilValidate.isEmpty(termType)){
	termType=[];
}
context.termType=termType;

