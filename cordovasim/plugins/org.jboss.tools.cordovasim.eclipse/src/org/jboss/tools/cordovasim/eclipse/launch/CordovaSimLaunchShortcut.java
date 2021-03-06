/*******************************************************************************
 * Copyright (c) 2013-2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.cordovasim.eclipse.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.jboss.tools.cordovasim.eclipse.Activator;
import org.jboss.tools.cordovasim.eclipse.launch.internal.CordovaSimLaunchConfigurationAutofillUtil;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class CordovaSimLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		IProject project = CordovaSimLaunchConfigurationAutofillUtil.getProjectToRun(selection);
		launch(project, mode);
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = ResourceUtil.getFile(input);
		if (file != null) {
			IProject project = file.getProject();
			launch(project, mode);
		}
	}
	
	protected void launch(IProject project, String mode) {
		try {
			ILaunchConfigurationType cordovaSimLaunchConfiguraionType = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurationType(CordovaSimLaunchConstants.LAUNCH_CONFIGURATION_ID); 
			ILaunchConfiguration[] configurations = DebugPlugin.getDefault()
					.getLaunchManager().getLaunchConfigurations(cordovaSimLaunchConfiguraionType);
			
			ILaunchConfiguration existingConfiguraion = CordovaSimLaunchConfigurationAutofillUtil
					.chooseLaunchConfiguration(configurations, project);
			if (existingConfiguraion != null) {
				DebugUITools.launch(existingConfiguraion, mode);
			} else {
				ILaunchConfigurationWorkingCopy newConfiguration = createEmptyLaunchConfiguration(project.getName());
				CordovaSimLaunchConfigurationAutofillUtil.fillLaunchConfiguraion(newConfiguration, project);
				newConfiguration.doSave();
				DebugUITools.launch(newConfiguration, mode);				
			}
		} catch (CoreException e) {
			Activator.logError(e.getMessage(), e);
		}
	}

	protected ILaunchConfigurationWorkingCopy createEmptyLaunchConfiguration(
			String namePrefix) throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType 
				= launchManager.getLaunchConfigurationType(CordovaSimLaunchConstants.LAUNCH_CONFIGURATION_ID);
		ILaunchConfigurationWorkingCopy launchConfiguration;
		launchConfiguration = launchConfigurationType.newInstance(
				null, launchManager.generateLaunchConfigurationName(namePrefix));
		return launchConfiguration;
	}
}
