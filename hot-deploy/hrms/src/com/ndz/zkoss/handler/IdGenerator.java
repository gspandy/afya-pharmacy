package com.ndz.zkoss.handler;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;

public interface IdGenerator {
	
	public String nextComponentUuid(Desktop desktop, Component comp);
	public String nextPageUuid(Page page);
	public String nextDesktopId(Desktop desktop);


}
