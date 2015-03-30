package com.ndz.zkoss;

import java.util.Comparator;

import org.ofbiz.entity.GenericValue;

@SuppressWarnings("unchecked")
public class ColumnComparator implements Comparator<GenericValue> {
	
	private boolean asc;
	
	private String columnName; 
	
	public ColumnComparator(String columnName) {
		this.columnName=columnName;
	}
	
	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public int compare(GenericValue arg1, GenericValue arg2) {
		Comparable columnValue1 = (Comparable) arg1.get(columnName);
		Comparable columnValue2 = (Comparable)arg2.get(columnName);
		return asc ? columnValue1.compareTo(columnValue2) : columnValue2.compareTo(columnValue1); 
	}
}
