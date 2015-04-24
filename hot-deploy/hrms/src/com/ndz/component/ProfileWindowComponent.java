package com.ndz.component;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.api.Vbox;

public class ProfileWindowComponent extends GenericForwardComposer {

      @Override
      public void doAfterCompose(Component comp) throws Exception {
            super.doAfterCompose(comp);

            GenericValue userLogin = (GenericValue) comp.getDesktop().getSession()
                        .getAttribute("userLogin");
            
            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
            if (delegator == null)
                  GenericDelegator.getGenericDelegator("default");

            GenericValue person = userLogin.getRelatedOne("Person");
            
            
            Map <String, Object> allFields = person.getAllFields();
            
            int count = 0;

            Collection<Component> leftHalf = new LinkedList<Component>();
            Collection<Component> rightHalf = new LinkedList<Component>();
            Label label = null;
            Label labelDesc = null;
            for (String key : allFields.keySet()) {

                  Object obj = allFields.get(key);
                  label = new Label();
                  labelDesc = new Label();
                  labelDesc.setValue(key);
                  label.setValue(obj != null ? obj.toString() : "");
                  Hbox hbox = new Hbox(new Component[] { labelDesc, label });
                  if (count % 2 == 0) {
                        leftHalf.add(hbox);
                  } else {
                        rightHalf.add(hbox);
                  }
                  count++;

            }

            Vbox vbox_1 = new org.zkoss.zul.Vbox(leftHalf
                        .toArray(new Component[leftHalf.size()]));
            Vbox vbox_2 = new org.zkoss.zul.Vbox(rightHalf
                        .toArray(new Component[rightHalf.size()]));
            comp.appendChild(vbox_1);
            comp.appendChild(vbox_2);
      }
}

