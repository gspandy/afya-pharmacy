package com.zpc.timesheet.view;

import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

/**
 * Author: Nthdimenzion
 */

public class DummyVM {

    @Init
    public void init(){
        System.out.println("Inside Dummy");
    }

    @Getter
    @Setter
    private String hello = "Hello World";

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view){
        Selectors.wireComponents(view, this, false);
    }


    @Command
    public void save(){
        System.out.println("Iam saving " + hello);
    }

}
