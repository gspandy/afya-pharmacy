package com.nzion.tally;

import static com.nzion.tally.TallyUtil.getTextContent;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.axis.utils.StringUtils;
import org.ofbiz.entity.GenericValue;
import org.w3c.dom.Node;

public class Ledger implements Visitable {

	private final Node xmlNode;
	private String parentLedger;
	private String ledgerName;
	private String taxClassification;
	private BigDecimal taxRate;
	private String taxType;
	private String taxSubType;
	private final GenericValue userLogin;
	
	public Ledger(Node xmlNode, GenericValue userLogin) {
	this.xmlNode = xmlNode;
	this.userLogin = userLogin;
	ledgerName = xmlNode.getAttributes().getNamedItem("NAME").getNodeValue();
	parentLedger = getTextContent(xmlNode, "PARENT");
	taxClassification = getTextContent(xmlNode, "TAXCLASSIFICATIONNAME");
	taxType = getTextContent(xmlNode, "TAXTYPE");
	String rateOfTax = getTextContent(xmlNode, "RATEOFTAXCALCULATION");
	taxSubType = (taxClassification.contains("Purchase") || taxClassification.contains("Input")) ? "PURCHASE" : "SALES";
	taxRate = null;
	if (!StringUtils.isEmpty(rateOfTax)) {
		try {
			Number num = NumberFormat.getInstance().parse(rateOfTax.trim());
			taxRate = new BigDecimal(num.doubleValue());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println("Warn:  [" + ledgerName + "] Cannot parse the Tax Rate [" + rateOfTax + "].");
		} catch (ParseException e) {
			System.err.println("Warn:  [" + ledgerName + "] Cannot parse the Tax Rate [" + rateOfTax + "].");
			e.printStackTrace();
		}
	}
	}

	@Override
	public void accept(Visitor visitor) throws Exception {
	visitor.visit(this);
	}

	public String getParentLedger() {
	return parentLedger;
	}

	public void setParentLedger(String parentLedger) {
	this.parentLedger = parentLedger;
	}

	public String getLedgerName() {
	return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
	this.ledgerName = ledgerName;
	}

	public String getTaxClassification() {
	return taxClassification;
	}

	public void setTaxClassification(String taxClassification) {
	this.taxClassification = taxClassification;
	}

	public BigDecimal getTaxRate() {
	return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
	this.taxRate = taxRate;
	}

	public String getTaxType() {
	return taxType;
	}

	public void setTaxType(String taxType) {
	this.taxType = taxType;
	}

	public String getTaxSubType() {
	return taxSubType;
	}

	public void setTaxSubType(String taxSubType) {
	this.taxSubType = taxSubType;
	}

	public Node getXmlNode() {
	return xmlNode;
	}

	public GenericValue getUserLogin() {
	return userLogin;
	}

	@Override
	public String toString() {
	StringBuilder buffer = new StringBuilder("Ledger [");
	buffer.append("Ledger Name =").append(ledgerName);
	buffer.append(" Tax Classification = ").append(taxClassification);
	buffer.append(" Tax Type =").append(taxType);
	buffer.append(" Rate of Tax = ").append(taxRate);
	buffer.append(" Tax SubType=").append(taxSubType);
	return buffer.toString();
	}
}
