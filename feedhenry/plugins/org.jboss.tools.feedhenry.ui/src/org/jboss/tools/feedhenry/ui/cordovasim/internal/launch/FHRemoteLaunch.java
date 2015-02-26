/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.feedhenry.ui.cordovasim.internal.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.jboss.tools.cordovasim.eclipse.launch.CordovaSimLaunchConstants;
import org.jboss.tools.cordovasim.eclipse.launch.RippleProxy;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class FHRemoteLaunch extends FHLaunchShortcut {
	private static final String FH_REMOTE_PREFIX = "(FeedHenry remote)"; //$NON-NLS-1$

	@Override
	public String getLaunchPrefix() {
		return FH_REMOTE_PREFIX;
	}
	
	@Override
	protected void setConfigAttributes(ILaunchConfigurationWorkingCopy launchConfiguration, IProject project) {
		if (project != null) {
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.PROJECT, project.getName());
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.FH, getLaunchPrefix());
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.PROXY, RippleProxy.DISABLED.getValue());
		}
	}

}
