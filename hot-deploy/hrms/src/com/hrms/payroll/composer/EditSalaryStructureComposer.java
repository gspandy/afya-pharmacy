package com.hrms.payroll.composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Panel;

import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.ndz.zkoss.util.MessageUtils;

public class EditSalaryStructureComposer extends HrmsComposer {

	private String salaryStructureId;

	public String getSalaryStructureId() {
		return salaryStructureId;
	}

	public void setSalaryStructureId(String salaryStructureId) {
		this.salaryStructureId = salaryStructureId;
	}

	private BindingListModel attachedSalaryHeads;

	private BindingListModel availableSalaryHeads;

	private GenericValue salaryStructure;

	private Listbox attachedSHListbox;
	private Listbox availableSHListbox;
	List<GenericValue> allHeads;
	List<GenericValue> attachedSalaryHeadList;
	Panel rulesPanel;

	List payrollRules = new ArrayList();

	private boolean havingFormulas;

	public boolean isHavingFormulas() {
		return havingFormulas;
	}

	public void setHavingFormulas(boolean havingFormulas) {
		this.havingFormulas = havingFormulas;
	}

	public List getPayrollRules() {
		return payrollRules;
	}

	public void setPayrollRules(List payrollRules) {
		this.payrollRules = payrollRules;
	}

	public GenericValue getSalaryStructure() {
		return salaryStructure;
	}

	public void setSalaryStructure(GenericValue salaryStructure) {
		this.salaryStructure = salaryStructure;
	}

	@Override
	/*
	 * protected void executeAfterCompose(Component comp) throws Exception {
	 * 
	 * EntityCondition ec1 = EntityCondition.makeCondition("salaryHeadTypeId",
	 * EntityComparisonOperator.NOT_EQUAL, "Adhoc"); EntityCondition ec3 =
	 * EntityCondition.makeCondition("geoId",
	 * HumanResUtil.getDefaultGeoIdForLoggedInUser(userLogin)); allHeads =
	 * delegator.findList("SalaryHead", EntityConditionList.makeCondition(ec1,
	 * ec3), null, UtilMisc.toList("-hrName"), null, false);
	 * attachedSalaryHeadList = delegator.findList("SalaryStructureHeadDetail",
	 * EntityCondition.makeCondition("salaryStructureId", salaryStructureId),
	 * null, null, null, false);
	 * 
	 * if (UtilValidate.isEmpty(attachedSalaryHeadList)) { EntityCondition ec4 =
	 * EntityCondition.makeCondition("isMandatory", "Y"); attachedSalaryHeadList
	 * = delegator.findList("SalaryHead", EntityConditionList.makeCondition(ec1,
	 * ec3, ec4), null, UtilMisc .toList("-hrName"), null, false); }
	 * 
	 * List<String> allSalaryIds = new ArrayList<String>();
	 * 
	 * for (GenericValue salaryHead : attachedSalaryHeadList) {
	 * allSalaryIds.add(salaryHead.getString("salaryHeadId")); }
	 * 
	 * for (Iterator<GenericValue> iter = allHeads.iterator(); iter.hasNext();)
	 * { GenericValue gv = iter.next(); if (gv == null ||
	 * allSalaryIds.contains(gv.getString("salaryHeadId"))) { iter.remove();
	 * iter = allHeads.iterator(); } }
	 * 
	 * salaryStructure = delegator.findOne("SalaryStructure",
	 * UtilMisc.toMap("salaryStructureId", salaryStructureId), false);
	 * 
	 * availableSalaryHeads = new BindingListModelList(allHeads, false);
	 * attachedSalaryHeads = new BindingListModelList(attachedSalaryHeadList,
	 * false);
	 * 
	 * binder.loadAll(); }
	 */
	protected void executeAfterCompose(Component comp) throws Exception {

		EntityCondition ec1 = EntityCondition.makeCondition("salaryHeadTypeId", EntityComparisonOperator.NOT_EQUAL, "Adhoc");
		allHeads = delegator.findList("SalaryHead", EntityConditionList.makeCondition(ec1), null, UtilMisc.toList("-hrName"), null, false);

		attachedSalaryHeadList = delegator.findList("SalaryStructureHeadDetail", EntityCondition.makeCondition("salaryStructureId", salaryStructureId), null,
				null, null, false);

		if (UtilValidate.isEmpty(attachedSalaryHeadList)) {
			EntityCondition ec4 = EntityCondition.makeCondition("isMandatory", "Y");
			attachedSalaryHeadList = delegator.findList("SalaryHead", EntityConditionList.makeCondition(ec1, ec4), null, UtilMisc.toList("-hrName"), null,
					false);
		}

		List<String> allSalaryIds = new ArrayList<String>();

		for (GenericValue salaryHead : attachedSalaryHeadList) {
			allSalaryIds.add(salaryHead.getString("salaryHeadId"));
		}

		for (Iterator<GenericValue> iter = allHeads.iterator(); iter.hasNext();) {
			GenericValue gv = iter.next();
			if (gv == null || allSalaryIds.contains(gv.getString("salaryHeadId"))) {
				iter.remove();
				iter = allHeads.iterator();
			}
		}

		salaryStructure = delegator.findOne("SalaryStructure", UtilMisc.toMap("salaryStructureId", salaryStructureId), false);

		availableSalaryHeads = new BindingListModelList(allHeads, false);
		attachedSalaryHeads = new BindingListModelList(attachedSalaryHeadList, false);

		binder.loadAll();
	}

	public String getSalaryStrucuteId() {
		return salaryStructureId;
	}

	public void setSalaryStrucuteId(String salaryStrucuteId) {
		this.salaryStructureId = salaryStrucuteId;
	}

	public BindingListModel getAttachedSalaryHeads() {
		return attachedSalaryHeads;
	}

	public void setAttachedSalaryHeads(BindingListModel attachedSalaryHeads) {
		this.attachedSalaryHeads = attachedSalaryHeads;
	}

	public BindingListModel getAvailableSalaryHeads() {
		return availableSalaryHeads;
	}

	public void setAvailableSalaryHeads(BindingListModel availableSalaryHeads) {
		this.availableSalaryHeads = availableSalaryHeads;
	}

	public void onClick$removeSH(Event event) throws Exception {
		Set<Listitem> items = attachedSHListbox.getSelectedItems();

		for (Iterator<Listitem> iter = items.iterator(); iter.hasNext();) {
			Listitem li = iter.next();
			GenericValue gv = (GenericValue) li.getValue();

			if ("N".equals(gv.getString("isMandatory"))) {
				attachedSalaryHeadList.remove(gv);
				allHeads.add((GenericValue) li.getValue());
			} else {
				MessageUtils.showInfoMessage("Cannnot remove " + gv.getString("hrName"));
				System.out.println(" Not Removing Mandatory Salary Heads.");
			}
		}
		System.out.println("*********remove allHeads size " + allHeads.size());
		System.out.println("*********remove attachedSalaryHeadList size " + attachedSalaryHeadList.size());

		availableSalaryHeads = new BindingListModelList(allHeads, false);
		attachedSalaryHeads = new BindingListModelList(attachedSalaryHeadList, false);
		binder.loadAttribute(availableSHListbox, "model");
		binder.loadAttribute(attachedSHListbox, "model");

	}

	public void onClick$addSH(Event event) {
		Set<Listitem> items = availableSHListbox.getSelectedItems();

		for (Iterator<Listitem> iter = items.iterator(); iter.hasNext();) {
			Listitem li = iter.next();
			allHeads.remove((GenericValue) li.getValue());
			attachedSalaryHeadList.add((GenericValue) li.getValue());
		}
		System.out.println("*********add allHeads size " + allHeads.size());
		System.out.println("*********add attachedSalaryHeadList size " + attachedSalaryHeadList.size());

		availableSalaryHeads = new BindingListModelList(allHeads, false);
		attachedSalaryHeads = new BindingListModelList(attachedSalaryHeadList, false);
		binder.loadAttribute(availableSHListbox, "model");
		binder.loadAttribute(attachedSHListbox, "model");
	}

	public void onClick$saveAndContinue(Event event) throws Exception {
		boolean beganTransaction = TransactionUtil.begin();

		List<String> salaryHeadsToDelete = new ArrayList<String>();
		for (GenericValue each : attachedSalaryHeadList) {
			salaryHeadsToDelete.add(each.getString("salaryHeadId"));
		}
		EntityCondition ec1 = EntityCondition.makeCondition("salaryHeadId", EntityComparisonOperator.NOT_IN, salaryHeadsToDelete);
		EntityCondition ec2 = EntityCondition.makeCondition("salaryStructureId", salaryStructureId);
		if(salaryHeadsToDelete.size() > 0){
			List<GenericValue> salaryStHeadList = delegator.findList("SalaryStructureHead", EntityConditionList.makeCondition(ec1, ec2), null, null, null, false);
		for(GenericValue gv : salaryStHeadList){
			String salaryStructureHeadId = gv.getString("salaryStructureHeadId");
			delegator.removeByCondition("PayrollHeadRule", EntityConditionList.makeCondition("salaryStructureHeadId",EntityOperator.EQUALS,salaryStructureHeadId));
		}
		delegator.removeByCondition("SalaryStructureHead", EntityConditionList.makeCondition(ec1, ec2));
		}
		for (GenericValue each : attachedSalaryHeadList) {

			GenericValue value = EntityUtil.getFirst(delegator.findList("SalaryStructureHead",
					EntityCondition.makeCondition(UtilMisc.toMap("salaryHeadId", each.getString("salaryHeadId"), "salaryStructureId", salaryStructureId)),
					null, null, null, false));

			String seqId = null;
			if (value == null) {
				seqId = delegator.getNextSeqId("SalaryStructureHead");
				value = delegator.makeValidValue("SalaryStructureHead",
						UtilMisc.toMap("salaryStructureHeadId", seqId, "salaryStructureId", salaryStructureId, "salaryHeadId", each.getString("salaryHeadId")));
			} else {
				seqId = value.getString("salaryStructureHeadId");
			}
			if ("FORMULA".equals(each.getString("salaryComputationTypeId"))) {
				payrollRules.add(UtilMisc.toMap("salaryStructureHeadId", seqId, "salaryHeadId", each.getString("salaryHeadId"), "hrName",
						value.getRelatedOne("SalaryHead").getString("hrName")));
				havingFormulas = true;
			}

			delegator.createOrStore(value);
		}

		if (payrollRules.size() == 0)
			salaryStructure.put("active", "1");

		delegator.store(salaryStructure);

		TransactionUtil.commit(beganTransaction);
		Map<String, GenericForwardComposer> m = new HashMap<String, GenericForwardComposer>();
		m.put("composer", this);
		System.out.println(event.getTarget());
		Executions.createComponents("salStructureRuleAssoc.zul", event.getTarget().getParent(), m);
		event.getTarget().detach();

	}

	public ListitemRenderer getSalaryHeadRenderer() {
		return new ListitemRenderer() {
			public void render(Listitem li, Object data) {
				GenericValue sh = (GenericValue) data;
				new Listcell(sh.getString("hrName")).setParent(li);
				new Listcell(sh.getString("salaryHeadTypeId")).setParent(li);
				new Listcell(sh.getString("isCr")).setParent(li);
				new Listcell(sh.getString("isTaxable")).setParent(li);
				new Listcell(sh.getString("isMandatory")).setParent(li);
				String computationType = sh.getString("salaryComputationTypeId");
				new Listcell(computationType).setParent(li);
			}
		};
	}

	public ListitemRenderer getSalaryHeadRuleRenderer() throws Exception {
		return new SalaryHeadRuleRenderer(delegator);
	}

	private static class SalaryHeadRuleRenderer implements ListitemRenderer {

		private GenericDelegator delegator;
		List<String> rules = null;

		public SalaryHeadRuleRenderer(GenericDelegator delegator) {
			this.delegator = delegator;
			try {
				List<GenericValue> l = delegator.findList("PayrollRule", null, null, null, null, true);
				rules = new ArrayList<String>();
				for (GenericValue g : l) {
					rules.add(g.getString("ruleId"));
				}
			} catch (Exception e) {
			}

		}

		public void render(Listitem li, final Object data) {
			try {
				Map sh = (Map) data;
				String salaryHeadId = (String) sh.get("salaryHeadId");
				new Listcell(salaryHeadId).setParent(li);

				String salaryHeadName = (String) sh.get("hrName");
				new Listcell(salaryHeadName).setParent(li);

				final GenericValue existingRec = EntityUtil.getFirst(delegator.findList("PayrollHeadRule",
						EntityCondition.makeCondition("salaryStructureHeadId", sh.get("salaryStructureHeadId")), null, null, null, false));
				String existingRuleId = null;
				System.out.println("Get rules if any for Salary Structure Head Id " + sh.get("salaryStructureHeadId") + " --- " + existingRec);
				if (existingRec != null) {
					existingRuleId = existingRec.getString("ruleId");
					sh.put("ruleId", existingRuleId);
				} else {
					rules.add(0, null);
				}
				Listcell cell = new Listcell();
				cell.setParent(li);

				final Listbox ruleBox = new Listbox();
				ruleBox.setMold("select");
				// Create a Map for binding
				ruleBox.setModel(new BindingListModelList(rules, false));
				ruleBox.setItemRenderer(new RulesComboitemRenderer(existingRuleId));
				sh.put("ruleListbox", ruleBox);

				ruleBox.addEventListener("onSelect", new EventListener() {
					public void onEvent(Event event) throws Exception {
						Map sh = (Map) data;
						sh.put("ruleId", ((Listbox) event.getTarget()).getSelectedItem().getValue());
						Clients.closeErrorBox(ruleBox);
					}
				});

				// ruleBox.setConstraint(new SimpleConstraint("no empty"));
				ruleBox.setParent(cell);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class RulesComboitemRenderer implements ListitemRenderer {

		private String existingRuleId;

		public RulesComboitemRenderer(String existingRuleId) {
			this.existingRuleId = existingRuleId;
		}

		public void render(Listitem li, Object data) {
			if (data == null)
				return;
			String ruleId = (String) data;
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			GenericValue val = null;
			li.setValue(ruleId);
			try {
				val = delegator.findOne("PayrollRule", UtilMisc.toMap("ruleId", ruleId), true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (ruleId.equals(existingRuleId)) {
				li.setSelected(true);
				int index = li.getIndex();
				((Listbox) li.getParent()).setSelectedIndex(index);
			}
			li.setLabel(val.getString("ruleName"));
		}
	}
}
