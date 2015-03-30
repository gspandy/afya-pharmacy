package com.ndz.zkoss.util;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zkplus.databind.TypeConverter;

public class RunTimeFormatter implements TypeConverter {
	static GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	public Object coerceToBean(Object arg0, Component arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object coerceToUi(Object arg0, Component arg1) {
		final Annotation entity = ((ComponentCtrl) arg1).getAnnotation("entity");
		final Annotation field = ((ComponentCtrl) arg1).getAnnotation("field");
		String entityStr = null;
		String fieldStr = null;
		if(arg0 instanceof String){
		if (entity != null && field != null) {
			entityStr = entity.getAttribute("value");
			fieldStr = field.getAttribute("value");
			return getDescription((String)arg0,entityStr,fieldStr);
		}else
			return getStatusItem((String)arg0);
		}
		return arg0;
	}
	
	public static String getDescription(String id,String entity,String field){
		try {
			   return (delegator.findByPrimaryKey(entity,UtilMisc.toMap(field,id))).getString("description") ;
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			return null;
	}
	
	public static String getStatusItem(String id){
		try {
		   return (delegator.findByPrimaryKey("StatusItem",UtilMisc.toMap("statusId",id))).getString("description") ;
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return null;
	}

}
