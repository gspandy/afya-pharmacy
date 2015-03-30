import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.order.*;
import org.sme.order.util.OrderMgrUtil;

List<String> managerSubOrdinateList = new ArrayList<String>();

GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
partyId = userLogin.partyId;

if ("admin".equals(partyId)) {
	parameters.portalPageId = "OrderPortalPage";
	return;
}

if( ((OrderMgrUtil.isSalesManger(partyId)) || (OrderMgrUtil.isSalesReprensentative(partyId))) && ((OrderMgrUtil.isPurchaseManger(partyId)) || (OrderMgrUtil.isPurchaseReprensentative(partyId))) ) {
	parameters.portalPageId = "OrderPortalPage";
	return;
}

if ((OrderMgrUtil.isSalesManger(partyId) || OrderMgrUtil.isSalesReprensentative(partyId)) && ((OrderMgrUtil.isQuoteManger(partyId))|| OrderMgrUtil.isQuoteReprensentative(partyId) || OrderMgrUtil.isRequirementManger(partyId)||OrderMgrUtil.isRequirementsRepresentative(partyId))){
	parameters.portalPageId = "OrderPortalPageSo";
	return;
}
if (OrderMgrUtil.isPurchaseManger(partyId) || OrderMgrUtil.isPurchaseReprensentative(partyId) && (OrderMgrUtil.isQuoteManger(partyId) || OrderMgrUtil.isQuoteReprensentative(partyId)|| OrderMgrUtil.isRequirementManger(partyId)||OrderMgrUtil.isRequirementsRepresentative(partyId) )) {
	parameters.portalPageId = "OrderPortalPagePo";
	return;
}

if (OrderMgrUtil.isSalesManger(partyId) || OrderMgrUtil.isSalesReprensentative(partyId)) {
	parameters.portalPageId = "OrderPortalPageSo";
}

if (OrderMgrUtil.isPurchaseManger(partyId) || OrderMgrUtil.isPurchaseReprensentative(partyId)) {
	parameters.portalPageId = "OrderPortalPagePo";
}

if (OrderMgrUtil.isQuoteManger(partyId) || OrderMgrUtil.isQuoteReprensentative(partyId)) {
	parameters.portalPageId = "OrderPortalPage";
}
if (OrderMgrUtil.isRequirementManger(partyId) || OrderMgrUtil.isRequirementsRepresentative(partyId)) {
	parameters.portalPageId = "OrderPortalPagePo";
}