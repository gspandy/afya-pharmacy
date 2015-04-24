package com.hrms.composer;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class EmployeeSearchController extends GenericForwardComposer {

	private String firstName;

	private String lastName;

	private String partyId;
	
	private GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	
	private List<GenericValue> employees;

	public void doAfterCompose(Component component) throws Exception {
		super.doAfterCompose(component);
	}

	public List<GenericValue> getPersons() throws GenericEntityException{
	EntityCondition condition1 = EntityCondition.makeCondition("firstName",	EntityComparisonOperator.LIKE, firstName + "%");
	EntityCondition condition2 =EntityCondition.makeCondition("lastName",EntityComparisonOperator.LIKE, lastName + "%");
	EntityCondition condition3 = EntityCondition.makeCondition("partyId",EntityComparisonOperator.EQUALS, partyId);
	EntityCondition joinCondition = EntityCondition.makeCondition(condition1,EntityComparisonOperator.OR,condition2);
	EntityCondition entityCondition = EntityCondition.makeCondition(joinCondition,EntityComparisonOperator.OR,condition3);
	return delegator.findList("Person", entityCondition, null, null, null, false);
	}

	
	public void filterPersonByRole() throws GenericEntityException{
		EntityCondition ec1 = EntityCondition.makeCondition("roleTypeId",EntityComparisonOperator.NOT_EQUAL ,"PROSPECT");
		EntityCondition ec2 = EntityCondition.makeCondition("roleTypeId",EntityComparisonOperator.NOT_EQUAL ,"_NA_");
		EntityCondition ec3 = EntityCondition.makeCondition(ec1,EntityComparisonOperator.AND,ec2);
		for(GenericValue  genericValue : getPersons()){
			EntityCondition ec4 = EntityCondition.makeCondition("partyId",	EntityComparisonOperator.EQUALS ,genericValue.getString("partyId"));
			EntityCondition ec = EntityCondition.makeCondition(ec3, EntityComparisonOperator.AND,ec4);
			List<GenericValue> partyRoleGv = delegator.findList("PartyRole", ec, null, null, null, false);
			if(UtilValidate.isNotEmpty(partyRoleGv)){
				employees.add(genericValue);
			}
				
		}
	}
	
	public ListitemRenderer getListItemRenderer(){
		return new ListitemRenderer() {
			
			public void render(Listitem listitem, Object object) throws Exception {
				GenericValue gv = (GenericValue)object;
				listitem.setValue(gv);
				Listcell firstNameListCell = new Listcell(gv.getString("firstName"));
				firstNameListCell.setParent(listitem);
				Listcell lastNameListcell = new Listcell(gv.getString("lastName"));
				lastNameListcell.setParent(listitem);
				Listcell partyIdListcell = new Listcell(gv.getString("partyId"));
				partyIdListcell.setParent(listitem);
			}
		};
	}
	
	public void serachEmployee() throws GenericEntityException{
	employees = new ArrayList<GenericValue>();
	filterPersonByRole();
	}
	public List<GenericValue> getEmployees() {
		return employees;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	private static final long serialVersionUID = 1L;

}
