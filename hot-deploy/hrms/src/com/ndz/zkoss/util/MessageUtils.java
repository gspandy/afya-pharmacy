package com.ndz.zkoss.util;

import org.zkoss.zul.Messagebox;

public class MessageUtils {

	public static void showSuccessMessage() throws Exception {
		Messagebox.show("Action executed successfully", "Sucess", 1, "z-msgbox z-msgbox-imformation");
		// Messagebox.show("Action was successfull", "Success", Messagebox.OK,
		// "", new EventListener() {
		// public void onEvent(Event event) {
		// Timer timer = new Timer();
		// timer.setRepeats(false);
		// timer.setDelay(10);
		// }
		// });
	}

	public static void showErrorMessage() throws Exception {
		Messagebox.show("Action failed.", "Error", 1, "z-msgbox z-msgbox-error");
	}
	
	public static void showErrorMessage(String message) throws Exception {
		Messagebox.show(message, "Error", 1, "z-msgbox z-msgbox-error");
	}

	
	public static void showInfoMessage(String msg) throws Exception {
		Messagebox.show(msg, "Information", 1, "z-msgbox z-msgbox-imformation");
	}

	
}
