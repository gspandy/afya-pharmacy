package com.nthdimenzion.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Author: Nthdimenzion
 */

public final class Initialise {

    public static ApplicationContext ApplicationContext;

    static {
        init();
    }

    public static void init(){
        ApplicationContext = new ClassPathXmlApplicationContext("/appContext.xml");
    }
}
