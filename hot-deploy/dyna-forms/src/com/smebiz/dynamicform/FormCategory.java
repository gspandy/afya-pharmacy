package com.smebiz.dynamicform;

import java.util.Collection;
import java.util.LinkedList;

import com.smebiz.dynamicform.input.FormInput;

public class FormCategory {

	private Collection<FormInput> inputs = new LinkedList<FormInput>();

	private Collection<FormCategory> categories = new LinkedList<FormCategory>();

	private String name;
	
	public Collection<FormInput> getInputs() {
		return inputs;
	}

	public void setInputs(Collection<FormInput> inputs) {
		this.inputs = inputs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<FormCategory> getCategories() {
		return categories;
	}

	public void setCategories(Collection<FormCategory> categories) {
		this.categories = categories;
	}

	public void addFormInput(FormInput input) {
		inputs.add(input);
	}
	
	public void addCategory(FormCategory category){
		categories.add(category);
	}
}
