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
package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginManager;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginInfo;

public class InstalledPluginFilter extends ViewerFilter {

	private HybridProject project;
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(project == null ) 
			return false;
		CordovaPluginManager pm = project.getPluginManager();
		if(element instanceof CordovaRegistryPluginInfo ){
			CordovaRegistryPluginInfo plugin = (CordovaRegistryPluginInfo) element;
			return !pm.isPluginInstalled(plugin.getName());
		}
		return false;
	}
	
	public void setProject(HybridProject project){
		this.project = project;
	}

}
