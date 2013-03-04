/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.core.config;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The root object for the all config.xml model objects.
 * 
 * @author Gorkem Ercan
 * 
 */
public class AbstractConfigObject {

	protected String getNodeAttribute(Node node, String name) {
		NamedNodeMap nodeMap = node.getAttributes();
		if (nodeMap == null) {
			return null;
		}
		Node attribute = nodeMap.getNamedItem(name);
		if (attribute != null) {
			return attribute.getNodeValue();
		}
		return null;
	}
	
	protected String getTextContent(Node node, String name){
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName(name);
		if(nodes.getLength()>0){
			return nodes.item(0).getTextContent();
		}
		return null;
	}

}
