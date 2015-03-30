import org.ofbiz.entity.util.*;
GenericValue company = delegator.findOne("PartyGroup",["partyId":"Company"],true);
System.out.println(" COMPANY "+company);
context.company=company;