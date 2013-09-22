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
package org.jboss.tools.aerogear.hybrid.core.plugin.actions;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.platform.IPluginInstallationAction;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginManager;

public class DependencyInstallAction implements IPluginInstallationAction {

	private final HybridProject project;
	private final String dependencyPluginId;
	private final URI uri;
	private final String commit;
	private final String  subdir;
	
	
	public DependencyInstallAction(String dependencyId, URI uri,
			String commit, String subdir, HybridProject project) {
		this.project = project;
		this.uri = uri;
		this.dependencyPluginId = dependencyId;
		this.commit = commit;
		this.subdir = subdir;
	}

	@Override
	public void install() throws CoreException {
		CordovaPluginManager pluginManager = project.getPluginManager();
		if(!pluginManager.isPluginInstalled(dependencyPluginId)){
			pluginManager.installPlugin(uri,commit,subdir, new NullProgressMonitor());
		}
	}

	@Override
	public void unInstall() throws CoreException {
		CordovaPluginManager pluginManager = project.getPluginManager();
		pluginManager.unInstallPlugin(dependencyPluginId, new NullProgressMonitor());

	}

}
