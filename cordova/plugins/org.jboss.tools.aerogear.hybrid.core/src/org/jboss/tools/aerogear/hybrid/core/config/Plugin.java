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

import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Plugin extends AbstractConfigObject{
	
	private String name;
	private String version;
	private Properties params;
	
	Plugin( Node node ){
		setName(getNodeAttribute(node, "name"));
		setVersion(getNodeAttribute(node, "version"));
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName("param");
		for( int i=0; i <nodes.getLength(); i++ ){
			String name = getNodeAttribute(nodes.item(i), "name");
			String value = getNodeAttribute(nodes.item(i),"value");
			params.put(name, value);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public Properties getParams() {
		return params;
	}
}
