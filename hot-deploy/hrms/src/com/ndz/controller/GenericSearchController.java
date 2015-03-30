package com.ndz.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.impl.InputElement;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class GenericSearchController extends GenericForwardComposer {

	protected Event event;
	protected Component eventTarget;
	protected GenericValue userLogin;
	protected String componentId;

	public Map createSearchData() {
		this.userLogin = (GenericValue) Executions.getCurrent().getDesktop()
				.getSession().getAttribute("userLogin");

		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//(GenericDispatcher) GenericDispatcher.getLocalDispatcher("default", delegator);

		// "viewSize",
		// pageSize, "viewIndex", itemStartNumber,
		org.ofbiz.security.Security security = (Security) requestScope.get("security");
		
		boolean isAdmin = security.hasPermission("HUMANRES_ADMIN",userLogin);
		Map searchParams=null;
		if(!isAdmin){
		searchParams = prepareContext();
		}
		else{
			searchParams = prepareAdminContext();
		}
		return searchParams;
	}

	private Map prepareContext() {

		System.out.println(" Component Id " + eventTarget.getId());
		System.out.println(" Attributes " + eventTarget.getAttributes());

		Map inputField = new HashMap();

		Collection<Component> comps = eventTarget.getFellowIfAny(this.componentId).getFellows();

		Collection<Component> searchComps = new ArrayList<Component>(10);
		for (Component comp : comps) {
			String id = comp.getId();
			System.out.println(" component id " + id);
			if ((id != null && id.startsWith("macro"))
					|| id.endsWith("_searchfld")) {
				searchComps.addAll(comp.getFellows());
			}
		}

		System.out.println(" Found " + searchComps.size() + " macros.");

		for (Component comp : searchComps) {

			Class klass = (Class) comp.getDefinition().getImplementationClass();
			if (InputElement.class.isAssignableFrom(klass)) {
				String fieldName = comp.getId();
				InputElement elem = (InputElement) comp;
				String fieldValue = elem.getRawText();
				if (StringUtils.isNotBlank(fieldValue)) {
					System.out.println(" Putting Field Name " + fieldName
							+ " and value " + fieldValue);

					if (fieldName.endsWith("_searchfld"))
						fieldName = fieldName.substring(0, fieldName
								.indexOf("_searchfld"));

					inputField.put(fieldName, fieldValue);

				}
			} else if (Listbox.class.isAssignableFrom(klass)) {
				Listbox listbox = (Listbox) comp;
				String fieldName = listbox.getId();
				if (listbox.getSelectedItem() != null) {
					Object fieldValue = listbox.getSelectedItem().getValue();
					System.out.println(" Putting Field Name " + fieldName
							+ " and value " + fieldValue);
					inputField.put(fieldName, fieldValue);
				}
			}

		}
		return inputField;
	}
	private Map prepareAdminContext() {

		System.out.println(" Component Id " + eventTarget.getId());
		System.out.println(" Attributes " + eventTarget.getAttributes());

		Map inputField = new HashMap();

		Collection<Component> comps = eventTarget.getFellowIfAny(
				this.componentId).getFellows();

		Collection<Component> searchComps = new ArrayList<Component>(10);
		for (Component comp : comps) {
			String id = comp.getId();
			System.out.println(" component id " + id);
			if ((id != null && id.startsWith("macro"))
					|| id.endsWith("_1adminfld")) {
				searchComps.addAll(comp.getFellows());
			}
		}

		System.out.println(" Found " + searchComps.size() + " macros.");

		for (Component comp : searchComps) {

			Class klass = (Class) comp.getDefinition().getImplementationClass();
			if (InputElement.class.isAssignableFrom(klass)) {
				String fieldName = comp.getId();
				InputElement elem = (InputElement) comp;
				String fieldValue = elem.getRawText();
				if (StringUtils.isNotBlank(fieldValue)) {
					System.out.println(" Putting Field Name " + fieldName
							+ " and value " + fieldValue);

					if (fieldName.endsWith("_1adminfld"))
						fieldName = fieldName.substring(0, fieldName
								.indexOf("_1adminfld"));

					inputField.put(fieldName, fieldValue);

				}
			} else if (Listbox.class.isAssignableFrom(klass)) {
				Listbox listbox = (Listbox) comp;
				String fieldName = listbox.getId();
				if (listbox.getSelectedItem() != null) {
					Object fieldValue = listbox.getSelectedItem().getValue();
					System.out.println(" Putting Field Name " + fieldName
							+ " and value " + fieldValue);
					inputField.put(fieldName, fieldValue);
				}
			}

		}
		return inputField;
	}
}
