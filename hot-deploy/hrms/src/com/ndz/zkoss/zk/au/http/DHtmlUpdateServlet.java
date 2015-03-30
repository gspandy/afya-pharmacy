package com.ndz.zkoss.zk.au.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilTimer;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.stats.VisitHandler;

public class DHtmlUpdateServlet extends org.zkoss.zk.au.http.DHtmlUpdateServlet {

	private String module = DHtmlUpdateServlet.class.getName();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		preIntercept(request, response);
		super.doGet(request, response);
		postIntecept();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		preIntercept(request, response);
		super.doPost(request, response);
		postIntecept();
	}

	private void preIntercept(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Debug.logInfo("Invoked Customer Process Method.", module);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		String charset = getServletContext().getInitParameter("charset");
		if (charset == null || charset.length() == 0)
			charset = request.getCharacterEncoding();
		if (charset == null || charset.length() == 0)
			charset = "UTF-8";
		if (Debug.verboseOn())
			Debug
					.logVerbose(
							"The character encoding of the request is: ["
									+ request.getCharacterEncoding()
									+ "]. The character encoding we will use for the request and response is: ["
									+ charset + "]", module);

		if (!"none".equals(charset)) {
			request.setCharacterEncoding(charset);
		}
		String webappName = UtilHttp.getApplicationName(request);

		String rname = "";
		if (request.getPathInfo() != null) {
			rname = request.getPathInfo().substring(1);
		}
		if (rname.indexOf('/') > 0) {
			rname = rname.substring(0, rname.indexOf('/'));
		}
		UtilTimer timer = null;
		if (Debug.timingOn()) {
			timer = new UtilTimer();
			timer.setLog(true);
			timer.timerString("[" + rname + "] Request Begun, encoding=["
					+ charset + "]", module);
		}

		// set the Entity Engine user info if we have a userLogin
		if (userLogin != null) {
			GenericDelegator.pushUserIdentifier(userLogin
					.getString("userLoginId"));
		}
		GenericDelegator delegator = null;
		String delegatorName = (String) session.getAttribute("delegatorName");
		if (UtilValidate.isNotEmpty(delegatorName)) {
			delegator = GenericDelegator.getGenericDelegator(delegatorName);
		}
		if (delegator == null) {
			delegator = (GenericDelegator) getServletContext().getAttribute(
					"delegator");
		}
		if (delegator == null) {
			Debug
					.logError(
							"[ControlServlet] ERROR: delegator not found in ServletContext",
							module);
		} else {
			request.setAttribute("delegator", delegator);
			// always put this in the session too so that session events can use
			// the delegator
			session.setAttribute("delegatorName", delegator.getDelegatorName());
		}

		LocalDispatcher dispatcher = (LocalDispatcher) session
				.getAttribute("dispatcher");
		if (dispatcher == null) {
			dispatcher = (LocalDispatcher) getServletContext().getAttribute(
					"dispatcher");
		}
		if (dispatcher == null) {
			Debug
					.logError(
							"[ControlServlet] ERROR: dispatcher not found in ServletContext",
							module);
		}
		request.setAttribute("dispatcher", dispatcher);

		Security security = (Security) session.getAttribute("security");
		if (security == null) {
			security = (Security) getServletContext().getAttribute("security");
		}
		if (security == null) {
			Debug
					.logError(
							"[ControlServlet] ERROR: security not found in ServletContext",
							module);
		}
		request.setAttribute("security", security);
		UtilHttp.setInitialRequestInfo(request);
		VisitHandler.getVisitor(request, response);

	}

	private void postIntecept() {
		// sanity check: make sure we don't have any transactions in place
		try {
			// roll back current TX first
			if (TransactionUtil.isTransactionInPlace()) {
				Debug
						.logWarning(
								"*** NOTICE: ControlServlet finished w/ a transaction in place! Rolling back.",
								module);
				TransactionUtil.rollback();
			}

			// now resume/rollback any suspended txs
			if (TransactionUtil.suspendedTransactionsHeld()) {
				int suspended = TransactionUtil.cleanSuspendedTransactions();
				Debug.logWarning("Resumed/Rolled Back [" + suspended
						+ "] transactions.", module);
			}
		} catch (GenericTransactionException e) {
			Debug.logWarning(e, module);
		}

		// sanity check 2: make sure there are no user or session infos in the
		// delegator, ie clear the thread
		GenericDelegator.clearUserIdentifierStack();
		GenericDelegator.clearSessionIdentifierStack();
	}

}
