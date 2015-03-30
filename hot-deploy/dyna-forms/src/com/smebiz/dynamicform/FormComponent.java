package com.smebiz.dynamicform;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.smebiz.dynamicform.renderer.Renderer;

public class FormComponent {
	
	protected Renderer renderer;
	private List<FormComponent> components=new LinkedList<FormComponent>();;

	public void addComponent(FormComponent component) {
		components.add(component);
	}

	public final List<FormComponent> getComponents() {
		return components;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
	
	public final void setComponents(List<FormComponent> components) {
		this.components = components;
	}
	
	public void render() throws IOException {
		this.renderer.encodeBegin();
		this.renderer.encodeChild();
		this.renderer.encodeEnd();
	}

	public Renderer getRenderer() {
		return renderer;
	}

}
