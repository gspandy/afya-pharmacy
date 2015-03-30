package com.smebiz.dynamicform.renderer;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;

public final class RendererContext extends HashMap<String,Object>{
	
	private final static ThreadLocal<RendererContext> tl = new ThreadLocal<RendererContext>();

	private OutputStream oos;
	private Writer pw;

	public OutputStream getOutputStream() {
		return oos;
	}

	public Writer getWriter() {
		return pw;
	}
	
	public void setWriter(Writer writer){
		this.pw=writer;
	}

	
	public static RendererContext getRendererContext() {
		return tl.get();
	}

	public static void setRendererContext(RendererContext ctx) {
		if (ctx != null) {
			tl.set(ctx);
		}
	}
}
