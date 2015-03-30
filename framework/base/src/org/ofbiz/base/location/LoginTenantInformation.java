/**
 * 
 */
package org.ofbiz.base.location;


/**
 * @author Nafis
 *
 */
public final class LoginTenantInformation {

	private static final ThreadLocal<String> REQUESTTHREADLOCAL = new ThreadLocal<String>();
	
	public static Object getTenantId() {
	return REQUESTTHREADLOCAL.get();
	}

	public static void setTenantId(String id) {
	 REQUESTTHREADLOCAL.set(id);
	}

}
