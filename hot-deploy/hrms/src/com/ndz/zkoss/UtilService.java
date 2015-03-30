package com.ndz.zkoss;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.impl.InputElement;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class UtilService {

	public static Map<String,Object> runService(String serviceName, Event event) throws GenericServiceException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		ModelService modelService = dispatcher.getDispatchContext()
				.getModelService(serviceName);
		Set<String> inParamNames = modelService.getInParamNames();

		Map<String, Object> serviceContext = new HashMap<String, Object>();
		for (String inParam : inParamNames) {
			Component comp = event.getTarget().getFellowIfAny(inParam, true);
			Object value = null;
			if (comp instanceof InputElement) {
				InputElement inputElem = (InputElement) comp;
				value = inputElem.getRawValue();
			} else if (comp instanceof Listbox) {
				Listbox listbox = (Listbox) comp;
				if (listbox.getSelectedItem() != null) {
					value = listbox.getSelectedItem().getValue();
				}
			}
			if (value != null)
				serviceContext.put(inParam, value);
		}
		serviceContext = modelService.makeValid(serviceContext,
				ModelService.IN_PARAM);
		serviceContext.put("userLogin", userLogin);
		return dispatcher.runSync(serviceName, serviceContext);
	}
}
