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
package org.jboss.tools.aerogear.hybrid.ios.core.simulator;

import static org.jboss.tools.aerogear.hybrid.ios.core.simulator.IOSSimulatorLaunchConstants.ATTR_DEVICE_FAMILY;
import static org.jboss.tools.aerogear.hybrid.ios.core.simulator.IOSSimulatorLaunchConstants.ATTR_USE_RETINA;
import static org.jboss.tools.aerogear.hybrid.ios.core.simulator.IOSSimulatorLaunchConstants.ATTR_USE_TALL;
import static org.jboss.tools.aerogear.hybrid.ios.core.simulator.IOSSimulatorLaunchConstants.VAL_DEVICE_FAMILY_IPHONE;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.jboss.tools.aerogear.hybrid.core.HybridProjectLaunchConfigConstants;
import org.jboss.tools.aerogear.hybrid.ios.core.xcode.XCodeBuild;
/**
 * {@link ILaunchDelegate} for running the iOS simulator. This delegate is unusual 
 * because besides running the emulator it also generates and builds the cordova project.
 * 
 * @author Gorkem Ercan
 *
 */
public class IOSSimulatorLaunchDelegate implements
		ILaunchConfigurationDelegate2 {

	private File buildArtifact;
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Launch iOS Enulator", 10);
		IOSSimulator simulator = new IOSSimulator();
		IProject project = getProject(configuration);
		Assert.isNotNull(project, "Can not launch with a null project");
		simulator.setPathToBinary(buildArtifact.getPath());
		simulator.setFamily(configuration.getAttribute(ATTR_DEVICE_FAMILY, VAL_DEVICE_FAMILY_IPHONE));
		simulator.setRetina(configuration.getAttribute(ATTR_USE_RETINA, false));
		simulator.setTall(configuration.getAttribute(ATTR_USE_TALL, false));
	
		String[] envp = DebugPlugin.getDefault().getLaunchManager()
				.getEnvironment(configuration);
		simulator.setProcessEnvironmentVariables(envp);
		monitor.worked(2);
		simulator.launch();
		monitor.done();
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		return null;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		
		XCodeBuild build = new XCodeBuild();
		build.setLaunchConfiguration(configuration);
		build.init(getProject(configuration), null);
		build.buildNow(monitor);
		buildArtifact = build.getBuildArtifact();
		
		return false;
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		monitor.done();
		return true;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		monitor.done();
		return true;
	}
	
	private IProject getProject(ILaunchConfiguration configuration){
		try{
			String projectName = configuration.getAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, (String)null);
			if(projectName != null ){
				 return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		}catch(CoreException e){
			return null;
		}
		return null;
	}

}
