package com.nzion.tally;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import sun.awt.datatransfer.DataTransferer.IndexOrderComparator;

public class TallyConnectionManager {

	public static Map postXML(DispatchContext dctx, Map context) {
	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	PostMethod post = null;
	// Get target URL
	// String fileName=(String)context.get("xmlFile");
	String tallyServerURL = "";
	try {
		GenericValue connectionInfo = delegator.findByPrimaryKey("TallyConfiguration", UtilMisc.toMap("tallyId",
				"10005"));
				Map m = ServiceUtil.returnSuccess("Tally Connection Url not available.");
				m.put("tallyResponse","");
		if(connectionInfo==null)return m;
		tallyServerURL = connectionInfo.getString("connectionString");
	} catch (Exception e) {
		return ServiceUtil.returnError(e.getMessage());
	}
	String response = null;
	Map result = new HashMap();
	Map xml = (Map) context.get("xml");
	File xmlFile = (File) xml.get("xmlFile");
	try {
		post = new PostMethod(tallyServerURL);
		post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(xmlFile), xmlFile.length()));
		post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
		HttpClient httpclient = new HttpClient();
		int res = httpclient.executeMethod(post);
		System.out.println("Response status code: " + res);
		System.out.println("Response body: ");
		InputStream is = post.getResponseBodyAsStream();
		Reader in = new BufferedReader(new InputStreamReader(is, post.getRequestCharSet()));
		StringBuffer buffer = new StringBuffer();
		int ch;
		while ((ch = in.read()) > -1) {
			buffer.append((char) ch);
		}
		in.close();
		String responceStr = buffer.toString();
		responceStr.replace(" quot;", "&quot;");
		responceStr.replace("&", "&amp;");
		responceStr.replace("'", "&apos;");
		response = responceStr;
	} catch (java.net.NoRouteToHostException e) {
		System.out.println("Unable to Connect to Tally Server");
		result.put("tallyResponse", "UNABLE TO CONNECT TO TALLY SERVER");
		return result;
		// return ServiceUtil.returnFailure();
	} catch (IOException ex) {
		return ServiceUtil.returnFailure(ex.getMessage());
	} finally {
		post.releaseConnection();
	}

	result.put("tallyResponse", response);
	return result;
	}

	public static Map checkConnection(DispatchContext dctx, Map context) {
	PostMethod post = null;
	String tallyServerURL = (String) context.get("tallyServerURL");
	File file = new File("check/check.xml");
	String s = file.getAbsolutePath();
	File dummyFile = new File(s);
	Map result = new HashMap();
	String response = "";
	try {
		post = new PostMethod(tallyServerURL);
		post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(dummyFile), dummyFile.length()));
		post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
		HttpClient httpclient = new HttpClient();
		int res = httpclient.executeMethod(post);
		System.out.println("Response status code: " + res);
		System.out.println("Response body: ");
		response = post.getResponseBodyAsString();
	} catch (java.net.NoRouteToHostException e) {
		System.out.println("Unable to Connect to Tally Server");
		return ServiceUtil.returnError("TALLY SERVER IS NOT CONNECTED");
	} catch (IOException ex) {
		return ServiceUtil.returnFailure(ex.getMessage());
	} finally {
		post.releaseConnection();
	}
	return ServiceUtil.returnSuccess("TALLY SERVER IS CONNECTED");
	}

	public static Map checkConnect(DispatchContext dctx, Map context) {
	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	PostMethod post = null;
	String tallyServerURL = "";
	try {
		GenericValue connectionInfo = delegator.findByPrimaryKey("TallyConfiguration", UtilMisc.toMap("tallyId",
				"10005"));
		tallyServerURL = connectionInfo.getString("connectionString");
	} catch (Exception e) {
		return ServiceUtil.returnError(e.getMessage());
	}

	File file = new File("check/check.xml");
	String s = file.getAbsolutePath();
	File dummyFile = new File(s);
	Map result = new HashMap();
	String response = "";
	try {
		post = new PostMethod(tallyServerURL);
		post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(dummyFile), dummyFile.length()));
		post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
		HttpClient httpclient = new HttpClient();
		int res = httpclient.executeMethod(post);
		System.out.println("Response status code: " + res);
		System.out.println("Response body: ");
		response = post.getResponseBodyAsString();
	} catch (java.net.NoRouteToHostException e) {
		return ServiceUtil.returnError("Tally Server is not connected");
	} catch (IOException ex) {
		return ServiceUtil.returnFailure(ex.getMessage());
	} finally {
		post.releaseConnection();
	}
	return ServiceUtil.returnSuccess("Tally Server is connected");
	}
}