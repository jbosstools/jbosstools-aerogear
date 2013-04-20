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
package org.jboss.tools.aerogear.hybrid.android.core.adt;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.jboss.tools.aerogear.hybrid.android.core.AndroidCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.HybridProjectLaunchConfigConstants;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;

public class AndroidLaunchDelegate implements ILaunchConfigurationDelegate2 {

	private File buildDir;
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		AndroidSDKManager sdk = new AndroidSDKManager();
	
		HybridProject project = HybridProject.getHybridProject(getProject(configuration));
		Widget widget = project.getWidget();
		String packageName = widget.getId();
		String name = project.getBuildArtifactAppName();

		sdk.installApk(new File(buildDir,name+"-debug.apk" ), false);
		
		sdk.startApp(packageName+"/."+name);
		String logcatFilter = configuration.getAttribute(AndroidLaunchConstants.ATTR_LOGCAT_FILTER, "");
		sdk.logcat(logcatFilter,null,null );
		
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		return null;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		if( monitor.isCanceled() ){
			return false;
		}
		AndroidProjectGenerator creator = new AndroidProjectGenerator(getProject(configuration), null);
		SubProgressMonitor generateMonitor = new SubProgressMonitor(monitor, 1);
		File projectDirectory = creator.generateNow(generateMonitor);
		monitor.worked(1);
		if(monitor.isCanceled() ){
			return false;
		}
		AndroidSDKManager sdkManager = new AndroidSDKManager();
		buildDir = sdkManager.buildProject(projectDirectory);
		monitor.done();
		return true;
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
		// Start ADB Server
		AndroidSDKManager sdk = new AndroidSDKManager();
		sdk.startADBServer();
		// Do we have any devices to run on?	
		if ( !deviceReady() ){
			// No devices lets start an emulator.
			// Check if we have an AVD
			List<String> avds = sdk.listAVDs();
			if (avds == null || avds.isEmpty()){
				throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "No Android AVDs are available"));
			}
			String avdName = configuration.getAttribute(AndroidLaunchConstants.ATTR_AVD_NAME, (String)null);
			if(avdName == null || !avds.contains(avdName)){
				avdName = avds.get(0);
			}
			//start the emulator.
			sdk.startEmulator(avdName);
			// wait for it to come online 
			sdk.waitForDevice();
		}
		monitor.done();
		return true;
	}
	
	private boolean deviceReady() throws CoreException{
		AndroidSDKManager sdk = new AndroidSDKManager();
		List<AndroidDevice> devices = sdk.listDevices();
		if(devices == null ) 
			return false;
		for (AndroidDevice androidDevice : devices) {
			if(androidDevice.getState() == AndroidDevice.STATE_DEVICE )
				return true;
		}
		return false;
	}
	
	//TODO: duplicated form IOSLaunchDelegate... move both to a common utility.
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
