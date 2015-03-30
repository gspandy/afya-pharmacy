package com.smebiz.dynamicform.renderer;

import java.io.IOException;
import java.io.Writer;

import com.smebiz.dynamicform.FormComponent;
import com.smebiz.dynamicform.FormRow;

public class HtmlFormRowRenderer implements Renderer {

	private FormRow form;

	public HtmlFormRowRenderer(FormRow form) {
		this.form = form;
	}

	public void encodeBegin() throws IOException {
		RendererContext context = RendererContext.getRendererContext();
		Writer pw = context.getWriter();
		pw.write("<tr>");
	}

	public void encodeChild() throws IOException {
		if (form.getComponents() != null) {
			for (FormComponent comp : form.getComponents()) {
				comp.render();
			}
		}
		if (form.getFormInput() != null)
			form.getFormInput().getInputRenderer().render();
	}

	public void encodeEnd() throws IOException {
		RendererContext context = RendererContext.getRendererContext();
		Writer pw = context.getWriter();
		pw.write("</tr>");
	}
}
