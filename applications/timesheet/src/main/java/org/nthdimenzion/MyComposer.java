package org.nthdimenzion;

import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import org.nthdimenzion.common.service.SpringApplicationContext;
import org.nthdimenzion.crud.ICrud;
import org.ofbiz.base.util.UtilMisc;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;

import java.util.Map;

public class MyComposer extends GenericForwardComposer {

    private ICrud crudDao;

    public void doAfterCompose(Component allocateLoanWindow) throws Exception {
        System.out.println(OfbizGateway.getUser());
        System.out.println(OfbizGateway.getUserId());
        System.out.println(OfbizGateway.getDispatcher());
        crudDao = SpringApplicationContext.getBean("crudDao");
        System.out.println(crudDao);
        System.out.println("\n\n\n\n\n  OfbizGateway.getUser()  "+OfbizGateway.getUser());
        // System.out.println("\n\n\n\n\n  OfbizGateway.getDelegator()  "+OfbizGateway.getDelegator());

        Map allDepartment = UtilMisc.toMap("userLogin", OfbizGateway.getUser(), "inputFields", UtilMisc.toMap("roleTypeId","ORGANIZATION_ROLE"), "entityName", "PartyRoleAndPartyDetail");
        Map result = OfbizGateway.getDispatcher().runSync("performFindList",allDepartment);
        System.out.println("All Departments  DepartmentId " + result);


        String loggedInUserPartyId = (String)OfbizGateway.getUser().get("partyId");


        System.out.println ("loggedInUserPartyId " + loggedInUserPartyId);
        Map employeeDepartment = UtilMisc.toMap("userLogin", OfbizGateway.getUser(),
                "inputFields",UtilMisc.toMap("roleTypeIdFrom","ORGANIZATION_ROLE","roleTypeIdTo","EMPLOYEE","partyIdTo",loggedInUserPartyId,"thruDate",null) ,
                "entityName", "PartyRelationshipAndDetail");


        result = OfbizGateway.getDispatcher().runSync("performFindList",employeeDepartment);
        System.out.println("\n\n\n\n Employee Department PartyIdTo takes this and find the employeeDepartment from above " + result);



    }


}
