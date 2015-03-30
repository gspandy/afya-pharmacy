package com.ndz.component.party;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceValidationException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class PartySearchPanel extends GenericForwardComposer {

	private Grid resultTable;
	private Textbox viewSize;
	private Textbox viewIndex;
    
	public void onPaging$resultTable(Event event) {
		System.out.println("onClick$pagination*********************" + event);
		int pgno = ((PagingEvent) ((ForwardEvent) event).getOrigin())
				.getActivePage();
		System.out.println("getActivePage *********************" + pgno);
		System.out.println(requestScope.values());
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		System.out.println(" GenericDispatcher " + dispatcher);
		Map context = UtilMisc.toMap("VIEW_SIZE", viewSize.getValue(),
				"VIEW_INDEX", String.valueOf(viewIndex.getValue()), "showAll", "Y", "userLogin", requestScope
						.get("userLogin"),"lookupFlag","Y");
		Map results = null;
		try {
			results = dispatcher.runSync("findParty", context);
		} catch (ServiceValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List partyList = (List)results.get("partyList");
		resultTable.setModel(new SimpleListModel(partyList));
		resultTable.setPageSize((Integer)results.get("partyListSize"));
		resultTable.renderAll();
	}

	public Grid getResultTable() {
		return resultTable;
	}

	public void setResultTable(Grid resultTable) {
		this.resultTable = resultTable;
	}

	public Textbox getViewSize() {
		return viewSize;
	}

	public void setViewSize(Textbox viewSize) {
		this.viewSize = viewSize;
	}

	public Textbox getViewIndex() {
		return viewIndex;
	}

	public void setViewIndex(Textbox viewIndex) {
		this.viewIndex = viewIndex;
	}

	public void onClick$testButton(Event event) {
		System.out.println("onClick*********************" + event);

	}
}
