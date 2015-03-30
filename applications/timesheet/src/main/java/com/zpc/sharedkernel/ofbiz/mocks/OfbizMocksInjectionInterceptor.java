package com.zpc.sharedkernel.ofbiz.mocks;

import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.DesktopInit;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */

public class OfbizMocksInjectionInterceptor implements DesktopInit {

    private LocalDispatcher dispatcher = new OfbizMockDispatcher();

    public Map<String,Object> userLogin(){
        Map<String,Object> userLogin = new HashMap();
        userLogin.put("userLoginId","admin");
        userLogin.put("partyId","10030");
        return userLogin;
    }

    @Override
    public void init(Desktop desktop, Object request) throws Exception {
        desktop.getSession().setAttribute("userLogin",userLogin());
        Executions.getCurrent().setAttribute("dispatcher", dispatcher);
        Executions.getCurrent().getSession().setAttribute("dispatcher", dispatcher);
        desktop.getSession().setAttribute("security", new MockSecurity());
    }
}
