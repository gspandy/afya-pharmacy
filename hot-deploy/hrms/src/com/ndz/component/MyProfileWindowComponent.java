package com.ndz.component;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.api.Vbox;
public class MyProfileWindowComponent extends GenericForwardComposer {
	 
	public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        GenericValue userLogin = (GenericValue) comp.getDesktop().getSession()
        .getAttribute("userLogin");
       
        String strfname=null;

        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        if (delegator == null)
            GenericDelegator.getGenericDelegator("default");
        GenericValue person = userLogin.getRelatedOne("Person");
        Map <String, Object> allFields = person.getAllFields();
        strfname=(String) allFields.get("firstName");
        
        

	}

}
