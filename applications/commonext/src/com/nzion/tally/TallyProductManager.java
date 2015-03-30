package com.nzion.tally;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transaction;
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
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
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

public class TallyProductManager {

	public static Map retrieveAndStoreProductCategories(DispatchContext dctx, Map context) {
	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	String prodCatalogId = (String) context.remove("prodCatalogId");
	File xmlFile = null;
	Map companyMap = FastMap.newInstance();
	xmlFile = FileUtil.getFile("component://commonext/config/ListOfStockGroup.xml");
	Map xml = UtilMisc.toMap("xmlFile", xmlFile);
	context.put("xml", xml);
	Map<String, Object> results = null;
	String companyName = "";
	try {
		results = dispatcher.runSync("postXML", context);
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (ServiceUtil.isSuccess(results)) {
		String tallyResponse = (String) results.get("tallyResponse");
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(tallyResponse)));
			NodeIterator nl = XPathAPI.selectNodeIterator(doc, "ENVELOPE/BODY/DATA/COLLECTION/STOCKGROUP");
			Node n = null;
			Map createPrimaryProductCategoryCtx = FastMap.newInstance();
			while ((n = nl.nextNode()) != null) {
				createPrimaryProductCategoryCtx.put("categoryName", n.getAttributes().getNamedItem("NAME")
						.getNodeValue());
				createPrimaryProductCategoryCtx.put("description", n.getAttributes().getNamedItem("NAME")
						.getNodeValue());
				createPrimaryProductCategoryCtx.put("userLogin", context.get("userLogin"));
				createPrimaryProductCategoryCtx.put("prodCatalogId", prodCatalogId);
				createPrimaryProductCategoryCtx.put("productCategoryTypeId", "CATALOG_CATEGORY");
				dispatcher.runSync("createProductCategoryAndAddToProdCatalog", createPrimaryProductCategoryCtx);
				createPrimaryProductCategoryCtx.clear();
			}
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
	return results;
	}

	public static Map importUoms(DispatchContext dctx, Map context) {
	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	String prodCatalogId = (String) context.remove("partyId");
	File xmlFile = null;
	Map companyMap = FastMap.newInstance();
	xmlFile = FileUtil.getFile("component://commonext/config/ListOfUnits.xml");
	Map xml = UtilMisc.toMap("xmlFile", xmlFile);
	context.put("xml", xml);
	Map<String, Object> results = null;
	String companyName = "";
	try {
		results = dispatcher.runSync("postXML", context);
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (ServiceUtil.isSuccess(results)) {
		String tallyResponse = (String) results.get("tallyResponse");
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(tallyResponse)));
			NodeIterator nl = XPathAPI.selectNodeIterator(doc, "ENVELOPE/BODY/DATA/COLLECTION/UNIT");
			Node n = null;
			while ((n = nl.nextNode()) != null) {
				String uom = getAttributeValue(n, "NAME");
				GenericValue uomGV = delegator.makeValidValue("Uom",
						UtilMisc.toMap("uomId", uom, "uomTypeId", "OTHER_MEASURE", "description", uom));
				delegator.create(uomGV);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	return new HashMap();
	}

	public static Map importProducts(DispatchContext dctx, Map context) {
	Transaction originalTransaction = null;
	try {
		originalTransaction = TransactionUtil.suspend();
		TransactionUtil.begin(360000);
	} catch (GenericTransactionException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
	LocalDispatcher dispatcher = dctx.getDispatcher();
	String prodCatalogId = (String) context.remove("partyId");
	File xmlFile = null;
	Map companyMap = FastMap.newInstance();
	xmlFile = FileUtil.getFile("component://commonext/config/ListOfStockItems.xml");
	Map xml = UtilMisc.toMap("xmlFile", xmlFile);
	context.put("xml", xml);
	Map<String, Object> results = null;
	String companyName = "";
	try {
		results = dispatcher.runSync("postXML", context);
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (ServiceUtil.isSuccess(results)) {
		String tallyResponse = (String) results.get("tallyResponse");
		tallyResponse= tallyResponse.replace("&#13;","");
		tallyResponse= tallyResponse.replace("&#10;","");
		try{
			String basePath = System.getProperty("ofbiz.home") + File.separator + "Vouchers";
			java.io.File f = new java.io.File(basePath, "Products.xml");
			java.io.FileWriter writer = new java.io.FileWriter(f);
			writer.write(tallyResponse);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(tallyResponse)));
			NodeIterator nl = XPathAPI.selectNodeIterator(doc, "ENVELOPE/BODY/DATA/COLLECTION/STOCKITEM");
			Node n = null;
			while ((n = nl.nextNode()) != null) {
				String productName = getAttributeValue(n, "NAME");
				Node productNode = getProductDetailNode(dispatcher, productName);
				String baseUnit = getTextContent(productNode, "BASEUNITS");
				if (baseUnit != null && baseUnit.trim().startsWith("Not Applicable")) {
					baseUnit = null;
				}
				String parent = getTextContent(productNode, "PARENT");
				BigDecimal sellPrice = getPrice(productNode, "STANDARDCOSTLIST.LIST/RATE");
				BigDecimal buyPrice = getPrice(productNode, "STANDARDPRICELIST.LIST/RATE");

				BigDecimal rateOfVat = getPrice(productNode, "RATEOFVAT");

				GenericValue prodCatGV = null;
				if (!StringUtils.isEmpty(parent)) {
					prodCatGV = delegator.makeValidValue("ProductCategory", UtilMisc.toMap("productCategoryId", parent,
							"productCategoryTypeId", "CATALOG_CATEGORY", "categoryName", parent));
					try {
						delegator.createOrStore(prodCatGV);
					} catch (Exception e) {

					}
				}
				String productId = (String) dispatcher.runSync(
						"createProduct",
						UtilMisc.toMap("userLogin", context.get("userLogin"), "description", productName,
								"internalName", productName, "productName", productName, "productTypeId",
								"Primary".equals(parent) ? "FINISHED_GOOD" : "RAW_MATERIAL", "quantityUomId",
								baseUnit, "primaryProductCategoryId", prodCatGV == null ? null : parent)).get(
						"productId");

				dispatcher.runSync("createProductPrice", UtilMisc.toMap("userLogin", context.get("userLogin"),
						"productId", productId, "productPriceTypeId", "DEFAULT_PRICE", "price", sellPrice,
						"productPricePurposeId", "PURCHASE", "productStoreGroupId", "_NA_", "currencyUomId", "INR"));

				dispatcher.runSync("createProductPrice", UtilMisc.toMap("userLogin", context.get("userLogin"),
						"productId", productId, "productPriceTypeId", "AVERAGE_COST", "price", sellPrice,
						"productPricePurposeId", "PURCHASE", "productStoreGroupId", "_NA_", "currencyUomId", "INR"));

				List<GenericValue> productCategories = delegator.findList("ProductCategory",
						EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "TAX_CATEGORY",
								"rateOfTax", rateOfVat)), null, null, null, false);

				if (productCategories.size() > 0) {
					GenericValue taxCategory = EntityUtil.getFirst(productCategories);
					String taxCategoryId = (String) taxCategory.get("productCategoryId");
					GenericValue taxCatMember = delegator.makeValidValue("ProductCategoryMember", UtilMisc.toMap(
							"productCategoryId", taxCategoryId, "productId", productId, "fromDate",
							UtilDateTime.nowTimestamp()));
					delegator.create(taxCatMember);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	try {
		TransactionUtil.commit(true);
		TransactionUtil.resume(originalTransaction);
	} catch (GenericTransactionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return new HashMap();
	}

	/**
	 * 
	 * @param node
	 * @param xpath
	 * @return if price is not defined in Tally the default value i.e. 1 would be return.
	 * The Product in SME cannot be sold if prices are not available and if its zero.
	 * @throws Exception 
	 *  
	 */
	private static BigDecimal getPrice(Node node, String xpath) throws Exception {
	BigDecimal price = BigDecimal.ONE;
	String priceStr = getTextContent(node, xpath);
	if (!StringUtils.isEmpty(priceStr)) {
		try {
			// Usually the RATE would be like 100/Rolls where Rolls is the BaseUnits.
			int idx = priceStr.indexOf("/");
			if (idx != -1) {
				priceStr = priceStr.substring(0, idx);
			}
			Number num = NumberFormat.getInstance().parse(priceStr.trim());
			price = new BigDecimal(num.doubleValue());
		} catch (NumberFormatException nfe) {
		}
	}
	return price;
	}

	private static Node getProductDetailNode(LocalDispatcher dispatcher, String productName) throws Exception {
	Node n = null;
	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	File xmlFile = FileUtil.getFile("component://commonext/config/ProductInfo.xml");
	Document productDoc = docBuilder.parse(new InputSource(new FileReader(xmlFile)));
	NodeIterator nl = XPathAPI.selectNodeIterator(productDoc, "ENVELOPE/HEADER/ID");
	n = nl.nextNode();
	n.setTextContent(productName);

	Transformer serializer = TransformerFactory.newInstance().newTransformer();
	serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	File productInfoRequest = new File("ProductInfoReq.xml");
	productInfoRequest.deleteOnExit();
	FileOutputStream outputstream = new FileOutputStream(productInfoRequest);
	StreamResult outputStream = new StreamResult(outputstream);
	DOMSource source = new DOMSource(productDoc);
	serializer.transform(source, outputStream);
	outputstream.flush();
	outputstream.close();

	Map xml = UtilMisc.toMap("xmlFile", productInfoRequest);
	Map context = FastMap.newInstance();
	context.put("xml", xml);
	Map<String, Object> results = null;
	String companyName = "";
	try {
		results = dispatcher.runSync("postXML", context);
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (ServiceUtil.isSuccess(results)) {
		String tallyResponse = (String) results.get("tallyResponse");
		tallyResponse = tallyResponse.replace("&#4;", "");
		/*try{
			String basePath = System.getProperty("ofbiz.home") + File.separator + "Vouchers";
			java.io.File f = new java.io.File(basePath, productName+".xml");
			java.io.FileWriter writer = new java.io.FileWriter(f);
			writer.write(tallyResponse);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		Document doc = docBuilder.parse(new InputSource(new StringReader(tallyResponse)));
		nl = XPathAPI.selectNodeIterator(doc, "ENVELOPE/BODY/DATA/TALLYMESSAGE/STOCKITEM");
		n = nl.nextNode();
	}
	return n;
	}

	private static String getAttributeValue(Node node, String string) throws TransformerException {
	String text = null;
	text = node.getAttributes().getNamedItem(string).getNodeValue();
	return text;
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
