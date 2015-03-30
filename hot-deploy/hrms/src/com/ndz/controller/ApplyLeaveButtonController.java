package com.ndz.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericComposer;

public class ApplyLeaveButtonController extends GenericComposer{
	
	public void onClick(Event event)
	{
		Component partyWindow = event.getTarget().getParent();
		System.out
				.println("***********onClick$createParty Called****************");
	}
	

}
