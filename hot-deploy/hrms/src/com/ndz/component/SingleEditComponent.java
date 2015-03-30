package com.ndz.component;

import java.util.Collection;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class SingleEditComponent extends Window {

	public void onClick() {

		Map attrs = getAttributes();
		System.out.println(" Component Attrs " + attrs);

		Object obj = ((Textbox) getFellow("fieldName")).getValue();
		System.out.println(" Value " + obj);
		this.setVisible(false);

		try {
			System.out.println(" PROFILE " + getFellowIfAny("dynaProfile"));
		} catch (Exception e) {
		}

		Collection<Component> comps = getDesktop().getComponents();
		Window comp = null;
		for (Component c : comps) {
			System.out.println(" Component " + c.getId());
			if ("dynaProfile".equals(c.getId())) {
				comp = (Window) c;
			}
		}

		String labelId = (String) attrs.get("editableFieldId");
		System.out.println(" Editable Field Id " + labelId);
		((Label) comp.getFellowIfAny(labelId)).setValue(obj.toString());
		this.detach();

	}
}
