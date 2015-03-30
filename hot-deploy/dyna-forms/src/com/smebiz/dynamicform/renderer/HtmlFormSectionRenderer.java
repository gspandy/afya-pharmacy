package com.smebiz.dynamicform.renderer;

import java.io.IOException;
import java.io.Writer;

import com.smebiz.dynamicform.FormComponent;
import com.smebiz.dynamicform.FormSection;

public class HtmlFormSectionRenderer implements Renderer {

	private FormSection formSection;

	public HtmlFormSectionRenderer(FormSection section) {
		this.formSection = section;
	}

	public void encodeBegin() throws IOException {
		RendererContext context = RendererContext.getRendererContext();
		Writer pw = context.getWriter();
		pw.write("<br/><table width=\"100%\" cellspacing=\"5px\" cellpadding=\"10px\" border=\"1px\">");
	}

	public void encodeChild() throws IOException {
			for (FormComponent row : formSection.getComponents()) {
				row.render();
			}
	}

	public void encodeEnd() throws IOException {
		RendererContext context = RendererContext.getRendererContext();
		Writer pw = context.getWriter();
		pw.write("</table>");
		pw.write("<br/>");
	}
}
