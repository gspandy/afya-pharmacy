package com.ndz.controller;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.impl.XulElement;

import com.ndz.zkoss.PagingSimpleListModel;

public class TestController extends GenericSearchController {


	private static final long serialVersionUID = 1L;
	
	private final int _pageSize = 10;
	private int _startPageNumber = 0;
	private int _totalSize = 0;
	private boolean _needsTotalSizeUpdate = true;

	private XulElement dataGrid;
	private Paging paging;

	PagingSimpleListModel model = null;

	public TestController() {
		super();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if (!"searchPanel".equals(comp.getId()))
			return;
		populateGrid(comp,true);
	}

	@SuppressWarnings("unchecked")
	private void populateGrid(Component comp,boolean perEmployee) {
		//super.event = event;
		//super.eventTarget = event.getTarget();
		paging.setPageSize(_pageSize);
		Map<String, String> inputParams = createSearchData();
		if (perEmployee)
			inputParams.put("partyId", userLogin.getString("partyId"));
		
		Map context = UtilMisc.toMap("userLogin", super.userLogin,
				"inputFields", inputParams, "noConditionFind", "Y",
				"entityName", ((Component) comp.getFellows()).getAttribute("entityName"));
		try {
			model = new PagingSimpleListModel(context, 0, _pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		_totalSize = model.getTotalSize();
		paging.setTotalSize(_totalSize);

		if (dataGrid instanceof Grid) {
			Grid grid = (Grid) dataGrid;
			grid.setModel(model);
		} else if (dataGrid instanceof Listbox) {
			Listbox lbox = (Listbox) dataGrid;
			lbox.setModel(model);
		}
		
	}
	public void onPaging$paging(ForwardEvent event) {
		final PagingEvent pe = (PagingEvent) event.getOrigin();
		_startPageNumber = pe.getActivePage();
		refreshModel(event.getTarget(), _startPageNumber);
	}

	@SuppressWarnings("unchecked")
	private void refreshModel(Component comp, int activePage) {
		System.out.println(" refreshModel activePage "+activePage);
		System.out.println(" refreshModel paging "+paging);
		System.out.println(" refreshModel comp "+comp);
		
		Boolean companyWideSearch = Boolean.parseBoolean((String)comp.getAttribute("companyWide"));
		
		
		paging = paging==null?(Paging)comp:paging;
		
		if (paging != null) {
			paging.setPageSize(_pageSize);
			Map<String, String> inputParams = createSearchData();
			if(!companyWideSearch)
			inputParams.put("partyId", userLogin.getString("partyId"));
			
			Map context = UtilMisc.toMap("userLogin", super.userLogin,
					"inputFields", inputParams, "noConditionFind", "Y",
					"entityName", event.getTarget().getAttribute("entityName"));
			try {
				model = new PagingSimpleListModel(context, activePage, _pageSize);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (_needsTotalSizeUpdate) {
				_totalSize = model.getTotalSize();
				_needsTotalSizeUpdate = false;
			}
			System.out.println(" refreshModel _totalSize = "+_totalSize);
			paging.setTotalSize(_totalSize);
			if (dataGrid instanceof Grid) {
				Grid grid = (Grid) dataGrid;
				grid.setModel(model);
			} else if (dataGrid instanceof Listbox) {
				Listbox lbox = (Listbox) dataGrid;
				lbox.setModel(model);
			}
		}
	}
	
}
