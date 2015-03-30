package com.hrms.payroll.composer;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Panel;

import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.util.MessageUtils;

public class PreferenceComposer extends HrmsComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String employeeId;
	private Panel preferencePanel;
	private GenericValue preference;

	public GenericValue getPreference() {
		return preference;
	}

	public void setPreference(GenericValue preference) {
		this.preference = preference;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		employeeId = userLogin.getString("partyId");
		preference = delegator.findOne("Preferences", UtilMisc.toMap("partyId", employeeId), false);
		if(preference == null)
			preference = delegator.makeValidValue("Preferences", UtilMisc.toMap("partyId", employeeId));
	}

	public void onSelect$employeeLookup(Event event) throws Exception {
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), true);
		if(person==null){
			MessageUtils.showInfoMessage("No User found.");
			return;
		}
		preference = delegator.findOne("Preferences", UtilMisc.toMap("partyId", employeeId), false);
		if (preference == null) {
			System.out.println(" No Preference found creating one.");
			preference = delegator.makeValidValue("Preferences", UtilMisc.toMap("partyId", employeeId));
		}
		Caption caption = (Caption) preferencePanel.getFirstChild();
		caption.setLabel("Preference of " + person.getString("firstName") + person.getString("lastName"));
		binder.loadAll();

	}

	public void onClick$savePreference(Event event) throws Exception {
		try {
			delegator.createOrStore(preference);
			MessageUtils.showSuccessMessage();
		} catch (Throwable t) {
			MessageUtils.showErrorMessage();
		}
	}
}
