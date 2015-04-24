package com.ndz.zkoss;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.InputElement;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			Textbox box = new Textbox();
			Class implClass = (Class)box.getDefinition().getImplementationClass();
			
			System.out.println(implClass.isAssignableFrom(InputElement.class));
			System.out.println(InputElement.class.isAssignableFrom(implClass));
			
			Listbox listbox = new Listbox();
			implClass = (Class)listbox.getDefinition().getImplementationClass();
			System.out.println(Listbox.class.isAssignableFrom(implClass));
			

	}

}
