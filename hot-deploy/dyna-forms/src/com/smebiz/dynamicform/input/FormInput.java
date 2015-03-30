package com.smebiz.dynamicform.input;

import com.smebiz.dynamicform.input.renderer.FormInputRenderer;


public class FormInput {
	
	public static final String TABULAR = "table";
	
	private String id;
	private String display;
	private String outputType;
	private String columns;
	private FormInputRenderer inputRenderer;
	
	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public FormInputRenderer getInputRenderer(){
		return this.inputRenderer;
	}
	
	public void setFormInputRenderer(FormInputRenderer renderer){
		this.inputRenderer=renderer;
	}
	
	public String getColumns() {
		return columns;
	}

}
