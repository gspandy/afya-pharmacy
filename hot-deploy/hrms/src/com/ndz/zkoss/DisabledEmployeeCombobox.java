package com.ndz.zkoss;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ASUS on 12-Feb-15.
 */
public class DisabledEmployeeCombobox extends Combobox {

    public DisabledEmployeeCombobox(){
        refresh("");
    }

    public DisabledEmployeeCombobox(String value){
       refresh(value);
    }

    public void setValue(String value) {
        super.setValue(value);
        refresh( value); // refresh the child comboitems
    }
    public void onChanging(InputEvent evt) {
        if (!evt.isChangingBySelectBack())
            refresh( evt.getValue());
    }

    private void refresh( String value) {
        if (StringUtils.isEmpty(value) && value.length() < 3)
            return;

        //List disabledPartyId =  new ArrayList();
        GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
        try {
            /*List employeeIdCreatedUsingDisabledEmployee = new ArrayList();
            EntityCondition previousEmployeeIdCond = EntityCondition.makeCondition("previousEmployeeId",EntityOperator.NOT_EQUAL,null);
            List<GenericValue> employeeCreatedUsingDisabledEmployeeIdList = delegator.findList("Person", previousEmployeeIdCond, null, null, null, false);
            for(GenericValue employeeCreatedUsingDisabledEmployeeId:employeeCreatedUsingDisabledEmployeeIdList){
                employeeIdCreatedUsingDisabledEmployee.add(employeeCreatedUsingDisabledEmployeeId.getString("previousEmployeeId"));
            }*/


            /*EntityCondition roleTypeIdFrom = EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"ORGANIZATION_ROLE");
            EntityCondition roleTypeIdTo = EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.IN, Arrays.asList("EMPLOYEE","MANAGER"));
            EntityCondition thruDate = EntityCondition.makeCondition("thruDate",EntityOperator.NOT_EQUAL,null);
            List conditions = Arrays.asList(roleTypeIdFrom,roleTypeIdTo,thruDate);
            List<GenericValue> employmentList = delegator.findList("Employment", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
            for(GenericValue employment:employmentList){
                if(!employeeIdCreatedUsingDisabledEmployee.contains(employment.getString("partyIdTo"))){
                    disabledPartyId.add(employment.getString("partyIdTo"));
                }
            }
            if(UtilValidate.isEmpty(disabledPartyId)){
                return;
            }*/
            String likeString = "%" + value.toLowerCase() + "%";

            EntityCondition ec1 = EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "Y");
            EntityCondition ec2 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));
            EntityCondition ec3 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));
            EntityCondition ec4 = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityComparisonOperator.LIKE, EntityFunction.UPPER(likeString));
            EntityCondition orCondition = EntityCondition.makeCondition(UtilMisc.toList(ec2, ec3, ec4), EntityJoinOperator.OR);
            EntityCondition condition = EntityCondition.makeCondition(ec1, orCondition);
            List<GenericValue> employees = delegator.findList("Person", condition, null, null, null, false);
            if(UtilValidate.isEmpty(employees)){
                return;
            }
            Iterator<Comboitem> it = getItems().iterator();
            for (GenericValue gv : employees) {
                Comboitem ci = null;
                if (it != null && it.hasNext()) {
                    ci = ((Comboitem) it.next());
                } else {
                    it = null;
                    ci = new Comboitem();
                }
                ci.setValue(gv.getString("partyId"));
                ci.setLabel(gv.getString("partyId"));
                ci.setDescription(gv.getString("firstName") + " " + gv.getString("lastName"));
                ci.setParent(this);
            }
            // Remove the extra ones
            while (it != null && it.hasNext()) {
                it.next();
                it.remove();
            }

            this.open();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }




    }
}
