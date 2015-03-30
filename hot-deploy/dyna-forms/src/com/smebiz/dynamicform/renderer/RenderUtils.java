package com.smebiz.dynamicform.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smebiz.dynamicform.FormCategory;
import com.smebiz.dynamicform.input.FormInput;
import com.smebiz.dynamicform.input.renderer.FormInputRenderer;

public final class RenderUtils {

	public static FormCategory root = null;

	public static String format(String s){
		if(s==null)
			return s;
		
		List<String> tokens = getAllTokens(s);
		Map context = new HashMap();
		FormInputRenderer renderer = null;
		
		for(String token:tokens){
			FormInput input = getFormInputFor(token);
			context.clear();
			context.putAll(RendererContext.getRendererContext());
			if(input.getInputRenderer().getArgs()!=null){
				context.put("arguments",input.getInputRenderer().getArgs().split(","));
			}
			renderer = input.getInputRenderer();
			renderer.executeScript(context);
			String result = (String)context.get("result");
			
			s=s.replace("${"+token+"}",result==null?renderer.getDefaultValue():result);
		}
		
		
		return s.replace("\n","<br/>");
	}

	private static FormInput getFormInputFor(String token) {
		FormInput input = null;
		for (FormCategory cat : root.getCategories()) {
			for (FormInput in : cat.getInputs()) {
				if (in.getId().equals(token)) {
					input = in;
					break;
				}
			}
		}
		return input;
	}

	private static List<String> getAllTokens(String s) {

		List<String> tokens = new ArrayList<String>();
		int idx = s.indexOf("${");
		String token = null;
		while (idx != -1) {
			int idx2 = s.indexOf("}");
			token = s.substring(idx + 2, idx2);
			System.out.println("TOKEN " + token);
			s = s.substring(idx2 + 1);
			System.out.println(" NEW STR " + s);
			idx = s.indexOf("${");
			tokens.add(token);
		}
		return tokens;
	}

	public static void main(String[] args) {
		getAllTokens("I AM ${name} how about u Mr.${urname}");
	}

	public static void setCategories(FormCategory categories) {
		root = categories;
	}
}
