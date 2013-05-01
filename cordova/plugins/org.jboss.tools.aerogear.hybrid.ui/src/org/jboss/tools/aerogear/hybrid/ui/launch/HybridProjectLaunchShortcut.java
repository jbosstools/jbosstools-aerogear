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
package org.jboss.tools.aerogear.hybrid.ui.launch;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
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
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.HybridProjectLaunchConfigConstants;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
/**
 * General hybrid mobile project launch shortcut.
 * 
 * @author Gorkem Ercan
 *
 */
public abstract class HybridProjectLaunchShortcut implements ILaunchShortcut{

	@Override
	public void launch(ISelection selection, String mode) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object selected = ssel.getFirstElement();
			IResource res = (IResource) selected;
			launch(res.getProject());
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IFile file = ResourceUtil.getFile(editor.getEditorInput());
		if (file != null) {
			IProject project = file.getProject();
			launch(project);
		}
	}

	private void launch(IProject project) {
		try {
			
			validateBuildToolsReady();
			if (!shouldProceedWithLaunch(HybridProject
					.getHybridProject(project)))
				return;
			
			ILaunchConfiguration launchConfig = findOrCreateLaunchConfiguration(project);
			ILaunchConfigurationWorkingCopy wc = launchConfig.getWorkingCopy();
			updateLaunchConfiguration(wc);
			launchConfig = wc.doSave();
			DebugUITools.launch(launchConfig, "run");
			
		} catch (CoreException e) {
			StatusManager manager = StatusManager.getManager();
			if (e.getCause() instanceof IOException) {
				Status status = new Status(IStatus.ERROR, HybridUI.PLUGIN_ID,
						"Unable to complete the build for target plarform",
						e.getCause());
				StatusAdapter adapter = new StatusAdapter(status);
				adapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY,
						"Mobile Hybrid Project Build Error");
				manager.handle(adapter, StatusManager.SHOW);
			} else {
				manager.handle(e.getStatus(),StatusManager.SHOW);
			}
		}
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
		ILaunchConfigurationType configType = getLaunchConfigurationType();
		ILaunchConfiguration[] confs = lm.getLaunchConfigurations(configType);
		for (ILaunchConfiguration configuration : confs) {
			String projName = configuration.getAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, (String)null);
			if(project.getName().equals(projName)){//TODO: Handle the case for a project has more than one 
				                                   // launch config associated with it. Present a dialog to select?
				return configuration;
			}
		}
		return createLaunchConfiguration(project);
	}

	
	private ILaunchConfiguration createLaunchConfiguration(IProject project) throws CoreException{
		ILaunchConfigurationWorkingCopy wc = getLaunchConfigurationType().newInstance(null,
				getLaunchManager().generateLaunchConfigurationName(project.getName()));
		wc.setAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, project.getName());
		return wc.doSave();
	}

	private ILaunchManager getLaunchManager(){
		return DebugPlugin.getDefault().getLaunchManager();
	}
	
	
	private ILaunchConfigurationType getLaunchConfigurationType(){
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		String launchTypeID = getLaunchConfigurationTypeID();
		Assert.isNotNull(launchTypeID);
		ILaunchConfigurationType configType = lm.getLaunchConfigurationType(launchTypeID);
		return configType;
	}
	
	/**
	 * Invoked before commencing with the launch to give platfom a chance
	 *  to check if the prerequisites for a build are in place. 
	 *  Implementors should throw a {@link CoreException} with a 
	 *  Status message.
	 *  
	 * @throws CoreException if build tools are not working properly or 
	 * 	does not exist
	 */
	protected abstract void validateBuildToolsReady() throws CoreException;
	
	/**
	 * Return the launchConfiguratonType ID. Which will be used to 
	 * create a launch configuration.
	 * @return launchConfig id
	 */
	protected abstract String getLaunchConfigurationTypeID();
	/**
	 * Last chance for implementors to abort the launch. Default 
	 * implementation checks the validity of the project.
	 * 
	 * @param project
	 * @return
	 */
	protected boolean shouldProceedWithLaunch(HybridProject project){
		return project != null;
	}
	
	/**
	 * Update the launch configuration with platform implementation specific values.
	 * 
	 * @param wc
	 */
	protected void updateLaunchConfiguration(ILaunchConfigurationWorkingCopy wc){
		
	}

}
