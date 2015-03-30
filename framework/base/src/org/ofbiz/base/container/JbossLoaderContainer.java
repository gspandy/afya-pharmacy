/*
 * 
 */
package org.ofbiz.base.container;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.start.Config;
import org.ofbiz.base.start.StartupException;
import org.ofbiz.base.start.StartupLoader;
import org.ofbiz.base.util.Debug;

public class JbossLoaderContainer implements StartupLoader {

    protected List<Container> loadedContainers = new LinkedList<Container>();
    protected String configFile = null;
    public static Container rmiLoadedContainer = null; // used in Geronimo/WASCE
                                                       // to allow to
                                                       // deregister
    public static final String module = JbossLoaderContainer.class.getName();

    public void load(Config config, String[] args) throws StartupException {
        this.configFile = config.containerConfig;
        System.out.println(this.configFile);
        Collection<ContainerConfig.Container> containers = null;
        try {
            containers = ContainerConfig.getContainers(configFile);
        } catch (ContainerException e) {
            throw new StartupException(e);
        }
        if (containers != null) {
            for (ContainerConfig.Container containerCfg : containers) {
                Container tmpContainer = loadContainer(containerCfg, args);
                loadedContainers.add(tmpContainer);

                // This is only used in case of OFBiz running in Geronimo or
                // WASCE. It allows to use the RMIDispatcher
                if (containerCfg.name.equals("rmi-dispatcher") && configFile.equals("limited-containers.xml")) {
                    try {
                        ContainerConfig.Container.Property initialCtxProp = containerCfg.getProperty("use-initial-context");
                        String useCtx = initialCtxProp == null || initialCtxProp.value == null ? "false" : initialCtxProp.value;
                        if (!useCtx.equalsIgnoreCase("true")) {
                            // system.setProperty("java.security.policy",
                            // "client.policy"); maybe used if needed...
                            if (System.getSecurityManager() == null) { // needed
                                // by
                                // WASCE
                                // with
                                // a
                                // client.policy
                                // file.
                                System.setSecurityManager(new java.rmi.RMISecurityManager());
                            }
                            tmpContainer.start();
                            rmiLoadedContainer = tmpContainer; // used in
                            // Geronimo/WASCE
                            // to allow to
                            // deregister
                        }
                    } catch (ContainerException e) {
                        throw new StartupException("Cannot start() " + tmpContainer.getClass().getName(), e);
                    } catch (java.lang.AbstractMethodError e) {
                        throw new StartupException("Cannot start() " + tmpContainer.getClass().getName(), e);
                    }
                }
            }
        }
    }

    private Container loadContainer(ContainerConfig.Container containerCfg, String[] args) throws StartupException {
        // load the container class
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            Debug.logWarning("Unable to get context classloader; using system", module);
            loader = ClassLoader.getSystemClassLoader();
        }
        Class containerClass = null;
        try {
            containerClass = loader.loadClass(containerCfg.className);
        } catch (ClassNotFoundException e) {
            throw new StartupException("Cannot locate container class", e);
        }
        if (containerClass == null) {
            throw new StartupException("Component container class not loaded");
        }

        // create a new instance of the container object
        Container containerObj = null;
        try {
            containerObj = (Container) containerClass.newInstance();
        } catch (InstantiationException e) {
            throw new StartupException("Cannot create " + containerCfg.name, e);
        } catch (IllegalAccessException e) {
            throw new StartupException("Cannot create " + containerCfg.name, e);
        } catch (ClassCastException e) {
            throw new StartupException("Cannot create " + containerCfg.name, e);
        }

        if (containerObj == null) {
            throw new StartupException("Unable to create instance of component container");
        }

        // initialize the container object
        try {
            containerObj.init(args, configFile);
        } catch (ContainerException e) {
            throw new StartupException("Cannot init() " + containerCfg.name, e);
        } catch (java.lang.AbstractMethodError e) {
            throw new StartupException("Cannot init() " + containerCfg.name, e);
        }

        return containerObj;
    }

    public void start() throws StartupException {
        // TODO Auto-generated method stub
    }

    public void unload() throws StartupException {
        // TODO Auto-generated method stub
    }
}
