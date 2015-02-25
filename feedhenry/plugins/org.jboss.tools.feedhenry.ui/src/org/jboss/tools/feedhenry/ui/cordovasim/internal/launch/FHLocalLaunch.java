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
import org.jboss.tools.feedhenry.ui.cordovasim.util.FeedHenryUtil;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.CordovaSimLaunchConstants;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.CordovaSimLaunchParametersUtil;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.RippleProxy;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class FHLocalLaunch extends FHLaunchShortcut {
	private static final String FH_LOCAL_PREFIX = "(FeedHenry local)"; //$NON-NLS-1$

	@Override
	public String getLaunchPrefix() {
		return FH_LOCAL_PREFIX;
	}
	
	@Override
	protected void launch(IProject project, String mode) {
		try {
			ILaunchConfigurationType cordovaSimLaunchConfiguraionType = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurationType(CordovaSimLaunchConstants.LAUNCH_CONFIGURATION_ID);
			ILaunchConfiguration[] configurations = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(cordovaSimLaunchConfiguraionType);
			ILaunchConfiguration existingConfiguraion = chooseLaunchConfiguration(configurations, project);
			
			if (existingConfiguraion != null) {
				existingConfiguraion.delete(); 
			} 
			
			if (project != null) {
				ILaunchConfigurationWorkingCopy newConfiguration = createEmptyLaunchConfiguration(project.getName() + getLaunchPrefix());
				setConfigAttributes(newConfiguration, project);
				newConfiguration.doSave();
				DebugUITools.launch(newConfiguration, mode);
			}
		} catch (CoreException e) {
			FHPlugin.log(IStatus.ERROR, e.getMessage(), e);
		}
	}
	
	@Override
	protected void setConfigAttributes(ILaunchConfigurationWorkingCopy launchConfiguration, IProject project) {
		if (project != null) {
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.PROJECT, project.getName());
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.START_PAGE, getStartPage(project));
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.FH, getLaunchPrefix());
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.PROXY, RippleProxy.DISABLED.getValue());
		}
	}

	private String getStartPage(IProject project) {
		String startPage = CordovaSimLaunchParametersUtil.getDefaultStartPageFromConfigXml(project);
		return FeedHenryUtil.addDefaultServerParameter(startPage);
	}

}