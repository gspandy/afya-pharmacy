package com.ndz.component;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Textbox;

/**
 * @author Nafis
 * 
 */
public class HRMSTextBox extends Textbox{
	private static final long serialVersionUID = 1L;

	public HRMSTextBox() {
		addEventListener("onBlur", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				((Textbox) event.getTarget()).setValue(((Textbox) event.getTarget()).getValue().trim());
				Clients.clearWrongValue(event.getTarget());
			}
		});
	}
}
