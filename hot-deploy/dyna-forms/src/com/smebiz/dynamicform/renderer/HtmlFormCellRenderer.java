package com.smebiz.dynamicform.renderer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.smebiz.dynamicform.FormCell;
import com.smebiz.dynamicform.FormComponent;
import com.smebiz.dynamicform.input.renderer.FormInputRenderer;

public class HtmlFormCellRenderer implements Renderer {

	private FormCell formCell;
	
	public HtmlFormCellRenderer(FormCell formCell) {
		this.formCell = formCell;
	}

	public void encodeBegin() throws IOException {
		RendererContext context = RendererContext.getRendererContext();
		Writer pw = context.getWriter();
		if(formCell.getFormInput()==null){
			pw.write("<td>");
		}
	}

	public void encodeChild() throws IOException {
		RendererContext context = RendererContext.getRendererContext();
		Writer pw = context.getWriter();
		if(formCell.getFormInput()!=null){
			
			FormInputRenderer renderer = formCell.getFormInput().getInputRenderer();
			renderer.render();
			return;
		}
		
		if (formCell.getComponents().size()>=1) {
			for(FormComponent comp:formCell.getComponents())
			{
				pw.write("<table width=\"100%\" bordercolor=\"red\"  border=\"2\">");
				comp.render();
				pw.write("</table>");
			}
		}else{
			if(formCell.getDescription()!=null){
				pw.write(RenderUtils.format(formCell.getDescription()));
			}
		}
	}

	public void encodeEnd() throws IOException {
		RendererContext context = RendererContext.getRendererContext();
		Writer pw = context.getWriter();
		if(formCell.getFormInput()==null){
			pw.write("</td>");
		}
	}
}
