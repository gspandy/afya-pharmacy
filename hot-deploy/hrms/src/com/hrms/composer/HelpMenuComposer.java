package com.hrms.composer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

public class HelpMenuComposer extends HrmsComposer {

	private static final long serialVersionUID = 1L;

	private static final String[] treeItems = new String[]{"employeemgmt", "leavemgmt", "claimmgmt", "loanmgmt", "terminationmgmt", "requisitionmgmt",
			"budgetMgmt", "performancereviewmgmt", "announcementmgmt", "pollmgmt", "timesheetmgmt", "trainingmgmt","requestmgmt"};
	private static final String[] settingstreeItems = new String[]{"globalhrmgmt", "settingspayrollmgmt", "settingsapprisalmgmt", "importgmgmt", "companymgmt"};
	private static final String[] reporttreeItems = new String[]{"hrreportmgmt", "recruitmentreportmgmt", "hrmspayrollreport", "appraisalreportmgmt"};
	
	public void renderHelpDemoTree(Div templateeastDiv){
		Component childWindow = templateeastDiv.getFirstChild();
		Component settingItem = templateeastDiv.getFellow("settingitem");
		Component reportitem = templateeastDiv.getFellow("reportitem");
		Component applicationitem = templateeastDiv.getFellow("applicationitem");
		String pageId = childWindow.getPage().getId();
		for (String s : treeItems) {
			templateeastDiv.getFellow(s).setVisible(pageId.equals(s));
			if(pageId.equals(s)){
				settingItem.setVisible(false);
				reportitem.setVisible(false);
			}
		}
		for (String s1 : settingstreeItems) {
			templateeastDiv.getFellow(s1).setVisible(pageId.equals(s1));
			if(pageId.equals(s1)){
				applicationitem.setVisible(false);
				reportitem.setVisible(false);
			}
		}
		
		for (String s2 : reporttreeItems) {
			templateeastDiv.getFellow(s2).setVisible(pageId.equals(s2));
			if(pageId.equals(s2)){
				applicationitem.setVisible(false);
				settingItem.setVisible(false);
			}
		}
	}

}
