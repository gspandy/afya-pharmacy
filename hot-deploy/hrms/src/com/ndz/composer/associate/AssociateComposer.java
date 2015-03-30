package com.ndz.composer.associate;

import java.util.Collection;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.api.Listbox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class AssociateComposer extends GenericForwardComposer {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		System.out.println(comp.getDesktop().getSession().getAttributes());
		GenericDelegator delegator = (GenericDelegator) comp.getDesktop()
				.getSession().getAttribute("delegator");

		if (delegator == null)
			delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition whereCondition = EntityCondition.makeCondition(UtilMisc
				.toMap("partyTypeId", "PARTY_GROUP", "partyRelationshipTypeId",
						"GROUP_ROLLUP", "partyIdFrom", "Company"));

		boolean beganTransaction = false;
		if (!TransactionUtil.isTransactionInPlace())
			beganTransaction = TransactionUtil.begin();

		EntityListIterator eli = delegator.find("PartyRelationshipAndDetail",
				whereCondition, null, null, null, null);
		Listbox departmentComp = ((Listbox) comp.getFellow("department"));
		departmentComp.setModel(new SimpleListModel(eli.getCompleteList()));
		departmentComp.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem listItem, Object data) throws Exception {
				GenericValue gv = (GenericValue) data;
				listItem.setLabel(gv.getString("groupName"));
				listItem.setValue(gv.getString("partyId"));
			}
		});
		eli.close();
		if (beganTransaction)
			TransactionUtil.commit();

	}

	public void onClick$saveAssociate(Event event) {
		System.out.println(" Event Invoked");
		System.out.println(event.getPage().getAttributes());
		
		Collection<Component> comps = event.getTarget().getFellows();

		for(Component comp:comps){
			if(comp.getDefinition().getName()!=null){
				System.out.println(comp.getDefinition().getName());
				System.out.println(comp.getAttributes());
			}
		}
	}
}
