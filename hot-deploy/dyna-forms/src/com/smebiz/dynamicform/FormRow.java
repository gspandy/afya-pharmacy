package com.smebiz.dynamicform;

import java.io.PrintStream;

import com.smebiz.dynamicform.input.FormInput;
import com.smebiz.dynamicform.renderer.HtmlFormRowRenderer;

public class FormRow extends FormComponent{

	private String name;
	
	private FormInput formInput;
	
	public FormInput getFormInput() {
		return formInput;
	}

	public void setFormInput(FormInput formInput) {
		this.formInput = formInput;
	}

	public FormRow() {
		super.renderer = new HtmlFormRowRenderer(this);
	}

	public FormRow(String name) {
		this();
		this.name = name;
	}


	public String getName() {
		return name;
	}

	public void print(PrintStream out) {
		print(out, false);
	}

	public void print(PrintStream out, boolean subFormFlag) {
	}
	
	public void setName(String name) {
		this.name = name;
	}

	

}
