package com.nzion.tally;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javolution.util.FastList;

import org.apache.xpath.XPathAPI;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TallyLedgerManager {

	public static Map storeGroupAndLedger(DispatchContext dctx, Map context) {
	Transaction originalTransaction = null;
	try {
		originalTransaction = TransactionUtil.suspend();
		TransactionUtil.begin(360000);
	} catch (GenericTransactionException e1) {
		e1.printStackTrace();
	}

	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	File xmlFile = null;
	xmlFile = FileUtil.getFile("component://commonext/config/ListOfAccounts.xml");
	Map xml = UtilMisc.toMap("xmlFile", xmlFile);
	context.put("xml", xml);
	Map<String, Object> results = new HashMap<String, Object>();
	String organizationPartyId = (String) context.remove("partyId");
	String tallyResponse = null;
	try {
		results = dispatcher.runSync("postXML", context);
		if (ServiceUtil.isSuccess(results)) {
			tallyResponse = (String) results.get("tallyResponse");
		}
	} catch (GenericServiceException e) {
		e.printStackTrace();
	}
	tallyResponse = tallyResponse.replace("&#4;","");
	try{
	String basePath = System.getProperty("ofbiz.home") + File.separator + "Vouchers";
	java.io.File f = new java.io.File(basePath, "Ledgers.xml");
	java.io.FileWriter writer = new java.io.FileWriter(f);
	writer.write(tallyResponse);
	writer.flush();
	writer.close();
	}catch(Exception e){
		e.printStackTrace();
	}
	if (tallyResponse == null) {
		return ServiceUtil.returnError("No Response from Tally");
	}
	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = null;
	try {
		docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = null;
		if (tallyResponse != null) doc = docBuilder.parse(new InputSource(new StringReader(tallyResponse)));

		NodeIterator nl = XPathAPI.selectNodeIterator(doc, "ENVELOPE/BODY/IMPORTDATA/REQUESTDATA/TALLYMESSAGE/LEDGER");
		Node xmlNode = null;
		List<String> supplierPartyIds = new ArrayList<String>();
		List<Map<String, String>> taxAuthLedgers = new ArrayList<Map<String, String>>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		List<Visitor> visitors = getAllVisitors(dispatcher, userLogin, organizationPartyId);

		while ((xmlNode = nl.nextNode()) != null) {
			Ledger ledger = new Ledger(xmlNode, userLogin);
			System.out.println(" Migrating Ledger " + ledger);
			for (Visitor visitor : visitors) {
				System.out.println("Visitor " + visitor);
				ledger.accept(visitor);
			}
		}

		// Creating GLAccount with TaxAuthority
		List<GenericValue> suppliers = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", "SUPPLIER"));
		/*List<GenericValue> taxAuthorities = delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthorityType",
				"PURCHASE"));

		for (GenericValue supplier : suppliers) {
			for (GenericValue taxAuthority : taxAuthorities) {
				List<GenericValue> glAccounts = delegator.findByAnd("GlAccount", UtilMisc.toMap("taxClassification",
						taxAuthority.getString("taxClassificationName"),"parentGlAccountId", "2300"));
				if (glAccounts.size() > 0) {
					GenericValue glAccount = glAccounts.get(0);
					dispatcher.runSync("createTaxAuthorityGlAccount", UtilMisc.toMap("userLogin", context
							.get("userLogin"), "glAccountId", glAccount.get("glAccountId"), "taxAuthPartyId",
							taxAuthority.get("taxAuthPartyId"), "taxAuthGeoId", taxAuthority.get("taxAuthGeoId"),
							"organizationPartyId", supplier.getString("partyId")));
				}
			}
		}

		taxAuthorities = delegator.findByAnd("TaxAuthority", UtilMisc.toMap("taxAuthorityType", "SALES"));
		for (GenericValue taxAuthority : taxAuthorities) {
			List<GenericValue> glAccounts = delegator.findByAnd("GlAccount", UtilMisc.toMap("taxClassification",
					taxAuthority.getString("taxClassificationName"),"parentGlAccountId", "2300"));
			if (glAccounts.size() > 0) {
				GenericValue glAccount = glAccounts.get(0);
				String glAccountId = glAccount.getString("glAccountId");
				dispatcher.runSync("createTaxAuthorityGlAccount", UtilMisc.toMap("userLogin", context.get("userLogin"),
						"glAccountId", glAccountId, "taxAuthPartyId", taxAuthority.get("taxAuthPartyId"),
						"taxAuthGeoId", taxAuthority.get("taxAuthGeoId"), "organizationPartyId", organizationPartyId));
				GenericValue gv = delegator.makeValidValue("GlAccountOrganization", UtilMisc.toMap(
						"organizationPartyId", organizationPartyId, "glAccountId", glAccountId));
				delegator.createOrStore(gv);

			}

		}
		*/
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
	results.clear();
	try {
		TransactionUtil.commit(true);
		TransactionUtil.resume(originalTransaction);
	} catch (GenericTransactionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return results;
	}

	private static List<Visitor> getAllVisitors(LocalDispatcher dispatcher, GenericValue userLogin,
			String organizationId) {
	List<Visitor> l = FastList.newInstance();
	l.add(new SundryCreditorVisitor(dispatcher, userLogin, organizationId));
	l.add(new SundryDebtorVisitor(dispatcher, userLogin, organizationId));
	l.add(new PurchaseAccountVisitor(dispatcher, userLogin, organizationId));
	l.add(new SalesAccountVisitor(dispatcher, userLogin, organizationId));
	//l.add(new DutiesAccountVisitor(dispatcher, userLogin, organizationId));
	return l;
	}

}
