package com.smebiz.dynamicform.input.renderer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.groovy.control.CompilationFailedException;
import org.ofbiz.entity.GenericDelegator;

import com.smebiz.dynamicform.renderer.RendererContext;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

public class FormInputRenderer {

	private String classname;
	private String invoke;
	private String templateName;
	private String scriptFileName;
	private String args;
	private String defaultValue;
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	protected SimpleHash model = new SimpleHash();

	public String getScriptFileName() {
		return scriptFileName;
	}

	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getInvoke() {
		return invoke;
	}

	public void setInvoke(String invoke) {
		this.invoke = invoke;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public void render() {

		RendererContext ctx = RendererContext.getRendererContext();
		executeScript(ctx);

		Configuration cfg = new Configuration();
		Template template;
		try {

			String ofbizHome = System.getProperty("ofbiz.home");
			cfg
					.setDirectoryForTemplateLoading(new File(
							ofbizHome
									+ "/hot-deploy/dyna-forms/webapp/dyna-forms/WEB-INF/templates"));
			template = cfg.getTemplate(getTemplateName());
			model.putAll(ctx);
			template.process(model, ctx.getWriter());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeScript(Map context) {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");
		Binding binding = new Binding();
		binding.setVariable("delegator", delegator);
		binding.setVariable("partyId", context.get("partyId"));
		if(args!=null){
			binding.setVariable("arguments",args.split(","));
		}else
			binding.setVariable("arguments",new String[]{});
		
		String ofbizHome = System.getProperty("ofbiz.home");
		File scriptFile = new File(ofbizHome
				+ "/hot-deploy/dyna-forms/webapp/dyna-forms/WEB-INF/scripts",
				getScriptFileName());
		try {
			ClassLoader parent = getClass().getClassLoader();
			GroovyClassLoader loader = new GroovyClassLoader(parent);
			Class groovyClass = loader.parseClass(scriptFile);
			GroovyObject groovyObject = (GroovyObject) groovyClass
					.newInstance();
			Object[] args = {context};
			groovyObject.invokeMethod("run", args);

		} catch (CompilationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
