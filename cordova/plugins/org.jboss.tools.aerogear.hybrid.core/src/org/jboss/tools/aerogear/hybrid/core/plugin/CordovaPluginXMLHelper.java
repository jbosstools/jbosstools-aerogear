/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.core.plugin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.jboss.tools.aerogear.hybrid.core.CordovaEngine;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CordovaPluginXMLHelper {
	
	private static final XPathFactory xpathFactory = XPathFactory.newInstance();;
	
	public static Node getPlatformNode(Document document, String platform){
		try {
			XPath xpath = getXPath();
			XPathExpression expression = xpath.compile("./:platform[@name='"+platform+"']");
			Node node =	(Node) expression.evaluate(document.getDocumentElement(), XPathConstants.NODE);
			return node;
		} catch (XPathExpressionException e) {
			HybridCore.log(IStatus.ERROR, "Can not evaluate xpath expression", e);
			return null;
		}
	}

	private static XPath getXPath() {
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {
			
			@Override
			public Iterator getPrefixes(String arg0) {
				return null;
			}
			
			@Override
			public String getPrefix(String arg0) {
				return null;
			}
			
			@Override
			public String getNamespaceURI(String prefix) {
				if (prefix == null) {
		            throw new IllegalArgumentException("No prefix provided!");
		        } 
				if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
		            return "http://www.phonegap.com/ns/plugins/1.0";
				}
				return XMLConstants.NULL_NS_URI;
			}
		});
		return xpath;
	}
	
	public static NodeList getSourceFileNodes(Node node){
		return getNodes(node, "./:source-file");
	}
	
	public static NodeList getResourceFileNodes(Node node){
		return getNodes(node, "./:resource-file");
	}
	
	public static NodeList getHeaderFileNodes(Node node){
		return getNodes(node, "./:header-file");
	}
	
	public static NodeList getAssets(Node node){
		return getNodes(node, "./:asset");
	}
	
	public static NodeList getConfigFileNodes(Node node) {
		return getNodes(node, "./:config-file");
	}	
	
	public static NodeList getLibFileNodes(Node node) {
		return getNodes(node, "./:lib-file");
	}
	
	public static NodeList getFrameworks(Node node){
		return getNodes(node, "./:framework");
	}
		
	private static NodeList getNodes(Node node, String xpathExpression){
		try{
			XPath xpath = getXPath(); 
			XPathExpression expression = xpath.compile(xpathExpression);
			return (NodeList) expression.evaluate(node, XPathConstants.NODESET);
		}
		catch(XPathExpressionException e ){
			HybridCore.log(IStatus.ERROR, "Can not evaluate xpath expression", e);
			return null;
		}
	}
	
	public static String getAttributeValue(Node node, String attribute){
		Assert.isLegal(node !=null, "Null node value");
		NamedNodeMap map = node.getAttributes();
		Node attrib = map.getNamedItem(attribute);
		if(attrib == null ){
			return null;
		}
		return attrib.getNodeValue();
	}
	
	public static String stringifyNode(Node node){
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Source source = new DOMSource(node);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Result result = new StreamResult(out);
			transformer.transform(source, result);
			return out.toString();
			
		} catch (TransformerConfigurationException e) {
			return null;
		} catch (TransformerFactoryConfigurationError e) {
			return null;
		} catch (TransformerException e) {
			return null;
		}
	}

	public static CordovaPlugin createCordovaPlugin(InputStream contents) throws CoreException {
		Document doc = XMLUtil.loadXML(contents,true);
		CordovaPlugin plugin = new CordovaPlugin();
		Element rootNode = doc.getDocumentElement();
		plugin.setId(getAttributeValue(rootNode, "id"));
		plugin.setVersion(getAttributeValue(rootNode, "version"));
		plugin.setAuthor(getChildNodeValue(rootNode, "author"));
		plugin.setDescription(getChildNodeValue(rootNode, "description"));
		plugin.setName(getChildNodeValue(rootNode, "name"));
		plugin.setLicense(getChildNodeValue(rootNode, "license"));
		plugin.setKeywords(getChildNodeValue(rootNode, "keywords"));
		plugin.setInfo(getChildNodeValue(rootNode, "info"));
		//js-modules
		NodeList moduleNodes = getNodes(rootNode, "//:js-module");
		for (int i = 0; i < moduleNodes.getLength(); i++) {
			Node n = moduleNodes.item(i);
			PluginJavaScriptModule module = new PluginJavaScriptModule();
			module.setName(plugin.getId()+"."+getAttributeValue(n, "name"));
			module.setSource(getAttributeValue(n, "src"));
			NodeList childNodes = n.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node item = childNodes.item(j);
				if(item.getNodeName().equals("runs")){
					module.setRuns(true);
				}else
				if(item.getNodeName().equals("merges")){
					module.addMerge(getAttributeValue(item,"target"));
				}else
				if(item.getNodeName().equals("clobbers")){
					module.addClobber(getAttributeValue(item,"target"));
				}	
			}
			plugin.addModule(module);
		}
		// engines
		NodeList nods = rootNode.getElementsByTagName("engines");
		if (nods.getLength()==1) {
			Node enginesNode = nods.item(0);
			NodeList engineNodes = enginesNode.getChildNodes();
			for (int i = 0; i < engineNodes.getLength(); i++) {
				Node engineNode = engineNodes.item(i);
				CordovaEngine engine = new CordovaEngine();
				engine.setName(getAttributeValue(engineNode, "name"));
				engine.setVersion(getAttributeValue(engineNode, "version"));
				plugin.addSupportedEngine(engine);
			}
		}
		return plugin;
	}
	
	private static String getChildNodeValue(Element node, String tagName){
		NodeList nodes = node.getElementsByTagName(tagName);
		if(nodes.getLength()>0){
			return nodes.item(0).getTextContent();
		}
		return null;
	}
	
	
}
