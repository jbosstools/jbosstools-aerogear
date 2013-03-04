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
package org.jboss.tools.aerogear.hybrid.ios.ui;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.jboss.tools.aerogear.hybrid.ios.core.simulator.IOSSimulatorLaunchConstants;
import org.jboss.tools.aerogear.hybrid.ios.core.xcode.XCodeBuild;
/**
 * Launch shortcut for launching iOS Simulator. 
 * 
 * @author Gorkem Ercan
 *
 */
public class IOSLaunchShortcut implements ILaunchShortcut{

	@Override
	public void launch(ISelection selection, String mode) {		
		try {
			XCodeBuild xcode = new XCodeBuild();
			xcode.version();
			
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object selected = ssel.getFirstElement();
			IResource res = (IResource) selected;
			IProject project = res.getProject();
			
		    DebugUITools.launch(findOrCreateLaunchConfiguration(project), mode);
			
		} catch (CoreException e) {
			StatusManager manager = StatusManager.getManager();
			if ( e.getCause() instanceof IOException ){
				Status status = new Status(IStatus.ERROR, IOSUI.PLUGIN_ID, "Unable to invoke XCode command line tools."
						+ " Make sure that the latest XCode and XCode Command Line tools is installed",e.getCause());
				StatusAdapter adapter = new StatusAdapter(status);
				adapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "XCode Command Line Tools Error");
				manager.handle(adapter, StatusManager.SHOW );
			}else{
				manager.handle(e,IOSUI.PLUGIN_ID);
			}
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		// TODO Auto-generated method stub¯§
	}
	
	/**
	 * Creates a new lauch configuration for the given project if one does not exists.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	private ILaunchConfiguration findOrCreateLaunchConfiguration(IProject project) throws CoreException{

		ILaunchManager lm = getLaunchManager();
		ILaunchConfigurationType configType = getIOSSimulatorLaunchConfigType();
		ILaunchConfiguration[] confs = lm.getLaunchConfigurations(configType);
		for (ILaunchConfiguration configuration : confs) {
			String projName = configuration.getAttribute(IOSSimulatorLaunchConstants.ATTR_BUILD_SCOPE, (String)null);
			if(project.getName().equals(projName)){//TODO: Handle the case for a project has more than one 
				                                   // launch config associated with it. Present a dialog to select?
				return configuration;
			}
		}
		return createLaunchConfiguration(project);
	}

	private ILaunchManager getLaunchManager(){
		return DebugPlugin.getDefault().getLaunchManager();
	}

	private ILaunchConfigurationType getIOSSimulatorLaunchConfigType(){
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = lm.getLaunchConfigurationType(IOSSimulatorLaunchConstants.LAUNCH_CONFIG_TYPE);
		return configType;

	}
	
	private ILaunchConfiguration createLaunchConfiguration(IProject project) throws CoreException{
		ILaunchConfigurationWorkingCopy wc = getIOSSimulatorLaunchConfigType().newInstance(null,
				getLaunchManager().generateLaunchConfigurationName(project.getName()));
		wc.setAttribute(IOSSimulatorLaunchConstants.ATTR_BUILD_SCOPE, project.getName());
		return wc.doSave();
	}
}
