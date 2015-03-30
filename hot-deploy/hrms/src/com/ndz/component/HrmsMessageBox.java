package com.ndz.component;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;
import org.zkoss.zk.ui.event.EventListener;

public class HrmsMessageBox extends Messagebox {
	public static void showOk(String message, String title,final String sendRedirect) {
		try {
			Messagebox.show(message, title, Messagebox.OK, Messagebox.NONE, new EventListener() {
				public void onEvent(Event evt) {
					if ("onOK".equals(evt.getName())) {
						Executions.sendRedirect(sendRedirect);
					}
				}
			});
		} catch (InterruptedException e) {
		}
	}
}
