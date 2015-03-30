package com.smebiz.dynamicform;

import java.io.PrintStream;

import com.smebiz.dynamicform.renderer.HtmlFormSectionRenderer;

public class FormSection extends FormComponent{

	private String description;

	public FormSection() {
		this.renderer = new HtmlFormSectionRenderer(this);
	}

	public FormSection(String desc) {
		this();
		this.description = desc;
	}

	public String getDescription() {
		return description;
	}

	public void print(PrintStream os) {
		os.println(description);
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
