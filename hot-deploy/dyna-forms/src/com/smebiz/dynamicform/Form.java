package com.smebiz.dynamicform;

import com.smebiz.dynamicform.renderer.HtmlFormRenderer;

public class Form extends FormComponent {

	private String description;
	private String keywords;
	
	private boolean renderBodyOnly=false;

	public boolean isRenderBodyOnly() {
		return renderBodyOnly;
	}

	public void setRenderBodyOnly(boolean renderBodyOnly) {
		this.renderBodyOnly = renderBodyOnly;
	}

	private String name;

	public Form() {
		super.renderer = new HtmlFormRenderer(this);
	}

	public String getDescription() {
		return description;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void setName(String name) {
		this.name = name;
	}

}
