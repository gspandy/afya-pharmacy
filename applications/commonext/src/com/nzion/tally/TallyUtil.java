package com.nzion.tally;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public class TallyUtil {

	public static String getTextContent(Node node, String nodeName) {
	String text = null;
	NodeIterator nodeIterator = null;
	try {
		nodeIterator = XPathAPI.selectNodeIterator(node, nodeName);
		node = nodeIterator.nextNode();
		if (node != null) {
			text = node.getTextContent();
		}
	} catch (TransformerException e) {
		e.printStackTrace();
	}
	return text;
	}

}
