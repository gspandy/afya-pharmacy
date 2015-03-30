package com.ndz.zkoss;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.ListModelExt;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class PagingSimpleListModel extends
		AbstractPagingListModel<GenericValue> implements ListModelExt{

	private static String module = PagingSimpleListModel.class.getName();

	private static final long serialVersionUID = 4370353438941246687L;

	private boolean restrictEmployee = true;

	public PagingSimpleListModel(Map searchParams, int startPageNumber,
			int pageSize) throws Exception {
		super(searchParams, startPageNumber, pageSize);
	}

	@Override
	protected Map<String, Object> getPageData(Map searchParams,
			int itemStartNumber, int pageSize) throws GenericServiceException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//(GenericDispatcher) GenericDispatcher.getLocalDispatcher("default", delegator);

		searchParams.put("viewSize", pageSize);
		searchParams.put("viewIndex", itemStartNumber);
		System.out.println(" performFindList serviceMap " + searchParams);
		return dispatcher.runSync("performFindList", searchParams);
	}

	@Override
	public int getTotalSize() {
		System.out.println("************** TOTAL SIZE " + totalSize);
		return totalSize;
	}

	public void sort(Comparator arg0, boolean arg1) {
		ColumnComparator comparator = (ColumnComparator)arg0;
		comparator.setAsc(arg1);
		for(int i=0;i<_items.size();i++){
			for(int j=0;j<_items.size();j++){
				if(comparator.compare(_items.get(i), _items.get(j)) > 0){
					GenericValue ith = _items.get(i);
					_items.set(i, _items.get(j));
					_items.set(j, ith);
				}
			}
		}
		
		
	}
	public List<GenericValue> getItems(){
		return _items;
	}
}
