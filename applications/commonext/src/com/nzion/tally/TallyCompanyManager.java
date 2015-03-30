package com.nzion.tally;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javolution.util.FastMap;

import org.apache.axis.utils.StringUtils;
import org.apache.xpath.XPathAPI;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TallyCompanyManager {

	public static Map getCompanyInfo(DispatchContext dctx, Map context) {
	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	File xmlFile = null;
	Map<String,Object> companyMap = FastMap.newInstance();
	xmlFile = FileUtil.getFile("component://commonext/config/ListOfCompanies.xml");
	Map xml = UtilMisc.toMap("xmlFile", xmlFile);
	context.put("xml", xml);
	Map<String, Object> results = new HashMap();
	String companyName = "";
	try {
		results = dispatcher.runSync("postXML", context);
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String tallyResponse = (String) results.get("tallyResponse");
	if (!StringUtils.isEmpty(tallyResponse)) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(tallyResponse)));
			NodeIterator nl = XPathAPI.selectNodeIterator(doc, "ENVELOPE/COMPANYNAME.LIST/COMPANYNAME");
			Node n = nl.nextNode();
			if (n != null) {
				companyName = n.getTextContent();
			}
			tallyResponse = getCompanyInfoFromTally(dispatcher, companyName);
			doc = docBuilder.parse(new InputSource(new StringReader(tallyResponse)));
			nl = XPathAPI.selectNodeIterator(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/ADDRESS.LIST/ADDRESS");
			companyMap.put("companyName", companyName);
			int i = 1;
			while ((n = nl.nextNode()) != null) {
				String s = n.getTextContent();
				companyMap.put("address_" + i, s);
				i++;
			}
			String incomeTaxNumber = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/INCOMETAXNUMBER");
			String salesTaxNumber = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/SALESTAXNUMBER");
			String interstateServiceTaxNumber = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/INTERSTATESTNUMBER");
			String stateName = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/STATENAME");
			String zipCode = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/PINCODE");
			String phoneNumber = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/PHONENUMBER");
			String email = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/EMAIL");
			String country = getTextContent(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/COMPANY/COUNTRYNAME");

		
			if (!StringUtils.isEmpty(country)) {
				List l = delegator.findByAnd("Geo", UtilMisc.toMap("geoTypeId", "COUNTRY", "geoName", country));
				GenericValue geoGV = EntityUtil.getFirst(l);
				if (geoGV != null)
					country = (String) geoGV.get("geoId");
			} else {
				country = "IND";
			}

			if (StringUtils.isEmpty(zipCode)) zipCode = "000000";
			if (!StringUtils.isEmpty(stateName)) {
				List l = delegator.findByAnd("Geo", UtilMisc.toMap("geoTypeId", "STATE", "geoName", stateName));
				GenericValue geoGV = EntityUtil.getFirst(l);
				if (geoGV != null)
					stateName = (String) geoGV.get("geoId");
				else
					stateName = "IN-KA";
			}
			
			companyMap.put("USER_STATE", stateName);
			companyMap.put("USER_POSTAL_CODE",zipCode);
			companyMap.put("USER_WORK_CONTACT",phoneNumber);
			companyMap.put("USER_EMAIL",email);
			companyMap.put("interstateServiceTaxNumber",interstateServiceTaxNumber);
			companyMap.put("salesTaxNumber",salesTaxNumber);
			companyMap.put("incomeTaxNumber",incomeTaxNumber);
			companyMap.put("USER_COUNTRY",country);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	results.clear();
	results.put("companyMap", companyMap);
	return results;
	}

	private static String getCompanyInfoFromTally(LocalDispatcher dispatcher, String companyName) throws Exception {
	Map<String, Object> results = null;
	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = null;
	docBuilder = docBuilderFactory.newDocumentBuilder();
	Document companyDoc = docBuilder.parse(FileUtil.getFile("component://commonext/config/CompanyInfo.xml"));
	NodeIterator nl = XPathAPI.selectNodeIterator(companyDoc, "ENVELOPE/HEADER/ID");
	Node n = nl.nextNode();
	n.setTextContent(companyName);
	Transformer serializer = TransformerFactory.newInstance().newTransformer();
	serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	File companyInfoRequest = new File("CompanyInfoReq.xml");
	FileOutputStream companyInfoStream = new FileOutputStream(companyInfoRequest);
	StreamResult outputStream = new StreamResult(companyInfoStream);
	DOMSource source = new DOMSource(companyDoc);
	serializer.transform(source, outputStream);
	companyInfoStream.flush();
	companyInfoStream.close();
	Map context = FastMap.newInstance();
	Map xml = UtilMisc.toMap("xmlFile", companyInfoRequest);
	context.put("xml", xml);
	results = dispatcher.runSync("postXML", context);
	return (String) results.get("tallyResponse");
	}

	private static String getTextContent(Node doc, String string) throws TransformerException {
	String text = null;
	NodeIterator nodeIterator = XPathAPI.selectNodeIterator(doc, string);
	Node node = nodeIterator.nextNode();
	if (node != null) {
		text = node.getTextContent();
	}
	return text;
	}
}
