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
package org.jboss.tools.aerogear.hybrid.ui.plugins.navigator.internal;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class PluginContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(!(parentElement instanceof IFolder)){
			return new Object[0];
		}
		IFolder folder = (IFolder) parentElement;
		if(folder.getProjectRelativePath().segmentCount()>1){//only plugins folder at the root of the project
			return new Object[0];
		}
		HybridProject project = HybridProject.getHybridProject(folder.getProject());
		try {
			List<CordovaPlugin> plugins = project.getPluginManager().getInstalledPlugins();
			return plugins.toArray();
		}catch(CoreException e){
			HybridUI.log(IStatus.ERROR, "Error retrieving the installed plugins", e);	
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(!(element instanceof IFolder)){
			return false;
		}
		IFolder folder = (IFolder) element;
		if(folder.getProjectRelativePath().segmentCount()>1){//folder at the root of the project?
			return false;
		}
		HybridProject project = HybridProject.getHybridProject(folder.getProject());
		try {
			List<CordovaPlugin> plugins = project.getPluginManager().getInstalledPlugins();
			return plugins.isEmpty();
		} catch (CoreException e) {
			HybridUI.log(IStatus.ERROR, "Error determining the installed plugins", e);
		}
		return false;
	}

}
