package com.nzion.tally;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

public abstract class AbstractVisitor implements Visitor {

	protected final Delegator delegator;
	protected final LocalDispatcher dispatcher;
	protected final GenericValue userLogin;
	protected String partyId;
	protected String organizationId;

	protected AbstractVisitor(LocalDispatcher dispatcher, GenericValue userLogin, String organizationId) {
	this.delegator = dispatcher.getDelegator();
	this.dispatcher = dispatcher;
	this.userLogin = userLogin;
	this.organizationId = organizationId;
	}
	
	@Override
	public String toString() {
	return this.getClass().getName();
	}
}
