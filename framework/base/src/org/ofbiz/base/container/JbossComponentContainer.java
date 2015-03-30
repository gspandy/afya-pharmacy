package org.ofbiz.base.container;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.component.AlreadyLoadedException;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.component.ComponentLoaderConfig;
import org.ofbiz.base.container.ComponentContainer;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

public class JbossComponentContainer extends ComponentContainer {
	
	private static String module =JbossComponentContainer.class.getName();
	public synchronized void loadComponents(String loaderConfig, boolean updateClasspath) throws AlreadyLoadedException, ComponentException {
		if (!loaded) {
			loaded = true;
		} else {
			throw new AlreadyLoadedException("Components already loaded, cannot start");
		}

		List<ComponentConfig> configs = new ArrayList<ComponentConfig>();

		List<ComponentLoaderConfig.ComponentDef> components = ComponentLoaderConfig.getRootComponents(loaderConfig);

		if (components != null) {
			for (ComponentLoaderConfig.ComponentDef def : components) {
				Debug.logInfo("Loading Component Def "+def.location, loaderConfig);
				this.loadComponentFromConfig(def.location,def);
			}
		}
		

		Debug.logInfo("All components loaded", module);
	}

	private void loadComponentFromConfig(String parentLocation,ComponentLoaderConfig.ComponentDef def) {
		if (def.type == ComponentLoaderConfig.SINGLE_COMPONENT) {
			ComponentConfig config = null;
			try {
				Debug.logInfo("Loading components from URL: " +parentLocation+"/"+ def.location  + "/" + ComponentConfig.OFBIZ_COMPONENT_XML_FILENAME, module);
				URL configUrl = Thread.currentThread().getContextClassLoader().getResource(parentLocation+"/"+
						def.location + "/" + ComponentConfig.OFBIZ_COMPONENT_XML_FILENAME);
				if (configUrl != null) {
					config = new ComponentConfig(def.location,parentLocation+"/",configUrl);
					if (UtilValidate.isEmpty(def.name)) {
						def.name = config.getGlobalName();
					}
				}
			} catch (ComponentException e) {
				Debug.logError("Cannot load component : " + def.name + " @ " + def.location + " : " + e.getMessage(), module);
			}
			if (config == null) {
				Debug.logError("Cannot load component : " + def.name + " @ " + def.location, module);
			} else {
				// this.loadComponent(config);
			}
		} else if (def.type == ComponentLoaderConfig.COMPONENT_DIRECTORY) {
			Debug.logInfo("Loading component : " + def.name + " @ " + def.location + " : ", module);
			this.loadComponentDirectory(def.location);
		}
	}

	public void loadComponentDirectory(String location) {
		URL configUrl = null;
		try {
			Debug.logInfo("Loading components from URL: " + location + "/component-load.xml", module);
			configUrl = Thread.currentThread().getContextClassLoader().getResource(location + "/component-load.xml");
			if (configUrl != null) {
				List<ComponentLoaderConfig.ComponentDef> componentsToLoad = ComponentLoaderConfig.getComponentsFromConfig(configUrl);
				if (componentsToLoad != null) {
					for (ComponentLoaderConfig.ComponentDef def : componentsToLoad) {
						this.loadComponentFromConfig(location,def);
					}
				}
			}else{
				Debug.logError("Cannot load component : "+ location + "/component-load.xml", module);
			}
		} catch (ComponentException e) {
			Debug.logError(e, "Unable to load components from URL: " + configUrl.toExternalForm(), module);
		}
	}
}
