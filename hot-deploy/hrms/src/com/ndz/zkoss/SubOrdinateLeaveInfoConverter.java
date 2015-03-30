package com.ndz.zkoss;

import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;

/**
 * @author Nafis
 * Feb 1, 2012
 */
public class SubOrdinateLeaveInfoConverter implements TypeConverter{
	private static final long serialVersionUID = 1L;

	@Override
	public Object coerceToBean(Object arg0, Component arg1) {
		return null;
	}

	@Override
	public Object coerceToUi(Object arg0, Component arg1) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
		GenericValue emplLeaveGv = (GenericValue) arg0;
		new Listcell(emplLeaveGv.getString("partyId")).setParent(arg1);
		new Listcell(HrmsUtil.getEmployeeNameByPartyId(emplLeaveGv.getString("partyId"))).setParent(arg1);
		new Listcell(UtilDateTime.formatDate(emplLeaveGv.getTimestamp("fromDate"))).setParent(arg1);
		new Listcell(UtilDateTime.formatDate(emplLeaveGv.getTimestamp("thruDate"))).setParent(arg1);
		try {
			new Listcell(HrmsUtil.getStatusItemDescription(emplLeaveGv.getString("leaveStatusId"))).setParent(arg1);
		} catch (GenericEntityException e) {}
		
		return arg0;
	}
	
	

}
