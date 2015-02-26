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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.cordovasim.eclipse.launch.CordovaSimLaunchConstants;
import org.jboss.tools.cordovasim.eclipse.launch.CordovaSimLaunchShortcut;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public abstract class FHLaunchShortcut extends CordovaSimLaunchShortcut {
	public abstract String getLaunchPrefix();
	
	protected abstract void setConfigAttributes(ILaunchConfigurationWorkingCopy launch, IProject project);
	
	@Override
	protected void launch(IProject project, String mode) {
		try {
			ILaunchConfigurationType cordovaSimLaunchConfiguraionType = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurationType(CordovaSimLaunchConstants.LAUNCH_CONFIGURATION_ID);
			ILaunchConfiguration[] configurations = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(cordovaSimLaunchConfiguraionType);

			ILaunchConfiguration existingConfiguraion = chooseLaunchConfiguration(configurations, project);
			if (existingConfiguraion != null) {
				DebugUITools.launch(existingConfiguraion, mode);
			} else if (project != null) {
				ILaunchConfigurationWorkingCopy newConfiguration = createEmptyLaunchConfiguration(project.getName() + getLaunchPrefix());
				setConfigAttributes(newConfiguration, project);
				newConfiguration.doSave();
				DebugUITools.launch(newConfiguration, mode);
			}
		} catch (CoreException e) {
			FHPlugin.log(IStatus.ERROR, e.getMessage(), e);
		}
	}

	private ILaunchConfiguration chooseLaunchConfiguration(ILaunchConfiguration[] configurations, IProject project) {
		try {
			for (ILaunchConfiguration configuration : configurations) {
				String projectName = configuration.getAttribute(CordovaSimLaunchConstants.PROJECT, (String) null);
				String fh = configuration.getAttribute(CordovaSimLaunchConstants.FH, (String) null);
				if (fh != null && fh.equals(getLaunchPrefix()) && projectName != null && projectName.equals(project.getName())) {
					return configuration;
				}
			}
		} catch (CoreException e) {
			FHPlugin.log(IStatus.ERROR, e.getMessage(), e);
		}
		return null;
	}

}