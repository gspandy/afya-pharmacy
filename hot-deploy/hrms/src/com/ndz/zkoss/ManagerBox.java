package com.ndz.zkoss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.security.Security;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

public class ManagerBox extends Combobox{

	private static final long serialVersionUID = 1L;
	public ManagerBox() {
		refresh("");
	}

	public ManagerBox(String value) {
		super(value); // it invokes setValue(), which inits the child comboitems
	}

	public void setValue(String value) {
		super.setValue(value);
		refresh(value); // refresh the child comboitems
	}

	/**
	 * Listens what an user is entering.
	 */
	public void onChanging(InputEvent evt) {
		if (!evt.isChangingBySelectBack())
			refresh(evt.getValue());
	}

	/**
	 * Refresh comboitem based on the specified value.
	 */

	@SuppressWarnings("deprecation")
	private void refresh(String val) {
		if (StringUtils.isEmpty(val) && val.length() < 3)
			return;

		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");

		Security security = (Security) Executions.getCurrent().getAttributes().get("security");
		EntityCondition ec1 = EntityCondition.makeCondition("groupId", "HUMANRES_MGR");
		//EntityCondition ec2 = EntityCondition.makeCondition("isRequisitionAdmin", "N");
		//EntityCondition emplCondition = EntityCondition.makeCondition(ec1,ec2);
		EntityCondition emplCondition = ec1;

		String likeString = "%" + val.toLowerCase() + "%";

		EntityCondition ec3 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));
		EntityCondition ec4 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));
		EntityCondition ec5 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));

		EntityCondition orCondition = EntityCondition.makeCondition(UtilMisc.toList(ec3, ec4, ec5),EntityJoinOperator.OR); 

		EntityCondition condition = EntityCondition.makeCondition(emplCondition, orCondition);

		List<GenericValue> employees = new ArrayList<GenericValue>(0);
			try {
				employees = delegator.findList("PartySecurityAndPartyDetail", condition, UtilMisc.toSet("partyId", "firstName","lastName"), UtilMisc.toList("partyId"),null,true);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		Iterator<Comboitem> it = getItems().iterator();
		for (GenericValue gv : employees) {
			Comboitem ci = null;
			if (it != null && it.hasNext()) {
				ci = ((Comboitem) it.next());
			} else {
				it = null;
				ci = new Comboitem();
			}
			ci.setValue(gv.getString("partyId"));
			ci.setLabel(gv.getString("partyId"));
			ci.setDescription(gv.getString("firstName") +" "+ gv.getString("lastName"));
			ci.setParent(this);
		}
		while (it != null && it.hasNext()) {
			it.next();
			it.remove();
		}
		
		this.open();
		
	}

}
