package com.ndz.zkoss.handler;

import java.util.List;

import org.ofbiz.entity.transaction.TransactionUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventThreadCleanup;
import org.zkoss.zk.ui.event.EventThreadInit;

public class EventTransactionHandler implements EventThreadInit,
		EventThreadCleanup {

	private boolean hasErrors;

	public boolean init(Component comp, Event event) throws Exception {
		return true;
	}

	public void prepare(Component comp, Event event) throws Exception {
		boolean beganTrans = false;
		if (!TransactionUtil.isTransactionInPlace()) {
			beganTrans = TransactionUtil.begin(0);
		}
		Executions.getCurrent().setAttribute("beganTrans", beganTrans);
	}

	public void cleanup(Component comp, Event evt, List errs) throws Exception {
		if(errs != null)
			hasErrors = false;
	}

	public void complete(Component comp, Event evt) throws Exception {
		System.out
				.println(" ************ EventTransactionHandler cleanup ..........");

		boolean beganTransaction = (Boolean) Executions.getCurrent()
				.getAttribute("beganTrans");

		if (beganTransaction) {
			System.out.println("!!! Commiting Global Txn.............");
			TransactionUtil.commit();
		}
		if (hasErrors && beganTransaction) {
			System.out.println("!!! Rolling back Global Txn.............");
			TransactionUtil.rollback();
		}
	}

}
