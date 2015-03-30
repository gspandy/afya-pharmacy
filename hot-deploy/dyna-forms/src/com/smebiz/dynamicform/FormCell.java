package com.smebiz.dynamicform;

import java.io.PrintStream;

import com.smebiz.dynamicform.input.FormInput;
import com.smebiz.dynamicform.renderer.HtmlFormCellRenderer;

public class FormCell extends FormComponent {

	private String description;

	private FormInput input;

	public FormInput getFormInput() {
		return input;
	}

	public void setFormInput(FormInput input) {
		this.input = input;
	}

	public FormCell() {
		this.renderer = new HtmlFormCellRenderer(this);
	}

	public FormCell(String desc) {
		this.renderer = new HtmlFormCellRenderer(this);
		this.description = desc;
	}

	public void print(PrintStream pw) {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
