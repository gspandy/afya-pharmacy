package com.ndz.controller;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class LocationManagementController extends GenericForwardComposer {

	public static void addLocation(Event event) throws GenericServiceException, GenericEntityException, InterruptedException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Component addLocationDiv = event.getTarget();
		String locationName = (String) ((Textbox) addLocationDiv.getFellow("locationName")).getValue();
		String address1 = (String) ((Textbox) addLocationDiv.getFellow("address1")).getValue();
		String address2 = (String) ((Textbox) addLocationDiv.getFellow("address2")).getValue();
		String city = (String) ((Textbox) addLocationDiv.getFellow("city")).getValue();
		Listitem countryItem = (Listitem) ((Listbox) addLocationDiv.getFellow("countryGeoId")).getSelectedItem();
		String country = (String) countryItem.getValue();
		Listitem stateItem = (Listitem) ((Listbox) addLocationDiv.getFellow("stateProvinceGeoId")).getSelectedItem();
		String state = (String) stateItem.getValue();
		String postalCode = (String) ((Textbox) addLocationDiv.getFellow("postalCode")).getValue();

		Map<String, String> postalAddressDetails = UtilMisc.toMap("userLogin", userLogin, "address1", address1, "address2", address2, "city", city, "countryGeoId", country, "stateProvinceGeoId",
				state, "postalCode", postalCode);
		Map result = null;
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		result = dispatcher.runSync("createPostalAddress", postalAddressDetails);
		String contactMechId = (String) result.get("contactMechId");

		Map locationDetails = UtilMisc.toMap("locationId", delegator.getNextSeqId("Location"), "locationName", locationName, "contactMechId", contactMechId, "locationType", "general");
		GenericValue locGv = delegator.makeValue("Location", locationDetails);
		try{
			locGv.create();
		} catch (GenericEntityException e) {
			Messagebox.show("Location name already exists.", "Error", 1, null);
			return;
		}
		Messagebox.show("Location Created", "Success", 1, null);
	}

	public static void editLocation(Event event) throws GenericServiceException, InterruptedException, GenericEntityException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Component addLocationDiv = event.getTarget();
		String locationId = (String) ((Textbox) addLocationDiv.getFellow("locationId")).getValue();
		String contactMechId = (String) ((Textbox) addLocationDiv.getFellow("contactMechId")).getValue();
		String locationName = (String) ((Textbox) addLocationDiv.getFellow("locationName")).getValue();
		String address1 = (String) ((Textbox) addLocationDiv.getFellow("address1")).getValue();
		String address2 = (String) ((Textbox) addLocationDiv.getFellow("address2")).getValue();
		String city = (String) ((Textbox) addLocationDiv.getFellow("city")).getValue();
		Listitem countryItem = (Listitem) ((Listbox) addLocationDiv.getFellow("countryGeoId")).getSelectedItem();
		String country = (String) countryItem.getValue();
		Listitem stateItem = (Listitem) ((Listbox) addLocationDiv.getFellow("stateProvinceGeoId")).getSelectedItem();
		String state = (String) stateItem.getValue();
		String postalCode = (String) ((Textbox) addLocationDiv.getFellow("postalCode")).getValue();

		Map<String, String> postalAddressDetails = UtilMisc.toMap("address1", address1, "address2", address2, "city", city, "countryGeoId", country, "stateProvinceGeoId", state, "postalCode",
				postalCode, "contactMechId", contactMechId);
		// Map result = null;
		// LocalDispatcher dispatcher =
		// GenericDispatcher.getLocalDispatcher("default", delegator);

		GenericValue postalAddGV = delegator.makeValue("PostalAddress", postalAddressDetails);
		delegator.store(postalAddGV);

		// result = dispatcher.runSync("updatePostalAddress",
		// postalAddressDetails);

		List locationDetailsList = delegator.findByAnd("Location", UtilMisc.toMap("locationId", locationId));
		GenericValue locationDetailGv = EntityUtil.getFirst(locationDetailsList);
		Map locationDetails = UtilMisc.toMap("locationId", locationId, "locationName", locationName, "contactMechId", contactMechId, "locationType", "general");
		locationDetailGv.putAll(locationDetails);
		try{
		locationDetailGv.store();
		} catch (GenericEntityException e) {
			Messagebox.show("Location name already exists.", "Error", 1, null);
			return;
		}
		Messagebox.show("Location Updated", "Success", 1, null);

	}
	public static void deleteLocation( final Event event, final GenericValue gv ) throws InterruptedException {
		Messagebox.show("Are You Sure You want to Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent( Event evt ) {
				if ("onYes".equals(evt.getName())) {

					try {
						Component searchPanel = event.getTarget();
						GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
						GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
						String locationId = gv.getString("locationId");
						Map map = UtilMisc.toMap("userLogin", userLogin, "locationId", locationId);
						LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
						Map result=dispatcher.runSync("deleteLocation", map);
						if (result.get("errorMessage")==null)
						{
						System.out.println(result+"\n\n\n\n");
						Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
						Messagebox.show("Deleted Successfully", "Success", 1, null);
						}
						else{
							Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);
							Messagebox.show("The Selected Location is in Use;Can not be Deleted ", "Error", 1, null);
						}
					
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}}
		
		
		

