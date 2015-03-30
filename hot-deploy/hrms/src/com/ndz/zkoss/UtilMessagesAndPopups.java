package com.ndz.zkoss;

import org.zkoss.zul.Messagebox;
import com.ndz.zkoss.MultiLineMessageBox;

public class UtilMessagesAndPopups {

	public static final String TITLE = "Ecosmos - Health Care";

	public static void showSuccess() {
		showSuccess("Operation Completed Successfully");
	}

	public static void showSuccess(String message) {
		display(message, Messagebox.OK, Messagebox.INFORMATION);
	}

	public static void showMessage(String message) {
		display(message, Messagebox.OK, Messagebox.EXCLAMATION);
	}

	public static void showError(String message) {
		display(message, MultiLineMessageBox.OK, Messagebox.ERROR);
	}

	public static void displayError(String message) {
		display(message, MultiLineMessageBox.OK, MultiLineMessageBox.ERROR);
	}

	public static void displaySuccess() {
		displaySuccess("Operation Completed Successfully");
	}

	public static void displaySuccess(String message) {
		display(message, MultiLineMessageBox.OK,
				MultiLineMessageBox.INFORMATION);
	}

	private static void display(String message, int buttons, String icon) {
		MultiLineMessageBox.doSetTemplate();
		try {
			MultiLineMessageBox.show(message, TITLE, buttons, icon, true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}