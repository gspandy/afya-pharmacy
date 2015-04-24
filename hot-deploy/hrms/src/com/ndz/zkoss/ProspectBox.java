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
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

public class ProspectBox extends Combobox{

	private static final long serialVersionUID = 1L;
	public ProspectBox() {
		refresh("");
	}

	public ProspectBox(String value) {
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
	private void refresh(String val) {
		if (StringUtils.isEmpty(val) && val.length() < 3)
			return;

		GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");

		Security security = (Security) Executions.getCurrent().getAttributes().get("security");

		boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);

		// PartyRoleAndPartyDetail
		EntityCondition ec1 = EntityCondition.makeCondition("partyTypeId", "PERSON");
		EntityCondition ec2 = EntityCondition.makeCondition("roleTypeId", "PROSPECT");

		EntityCondition emplCondition = EntityCondition.makeCondition(ec1, ec2);

		String likeString = "%" + val.toLowerCase() + "%";

		//EntityCondition ec3 = EntityCondition.makeCondition("firstName", EntityComparisonOperator.LIKE, likeString);
		//EntityCondition ec4 = EntityCondition.makeCondition("lastName", EntityComparisonOperator.LIKE, likeString);
		//EntityCondition ec5 = EntityCondition.makeCondition("partyId", EntityComparisonOperator.LIKE, likeString);

	//	String likeUpperCaseString = "%" + val.toUpperCase() + "%";
		
		
		EntityCondition ec3 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));
		EntityCondition ec4 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));
		EntityCondition ec5 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));

		//EntityCondition ec6 = EntityCondition.makeCondition("firstName", EntityComparisonOperator.LIKE, likeUpperCaseString);
		//EntityCondition ec7 = EntityCondition.makeCondition("lastName", EntityComparisonOperator.LIKE, likeUpperCaseString);
		//EntityCondition ec8 = EntityCondition.makeCondition("partyId", EntityComparisonOperator.LIKE, likeUpperCaseString);

		EntityCondition orCondition = EntityConditionList.makeCondition(UtilMisc.toList(ec3, ec4, ec5),
				EntityJoinOperator.OR);

		EntityCondition condition = EntityCondition.makeCondition(emplCondition, orCondition);

		List<GenericValue> employees = new ArrayList<GenericValue>(0);
			try {
				System.out.println(EntityCondition.makeCondition(emplCondition, orCondition));
				employees = delegator.findList("PartyRoleAndPartyDetail", condition, UtilMisc.toSet("partyId", "firstName",
						"lastName"), UtilMisc.toList("partyId"),null,true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
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
		// Remove the extra ones
		while (it != null && it.hasNext()) {
			it.next();
			it.remove();
		}
		
		this.open();
		
	}

}
