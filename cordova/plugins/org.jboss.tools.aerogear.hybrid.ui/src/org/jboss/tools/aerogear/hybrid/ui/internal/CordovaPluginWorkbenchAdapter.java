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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class CordovaPluginWorkbenchAdapter implements IWorkbenchAdapter {
	ImageDescriptor icon = HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, "/icons/obj16/plug16_obj.png");


	@Override
	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return icon;
	}

	@Override
	public String getLabel(Object o) {
		if(o instanceof CordovaPlugin){
			CordovaPlugin plugin = (CordovaPlugin) o;
			String label = plugin.getName();
			if(label == null || label.isEmpty()){
				label = plugin.getId();
			}
			return label;
		}
		return null;
	}

	@Override
	public Object getParent(Object o) {
		if(o instanceof CordovaPlugin){
			CordovaPlugin plugin = (CordovaPlugin) o;
			return plugin.getFolder();
		}
		return null;
	}

}
