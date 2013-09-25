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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;

public class PluginsFolderContentFilter extends ViewerFilter {

	public PluginsFolderContentFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(!(element instanceof IFolder) )
			return true;
		IFolder folder = (IFolder) element;
		IContainer parent = folder.getParent();
		if(parent.getName().equals(PlatformConstants.DIR_PLUGINS) && parent.getProjectRelativePath().segmentCount() == 1){
			return false;
		}
		return true;
	}

}
