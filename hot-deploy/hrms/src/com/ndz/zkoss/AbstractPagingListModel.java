package com.ndz.zkoss;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.AbstractListModel;

public abstract class AbstractPagingListModel<T> extends AbstractListModel {

	private static final long serialVersionUID = 6613208067174831719L;

	private int _startPageNumber;
	private int _pageSize;
	private int _itemStartNumber;
	protected int totalSize = 0;

	// internal use only
	protected List<T> _items = new ArrayList<T>();

	public AbstractPagingListModel(int startPageNumber, int pageSize) throws Exception {
		this(null, startPageNumber, pageSize);
	}

	public AbstractPagingListModel(Map searchParams, int startPageNumber,
			int pageSize) throws Exception {
		super();
		this._startPageNumber = startPageNumber;
		this._pageSize = pageSize;
		this._itemStartNumber = startPageNumber * pageSize;
		Map results = getPageData(searchParams,_startPageNumber, _pageSize);
		if (results != null) {
			 System.out.println(" *********** QUERY "+results.get("queryString"));
			_items = (List<T>) results.get("list");
			totalSize = (Integer) results.get("listSize");
		}
	}

	public abstract int getTotalSize();

	protected abstract Map<String, Object> getPageData(Map searchParams,int itemStartNumber,
			int pageSize)throws Exception;

	public Object getElementAt(int index) {
		return _items.get(index);
	}

	public int getSize() {
		return _items.size();
	}

	public int getStartPageNumber() {
		return this._startPageNumber;
	}

	public int getPageSize() {
		return this._pageSize;
	}

	public int getItemStartNumber() {
		return _itemStartNumber;
	}
}
