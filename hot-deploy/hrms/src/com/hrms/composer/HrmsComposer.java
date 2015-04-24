package com.hrms.composer;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;

public abstract class HrmsComposer extends GenericForwardComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2950234377482605830L;
	protected AnnotateDataBinder binder;
	protected GenericDelegator delegator = null;
	public GenericDelegator getDelegator() {
		return delegator;
	}

	public void setDelegator(GenericDelegator delegator) {
		this.delegator = delegator;
	}

	protected LocalDispatcher dispatcher;
	protected GenericValue userLogin;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		boolean beganTrans = false;
		if (!TransactionUtil.isTransactionInPlace()) {
			beganTrans = TransactionUtil.begin();
		}
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		delegator = (GenericDelegator) Executions.getCurrent().getAttributes()
				.get("delegator");
		dispatcher = (LocalDispatcher) Executions.getCurrent().getAttributes()
		.get("dispatcher");
		userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
		executeAfterCompose(comp);
		if (beganTrans) {
			TransactionUtil.commit();
		}
	}

	protected  void executeAfterCompose(Component comp) throws Exception{}

}
