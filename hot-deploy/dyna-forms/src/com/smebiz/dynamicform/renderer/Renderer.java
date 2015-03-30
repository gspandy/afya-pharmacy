package com.smebiz.dynamicform.renderer;

import java.io.IOException;

public interface Renderer {
	
	public void encodeBegin() throws IOException;
	public void encodeChild() throws IOException;
	public void encodeEnd() throws IOException;
}
