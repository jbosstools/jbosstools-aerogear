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

import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.FEATURE_ATTR_NAME;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.FEATURE_ATTR_REQUIRED;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * Feature tag on config.xml
 * 
 * @author Gorkem Ercan
 *
 */
public class Feature extends AbstractConfigObject {
	
	private Property<String> name = new Property<String>(FEATURE_ATTR_NAME);
	private Property<Boolean> required = new Property<Boolean>(FEATURE_ATTR_REQUIRED);
	
	Feature(Node node){
		this.itemNode = (Element)node;
		name.setValue(getNodeAttribute(node, null, FEATURE_ATTR_NAME));
		required.setValue(Boolean.parseBoolean(getNodeAttribute(node, null, FEATURE_ATTR_REQUIRED)));
	}

	public String getName() {
		return name.getValue();
	}
	
	public boolean getRequired() {
		if(required.getValue() == null ){
			return false;
		}
		return required.getValue();
	}

	public void setRequired(boolean required) {
		setAttributeValue(itemNode, null, FEATURE_ATTR_REQUIRED, Boolean.toString(required));
		this.required.setValue(Boolean.valueOf(required));
	}

	public void setName(String name) {
		setAttributeValue(itemNode, null, FEATURE_ATTR_NAME, name);
		this.name.setValue(name);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Feature))
			return false;
		if (obj == this ) 
			return true;
		Feature that = (Feature)obj;
		return equalField(that.getName(), this.getName());
	}
	
	@Override
	public int hashCode() {
		if(getName() != null)
			return getName().hashCode();
		return super.hashCode();
	}

}
