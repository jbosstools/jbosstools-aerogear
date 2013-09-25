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
package org.jboss.tools.aerogear.hybrid.ui.internal;


import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.*;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;

public class CordovaPluginProperties implements IPropertySource {
	
	private CordovaPlugin source;
	private static IPropertyDescriptor[] descriptors;
	
	static{
		descriptors = new IPropertyDescriptor[6];
		descriptors[0] = createPropertyDescriptor(PLGN_PROPERTY_NAME, "Name");
		descriptors[1] = createPropertyDescriptor(PLGN_PROPERTY_ID, "ID");
		descriptors[2] = createPropertyDescriptor(PLGN_PROPERTY_VERSION, "Version");
		descriptors[3] = createPropertyDescriptor(PLGN_PROPERTY_LICENSE, "License");
		descriptors[4] = createPropertyDescriptor(PLGN_PROPERTY_AUTHOR, "Author");
		descriptors[5] = createPropertyDescriptor(PLGN_PROPERTY_INFO, "Info");
	}
	
	public CordovaPluginProperties(CordovaPlugin plugin){
		this.source = plugin;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if(PLGN_PROPERTY_NAME.equals(id)){
			return source.getName();
		}
		if(PLGN_PROPERTY_ID.equals(id)){
			return source.getId();
		}
		if(PLGN_PROPERTY_VERSION.equals(id)){
			return source.getVersion();
		}
		if(PLGN_PROPERTY_LICENSE.equals(id)){
			return source.getLicense();
		}
		if(PLGN_PROPERTY_AUTHOR.equals(id)){
			return source.getAuthor();
		}
		if(PLGN_PROPERTY_INFO.equals(id)){
			return source.getInfo();
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
	}
	
	private static IPropertyDescriptor createPropertyDescriptor(String field, String label){
		PropertyDescriptor descriptor = new PropertyDescriptor(field, label);
		descriptor.setCategory("Cordova Plugin");
		descriptor.setAlwaysIncompatible(true);
		return descriptor;

		
	}
}
