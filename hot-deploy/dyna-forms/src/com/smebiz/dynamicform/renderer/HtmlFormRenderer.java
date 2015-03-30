package com.smebiz.dynamicform.renderer;

import java.io.IOException;
import java.io.Writer;

import com.smebiz.dynamicform.Form;
import com.smebiz.dynamicform.FormComponent;

public class HtmlFormRenderer implements Renderer {

	private Form formGrp;

	public HtmlFormRenderer(Form formGrp) {
		this.formGrp = formGrp;
	}

	public void encodeBegin() throws IOException {
		if (!formGrp.isRenderBodyOnly()) {
			RendererContext context = RendererContext.getRendererContext();
			Writer pw = context.getWriter();
			pw.write("<html><body>");
			pw
					.write("<p><h4 style=\"text-align:center\" >"
							+ formGrp.getName());
			pw.write("<br/> " + RenderUtils.format(formGrp.getDescription()));
			pw.write("</h4></p>");
		}
	}

	public void encodeChild() throws IOException {
		for (FormComponent section : formGrp.getComponents()) {
			section.render();
		}
	}

	public void encodeEnd() throws IOException {
		if (!formGrp.isRenderBodyOnly()) {
			RendererContext context = RendererContext.getRendererContext();
			Writer pw = context.getWriter();
			pw.write("</body></html>");
		}
	}

}
