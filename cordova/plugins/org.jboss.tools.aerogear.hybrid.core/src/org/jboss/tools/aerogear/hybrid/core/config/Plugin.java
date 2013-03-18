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

import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.NS_PHONEGAP_1_0;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.PARAM_ATTR_NAME;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.PARAM_ATTR_VALUE;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.PLUGIN_ATTR_NAME;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.PLUGIN_ATTR_VERSION;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.PLUGIN_PARAM_TAG;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Plugin tag for config.xml
 * 
 * @author Gorkem Ercan
 *
 */
public class Plugin extends AbstractConfigObject{
	
	private Property<String> name = new Property<String>(PLUGIN_ATTR_NAME);
	private Property<String> version = new Property<String>(PLUGIN_ATTR_VERSION);
	private Property<HashMap<String, String>> params = new Property<HashMap<String, String>>(PLUGIN_PARAM_TAG); 
	
	Plugin( Node node ){
		itemNode = (Element)node;
		name.setValue(getNodeAttribute(node, null, PLUGIN_ATTR_NAME));
		version.setValue(getNodeAttribute(node, null, PLUGIN_ATTR_VERSION));
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName(PLUGIN_PARAM_TAG);
		if (nodes.getLength() > 0) {
			HashMap<String, String> props = new HashMap<String, String>(nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				String name = getNodeAttribute(nodes.item(i), null, PARAM_ATTR_NAME);
				String value = getNodeAttribute(nodes.item(i), null, PARAM_ATTR_VALUE);
				props.put(name, value);
			}
			params.setValue(props);
		}else{
			params.setValue(null);
		}
	}

	public String getName() {
		return name.getValue();
	}

	public String getVersion() {
		return version.getValue();
	}

	public Map<String, String> getParams() {
		return params.getValue();
	}
	
	public void setName(String name) {
		this.name.setValue(name);
		setAttributeValue(itemNode, NS_PHONEGAP_1_0, PLUGIN_ATTR_NAME, name);
	}

	public void setVersion(String version) {
		this.version.setValue(version);
		setAttributeValue(itemNode, NS_PHONEGAP_1_0, PLUGIN_ATTR_VERSION, version);
	}
	
	public void addParam(String name, String value){
		HashMap<String, String> props = new HashMap<String, String>();
		if(params.getValue() != null ){
			props.putAll(params.getValue());
		}
		props.put(name, value);
		Document doc = itemNode.getOwnerDocument();
		Element el = doc.createElementNS(NS_PHONEGAP_1_0, PLUGIN_PARAM_TAG);
		el.setAttributeNS(NS_PHONEGAP_1_0, PARAM_ATTR_NAME, name);
		el.setAttribute(PARAM_ATTR_VALUE, value);
		itemNode.appendChild(el);
		
		params.setValue(props);
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Plugin ))
			return false;
		if(obj == this )
			return true;
		Plugin that = (Plugin)obj;
		return equalField(that.getName(), this.getName()) && 
				equalField(that.getVersion(), this.getVersion());
		}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(getName() != null )
			hash*= getName().hashCode();
		if(getVersion() != null )
			hash *= getVersion().hashCode();
		return hash;
	}
	
}
