package com.ndz.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Window;

import com.ndz.component.party.PartyListRenderer;
import com.ndz.component.party.PartyRoleRenderer;
import com.ndz.component.party.PartySecurityGroupRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class CreateParty extends GenericForwardComposer {

	private Listbox listPartyGroup;
	private Listbox listPartyRole;
	private Listbox listsecurityGroup;
	private Window partyWindow;
	private Button createParty;
	private String firstName;
	private String middleName;
	private String lastName;
	private String userName;
	private String password;
	private String verifyPassword;
	private String positionId;
	public void doAfterCompose(Component partyWindow)
			throws GenericEntityException {

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, "Company");

		EntityCondition securityCondition1 = EntityCondition.makeCondition("groupId",
				EntityOperator.EQUALS, "HUMANRES_MGR");
		/*
		 * EntityCondition securityCondition2 = EntityCondition.makeCondition("groupId",
		 * EntityOperator.EQUALS, "HUMANRES_USER"); EntityCondition
		 * securityCondition3 = EntityCondition.makeCondition("groupId", EntityOperator.EQUALS,
		 * "HUMANRES_ADMIN");
		 * 
		 * EntityCondition securityCondition =
		 * EntityCondition.makeCondition(securityCondition1
		 * ,securityCondition2,securityCondition3);
		 */

		Set fieldToDisplay = new HashSet();
		Set roleToDisplay = new HashSet();
		Set securityGroupToDisplay = new HashSet();
		Set securityToDisplay = new HashSet();
		/* Render PartyGroup */
		fieldToDisplay.add("partyIdTo");
		List partyGroup = delegator.findList("PartyRelationship", condition,
				fieldToDisplay, null, null, false);
		Object partyGroupArray = partyGroup.toArray(new GenericValue[partyGroup
				.size()]);
		SimpleListModel partyGroupList = new SimpleListModel(partyGroup);

		listPartyGroup = (Listbox) partyWindow.getFellow("listPartyGroup");
		listPartyGroup.setModel(partyGroupList);
		listPartyGroup.setItemRenderer(new PartyListRenderer());

		/* Render PartyRoleType */
		roleToDisplay.add("roleTypeId");
		List partyRole = delegator.findList("PartyRole", null, roleToDisplay,
				null, null, false);
		Object partyRoleArray = partyRole.toArray(new GenericValue[partyRole
				.size()]);
		SimpleListModel partyRoleList = new SimpleListModel(partyRole);
		listPartyRole = (Listbox) partyWindow.getFellow("listPartyRole");
		listPartyRole.setModel(partyRoleList);
		listPartyRole.setItemRenderer(new PartyRoleRenderer());

		/* Render PartySecurityGroup */
		securityToDisplay.add("description");
		List securityGroup = delegator.findList("SecurityGroup",
				securityCondition1, securityToDisplay, null, null, false);
		Object securityGroupArray = securityGroup
				.toArray(new GenericValue[securityGroup.size()]);
		SimpleListModel securityGroupList = new SimpleListModel(securityGroup);
		listsecurityGroup = (Listbox) partyWindow
				.getFellow("listsecurityGroup");
		listsecurityGroup.setModel(securityGroupList);
		listsecurityGroup.setItemRenderer(new PartySecurityGroupRenderer());
		
	}


	public String getPositionId() {
		return positionId;
	}


	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}


	public String getVerifyPassword() {
		return verifyPassword;
	}


	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}


	public void onClick(Event event) {
		System.out.println("***********OnClick Called****************");
	}
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Button getCreateParty() {
		return createParty;
	}

	public void setCreateParty(Button createParty) {
		this.createParty = createParty;
	}

	public Listbox getListsecurityGroup() {
		return listsecurityGroup;
	}

	public void setListsecurityGroup(Listbox listsecurityGroup) {
		this.listsecurityGroup = listsecurityGroup;
	}

	public Listbox getListPartyRole() {
		return listPartyRole;
	}

	public void setListPartyRole(Listbox listPartyRole) {
		this.listPartyRole = listPartyRole;
	}

	public Listbox getListPartyGroup() {
		return listPartyGroup;
	}

	public void setListPartyGroup(Listbox listPartyGroup) {
		this.listPartyGroup = listPartyGroup;
	}

	public Window getPartyWindow() {
		return partyWindow;
	}

	public void setPartyWindow(Window partyWindow) {
		this.partyWindow = partyWindow;
	}

}
