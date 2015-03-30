package com.ndz.zkoss;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;

public class HrmsButton extends Button{
	static final String STYLE = "cursor:pointer ;cursor:hand ;";
	static final String DISABLE_STYLE = "cursor:default ;cursor:hand ;"
		+ "background:none repeat scroll 0 0 Silver";

	public HrmsButton() {
		super();
		setStyle(STYLE);
		addEventListener("onClick", new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				HrmsButton.this.setStyle(STYLE);
			}
		});
	}
	@Override
	public void setDisabled(boolean disabled) {
		super.setDisabled(disabled);
		if(disabled)
			setStyle(DISABLE_STYLE);
		else
			setStyle(STYLE);
	}
}
